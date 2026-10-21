package it.unibo.gridgames;

import it.unibo.gridgames.controller.leaderboard.MenuController;
import it.unibo.gridgames.view.menu.LeaderboardView;
import it.unibo.gridgames.view.menu.MainAppView;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * The main application class for the GridGames project.
 */
public final class MainApp extends Application {

    /**
     * Button handlers are set up to print messages to the console when the corresponding buttons are clicked. 
     */
    @Override
    public void start(final Stage stage) {
        final MainAppView view = new MainAppView();
        final Scene scene = new Scene(view, 500, 400);
        /*view.setOn2048Selected(() -> System.out.println("Avvio 2048... (Jacopo)"));
        view.setOnSudokuSelected(() -> System.out.println("Avvio Sudoku... (Audray)"));
        view.setOnGiocoDel15Selected(() -> System.out.println("Avvio Gioco del 15... (Martina)"));
        view.setOnLeaderboardSelected(() -> {
            System.out.println("Apertura Classifics & Statistiche (Chiara)");
        });*/

        new MenuController(view, scene);
        
        stage.setTitle("GridGames");
        stage.setScene(scene);
        stage.show();
    }

    /**
     * Application entry point for JavaFX runtime.
     *
     * @param args command-line arguments
     */
    public static void main(final String[] args) {
        launch(args);
    }
}
