package com.github.LifeSimulation.simulation;

import com.github.LifeSimulation.utils.ResourcesLoader;

import javax.swing.*;
import java.awt.*;
import java.util.Properties;


import static javax.swing.JFrame.EXIT_ON_CLOSE;

public class Window extends Canvas {
    private JFrame frame;

    public Window(Canvas simulation) {

        Properties props = ResourcesLoader.loadWindowProperties();

        frame = new JFrame("Panel Example");
        frame.setSize(
                Integer.parseInt(props.getProperty("width")),
                Integer.parseInt(props.getProperty("height")));
        frame.setVisible(true);
        frame.setDefaultCloseOperation(EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.setVisible(true);
        frame.add(simulation);
    }
}
