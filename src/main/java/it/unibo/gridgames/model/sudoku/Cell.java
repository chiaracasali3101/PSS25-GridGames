package it.unibo.gridgames.model.sudoku;

/**
 * Represents a single cell of the Sudoku grid.
 */
public final class Cell {
    private static final int EMPTY = 0;
    private static final int MAX_VALUE = 9;

    private int value;
    private final boolean fixed;

    /**
     * Creates a new cell.
     *
     * @param value the number in the cell, or 0 if the cell is empty
     * @param fixed true if the cell belongs to the initial puzzle
     * @throws IllegalArgumentException if the value is not between 0 and 9
     */
    public Cell(final int value, final boolean fixed) {
        if (value < EMPTY || value > MAX_VALUE) {
            throw new IllegalArgumentException("Value must be between 0 and 9");
        }
        this.value = value;
        this.fixed = fixed;
    }

    /**
     * Returns the value of this cell.
     *
     * @return the value, or 0 if the cell is empty
     */
    public int getValue() {
        return this.value;
    }

    /**
     * Changes the value of this cell.
     *
     * @param newValue the new value, or 0 to empty the cell
     * @throws IllegalStateException if the cell is fixed
     * @throws IllegalArgumentException if the value is not between 0 and 9
     */
    public void setValue(final int newValue) {
        if (this.fixed) {
            throw new IllegalStateException("Cannot modify a fixed cell");
        }
        if (newValue < EMPTY || newValue > MAX_VALUE) {
            throw new IllegalArgumentException("Value must be between 0 and 9");
        }
        this.value = newValue;
    }

    /**
     * Tells whether this cell is fixed.
     *
     * @return true if the cell cannot be modified by the player
     */
    public boolean isFixed() {
        return this.fixed;
    }

    /**
     * Tells whether this cell is empty.
     *
     * @return true if the cell has no value
     */
    public boolean isEmpty() {
        return this.value == EMPTY;
    }
}
