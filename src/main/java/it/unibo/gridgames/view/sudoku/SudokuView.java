package it.unibo.gridgames.view.sudoku;

// Consumer: un "contenitore" per un'azione che riceve un valore (qui: il numero cliccato)
import java.util.function.Consumer;

import it.unibo.gridgames.model.sudoku.Grid;
import it.unibo.gridgames.view.UIConstants;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;

/**
 * Screen where the player plays a Sudoku game.
 */
// La schermata è un BorderPane: zone in alto (top), al centro (center) e in basso (bottom)
public final class SudokuView extends BorderPane {

    // pezzo di stile aggiunto alla cella scelta: bordo arancione più spesso
    // (in CSS, se una proprietà è scritta due volte, vale l'ultima)
    private static final String SELECTED = " -fx-border-color: darkorange; -fx-border-width: 2;";

    // bottoni
    private final Button btnBack = new Button("Indietro");
    // le 81 celle della griglia, salvate in una tabella per poterle cambiare dopo
    private final Button[][] cells = new Button[Grid.SIZE][Grid.SIZE];
    // i 9 bottoni dei numeri in basso (posizione 0 = numero 1, ..., posizione 8 = numero 9)
    private final Button[] numberButtons = new Button[Grid.SIZE];

    // la cella scelta dal giocatore (-1 = nessuna cella scelta)
    private int selectedRow = -1;
    private int selectedCol = -1;

    // scritte
    private final Label errorsLabel = new Label("Errori: 0/3");

    /**
     * Creates the screen with the 9x9 grid and the numbers from 1 to 9.
     */
    public SudokuView() {
        this.setStyle("-fx-background-color: #f7f9fa;"); // sfondo, come la classifica di Chiara

        // ----- IN ALTO: bottone indietro e numero di errori -----
        btnBack.setStyle("-fx-background-color: darkorange; -fx-text-fill: white; -fx-font-weight: bold;");
        errorsLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #333333;");
        // HBox: mette gli elementi uno accanto all'altro, con 10 di spazio tra loro
        final HBox topBox = new HBox(UIConstants.GRID_SIZE.getValue());
        topBox.setAlignment(Pos.CENTER_LEFT); // tutto a sinistra
        topBox.getChildren().add(btnBack);
        topBox.getChildren().add(errorsLabel);
        BorderPane.setMargin(topBox, new Insets(UIConstants.GRID_SIZE.getValue())); // margine intorno
        this.setTop(topBox); // la scatola va nella zona in alto

        // ----- AL CENTRO: la griglia 9x9 -----
        // GridPane: una tabella che mette gli elementi in righe e colonne
        final GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        // primo for: le righe (da 0 a 8), secondo for: le colonne (da 0 a 8)
        for (int row = 0; row < Grid.SIZE; row++) {
            for (int col = 0; col < Grid.SIZE; col++) {
                // ogni cella è un bottone quadrato, bianco con il bordo grigio
                final Button cell = new Button();
                cell.setPrefSize(UIConstants.CELL_SIZE.getValue(), UIConstants.CELL_SIZE.getValue());
                cell.setStyle("-fx-background-color: white; -fx-border-color: #cccccc; -fx-background-radius: 0;");
                // copie "final" di row e col: dentro una lambda (e -> ...) Java accetta
                // solo variabili che non cambiano, mentre row e col cambiano a ogni giro
                final int r = row;
                final int c = col;
                // quando il giocatore clicca la cella, questa diventa la cella scelta
                cell.setOnAction(e -> selectCell(r, c));
                cells[row][col] = cell; // la salviamo nella tabella
                grid.add(cell, col, row); // attenzione: in GridPane prima la colonna, poi la riga
            }
        }
        this.setCenter(grid); // la griglia va nella zona centrale

        // ----- IN BASSO: i numeri da 1 a 9 -----
        final HBox numbersBox = new HBox(UIConstants.GRID_SIZE.getValue());
        numbersBox.setAlignment(Pos.CENTER);
        for (int i = 1; i <= Grid.SIZE; i++) {
            // il testo del bottone è il numero (String.valueOf trasforma 5 in "5")
            final Button number = new Button(String.valueOf(i));
            number.setPrefSize(UIConstants.CELL_SIZE.getValue(), UIConstants.CELL_SIZE.getValue());
            number.setStyle("-fx-background-color: #e67e22; -fx-text-fill: white; -fx-background-radius: 8;");
            numbersBox.getChildren().add(number);
            // i va da 1 a 9, l'array da 0 a 8: per questo i - 1
            numberButtons[i - 1] = number;
        }
        BorderPane.setMargin(numbersBox, new Insets(UIConstants.GRID_SIZE.getValue()));
        this.setBottom(numbersBox); // i numeri vanno nella zona in basso
    }

