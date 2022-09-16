/**
 * UNIMARE_Type.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package it.gov.lavoro.servizi.RicercaCO._2_0;

public class UNIMARE_Type implements java.io.Serializable {
	private java.lang.String datoreLavoro_CodiceFiscale;

	private java.lang.String datoreLavoro_Denominazione;

	private java.lang.String datoreLavoro_Settore;

	private java.lang.String datoreLavoroDistaccatario_CodiceFiscale;

	private java.lang.String datoreLavoro_SedeLavoro_NaveTipo;

	private java.lang.String datoreLavoro_SedeLavoro_NaveInternazionale;

	private java.lang.String datoreLavoro_SedeLavoro_NaveStazzaMin5Ton;

	private java.lang.String datoreLavoro_SedeLavoro_NaveDenominazione;

	private java.lang.String datoreLavoro_SedeLavoro_NaveNIMO;

	private java.lang.String datoreLavoro_SedeLavoro_NaveNIscrizione;

	private java.lang.String datoreLavoro_SedeLavoro_NaveLuogoIscr;

	private java.lang.String datoreLavoro_RapportoTerraComandata;

	private java.lang.String datoreLavoro_SedeLavoro_TerraComuneNazione;

	private java.lang.String datoreLavoro_SedeLavoro_TerraCAP;

	private java.lang.String datoreLavoro_SedeLavoro_TerraIndirizzo;

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

	private java.lang.String lavoratore_CategoriaIscrizione;

	private java.lang.String lavoratore_NumeroMatricola;

	private java.lang.String lavoratore_LuogoMatricola;

	private java.lang.String rapporto_Agevolazioni;

	private java.lang.String rapporto_CCNL;

	private java.lang.String rapporto_Cessazione_CodiceCausa;

	private java.lang.String rapporto_Cessazione_Data;

	private java.lang.String rapporto_ContrattoVerbale;

	private java.lang.String rapporto_DataFine;

	private java.lang.String rapporto_DataInizio;

	private java.lang.String rapporto_EntePrevidenziale;

	private java.lang.String rapporto_Proroga_DataFine;

	private java.lang.String rapporto_SocioLavoratore;

	private java.lang.String rapporto_TipologiaContrattuale;

	private java.lang.String rapporto_Trasformazione_CodiceTrasformazione;

	private java.lang.String rapporto_Trasformazione_Data;

	private java.util.Calendar SYS_DATARICEZIONE;

	private java.util.Calendar SYS_DATARIFERIMENTO;

	private java.lang.String SYS_TIPOMODULO;

	private int rowOrder; // attribute

	public UNIMARE_Type() {
	}

	public UNIMARE_Type(java.lang.String datoreLavoro_CodiceFiscale, java.lang.String datoreLavoro_Denominazione,
			java.lang.String datoreLavoro_Settore, java.lang.String datoreLavoroDistaccatario_CodiceFiscale,
			java.lang.String datoreLavoro_SedeLavoro_NaveTipo,
			java.lang.String datoreLavoro_SedeLavoro_NaveInternazionale,
			java.lang.String datoreLavoro_SedeLavoro_NaveStazzaMin5Ton,
			java.lang.String datoreLavoro_SedeLavoro_NaveDenominazione,
			java.lang.String datoreLavoro_SedeLavoro_NaveNIMO, java.lang.String datoreLavoro_SedeLavoro_NaveNIscrizione,
			java.lang.String datoreLavoro_SedeLavoro_NaveLuogoIscr,
			java.lang.String datoreLavoro_RapportoTerraComandata,
			java.lang.String datoreLavoro_SedeLavoro_TerraComuneNazione,
			java.lang.String datoreLavoro_SedeLavoro_TerraCAP, java.lang.String datoreLavoro_SedeLavoro_TerraIndirizzo,
			java.lang.String invio_CodiceComunicazione, java.lang.String invio_CodiceComunicazionePrecedente,
			java.lang.String invio_ForzaMaggiore, java.lang.String lavoratore_Cittadinanza,
			java.lang.String lavoratore_CodiceFiscale, java.lang.String lavoratore_Cognome,
			java.lang.String lavoratore_Nascita_ComuneNazione, java.lang.String lavoratore_Nascita_Data,
			java.lang.String lavoratore_Nome, java.lang.String lavoratore_Sesso,
			java.lang.String lavoratore_CategoriaIscrizione, java.lang.String lavoratore_NumeroMatricola,
			java.lang.String lavoratore_LuogoMatricola, java.lang.String rapporto_Agevolazioni,
			java.lang.String rapporto_CCNL, java.lang.String rapporto_Cessazione_CodiceCausa,
			java.lang.String rapporto_Cessazione_Data, java.lang.String rapporto_ContrattoVerbale,
			java.lang.String rapporto_DataFine, java.lang.String rapporto_DataInizio,
			java.lang.String rapporto_EntePrevidenziale, java.lang.String rapporto_Proroga_DataFine,
			java.lang.String rapporto_SocioLavoratore, java.lang.String rapporto_TipologiaContrattuale,
			java.lang.String rapporto_Trasformazione_CodiceTrasformazione,
			java.lang.String rapporto_Trasformazione_Data, java.util.Calendar SYS_DATARICEZIONE,
			java.util.Calendar SYS_DATARIFERIMENTO, java.lang.String SYS_TIPOMODULO, int rowOrder) {
		this.datoreLavoro_CodiceFiscale = datoreLavoro_CodiceFiscale;
		this.datoreLavoro_Denominazione = datoreLavoro_Denominazione;
		this.datoreLavoro_Settore = datoreLavoro_Settore;
		this.datoreLavoroDistaccatario_CodiceFiscale = datoreLavoroDistaccatario_CodiceFiscale;
		this.datoreLavoro_SedeLavoro_NaveTipo = datoreLavoro_SedeLavoro_NaveTipo;
		this.datoreLavoro_SedeLavoro_NaveInternazionale = datoreLavoro_SedeLavoro_NaveInternazionale;
		this.datoreLavoro_SedeLavoro_NaveStazzaMin5Ton = datoreLavoro_SedeLavoro_NaveStazzaMin5Ton;
		this.datoreLavoro_SedeLavoro_NaveDenominazione = datoreLavoro_SedeLavoro_NaveDenominazione;
		this.datoreLavoro_SedeLavoro_NaveNIMO = datoreLavoro_SedeLavoro_NaveNIMO;
		this.datoreLavoro_SedeLavoro_NaveNIscrizione = datoreLavoro_SedeLavoro_NaveNIscrizione;
		this.datoreLavoro_SedeLavoro_NaveLuogoIscr = datoreLavoro_SedeLavoro_NaveLuogoIscr;
		this.datoreLavoro_RapportoTerraComandata = datoreLavoro_RapportoTerraComandata;
		this.datoreLavoro_SedeLavoro_TerraComuneNazione = datoreLavoro_SedeLavoro_TerraComuneNazione;
		this.datoreLavoro_SedeLavoro_TerraCAP = datoreLavoro_SedeLavoro_TerraCAP;
		this.datoreLavoro_SedeLavoro_TerraIndirizzo = datoreLavoro_SedeLavoro_TerraIndirizzo;
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
		this.lavoratore_CategoriaIscrizione = lavoratore_CategoriaIscrizione;
		this.lavoratore_NumeroMatricola = lavoratore_NumeroMatricola;
		this.lavoratore_LuogoMatricola = lavoratore_LuogoMatricola;
		this.rapporto_Agevolazioni = rapporto_Agevolazioni;
		this.rapporto_CCNL = rapporto_CCNL;
		this.rapporto_Cessazione_CodiceCausa = rapporto_Cessazione_CodiceCausa;
		this.rapporto_Cessazione_Data = rapporto_Cessazione_Data;
		this.rapporto_ContrattoVerbale = rapporto_ContrattoVerbale;
		this.rapporto_DataFine = rapporto_DataFine;
		this.rapporto_DataInizio = rapporto_DataInizio;
		this.rapporto_EntePrevidenziale = rapporto_EntePrevidenziale;
		this.rapporto_Proroga_DataFine = rapporto_Proroga_DataFine;
		this.rapporto_SocioLavoratore = rapporto_SocioLavoratore;
		this.rapporto_TipologiaContrattuale = rapporto_TipologiaContrattuale;
		this.rapporto_Trasformazione_CodiceTrasformazione = rapporto_Trasformazione_CodiceTrasformazione;
		this.rapporto_Trasformazione_Data = rapporto_Trasformazione_Data;
		this.SYS_DATARICEZIONE = SYS_DATARICEZIONE;
		this.SYS_DATARIFERIMENTO = SYS_DATARIFERIMENTO;
		this.SYS_TIPOMODULO = SYS_TIPOMODULO;
		this.rowOrder = rowOrder;
	}

	/**
	 * Gets the datoreLavoro_CodiceFiscale value for this UNIMARE_Type.
	 * 
	 * @return datoreLavoro_CodiceFiscale
	 */
	public java.lang.String getDatoreLavoro_CodiceFiscale() {
		return datoreLavoro_CodiceFiscale;
	}

	/**
	 * Sets the datoreLavoro_CodiceFiscale value for this UNIMARE_Type.
	 * 
	 * @param datoreLavoro_CodiceFiscale
	 */
	public void setDatoreLavoro_CodiceFiscale(java.lang.String datoreLavoro_CodiceFiscale) {
		this.datoreLavoro_CodiceFiscale = datoreLavoro_CodiceFiscale;
	}

	/**
	 * Gets the datoreLavoro_Denominazione value for this UNIMARE_Type.
	 * 
	 * @return datoreLavoro_Denominazione
	 */
	public java.lang.String getDatoreLavoro_Denominazione() {
		return datoreLavoro_Denominazione;
	}

	/**
	 * Sets the datoreLavoro_Denominazione value for this UNIMARE_Type.
	 * 
	 * @param datoreLavoro_Denominazione
	 */
	public void setDatoreLavoro_Denominazione(java.lang.String datoreLavoro_Denominazione) {
		this.datoreLavoro_Denominazione = datoreLavoro_Denominazione;
	}

	/**
	 * Gets the datoreLavoro_Settore value for this UNIMARE_Type.
	 * 
	 * @return datoreLavoro_Settore
	 */
	public java.lang.String getDatoreLavoro_Settore() {
		return datoreLavoro_Settore;
	}

	/**
	 * Sets the datoreLavoro_Settore value for this UNIMARE_Type.
	 * 
	 * @param datoreLavoro_Settore
	 */
	public void setDatoreLavoro_Settore(java.lang.String datoreLavoro_Settore) {
		this.datoreLavoro_Settore = datoreLavoro_Settore;
	}

	/**
	 * Gets the datoreLavoroDistaccatario_CodiceFiscale value for this UNIMARE_Type.
	 * 
	 * @return datoreLavoroDistaccatario_CodiceFiscale
	 */
	public java.lang.String getDatoreLavoroDistaccatario_CodiceFiscale() {
		return datoreLavoroDistaccatario_CodiceFiscale;
	}

	/**
	 * Sets the datoreLavoroDistaccatario_CodiceFiscale value for this UNIMARE_Type.
	 * 
	 * @param datoreLavoroDistaccatario_CodiceFiscale
	 */
	public void setDatoreLavoroDistaccatario_CodiceFiscale(java.lang.String datoreLavoroDistaccatario_CodiceFiscale) {
		this.datoreLavoroDistaccatario_CodiceFiscale = datoreLavoroDistaccatario_CodiceFiscale;
	}

	/**
	 * Gets the datoreLavoro_SedeLavoro_NaveTipo value for this UNIMARE_Type.
	 * 
	 * @return datoreLavoro_SedeLavoro_NaveTipo
	 */
	public java.lang.String getDatoreLavoro_SedeLavoro_NaveTipo() {
		return datoreLavoro_SedeLavoro_NaveTipo;
	}

	/**
	 * Sets the datoreLavoro_SedeLavoro_NaveTipo value for this UNIMARE_Type.
	 * 
	 * @param datoreLavoro_SedeLavoro_NaveTipo
	 */
	public void setDatoreLavoro_SedeLavoro_NaveTipo(java.lang.String datoreLavoro_SedeLavoro_NaveTipo) {
		this.datoreLavoro_SedeLavoro_NaveTipo = datoreLavoro_SedeLavoro_NaveTipo;
	}

	/**
	 * Gets the datoreLavoro_SedeLavoro_NaveInternazionale value for this UNIMARE_Type.
	 * 
	 * @return datoreLavoro_SedeLavoro_NaveInternazionale
	 */
	public java.lang.String getDatoreLavoro_SedeLavoro_NaveInternazionale() {
		return datoreLavoro_SedeLavoro_NaveInternazionale;
	}

	/**
	 * Sets the datoreLavoro_SedeLavoro_NaveInternazionale value for this UNIMARE_Type.
	 * 
	 * @param datoreLavoro_SedeLavoro_NaveInternazionale
	 */
	public void setDatoreLavoro_SedeLavoro_NaveInternazionale(
			java.lang.String datoreLavoro_SedeLavoro_NaveInternazionale) {
		this.datoreLavoro_SedeLavoro_NaveInternazionale = datoreLavoro_SedeLavoro_NaveInternazionale;
	}

	/**
	 * Gets the datoreLavoro_SedeLavoro_NaveStazzaMin5Ton value for this UNIMARE_Type.
	 * 
	 * @return datoreLavoro_SedeLavoro_NaveStazzaMin5Ton
	 */
	public java.lang.String getDatoreLavoro_SedeLavoro_NaveStazzaMin5Ton() {
		return datoreLavoro_SedeLavoro_NaveStazzaMin5Ton;
	}

	/**
	 * Sets the datoreLavoro_SedeLavoro_NaveStazzaMin5Ton value for this UNIMARE_Type.
	 * 
	 * @param datoreLavoro_SedeLavoro_NaveStazzaMin5Ton
	 */
	public void setDatoreLavoro_SedeLavoro_NaveStazzaMin5Ton(
			java.lang.String datoreLavoro_SedeLavoro_NaveStazzaMin5Ton) {
		this.datoreLavoro_SedeLavoro_NaveStazzaMin5Ton = datoreLavoro_SedeLavoro_NaveStazzaMin5Ton;
	}

	/**
	 * Gets the datoreLavoro_SedeLavoro_NaveDenominazione value for this UNIMARE_Type.
	 * 
	 * @return datoreLavoro_SedeLavoro_NaveDenominazione
	 */
	public java.lang.String getDatoreLavoro_SedeLavoro_NaveDenominazione() {
		return datoreLavoro_SedeLavoro_NaveDenominazione;
	}

	/**
	 * Sets the datoreLavoro_SedeLavoro_NaveDenominazione value for this UNIMARE_Type.
	 * 
	 * @param datoreLavoro_SedeLavoro_NaveDenominazione
	 */
	public void setDatoreLavoro_SedeLavoro_NaveDenominazione(
			java.lang.String datoreLavoro_SedeLavoro_NaveDenominazione) {
		this.datoreLavoro_SedeLavoro_NaveDenominazione = datoreLavoro_SedeLavoro_NaveDenominazione;
	}

	/**
	 * Gets the datoreLavoro_SedeLavoro_NaveNIMO value for this UNIMARE_Type.
	 * 
	 * @return datoreLavoro_SedeLavoro_NaveNIMO
	 */
	public java.lang.String getDatoreLavoro_SedeLavoro_NaveNIMO() {
		return datoreLavoro_SedeLavoro_NaveNIMO;
	}

	/**
	 * Sets the datoreLavoro_SedeLavoro_NaveNIMO value for this UNIMARE_Type.
	 * 
	 * @param datoreLavoro_SedeLavoro_NaveNIMO
	 */
	public void setDatoreLavoro_SedeLavoro_NaveNIMO(java.lang.String datoreLavoro_SedeLavoro_NaveNIMO) {
		this.datoreLavoro_SedeLavoro_NaveNIMO = datoreLavoro_SedeLavoro_NaveNIMO;
	}

	/**
	 * Gets the datoreLavoro_SedeLavoro_NaveNIscrizione value for this UNIMARE_Type.
	 * 
	 * @return datoreLavoro_SedeLavoro_NaveNIscrizione
	 */
	public java.lang.String getDatoreLavoro_SedeLavoro_NaveNIscrizione() {
		return datoreLavoro_SedeLavoro_NaveNIscrizione;
	}

	/**
	 * Sets the datoreLavoro_SedeLavoro_NaveNIscrizione value for this UNIMARE_Type.
	 * 
	 * @param datoreLavoro_SedeLavoro_NaveNIscrizione
	 */
	public void setDatoreLavoro_SedeLavoro_NaveNIscrizione(java.lang.String datoreLavoro_SedeLavoro_NaveNIscrizione) {
		this.datoreLavoro_SedeLavoro_NaveNIscrizione = datoreLavoro_SedeLavoro_NaveNIscrizione;
	}

	/**
	 * Gets the datoreLavoro_SedeLavoro_NaveLuogoIscr value for this UNIMARE_Type.
	 * 
	 * @return datoreLavoro_SedeLavoro_NaveLuogoIscr
	 */
	public java.lang.String getDatoreLavoro_SedeLavoro_NaveLuogoIscr() {
		return datoreLavoro_SedeLavoro_NaveLuogoIscr;
	}

	/**
	 * Sets the datoreLavoro_SedeLavoro_NaveLuogoIscr value for this UNIMARE_Type.
	 * 
	 * @param datoreLavoro_SedeLavoro_NaveLuogoIscr
	 */
	public void setDatoreLavoro_SedeLavoro_NaveLuogoIscr(java.lang.String datoreLavoro_SedeLavoro_NaveLuogoIscr) {
		this.datoreLavoro_SedeLavoro_NaveLuogoIscr = datoreLavoro_SedeLavoro_NaveLuogoIscr;
	}

	/**
	 * Gets the datoreLavoro_RapportoTerraComandata value for this UNIMARE_Type.
	 * 
	 * @return datoreLavoro_RapportoTerraComandata
	 */
	public java.lang.String getDatoreLavoro_RapportoTerraComandata() {
		return datoreLavoro_RapportoTerraComandata;
	}

	/**
	 * Sets the datoreLavoro_RapportoTerraComandata value for this UNIMARE_Type.
	 * 
	 * @param datoreLavoro_RapportoTerraComandata
	 */
	public void setDatoreLavoro_RapportoTerraComandata(java.lang.String datoreLavoro_RapportoTerraComandata) {
		this.datoreLavoro_RapportoTerraComandata = datoreLavoro_RapportoTerraComandata;
	}

	/**
	 * Gets the datoreLavoro_SedeLavoro_TerraComuneNazione value for this UNIMARE_Type.
	 * 
	 * @return datoreLavoro_SedeLavoro_TerraComuneNazione
	 */
	public java.lang.String getDatoreLavoro_SedeLavoro_TerraComuneNazione() {
		return datoreLavoro_SedeLavoro_TerraComuneNazione;
	}

	/**
	 * Sets the datoreLavoro_SedeLavoro_TerraComuneNazione value for this UNIMARE_Type.
	 * 
	 * @param datoreLavoro_SedeLavoro_TerraComuneNazione
	 */
	public void setDatoreLavoro_SedeLavoro_TerraComuneNazione(
			java.lang.String datoreLavoro_SedeLavoro_TerraComuneNazione) {
		this.datoreLavoro_SedeLavoro_TerraComuneNazione = datoreLavoro_SedeLavoro_TerraComuneNazione;
	}

	/**
	 * Gets the datoreLavoro_SedeLavoro_TerraCAP value for this UNIMARE_Type.
	 * 
	 * @return datoreLavoro_SedeLavoro_TerraCAP
	 */
	public java.lang.String getDatoreLavoro_SedeLavoro_TerraCAP() {
		return datoreLavoro_SedeLavoro_TerraCAP;
	}

	/**
	 * Sets the datoreLavoro_SedeLavoro_TerraCAP value for this UNIMARE_Type.
	 * 
	 * @param datoreLavoro_SedeLavoro_TerraCAP
	 */
	public void setDatoreLavoro_SedeLavoro_TerraCAP(java.lang.String datoreLavoro_SedeLavoro_TerraCAP) {
		this.datoreLavoro_SedeLavoro_TerraCAP = datoreLavoro_SedeLavoro_TerraCAP;
	}

	/**
	 * Gets the datoreLavoro_SedeLavoro_TerraIndirizzo value for this UNIMARE_Type.
	 * 
	 * @return datoreLavoro_SedeLavoro_TerraIndirizzo
	 */
	public java.lang.String getDatoreLavoro_SedeLavoro_TerraIndirizzo() {
		return datoreLavoro_SedeLavoro_TerraIndirizzo;
	}

	/**
	 * Sets the datoreLavoro_SedeLavoro_TerraIndirizzo value for this UNIMARE_Type.
	 * 
	 * @param datoreLavoro_SedeLavoro_TerraIndirizzo
	 */
	public void setDatoreLavoro_SedeLavoro_TerraIndirizzo(java.lang.String datoreLavoro_SedeLavoro_TerraIndirizzo) {
		this.datoreLavoro_SedeLavoro_TerraIndirizzo = datoreLavoro_SedeLavoro_TerraIndirizzo;
	}

	/**
	 * Gets the invio_CodiceComunicazione value for this UNIMARE_Type.
	 * 
	 * @return invio_CodiceComunicazione
	 */
	public java.lang.String getInvio_CodiceComunicazione() {
		return invio_CodiceComunicazione;
	}

	/**
	 * Sets the invio_CodiceComunicazione value for this UNIMARE_Type.
	 * 
	 * @param invio_CodiceComunicazione
	 */
	public void setInvio_CodiceComunicazione(java.lang.String invio_CodiceComunicazione) {
		this.invio_CodiceComunicazione = invio_CodiceComunicazione;
	}

	/**
	 * Gets the invio_CodiceComunicazionePrecedente value for this UNIMARE_Type.
	 * 
	 * @return invio_CodiceComunicazionePrecedente
	 */
	public java.lang.String getInvio_CodiceComunicazionePrecedente() {
		return invio_CodiceComunicazionePrecedente;
	}

	/**
	 * Sets the invio_CodiceComunicazionePrecedente value for this UNIMARE_Type.
	 * 
	 * @param invio_CodiceComunicazionePrecedente
	 */
	public void setInvio_CodiceComunicazionePrecedente(java.lang.String invio_CodiceComunicazionePrecedente) {
		this.invio_CodiceComunicazionePrecedente = invio_CodiceComunicazionePrecedente;
	}

	/**
	 * Gets the invio_ForzaMaggiore value for this UNIMARE_Type.
	 * 
	 * @return invio_ForzaMaggiore
	 */
	public java.lang.String getInvio_ForzaMaggiore() {
		return invio_ForzaMaggiore;
	}

	/**
	 * Sets the invio_ForzaMaggiore value for this UNIMARE_Type.
	 * 
	 * @param invio_ForzaMaggiore
	 */
	public void setInvio_ForzaMaggiore(java.lang.String invio_ForzaMaggiore) {
		this.invio_ForzaMaggiore = invio_ForzaMaggiore;
	}

	/**
	 * Gets the lavoratore_Cittadinanza value for this UNIMARE_Type.
	 * 
	 * @return lavoratore_Cittadinanza
	 */
	public java.lang.String getLavoratore_Cittadinanza() {
		return lavoratore_Cittadinanza;
	}

	/**
	 * Sets the lavoratore_Cittadinanza value for this UNIMARE_Type.
	 * 
	 * @param lavoratore_Cittadinanza
	 */
	public void setLavoratore_Cittadinanza(java.lang.String lavoratore_Cittadinanza) {
		this.lavoratore_Cittadinanza = lavoratore_Cittadinanza;
	}

	/**
	 * Gets the lavoratore_CodiceFiscale value for this UNIMARE_Type.
	 * 
	 * @return lavoratore_CodiceFiscale
	 */
	public java.lang.String getLavoratore_CodiceFiscale() {
		return lavoratore_CodiceFiscale;
	}

	/**
	 * Sets the lavoratore_CodiceFiscale value for this UNIMARE_Type.
	 * 
	 * @param lavoratore_CodiceFiscale
	 */
	public void setLavoratore_CodiceFiscale(java.lang.String lavoratore_CodiceFiscale) {
		this.lavoratore_CodiceFiscale = lavoratore_CodiceFiscale;
	}

	/**
	 * Gets the lavoratore_Cognome value for this UNIMARE_Type.
	 * 
	 * @return lavoratore_Cognome
	 */
	public java.lang.String getLavoratore_Cognome() {
		return lavoratore_Cognome;
	}

	/**
	 * Sets the lavoratore_Cognome value for this UNIMARE_Type.
	 * 
	 * @param lavoratore_Cognome
	 */
	public void setLavoratore_Cognome(java.lang.String lavoratore_Cognome) {
		this.lavoratore_Cognome = lavoratore_Cognome;
	}

	/**
	 * Gets the lavoratore_Nascita_ComuneNazione value for this UNIMARE_Type.
	 * 
	 * @return lavoratore_Nascita_ComuneNazione
	 */
	public java.lang.String getLavoratore_Nascita_ComuneNazione() {
		return lavoratore_Nascita_ComuneNazione;
	}

	/**
	 * Sets the lavoratore_Nascita_ComuneNazione value for this UNIMARE_Type.
	 * 
	 * @param lavoratore_Nascita_ComuneNazione
	 */
	public void setLavoratore_Nascita_ComuneNazione(java.lang.String lavoratore_Nascita_ComuneNazione) {
		this.lavoratore_Nascita_ComuneNazione = lavoratore_Nascita_ComuneNazione;
	}

	/**
	 * Gets the lavoratore_Nascita_Data value for this UNIMARE_Type.
	 * 
	 * @return lavoratore_Nascita_Data
	 */
	public java.lang.String getLavoratore_Nascita_Data() {
		return lavoratore_Nascita_Data;
	}

	/**
	 * Sets the lavoratore_Nascita_Data value for this UNIMARE_Type.
	 * 
	 * @param lavoratore_Nascita_Data
	 */
	public void setLavoratore_Nascita_Data(java.lang.String lavoratore_Nascita_Data) {
		this.lavoratore_Nascita_Data = lavoratore_Nascita_Data;
	}

	/**
	 * Gets the lavoratore_Nome value for this UNIMARE_Type.
	 * 
	 * @return lavoratore_Nome
	 */
	public java.lang.String getLavoratore_Nome() {
		return lavoratore_Nome;
	}

	/**
	 * Sets the lavoratore_Nome value for this UNIMARE_Type.
	 * 
	 * @param lavoratore_Nome
	 */
	public void setLavoratore_Nome(java.lang.String lavoratore_Nome) {
		this.lavoratore_Nome = lavoratore_Nome;
	}

	/**
	 * Gets the lavoratore_Sesso value for this UNIMARE_Type.
	 * 
	 * @return lavoratore_Sesso
	 */
	public java.lang.String getLavoratore_Sesso() {
		return lavoratore_Sesso;
	}

	/**
	 * Sets the lavoratore_Sesso value for this UNIMARE_Type.
	 * 
	 * @param lavoratore_Sesso
	 */
	public void setLavoratore_Sesso(java.lang.String lavoratore_Sesso) {
		this.lavoratore_Sesso = lavoratore_Sesso;
	}

	/**
	 * Gets the lavoratore_CategoriaIscrizione value for this UNIMARE_Type.
	 * 
	 * @return lavoratore_CategoriaIscrizione
	 */
	public java.lang.String getLavoratore_CategoriaIscrizione() {
		return lavoratore_CategoriaIscrizione;
	}

	/**
	 * Sets the lavoratore_CategoriaIscrizione value for this UNIMARE_Type.
	 * 
	 * @param lavoratore_CategoriaIscrizione
	 */
	public void setLavoratore_CategoriaIscrizione(java.lang.String lavoratore_CategoriaIscrizione) {
		this.lavoratore_CategoriaIscrizione = lavoratore_CategoriaIscrizione;
	}

	/**
	 * Gets the lavoratore_NumeroMatricola value for this UNIMARE_Type.
	 * 
	 * @return lavoratore_NumeroMatricola
	 */
	public java.lang.String getLavoratore_NumeroMatricola() {
		return lavoratore_NumeroMatricola;
	}

	/**
	 * Sets the lavoratore_NumeroMatricola value for this UNIMARE_Type.
	 * 
	 * @param lavoratore_NumeroMatricola
	 */
	public void setLavoratore_NumeroMatricola(java.lang.String lavoratore_NumeroMatricola) {
		this.lavoratore_NumeroMatricola = lavoratore_NumeroMatricola;
	}

	/**
	 * Gets the lavoratore_LuogoMatricola value for this UNIMARE_Type.
	 * 
	 * @return lavoratore_LuogoMatricola
	 */
	public java.lang.String getLavoratore_LuogoMatricola() {
		return lavoratore_LuogoMatricola;
	}

	/**
	 * Sets the lavoratore_LuogoMatricola value for this UNIMARE_Type.
	 * 
	 * @param lavoratore_LuogoMatricola
	 */
	public void setLavoratore_LuogoMatricola(java.lang.String lavoratore_LuogoMatricola) {
		this.lavoratore_LuogoMatricola = lavoratore_LuogoMatricola;
	}

	/**
	 * Gets the rapporto_Agevolazioni value for this UNIMARE_Type.
	 * 
	 * @return rapporto_Agevolazioni
	 */
	public java.lang.String getRapporto_Agevolazioni() {
		return rapporto_Agevolazioni;
	}

	/**
	 * Sets the rapporto_Agevolazioni value for this UNIMARE_Type.
	 * 
	 * @param rapporto_Agevolazioni
	 */
	public void setRapporto_Agevolazioni(java.lang.String rapporto_Agevolazioni) {
		this.rapporto_Agevolazioni = rapporto_Agevolazioni;
	}

	/**
	 * Gets the rapporto_CCNL value for this UNIMARE_Type.
	 * 
	 * @return rapporto_CCNL
	 */
	public java.lang.String getRapporto_CCNL() {
		return rapporto_CCNL;
	}

	/**
	 * Sets the rapporto_CCNL value for this UNIMARE_Type.
	 * 
	 * @param rapporto_CCNL
	 */
	public void setRapporto_CCNL(java.lang.String rapporto_CCNL) {
		this.rapporto_CCNL = rapporto_CCNL;
	}

	/**
	 * Gets the rapporto_Cessazione_CodiceCausa value for this UNIMARE_Type.
	 * 
	 * @return rapporto_Cessazione_CodiceCausa
	 */
	public java.lang.String getRapporto_Cessazione_CodiceCausa() {
		return rapporto_Cessazione_CodiceCausa;
	}

	/**
	 * Sets the rapporto_Cessazione_CodiceCausa value for this UNIMARE_Type.
	 * 
	 * @param rapporto_Cessazione_CodiceCausa
	 */
	public void setRapporto_Cessazione_CodiceCausa(java.lang.String rapporto_Cessazione_CodiceCausa) {
		this.rapporto_Cessazione_CodiceCausa = rapporto_Cessazione_CodiceCausa;
	}

	/**
	 * Gets the rapporto_Cessazione_Data value for this UNIMARE_Type.
	 * 
	 * @return rapporto_Cessazione_Data
	 */
	public java.lang.String getRapporto_Cessazione_Data() {
		return rapporto_Cessazione_Data;
	}

	/**
	 * Sets the rapporto_Cessazione_Data value for this UNIMARE_Type.
	 * 
	 * @param rapporto_Cessazione_Data
	 */
	public void setRapporto_Cessazione_Data(java.lang.String rapporto_Cessazione_Data) {
		this.rapporto_Cessazione_Data = rapporto_Cessazione_Data;
	}

	/**
	 * Gets the rapporto_ContrattoVerbale value for this UNIMARE_Type.
	 * 
	 * @return rapporto_ContrattoVerbale
	 */
	public java.lang.String getRapporto_ContrattoVerbale() {
		return rapporto_ContrattoVerbale;
	}

	/**
	 * Sets the rapporto_ContrattoVerbale value for this UNIMARE_Type.
	 * 
	 * @param rapporto_ContrattoVerbale
	 */
	public void setRapporto_ContrattoVerbale(java.lang.String rapporto_ContrattoVerbale) {
		this.rapporto_ContrattoVerbale = rapporto_ContrattoVerbale;
	}

	/**
	 * Gets the rapporto_DataFine value for this UNIMARE_Type.
	 * 
	 * @return rapporto_DataFine
	 */
	public java.lang.String getRapporto_DataFine() {
		return rapporto_DataFine;
	}

	/**
	 * Sets the rapporto_DataFine value for this UNIMARE_Type.
	 * 
	 * @param rapporto_DataFine
	 */
	public void setRapporto_DataFine(java.lang.String rapporto_DataFine) {
		this.rapporto_DataFine = rapporto_DataFine;
	}

	/**
	 * Gets the rapporto_DataInizio value for this UNIMARE_Type.
	 * 
	 * @return rapporto_DataInizio
	 */
	public java.lang.String getRapporto_DataInizio() {
		return rapporto_DataInizio;
	}

	/**
	 * Sets the rapporto_DataInizio value for this UNIMARE_Type.
	 * 
	 * @param rapporto_DataInizio
	 */
	public void setRapporto_DataInizio(java.lang.String rapporto_DataInizio) {
		this.rapporto_DataInizio = rapporto_DataInizio;
	}

	/**
	 * Gets the rapporto_EntePrevidenziale value for this UNIMARE_Type.
	 * 
	 * @return rapporto_EntePrevidenziale
	 */
	public java.lang.String getRapporto_EntePrevidenziale() {
		return rapporto_EntePrevidenziale;
	}

	/**
	 * Sets the rapporto_EntePrevidenziale value for this UNIMARE_Type.
	 * 
	 * @param rapporto_EntePrevidenziale
	 */
	public void setRapporto_EntePrevidenziale(java.lang.String rapporto_EntePrevidenziale) {
		this.rapporto_EntePrevidenziale = rapporto_EntePrevidenziale;
	}

	/**
	 * Gets the rapporto_Proroga_DataFine value for this UNIMARE_Type.
	 * 
	 * @return rapporto_Proroga_DataFine
	 */
	public java.lang.String getRapporto_Proroga_DataFine() {
		return rapporto_Proroga_DataFine;
	}

	/**
	 * Sets the rapporto_Proroga_DataFine value for this UNIMARE_Type.
	 * 
	 * @param rapporto_Proroga_DataFine
	 */
	public void setRapporto_Proroga_DataFine(java.lang.String rapporto_Proroga_DataFine) {
		this.rapporto_Proroga_DataFine = rapporto_Proroga_DataFine;
	}

	/**
	 * Gets the rapporto_SocioLavoratore value for this UNIMARE_Type.
	 * 
	 * @return rapporto_SocioLavoratore
	 */
	public java.lang.String getRapporto_SocioLavoratore() {
		return rapporto_SocioLavoratore;
	}

	/**
	 * Sets the rapporto_SocioLavoratore value for this UNIMARE_Type.
	 * 
	 * @param rapporto_SocioLavoratore
	 */
	public void setRapporto_SocioLavoratore(java.lang.String rapporto_SocioLavoratore) {
		this.rapporto_SocioLavoratore = rapporto_SocioLavoratore;
	}

	/**
	 * Gets the rapporto_TipologiaContrattuale value for this UNIMARE_Type.
	 * 
	 * @return rapporto_TipologiaContrattuale
	 */
	public java.lang.String getRapporto_TipologiaContrattuale() {
		return rapporto_TipologiaContrattuale;
	}

	/**
	 * Sets the rapporto_TipologiaContrattuale value for this UNIMARE_Type.
	 * 
	 * @param rapporto_TipologiaContrattuale
	 */
	public void setRapporto_TipologiaContrattuale(java.lang.String rapporto_TipologiaContrattuale) {
		this.rapporto_TipologiaContrattuale = rapporto_TipologiaContrattuale;
	}

	/**
	 * Gets the rapporto_Trasformazione_CodiceTrasformazione value for this UNIMARE_Type.
	 * 
	 * @return rapporto_Trasformazione_CodiceTrasformazione
	 */
	public java.lang.String getRapporto_Trasformazione_CodiceTrasformazione() {
		return rapporto_Trasformazione_CodiceTrasformazione;
	}

	/**
	 * Sets the rapporto_Trasformazione_CodiceTrasformazione value for this UNIMARE_Type.
	 * 
	 * @param rapporto_Trasformazione_CodiceTrasformazione
	 */
	public void setRapporto_Trasformazione_CodiceTrasformazione(
			java.lang.String rapporto_Trasformazione_CodiceTrasformazione) {
		this.rapporto_Trasformazione_CodiceTrasformazione = rapporto_Trasformazione_CodiceTrasformazione;
	}

	/**
	 * Gets the rapporto_Trasformazione_Data value for this UNIMARE_Type.
	 * 
	 * @return rapporto_Trasformazione_Data
	 */
	public java.lang.String getRapporto_Trasformazione_Data() {
		return rapporto_Trasformazione_Data;
	}

	/**
	 * Sets the rapporto_Trasformazione_Data value for this UNIMARE_Type.
	 * 
	 * @param rapporto_Trasformazione_Data
	 */
	public void setRapporto_Trasformazione_Data(java.lang.String rapporto_Trasformazione_Data) {
		this.rapporto_Trasformazione_Data = rapporto_Trasformazione_Data;
	}

	/**
	 * Gets the SYS_DATARICEZIONE value for this UNIMARE_Type.
	 * 
	 * @return SYS_DATARICEZIONE
	 */
	public java.util.Calendar getSYS_DATARICEZIONE() {
		return SYS_DATARICEZIONE;
	}

	/**
	 * Sets the SYS_DATARICEZIONE value for this UNIMARE_Type.
	 * 
	 * @param SYS_DATARICEZIONE
	 */
	public void setSYS_DATARICEZIONE(java.util.Calendar SYS_DATARICEZIONE) {
		this.SYS_DATARICEZIONE = SYS_DATARICEZIONE;
	}

	/**
	 * Gets the SYS_DATARIFERIMENTO value for this UNIMARE_Type.
	 * 
	 * @return SYS_DATARIFERIMENTO
	 */
	public java.util.Calendar getSYS_DATARIFERIMENTO() {
		return SYS_DATARIFERIMENTO;
	}

	/**
	 * Sets the SYS_DATARIFERIMENTO value for this UNIMARE_Type.
	 * 
	 * @param SYS_DATARIFERIMENTO
	 */
	public void setSYS_DATARIFERIMENTO(java.util.Calendar SYS_DATARIFERIMENTO) {
		this.SYS_DATARIFERIMENTO = SYS_DATARIFERIMENTO;
	}

	/**
	 * Gets the SYS_TIPOMODULO value for this UNIMARE_Type.
	 * 
	 * @return SYS_TIPOMODULO
	 */
	public java.lang.String getSYS_TIPOMODULO() {
		return SYS_TIPOMODULO;
	}

	/**
	 * Sets the SYS_TIPOMODULO value for this UNIMARE_Type.
	 * 
	 * @param SYS_TIPOMODULO
	 */
	public void setSYS_TIPOMODULO(java.lang.String SYS_TIPOMODULO) {
		this.SYS_TIPOMODULO = SYS_TIPOMODULO;
	}

	/**
	 * Gets the rowOrder value for this UNIMARE_Type.
	 * 
	 * @return rowOrder
	 */
	public int getRowOrder() {
		return rowOrder;
	}

	/**
	 * Sets the rowOrder value for this UNIMARE_Type.
	 * 
	 * @param rowOrder
	 */
	public void setRowOrder(int rowOrder) {
		this.rowOrder = rowOrder;
	}

	private java.lang.Object __equalsCalc = null;

	public synchronized boolean equals(java.lang.Object obj) {
		if (!(obj instanceof UNIMARE_Type))
			return false;
		UNIMARE_Type other = (UNIMARE_Type) obj;
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
				&& ((this.datoreLavoro_Settore == null && other.getDatoreLavoro_Settore() == null)
						|| (this.datoreLavoro_Settore != null
								&& this.datoreLavoro_Settore.equals(other.getDatoreLavoro_Settore())))
				&& ((this.datoreLavoroDistaccatario_CodiceFiscale == null
						&& other.getDatoreLavoroDistaccatario_CodiceFiscale() == null)
						|| (this.datoreLavoroDistaccatario_CodiceFiscale != null
								&& this.datoreLavoroDistaccatario_CodiceFiscale
										.equals(other.getDatoreLavoroDistaccatario_CodiceFiscale())))
				&& ((this.datoreLavoro_SedeLavoro_NaveTipo == null
						&& other.getDatoreLavoro_SedeLavoro_NaveTipo() == null)
						|| (this.datoreLavoro_SedeLavoro_NaveTipo != null && this.datoreLavoro_SedeLavoro_NaveTipo
								.equals(other.getDatoreLavoro_SedeLavoro_NaveTipo())))
				&& ((this.datoreLavoro_SedeLavoro_NaveInternazionale == null
						&& other.getDatoreLavoro_SedeLavoro_NaveInternazionale() == null)
						|| (this.datoreLavoro_SedeLavoro_NaveInternazionale != null
								&& this.datoreLavoro_SedeLavoro_NaveInternazionale
										.equals(other.getDatoreLavoro_SedeLavoro_NaveInternazionale())))
				&& ((this.datoreLavoro_SedeLavoro_NaveStazzaMin5Ton == null
						&& other.getDatoreLavoro_SedeLavoro_NaveStazzaMin5Ton() == null)
						|| (this.datoreLavoro_SedeLavoro_NaveStazzaMin5Ton != null
								&& this.datoreLavoro_SedeLavoro_NaveStazzaMin5Ton
										.equals(other.getDatoreLavoro_SedeLavoro_NaveStazzaMin5Ton())))
				&& ((this.datoreLavoro_SedeLavoro_NaveDenominazione == null
						&& other.getDatoreLavoro_SedeLavoro_NaveDenominazione() == null)
						|| (this.datoreLavoro_SedeLavoro_NaveDenominazione != null
								&& this.datoreLavoro_SedeLavoro_NaveDenominazione
										.equals(other.getDatoreLavoro_SedeLavoro_NaveDenominazione())))
				&& ((this.datoreLavoro_SedeLavoro_NaveNIMO == null
						&& other.getDatoreLavoro_SedeLavoro_NaveNIMO() == null)
						|| (this.datoreLavoro_SedeLavoro_NaveNIMO != null && this.datoreLavoro_SedeLavoro_NaveNIMO
								.equals(other.getDatoreLavoro_SedeLavoro_NaveNIMO())))
				&& ((this.datoreLavoro_SedeLavoro_NaveNIscrizione == null
						&& other.getDatoreLavoro_SedeLavoro_NaveNIscrizione() == null)
						|| (this.datoreLavoro_SedeLavoro_NaveNIscrizione != null
								&& this.datoreLavoro_SedeLavoro_NaveNIscrizione
										.equals(other.getDatoreLavoro_SedeLavoro_NaveNIscrizione())))
				&& ((this.datoreLavoro_SedeLavoro_NaveLuogoIscr == null
						&& other.getDatoreLavoro_SedeLavoro_NaveLuogoIscr() == null)
						|| (this.datoreLavoro_SedeLavoro_NaveLuogoIscr != null
								&& this.datoreLavoro_SedeLavoro_NaveLuogoIscr
										.equals(other.getDatoreLavoro_SedeLavoro_NaveLuogoIscr())))
				&& ((this.datoreLavoro_RapportoTerraComandata == null
						&& other.getDatoreLavoro_RapportoTerraComandata() == null)
						|| (this.datoreLavoro_RapportoTerraComandata != null && this.datoreLavoro_RapportoTerraComandata
								.equals(other.getDatoreLavoro_RapportoTerraComandata())))
				&& ((this.datoreLavoro_SedeLavoro_TerraComuneNazione == null
						&& other.getDatoreLavoro_SedeLavoro_TerraComuneNazione() == null)
						|| (this.datoreLavoro_SedeLavoro_TerraComuneNazione != null
								&& this.datoreLavoro_SedeLavoro_TerraComuneNazione
										.equals(other.getDatoreLavoro_SedeLavoro_TerraComuneNazione())))
				&& ((this.datoreLavoro_SedeLavoro_TerraCAP == null
						&& other.getDatoreLavoro_SedeLavoro_TerraCAP() == null)
						|| (this.datoreLavoro_SedeLavoro_TerraCAP != null && this.datoreLavoro_SedeLavoro_TerraCAP
								.equals(other.getDatoreLavoro_SedeLavoro_TerraCAP())))
				&& ((this.datoreLavoro_SedeLavoro_TerraIndirizzo == null
						&& other.getDatoreLavoro_SedeLavoro_TerraIndirizzo() == null)
						|| (this.datoreLavoro_SedeLavoro_TerraIndirizzo != null
								&& this.datoreLavoro_SedeLavoro_TerraIndirizzo
										.equals(other.getDatoreLavoro_SedeLavoro_TerraIndirizzo())))
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
				&& ((this.lavoratore_CategoriaIscrizione == null && other.getLavoratore_CategoriaIscrizione() == null)
						|| (this.lavoratore_CategoriaIscrizione != null && this.lavoratore_CategoriaIscrizione
								.equals(other.getLavoratore_CategoriaIscrizione())))
				&& ((this.lavoratore_NumeroMatricola == null && other.getLavoratore_NumeroMatricola() == null)
						|| (this.lavoratore_NumeroMatricola != null
								&& this.lavoratore_NumeroMatricola.equals(other.getLavoratore_NumeroMatricola())))
				&& ((this.lavoratore_LuogoMatricola == null && other.getLavoratore_LuogoMatricola() == null)
						|| (this.lavoratore_LuogoMatricola != null
								&& this.lavoratore_LuogoMatricola.equals(other.getLavoratore_LuogoMatricola())))
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
				&& ((this.rapporto_ContrattoVerbale == null && other.getRapporto_ContrattoVerbale() == null)
						|| (this.rapporto_ContrattoVerbale != null
								&& this.rapporto_ContrattoVerbale.equals(other.getRapporto_ContrattoVerbale())))
				&& ((this.rapporto_DataFine == null && other.getRapporto_DataFine() == null)
						|| (this.rapporto_DataFine != null
								&& this.rapporto_DataFine.equals(other.getRapporto_DataFine())))
				&& ((this.rapporto_DataInizio == null && other.getRapporto_DataInizio() == null)
						|| (this.rapporto_DataInizio != null
								&& this.rapporto_DataInizio.equals(other.getRapporto_DataInizio())))
				&& ((this.rapporto_EntePrevidenziale == null && other.getRapporto_EntePrevidenziale() == null)
						|| (this.rapporto_EntePrevidenziale != null
								&& this.rapporto_EntePrevidenziale.equals(other.getRapporto_EntePrevidenziale())))
				&& ((this.rapporto_Proroga_DataFine == null && other.getRapporto_Proroga_DataFine() == null)
						|| (this.rapporto_Proroga_DataFine != null
								&& this.rapporto_Proroga_DataFine.equals(other.getRapporto_Proroga_DataFine())))
				&& ((this.rapporto_SocioLavoratore == null && other.getRapporto_SocioLavoratore() == null)
						|| (this.rapporto_SocioLavoratore != null
								&& this.rapporto_SocioLavoratore.equals(other.getRapporto_SocioLavoratore())))
				&& ((this.rapporto_TipologiaContrattuale == null && other.getRapporto_TipologiaContrattuale() == null)
						|| (this.rapporto_TipologiaContrattuale != null && this.rapporto_TipologiaContrattuale
								.equals(other.getRapporto_TipologiaContrattuale())))
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
		if (getDatoreLavoro_Settore() != null) {
			_hashCode += getDatoreLavoro_Settore().hashCode();
		}
		if (getDatoreLavoroDistaccatario_CodiceFiscale() != null) {
			_hashCode += getDatoreLavoroDistaccatario_CodiceFiscale().hashCode();
		}
		if (getDatoreLavoro_SedeLavoro_NaveTipo() != null) {
			_hashCode += getDatoreLavoro_SedeLavoro_NaveTipo().hashCode();
		}
		if (getDatoreLavoro_SedeLavoro_NaveInternazionale() != null) {
			_hashCode += getDatoreLavoro_SedeLavoro_NaveInternazionale().hashCode();
		}
		if (getDatoreLavoro_SedeLavoro_NaveStazzaMin5Ton() != null) {
			_hashCode += getDatoreLavoro_SedeLavoro_NaveStazzaMin5Ton().hashCode();
		}
		if (getDatoreLavoro_SedeLavoro_NaveDenominazione() != null) {
			_hashCode += getDatoreLavoro_SedeLavoro_NaveDenominazione().hashCode();
		}
		if (getDatoreLavoro_SedeLavoro_NaveNIMO() != null) {
			_hashCode += getDatoreLavoro_SedeLavoro_NaveNIMO().hashCode();
		}
		if (getDatoreLavoro_SedeLavoro_NaveNIscrizione() != null) {
			_hashCode += getDatoreLavoro_SedeLavoro_NaveNIscrizione().hashCode();
		}
		if (getDatoreLavoro_SedeLavoro_NaveLuogoIscr() != null) {
			_hashCode += getDatoreLavoro_SedeLavoro_NaveLuogoIscr().hashCode();
		}
		if (getDatoreLavoro_RapportoTerraComandata() != null) {
			_hashCode += getDatoreLavoro_RapportoTerraComandata().hashCode();
		}
		if (getDatoreLavoro_SedeLavoro_TerraComuneNazione() != null) {
			_hashCode += getDatoreLavoro_SedeLavoro_TerraComuneNazione().hashCode();
		}
		if (getDatoreLavoro_SedeLavoro_TerraCAP() != null) {
			_hashCode += getDatoreLavoro_SedeLavoro_TerraCAP().hashCode();
		}
		if (getDatoreLavoro_SedeLavoro_TerraIndirizzo() != null) {
			_hashCode += getDatoreLavoro_SedeLavoro_TerraIndirizzo().hashCode();
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
		if (getLavoratore_CategoriaIscrizione() != null) {
			_hashCode += getLavoratore_CategoriaIscrizione().hashCode();
		}
		if (getLavoratore_NumeroMatricola() != null) {
			_hashCode += getLavoratore_NumeroMatricola().hashCode();
		}
		if (getLavoratore_LuogoMatricola() != null) {
			_hashCode += getLavoratore_LuogoMatricola().hashCode();
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
		if (getRapporto_ContrattoVerbale() != null) {
			_hashCode += getRapporto_ContrattoVerbale().hashCode();
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
		if (getRapporto_Proroga_DataFine() != null) {
			_hashCode += getRapporto_Proroga_DataFine().hashCode();
		}
		if (getRapporto_SocioLavoratore() != null) {
			_hashCode += getRapporto_SocioLavoratore().hashCode();
		}
		if (getRapporto_TipologiaContrattuale() != null) {
			_hashCode += getRapporto_TipologiaContrattuale().hashCode();
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
			UNIMARE_Type.class, true);

	static {
		typeDesc.setXmlType(
				new javax.xml.namespace.QName("http://servizi.lavoro.gov.it/RicercaCO/2.0", "UNIMARE_Type"));
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
		elemField.setNillable(false);
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
		elemField.setFieldName("datoreLavoro_SedeLavoro_NaveTipo");
		elemField.setXmlName(new javax.xml.namespace.QName("http://servizi.lavoro.gov.it/RicercaCO/2.0",
				"DatoreLavoro_SedeLavoro_NaveTipo"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
		elemField.setNillable(true);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("datoreLavoro_SedeLavoro_NaveInternazionale");
		elemField.setXmlName(new javax.xml.namespace.QName("http://servizi.lavoro.gov.it/RicercaCO/2.0",
				"DatoreLavoro_SedeLavoro_NaveInternazionale"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
		elemField.setNillable(true);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("datoreLavoro_SedeLavoro_NaveStazzaMin5Ton");
		elemField.setXmlName(new javax.xml.namespace.QName("http://servizi.lavoro.gov.it/RicercaCO/2.0",
				"DatoreLavoro_SedeLavoro_NaveStazzaMin5Ton"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
		elemField.setNillable(true);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("datoreLavoro_SedeLavoro_NaveDenominazione");
		elemField.setXmlName(new javax.xml.namespace.QName("http://servizi.lavoro.gov.it/RicercaCO/2.0",
				"DatoreLavoro_SedeLavoro_NaveDenominazione"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
		elemField.setNillable(true);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("datoreLavoro_SedeLavoro_NaveNIMO");
		elemField.setXmlName(new javax.xml.namespace.QName("http://servizi.lavoro.gov.it/RicercaCO/2.0",
				"DatoreLavoro_SedeLavoro_NaveNIMO"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
		elemField.setNillable(true);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("datoreLavoro_SedeLavoro_NaveNIscrizione");
		elemField.setXmlName(new javax.xml.namespace.QName("http://servizi.lavoro.gov.it/RicercaCO/2.0",
				"DatoreLavoro_SedeLavoro_NaveNIscrizione"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
		elemField.setNillable(true);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("datoreLavoro_SedeLavoro_NaveLuogoIscr");
		elemField.setXmlName(new javax.xml.namespace.QName("http://servizi.lavoro.gov.it/RicercaCO/2.0",
				"DatoreLavoro_SedeLavoro_NaveLuogoIscr"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
		elemField.setNillable(true);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("datoreLavoro_RapportoTerraComandata");
		elemField.setXmlName(new javax.xml.namespace.QName("http://servizi.lavoro.gov.it/RicercaCO/2.0",
				"DatoreLavoro_RapportoTerraComandata"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
		elemField.setNillable(true);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("datoreLavoro_SedeLavoro_TerraComuneNazione");
		elemField.setXmlName(new javax.xml.namespace.QName("http://servizi.lavoro.gov.it/RicercaCO/2.0",
				"DatoreLavoro_SedeLavoro_TerraComuneNazione"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
		elemField.setNillable(true);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("datoreLavoro_SedeLavoro_TerraCAP");
		elemField.setXmlName(new javax.xml.namespace.QName("http://servizi.lavoro.gov.it/RicercaCO/2.0",
				"DatoreLavoro_SedeLavoro_TerraCAP"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
		elemField.setNillable(true);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("datoreLavoro_SedeLavoro_TerraIndirizzo");
		elemField.setXmlName(new javax.xml.namespace.QName("http://servizi.lavoro.gov.it/RicercaCO/2.0",
				"DatoreLavoro_SedeLavoro_TerraIndirizzo"));
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
		elemField.setFieldName("lavoratore_CategoriaIscrizione");
		elemField.setXmlName(new javax.xml.namespace.QName("http://servizi.lavoro.gov.it/RicercaCO/2.0",
				"Lavoratore_CategoriaIscrizione"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
		elemField.setNillable(true);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("lavoratore_NumeroMatricola");
		elemField.setXmlName(new javax.xml.namespace.QName("http://servizi.lavoro.gov.it/RicercaCO/2.0",
				"Lavoratore_NumeroMatricola"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
		elemField.setNillable(true);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("lavoratore_LuogoMatricola");
		elemField.setXmlName(new javax.xml.namespace.QName("http://servizi.lavoro.gov.it/RicercaCO/2.0",
				"Lavoratore_LuogoMatricola"));
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
		elemField.setFieldName("rapporto_ContrattoVerbale");
		elemField.setXmlName(new javax.xml.namespace.QName("http://servizi.lavoro.gov.it/RicercaCO/2.0",
				"Rapporto_ContrattoVerbale"));
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
