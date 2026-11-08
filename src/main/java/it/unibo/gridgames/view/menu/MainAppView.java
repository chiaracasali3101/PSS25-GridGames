package it.unibo.gridgames.view.menu;

import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import it.unibo.gridgames.view.UIConstants;
import javafx.beans.binding.Bindings;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
//import it.unibo.gridgames.view.UIConstants;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;

/**
 * Main view of the application, displaying the title and buttons for different games and leaderboard.
 */
public class MainAppView extends BorderPane {

    private final String BUTTON_COLOR = "-fx-background-color: #e67e22; -fx-text-fill: white; -fx-background-radius: 8;";
    // bottoni
    private final Button btn2048 = new Button("2048");
    private final Button btnSudoku = new Button("Sudoku");
    private final Button btn15Game = new Button("Gioco del 15");
    private final Button btnLeaderboard = new Button("Classifica");

    /**
     * Constructs a new instance and initializes the UI components.
     */
    public MainAppView() {
        final Label titleLabel = new Label("GridGames");
        BorderPane.setAlignment(titleLabel, Pos.CENTER); // titolo in alto al centro
        BorderPane.setMargin(titleLabel, new Insets(
            UIConstants.MARGIN_TOP.getValue(), 
            0, 
            UIConstants.MARGIN_BOTTOM.getValue(), 
            0)); 
        this.setTop(titleLabel);
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, UIConstants.FONT_SIZE_TITLE.getValue())); // testo
        titleLabel.setTextFill(Color.DARKORANGE); // colore

        // griglia per i bottoni
        final GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(UIConstants.GRID_SIZE.getValue());
        grid.setVgap(UIConstants.GRID_SIZE.getValue());

        //colonne
        final ColumnConstraints col1 = new ColumnConstraints();
        col1.setPercentWidth(50);
        col1.setHalignment(HPos.CENTER);
        col1.setHgrow(Priority.ALWAYS);

        final ColumnConstraints col2 = new ColumnConstraints();
        col2.setPercentWidth(50);
        col2.setHalignment(HPos.CENTER);
        col2.setHgrow(Priority.ALWAYS);
        grid.getColumnConstraints().addAll(col1, col2);

        // rige
        final RowConstraints row1 = new RowConstraints();
        row1.setPercentHeight(50);
        row1.setValignment(VPos.CENTER);
        row1.setVgrow(Priority.ALWAYS);

        final RowConstraints row2 = new RowConstraints();
        row2.setPercentHeight(50);
        row2.setValignment(VPos.CENTER);
        row2.setVgrow(Priority.ALWAYS);
        grid.getRowConstraints().addAll(row1, row2);

  
        grid.maxWidthProperty().bind(this.widthProperty().multiply(0.35));
        grid.maxHeightProperty().bind(this.heightProperty().multiply(0.25));
        final Button[] buttons = { btn2048, btn15Game, btnSudoku, btnLeaderboard };

        for (final Button btn : buttons) {
            btn.setStyle(this.BUTTON_COLOR + " -fx-font-size: 15px; -fx-font-weight: bold;");
            btn.setMaxWidth(200);
            btn.setMaxHeight(60);
            btn.setMinWidth(130);   
            btn.setMinHeight(45);
        }


        grid.add(btn2048, 0, 0);
        //btn2048.setStyle(BUTTON_COLOR);
        grid.add(btn15Game, 1, 0);
        //btn15Game.setStyle(BUTTON_COLOR);
        grid.add(btnSudoku, 0, 1);
        //btnSudoku.setStyle(BUTTON_COLOR);
        grid.add(btnLeaderboard, 1, 1);
       // btnLeaderboard.setStyle(BUTTON_COLOR);

        this.setCenter(grid); // gliglia al centro
    }

    /**
     * Sets the action handler for the 2048 button.
     * 
     * @param handler sets the action handler for the 2048 button
     */
    public void setOn2048Selected(final Runnable handler) {
        this.btn2048.setOnAction(e -> handler.run());
    }

    /**
     * Sets the action handler for the 15Game button.
     * 
     * @param handler sets the action handler for the 15Game button
     */
    public void setOnGiocoDel15Selected(final Runnable handler) {
        this.btn15Game.setOnAction(e -> handler.run());
    }

    /**
     * Sets the action handler for the Sudoku button.
     * 
     * @param handler sets the action handler for the Sudoku button
     */
    public void setOnSudokuSelected(final Runnable handler) {
        this.btnSudoku.setOnAction(e -> handler.run());
    }

    /**
     * Sets the action handler for the Leader button.
     * 
     * @param handler sets the action handler for the Leader button
     */
    public void setOnLeaderboardSelected(final Runnable handler) {
        this.btnLeaderboard.setOnAction(e -> handler.run());
    }
}
