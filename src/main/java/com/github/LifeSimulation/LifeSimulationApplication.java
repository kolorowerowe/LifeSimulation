package com.github.LifeSimulation;


import com.github.LifeSimulation.core.SimulationCore;
import com.github.LifeSimulation.core.Window;

import lombok.extern.log4j.Log4j;

@Log4j
public class LifeSimulationApplication {

    public static void main(String[] args) {

        Window window = new Window();
        SimulationCore simulation = new SimulationCore(window.getDimension());

        window.addCanvas(simulation);
        simulation.start();

        log.info("Life Simulation Application has started");

    }

}
