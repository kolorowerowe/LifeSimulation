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
    private boolean isOPressed = false;
    private boolean isOKPressed = false;
    private boolean isCancelPressed = false;
    private boolean isQPressed = false;
    private boolean isEPressed = false;
    private boolean isLPressed = false;
    private boolean isPlusPressed = false;
    private boolean isMinusPressed = false;

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
        isOPressed = keys[KeyEvent.VK_O];

        isQPressed = keys[KeyEvent.VK_Q];
        isEPressed = keys[KeyEvent.VK_E];

        isLPressed = keys[KeyEvent.VK_L];

        isPlusPressed = keys[KeyEvent.VK_EQUALS];
        isMinusPressed = keys[KeyEvent.VK_MINUS];
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