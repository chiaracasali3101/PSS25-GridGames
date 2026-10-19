package it.unibo.gridgames.sudoku.model;

/**
 * The difficulty levels of a Sudoku puzzle.
 */
public enum Difficulty {
    /** Few empty cells. */
    EASY(35, 400, 600),
    /** A medium number of empty cells. */
    MEDIUM(45, 700, 1200),
    /** Many empty cells. */
    HARD(55, 1000, 1800);

    private final int cellsToRemove;
    private final int basePoints;
    private final long timeLimitSeconds;

    Difficulty(final int cellsToRemove, final int basePoints, final long timeLimitSeconds) {
        this.cellsToRemove = cellsToRemove;
        this.basePoints = basePoints;
        this.timeLimitSeconds = timeLimitSeconds;
    }

    /**
     * Returns how many cells are emptied at this difficulty.
     *
     * @return the number of cells to remove from a full grid
     */
    public int getCellsToRemove() {
        return cellsToRemove;
    }

    /**
     * Returns the points for a game won quickly and without errors.
     *
     * @return the maximum score at this difficulty
     */
    public int getBasePoints() {
        return basePoints;
    }

    /**
     * Returns the time after which the time bonus is at its minimum.
     *
     * @return the time limit in seconds
     */
    public long getTimeLimitSeconds() {
        return timeLimitSeconds;
    }
}
