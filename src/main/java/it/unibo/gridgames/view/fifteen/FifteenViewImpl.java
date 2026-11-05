package it.unibo.gridgames.view.fifteen;

import it.unibo.gridgames.model.fifteen.Direction;
import it.unibo.gridgames.model.fifteen.FifteenBoard;
import java.time.Duration;
import java.util.Locale;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextInputDialog;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

/**
 * JavaFX implementation of {@link FifteenView}.
 */
public final class FifteenViewImpl extends BorderPane implements FifteenView {

    private static final double TILE_SIZE = 60;
    private static final double SPACING = 5;
    private static final double MARGIN = 10;
    private static final int SECONDS_PER_MINUTE = 60;
    private static final String BUTTON_STYLE =
        "-fx-background-color: #e67e22; -fx-text-fill: white; -fx-background-radius: 8;";
    private static final String TILE_STYLE = BUTTON_STYLE + " -fx-font-size: 20; -fx-font-weight: bold;";
    private static final String COUNTER_STYLE = "-fx-font-size: 16;";
    private static final Map<KeyCode, Direction> ARROWS = Map.of(
        KeyCode.UP, Direction.UP,
        KeyCode.DOWN, Direction.DOWN,
        KeyCode.LEFT, Direction.LEFT,
        KeyCode.RIGHT, Direction.RIGHT
    );

    private final Consumer<Parent> rootSetter;
    private final Button[][] tiles = new Button[FifteenBoard.SIZE][FifteenBoard.SIZE];
    private final Label moves = new Label();
    private final Label time = new Label();
    private final Label message = new Label();
    private BiConsumer<Integer, Integer> tileListener = (row, column) -> { };
    private Consumer<Direction> directionListener = direction -> { };
    private Runnable newGameListener = () -> { };
    private Runnable backListener = () -> { };

    /**
     * Creates the screen, without showing it.
     *
     * @param scene the scene of the application, where the screen is shown
     * @throws IllegalArgumentException if the scene is null
     */
    public FifteenViewImpl(final Scene scene) {
        super();
        if (scene == null) {
            throw new IllegalArgumentException("The scene must not be null");
        }
        this.rootSetter = scene::setRoot;
        this.setTop(createCounters());
        this.setCenter(createBoard());
        this.setBottom(createFooter());
        this.setFocusTraversable(true);
        this.setOnKeyPressed(this::handleKey);
    }

    @Override
    public void show() {
        this.rootSetter.accept(this);
        this.requestFocus();
    }

    @Override
    public void showBoard(final int[][] board) {
        this.message.setText("");
        for (int row = 0; row < FifteenBoard.SIZE; row++) {
            for (int column = 0; column < FifteenBoard.SIZE; column++) {
                final int tile = board[row][column];
                this.tiles[row][column].setText(String.valueOf(tile));
                this.tiles[row][column].setVisible(tile != FifteenBoard.EMPTY);
            }
        }
    }

    @Override
    public void showMoves(final int count) {
        this.moves.setText("Mosse: " + count);
    }

    @Override
    public void showTime(final Duration elapsed) {
        this.time.setText(String.format(
            Locale.ROOT,
            "Tempo: %02d:%02d",
            elapsed.toSeconds() / SECONDS_PER_MINUTE,
            elapsed.toSeconds() % SECONDS_PER_MINUTE
        ));
    }

    @Override
    public void showVictory() {
        this.message.setText("Hai vinto!");
    }

    @Override
    public void showIllegalMove() {
        this.message.setText("Mossa non valida");
    }

    @Override
    public void askPlayerName(final Consumer<String> listener) {
        final TextInputDialog dialog = new TextInputDialog();
        dialog.initOwner(this.getScene().getWindow());
        dialog.setTitle("Hai vinto!");
        dialog.setHeaderText("Inserisci il tuo nome per salvare il risultato");
        dialog.setContentText("Nome:");
        dialog.showAndWait().ifPresent(listener);
    }

    @Override
    public void showInvalidPlayerName() {
        final Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.initOwner(this.getScene().getWindow());
        alert.setTitle("Nome non valido");
        alert.setHeaderText(null);
        alert.setContentText("Il nome non può essere vuoto e non può contenere virgole.");
        alert.showAndWait();
    }

    @Override
    public void showSaveError() {
        this.message.setText("Impossibile salvare il risultato");
    }

    @Override
    public void onTileSelected(final BiConsumer<Integer, Integer> listener) {
        this.tileListener = listener;
    }

    @Override
    public void onDirectionSelected(final Consumer<Direction> listener) {
        this.directionListener = listener;
    }

    @Override
    public void onNewGameSelected(final Runnable listener) {
        this.newGameListener = listener;
    }

    @Override
    public void onBackSelected(final Runnable listener) {
        this.backListener = listener;
    }

    private HBox createCounters() {
        this.moves.setStyle(COUNTER_STYLE);
        this.time.setStyle(COUNTER_STYLE);
        final Region gap = new Region();
        HBox.setHgrow(gap, Priority.ALWAYS);
        final HBox counters = new HBox(this.moves, gap, this.time);
        counters.setPadding(new Insets(MARGIN));
        return counters;
    }

    private GridPane createBoard() {
        final GridPane board = new GridPane();
        board.setAlignment(Pos.CENTER);
        board.setHgap(SPACING);
        board.setVgap(SPACING);
        for (int row = 0; row < FifteenBoard.SIZE; row++) {
            for (int column = 0; column < FifteenBoard.SIZE; column++) {
                this.tiles[row][column] = createTile(row, column);
                board.add(this.tiles[row][column], column, row);
            }
        }
        return board;
    }

    private Button createTile(final int row, final int column) {
        final Button tile = createButton("", () -> this.tileListener.accept(row, column));
        tile.setStyle(TILE_STYLE);
        tile.setMinSize(TILE_SIZE, TILE_SIZE);
        tile.setMaxSize(TILE_SIZE, TILE_SIZE);
        return tile;
    }

    private VBox createFooter() {
        final Region gap = new Region();
        HBox.setHgrow(gap, Priority.ALWAYS);
        final HBox buttons = new HBox(
            createButton("← Menu", this::notifyBack),
            gap,
            createButton("Nuova partita", this::notifyNewGame)
        );
        final VBox footer = new VBox(SPACING, this.message, buttons);
        footer.setAlignment(Pos.CENTER);
        footer.setPadding(new Insets(MARGIN));
        return footer;
    }

    // The buttons never take the focus, because a focused button would use the arrow keys to move it.
    private Button createButton(final String text, final Runnable action) {
        final Button button = new Button(text);
        button.setStyle(BUTTON_STYLE);
        button.setFocusTraversable(false);
        button.setOnAction(e -> action.run());
        return button;
    }

    private void notifyBack() {
        this.backListener.run();
    }

    private void notifyNewGame() {
        this.newGameListener.run();
    }

    private void handleKey(final KeyEvent event) {
        final Direction direction = ARROWS.get(event.getCode());
        if (direction != null) {
            this.directionListener.accept(direction);
            event.consume();
        }
    }
}
