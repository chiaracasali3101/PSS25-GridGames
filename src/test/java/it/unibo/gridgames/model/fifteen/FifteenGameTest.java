package it.unibo.gridgames.model.fifteen;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Iterator;
import java.util.List;
import java.util.Random;
import org.junit.jupiter.api.Test;

class FifteenGameTest {

    private static final int[][] SOLVED = {
        {1, 2, 3, 4},
        {5, 6, 7, 8},
        {9, 10, 11, 12},
        {13, 14, 15, 0},
    };
    private static final int[][] ONE_MOVE_LEFT = {
        {1, 2, 3, 4},
        {5, 6, 7, 8},
        {9, 10, 11, 12},
        {13, 14, 0, 15},
    };
    private static final int[][] ONE_MOVE_LEFT_AFTER_DOWN = {
        {1, 2, 3, 4},
        {5, 6, 7, 8},
        {9, 10, 0, 12},
        {13, 14, 11, 15},
    };
    private static final int[][] THREE_MOVES_LEFT = {
        {1, 2, 3, 4},
        {5, 6, 0, 8},
        {9, 10, 7, 11},
        {13, 14, 15, 12},
    };
    private static final int MOVES_TO_WIN = 3;
    private static final int LAST = FifteenBoard.SIZE - 1;
    private static final long SEED = 42L;

    @Test
    void nullShufflerIsRejected() {
        assertThrows(IllegalArgumentException.class, () -> new FifteenGameImpl(null));
    }

    @Test
    void gameStartsFromTheShuffledBoard() {
        final FifteenGame game = gameFrom(THREE_MOVES_LEFT);
        assertArrayEquals(THREE_MOVES_LEFT, game.getTiles());
        assertEquals(FifteenGameState.PLAYING, game.getState());
        assertEquals(0, game.getMoveCount());
    }

    @Test
    void gameWorksWithTheRealShuffler() {
        final FifteenGame game = new FifteenGameImpl(new FifteenShufflerImpl(new Random(SEED)));
        assertEquals(FifteenGameState.PLAYING, game.getState());
        assertTrue(new FifteenBoardImpl(game.getTiles()).isSolvable());
    }

    @Test
    void legalMoveUpdatesTheBoardAndTheCounter() {
        final FifteenGame game = gameFrom(ONE_MOVE_LEFT);
        assertTrue(game.move(Direction.DOWN));
        assertArrayEquals(ONE_MOVE_LEFT_AFTER_DOWN, game.getTiles());
        assertEquals(1, game.getMoveCount());
        assertEquals(FifteenGameState.PLAYING, game.getState());
    }

    @Test
    void illegalMovesChangeNothing() {
        final FifteenGame game = gameFrom(ONE_MOVE_LEFT);
        assertFalse(game.move(Direction.UP));
        assertFalse(game.moveTile(0, 0));
        assertArrayEquals(ONE_MOVE_LEFT, game.getTiles());
        assertEquals(0, game.getMoveCount());
        assertEquals(FifteenGameState.PLAYING, game.getState());
    }

    @Test
    void invalidMovesAreRejectedWhilePlaying() {
        final FifteenGame game = gameFrom(ONE_MOVE_LEFT);
        assertThrows(IllegalArgumentException.class, () -> game.move(null));
        assertThrows(IllegalArgumentException.class, () -> game.moveTile(-1, 0));
    }

    @Test
    void lastMoveByDirectionWinsTheGame() {
        final FifteenGame game = gameFrom(ONE_MOVE_LEFT);
        assertTrue(game.move(Direction.LEFT));
        assertEquals(FifteenGameState.WON, game.getState());
        assertArrayEquals(SOLVED, game.getTiles());
    }

    @Test
    void lastMoveByTileWinsTheGame() {
        final FifteenGame game = gameFrom(ONE_MOVE_LEFT);
        assertTrue(game.moveTile(LAST, LAST));
        assertEquals(FifteenGameState.WON, game.getState());
        assertArrayEquals(SOLVED, game.getTiles());
    }

    @Test
    void completeGameEndsInVictoryWithTheRightCount() {
        final FifteenGame game = gameFrom(THREE_MOVES_LEFT);
        assertTrue(game.move(Direction.UP));
        assertEquals(FifteenGameState.PLAYING, game.getState());
        assertTrue(game.move(Direction.LEFT));
        assertEquals(FifteenGameState.PLAYING, game.getState());
        assertTrue(game.move(Direction.UP));
        assertEquals(FifteenGameState.WON, game.getState());
        assertEquals(MOVES_TO_WIN, game.getMoveCount());
    }

    @Test
    void wonGameRefusesEveryMove() {
        final FifteenGame game = gameFrom(ONE_MOVE_LEFT);
        game.move(Direction.LEFT);
        assertFalse(game.move(Direction.DOWN));
        assertFalse(game.moveTile(LAST - 1, LAST));
        assertArrayEquals(SOLVED, game.getTiles());
        assertEquals(1, game.getMoveCount());
        assertEquals(FifteenGameState.WON, game.getState());
    }

    @Test
    void returnedTilesAreACopy() {
        final FifteenGame game = gameFrom(ONE_MOVE_LEFT);
        game.getTiles()[0][0] = FifteenBoard.EMPTY;
        assertArrayEquals(ONE_MOVE_LEFT, game.getTiles());
    }

    @Test
    void newGameTakesAnotherBoardAndResetsTheCounter() {
        final FifteenGame game = gameFrom(ONE_MOVE_LEFT, THREE_MOVES_LEFT);
        game.move(Direction.DOWN);
        game.newGame();
        assertArrayEquals(THREE_MOVES_LEFT, game.getTiles());
        assertEquals(0, game.getMoveCount());
        assertEquals(FifteenGameState.PLAYING, game.getState());
    }

    @Test
    void newGameAfterVictoryCanBePlayed() {
        final FifteenGame game = gameFrom(ONE_MOVE_LEFT, ONE_MOVE_LEFT);
        game.move(Direction.LEFT);
        game.newGame();
        assertEquals(FifteenGameState.PLAYING, game.getState());
        assertTrue(game.move(Direction.LEFT));
        assertEquals(FifteenGameState.WON, game.getState());
    }

    private static FifteenGame gameFrom(final int[][]... layouts) {
        final Iterator<int[][]> boards = List.of(layouts).iterator();
        return new FifteenGameImpl(() -> new FifteenBoardImpl(boards.next()));
    }
}
