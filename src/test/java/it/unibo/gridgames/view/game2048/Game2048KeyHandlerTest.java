package it.unibo.gridgames.view.game2048;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import it.unibo.gridgames.model.game2048.Direction;
import javafx.application.Platform;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

/**
 * Tests for {@link Game2048KeyHandler}.
 */
class Game2048KeyHandlerTest {

    private static boolean javaFxAvailable;

    private final List<Direction> received = new ArrayList<>();
    private final Game2048KeyHandler handler = new Game2048KeyHandler(this.received::add);

    @BeforeAll
    static void startJavaFx() {
        try {
            Platform.startup(() -> { });
            javaFxAvailable = true;
        } catch (final IllegalStateException alreadyStarted) {
            javaFxAvailable = true;
        } catch (final UnsupportedOperationException noScreen) {
            // computer senza schermo: il test sul campo di testo viene saltato
            javaFxAvailable = false;
        }
    }

    @ParameterizedTest
    @CsvSource({
        "UP, UP", "DOWN, DOWN", "LEFT, LEFT", "RIGHT, RIGHT",
        "W, UP", "S, DOWN", "A, LEFT", "D, RIGHT"
    })
    void movementKeyNotifiesItsDirectionAndIsConsumed(final KeyCode code, final Direction expected) {
        final KeyEvent event = pressed(code);
        this.handler.handle(event);
        assertEquals(List.of(expected), this.received);
        assertTrue(event.isConsumed());
    }

    @Test
    void otherKeysAreIgnoredAndLeftToTheOtherControls() {
        final KeyEvent event = pressed(KeyCode.ENTER);
        this.handler.handle(event);
        assertTrue(this.received.isEmpty());
        assertFalse(event.isConsumed());
    }

    @Test
    void keysTypedInATextFieldAreLeftToTheField() {
        assumeTrue(javaFxAvailable, "JavaFX cannot start on this computer");
        final KeyEvent event = new KeyEvent(null, new TextField(), KeyEvent.KEY_PRESSED, "", "", KeyCode.A,
            false, false, false, false);
        this.handler.handle(event);
        assertTrue(this.received.isEmpty());
        assertFalse(event.isConsumed());
    }

    @Test
    void nullListenerIsRejected() {
        assertThrows(NullPointerException.class, () -> new Game2048KeyHandler(null));
    }

    private static KeyEvent pressed(final KeyCode code) {
        return new KeyEvent(KeyEvent.KEY_PRESSED, "", "", code, false, false, false, false);
    }
}
