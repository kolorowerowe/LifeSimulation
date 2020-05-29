package com.github.LifeSimulation.core;

import com.github.LifeSimulation.objects.SimpleEntity;
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
        int numberOfSimpleEntities = getInitNumberOfSimpleEntities();

        for(Integer i=0; i<numberOfSimpleEntities; i++) {
            addObjectToSimulation(new SimpleEntity());
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
