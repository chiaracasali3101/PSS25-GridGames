package it.unibo.gridgames.controller.fifteen;

/**
 * Coordinates a session of the Game of Fifteen between the view, the game and the timer.
 */
@FunctionalInterface
public interface FifteenController {

    /**
     * Shows the game screen and starts a new game.
     */
    void start();
}
