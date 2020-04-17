package com.github.LifeSimulation.objects;

import lombok.Getter;
import lombok.Setter;

import java.awt.Graphics;

@Getter
@Setter
public abstract class SimulationObject {
    private Integer x;
    private Integer y;

    public SimulationObject(Integer x, Integer y) {
        this.x = x;
        this.y = y;
    }

    public abstract void tick();
    public abstract void render(Graphics g);

}
