package it.unibo.gridgames.controller;

/** 
 * ControllerConstants defines constants used for UI layout and styling.
 */

public enum ControllerConstants {

    WINDOW_WIDTH(500),
    WINDOW_HEIGHT(400),
    MAX_SIZE(5);

    private final int value;

    ControllerConstants(final int value) {
        this.value = value;
    }

    /**
     * Returns the integer value associated with the controller constants.
     * 
     * @return value 
     */
    public int getValue() {
        return value;
    }
}
