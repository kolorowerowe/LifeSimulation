package com.github.LifeSimulation.objects;

import lombok.Getter;
import lombok.Setter;

import java.awt.Graphics;
import java.awt.Color;

@Getter
@Setter
public class SimpleDot extends SimulationObject{

    private Integer moveX;
    private Integer moveY;

    public SimpleDot() {
        super();
        this.moveX = 1;
        this.moveY = 1;
    }

    public SimpleDot(int x, int y) {
        super(x, y);
        this.moveX = 1;
        this.moveY = 1;
    }

    public void tick() {
        move();
    }

    public void render(Graphics g) {
        g.setColor(Color.CYAN);
        g.fillRect(getX(),getY(),15,15);
    }

    private void move(){
        setX(getX()+moveX);
        setY(getY()+moveY);
    }
}
