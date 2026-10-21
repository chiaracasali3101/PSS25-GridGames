package it.unibo.gridgames.model.sudoku;

/**
 * The possible outcomes of a move.
 */
public enum MoveResult {
    /** The value was right and has been written in the cell. */
    CORRECT,
    /** The value was wrong: the cell stays empty and an error is counted. */
    WRONG,
    /** The move was ignored: the cell is not empty or the game is over. */
    NOT_ALLOWED
}
