package it.unibo.gridgames.model.fifteen;

/**
 * Computes the score of a game of Fifteen, where a lower score is better.
 */
public final class FifteenScore {

    private static final int POINTS_PER_MOVE = 10;

    private FifteenScore() {
    }

    /**
     * Computes the score of a game.
     *
     * @param moves the number of moves made
     * @return the score, which grows with each move
     * @throws IllegalArgumentException if the moves are negative
     */
    public static int compute(final int moves) {
        if (moves < 0) {
            throw new IllegalArgumentException("Moves cannot be negative");
        }
        return moves * POINTS_PER_MOVE;
    }
}
