package com.github.LifeSimulation.core;

import com.github.LifeSimulation.environment.Environment;
import com.github.LifeSimulation.objects.SimpleEntity;
import com.github.LifeSimulation.objects.SimulationObject;
import lombok.Getter;

import java.awt.*;
import java.util.ArrayList;
import java.util.Random;

import static com.github.LifeSimulation.utils.ResourcesLoader.*;

public class ObjectsManager {
    @Getter
    private ArrayList<SimulationObject> simulationObjectList = new ArrayList<>();
    private Statistics statistics;

    private Random random = new Random();

    // Prevent ConcurrentModificationException while adding
    private ArrayList<SimulationObject> newSimulationObjectList = new ArrayList<>();
    // Prevent ConcurrentModificationException while removing
    private ArrayList<SimulationObject> removedSimulationObjectList = new ArrayList<>();

    public ObjectsManager() {
        this.statistics = Statistics.getInstance();
        populateFromInitProperties();
    }

    private void populateFromInitProperties() {
        int numberOfSimpleEntities = getInitNumberOfSimpleEntities();
        for (int i = 0; i < numberOfSimpleEntities; i++) {
            addObjectToSimulation();
        }
    }

    public void addObjectToSimulation() {
        SimpleEntity entity = new SimpleEntity();
        entity.setRandomPosition();
        entity.setEnergy(10.f + random.nextFloat() * 20.f);
        entity.setAge(random.nextInt(30));
        addObjectToSimulation(entity);
    }

    synchronized public void addObjectToSimulation(SimulationObject object) {
        newSimulationObjectList.add(object);
    }

    public void tick(Environment environment) {
        simulationObjectList.addAll(newSimulationObjectList);
        newSimulationObjectList.clear();
        for (SimulationObject simulationObject : simulationObjectList) {
            simulationObject.tick(environment, this);
            if (simulationObject.isShouldBeRemoved()) {
                removedSimulationObjectList.add(simulationObject);
            }
        }
        simulationObjectList.removeAll(removedSimulationObjectList);
        removedSimulationObjectList.clear();
        for (int i = 0; i < simulationObjectList.size(); ++i) {
            SimulationObject o1 = simulationObjectList.get(i);
            for (int j = i + 1; j < simulationObjectList.size(); ++j) {
                o1.collideWith(simulationObjectList.get(j));
            }
        }
    }

    public void render(Graphics2D g2) {
        for (SimulationObject simulationObject : simulationObjectList) {
            simulationObject.render(g2);
        }
    }
}

