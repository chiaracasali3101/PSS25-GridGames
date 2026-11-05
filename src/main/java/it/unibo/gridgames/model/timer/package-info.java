/**
 * Shared session timer used by all games to measure how long a player takes
 * to solve a level.
 *
 * <p>The timer is part of the pure model: it has no dependency on any graphical
 * toolkit, so the graphical refresh (per-second tick) is left to the view or
 * controller layer.
 */
package it.unibo.gridgames.model.timer;
