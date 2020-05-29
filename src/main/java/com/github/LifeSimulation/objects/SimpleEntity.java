package com.github.LifeSimulation.objects;

import com.github.LifeSimulation.enums.LivingState;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.awt.Graphics;
import java.awt.Color;

import static com.github.LifeSimulation.utils.ResourcesLoader.*;

@Data
public class SimpleEntity extends SimulationObject {

    private Integer moveX;
    private Integer moveY;

    private Double probabilityToMove;
    private Color color;

    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    private Integer ticksForYear = getTicksForYear();

    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    private Integer maxLifeAge = getMaxLifeAge();

    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    private Integer displayDeathTime = getDisplayDeathTime();

    private Integer ticksInCurrentAge = 0;
    private Integer age = 0;
    private LivingState livingState;
    private Long deathTimestamp = 0L;



    public SimpleEntity() {
        super();
        init();
    }

    public SimpleEntity(Integer x, Integer y) {
        super(x, y);
        init();
    }

    private void init() {
        this.probabilityToMove = random.nextDouble();
        this.moveX = 1;
        this.moveY = 1;
        this.color = Color.CYAN;
        this.livingState = LivingState.ALIVE;
        super.statistics.increaseCountOfLivingObjects();
    }

    public void tick() {
        if (livingState == LivingState.ALIVE){
            if (ticksInCurrentAge >= ticksForYear) {
                ticksInCurrentAge = 0;
                age++;
            }

            // calculate propability of death only once per entity age
            if (ticksInCurrentAge == 0){
                if (shouldDie(age, maxLifeAge)){
                    deathTimestamp = System.currentTimeMillis();
                    livingState = LivingState.DIED_DISPLAY;
                    color = Color.RED;
                    super.statistics.decreaseCountOfLivingObjects();
                    super.statistics.increaseCountOfDiedObjects();
                }
            }

            ticksInCurrentAge++;
            move();

        } else if (livingState == LivingState.DIED_DISPLAY){
            if (System.currentTimeMillis() - deathTimestamp > displayDeathTime){
                livingState = LivingState.DIED_BURIED;

            }
        }

    }

    public void render(Graphics g) {
        if (livingState != LivingState.DIED_BURIED){
            g.setColor(color);
            g.fillRect(getX(), getY(), getWidth(), getHeight());
        }

    }

    private void move() {
        if (random.nextDouble() < probabilityToMove) {
            Integer newXPos = getX() + moveX;
            if (newXPos <= 0 || newXPos + getWidth() >= getWorldWidth()) {
                moveX = -moveX;
            } else {
                setX(newXPos);
            }

            Integer newYPos = getY() + moveY;
            if (newYPos <= 0 || newYPos + getHeight() >= getWorldHeight()) {
                moveY = -moveY;
            } else {
                setY(newYPos);
            }
        }

    }

    private Boolean shouldDie(Integer age, Integer maxLifeAge){
        Double randomFrom0To1 = super.random.nextDouble();
        Double probabilityToDie = 1 / (Double.valueOf(maxLifeAge) - Double.valueOf(age));

        return randomFrom0To1 < probabilityToDie;
    }

}
