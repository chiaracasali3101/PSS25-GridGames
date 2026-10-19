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

        @Test
    void puzzleHasRightNumberOfEmptyCells() {
        for (final Difficulty difficulty : Difficulty.values()) {
            final SudokuGenerator generator = new SudokuGenerator(new Random(SEED));
            final Grid puzzle = generator.createPuzzle(generator.generateFullGrid(), difficulty);
            assertEquals(difficulty.getCellsToRemove(), countEmptyCells(puzzle));
        }
    }

    @Test
    void puzzleCellsAreFixedOnlyIfFilled() {
        final SudokuGenerator generator = new SudokuGenerator(new Random(SEED));
        final Grid solution = generator.generateFullGrid();
        final Grid puzzle = generator.createPuzzle(solution, Difficulty.MEDIUM);
        for (int row = 0; row < Grid.SIZE; row++) {
            for (int col = 0; col < Grid.SIZE; col++) {
                final Position position = new Position(row, col);
                final Cell cell = puzzle.getCell(position);
                if (cell.isEmpty()) {
                    assertFalse(cell.isFixed());
                } else {
                    assertTrue(cell.isFixed());
                    assertEquals(solution.getCell(position).getValue(), cell.getValue());
                }
            }
        }
    }

    @Test
    void createPuzzleDoesNotChangeSolution() {
        final SudokuGenerator generator = new SudokuGenerator(new Random(SEED));
        final Grid solution = generator.generateFullGrid();
        generator.createPuzzle(solution, Difficulty.HARD);
        assertEquals(0, countEmptyCells(solution));
    }

    private int countEmptyCells(final Grid grid) {
        int count = 0;
        for (int row = 0; row < Grid.SIZE; row++) {
            for (int col = 0; col < Grid.SIZE; col++) {
                if (grid.getCell(new Position(row, col)).isEmpty()) {
                    count++;
                }
            }
        }
        return count;
    }
}
