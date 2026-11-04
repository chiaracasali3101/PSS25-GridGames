package it.unibo.gridgames.view.sudoku;

import java.util.function.Consumer;

import it.unibo.gridgames.model.sudoku.Difficulty;
import it.unibo.gridgames.view.UIConstants;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

/**
 * Screen where the player chooses the difficulty of a new Sudoku game.
 */
public final class DifficultyView extends BorderPane {

    private static final double BUTTON_WIDTH = 160;

    // bottoni
    private final Button btnBack = new Button("Indietro");
    private final Button btnEasy = new Button("Facile");
    private final Button btnMedium = new Button("Medio");
    private final Button btnHard = new Button("Difficile");

    /**
     * Creates the screen with one button for each difficulty.
     */
    public DifficultyView() {
        this.setStyle("-fx-background-color: #f7f9fa;"); // sfondo

        // titolo
        final Label title = new Label("Sudoku");
        title.setFont(Font.font("Arial", FontWeight.BOLD, UIConstants.FONT_SIZE_TITLE.getValue()));
        title.setTextFill(Color.DARKORANGE);

        // bottone indietro
        btnBack.setStyle("-fx-background-color: darkorange; -fx-text-fill: white; -fx-font-weight: bold;");

        // bottone indietro nella sua scatola, a destra
        final HBox backBox = new HBox(btnBack);
        backBox.setAlignment(Pos.CENTER_LEFT);
        VBox.setMargin(backBox, new Insets(UIConstants.BUTTON_BACK.getValue()));

        // in alto: prima il bottone, poi il titolo al centro
        final VBox topBox = new VBox(UIConstants.GRID_SIZE.getValue());
        topBox.setAlignment(Pos.CENTER);
        topBox.getChildren().add(backBox);
        topBox.getChildren().add(title);
        this.setTop(topBox);

        // bottoni delle difficoltà
        btnEasy.setPrefWidth(BUTTON_WIDTH);
        btnEasy.setStyle("-fx-background-color: rgb(240, 158, 85); -fx-text-fill: white; -fx-background-radius: 8;");
        btnMedium.setPrefWidth(BUTTON_WIDTH);
        btnMedium.setStyle("-fx-background-color: #e67e22; -fx-text-fill: white; -fx-background-radius: 8;");
        btnHard.setPrefWidth(BUTTON_WIDTH);
        btnHard.setStyle("-fx-background-color: rgb(122, 60, 7); -fx-text-fill: white; -fx-background-radius: 8;");

        // bottoni al centro, uno sotto l'altro
        final VBox buttons = new VBox(UIConstants.GRID_SIZE.getValue());
        buttons.setAlignment(Pos.CENTER);
        buttons.getChildren().add(btnEasy);
        buttons.getChildren().add(btnMedium);
        buttons.getChildren().add(btnHard);
        this.setCenter(buttons);
    }

    /**
     * Sets what happens when the player chooses a difficulty.
     *
     * @param handler receives the chosen difficulty
     */
    public void setOnDifficultySelected(final Consumer<Difficulty> handler) {
        this.btnEasy.setOnAction(e -> handler.accept(Difficulty.EASY));
        this.btnMedium.setOnAction(e -> handler.accept(Difficulty.MEDIUM));
        this.btnHard.setOnAction(e -> handler.accept(Difficulty.HARD));
    }

    /**
     * Sets what happens when the player wants to go back to the menu.
     *
     * @param handler the action to run
     */
    public void setOnBackSelected(final Runnable handler) {
        this.btnBack.setOnAction(e -> handler.run());
    }
}
