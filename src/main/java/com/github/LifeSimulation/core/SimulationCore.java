package com.github.LifeSimulation.core;

import com.github.LifeSimulation.enums.SimulationState;
import com.github.LifeSimulation.environment.Environment;
import lombok.extern.log4j.Log4j;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferStrategy;

import static com.github.LifeSimulation.utils.ResourcesLoader.*;

@Log4j
public class SimulationCore extends Canvas implements Runnable {

    private long nowTime;
    private long oldTime = 0L;
    private long lastKeyTime = 0L;

    private float simulationTimeMultiplier = 1.f;

    private int ticksForYear = getTicksForYear();
    private int ticksInCurrentYear = 0;

    private final float CAMERA_MOVE_MULTIPLIER = 0.008f;
    private final float CAMERA_ZOOM_MULTIPLIER = 1.03f;
    private float cameraX = getWorldWidth() * 0.5f;
    private float cameraY = getWorldHeight() * 0.5f;
    private float cameraZoom = (getWorldWidth() + getWorldHeight()) * 1.5f;

    private Thread thread;
    private boolean running;

    private final ObjectsManager objectsManager = new ObjectsManager();
    private final Environment environment = new Environment(getWorldWidth(), getWorldHeight());
    private final Statistics statistics = Statistics.getInstance();
    private final InputHandler inputHandler = InputHandler.getInstance();

    private SimulationState simulationState;

    private int fastForwawrdSkipedYears = 0;
    private boolean fastForwardingShouldRender = false;

    private boolean isLagging = false;

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
        // how much milliseconds since last tick
        float tickAccumulator = 0;
        // how much milliseconds since last frame
        float frameAccumulator = 0;
        // how much milliseconds per one tick
        float tickTime = 1000.0f / getBaseTickRate();
        // how much milliseconds per one frame
        float frameTime = 1000.0f / getFpsTarget();
        // if the thread should sleep, generally it shouldn't sleep if it needs to catch up with computation
        boolean shouldSleep;
        oldTime = System.currentTimeMillis();
        int skippedFrames = 0;
        int skippedFramesThreshold = 4;
        while (running) {
            // increase accumulators depending on how much time passed
            nowTime = System.currentTimeMillis();
            tickAccumulator += nowTime - oldTime;
            frameAccumulator += nowTime - oldTime;
            oldTime = nowTime;
            // limit accumulators to prevent accumulating infinitely if the process is suspended (debugging)
            tickAccumulator = Math.min(tickAccumulator, 1000.f);
            frameAccumulator = Math.min(frameAccumulator, 1000.f);
            // if fast forwarding, always tick
            if (simulationState == SimulationState.FAST_FORWARD) {
                shouldSleep = false;
                isLagging = false;
                tick();
            } else {
                shouldSleep = true;
            }
            // do the tick when needed
            if (tickAccumulator > tickTime * simulationTimeMultiplier || tickAccumulator == 1000.f) {
                if (tickAccumulator == 1000.f) {
                    skippedFramesThreshold = 16;
                } else {
                    if (skippedFramesThreshold > 4) {
                        skippedFramesThreshold--;
                    }
                }
                tickAccumulator -= tickTime * simulationTimeMultiplier;
                if (simulationState == SimulationState.RUNNING) {
                    tick();
                }
                shouldSleep = false;
            }
            // do the update/render when needed
            if (frameAccumulator > frameTime || frameAccumulator == 1000.f) {
                frameAccumulator -= frameTime;
                update();
                // if simulation can't catch up, skip displaying frames
                if (tickAccumulator > tickTime * simulationTimeMultiplier && skippedFrames < skippedFramesThreshold) {
                    skippedFrames++;
                } else {
                    if (simulationState != SimulationState.FAST_FORWARD || fastForwardingShouldRender) {
                        fastForwardingShouldRender = false;
                        render();
                        skippedFrames = 0;
                    }
                }
                shouldSleep = false;
            }
            if (shouldSleep) {
                isLagging = false;
                try {
                    Thread.sleep(1);
                } catch (InterruptedException e) {
                    log.error("An error occurred while sleeping: ", e);
                }
            } else if (simulationState != SimulationState.FAST_FORWARD) {
                isLagging = true;
            }
        }
    }

    private void tick() {
        ticksInCurrentYear++;
        if (ticksInCurrentYear >= ticksForYear) {
            ticksInCurrentYear = 0;
            statistics.increaseYear();
            if (fastForwawrdSkipedYears < getFastForwardSkipYears()) {
                fastForwawrdSkipedYears++;
            } else {
                fastForwawrdSkipedYears = 0;
                fastForwardingShouldRender = true;
            }
        }
        environment.growFood(getFoodGrowthDensity());
        objectsManager.tick(environment);
    }

    private void update() {
        inputHandler.update();

        if (nowTime - lastKeyTime > 200) {
            if (inputHandler.isPausePressed()) {
                simulationState = simulationState.togglePause();
                log.info("Simulation " + simulationState.getDescription());
                lastKeyTime = nowTime;
            }
            if (inputHandler.isOPressed()) {
                objectsManager.addHerbivore();
                lastKeyTime = nowTime;
            }

            if (inputHandler.isLPressed()) {
                simulationState = simulationState.toggleFastForward();
                log.info("Simulation " + simulationState.getDescription());
                lastKeyTime = nowTime;
            }
            if (inputHandler.isPlusPressed()) {
                simulationTimeMultiplier *= 0.5f;
                lastKeyTime = nowTime;
            }
            if (inputHandler.isMinusPressed()) {
                if (simulationTimeMultiplier < 8.f) {
                    simulationTimeMultiplier *= 2.f;
                }
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

        if (simulationState != SimulationState.FAST_FORWARD) {
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
        }

        statistics.render(g2, this, simulationState, 1.f / simulationTimeMultiplier, isLagging);

        g.dispose();
        bs.show();
    }
}
