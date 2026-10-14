package it.unibo.gridgames.sudoku.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class GridTest {

    private static final int VALUE = 5;

    @Test
    void newGridHasAllCellsEmptyAndNotFixed() {
        final Grid grid = new Grid();
        for (int row = 0; row < Grid.SIZE; row++) {
            for (int col = 0; col < Grid.SIZE; col++) {
                final Cell cell = grid.getCell(new Position(row, col));
                assertTrue(cell.isEmpty());
                assertFalse(cell.isFixed());
            }
        }
    }

    @Test
    void changingCellChangesGrid() {
        final Grid grid = new Grid();
        final Position position = new Position(2, 3);
        grid.getCell(position).setValue(VALUE);
        assertEquals(VALUE, grid.getCell(position).getValue());
    }
}
