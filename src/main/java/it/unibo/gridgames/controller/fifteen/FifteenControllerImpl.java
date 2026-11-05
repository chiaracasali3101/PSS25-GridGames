package it.unibo.gridgames.controller.fifteen;

import it.unibo.gridgames.model.fifteen.Direction;
import it.unibo.gridgames.model.fifteen.FifteenGame;
import it.unibo.gridgames.model.fifteen.FifteenGameImpl;
import it.unibo.gridgames.model.fifteen.FifteenGameState;
import it.unibo.gridgames.model.fifteen.FifteenScore;
import it.unibo.gridgames.model.fifteen.FifteenShufflerImpl;
import it.unibo.gridgames.model.score.GameRecordImpl;
import it.unibo.gridgames.model.score.GameType;
import it.unibo.gridgames.model.timer.GameTimer;
import it.unibo.gridgames.model.timer.GameTimerImpl;
import it.unibo.gridgames.persistence.ScoreManager;
import it.unibo.gridgames.view.fifteen.FifteenView;
import it.unibo.gridgames.view.fifteen.FifteenViewImpl;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.concurrent.Executor;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.random.RandomGenerator;
import javafx.application.Platform;
import javafx.scene.Scene;

/**
 * Default implementation of {@link FifteenController}.
 */
public final class FifteenControllerImpl implements FifteenController {

    private static final Logger LOGGER = Logger.getLogger(FifteenControllerImpl.class.getName());

    private final FifteenView view;
    private final FifteenGame game;
    private final GameTimer timer;
    private final Executor uiExecutor;
    private final ScoreManager scoreManager;

    /**
     * Creates a controller with the real screen, game and timer, ready to be used by the menu.
     *
     * @param scene the scene of the application, where the game is shown
     * @param scoreManager saves the results of the won games
     * @param onBack the action that leaves the game screen
     * @throws IllegalArgumentException if an argument is null
     */
    public FifteenControllerImpl(final Scene scene, final ScoreManager scoreManager, final Runnable onBack) {
        this(
            new FifteenViewImpl(scene),
            new FifteenGameImpl(new FifteenShufflerImpl(RandomGenerator.getDefault())),
            new GameTimerImpl(),
            Platform::runLater,
            scoreManager,
            onBack
        );
    }

    /**
     * Creates a controller and connects it to the view and to the timer.
     *
     * @param view the screen of the game
     * @param game the game to play
     * @param timer the timer that measures each game
     * @param uiExecutor runs an action on the thread of the graphical toolkit
     * @param scoreManager saves the results of the won games
     * @param onBack the action that leaves the game screen
     * @throws IllegalArgumentException if an argument is null
     */
    public FifteenControllerImpl(
        final FifteenView view,
        final FifteenGame game,
        final GameTimer timer,
        final Executor uiExecutor,
        final ScoreManager scoreManager,
        final Runnable onBack
    ) {
        if (view == null || game == null || timer == null || uiExecutor == null || scoreManager == null
            || onBack == null) {
            throw new IllegalArgumentException("The arguments must not be null");
        }
        this.view = view;
        this.game = game;
        this.timer = timer;
        this.uiExecutor = uiExecutor;
        this.scoreManager = scoreManager;
        this.view.onTileSelected(this::moveTile);
        this.view.onDirectionSelected(this::move);
        this.view.onNewGameSelected(this::startNewGame);
        this.view.onBackSelected(() -> {
            this.timer.reset();
            onBack.run();
        });
        this.timer.onTick(elapsed -> this.uiExecutor.execute(this::showRunningTime));
    }

    @Override
    public void start() {
        startNewGame();
        this.view.show();
    }

    private void startNewGame() {
        this.game.newGame();
        this.timer.reset();
        this.view.showBoard(this.game.getTiles());
        this.view.showMoves(this.game.getMoveCount());
        this.view.showTime(this.timer.getElapsed());
        this.timer.start();
    }

    private void moveTile(final int row, final int column) {
        if (this.game.moveTile(row, column)) {
            handleMove();
        }
    }

    private void move(final Direction direction) {
        if (this.game.move(direction)) {
            handleMove();
        }
    }

    private void handleMove() {
        this.view.showBoard(this.game.getTiles());
        this.view.showMoves(this.game.getMoveCount());
        if (this.game.getState() == FifteenGameState.WON) {
            this.timer.stop();
            this.view.showTime(this.timer.getElapsed());
            this.view.showVictory();
            askPlayerName(this.game.getMoveCount(), this.timer.getElapsed().toSeconds());
        }
    }

    // The result is fixed now, because the name can arrive when another game has already started.
    private void askPlayerName(final int moves, final long seconds) {
        this.view.askPlayerName(name -> saveResult(name, moves, seconds));
    }

    private void saveResult(final String name, final int moves, final long seconds) {
        final GameRecordImpl result;
        try {
            result = new GameRecordImpl(
                name,
                FifteenScore.compute(moves),
                GameType.PUZZLE_15,
                moves,
                seconds,
                LocalDateTime.now()
            );
        } catch (final IllegalArgumentException e) {
            LOGGER.log(Level.FINE, "Player name refused", e);
            this.view.showInvalidPlayerName();
            askPlayerName(moves, seconds);
            return;
        }
        try {
            this.scoreManager.saveScore(result);
        } catch (final IOException e) {
            LOGGER.log(Level.SEVERE, "Cannot save the result of the game", e);
            this.view.showSaveError();
        }
    }

    // A tick can be handled after the end of its game, when the timer no longer runs.
    private void showRunningTime() {
        if (this.timer.isRunning()) {
            this.view.showTime(this.timer.getElapsed());
        }
    }
}
