package com.github.LifeSimulation.core;

import com.github.LifeSimulation.environment.Environment;
import com.github.LifeSimulation.objects.BreedingEntity;
import com.github.LifeSimulation.objects.Herbivore;
import com.github.LifeSimulation.objects.Predator;
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

    private SpacialIndexGrid spacialIndexGrid = new SpacialIndexGrid(getWorldWidth(), getWorldHeight());

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
        for (int i = 0; i < getInitNumberOfHerbivores(); i++) {
            addHerbivore();
        }
        for (int i = 0; i < getInitNumberOfPredators(); i++) {
            addPredator();
        }
    }

    public void addHerbivore() {
        Herbivore entity = new Herbivore();
        entity.setEnergy(40.f + random.nextFloat() * 20.f);
        entity.setRandomPosition();
        entity.setAge(random.nextInt(50));
        addObjectToSimulation(entity);
    }

    public void addPredator() {
        Predator entity = new Predator();
        entity.setEnergy(100.f + random.nextFloat() * 400.f);
        entity.setRandomPosition();
        entity.setAge(random.nextInt(50));
        addObjectToSimulation(entity);
    }

    synchronized public void addObjectToSimulation(SimulationObject object) {
        newSimulationObjectList.add(object);
        spacialIndexGrid.addSimulationObject(object);
    }

    public void tick(Environment environment) {
        simulationObjectList.addAll(newSimulationObjectList);
        newSimulationObjectList.clear();
        for (SimulationObject simulationObject : simulationObjectList) {
            float previousPosX = simulationObject.getPosX();
            float previousPosY = simulationObject.getPosY();
            simulationObject.tick(environment, this);
            if (simulationObject.isShouldBeRemoved()) {
                removedSimulationObjectList.add(simulationObject);
                spacialIndexGrid.removeSimulationObject(simulationObject);
            } else {
                spacialIndexGrid.updateSimulationObject(simulationObject, previousPosX, previousPosY);
            }
        }
        simulationObjectList.removeAll(removedSimulationObjectList);
        removedSimulationObjectList.clear();
        for (int i = 0; i < simulationObjectList.size(); ++i) {
            SimulationObject o1 = simulationObjectList.get(i);
            spacialIndexGrid.executeForEachAround(o1.getPosX(), o1.getPosY(), (SimulationObject o2) -> {
                if (o1.getId() < o2.getId()) {
                    o1.collideWith(o2);
                }
            });
//            for (int j = i + 1; j < simulationObjectList.size(); ++j) {
//                o1.collideWith(simulationObjectList.get(j));
//            }
        }
    }

    public void render(Graphics2D g2) {
        for (SimulationObject simulationObject : simulationObjectList) {
            simulationObject.render(g2);
        }
    }

    public SpacialIndexGrid getSpacialIndexGrid() {
        return spacialIndexGrid;
    }
}

