package it.unibo.gridgames.model.game2048;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.random.RandomGenerator;

/**
 * Implementation of {@link TileSpawner} following the 2048 rules: the cell is
 * chosen at random among the empty ones and the tile is a 2 with probability 0.9,
 * otherwise a 4.
 */
public final class TileSpawnerImpl implements TileSpawner {

    private static final int TWO = 2;
    private static final int FOUR = 4;
    private static final double FOUR_PROBABILITY = 0.1;

    private final RandomGenerator random;

    /**
     * Creates a spawner that uses the default random generator.
     */
    public TileSpawnerImpl() {
        this(RandomGenerator.getDefault());
    }

    /**
     * Creates a spawner that uses the given random generator.
     *
     * @param random the source of randomness, for example a seeded one in tests
     */
    public TileSpawnerImpl(final RandomGenerator random) {
        this.random = Objects.requireNonNull(random);
    }

    @Override
    public Optional<Coordinate> spawn(final Board board) {
        final List<Coordinate> emptyCells = board.getEmptyCells();
        if (emptyCells.isEmpty()) {
            return Optional.empty();
        }
        final Coordinate cell = emptyCells.get(this.random.nextInt(emptyCells.size()));
        final int value = this.random.nextDouble() < FOUR_PROBABILITY ? FOUR : TWO;
        board.setValue(cell.row(), cell.column(), value);
        return Optional.of(cell);
    }
}
