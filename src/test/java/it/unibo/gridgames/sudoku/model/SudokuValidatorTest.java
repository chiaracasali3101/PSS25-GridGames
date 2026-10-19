package it.unibo.gridgames.sudoku.model;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class SudokuValidatorTest {

    private static final int VALUE = 5;
    private static final int OTHER_VALUE = 6;

    private static final Position CENTER = new Position(4, 4);
    private static final Position ROW_START = new Position(4, 0);
    private static final Position ROW_END = new Position(4, 8);
    private static final Position COLUMN_TOP = new Position(0, 7);
    private static final Position COLUMN_BOTTOM = new Position(8, 7);
    private static final Position BOX_TOP_LEFT = new Position(3, 6);
    private static final Position BOX_BOTTOM_RIGHT = new Position(5, 8);
    private static final Position FIRST_BOX_CORNER = new Position(2, 2);
    private static final Position NEXT_BOX_CORNER = new Position(3, 3);

    private Grid grid;
    private SudokuValidator validator;

    @BeforeEach
    void setUp() {
        grid = new Grid();
        validator = new SudokuValidator();
    }

    @Test
    void anyValueIsValidInEmptyGrid() {
        assertTrue(validator.isValidMove(grid, CENTER, VALUE));
    }

    @Test
    void sameValueInRowIsNotValid() {
        grid.getCell(ROW_START).setValue(VALUE);
        assertFalse(validator.isValidMove(grid, ROW_END, VALUE));
    }

    @Test
    void sameValueInColumnIsNotValid() {
        grid.getCell(COLUMN_TOP).setValue(VALUE);
        assertFalse(validator.isValidMove(grid, COLUMN_BOTTOM, VALUE));
    }

    @Test
    void sameValueInBoxIsNotValid() {
        grid.getCell(BOX_TOP_LEFT).setValue(VALUE);
        assertFalse(validator.isValidMove(grid, BOX_BOTTOM_RIGHT, VALUE));
    }

    @Test
    void differentValueInRowIsValid() {
        grid.getCell(ROW_START).setValue(VALUE);
        assertTrue(validator.isValidMove(grid, ROW_END, OTHER_VALUE));
    }

    @Test
    void cellDoesNotConflictWithItself() {
        grid.getCell(CENTER).setValue(VALUE);
        assertTrue(validator.isValidMove(grid, CENTER, VALUE));
    }

    @Test
    void sameValueInNearbyBoxIsValid() {
        grid.getCell(FIRST_BOX_CORNER).setValue(VALUE);
        assertTrue(validator.isValidMove(grid, NEXT_BOX_CORNER, VALUE));
    }
}
