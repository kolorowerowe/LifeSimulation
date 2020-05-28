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

    @Getter
    @Setter
    private Integer year = 0;

    private static final Integer LEFT_POSITION = getWorldWidth() + 10;

    private Statistics() {
    }

    public static Statistics getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new Statistics();
        }
        return INSTANCE;
    }

    public void render(Graphics g, SimulationState state) {
        g.setColor(Color.lightGray);
        g.fillRect(getWorldWidth(), 0, getWindowWidth(), getWorldHeight());

        g.setColor(Color.black);
        g.setFont(new Font("Arial", Font.PLAIN, 14 ));
        g.drawString("Simulation state: " + state.toString().toLowerCase(), LEFT_POSITION, 20);
        g.drawString("Simulation year: " + year, LEFT_POSITION, 40);
        g.drawString("Count of objects: " + countOfObjects, LEFT_POSITION, 60);

        g.drawString("P - pause/run", LEFT_POSITION, 500);

    }

}
