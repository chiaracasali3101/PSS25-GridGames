package it.unibo.gridgames.controller.leaderboard;

import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import it.unibo.gridgames.model.score.GameRecordImpl;
import it.unibo.gridgames.persistence.ScoreManager;
import it.unibo.gridgames.view.menu.LeaderboardView;
import it.unibo.gridgames.view.menu.MainAppView;

public class LeaderBoardController {
    
    private ScoreManager scoreManager;
    private LeaderboardView view;

    public LeaderBoardController(final ScoreManager scoreManager, final MainAppView view2, LeaderboardView view) {
        this.scoreManager = Objects.requireNonNull(scoreManager, "ScoreManager cannot be null");
        this.view = Objects.requireNonNull(view, "LeaderboardView cannot be null");

        this.scoreManager = scoreManager;
        this.view = view;
    }

    public void loadAndDisplayScores() {
        try {
            final List<GameRecordImpl> scores = this.scoreManager.loadAllScores(); //prende i punteggi 
            final List<GameRecordImpl> sortedScores = scores.stream() //ordinamento con stream di java
                .sorted(Comparator.comparingInt(GameRecordImpl::getScore).reversed())
                .collect(Collectors.toList());
        } catch(final IOException e){
        }
    }

    public LeaderboardView getView() {
        return this.view;
    }
}
