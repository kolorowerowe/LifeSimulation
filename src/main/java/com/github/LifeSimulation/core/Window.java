package com.github.LifeSimulation.core;

import com.github.LifeSimulation.utils.ResourcesLoader;
import lombok.Getter;

import javax.swing.JFrame;
import java.awt.Dimension;
import java.awt.Canvas;
import java.util.Properties;


import static javax.swing.JFrame.EXIT_ON_CLOSE;

@Getter
public class Window extends Canvas {

    private JFrame frame;
    private Dimension dimension;

    public Window() {

        Properties props = ResourcesLoader.loadWindowProperties();
        dimension = new Dimension(
                Integer.parseInt(props.getProperty("width")),
                Integer.parseInt(props.getProperty("height"))
        );

        frame = new JFrame("Panel Example");
        frame.setSize(dimension);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.setVisible(true);
    }

    public void addCanvas(Canvas canvas){
        frame.add(canvas);
    }
}
