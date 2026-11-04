package it.unibo.gridgames.model.timer;

import java.time.Duration;
import java.util.function.Consumer;
import java.util.function.LongSupplier;

/**
 * Default implementation of {@link GameTimer}.
 *
 * <p>The elapsed time is always computed from the clock. The tick thread only notifies the
 * listener, and it exists only while the timer is running.
 */
public final class GameTimerImpl implements GameTimer {

    static final String TICK_THREAD_NAME = "game-timer-tick";

    private static final Duration DEFAULT_TICK_PERIOD = Duration.ofSeconds(1);

    private final Object lock = new Object();
    private final LongSupplier clock;
    private final Duration tickPeriod;
    private Consumer<Duration> tickListener = elapsed -> { };
    private State state = State.STOPPED;
    private long accumulatedNanos;
    private long runningSince;
    private Thread tickThread;

    /**
     * Creates a timer that uses the system clock and ticks every second.
     */
    public GameTimerImpl() {
        this(System::nanoTime, DEFAULT_TICK_PERIOD);
    }

    /**
     * Creates a timer.
     *
     * @param clock the source of the current time, in nanoseconds
     * @param tickPeriod the time between two ticks
     * @throws IllegalArgumentException if an argument is null or the period is not positive
     */
    public GameTimerImpl(final LongSupplier clock, final Duration tickPeriod) {
        if (clock == null) {
            throw new IllegalArgumentException("The clock must not be null");
        }
        if (tickPeriod == null || tickPeriod.isZero() || tickPeriod.isNegative()) {
            throw new IllegalArgumentException("The tick period must be positive");
        }
        this.clock = clock;
        this.tickPeriod = tickPeriod;
    }

    @Override
    public void start() {
        synchronized (this.lock) {
            if (this.state == State.STOPPED) {
                this.accumulatedNanos = 0;
                run();
            }
        }
    }

    @Override
    public void pause() {
        synchronized (this.lock) {
            if (this.state == State.RUNNING) {
                suspend();
                this.state = State.PAUSED;
            }
        }
    }

    @Override
    public void resume() {
        synchronized (this.lock) {
            if (this.state == State.PAUSED) {
                run();
            }
        }
    }

    @Override
    public void stop() {
        synchronized (this.lock) {
            if (this.state == State.RUNNING) {
                suspend();
            }
            this.state = State.STOPPED;
        }
    }

    @Override
    public void reset() {
        synchronized (this.lock) {
            stop();
            this.accumulatedNanos = 0;
        }
    }

    @Override
    public Duration getElapsed() {
        synchronized (this.lock) {
            return Duration.ofNanos(this.state == State.RUNNING
                ? this.accumulatedNanos + this.clock.getAsLong() - this.runningSince
                : this.accumulatedNanos);
        }
    }

    @Override
    public boolean isRunning() {
        synchronized (this.lock) {
            return this.state == State.RUNNING;
        }
    }

    @Override
    public void onTick(final Consumer<Duration> listener) {
        if (listener == null) {
            throw new IllegalArgumentException("The listener must not be null");
        }
        synchronized (this.lock) {
            this.tickListener = listener;
        }
    }

    private void run() {
        this.state = State.RUNNING;
        this.runningSince = this.clock.getAsLong();
        this.tickThread = new Thread(this::tickLoop, TICK_THREAD_NAME);
        this.tickThread.setDaemon(true);
        this.tickThread.start();
    }

    private void suspend() {
        this.accumulatedNanos += this.clock.getAsLong() - this.runningSince;
        this.tickThread.interrupt();
        this.tickThread = null;
    }

    private void tickLoop() {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                Thread.sleep(this.tickPeriod);
                notifyTick();
            } catch (final InterruptedException e) {
                // Restoring the flag is what makes the loop end.
                Thread.currentThread().interrupt();
            }
        }
    }

    private void notifyTick() {
        final Consumer<Duration> currentListener;
        final Duration elapsed;
        synchronized (this.lock) {
            // A thread woken up just before being interrupted must not notify anymore.
            if (!Thread.currentThread().equals(this.tickThread)) {
                return;
            }
            currentListener = this.tickListener;
            elapsed = getElapsed();
        }
        // Outside the lock, so that a slow listener cannot block the callers of the timer.
        currentListener.accept(elapsed);
    }

    private enum State {
        STOPPED, RUNNING, PAUSED
    }
}
