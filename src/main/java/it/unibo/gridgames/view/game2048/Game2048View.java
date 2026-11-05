package it.unibo.gridgames.view.game2048;

import it.unibo.gridgames.model.game2048.GameState2048;

/**
 * What the 2048 controller can ask the view to show. It contains no JavaFX
 * type, so the controller can be tested without a window.
 */
public interface Game2048View {

    /**
     * Draws the current match.
     *
     * @param grid the cell values indexed by [row][column], where 0 means empty
     * @param score the current score
     * @param moves the number of valid moves made so far
     */
    void showGame(int[][] grid, int score, int moves);

    /**
     * Tells the player that the match is over and asks for the name to save the score.
     *
     * @param result how the match ended, either WON or GAME_OVER
     */
    void showGameEnded(GameState2048 result);

    /**
     * Tells the player that the score has been saved.
     */
    void showScoreSaved();

    /**
     * Tells the player that the score was not saved, so that saving can be tried again.
     *
     * @param message the reason of the failure
     */
    void showSaveError(String message);
}
