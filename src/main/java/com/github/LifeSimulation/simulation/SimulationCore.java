package com.github.LifeSimulation.simulation;

import lombok.extern.log4j.Log4j;

import java.awt.*;

@Log4j
public class SimulationCore extends Canvas implements Runnable {

    private long t1;
    private long t2 = 0;
    private Thread thread;
    private boolean running;

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
            if ((t1 - t2) > 1000) {
                this.repaint();
                t2 = t1;
            } else {
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    log.error("En error occurred: ", e);
                }
            }

        }
    }
}
