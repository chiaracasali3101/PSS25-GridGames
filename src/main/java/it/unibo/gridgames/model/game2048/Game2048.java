package it.unibo.gridgames.model.game2048;

/**
 * A single match of 2048: the grid, the score, the number of moves and the state.
 */
public interface Game2048 {

    /**
     * Moves all tiles in the given direction. If the move is valid a new tile
     * is added and the move is counted, otherwise nothing changes.
     *
     * @param direction the direction the tiles are pushed to
     * @return true if the move changed the grid
     * @throws NullPointerException if direction is null
     */
    boolean move(Direction direction);

    /**
     * Takes back the last valid move, restoring grid, score and move counter.
     * Only one move can be taken back, and only while the match is in progress.
     *
     * @return true if the move was taken back
     */
    boolean undo();

    /**
     * Returns a copy of the grid indexed by [row][column], where 0 means empty.
     *
     * @return the current cell values
     */
    int[][] getGrid();

    /**
     * Returns the score, that is the sum of the values of all merged tiles.
     *
     * @return the current score
     */
    int getScore();

    /**
     * Returns the number of valid moves made so far.
     *
     * @return the move counter
     */
    int getMoves();

    /**
     * Returns the state of the match.
     *
     * @return the current state
     */
    GameState2048 getState();
}
