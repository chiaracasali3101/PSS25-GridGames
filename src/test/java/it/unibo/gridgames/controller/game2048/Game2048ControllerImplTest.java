package it.unibo.gridgames.controller.game2048;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.time.InstantSource;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

import org.junit.jupiter.api.Test;

import it.unibo.gridgames.model.game2048.Direction;
import it.unibo.gridgames.model.game2048.Game2048;
import it.unibo.gridgames.model.game2048.Game2048Impl;
import it.unibo.gridgames.model.game2048.GameState2048;
import it.unibo.gridgames.model.score.GameRecordImpl;
import it.unibo.gridgames.model.score.GameType;
import it.unibo.gridgames.persistence.ScoreManager;
import it.unibo.gridgames.view.game2048.Game2048View;

/**
 * Tests for {@link Game2048ControllerImpl}, with a real game and fake view,
 * score manager and clock.
 */
class Game2048ControllerImplTest {

    private static final int SIZE = 4;
    private static final int FOUR = 4;
    private static final int WINNING_SCORE = 2048;
    private static final long THIRTY_SECONDS = 30L;
    private static final String PLAYER = "Jacopo";
    private static final String DISK_ERROR = "disk full";
    private static final int[][] START = topRow(2, 2, 0, 0);
    private static final int[][] AFTER_LEFT = topRow(4, 0, 0, 0);
    private static final int[][] WINNABLE = topRow(1024, 1024, 0, 0);
    private static final int[][] ONE_MOVE_FROM_GAME_OVER = {
        {0, 4, 2, 4},
        {2, 8, 2, 4},
        {4, 2, 4, 2},
        {2, 4, 2, 4},
    };
    private static final int[][] LOST = {
        {4, 2, 4, 2},
        {2, 8, 2, 4},
        {4, 2, 4, 2},
        {2, 4, 2, 4},
    };

    private final FakeView view = new FakeView();
    private final FakeScoreManager scores = new FakeScoreManager();
    private final FakeClock clock = new FakeClock();

    @Test
    void creationShowsTheFirstGame() {
        controllerOn(START);

        assertArrayEquals(START, this.view.grid);
        assertEquals(0, this.view.score);
        assertEquals(0, this.view.moves);
    }

    @Test
    void validMoveRedrawsTheView() {
        final Game2048Controller controller = controllerOn(START);

        controller.move(Direction.LEFT);

        assertArrayEquals(AFTER_LEFT, this.view.grid);
        assertEquals(FOUR, this.view.score);
        assertEquals(1, this.view.moves);
    }

    @Test
    void invalidMoveDoesNotRedraw() {
        final Game2048Controller controller = controllerOn(topRow(2, 4, 0, 0));
        final int redraws = this.view.redraws;

        controller.move(Direction.LEFT);

        assertEquals(redraws, this.view.redraws);
    }

    @Test
    void undoRedrawsThePreviousGrid() {
        final Game2048Controller controller = controllerOn(START);
        controller.move(Direction.LEFT);

        controller.undo();

        assertArrayEquals(START, this.view.grid);
        assertEquals(0, this.view.score);
        assertEquals(0, this.view.moves);
    }

    @Test
    void undoWithoutMovesDoesNotRedraw() {
        final Game2048Controller controller = controllerOn(START);
        final int redraws = this.view.redraws;

        controller.undo();

        assertEquals(redraws, this.view.redraws);
    }

    @Test
    void newGameDrawsAFreshGame() {
        final Game2048Controller controller = controllerOn(START);
        controller.move(Direction.LEFT);

        controller.newGame();

        assertArrayEquals(START, this.view.grid);
        assertEquals(0, this.view.moves);
    }

    @Test
    void theViewIsToldWhenTheGameIsWon() {
        final Game2048Controller controller = controllerOn(WINNABLE);

        controller.move(Direction.LEFT);

        assertEquals(Optional.of(GameState2048.WON), this.view.ended);
    }

