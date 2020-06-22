package com.github.LifeSimulation.core;


import com.github.LifeSimulation.enums.SimulationState;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.awt.*;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

@Data
public class Statistics {

    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    private static Statistics INSTANCE;

    private int countOfLivingObjects = 0;
    private int countOfDeadObjects = 0;
    private int countOfLivingHerbivores = 0;
    private int countOfDeadHerbivores = 0;
    private int countOfLivingPredators = 0;
    private int countOfDeadPredators = 0;
    private int year = 0;

    public static final Integer STATISTICS_WIDTH = 210;

    private PrintWriter fileOutput;

    private Statistics() {
        try {
            fileOutput = new PrintWriter(new FileWriter("statistics.csv"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Statistics getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new Statistics();
        }
        return INSTANCE;
    }

    public void increaseCountOfLivingObjects() {
        this.countOfLivingObjects++;
    }

    public void decreaseCountOfLivingObjects() {
        this.countOfLivingObjects--;
    }

    public void increaseCountOfDeadObjects() {
        this.countOfDeadObjects++;
    }

    public void increaseCountOfLivingHerbivores() {
        this.countOfLivingHerbivores++;
    }

    public void decreaseCountOfLivingHerbivores() {
        this.countOfLivingHerbivores--;
    }

    public void increaseCountOfDeadHerbivores() {
        this.countOfDeadHerbivores++;
    }

    public void increaseCountOfLivingPredators() {
        this.countOfLivingPredators++;
    }

    public void decreaseCountOfLivingPredators() {
        this.countOfLivingPredators--;
    }

    public void increaseCountOfDeadPredators() {
        this.countOfDeadPredators++;
    }

    public void increaseYear() {
        this.year++;
        fileOutput.print(countOfLivingHerbivores);
        fileOutput.print(",");
        fileOutput.println(countOfLivingPredators);
        fileOutput.flush();
    }

    public void render(Graphics g, Canvas canvas, SimulationState state, float simulationSpeed, boolean isLagging) {
        g.setColor(Color.lightGray);
        int left_position = canvas.getWidth() - STATISTICS_WIDTH + 10;
        g.fillRect(left_position - 10, 0, canvas.getWidth(), canvas.getHeight());

        g.setColor(Color.black);

        g.setFont(getHeaderFont());
        g.drawString("----- STATISTICS -----", left_position, 30);

        g.setFont(getRegularFont());
        g.drawString("Simulation state: " + state.getDescription(), left_position, 60);
        g.drawString("Speed: " + simulationSpeed + (isLagging ? " [LAG]" : ""), left_position, 80);
        g.drawString("Simulation year: " + year, left_position, 100);

        g.drawString("Herbivores: " + countOfLivingHerbivores, left_position, 160);
        g.drawString("Predators: " + countOfLivingPredators, left_position, 180);

        g.setFont(getHeaderFont());
        g.drawString("---- INSTRUCTION ----", left_position, 300);

        g.setFont(getRegularFont());
        g.drawString("WASD/Arrows - move", left_position, 330);
        g.drawString("Q, E - zoom", left_position, 350);
        g.drawString("P - pause/run", left_position, 370);
        g.drawString("O - add new entity", left_position, 390);
        g.drawString("L - fast forward", left_position, 410);
        g.drawString("+(=), -(_) - change speed", left_position, 430);
    }

    private Font getHeaderFont() {
        return new Font("Arial", Font.BOLD, 16);
    }

    private Font getRegularFont() {
        return new Font("Arial", Font.PLAIN, 14);
    }

}
