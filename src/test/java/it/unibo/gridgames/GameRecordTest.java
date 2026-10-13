package it.unibo.gridgames;

import org.junit.jupiter.api.Test;

public class GameRecordTest {

   @Test 
    public void testGetters() {
        //Verifica che ciascun metodo getter restituisca esattamente il dato assegnato in fase di istanziazione.
     }

    @Test 
    public void testEqualsAndHashCode() {
        //Verifica che due istanze con gli stessi valori risultino uguali con lo stesso hash, e distinte rispetto a istanze con campi differenti.
    }

    @Test 
    public void testToString() {
        //Verifica che il metodo toString restituisca una rappresentazione leggibile e coerente dei dati dell'istanza.
    }

    @Test 
    public void testNullPlayerThrowsException() {
        //Verifica che la creazione di un GameRecord con playerName null generi un'eccezione.
    }
}
