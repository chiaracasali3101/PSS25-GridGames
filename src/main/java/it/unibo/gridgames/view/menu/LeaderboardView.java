package it.unibo.gridgames.view.menu;

import javafx.scene.layout.BorderPane;
import it.unibo.gridgames.persistence.ScoreManager;
import it.unibo.gridgames.view.UIConstants;
import javafx.geometry.Insets;
import javafx.scene.control.Button;

/**
 * LeaderboardView represents the user interface for displaying the leaderboard of game scores.
 */

public class LeaderboardView extends BorderPane {
    private final Button btnBack = new Button("Indietro");

    /**
     * Constructs a new LeaderboardView instance and initializes the UI components.
     */
    public LeaderboardView() {
        this.setStyle("-fx-background-color: #f7f9fa;");

        this.setTop(this.btnBack);
        BorderPane.setMargin(this.btnBack, new Insets(UIConstants.BUTTON_BACK.getValue()));

        btnBack.setStyle("-fx-background-color: darkorange; -fx-text-fill: white; -fx-font-weight: bold;");
    }

    /**
     * Sets the action handler for the back button.
     * 
     * @param handler the Runnable to execute when the back button is clicked
     */
    public void setOnBackSelected(final Runnable handler) {
        this.btnBack.setOnAction(e -> handler.run());
    }
}
