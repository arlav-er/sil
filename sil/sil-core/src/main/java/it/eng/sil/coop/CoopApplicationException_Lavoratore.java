/*
 * Created on 06-Apr-06
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package it.eng.sil.coop;

import it.eng.sil.coop.utils.MessageBundle;

/**
 * @author rolfini
 *
 *         To change the template for this generated type comment go to Window&gt;Preferences&gt;Java&gt;Code
 *         Generation&gt;Code and Comments
 */
public class CoopApplicationException_Lavoratore extends Exception {

	private String codiceFiscale = null;

	/**
	 * 
	 */
	public CoopApplicationException_Lavoratore() {
		super();
	}

	public CoopApplicationException_Lavoratore(String messaggio, String cf) {
		super(messaggio);
		this.codiceFiscale = cf;
	}

	public CoopApplicationException_Lavoratore(int msgCode, String cf) {
		super(MessageBundle.getMessage(msgCode, null));
		this.codiceFiscale = cf;
	}

	public CoopApplicationException_Lavoratore(int msgCode, java.util.Vector params, String cf) {
		super(MessageBundle.getMessage(msgCode, params));
		this.codiceFiscale = cf;
	}

	public void setCodiceFiscale(String cf) {
		this.codiceFiscale = cf;
	}

	public String getCodiceFiscale() {
		return this.codiceFiscale;
	}

}
