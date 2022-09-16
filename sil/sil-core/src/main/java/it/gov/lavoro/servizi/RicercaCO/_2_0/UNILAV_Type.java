/**
 * UNILAV_Type.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package it.gov.lavoro.servizi.RicercaCO._2_0;

public class UNILAV_Type implements java.io.Serializable {
	private java.lang.String datoreLavoro_CodiceFiscale;

	private java.lang.String datoreLavoro_Denominazione;

	private java.lang.String datoreLavoro_PA;

	private java.lang.String datoreLavoro_SedeLavoro_CAP;

	private java.lang.String datoreLavoro_SedeLavoro_ComuneNazione;

	private java.lang.String datoreLavoro_SedeLavoro_Indirizzo;

	private java.lang.String datoreLavoro_Settore;

	private java.lang.String datoreLavoroDistaccatario_CodiceFiscale;

	private java.lang.String invio_CodiceComunicazione;

	private java.lang.String invio_CodiceComunicazionePrecedente;

	private java.lang.String invio_ForzaMaggiore;

	private java.lang.String lavoratore_Cittadinanza;

	private java.lang.String lavoratore_CodiceFiscale;

	private java.lang.String lavoratore_Cognome;

	private java.lang.String lavoratore_Nascita_ComuneNazione;

	private java.lang.String lavoratore_Nascita_Data;

	private java.lang.String lavoratore_Nome;

	private java.lang.String lavoratore_Sesso;

	private java.lang.String lavoratoreCoobbligato_CodiceFiscale;

	private java.lang.String rapporto_Agevolazioni;

	private java.lang.String rapporto_CCNL;

	private java.lang.String rapporto_Cessazione_CodiceCausa;

	private java.lang.String rapporto_Cessazione_Data;

	private java.lang.String rapporto_DataFine;

	private java.lang.String rapporto_DataInizio;

	private java.lang.String rapporto_EntePrevidenziale;

	private java.lang.String rapporto_LavoroAgricoltura;

	private java.lang.String rapporto_OreSettimanaliMedie;

	private java.lang.String rapporto_Proroga_DataFine;

	private java.lang.String rapporto_SocioLavoratore;

	private java.lang.String rapporto_TipologiaContrattuale;

	private java.lang.String rapporto_TipoOrario;

	private java.lang.String rapporto_Trasformazione_CodiceTrasformazione;

	private java.lang.String rapporto_Trasformazione_Data;

	private java.util.Calendar SYS_DATARICEZIONE;

	private java.util.Calendar SYS_DATARIFERIMENTO;

	private java.lang.String SYS_TIPOMODULO;

	private int rowOrder; // attribute

	public UNILAV_Type() {
	}

	public UNILAV_Type(java.lang.String datoreLavoro_CodiceFiscale, java.lang.String datoreLavoro_Denominazione,
			java.lang.String datoreLavoro_PA, java.lang.String datoreLavoro_SedeLavoro_CAP,
			java.lang.String datoreLavoro_SedeLavoro_ComuneNazione, java.lang.String datoreLavoro_SedeLavoro_Indirizzo,
			java.lang.String datoreLavoro_Settore, java.lang.String datoreLavoroDistaccatario_CodiceFiscale,
			java.lang.String invio_CodiceComunicazione, java.lang.String invio_CodiceComunicazionePrecedente,
			java.lang.String invio_ForzaMaggiore, java.lang.String lavoratore_Cittadinanza,
			java.lang.String lavoratore_CodiceFiscale, java.lang.String lavoratore_Cognome,
			java.lang.String lavoratore_Nascita_ComuneNazione, java.lang.String lavoratore_Nascita_Data,
			java.lang.String lavoratore_Nome, java.lang.String lavoratore_Sesso,
			java.lang.String lavoratoreCoobbligato_CodiceFiscale, java.lang.String rapporto_Agevolazioni,
			java.lang.String rapporto_CCNL, java.lang.String rapporto_Cessazione_CodiceCausa,
			java.lang.String rapporto_Cessazione_Data, java.lang.String rapporto_DataFine,
			java.lang.String rapporto_DataInizio, java.lang.String rapporto_EntePrevidenziale,
			java.lang.String rapporto_LavoroAgricoltura, java.lang.String rapporto_OreSettimanaliMedie,
			java.lang.String rapporto_Proroga_DataFine, java.lang.String rapporto_SocioLavoratore,
			java.lang.String rapporto_TipologiaContrattuale, java.lang.String rapporto_TipoOrario,
			java.lang.String rapporto_Trasformazione_CodiceTrasformazione,
			java.lang.String rapporto_Trasformazione_Data, java.util.Calendar SYS_DATARICEZIONE,
			java.util.Calendar SYS_DATARIFERIMENTO, java.lang.String SYS_TIPOMODULO, int rowOrder) {
		this.datoreLavoro_CodiceFiscale = datoreLavoro_CodiceFiscale;
		this.datoreLavoro_Denominazione = datoreLavoro_Denominazione;
		this.datoreLavoro_PA = datoreLavoro_PA;
		this.datoreLavoro_SedeLavoro_CAP = datoreLavoro_SedeLavoro_CAP;
		this.datoreLavoro_SedeLavoro_ComuneNazione = datoreLavoro_SedeLavoro_ComuneNazione;
		this.datoreLavoro_SedeLavoro_Indirizzo = datoreLavoro_SedeLavoro_Indirizzo;
		this.datoreLavoro_Settore = datoreLavoro_Settore;
		this.datoreLavoroDistaccatario_CodiceFiscale = datoreLavoroDistaccatario_CodiceFiscale;
		this.invio_CodiceComunicazione = invio_CodiceComunicazione;
		this.invio_CodiceComunicazionePrecedente = invio_CodiceComunicazionePrecedente;
		this.invio_ForzaMaggiore = invio_ForzaMaggiore;
		this.lavoratore_Cittadinanza = lavoratore_Cittadinanza;
		this.lavoratore_CodiceFiscale = lavoratore_CodiceFiscale;
		this.lavoratore_Cognome = lavoratore_Cognome;
		this.lavoratore_Nascita_ComuneNazione = lavoratore_Nascita_ComuneNazione;
		this.lavoratore_Nascita_Data = lavoratore_Nascita_Data;
		this.lavoratore_Nome = lavoratore_Nome;
		this.lavoratore_Sesso = lavoratore_Sesso;
		this.lavoratoreCoobbligato_CodiceFiscale = lavoratoreCoobbligato_CodiceFiscale;
		this.rapporto_Agevolazioni = rapporto_Agevolazioni;
		this.rapporto_CCNL = rapporto_CCNL;
		this.rapporto_Cessazione_CodiceCausa = rapporto_Cessazione_CodiceCausa;
		this.rapporto_Cessazione_Data = rapporto_Cessazione_Data;
		this.rapporto_DataFine = rapporto_DataFine;
		this.rapporto_DataInizio = rapporto_DataInizio;
		this.rapporto_EntePrevidenziale = rapporto_EntePrevidenziale;
		this.rapporto_LavoroAgricoltura = rapporto_LavoroAgricoltura;
		this.rapporto_OreSettimanaliMedie = rapporto_OreSettimanaliMedie;
		this.rapporto_Proroga_DataFine = rapporto_Proroga_DataFine;
		this.rapporto_SocioLavoratore = rapporto_SocioLavoratore;
		this.rapporto_TipologiaContrattuale = rapporto_TipologiaContrattuale;
		this.rapporto_TipoOrario = rapporto_TipoOrario;
		this.rapporto_Trasformazione_CodiceTrasformazione = rapporto_Trasformazione_CodiceTrasformazione;
		this.rapporto_Trasformazione_Data = rapporto_Trasformazione_Data;
		this.SYS_DATARICEZIONE = SYS_DATARICEZIONE;
		this.SYS_DATARIFERIMENTO = SYS_DATARIFERIMENTO;
		this.SYS_TIPOMODULO = SYS_TIPOMODULO;
		this.rowOrder = rowOrder;
	}

	/**
	 * Gets the datoreLavoro_CodiceFiscale value for this UNILAV_Type.
	 * 
	 * @return datoreLavoro_CodiceFiscale
	 */
	public java.lang.String getDatoreLavoro_CodiceFiscale() {
		return datoreLavoro_CodiceFiscale;
	}

	/**
	 * Sets the datoreLavoro_CodiceFiscale value for this UNILAV_Type.
	 * 
	 * @param datoreLavoro_CodiceFiscale
	 */
	public void setDatoreLavoro_CodiceFiscale(java.lang.String datoreLavoro_CodiceFiscale) {
		this.datoreLavoro_CodiceFiscale = datoreLavoro_CodiceFiscale;
	}

	/**
	 * Gets the datoreLavoro_Denominazione value for this UNILAV_Type.
	 * 
	 * @return datoreLavoro_Denominazione
	 */
	public java.lang.String getDatoreLavoro_Denominazione() {
		return datoreLavoro_Denominazione;
	}

	/**
	 * Sets the datoreLavoro_Denominazione value for this UNILAV_Type.
	 * 
	 * @param datoreLavoro_Denominazione
	 */
	public void setDatoreLavoro_Denominazione(java.lang.String datoreLavoro_Denominazione) {
		this.datoreLavoro_Denominazione = datoreLavoro_Denominazione;
	}

	/**
	 * Gets the datoreLavoro_PA value for this UNILAV_Type.
	 * 
	 * @return datoreLavoro_PA
	 */
	public java.lang.String getDatoreLavoro_PA() {
		return datoreLavoro_PA;
	}

	/**
	 * Sets the datoreLavoro_PA value for this UNILAV_Type.
	 * 
	 * @param datoreLavoro_PA
	 */
	public void setDatoreLavoro_PA(java.lang.String datoreLavoro_PA) {
		this.datoreLavoro_PA = datoreLavoro_PA;
	}

	/**
	 * Gets the datoreLavoro_SedeLavoro_CAP value for this UNILAV_Type.
	 * 
	 * @return datoreLavoro_SedeLavoro_CAP
	 */
	public java.lang.String getDatoreLavoro_SedeLavoro_CAP() {
		return datoreLavoro_SedeLavoro_CAP;
	}

	/**
	 * Sets the datoreLavoro_SedeLavoro_CAP value for this UNILAV_Type.
	 * 
	 * @param datoreLavoro_SedeLavoro_CAP
	 */
	public void setDatoreLavoro_SedeLavoro_CAP(java.lang.String datoreLavoro_SedeLavoro_CAP) {
		this.datoreLavoro_SedeLavoro_CAP = datoreLavoro_SedeLavoro_CAP;
	}

	/**
	 * Gets the datoreLavoro_SedeLavoro_ComuneNazione value for this UNILAV_Type.
	 * 
	 * @return datoreLavoro_SedeLavoro_ComuneNazione
	 */
	public java.lang.String getDatoreLavoro_SedeLavoro_ComuneNazione() {
		return datoreLavoro_SedeLavoro_ComuneNazione;
	}

	/**
	 * Sets the datoreLavoro_SedeLavoro_ComuneNazione value for this UNILAV_Type.
	 * 
	 * @param datoreLavoro_SedeLavoro_ComuneNazione
	 */
	public void setDatoreLavoro_SedeLavoro_ComuneNazione(java.lang.String datoreLavoro_SedeLavoro_ComuneNazione) {
		this.datoreLavoro_SedeLavoro_ComuneNazione = datoreLavoro_SedeLavoro_ComuneNazione;
	}

	/**
	 * Gets the datoreLavoro_SedeLavoro_Indirizzo value for this UNILAV_Type.
	 * 
	 * @return datoreLavoro_SedeLavoro_Indirizzo
	 */
	public java.lang.String getDatoreLavoro_SedeLavoro_Indirizzo() {
		return datoreLavoro_SedeLavoro_Indirizzo;
	}

	/**
	 * Sets the datoreLavoro_SedeLavoro_Indirizzo value for this UNILAV_Type.
	 * 
	 * @param datoreLavoro_SedeLavoro_Indirizzo
	 */
	public void setDatoreLavoro_SedeLavoro_Indirizzo(java.lang.String datoreLavoro_SedeLavoro_Indirizzo) {
		this.datoreLavoro_SedeLavoro_Indirizzo = datoreLavoro_SedeLavoro_Indirizzo;
	}

	/**
	 * Gets the datoreLavoro_Settore value for this UNILAV_Type.
	 * 
	 * @return datoreLavoro_Settore
	 */
	public java.lang.String getDatoreLavoro_Settore() {
		return datoreLavoro_Settore;
	}

	/**
	 * Sets the datoreLavoro_Settore value for this UNILAV_Type.
	 * 
	 * @param datoreLavoro_Settore
	 */
	public void setDatoreLavoro_Settore(java.lang.String datoreLavoro_Settore) {
		this.datoreLavoro_Settore = datoreLavoro_Settore;
	}

	/**
	 * Gets the datoreLavoroDistaccatario_CodiceFiscale value for this UNILAV_Type.
	 * 
	 * @return datoreLavoroDistaccatario_CodiceFiscale
	 */
	public java.lang.String getDatoreLavoroDistaccatario_CodiceFiscale() {
		return datoreLavoroDistaccatario_CodiceFiscale;
	}

	/**
	 * Sets the datoreLavoroDistaccatario_CodiceFiscale value for this UNILAV_Type.
	 * 
	 * @param datoreLavoroDistaccatario_CodiceFiscale
	 */
	public void setDatoreLavoroDistaccatario_CodiceFiscale(java.lang.String datoreLavoroDistaccatario_CodiceFiscale) {
		this.datoreLavoroDistaccatario_CodiceFiscale = datoreLavoroDistaccatario_CodiceFiscale;
	}

	/**
	 * Gets the invio_CodiceComunicazione value for this UNILAV_Type.
	 * 
	 * @return invio_CodiceComunicazione
	 */
	public java.lang.String getInvio_CodiceComunicazione() {
		return invio_CodiceComunicazione;
	}

	/**
	 * Sets the invio_CodiceComunicazione value for this UNILAV_Type.
	 * 
	 * @param invio_CodiceComunicazione
	 */
	public void setInvio_CodiceComunicazione(java.lang.String invio_CodiceComunicazione) {
		this.invio_CodiceComunicazione = invio_CodiceComunicazione;
	}

	/**
	 * Gets the invio_CodiceComunicazionePrecedente value for this UNILAV_Type.
	 * 
	 * @return invio_CodiceComunicazionePrecedente
	 */
	public java.lang.String getInvio_CodiceComunicazionePrecedente() {
		return invio_CodiceComunicazionePrecedente;
	}

	/**
	 * Sets the invio_CodiceComunicazionePrecedente value for this UNILAV_Type.
	 * 
	 * @param invio_CodiceComunicazionePrecedente
	 */
	public void setInvio_CodiceComunicazionePrecedente(java.lang.String invio_CodiceComunicazionePrecedente) {
		this.invio_CodiceComunicazionePrecedente = invio_CodiceComunicazionePrecedente;
	}

	/**
	 * Gets the invio_ForzaMaggiore value for this UNILAV_Type.
	 * 
	 * @return invio_ForzaMaggiore
	 */
	public java.lang.String getInvio_ForzaMaggiore() {
		return invio_ForzaMaggiore;
	}

	/**
	 * Sets the invio_ForzaMaggiore value for this UNILAV_Type.
	 * 
	 * @param invio_ForzaMaggiore
	 */
	public void setInvio_ForzaMaggiore(java.lang.String invio_ForzaMaggiore) {
		this.invio_ForzaMaggiore = invio_ForzaMaggiore;
	}

	/**
	 * Gets the lavoratore_Cittadinanza value for this UNILAV_Type.
	 * 
	 * @return lavoratore_Cittadinanza
	 */
	public java.lang.String getLavoratore_Cittadinanza() {
		return lavoratore_Cittadinanza;
	}

	/**
	 * Sets the lavoratore_Cittadinanza value for this UNILAV_Type.
	 * 
	 * @param lavoratore_Cittadinanza
	 */
	public void setLavoratore_Cittadinanza(java.lang.String lavoratore_Cittadinanza) {
		this.lavoratore_Cittadinanza = lavoratore_Cittadinanza;
	}

	/**
	 * Gets the lavoratore_CodiceFiscale value for this UNILAV_Type.
	 * 
	 * @return lavoratore_CodiceFiscale
	 */
	public java.lang.String getLavoratore_CodiceFiscale() {
		return lavoratore_CodiceFiscale;
	}

	/**
	 * Sets the lavoratore_CodiceFiscale value for this UNILAV_Type.
	 * 
	 * @param lavoratore_CodiceFiscale
	 */
	public void setLavoratore_CodiceFiscale(java.lang.String lavoratore_CodiceFiscale) {
		this.lavoratore_CodiceFiscale = lavoratore_CodiceFiscale;
	}

	/**
	 * Gets the lavoratore_Cognome value for this UNILAV_Type.
	 * 
	 * @return lavoratore_Cognome
	 */
	public java.lang.String getLavoratore_Cognome() {
		return lavoratore_Cognome;
	}

	/**
	 * Sets the lavoratore_Cognome value for this UNILAV_Type.
	 * 
	 * @param lavoratore_Cognome
	 */
	public void setLavoratore_Cognome(java.lang.String lavoratore_Cognome) {
		this.lavoratore_Cognome = lavoratore_Cognome;
	}

	/**
	 * Gets the lavoratore_Nascita_ComuneNazione value for this UNILAV_Type.
	 * 
	 * @return lavoratore_Nascita_ComuneNazione
	 */
	public java.lang.String getLavoratore_Nascita_ComuneNazione() {
		return lavoratore_Nascita_ComuneNazione;
	}

	/**
	 * Sets the lavoratore_Nascita_ComuneNazione value for this UNILAV_Type.
	 * 
	 * @param lavoratore_Nascita_ComuneNazione
	 */
	public void setLavoratore_Nascita_ComuneNazione(java.lang.String lavoratore_Nascita_ComuneNazione) {
		this.lavoratore_Nascita_ComuneNazione = lavoratore_Nascita_ComuneNazione;
	}

	/**
	 * Gets the lavoratore_Nascita_Data value for this UNILAV_Type.
	 * 
	 * @return lavoratore_Nascita_Data
	 */
	public java.lang.String getLavoratore_Nascita_Data() {
		return lavoratore_Nascita_Data;
	}

	/**
	 * Sets the lavoratore_Nascita_Data value for this UNILAV_Type.
	 * 
	 * @param lavoratore_Nascita_Data
	 */
	public void setLavoratore_Nascita_Data(java.lang.String lavoratore_Nascita_Data) {
		this.lavoratore_Nascita_Data = lavoratore_Nascita_Data;
	}

	/**
	 * Gets the lavoratore_Nome value for this UNILAV_Type.
	 * 
	 * @return lavoratore_Nome
	 */
	public java.lang.String getLavoratore_Nome() {
		return lavoratore_Nome;
	}

	/**
	 * Sets the lavoratore_Nome value for this UNILAV_Type.
	 * 
	 * @param lavoratore_Nome
	 */
	public void setLavoratore_Nome(java.lang.String lavoratore_Nome) {
		this.lavoratore_Nome = lavoratore_Nome;
	}

	/**
	 * Gets the lavoratore_Sesso value for this UNILAV_Type.
	 * 
	 * @return lavoratore_Sesso
	 */
	public java.lang.String getLavoratore_Sesso() {
		return lavoratore_Sesso;
	}

	/**
	 * Sets the lavoratore_Sesso value for this UNILAV_Type.
	 * 
	 * @param lavoratore_Sesso
	 */
	public void setLavoratore_Sesso(java.lang.String lavoratore_Sesso) {
		this.lavoratore_Sesso = lavoratore_Sesso;
	}

	/**
	 * Gets the lavoratoreCoobbligato_CodiceFiscale value for this UNILAV_Type.
	 * 
	 * @return lavoratoreCoobbligato_CodiceFiscale
	 */
	public java.lang.String getLavoratoreCoobbligato_CodiceFiscale() {
		return lavoratoreCoobbligato_CodiceFiscale;
	}

	/**
	 * Sets the lavoratoreCoobbligato_CodiceFiscale value for this UNILAV_Type.
	 * 
	 * @param lavoratoreCoobbligato_CodiceFiscale
	 */
	public void setLavoratoreCoobbligato_CodiceFiscale(java.lang.String lavoratoreCoobbligato_CodiceFiscale) {
		this.lavoratoreCoobbligato_CodiceFiscale = lavoratoreCoobbligato_CodiceFiscale;
	}

	/**
	 * Gets the rapporto_Agevolazioni value for this UNILAV_Type.
	 * 
	 * @return rapporto_Agevolazioni
	 */
	public java.lang.String getRapporto_Agevolazioni() {
		return rapporto_Agevolazioni;
	}

	/**
	 * Sets the rapporto_Agevolazioni value for this UNILAV_Type.
	 * 
	 * @param rapporto_Agevolazioni
	 */
	public void setRapporto_Agevolazioni(java.lang.String rapporto_Agevolazioni) {
		this.rapporto_Agevolazioni = rapporto_Agevolazioni;
	}

	/**
	 * Gets the rapporto_CCNL value for this UNILAV_Type.
	 * 
	 * @return rapporto_CCNL
	 */
	public java.lang.String getRapporto_CCNL() {
		return rapporto_CCNL;
	}

	/**
	 * Sets the rapporto_CCNL value for this UNILAV_Type.
	 * 
	 * @param rapporto_CCNL
	 */
	public void setRapporto_CCNL(java.lang.String rapporto_CCNL) {
		this.rapporto_CCNL = rapporto_CCNL;
	}

	/**
	 * Gets the rapporto_Cessazione_CodiceCausa value for this UNILAV_Type.
	 * 
	 * @return rapporto_Cessazione_CodiceCausa
	 */
	public java.lang.String getRapporto_Cessazione_CodiceCausa() {
		return rapporto_Cessazione_CodiceCausa;
	}

	/**
	 * Sets the rapporto_Cessazione_CodiceCausa value for this UNILAV_Type.
	 * 
	 * @param rapporto_Cessazione_CodiceCausa
	 */
	public void setRapporto_Cessazione_CodiceCausa(java.lang.String rapporto_Cessazione_CodiceCausa) {
		this.rapporto_Cessazione_CodiceCausa = rapporto_Cessazione_CodiceCausa;
	}

	/**
	 * Gets the rapporto_Cessazione_Data value for this UNILAV_Type.
	 * 
	 * @return rapporto_Cessazione_Data
	 */
	public java.lang.String getRapporto_Cessazione_Data() {
		return rapporto_Cessazione_Data;
	}

	/**
	 * Sets the rapporto_Cessazione_Data value for this UNILAV_Type.
	 * 
	 * @param rapporto_Cessazione_Data
	 */
	public void setRapporto_Cessazione_Data(java.lang.String rapporto_Cessazione_Data) {
		this.rapporto_Cessazione_Data = rapporto_Cessazione_Data;
	}

	/**
	 * Gets the rapporto_DataFine value for this UNILAV_Type.
	 * 
	 * @return rapporto_DataFine
	 */
	public java.lang.String getRapporto_DataFine() {
		return rapporto_DataFine;
	}

	/**
	 * Sets the rapporto_DataFine value for this UNILAV_Type.
	 * 
	 * @param rapporto_DataFine
	 */
	public void setRapporto_DataFine(java.lang.String rapporto_DataFine) {
		this.rapporto_DataFine = rapporto_DataFine;
	}

	/**
	 * Gets the rapporto_DataInizio value for this UNILAV_Type.
	 * 
	 * @return rapporto_DataInizio
	 */
	public java.lang.String getRapporto_DataInizio() {
		return rapporto_DataInizio;
	}

	/**
	 * Sets the rapporto_DataInizio value for this UNILAV_Type.
	 * 
	 * @param rapporto_DataInizio
	 */
	public void setRapporto_DataInizio(java.lang.String rapporto_DataInizio) {
		this.rapporto_DataInizio = rapporto_DataInizio;
	}

	/**
	 * Gets the rapporto_EntePrevidenziale value for this UNILAV_Type.
	 * 
	 * @return rapporto_EntePrevidenziale
	 */
	public java.lang.String getRapporto_EntePrevidenziale() {
		return rapporto_EntePrevidenziale;
	}

	/**
	 * Sets the rapporto_EntePrevidenziale value for this UNILAV_Type.
	 * 
	 * @param rapporto_EntePrevidenziale
	 */
	public void setRapporto_EntePrevidenziale(java.lang.String rapporto_EntePrevidenziale) {
		this.rapporto_EntePrevidenziale = rapporto_EntePrevidenziale;
	}

	/**
	 * Gets the rapporto_LavoroAgricoltura value for this UNILAV_Type.
	 * 
	 * @return rapporto_LavoroAgricoltura
	 */
	public java.lang.String getRapporto_LavoroAgricoltura() {
		return rapporto_LavoroAgricoltura;
	}

	/**
	 * Sets the rapporto_LavoroAgricoltura value for this UNILAV_Type.
	 * 
	 * @param rapporto_LavoroAgricoltura
	 */
	public void setRapporto_LavoroAgricoltura(java.lang.String rapporto_LavoroAgricoltura) {
		this.rapporto_LavoroAgricoltura = rapporto_LavoroAgricoltura;
	}

	/**
	 * Gets the rapporto_OreSettimanaliMedie value for this UNILAV_Type.
	 * 
	 * @return rapporto_OreSettimanaliMedie
	 */
	public java.lang.String getRapporto_OreSettimanaliMedie() {
		return rapporto_OreSettimanaliMedie;
	}

	/**
	 * Sets the rapporto_OreSettimanaliMedie value for this UNILAV_Type.
	 * 
	 * @param rapporto_OreSettimanaliMedie
	 */
	public void setRapporto_OreSettimanaliMedie(java.lang.String rapporto_OreSettimanaliMedie) {
		this.rapporto_OreSettimanaliMedie = rapporto_OreSettimanaliMedie;
	}

	/**
	 * Gets the rapporto_Proroga_DataFine value for this UNILAV_Type.
	 * 
	 * @return rapporto_Proroga_DataFine
	 */
	public java.lang.String getRapporto_Proroga_DataFine() {
		return rapporto_Proroga_DataFine;
	}

	/**
	 * Sets the rapporto_Proroga_DataFine value for this UNILAV_Type.
	 * 
	 * @param rapporto_Proroga_DataFine
	 */
	public void setRapporto_Proroga_DataFine(java.lang.String rapporto_Proroga_DataFine) {
		this.rapporto_Proroga_DataFine = rapporto_Proroga_DataFine;
	}

	/**
	 * Gets the rapporto_SocioLavoratore value for this UNILAV_Type.
	 * 
	 * @return rapporto_SocioLavoratore
	 */
	public java.lang.String getRapporto_SocioLavoratore() {
		return rapporto_SocioLavoratore;
	}

	/**
	 * Sets the rapporto_SocioLavoratore value for this UNILAV_Type.
	 * 
	 * @param rapporto_SocioLavoratore
	 */
	public void setRapporto_SocioLavoratore(java.lang.String rapporto_SocioLavoratore) {
		this.rapporto_SocioLavoratore = rapporto_SocioLavoratore;
	}

	/**
	 * Gets the rapporto_TipologiaContrattuale value for this UNILAV_Type.
	 * 
	 * @return rapporto_TipologiaContrattuale
	 */
	public java.lang.String getRapporto_TipologiaContrattuale() {
		return rapporto_TipologiaContrattuale;
	}

	/**
	 * Sets the rapporto_TipologiaContrattuale value for this UNILAV_Type.
	 * 
	 * @param rapporto_TipologiaContrattuale
	 */
	public void setRapporto_TipologiaContrattuale(java.lang.String rapporto_TipologiaContrattuale) {
		this.rapporto_TipologiaContrattuale = rapporto_TipologiaContrattuale;
	}

	/**
	 * Gets the rapporto_TipoOrario value for this UNILAV_Type.
	 * 
	 * @return rapporto_TipoOrario
	 */
	public java.lang.String getRapporto_TipoOrario() {
		return rapporto_TipoOrario;
	}

	/**
	 * Sets the rapporto_TipoOrario value for this UNILAV_Type.
	 * 
	 * @param rapporto_TipoOrario
	 */
	public void setRapporto_TipoOrario(java.lang.String rapporto_TipoOrario) {
		this.rapporto_TipoOrario = rapporto_TipoOrario;
	}

	/**
	 * Gets the rapporto_Trasformazione_CodiceTrasformazione value for this UNILAV_Type.
	 * 
	 * @return rapporto_Trasformazione_CodiceTrasformazione
	 */
	public java.lang.String getRapporto_Trasformazione_CodiceTrasformazione() {
		return rapporto_Trasformazione_CodiceTrasformazione;
	}

	/**
	 * Sets the rapporto_Trasformazione_CodiceTrasformazione value for this UNILAV_Type.
	 * 
	 * @param rapporto_Trasformazione_CodiceTrasformazione
	 */
	public void setRapporto_Trasformazione_CodiceTrasformazione(
			java.lang.String rapporto_Trasformazione_CodiceTrasformazione) {
		this.rapporto_Trasformazione_CodiceTrasformazione = rapporto_Trasformazione_CodiceTrasformazione;
	}

	/**
	 * Gets the rapporto_Trasformazione_Data value for this UNILAV_Type.
	 * 
	 * @return rapporto_Trasformazione_Data
	 */
	public java.lang.String getRapporto_Trasformazione_Data() {
		return rapporto_Trasformazione_Data;
	}

	/**
	 * Sets the rapporto_Trasformazione_Data value for this UNILAV_Type.
	 * 
	 * @param rapporto_Trasformazione_Data
	 */
	public void setRapporto_Trasformazione_Data(java.lang.String rapporto_Trasformazione_Data) {
		this.rapporto_Trasformazione_Data = rapporto_Trasformazione_Data;
	}

	/**
	 * Gets the SYS_DATARICEZIONE value for this UNILAV_Type.
	 * 
	 * @return SYS_DATARICEZIONE
	 */
	public java.util.Calendar getSYS_DATARICEZIONE() {
		return SYS_DATARICEZIONE;
	}

	/**
	 * Sets the SYS_DATARICEZIONE value for this UNILAV_Type.
	 * 
	 * @param SYS_DATARICEZIONE
	 */
	public void setSYS_DATARICEZIONE(java.util.Calendar SYS_DATARICEZIONE) {
		this.SYS_DATARICEZIONE = SYS_DATARICEZIONE;
	}

	/**
	 * Gets the SYS_DATARIFERIMENTO value for this UNILAV_Type.
	 * 
	 * @return SYS_DATARIFERIMENTO
	 */
	public java.util.Calendar getSYS_DATARIFERIMENTO() {
		return SYS_DATARIFERIMENTO;
	}

	/**
	 * Sets the SYS_DATARIFERIMENTO value for this UNILAV_Type.
	 * 
	 * @param SYS_DATARIFERIMENTO
	 */
	public void setSYS_DATARIFERIMENTO(java.util.Calendar SYS_DATARIFERIMENTO) {
		this.SYS_DATARIFERIMENTO = SYS_DATARIFERIMENTO;
	}

	/**
	 * Gets the SYS_TIPOMODULO value for this UNILAV_Type.
	 * 
	 * @return SYS_TIPOMODULO
	 */
	public java.lang.String getSYS_TIPOMODULO() {
		return SYS_TIPOMODULO;
	}

	/**
	 * Sets the SYS_TIPOMODULO value for this UNILAV_Type.
	 * 
	 * @param SYS_TIPOMODULO
	 */
	public void setSYS_TIPOMODULO(java.lang.String SYS_TIPOMODULO) {
		this.SYS_TIPOMODULO = SYS_TIPOMODULO;
	}

	/**
	 * Gets the rowOrder value for this UNILAV_Type.
	 * 
	 * @return rowOrder
	 */
	public int getRowOrder() {
		return rowOrder;
	}

	/**
	 * Sets the rowOrder value for this UNILAV_Type.
	 * 
	 * @param rowOrder
	 */
	public void setRowOrder(int rowOrder) {
		this.rowOrder = rowOrder;
	}

	private java.lang.Object __equalsCalc = null;

	public synchronized boolean equals(java.lang.Object obj) {
		if (!(obj instanceof UNILAV_Type))
			return false;
		UNILAV_Type other = (UNILAV_Type) obj;
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
				&& ((this.datoreLavoro_CodiceFiscale == null && other.getDatoreLavoro_CodiceFiscale() == null)
						|| (this.datoreLavoro_CodiceFiscale != null
								&& this.datoreLavoro_CodiceFiscale.equals(other.getDatoreLavoro_CodiceFiscale())))
				&& ((this.datoreLavoro_Denominazione == null && other.getDatoreLavoro_Denominazione() == null)
						|| (this.datoreLavoro_Denominazione != null
								&& this.datoreLavoro_Denominazione.equals(other.getDatoreLavoro_Denominazione())))
				&& ((this.datoreLavoro_PA == null && other.getDatoreLavoro_PA() == null)
						|| (this.datoreLavoro_PA != null && this.datoreLavoro_PA.equals(other.getDatoreLavoro_PA())))
				&& ((this.datoreLavoro_SedeLavoro_CAP == null && other.getDatoreLavoro_SedeLavoro_CAP() == null)
						|| (this.datoreLavoro_SedeLavoro_CAP != null
								&& this.datoreLavoro_SedeLavoro_CAP.equals(other.getDatoreLavoro_SedeLavoro_CAP())))
				&& ((this.datoreLavoro_SedeLavoro_ComuneNazione == null
						&& other.getDatoreLavoro_SedeLavoro_ComuneNazione() == null)
						|| (this.datoreLavoro_SedeLavoro_ComuneNazione != null
								&& this.datoreLavoro_SedeLavoro_ComuneNazione
										.equals(other.getDatoreLavoro_SedeLavoro_ComuneNazione())))
				&& ((this.datoreLavoro_SedeLavoro_Indirizzo == null
						&& other.getDatoreLavoro_SedeLavoro_Indirizzo() == null)
						|| (this.datoreLavoro_SedeLavoro_Indirizzo != null && this.datoreLavoro_SedeLavoro_Indirizzo
								.equals(other.getDatoreLavoro_SedeLavoro_Indirizzo())))
				&& ((this.datoreLavoro_Settore == null && other.getDatoreLavoro_Settore() == null)
						|| (this.datoreLavoro_Settore != null
								&& this.datoreLavoro_Settore.equals(other.getDatoreLavoro_Settore())))
				&& ((this.datoreLavoroDistaccatario_CodiceFiscale == null
						&& other.getDatoreLavoroDistaccatario_CodiceFiscale() == null)
						|| (this.datoreLavoroDistaccatario_CodiceFiscale != null
								&& this.datoreLavoroDistaccatario_CodiceFiscale
										.equals(other.getDatoreLavoroDistaccatario_CodiceFiscale())))
				&& ((this.invio_CodiceComunicazione == null && other.getInvio_CodiceComunicazione() == null)
						|| (this.invio_CodiceComunicazione != null
								&& this.invio_CodiceComunicazione.equals(other.getInvio_CodiceComunicazione())))
				&& ((this.invio_CodiceComunicazionePrecedente == null
						&& other.getInvio_CodiceComunicazionePrecedente() == null)
						|| (this.invio_CodiceComunicazionePrecedente != null && this.invio_CodiceComunicazionePrecedente
								.equals(other.getInvio_CodiceComunicazionePrecedente())))
				&& ((this.invio_ForzaMaggiore == null && other.getInvio_ForzaMaggiore() == null)
						|| (this.invio_ForzaMaggiore != null
								&& this.invio_ForzaMaggiore.equals(other.getInvio_ForzaMaggiore())))
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
				&& ((this.lavoratoreCoobbligato_CodiceFiscale == null
						&& other.getLavoratoreCoobbligato_CodiceFiscale() == null)
						|| (this.lavoratoreCoobbligato_CodiceFiscale != null && this.lavoratoreCoobbligato_CodiceFiscale
								.equals(other.getLavoratoreCoobbligato_CodiceFiscale())))
				&& ((this.rapporto_Agevolazioni == null && other.getRapporto_Agevolazioni() == null)
						|| (this.rapporto_Agevolazioni != null
								&& this.rapporto_Agevolazioni.equals(other.getRapporto_Agevolazioni())))
				&& ((this.rapporto_CCNL == null && other.getRapporto_CCNL() == null)
						|| (this.rapporto_CCNL != null && this.rapporto_CCNL.equals(other.getRapporto_CCNL())))
				&& ((this.rapporto_Cessazione_CodiceCausa == null && other.getRapporto_Cessazione_CodiceCausa() == null)
						|| (this.rapporto_Cessazione_CodiceCausa != null && this.rapporto_Cessazione_CodiceCausa
								.equals(other.getRapporto_Cessazione_CodiceCausa())))
				&& ((this.rapporto_Cessazione_Data == null && other.getRapporto_Cessazione_Data() == null)
						|| (this.rapporto_Cessazione_Data != null
								&& this.rapporto_Cessazione_Data.equals(other.getRapporto_Cessazione_Data())))
				&& ((this.rapporto_DataFine == null && other.getRapporto_DataFine() == null)
						|| (this.rapporto_DataFine != null
								&& this.rapporto_DataFine.equals(other.getRapporto_DataFine())))
				&& ((this.rapporto_DataInizio == null && other.getRapporto_DataInizio() == null)
						|| (this.rapporto_DataInizio != null
								&& this.rapporto_DataInizio.equals(other.getRapporto_DataInizio())))
				&& ((this.rapporto_EntePrevidenziale == null && other.getRapporto_EntePrevidenziale() == null)
						|| (this.rapporto_EntePrevidenziale != null
								&& this.rapporto_EntePrevidenziale.equals(other.getRapporto_EntePrevidenziale())))
				&& ((this.rapporto_LavoroAgricoltura == null && other.getRapporto_LavoroAgricoltura() == null)
						|| (this.rapporto_LavoroAgricoltura != null
								&& this.rapporto_LavoroAgricoltura.equals(other.getRapporto_LavoroAgricoltura())))
				&& ((this.rapporto_OreSettimanaliMedie == null && other.getRapporto_OreSettimanaliMedie() == null)
						|| (this.rapporto_OreSettimanaliMedie != null
								&& this.rapporto_OreSettimanaliMedie.equals(other.getRapporto_OreSettimanaliMedie())))
				&& ((this.rapporto_Proroga_DataFine == null && other.getRapporto_Proroga_DataFine() == null)
						|| (this.rapporto_Proroga_DataFine != null
								&& this.rapporto_Proroga_DataFine.equals(other.getRapporto_Proroga_DataFine())))
				&& ((this.rapporto_SocioLavoratore == null && other.getRapporto_SocioLavoratore() == null)
						|| (this.rapporto_SocioLavoratore != null
								&& this.rapporto_SocioLavoratore.equals(other.getRapporto_SocioLavoratore())))
				&& ((this.rapporto_TipologiaContrattuale == null && other.getRapporto_TipologiaContrattuale() == null)
						|| (this.rapporto_TipologiaContrattuale != null && this.rapporto_TipologiaContrattuale
								.equals(other.getRapporto_TipologiaContrattuale())))
				&& ((this.rapporto_TipoOrario == null && other.getRapporto_TipoOrario() == null)
						|| (this.rapporto_TipoOrario != null
								&& this.rapporto_TipoOrario.equals(other.getRapporto_TipoOrario())))
				&& ((this.rapporto_Trasformazione_CodiceTrasformazione == null
						&& other.getRapporto_Trasformazione_CodiceTrasformazione() == null)
						|| (this.rapporto_Trasformazione_CodiceTrasformazione != null
								&& this.rapporto_Trasformazione_CodiceTrasformazione
										.equals(other.getRapporto_Trasformazione_CodiceTrasformazione())))
				&& ((this.rapporto_Trasformazione_Data == null && other.getRapporto_Trasformazione_Data() == null)
						|| (this.rapporto_Trasformazione_Data != null
								&& this.rapporto_Trasformazione_Data.equals(other.getRapporto_Trasformazione_Data())))
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
		if (getDatoreLavoro_CodiceFiscale() != null) {
			_hashCode += getDatoreLavoro_CodiceFiscale().hashCode();
		}
		if (getDatoreLavoro_Denominazione() != null) {
			_hashCode += getDatoreLavoro_Denominazione().hashCode();
		}
		if (getDatoreLavoro_PA() != null) {
			_hashCode += getDatoreLavoro_PA().hashCode();
		}
		if (getDatoreLavoro_SedeLavoro_CAP() != null) {
			_hashCode += getDatoreLavoro_SedeLavoro_CAP().hashCode();
		}
		if (getDatoreLavoro_SedeLavoro_ComuneNazione() != null) {
			_hashCode += getDatoreLavoro_SedeLavoro_ComuneNazione().hashCode();
		}
		if (getDatoreLavoro_SedeLavoro_Indirizzo() != null) {
			_hashCode += getDatoreLavoro_SedeLavoro_Indirizzo().hashCode();
		}
		if (getDatoreLavoro_Settore() != null) {
			_hashCode += getDatoreLavoro_Settore().hashCode();
		}
		if (getDatoreLavoroDistaccatario_CodiceFiscale() != null) {
			_hashCode += getDatoreLavoroDistaccatario_CodiceFiscale().hashCode();
		}
		if (getInvio_CodiceComunicazione() != null) {
			_hashCode += getInvio_CodiceComunicazione().hashCode();
		}
		if (getInvio_CodiceComunicazionePrecedente() != null) {
			_hashCode += getInvio_CodiceComunicazionePrecedente().hashCode();
		}
		if (getInvio_ForzaMaggiore() != null) {
			_hashCode += getInvio_ForzaMaggiore().hashCode();
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
		if (getLavoratoreCoobbligato_CodiceFiscale() != null) {
			_hashCode += getLavoratoreCoobbligato_CodiceFiscale().hashCode();
		}
		if (getRapporto_Agevolazioni() != null) {
			_hashCode += getRapporto_Agevolazioni().hashCode();
		}
		if (getRapporto_CCNL() != null) {
			_hashCode += getRapporto_CCNL().hashCode();
		}
		if (getRapporto_Cessazione_CodiceCausa() != null) {
			_hashCode += getRapporto_Cessazione_CodiceCausa().hashCode();
		}
		if (getRapporto_Cessazione_Data() != null) {
			_hashCode += getRapporto_Cessazione_Data().hashCode();
		}
		if (getRapporto_DataFine() != null) {
			_hashCode += getRapporto_DataFine().hashCode();
		}
		if (getRapporto_DataInizio() != null) {
			_hashCode += getRapporto_DataInizio().hashCode();
		}
		if (getRapporto_EntePrevidenziale() != null) {
			_hashCode += getRapporto_EntePrevidenziale().hashCode();
		}
		if (getRapporto_LavoroAgricoltura() != null) {
			_hashCode += getRapporto_LavoroAgricoltura().hashCode();
		}
		if (getRapporto_OreSettimanaliMedie() != null) {
			_hashCode += getRapporto_OreSettimanaliMedie().hashCode();
		}
		if (getRapporto_Proroga_DataFine() != null) {
			_hashCode += getRapporto_Proroga_DataFine().hashCode();
		}
		if (getRapporto_SocioLavoratore() != null) {
			_hashCode += getRapporto_SocioLavoratore().hashCode();
		}
		if (getRapporto_TipologiaContrattuale() != null) {
			_hashCode += getRapporto_TipologiaContrattuale().hashCode();
		}
		if (getRapporto_TipoOrario() != null) {
			_hashCode += getRapporto_TipoOrario().hashCode();
		}
		if (getRapporto_Trasformazione_CodiceTrasformazione() != null) {
			_hashCode += getRapporto_Trasformazione_CodiceTrasformazione().hashCode();
		}
		if (getRapporto_Trasformazione_Data() != null) {
			_hashCode += getRapporto_Trasformazione_Data().hashCode();
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
			UNILAV_Type.class, true);

	static {
		typeDesc.setXmlType(new javax.xml.namespace.QName("http://servizi.lavoro.gov.it/RicercaCO/2.0", "UNILAV_Type"));
		org.apache.axis.description.AttributeDesc attrField = new org.apache.axis.description.AttributeDesc();
		attrField.setFieldName("rowOrder");
		attrField.setXmlName(new javax.xml.namespace.QName("", "rowOrder"));
		attrField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
		typeDesc.addFieldDesc(attrField);
		org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("datoreLavoro_CodiceFiscale");
		elemField.setXmlName(new javax.xml.namespace.QName("http://servizi.lavoro.gov.it/RicercaCO/2.0",
				"DatoreLavoro_CodiceFiscale"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
		elemField.setNillable(false);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("datoreLavoro_Denominazione");
		elemField.setXmlName(new javax.xml.namespace.QName("http://servizi.lavoro.gov.it/RicercaCO/2.0",
				"DatoreLavoro_Denominazione"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
		elemField.setNillable(true);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("datoreLavoro_PA");
		elemField.setXmlName(
				new javax.xml.namespace.QName("http://servizi.lavoro.gov.it/RicercaCO/2.0", "DatoreLavoro_PA"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
		elemField.setNillable(true);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("datoreLavoro_SedeLavoro_CAP");
		elemField.setXmlName(new javax.xml.namespace.QName("http://servizi.lavoro.gov.it/RicercaCO/2.0",
				"DatoreLavoro_SedeLavoro_CAP"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
		elemField.setNillable(true);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("datoreLavoro_SedeLavoro_ComuneNazione");
		elemField.setXmlName(new javax.xml.namespace.QName("http://servizi.lavoro.gov.it/RicercaCO/2.0",
				"DatoreLavoro_SedeLavoro_ComuneNazione"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
		elemField.setNillable(true);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("datoreLavoro_SedeLavoro_Indirizzo");
		elemField.setXmlName(new javax.xml.namespace.QName("http://servizi.lavoro.gov.it/RicercaCO/2.0",
				"DatoreLavoro_SedeLavoro_Indirizzo"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
		elemField.setNillable(true);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("datoreLavoro_Settore");
		elemField.setXmlName(
				new javax.xml.namespace.QName("http://servizi.lavoro.gov.it/RicercaCO/2.0", "DatoreLavoro_Settore"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
		elemField.setNillable(true);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("datoreLavoroDistaccatario_CodiceFiscale");
		elemField.setXmlName(new javax.xml.namespace.QName("http://servizi.lavoro.gov.it/RicercaCO/2.0",
				"DatoreLavoroDistaccatario_CodiceFiscale"));
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
		elemField.setFieldName("invio_ForzaMaggiore");
		elemField.setXmlName(
				new javax.xml.namespace.QName("http://servizi.lavoro.gov.it/RicercaCO/2.0", "Invio_ForzaMaggiore"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
		elemField.setNillable(true);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("lavoratore_Cittadinanza");
		elemField.setXmlName(
				new javax.xml.namespace.QName("http://servizi.lavoro.gov.it/RicercaCO/2.0", "Lavoratore_Cittadinanza"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
		elemField.setNillable(false);
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
		elemField.setFieldName("lavoratoreCoobbligato_CodiceFiscale");
		elemField.setXmlName(new javax.xml.namespace.QName("http://servizi.lavoro.gov.it/RicercaCO/2.0",
				"LavoratoreCoobbligato_CodiceFiscale"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
		elemField.setNillable(true);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("rapporto_Agevolazioni");
		elemField.setXmlName(
				new javax.xml.namespace.QName("http://servizi.lavoro.gov.it/RicercaCO/2.0", "Rapporto_Agevolazioni"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
		elemField.setNillable(true);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("rapporto_CCNL");
		elemField.setXmlName(
				new javax.xml.namespace.QName("http://servizi.lavoro.gov.it/RicercaCO/2.0", "Rapporto_CCNL"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
		elemField.setNillable(true);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("rapporto_Cessazione_CodiceCausa");
		elemField.setXmlName(new javax.xml.namespace.QName("http://servizi.lavoro.gov.it/RicercaCO/2.0",
				"Rapporto_Cessazione_CodiceCausa"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
		elemField.setNillable(true);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("rapporto_Cessazione_Data");
		elemField.setXmlName(new javax.xml.namespace.QName("http://servizi.lavoro.gov.it/RicercaCO/2.0",
				"Rapporto_Cessazione_Data"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
		elemField.setNillable(true);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("rapporto_DataFine");
		elemField.setXmlName(
				new javax.xml.namespace.QName("http://servizi.lavoro.gov.it/RicercaCO/2.0", "Rapporto_DataFine"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
		elemField.setNillable(true);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("rapporto_DataInizio");
		elemField.setXmlName(
				new javax.xml.namespace.QName("http://servizi.lavoro.gov.it/RicercaCO/2.0", "Rapporto_DataInizio"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
		elemField.setNillable(false);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("rapporto_EntePrevidenziale");
		elemField.setXmlName(new javax.xml.namespace.QName("http://servizi.lavoro.gov.it/RicercaCO/2.0",
				"Rapporto_EntePrevidenziale"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
		elemField.setNillable(true);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("rapporto_LavoroAgricoltura");
		elemField.setXmlName(new javax.xml.namespace.QName("http://servizi.lavoro.gov.it/RicercaCO/2.0",
				"Rapporto_LavoroAgricoltura"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
		elemField.setNillable(true);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("rapporto_OreSettimanaliMedie");
		elemField.setXmlName(new javax.xml.namespace.QName("http://servizi.lavoro.gov.it/RicercaCO/2.0",
				"Rapporto_OreSettimanaliMedie"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
		elemField.setNillable(true);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("rapporto_Proroga_DataFine");
		elemField.setXmlName(new javax.xml.namespace.QName("http://servizi.lavoro.gov.it/RicercaCO/2.0",
				"Rapporto_Proroga_DataFine"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
		elemField.setNillable(true);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("rapporto_SocioLavoratore");
		elemField.setXmlName(new javax.xml.namespace.QName("http://servizi.lavoro.gov.it/RicercaCO/2.0",
				"Rapporto_SocioLavoratore"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
		elemField.setNillable(true);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("rapporto_TipologiaContrattuale");
		elemField.setXmlName(new javax.xml.namespace.QName("http://servizi.lavoro.gov.it/RicercaCO/2.0",
				"Rapporto_TipologiaContrattuale"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
		elemField.setNillable(false);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("rapporto_TipoOrario");
		elemField.setXmlName(
				new javax.xml.namespace.QName("http://servizi.lavoro.gov.it/RicercaCO/2.0", "Rapporto_TipoOrario"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
		elemField.setNillable(true);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("rapporto_Trasformazione_CodiceTrasformazione");
		elemField.setXmlName(new javax.xml.namespace.QName("http://servizi.lavoro.gov.it/RicercaCO/2.0",
				"Rapporto_Trasformazione_CodiceTrasformazione"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
		elemField.setNillable(true);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("rapporto_Trasformazione_Data");
		elemField.setXmlName(new javax.xml.namespace.QName("http://servizi.lavoro.gov.it/RicercaCO/2.0",
				"Rapporto_Trasformazione_Data"));
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
