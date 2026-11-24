package it.unibo.gridgames.view.menu;

import java.time.format.DateTimeFormatter;
import java.util.List;
import it.unibo.gridgames.model.score.GameRecordImpl;
import it.unibo.gridgames.view.UIConstants;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import it.unibo.gridgames.model.score.GameType;
import it.unibo.gridgames.model.score.ScoreStats;
import java.util.Map;
import java.util.Comparator;
import java.util.EnumMap;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;

/**
 * LeaderboardView represents the user interface for displaying the leaderboard of game scores.
 */
public final class LeaderboardView extends BorderPane {
    private static final double LIST_VERTICAL_MARGIN = 10.0;
    private static final double LIST_HORIZONTAL_MARGIN = 20.0;
    private static final double TOP_BOX_SPACING = 10.0;
    private final Label statsLabel = new Label();
    private final Button btnBack = new Button("Indietro");
    private final Label titleLabel = new Label("Classifica");
    private final ListView<String> scoreListView = new ListView<>();
    private static final int CHART_MAX_HEIGHT = 240;
    private static final DateTimeFormatter CHART_DATE_FORMAT =
        DateTimeFormatter.ofPattern("dd/MM HH:mm");
    private static final double CHARTS_SPACING = 10.0;
    private final Map<GameType, LineChart<String, Number>> gameCharts = new EnumMap<>(GameType.class);

    /**
     * Constructs a new LeaderboardView instance and initializes the UI components.
     */
    public LeaderboardView() {
        this.setStyle("-fx-background-color: #f7f9fa;");

        this.btnBack.setStyle("-fx-background-color: darkorange; -fx-text-fill: white; -fx-font-weight: bold;");
        this.btnBack.setMinWidth(120);
        this.btnBack.prefWidthProperty().bind(this.widthProperty().multiply(0.25));
        this.titleLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #333333;");
        this.scoreListView.setMaxWidth(Double.MAX_VALUE);
        this.scoreListView.setMaxHeight(Double.MAX_VALUE);

        final VBox topBox = new VBox(TOP_BOX_SPACING);
        topBox.setAlignment(Pos.CENTER);
        topBox.getChildren().add(this.btnBack);
        topBox.getChildren().add(this.titleLabel);
        VBox.setMargin(this.btnBack, new Insets(UIConstants.BUTTON_BACK.getValue()));
        topBox.setAlignment(Pos.CENTER);

        final VBox bottomBox = new VBox(this.statsLabel);
        bottomBox.setAlignment(Pos.CENTER);
        this.setBottom(bottomBox);
        this.setTop(topBox);

        BorderPane.setMargin(this.scoreListView, new Insets(
            LIST_VERTICAL_MARGIN, 
            LIST_HORIZONTAL_MARGIN, 
            LIST_HORIZONTAL_MARGIN, 
            LIST_HORIZONTAL_MARGIN
        ));
        
        final HBox chartsBox = new HBox(CHARTS_SPACING);
        chartsBox.setAlignment(Pos.CENTER);
        for (final GameType type : GameType.values()) {
            final CategoryAxis xAxis = new CategoryAxis();
            xAxis.setLabel("Data");
            final NumberAxis yAxis = new NumberAxis();
            yAxis.setLabel("Punteggio");

            final LineChart<String, Number> chart = new LineChart<>(xAxis, yAxis);
            chart.setTitle(type.name());
            chart.setLegendVisible(false);
            chart.setAnimated(false);
            chart.setMaxHeight(CHART_MAX_HEIGHT);
            HBox.setHgrow(chart, Priority.ALWAYS);

            this.gameCharts.put(type, chart);
            chartsBox.getChildren().add(chart);
        }

        final VBox centerBox = new VBox(TOP_BOX_SPACING, this.scoreListView, chartsBox);
        VBox.setVgrow(this.scoreListView, Priority.ALWAYS);
        BorderPane.setMargin(centerBox, new Insets(
            LIST_VERTICAL_MARGIN,
            LIST_HORIZONTAL_MARGIN,
            LIST_HORIZONTAL_MARGIN,
            LIST_HORIZONTAL_MARGIN
        ));
        this.setCenter(centerBox);
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

        final Map<GameType, Double> averages = ScoreStats.calculateAverage(scores);
        for (final GameRecordImpl record : scores) {
            if (lastGame != record.getGameType()) {
                if (lastGame != null) {
                    this.scoreListView.getItems().add(""); 
                }
                final GameType currentGame = record.getGameType();
                final double avg = averages.getOrDefault(currentGame, 0.0);

                this.scoreListView.getItems().add("=== " + currentGame.name() + " ===");
                this.scoreListView.getItems().add(String.format("   Media punteggi: %.2f pt", avg));
                this.scoreListView.getItems().add("----------------------------------------");
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

        if (this.statsLabel != null) {
                ((javafx.scene.control.Labeled) this.statsLabel).setText("");
            }
        }

        /**
     * Updates the score trend line charts for all game types using the given records.
     *
     * @param records the complete list of game records
     */
    public void updateAllCharts(final List<GameRecordImpl> records) {
        for (final GameType type : GameType.values()) {
            final LineChart<String, Number> chart = this.gameCharts.get(type);
            chart.getData().clear();

            final XYChart.Series<String, Number> series = new XYChart.Series<>();
            records.stream()
                .filter(record -> record.getGameType() == type)
                .sorted(Comparator.comparing(GameRecordImpl::getDateTime))
                .forEach(record -> {
                    final String label = record.getDateTime().format(CHART_DATE_FORMAT);
                    series.getData().add(new XYChart.Data<>(label, record.getScore()));
                });

            chart.getData().add(series);
        }
    }
}
