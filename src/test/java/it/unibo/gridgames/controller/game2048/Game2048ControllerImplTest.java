package it.unibo.gridgames.controller.game2048;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.time.Duration;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Queue;
import java.util.concurrent.Executor;
import java.util.function.Consumer;
import java.util.function.Supplier;

import org.junit.jupiter.api.Test;

import it.unibo.gridgames.model.game2048.Direction;
import it.unibo.gridgames.model.game2048.Game2048;
import it.unibo.gridgames.model.game2048.Game2048Impl;
import it.unibo.gridgames.model.game2048.GameState2048;
import it.unibo.gridgames.model.score.GameRecordImpl;
import it.unibo.gridgames.model.score.GameType;
import it.unibo.gridgames.model.timer.GameTimer;
import it.unibo.gridgames.persistence.ScoreManager;
import it.unibo.gridgames.view.game2048.Game2048View;

/**
 * Tests for {@link Game2048ControllerImpl}, with a real game and fake view,
 * score manager and timer.
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
    private static final int[][] WINNABLE_IN_TWO_MOVES = topRow(1024, 0, 0, 1024);
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
    private final FakeTimer timer = new FakeTimer();
    private final BackToMenu backToMenu = new BackToMenu();
    private final QueuedExecutor background = new QueuedExecutor();
    private final QueuedExecutor ui = new QueuedExecutor();

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
        final Game2048Controller controller = new Game2048ControllerImpl(this.view, this.scores, this.backToMenu,
            () -> new Game2048Impl(ONE_MOVE_FROM_GAME_OVER, board -> {
                board.setValue(0, SIZE - 1, 2);
                return Optional.empty();
            }), this.timer, Runnable::run, Runnable::run);

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
        final Game2048Controller controller = controllerOn(WINNABLE_IN_TWO_MOVES);
        this.timer.advance(THIRTY_SECONDS);
        controller.move(Direction.UP);
        this.timer.advance(THIRTY_SECONDS);
        controller.move(Direction.DOWN);
        this.timer.advance(THIRTY_SECONDS);
        controller.move(Direction.LEFT);
        this.timer.advance(THIRTY_SECONDS);

        controller.saveScore(PLAYER);

        assertEquals(THIRTY_SECONDS, this.scores.saved.getFirst().getDurationSeconds());
    }

    @Test
    void undoingTheFirstMoveDoesNotRestartTheTimer() {
        final Game2048Controller controller = controllerOn(WINNABLE);
        controller.move(Direction.DOWN);
        controller.undo();
        this.timer.advance(THIRTY_SECONDS);

        controller.move(Direction.LEFT);
        controller.saveScore(PLAYER);

        assertEquals(THIRTY_SECONDS, this.scores.saved.getFirst().getDurationSeconds());
    }

    @Test
    void creationShowsTheTimeAtZero() {
        controllerOn(START);

        assertEquals(Optional.of(Duration.ZERO), this.view.time);
        assertFalse(this.timer.isRunning());
    }

    @Test
    void anInvalidMoveDoesNotStartTheTimer() {
        final Game2048Controller controller = controllerOn(topRow(2, 4, 0, 0));

        controller.move(Direction.LEFT);

        assertFalse(this.timer.isRunning());
    }

    @Test
    void theFirstValidMoveStartsTheTimer() {
        final Game2048Controller controller = controllerOn(START);

        controller.move(Direction.LEFT);

        assertTrue(this.timer.isRunning());
    }

    @Test
    void eachTickShowsTheTimeOnTheUiThread() {
        final Game2048Controller controller = controllerWithQueuesOn(START);
        controller.move(Direction.LEFT);
        this.timer.advance(THIRTY_SECONDS);

        this.timer.tick();

        assertEquals(Optional.of(Duration.ZERO), this.view.time);
        this.ui.runAll();
        assertEquals(Optional.of(Duration.ofSeconds(THIRTY_SECONDS)), this.view.time);
    }

    @Test
    void aTickArrivingAfterANewGameDoesNotShowTheOldTime() {
        final Game2048Controller controller = controllerWithQueuesOn(START);
        controller.move(Direction.LEFT);
        this.timer.advance(THIRTY_SECONDS);
        this.timer.tick();

        controller.newGame();
        this.ui.runAll();

        assertEquals(Optional.of(Duration.ZERO), this.view.time);
    }

    @Test
    void theEndOfTheGameStopsTheTimerAndShowsTheFinalTime() {
        final Game2048Controller controller = controllerOn(WINNABLE_IN_TWO_MOVES);
        controller.move(Direction.DOWN);
        this.timer.advance(THIRTY_SECONDS);

        controller.move(Direction.LEFT);

        assertFalse(this.timer.isRunning());
        assertEquals(Optional.of(Duration.ofSeconds(THIRTY_SECONDS)), this.view.time);
    }

    @Test
    void newGameResetsTheTime() {
        final Game2048Controller controller = controllerOn(START);
        controller.move(Direction.LEFT);
        this.timer.advance(THIRTY_SECONDS);

        controller.newGame();

        assertFalse(this.timer.isRunning());
        assertEquals(Duration.ZERO, this.timer.getElapsed());
        assertEquals(Optional.of(Duration.ZERO), this.view.time);
    }

    @Test
    void goingBackToTheMenuStopsTheTimer() {
        final Game2048Controller controller = controllerOn(START);
        controller.move(Direction.LEFT);

        this.view.backListener.run();

        assertFalse(this.timer.isRunning());
        assertTrue(this.backToMenu.called);
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
        this.timer.advance(THIRTY_SECONDS);

        controller.newGame();
        controller.move(Direction.LEFT);
        controller.saveScore(PLAYER);

        assertEquals(2, this.scores.saved.size());
        assertEquals(0L, this.scores.saved.getLast().getDurationSeconds());
    }

    @Test
    void startShowsTheScreenWithAFreshGame() {
        final Game2048Controller controller = controllerOn(START);
        controller.move(Direction.LEFT);

        controller.start();

        assertEquals(1, this.view.shown);
        assertArrayEquals(START, this.view.grid);
        assertEquals(0, this.view.moves);
    }

    @Test
    void creationDoesNotShowTheScreen() {
        controllerOn(START);

        assertEquals(0, this.view.shown);
    }

    @Test
    void directionsChosenInTheViewMoveTheTiles() {
        controllerOn(START);

        this.view.directionListener.accept(Direction.LEFT);

        assertArrayEquals(AFTER_LEFT, this.view.grid);
    }

    @Test
    void backInTheViewReturnsToTheMenu() {
        controllerOn(START);

        this.view.backListener.run();

        assertTrue(this.backToMenu.called);
    }

    @Test
    void newGameInTheViewStartsAFreshGame() {
        final Game2048Controller controller = controllerOn(START);
        controller.move(Direction.LEFT);

        this.view.newGameListener.run();

        assertArrayEquals(START, this.view.grid);
        assertEquals(0, this.view.moves);
    }

    @Test
    void undoInTheViewTakesBackTheLastMove() {
        final Game2048Controller controller = controllerOn(START);
        controller.move(Direction.LEFT);

        this.view.undoListener.run();

        assertArrayEquals(START, this.view.grid);
        assertEquals(0, this.view.score);
    }

    @Test
    void theNameEnteredInTheViewSavesTheFinishedGame() {
        final Game2048Controller controller = controllerOn(WINNABLE);
        controller.move(Direction.LEFT);

        this.view.saveListener.accept(PLAYER);

        assertEquals(PLAYER, this.scores.saved.getFirst().getPlayerName());
        assertTrue(this.view.scoreSaved);
    }

    @Test
    void theScoreIsWrittenInTheBackgroundAndShownOnTheUiThread() {
        final Game2048Controller controller = controllerWithQueuesOn(WINNABLE);
        controller.move(Direction.LEFT);

        controller.saveScore(PLAYER);

        assertTrue(this.scores.saved.isEmpty());
        this.background.runAll();
        assertEquals(1, this.scores.saved.size());
        assertFalse(this.view.scoreSaved);
        this.ui.runAll();
        assertTrue(this.view.scoreSaved);
    }

    @Test
    void aBackgroundFailureIsShownOnTheUiThread() {
        final Game2048Controller controller = controllerWithQueuesOn(WINNABLE);
        controller.move(Direction.LEFT);
        this.scores.failing = true;

        controller.saveScore(PLAYER);
        this.background.runAll();

        assertTrue(this.view.saveError.isEmpty());
        this.ui.runAll();
        assertEquals(DISK_ERROR, this.view.saveError);
    }

    @Test
    void aSaveRequestedWhileSavingIsIgnored() {
        final Game2048Controller controller = controllerWithQueuesOn(WINNABLE);
        controller.move(Direction.LEFT);

        controller.saveScore(PLAYER);
        controller.saveScore(PLAYER);
        this.background.runAll();
        this.ui.runAll();

        assertEquals(1, this.scores.saved.size());
    }

    @Test
    void aSaveEndingAfterANewGameDoesNotAffectTheNewGame() {
        final Game2048Controller controller = controllerWithQueuesOn(WINNABLE);
        controller.move(Direction.LEFT);
        controller.saveScore(PLAYER);

        controller.newGame();
        this.background.runAll();
        this.ui.runAll();

        assertFalse(this.view.scoreSaved);
        controller.move(Direction.LEFT);
        controller.saveScore(PLAYER);
        this.background.runAll();
        this.ui.runAll();
        assertEquals(2, this.scores.saved.size());
        assertTrue(this.view.scoreSaved);
    }

    @Test
    void nullArgumentsAreRejected() {
        final Supplier<Game2048> games = Game2048Impl::new;
        final Executor now = Runnable::run;

        assertThrows(NullPointerException.class,
            () -> new Game2048ControllerImpl(null, this.scores, this.backToMenu, games, this.timer, now, now));
        assertThrows(NullPointerException.class,
            () -> new Game2048ControllerImpl(this.view, null, this.backToMenu, games, this.timer, now, now));
        assertThrows(NullPointerException.class,
            () -> new Game2048ControllerImpl(this.view, this.scores, null, games, this.timer, now, now));
        assertThrows(NullPointerException.class,
            () -> new Game2048ControllerImpl(this.view, this.scores, this.backToMenu, null, this.timer, now, now));
        assertThrows(NullPointerException.class,
            () -> new Game2048ControllerImpl(this.view, this.scores, this.backToMenu, games, null, now, now));
        assertThrows(NullPointerException.class,
            () -> new Game2048ControllerImpl(this.view, this.scores, this.backToMenu, games, this.timer, null, now));
        assertThrows(NullPointerException.class,
            () -> new Game2048ControllerImpl(this.view, this.scores, this.backToMenu, games, this.timer, now, null));
    }

    // partita reale sulla griglia data, senza tessere casuali; salvataggio immediato
    private Game2048Controller controllerOn(final int[][] grid) {
        return new Game2048ControllerImpl(this.view, this.scores, this.backToMenu,
            () -> new Game2048Impl(grid, board -> Optional.empty()), this.timer, Runnable::run, Runnable::run);
    }

    // come controllerOn, ma il salvataggio e l'aggiornamento della view aspettano runAll()
    private Game2048Controller controllerWithQueuesOn(final int[][] grid) {
        return new Game2048ControllerImpl(this.view, this.scores, this.backToMenu,
            () -> new Game2048Impl(grid, board -> Optional.empty()), this.timer, this.background, this.ui);
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
        private Optional<Duration> time = Optional.empty();
        private Optional<GameState2048> ended = Optional.empty();
        private boolean scoreSaved;
        private String saveError = "";
        private int shown;
        private Consumer<Direction> directionListener = direction -> { };
        private Runnable backListener = () -> { };
        private Runnable newGameListener = () -> { };
        private Runnable undoListener = () -> { };
        private Consumer<String> saveListener = name -> { };

        @Override
        public void show() {
            this.shown++;
        }

        @Override
        public void onSaveSelected(final Consumer<String> listener) {
            this.saveListener = listener;
        }

        @Override
        public void onNewGameSelected(final Runnable listener) {
            this.newGameListener = listener;
        }

        @Override
        public void onUndoSelected(final Runnable listener) {
            this.undoListener = listener;
        }

        @Override
        public void onDirectionSelected(final Consumer<Direction> listener) {
            this.directionListener = listener;
        }

        @Override
        public void onBackSelected(final Runnable listener) {
            this.backListener = listener;
        }

        @Override
        public void showGame(final int[][] newGrid, final int newScore, final int newMoves) {
            this.grid = newGrid.clone();
            this.score = newScore;
            this.moves = newMoves;
            this.redraws++;
        }

        @Override
        public void showTime(final Duration elapsed) {
            this.time = Optional.of(elapsed);
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
     * Action that remembers whether the controller asked to go back to the menu.
     */
    private static final class BackToMenu implements Runnable {

        private boolean called;

        @Override
        public void run() {
            this.called = true;
        }
    }

    /**
     * Executor that keeps the tasks until the test runs them, to simulate another thread.
     */
    private static final class QueuedExecutor implements Executor {

        private final Queue<Runnable> tasks = new ArrayDeque<>();

        @Override
        public void execute(final Runnable task) {
            this.tasks.add(task);
        }

        private void runAll() {
            while (!this.tasks.isEmpty()) {
                this.tasks.remove().run();
            }
        }
    }

    /**
     * Timer whose time moves and ticks only when the test asks it to.
     */
    private static final class FakeTimer implements GameTimer {

        private Duration elapsed = Duration.ZERO;
        private boolean running;
        private Consumer<Duration> tickListener = time -> { };

        @Override
        public void start() {
            if (!this.running) {
                this.elapsed = Duration.ZERO;
                this.running = true;
            }
        }

        @Override
        public void pause() {
            throw new UnsupportedOperationException("the controller never pauses the timer");
        }

        @Override
        public void resume() {
            throw new UnsupportedOperationException("the controller never pauses the timer");
        }

        @Override
        public void stop() {
            this.running = false;
        }

        @Override
        public void reset() {
            this.running = false;
            this.elapsed = Duration.ZERO;
        }

        @Override
        public Duration getElapsed() {
            return this.elapsed;
        }

        @Override
        public boolean isRunning() {
            return this.running;
        }

        @Override
        public void onTick(final Consumer<Duration> listener) {
            this.tickListener = listener;
        }

        private void advance(final long seconds) {
            if (this.running) {
                this.elapsed = this.elapsed.plusSeconds(seconds);
            }
        }

        // come il thread del timer: avvisa con il tempo misurato finora
        private void tick() {
            this.tickListener.accept(this.elapsed);
        }
    }
}
