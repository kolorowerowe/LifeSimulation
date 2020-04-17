package com.github.LifeSimulation;


import com.github.LifeSimulation.simulation.SimulationCore;
import com.github.LifeSimulation.simulation.Window;

import lombok.extern.log4j.Log4j;

@Log4j
public class LifeSimulationApplication {

    public static void main(String[] args) {

        SimulationCore simulation = new SimulationCore();
        new Window(simulation);
        simulation.start();

        log.info("Life Simulation Application has started");

    }

}
