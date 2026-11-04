package it.unibo.gridgames.controller.fifteen;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import it.unibo.gridgames.model.fifteen.Direction;
import it.unibo.gridgames.model.fifteen.FifteenBoardImpl;
import it.unibo.gridgames.model.fifteen.FifteenGame;
import it.unibo.gridgames.model.fifteen.FifteenGameImpl;
import it.unibo.gridgames.model.timer.GameTimer;
import it.unibo.gridgames.view.fifteen.FifteenView;
import java.time.Duration;
import java.util.ArrayDeque;
import java.util.Queue;
import java.util.concurrent.Executor;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class FifteenControllerTest {

    private static final int[][] ONE_MOVE_LEFT = {
        {1, 2, 3, 4},
        {5, 6, 7, 8},
        {9, 10, 11, 12},
        {13, 14, 0, 15},
    };
    private static final int[][] AFTER_SLIDING_FOURTEEN = {
        {1, 2, 3, 4},
        {5, 6, 7, 8},
        {9, 10, 11, 12},
        {13, 0, 14, 15},
    };
    private static final int[][] SOLVED = {
        {1, 2, 3, 4},
        {5, 6, 7, 8},
        {9, 10, 11, 12},
        {13, 14, 15, 0},
    };
    private static final int LAST = 3;
    private static final int EMPTY_COLUMN = 2;
    private static final int FOURTEEN_COLUMN = 1;
    private static final Duration PLAYED = Duration.ofSeconds(7);
    private static final Duration PLAYED_LATER = Duration.ofSeconds(9);

    private FakeView view;
    private FakeTimer timer;
    private QueuedExecutor uiExecutor;
    private int backCalls;
    private FifteenController controller;

    @BeforeEach
    void createController() {
        this.view = new FakeView();
        this.timer = new FakeTimer();
        this.uiExecutor = new QueuedExecutor();
        this.controller = new FifteenControllerImpl(
            this.view,
            newGame(),
            this.timer,
            this.uiExecutor,
            () -> this.backCalls++
        );
    }

    @Test
    void nullArgumentsAreRejected() {
        final FifteenGame game = newGame();
        final Runnable onBack = () -> { };
        assertThrows(IllegalArgumentException.class,
            () -> new FifteenControllerImpl(null, game, this.timer, this.uiExecutor, onBack));
        assertThrows(IllegalArgumentException.class,
            () -> new FifteenControllerImpl(this.view, null, this.timer, this.uiExecutor, onBack));
        assertThrows(IllegalArgumentException.class,
            () -> new FifteenControllerImpl(this.view, game, null, this.uiExecutor, onBack));
        assertThrows(IllegalArgumentException.class,
            () -> new FifteenControllerImpl(this.view, game, this.timer, null, onBack));
        assertThrows(IllegalArgumentException.class,
            () -> new FifteenControllerImpl(this.view, game, this.timer, this.uiExecutor, null));
    }

    @Test
    void creatingTheControllerDoesNotStartAnything() {
        assertEquals(0, this.view.showCalls);
        assertFalse(this.timer.isRunning());
    }

    @Test
    void startShowsTheScreenWithANewGame() {
        this.controller.start();
        assertEquals(1, this.view.showCalls);
        assertArrayEquals(ONE_MOVE_LEFT, this.view.board);
        assertEquals(0, this.view.moves);
        assertEquals(Duration.ZERO, this.view.time);
        assertTrue(this.timer.isRunning());
    }

    @Test
    void legalMoveOnATileUpdatesTheScreen() {
        this.controller.start();
        this.view.selectTile(LAST, FOURTEEN_COLUMN);
        assertArrayEquals(AFTER_SLIDING_FOURTEEN, this.view.board);
        assertEquals(1, this.view.moves);
        assertTrue(this.timer.isRunning());
        assertEquals(0, this.view.victories);
    }

    @Test
    void legalMoveByDirectionUpdatesTheScreen() {
        this.controller.start();
        this.view.selectDirection(Direction.RIGHT);
        assertArrayEquals(AFTER_SLIDING_FOURTEEN, this.view.board);
        assertEquals(1, this.view.moves);
    }

    @Test
    void illegalMoveOnATileChangesNothing() {
        this.controller.start();
        final int drawsBefore = this.view.boardDraws;
        this.view.selectTile(0, 0);
        assertEquals(drawsBefore, this.view.boardDraws);
        assertArrayEquals(ONE_MOVE_LEFT, this.view.board);
        assertEquals(0, this.view.moves);
    }

    @Test
    void illegalMoveByDirectionChangesNothing() {
        this.controller.start();
        final int drawsBefore = this.view.boardDraws;
        this.view.selectDirection(Direction.UP);
        assertEquals(drawsBefore, this.view.boardDraws);
        assertEquals(0, this.view.moves);
    }

    @Test
    void winningOnATileEndsTheGame() {
        this.controller.start();
        this.timer.elapsed = PLAYED;
        this.view.selectTile(LAST, LAST);
        assertArrayEquals(SOLVED, this.view.board);
        assertEquals(1, this.view.moves);
        assertEquals(1, this.view.victories);
        assertEquals(PLAYED, this.view.time);
        assertFalse(this.timer.isRunning());
    }

    @Test
    void winningByDirectionEndsTheGame() {
        this.controller.start();
        this.view.selectDirection(Direction.LEFT);
        assertEquals(1, this.view.victories);
        assertFalse(this.timer.isRunning());
    }

    @Test
    void movesAfterTheVictoryAreIgnored() {
        this.controller.start();
        this.view.selectTile(LAST, LAST);
        final int drawsBefore = this.view.boardDraws;
        this.view.selectTile(LAST, EMPTY_COLUMN);
        this.view.selectDirection(Direction.RIGHT);
        assertEquals(drawsBefore, this.view.boardDraws);
        assertEquals(1, this.view.moves);
        assertEquals(1, this.view.victories);
    }

    @Test
    void newGameRestartsBoardCounterAndTime() {
        this.controller.start();
        this.view.selectTile(LAST, FOURTEEN_COLUMN);
        this.timer.elapsed = PLAYED;
        this.view.selectNewGame();
        assertArrayEquals(ONE_MOVE_LEFT, this.view.board);
        assertEquals(0, this.view.moves);
        assertEquals(Duration.ZERO, this.view.time);
        assertEquals(Duration.ZERO, this.timer.getElapsed());
        assertTrue(this.timer.isRunning());
    }

    @Test
    void newGameAfterTheVictoryCanBePlayed() {
        this.controller.start();
        this.view.selectTile(LAST, LAST);
        this.view.selectNewGame();
        assertTrue(this.timer.isRunning());
        this.view.selectTile(LAST, LAST);
        assertEquals(1, this.view.moves);
        assertEquals(2, this.view.victories);
    }

    @Test
    void backStopsTheTimerAndLeaves() {
        this.controller.start();
        this.timer.elapsed = PLAYED;
        this.view.selectBack();
        assertEquals(1, this.backCalls);
        assertFalse(this.timer.isRunning());
        assertEquals(Duration.ZERO, this.timer.getElapsed());
    }

    @Test
    void startAfterBackBeginsAFreshGame() {
        this.controller.start();
        this.view.selectTile(LAST, FOURTEEN_COLUMN);
        this.view.selectBack();
        this.controller.start();
        assertArrayEquals(ONE_MOVE_LEFT, this.view.board);
        assertEquals(0, this.view.moves);
        assertEquals(Duration.ZERO, this.view.time);
        assertTrue(this.timer.isRunning());
    }

    @Test
    void ticksReachTheViewOnlyThroughTheUiExecutor() {
        this.controller.start();
        this.timer.elapsed = PLAYED;
        this.timer.tick();
        assertEquals(Duration.ZERO, this.view.time);
        this.uiExecutor.runAll();
        assertEquals(PLAYED, this.view.time);
    }

    @Test
    void tickShowsTheTimeReachedWhenItIsHandled() {
        this.controller.start();
        this.timer.elapsed = PLAYED;
        this.timer.tick();
        this.timer.elapsed = PLAYED_LATER;
        this.uiExecutor.runAll();
        assertEquals(PLAYED_LATER, this.view.time);
    }

    @Test
    void lateTickDoesNotOverwriteTheFinalTime() {
        this.controller.start();
        this.timer.elapsed = PLAYED;
        this.timer.tick();
        this.timer.elapsed = PLAYED_LATER;
        this.view.selectTile(LAST, LAST);
        final int updatesBefore = this.view.timeUpdates;
        this.uiExecutor.runAll();
        assertEquals(updatesBefore, this.view.timeUpdates);
        assertEquals(PLAYED_LATER, this.view.time);
    }

    @Test
    void lateTickDoesNotOverwriteTheTimeOfANewGame() {
        this.controller.start();
        this.timer.elapsed = PLAYED;
        this.timer.tick();
        this.view.selectNewGame();
        this.uiExecutor.runAll();
        assertEquals(Duration.ZERO, this.view.time);
    }

    @Test
    void lateTickAfterBackDoesNotTouchTheView() {
        this.controller.start();
        this.timer.tick();
        this.view.selectBack();
        final int updatesBefore = this.view.timeUpdates;
        this.uiExecutor.runAll();
        assertEquals(updatesBefore, this.view.timeUpdates);
    }

    private static FifteenGame newGame() {
        return new FifteenGameImpl(() -> new FifteenBoardImpl(ONE_MOVE_LEFT));
    }

    private static final class QueuedExecutor implements Executor {

        private final Queue<Runnable> tasks = new ArrayDeque<>();

        @Override
        public void execute(final Runnable task) {
            this.tasks.add(task);
        }

        void runAll() {
            while (!this.tasks.isEmpty()) {
                this.tasks.remove().run();
            }
        }
    }

    private static final class FakeTimer implements GameTimer {

        private Duration elapsed = Duration.ZERO;
        private boolean running;
        private Consumer<Duration> listener = time -> { };

        void tick() {
            this.listener.accept(this.elapsed);
        }

        @Override
        public void start() {
            this.running = true;
        }

        @Override
        public void pause() {
            this.running = false;
        }

        @Override
        public void resume() {
            this.running = true;
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
        public void onTick(final Consumer<Duration> tickListener) {
            this.listener = tickListener;
        }
    }

    private static final class FakeView implements FifteenView {

        private int showCalls;
        private int[][] board;
        private int boardDraws;
        private int moves = -1;
        private Duration time;
        private int timeUpdates;
        private int victories;
        private BiConsumer<Integer, Integer> onTile = (row, column) -> { };
        private Consumer<Direction> onDirection = direction -> { };
        private Runnable onNewGame = () -> { };
        private Runnable onBack = () -> { };

        void selectTile(final int row, final int column) {
            this.onTile.accept(row, column);
        }

        void selectDirection(final Direction direction) {
            this.onDirection.accept(direction);
        }

        void selectNewGame() {
            this.onNewGame.run();
        }

        void selectBack() {
            this.onBack.run();
        }

        @Override
        public void show() {
            this.showCalls++;
        }

        @Override
        public void showBoard(final int[][] tiles) {
            this.board = tiles.clone();
            this.boardDraws++;
        }

        @Override
        public void showMoves(final int count) {
            this.moves = count;
        }

        @Override
        public void showTime(final Duration elapsed) {
            this.time = elapsed;
            this.timeUpdates++;
        }

        @Override
        public void showVictory() {
            this.victories++;
        }

        @Override
        public void onTileSelected(final BiConsumer<Integer, Integer> listener) {
            this.onTile = listener;
        }

        @Override
        public void onDirectionSelected(final Consumer<Direction> listener) {
            this.onDirection = listener;
        }

        @Override
        public void onNewGameSelected(final Runnable listener) {
            this.onNewGame = listener;
        }

        @Override
        public void onBackSelected(final Runnable listener) {
            this.onBack = listener;
        }
    }
}
