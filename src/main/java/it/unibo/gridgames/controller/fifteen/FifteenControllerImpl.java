package it.unibo.gridgames.controller.fifteen;

import it.unibo.gridgames.model.fifteen.Direction;
import it.unibo.gridgames.model.fifteen.FifteenGame;
import it.unibo.gridgames.model.fifteen.FifteenGameState;
import it.unibo.gridgames.model.timer.GameTimer;
import it.unibo.gridgames.view.fifteen.FifteenView;
import java.util.concurrent.Executor;

/**
 * Default implementation of {@link FifteenController}.
 */
public final class FifteenControllerImpl implements FifteenController {

    private final FifteenView view;
    private final FifteenGame game;
    private final GameTimer timer;
    private final Executor uiExecutor;

    /**
     * Creates a controller and connects it to the view and to the timer.
     *
     * @param view the screen of the game
     * @param game the game to play
     * @param timer the timer that measures each game
     * @param uiExecutor runs an action on the thread of the graphical toolkit
     * @param onBack the action that leaves the game screen
     * @throws IllegalArgumentException if an argument is null
     */
    public FifteenControllerImpl(
        final FifteenView view,
        final FifteenGame game,
        final GameTimer timer,
        final Executor uiExecutor,
        final Runnable onBack
    ) {
        if (view == null || game == null || timer == null || uiExecutor == null || onBack == null) {
            throw new IllegalArgumentException("The arguments must not be null");
        }
        this.view = view;
        this.game = game;
        this.timer = timer;
        this.uiExecutor = uiExecutor;
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
        }
    }

    // A tick can be handled after the end of its game, when the timer no longer runs.
    private void showRunningTime() {
        if (this.timer.isRunning()) {
            this.view.showTime(this.timer.getElapsed());
        }
    }
}
