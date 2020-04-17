package com.github.LifeSimulation.objects;

import lombok.Getter;
import lombok.Setter;

import java.awt.Graphics;
import java.awt.Color;

@Getter
@Setter
public class SimpleDot extends SimulationObject{

    public SimpleDot(int x, int y) {
        super(x, y);
    }

    public void tick() {
        setX(getX()+1);
        setY(getY()+1);
    }

    public void render(Graphics g) {
        g.setColor(Color.CYAN);
        g.fillRect(getX(),getY(),15,15);
    }
}
