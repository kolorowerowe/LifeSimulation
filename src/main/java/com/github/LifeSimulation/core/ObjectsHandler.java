package com.github.LifeSimulation.core;

import com.github.LifeSimulation.objects.SimpleDot;
import com.github.LifeSimulation.objects.SimulationObject;
import com.github.LifeSimulation.utils.ResourcesLoader;

import java.awt.Graphics;
import java.util.LinkedList;
import java.util.List;

public class ObjectsHandler {
    private List<SimulationObject> simulationObjectList = new LinkedList();

    public ObjectsHandler() {
        int numberOfSimpleDots = Integer.parseInt(
                ResourcesLoader.loadSimulationProperties().getProperty("initNumberOfSimpleDots"));

        for(Integer i=0; i<numberOfSimpleDots; i++) {
           addObjectToSimulation(new SimpleDot(30, 30));
        }
    }

    private void addObjectToSimulation(SimulationObject object) {
        simulationObjectList.add(object);
    }

    public void tick() {
        for (SimulationObject simulationObject : simulationObjectList) {
            simulationObject.tick();
        }
    }

    public void render(Graphics g) {
        for (SimulationObject simulationObject : simulationObjectList) {
            simulationObject.render(g);
        }
    }
}
