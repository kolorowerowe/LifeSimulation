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

    public interface SimulationObjectOperationWithSquareRadius {
        void op(SimulationObject simulationObject, float radiusSqr);
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
        if (previousCellX != cellX || previousCellY != cellY) {
            grid.get(previousCellY * widthCells + previousCellX).remove(simulationObject);
            grid.get(cellY * widthCells + cellX).add(simulationObject);
        }
    }

    public void executeForEachAround(float posX, float posY, SimulationObjectOperation operation) {
        int cell1X = (int) (coordMultiplier * (posX - halfCellSize));
        int cell1Y = (int) (coordMultiplier * (posY - halfCellSize));
        if (cell1X >= 0) {
            if (cell1Y >= 0) {
                for (SimulationObject o : grid.get(cell1Y * widthCells + cell1X)) {
                    operation.op(o);
                }
            }
            if (cell1Y + 1 < heightCells) {
                for (SimulationObject o : grid.get(cell1Y * widthCells + cell1X + widthCells)) {
                    operation.op(o);
                }
            }
        }
        if (cell1X + 1 < widthCells) {
            if (cell1Y >= 0) {
                for (SimulationObject o : grid.get(cell1Y * widthCells + cell1X + 1)) {
                    operation.op(o);
                }
            }
            if (cell1Y + 1 < heightCells) {
                for (SimulationObject o : grid.get(cell1Y * widthCells + cell1X + widthCells + 1)) {
                    operation.op(o);
                }
            }
        }
    }

    public void executeForEachInRadius(float posX, float posY, float radius, SimulationObjectOperationWithSquareRadius operation) {
        float thisCellX = (float) Math.floor(coordMultiplier * (posX - halfCellSize));
        float thisCellY = (float) Math.floor(coordMultiplier * (posY - halfCellSize));
        float radiusSqr = radius * radius;
        float checkedCellRadiusSqr = radius * coordMultiplier + 0.5f;
        checkedCellRadiusSqr *= checkedCellRadiusSqr;
        int beginCellX = (int) (coordMultiplier * (posX - radius - halfCellSize));
        int beginCellY = (int) (coordMultiplier * (posY - radius - halfCellSize));
        int endCellX = (int) (coordMultiplier * (posX + radius - halfCellSize));
        int endCellY = (int) (coordMultiplier * (posY + radius - halfCellSize));
        for (int j = beginCellY; j < endCellY; ++j) {
            for (int i = beginCellX; i < endCellX; ++i) {
                if (i >= 0 && i < widthCells && j >= 0 && j < heightCells) {
                    float cellX = (float) i;
                    float cellY = (float) j;
                    float cellDistSqr = (thisCellX - cellX) * (thisCellX - cellX) + (thisCellY - cellY) * (thisCellY - cellY);
                    if (cellDistSqr < checkedCellRadiusSqr) {
                        for (SimulationObject o : grid.get(j * widthCells + i)) {
                            float distSqr = (posX - o.getPosX()) * (posX - o.getPosX()) + (posY - o.getPosY()) * (posY - o.getPosY());
                            if (distSqr < radiusSqr) {
                                operation.op(o, distSqr);
                            }
                        }
                    }
                }
            }
        }
    }


    public void executeForEachInRadiusApproximate(float posX, float posY, float radius, SimulationObjectOperation operation) {
        float thisCellX = (float) Math.floor(coordMultiplier * (posX - halfCellSize));
        float thisCellY = (float) Math.floor(coordMultiplier * (posY - halfCellSize));
        float checkedCellRadiusSqr = radius * coordMultiplier + 0.5f;
        checkedCellRadiusSqr *= checkedCellRadiusSqr;
        int beginCellX = (int) (coordMultiplier * (posX - radius - halfCellSize));
        int beginCellY = (int) (coordMultiplier * (posY - radius - halfCellSize));
        int endCellX = (int) (coordMultiplier * (posX + radius - halfCellSize));
        int endCellY = (int) (coordMultiplier * (posY + radius - halfCellSize));
        for (int j = beginCellY; j < endCellY; ++j) {
            for (int i = beginCellX; i < endCellX; ++i) {
                if (i >= 0 && i < widthCells && j >= 0 && j < heightCells) {
                    float cellX = (float) i;
                    float cellY = (float) j;
                    float cellDistSqr = (thisCellX - cellX) * (thisCellX - cellX) + (thisCellY - cellY) * (thisCellY - cellY);
                    if (cellDistSqr < checkedCellRadiusSqr) {
                        for (SimulationObject o : grid.get(j * widthCells + i)) {
                            operation.op(o);
                        }
                    }
                }
            }
        }
    }
}
