package it.unibo.gridgames.controller.leaderboard;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;

import it.unibo.gridgames.controller.ControllerConstants;
import it.unibo.gridgames.model.score.GameRecordImpl;
import it.unibo.gridgames.model.score.GameType;
import it.unibo.gridgames.persistence.ScoreManager;
import it.unibo.gridgames.view.menu.LeaderboardView;
import java.util.ArrayList;
import it.unibo.gridgames.model.score.GameType;

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
        this.view.setTitle("Classifica Generale");

        final List<GameRecordImpl> allScores = this.scoreManager.loadAllScores();
        final List<GameRecordImpl> topScoresAllGames = new ArrayList<>();

        for (final GameType type : GameType.values()) {
            final List<GameRecordImpl> topPerGame = allScores.stream()
                .filter(record -> record.getGameType() == type)
                .sorted(Comparator.comparingInt(GameRecordImpl::getScore).reversed())
                .limit(5)
                .toList();

            topScoresAllGames.addAll(topPerGame);
        }

        this.view.updateScores(topScoresAllGames);
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
