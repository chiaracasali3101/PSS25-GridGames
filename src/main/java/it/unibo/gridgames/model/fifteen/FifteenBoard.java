package it.unibo.gridgames.model.fifteen;

/**
 * The 4x4 board of the Game of Fifteen.
 */
public interface FifteenBoard {

    /** Number of rows and columns of the board. */
    int SIZE = 4;

    /** Value of the empty cell. */
    int EMPTY = 0;

    /**
     * Returns the value of a cell.
     *
     * @param row the row index, from 0 to {@link #SIZE} - 1
     * @param column the column index, from 0 to {@link #SIZE} - 1
     * @return the tile in that cell, or {@link #EMPTY}
     * @throws IllegalArgumentException if the cell is outside the board
     */
    int getTile(int row, int column);

    /**
     * Returns the row of the empty cell.
     *
     * @return the row index
     */
    int getEmptyRow();

    /**
     * Returns the column of the empty cell.
     *
     * @return the column index
     */
    int getEmptyColumn();

    /**
     * Returns a copy of the whole board.
     *
     * @return the tiles, indexed by row and then by column
     */
    int[][] getTiles();
}
