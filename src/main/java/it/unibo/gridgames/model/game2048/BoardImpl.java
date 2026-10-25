package it.unibo.gridgames.model.game2048;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.IntStream;

/**
 * Implementation of {@link Board} backed by a two-dimensional int array.
 */
public final class BoardImpl implements Board {

    private static final int EMPTY = 0;

    private final int size;
    private final int[][] cells;

    /**
     * Creates an empty square board.
     *
     * @param size the number of rows and columns
     * @throws IllegalArgumentException if size is not positive
     */
    public BoardImpl(final int size) {
        if (size <= 0) {
            throw new IllegalArgumentException("Board size must be positive");
        }
        this.size = size;
        this.cells = new int[size][size];
    }

    /**
     * Creates a board with the given content. The matrix is copied.
     *
     * @param matrix the cell values indexed by [row][column]
     * @throws IllegalArgumentException if the matrix is empty, not square or contains invalid values
     */
    public BoardImpl(final int[][] matrix) {
        this(Objects.requireNonNull(matrix).length);
        for (int row = 0; row < this.size; row++) {
            if (matrix[row].length != this.size) {
                throw new IllegalArgumentException("Matrix must be square");
            }
            for (int column = 0; column < this.size; column++) {
                setValue(row, column, matrix[row][column]);
            }
        }
    }

    @Override
    public int getSize() {
        return this.size;
    }

    @Override
    public int getValue(final int row, final int column) {
        checkBounds(row, column);
        return this.cells[row][column];
    }

    @Override
    public void setValue(final int row, final int column, final int value) {
        checkBounds(row, column);
        if (!isValidValue(value)) {
            throw new IllegalArgumentException("Invalid tile value: " + value);
        }
        this.cells[row][column] = value;
    }

    @Override
    public boolean isEmpty(final int row, final int column) {
        return getValue(row, column) == EMPTY;
    }

    @Override
    public List<Coordinate> getEmptyCells() {
        return IntStream.range(0, this.size * this.size)
            .mapToObj(index -> new Coordinate(index / this.size, index % this.size))
            .filter(c -> this.cells[c.row()][c.column()] == EMPTY)
            .toList();
    }

    @Override
    public int[][] toMatrix() {
        return Arrays.stream(this.cells)
            .map(int[]::clone)
            .toArray(int[][]::new);
    }

    @Override
    public String toString() {
        return Arrays.deepToString(this.cells);
    }

    private void checkBounds(final int row, final int column) {
        Objects.checkIndex(row, this.size);
        Objects.checkIndex(column, this.size);
    }

    // una potenza di 2 ha un solo bit a 1, quindi value & (value - 1) vale 0
    private static boolean isValidValue(final int value) {
        return value == EMPTY || value > 1 && (value & (value - 1)) == 0;
    }
}
