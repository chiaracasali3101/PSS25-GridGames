package it.unibo.gridgames.model.sudoku;

/**
 * Represents a position (row and column) in the Sudoku grid.
 */
public final class Position {
    private static final int GRID_SIZE = 9;
    private final int row;
    private final int column;

    /**
     * Creates a new position.
     *
     * @param row the row index, from 0 to 8
     * @param column the column index, from 0 to 8
     * @throws IllegalArgumentException if the position is outside the grid
     */
    public Position(final int row, final int column) {
        if (row < 0 || row >= GRID_SIZE || column < 0 || column >= GRID_SIZE) {
            throw new IllegalArgumentException("Position out of grid");
        }

        this.row = row;
        this.column = column;
    }

    /**
     * Returns the row of this position.
     *
     * @return the row index
     */
    public int getRow() {
        return this.row;
    }

    /**
     * Returns the column of this position.
     *
     * @return the column index
     */
    public int getColumn() {
        return this.column;
    }
}
