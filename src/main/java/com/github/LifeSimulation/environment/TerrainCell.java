package com.github.LifeSimulation.environment;

import lombok.Getter;
import lombok.Setter;

import java.awt.*;

import static com.github.LifeSimulation.utils.ResourcesLoader.*;

@Getter
@Setter
public class TerrainCell {
    private float food;

    public Color getColor() {
        float a = (float) Math.tanh(food * 0.05f) * 0.2f;
        return new Color(0.5f - a, 0.5f + a * 2.f, 0.5f - a);
    }

    public void grow() {
        if (food < getMaximumFoodOnTheGround()) {
            food += 1.f;
        }
    }
}
