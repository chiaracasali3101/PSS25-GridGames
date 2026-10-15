package it.unibo.gridgames.fifteen;
/**
 * This class deals with the menagementof the board and the rules of the game.
 */
public class FifteenGame {

    private final int[][] board = new int[4][4];

    private int emptyRow;
    private int emptyColumn;

    /**
     * Constructor that creates a new board with an empty botton
     */
    public FifteenGame() {
        for (int row = 0; row < 4; row++) {
            for (int col = 0; col < 4; col++) {
                board[row][col] = row * 4 + col + 1;
            }
        }
        board[3][3] = 0;

        emptyRow = 3;
        emptyColumn = 3;
    }
}