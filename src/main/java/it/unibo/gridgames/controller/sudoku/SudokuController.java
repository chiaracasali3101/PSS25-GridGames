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
import it.unibo.gridgames.view.sudoku.SudokuView;
import it.unibo.gridgames.model.sudoku.Grid;
import it.unibo.gridgames.model.sudoku.Position;
import it.unibo.gridgames.model.sudoku.SudokuGame;
import javafx.scene.Scene;

/**
 * Controller that manages the screens of the Sudoku game.
 */
public class SudokuController {

    private static final Logger LOGGER = Logger.getLogger(SudokuController.class.getName());

    private final Scene scene;
    private final DifficultyView difficultyView = new DifficultyView();
    private final SudokuView sudokuView = new SudokuView(); // NUOVO
    private final ScoreManager scoreManager;
    private SudokuGame game; // la partita in corso

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
        this.sudokuView.setOnBackSelected(this::start); // NUOVO: dal gioco torna alla scelta della difficoltà
    }

    /**
     * Shows the difficulty selection screen.
     */
    public void start() {
        this.scene.setRoot(this.difficultyView);
    }

    private void startGame(final Difficulty difficulty) {
        // crea una nuova partita
        this.game = new SudokuGame(difficulty);

        // copia i numeri della partita nella griglia
        for (int row = 0; row < Grid.SIZE; row++) {
            for (int col = 0; col < Grid.SIZE; col++) {
                final Position position = new Position(row, col);
                this.sudokuView.showCell(row, col, this.game.getValue(position), this.game.isFixed(position));
            }
        }
        this.scene.setRoot(this.sudokuView); // mostra la griglia

        // --- SIMULAZIONE FINE PARTITA (da rimuovere dopo il test) ---
        this.saveGameRecord("TestSudoku", 1500, 24, 180L);
        this.saveGameRecord("TestSudoku", 2000, 12, 12L);
    }

    /**
     * Saves the final game score.
     *
     * @param playerName the player's name
     * @param finalScore the score achieved
     * @param totalMoves the total moves performed
     * @param duration   the duration in seconds
     */
    public void saveGameRecord(final String playerName, final int finalScore, final int totalMoves,
            final long duration) {
        try {
            this.scoreManager.saveScore(new GameRecordImpl(
                    playerName,
                    finalScore,
                    GameType.SUDOKU,
                    totalMoves,
                    duration,
                    java.time.LocalDateTime.now()));
        } catch (final IOException e) {
            LOGGER.log(Level.SEVERE, "Errore durante il salvataggio del punteggio", e);
        }
    }
}
