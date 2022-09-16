/*
 * Creato il 13-Apr-04
 * Author: rolfini
 * 
 */
package it.eng.sil.coop.webservices.schedaLavoratore;

import java.io.Serializable;

/**
 * @author rolfini
 * 
 */
public class DatiPersonaliBean implements Serializable {

	private String codiceFiscale = "";
	private String nome = "";
	private String cognome = "";
	private String dataNascita = "";
	private String sesso = "";
	private String desComuneNascita = "";
	private String desCittadinanza1 = "";
	private String desCittadinanza2 = "";
	private String militeEsenteAssolto = "";

	private String IndirizzoResidenza = "";
	private String localitaResidenza = "";
	private String desComuneResidenza = "";
	private String capResidenza = "";

	private String IndirizzoDomicilio = "";
	private String localitaDomicilio = "";
	private String desComuneDomicilio = "";
	private String capDomicilio = "";
	private String cpi297 = "";

	/**
	 * 
	 */
	public DatiPersonaliBean() {
		super();
	}

	public DatiPersonaliBean(String codiceFiscale, String nome, String cognome, String sesso, String dataNascita,
			String desComuneNascita, String desCittadinanza1, String desCittadinanza2, String militeEsenteAssolto,

			String IndirizzoResidenza, String localitaResidenza, String desComuneResidenza, String capResidenza,

			String IndirizzoDomicilio, String localitaDomicilio, String desComuneDomicilio, String capDomicilio,
			String cpi297) {
		super();
		this.codiceFiscale = codiceFiscale;
		this.nome = nome;
		this.cognome = cognome;
		this.dataNascita = dataNascita;
		this.sesso = sesso;
		this.desComuneNascita = desComuneNascita;
		this.desCittadinanza1 = desCittadinanza1;
		this.desCittadinanza2 = desCittadinanza2;
		this.militeEsenteAssolto = militeEsenteAssolto;

		this.IndirizzoResidenza = IndirizzoResidenza;
		this.localitaResidenza = localitaResidenza;
		this.desComuneResidenza = desComuneResidenza;
		this.capResidenza = capResidenza;

		this.IndirizzoDomicilio = IndirizzoDomicilio;
		this.localitaDomicilio = localitaDomicilio;
		this.desComuneDomicilio = desComuneDomicilio;
		this.capDomicilio = capDomicilio;
		this.cpi297 = cpi297;

	}

	/**
	 * @return
	 */
	public String getCapDomicilio() {
		return capDomicilio;
	}

	/**
	 * @return
	 */
	public String getCapResidenza() {
		return capResidenza;
	}

	/**
	 * @return
	 */
	public String getCodiceFiscale() {
		return codiceFiscale;
	}

	/**
	 * @return
	 */
	public String getCognome() {
		return cognome;
	}

	/**
	 * @return
	 */
	public String getCpi297() {
		return cpi297;
	}

	/**
	 * @return
	 */
	public String getDataNascita() {
		return dataNascita;
	}

	/**
	 * @return
	 */
	public String getDesCittadinanza1() {
		return desCittadinanza1;
	}

	/**
	 * @return
	 */
	public String getDesCittadinanza2() {
		return desCittadinanza2;
	}

	/**
	 * @return
	 */
	public String getDesComuneDomicilio() {
		return desComuneDomicilio;
	}

	/**
	 * @return
	 */
	public String getDesComuneNascita() {
		return desComuneNascita;
	}

	/**
	 * @return
	 */
	public String getDesComuneResidenza() {
		return desComuneResidenza;
	}

	/**
	 * @return
	 */
	public String getIndirizzoDomicilio() {
		return IndirizzoDomicilio;
	}

	/**
	 * @return
	 */
	public String getIndirizzoResidenza() {
		return IndirizzoResidenza;
	}

	/**
	 * @return
	 */
	public String getLocalitaDomicilio() {
		return localitaDomicilio;
	}

	/**
	 * @return
	 */
	public String getLocalitaResidenza() {
		return localitaResidenza;
	}

	/**
	 * @return
	 */
	public String getMiliteEsenteAssolto() {
		return militeEsenteAssolto;
	}

	/**
	 * @return
	 */
	public String getNome() {
		return nome;
	}

	/**
	 * @param string
	 */
	public void setCapDomicilio(String string) {
		capDomicilio = string;
	}

	/**
	 * @param string
	 */
	public void setCapResidenza(String string) {
		capResidenza = string;
	}

	/**
	 * @param string
	 */
	public void setCodiceFiscale(String string) {
		codiceFiscale = string;
	}

	/**
	 * @param string
	 */
	public void setCognome(String string) {
		cognome = string;
	}

	/**
	 * @param string
	 */
	public void setCpi297(String string) {
		cpi297 = string;
	}

	/**
	 * @param string
	 */
	public void setDataNascita(String string) {
		dataNascita = string;
	}

	/**
	 * @param string
	 */
	public void setDesCittadinanza1(String string) {
		desCittadinanza1 = string;
	}

	/**
	 * @param string
	 */
	public void setDesCittadinanza2(String string) {
		desCittadinanza2 = string;
	}

	/**
	 * @param string
	 */
	public void setDesComuneDomicilio(String string) {
		desComuneDomicilio = string;
	}

	/**
	 * @param string
	 */
	public void setDesComuneNascita(String string) {
		desComuneNascita = string;
	}

	/**
	 * @param string
	 */
	public void setDesComuneResidenza(String string) {
		desComuneResidenza = string;
	}

	/**
	 * @param string
	 */
	public void setIndirizzoDomicilio(String string) {
		IndirizzoDomicilio = string;
	}

	/**
	 * @param string
	 */
	public void setIndirizzoResidenza(String string) {
		IndirizzoResidenza = string;
	}

	/**
	 * @param string
	 */
	public void setLocalitaDomicilio(String string) {
		localitaDomicilio = string;
	}

	/**
	 * @param string
	 */
	public void setLocalitaResidenza(String string) {
		localitaResidenza = string;
	}

	/**
	 * @param string
	 */
	public void setMiliteEsenteAssolto(String string) {
		militeEsenteAssolto = string;
	}

	/**
	 * @param string
	 */
	public void setNome(String string) {
		nome = string;
	}

	/**
	 * @return
	 */
	public String getSesso() {
		return sesso;
	}

	/**
	 * @param string
	 */
	public void setSesso(String string) {
		sesso = string;
	}

}
