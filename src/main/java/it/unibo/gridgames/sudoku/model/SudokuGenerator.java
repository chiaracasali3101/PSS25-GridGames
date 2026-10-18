package it.unibo.gridgames.sudoku.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * Generates Sudoku grids using backtracking.
 */
public final class SudokuGenerator {

    private static final int CELL_COUNT = Grid.SIZE * Grid.SIZE;

    private final SudokuValidator validator = new SudokuValidator();
    private final Random random;

    /**
     * Creates a generator that produces a different grid each time.
     */
    public SudokuGenerator() {
        this(new Random());
    }

    /**
     * Creates a generator that uses the given source of randomness.
     *
     * @param random the source of randomness used to shuffle the numbers
     */
    public SudokuGenerator(final Random random) {
        this.random = random;
    }

    /**
     * Creates a grid completely filled with numbers that respect the rules.
     *
     * @return a full, valid grid
     */
    public Grid generateFullGrid() {
        final Grid grid = new Grid();
        fill(grid, 0);
        return grid;
    }

    private boolean fill(final Grid grid, final int index) {
        if (index == CELL_COUNT) {
            return true;
        }
        final Position position = new Position(index / Grid.SIZE, index % Grid.SIZE);
        final Cell cell = grid.getCell(position);
        for (final int value : shuffledValues()) {
            if (validator.isValidMove(grid, position, value)) {
                cell.setValue(value);
                if (fill(grid, index + 1)) {
                    return true;
                }
                cell.setValue(0);
            }
        }
        return false;
    }

    private List<Integer> shuffledValues() {
        final List<Integer> values = new ArrayList<>();
        for (int value = 1; value <= Grid.SIZE; value++) {
            values.add(value);
        }
        Collections.shuffle(values, random);
        return values;
    }
}
