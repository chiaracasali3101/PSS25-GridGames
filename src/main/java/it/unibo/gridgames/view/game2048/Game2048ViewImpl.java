package it.unibo.gridgames.view.game2048;

import java.time.Duration;
import java.util.Locale;
import java.util.Objects;
import java.util.function.Consumer;

import it.unibo.gridgames.model.game2048.Direction;
import it.unibo.gridgames.model.game2048.GameState2048;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

/**
 * JavaFX screen of the 2048 game: the grid, the score, the moves, the time, the buttons
 * to play and, at the end of a match, the field to save the score. Messages are
 * shown inside the screen, so the window is never blocked. It only shows what
 * the controller asks and notifies the player's choices, without deciding anything.
 */
public final class Game2048ViewImpl extends BorderPane implements Game2048View {

    private static final double MARGIN = 10;
    private static final long SECONDS_PER_MINUTE = 60;
    private static final String BUTTON_STYLE =
        "-fx-background-color: #e67e22; -fx-text-fill: white; -fx-background-radius: 8;";
    private static final String INFO_STYLE = "-fx-font-size: 16;";
    private static final String MESSAGE_STYLE = "-fx-font-size: 16; -fx-font-weight: bold;";
    private static final String LISTENER_NO_NULL = "listener cannot be null";

    private final Consumer<Parent> rootSetter;
    private final Game2048Panel panel = new Game2048Panel();
    private final Label info = new Label();
    private final Label time = new Label();
    private final Label message = new Label();
    private final Button backButton = createButton("← Menu");
    private final Button newGameButton = createButton("Nuova partita");
    private final Button undoButton = createButton("Annulla");
    private final TextField nameField = new TextField();
    private final Button saveButton = createButton("Salva");
    private final HBox saveBox = new HBox(MARGIN, this.nameField, this.saveButton);
    private Consumer<Direction> directionListener = direction -> { };

    /**
     * Creates the screen, without showing it.
     *
     * @param scene the application scene, where the screen is shown
     * @throws NullPointerException if scene is null
     */
    public Game2048ViewImpl(final Scene scene) {
        super();
        this.rootSetter = Objects.requireNonNull(scene, "scene cannot be null")::setRoot;
        this.info.setStyle(INFO_STYLE);
        this.time.setStyle(INFO_STYLE);
        this.message.setStyle(MESSAGE_STYLE);
        this.nameField.setPromptText("Il tuo nome");
        this.saveBox.setAlignment(Pos.CENTER);
        setSaveBoxVisible(false);
        final HBox stats = new HBox(MARGIN, this.info, this.time);
        stats.setAlignment(Pos.CENTER);
        final VBox top = new VBox(MARGIN, stats, this.message, this.saveBox);
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
        this.directionListener = Objects.requireNonNull(listener, LISTENER_NO_NULL);
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
    public void onSaveSelected(final Consumer<String> listener) {
        Objects.requireNonNull(listener, LISTENER_NO_NULL);
        // si salva sia col bottone sia premendo Invio nel campo del nome
        final EventHandler<ActionEvent> save = e -> listener.accept(this.nameField.getText());
        this.saveButton.setOnAction(save);
        this.nameField.setOnAction(save);
    }

    @Override
    public void showGame(final int[][] grid, final int score, final int moves) {
        this.panel.showGrid(grid);
        this.info.setText("Punteggio: " + score + "    Mosse: " + moves);
        this.message.setText("");
        setSaveBoxVisible(false);
    }

    @Override
    public void showTime(final Duration elapsed) {
        final long seconds = elapsed.toSeconds();
        this.time.setText(String.format(Locale.ROOT, "Tempo: %02d:%02d",
            seconds / SECONDS_PER_MINUTE, seconds % SECONDS_PER_MINUTE));
    }

    @Override
    public void showGameEnded(final GameState2048 result) {
        this.message.setText((result == GameState2048.WON ? "Hai vinto!" : "Game over: nessuna mossa possibile")
            + " Scrivi il tuo nome per salvare il punteggio.");
        setSaveBoxVisible(true);
        this.nameField.requestFocus();
    }

    @Override
    public void showScoreSaved() {
        this.message.setText("Punteggio salvato");
        setSaveBoxVisible(false);
        this.requestFocus();
    }

    @Override
    public void showSaveError(final String reason) {
        this.message.setText("Salvataggio non riuscito: " + reason);
        this.nameField.requestFocus();
    }

    private HBox createFooter() {
        final HBox footer = new HBox(MARGIN, this.backButton, this.newGameButton, this.undoButton);
        footer.setAlignment(Pos.CENTER);
        footer.setPadding(new Insets(MARGIN));
        return footer;
    }

    // nascosto non occupa spazio, così la griglia non si sposta
    private void setSaveBoxVisible(final boolean visible) {
        this.saveBox.setVisible(visible);
        this.saveBox.setManaged(visible);
    }

    // dopo il click il focus torna alla schermata, così le frecce restano al gioco
    private void setAction(final Button button, final Runnable listener) {
        Objects.requireNonNull(listener, LISTENER_NO_NULL);
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
