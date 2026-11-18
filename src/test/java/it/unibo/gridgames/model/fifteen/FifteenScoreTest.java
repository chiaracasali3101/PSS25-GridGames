package it.unibo.gridgames.model.fifteen;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class FifteenScoreTest {

    private static final int POINTS_PER_MOVE = 10;
    private static final int MOVES = 150;
    private static final int SCORE_OF_MOVES = 1_500;

    @Test
    void noMovesAreWorthNothing() {
        assertEquals(0, FifteenScore.compute(0));
    }

    @Test
    void oneMoveIsWorthTenPoints() {
        assertEquals(POINTS_PER_MOVE, FifteenScore.compute(1));
    }

    @Test
    void scoreIsTheMovesTimesTen() {
        assertEquals(SCORE_OF_MOVES, FifteenScore.compute(MOVES));
    }

    @Test
    void everyMoveRaisesTheScore() {
        assertEquals(FifteenScore.compute(MOVES) + POINTS_PER_MOVE, FifteenScore.compute(MOVES + 1));
    }

    @Test
    void fewerMovesGiveALowerScore() {
        assertTrue(FifteenScore.compute(MOVES) < FifteenScore.compute(MOVES + 1));
    }

    @Test
    void negativeMovesAreRejected() {
        assertThrows(IllegalArgumentException.class, () -> FifteenScore.compute(-1));
    }
}
