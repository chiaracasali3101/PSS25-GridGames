package it.unibo.gridgames.view;

/** 
 * UIConstants defines constants used for UI layout and styling.
 */

public enum UIConstants {
    MARGIN_TOP(30),
    MARGIN_BOTTOM(20),
    FONT_SIZE_TITLE(36),
    GRID_SIZE(10),
    BUTTON_BACK(15);

    private final int value;

    UIConstants(final int value) {
        this.value = value;
    }

    /**
     * Returns the integer value associated with the UI constant.
     * 
     * @return value 
     */
    public int getValue() {
        return value;
    }
}
