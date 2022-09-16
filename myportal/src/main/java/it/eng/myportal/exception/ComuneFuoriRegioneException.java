package it.eng.myportal.exception;

import it.eng.myportal.utils.ConstantsSingleton;

public class ComuneFuoriRegioneException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6482397611511358646L;

	private String errorMessageKey;

	public ComuneFuoriRegioneException() {
		super();
		errorMessageKey = new StringBuilder("regforte.domiciliofuoriregione").append(".")
				.append(ConstantsSingleton.COD_REGIONE).toString();
	}

	public String getErrorMessageKey() {
		return errorMessageKey;
	}

}
