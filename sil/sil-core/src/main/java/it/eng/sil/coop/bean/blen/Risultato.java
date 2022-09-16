/**
 * Risultato.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

/**
 * Risultato.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package it.eng.sil.coop.bean.blen;

public class Risultato implements java.io.Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -2576324852757375740L;

	private java.lang.String codice;

	private java.lang.String messaggio;

	public Risultato() {
	}

	public Risultato(java.lang.String codice, java.lang.String messaggio) {
		this.codice = codice;
		this.messaggio = messaggio;
	}

	/**
	 * Gets the codice value for this Risultato.
	 * 
	 * @return codice
	 */
	public java.lang.String getCodice() {
		return codice;
	}

	/**
	 * Sets the codice value for this Risultato.
	 * 
	 * @param codice
	 */
	public void setCodice(java.lang.String codice) {
		this.codice = codice;
	}

	/**
	 * Gets the messaggio value for this Risultato.
	 * 
	 * @return messaggio
	 */
	public java.lang.String getMessaggio() {
		return messaggio;
	}

	/**
	 * Sets the messaggio value for this Risultato.
	 * 
	 * @param messaggio
	 */
	public void setMessaggio(java.lang.String messaggio) {
		this.messaggio = messaggio;
	}

	/**
	 * Gets the codcie + messaggio value for this Risultato.
	 * 
	 * @return messaggio
	 */
	public java.lang.String getCodiceEMessaggio() {
		return this.codice + " " + this.messaggio;
	}

}
