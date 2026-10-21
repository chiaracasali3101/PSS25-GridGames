package it.unibo.gridgames.model.game2048;

import java.util.List;

/**
 * A square grid of cells for the 2048 game.
 * Each cell holds either 0 (empty) or the value of a tile, which is a power of two.
 */
public interface Board {

    /**
     * Returns the number of rows (and columns) of the board.
     *
     * @return the side length of the board
     */
    int getSize();

    /**
     * Returns the value stored in a cell.
     *
     * @param row the row index
     * @param column the column index
     * @return the tile value, or 0 if the cell is empty
     * @throws IndexOutOfBoundsException if the cell is outside the board
     */
    int getValue(int row, int column);

    /**
     * Stores a value in a cell.
     *
     * @param row the row index
     * @param column the column index
     * @param value the tile value, or 0 to empty the cell
     * @throws IndexOutOfBoundsException if the cell is outside the board
     * @throws IllegalArgumentException if the value is neither 0 nor a power of two greater than 1
     */
    void setValue(int row, int column, int value);

    /**
     * Tells whether a cell contains no tile.
     *
     * @param row the row index
     * @param column the column index
     * @return true if the cell is empty
     * @throws IndexOutOfBoundsException if the cell is outside the board
     */
    boolean isEmpty(int row, int column);

    /**
     * Returns the coordinates of all empty cells, in row-major order.
     *
     * @return an unmodifiable list of empty cell coordinates
     */
    List<Coordinate> getEmptyCells();

    /**
     * Returns a copy of the board content as a matrix indexed by [row][column].
     * Changing the returned matrix does not affect the board.
     *
     * @return a copy of the cell values
     */
    int[][] toMatrix();
}
