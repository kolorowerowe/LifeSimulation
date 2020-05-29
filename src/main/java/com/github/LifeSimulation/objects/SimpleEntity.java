package com.github.LifeSimulation.objects;

import com.github.LifeSimulation.enums.Gender;
import com.github.LifeSimulation.enums.LivingState;
import com.github.LifeSimulation.utils.ResourcesLoader;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.awt.Graphics;
import java.awt.Color;

import static com.github.LifeSimulation.enums.Gender.FEMALE;
import static com.github.LifeSimulation.enums.Gender.MALE;
import static com.github.LifeSimulation.enums.LivingState.*;
import static com.github.LifeSimulation.enums.LivingState.ALIVE_MIDDLE_AGE;
import static com.github.LifeSimulation.enums.LivingState.ALIVE_YOUNG;
import static com.github.LifeSimulation.utils.ResourcesLoader.*;

@Data
public class SimpleEntity extends SimulationObject {

    private Integer moveX;
    private Integer moveY;

    private Double probabilityToMove;
    private Color color;
    private Gender gender;

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
        this.color = Color.WHITE;
        this.livingState = ALIVE_YOUNG;
        this.gender = getRandomGender();
        super.statistics.increaseCountOfLivingObjects();
    }

    public void tick() {
        if (isAlive(livingState)) {
            if (ticksInCurrentAge >= ResourcesLoader.getTicksForYear()) {
                ticksInCurrentAge = 0;
                age++;

                if (age > ResourcesLoader.getOldAgeThreshold()) {
                    this.livingState = ALIVE_OLD;
                } else if (age > ResourcesLoader.getMiddleAgeThreshold()) {
                    this.livingState = ALIVE_MIDDLE_AGE;
                }
                setColorBasedOnAgeAndGender(livingState, gender);

            }

            // calculate propability of death only once per entity age
            if (ticksInCurrentAge == 0) {
                if (shouldDie(age, ResourcesLoader.getMaxLifeAge())) {
                    deathTimestamp = System.currentTimeMillis();
                    livingState = DIED_DISPLAY;
                    color = Color.GRAY;
                    super.statistics.decreaseCountOfLivingObjects();
                    super.statistics.increaseCountOfDiedObjects();
                }
            }

            ticksInCurrentAge++;
            move();

        } else if (livingState == DIED_DISPLAY) {
            if (System.currentTimeMillis() - deathTimestamp > ResourcesLoader.getDisplayDeathTime()) {
                livingState = DIED_BURIED;
            }
        }

    }

    public void render(Graphics g) {
        if (livingState != DIED_BURIED) {
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

    private Boolean isAlive(LivingState livingState) {
        switch (livingState) {
            case ALIVE_YOUNG:
            case ALIVE_MIDDLE_AGE:
            case ALIVE_OLD:
                return true;
            default:
                return false;

        }
    }

    private void setColorBasedOnAgeAndGender(LivingState livingState, Gender gender) {
        if (livingState == ALIVE_YOUNG) {
            if (gender == MALE) {
                this.color = new Color(0, 200, 255);
            } else {
                this.color = new Color(255, 250, 0);
            }
        } else if (livingState == ALIVE_MIDDLE_AGE) {
            if (gender == MALE) {
                this.color = new Color(0, 0, 200);
            } else {
                this.color = new Color(200, 0, 0);
            }
        } else if (livingState == ALIVE_OLD) {
            if (gender == MALE) {
                this.color = new Color(0, 0, 50);
            } else {
                this.color = new Color(50, 0, 0);
            }
        }
    }

    private Gender getRandomGender() {
        return super.random.nextBoolean() ? MALE : FEMALE;
    }

    private Boolean shouldDie(Integer age, Integer maxLifeAge) {
        Double randomFrom0To1 = super.random.nextDouble();
        Double probabilityToDie = 1 / (Double.valueOf(maxLifeAge) - Double.valueOf(age));

        return randomFrom0To1 < probabilityToDie;
    }

}
