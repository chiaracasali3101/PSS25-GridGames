package it.unibo.gridgames.model.fifteen;

import java.util.Arrays;
import java.util.random.RandomGenerator;
import java.util.stream.IntStream;

/**
 * Default implementation of {@link FifteenShuffler}.
 */
public final class FifteenShufflerImpl implements FifteenShuffler {

    private static final int CELLS = FifteenBoard.SIZE * FifteenBoard.SIZE;

    private final RandomGenerator random;

    /**
     * Creates a shuffler.
     *
     * @param random the source of randomness
     * @throws IllegalArgumentException if the generator is null
     */
    public FifteenShufflerImpl(final RandomGenerator random) {
        if (random == null) {
            throw new IllegalArgumentException("The generator must not be null");
        }
        this.random = random;
    }

    @Override
    public FifteenBoard shuffle() {
        FifteenBoard board;
        do {
            board = randomSolvableBoard();
        } while (board.isSolved());
        return board;
    }

    private FifteenBoard randomSolvableBoard() {
        final int[] cells = randomPermutation();
        final FifteenBoard board = toBoard(cells);
        if (board.isSolvable()) {
            return board;
        }
        // Swapping two tiles flips the parity, which makes the layout solvable.
        final int[] tiles = IntStream.range(0, CELLS)
            .filter(cell -> cells[cell] != FifteenBoard.EMPTY)
            .limit(2)
            .toArray();
        swap(cells, tiles[0], tiles[1]);
        return toBoard(cells);
    }

    private int[] randomPermutation() {
        final int[] cells = Arrays.stream(new FifteenBoardImpl().getTiles())
            .flatMapToInt(Arrays::stream)
            .toArray();
        for (int last = CELLS - 1; last > 0; last--) {
            swap(cells, last, this.random.nextInt(last + 1));
        }
        return cells;
    }

    private static FifteenBoard toBoard(final int[] cells) {
        return new FifteenBoardImpl(IntStream.range(0, FifteenBoard.SIZE)
            .mapToObj(row -> Arrays.copyOfRange(cells, row * FifteenBoard.SIZE, (row + 1) * FifteenBoard.SIZE))
            .toArray(int[][]::new));
    }

    private static void swap(final int[] cells, final int first, final int second) {
        final int value = cells[first];
        cells[first] = cells[second];
        cells[second] = value;
    }
}
