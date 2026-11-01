package it.unibo.gridgames.controller.game2048;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.time.InstantSource;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;

import it.unibo.gridgames.model.game2048.Direction;
import it.unibo.gridgames.model.game2048.Game2048;
import it.unibo.gridgames.model.game2048.Game2048Impl;
import it.unibo.gridgames.model.game2048.GameState2048;
import it.unibo.gridgames.model.score.GameRecordImpl;
import it.unibo.gridgames.model.score.GameType;
import it.unibo.gridgames.persistence.ScoreManager;
import it.unibo.gridgames.view.game2048.Game2048View;

/**
 * Implementation of {@link Game2048Controller}. The first match starts and is
 * shown on creation. Its duration goes from the first valid move to the end of
 * the match.
 */
public final class Game2048ControllerImpl implements Game2048Controller {

    private final Game2048View view;
    private final ScoreManager scoreManager;
    private final Supplier<Game2048> gameFactory;
    private final InstantSource clock;
    private Game2048 game;
    private Optional<Instant> startTime = Optional.empty();
    private long durationSeconds;
    private boolean scoreSaved;

    /**
     * Creates a controller that plays standard matches.
     *
     * @param view the view to update
     * @param scoreManager where the final scores are saved
     */
    public Game2048ControllerImpl(final Game2048View view, final ScoreManager scoreManager) {
        this(view, scoreManager, Game2048Impl::new, InstantSource.system());
    }

    /**
     * Creates a controller with custom matches and clock, for example in tests.
     *
     * @param view the view to update
     * @param scoreManager where the final scores are saved
     * @param gameFactory creates each new match
     * @param clock the source of the current time
     */
    public Game2048ControllerImpl(final Game2048View view, final ScoreManager scoreManager,
            final Supplier<Game2048> gameFactory, final InstantSource clock) {
        this.view = Objects.requireNonNull(view);
        this.scoreManager = Objects.requireNonNull(scoreManager);
        this.gameFactory = Objects.requireNonNull(gameFactory);
        this.clock = Objects.requireNonNull(clock);
        this.game = gameFactory.get();
        showGame();
    }

    @Override
    public void move(final Direction direction) {
        if (this.game.move(direction)) {
            if (this.startTime.isEmpty()) {
                this.startTime = Optional.of(this.clock.instant());
            }
            showGame();
            final GameState2048 state = this.game.getState();
            if (state != GameState2048.PLAYING) {
                this.durationSeconds = Duration.between(this.startTime.get(), this.clock.instant()).toSeconds();
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
        this.startTime = Optional.empty();
        this.durationSeconds = 0;
        this.scoreSaved = false;
        showGame();
    }

    @Override
    public void saveScore(final String playerName) {
        if (this.scoreSaved || this.game.getState() == GameState2048.PLAYING) {
            return;
        }
        try {
            this.scoreManager.saveScore(new GameRecordImpl(playerName, this.game.getScore(), GameType.GAME_2048,
                this.game.getMoves(), this.durationSeconds, LocalDateTime.now()));
            this.scoreSaved = true;
            this.view.showScoreSaved();
        } catch (final IOException | IllegalArgumentException e) {
            this.view.showSaveError(e.getMessage());
        }
    }

    private void showGame() {
        this.view.showGame(this.game.getGrid(), this.game.getScore(), this.game.getMoves());
    }
}
