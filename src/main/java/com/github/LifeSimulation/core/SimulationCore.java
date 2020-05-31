package com.github.LifeSimulation.core;

import com.github.LifeSimulation.enums.SimulationState;
import com.github.LifeSimulation.environment.Environment;
import com.github.LifeSimulation.utils.ResourcesLoader;
import lombok.extern.log4j.Log4j;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferStrategy;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import static com.github.LifeSimulation.utils.ResourcesLoader.*;

@Log4j
public class SimulationCore extends Canvas implements Runnable {

    private Long nowTime;
    private Long oldTime = 0L;
    private Long lastKeyTime = 0L;

    private Integer ticksForYear = getTicksForYear();
    private Integer ticksInCurrentYear = 0;
    private Integer currentYear = 0;

    private final float CAMERA_MOVE_MULTIPLIER = 0.008f;
    private final float CAMERA_ZOOM_MULTIPLIER = 1.03f;
    private float cameraX = getWorldWidth() * 0.5f;
    private float cameraY = getWorldHeight() * 0.5f;
    private float cameraZoom = (getWorldWidth() + getWorldHeight()) * 1.5f;

    private Thread thread;
    private Boolean running;

    private final ObjectsManager objectsManager = new ObjectsManager();
    private final Environment environment = new Environment(getWorldWidth(), getWorldHeight());
    private final Statistics statistics = Statistics.getInstance();
    private final InputHandler inputHandler = InputHandler.getInstance();

    private SimulationState simulationState;

    private int nextStatisticsPrintDelay = 0;

    private static final Integer MAX_FPS = 60;

    public SimulationCore() {
        simulationState = SimulationState.PAUSED;
        addKeyListener(inputHandler);
    }

    public synchronized void start() {
        thread = new Thread(this);
        thread.start();
        running = true;
        simulationState = SimulationState.RUNNING;
    }

    public synchronized void stop() {
        try {
            thread.join();
            running = false;
        } catch (Exception e) {
            log.error("An error occurred when joining threads ", e);
        }
    }

    public void run() {
        while (running) {
            nowTime = System.currentTimeMillis();
            if ((nowTime - oldTime) > 1000 / MAX_FPS) {
                oldTime = nowTime;
                update();
                render();
                if (nextStatisticsPrintDelay > 0) {
                    nextStatisticsPrintDelay--;
                } else {
                    nextStatisticsPrintDelay = 59;
                    statistics.outputStatistics();
                }
            } else {
                try {
                    Thread.sleep(4);
                    if (simulationState == SimulationState.RUNNING) {
                        tick();
                    }
                } catch (InterruptedException e) {
                    log.error("An error occurred: ", e);
                }
            }

        }
    }

    private void tick() {
        ticksInCurrentYear++;
        if (ticksInCurrentYear >= ticksForYear) {
            ticksInCurrentYear = 0;
            currentYear++;
            statistics.setYear(currentYear);
        }
        environment.growFood(getFoodGrowthDensity());
        objectsManager.tick(environment);
    }

    private void update() {
        inputHandler.update();

        if (nowTime - lastKeyTime > 200) {
            if (inputHandler.isPausePressed()) {
                if (simulationState == SimulationState.RUNNING) {
                    simulationState = SimulationState.PAUSED;
                    log.info("Simulation paused");
                } else if (simulationState == SimulationState.PAUSED) {
                    simulationState = SimulationState.RUNNING;
                    log.info("Simulation running");
                }
                lastKeyTime = nowTime;
            }

            if (inputHandler.isOPressed()) {
                objectsManager.addObjectToSimulation();
                lastKeyTime = nowTime;
            }
        }
        if (inputHandler.isLeftPressed()) {
            cameraX -= CAMERA_MOVE_MULTIPLIER * cameraZoom;
        }
        if (inputHandler.isRightPressed()) {
            cameraX += CAMERA_MOVE_MULTIPLIER * cameraZoom;
        }
        if (inputHandler.isUpPressed()) {
            cameraY -= CAMERA_MOVE_MULTIPLIER * cameraZoom;
        }
        if (inputHandler.isDownPressed()) {
            cameraY += CAMERA_MOVE_MULTIPLIER * cameraZoom;
        }
        if (inputHandler.isQPressed()) {
            cameraZoom *= CAMERA_ZOOM_MULTIPLIER;
        }
        if (inputHandler.isEPressed()) {
            cameraZoom /= CAMERA_ZOOM_MULTIPLIER;
        }
    }

    private void render() {
        BufferStrategy bs = this.getBufferStrategy();
        if (bs == null) {
            this.createBufferStrategy(3);
            return;
        }
        Graphics g = bs.getDrawGraphics();
        Graphics2D g2 = (Graphics2D) g;

        g2.setColor(Color.black);
        g2.fillRect(0, 0, getWidth(), getHeight());

        AffineTransform baseTransform = g2.getTransform();

        int view_width = getWidth() - Statistics.STATISTICS_WIDTH;
        int view_height = getHeight();
        g2.translate(view_width / 2, view_height / 2);
        float scale = (view_height + view_width) / cameraZoom;
        g2.scale(scale, scale);
        g2.translate(-cameraX, -cameraY);

        environment.render(g2);
        objectsManager.render(g2);
        g2.setTransform(baseTransform);

        statistics.render(g2, this, simulationState);

        g.dispose();
        bs.show();
    }
}
