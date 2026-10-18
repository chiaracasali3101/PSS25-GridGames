package it.unibo.gridgames.sudoku.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Random;

import org.junit.jupiter.api.Test;

class SudokuGeneratorTest {

    private static final long SEED = 42L;

    @Test
    void fullGridHasNoEmptyCells() {
        final Grid grid = new SudokuGenerator(new Random(SEED)).generateFullGrid();
        for (int row = 0; row < Grid.SIZE; row++) {
            for (int col = 0; col < Grid.SIZE; col++) {
                assertFalse(grid.getCell(new Position(row, col)).isEmpty());
            }
        }
    }

    @Test
    void fullGridRespectsRules() {
        final Grid grid = new SudokuGenerator(new Random(SEED)).generateFullGrid();
        final SudokuValidator validator = new SudokuValidator();
        for (int row = 0; row < Grid.SIZE; row++) {
            for (int col = 0; col < Grid.SIZE; col++) {
                final Position position = new Position(row, col);
                final int value = grid.getCell(position).getValue();
                assertTrue(validator.isValidMove(grid, position, value));
            }
        }
    }

    @Test
    void sameSeedGivesSameGrid() {
        final Grid first = new SudokuGenerator(new Random(SEED)).generateFullGrid();
        final Grid second = new SudokuGenerator(new Random(SEED)).generateFullGrid();
        for (int row = 0; row < Grid.SIZE; row++) {
            for (int col = 0; col < Grid.SIZE; col++) {
                final Position position = new Position(row, col);
                assertEquals(first.getCell(position).getValue(), second.getCell(position).getValue());
            }
        }
    }
}
