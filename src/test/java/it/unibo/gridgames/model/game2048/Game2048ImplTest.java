package it.unibo.gridgames.model.game2048;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.Optional;

import org.junit.jupiter.api.Test;

class Game2048ImplTest {

    private static final int SIZE = 4;
    private static final int TWO = 2;
    private static final int FOUR = 4;
    private static final int EIGHT = 8;

    @Test
    void newGameStartsWithTwoTiles() {
        final Game2048 game = new Game2048Impl();

        final long tiles = Arrays.stream(game.getGrid())
            .flatMapToInt(Arrays::stream)
            .filter(value -> value != 0)
            .count();

        assertEquals(TWO, tiles);
        assertEquals(SIZE, game.getGrid().length);
        assertEquals(0, game.getScore());
        assertEquals(0, game.getMoves());
        assertEquals(GameState2048.PLAYING, game.getState());
    }

    @Test
    void newGameAsksTheSpawnerForTwoTiles() {
        final CountingSpawner spawner = new CountingSpawner();

        new Game2048Impl(spawner);

        assertEquals(TWO, spawner.calls);
    }

    @Test
    void validMoveSpawnsOneTileAndCountsTheMove() {
        final CountingSpawner spawner = new CountingSpawner();
        final Game2048 game = new Game2048Impl(new int[][] {
            {0, 0, 0, 2},
            {0, 0, 0, 0},
            {0, 0, 0, 0},
            {0, 0, 0, 0},
        }, spawner);

        assertTrue(game.move(Direction.LEFT));

        assertArrayEquals(new int[][] {
            {2, 2, 0, 0},
            {0, 0, 0, 0},
            {0, 0, 0, 0},
            {0, 0, 0, 0},
        }, game.getGrid());
        assertEquals(1, spawner.calls);
        assertEquals(1, game.getMoves());
    }

    @Test
    void invalidMoveChangesNothing() {
        final CountingSpawner spawner = new CountingSpawner();
        final int[][] grid = {
            {2, 4, 0, 0},
            {0, 0, 0, 0},
            {0, 0, 0, 0},
            {0, 0, 0, 0},
        };
        final Game2048 game = new Game2048Impl(grid, spawner);

        assertFalse(game.move(Direction.LEFT));

        assertArrayEquals(grid, game.getGrid());
        assertEquals(0, spawner.calls);
        assertEquals(0, game.getMoves());
        assertEquals(0, game.getScore());
    }

    @Test
    void scoreIsTheSumOfAllMerges() {
        final Game2048 game = new Game2048Impl(new int[][] {
            {2, 2, 4, 0},
            {0, 0, 0, 0},
            {0, 0, 0, 0},
            {0, 0, 0, 0},
        }, new CountingSpawner());

        game.move(Direction.LEFT);
        assertEquals(FOUR, game.getScore());

        game.move(Direction.LEFT);
        assertEquals(FOUR + EIGHT, game.getScore());
        assertEquals(TWO, game.getMoves());
    }

    @Test
    void initialGridIsCopied() {
        final int[][] grid = new int[SIZE][SIZE];
        final Game2048 game = new Game2048Impl(grid, new CountingSpawner());

        grid[0][0] = TWO;

        assertEquals(0, game.getGrid()[0][0]);
    }

    @Test
    void changingReturnedGridDoesNotAffectTheGame() {
        final Game2048 game = new Game2048Impl(new int[SIZE][SIZE], new CountingSpawner());

        game.getGrid()[0][0] = TWO;

        assertEquals(0, game.getGrid()[0][0]);
    }

    @Test
    void nullArgumentsAreRejected() {
        final Game2048 game = new Game2048Impl();

        assertThrows(NullPointerException.class, () -> game.move(null));
        assertThrows(NullPointerException.class, () -> new Game2048Impl(null));
        assertThrows(NullPointerException.class, () -> new Game2048Impl(null, new CountingSpawner()));
    }

    /**
     * Predictable spawner: puts a 2 in the first empty cell and counts its calls.
     */
    private static final class CountingSpawner implements TileSpawner {

        private int calls;

        @Override
        public Optional<Coordinate> spawn(final Board board) {
            this.calls++;
            final Optional<Coordinate> cell = board.getEmptyCells().stream().findFirst();
            cell.ifPresent(c -> board.setValue(c.row(), c.column(), TWO));
            return cell;
        }
    }
}
