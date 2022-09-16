/*
 * Created on 15-nov-07
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package it.eng.sil.coop.webservices.madreperla.servizi;

/**
 * @author loc_esposito
 *
 *         To change the template for this generated type comment go to Window&gt;Preferences&gt;Java&gt;Code
 *         Generation&gt;Code and Comments
 */
public class ValidazioneException extends Exception {
	private String problema;

	public ValidazioneException() {
		super();
		problema = "";
	}

	public ValidazioneException(String err) {
		super(err);
		problema = err;
	}

	public String getError() {
		return problema;
	}
}
