package it.unibo.gridgames.model.game2048;

/**
 * The outcome of moving all tiles of a 2048 board in one direction.
 *
 * @param changed true if at least one tile moved or merged, that is the move is valid
 * @param points the score gained by the move
 */
public record MoveResult(boolean changed, int points) { }
