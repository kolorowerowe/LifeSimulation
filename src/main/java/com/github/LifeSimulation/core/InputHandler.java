package com.github.LifeSimulation.core;

import lombok.Getter;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public final class InputHandler implements KeyListener {

    private static InputHandler INSTANCE;

    private boolean[] keys = new boolean[1000];

    @Getter
    private boolean isUpPressed = false;
    @Getter
    private boolean isDownPressed = false;
    @Getter
    private boolean isLeftPressed = false;
    @Getter
    private boolean isRightPressed = false;
    @Getter
    private boolean isUsePressed = false;
    @Getter
    private boolean isPausePressed = false;
    @Getter
    private boolean isOKPressed = false;
    @Getter
    private boolean isCancelPressed = false;

    private InputHandler() {
    }

    public static InputHandler getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new InputHandler();
        }
        return INSTANCE;
    }

    public void update() {

        isUpPressed = keys[KeyEvent.VK_UP] || keys[KeyEvent.VK_W];
        isDownPressed = keys[KeyEvent.VK_DOWN] || keys[KeyEvent.VK_S];
        isLeftPressed = keys[KeyEvent.VK_LEFT] || keys[KeyEvent.VK_A];
        isRightPressed = keys[KeyEvent.VK_RIGHT] || keys[KeyEvent.VK_D];

        isOKPressed = keys[KeyEvent.VK_Z];
        isCancelPressed = keys[KeyEvent.VK_X];
        isUsePressed = keys[KeyEvent.VK_SPACE];
        isPausePressed = keys[KeyEvent.VK_P];
    }

    @Override
    public void keyTyped(KeyEvent e) {
        //nothing
    }

    @Override
    public void keyPressed(KeyEvent e) {
        keys[e.getKeyCode()] = true;

    }

    @Override
    public void keyReleased(KeyEvent e) {
        keys[e.getKeyCode()] = false;
    }

}