package it.unibo.gridgames.model.fifteen;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Test;

class FifteenBoardTest {

    private static final int[][] SOLVED = {
        {1, 2, 3, 4},
        {5, 6, 7, 8},
        {9, 10, 11, 12},
        {13, 14, 15, 0},
    };
    private static final int[][] MIXED = {
        {5, 1, 2, 4},
        {9, 6, 0, 3},
        {13, 10, 7, 8},
        {14, 15, 11, 12},
    };
    private static final int[][] LAST_TILES_SWAPPED = {
        {1, 2, 3, 4},
        {5, 6, 7, 8},
        {9, 10, 11, 12},
        {13, 15, 14, 0},
    };
    private static final int[][] EMPTY_CELL_FIRST = {
        {0, 1, 2, 3},
        {4, 5, 6, 7},
        {8, 9, 10, 11},
        {12, 13, 14, 15},
    };
    private static final int[][] EMPTY_CELL_ONE_ROW_UP = {
        {1, 2, 3, 4},
        {5, 6, 7, 8},
        {9, 10, 11, 0},
        {13, 14, 15, 12},
    };
    private static final int[][] EMPTY_CELL_ONE_ROW_UP_WITH_SWAP = {
        {2, 1, 3, 4},
        {5, 6, 7, 8},
        {9, 10, 11, 0},
        {13, 14, 15, 12},
    };
    private static final int[][] NULL_ROW = {
        {1, 2, 3, 4},
        null,
        {9, 10, 11, 12},
        {13, 14, 15, 0},
    };
    private static final int[][] MISSING_ROW = {
        {1, 2, 3, 4},
        {5, 6, 7, 8},
        {9, 10, 11, 12},
    };
    private static final int[][] SHORT_ROW = {
        {1, 2, 3, 4},
        {5, 6, 7},
        {8, 9, 10, 11},
        {12, 13, 14, 0},
    };
    private static final int[][] DUPLICATED_TILE = {
        {1, 1, 3, 4},
        {5, 6, 7, 8},
        {9, 10, 11, 12},
        {13, 14, 15, 0},
    };
    private static final int[][] TILE_OUT_OF_RANGE = {
        {1, 2, 3, 4},
        {5, 6, 7, 8},
        {9, 10, 11, 12},
        {13, 14, 16, 0},
    };
    private static final int[][] NO_EMPTY_CELL = {
        {1, 2, 3, 4},
        {5, 6, 7, 8},
        {9, 10, 11, 12},
        {13, 14, 15, 15},
    };
    private static final int MIXED_EMPTY_ROW = 1;
    private static final int MIXED_EMPTY_COLUMN = 2;
    private static final int LAST_INDEX = FifteenBoard.SIZE - 1;
    private static final int FIRST_TILE_OF_MIXED = 5;
    private static final int TAMPERED_VALUE = 99;

    @Test
    void newBoardIsInSolvedLayout() {
        assertArrayEquals(SOLVED, new FifteenBoardImpl().getTiles());
    }

    @Test
    void newBoardHasEmptyCellInBottomRightCorner() {
        final FifteenBoard board = new FifteenBoardImpl();
        assertEquals(LAST_INDEX, board.getEmptyRow());
        assertEquals(LAST_INDEX, board.getEmptyColumn());
    }

    @Test
    void boardBuiltFromTilesKeepsTheirLayout() {
        assertArrayEquals(MIXED, new FifteenBoardImpl(MIXED).getTiles());
    }

    @Test
    void boardBuiltFromTilesLocatesEmptyCell() {
        final FifteenBoard board = new FifteenBoardImpl(MIXED);
        assertEquals(MIXED_EMPTY_ROW, board.getEmptyRow());
        assertEquals(MIXED_EMPTY_COLUMN, board.getEmptyColumn());
    }

    @Test
    void tileIsReadFromTheRequestedCell() {
        final FifteenBoard board = new FifteenBoardImpl(MIXED);
        assertEquals(FIRST_TILE_OF_MIXED, board.getTile(0, 0));
        assertEquals(FifteenBoard.EMPTY, board.getTile(MIXED_EMPTY_ROW, MIXED_EMPTY_COLUMN));
    }

