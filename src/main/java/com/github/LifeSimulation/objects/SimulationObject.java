package com.github.LifeSimulation.objects;

import com.github.LifeSimulation.core.ObjectsManager;
import com.github.LifeSimulation.core.Statistics;
import com.github.LifeSimulation.environment.Environment;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j;

import java.awt.*;
import java.util.Random;

import static com.github.LifeSimulation.utils.ResourcesLoader.*;

@Getter
@Setter
@Log4j
public abstract class SimulationObject {
    // position
    protected float posX;
    protected float posY;
    // velocity
    protected float velX;
    protected float velY;
    // radius (for collisions)
    protected float radius;
    // object's id, currently only used to prevent multiple collisions
    private int id;
    // next id, incremented at each object creation
    private static int nextId = 0;

    protected Random random = new Random();
    protected Statistics statistics;

    private boolean shouldBeRemoved = false;

    public SimulationObject(float radius) {
        this.posX = 0;
        this.posY = 0;
        this.velX = 0;
        this.velY = 0;
        this.radius = radius;
        this.id = nextId;
        nextId++;
        this.statistics = Statistics.getInstance();
    }

    public SimulationObject(float radius, float posX, float posY) {
        this.posX = posX;
        this.posY = posY;
        this.velX = 0;
        this.velY = 0;
        this.radius = radius;
        this.id = nextId;
        nextId++;
        this.statistics = Statistics.getInstance();
    }

    public abstract void render(Graphics2D g2);

    public void tick(Environment environment, ObjectsManager objectsManager) {
        posX += velX;
        posY += velY;
        if (posX < radius) {
            posX = radius;
            velX = -velX;
        }
        if (posY < radius) {
            posY = radius;
            velY = -velY;
        }
        if (posX > environment.getWidth() - radius) {
            posX = environment.getWidth() - radius;
            velX = -velX;
        }
        if (posY > environment.getHeight() - radius) {
            posY = environment.getHeight() - radius;
            velY = -velY;
        }
        velX *= 0.85f;
        velY *= 0.85f;
    }

    public void setRandomPosition(){
        this.posX = random.nextFloat() * getWorldWidth();
        this.posY = random.nextFloat() * getWorldHeight();
    }

    public void collideWith(SimulationObject other) {
        float m21,dvx2,a,x21,y21,vx21,vy21,fy21,sign;
        m21 = 1;
        x21 = other.posX - posX;
        y21 = other.posY - posY;
        vx21 = other.velX - velX;
        vy21 = other.velY - velY;
        if (Math.sqrt(x21*x21 + y21*y21) > radius + other.radius || (vx21*x21 + vy21*y21) >= 0) {
            return;
        }
        fy21 = 1.0e-12f * Math.abs(y21);
        if (Math.abs(x21) < fy21) {
            if (x21 < 0) { sign = -1; } else { sign = 1; }
            x21 = fy21 * sign;
        }
        a = y21 / x21;
        dvx2 = -2 * (vx21 + a*vy21) / ((1 + a*a) * (1 + m21));
        if (!Float.isNaN(dvx2)) {
            other.velX += dvx2;
            other.velY += a*dvx2;
            velX -= m21*dvx2;
            velY -= a*m21*dvx2;
        }
    }
}
