package it.unibo.gridgames.view.game2048;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;

import it.unibo.gridgames.model.game2048.Direction;
import javafx.event.EventHandler;
import javafx.scene.control.TextInputControl;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

/**
 * Turns the arrow and WASD keys into the directions of the 2048 game. It must be
 * registered as a filter, so that it runs before a focused button uses the
 * arrows to move the focus. Keys typed in a text field are left to the field.
 */
public final class Game2048KeyHandler implements EventHandler<KeyEvent> {

    private static final Map<KeyCode, Direction> DIRECTIONS = Map.of(
        KeyCode.UP, Direction.UP,
        KeyCode.DOWN, Direction.DOWN,
        KeyCode.LEFT, Direction.LEFT,
        KeyCode.RIGHT, Direction.RIGHT,
        KeyCode.W, Direction.UP,
        KeyCode.S, Direction.DOWN,
        KeyCode.A, Direction.LEFT,
        KeyCode.D, Direction.RIGHT
    );

    private final Consumer<Direction> onDirection;

    /**
     * Creates a handler that notifies the chosen directions.
     *
     * @param onDirection called with the direction of each movement key
     * @throws NullPointerException if onDirection is null
     */
    public Game2048KeyHandler(final Consumer<Direction> onDirection) {
        this.onDirection = Objects.requireNonNull(onDirection);
    }

    @Override
    public void handle(final KeyEvent event) {
        if (!(event.getTarget() instanceof TextInputControl)) {
            Optional.ofNullable(DIRECTIONS.get(event.getCode())).ifPresent(direction -> {
                event.consume();
                this.onDirection.accept(direction);
            });
        }
    }
}
