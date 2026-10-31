package it.unibo.gridgames.view.menu;

import java.util.List;

import it.unibo.gridgames.model.score.GameRecordImpl;
import it.unibo.gridgames.view.UIConstants;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import it.unibo.gridgames.model.score.GameType;

/**
 * LeaderboardView represents the user interface for displaying the leaderboard of game scores.
 */
public final class LeaderboardView extends BorderPane {
    private static final double LIST_VERTICAL_MARGIN = 10.0;
    private static final double LIST_HORIZONTAL_MARGIN = 20.0;
    private static final double TOP_BOX_SPACING = 10.0;

    private final Button btnBack = new Button("Indietro");
    private final Label titleLabel = new Label("Classifica");
    private final ListView<String> scoreListView = new ListView<>();

    /**
     * Constructs a new LeaderboardView instance and initializes the UI components.
     */
    public LeaderboardView() {
        this.setStyle("-fx-background-color: #f7f9fa;");

        this.btnBack.setStyle("-fx-background-color: darkorange; -fx-text-fill: white; -fx-font-weight: bold;");
        this.titleLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #333333;");

        final VBox topBox = new VBox(TOP_BOX_SPACING);
        topBox.setAlignment(Pos.CENTER);
        topBox.getChildren().add(this.btnBack);
        topBox.getChildren().add(this.titleLabel);
        VBox.setMargin(this.btnBack, new Insets(UIConstants.BUTTON_BACK.getValue()));

        this.setTop(topBox);

        BorderPane.setMargin(this.scoreListView, new Insets(
            LIST_VERTICAL_MARGIN, 
            LIST_HORIZONTAL_MARGIN, 
            LIST_HORIZONTAL_MARGIN, 
            LIST_HORIZONTAL_MARGIN
        ));
        this.setCenter(this.scoreListView);
    }

    /**
     * Sets the title displayed above the scores list.
     *
     * @param title the game title to display
     */
    public void setTitle(final String title) {
        this.titleLabel.setText(title);
    }

    /**
     * Sets the action handler for the back button.
     *
     * @param handler the Runnable to execute when the back button is clicked
     */
    public void setOnBackSelected(final Runnable handler) {
        this.btnBack.setOnAction(e -> handler.run());
    }

    /**
     * Updates the score list displayed in the view.
     *
     * @param scores the list of records to show
     */
    public void updateScores(final List<GameRecordImpl> scores) {
        this.scoreListView.getItems().clear();

        if (scores.isEmpty()) {
            this.scoreListView.getItems().add("Nessun punteggio registrato.");
            return;
        }

        int rank = 1;
        GameType lastGame = null;

        for (final GameRecordImpl record : scores) {
            if (lastGame != record.getGameType()) {
                if (lastGame != null) {
                    this.scoreListView.getItems().add(""); 
                }
                this.scoreListView.getItems().add("=== " + record.getGameType().name() + " ===");
                rank = 1;
                lastGame = record.getGameType();
            }

            final String formattedDate = record.getDateTime().toLocalDate().toString();
            final String labelText = String.format("  #%d - %d pt (%s, %d mosse)",
                rank,
                record.getScore(),
                formattedDate,
                record.getMoves()
            );

            this.scoreListView.getItems().add(labelText);
            rank++;
        }
    }
}