package com.github.LifeSimulation.core;


import lombok.Getter;
import lombok.Setter;

import java.awt.*;

import static com.github.LifeSimulation.utils.ResourcesLoader.*;

public class Statistics {
    private static Statistics INSTANCE;

    @Getter
    @Setter
    private Integer countOfObjects = 0;

    private Statistics() {
    }

    public static Statistics getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new Statistics();
        }
        return INSTANCE;
    }

    public void render(Graphics g) {
        g.setColor(Color.lightGray);
        g.fillRect(getWorldWidth(), 0, getWindowWidth(), getWorldHeight());

        g.setColor(Color.black);
        g.drawString("Count of objects: " + countOfObjects, getWorldWidth() + 10, 20);

    }

}
