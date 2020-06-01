package com.github.LifeSimulation.enums;

import lombok.Getter;

public enum SimulationState {

    PAUSED("paused"),
    RUNNING("running"),
    PAUSED_FAST_FORWARD("paused"),
    FAST_FORWARD("fast forward");

    @Getter
    private String description;

    SimulationState(String description) {
        this.description = description;
    }

    public SimulationState togglePause() {
        switch(this) {
            case PAUSED:
                return RUNNING;
            case RUNNING:
                return PAUSED;
            case PAUSED_FAST_FORWARD:
                return FAST_FORWARD;
            default:
                return PAUSED_FAST_FORWARD;
        }
    }

    public SimulationState toggleFastForward() {
        switch(this) {
            case PAUSED:
                return PAUSED_FAST_FORWARD;
            case RUNNING:
                return FAST_FORWARD;
            case PAUSED_FAST_FORWARD:
                return PAUSED;
            default:
                return RUNNING;
        }
    }

}
