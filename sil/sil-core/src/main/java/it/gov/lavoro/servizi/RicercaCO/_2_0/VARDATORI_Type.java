/**
 * VARDATORI_Type.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package it.gov.lavoro.servizi.RicercaCO._2_0;

public class VARDATORI_Type implements java.io.Serializable {
	private java.lang.String datoreLavoro_CodiceFiscale;

	private java.lang.String datoreLavoro_Denominazione;

	private java.lang.String datoreLavoro_PA;

	private java.lang.String datoreLavoro_SedeLavoro_CAP;

	private java.lang.String datoreLavoro_SedeLavoro_ComuneNazione;

	private java.lang.String datoreLavoro_SedeLavoro_Indirizzo;

	private java.lang.String datoreLavoro_Settore;

	private java.lang.String datoreLavoroPrecedente_CodiceFiscale;

	private java.lang.String datoreLavoroPrecedente_Denominazione;

	private java.lang.String datoreLavoroPrecedente_Settore;

	private java.lang.String trasferimentoAzienda_CodiceTrasferimento;

	private java.lang.String trasferimentoAzienda_DataInizio;

	private java.lang.String invio_CodiceComunicazione;

	private java.lang.String invio_CodiceComunicazionePrecedente;

	private java.lang.String lavoratore_CodiceFiscale;

	private java.lang.String lavoratore_Cognome;

	private java.lang.String lavoratore_Nome;

	private java.lang.String rapporto_Agevolazioni;

	private java.lang.String rapporto_CCNL;

	private java.lang.String rapporto_DataFine;

	private java.lang.String rapporto_DataInizio;

	private java.lang.String rapporto_EntePrevidenziale;

	private java.lang.String rapporto_LavoroAgricoltura;

	private java.lang.String rapporto_SocioLavoratore;

	private java.lang.String rapporto_TipologiaContrattuale;

	private java.util.Calendar SYS_DATARICEZIONE;

	private java.util.Calendar SYS_DATARIFERIMENTO;

	private java.lang.String SYS_TIPOMODULO;

	private int rowOrder; // attribute

	public VARDATORI_Type() {
	}

	public VARDATORI_Type(java.lang.String datoreLavoro_CodiceFiscale, java.lang.String datoreLavoro_Denominazione,
			java.lang.String datoreLavoro_PA, java.lang.String datoreLavoro_SedeLavoro_CAP,
			java.lang.String datoreLavoro_SedeLavoro_ComuneNazione, java.lang.String datoreLavoro_SedeLavoro_Indirizzo,
			java.lang.String datoreLavoro_Settore, java.lang.String datoreLavoroPrecedente_CodiceFiscale,
			java.lang.String datoreLavoroPrecedente_Denominazione, java.lang.String datoreLavoroPrecedente_Settore,
			java.lang.String trasferimentoAzienda_CodiceTrasferimento, java.lang.String trasferimentoAzienda_DataInizio,
			java.lang.String invio_CodiceComunicazione, java.lang.String invio_CodiceComunicazionePrecedente,
			java.lang.String lavoratore_CodiceFiscale, java.lang.String lavoratore_Cognome,
			java.lang.String lavoratore_Nome, java.lang.String rapporto_Agevolazioni, java.lang.String rapporto_CCNL,
			java.lang.String rapporto_DataFine, java.lang.String rapporto_DataInizio,
			java.lang.String rapporto_EntePrevidenziale, java.lang.String rapporto_LavoroAgricoltura,
			java.lang.String rapporto_SocioLavoratore, java.lang.String rapporto_TipologiaContrattuale,
			java.util.Calendar SYS_DATARICEZIONE, java.util.Calendar SYS_DATARIFERIMENTO,
			java.lang.String SYS_TIPOMODULO, int rowOrder) {
		this.datoreLavoro_CodiceFiscale = datoreLavoro_CodiceFiscale;
		this.datoreLavoro_Denominazione = datoreLavoro_Denominazione;
		this.datoreLavoro_PA = datoreLavoro_PA;
		this.datoreLavoro_SedeLavoro_CAP = datoreLavoro_SedeLavoro_CAP;
		this.datoreLavoro_SedeLavoro_ComuneNazione = datoreLavoro_SedeLavoro_ComuneNazione;
		this.datoreLavoro_SedeLavoro_Indirizzo = datoreLavoro_SedeLavoro_Indirizzo;
		this.datoreLavoro_Settore = datoreLavoro_Settore;
		this.datoreLavoroPrecedente_CodiceFiscale = datoreLavoroPrecedente_CodiceFiscale;
		this.datoreLavoroPrecedente_Denominazione = datoreLavoroPrecedente_Denominazione;
		this.datoreLavoroPrecedente_Settore = datoreLavoroPrecedente_Settore;
		this.trasferimentoAzienda_CodiceTrasferimento = trasferimentoAzienda_CodiceTrasferimento;
		this.trasferimentoAzienda_DataInizio = trasferimentoAzienda_DataInizio;
		this.invio_CodiceComunicazione = invio_CodiceComunicazione;
		this.invio_CodiceComunicazionePrecedente = invio_CodiceComunicazionePrecedente;
		this.lavoratore_CodiceFiscale = lavoratore_CodiceFiscale;
		this.lavoratore_Cognome = lavoratore_Cognome;
		this.lavoratore_Nome = lavoratore_Nome;
		this.rapporto_Agevolazioni = rapporto_Agevolazioni;
		this.rapporto_CCNL = rapporto_CCNL;
		this.rapporto_DataFine = rapporto_DataFine;
		this.rapporto_DataInizio = rapporto_DataInizio;
		this.rapporto_EntePrevidenziale = rapporto_EntePrevidenziale;
		this.rapporto_LavoroAgricoltura = rapporto_LavoroAgricoltura;
		this.rapporto_SocioLavoratore = rapporto_SocioLavoratore;
		this.rapporto_TipologiaContrattuale = rapporto_TipologiaContrattuale;
		this.SYS_DATARICEZIONE = SYS_DATARICEZIONE;
		this.SYS_DATARIFERIMENTO = SYS_DATARIFERIMENTO;
		this.SYS_TIPOMODULO = SYS_TIPOMODULO;
		this.rowOrder = rowOrder;
	}

	/**
	 * Gets the datoreLavoro_CodiceFiscale value for this VARDATORI_Type.
	 * 
	 * @return datoreLavoro_CodiceFiscale
	 */
	public java.lang.String getDatoreLavoro_CodiceFiscale() {
		return datoreLavoro_CodiceFiscale;
	}

	/**
	 * Sets the datoreLavoro_CodiceFiscale value for this VARDATORI_Type.
	 * 
	 * @param datoreLavoro_CodiceFiscale
	 */
	public void setDatoreLavoro_CodiceFiscale(java.lang.String datoreLavoro_CodiceFiscale) {
		this.datoreLavoro_CodiceFiscale = datoreLavoro_CodiceFiscale;
	}

	/**
	 * Gets the datoreLavoro_Denominazione value for this VARDATORI_Type.
	 * 
	 * @return datoreLavoro_Denominazione
	 */
	public java.lang.String getDatoreLavoro_Denominazione() {
		return datoreLavoro_Denominazione;
	}

	/**
	 * Sets the datoreLavoro_Denominazione value for this VARDATORI_Type.
	 * 
	 * @param datoreLavoro_Denominazione
	 */
	public void setDatoreLavoro_Denominazione(java.lang.String datoreLavoro_Denominazione) {
		this.datoreLavoro_Denominazione = datoreLavoro_Denominazione;
	}

	/**
	 * Gets the datoreLavoro_PA value for this VARDATORI_Type.
	 * 
	 * @return datoreLavoro_PA
	 */
	public java.lang.String getDatoreLavoro_PA() {
		return datoreLavoro_PA;
	}

	/**
	 * Sets the datoreLavoro_PA value for this VARDATORI_Type.
	 * 
	 * @param datoreLavoro_PA
	 */
	public void setDatoreLavoro_PA(java.lang.String datoreLavoro_PA) {
		this.datoreLavoro_PA = datoreLavoro_PA;
	}

	/**
	 * Gets the datoreLavoro_SedeLavoro_CAP value for this VARDATORI_Type.
	 * 
	 * @return datoreLavoro_SedeLavoro_CAP
	 */
	public java.lang.String getDatoreLavoro_SedeLavoro_CAP() {
		return datoreLavoro_SedeLavoro_CAP;
	}

	/**
	 * Sets the datoreLavoro_SedeLavoro_CAP value for this VARDATORI_Type.
	 * 
	 * @param datoreLavoro_SedeLavoro_CAP
	 */
	public void setDatoreLavoro_SedeLavoro_CAP(java.lang.String datoreLavoro_SedeLavoro_CAP) {
		this.datoreLavoro_SedeLavoro_CAP = datoreLavoro_SedeLavoro_CAP;
	}

	/**
	 * Gets the datoreLavoro_SedeLavoro_ComuneNazione value for this VARDATORI_Type.
	 * 
	 * @return datoreLavoro_SedeLavoro_ComuneNazione
	 */
	public java.lang.String getDatoreLavoro_SedeLavoro_ComuneNazione() {
		return datoreLavoro_SedeLavoro_ComuneNazione;
	}

	/**
	 * Sets the datoreLavoro_SedeLavoro_ComuneNazione value for this VARDATORI_Type.
	 * 
	 * @param datoreLavoro_SedeLavoro_ComuneNazione
	 */
	public void setDatoreLavoro_SedeLavoro_ComuneNazione(java.lang.String datoreLavoro_SedeLavoro_ComuneNazione) {
		this.datoreLavoro_SedeLavoro_ComuneNazione = datoreLavoro_SedeLavoro_ComuneNazione;
	}

	/**
	 * Gets the datoreLavoro_SedeLavoro_Indirizzo value for this VARDATORI_Type.
	 * 
	 * @return datoreLavoro_SedeLavoro_Indirizzo
	 */
	public java.lang.String getDatoreLavoro_SedeLavoro_Indirizzo() {
		return datoreLavoro_SedeLavoro_Indirizzo;
	}

	/**
	 * Sets the datoreLavoro_SedeLavoro_Indirizzo value for this VARDATORI_Type.
	 * 
	 * @param datoreLavoro_SedeLavoro_Indirizzo
	 */
	public void setDatoreLavoro_SedeLavoro_Indirizzo(java.lang.String datoreLavoro_SedeLavoro_Indirizzo) {
		this.datoreLavoro_SedeLavoro_Indirizzo = datoreLavoro_SedeLavoro_Indirizzo;
	}

	/**
	 * Gets the datoreLavoro_Settore value for this VARDATORI_Type.
	 * 
	 * @return datoreLavoro_Settore
	 */
	public java.lang.String getDatoreLavoro_Settore() {
		return datoreLavoro_Settore;
	}

	/**
	 * Sets the datoreLavoro_Settore value for this VARDATORI_Type.
	 * 
	 * @param datoreLavoro_Settore
	 */
	public void setDatoreLavoro_Settore(java.lang.String datoreLavoro_Settore) {
		this.datoreLavoro_Settore = datoreLavoro_Settore;
	}

	/**
	 * Gets the datoreLavoroPrecedente_CodiceFiscale value for this VARDATORI_Type.
	 * 
	 * @return datoreLavoroPrecedente_CodiceFiscale
	 */
	public java.lang.String getDatoreLavoroPrecedente_CodiceFiscale() {
		return datoreLavoroPrecedente_CodiceFiscale;
	}

	/**
	 * Sets the datoreLavoroPrecedente_CodiceFiscale value for this VARDATORI_Type.
	 * 
	 * @param datoreLavoroPrecedente_CodiceFiscale
	 */
	public void setDatoreLavoroPrecedente_CodiceFiscale(java.lang.String datoreLavoroPrecedente_CodiceFiscale) {
		this.datoreLavoroPrecedente_CodiceFiscale = datoreLavoroPrecedente_CodiceFiscale;
	}

	/**
	 * Gets the datoreLavoroPrecedente_Denominazione value for this VARDATORI_Type.
	 * 
	 * @return datoreLavoroPrecedente_Denominazione
	 */
	public java.lang.String getDatoreLavoroPrecedente_Denominazione() {
		return datoreLavoroPrecedente_Denominazione;
	}

	/**
	 * Sets the datoreLavoroPrecedente_Denominazione value for this VARDATORI_Type.
	 * 
	 * @param datoreLavoroPrecedente_Denominazione
	 */
	public void setDatoreLavoroPrecedente_Denominazione(java.lang.String datoreLavoroPrecedente_Denominazione) {
		this.datoreLavoroPrecedente_Denominazione = datoreLavoroPrecedente_Denominazione;
	}

	/**
	 * Gets the datoreLavoroPrecedente_Settore value for this VARDATORI_Type.
	 * 
	 * @return datoreLavoroPrecedente_Settore
	 */
	public java.lang.String getDatoreLavoroPrecedente_Settore() {
		return datoreLavoroPrecedente_Settore;
	}

	/**
	 * Sets the datoreLavoroPrecedente_Settore value for this VARDATORI_Type.
	 * 
	 * @param datoreLavoroPrecedente_Settore
	 */
	public void setDatoreLavoroPrecedente_Settore(java.lang.String datoreLavoroPrecedente_Settore) {
		this.datoreLavoroPrecedente_Settore = datoreLavoroPrecedente_Settore;
	}

	/**
	 * Gets the trasferimentoAzienda_CodiceTrasferimento value for this VARDATORI_Type.
	 * 
	 * @return trasferimentoAzienda_CodiceTrasferimento
	 */
	public java.lang.String getTrasferimentoAzienda_CodiceTrasferimento() {
		return trasferimentoAzienda_CodiceTrasferimento;
	}

	/**
	 * Sets the trasferimentoAzienda_CodiceTrasferimento value for this VARDATORI_Type.
	 * 
	 * @param trasferimentoAzienda_CodiceTrasferimento
	 */
	public void setTrasferimentoAzienda_CodiceTrasferimento(java.lang.String trasferimentoAzienda_CodiceTrasferimento) {
		this.trasferimentoAzienda_CodiceTrasferimento = trasferimentoAzienda_CodiceTrasferimento;
	}

	/**
	 * Gets the trasferimentoAzienda_DataInizio value for this VARDATORI_Type.
	 * 
	 * @return trasferimentoAzienda_DataInizio
	 */
	public java.lang.String getTrasferimentoAzienda_DataInizio() {
		return trasferimentoAzienda_DataInizio;
	}

	/**
	 * Sets the trasferimentoAzienda_DataInizio value for this VARDATORI_Type.
	 * 
	 * @param trasferimentoAzienda_DataInizio
	 */
	public void setTrasferimentoAzienda_DataInizio(java.lang.String trasferimentoAzienda_DataInizio) {
		this.trasferimentoAzienda_DataInizio = trasferimentoAzienda_DataInizio;
	}

	/**
	 * Gets the invio_CodiceComunicazione value for this VARDATORI_Type.
	 * 
	 * @return invio_CodiceComunicazione
	 */
	public java.lang.String getInvio_CodiceComunicazione() {
		return invio_CodiceComunicazione;
	}

	/**
	 * Sets the invio_CodiceComunicazione value for this VARDATORI_Type.
	 * 
	 * @param invio_CodiceComunicazione
	 */
	public void setInvio_CodiceComunicazione(java.lang.String invio_CodiceComunicazione) {
		this.invio_CodiceComunicazione = invio_CodiceComunicazione;
	}

	/**
	 * Gets the invio_CodiceComunicazionePrecedente value for this VARDATORI_Type.
	 * 
	 * @return invio_CodiceComunicazionePrecedente
	 */
	public java.lang.String getInvio_CodiceComunicazionePrecedente() {
		return invio_CodiceComunicazionePrecedente;
	}

	/**
	 * Sets the invio_CodiceComunicazionePrecedente value for this VARDATORI_Type.
	 * 
	 * @param invio_CodiceComunicazionePrecedente
	 */
	public void setInvio_CodiceComunicazionePrecedente(java.lang.String invio_CodiceComunicazionePrecedente) {
		this.invio_CodiceComunicazionePrecedente = invio_CodiceComunicazionePrecedente;
	}

	/**
	 * Gets the lavoratore_CodiceFiscale value for this VARDATORI_Type.
	 * 
	 * @return lavoratore_CodiceFiscale
	 */
	public java.lang.String getLavoratore_CodiceFiscale() {
		return lavoratore_CodiceFiscale;
	}

	/**
	 * Sets the lavoratore_CodiceFiscale value for this VARDATORI_Type.
	 * 
	 * @param lavoratore_CodiceFiscale
	 */
	public void setLavoratore_CodiceFiscale(java.lang.String lavoratore_CodiceFiscale) {
		this.lavoratore_CodiceFiscale = lavoratore_CodiceFiscale;
	}

	/**
	 * Gets the lavoratore_Cognome value for this VARDATORI_Type.
	 * 
	 * @return lavoratore_Cognome
	 */
	public java.lang.String getLavoratore_Cognome() {
		return lavoratore_Cognome;
	}

	/**
	 * Sets the lavoratore_Cognome value for this VARDATORI_Type.
	 * 
	 * @param lavoratore_Cognome
	 */
	public void setLavoratore_Cognome(java.lang.String lavoratore_Cognome) {
		this.lavoratore_Cognome = lavoratore_Cognome;
	}

	/**
	 * Gets the lavoratore_Nome value for this VARDATORI_Type.
	 * 
	 * @return lavoratore_Nome
	 */
	public java.lang.String getLavoratore_Nome() {
		return lavoratore_Nome;
	}

	/**
	 * Sets the lavoratore_Nome value for this VARDATORI_Type.
	 * 
	 * @param lavoratore_Nome
	 */
	public void setLavoratore_Nome(java.lang.String lavoratore_Nome) {
		this.lavoratore_Nome = lavoratore_Nome;
	}

	/**
	 * Gets the rapporto_Agevolazioni value for this VARDATORI_Type.
	 * 
	 * @return rapporto_Agevolazioni
	 */
	public java.lang.String getRapporto_Agevolazioni() {
		return rapporto_Agevolazioni;
	}

	/**
	 * Sets the rapporto_Agevolazioni value for this VARDATORI_Type.
	 * 
	 * @param rapporto_Agevolazioni
	 */
	public void setRapporto_Agevolazioni(java.lang.String rapporto_Agevolazioni) {
		this.rapporto_Agevolazioni = rapporto_Agevolazioni;
	}

	/**
	 * Gets the rapporto_CCNL value for this VARDATORI_Type.
	 * 
	 * @return rapporto_CCNL
	 */
	public java.lang.String getRapporto_CCNL() {
		return rapporto_CCNL;
	}

	/**
	 * Sets the rapporto_CCNL value for this VARDATORI_Type.
	 * 
	 * @param rapporto_CCNL
	 */
	public void setRapporto_CCNL(java.lang.String rapporto_CCNL) {
		this.rapporto_CCNL = rapporto_CCNL;
	}

	/**
	 * Gets the rapporto_DataFine value for this VARDATORI_Type.
	 * 
	 * @return rapporto_DataFine
	 */
	public java.lang.String getRapporto_DataFine() {
		return rapporto_DataFine;
	}

	/**
	 * Sets the rapporto_DataFine value for this VARDATORI_Type.
	 * 
	 * @param rapporto_DataFine
	 */
	public void setRapporto_DataFine(java.lang.String rapporto_DataFine) {
		this.rapporto_DataFine = rapporto_DataFine;
	}

	/**
	 * Gets the rapporto_DataInizio value for this VARDATORI_Type.
	 * 
	 * @return rapporto_DataInizio
	 */
	public java.lang.String getRapporto_DataInizio() {
		return rapporto_DataInizio;
	}

	/**
	 * Sets the rapporto_DataInizio value for this VARDATORI_Type.
	 * 
	 * @param rapporto_DataInizio
	 */
	public void setRapporto_DataInizio(java.lang.String rapporto_DataInizio) {
		this.rapporto_DataInizio = rapporto_DataInizio;
	}

	/**
	 * Gets the rapporto_EntePrevidenziale value for this VARDATORI_Type.
	 * 
	 * @return rapporto_EntePrevidenziale
	 */
	public java.lang.String getRapporto_EntePrevidenziale() {
		return rapporto_EntePrevidenziale;
	}

	/**
	 * Sets the rapporto_EntePrevidenziale value for this VARDATORI_Type.
	 * 
	 * @param rapporto_EntePrevidenziale
	 */
	public void setRapporto_EntePrevidenziale(java.lang.String rapporto_EntePrevidenziale) {
		this.rapporto_EntePrevidenziale = rapporto_EntePrevidenziale;
	}

	/**
	 * Gets the rapporto_LavoroAgricoltura value for this VARDATORI_Type.
	 * 
	 * @return rapporto_LavoroAgricoltura
	 */
	public java.lang.String getRapporto_LavoroAgricoltura() {
		return rapporto_LavoroAgricoltura;
	}

	/**
	 * Sets the rapporto_LavoroAgricoltura value for this VARDATORI_Type.
	 * 
	 * @param rapporto_LavoroAgricoltura
	 */
	public void setRapporto_LavoroAgricoltura(java.lang.String rapporto_LavoroAgricoltura) {
		this.rapporto_LavoroAgricoltura = rapporto_LavoroAgricoltura;
	}

	/**
	 * Gets the rapporto_SocioLavoratore value for this VARDATORI_Type.
	 * 
	 * @return rapporto_SocioLavoratore
	 */
	public java.lang.String getRapporto_SocioLavoratore() {
		return rapporto_SocioLavoratore;
	}

	/**
	 * Sets the rapporto_SocioLavoratore value for this VARDATORI_Type.
	 * 
	 * @param rapporto_SocioLavoratore
	 */
	public void setRapporto_SocioLavoratore(java.lang.String rapporto_SocioLavoratore) {
		this.rapporto_SocioLavoratore = rapporto_SocioLavoratore;
	}

	/**
	 * Gets the rapporto_TipologiaContrattuale value for this VARDATORI_Type.
	 * 
	 * @return rapporto_TipologiaContrattuale
	 */
	public java.lang.String getRapporto_TipologiaContrattuale() {
		return rapporto_TipologiaContrattuale;
	}

	/**
	 * Sets the rapporto_TipologiaContrattuale value for this VARDATORI_Type.
	 * 
	 * @param rapporto_TipologiaContrattuale
	 */
	public void setRapporto_TipologiaContrattuale(java.lang.String rapporto_TipologiaContrattuale) {
		this.rapporto_TipologiaContrattuale = rapporto_TipologiaContrattuale;
	}

	/**
	 * Gets the SYS_DATARICEZIONE value for this VARDATORI_Type.
	 * 
	 * @return SYS_DATARICEZIONE
	 */
	public java.util.Calendar getSYS_DATARICEZIONE() {
		return SYS_DATARICEZIONE;
	}

	/**
	 * Sets the SYS_DATARICEZIONE value for this VARDATORI_Type.
	 * 
	 * @param SYS_DATARICEZIONE
	 */
	public void setSYS_DATARICEZIONE(java.util.Calendar SYS_DATARICEZIONE) {
		this.SYS_DATARICEZIONE = SYS_DATARICEZIONE;
	}

	/**
	 * Gets the SYS_DATARIFERIMENTO value for this VARDATORI_Type.
	 * 
	 * @return SYS_DATARIFERIMENTO
	 */
	public java.util.Calendar getSYS_DATARIFERIMENTO() {
		return SYS_DATARIFERIMENTO;
	}

	/**
	 * Sets the SYS_DATARIFERIMENTO value for this VARDATORI_Type.
	 * 
	 * @param SYS_DATARIFERIMENTO
	 */
	public void setSYS_DATARIFERIMENTO(java.util.Calendar SYS_DATARIFERIMENTO) {
		this.SYS_DATARIFERIMENTO = SYS_DATARIFERIMENTO;
	}

	/**
	 * Gets the SYS_TIPOMODULO value for this VARDATORI_Type.
	 * 
	 * @return SYS_TIPOMODULO
	 */
	public java.lang.String getSYS_TIPOMODULO() {
		return SYS_TIPOMODULO;
	}

	/**
	 * Sets the SYS_TIPOMODULO value for this VARDATORI_Type.
	 * 
	 * @param SYS_TIPOMODULO
	 */
	public void setSYS_TIPOMODULO(java.lang.String SYS_TIPOMODULO) {
		this.SYS_TIPOMODULO = SYS_TIPOMODULO;
	}

	/**
	 * Gets the rowOrder value for this VARDATORI_Type.
	 * 
	 * @return rowOrder
	 */
	public int getRowOrder() {
		return rowOrder;
	}

	/**
	 * Sets the rowOrder value for this VARDATORI_Type.
	 * 
	 * @param rowOrder
	 */
	public void setRowOrder(int rowOrder) {
		this.rowOrder = rowOrder;
	}

	private java.lang.Object __equalsCalc = null;

	public synchronized boolean equals(java.lang.Object obj) {
		if (!(obj instanceof VARDATORI_Type))
			return false;
		VARDATORI_Type other = (VARDATORI_Type) obj;
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
				&& ((this.datoreLavoroPrecedente_CodiceFiscale == null
						&& other.getDatoreLavoroPrecedente_CodiceFiscale() == null)
						|| (this.datoreLavoroPrecedente_CodiceFiscale != null
								&& this.datoreLavoroPrecedente_CodiceFiscale
										.equals(other.getDatoreLavoroPrecedente_CodiceFiscale())))
				&& ((this.datoreLavoroPrecedente_Denominazione == null
						&& other.getDatoreLavoroPrecedente_Denominazione() == null)
						|| (this.datoreLavoroPrecedente_Denominazione != null
								&& this.datoreLavoroPrecedente_Denominazione
										.equals(other.getDatoreLavoroPrecedente_Denominazione())))
				&& ((this.datoreLavoroPrecedente_Settore == null && other.getDatoreLavoroPrecedente_Settore() == null)
						|| (this.datoreLavoroPrecedente_Settore != null && this.datoreLavoroPrecedente_Settore
								.equals(other.getDatoreLavoroPrecedente_Settore())))
				&& ((this.trasferimentoAzienda_CodiceTrasferimento == null
						&& other.getTrasferimentoAzienda_CodiceTrasferimento() == null)
						|| (this.trasferimentoAzienda_CodiceTrasferimento != null
								&& this.trasferimentoAzienda_CodiceTrasferimento
										.equals(other.getTrasferimentoAzienda_CodiceTrasferimento())))
				&& ((this.trasferimentoAzienda_DataInizio == null && other.getTrasferimentoAzienda_DataInizio() == null)
						|| (this.trasferimentoAzienda_DataInizio != null && this.trasferimentoAzienda_DataInizio
								.equals(other.getTrasferimentoAzienda_DataInizio())))
				&& ((this.invio_CodiceComunicazione == null && other.getInvio_CodiceComunicazione() == null)
						|| (this.invio_CodiceComunicazione != null
								&& this.invio_CodiceComunicazione.equals(other.getInvio_CodiceComunicazione())))
				&& ((this.invio_CodiceComunicazionePrecedente == null
						&& other.getInvio_CodiceComunicazionePrecedente() == null)
						|| (this.invio_CodiceComunicazionePrecedente != null && this.invio_CodiceComunicazionePrecedente
								.equals(other.getInvio_CodiceComunicazionePrecedente())))
				&& ((this.lavoratore_CodiceFiscale == null && other.getLavoratore_CodiceFiscale() == null)
						|| (this.lavoratore_CodiceFiscale != null
								&& this.lavoratore_CodiceFiscale.equals(other.getLavoratore_CodiceFiscale())))
				&& ((this.lavoratore_Cognome == null && other.getLavoratore_Cognome() == null)
						|| (this.lavoratore_Cognome != null
								&& this.lavoratore_Cognome.equals(other.getLavoratore_Cognome())))
				&& ((this.lavoratore_Nome == null && other.getLavoratore_Nome() == null)
						|| (this.lavoratore_Nome != null && this.lavoratore_Nome.equals(other.getLavoratore_Nome())))
				&& ((this.rapporto_Agevolazioni == null && other.getRapporto_Agevolazioni() == null)
						|| (this.rapporto_Agevolazioni != null
								&& this.rapporto_Agevolazioni.equals(other.getRapporto_Agevolazioni())))
				&& ((this.rapporto_CCNL == null && other.getRapporto_CCNL() == null)
						|| (this.rapporto_CCNL != null && this.rapporto_CCNL.equals(other.getRapporto_CCNL())))
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
				&& ((this.rapporto_SocioLavoratore == null && other.getRapporto_SocioLavoratore() == null)
						|| (this.rapporto_SocioLavoratore != null
								&& this.rapporto_SocioLavoratore.equals(other.getRapporto_SocioLavoratore())))
				&& ((this.rapporto_TipologiaContrattuale == null && other.getRapporto_TipologiaContrattuale() == null)
						|| (this.rapporto_TipologiaContrattuale != null && this.rapporto_TipologiaContrattuale
								.equals(other.getRapporto_TipologiaContrattuale())))
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
		if (getDatoreLavoroPrecedente_CodiceFiscale() != null) {
			_hashCode += getDatoreLavoroPrecedente_CodiceFiscale().hashCode();
		}
		if (getDatoreLavoroPrecedente_Denominazione() != null) {
			_hashCode += getDatoreLavoroPrecedente_Denominazione().hashCode();
		}
		if (getDatoreLavoroPrecedente_Settore() != null) {
			_hashCode += getDatoreLavoroPrecedente_Settore().hashCode();
		}
		if (getTrasferimentoAzienda_CodiceTrasferimento() != null) {
			_hashCode += getTrasferimentoAzienda_CodiceTrasferimento().hashCode();
		}
		if (getTrasferimentoAzienda_DataInizio() != null) {
			_hashCode += getTrasferimentoAzienda_DataInizio().hashCode();
		}
		if (getInvio_CodiceComunicazione() != null) {
			_hashCode += getInvio_CodiceComunicazione().hashCode();
		}
		if (getInvio_CodiceComunicazionePrecedente() != null) {
			_hashCode += getInvio_CodiceComunicazionePrecedente().hashCode();
		}
		if (getLavoratore_CodiceFiscale() != null) {
			_hashCode += getLavoratore_CodiceFiscale().hashCode();
		}
		if (getLavoratore_Cognome() != null) {
			_hashCode += getLavoratore_Cognome().hashCode();
		}
		if (getLavoratore_Nome() != null) {
			_hashCode += getLavoratore_Nome().hashCode();
		}
		if (getRapporto_Agevolazioni() != null) {
			_hashCode += getRapporto_Agevolazioni().hashCode();
		}
		if (getRapporto_CCNL() != null) {
			_hashCode += getRapporto_CCNL().hashCode();
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
		if (getRapporto_SocioLavoratore() != null) {
			_hashCode += getRapporto_SocioLavoratore().hashCode();
		}
		if (getRapporto_TipologiaContrattuale() != null) {
			_hashCode += getRapporto_TipologiaContrattuale().hashCode();
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
			VARDATORI_Type.class, true);

	static {
		typeDesc.setXmlType(
				new javax.xml.namespace.QName("http://servizi.lavoro.gov.it/RicercaCO/2.0", "VARDATORI_Type"));
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
		elemField.setFieldName("datoreLavoroPrecedente_CodiceFiscale");
		elemField.setXmlName(new javax.xml.namespace.QName("http://servizi.lavoro.gov.it/RicercaCO/2.0",
				"DatoreLavoroPrecedente_CodiceFiscale"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
		elemField.setNillable(true);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("datoreLavoroPrecedente_Denominazione");
		elemField.setXmlName(new javax.xml.namespace.QName("http://servizi.lavoro.gov.it/RicercaCO/2.0",
				"DatoreLavoroPrecedente_Denominazione"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
		elemField.setNillable(true);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("datoreLavoroPrecedente_Settore");
		elemField.setXmlName(new javax.xml.namespace.QName("http://servizi.lavoro.gov.it/RicercaCO/2.0",
				"DatoreLavoroPrecedente_Settore"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
		elemField.setNillable(true);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("trasferimentoAzienda_CodiceTrasferimento");
		elemField.setXmlName(new javax.xml.namespace.QName("http://servizi.lavoro.gov.it/RicercaCO/2.0",
				"TrasferimentoAzienda_CodiceTrasferimento"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
		elemField.setNillable(true);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("trasferimentoAzienda_DataInizio");
		elemField.setXmlName(new javax.xml.namespace.QName("http://servizi.lavoro.gov.it/RicercaCO/2.0",
				"TrasferimentoAzienda_DataInizio"));
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
		elemField.setFieldName("lavoratore_Nome");
		elemField.setXmlName(
				new javax.xml.namespace.QName("http://servizi.lavoro.gov.it/RicercaCO/2.0", "Lavoratore_Nome"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
		elemField.setNillable(false);
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
