package it.unibo.gridgames.controller.sudoku;

import java.io.IOException;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

import it.unibo.gridgames.model.score.GameRecordImpl;
import it.unibo.gridgames.model.score.GameType;
import it.unibo.gridgames.model.sudoku.Difficulty;
import it.unibo.gridgames.model.sudoku.Grid;
import it.unibo.gridgames.model.sudoku.MoveResult;
import it.unibo.gridgames.model.sudoku.Position;
import it.unibo.gridgames.model.sudoku.SudokuGame;
import it.unibo.gridgames.persistence.ScoreManager;
import it.unibo.gridgames.view.sudoku.DifficultyView;
import it.unibo.gridgames.view.sudoku.SudokuView;
import javafx.scene.Scene;

/**
 * Controller that manages the screens of the Sudoku game.
 */
// Il controller fa da "ponte": riceve i clic dalle viste, chiede al model (SudokuGame)
// cosa succede e poi dice alle viste cosa mostrare. Vista e model non si parlano mai.
public class SudokuController {

    // serve per scrivere i messaggi di errore (per esempio se il salvataggio fallisce)
    private static final Logger LOGGER = Logger.getLogger(SudokuController.class.getName());

    // la scena della finestra: cambiando la sua "root" si cambia schermata
    private final Scene scene;
    // le due schermate del Sudoku, create una volta sola
    private final DifficultyView difficultyView = new DifficultyView();
    private final SudokuView sudokuView = new SudokuView();
    // il gestore dei punteggi di Chiara, per salvare i risultati
    private final ScoreManager scoreManager;
    // la partita in corso: non è final perché ogni nuova partita la sostituisce
    private SudokuGame game;

    /**
     * Creates the Sudoku controller.
     *
     * @param scene        the application scene
     * @param scoreManager the score persistence manager
     * @param onBackToMenu the action that shows the main menu again
     */
    public SudokuController(final Scene scene, final ScoreManager scoreManager, final Runnable onBackToMenu) {
        // requireNonNull: se qualcuno passa null, il programma si ferma subito con un messaggio chiaro
        this.scene = Objects.requireNonNull(scene, "Scene cannot be null");
        this.scoreManager = Objects.requireNonNull(scoreManager, "ScoreManager cannot be null");

        // schermata difficoltà: "Indietro" torna al menu, una difficoltà fa partire il gioco
        this.difficultyView.setOnBackSelected(onBackToMenu);
        this.difficultyView.setOnDifficultySelected(this::startGame);
        // schermata di gioco: "Indietro" torna alla scelta della difficoltà
        this.sudokuView.setOnBackSelected(this::start);
        // schermata di gioco: un clic su un numero fa una mossa
        this.sudokuView.setOnNumberSelected(this::play);
    }

    /**
     * Shows the difficulty selection screen.
     */
    // chiamato dal menu (bottone Sudoku) e dal bottone "Indietro" del gioco
    public void start() {
        this.scene.setRoot(this.difficultyView);
    }

    // chiamato quando il giocatore sceglie una difficoltà
    private void startGame(final Difficulty difficulty) {
        // crea una nuova partita con la difficoltà scelta
        this.game = new SudokuGame(difficulty);
        // nuova partita: zero errori
        this.sudokuView.showErrors(0);

        // copia i numeri della partita nella griglia, cella per cella
        for (int row = 0; row < Grid.SIZE; row++) {
            for (int col = 0; col < Grid.SIZE; col++) {
                final Position position = new Position(row, col);
                this.sudokuView.showCell(row, col, this.game.getValue(position), this.game.isFixed(position));
            }
        }

        // mostra la schermata con la griglia
        this.scene.setRoot(this.sudokuView);

        // --- SIMULAZIONE FINE PARTITA (da rimuovere al passo 4) ---
        this.saveGameRecord("TestSudoku", 1500, 24, 180L);
        this.saveGameRecord("TestSudoku", 2000, 12, 12L);
    }

    // chiamato quando il giocatore clicca un numero da 1 a 9
    private void play(final int value) {
        // chiede alla vista quale cella è stata scelta
        final int row = this.sudokuView.getSelectedRow();
        final int col = this.sudokuView.getSelectedCol();

        // nessuna cella scelta: non fare niente
        if (row < 0) {
            return;
        }

        // chiede al model di fare la mossa: il model controlla se il numero è giusto
        final MoveResult result = this.game.play(new Position(row, col), value);

        if (result == MoveResult.CORRECT) {
            // numero giusto: lo scrive nella cella (arancione, perché non è fisso)
            this.sudokuView.showCell(row, col, value, false);
        } else if (result == MoveResult.WRONG) {
            // numero sbagliato: aggiorna il contatore degli errori
            this.sudokuView.showErrors(this.game.getErrors());
        }
        // NOT_ALLOWED (cella fissa o già piena, partita finita): non fare niente
    }

    /**
     * Saves the final game score.
     *
     * @param playerName the player's name
     * @param finalScore the score achieved
     * @param totalMoves the total moves performed
     * @param duration   the duration in seconds
     */
    // salva un risultato nella classifica, usando lo ScoreManager di Chiara
    public void saveGameRecord(final String playerName, final int finalScore, final int totalMoves,
            final long duration) {
        // try/catch: il salvataggio su file può fallire (IOException);
        // in quel caso scriviamo l'errore nel log invece di far chiudere il programma
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
