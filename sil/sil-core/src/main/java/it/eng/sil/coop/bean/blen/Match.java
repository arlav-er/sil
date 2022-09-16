/**
 * Match.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package it.eng.sil.coop.bean.blen;

public class Match implements java.io.Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 113812276555567086L;

	private java.lang.String idOperatore;

	private java.lang.String codDomanda;

	private java.lang.String domanda;

	private CandidaturaEstesa[] candidature;

	private Match match;

	public Match() {
	}

	public Match(java.lang.String idOperatore, java.lang.String codDomanda, java.lang.String domanda,
			CandidaturaEstesa[] candidature) {
		this.idOperatore = idOperatore;
		this.codDomanda = codDomanda;
		this.domanda = domanda;
		this.candidature = candidature;
	}

	/**
	 * Gets the idOperatore value for this Match.
	 * 
	 * @return idOperatore
	 */
	public java.lang.String getIdOperatore() {
		return idOperatore;
	}

	/**
	 * Sets the idOperatore value for this Match.
	 * 
	 * @param idOperatore
	 */
	public void setIdOperatore(java.lang.String idOperatore) {
		this.idOperatore = idOperatore;
	}

	/**
	 * Gets the codDomanda value for this Match.
	 * 
	 * @return codDomanda
	 */
	public java.lang.String getCodDomanda() {
		return codDomanda;
	}

	/**
	 * Sets the codDomanda value for this Match.
	 * 
	 * @param codDomanda
	 */
	public void setCodDomanda(java.lang.String codDomanda) {
		this.codDomanda = codDomanda;
	}

	/**
	 * Gets the domanda value for this Match.
	 * 
	 * @return domanda
	 */
	public java.lang.String getDomanda() {
		return domanda;
	}

	/**
	 * Sets the domanda value for this Match.
	 * 
	 * @param domanda
	 */
	public void setDomanda(java.lang.String domanda) {
		this.domanda = domanda;
	}

	/**
	 * Gets the candidature value for this Match.
	 * 
	 * @return candidature
	 */
	public CandidaturaEstesa[] getCandidature() {
		return candidature;
	}

	/**
	 * Sets the candidature value for this Match.
	 * 
	 * @param candidature
	 */
	public void setCandidature(CandidaturaEstesa[] candidature) {
		this.candidature = candidature;
	}
}
