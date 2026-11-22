package it.unibo.gridgames.controller.game2048;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.Executor;
import java.util.function.Supplier;

import it.unibo.gridgames.model.game2048.Direction;
import it.unibo.gridgames.model.game2048.Game2048;
import it.unibo.gridgames.model.game2048.Game2048Impl;
import it.unibo.gridgames.model.game2048.GameState2048;
import it.unibo.gridgames.model.score.GameRecordImpl;
import it.unibo.gridgames.model.score.GameType;
import it.unibo.gridgames.model.timer.GameTimer;
import it.unibo.gridgames.model.timer.GameTimerImpl;
import it.unibo.gridgames.persistence.ScoreManager;
import it.unibo.gridgames.view.game2048.Game2048View;
import it.unibo.gridgames.view.game2048.Game2048ViewImpl;
import javafx.application.Platform;
import javafx.scene.Scene;

/**
 * Implementation of {@link Game2048Controller}. The first match is created and
 * drawn on creation, but the screen appears only with {@link #start()}. The
 * timer of a match runs from the first valid move to the end of the match, and
 * its time is shown on the thread of the graphical toolkit at each tick.
 * The score is written on a separate thread, so the window is never blocked,
 * and the result is shown back on the thread of the graphical toolkit.
 */
public final class Game2048ControllerImpl implements Game2048Controller {

    private static final String SAVE_THREAD_NAME = "game2048-save";

    private final Game2048View view;
    private final ScoreManager scoreManager;
    private final Supplier<Game2048> gameFactory;
    private final GameTimer timer;
    private final Executor saveExecutor;
    private final Executor uiExecutor;
    private Game2048 game;
    private int matchNumber;
    private boolean scoreSaved;
    private boolean saving;

    /**
     * Creates a controller that plays standard matches in the JavaFX window.
     *
     * @param scene the application scene, where the game screen is shown
     * @param scoreManager where the final scores are saved
     * @param onBackToMenu shows the main menu again
     * @throws NullPointerException if any argument is null
     */
    public Game2048ControllerImpl(final Scene scene, final ScoreManager scoreManager, final Runnable onBackToMenu) {
        this(new Game2048ViewImpl(scene), scoreManager, onBackToMenu, Game2048Impl::new, new GameTimerImpl(),
            Game2048ControllerImpl::runOnNewThread, Platform::runLater);
    }

    /**
     * Creates a controller with custom view, matches, timer and threads, for example in tests.
     *
     * @param view the view to update
     * @param scoreManager where the final scores are saved
     * @param onBackToMenu shows the main menu again
     * @param gameFactory creates each new match
     * @param timer measures how long each match lasts
     * @param saveExecutor runs the writing of a score, away from the graphical toolkit
     * @param uiExecutor runs an action on the thread of the graphical toolkit
     * @throws NullPointerException if any argument is null
     */
    public Game2048ControllerImpl(final Game2048View view, final ScoreManager scoreManager,
            final Runnable onBackToMenu, final Supplier<Game2048> gameFactory, final GameTimer timer,
            final Executor saveExecutor, final Executor uiExecutor) {
        this.view = Objects.requireNonNull(view, "view cannot be null");
        this.scoreManager = Objects.requireNonNull(scoreManager, "scoreManager cannot be null");
        this.gameFactory = Objects.requireNonNull(gameFactory, "gameFactory cannot be null");
        this.timer = Objects.requireNonNull(timer, "timer cannot be null");
        this.saveExecutor = Objects.requireNonNull(saveExecutor, "saveExecutor cannot be null");
        this.uiExecutor = Objects.requireNonNull(uiExecutor, "uiExecutor cannot be null");
        this.view.onDirectionSelected(this::move);
        Objects.requireNonNull(onBackToMenu, "onBackToMenu cannot be null");
        this.view.onBackSelected(() -> {
            this.timer.reset();
            onBackToMenu.run();
        });
        this.view.onNewGameSelected(this::newGame);
        this.view.onUndoSelected(this::undo);
        this.view.onSaveSelected(this::saveScore);
        // il tick arriva dal thread del timer: la view si aggiorna solo sul thread grafico
        this.timer.onTick(elapsed -> this.uiExecutor.execute(this::showRunningTime));
        newGame();
    }

    @Override
    public void start() {
        newGame();
        this.view.show();
    }

    @Override
    public void move(final Direction direction) {
        if (this.game.move(direction)) {
            // il tempo parte dalla prima mossa valida; se il timer sta già andando start() non fa nulla
            this.timer.start();
            showGame();
            final GameState2048 state = this.game.getState();
            if (state != GameState2048.PLAYING) {
                this.timer.stop();
                this.view.showTime(this.timer.getElapsed());
                this.view.showGameEnded(state);
            }
        }
    }

    @Override
    public void undo() {
        if (this.game.undo()) {
            showGame();
        }
    }

    @Override
    public void newGame() {
        this.game = this.gameFactory.get();
        this.matchNumber++;
        this.timer.reset();
        this.view.showTime(this.timer.getElapsed());
        this.scoreSaved = false;
        this.saving = false;
        showGame();
    }

    @Override
    public void saveScore(final String playerName) {
        if (this.scoreSaved || this.saving || this.game.getState() == GameState2048.PLAYING) {
            return;
        }
        final GameRecordImpl record;
        try {
            record = new GameRecordImpl(playerName, this.game.getScore(), GameType.GAME_2048,
                this.game.getMoves(), this.timer.getElapsed().toSeconds(), LocalDateTime.now());
        } catch (final IllegalArgumentException e) {
            this.view.showSaveError(e.getMessage());
            return;
        }
        this.saving = true;
        final int savedMatch = this.matchNumber;
        // scrittura su file fuori dal thread grafico, esito mostrato di nuovo sul thread grafico
        this.saveExecutor.execute(() -> {
            final Optional<String> error = write(record);
            this.uiExecutor.execute(() -> saveEnded(savedMatch, error));
        });
    }

    private Optional<String> write(final GameRecordImpl record) {
        try {
            this.scoreManager.saveScore(record);
            return Optional.empty();
        } catch (final IOException e) {
            return Optional.of(String.valueOf(e.getMessage()));
        }
    }

    private void saveEnded(final int savedMatch, final Optional<String> error) {
        // nel frattempo è iniziata un'altra partita: l'esito non la riguarda
        if (savedMatch != this.matchNumber) {
            return;
        }
        this.saving = false;
        if (error.isPresent()) {
            this.view.showSaveError(error.get());
        } else {
            this.scoreSaved = true;
            this.view.showScoreSaved();
        }
    }

    private void showGame() {
        this.view.showGame(this.game.getGrid(), this.game.getScore(), this.game.getMoves());
    }

    // un tick partito prima di uno stop o di una nuova partita non deve mostrare un tempo vecchio
    private void showRunningTime() {
        if (this.timer.isRunning()) {
            this.view.showTime(this.timer.getElapsed());
        }
    }

    private static void runOnNewThread(final Runnable task) {
        new Thread(task, SAVE_THREAD_NAME).start();
    }
}
