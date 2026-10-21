package it.unibo.gridgames.model.game2048;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

class BoardImplTest {

    private static final int SIZE = 4;
    private static final int TWO = 2;
    private static final int FOUR = 4;
    private static final int NOT_POWER_OF_TWO = 6;

    @Test
    void newBoardIsEmpty() {
        final Board board = new BoardImpl(SIZE);

        assertEquals(SIZE, board.getSize());
        assertEquals(SIZE * SIZE, board.getEmptyCells().size());
        assertTrue(board.isEmpty(0, 0));
    }

    @Test
    void setValueIsReadBack() {
        final Board board = new BoardImpl(SIZE);

        board.setValue(1, 2, FOUR);

        assertEquals(FOUR, board.getValue(1, 2));
        assertFalse(board.isEmpty(1, 2));
    }

    @Test
    void emptyCellsExcludeOccupiedOnes() {
        final Board board = new BoardImpl(SIZE);
        board.setValue(0, 0, TWO);
        board.setValue(3, 3, TWO);

        final List<Coordinate> empty = board.getEmptyCells();

        assertEquals(SIZE * SIZE - 2, empty.size());
        assertFalse(empty.contains(new Coordinate(0, 0)));
        assertFalse(empty.contains(new Coordinate(3, 3)));
        assertTrue(empty.contains(new Coordinate(1, 1)));
    }

    @Test
    void settingZeroEmptiesTheCell() {
        final Board board = new BoardImpl(SIZE);
        board.setValue(2, 2, TWO);

        board.setValue(2, 2, 0);

        assertTrue(board.isEmpty(2, 2));
    }

    @Test
    void boardIsBuiltFromMatrix() {
        final int[][] matrix = {
            {2, 0, 0, 0},
            {0, 4, 0, 0},
            {0, 0, 8, 0},
            {0, 0, 0, 16},
        };

        final Board board = new BoardImpl(matrix);

        assertArrayEquals(matrix, board.toMatrix());
    }

    @Test
    void constructorCopiesTheMatrix() {
        final int[][] matrix = new int[SIZE][SIZE];
        final Board board = new BoardImpl(matrix);

        matrix[0][0] = TWO;

        assertTrue(board.isEmpty(0, 0));
    }

    @Test
    void changingExportedMatrixDoesNotAffectBoard() {
        final Board board = new BoardImpl(SIZE);

        board.toMatrix()[0][0] = TWO;

        assertTrue(board.isEmpty(0, 0));
    }

    @Test
    void nonPositiveSizeIsRejected() {
        assertThrows(IllegalArgumentException.class, () -> new BoardImpl(0));
    }

    @Test
    void nonSquareMatrixIsRejected() {
        final int[][] matrix = {{0, 0}, {0}};

        assertThrows(IllegalArgumentException.class, () -> new BoardImpl(matrix));
    }

    @Test
    void outOfBoundsAccessIsRejected() {
        final Board board = new BoardImpl(SIZE);

        assertThrows(IndexOutOfBoundsException.class, () -> board.getValue(SIZE, 0));
        assertThrows(IndexOutOfBoundsException.class, () -> board.setValue(0, -1, TWO));
    }

    @Test
    void invalidTileValuesAreRejected() {
        final Board board = new BoardImpl(SIZE);

        assertThrows(IllegalArgumentException.class, () -> board.setValue(0, 0, NOT_POWER_OF_TWO));
        assertThrows(IllegalArgumentException.class, () -> board.setValue(0, 0, 1));
        assertThrows(IllegalArgumentException.class, () -> board.setValue(0, 0, -TWO));
    }

    @Test
    void invalidValueInMatrixIsRejected() {
        final int[][] matrix = {{3, 0}, {0, 0}};

        assertThrows(IllegalArgumentException.class, () -> new BoardImpl(matrix));
    }
}
