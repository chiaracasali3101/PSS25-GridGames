package it.unibo.gridgames.view.fifteen;

import it.unibo.gridgames.model.fifteen.Direction;
import java.time.Duration;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * The screen of the Game of Fifteen, as seen by its controller.
 *
 * <p>The view shows what it is told and reports the user input, without taking game decisions.
 * All the methods must be called on the thread of the graphical toolkit.
 */
public interface FifteenView {

    /**
     * Brings this screen to the foreground.
     */
    void show();

    /**
     * Draws the board.
     *
     * @param tiles the tiles, indexed by row and then by column
     */
    void showBoard(int[][] tiles);

    /**
     * Shows the number of moves made.
     *
     * @param moves the move count
     */
    void showMoves(int moves);

    /**
     * Shows the time spent playing.
     *
     * @param elapsed the elapsed time
     */
    void showTime(Duration elapsed);

    /**
     * Tells the user that the game is won.
     */
    void showVictory();

    /**
     * Sets the action called when the user selects a tile.
     *
     * @param listener the action, which receives the row and the column of the tile
     */
    void onTileSelected(BiConsumer<Integer, Integer> listener);

    /**
     * Sets the action called when the user asks to slide a tile towards a direction.
     *
     * @param listener the action, which receives the direction
     */
    void onDirectionSelected(Consumer<Direction> listener);

    /**
     * Sets the action called when the user asks for a new game.
     *
     * @param listener the action
     */
    void onNewGameSelected(Runnable listener);

    /**
     * Sets the action called when the user asks to leave the game.
     *
     * @param listener the action
     */
    void onBackSelected(Runnable listener);
}
