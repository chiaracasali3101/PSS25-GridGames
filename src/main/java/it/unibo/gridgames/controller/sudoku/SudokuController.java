package it.unibo.gridgames.controller.sudoku;

import java.io.IOException;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

import it.unibo.gridgames.model.score.GameRecordImpl;
import it.unibo.gridgames.model.score.GameType;
import it.unibo.gridgames.model.sudoku.Difficulty;
import it.unibo.gridgames.persistence.ScoreManager;
import it.unibo.gridgames.view.sudoku.DifficultyView;
import javafx.scene.Scene;

/**
 * Controller that manages the screens of the Sudoku game.
 */
public class SudokuController {

    private static final Logger LOGGER = Logger.getLogger(SudokuController.class.getName());

    private final Scene scene;
    private final DifficultyView difficultyView = new DifficultyView();
    private final ScoreManager scoreManager;

    /**
     * Creates the Sudoku controller.
     *
     * @param scene        the application scene
     * @param scoreManager the score persistence manager
     * @param onBackToMenu the action that shows the main menu again
     */
    public SudokuController(final Scene scene, final ScoreManager scoreManager, final Runnable onBackToMenu) {
        this.scene = Objects.requireNonNull(scene, "Scene cannot be null");
        this.scoreManager = Objects.requireNonNull(scoreManager, "ScoreManager cannot be null");

        this.difficultyView.setOnBackSelected(onBackToMenu);
        this.difficultyView.setOnDifficultySelected(this::startGame);
    } // <-- Il costruttore si chiude qui!

    /**
     * Shows the difficulty selection screen.
     */
    public void start() {
        this.scene.setRoot(this.difficultyView);
    }

    private void startGame(final Difficulty difficulty) {
        // temporaneo: qui poi si aprirà la SudokuView
        System.out.println("Difficoltà scelta: " + difficulty);

        // --- SIMULAZIONE FINE PARTITA (da rimuovere dopo il test) ---
        this.saveGameRecord("TestSudoku", 1500, 24, 180L);
        this.saveGameRecord("TestSudoku", 2000, 12, 12L);
    }

    /**
     * Saves the final game score.
     *
     * @param playerName   the player's name
     * @param finalScore   the score achieved
     * @param totalMoves   the total moves performed
     * @param duration     the duration in seconds
     */
    public void saveGameRecord(final String playerName, final int finalScore, final int totalMoves, final long duration) {
        try {
            this.scoreManager.saveScore(new GameRecordImpl(
                playerName,
                finalScore,
                GameType.SUDOKU,
                totalMoves,
                duration,
                java.time.LocalDateTime.now()
            ));
        } catch (final IOException e) {
            LOGGER.log(Level.SEVERE, "Errore durante il salvataggio del punteggio", e);
        }
    }
}