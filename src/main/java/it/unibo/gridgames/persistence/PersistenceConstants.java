package it.unibo.gridgames.persistence;

/** 
 * UIConstants defines constants used for UI layout and styling.
 */

public enum PersistenceConstants {
    EXPECTED_FIELDS_COUNT(6);

    private final int value;

    PersistenceConstants(final int value) {
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
