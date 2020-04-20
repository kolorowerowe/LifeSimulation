package com.github.LifeSimulation.objects;

import lombok.Getter;
import lombok.Setter;

import java.awt.Graphics;
import java.util.Random;

import static com.github.LifeSimulation.utils.ResourcesLoader.*;

@Getter
@Setter
public abstract class SimulationObject {
    private Integer x;
    private Integer y;

    public SimulationObject() {
        setRandomPosition();
    }

    public SimulationObject(Integer x, Integer y) {
        this.x = x;
        this.y = y;
    }

    public abstract void tick();
    public abstract void render(Graphics g);

    private void setRandomPosition(){
        Random random = new Random();
        this.x = random.nextInt(getWorldWidth());
        this.y = random.nextInt(getWorldHeight());
    }

}
