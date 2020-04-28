package com.github.LifeSimulation.objects;

import javafx.scene.control.RadioMenuItem;
import lombok.Getter;
import lombok.Setter;

import java.awt.Graphics;
import java.util.Random;

import static com.github.LifeSimulation.utils.ResourcesLoader.*;

@Getter
@Setter
public abstract class SimulationObject {
    private Integer x; //left border
    private Integer y; //upper border
    private Integer width;
    private Integer height;

    protected Random random = new Random();

    public SimulationObject() {
        setRandomPosition();
        this.width = 10;
        this.height = 10;
    }

    public SimulationObject(Integer x, Integer y) {
        this.x = x;
        this.y = y;

        this.width = 10;
        this.height = 10;
    }

    public SimulationObject(Integer x, Integer y, Integer width, Integer height) {
        this.x = x;
        this.y = y;

        this.width = width;
        this.height = height;
    }

    public abstract void tick();
    public abstract void render(Graphics g);

    private void setRandomPosition(){
        this.x = random.nextInt(getWorldWidth());
        this.y = random.nextInt(getWorldHeight());
    }

}
