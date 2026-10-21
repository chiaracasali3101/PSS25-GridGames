package it.unibo.gridgames.model.sudoku;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class PositionTest {

    private static final int VALID_ROW = 2;
    private static final int VALID_COLUMN = 5;
    private static final int OUT_OF_BOUNDS = 9;

    @Test
    void validPositionKeepsRowAndColumn() {
        final Position position = new Position(VALID_ROW, VALID_COLUMN);

        assertEquals(VALID_ROW, position.getRow());
        assertEquals(VALID_COLUMN, position.getColumn());
    }

    @Test
    void negativeRowIsRejected() {
        assertThrows(IllegalArgumentException.class, () -> new Position(-1, 0));
    }

    @Test
    void negativeColumnIsRejected() {
        assertThrows(IllegalArgumentException.class, () -> new Position(0, -1));
    }

    @Test
    void rowTooLargeIsRejected() {
        assertThrows(IllegalArgumentException.class, () -> new Position(OUT_OF_BOUNDS, 0));
    }

    @Test
    void columnTooLargeIsRejected() {
        assertThrows(IllegalArgumentException.class, () -> new Position(0, OUT_OF_BOUNDS));
    }
}
