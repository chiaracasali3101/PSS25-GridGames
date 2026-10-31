package it.unibo.gridgames.controller.leaderboard;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;

import it.unibo.gridgames.controller.ControllerConstants;
import it.unibo.gridgames.model.score.GameRecordImpl;
import it.unibo.gridgames.model.score.GameType;
import it.unibo.gridgames.persistence.ScoreManager;
import it.unibo.gridgames.view.menu.LeaderboardView;

/**
 * Controller responsible for managing and displaying the leaderboard.
 */
public final class LeaderBoardController {

    private final ScoreManager scoreManager;
    private final LeaderboardView view;

    /**
     * Constructs a new LeaderBoardController.
     *
     * @param scoreManager the score manager used to retrieve scores
     * @param view the leaderboard view to be updated
     */
    public LeaderBoardController(final ScoreManager scoreManager, final LeaderboardView view) {
        this.scoreManager = Objects.requireNonNull(scoreManager, "ScoreManager cannot be null");
        this.view = Objects.requireNonNull(view, "LeaderboardView cannot be null");
    }

    /**
     * Loads the scores from persistence, filters the top entries for Sudoku,
     * and updates the view with the results.
     */
    public void loadAndDisplayScores() {
        final List<GameRecordImpl> allScores = this.scoreManager.loadAllScores();
        final List<GameRecordImpl> top5Scores = allScores.stream()
            .filter(record -> record.getGameType() == GameType.SUDOKU)
            .sorted(Comparator.comparingInt(GameRecordImpl::getScore).reversed())
            .limit(ControllerConstants.MAX_SIZE.getValue())
            .toList();
            this.view.updateScores(top5Scores);
    }

    /**
     * Returns the view managed by this controller.
     *
     * @return the leaderboard view
     */
    public LeaderboardView getView() {
        return this.view;
    }
}
