package com.github.LifeSimulation.core;

import lombok.AccessLevel;
import lombok.Getter;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

@Getter
public final class InputHandler implements KeyListener {

    @Getter(AccessLevel.NONE)
    private static InputHandler INSTANCE;

    @Getter(AccessLevel.NONE)
    private boolean[] keys = new boolean[1000];

    private boolean isUpPressed = false;
    private boolean isDownPressed = false;
    private boolean isLeftPressed = false;
    private boolean isRightPressed = false;
    private boolean isUsePressed = false;
    private boolean isPausePressed = false;
    private boolean isAPressed = false;
    private boolean isOKPressed = false;
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
        isAPressed = keys[KeyEvent.VK_A];
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