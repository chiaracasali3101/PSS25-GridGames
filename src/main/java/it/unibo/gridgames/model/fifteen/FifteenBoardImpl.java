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
        if (row < 0 || row >= SIZE || column < 0 || column >= SIZE) {
            throw new IllegalArgumentException("Cell outside the board");
        }
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

    private static int[][] solvedTiles() {
        final int[][] solved = new int[SIZE][SIZE];
        for (int cell = 0; cell < CELLS; cell++) {
            // Tiles are 1-based, and the last cell wraps around to EMPTY.
            solved[cell / SIZE][cell % SIZE] = (cell + 1) % CELLS;
        }
        return solved;
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
