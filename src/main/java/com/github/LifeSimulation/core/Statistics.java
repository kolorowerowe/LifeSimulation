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

    private Integer countOfLivingObjects = 0;
    private Integer countOfDeadObjects = 0;
    private Integer year = 0;

    public static final Integer STATISTICS_WIDTH = 200;

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

    public void outputStatistics() {
        fileOutput.print(countOfLivingObjects);
        fileOutput.print(",");
        fileOutput.println(countOfDeadObjects);
        fileOutput.flush();
    }

    public void increaseCountOfLivingObjects(){
        this.countOfLivingObjects++;
    }

    public void decreaseCountOfLivingObjects(){
        this.countOfLivingObjects--;
    }

    public void increaseCountOfDiedObjects(){
        this.countOfDeadObjects++;
    }

    public void increaseYear(){
        this.year++;
    }

    public void render(Graphics g, Canvas canvas, SimulationState state) {
        g.setColor(Color.lightGray);
        int left_position = canvas.getWidth() - STATISTICS_WIDTH + 10;
        g.fillRect(left_position - 10, 0, canvas.getWidth(), canvas.getHeight());

        g.setColor(Color.black);

        g.setFont(getHeaderFont());
        g.drawString("------ STATISTICS ------", left_position, 30);

        g.setFont(getRegularFont());
        g.drawString("Simulation state: " + state.toString().toLowerCase(), left_position, 60);
        g.drawString("Simulation year: " + year, left_position, 80);

        g.drawString("Living entities: " + countOfLivingObjects, left_position, 140);
        g.drawString("Dead entities: " + countOfDeadObjects, left_position, 160);

        g.setFont(getHeaderFont());
        g.drawString("----- INSTRUCTION -----", left_position, 300);

        g.setFont(getRegularFont());
        g.drawString("WASD/Arrows - move", left_position, 330);
        g.drawString("Q, E - zoom", left_position, 350);
        g.drawString("P - pause/run", left_position, 370);
        g.drawString("O - add new entity", left_position, 390);
    }

    private Font getHeaderFont(){
        return new Font("Arial", Font.BOLD, 16 );
    }

    private Font getRegularFont(){
        return new Font("Arial", Font.PLAIN, 14 );
    }

}
