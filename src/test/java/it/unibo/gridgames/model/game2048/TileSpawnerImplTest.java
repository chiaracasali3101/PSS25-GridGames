package it.unibo.gridgames.model.game2048;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashSet;
import java.util.Optional;
import java.util.Random;
import java.util.Set;

import org.junit.jupiter.api.Test;

class TileSpawnerImplTest {

    private static final int SIZE = 4;
    private static final int TWO = 2;
    private static final int FOUR = 4;
    private static final long SEED = 42L;
    private static final int RUNS = 1000;
    private static final int MIN_FOURS = 50;
    private static final int MAX_FOURS = 150;

    private final TileSpawner spawner = new TileSpawnerImpl(new Random(SEED));

    @Test
    void spawnFillsExactlyOneEmptyCell() {
        final Board board = new BoardImpl(SIZE);

        final Optional<Coordinate> cell = spawner.spawn(board);

        assertTrue(cell.isPresent());
        assertEquals(SIZE * SIZE - 1, board.getEmptyCells().size());
        assertTrue(isTwoOrFour(board.getValue(cell.get().row(), cell.get().column())));
    }

    @Test
    void spawnUsesTheOnlyEmptyCell() {
        final Board board = new BoardImpl(new int[][] {
            {2, 4, 2, 4},
            {4, 2, 4, 2},
            {2, 4, 0, 4},
            {4, 2, 4, 2},
        });

        final Optional<Coordinate> cell = spawner.spawn(board);

        assertEquals(Optional.of(new Coordinate(2, 2)), cell);
        assertTrue(isTwoOrFour(board.getValue(2, 2)));
    }

    @Test
    void fullBoardSpawnsNothing() {
        final int[][] matrix = {
            {2, 4, 2, 4},
            {4, 2, 4, 2},
            {2, 4, 2, 4},
            {4, 2, 4, 2},
        };
        final Board board = new BoardImpl(matrix);

        assertTrue(spawner.spawn(board).isEmpty());
        assertArrayEquals(matrix, board.toMatrix());
    }

    @Test
    void everyEmptyCellCanBeChosen() {
        final Set<Coordinate> chosen = new HashSet<>();

        for (int run = 0; run < RUNS; run++) {
            final Board board = new BoardImpl(new int[][] {
                {0, 4, 2, 4},
                {4, 2, 4, 2},
                {2, 4, 2, 4},
                {4, 2, 4, 0},
            });
            spawner.spawn(board).ifPresent(chosen::add);
        }

        assertEquals(Set.of(new Coordinate(0, 0), new Coordinate(3, 3)), chosen);
    }

    @Test
    void aboutOneTileInTenIsAFour() {
        int fours = 0;

        for (int run = 0; run < RUNS; run++) {
            final Board board = new BoardImpl(SIZE);
            final Coordinate cell = spawner.spawn(board).orElseThrow();
            final int value = board.getValue(cell.row(), cell.column());
            assertTrue(isTwoOrFour(value));
            if (value == FOUR) {
                fours++;
            }
        }

        assertTrue(fours >= MIN_FOURS && fours <= MAX_FOURS, "fours: " + fours);
    }

    @Test
    void nullArgumentsAreRejected() {
        assertThrows(NullPointerException.class, () -> spawner.spawn(null));
        assertThrows(NullPointerException.class, () -> new TileSpawnerImpl(null));
    }

    private static boolean isTwoOrFour(final int value) {
        return value == TWO || value == FOUR;
    }
}