    @Test
    void theViewIsToldWhenTheGameIsLost() {
        final Game2048Controller controller = new Game2048ControllerImpl(this.view, this.scores,
            () -> new Game2048Impl(ONE_MOVE_FROM_GAME_OVER, board -> {
                board.setValue(0, SIZE - 1, 2);
                return Optional.empty();
            }), this.clock);

        controller.move(Direction.LEFT);

        assertArrayEquals(LOST, this.view.grid);
        assertEquals(Optional.of(GameState2048.GAME_OVER), this.view.ended);
    }

    @Test
    void theViewIsNotToldAnEndWhileThePlayerIsPlaying() {
        final Game2048Controller controller = controllerOn(START);

        controller.move(Direction.LEFT);

        assertEquals(Optional.empty(), this.view.ended);
    }

    @Test
    void savedRecordDescribesTheFinishedGame() {
        final Game2048Controller controller = controllerOn(WINNABLE);
        controller.move(Direction.LEFT);

        controller.saveScore(PLAYER);

        final GameRecordImpl saved = this.scores.saved.getFirst();
        assertEquals(PLAYER, saved.getPlayerName());
        assertEquals(WINNING_SCORE, saved.getScore());
        assertEquals(1, saved.getMoves());
        assertEquals(GameType.GAME_2048, saved.getGameType());
        assertTrue(this.view.scoreSaved);
    }

    @Test
    void durationGoesFromTheFirstValidMoveToTheEndOfTheGame() {
        final Game2048Controller controller = controllerOn(topRow(1024, 0, 0, 1024));
        this.clock.advance(THIRTY_SECONDS);
        controller.move(Direction.UP);
        this.clock.advance(THIRTY_SECONDS);
        controller.move(Direction.DOWN);
        this.clock.advance(THIRTY_SECONDS);
        controller.move(Direction.LEFT);
        this.clock.advance(THIRTY_SECONDS);

        controller.saveScore(PLAYER);

        assertEquals(THIRTY_SECONDS, this.scores.saved.getFirst().getDurationSeconds());
    }

    @Test
    void undoingTheFirstMoveDoesNotRestartTheTimer() {
        final Game2048Controller controller = controllerOn(WINNABLE);
        controller.move(Direction.DOWN);
        controller.undo();
        this.clock.advance(THIRTY_SECONDS);

        controller.move(Direction.LEFT);
        controller.saveScore(PLAYER);

        assertEquals(THIRTY_SECONDS, this.scores.saved.getFirst().getDurationSeconds());
    }

    @Test
    void savingWhilePlayingIsIgnored() {
        final Game2048Controller controller = controllerOn(START);

        controller.saveScore(PLAYER);

        assertTrue(this.scores.saved.isEmpty());
        assertFalse(this.view.scoreSaved);
    }

    @Test
    void aFinishedGameIsSavedOnlyOnce() {
        final Game2048Controller controller = controllerOn(WINNABLE);
        controller.move(Direction.LEFT);

        controller.saveScore(PLAYER);
        controller.saveScore(PLAYER);

        assertEquals(1, this.scores.saved.size());
    }

    @Test
    void aFailedSaveIsReportedAndCanBeRetried() {
        final Game2048Controller controller = controllerOn(WINNABLE);
        controller.move(Direction.LEFT);
        this.scores.failing = true;

        controller.saveScore(PLAYER);

        assertEquals(DISK_ERROR, this.view.saveError);
        assertFalse(this.view.scoreSaved);

        this.scores.failing = false;
        controller.saveScore(PLAYER);

        assertEquals(1, this.scores.saved.size());
        assertTrue(this.view.scoreSaved);
    }

    @Test
    void anInvalidNameIsReportedAndCanBeRetried() {
        final Game2048Controller controller = controllerOn(WINNABLE);
        controller.move(Direction.LEFT);

        controller.saveScore(" ");

        assertFalse(this.view.saveError.isEmpty());
        assertTrue(this.scores.saved.isEmpty());

        controller.saveScore(PLAYER);

        assertEquals(1, this.scores.saved.size());
    }

