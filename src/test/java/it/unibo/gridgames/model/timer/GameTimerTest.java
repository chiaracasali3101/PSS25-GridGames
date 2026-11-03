package it.unibo.gridgames.model.timer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTimeoutPreemptively;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.LongSupplier;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class GameTimerTest {

    private static final Duration STEP = Duration.ofSeconds(5);
    private static final Duration OTHER_STEP = Duration.ofSeconds(3);
    private static final Duration LONG_PERIOD = Duration.ofHours(1);
    private static final Duration TICK_PERIOD = Duration.ofMillis(5);
    private static final Duration TIMEOUT = Duration.ofSeconds(30);
    private static final int WORKERS = 8;
    private static final int OPERATIONS = 500;

    private FakeClock clock;
    private GameTimer timer;

    @BeforeEach
    void createTimer() {
        this.clock = new FakeClock();
        this.timer = new GameTimerImpl(this.clock, LONG_PERIOD);
    }

    // Every test also proves that stopping the timer leaves no thread behind.
    @AfterEach
    void stopTimer() throws InterruptedException {
        this.timer.stop();
        assertNoTickThreadLeft();
    }

    @Test
    void invalidArgumentsAreRejected() {
        assertThrows(IllegalArgumentException.class, () -> new GameTimerImpl(null, LONG_PERIOD));
        assertThrows(IllegalArgumentException.class, () -> new GameTimerImpl(this.clock, null));
        assertThrows(IllegalArgumentException.class, () -> new GameTimerImpl(this.clock, Duration.ZERO));
        assertThrows(IllegalArgumentException.class, () -> new GameTimerImpl(this.clock, LONG_PERIOD.negated()));
        assertThrows(IllegalArgumentException.class, () -> this.timer.setOnTick(null));
    }

    @Test
    void newTimerIsStoppedAtZero() {
        assertFalse(this.timer.isRunning());
        this.clock.advance(STEP);
        assertEquals(Duration.ZERO, this.timer.getElapsed());
    }

    @Test
    void defaultTimerMeasuresRealTime() {
        final GameTimer realTimer = new GameTimerImpl();
        realTimer.start();
        assertTrue(realTimer.isRunning());
        assertTimeoutPreemptively(TIMEOUT, () -> {
            while (realTimer.getElapsed().isZero()) {
                Thread.onSpinWait();
            }
        });
        realTimer.stop();
        assertFalse(realTimer.isRunning());
    }

    @Test
    void elapsedTimeGrowsWhileRunning() {
        this.timer.start();
        assertTrue(this.timer.isRunning());
        this.clock.advance(STEP);
        assertEquals(STEP, this.timer.getElapsed());
        this.clock.advance(OTHER_STEP);
        assertEquals(STEP.plus(OTHER_STEP), this.timer.getElapsed());
    }

    @Test
    void pauseFreezesTheElapsedTime() {
        this.timer.start();
        this.clock.advance(STEP);
        this.timer.pause();
        this.clock.advance(OTHER_STEP);
        assertFalse(this.timer.isRunning());
        assertEquals(STEP, this.timer.getElapsed());
    }

    @Test
    void resumeSkipsThePausedTime() {
        this.timer.start();
        this.clock.advance(STEP);
        this.timer.pause();
        this.clock.advance(LONG_PERIOD);
        this.timer.resume();
        this.clock.advance(OTHER_STEP);
        assertTrue(this.timer.isRunning());
        assertEquals(STEP.plus(OTHER_STEP), this.timer.getElapsed());
    }

    @Test
    void severalPausesAddUp() {
        this.timer.start();
        this.clock.advance(STEP);
        this.timer.pause();
        this.timer.resume();
        this.clock.advance(STEP);
        this.timer.pause();
        this.timer.resume();
        this.clock.advance(OTHER_STEP);
        assertEquals(STEP.plus(STEP).plus(OTHER_STEP), this.timer.getElapsed());
    }

    @Test
    void stopKeepsTheFinalTime() {
        this.timer.start();
        this.clock.advance(STEP);
        this.timer.stop();
        this.clock.advance(OTHER_STEP);
        assertFalse(this.timer.isRunning());
        assertEquals(STEP, this.timer.getElapsed());
    }

    @Test
    void stopWhilePausedKeepsThePausedTime() {
        this.timer.start();
        this.clock.advance(STEP);
        this.timer.pause();
        this.clock.advance(OTHER_STEP);
        this.timer.stop();
        assertEquals(STEP, this.timer.getElapsed());
    }

    @Test
    void startAfterStopCountsFromZero() {
        this.timer.start();
        this.clock.advance(STEP);
        this.timer.stop();
        this.timer.start();
        this.clock.advance(OTHER_STEP);
        assertEquals(OTHER_STEP, this.timer.getElapsed());
    }

    @Test
    void startWhileRunningIsIgnored() {
        this.timer.start();
        this.clock.advance(STEP);
        this.timer.start();
        this.clock.advance(OTHER_STEP);
        assertEquals(STEP.plus(OTHER_STEP), this.timer.getElapsed());
        assertEquals(1, tickThreads().size());
    }

    @Test
    void startWhilePausedIsIgnored() {
        this.timer.start();
        this.clock.advance(STEP);
        this.timer.pause();
        this.timer.start();
        this.clock.advance(OTHER_STEP);
        assertFalse(this.timer.isRunning());
        assertEquals(STEP, this.timer.getElapsed());
    }

    @Test
    void pauseWithoutARunningTimerIsIgnored() {
        this.timer.pause();
        assertFalse(this.timer.isRunning());
        assertEquals(Duration.ZERO, this.timer.getElapsed());
    }

    @Test
    void resumeWithoutAPauseIsIgnored() {
        this.timer.resume();
        assertFalse(this.timer.isRunning());
        this.timer.start();
        this.clock.advance(STEP);
        this.timer.resume();
        this.clock.advance(OTHER_STEP);
        assertEquals(STEP.plus(OTHER_STEP), this.timer.getElapsed());
        assertEquals(1, tickThreads().size());
    }

    @Test
    void resumeAfterStopIsIgnored() {
        this.timer.start();
        this.clock.advance(STEP);
        this.timer.stop();
        this.timer.resume();
        this.clock.advance(OTHER_STEP);
        assertFalse(this.timer.isRunning());
        assertEquals(STEP, this.timer.getElapsed());
    }

    @Test
    void resetWhileRunningStopsAtZero() {
        this.timer.start();
        this.clock.advance(STEP);
        this.timer.reset();
        this.clock.advance(OTHER_STEP);
        assertFalse(this.timer.isRunning());
        assertEquals(Duration.ZERO, this.timer.getElapsed());
    }

    @Test
    void resetWhilePausedStopsAtZero() {
        this.timer.start();
        this.clock.advance(STEP);
        this.timer.pause();
        this.timer.reset();
        this.timer.resume();
        assertFalse(this.timer.isRunning());
        assertEquals(Duration.ZERO, this.timer.getElapsed());
    }

    @Test
    void ticksCarryTheElapsedTime() {
        final BlockingQueue<Duration> ticks = new LinkedBlockingQueue<>();
        this.timer = new GameTimerImpl(this.clock, TICK_PERIOD);
        this.timer.setOnTick(ticks::add);
        this.timer.start();
        this.clock.advance(STEP);
        assertTimeoutPreemptively(TIMEOUT, () -> {
            while (!STEP.equals(ticks.take())) {
                Thread.onSpinWait();
            }
        });
    }

    @Test
    void ticksStopDuringAPauseAndRestartOnResume() throws InterruptedException {
        final Semaphore ticks = startTickingTimer();
        assertTick(ticks);
        this.timer.pause();
        assertNoTickThreadLeft();
        ticks.drainPermits();
        this.timer.resume();
        assertTick(ticks);
    }

    @Test
    void stopEndsTheTickThread() throws InterruptedException {
        assertTick(startTickingTimer());
        this.timer.stop();
        assertNoTickThreadLeft();
    }

    @Test
    void resetEndsTheTickThread() throws InterruptedException {
        assertTick(startTickingTimer());
        this.timer.reset();
        assertNoTickThreadLeft();
    }

    @Test
    void tickThreadDoesNotKeepTheApplicationAlive() {
        this.timer.start();
        final List<Thread> threads = tickThreads();
        assertEquals(1, threads.size());
        assertTrue(threads.getFirst().isDaemon());
    }

    @Test
    void timerCanBeStoppedFromItsOwnTick() throws InterruptedException {
        final Semaphore stopped = new Semaphore(0);
        this.timer = new GameTimerImpl(this.clock, TICK_PERIOD);
        this.timer.setOnTick(elapsed -> {
            this.timer.stop();
            stopped.release();
        });
        this.timer.start();
        assertTick(stopped);
        assertFalse(this.timer.isRunning());
    }

    @Test
    void concurrentCallsLeaveTheTimerConsistent() {
        final GameTimer shared = new GameTimerImpl(System::nanoTime, TICK_PERIOD);
        this.timer = shared;
        final List<Runnable> actions = List.of(
            shared::start,
            shared::pause,
            shared::resume,
            shared::stop,
            shared::reset,
            () -> assertFalse(shared.getElapsed().isNegative())
        );
        assertTimeoutPreemptively(TIMEOUT, () -> {
            try (ExecutorService executor = Executors.newFixedThreadPool(WORKERS)) {
                final List<Future<?>> results = new ArrayList<>();
                for (int worker = 0; worker < WORKERS; worker++) {
                    final int offset = worker;
                    results.add(executor.submit(() -> runOperations(actions, offset)));
                }
                for (final Future<?> result : results) {
                    result.get();
                }
            }
        });
        shared.stop();
        assertFalse(shared.isRunning());
    }

    private static void runOperations(final List<Runnable> actions, final int offset) {
        for (int operation = 0; operation < OPERATIONS; operation++) {
            actions.get((operation + offset) % actions.size()).run();
        }
    }

    private Semaphore startTickingTimer() {
        final Semaphore ticks = new Semaphore(0);
        this.timer = new GameTimerImpl(this.clock, TICK_PERIOD);
        this.timer.setOnTick(elapsed -> ticks.release());
        this.timer.start();
        return ticks;
    }

    private static void assertTick(final Semaphore ticks) throws InterruptedException {
        assertTrue(ticks.tryAcquire(TIMEOUT.toSeconds(), TimeUnit.SECONDS));
    }

    private static void assertNoTickThreadLeft() throws InterruptedException {
        for (final Thread thread : tickThreads()) {
            thread.join(TIMEOUT.toMillis());
            assertFalse(thread.isAlive());
        }
    }

    private static List<Thread> tickThreads() {
        return Thread.getAllStackTraces().keySet().stream()
            .filter(thread -> GameTimerImpl.TICK_THREAD_NAME.equals(thread.getName()))
            .toList();
    }

    private static final class FakeClock implements LongSupplier {

        // Not zero, so that the tests fail if the timer forgets to subtract the start time.
        private static final long START = 1_000L;

        private final AtomicLong nanos = new AtomicLong(START);

        void advance(final Duration time) {
            this.nanos.addAndGet(time.toNanos());
        }

        @Override
        public long getAsLong() {
            return this.nanos.get();
        }
    }
}
