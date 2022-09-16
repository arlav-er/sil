/*
 * Creato il 4-nov-04
 */
package it.eng.sil.module.movimenti;

/**
 * Eccezione lanciata nel log degli inserimenti/validazioni di movimenti
 * <p/>
 * 
 * @author roccetti
 */
public class LogException extends Exception {

	public LogException() {
		super();
	}

	public LogException(String s) {
		super(s);
	}
}
