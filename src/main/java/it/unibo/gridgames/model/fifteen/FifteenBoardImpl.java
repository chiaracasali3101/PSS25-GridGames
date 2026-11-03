package it.unibo.gridgames.model.fifteen;

import java.util.Arrays;
import java.util.stream.IntStream;

/**
 * Default implementation of {@link FifteenBoard}.
 */
public final class FifteenBoardImpl implements FifteenBoard {

    private static final int CELLS = SIZE * SIZE;

    private final int[][] tiles;
    private int emptyRow;
    private int emptyColumn;
    private int moveCount;

    /**
     * Creates a board in the solved layout.
     */
    public FifteenBoardImpl() {
        this(solvedTiles());
    }

    /**
     * Creates a board with the given layout.
     *
     * @param tiles the tiles indexed by row and then by column, holding each value from
     *     {@link #EMPTY} to 15 exactly once
     * @throws IllegalArgumentException if the layout is not valid
     */
    public FifteenBoardImpl(final int[][] tiles) {
        checkLayout(tiles);
        this.tiles = copyOf(tiles);
        for (int row = 0; row < SIZE; row++) {
            for (int column = 0; column < SIZE; column++) {
                if (this.tiles[row][column] == EMPTY) {
                    this.emptyRow = row;
                    this.emptyColumn = column;
                }
            }
        }
    }

    @Override
    public int getTile(final int row, final int column) {
        checkInside(row, column);
        return this.tiles[row][column];
    }

    @Override
    public int getEmptyRow() {
        return this.emptyRow;
    }

    @Override
    public int getEmptyColumn() {
        return this.emptyColumn;
    }

    @Override
    public int[][] getTiles() {
        return copyOf(this.tiles);
    }

    @Override
    public boolean isSolved() {
        final int[] cells = Arrays.stream(this.tiles).flatMapToInt(Arrays::stream).toArray();
        return IntStream.range(0, CELLS).allMatch(cell -> cells[cell] == solvedValue(cell));
    }

    @Override
    public boolean move(final Direction direction) {
        if (direction == null) {
            throw new IllegalArgumentException("The direction must not be null");
        }
        // The tile that slides is on the opposite side of the empty cell.
        return switch (direction) {
            case UP -> moveIfInside(this.emptyRow + 1, this.emptyColumn);
            case DOWN -> moveIfInside(this.emptyRow - 1, this.emptyColumn);
            case LEFT -> moveIfInside(this.emptyRow, this.emptyColumn + 1);
            case RIGHT -> moveIfInside(this.emptyRow, this.emptyColumn - 1);
        };
    }

    @Override
    public boolean moveTile(final int row, final int column) {
        checkInside(row, column);
        if (Math.abs(row - this.emptyRow) + Math.abs(column - this.emptyColumn) != 1) {
            return false;
        }
        this.tiles[this.emptyRow][this.emptyColumn] = this.tiles[row][column];
        this.tiles[row][column] = EMPTY;
        this.emptyRow = row;
        this.emptyColumn = column;
        this.moveCount++;
        return true;
    }

    @Override
    public int getMoveCount() {
        return this.moveCount;
    }

    private boolean moveIfInside(final int row, final int column) {
        return isInside(row, column) && moveTile(row, column);
    }

    private static boolean isInside(final int row, final int column) {
        return row >= 0 && row < SIZE && column >= 0 && column < SIZE;
    }

    private static void checkInside(final int row, final int column) {
        if (!isInside(row, column)) {
            throw new IllegalArgumentException("Cell outside the board");
        }
    }

    private static int[][] solvedTiles() {
        final int[][] solved = new int[SIZE][SIZE];
        for (int cell = 0; cell < CELLS; cell++) {
            solved[cell / SIZE][cell % SIZE] = solvedValue(cell);
        }
        return solved;
    }

    // Tiles are 1-based, and the last cell wraps around to EMPTY.
    private static int solvedValue(final int cell) {
        return (cell + 1) % CELLS;
    }

    private static void checkLayout(final int[][] tiles) {
        if (tiles == null || tiles.length != SIZE) {
            throw new IllegalArgumentException("The board must have " + SIZE + " rows");
        }
        for (final int[] row : tiles) {
            if (row == null || row.length != SIZE) {
                throw new IllegalArgumentException("Each row must have " + SIZE + " columns");
            }
        }
        final int[] sorted = Arrays.stream(tiles).flatMapToInt(Arrays::stream).sorted().toArray();
        if (!Arrays.equals(sorted, IntStream.range(0, CELLS).toArray())) {
            throw new IllegalArgumentException("Each value must appear exactly once");
        }
    }

    private static int[][] copyOf(final int[][] source) {
        return Arrays.stream(source).map(int[]::clone).toArray(int[][]::new);
    }
}
