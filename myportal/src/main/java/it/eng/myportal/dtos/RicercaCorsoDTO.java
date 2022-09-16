package it.eng.myportal.dtos;

import it.eng.myportal.enums.TipoRicercaCorso;

public class RicercaCorsoDTO implements IDTO {
	/**
	 * 
	 */
	private static final long serialVersionUID = 7167532462171592387L;

	// Ricerca corsi Generica (NO Formazione regolamentata)
	private String provincia;

	private String codProvincia;

	private String formazione;

	private String codFormazione;

	private String codGruppoProfessionale;

	private String strGruppoProfessionale;

	// Ricerca corsi di Formazione regolamentata
	private String provinciaFR;

	private String codProvinciaFR;

	private String codProfessione;

	private String strProfessione;
	// Ricerca corsi RifPA
	private String codRifPA;

	// Ricerca corsi Libera
	private String ricercaLibera;

	private TipoRicercaCorso tipo = TipoRicercaCorso.RIF_PA;

	public TipoRicercaCorso getTipo() {
		return tipo;
	}

	public void setTipo(TipoRicercaCorso tipo) {
		this.tipo = tipo;
	}
	
//	public void setTipoRicercaCorso(String tipo) {
//		this.tipo = TipoRicercaCorso.valueOf(tipo);
//	}

	public String getCodRifPA() {
		return codRifPA;
	}

	public void setCodRifPA(String codRifPA) {
		this.codRifPA = codRifPA;
	}

	public String getRicercaLibera() {
		return ricercaLibera;
	}

	public void setRicercaLibera(String ricercaLibera) {
		this.ricercaLibera = ricercaLibera;
	}

	public String getProvincia() {
		return provincia;
	}

	public void setProvincia(String provincia) {
		this.provincia = provincia;
	}

	public String getCodProvincia() {
		return codProvincia;
	}

	public void setCodProvincia(String codProvincia) {
		this.codProvincia = codProvincia;
	}

	public String getFormazione() {
		return formazione;
	}

	public void setFormazione(String formazione) {
		this.formazione = formazione;
	}

	public String getCodFormazione() {
		return codFormazione;
	}

	public void setCodFormazione(String codFormazione) {
		this.codFormazione = codFormazione;
	}

	public String getCodGruppoProfessionale() {
		return codGruppoProfessionale;
	}

	public void setCodGruppoProfessionale(String codGruppoProfessionale) {
		this.codGruppoProfessionale = codGruppoProfessionale;
	}

	public String getStrGruppoProfessionale() {
		return strGruppoProfessionale;
	}

	public void setStrGruppoProfessionale(String strGruppoProfessionale) {
		this.strGruppoProfessionale = strGruppoProfessionale;
	}

	public String getCodProfessione() {
		return codProfessione;
	}

	public void setCodProfessione(String codProfessione) {
		this.codProfessione = codProfessione;
	}

	public String getStrProfessione() {
		return strProfessione;
	}

	public void setStrProfessione(String strProfessione) {
		this.strProfessione = strProfessione;
	}

	public String getProvinciaFR() {
		return provinciaFR;
	}

	public void setProvinciaFR(String provinciaFR) {
		this.provinciaFR = provinciaFR;
	}

	public String getCodProvinciaFR() {
		return codProvinciaFR;
	}

	public void setCodProvinciaFR(String codProvinciaFR) {
		this.codProvinciaFR = codProvinciaFR;
	}

}
