package it.unibo.gridgames.view.sudoku;

import it.unibo.gridgames.model.sudoku.Grid;
import it.unibo.gridgames.view.UIConstants;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;

/**
 * Screen where the player plays a Sudoku game.
 */
public final class SudokuView extends BorderPane {

    // bottoni
    private final Button btnBack = new Button("Indietro");

    // scritte
    private final Label errorsLabel = new Label("Errori: 0/3");

    /**
     * Creates the screen with the 9x9 grid and the numbers from 1 to 9.
     */
    public SudokuView() {
        this.setStyle("-fx-background-color: #f7f9fa;"); // sfondo

        // in alto: bottone indietro e numero di errori
        btnBack.setStyle("-fx-background-color: darkorange; -fx-text-fill: white; -fx-font-weight: bold;");
        errorsLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #333333;");
        final HBox topBox = new HBox(UIConstants.GRID_SIZE.getValue());
        topBox.setAlignment(Pos.CENTER_LEFT);
        topBox.getChildren().add(btnBack);
        topBox.getChildren().add(errorsLabel);
        BorderPane.setMargin(topBox, new Insets(UIConstants.GRID_SIZE.getValue()));
        this.setTop(topBox);

        // al centro: la griglia 9x9
        final GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        for (int row = 0; row < Grid.SIZE; row++) {
            for (int col = 0; col < Grid.SIZE; col++) {
                final Button cell = new Button();
                cell.setPrefSize(UIConstants.CELL_SIZE.getValue(), UIConstants.CELL_SIZE.getValue());
                cell.setStyle("-fx-background-color: white; -fx-border-color: #cccccc; -fx-background-radius: 0;");
                grid.add(cell, col, row);
            }
        }
        this.setCenter(grid);

        // in basso: i numeri da 1 a 9
        final HBox numbersBox = new HBox(UIConstants.GRID_SIZE.getValue());
        numbersBox.setAlignment(Pos.CENTER);
        for (int i = 1; i <= Grid.SIZE; i++) {
            final Button number = new Button(String.valueOf(i));
            number.setPrefSize(UIConstants.CELL_SIZE.getValue(), UIConstants.CELL_SIZE.getValue());
            number.setStyle("-fx-background-color: #e67e22; -fx-text-fill: white; -fx-background-radius: 8;");
            numbersBox.getChildren().add(number);
        }
        BorderPane.setMargin(numbersBox, new Insets(UIConstants.GRID_SIZE.getValue()));
        this.setBottom(numbersBox);
    }

    /**
     * Sets what happens when the player wants to go back.
     *
     * @param handler the action to run
     */
    public void setOnBackSelected(final Runnable handler) {
        this.btnBack.setOnAction(e -> handler.run());
    }
}
