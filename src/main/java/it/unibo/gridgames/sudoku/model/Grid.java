package it.unibo.gridgames.sudoku.model;

/** Represents the 9x9 Sudoku grid.*/
public final class Grid {
    /**
     * Number of rows and columns of the grid.
     */
    public static final int SIZE = 9;

    private final Cell[][] cells = new Cell[SIZE][SIZE];

    /**
     * Creates an empty grid: every cell has value 0 and is not fixed.
     */
    public Grid() {
        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                cells[row][col] = new Cell(0, false);
            }
        }
    }

    /**
     * Returns the cell at the given position.
     *
     * @param position the position of the cell
     * @return the cell at that position
     */
    public Cell getCell(final Position position) {
        return cells[position.getRow()][position.getColumn()];
    }
}
