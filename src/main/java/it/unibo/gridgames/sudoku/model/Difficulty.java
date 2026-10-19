package it.unibo.gridgames.sudoku.model;

/**
 * The difficulty levels of a Sudoku puzzle.
 */
public enum Difficulty {
    /** Few empty cells. */
    EASY(35),
    /** A medium number of empty cells. */
    MEDIUM(45),
    /** Many empty cells. */
    HARD(55);

    private final int cellsToRemove;

    Difficulty(final int cellsToRemove) {
        this.cellsToRemove = cellsToRemove;
    }

    /**
     * Returns how many cells are emptied at this difficulty.
     *
     * @return the number of cells to remove from a full grid
     */
    public int getCellsToRemove() {
        return cellsToRemove;
    }
}
