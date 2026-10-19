package it.unibo.gridgames.model.score;

//GameType = elenco dei giochi. File enum

/**
 * Represents the available grid games in the application.
 */

public enum GameType {

    GAME_2048("2048"),
    SUDOKU("Sudoku"),
    PUZZLE_15("Gioco del 15");

    private final String name;

    /**
     * Constructs a GameType enum with a display name.
     * 
     * @param name the display name of the game type
     */
    GameType(final String name) {
        this.name = name;
    }

    /**
     * Returns the display name of the game type.
     * 
     * @return the display name of the game type
     */
    public String getDisplayName() {
        return this.name;
    }
}
