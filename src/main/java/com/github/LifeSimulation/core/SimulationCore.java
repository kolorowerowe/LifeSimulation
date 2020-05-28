package com.github.LifeSimulation.core;

import lombok.extern.log4j.Log4j;

import java.awt.Canvas;
import java.awt.Graphics;
import java.awt.Color;
import java.awt.image.BufferStrategy;

import static com.github.LifeSimulation.utils.ResourcesLoader.*;

@Log4j
public class SimulationCore extends Canvas implements Runnable {

    private Long nowTime;
    private Long oldTime = 0L;
    private Long lastKeyTime = 0L;

    private Integer ticksForYear = getTicksForYear();
    private Integer ticksInCurrentYear = 0;
    private Integer currentYear = 0;

    private Thread thread;
    private Boolean running;

    private final ObjectsHandler objectsHandler = new ObjectsHandler();
    private final Statistics statistics = Statistics.getInstance();
    private final InputHandler inputHandler = InputHandler.getInstance();

    private SimulationState simulationState;

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
            log.error("En error occurred when joining threads ", e);
        }
    }

    public void run() {
        while (running) {
            nowTime = System.currentTimeMillis();
            if ((nowTime - oldTime) > 1000 / MAX_FPS) {
                oldTime = nowTime;
                update();
                render();
            } else {
                try {
                    Thread.sleep(4);
                    if (simulationState == SimulationState.RUNNING) {
                        tick();
                    }
                } catch (InterruptedException e) {
                    log.error("En error occurred: ", e);
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
        objectsHandler.tick();
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
        }

    }

    private void render() {
        BufferStrategy bs = this.getBufferStrategy();
        if (bs == null) {
            this.createBufferStrategy(3);
            return;
        }
        Graphics g = bs.getDrawGraphics();

        g.setColor(Color.black);
        g.fillRect(0, 0, getWindowWidth(), getWindowHeight());

        statistics.render(g, simulationState);
        objectsHandler.render(g);

        g.dispose();
        bs.show();
    }
}
