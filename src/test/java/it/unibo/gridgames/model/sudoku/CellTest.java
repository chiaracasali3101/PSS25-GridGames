package it.unibo.gridgames.model.sudoku;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class CellTest {

    private static final int EMPTY = 0;
    private static final int VALID_VALUE = 5;
    private static final int OTHER_VALUE = 7;
    private static final int TOO_LARGE = 10;

    @Test
    void cellKeepsItsValue() {
        final Cell cell = new Cell(VALID_VALUE, false);

        assertEquals(VALID_VALUE, cell.getValue());
        assertFalse(cell.isEmpty());
    }

    @Test
    void cellWithZeroIsEmpty() {
        final Cell cell = new Cell(EMPTY, false);

        assertTrue(cell.isEmpty());
    }

    @Test
    void cellRemembersIfItIsFixed() {
        assertTrue(new Cell(VALID_VALUE, true).isFixed());
        assertFalse(new Cell(VALID_VALUE, false).isFixed());
    }

    @Test
    void negativeValueIsRejected() {
        assertThrows(IllegalArgumentException.class, () -> new Cell(-1, false));
    }

    @Test
    void valueTooLargeIsRejected() {
        assertThrows(IllegalArgumentException.class, () -> new Cell(TOO_LARGE, false));
    }

    @Test
    void setValueChangesTheValue() {
        final Cell cell = new Cell(EMPTY, false);

        cell.setValue(OTHER_VALUE);

        assertEquals(OTHER_VALUE, cell.getValue());
    }

    @Test
    void setValueToZeroEmptiesTheCell() {
        final Cell cell = new Cell(VALID_VALUE, false);

        cell.setValue(EMPTY);

        assertTrue(cell.isEmpty());
    }

    @Test
    void fixedCellCannotBeModified() {
        final Cell cell = new Cell(VALID_VALUE, true);

        assertThrows(IllegalStateException.class, () -> cell.setValue(OTHER_VALUE));
        assertEquals(VALID_VALUE, cell.getValue());
    }

    @Test
    void setValueRejectsNegativeValue() {
        final Cell cell = new Cell(VALID_VALUE, false);

        assertThrows(IllegalArgumentException.class, () -> cell.setValue(-1));
    }

    @Test
    void setValueRejectsValueTooLarge() {
        final Cell cell = new Cell(VALID_VALUE, false);

        assertThrows(IllegalArgumentException.class, () -> cell.setValue(TOO_LARGE));
    }
}
