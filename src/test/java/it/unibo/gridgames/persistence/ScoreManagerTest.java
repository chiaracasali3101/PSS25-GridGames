package it.unibo.gridgames.persistence;

import it.unibo.gridgames.model.GameRecordImpl;
import it.unibo.gridgames.model.GameType;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests for the ScoreManager class.
 * ScoreManagerTest
 */
class ScoreManagerTest {

    private static final String P1 = "Alice";
    private static final String P2 = "Bob";
    private static final String P3 = "Charlie";

    private static final long DURATION_60 = 60L;
    private static final long DURATION_120 = 120L;
    private static final long DURATION_180 = 180L;

    private static final int SCORE_20 = 20;
    private static final int SCORE_100 = 100;
    private static final int SCORE_200 = 200;
    private static final int SCORE_300 = 300;

    private static final int MOVES_10 = 10;
    private static final int MOVES_20 = 20;
    private static final int MOVES_30 = 30;

    private static final double EXPECTED_AVERAGE = 200.0;
    private static final double DELTA = 0.001;

    private static final int EXPECTED_TOTAL_RECORDS = 3;

    @TempDir
    private Path tempDir;

    private ScoreManager scoreManager;

    /**
     * Setup method to initialize the ScoreManager instance before each test.
     */
    @BeforeEach
    public void setUp() {
        final Path tempFile = tempDir.resolve("scores_test.csv");
        this.scoreManager = new ScoreManagerImpl(tempFile.toString());
    }

    /**
     * Test for the loadAllScores method of ScoreManager when the file does not exist.
     * 
     * @throws IOException issued if there is an error reading or writing the score file.
     */
    @Test
    void testLoadScoresWhenFileDoesNotExist() throws IOException {
        final List<GameRecordImpl> scores = this.scoreManager.loadAllScores();
        assertTrue(scores.isEmpty(), "La lista deve essere vuota se il file non esiste");
    }

    /**
     * Test for the getNumberOfGamesPlayed method of ScoreManager.
     * 
     * @throws IOException issued if there is an error reading or writing the score file.
     */
    @Test
    void testGetNumberOfGamesPlayed() throws IOException {
        final LocalDateTime now = LocalDateTime.now().withNano(0);
        final GameRecordImpl record1 = new GameRecordImpl(P1, 100, GameType.GAME_2048, 10, DURATION_60, now);
        final GameRecordImpl record2 = new GameRecordImpl(P2, 200, GameType.SUDOKU, 20, DURATION_120, now);
        final GameRecordImpl record3 = new GameRecordImpl(P3, 300, GameType.PUZZLE_15, 0, DURATION_180, now);

        this.scoreManager.saveScore(record1);
        this.scoreManager.saveScore(record2);
        this.scoreManager.saveScore(record3);

        assertEquals(1, this.scoreManager.getNumberOfGamesPlayed(GameType.GAME_2048));
        assertEquals(1, this.scoreManager.getNumberOfGamesPlayed(GameType.SUDOKU));
        assertEquals(1, this.scoreManager.getNumberOfGamesPlayed(GameType.PUZZLE_15));
    }

    /**
     * Test for the getTopScores method of ScoreManager.
     * 
     * @throws IOException issued if there is an error reading or writing the score file.
     */
    @Test
    void testGetTopScore() throws IOException {
        final LocalDateTime now = LocalDateTime.now().withNano(0);
        final GameRecordImpl record1 = new GameRecordImpl(P1, 100, GameType.GAME_2048, 10, DURATION_60, now);
        final GameRecordImpl record2 = new GameRecordImpl(P2, 200, GameType.SUDOKU, 20, DURATION_120, now);
        final GameRecordImpl record3 = new GameRecordImpl(P3, 300, GameType.PUZZLE_15, 30, DURATION_180, now);

        this.scoreManager.saveScore(record1);
        this.scoreManager.saveScore(record2);
        this.scoreManager.saveScore(record3);

        final List<GameRecordImpl> topScores = this.scoreManager.getTopScores(GameType.GAME_2048, 1);
 
        assertEquals(1, topScores.size(), "Dovrebbe esserci un solo record nella classifica");

        assertEquals(100, topScores.get(0).getScore(), "Il punteggio dovrebbe essere 100");
    }

    /**
     * Test for the getHighestScore method of ScoreManager.
     * 
     * @throws IOException issued if there is an error reading or writing the score file.
     */
    @Test
    void testGetHighestScore() throws IOException {
    final LocalDateTime now = LocalDateTime.now().withNano(0);
    this.scoreManager.saveScore(new GameRecordImpl(
        P1, SCORE_100, GameType.GAME_2048, 10, DURATION_60, now));
    this.scoreManager.saveScore(new GameRecordImpl(
        P2, 
        SCORE_300, 
        GameType.GAME_2048, 
        SCORE_20, 
        DURATION_120, now)); // BOB su GAME_2048
    this.scoreManager.saveScore(new GameRecordImpl(
        P3, 
        SCORE_200, 
        GameType.PUZZLE_15, 
        SCORE_20,
        DURATION_180, now));

    final GameRecordImpl highest = this.scoreManager.getHighestScore(GameType.GAME_2048);
    assertNotNull(highest, "Il punteggio più alto non dovrebbe essere null");
    assertEquals(SCORE_300, highest.getScore());
    assertEquals(P2, highest.getPlayerName());
}

    /**
     * Test for the getAverageScore method of ScoreManager.
     * 
     * @throws IOException if an error occurs while saving test scores.
     */
    @Test 
    void testGetAverageScore() throws IOException {
        this.scoreManager.saveScore(new GameRecordImpl(
            P1, SCORE_100, GameType.GAME_2048, 10, DURATION_60, LocalDateTime.now()));
        this.scoreManager.saveScore(new GameRecordImpl(
            P2, SCORE_200, GameType.GAME_2048, MOVES_20, DURATION_120, LocalDateTime.now()));
        this.scoreManager.saveScore(new GameRecordImpl(
            P3, SCORE_300, GameType.GAME_2048, MOVES_30, DURATION_180, LocalDateTime.now()));

        final double actualAverage = this.scoreManager.getAverageScore(GameType.GAME_2048);
        assertEquals(EXPECTED_AVERAGE, actualAverage, DELTA, "La media calcolata non è corretta");
    }

    /**
     * Test for the loadAllScores method of ScoreManager.
     * 
     * @throws IOException if an I/O error occurs during file operations.
     */
    @Test 
    void testloadAllScores() throws IOException {
        this.scoreManager.saveScore(new GameRecordImpl(
            P1, SCORE_100, GameType.GAME_2048, MOVES_10, DURATION_60, LocalDateTime.now()));
        this.scoreManager.saveScore(new GameRecordImpl(
            P2, SCORE_200, GameType.GAME_2048, MOVES_20, DURATION_120, LocalDateTime.now()));
        this.scoreManager.saveScore(new GameRecordImpl(
            P3, SCORE_300, GameType.GAME_2048, MOVES_30, DURATION_180, LocalDateTime.now()));

        final List<GameRecordImpl> allScores = this.scoreManager.loadAllScores();
        assertEquals(EXPECTED_TOTAL_RECORDS, allScores.size(), 
        "Il numero totale di record caricati non corrisponde a quello salvato");
    }
}
