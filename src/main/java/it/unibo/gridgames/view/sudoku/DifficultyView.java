package it.unibo.gridgames.view.sudoku;

import java.util.function.Consumer;

import it.unibo.gridgames.model.sudoku.Difficulty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

/**
 * Screen where the player chooses the difficulty of a new Sudoku game.
 */
public final class DifficultyView extends BorderPane {

    private static final double TITLE_SIZE = 32;
    private static final double BUTTON_WIDTH = 160;
    private static final double SPACING = 15;
    private static final double MARGIN = 20;

    private Consumer<Difficulty> onDifficultySelected = difficulty -> { };
    private Runnable onBackSelected = () -> { };

    /**
     * Creates the screen with one button for each difficulty.
     */
    public DifficultyView() {
        final Label title = new Label("Sudoku");
        title.setFont(Font.font("Arial", FontWeight.BOLD, TITLE_SIZE));
        BorderPane.setAlignment(title, Pos.CENTER);
        BorderPane.setMargin(title, new Insets(MARGIN));
        this.setTop(title);

        final VBox buttons = new VBox(SPACING);
        buttons.setAlignment(Pos.CENTER);
        for (final Difficulty difficulty : Difficulty.values()) {
            final Button button = new Button(labelOf(difficulty));
            button.setPrefWidth(BUTTON_WIDTH);
            button.setOnAction(e -> onDifficultySelected.accept(difficulty));
            buttons.getChildren().add(button);
        }
        this.setCenter(buttons);

        final Button back = new Button("← Menu");
        back.setOnAction(e -> onBackSelected.run());
        BorderPane.setMargin(back, new Insets(MARGIN));
        this.setBottom(back);
    }

    /**
     * Sets what happens when the player chooses a difficulty.
     *
     * @param handler receives the chosen difficulty
     */
    public void setOnDifficultySelected(final Consumer<Difficulty> handler) {
        this.onDifficultySelected = handler;
    }

    /**
     * Sets what happens when the player wants to go back to the menu.
     *
     * @param handler the action to run
     */
    public void setOnBackSelected(final Runnable handler) {
        this.onBackSelected = handler;
    }

    private static String labelOf(final Difficulty difficulty) {
        return switch (difficulty) {
            case EASY -> "Facile";
            case MEDIUM -> "Medio";
            case HARD -> "Difficile";
        };
    }
}
