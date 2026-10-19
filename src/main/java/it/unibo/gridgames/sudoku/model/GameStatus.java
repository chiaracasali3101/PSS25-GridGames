package it.unibo.gridgames.sudoku.model;

/**
 * The possible states of a Sudoku game.
 */
public enum GameStatus {
    /** The game is still going on. */
    PLAYING,
    /** The player filled every cell. */
    WON,
    /** The player made too many errors. */
    LOST
}
