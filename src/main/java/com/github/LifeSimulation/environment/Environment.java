package com.github.LifeSimulation.environment;

import lombok.Getter;
import lombok.extern.log4j.Log4j;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Random;

@Log4j
public class Environment {
    ArrayList<TerrainCell> terrain;

    @Getter
    private int width;
    @Getter
    private int height;

    private Random random = new Random();

    public Environment(int width, int height) {
        this.width = width;
        this.height = height;
        this.terrain = new ArrayList<>(width * height);
        for (int i = 0; i < width*height; ++i) {
            TerrainCell cell = new TerrainCell();
            cell.setFood(random.nextFloat() * 10.f);
            terrain.add(cell);
        }
    }

    public void render(Graphics2D g2) {
        Rectangle2D.Float rect = new Rectangle2D.Float(0, 0, 1.f, 1.f);
        for (int j = 0; j < height; ++j) {
            for (int i = 0; i < width; ++i) {
                rect.x = i;
                rect.y = j;
                g2.setColor(getTerrainCell(i, j).getColor());
                g2.fill(rect);
            }
        }
    }

    public void growFood(float growthDensity) {
        float amount = growthDensity * terrain.size();
        int amountInt = (int) amount;
        for (int i = 0; i < amountInt; ++i) {
            TerrainCell cell = terrain.get(random.nextInt(terrain.size()));
            cell.grow();
        }
        if (amount - amountInt > random.nextFloat()) {
            TerrainCell cell = terrain.get(random.nextInt(terrain.size()));
            cell.grow();
        }
    }

    public TerrainCell getTerrainCell(int x, int y) {
        return terrain.get(y * width + x);
    }

}
