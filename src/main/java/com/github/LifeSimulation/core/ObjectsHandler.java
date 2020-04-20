package com.github.LifeSimulation.core;

import com.github.LifeSimulation.objects.SimpleDot;
import com.github.LifeSimulation.objects.SimulationObject;

import java.awt.Graphics;
import java.util.LinkedList;
import java.util.List;

import static com.github.LifeSimulation.utils.ResourcesLoader.*;

public class ObjectsHandler {
    private List<SimulationObject> simulationObjectList = new LinkedList();
    private Statistics statistics;

    public ObjectsHandler() {
        this.statistics = Statistics.getInstance();
        populateFromInitProperties();
    }

    private void populateFromInitProperties(){
        int numberOfSimpleDots = getInitNumberOfSimpleDots();

        for(Integer i=0; i<numberOfSimpleDots; i++) {
            addObjectToSimulation(new SimpleDot());
        }
    }

    private void addObjectToSimulation(SimulationObject object) {
        simulationObjectList.add(object);
    }

    public void tick() {
        statistics.setCountOfObjects(simulationObjectList.size());
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