    @Test
    void aNewGameCanBeSavedAndTimedAgain() {
        final Game2048Controller controller = controllerOn(WINNABLE);
        controller.move(Direction.LEFT);
        controller.saveScore(PLAYER);
        this.clock.advance(THIRTY_SECONDS);

        controller.newGame();
        controller.move(Direction.LEFT);
        controller.saveScore(PLAYER);

        assertEquals(2, this.scores.saved.size());
        assertEquals(0L, this.scores.saved.getLast().getDurationSeconds());
    }

    @Test
    void nullArgumentsAreRejected() {
        final Supplier<Game2048> games = Game2048Impl::new;

        assertThrows(NullPointerException.class,
            () -> new Game2048ControllerImpl(null, this.scores, games, this.clock));
        assertThrows(NullPointerException.class,
            () -> new Game2048ControllerImpl(this.view, null, games, this.clock));
        assertThrows(NullPointerException.class,
            () -> new Game2048ControllerImpl(this.view, this.scores, null, this.clock));
        assertThrows(NullPointerException.class,
            () -> new Game2048ControllerImpl(this.view, this.scores, games, null));
    }

    // partita reale sulla griglia data, senza tessere casuali
    private Game2048Controller controllerOn(final int[][] grid) {
        return new Game2048ControllerImpl(this.view, this.scores,
            () -> new Game2048Impl(grid, board -> Optional.empty()), this.clock);
    }

    private static int[][] topRow(final int... values) {
        final int[][] grid = new int[SIZE][SIZE];
        grid[0] = values.clone();
        return grid;
    }

    /**
     * View that remembers what the controller asked to show.
     */
    private static final class FakeView implements Game2048View {

        private int[][] grid = new int[0][0];
        private int score;
        private int moves;
        private int redraws;
        private Optional<GameState2048> ended = Optional.empty();
        private boolean scoreSaved;
        private String saveError = "";

        @Override
        public void showGame(final int[][] newGrid, final int newScore, final int newMoves) {
            this.grid = newGrid.clone();
            this.score = newScore;
            this.moves = newMoves;
            this.redraws++;
        }

        @Override
        public void showGameEnded(final GameState2048 result) {
            this.ended = Optional.of(result);
        }

        @Override
        public void showScoreSaved() {
            this.scoreSaved = true;
        }

        @Override
        public void showSaveError(final String message) {
            this.saveError = message;
        }
    }

    /**
     * Score manager that keeps records in memory and can simulate a write failure.
     */
    private static final class FakeScoreManager implements ScoreManager {

        private final List<GameRecordImpl> saved = new ArrayList<>();
        private boolean failing;

        @Override
        public void saveScore(final GameRecordImpl record) throws IOException {
            if (this.failing) {
                throw new IOException(DISK_ERROR);
            }
            this.saved.add(record);
        }

        @Override
        public List<GameRecordImpl> loadAllScores() {
            return List.copyOf(this.saved);
        }

        @Override
        public List<GameRecordImpl> getTopScores(final GameType gameType, final int limit) {
            return List.copyOf(this.saved);
        }

        @Override
        public int getNumberOfGamesPlayed(final GameType gameType) {
            return this.saved.size();
        }

        @Override
        public double getAverageScore(final GameType gameType) {
            return 0;
        }

        @Override
        public GameRecordImpl getHighestScore(final GameType gameType) {
            return this.saved.getFirst();
        }
    }

    /**
     * Clock that moves only when the test asks it to.
     */
    private static final class FakeClock implements InstantSource {

        private Instant now = Instant.EPOCH;

        @Override
        public Instant instant() {
            return this.now;
        }

        private void advance(final long seconds) {
            this.now = this.now.plus(Duration.ofSeconds(seconds));
        }
    }
}
