package it.unibo.gridgames.model.fifteen;

/**
 * A game of Fifteen, from the shuffled board to the victory.
 */
public interface FifteenGame {

    /**
     * Returns the current state of the game.
     *
     * @return the game state
     */
    FifteenGameState getState();

    /**
     * Returns a copy of the current board.
     *
     * @return the tiles, indexed by row and then by column
     */
    int[][] getTiles();

    /**
     * Returns the number of legal moves made in this game.
     *
     * @return the move count
     */
    int getMoveCount();

    /**
     * Slides a tile towards the given direction.
     *
     * @param direction the direction the tile slides towards
     * @return true if a tile moved, false if no tile can slide that way or the game is won
     * @throws IllegalArgumentException if the game is in progress and the direction is null
     */
    boolean move(Direction direction);

    /**
     * Slides the tile in the given cell into the empty cell.
     *
     * @param row the row index of the tile
     * @param column the column index of the tile
     * @return true if the tile moved, false if it is not adjacent to the empty cell or the game is won
     * @throws IllegalArgumentException if the game is in progress and the cell is outside the board
     */
    boolean moveTile(int row, int column);

    /**
     * Starts a new game on a newly shuffled board.
     */
    void newGame();
}
