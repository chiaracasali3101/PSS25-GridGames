package it.unibo.gridgames.model.game2048;

/**
 * An immutable pair of row and column identifying a cell of a 2048 board.
 *
 * @param row the row index, starting from 0
 * @param column the column index, starting from 0
 */
public record Coordinate(int row, int column) {

    /**
     * Creates a new coordinate.
     *
     * @throws IllegalArgumentException if row or column is negative
     */
    public Coordinate {
        if (row < 0 || column < 0) {
            throw new IllegalArgumentException("Coordinates cannot be negative");
        }
    }
}
