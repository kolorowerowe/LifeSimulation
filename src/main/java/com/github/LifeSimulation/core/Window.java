package com.github.LifeSimulation.core;

import lombok.Getter;

import javax.swing.JFrame;
import java.awt.Canvas;

import static com.github.LifeSimulation.utils.ResourcesLoader.*;
import static javax.swing.JFrame.EXIT_ON_CLOSE;

@Getter
public class Window extends Canvas {

    private JFrame frame;

    public Window() {
        frame = new JFrame(getApplicationName());
        frame.setSize(getWindowWidth(), getWindowHeight());
        frame.setVisible(true);
        frame.setDefaultCloseOperation(EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.setVisible(true);
        frame.setAutoRequestFocus(true);
    }

    public void addCanvas(Canvas canvas) {
        frame.add(canvas);
    }
}
