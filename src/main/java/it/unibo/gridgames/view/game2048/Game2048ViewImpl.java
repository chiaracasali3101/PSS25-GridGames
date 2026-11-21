package it.unibo.gridgames.view.game2048;

import java.util.Objects;
import java.util.function.Consumer;

import it.unibo.gridgames.model.game2048.Direction;
import it.unibo.gridgames.model.game2048.GameState2048;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

/**
 * JavaFX screen of the 2048 game: the grid, the score, the moves and the button
 * to go back to the menu. It only shows what the controller asks and notifies
 * the player's choices, without deciding anything.
 */
public final class Game2048ViewImpl extends BorderPane implements Game2048View {

    private static final double MARGIN = 10;
    private static final String BUTTON_STYLE =
        "-fx-background-color: #e67e22; -fx-text-fill: white; -fx-background-radius: 8;";
    private static final String INFO_STYLE = "-fx-font-size: 16;";
    private static final String MESSAGE_STYLE = "-fx-font-size: 16; -fx-font-weight: bold;";

    private final Consumer<Parent> rootSetter;
    private final Game2048Panel panel = new Game2048Panel();
    private final Label info = new Label();
    private final Label message = new Label();
    private Consumer<Direction> directionListener = direction -> { };
    private final Button backButton = createButton("← Menu");
    private final Button newGameButton = createButton("Nuova partita");
    private final Button undoButton = createButton("Annulla");

    /**
     * Creates the screen, without showing it.
     *
     * @param scene the application scene, where the screen is shown
     * @throws NullPointerException if scene is null
     */
    public Game2048ViewImpl(final Scene scene) {
        super();
        this.rootSetter = Objects.requireNonNull(scene)::setRoot;
        this.info.setStyle(INFO_STYLE);
        this.message.setStyle(MESSAGE_STYLE);
        final VBox top = new VBox(MARGIN, this.info, this.message);
        top.setAlignment(Pos.CENTER);
        top.setPadding(new Insets(MARGIN));
        this.setTop(top);
        this.setCenter(this.panel);
        this.setBottom(createFooter());
        this.setFocusTraversable(true);
        this.addEventFilter(KeyEvent.KEY_PRESSED,
            new Game2048KeyHandler(direction -> this.directionListener.accept(direction)));
    }

    @Override
    public void show() {
        this.rootSetter.accept(this);
        this.requestFocus();
    }

    @Override
    public void onDirectionSelected(final Consumer<Direction> listener) {
        this.directionListener = Objects.requireNonNull(listener);
    }

    @Override
    public void onBackSelected(final Runnable listener) {
        setAction(this.backButton, listener);
    }

    @Override
    public void onNewGameSelected(final Runnable listener) {
        setAction(this.newGameButton, listener);
    }

    @Override
    public void onUndoSelected(final Runnable listener) {
        setAction(this.undoButton, listener);
    }

    @Override
    public void showGame(final int[][] grid, final int score, final int moves) {
        this.panel.showGrid(grid);
        this.info.setText("Punteggio: " + score + "    Mosse: " + moves);
        this.message.setText("");
    }

    @Override
    public void showGameEnded(final GameState2048 result) {
        this.message.setText(result == GameState2048.WON ? "Hai vinto!" : "Game over: nessuna mossa possibile");
    }

    @Override
    public void showScoreSaved() {
        this.message.setText("Punteggio salvato");
    }

    @Override
    public void showSaveError(final String reason) {
        this.message.setText("Salvataggio non riuscito: " + reason);
    }

    private HBox createFooter() {
        final HBox footer = new HBox(MARGIN, this.backButton, this.newGameButton, this.undoButton);
        footer.setAlignment(Pos.CENTER);
        footer.setPadding(new Insets(MARGIN));
        return footer;
    }

    // dopo il click il focus torna alla schermata, così le frecce restano al gioco
    private void setAction(final Button button, final Runnable listener) {
        Objects.requireNonNull(listener);
        button.setOnAction(e -> {
            listener.run();
            this.requestFocus();
        });
    }

    // i bottoni non prendono il focus con le frecce/tab
    private static Button createButton(final String text) {
        final Button button = new Button(text);
        button.setStyle(BUTTON_STYLE);
        button.setFocusTraversable(false);
        return button;
    }
}
