package it.unibo.gridgames.view.game2048;

import java.util.Map;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Region;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

/**
 * The 4x4 grid of the 2048 game, where each tile is colored according to its value.
 */
public final class Game2048Panel extends GridPane {

    private static final int SIZE = 4;
    private static final double TILE_SIZE = 70;
    private static final double GAP = 8;
    private static final double FONT_SIZE = 24;
    private static final int LAST_DARK_TEXT_VALUE = 4;
    private static final String DARK_TEXT = "#776e65";
    private static final String LIGHT_TEXT = "#f9f6f2";
    private static final Map<Integer, String> BACKGROUNDS = Map.ofEntries(
        Map.entry(0, "#cdc1b4"),
        Map.entry(2, "#eee4da"),
        Map.entry(4, "#ede0c8"),
        Map.entry(8, "#f2b179"),
        Map.entry(16, "#f59563"),
        Map.entry(32, "#f67c5f"),
        Map.entry(64, "#f65e3b"),
        Map.entry(128, "#edcf72"),
        Map.entry(256, "#edcc61"),
        Map.entry(512, "#edc850"),
        Map.entry(1024, "#edc53f"),
        Map.entry(2048, "#edc22e")
    );

    private final Label[][] tiles = new Label[SIZE][SIZE];

    /**
     * Creates an empty grid.
     */
    public Game2048Panel() {
        super();
        this.setHgap(GAP);
        this.setVgap(GAP);
        this.setPadding(new Insets(GAP));
        this.setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
        this.setStyle("-fx-background-color: #bbada0; -fx-background-radius: 6;");
        for (int row = 0; row < SIZE; row++) {
            for (int column = 0; column < SIZE; column++) {
                this.tiles[row][column] = createTile();
                this.add(this.tiles[row][column], column, row);
            }
        }
        this.showGrid(new int[SIZE][SIZE]);
    }

    /**
     * Draws the given cell values.
     *
     * @param grid the 4x4 cell values indexed by [row][column], where 0 means empty
     */
    public void showGrid(final int[][] grid) {
        for (int row = 0; row < SIZE; row++) {
            for (int column = 0; column < SIZE; column++) {
                final int value = grid[row][column];
                final Label tile = this.tiles[row][column];
                tile.setText(value == 0 ? "" : Integer.toString(value));
                tile.setStyle(styleOf(value));
            }
        }
    }

    private static Label createTile() {
        final Label tile = new Label();
        tile.setMinSize(TILE_SIZE, TILE_SIZE);
        tile.setPrefSize(TILE_SIZE, TILE_SIZE);
        tile.setAlignment(Pos.CENTER);
        tile.setFont(Font.font("Arial", FontWeight.BOLD, FONT_SIZE));
        return tile;
    }

    private static String styleOf(final int value) {
        final String text = value <= LAST_DARK_TEXT_VALUE ? DARK_TEXT : LIGHT_TEXT;
        return "-fx-background-color: " + BACKGROUNDS.get(value)
            + "; -fx-text-fill: " + text
            + "; -fx-background-radius: 4;";
    }
}
