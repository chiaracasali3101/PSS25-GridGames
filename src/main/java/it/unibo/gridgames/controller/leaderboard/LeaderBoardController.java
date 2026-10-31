package it.unibo.gridgames.controller.leaderboard;

import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import it.unibo.gridgames.model.score.GameRecordImpl;
import it.unibo.gridgames.model.score.GameType;
import it.unibo.gridgames.persistence.ScoreManager;
import it.unibo.gridgames.view.menu.LeaderboardView;
import it.unibo.gridgames.view.menu.MainAppView;

public class LeaderBoardController {
    
    private final ScoreManager scoreManager;
    private final LeaderboardView view;

    public LeaderBoardController(final ScoreManager scoreManager, final MainAppView view2, LeaderboardView view) {
        this.scoreManager = Objects.requireNonNull(scoreManager, "ScoreManager cannot be null");
        this.view = Objects.requireNonNull(view, "LeaderboardView cannot be null");
    }

    public void loadAndDisplayScores() {
        final List<GameRecordImpl> allScores = this.scoreManager.loadAllScores();
        final List<GameRecordImpl> top5Scores = allScores.stream()
            .filter(record -> record.getGameType() == GameType.SUDOKU)
            .sorted(Comparator.comparingInt(GameRecordImpl::getScore).reversed())
            .limit(5)
            .toList();
            this.view.updateScores(top5Scores);
    }   

    public LeaderboardView getView() {
        return this.view;
    }
}
