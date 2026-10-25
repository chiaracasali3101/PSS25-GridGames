package it.unibo.gridgames.model.game2048;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class BoardMoverImplTest {

    private static final int HORIZONTAL_POINTS = 20;
    private static final int VERTICAL_POINTS = 4;

    private final BoardMover mover = new BoardMoverImpl();

    private static Board startingBoard() {
        return new BoardImpl(new int[][] {
            {2, 0, 2, 4},
            {0, 4, 4, 0},
            {2, 2, 2, 2},
            {0, 0, 0, 8},
        });
    }

    @Test
    void moveLeft() {
        final Board board = startingBoard();

        final MoveResult result = mover.move(board, Direction.LEFT);

        assertArrayEquals(new int[][] {
            {4, 4, 0, 0},
            {8, 0, 0, 0},
            {4, 4, 0, 0},
            {8, 0, 0, 0},
        }, board.toMatrix());
        assertTrue(result.changed());
        assertEquals(HORIZONTAL_POINTS, result.points());
    }

    @Test
    void moveRight() {
        final Board board = startingBoard();

        final MoveResult result = mover.move(board, Direction.RIGHT);

        assertArrayEquals(new int[][] {
            {0, 0, 4, 4},
            {0, 0, 0, 8},
            {0, 0, 4, 4},
            {0, 0, 0, 8},
        }, board.toMatrix());
        assertTrue(result.changed());
        assertEquals(HORIZONTAL_POINTS, result.points());
    }

    @Test
    void moveUp() {
        final Board board = startingBoard();

        final MoveResult result = mover.move(board, Direction.UP);

        assertArrayEquals(new int[][] {
            {4, 4, 2, 4},
            {0, 2, 4, 2},
            {0, 0, 2, 8},
            {0, 0, 0, 0},
        }, board.toMatrix());
        assertTrue(result.changed());
        assertEquals(VERTICAL_POINTS, result.points());
    }

    @Test
    void moveDown() {
        final Board board = startingBoard();

        final MoveResult result = mover.move(board, Direction.DOWN);

        assertArrayEquals(new int[][] {
            {0, 0, 0, 0},
            {0, 0, 2, 4},
            {0, 4, 4, 2},
            {4, 2, 2, 8},
        }, board.toMatrix());
        assertTrue(result.changed());
        assertEquals(VERTICAL_POINTS, result.points());
    }

    @Test
    void slidingWithoutMergingIsAValidMove() {
        final Board board = new BoardImpl(new int[][] {
            {0, 0, 0, 2},
            {0, 0, 0, 0},
            {0, 0, 0, 0},
            {0, 0, 0, 0},
        });

        final MoveResult result = mover.move(board, Direction.LEFT);

        assertTrue(result.changed());
        assertEquals(0, result.points());
        assertEquals(2, board.getValue(0, 0));
    }

    @Test
    void moveThatChangesNothingIsNotValid() {
        final int[][] matrix = {
            {2, 4, 0, 0},
            {4, 2, 0, 0},
            {8, 0, 0, 0},
            {0, 0, 0, 0},
        };
        final Board board = new BoardImpl(matrix);

        final MoveResult result = mover.move(board, Direction.LEFT);

        assertFalse(result.changed());
        assertEquals(0, result.points());
        assertArrayEquals(matrix, board.toMatrix());
    }

    @Test
    void blockedBoardCannotMoveInAnyDirection() {
        final Board board = new BoardImpl(new int[][] {
            {2, 4, 2, 4},
            {4, 2, 4, 2},
            {2, 4, 2, 4},
            {4, 2, 4, 2},
        });

        for (final Direction direction : Direction.values()) {
            assertFalse(mover.move(board, direction).changed(), direction::name);
        }
    }

    @Test
    void nullArgumentsAreRejected() {
        final Board board = startingBoard();

        assertThrows(NullPointerException.class, () -> mover.move(null, Direction.LEFT));
        assertThrows(NullPointerException.class, () -> mover.move(board, null));
    }
}
