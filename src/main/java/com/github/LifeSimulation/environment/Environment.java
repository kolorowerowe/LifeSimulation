package com.github.LifeSimulation.environment;

import lombok.Getter;
import lombok.extern.log4j.Log4j;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.util.ArrayList;
import java.util.Random;

import static java.awt.image.BufferedImage.TYPE_3BYTE_BGR;

@Log4j
public class Environment {
    private ArrayList<TerrainCell> terrain;

    private TexturePaint texture;

    private int[] rasterData;

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
        texture = new TexturePaint(new BufferedImage(width, height, TYPE_3BYTE_BGR), new Rectangle2D.Float(0, 0, width, height));
        rasterData = new int[width * height * 3];
    }

    public void render(Graphics2D g2) {
//        Rectangle2D.Float rect = new Rectangle2D.Float(0, 0, 1.f, 1.f);
//        for (int j = 0; j < height; ++j) {
//            for (int i = 0; i < width; ++i) {
//                rect.x = i;
//                rect.y = j;
//                g2.setColor(getTerrainCell(i, j).getColor());
//                g2.fill(rect);
//            }
//        }
        WritableRaster texRaster = texture.getImage().getRaster();
        for (int j = 0; j < height; ++j) {
            for (int i = 0; i < width; ++i) {
                int index = j * width + i;
                Color col = terrain.get(index).getColor();
                rasterData[3 * index] = col.getBlue();
                rasterData[3 * index + 1] = col.getGreen();
                rasterData[3 * index + 2] = col.getRed();
            }
        }
        texRaster.setPixels(0, 0, width, height, rasterData);
        g2.setPaint(texture);
        Rectangle2D.Float rect = new Rectangle2D.Float(0, 0, width, height);
        g2.fill(rect);
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
