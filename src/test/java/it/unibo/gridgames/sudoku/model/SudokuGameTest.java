package it.unibo.gridgames.sudoku.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class SudokuGameTest {

    private static final long SEED = 42L;

    private Grid solution;
    private SudokuGame game;

    @BeforeEach
    void setUp() {
        solution = new SudokuGenerator(SEED).generateFullGrid();
        game = new SudokuGame(Difficulty.EASY, new SudokuGenerator(SEED));
    }

    @Test
    void correctValueIsWritten() {
        final Position position = findEmptyCell();
        final int value = rightValue(position);
        assertEquals(MoveResult.CORRECT, game.play(position, value));
        assertEquals(value, game.getValue(position));
        assertEquals(0, game.getErrors());
    }

    @Test
    void wrongValueCountsAnError() {
        final Position position = findEmptyCell();
        assertEquals(MoveResult.WRONG, game.play(position, wrongValue(position)));
        assertEquals(0, game.getValue(position));
        assertEquals(1, game.getErrors());
        assertEquals(GameStatus.PLAYING, game.getStatus());
    }

    @Test
    void tooManyErrorsLoseTheGame() {
        final Position position = findEmptyCell();
        for (int i = 0; i < SudokuGame.MAX_ERRORS; i++) {
            game.play(position, wrongValue(position));
        }
        assertEquals(GameStatus.LOST, game.getStatus());
        assertEquals(MoveResult.NOT_ALLOWED, game.play(position, rightValue(position)));
    }

    @Test
    void fixedCellCannotBeChanged() {
        final Position position = findFixedCell();
        assertEquals(MoveResult.NOT_ALLOWED, game.play(position, rightValue(position)));
        assertEquals(0, game.getErrors());
    }

    @Test
    void fillingAllCellsWinsTheGame() {
        for (int row = 0; row < Grid.SIZE; row++) {
            for (int col = 0; col < Grid.SIZE; col++) {
                final Position position = new Position(row, col);
                if (game.getValue(position) == 0) {
                    game.play(position, rightValue(position));
                }
            }
        }
        assertEquals(GameStatus.WON, game.getStatus());
    }

    @Test
    void valueOutOfRangeIsRejected() {
        final Position position = findEmptyCell();
        assertThrows(IllegalArgumentException.class, () -> game.play(position, 0));
    }

    private int rightValue(final Position position) {
        return solution.getCell(position).getValue();
    }

    private int wrongValue(final Position position) {
        return rightValue(position) % Grid.SIZE + 1;
    }

    private Position findEmptyCell() {
        for (int row = 0; row < Grid.SIZE; row++) {
            for (int col = 0; col < Grid.SIZE; col++) {
                final Position position = new Position(row, col);
                if (game.getValue(position) == 0) {
                    return position;
                }
            }
        }
        throw new IllegalStateException("No empty cell");
    }

    private Position findFixedCell() {
        for (int row = 0; row < Grid.SIZE; row++) {
            for (int col = 0; col < Grid.SIZE; col++) {
                final Position position = new Position(row, col);
                if (game.isFixed(position)) {
                    return position;
                }
            }
        }
        throw new IllegalStateException("No fixed cell");
    }

    @Test
    void movesCountRightAndWrongButNotIgnored() {
        final Position empty = findEmptyCell();
        game.play(empty, wrongValue(empty));
        game.play(empty, rightValue(empty));
        game.play(findFixedCell(), 1);
        assertEquals(2, game.getMoves());
    }
}
