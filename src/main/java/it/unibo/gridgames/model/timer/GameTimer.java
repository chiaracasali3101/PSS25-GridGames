package it.unibo.gridgames.model.timer;

import java.time.Duration;
import java.util.function.Consumer;

/**
 * A stopwatch that measures how long a game lasts.
 *
 * <p>All the methods can be called from any thread. A call that does not apply to the current
 * state of the timer is ignored.
 */
public interface GameTimer {

    /**
     * Starts measuring from zero, if the timer is stopped.
     */
    void start();

    /**
     * Suspends the measure, if the timer is running.
     */
    void pause();

    /**
     * Continues the measure, if the timer is paused.
     */
    void resume();

    /**
     * Stops the measure and keeps the time reached, ending the background activity of the timer.
     */
    void stop();

    /**
     * Stops the measure and brings the time back to zero.
     */
    void reset();

    /**
     * Returns the time measured so far, without the time spent in pause.
     *
     * @return the elapsed time
     */
    Duration getElapsed();

    /**
     * Tells whether the timer is measuring.
     *
     * @return true if the timer is running, false if it is paused or stopped
     */
    boolean isRunning();

    /**
     * Sets the action called periodically while the timer is running.
     *
     * <p>The action runs on a background thread, so a graphical update must be handed over to the
     * thread of the graphical toolkit. The action should return quickly and must not throw.
     *
     * @param listener the action, which receives the elapsed time
     * @throws IllegalArgumentException if the action is null
     */
    void setOnTick(Consumer<Duration> listener);
}
