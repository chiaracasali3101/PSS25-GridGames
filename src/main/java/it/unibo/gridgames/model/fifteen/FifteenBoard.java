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

    /**
     * Tells whether the tiles are in order, with the empty cell in the last position.
     *
     * @return true if the board is solved
     */
    boolean isSolved();

    /**
     * Slides into the empty cell the tile that can move towards the given direction.
     *
     * @param direction the direction the tile slides towards
     * @return true if a tile moved, false if no tile can slide that way
     * @throws IllegalArgumentException if the direction is null
     */
    boolean move(Direction direction);

    /**
     * Slides the tile in the given cell into the empty cell, if they are adjacent.
     *
     * @param row the row index of the tile
     * @param column the column index of the tile
     * @return true if the tile moved, false if it is not adjacent to the empty cell
     * @throws IllegalArgumentException if the cell is outside the board
     */
    boolean moveTile(int row, int column);

    /**
     * Returns the number of legal moves made so far.
     *
     * @return the move count
     */
    int getMoveCount();

    /**
     * Tells whether the solved layout can be reached from this one with legal moves.
     *
     * @return true if the board can be solved
     */
    boolean isSolvable();
}
