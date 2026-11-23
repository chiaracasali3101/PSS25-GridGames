package it.unibo.gridgames.view.game2048;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import it.unibo.gridgames.model.game2048.Direction;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

/**
 * Tests for {@link Game2048KeyHandler}. They do not start JavaFX, so the case of
 * a key typed in a text field, which needs a real control, is not covered here.
 */
class Game2048KeyHandlerTest {

    private final List<Direction> received = new ArrayList<>();
    private final Game2048KeyHandler handler = new Game2048KeyHandler(this.received::add);

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
    void nullListenerIsRejected() {
        assertThrows(NullPointerException.class, () -> new Game2048KeyHandler(null));
    }

    private static KeyEvent pressed(final KeyCode code) {
        return new KeyEvent(KeyEvent.KEY_PRESSED, "", "", code, false, false, false, false);
    }
}
