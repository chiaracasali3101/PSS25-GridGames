package it.unibo.gridgames.view.game2048;

import java.util.function.Consumer;

import it.unibo.gridgames.model.game2048.Direction;
import it.unibo.gridgames.model.game2048.GameState2048;

/**
 * What the 2048 controller can ask the view to show. It contains no JavaFX
 * type, so the controller can be tested without a window.
 */
public interface Game2048View {

    /**
     * Puts the game screen in the application window.
     */
    void show();

    /**
     * Sets what to do when the player chooses a direction.
     *
     * @param listener called with the chosen direction
     */
    void onDirectionSelected(Consumer<Direction> listener);

    /**
     * Sets what to do when the player asks to go back to the menu.
     *
     * @param listener called when the player leaves the game screen
     */
    void onBackSelected(Runnable listener);

    /**
     * Sets what to do when the player asks for a new match.
     *
     * @param listener called when the player wants to start over
     */
    void onNewGameSelected(Runnable listener);

    /**
     * Sets what to do when the player asks to take back the last move.
     *
     * @param listener called when the player wants to undo a move
     */
    void onUndoSelected(Runnable listener);

    /**
     * Sets what to do when the player asks to save the score of the finished match.
     *
     * @param listener called with the name written by the player
     */
    void onSaveSelected(Consumer<String> listener);

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
