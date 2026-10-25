package it.unibo.gridgames.model.game2048;

import java.util.Objects;

/**
 * The outcome of sliding and merging a single line of a 2048 board.
 *
 * @param line the cell values after the move
 * @param points the score gained, that is the sum of the values of the merged tiles
 */
public record MergeResult(int[] line, int points) {

    /**
     * Creates a new result. The line is copied.
     *
     * @throws IllegalArgumentException if points is negative
     */
    public MergeResult {
        line = Objects.requireNonNull(line).clone();
        if (points < 0) {
            throw new IllegalArgumentException("Points cannot be negative");
        }
    }

    /**
     * Returns a copy of the cell values after the move.
     *
     * @return the resulting line
     */
    @Override
    public int[] line() {
        return this.line.clone();
    }
}
