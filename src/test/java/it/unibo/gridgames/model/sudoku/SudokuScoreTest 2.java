package it.unibo.gridgames.model.sudoku;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class SudokuScoreTest {

    private static final long HALF_HARD_TIME = 900L;
    private static final int EXPECTED_EXAMPLE_SCORE = 675;

    @Test
    void gameNotWonIsWorthZero() {
        assertEquals(0, SudokuScore.compute(Difficulty.HARD, 0, 0, GameStatus.LOST));
        assertEquals(0, SudokuScore.compute(Difficulty.HARD, 0, 0, GameStatus.PLAYING));
    }

    @Test
    void instantWinWithoutErrorsGetsBasePoints() {
        for (final Difficulty difficulty : Difficulty.values()) {
            assertEquals(difficulty.getBasePoints(), SudokuScore.compute(difficulty, 0, 0, GameStatus.WON));
        }
    }

    @Test
    void exampleFromDesignIsRight() {
        assertEquals(EXPECTED_EXAMPLE_SCORE,
                SudokuScore.compute(Difficulty.HARD, HALF_HARD_TIME, 1, GameStatus.WON));
    }

    @Test
    void overTimeLimitGetsHalfPoints() {
        final Difficulty difficulty = Difficulty.MEDIUM;
        final long tooLong = difficulty.getTimeLimitSeconds() * 2;
        assertEquals(difficulty.getBasePoints() / 2,
                SudokuScore.compute(difficulty, tooLong, 0, GameStatus.WON));
    }

    @Test
    void moreErrorsGiveLowerScore() {
        final int noErrors = SudokuScore.compute(Difficulty.EASY, 0, 0, GameStatus.WON);
        final int twoErrors = SudokuScore.compute(Difficulty.EASY, 0, 2, GameStatus.WON);
        assertTrue(twoErrors < noErrors);
    }

    @Test
    void negativeValuesAreRejected() {
        assertThrows(IllegalArgumentException.class,
                () -> SudokuScore.compute(Difficulty.EASY, -1, 0, GameStatus.WON));
        assertThrows(IllegalArgumentException.class,
                () -> SudokuScore.compute(Difficulty.EASY, 0, -1, GameStatus.WON));
    }
}
