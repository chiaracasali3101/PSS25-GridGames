package it.unibo.gridgames.fifteen;

public class FifteenGame {

    private final int[][] board = new int[4][4];

    public FifteenGame() {
        for (int row = 0; row < 4; row ++) {
            for (int col = 0; col < 4; col ++) {
                board[row][col] = row * 4 + col + 1;
            }
        }
        board[3][3] = 0;
    }
}