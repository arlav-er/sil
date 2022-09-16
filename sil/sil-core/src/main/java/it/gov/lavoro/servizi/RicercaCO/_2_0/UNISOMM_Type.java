/**
 * UNISOMM_Type.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package it.gov.lavoro.servizi.RicercaCO._2_0;

public class UNISOMM_Type implements java.io.Serializable {
	private java.lang.String agenziaSomministrazione_CodiceFiscale;

	private java.lang.String agenziaSomministrazione_Denominazione;

	private java.lang.String agenziaSomministrazione_SedeLavoro_CAP;

	private java.lang.String agenziaSomministrazione_SedeLavoro_ComuneNazione;

	private java.lang.String agenziaSomministrazione_SedeLavoro_Indirizzo;

	private java.lang.String dittaUtilizzatrice_CodiceFiscale;

	private java.lang.String dittaUtilizzatrice_Denominazione;

	private java.lang.String dittaUtilizzatrice_PA;

	private java.lang.String dittaUtilizzatrice_SedeLavoro_CAP;

	private java.lang.String dittaUtilizzatrice_SedeLavoro_ComuneNazione;

	private java.lang.String dittaUtilizzatrice_SedeLavoro_Indirizzo;

	private java.lang.String dittaUtilizzatrice_Settore;

	private java.lang.String invio_CodiceComunicazione;

	private java.lang.String invio_CodiceComunicazionePrecedente;

	private java.lang.String lavoratore_Cittadinanza;

	private java.lang.String lavoratore_CodiceFiscale;

	private java.lang.String lavoratore_Cognome;

	private java.lang.String lavoratore_Nascita_ComuneNazione;

	private java.lang.String lavoratore_Nascita_Data;

	private java.lang.String lavoratore_Nome;

	private java.lang.String lavoratore_Sesso;

	private java.lang.String rapportoAgenziaSomm_CessazioneData;

	private java.lang.String rapportoAgenziaSomm_CodiceEntePrevidenziale;

	private java.lang.String rapportoAgenziaSomm_NMatricola;

	private java.lang.String rapportoAgenziaSomm_NAgenziaSomm;

	private java.lang.String rapportoAgenziaSomm_IndennitaDisponibilita;

	private java.lang.String rapportoAgenziaSomm_DataFine;

	private java.lang.String rapportoAgenziaSomm_DataInizio;

	private java.lang.String rapportoAgenziaSomm_Proroga_DataFine;

	private java.lang.String rapportoAgenziaSomm_TipologiaContrattuale;

	private java.lang.String rapportoDittaUtilizzatrice_CCNL;

	private java.lang.String rapportoDittaUtilizzatrice_CodiceAgevolazioni;

	private java.lang.String rapportoDittaUtilizzatrice_CodiceTrasformazione;

	private java.lang.String rapportoDittaUtilizzatrice_DataCessazione;

	private java.lang.String rapportoDittaUtilizzatrice_DataFineMissione;

	private java.lang.String rapportoDittaUtilizzatrice_DataFineProroga;

	private java.lang.String rapportoDittaUtilizzatrice_DataInizioMissione;

	private java.lang.String rapportoDittaUtilizzatrice_DataTrasformazione;

	private java.lang.String rapportoDittaUtilizzatrice_LavoroInAgricoltura;

	private java.lang.String rapportoDittaUtilizzatrice_TipoOrario;

	private java.lang.String rapportoDittaUtilizzatrice_TipoOrario_OreSett;

	private java.lang.String rapportoAgenziaDittaUtilizzatrice_NContrattoSomm;

	private java.util.Calendar SYS_DATARICEZIONE;

	private java.util.Calendar SYS_DATARIFERIMENTO;

	private java.lang.String SYS_TIPOMODULO;

	private int rowOrder; // attribute

	public UNISOMM_Type() {
	}

	public UNISOMM_Type(java.lang.String agenziaSomministrazione_CodiceFiscale,
			java.lang.String agenziaSomministrazione_Denominazione,
			java.lang.String agenziaSomministrazione_SedeLavoro_CAP,
			java.lang.String agenziaSomministrazione_SedeLavoro_ComuneNazione,
			java.lang.String agenziaSomministrazione_SedeLavoro_Indirizzo,
			java.lang.String dittaUtilizzatrice_CodiceFiscale, java.lang.String dittaUtilizzatrice_Denominazione,
			java.lang.String dittaUtilizzatrice_PA, java.lang.String dittaUtilizzatrice_SedeLavoro_CAP,
			java.lang.String dittaUtilizzatrice_SedeLavoro_ComuneNazione,
			java.lang.String dittaUtilizzatrice_SedeLavoro_Indirizzo, java.lang.String dittaUtilizzatrice_Settore,
			java.lang.String invio_CodiceComunicazione, java.lang.String invio_CodiceComunicazionePrecedente,
			java.lang.String lavoratore_Cittadinanza, java.lang.String lavoratore_CodiceFiscale,
			java.lang.String lavoratore_Cognome, java.lang.String lavoratore_Nascita_ComuneNazione,
			java.lang.String lavoratore_Nascita_Data, java.lang.String lavoratore_Nome,
			java.lang.String lavoratore_Sesso, java.lang.String rapportoAgenziaSomm_CessazioneData,
			java.lang.String rapportoAgenziaSomm_CodiceEntePrevidenziale,
			java.lang.String rapportoAgenziaSomm_NMatricola, java.lang.String rapportoAgenziaSomm_NAgenziaSomm,
			java.lang.String rapportoAgenziaSomm_IndennitaDisponibilita, java.lang.String rapportoAgenziaSomm_DataFine,
			java.lang.String rapportoAgenziaSomm_DataInizio, java.lang.String rapportoAgenziaSomm_Proroga_DataFine,
			java.lang.String rapportoAgenziaSomm_TipologiaContrattuale,
			java.lang.String rapportoDittaUtilizzatrice_CCNL,
			java.lang.String rapportoDittaUtilizzatrice_CodiceAgevolazioni,
			java.lang.String rapportoDittaUtilizzatrice_CodiceTrasformazione,
			java.lang.String rapportoDittaUtilizzatrice_DataCessazione,
			java.lang.String rapportoDittaUtilizzatrice_DataFineMissione,
			java.lang.String rapportoDittaUtilizzatrice_DataFineProroga,
			java.lang.String rapportoDittaUtilizzatrice_DataInizioMissione,
			java.lang.String rapportoDittaUtilizzatrice_DataTrasformazione,
			java.lang.String rapportoDittaUtilizzatrice_LavoroInAgricoltura,
			java.lang.String rapportoDittaUtilizzatrice_TipoOrario,
			java.lang.String rapportoDittaUtilizzatrice_TipoOrario_OreSett,
			java.lang.String rapportoAgenziaDittaUtilizzatrice_NContrattoSomm, java.util.Calendar SYS_DATARICEZIONE,
			java.util.Calendar SYS_DATARIFERIMENTO, java.lang.String SYS_TIPOMODULO, int rowOrder) {
		this.agenziaSomministrazione_CodiceFiscale = agenziaSomministrazione_CodiceFiscale;
		this.agenziaSomministrazione_Denominazione = agenziaSomministrazione_Denominazione;
		this.agenziaSomministrazione_SedeLavoro_CAP = agenziaSomministrazione_SedeLavoro_CAP;
		this.agenziaSomministrazione_SedeLavoro_ComuneNazione = agenziaSomministrazione_SedeLavoro_ComuneNazione;
		this.agenziaSomministrazione_SedeLavoro_Indirizzo = agenziaSomministrazione_SedeLavoro_Indirizzo;
		this.dittaUtilizzatrice_CodiceFiscale = dittaUtilizzatrice_CodiceFiscale;
		this.dittaUtilizzatrice_Denominazione = dittaUtilizzatrice_Denominazione;
		this.dittaUtilizzatrice_PA = dittaUtilizzatrice_PA;
		this.dittaUtilizzatrice_SedeLavoro_CAP = dittaUtilizzatrice_SedeLavoro_CAP;
		this.dittaUtilizzatrice_SedeLavoro_ComuneNazione = dittaUtilizzatrice_SedeLavoro_ComuneNazione;
		this.dittaUtilizzatrice_SedeLavoro_Indirizzo = dittaUtilizzatrice_SedeLavoro_Indirizzo;
		this.dittaUtilizzatrice_Settore = dittaUtilizzatrice_Settore;
		this.invio_CodiceComunicazione = invio_CodiceComunicazione;
		this.invio_CodiceComunicazionePrecedente = invio_CodiceComunicazionePrecedente;
		this.lavoratore_Cittadinanza = lavoratore_Cittadinanza;
		this.lavoratore_CodiceFiscale = lavoratore_CodiceFiscale;
		this.lavoratore_Cognome = lavoratore_Cognome;
		this.lavoratore_Nascita_ComuneNazione = lavoratore_Nascita_ComuneNazione;
		this.lavoratore_Nascita_Data = lavoratore_Nascita_Data;
		this.lavoratore_Nome = lavoratore_Nome;
		this.lavoratore_Sesso = lavoratore_Sesso;
		this.rapportoAgenziaSomm_CessazioneData = rapportoAgenziaSomm_CessazioneData;
		this.rapportoAgenziaSomm_CodiceEntePrevidenziale = rapportoAgenziaSomm_CodiceEntePrevidenziale;
		this.rapportoAgenziaSomm_NMatricola = rapportoAgenziaSomm_NMatricola;
		this.rapportoAgenziaSomm_NAgenziaSomm = rapportoAgenziaSomm_NAgenziaSomm;
		this.rapportoAgenziaSomm_IndennitaDisponibilita = rapportoAgenziaSomm_IndennitaDisponibilita;
		this.rapportoAgenziaSomm_DataFine = rapportoAgenziaSomm_DataFine;
		this.rapportoAgenziaSomm_DataInizio = rapportoAgenziaSomm_DataInizio;
		this.rapportoAgenziaSomm_Proroga_DataFine = rapportoAgenziaSomm_Proroga_DataFine;
		this.rapportoAgenziaSomm_TipologiaContrattuale = rapportoAgenziaSomm_TipologiaContrattuale;
		this.rapportoDittaUtilizzatrice_CCNL = rapportoDittaUtilizzatrice_CCNL;
		this.rapportoDittaUtilizzatrice_CodiceAgevolazioni = rapportoDittaUtilizzatrice_CodiceAgevolazioni;
		this.rapportoDittaUtilizzatrice_CodiceTrasformazione = rapportoDittaUtilizzatrice_CodiceTrasformazione;
		this.rapportoDittaUtilizzatrice_DataCessazione = rapportoDittaUtilizzatrice_DataCessazione;
		this.rapportoDittaUtilizzatrice_DataFineMissione = rapportoDittaUtilizzatrice_DataFineMissione;
		this.rapportoDittaUtilizzatrice_DataFineProroga = rapportoDittaUtilizzatrice_DataFineProroga;
		this.rapportoDittaUtilizzatrice_DataInizioMissione = rapportoDittaUtilizzatrice_DataInizioMissione;
		this.rapportoDittaUtilizzatrice_DataTrasformazione = rapportoDittaUtilizzatrice_DataTrasformazione;
		this.rapportoDittaUtilizzatrice_LavoroInAgricoltura = rapportoDittaUtilizzatrice_LavoroInAgricoltura;
		this.rapportoDittaUtilizzatrice_TipoOrario = rapportoDittaUtilizzatrice_TipoOrario;
		this.rapportoDittaUtilizzatrice_TipoOrario_OreSett = rapportoDittaUtilizzatrice_TipoOrario_OreSett;
		this.rapportoAgenziaDittaUtilizzatrice_NContrattoSomm = rapportoAgenziaDittaUtilizzatrice_NContrattoSomm;
		this.SYS_DATARICEZIONE = SYS_DATARICEZIONE;
		this.SYS_DATARIFERIMENTO = SYS_DATARIFERIMENTO;
		this.SYS_TIPOMODULO = SYS_TIPOMODULO;
		this.rowOrder = rowOrder;
	}

	/**
	 * Gets the agenziaSomministrazione_CodiceFiscale value for this UNISOMM_Type.
	 * 
	 * @return agenziaSomministrazione_CodiceFiscale
	 */
	public java.lang.String getAgenziaSomministrazione_CodiceFiscale() {
		return agenziaSomministrazione_CodiceFiscale;
	}

	/**
	 * Sets the agenziaSomministrazione_CodiceFiscale value for this UNISOMM_Type.
	 * 
	 * @param agenziaSomministrazione_CodiceFiscale
	 */
	public void setAgenziaSomministrazione_CodiceFiscale(java.lang.String agenziaSomministrazione_CodiceFiscale) {
		this.agenziaSomministrazione_CodiceFiscale = agenziaSomministrazione_CodiceFiscale;
	}

	/**
	 * Gets the agenziaSomministrazione_Denominazione value for this UNISOMM_Type.
	 * 
	 * @return agenziaSomministrazione_Denominazione
	 */
	public java.lang.String getAgenziaSomministrazione_Denominazione() {
		return agenziaSomministrazione_Denominazione;
	}

	/**
	 * Sets the agenziaSomministrazione_Denominazione value for this UNISOMM_Type.
	 * 
	 * @param agenziaSomministrazione_Denominazione
	 */
	public void setAgenziaSomministrazione_Denominazione(java.lang.String agenziaSomministrazione_Denominazione) {
		this.agenziaSomministrazione_Denominazione = agenziaSomministrazione_Denominazione;
	}

	/**
	 * Gets the agenziaSomministrazione_SedeLavoro_CAP value for this UNISOMM_Type.
	 * 
	 * @return agenziaSomministrazione_SedeLavoro_CAP
	 */
	public java.lang.String getAgenziaSomministrazione_SedeLavoro_CAP() {
		return agenziaSomministrazione_SedeLavoro_CAP;
	}

	/**
	 * Sets the agenziaSomministrazione_SedeLavoro_CAP value for this UNISOMM_Type.
	 * 
	 * @param agenziaSomministrazione_SedeLavoro_CAP
	 */
	public void setAgenziaSomministrazione_SedeLavoro_CAP(java.lang.String agenziaSomministrazione_SedeLavoro_CAP) {
		this.agenziaSomministrazione_SedeLavoro_CAP = agenziaSomministrazione_SedeLavoro_CAP;
	}

	/**
	 * Gets the agenziaSomministrazione_SedeLavoro_ComuneNazione value for this UNISOMM_Type.
	 * 
	 * @return agenziaSomministrazione_SedeLavoro_ComuneNazione
	 */
	public java.lang.String getAgenziaSomministrazione_SedeLavoro_ComuneNazione() {
		return agenziaSomministrazione_SedeLavoro_ComuneNazione;
	}

	/**
	 * Sets the agenziaSomministrazione_SedeLavoro_ComuneNazione value for this UNISOMM_Type.
	 * 
	 * @param agenziaSomministrazione_SedeLavoro_ComuneNazione
	 */
	public void setAgenziaSomministrazione_SedeLavoro_ComuneNazione(
			java.lang.String agenziaSomministrazione_SedeLavoro_ComuneNazione) {
		this.agenziaSomministrazione_SedeLavoro_ComuneNazione = agenziaSomministrazione_SedeLavoro_ComuneNazione;
	}

	/**
	 * Gets the agenziaSomministrazione_SedeLavoro_Indirizzo value for this UNISOMM_Type.
	 * 
	 * @return agenziaSomministrazione_SedeLavoro_Indirizzo
	 */
	public java.lang.String getAgenziaSomministrazione_SedeLavoro_Indirizzo() {
		return agenziaSomministrazione_SedeLavoro_Indirizzo;
	}

	/**
	 * Sets the agenziaSomministrazione_SedeLavoro_Indirizzo value for this UNISOMM_Type.
	 * 
	 * @param agenziaSomministrazione_SedeLavoro_Indirizzo
	 */
	public void setAgenziaSomministrazione_SedeLavoro_Indirizzo(
			java.lang.String agenziaSomministrazione_SedeLavoro_Indirizzo) {
		this.agenziaSomministrazione_SedeLavoro_Indirizzo = agenziaSomministrazione_SedeLavoro_Indirizzo;
	}

	/**
	 * Gets the dittaUtilizzatrice_CodiceFiscale value for this UNISOMM_Type.
	 * 
	 * @return dittaUtilizzatrice_CodiceFiscale
	 */
	public java.lang.String getDittaUtilizzatrice_CodiceFiscale() {
		return dittaUtilizzatrice_CodiceFiscale;
	}

	/**
	 * Sets the dittaUtilizzatrice_CodiceFiscale value for this UNISOMM_Type.
	 * 
	 * @param dittaUtilizzatrice_CodiceFiscale
	 */
	public void setDittaUtilizzatrice_CodiceFiscale(java.lang.String dittaUtilizzatrice_CodiceFiscale) {
		this.dittaUtilizzatrice_CodiceFiscale = dittaUtilizzatrice_CodiceFiscale;
	}

	/**
	 * Gets the dittaUtilizzatrice_Denominazione value for this UNISOMM_Type.
	 * 
	 * @return dittaUtilizzatrice_Denominazione
	 */
	public java.lang.String getDittaUtilizzatrice_Denominazione() {
		return dittaUtilizzatrice_Denominazione;
	}

	/**
	 * Sets the dittaUtilizzatrice_Denominazione value for this UNISOMM_Type.
	 * 
	 * @param dittaUtilizzatrice_Denominazione
	 */
	public void setDittaUtilizzatrice_Denominazione(java.lang.String dittaUtilizzatrice_Denominazione) {
		this.dittaUtilizzatrice_Denominazione = dittaUtilizzatrice_Denominazione;
	}

	/**
	 * Gets the dittaUtilizzatrice_PA value for this UNISOMM_Type.
	 * 
	 * @return dittaUtilizzatrice_PA
	 */
	public java.lang.String getDittaUtilizzatrice_PA() {
		return dittaUtilizzatrice_PA;
	}

	/**
	 * Sets the dittaUtilizzatrice_PA value for this UNISOMM_Type.
	 * 
	 * @param dittaUtilizzatrice_PA
	 */
	public void setDittaUtilizzatrice_PA(java.lang.String dittaUtilizzatrice_PA) {
		this.dittaUtilizzatrice_PA = dittaUtilizzatrice_PA;
	}

	/**
	 * Gets the dittaUtilizzatrice_SedeLavoro_CAP value for this UNISOMM_Type.
	 * 
	 * @return dittaUtilizzatrice_SedeLavoro_CAP
	 */
	public java.lang.String getDittaUtilizzatrice_SedeLavoro_CAP() {
		return dittaUtilizzatrice_SedeLavoro_CAP;
	}

	/**
	 * Sets the dittaUtilizzatrice_SedeLavoro_CAP value for this UNISOMM_Type.
	 * 
	 * @param dittaUtilizzatrice_SedeLavoro_CAP
	 */
	public void setDittaUtilizzatrice_SedeLavoro_CAP(java.lang.String dittaUtilizzatrice_SedeLavoro_CAP) {
		this.dittaUtilizzatrice_SedeLavoro_CAP = dittaUtilizzatrice_SedeLavoro_CAP;
	}

	/**
	 * Gets the dittaUtilizzatrice_SedeLavoro_ComuneNazione value for this UNISOMM_Type.
	 * 
	 * @return dittaUtilizzatrice_SedeLavoro_ComuneNazione
	 */
	public java.lang.String getDittaUtilizzatrice_SedeLavoro_ComuneNazione() {
		return dittaUtilizzatrice_SedeLavoro_ComuneNazione;
	}

	/**
	 * Sets the dittaUtilizzatrice_SedeLavoro_ComuneNazione value for this UNISOMM_Type.
	 * 
	 * @param dittaUtilizzatrice_SedeLavoro_ComuneNazione
	 */
	public void setDittaUtilizzatrice_SedeLavoro_ComuneNazione(
			java.lang.String dittaUtilizzatrice_SedeLavoro_ComuneNazione) {
		this.dittaUtilizzatrice_SedeLavoro_ComuneNazione = dittaUtilizzatrice_SedeLavoro_ComuneNazione;
	}

	/**
	 * Gets the dittaUtilizzatrice_SedeLavoro_Indirizzo value for this UNISOMM_Type.
	 * 
	 * @return dittaUtilizzatrice_SedeLavoro_Indirizzo
	 */
	public java.lang.String getDittaUtilizzatrice_SedeLavoro_Indirizzo() {
		return dittaUtilizzatrice_SedeLavoro_Indirizzo;
	}

	/**
	 * Sets the dittaUtilizzatrice_SedeLavoro_Indirizzo value for this UNISOMM_Type.
	 * 
	 * @param dittaUtilizzatrice_SedeLavoro_Indirizzo
	 */
	public void setDittaUtilizzatrice_SedeLavoro_Indirizzo(java.lang.String dittaUtilizzatrice_SedeLavoro_Indirizzo) {
		this.dittaUtilizzatrice_SedeLavoro_Indirizzo = dittaUtilizzatrice_SedeLavoro_Indirizzo;
	}

	/**
	 * Gets the dittaUtilizzatrice_Settore value for this UNISOMM_Type.
	 * 
	 * @return dittaUtilizzatrice_Settore
	 */
	public java.lang.String getDittaUtilizzatrice_Settore() {
		return dittaUtilizzatrice_Settore;
	}

	/**
	 * Sets the dittaUtilizzatrice_Settore value for this UNISOMM_Type.
	 * 
	 * @param dittaUtilizzatrice_Settore
	 */
	public void setDittaUtilizzatrice_Settore(java.lang.String dittaUtilizzatrice_Settore) {
		this.dittaUtilizzatrice_Settore = dittaUtilizzatrice_Settore;
	}

	/**
	 * Gets the invio_CodiceComunicazione value for this UNISOMM_Type.
	 * 
	 * @return invio_CodiceComunicazione
	 */
	public java.lang.String getInvio_CodiceComunicazione() {
		return invio_CodiceComunicazione;
	}

	/**
	 * Sets the invio_CodiceComunicazione value for this UNISOMM_Type.
	 * 
	 * @param invio_CodiceComunicazione
	 */
	public void setInvio_CodiceComunicazione(java.lang.String invio_CodiceComunicazione) {
		this.invio_CodiceComunicazione = invio_CodiceComunicazione;
	}

	/**
	 * Gets the invio_CodiceComunicazionePrecedente value for this UNISOMM_Type.
	 * 
	 * @return invio_CodiceComunicazionePrecedente
	 */
	public java.lang.String getInvio_CodiceComunicazionePrecedente() {
		return invio_CodiceComunicazionePrecedente;
	}

	/**
	 * Sets the invio_CodiceComunicazionePrecedente value for this UNISOMM_Type.
	 * 
	 * @param invio_CodiceComunicazionePrecedente
	 */
	public void setInvio_CodiceComunicazionePrecedente(java.lang.String invio_CodiceComunicazionePrecedente) {
		this.invio_CodiceComunicazionePrecedente = invio_CodiceComunicazionePrecedente;
	}

	/**
	 * Gets the lavoratore_Cittadinanza value for this UNISOMM_Type.
	 * 
	 * @return lavoratore_Cittadinanza
	 */
	public java.lang.String getLavoratore_Cittadinanza() {
		return lavoratore_Cittadinanza;
	}

	/**
	 * Sets the lavoratore_Cittadinanza value for this UNISOMM_Type.
	 * 
	 * @param lavoratore_Cittadinanza
	 */
	public void setLavoratore_Cittadinanza(java.lang.String lavoratore_Cittadinanza) {
		this.lavoratore_Cittadinanza = lavoratore_Cittadinanza;
	}

	/**
	 * Gets the lavoratore_CodiceFiscale value for this UNISOMM_Type.
	 * 
	 * @return lavoratore_CodiceFiscale
	 */
	public java.lang.String getLavoratore_CodiceFiscale() {
		return lavoratore_CodiceFiscale;
	}

	/**
	 * Sets the lavoratore_CodiceFiscale value for this UNISOMM_Type.
	 * 
	 * @param lavoratore_CodiceFiscale
	 */
	public void setLavoratore_CodiceFiscale(java.lang.String lavoratore_CodiceFiscale) {
		this.lavoratore_CodiceFiscale = lavoratore_CodiceFiscale;
	}

	/**
	 * Gets the lavoratore_Cognome value for this UNISOMM_Type.
	 * 
	 * @return lavoratore_Cognome
	 */
	public java.lang.String getLavoratore_Cognome() {
		return lavoratore_Cognome;
	}

	/**
	 * Sets the lavoratore_Cognome value for this UNISOMM_Type.
	 * 
	 * @param lavoratore_Cognome
	 */
	public void setLavoratore_Cognome(java.lang.String lavoratore_Cognome) {
		this.lavoratore_Cognome = lavoratore_Cognome;
	}

	/**
	 * Gets the lavoratore_Nascita_ComuneNazione value for this UNISOMM_Type.
	 * 
	 * @return lavoratore_Nascita_ComuneNazione
	 */
	public java.lang.String getLavoratore_Nascita_ComuneNazione() {
		return lavoratore_Nascita_ComuneNazione;
	}

	/**
	 * Sets the lavoratore_Nascita_ComuneNazione value for this UNISOMM_Type.
	 * 
	 * @param lavoratore_Nascita_ComuneNazione
	 */
	public void setLavoratore_Nascita_ComuneNazione(java.lang.String lavoratore_Nascita_ComuneNazione) {
		this.lavoratore_Nascita_ComuneNazione = lavoratore_Nascita_ComuneNazione;
	}

	/**
	 * Gets the lavoratore_Nascita_Data value for this UNISOMM_Type.
	 * 
	 * @return lavoratore_Nascita_Data
	 */
	public java.lang.String getLavoratore_Nascita_Data() {
		return lavoratore_Nascita_Data;
	}

	/**
	 * Sets the lavoratore_Nascita_Data value for this UNISOMM_Type.
	 * 
	 * @param lavoratore_Nascita_Data
	 */
	public void setLavoratore_Nascita_Data(java.lang.String lavoratore_Nascita_Data) {
		this.lavoratore_Nascita_Data = lavoratore_Nascita_Data;
	}

	/**
	 * Gets the lavoratore_Nome value for this UNISOMM_Type.
	 * 
	 * @return lavoratore_Nome
	 */
	public java.lang.String getLavoratore_Nome() {
		return lavoratore_Nome;
	}

	/**
	 * Sets the lavoratore_Nome value for this UNISOMM_Type.
	 * 
	 * @param lavoratore_Nome
	 */
	public void setLavoratore_Nome(java.lang.String lavoratore_Nome) {
		this.lavoratore_Nome = lavoratore_Nome;
	}

	/**
	 * Gets the lavoratore_Sesso value for this UNISOMM_Type.
	 * 
	 * @return lavoratore_Sesso
	 */
	public java.lang.String getLavoratore_Sesso() {
		return lavoratore_Sesso;
	}

	/**
	 * Sets the lavoratore_Sesso value for this UNISOMM_Type.
	 * 
	 * @param lavoratore_Sesso
	 */
	public void setLavoratore_Sesso(java.lang.String lavoratore_Sesso) {
		this.lavoratore_Sesso = lavoratore_Sesso;
	}

	/**
	 * Gets the rapportoAgenziaSomm_CessazioneData value for this UNISOMM_Type.
	 * 
	 * @return rapportoAgenziaSomm_CessazioneData
	 */
	public java.lang.String getRapportoAgenziaSomm_CessazioneData() {
		return rapportoAgenziaSomm_CessazioneData;
	}

	/**
	 * Sets the rapportoAgenziaSomm_CessazioneData value for this UNISOMM_Type.
	 * 
	 * @param rapportoAgenziaSomm_CessazioneData
	 */
	public void setRapportoAgenziaSomm_CessazioneData(java.lang.String rapportoAgenziaSomm_CessazioneData) {
		this.rapportoAgenziaSomm_CessazioneData = rapportoAgenziaSomm_CessazioneData;
	}

	/**
	 * Gets the rapportoAgenziaSomm_CodiceEntePrevidenziale value for this UNISOMM_Type.
	 * 
	 * @return rapportoAgenziaSomm_CodiceEntePrevidenziale
	 */
	public java.lang.String getRapportoAgenziaSomm_CodiceEntePrevidenziale() {
		return rapportoAgenziaSomm_CodiceEntePrevidenziale;
	}

	/**
	 * Sets the rapportoAgenziaSomm_CodiceEntePrevidenziale value for this UNISOMM_Type.
	 * 
	 * @param rapportoAgenziaSomm_CodiceEntePrevidenziale
	 */
	public void setRapportoAgenziaSomm_CodiceEntePrevidenziale(
			java.lang.String rapportoAgenziaSomm_CodiceEntePrevidenziale) {
		this.rapportoAgenziaSomm_CodiceEntePrevidenziale = rapportoAgenziaSomm_CodiceEntePrevidenziale;
	}

	/**
	 * Gets the rapportoAgenziaSomm_NMatricola value for this UNISOMM_Type.
	 * 
	 * @return rapportoAgenziaSomm_NMatricola
	 */
	public java.lang.String getRapportoAgenziaSomm_NMatricola() {
		return rapportoAgenziaSomm_NMatricola;
	}

	/**
	 * Sets the rapportoAgenziaSomm_NMatricola value for this UNISOMM_Type.
	 * 
	 * @param rapportoAgenziaSomm_NMatricola
	 */
	public void setRapportoAgenziaSomm_NMatricola(java.lang.String rapportoAgenziaSomm_NMatricola) {
		this.rapportoAgenziaSomm_NMatricola = rapportoAgenziaSomm_NMatricola;
	}

	/**
	 * Gets the rapportoAgenziaSomm_NAgenziaSomm value for this UNISOMM_Type.
	 * 
	 * @return rapportoAgenziaSomm_NAgenziaSomm
	 */
	public java.lang.String getRapportoAgenziaSomm_NAgenziaSomm() {
		return rapportoAgenziaSomm_NAgenziaSomm;
	}

	/**
	 * Sets the rapportoAgenziaSomm_NAgenziaSomm value for this UNISOMM_Type.
	 * 
	 * @param rapportoAgenziaSomm_NAgenziaSomm
	 */
	public void setRapportoAgenziaSomm_NAgenziaSomm(java.lang.String rapportoAgenziaSomm_NAgenziaSomm) {
		this.rapportoAgenziaSomm_NAgenziaSomm = rapportoAgenziaSomm_NAgenziaSomm;
	}

	/**
	 * Gets the rapportoAgenziaSomm_IndennitaDisponibilita value for this UNISOMM_Type.
	 * 
	 * @return rapportoAgenziaSomm_IndennitaDisponibilita
	 */
	public java.lang.String getRapportoAgenziaSomm_IndennitaDisponibilita() {
		return rapportoAgenziaSomm_IndennitaDisponibilita;
	}

	/**
	 * Sets the rapportoAgenziaSomm_IndennitaDisponibilita value for this UNISOMM_Type.
	 * 
	 * @param rapportoAgenziaSomm_IndennitaDisponibilita
	 */
	public void setRapportoAgenziaSomm_IndennitaDisponibilita(
			java.lang.String rapportoAgenziaSomm_IndennitaDisponibilita) {
		this.rapportoAgenziaSomm_IndennitaDisponibilita = rapportoAgenziaSomm_IndennitaDisponibilita;
	}

	/**
	 * Gets the rapportoAgenziaSomm_DataFine value for this UNISOMM_Type.
	 * 
	 * @return rapportoAgenziaSomm_DataFine
	 */
	public java.lang.String getRapportoAgenziaSomm_DataFine() {
		return rapportoAgenziaSomm_DataFine;
	}

	/**
	 * Sets the rapportoAgenziaSomm_DataFine value for this UNISOMM_Type.
	 * 
	 * @param rapportoAgenziaSomm_DataFine
	 */
	public void setRapportoAgenziaSomm_DataFine(java.lang.String rapportoAgenziaSomm_DataFine) {
		this.rapportoAgenziaSomm_DataFine = rapportoAgenziaSomm_DataFine;
	}

	/**
	 * Gets the rapportoAgenziaSomm_DataInizio value for this UNISOMM_Type.
	 * 
	 * @return rapportoAgenziaSomm_DataInizio
	 */
	public java.lang.String getRapportoAgenziaSomm_DataInizio() {
		return rapportoAgenziaSomm_DataInizio;
	}

	/**
	 * Sets the rapportoAgenziaSomm_DataInizio value for this UNISOMM_Type.
	 * 
	 * @param rapportoAgenziaSomm_DataInizio
	 */
	public void setRapportoAgenziaSomm_DataInizio(java.lang.String rapportoAgenziaSomm_DataInizio) {
		this.rapportoAgenziaSomm_DataInizio = rapportoAgenziaSomm_DataInizio;
	}

	/**
	 * Gets the rapportoAgenziaSomm_Proroga_DataFine value for this UNISOMM_Type.
	 * 
	 * @return rapportoAgenziaSomm_Proroga_DataFine
	 */
	public java.lang.String getRapportoAgenziaSomm_Proroga_DataFine() {
		return rapportoAgenziaSomm_Proroga_DataFine;
	}

	/**
	 * Sets the rapportoAgenziaSomm_Proroga_DataFine value for this UNISOMM_Type.
	 * 
	 * @param rapportoAgenziaSomm_Proroga_DataFine
	 */
	public void setRapportoAgenziaSomm_Proroga_DataFine(java.lang.String rapportoAgenziaSomm_Proroga_DataFine) {
		this.rapportoAgenziaSomm_Proroga_DataFine = rapportoAgenziaSomm_Proroga_DataFine;
	}

	/**
	 * Gets the rapportoAgenziaSomm_TipologiaContrattuale value for this UNISOMM_Type.
	 * 
	 * @return rapportoAgenziaSomm_TipologiaContrattuale
	 */
	public java.lang.String getRapportoAgenziaSomm_TipologiaContrattuale() {
		return rapportoAgenziaSomm_TipologiaContrattuale;
	}

	/**
	 * Sets the rapportoAgenziaSomm_TipologiaContrattuale value for this UNISOMM_Type.
	 * 
	 * @param rapportoAgenziaSomm_TipologiaContrattuale
	 */
	public void setRapportoAgenziaSomm_TipologiaContrattuale(
			java.lang.String rapportoAgenziaSomm_TipologiaContrattuale) {
		this.rapportoAgenziaSomm_TipologiaContrattuale = rapportoAgenziaSomm_TipologiaContrattuale;
	}

	/**
	 * Gets the rapportoDittaUtilizzatrice_CCNL value for this UNISOMM_Type.
	 * 
	 * @return rapportoDittaUtilizzatrice_CCNL
	 */
	public java.lang.String getRapportoDittaUtilizzatrice_CCNL() {
		return rapportoDittaUtilizzatrice_CCNL;
	}

	/**
	 * Sets the rapportoDittaUtilizzatrice_CCNL value for this UNISOMM_Type.
	 * 
	 * @param rapportoDittaUtilizzatrice_CCNL
	 */
	public void setRapportoDittaUtilizzatrice_CCNL(java.lang.String rapportoDittaUtilizzatrice_CCNL) {
		this.rapportoDittaUtilizzatrice_CCNL = rapportoDittaUtilizzatrice_CCNL;
	}

	/**
	 * Gets the rapportoDittaUtilizzatrice_CodiceAgevolazioni value for this UNISOMM_Type.
	 * 
	 * @return rapportoDittaUtilizzatrice_CodiceAgevolazioni
	 */
	public java.lang.String getRapportoDittaUtilizzatrice_CodiceAgevolazioni() {
		return rapportoDittaUtilizzatrice_CodiceAgevolazioni;
	}

	/**
	 * Sets the rapportoDittaUtilizzatrice_CodiceAgevolazioni value for this UNISOMM_Type.
	 * 
	 * @param rapportoDittaUtilizzatrice_CodiceAgevolazioni
	 */
	public void setRapportoDittaUtilizzatrice_CodiceAgevolazioni(
			java.lang.String rapportoDittaUtilizzatrice_CodiceAgevolazioni) {
		this.rapportoDittaUtilizzatrice_CodiceAgevolazioni = rapportoDittaUtilizzatrice_CodiceAgevolazioni;
	}

	/**
	 * Gets the rapportoDittaUtilizzatrice_CodiceTrasformazione value for this UNISOMM_Type.
	 * 
	 * @return rapportoDittaUtilizzatrice_CodiceTrasformazione
	 */
	public java.lang.String getRapportoDittaUtilizzatrice_CodiceTrasformazione() {
		return rapportoDittaUtilizzatrice_CodiceTrasformazione;
	}

	/**
	 * Sets the rapportoDittaUtilizzatrice_CodiceTrasformazione value for this UNISOMM_Type.
	 * 
	 * @param rapportoDittaUtilizzatrice_CodiceTrasformazione
	 */
	public void setRapportoDittaUtilizzatrice_CodiceTrasformazione(
			java.lang.String rapportoDittaUtilizzatrice_CodiceTrasformazione) {
		this.rapportoDittaUtilizzatrice_CodiceTrasformazione = rapportoDittaUtilizzatrice_CodiceTrasformazione;
	}

	/**
	 * Gets the rapportoDittaUtilizzatrice_DataCessazione value for this UNISOMM_Type.
	 * 
	 * @return rapportoDittaUtilizzatrice_DataCessazione
	 */
	public java.lang.String getRapportoDittaUtilizzatrice_DataCessazione() {
		return rapportoDittaUtilizzatrice_DataCessazione;
	}

	/**
	 * Sets the rapportoDittaUtilizzatrice_DataCessazione value for this UNISOMM_Type.
	 * 
	 * @param rapportoDittaUtilizzatrice_DataCessazione
	 */
	public void setRapportoDittaUtilizzatrice_DataCessazione(
			java.lang.String rapportoDittaUtilizzatrice_DataCessazione) {
		this.rapportoDittaUtilizzatrice_DataCessazione = rapportoDittaUtilizzatrice_DataCessazione;
	}

	/**
	 * Gets the rapportoDittaUtilizzatrice_DataFineMissione value for this UNISOMM_Type.
	 * 
	 * @return rapportoDittaUtilizzatrice_DataFineMissione
	 */
	public java.lang.String getRapportoDittaUtilizzatrice_DataFineMissione() {
		return rapportoDittaUtilizzatrice_DataFineMissione;
	}

	/**
	 * Sets the rapportoDittaUtilizzatrice_DataFineMissione value for this UNISOMM_Type.
	 * 
	 * @param rapportoDittaUtilizzatrice_DataFineMissione
	 */
	public void setRapportoDittaUtilizzatrice_DataFineMissione(
			java.lang.String rapportoDittaUtilizzatrice_DataFineMissione) {
		this.rapportoDittaUtilizzatrice_DataFineMissione = rapportoDittaUtilizzatrice_DataFineMissione;
	}

	/**
	 * Gets the rapportoDittaUtilizzatrice_DataFineProroga value for this UNISOMM_Type.
	 * 
	 * @return rapportoDittaUtilizzatrice_DataFineProroga
	 */
	public java.lang.String getRapportoDittaUtilizzatrice_DataFineProroga() {
		return rapportoDittaUtilizzatrice_DataFineProroga;
	}

	/**
	 * Sets the rapportoDittaUtilizzatrice_DataFineProroga value for this UNISOMM_Type.
	 * 
	 * @param rapportoDittaUtilizzatrice_DataFineProroga
	 */
	public void setRapportoDittaUtilizzatrice_DataFineProroga(
			java.lang.String rapportoDittaUtilizzatrice_DataFineProroga) {
		this.rapportoDittaUtilizzatrice_DataFineProroga = rapportoDittaUtilizzatrice_DataFineProroga;
	}

	/**
	 * Gets the rapportoDittaUtilizzatrice_DataInizioMissione value for this UNISOMM_Type.
	 * 
	 * @return rapportoDittaUtilizzatrice_DataInizioMissione
	 */
	public java.lang.String getRapportoDittaUtilizzatrice_DataInizioMissione() {
		return rapportoDittaUtilizzatrice_DataInizioMissione;
	}

	/**
	 * Sets the rapportoDittaUtilizzatrice_DataInizioMissione value for this UNISOMM_Type.
	 * 
	 * @param rapportoDittaUtilizzatrice_DataInizioMissione
	 */
	public void setRapportoDittaUtilizzatrice_DataInizioMissione(
			java.lang.String rapportoDittaUtilizzatrice_DataInizioMissione) {
		this.rapportoDittaUtilizzatrice_DataInizioMissione = rapportoDittaUtilizzatrice_DataInizioMissione;
	}

	/**
	 * Gets the rapportoDittaUtilizzatrice_DataTrasformazione value for this UNISOMM_Type.
	 * 
	 * @return rapportoDittaUtilizzatrice_DataTrasformazione
	 */
	public java.lang.String getRapportoDittaUtilizzatrice_DataTrasformazione() {
		return rapportoDittaUtilizzatrice_DataTrasformazione;
	}

	/**
	 * Sets the rapportoDittaUtilizzatrice_DataTrasformazione value for this UNISOMM_Type.
	 * 
	 * @param rapportoDittaUtilizzatrice_DataTrasformazione
	 */
	public void setRapportoDittaUtilizzatrice_DataTrasformazione(
			java.lang.String rapportoDittaUtilizzatrice_DataTrasformazione) {
		this.rapportoDittaUtilizzatrice_DataTrasformazione = rapportoDittaUtilizzatrice_DataTrasformazione;
	}

	/**
	 * Gets the rapportoDittaUtilizzatrice_LavoroInAgricoltura value for this UNISOMM_Type.
	 * 
	 * @return rapportoDittaUtilizzatrice_LavoroInAgricoltura
	 */
	public java.lang.String getRapportoDittaUtilizzatrice_LavoroInAgricoltura() {
		return rapportoDittaUtilizzatrice_LavoroInAgricoltura;
	}

	/**
	 * Sets the rapportoDittaUtilizzatrice_LavoroInAgricoltura value for this UNISOMM_Type.
	 * 
	 * @param rapportoDittaUtilizzatrice_LavoroInAgricoltura
	 */
	public void setRapportoDittaUtilizzatrice_LavoroInAgricoltura(
			java.lang.String rapportoDittaUtilizzatrice_LavoroInAgricoltura) {
		this.rapportoDittaUtilizzatrice_LavoroInAgricoltura = rapportoDittaUtilizzatrice_LavoroInAgricoltura;
	}

	/**
	 * Gets the rapportoDittaUtilizzatrice_TipoOrario value for this UNISOMM_Type.
	 * 
	 * @return rapportoDittaUtilizzatrice_TipoOrario
	 */
	public java.lang.String getRapportoDittaUtilizzatrice_TipoOrario() {
		return rapportoDittaUtilizzatrice_TipoOrario;
	}

	/**
	 * Sets the rapportoDittaUtilizzatrice_TipoOrario value for this UNISOMM_Type.
	 * 
	 * @param rapportoDittaUtilizzatrice_TipoOrario
	 */
	public void setRapportoDittaUtilizzatrice_TipoOrario(java.lang.String rapportoDittaUtilizzatrice_TipoOrario) {
		this.rapportoDittaUtilizzatrice_TipoOrario = rapportoDittaUtilizzatrice_TipoOrario;
	}

	/**
	 * Gets the rapportoDittaUtilizzatrice_TipoOrario_OreSett value for this UNISOMM_Type.
	 * 
	 * @return rapportoDittaUtilizzatrice_TipoOrario_OreSett
	 */
	public java.lang.String getRapportoDittaUtilizzatrice_TipoOrario_OreSett() {
		return rapportoDittaUtilizzatrice_TipoOrario_OreSett;
	}

	/**
	 * Sets the rapportoDittaUtilizzatrice_TipoOrario_OreSett value for this UNISOMM_Type.
	 * 
	 * @param rapportoDittaUtilizzatrice_TipoOrario_OreSett
	 */
	public void setRapportoDittaUtilizzatrice_TipoOrario_OreSett(
			java.lang.String rapportoDittaUtilizzatrice_TipoOrario_OreSett) {
		this.rapportoDittaUtilizzatrice_TipoOrario_OreSett = rapportoDittaUtilizzatrice_TipoOrario_OreSett;
	}

	/**
	 * Gets the rapportoAgenziaDittaUtilizzatrice_NContrattoSomm value for this UNISOMM_Type.
	 * 
	 * @return rapportoAgenziaDittaUtilizzatrice_NContrattoSomm
	 */
	public java.lang.String getRapportoAgenziaDittaUtilizzatrice_NContrattoSomm() {
		return rapportoAgenziaDittaUtilizzatrice_NContrattoSomm;
	}

	/**
	 * Sets the rapportoAgenziaDittaUtilizzatrice_NContrattoSomm value for this UNISOMM_Type.
	 * 
	 * @param rapportoAgenziaDittaUtilizzatrice_NContrattoSomm
	 */
	public void setRapportoAgenziaDittaUtilizzatrice_NContrattoSomm(
			java.lang.String rapportoAgenziaDittaUtilizzatrice_NContrattoSomm) {
		this.rapportoAgenziaDittaUtilizzatrice_NContrattoSomm = rapportoAgenziaDittaUtilizzatrice_NContrattoSomm;
	}

	/**
	 * Gets the SYS_DATARICEZIONE value for this UNISOMM_Type.
	 * 
	 * @return SYS_DATARICEZIONE
	 */
	public java.util.Calendar getSYS_DATARICEZIONE() {
		return SYS_DATARICEZIONE;
	}

	/**
	 * Sets the SYS_DATARICEZIONE value for this UNISOMM_Type.
	 * 
	 * @param SYS_DATARICEZIONE
	 */
	public void setSYS_DATARICEZIONE(java.util.Calendar SYS_DATARICEZIONE) {
		this.SYS_DATARICEZIONE = SYS_DATARICEZIONE;
	}

	/**
	 * Gets the SYS_DATARIFERIMENTO value for this UNISOMM_Type.
	 * 
	 * @return SYS_DATARIFERIMENTO
	 */
	public java.util.Calendar getSYS_DATARIFERIMENTO() {
		return SYS_DATARIFERIMENTO;
	}

	/**
	 * Sets the SYS_DATARIFERIMENTO value for this UNISOMM_Type.
	 * 
	 * @param SYS_DATARIFERIMENTO
	 */
	public void setSYS_DATARIFERIMENTO(java.util.Calendar SYS_DATARIFERIMENTO) {
		this.SYS_DATARIFERIMENTO = SYS_DATARIFERIMENTO;
	}

	/**
	 * Gets the SYS_TIPOMODULO value for this UNISOMM_Type.
	 * 
	 * @return SYS_TIPOMODULO
	 */
	public java.lang.String getSYS_TIPOMODULO() {
		return SYS_TIPOMODULO;
	}

	/**
	 * Sets the SYS_TIPOMODULO value for this UNISOMM_Type.
	 * 
	 * @param SYS_TIPOMODULO
	 */
	public void setSYS_TIPOMODULO(java.lang.String SYS_TIPOMODULO) {
		this.SYS_TIPOMODULO = SYS_TIPOMODULO;
	}

	/**
	 * Gets the rowOrder value for this UNISOMM_Type.
	 * 
	 * @return rowOrder
	 */
	public int getRowOrder() {
		return rowOrder;
	}

	/**
	 * Sets the rowOrder value for this UNISOMM_Type.
	 * 
	 * @param rowOrder
	 */
	public void setRowOrder(int rowOrder) {
		this.rowOrder = rowOrder;
	}

	private java.lang.Object __equalsCalc = null;

	public synchronized boolean equals(java.lang.Object obj) {
		if (!(obj instanceof UNISOMM_Type))
			return false;
		UNISOMM_Type other = (UNISOMM_Type) obj;
		if (obj == null)
			return false;
		if (this == obj)
			return true;
		if (__equalsCalc != null) {
			return (__equalsCalc == obj);
		}
		__equalsCalc = obj;
		boolean _equals;
		_equals = true
				&& ((this.agenziaSomministrazione_CodiceFiscale == null
						&& other.getAgenziaSomministrazione_CodiceFiscale() == null)
						|| (this.agenziaSomministrazione_CodiceFiscale != null
								&& this.agenziaSomministrazione_CodiceFiscale
										.equals(other.getAgenziaSomministrazione_CodiceFiscale())))
				&& ((this.agenziaSomministrazione_Denominazione == null
						&& other.getAgenziaSomministrazione_Denominazione() == null)
						|| (this.agenziaSomministrazione_Denominazione != null
								&& this.agenziaSomministrazione_Denominazione
										.equals(other.getAgenziaSomministrazione_Denominazione())))
				&& ((this.agenziaSomministrazione_SedeLavoro_CAP == null
						&& other.getAgenziaSomministrazione_SedeLavoro_CAP() == null)
						|| (this.agenziaSomministrazione_SedeLavoro_CAP != null
								&& this.agenziaSomministrazione_SedeLavoro_CAP
										.equals(other.getAgenziaSomministrazione_SedeLavoro_CAP())))
				&& ((this.agenziaSomministrazione_SedeLavoro_ComuneNazione == null
						&& other.getAgenziaSomministrazione_SedeLavoro_ComuneNazione() == null)
						|| (this.agenziaSomministrazione_SedeLavoro_ComuneNazione != null
								&& this.agenziaSomministrazione_SedeLavoro_ComuneNazione
										.equals(other.getAgenziaSomministrazione_SedeLavoro_ComuneNazione())))
				&& ((this.agenziaSomministrazione_SedeLavoro_Indirizzo == null
						&& other.getAgenziaSomministrazione_SedeLavoro_Indirizzo() == null)
						|| (this.agenziaSomministrazione_SedeLavoro_Indirizzo != null
								&& this.agenziaSomministrazione_SedeLavoro_Indirizzo
										.equals(other.getAgenziaSomministrazione_SedeLavoro_Indirizzo())))
				&& ((this.dittaUtilizzatrice_CodiceFiscale == null
						&& other.getDittaUtilizzatrice_CodiceFiscale() == null)
						|| (this.dittaUtilizzatrice_CodiceFiscale != null && this.dittaUtilizzatrice_CodiceFiscale
								.equals(other.getDittaUtilizzatrice_CodiceFiscale())))
				&& ((this.dittaUtilizzatrice_Denominazione == null
						&& other.getDittaUtilizzatrice_Denominazione() == null)
						|| (this.dittaUtilizzatrice_Denominazione != null && this.dittaUtilizzatrice_Denominazione
								.equals(other.getDittaUtilizzatrice_Denominazione())))
				&& ((this.dittaUtilizzatrice_PA == null && other.getDittaUtilizzatrice_PA() == null)
						|| (this.dittaUtilizzatrice_PA != null
								&& this.dittaUtilizzatrice_PA.equals(other.getDittaUtilizzatrice_PA())))
				&& ((this.dittaUtilizzatrice_SedeLavoro_CAP == null
						&& other.getDittaUtilizzatrice_SedeLavoro_CAP() == null)
						|| (this.dittaUtilizzatrice_SedeLavoro_CAP != null && this.dittaUtilizzatrice_SedeLavoro_CAP
								.equals(other.getDittaUtilizzatrice_SedeLavoro_CAP())))
				&& ((this.dittaUtilizzatrice_SedeLavoro_ComuneNazione == null
						&& other.getDittaUtilizzatrice_SedeLavoro_ComuneNazione() == null)
						|| (this.dittaUtilizzatrice_SedeLavoro_ComuneNazione != null
								&& this.dittaUtilizzatrice_SedeLavoro_ComuneNazione
										.equals(other.getDittaUtilizzatrice_SedeLavoro_ComuneNazione())))
				&& ((this.dittaUtilizzatrice_SedeLavoro_Indirizzo == null
						&& other.getDittaUtilizzatrice_SedeLavoro_Indirizzo() == null)
						|| (this.dittaUtilizzatrice_SedeLavoro_Indirizzo != null
								&& this.dittaUtilizzatrice_SedeLavoro_Indirizzo
										.equals(other.getDittaUtilizzatrice_SedeLavoro_Indirizzo())))
				&& ((this.dittaUtilizzatrice_Settore == null && other.getDittaUtilizzatrice_Settore() == null)
						|| (this.dittaUtilizzatrice_Settore != null
								&& this.dittaUtilizzatrice_Settore.equals(other.getDittaUtilizzatrice_Settore())))
				&& ((this.invio_CodiceComunicazione == null && other.getInvio_CodiceComunicazione() == null)
						|| (this.invio_CodiceComunicazione != null
								&& this.invio_CodiceComunicazione.equals(other.getInvio_CodiceComunicazione())))
				&& ((this.invio_CodiceComunicazionePrecedente == null
						&& other.getInvio_CodiceComunicazionePrecedente() == null)
						|| (this.invio_CodiceComunicazionePrecedente != null && this.invio_CodiceComunicazionePrecedente
								.equals(other.getInvio_CodiceComunicazionePrecedente())))
				&& ((this.lavoratore_Cittadinanza == null && other.getLavoratore_Cittadinanza() == null)
						|| (this.lavoratore_Cittadinanza != null
								&& this.lavoratore_Cittadinanza.equals(other.getLavoratore_Cittadinanza())))
				&& ((this.lavoratore_CodiceFiscale == null && other.getLavoratore_CodiceFiscale() == null)
						|| (this.lavoratore_CodiceFiscale != null
								&& this.lavoratore_CodiceFiscale.equals(other.getLavoratore_CodiceFiscale())))
				&& ((this.lavoratore_Cognome == null && other.getLavoratore_Cognome() == null)
						|| (this.lavoratore_Cognome != null
								&& this.lavoratore_Cognome.equals(other.getLavoratore_Cognome())))
				&& ((this.lavoratore_Nascita_ComuneNazione == null
						&& other.getLavoratore_Nascita_ComuneNazione() == null)
						|| (this.lavoratore_Nascita_ComuneNazione != null && this.lavoratore_Nascita_ComuneNazione
								.equals(other.getLavoratore_Nascita_ComuneNazione())))
				&& ((this.lavoratore_Nascita_Data == null && other.getLavoratore_Nascita_Data() == null)
						|| (this.lavoratore_Nascita_Data != null
								&& this.lavoratore_Nascita_Data.equals(other.getLavoratore_Nascita_Data())))
				&& ((this.lavoratore_Nome == null && other.getLavoratore_Nome() == null)
						|| (this.lavoratore_Nome != null && this.lavoratore_Nome.equals(other.getLavoratore_Nome())))
				&& ((this.lavoratore_Sesso == null && other.getLavoratore_Sesso() == null)
						|| (this.lavoratore_Sesso != null && this.lavoratore_Sesso.equals(other.getLavoratore_Sesso())))
				&& ((this.rapportoAgenziaSomm_CessazioneData == null
						&& other.getRapportoAgenziaSomm_CessazioneData() == null)
						|| (this.rapportoAgenziaSomm_CessazioneData != null && this.rapportoAgenziaSomm_CessazioneData
								.equals(other.getRapportoAgenziaSomm_CessazioneData())))
				&& ((this.rapportoAgenziaSomm_CodiceEntePrevidenziale == null
						&& other.getRapportoAgenziaSomm_CodiceEntePrevidenziale() == null)
						|| (this.rapportoAgenziaSomm_CodiceEntePrevidenziale != null
								&& this.rapportoAgenziaSomm_CodiceEntePrevidenziale
										.equals(other.getRapportoAgenziaSomm_CodiceEntePrevidenziale())))
				&& ((this.rapportoAgenziaSomm_NMatricola == null && other.getRapportoAgenziaSomm_NMatricola() == null)
						|| (this.rapportoAgenziaSomm_NMatricola != null && this.rapportoAgenziaSomm_NMatricola
								.equals(other.getRapportoAgenziaSomm_NMatricola())))
				&& ((this.rapportoAgenziaSomm_NAgenziaSomm == null
						&& other.getRapportoAgenziaSomm_NAgenziaSomm() == null)
						|| (this.rapportoAgenziaSomm_NAgenziaSomm != null && this.rapportoAgenziaSomm_NAgenziaSomm
								.equals(other.getRapportoAgenziaSomm_NAgenziaSomm())))
				&& ((this.rapportoAgenziaSomm_IndennitaDisponibilita == null
						&& other.getRapportoAgenziaSomm_IndennitaDisponibilita() == null)
						|| (this.rapportoAgenziaSomm_IndennitaDisponibilita != null
								&& this.rapportoAgenziaSomm_IndennitaDisponibilita
										.equals(other.getRapportoAgenziaSomm_IndennitaDisponibilita())))
				&& ((this.rapportoAgenziaSomm_DataFine == null && other.getRapportoAgenziaSomm_DataFine() == null)
						|| (this.rapportoAgenziaSomm_DataFine != null
								&& this.rapportoAgenziaSomm_DataFine.equals(other.getRapportoAgenziaSomm_DataFine())))
				&& ((this.rapportoAgenziaSomm_DataInizio == null && other.getRapportoAgenziaSomm_DataInizio() == null)
						|| (this.rapportoAgenziaSomm_DataInizio != null && this.rapportoAgenziaSomm_DataInizio
								.equals(other.getRapportoAgenziaSomm_DataInizio())))
				&& ((this.rapportoAgenziaSomm_Proroga_DataFine == null
						&& other.getRapportoAgenziaSomm_Proroga_DataFine() == null)
						|| (this.rapportoAgenziaSomm_Proroga_DataFine != null
								&& this.rapportoAgenziaSomm_Proroga_DataFine
										.equals(other.getRapportoAgenziaSomm_Proroga_DataFine())))
				&& ((this.rapportoAgenziaSomm_TipologiaContrattuale == null
						&& other.getRapportoAgenziaSomm_TipologiaContrattuale() == null)
						|| (this.rapportoAgenziaSomm_TipologiaContrattuale != null
								&& this.rapportoAgenziaSomm_TipologiaContrattuale
										.equals(other.getRapportoAgenziaSomm_TipologiaContrattuale())))
				&& ((this.rapportoDittaUtilizzatrice_CCNL == null && other.getRapportoDittaUtilizzatrice_CCNL() == null)
						|| (this.rapportoDittaUtilizzatrice_CCNL != null && this.rapportoDittaUtilizzatrice_CCNL
								.equals(other.getRapportoDittaUtilizzatrice_CCNL())))
				&& ((this.rapportoDittaUtilizzatrice_CodiceAgevolazioni == null
						&& other.getRapportoDittaUtilizzatrice_CodiceAgevolazioni() == null)
						|| (this.rapportoDittaUtilizzatrice_CodiceAgevolazioni != null
								&& this.rapportoDittaUtilizzatrice_CodiceAgevolazioni
										.equals(other.getRapportoDittaUtilizzatrice_CodiceAgevolazioni())))
				&& ((this.rapportoDittaUtilizzatrice_CodiceTrasformazione == null
						&& other.getRapportoDittaUtilizzatrice_CodiceTrasformazione() == null)
						|| (this.rapportoDittaUtilizzatrice_CodiceTrasformazione != null
								&& this.rapportoDittaUtilizzatrice_CodiceTrasformazione
										.equals(other.getRapportoDittaUtilizzatrice_CodiceTrasformazione())))
				&& ((this.rapportoDittaUtilizzatrice_DataCessazione == null
						&& other.getRapportoDittaUtilizzatrice_DataCessazione() == null)
						|| (this.rapportoDittaUtilizzatrice_DataCessazione != null
								&& this.rapportoDittaUtilizzatrice_DataCessazione
										.equals(other.getRapportoDittaUtilizzatrice_DataCessazione())))
				&& ((this.rapportoDittaUtilizzatrice_DataFineMissione == null
						&& other.getRapportoDittaUtilizzatrice_DataFineMissione() == null)
						|| (this.rapportoDittaUtilizzatrice_DataFineMissione != null
								&& this.rapportoDittaUtilizzatrice_DataFineMissione
										.equals(other.getRapportoDittaUtilizzatrice_DataFineMissione())))
				&& ((this.rapportoDittaUtilizzatrice_DataFineProroga == null
						&& other.getRapportoDittaUtilizzatrice_DataFineProroga() == null)
						|| (this.rapportoDittaUtilizzatrice_DataFineProroga != null
								&& this.rapportoDittaUtilizzatrice_DataFineProroga
										.equals(other.getRapportoDittaUtilizzatrice_DataFineProroga())))
				&& ((this.rapportoDittaUtilizzatrice_DataInizioMissione == null
						&& other.getRapportoDittaUtilizzatrice_DataInizioMissione() == null)
						|| (this.rapportoDittaUtilizzatrice_DataInizioMissione != null
								&& this.rapportoDittaUtilizzatrice_DataInizioMissione
										.equals(other.getRapportoDittaUtilizzatrice_DataInizioMissione())))
				&& ((this.rapportoDittaUtilizzatrice_DataTrasformazione == null
						&& other.getRapportoDittaUtilizzatrice_DataTrasformazione() == null)
						|| (this.rapportoDittaUtilizzatrice_DataTrasformazione != null
								&& this.rapportoDittaUtilizzatrice_DataTrasformazione
										.equals(other.getRapportoDittaUtilizzatrice_DataTrasformazione())))
				&& ((this.rapportoDittaUtilizzatrice_LavoroInAgricoltura == null
						&& other.getRapportoDittaUtilizzatrice_LavoroInAgricoltura() == null)
						|| (this.rapportoDittaUtilizzatrice_LavoroInAgricoltura != null
								&& this.rapportoDittaUtilizzatrice_LavoroInAgricoltura
										.equals(other.getRapportoDittaUtilizzatrice_LavoroInAgricoltura())))
				&& ((this.rapportoDittaUtilizzatrice_TipoOrario == null
						&& other.getRapportoDittaUtilizzatrice_TipoOrario() == null)
						|| (this.rapportoDittaUtilizzatrice_TipoOrario != null
								&& this.rapportoDittaUtilizzatrice_TipoOrario
										.equals(other.getRapportoDittaUtilizzatrice_TipoOrario())))
				&& ((this.rapportoDittaUtilizzatrice_TipoOrario_OreSett == null
						&& other.getRapportoDittaUtilizzatrice_TipoOrario_OreSett() == null)
						|| (this.rapportoDittaUtilizzatrice_TipoOrario_OreSett != null
								&& this.rapportoDittaUtilizzatrice_TipoOrario_OreSett
										.equals(other.getRapportoDittaUtilizzatrice_TipoOrario_OreSett())))
				&& ((this.rapportoAgenziaDittaUtilizzatrice_NContrattoSomm == null
						&& other.getRapportoAgenziaDittaUtilizzatrice_NContrattoSomm() == null)
						|| (this.rapportoAgenziaDittaUtilizzatrice_NContrattoSomm != null
								&& this.rapportoAgenziaDittaUtilizzatrice_NContrattoSomm
										.equals(other.getRapportoAgenziaDittaUtilizzatrice_NContrattoSomm())))
				&& ((this.SYS_DATARICEZIONE == null && other.getSYS_DATARICEZIONE() == null)
						|| (this.SYS_DATARICEZIONE != null
								&& this.SYS_DATARICEZIONE.equals(other.getSYS_DATARICEZIONE())))
				&& ((this.SYS_DATARIFERIMENTO == null && other.getSYS_DATARIFERIMENTO() == null)
						|| (this.SYS_DATARIFERIMENTO != null
								&& this.SYS_DATARIFERIMENTO.equals(other.getSYS_DATARIFERIMENTO())))
				&& ((this.SYS_TIPOMODULO == null && other.getSYS_TIPOMODULO() == null)
						|| (this.SYS_TIPOMODULO != null && this.SYS_TIPOMODULO.equals(other.getSYS_TIPOMODULO())))
				&& this.rowOrder == other.getRowOrder();
		__equalsCalc = null;
		return _equals;
	}

	private boolean __hashCodeCalc = false;

	public synchronized int hashCode() {
		if (__hashCodeCalc) {
			return 0;
		}
		__hashCodeCalc = true;
		int _hashCode = 1;
		if (getAgenziaSomministrazione_CodiceFiscale() != null) {
			_hashCode += getAgenziaSomministrazione_CodiceFiscale().hashCode();
		}
		if (getAgenziaSomministrazione_Denominazione() != null) {
			_hashCode += getAgenziaSomministrazione_Denominazione().hashCode();
		}
		if (getAgenziaSomministrazione_SedeLavoro_CAP() != null) {
			_hashCode += getAgenziaSomministrazione_SedeLavoro_CAP().hashCode();
		}
		if (getAgenziaSomministrazione_SedeLavoro_ComuneNazione() != null) {
			_hashCode += getAgenziaSomministrazione_SedeLavoro_ComuneNazione().hashCode();
		}
		if (getAgenziaSomministrazione_SedeLavoro_Indirizzo() != null) {
			_hashCode += getAgenziaSomministrazione_SedeLavoro_Indirizzo().hashCode();
		}
		if (getDittaUtilizzatrice_CodiceFiscale() != null) {
			_hashCode += getDittaUtilizzatrice_CodiceFiscale().hashCode();
		}
		if (getDittaUtilizzatrice_Denominazione() != null) {
			_hashCode += getDittaUtilizzatrice_Denominazione().hashCode();
		}
		if (getDittaUtilizzatrice_PA() != null) {
			_hashCode += getDittaUtilizzatrice_PA().hashCode();
		}
		if (getDittaUtilizzatrice_SedeLavoro_CAP() != null) {
			_hashCode += getDittaUtilizzatrice_SedeLavoro_CAP().hashCode();
		}
		if (getDittaUtilizzatrice_SedeLavoro_ComuneNazione() != null) {
			_hashCode += getDittaUtilizzatrice_SedeLavoro_ComuneNazione().hashCode();
		}
		if (getDittaUtilizzatrice_SedeLavoro_Indirizzo() != null) {
			_hashCode += getDittaUtilizzatrice_SedeLavoro_Indirizzo().hashCode();
		}
		if (getDittaUtilizzatrice_Settore() != null) {
			_hashCode += getDittaUtilizzatrice_Settore().hashCode();
		}
		if (getInvio_CodiceComunicazione() != null) {
			_hashCode += getInvio_CodiceComunicazione().hashCode();
		}
		if (getInvio_CodiceComunicazionePrecedente() != null) {
			_hashCode += getInvio_CodiceComunicazionePrecedente().hashCode();
		}
		if (getLavoratore_Cittadinanza() != null) {
			_hashCode += getLavoratore_Cittadinanza().hashCode();
		}
		if (getLavoratore_CodiceFiscale() != null) {
			_hashCode += getLavoratore_CodiceFiscale().hashCode();
		}
		if (getLavoratore_Cognome() != null) {
			_hashCode += getLavoratore_Cognome().hashCode();
		}
		if (getLavoratore_Nascita_ComuneNazione() != null) {
			_hashCode += getLavoratore_Nascita_ComuneNazione().hashCode();
		}
		if (getLavoratore_Nascita_Data() != null) {
			_hashCode += getLavoratore_Nascita_Data().hashCode();
		}
		if (getLavoratore_Nome() != null) {
			_hashCode += getLavoratore_Nome().hashCode();
		}
		if (getLavoratore_Sesso() != null) {
			_hashCode += getLavoratore_Sesso().hashCode();
		}
		if (getRapportoAgenziaSomm_CessazioneData() != null) {
			_hashCode += getRapportoAgenziaSomm_CessazioneData().hashCode();
		}
		if (getRapportoAgenziaSomm_CodiceEntePrevidenziale() != null) {
			_hashCode += getRapportoAgenziaSomm_CodiceEntePrevidenziale().hashCode();
		}
		if (getRapportoAgenziaSomm_NMatricola() != null) {
			_hashCode += getRapportoAgenziaSomm_NMatricola().hashCode();
		}
		if (getRapportoAgenziaSomm_NAgenziaSomm() != null) {
			_hashCode += getRapportoAgenziaSomm_NAgenziaSomm().hashCode();
		}
		if (getRapportoAgenziaSomm_IndennitaDisponibilita() != null) {
			_hashCode += getRapportoAgenziaSomm_IndennitaDisponibilita().hashCode();
		}
		if (getRapportoAgenziaSomm_DataFine() != null) {
			_hashCode += getRapportoAgenziaSomm_DataFine().hashCode();
		}
		if (getRapportoAgenziaSomm_DataInizio() != null) {
			_hashCode += getRapportoAgenziaSomm_DataInizio().hashCode();
		}
		if (getRapportoAgenziaSomm_Proroga_DataFine() != null) {
			_hashCode += getRapportoAgenziaSomm_Proroga_DataFine().hashCode();
		}
		if (getRapportoAgenziaSomm_TipologiaContrattuale() != null) {
			_hashCode += getRapportoAgenziaSomm_TipologiaContrattuale().hashCode();
		}
		if (getRapportoDittaUtilizzatrice_CCNL() != null) {
			_hashCode += getRapportoDittaUtilizzatrice_CCNL().hashCode();
		}
		if (getRapportoDittaUtilizzatrice_CodiceAgevolazioni() != null) {
			_hashCode += getRapportoDittaUtilizzatrice_CodiceAgevolazioni().hashCode();
		}
		if (getRapportoDittaUtilizzatrice_CodiceTrasformazione() != null) {
			_hashCode += getRapportoDittaUtilizzatrice_CodiceTrasformazione().hashCode();
		}
		if (getRapportoDittaUtilizzatrice_DataCessazione() != null) {
			_hashCode += getRapportoDittaUtilizzatrice_DataCessazione().hashCode();
		}
		if (getRapportoDittaUtilizzatrice_DataFineMissione() != null) {
			_hashCode += getRapportoDittaUtilizzatrice_DataFineMissione().hashCode();
		}
		if (getRapportoDittaUtilizzatrice_DataFineProroga() != null) {
			_hashCode += getRapportoDittaUtilizzatrice_DataFineProroga().hashCode();
		}
		if (getRapportoDittaUtilizzatrice_DataInizioMissione() != null) {
			_hashCode += getRapportoDittaUtilizzatrice_DataInizioMissione().hashCode();
		}
		if (getRapportoDittaUtilizzatrice_DataTrasformazione() != null) {
			_hashCode += getRapportoDittaUtilizzatrice_DataTrasformazione().hashCode();
		}
		if (getRapportoDittaUtilizzatrice_LavoroInAgricoltura() != null) {
			_hashCode += getRapportoDittaUtilizzatrice_LavoroInAgricoltura().hashCode();
		}
		if (getRapportoDittaUtilizzatrice_TipoOrario() != null) {
			_hashCode += getRapportoDittaUtilizzatrice_TipoOrario().hashCode();
		}
		if (getRapportoDittaUtilizzatrice_TipoOrario_OreSett() != null) {
			_hashCode += getRapportoDittaUtilizzatrice_TipoOrario_OreSett().hashCode();
		}
		if (getRapportoAgenziaDittaUtilizzatrice_NContrattoSomm() != null) {
			_hashCode += getRapportoAgenziaDittaUtilizzatrice_NContrattoSomm().hashCode();
		}
		if (getSYS_DATARICEZIONE() != null) {
			_hashCode += getSYS_DATARICEZIONE().hashCode();
		}
		if (getSYS_DATARIFERIMENTO() != null) {
			_hashCode += getSYS_DATARIFERIMENTO().hashCode();
		}
		if (getSYS_TIPOMODULO() != null) {
			_hashCode += getSYS_TIPOMODULO().hashCode();
		}
		_hashCode += getRowOrder();
		__hashCodeCalc = false;
		return _hashCode;
	}

	// Type metadata
	private static org.apache.axis.description.TypeDesc typeDesc = new org.apache.axis.description.TypeDesc(
			UNISOMM_Type.class, true);

	static {
		typeDesc.setXmlType(
				new javax.xml.namespace.QName("http://servizi.lavoro.gov.it/RicercaCO/2.0", "UNISOMM_Type"));
		org.apache.axis.description.AttributeDesc attrField = new org.apache.axis.description.AttributeDesc();
		attrField.setFieldName("rowOrder");
		attrField.setXmlName(new javax.xml.namespace.QName("", "rowOrder"));
		attrField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
		typeDesc.addFieldDesc(attrField);
		org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("agenziaSomministrazione_CodiceFiscale");
		elemField.setXmlName(new javax.xml.namespace.QName("http://servizi.lavoro.gov.it/RicercaCO/2.0",
				"AgenziaSomministrazione_CodiceFiscale"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
		elemField.setNillable(false);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("agenziaSomministrazione_Denominazione");
		elemField.setXmlName(new javax.xml.namespace.QName("http://servizi.lavoro.gov.it/RicercaCO/2.0",
				"AgenziaSomministrazione_Denominazione"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
		elemField.setNillable(true);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("agenziaSomministrazione_SedeLavoro_CAP");
		elemField.setXmlName(new javax.xml.namespace.QName("http://servizi.lavoro.gov.it/RicercaCO/2.0",
				"AgenziaSomministrazione_SedeLavoro_CAP"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
		elemField.setNillable(true);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("agenziaSomministrazione_SedeLavoro_ComuneNazione");
		elemField.setXmlName(new javax.xml.namespace.QName("http://servizi.lavoro.gov.it/RicercaCO/2.0",
				"AgenziaSomministrazione_SedeLavoro_ComuneNazione"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
		elemField.setNillable(true);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("agenziaSomministrazione_SedeLavoro_Indirizzo");
		elemField.setXmlName(new javax.xml.namespace.QName("http://servizi.lavoro.gov.it/RicercaCO/2.0",
				"AgenziaSomministrazione_SedeLavoro_Indirizzo"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
		elemField.setNillable(true);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("dittaUtilizzatrice_CodiceFiscale");
		elemField.setXmlName(new javax.xml.namespace.QName("http://servizi.lavoro.gov.it/RicercaCO/2.0",
				"DittaUtilizzatrice_CodiceFiscale"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
		elemField.setNillable(true);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("dittaUtilizzatrice_Denominazione");
		elemField.setXmlName(new javax.xml.namespace.QName("http://servizi.lavoro.gov.it/RicercaCO/2.0",
				"DittaUtilizzatrice_Denominazione"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
		elemField.setNillable(true);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("dittaUtilizzatrice_PA");
		elemField.setXmlName(
				new javax.xml.namespace.QName("http://servizi.lavoro.gov.it/RicercaCO/2.0", "DittaUtilizzatrice_PA"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
		elemField.setNillable(true);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("dittaUtilizzatrice_SedeLavoro_CAP");
		elemField.setXmlName(new javax.xml.namespace.QName("http://servizi.lavoro.gov.it/RicercaCO/2.0",
				"DittaUtilizzatrice_SedeLavoro_CAP"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
		elemField.setNillable(true);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("dittaUtilizzatrice_SedeLavoro_ComuneNazione");
		elemField.setXmlName(new javax.xml.namespace.QName("http://servizi.lavoro.gov.it/RicercaCO/2.0",
				"DittaUtilizzatrice_SedeLavoro_ComuneNazione"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
		elemField.setNillable(true);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("dittaUtilizzatrice_SedeLavoro_Indirizzo");
		elemField.setXmlName(new javax.xml.namespace.QName("http://servizi.lavoro.gov.it/RicercaCO/2.0",
				"DittaUtilizzatrice_SedeLavoro_Indirizzo"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
		elemField.setNillable(true);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("dittaUtilizzatrice_Settore");
		elemField.setXmlName(new javax.xml.namespace.QName("http://servizi.lavoro.gov.it/RicercaCO/2.0",
				"DittaUtilizzatrice_Settore"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
		elemField.setNillable(true);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("invio_CodiceComunicazione");
		elemField.setXmlName(new javax.xml.namespace.QName("http://servizi.lavoro.gov.it/RicercaCO/2.0",
				"Invio_CodiceComunicazione"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
		elemField.setNillable(false);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("invio_CodiceComunicazionePrecedente");
		elemField.setXmlName(new javax.xml.namespace.QName("http://servizi.lavoro.gov.it/RicercaCO/2.0",
				"Invio_CodiceComunicazionePrecedente"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
		elemField.setNillable(true);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("lavoratore_Cittadinanza");
		elemField.setXmlName(
				new javax.xml.namespace.QName("http://servizi.lavoro.gov.it/RicercaCO/2.0", "Lavoratore_Cittadinanza"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
		elemField.setNillable(true);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("lavoratore_CodiceFiscale");
		elemField.setXmlName(new javax.xml.namespace.QName("http://servizi.lavoro.gov.it/RicercaCO/2.0",
				"Lavoratore_CodiceFiscale"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
		elemField.setNillable(false);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("lavoratore_Cognome");
		elemField.setXmlName(
				new javax.xml.namespace.QName("http://servizi.lavoro.gov.it/RicercaCO/2.0", "Lavoratore_Cognome"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
		elemField.setNillable(false);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("lavoratore_Nascita_ComuneNazione");
		elemField.setXmlName(new javax.xml.namespace.QName("http://servizi.lavoro.gov.it/RicercaCO/2.0",
				"Lavoratore_Nascita_ComuneNazione"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
		elemField.setNillable(true);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("lavoratore_Nascita_Data");
		elemField.setXmlName(
				new javax.xml.namespace.QName("http://servizi.lavoro.gov.it/RicercaCO/2.0", "Lavoratore_Nascita_Data"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
		elemField.setNillable(true);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("lavoratore_Nome");
		elemField.setXmlName(
				new javax.xml.namespace.QName("http://servizi.lavoro.gov.it/RicercaCO/2.0", "Lavoratore_Nome"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
		elemField.setNillable(false);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("lavoratore_Sesso");
		elemField.setXmlName(
				new javax.xml.namespace.QName("http://servizi.lavoro.gov.it/RicercaCO/2.0", "Lavoratore_Sesso"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
		elemField.setNillable(false);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("rapportoAgenziaSomm_CessazioneData");
		elemField.setXmlName(new javax.xml.namespace.QName("http://servizi.lavoro.gov.it/RicercaCO/2.0",
				"RapportoAgenziaSomm_CessazioneData"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
		elemField.setNillable(true);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("rapportoAgenziaSomm_CodiceEntePrevidenziale");
		elemField.setXmlName(new javax.xml.namespace.QName("http://servizi.lavoro.gov.it/RicercaCO/2.0",
				"RapportoAgenziaSomm_CodiceEntePrevidenziale"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
		elemField.setNillable(true);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("rapportoAgenziaSomm_NMatricola");
		elemField.setXmlName(new javax.xml.namespace.QName("http://servizi.lavoro.gov.it/RicercaCO/2.0",
				"RapportoAgenziaSomm_NMatricola"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
		elemField.setNillable(true);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("rapportoAgenziaSomm_NAgenziaSomm");
		elemField.setXmlName(new javax.xml.namespace.QName("http://servizi.lavoro.gov.it/RicercaCO/2.0",
				"RapportoAgenziaSomm_NAgenziaSomm"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
		elemField.setNillable(true);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("rapportoAgenziaSomm_IndennitaDisponibilita");
		elemField.setXmlName(new javax.xml.namespace.QName("http://servizi.lavoro.gov.it/RicercaCO/2.0",
				"RapportoAgenziaSomm_IndennitaDisponibilita"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
		elemField.setNillable(true);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("rapportoAgenziaSomm_DataFine");
		elemField.setXmlName(new javax.xml.namespace.QName("http://servizi.lavoro.gov.it/RicercaCO/2.0",
				"RapportoAgenziaSomm_DataFine"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
		elemField.setNillable(true);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("rapportoAgenziaSomm_DataInizio");
		elemField.setXmlName(new javax.xml.namespace.QName("http://servizi.lavoro.gov.it/RicercaCO/2.0",
				"RapportoAgenziaSomm_DataInizio"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
		elemField.setNillable(false);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("rapportoAgenziaSomm_Proroga_DataFine");
		elemField.setXmlName(new javax.xml.namespace.QName("http://servizi.lavoro.gov.it/RicercaCO/2.0",
				"RapportoAgenziaSomm_Proroga_DataFine"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
		elemField.setNillable(true);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("rapportoAgenziaSomm_TipologiaContrattuale");
		elemField.setXmlName(new javax.xml.namespace.QName("http://servizi.lavoro.gov.it/RicercaCO/2.0",
				"RapportoAgenziaSomm_TipologiaContrattuale"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
		elemField.setNillable(true);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("rapportoDittaUtilizzatrice_CCNL");
		elemField.setXmlName(new javax.xml.namespace.QName("http://servizi.lavoro.gov.it/RicercaCO/2.0",
				"RapportoDittaUtilizzatrice_CCNL"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
		elemField.setNillable(true);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("rapportoDittaUtilizzatrice_CodiceAgevolazioni");
		elemField.setXmlName(new javax.xml.namespace.QName("http://servizi.lavoro.gov.it/RicercaCO/2.0",
				"RapportoDittaUtilizzatrice_CodiceAgevolazioni"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
		elemField.setNillable(true);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("rapportoDittaUtilizzatrice_CodiceTrasformazione");
		elemField.setXmlName(new javax.xml.namespace.QName("http://servizi.lavoro.gov.it/RicercaCO/2.0",
				"RapportoDittaUtilizzatrice_CodiceTrasformazione"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
		elemField.setNillable(true);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("rapportoDittaUtilizzatrice_DataCessazione");
		elemField.setXmlName(new javax.xml.namespace.QName("http://servizi.lavoro.gov.it/RicercaCO/2.0",
				"RapportoDittaUtilizzatrice_DataCessazione"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
		elemField.setNillable(true);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("rapportoDittaUtilizzatrice_DataFineMissione");
		elemField.setXmlName(new javax.xml.namespace.QName("http://servizi.lavoro.gov.it/RicercaCO/2.0",
				"RapportoDittaUtilizzatrice_DataFineMissione"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
		elemField.setNillable(true);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("rapportoDittaUtilizzatrice_DataFineProroga");
		elemField.setXmlName(new javax.xml.namespace.QName("http://servizi.lavoro.gov.it/RicercaCO/2.0",
				"RapportoDittaUtilizzatrice_DataFineProroga"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
		elemField.setNillable(true);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("rapportoDittaUtilizzatrice_DataInizioMissione");
		elemField.setXmlName(new javax.xml.namespace.QName("http://servizi.lavoro.gov.it/RicercaCO/2.0",
				"RapportoDittaUtilizzatrice_DataInizioMissione"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
		elemField.setNillable(true);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("rapportoDittaUtilizzatrice_DataTrasformazione");
		elemField.setXmlName(new javax.xml.namespace.QName("http://servizi.lavoro.gov.it/RicercaCO/2.0",
				"RapportoDittaUtilizzatrice_DataTrasformazione"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
		elemField.setNillable(true);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("rapportoDittaUtilizzatrice_LavoroInAgricoltura");
		elemField.setXmlName(new javax.xml.namespace.QName("http://servizi.lavoro.gov.it/RicercaCO/2.0",
				"RapportoDittaUtilizzatrice_LavoroInAgricoltura"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
		elemField.setNillable(true);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("rapportoDittaUtilizzatrice_TipoOrario");
		elemField.setXmlName(new javax.xml.namespace.QName("http://servizi.lavoro.gov.it/RicercaCO/2.0",
				"RapportoDittaUtilizzatrice_TipoOrario"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
		elemField.setNillable(true);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("rapportoDittaUtilizzatrice_TipoOrario_OreSett");
		elemField.setXmlName(new javax.xml.namespace.QName("http://servizi.lavoro.gov.it/RicercaCO/2.0",
				"RapportoDittaUtilizzatrice_TipoOrario_OreSett"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
		elemField.setNillable(true);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("rapportoAgenziaDittaUtilizzatrice_NContrattoSomm");
		elemField.setXmlName(new javax.xml.namespace.QName("http://servizi.lavoro.gov.it/RicercaCO/2.0",
				"RapportoAgenziaDittaUtilizzatrice_NContrattoSomm"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
		elemField.setNillable(true);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("SYS_DATARICEZIONE");
		elemField.setXmlName(
				new javax.xml.namespace.QName("http://servizi.lavoro.gov.it/RicercaCO/2.0", "SYS_DATARICEZIONE"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "dateTime"));
		elemField.setNillable(false);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("SYS_DATARIFERIMENTO");
		elemField.setXmlName(
				new javax.xml.namespace.QName("http://servizi.lavoro.gov.it/RicercaCO/2.0", "SYS_DATARIFERIMENTO"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "dateTime"));
		elemField.setNillable(false);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("SYS_TIPOMODULO");
		elemField.setXmlName(
				new javax.xml.namespace.QName("http://servizi.lavoro.gov.it/RicercaCO/2.0", "SYS_TIPOMODULO"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
		elemField.setNillable(false);
		typeDesc.addFieldDesc(elemField);
	}

	/**
	 * Return type metadata object
	 */
	public static org.apache.axis.description.TypeDesc getTypeDesc() {
		return typeDesc;
	}

	/**
	 * Get Custom Serializer
	 */
	public static org.apache.axis.encoding.Serializer getSerializer(java.lang.String mechType,
			java.lang.Class _javaType, javax.xml.namespace.QName _xmlType) {
		return new org.apache.axis.encoding.ser.BeanSerializer(_javaType, _xmlType, typeDesc);
	}

	/**
	 * Get Custom Deserializer
	 */
	public static org.apache.axis.encoding.Deserializer getDeserializer(java.lang.String mechType,
			java.lang.Class _javaType, javax.xml.namespace.QName _xmlType) {
		return new org.apache.axis.encoding.ser.BeanDeserializer(_javaType, _xmlType, typeDesc);
	}

}
