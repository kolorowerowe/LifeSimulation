package com.github.LifeSimulation.core;

import com.github.LifeSimulation.objects.SimulationObject;
import com.github.LifeSimulation.utils.ResourcesLoader;

import java.util.ArrayList;

public class SpacialIndexGrid {
    private ArrayList<ArrayList<SimulationObject>> grid;
    private float coordMultiplier;
    private float halfCellSize;
    private int widthCells;
    private int heightCells;

    public SpacialIndexGrid(int width, int height) {
        halfCellSize = 0.5f * ResourcesLoader.getSpatialIndexGridCellSize();
        coordMultiplier = 1.f / ResourcesLoader.getSpatialIndexGridCellSize();
        widthCells = (int) Math.ceil(width * coordMultiplier);
        heightCells = (int) Math.ceil(height * coordMultiplier);
        grid = new ArrayList<>(widthCells * heightCells);
        for (int i = 0; i < widthCells * heightCells; ++i) {
            grid.add(new ArrayList<>());
        }
    }

    public interface SimulationObjectOperation {
        void op(SimulationObject simulationObject);
    }

    void addSimulationObject(SimulationObject simulationObject) {
        int cellX = (int) (simulationObject.getPosX() * coordMultiplier);
        int cellY = (int) (simulationObject.getPosY() * coordMultiplier);
        grid.get(cellY * widthCells + cellX).add(simulationObject);
    }

    void removeSimulationObject(SimulationObject simulationObject) {
        int cellX = (int) (simulationObject.getPosX() * coordMultiplier);
        int cellY = (int) (simulationObject.getPosY() * coordMultiplier);
        grid.get(cellY * widthCells + cellX).remove(simulationObject);
    }

    void updateSimulationObject(SimulationObject simulationObject, float previousPosX, float previousPosY) {
        int previousCellX = (int) (previousPosX * coordMultiplier);
        int previousCellY = (int) (previousPosY * coordMultiplier);
        int cellX = (int) (simulationObject.getPosX() * coordMultiplier);
        int cellY = (int) (simulationObject.getPosY() * coordMultiplier);
        if  (previousCellX != cellX || previousCellY != cellY) {
            grid.get(previousCellY * widthCells + previousCellX).remove(simulationObject);
            grid.get(cellY * widthCells + cellX).add(simulationObject);
        }
    }

    void executeAround(float posX, float posY, SimulationObjectOperation operation) {
        int cell1X = (int) (coordMultiplier * (posX - halfCellSize));
        int cell1Y = (int) (coordMultiplier * (posY - halfCellSize));
        if (cell1X >= 0) {
            if (cell1Y >= 0) {
                for (SimulationObject simulationObject : grid.get(cell1Y * widthCells + cell1X)) {
                    operation.op(simulationObject);
                }
            }
            if (cell1Y + 1 < heightCells) {
                for (SimulationObject simulationObject : grid.get(cell1Y * widthCells + cell1X + widthCells)) {
                    operation.op(simulationObject);
                }
            }
        }
        if (cell1X + 1 < widthCells) {
            if (cell1Y >= 0) {
                for (SimulationObject simulationObject : grid.get(cell1Y * widthCells + cell1X + 1)) {
                    operation.op(simulationObject);
                }
            }
            if (cell1Y + 1 < heightCells) {
                for (SimulationObject simulationObject : grid.get(cell1Y * widthCells + cell1X + widthCells + 1)) {
                    operation.op(simulationObject);
                }
            }
        }
    }
}