    /**
     * Sets what happens when the player wants to go back.
     *
     * @param handler the action to run
     */
    // il controller decide cosa fa il bottone "Indietro"
    public void setOnBackSelected(final Runnable handler) {
        this.btnBack.setOnAction(e -> handler.run());
    }

    /**
     * Sets what happens when the player clicks a number from 1 to 9.
     *
     * @param handler receives the clicked number
     */
    // il controller decide cosa succede quando si clicca un numero:
    // ogni bottone passa al controller il proprio numero
    public void setOnNumberSelected(final Consumer<Integer> handler) {
        for (int i = 0; i < Grid.SIZE; i++) {
            final int value = i + 1; // posizione 0 = numero 1, ecc.
            numberButtons[i].setOnAction(e -> handler.accept(value));
        }
    }

    /**
     * Returns the row of the selected cell.
     *
     * @return the row, or -1 if no cell is selected
     */
    // il controller lo usa per sapere in quale riga scrivere il numero
    public int getSelectedRow() {
        return selectedRow;
    }

    /**
     * Returns the column of the selected cell.
     *
     * @return the column, or -1 if no cell is selected
     */
    // il controller lo usa per sapere in quale colonna scrivere il numero
    public int getSelectedCol() {
        return selectedCol;
    }

    /**
     * Shows a number in a cell of the grid.
     *
     * @param row   the row of the cell
     * @param col   the column of the cell
     * @param value the number to show, or 0 for an empty cell
     * @param fixed true if the number was given at the start of the game
     */
    // il controller lo usa per scrivere un numero in una cella
    public void showCell(final int row, final int col, final int value, final boolean fixed) {
        final Button cell = cells[row][col];

        // il numero: niente se la cella è vuota (valore 0)
        if (value == 0) {
            cell.setText("");
        } else {
            cell.setText(String.valueOf(value));
        }

        // il colore: marrone per i numeri fissi, arancione per quelli del giocatore
        if (fixed) {
            cell.setStyle("-fx-background-color: white; -fx-text-fill: rgb(122, 60, 7); -fx-font-weight: bold;"
                    + " -fx-border-color: #cccccc; -fx-background-radius: 0;");
        } else {
            cell.setStyle("-fx-background-color: white; -fx-text-fill: #e67e22;"
                    + " -fx-border-color: #cccccc; -fx-background-radius: 0;");
        }
    }

    /**
     * Shows the number of errors made by the player.
     *
     * @param errors the number of errors
     */
    // il controller lo usa dopo ogni errore, e con 0 a inizio partita
    public void showErrors(final int errors) {
        errorsLabel.setText("Errori: " + errors + "/3");
    }

    // chiamato quando il giocatore clicca una cella: privato, lo usa solo la vista
    private void selectCell(final int row, final int col) {
        // togli il bordo arancione dalla cella scelta prima (se ce n'era una)
        if (selectedRow >= 0) {
            final Button old = cells[selectedRow][selectedCol];
            old.setStyle(old.getStyle().replace(SELECTED, ""));
        }
        // ricorda la nuova cella e mettile il bordo arancione
        selectedRow = row;
        selectedCol = col;
        cells[row][col].setStyle(cells[row][col].getStyle() + SELECTED);
    }
}
