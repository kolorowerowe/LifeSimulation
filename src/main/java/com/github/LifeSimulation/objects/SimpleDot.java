package com.github.LifeSimulation.objects;

import lombok.Getter;
import lombok.Setter;

import java.awt.Graphics;
import java.awt.Color;

import static com.github.LifeSimulation.utils.ResourcesLoader.getWorldHeight;
import static com.github.LifeSimulation.utils.ResourcesLoader.getWorldWidth;

@Getter
@Setter
public class SimpleDot extends SimulationObject {

    private Integer moveX;
    private Integer moveY;

    private Double probabilityToMove;
    private Color color;


    public SimpleDot() {
        super();
        init();
    }

    public SimpleDot(Integer x, Integer y) {
        super(x, y);
        init();
    }

    private void init() {
        this.probabilityToMove = random.nextDouble();
        this.moveX = 1;
        this.moveY = 1;
        this.color = Color.CYAN;
    }

    public void tick() {
        move();
    }

    public void render(Graphics g) {
        g.setColor(color);
        g.fillRect(getX(), getY(), getWidth(), getHeight());
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
}
