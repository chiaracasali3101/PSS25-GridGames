package it.unibo.gridgames.sudoku.model;

/**
 * Checks whether a move respects the Sudoku rules.
 */
public final class SudokuValidator {

    /**
     * Tells whether a value can be placed at the given position.
     *
     * @param grid     the grid to check
     * @param position where the value would go
     * @param value    the value to place
     * @return true if the value is not already in the same row, column or box
     */
    public boolean isValidMove(final Grid grid, final Position position, final int value) {
        return !isInRow(grid, position, value)
                && !isInColumn(grid, position, value)
                && !isInBox(grid, position, value);
    }

    private boolean isInRow(final Grid grid, final Position position, final int value) {
        for (int col = 0; col < Grid.SIZE; col++) {
            if (col != position.getColumn()
                    && grid.getCell(new Position(position.getRow(), col)).getValue() == value) {
                return true;
            }
        }
        return false;
    }

    private boolean isInColumn(final Grid grid, final Position position, final int value) {
        for (int row = 0; row < Grid.SIZE; row++) {
            if (row != position.getRow()
                    && grid.getCell(new Position(row, position.getColumn())).getValue() == value) {
                return true;
            }
        }
        return false;
    }

    private boolean isInBox(final Grid grid, final Position position, final int value) {
        final int startRow = position.getRow() / Grid.BOX_SIZE * Grid.BOX_SIZE;
        final int startCol = position.getColumn() / Grid.BOX_SIZE * Grid.BOX_SIZE;
        for (int row = startRow; row < startRow + Grid.BOX_SIZE; row++) {
            for (int col = startCol; col < startCol + Grid.BOX_SIZE; col++) {
                final boolean isSameCell = row == position.getRow() && col == position.getColumn();
                if (!isSameCell && grid.getCell(new Position(row, col)).getValue() == value) {
                    return true;
                }
            }
        }
        return false;
    }
}
