package it.unibo.gridgames.model.fifteen;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.random.RandomGenerator;
import org.junit.jupiter.api.Test;

class FifteenShufflerTest {

    private static final long SEED = 42L;
    private static final long OTHER_SEED = 7L;
    private static final int SAMPLES = 1000;
    private static final int CELLS = FifteenBoard.SIZE * FifteenBoard.SIZE;

    @Test
    void nullGeneratorIsRejected() {
        assertThrows(IllegalArgumentException.class, () -> new FifteenShufflerImpl(null));
    }

    @Test
    void shuffledBoardsAreAlwaysSolvable() {
        final FifteenShuffler shuffler = seededShuffler(SEED);
        for (int sample = 0; sample < SAMPLES; sample++) {
            assertTrue(shuffler.shuffle().isSolvable());
        }
    }

    @Test
    void shuffledBoardsAreNeverAlreadySolved() {
        final FifteenShuffler shuffler = seededShuffler(SEED);
        for (int sample = 0; sample < SAMPLES; sample++) {
            assertFalse(shuffler.shuffle().isSolved());
        }
    }

    @Test
    void solvedPermutationIsDiscarded() {
        final FifteenBoard board = new FifteenShufflerImpl(new SolvedFirstRandom()).shuffle();
        assertFalse(board.isSolved());
        assertTrue(board.isSolvable());
    }

    @Test
    void shuffledBoardStartsWithoutMoves() {
        assertEquals(0, seededShuffler(SEED).shuffle().getMoveCount());
    }

    @Test
    void sameSeedGivesSameBoards() {
        final FifteenShuffler first = seededShuffler(SEED);
        final FifteenShuffler second = seededShuffler(SEED);
        for (int sample = 0; sample < SAMPLES; sample++) {
            assertArrayEquals(first.shuffle().getTiles(), second.shuffle().getTiles());
        }
    }

    @Test
    void differentSeedsGiveDifferentBoards() {
        final int[][] first = seededShuffler(SEED).shuffle().getTiles();
        final int[][] second = seededShuffler(OTHER_SEED).shuffle().getTiles();
        assertFalse(Arrays.deepEquals(first, second));
    }

    @Test
    void consecutiveShufflesGiveDifferentBoards() {
        final FifteenShuffler shuffler = seededShuffler(SEED);
        assertFalse(Arrays.deepEquals(shuffler.shuffle().getTiles(), shuffler.shuffle().getTiles()));
    }

    @Test
    void emptyCellCanEndUpInEveryCell() {
        final FifteenShuffler shuffler = seededShuffler(SEED);
        final Set<Integer> emptyCells = new HashSet<>();
        for (int sample = 0; sample < SAMPLES; sample++) {
            final FifteenBoard board = shuffler.shuffle();
            emptyCells.add(board.getEmptyRow() * FifteenBoard.SIZE + board.getEmptyColumn());
        }
        assertEquals(CELLS, emptyCells.size());
    }

    private static FifteenShuffler seededShuffler(final long seed) {
        return new FifteenShufflerImpl(new Random(seed));
    }

    /**
     * Picks the last allowed index for one whole permutation, which leaves the tiles in the
     * solved order when they are swapped from the last cell backwards, and then always the first.
     */
    private static final class SolvedFirstRandom implements RandomGenerator {

        private int calls;

        @Override
        public long nextLong() {
            return 0;
        }

        @Override
        public int nextInt(final int bound) {
            this.calls++;
            return this.calls < CELLS ? bound - 1 : 0;
        }
    }
}
