/**
 * CandidaturaEstesa.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package it.eng.sil.coop.bean.blen;

public class CandidaturaEstesa implements java.io.Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 8121109858667656439L;

	private java.lang.String candidatura;

	private java.lang.String codCV;

	private java.lang.String idCV;

	public CandidaturaEstesa() {
	}

	public CandidaturaEstesa(java.lang.String candidatura, java.lang.String codCV, java.lang.String idCV) {
		this.candidatura = candidatura;
		this.codCV = codCV;
		this.idCV = idCV;
	}

	/**
	 * Gets the candidatura value for this CandidaturaEstesa.
	 * 
	 * @return candidatura
	 */
	public java.lang.String getCandidatura() {
		return candidatura;
	}

	/**
	 * Sets the candidatura value for this CandidaturaEstesa.
	 * 
	 * @param candidatura
	 */
	public void setCandidatura(java.lang.String candidatura) {
		this.candidatura = candidatura;
	}

	/**
	 * Gets the codCV value for this CandidaturaEstesa.
	 * 
	 * @return codCV
	 */
	public java.lang.String getCodCV() {
		return codCV;
	}

	/**
	 * Sets the codCV value for this CandidaturaEstesa.
	 * 
	 * @param codCV
	 */
	public void setCodCV(java.lang.String codCV) {
		this.codCV = codCV;
	}

	/**
	 * Gets the idCV value for this CandidaturaEstesa.
	 * 
	 * @return idCV
	 */
	public java.lang.String getIdCV() {
		return idCV;
	}

	/**
	 * Sets the idCV value for this CandidaturaEstesa.
	 * 
	 * @param idCV
	 */
	public void setIdCV(java.lang.String idCV) {
		this.idCV = idCV;
	}

}
