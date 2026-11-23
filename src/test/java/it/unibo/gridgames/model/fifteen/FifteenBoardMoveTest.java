package it.unibo.gridgames.model.fifteen;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class FifteenBoardMoveTest {

    private static final int[][] SOLVED = {
        {1, 2, 3, 4},
        {5, 6, 7, 8},
        {9, 10, 11, 12},
        {13, 14, 15, 0},
    };
    private static final int[][] EMPTY_CELL_FIRST = {
        {0, 1, 2, 3},
        {4, 5, 6, 7},
        {8, 9, 10, 11},
        {12, 13, 14, 15},
    };
    private static final int[][] MIXED = {
        {5, 1, 2, 4},
        {9, 6, 0, 3},
        {13, 10, 7, 8},
        {14, 15, 11, 12},
    };
    private static final int[][] MIXED_AFTER_UP = {
        {5, 1, 2, 4},
        {9, 6, 7, 3},
        {13, 10, 0, 8},
        {14, 15, 11, 12},
    };
    private static final int[][] MIXED_AFTER_DOWN = {
        {5, 1, 0, 4},
        {9, 6, 2, 3},
        {13, 10, 7, 8},
        {14, 15, 11, 12},
    };
    private static final int[][] MIXED_AFTER_LEFT = {
        {5, 1, 2, 4},
        {9, 6, 3, 0},
        {13, 10, 7, 8},
        {14, 15, 11, 12},
    };
    private static final int[][] MIXED_AFTER_RIGHT = {
        {5, 1, 2, 4},
        {9, 0, 6, 3},
        {13, 10, 7, 8},
        {14, 15, 11, 12},
    };
    private static final int EMPTY_ROW = 1;
    private static final int EMPTY_COLUMN = 2;

    @Test
    void tileBelowEmptyCellSlidesUp() {
        assertDirectionApplied(Direction.UP, MIXED_AFTER_UP);
    }

    @Test
    void tileAboveEmptyCellSlidesDown() {
        assertDirectionApplied(Direction.DOWN, MIXED_AFTER_DOWN);
    }

    @Test
    void tileOnTheRightOfEmptyCellSlidesLeft() {
        assertDirectionApplied(Direction.LEFT, MIXED_AFTER_LEFT);
    }

    @Test
    void tileOnTheLeftOfEmptyCellSlidesRight() {
        assertDirectionApplied(Direction.RIGHT, MIXED_AFTER_RIGHT);
    }

    @Test
    void emptyCellPositionFollowsTheMove() {
        final FifteenBoard board = new FifteenBoardImpl(MIXED);
        board.move(Direction.UP);
        assertEquals(EMPTY_ROW + 1, board.getEmptyRow());
        assertEquals(EMPTY_COLUMN, board.getEmptyColumn());
    }

    @Test
    void directionWithoutTileToSlideIsRefused() {
        assertDirectionRefused(SOLVED, Direction.UP);
        assertDirectionRefused(SOLVED, Direction.LEFT);
        assertDirectionRefused(EMPTY_CELL_FIRST, Direction.DOWN);
        assertDirectionRefused(EMPTY_CELL_FIRST, Direction.RIGHT);
    }

    @Test
    void nullDirectionIsRejected() {
        final FifteenBoard board = new FifteenBoardImpl();
        assertThrows(IllegalArgumentException.class, () -> board.move(null));
    }

    @Test
    void movesInOppositeDirectionsRestoreTheLayout() {
        final FifteenBoard board = new FifteenBoardImpl();
        board.move(Direction.DOWN);
        assertFalse(board.isSolved());
        board.move(Direction.UP);
        assertTrue(board.isSolved());
    }

    @Test
    void adjacentTileBelowMovesIntoEmptyCell() {
        assertTileApplied(EMPTY_ROW + 1, EMPTY_COLUMN, MIXED_AFTER_UP);
    }

    @Test
    void adjacentTileAboveMovesIntoEmptyCell() {
        assertTileApplied(EMPTY_ROW - 1, EMPTY_COLUMN, MIXED_AFTER_DOWN);
    }

    @Test
    void adjacentTileOnTheRightMovesIntoEmptyCell() {
        assertTileApplied(EMPTY_ROW, EMPTY_COLUMN + 1, MIXED_AFTER_LEFT);
    }

    @Test
    void adjacentTileOnTheLeftMovesIntoEmptyCell() {
        assertTileApplied(EMPTY_ROW, EMPTY_COLUMN - 1, MIXED_AFTER_RIGHT);
    }

    @Test
    void diagonalTileIsRefused() {
        assertTileRefused(EMPTY_ROW - 1, EMPTY_COLUMN - 1);
    }

    @Test
    void distantTileInTheSameRowIsRefused() {
        assertTileRefused(EMPTY_ROW, EMPTY_COLUMN - 2);
    }

    @Test
    void distantTileInTheSameColumnIsRefused() {
        assertTileRefused(EMPTY_ROW + 2, EMPTY_COLUMN);
    }

    @Test
    void emptyCellCannotMoveOntoItself() {
        assertTileRefused(EMPTY_ROW, EMPTY_COLUMN);
    }

    @Test
    void movingOutsideTheBoardIsRejected() {
        final FifteenBoard board = new FifteenBoardImpl();
        assertThrows(IllegalArgumentException.class, () -> board.moveTile(-1, 0));
        assertThrows(IllegalArgumentException.class, () -> board.moveTile(0, -1));
        assertThrows(IllegalArgumentException.class, () -> board.moveTile(FifteenBoard.SIZE, 0));
        assertThrows(IllegalArgumentException.class, () -> board.moveTile(0, FifteenBoard.SIZE));
    }

    @Test
    void newBoardHasNoMoves() {
        assertEquals(0, new FifteenBoardImpl().getMoveCount());
        assertEquals(0, new FifteenBoardImpl(MIXED).getMoveCount());
    }

    @Test
    void everyLegalMoveIncrementsTheCounter() {
        final FifteenBoard board = new FifteenBoardImpl(MIXED);
        board.move(Direction.UP);
        assertEquals(1, board.getMoveCount());
        board.moveTile(EMPTY_ROW, EMPTY_COLUMN);
        assertEquals(2, board.getMoveCount());
    }

    @Test
    void illegalMovesLeaveTheCounterUnchanged() {
        final FifteenBoard board = new FifteenBoardImpl();
        board.move(Direction.UP);
        board.moveTile(0, 0);
        assertEquals(0, board.getMoveCount());
    }

    private static void assertDirectionApplied(final Direction direction, final int[][] expected) {
        final FifteenBoard board = new FifteenBoardImpl(MIXED);
        assertTrue(board.move(direction));
        assertArrayEquals(expected, board.getTiles());
    }

    private static void assertDirectionRefused(final int[][] layout, final Direction direction) {
        final FifteenBoard board = new FifteenBoardImpl(layout);
        assertFalse(board.move(direction));
        assertArrayEquals(layout, board.getTiles());
    }

    private static void assertTileApplied(final int row, final int column, final int[][] expected) {
        final FifteenBoard board = new FifteenBoardImpl(MIXED);
        assertTrue(board.moveTile(row, column));
        assertArrayEquals(expected, board.getTiles());
    }

    private static void assertTileRefused(final int row, final int column) {
        final FifteenBoard board = new FifteenBoardImpl(MIXED);
        assertFalse(board.moveTile(row, column));
        assertArrayEquals(MIXED, board.getTiles());
    }
}
