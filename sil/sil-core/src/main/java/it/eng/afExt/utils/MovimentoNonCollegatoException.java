package it.eng.afExt.utils;

/**
 * @author De Simone Giuseppe, 15/09/2004
 */
public class MovimentoNonCollegatoException extends Exception {

	private int code = 0;

	public MovimentoNonCollegatoException(int _code) {
		super();
		code = _code;
	}

	public int getMessageIdFail() {
		return code;
	}

}