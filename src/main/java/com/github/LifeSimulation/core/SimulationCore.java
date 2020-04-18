package com.github.LifeSimulation.core;

import lombok.extern.log4j.Log4j;

import java.awt.Canvas;
import java.awt.Graphics;
import java.awt.Color;
import java.awt.image.BufferStrategy;

import static com.github.LifeSimulation.utils.ResourcesLoader.getWindowHeight;
import static com.github.LifeSimulation.utils.ResourcesLoader.getWindowWidth;

@Log4j
public class SimulationCore extends Canvas implements Runnable {

    private Long t1;
    private Long t2 = 0L;
    private Thread thread;
    private Boolean running;

    private ObjectsHandler objectsHandler;

    private static final Integer MAX_FPS = 60;

    public SimulationCore() {
        this.objectsHandler = new ObjectsHandler();
    }

    public synchronized void start()
    {
        thread = new Thread(this);
        thread.start();
        running = true;
    }

    public synchronized void stop()
    {
        try{
            thread.join();
            running = false;
        }
        catch (Exception e) {
            log.error("En error occurred when joining threads ",e);
        }
    }

    public void run() {
        while (running) {
            t1 = System.currentTimeMillis();
            if ((t1 - t2) > 1000/MAX_FPS) {
                t2 = t1;
                render();
            } else {
                try {
                    Thread.sleep(4);
                    tick();
                } catch (InterruptedException e) {
                    log.error("En error occurred: ", e);
                }
            }

        }
    }

    private void tick(){
        objectsHandler.tick();
    }

    private void render(){
        BufferStrategy bs = this.getBufferStrategy();
        if(bs==null){
            this.createBufferStrategy(3);
            return;
        }
        Graphics g = bs.getDrawGraphics();

        g.setColor(Color.black);
        g.fillRect(0,0, getWindowWidth(), getWindowHeight());

        objectsHandler.render(g);

        g.dispose();
        bs.show();
    }
}
