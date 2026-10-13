package it.unibo.gridgames.model;

/** 
 * ModelConstants defines constants used for UI layout and styling.
 */
public class ModelConstants {

    private final int value;

    ModelConstants(final int value) {
        this.value = value;
    }

    /**
     * Returns the integer value associated with the constant.
     * 
     * @return value 
     */
    public int getValue() {
        return value;
    }
}
