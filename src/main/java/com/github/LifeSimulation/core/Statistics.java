package com.github.LifeSimulation.core;


import com.github.LifeSimulation.enums.SimulationState;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.awt.*;

import static com.github.LifeSimulation.utils.ResourcesLoader.*;

@Data
public class Statistics {

    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    private static Statistics INSTANCE;

    private Integer countOfLivingObjects = 0;
    private Integer countOfDiedObjects = 0;
    private Integer year = 0;

    private static final Integer LEFT_POSITION = getWorldWidth() + 10;

    private Statistics() {
    }

    public static Statistics getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new Statistics();
        }
        return INSTANCE;
    }

    public void increaseCountOfLivingObjects(){
        this.countOfLivingObjects++;
    }

    public void decreaseCountOfLivingObjects(){
        this.countOfLivingObjects--;
    }

    public void increaseCountOfDiedObjects(){
        this.countOfDiedObjects++;
    }

    public void increaseYear(){
        this.year++;
    }

    public void render(Graphics g, SimulationState state) {
        g.setColor(Color.lightGray);
        g.fillRect(getWorldWidth(), 0, getWindowWidth(), getWorldHeight());

        g.setColor(Color.black);

        g.setFont(getHeaderFont());
        g.drawString("------ STATISTICS ------", LEFT_POSITION, 30);

        g.setFont(getRegularFont());
        g.drawString("Simulation state: " + state.toString().toLowerCase(), LEFT_POSITION, 60);
        g.drawString("Simulation year: " + year, LEFT_POSITION, 80);

        g.drawString("Living entities: " + countOfLivingObjects, LEFT_POSITION, 140);
        g.drawString("Died entities: " + countOfDiedObjects, LEFT_POSITION, 160);

        g.setFont(getHeaderFont());
        g.drawString("----- INSTRUCTION -----", LEFT_POSITION, 500);

        g.setFont(getRegularFont());
        g.drawString("P - pause/run", LEFT_POSITION, 530);
        g.drawString("A - add new entity", LEFT_POSITION, 550);
    }

    private Font getHeaderFont(){
        return new Font("Arial", Font.BOLD, 16 );
    }

    private Font getRegularFont(){
        return new Font("Arial", Font.PLAIN, 14 );
    }

}
