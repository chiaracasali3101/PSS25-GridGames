package it.unibo.gridgames.model.game2048;

/**
 * The possible states of a 2048 game.
 */
public enum GameState2048 {
    /** The game is still going on. */
    PLAYING,
    /** A tile with the winning value has been created. */
    WON,
    /** The board is full and no merge is possible. */
    GAME_OVER
}
