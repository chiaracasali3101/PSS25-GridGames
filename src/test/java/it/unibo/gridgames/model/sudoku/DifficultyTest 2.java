package it.unibo.gridgames.model.sudoku;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class DifficultyTest {

    @Test
    void harderLevelsRemoveMoreCells() {
        assertTrue(Difficulty.EASY.getCellsToRemove() < Difficulty.MEDIUM.getCellsToRemove());
        assertTrue(Difficulty.MEDIUM.getCellsToRemove() < Difficulty.HARD.getCellsToRemove());
    }

    @Test
    void cellsToRemoveFitInGrid() {
        for (final Difficulty difficulty : Difficulty.values()) {
            assertTrue(difficulty.getCellsToRemove() > 0);
            assertTrue(difficulty.getCellsToRemove() < Grid.SIZE * Grid.SIZE);
        }
    }
}