    @Test
    void readingOutsideTheBoardIsRejected() {
        final FifteenBoard board = new FifteenBoardImpl();
        assertThrows(IllegalArgumentException.class, () -> board.getTile(-1, 0));
        assertThrows(IllegalArgumentException.class, () -> board.getTile(0, -1));
        assertThrows(IllegalArgumentException.class, () -> board.getTile(FifteenBoard.SIZE, 0));
        assertThrows(IllegalArgumentException.class, () -> board.getTile(0, FifteenBoard.SIZE));
    }

    @Test
    void returnedTilesAreDefensiveCopy() {
        final FifteenBoard board = new FifteenBoardImpl();
        board.getTiles()[0][0] = TAMPERED_VALUE;
        assertArrayEquals(SOLVED, board.getTiles());
    }

    @Test
    void constructorCopiesTheGivenTiles() {
        final int[][] tiles = Arrays.stream(MIXED).map(int[]::clone).toArray(int[][]::new);
        final FifteenBoard board = new FifteenBoardImpl(tiles);
        tiles[0][0] = TAMPERED_VALUE;
        assertArrayEquals(MIXED, board.getTiles());
    }

    @Test
    void nullTilesAreRejected() {
        assertRejected(null);
    }

    @Test
    void nullRowIsRejected() {
        assertRejected(NULL_ROW);
    }

    @Test
    void wrongNumberOfRowsIsRejected() {
        assertRejected(MISSING_ROW);
    }

    @Test
    void wrongNumberOfColumnsIsRejected() {
        assertRejected(SHORT_ROW);
    }

    @Test
    void duplicatedTileIsRejected() {
        assertRejected(DUPLICATED_TILE);
    }

    @Test
    void tileOutOfRangeIsRejected() {
        assertRejected(TILE_OUT_OF_RANGE);
    }

    @Test
    void missingEmptyCellIsRejected() {
        assertRejected(NO_EMPTY_CELL);
    }

    @Test
    void newBoardCountsAsSolved() {
        assertTrue(new FifteenBoardImpl().isSolved());
    }

    @Test
    void solvedLayoutCountsAsSolved() {
        assertTrue(new FifteenBoardImpl(SOLVED).isSolved());
    }

    @Test
    void mixedLayoutDoesNotCountAsSolved() {
        assertFalse(new FifteenBoardImpl(MIXED).isSolved());
    }

    @Test
    void twoSwappedTilesDoNotCountAsSolved() {
        assertFalse(new FifteenBoardImpl(LAST_TILES_SWAPPED).isSolved());
    }

    @Test
    void orderedTilesWithEmptyCellFirstDoNotCountAsSolved() {
        assertFalse(new FifteenBoardImpl(EMPTY_CELL_FIRST).isSolved());
    }

    @Test
    void solvedLayoutIsSolvable() {
        assertTrue(new FifteenBoardImpl().isSolvable());
    }

    @Test
    void layoutOneMoveFromSolvedIsSolvable() {
        assertTrue(new FifteenBoardImpl(EMPTY_CELL_ONE_ROW_UP).isSolvable());
    }

    @Test
    void lastTilesSwappedAreNotSolvable() {
        assertFalse(new FifteenBoardImpl(LAST_TILES_SWAPPED).isSolvable());
    }

    @Test
    void swappingTwoTilesMakesLayoutNotSolvable() {
        assertFalse(new FifteenBoardImpl(EMPTY_CELL_ONE_ROW_UP_WITH_SWAP).isSolvable());
    }

    @Test
    void orderedTilesWithEmptyCellFirstAreNotSolvable() {
        assertFalse(new FifteenBoardImpl(EMPTY_CELL_FIRST).isSolvable());
    }

    @Test
    void legalMovesKeepTheBoardSolvable() {
        final FifteenBoard board = new FifteenBoardImpl();
        for (final Direction direction : List.of(Direction.DOWN, Direction.RIGHT, Direction.DOWN, Direction.UP)) {
            assertTrue(board.move(direction));
            assertTrue(board.isSolvable());
        }
    }

    private static void assertRejected(final int[][] tiles) {
        assertThrows(IllegalArgumentException.class, () -> new FifteenBoardImpl(tiles));
    }
}
