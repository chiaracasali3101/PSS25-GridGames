package it.unibo.gridgames.model.fifteen;

/**
 * Creates the initial boards of the Game of Fifteen.
 */
@FunctionalInterface
public interface FifteenShuffler {

    /**
     * Creates a new shuffled board.
     *
     * @return a board that can be solved, is not already solved and has no moves yet
     */
    FifteenBoard shuffle();
}
