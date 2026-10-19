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
        this.random = new Random();
    }

    /**
     * Creates a generator that always produces the same grids for the same seed.
     *
     * @param seed the starting value of the random sequence
     */
    public SudokuGenerator(final long seed) {
        this.random = new Random(seed);
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

    /**
     * Creates a puzzle from a full grid by emptying some of its cells.
     * The cells that keep their value become fixed. The full grid is not modified.
     *
     * @param solution   a full, valid grid
     * @param difficulty how many cells to empty
     * @return a new grid with empty cells for the player to fill
     */
    public Grid createPuzzle(final Grid solution, final Difficulty difficulty) {
        final List<Position> positions = new ArrayList<>();
        for (int row = 0; row < Grid.SIZE; row++) {
            for (int col = 0; col < Grid.SIZE; col++) {
                positions.add(new Position(row, col));
            }
        }
        Collections.shuffle(positions, random);
        final Grid puzzle = new Grid();
        for (int i = difficulty.getCellsToRemove(); i < positions.size(); i++) {
            final Position position = positions.get(i);
            puzzle.setCell(position, new Cell(solution.getCell(position).getValue(), true));
        }
        return puzzle;
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
