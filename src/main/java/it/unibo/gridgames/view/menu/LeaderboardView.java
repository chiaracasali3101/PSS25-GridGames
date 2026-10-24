package it.unibo.gridgames.view.menu;

import javafx.scene.layout.BorderPane;

import java.util.List;

import it.unibo.gridgames.model.score.GameRecordImpl;
import it.unibo.gridgames.persistence.ScoreManager;
import it.unibo.gridgames.view.UIConstants;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;

/**
 * LeaderboardView represents the user interface for displaying the leaderboard of game scores.
 */

public class LeaderboardView extends BorderPane {
    private final Button btnBack = new Button("Indietro");
    private final ListView<String> scoreListView = new ListView<>(); //campo lista grafica 

    /**
     * Constructs a new LeaderboardView instance and initializes the UI components.
     */
    public LeaderboardView() {
        this.setStyle("-fx-background-color: #f7f9fa;");

        this.setTop(this.btnBack);
        BorderPane.setMargin(this.btnBack, new Insets(UIConstants.BUTTON_BACK.getValue()));

        btnBack.setStyle("-fx-background-color: darkorange; -fx-text-fill: white; -fx-font-weight: bold;");

        BorderPane.setMargin(this.scoreListView, new Insets(10, 20, 20, 20)); //visualizzazione punteggi
        this.setCenter(this.scoreListView);
    }

    /**
     * Sets the action handler for the back button.
     * 
     * @param handler the Runnable to execute when the back button is clicked
     */
    public void setOnBackSelected(final Runnable handler) {
        this.btnBack.setOnAction(e -> handler.run());
    }

    //ricevere punteggi
    public void updateScores(final List<GameRecordImpl> scores) {
    this.scoreListView.getItems().clear();
    for (final GameRecordImpl record : scores) {
        this.scoreListView.getItems().add(
            record.getPlayerName() + " - " + record.getScore() + " pt"
        );
    }
}
}
