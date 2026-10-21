package it.unibo.gridgames.controller.leaderboard;

import it.unibo.gridgames.view.menu.LeaderboardView;
import it.unibo.gridgames.view.menu.MainAppView;
import javafx.scene.Scene;

public class MenuController {
    private final MainAppView view;
    private final Scene scene; 

    /**
     * Construct Menu Controller 
     * @param view view the main app view
     * @param scene the application scene
     */
    public MenuController(MainAppView view, Scene scene) {
        this.view = view;
        this.scene = scene;

        this.view.setOnLeaderboardSelected(() -> {
            final LeaderboardView leaderboardView = new LeaderboardView();
        });
    }
}   
