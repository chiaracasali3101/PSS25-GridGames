package it.unibo.gridgames.sudoku.model;

/**
 * Computes the score of a finished Sudoku game.
 */
public final class SudokuScore {

    private static final double MIN_TIME_BONUS = 0.5;
    private static final double PENALTY_PER_ERROR = 0.1;

    private SudokuScore() {
    }

    /**
     * Computes the score of a game.
     * A game that is not won is worth 0 points.
     *
     * @param difficulty the difficulty of the game
     * @param seconds    how long the game lasted
     * @param errors     how many errors the player made
     * @param status     the final status of the game
     * @return the score, never negative
     * @throws IllegalArgumentException if seconds or errors are negative
     */
    public static int compute(final Difficulty difficulty, final long seconds, final int errors,
            final GameStatus status) {
        if (seconds < 0 || errors < 0) {
            throw new IllegalArgumentException("Seconds and errors cannot be negative");
        }
        if (status != GameStatus.WON) {
            return 0;
        }
        final long limit = difficulty.getTimeLimitSeconds();
        final double timeLeft = Math.max(0, limit - seconds);
        final double timeBonus = MIN_TIME_BONUS + (1 - MIN_TIME_BONUS) * timeLeft / limit;
        final double errorMalus = 1 - PENALTY_PER_ERROR * errors;
        return (int) Math.round(difficulty.getBasePoints() * timeBonus * errorMalus);
    }
}
