package it.unibo.gridgames.controller.leaderboard;

import java.util.Objects;

import it.unibo.gridgames.controller.sudoku.SudokuController;
import it.unibo.gridgames.persistence.ScoreManager;
import it.unibo.gridgames.view.menu.LeaderboardView;
import it.unibo.gridgames.view.menu.MainAppView;
import javafx.scene.Scene;

/**
 * Controller for handling main menu navigation.
 */
public class MenuController {
    private final MainAppView view;
    private final Scene scene;
    private final ScoreManager scoreManager;

    /**
     * Construct Menu Controller.
     *
     * @param view the main app view
     * @param scene the application scene
     * @param scoreManager the manager of the saved scores
     */
    public MenuController(final MainAppView view, final Scene scene, final ScoreManager scoreManager) {
        this.view = view;
        this.scene = scene;
        this.scoreManager = Objects.requireNonNull(scoreManager, "ScoreManager cannot be null");

        final LeaderboardView leaderboardView = new LeaderboardView(); //pannello grafico per la classifica
        final LeaderBoardController leaderboardController =
            new LeaderBoardController(scoreManager, view, leaderboardView);

        //carica i dati salvati e popola la vista della classifica
        leaderboardController.loadAndDisplayScores();

        //configura il tasto 'Indietro' per ripristinare il menu principale
        leaderboardView.setOnBackSelected(() -> this.scene.setRoot(this.view));

        this.view.setOnLeaderboardSelected(() -> {
            leaderboardController.loadAndDisplayScores();
            this.scene.setRoot(leaderboardView);
        });

        //Sudoku: il pulsante apre la schermata delle difficoltà
        final SudokuController sudokuController =
            new SudokuController(this.scene, this.scoreManager, () -> this.scene.setRoot(this.view));
        this.view.setOnSudokuSelected(sudokuController::start);

        this.scene.setRoot(this.view);
    }
}