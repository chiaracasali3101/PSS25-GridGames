package it.unibo.gridgames.controller.game2048;

import it.unibo.gridgames.model.game2048.Direction;

/**
 * Receives the player's actions from the 2048 view.
 */
public interface Game2048Controller {

    /**
     * Pushes the tiles in the given direction. The view is redrawn only if the
     * move is valid, and is told when the move ends the match.
     *
     * @param direction the direction chosen by the player
     * @throws NullPointerException if direction is null
     */
    void move(Direction direction);

    /**
     * Takes back the last move, if possible, and redraws the view.
     */
    void undo();

    /**
     * Abandons the current match and starts a new one.
     */
    void newGame();

    /**
     * Saves the score of the finished match. It does nothing if the match is
     * still in progress or its score has already been saved.
     *
     * @param playerName the name of the player
     */
    void saveScore(String playerName);
}
