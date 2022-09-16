/**
 * SchedaAnagraficaProfessionaleDTO.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package it.eng.sap.xml.sap;

public class SchedaAnagraficaProfessionaleDTO extends it.eng.sil.mysap.model.ejb.stateless.ws.exportsap.AbstractSapDTO
		implements java.io.Serializable {
	private java.lang.String capDomicilio;

	private java.lang.String capResidenza;

	private java.lang.String cellulare;

	private java.lang.String codComuneDomicilio;

	private java.lang.String codComuneNascita;

	private java.lang.String codComuneResidenza;

	private java.lang.String codNazioneCittadinanza;

	private java.lang.String cognome;

	private java.util.Calendar dataNascita;

	private java.lang.String descComuneDomicilio;

	private java.lang.String descComuneNascita;

	private java.lang.String descComuneResidenza;

	private java.lang.String descNazioneCittadinanza;

	private java.lang.String email;

	private java.lang.String fax;

	private java.lang.Integer idSap;

	private java.lang.String indirizzoDomicilio;

	private java.lang.String indirizzoResidenza;

	private java.lang.String nome;

	private it.eng.sil.mysap.model.ejb.stateless.ws.exportsap.SapAlboDTO[] sapAlboList;

	private it.eng.sil.mysap.model.ejb.stateless.ws.exportsap.SapConoscenzeInfoDTO[] sapConoscenzeInfoList;

	private it.eng.sil.mysap.model.ejb.stateless.ws.exportsap.SapEsperienzaLavDTO[] sapEsperienzaLavList;

	private it.eng.sil.mysap.model.ejb.stateless.ws.exportsap.SapFormazioneDTO[] sapFormazioneList;

	private it.eng.sil.mysap.model.ejb.stateless.ws.exportsap.SapLinguaDTO[] sapLinguaList;

	private it.eng.sil.mysap.model.ejb.stateless.ws.exportsap.SapPatenteDTO[] sapPatenteList;

	private it.eng.sil.mysap.model.ejb.stateless.ws.exportsap.SapPatentinoDTO[] sapPatentinoList;

	private it.eng.sil.mysap.model.ejb.stateless.ws.exportsap.SapPropensioneDTO[] sapPropensioneList;

	private it.eng.sil.mysap.model.ejb.stateless.ws.exportsap.SapTitoloStudioDTO[] sapTitoloStudioList;

	private java.lang.String sesso;

	private java.lang.String strAnnotazioniColloquio;

	private java.lang.String strCodiceFiscale;

	public SchedaAnagraficaProfessionaleDTO() {
	}

	public SchedaAnagraficaProfessionaleDTO(java.util.Calendar dtmIns, java.util.Calendar dtmMod,
			java.lang.Integer idPrincipalIns, java.lang.Integer idPrincipalMod, java.lang.String capDomicilio,
			java.lang.String capResidenza, java.lang.String cellulare, java.lang.String codComuneDomicilio,
			java.lang.String codComuneNascita, java.lang.String codComuneResidenza,
			java.lang.String codNazioneCittadinanza, java.lang.String cognome, java.util.Calendar dataNascita,
			java.lang.String descComuneDomicilio, java.lang.String descComuneNascita,
			java.lang.String descComuneResidenza, java.lang.String descNazioneCittadinanza, java.lang.String email,
			java.lang.String fax, java.lang.Integer idSap, java.lang.String indirizzoDomicilio,
			java.lang.String indirizzoResidenza, java.lang.String nome,
			it.eng.sil.mysap.model.ejb.stateless.ws.exportsap.SapAlboDTO[] sapAlboList,
			it.eng.sil.mysap.model.ejb.stateless.ws.exportsap.SapConoscenzeInfoDTO[] sapConoscenzeInfoList,
			it.eng.sil.mysap.model.ejb.stateless.ws.exportsap.SapEsperienzaLavDTO[] sapEsperienzaLavList,
			it.eng.sil.mysap.model.ejb.stateless.ws.exportsap.SapFormazioneDTO[] sapFormazioneList,
			it.eng.sil.mysap.model.ejb.stateless.ws.exportsap.SapLinguaDTO[] sapLinguaList,
			it.eng.sil.mysap.model.ejb.stateless.ws.exportsap.SapPatenteDTO[] sapPatenteList,
			it.eng.sil.mysap.model.ejb.stateless.ws.exportsap.SapPatentinoDTO[] sapPatentinoList,
			it.eng.sil.mysap.model.ejb.stateless.ws.exportsap.SapPropensioneDTO[] sapPropensioneList,
			it.eng.sil.mysap.model.ejb.stateless.ws.exportsap.SapTitoloStudioDTO[] sapTitoloStudioList,
			java.lang.String sesso, java.lang.String strAnnotazioniColloquio, java.lang.String strCodiceFiscale) {
		super(dtmIns, dtmMod, idPrincipalIns, idPrincipalMod);
		this.capDomicilio = capDomicilio;
		this.capResidenza = capResidenza;
		this.cellulare = cellulare;
		this.codComuneDomicilio = codComuneDomicilio;
		this.codComuneNascita = codComuneNascita;
		this.codComuneResidenza = codComuneResidenza;
		this.codNazioneCittadinanza = codNazioneCittadinanza;
		this.cognome = cognome;
		this.dataNascita = dataNascita;
		this.descComuneDomicilio = descComuneDomicilio;
		this.descComuneNascita = descComuneNascita;
		this.descComuneResidenza = descComuneResidenza;
		this.descNazioneCittadinanza = descNazioneCittadinanza;
		this.email = email;
		this.fax = fax;
		this.idSap = idSap;
		this.indirizzoDomicilio = indirizzoDomicilio;
		this.indirizzoResidenza = indirizzoResidenza;
		this.nome = nome;
		this.sapAlboList = sapAlboList;
		this.sapConoscenzeInfoList = sapConoscenzeInfoList;
		this.sapEsperienzaLavList = sapEsperienzaLavList;
		this.sapFormazioneList = sapFormazioneList;
		this.sapLinguaList = sapLinguaList;
		this.sapPatenteList = sapPatenteList;
		this.sapPatentinoList = sapPatentinoList;
		this.sapPropensioneList = sapPropensioneList;
		this.sapTitoloStudioList = sapTitoloStudioList;
		this.sesso = sesso;
		this.strAnnotazioniColloquio = strAnnotazioniColloquio;
		this.strCodiceFiscale = strCodiceFiscale;
	}

	/**
	 * Gets the capDomicilio value for this SchedaAnagraficaProfessionaleDTO.
	 * 
	 * @return capDomicilio
	 */
	public java.lang.String getCapDomicilio() {
		return capDomicilio;
	}

	/**
	 * Sets the capDomicilio value for this SchedaAnagraficaProfessionaleDTO.
	 * 
	 * @param capDomicilio
	 */
	public void setCapDomicilio(java.lang.String capDomicilio) {
		this.capDomicilio = capDomicilio;
	}

	/**
	 * Gets the capResidenza value for this SchedaAnagraficaProfessionaleDTO.
	 * 
	 * @return capResidenza
	 */
	public java.lang.String getCapResidenza() {
		return capResidenza;
	}

	/**
	 * Sets the capResidenza value for this SchedaAnagraficaProfessionaleDTO.
	 * 
	 * @param capResidenza
	 */
	public void setCapResidenza(java.lang.String capResidenza) {
		this.capResidenza = capResidenza;
	}

	/**
	 * Gets the cellulare value for this SchedaAnagraficaProfessionaleDTO.
	 * 
	 * @return cellulare
	 */
	public java.lang.String getCellulare() {
		return cellulare;
	}

	/**
	 * Sets the cellulare value for this SchedaAnagraficaProfessionaleDTO.
	 * 
	 * @param cellulare
	 */
	public void setCellulare(java.lang.String cellulare) {
		this.cellulare = cellulare;
	}

	/**
	 * Gets the codComuneDomicilio value for this SchedaAnagraficaProfessionaleDTO.
	 * 
	 * @return codComuneDomicilio
	 */
	public java.lang.String getCodComuneDomicilio() {
		return codComuneDomicilio;
	}

	/**
	 * Sets the codComuneDomicilio value for this SchedaAnagraficaProfessionaleDTO.
	 * 
	 * @param codComuneDomicilio
	 */
	public void setCodComuneDomicilio(java.lang.String codComuneDomicilio) {
		this.codComuneDomicilio = codComuneDomicilio;
	}

	/**
	 * Gets the codComuneNascita value for this SchedaAnagraficaProfessionaleDTO.
	 * 
	 * @return codComuneNascita
	 */
	public java.lang.String getCodComuneNascita() {
		return codComuneNascita;
	}

	/**
	 * Sets the codComuneNascita value for this SchedaAnagraficaProfessionaleDTO.
	 * 
	 * @param codComuneNascita
	 */
	public void setCodComuneNascita(java.lang.String codComuneNascita) {
		this.codComuneNascita = codComuneNascita;
	}

	/**
	 * Gets the codComuneResidenza value for this SchedaAnagraficaProfessionaleDTO.
	 * 
	 * @return codComuneResidenza
	 */
	public java.lang.String getCodComuneResidenza() {
		return codComuneResidenza;
	}

	/**
	 * Sets the codComuneResidenza value for this SchedaAnagraficaProfessionaleDTO.
	 * 
	 * @param codComuneResidenza
	 */
	public void setCodComuneResidenza(java.lang.String codComuneResidenza) {
		this.codComuneResidenza = codComuneResidenza;
	}

	/**
	 * Gets the codNazioneCittadinanza value for this SchedaAnagraficaProfessionaleDTO.
	 * 
	 * @return codNazioneCittadinanza
	 */
	public java.lang.String getCodNazioneCittadinanza() {
		return codNazioneCittadinanza;
	}

	/**
	 * Sets the codNazioneCittadinanza value for this SchedaAnagraficaProfessionaleDTO.
	 * 
	 * @param codNazioneCittadinanza
	 */
	public void setCodNazioneCittadinanza(java.lang.String codNazioneCittadinanza) {
		this.codNazioneCittadinanza = codNazioneCittadinanza;
	}

	/**
	 * Gets the cognome value for this SchedaAnagraficaProfessionaleDTO.
	 * 
	 * @return cognome
	 */
	public java.lang.String getCognome() {
		return cognome;
	}

	/**
	 * Sets the cognome value for this SchedaAnagraficaProfessionaleDTO.
	 * 
	 * @param cognome
	 */
	public void setCognome(java.lang.String cognome) {
		this.cognome = cognome;
	}

	/**
	 * Gets the dataNascita value for this SchedaAnagraficaProfessionaleDTO.
	 * 
	 * @return dataNascita
	 */
	public java.util.Calendar getDataNascita() {
		return dataNascita;
	}

	/**
	 * Sets the dataNascita value for this SchedaAnagraficaProfessionaleDTO.
	 * 
	 * @param dataNascita
	 */
	public void setDataNascita(java.util.Calendar dataNascita) {
		this.dataNascita = dataNascita;
	}

	/**
	 * Gets the descComuneDomicilio value for this SchedaAnagraficaProfessionaleDTO.
	 * 
	 * @return descComuneDomicilio
	 */
	public java.lang.String getDescComuneDomicilio() {
		return descComuneDomicilio;
	}

	/**
	 * Sets the descComuneDomicilio value for this SchedaAnagraficaProfessionaleDTO.
	 * 
	 * @param descComuneDomicilio
	 */
	public void setDescComuneDomicilio(java.lang.String descComuneDomicilio) {
		this.descComuneDomicilio = descComuneDomicilio;
	}

	/**
	 * Gets the descComuneNascita value for this SchedaAnagraficaProfessionaleDTO.
	 * 
	 * @return descComuneNascita
	 */
	public java.lang.String getDescComuneNascita() {
		return descComuneNascita;
	}

	/**
	 * Sets the descComuneNascita value for this SchedaAnagraficaProfessionaleDTO.
	 * 
	 * @param descComuneNascita
	 */
	public void setDescComuneNascita(java.lang.String descComuneNascita) {
		this.descComuneNascita = descComuneNascita;
	}

	/**
	 * Gets the descComuneResidenza value for this SchedaAnagraficaProfessionaleDTO.
	 * 
	 * @return descComuneResidenza
	 */
	public java.lang.String getDescComuneResidenza() {
		return descComuneResidenza;
	}

	/**
	 * Sets the descComuneResidenza value for this SchedaAnagraficaProfessionaleDTO.
	 * 
	 * @param descComuneResidenza
	 */
	public void setDescComuneResidenza(java.lang.String descComuneResidenza) {
		this.descComuneResidenza = descComuneResidenza;
	}

	/**
	 * Gets the descNazioneCittadinanza value for this SchedaAnagraficaProfessionaleDTO.
	 * 
	 * @return descNazioneCittadinanza
	 */
	public java.lang.String getDescNazioneCittadinanza() {
		return descNazioneCittadinanza;
	}

	/**
	 * Sets the descNazioneCittadinanza value for this SchedaAnagraficaProfessionaleDTO.
	 * 
	 * @param descNazioneCittadinanza
	 */
	public void setDescNazioneCittadinanza(java.lang.String descNazioneCittadinanza) {
		this.descNazioneCittadinanza = descNazioneCittadinanza;
	}

	/**
	 * Gets the email value for this SchedaAnagraficaProfessionaleDTO.
	 * 
	 * @return email
	 */
	public java.lang.String getEmail() {
		return email;
	}

	/**
	 * Sets the email value for this SchedaAnagraficaProfessionaleDTO.
	 * 
	 * @param email
	 */
	public void setEmail(java.lang.String email) {
		this.email = email;
	}

	/**
	 * Gets the fax value for this SchedaAnagraficaProfessionaleDTO.
	 * 
	 * @return fax
	 */
	public java.lang.String getFax() {
		return fax;
	}

	/**
	 * Sets the fax value for this SchedaAnagraficaProfessionaleDTO.
	 * 
	 * @param fax
	 */
	public void setFax(java.lang.String fax) {
		this.fax = fax;
	}

	/**
	 * Gets the idSap value for this SchedaAnagraficaProfessionaleDTO.
	 * 
	 * @return idSap
	 */
	public java.lang.Integer getIdSap() {
		return idSap;
	}

	/**
	 * Sets the idSap value for this SchedaAnagraficaProfessionaleDTO.
	 * 
	 * @param idSap
	 */
	public void setIdSap(java.lang.Integer idSap) {
		this.idSap = idSap;
	}

	/**
	 * Gets the indirizzoDomicilio value for this SchedaAnagraficaProfessionaleDTO.
	 * 
	 * @return indirizzoDomicilio
	 */
	public java.lang.String getIndirizzoDomicilio() {
		return indirizzoDomicilio;
	}

	/**
	 * Sets the indirizzoDomicilio value for this SchedaAnagraficaProfessionaleDTO.
	 * 
	 * @param indirizzoDomicilio
	 */
	public void setIndirizzoDomicilio(java.lang.String indirizzoDomicilio) {
		this.indirizzoDomicilio = indirizzoDomicilio;
	}

	/**
	 * Gets the indirizzoResidenza value for this SchedaAnagraficaProfessionaleDTO.
	 * 
	 * @return indirizzoResidenza
	 */
	public java.lang.String getIndirizzoResidenza() {
		return indirizzoResidenza;
	}

	/**
	 * Sets the indirizzoResidenza value for this SchedaAnagraficaProfessionaleDTO.
	 * 
	 * @param indirizzoResidenza
	 */
	public void setIndirizzoResidenza(java.lang.String indirizzoResidenza) {
		this.indirizzoResidenza = indirizzoResidenza;
	}

	/**
	 * Gets the nome value for this SchedaAnagraficaProfessionaleDTO.
	 * 
	 * @return nome
	 */
	public java.lang.String getNome() {
		return nome;
	}

	/**
	 * Sets the nome value for this SchedaAnagraficaProfessionaleDTO.
	 * 
	 * @param nome
	 */
	public void setNome(java.lang.String nome) {
		this.nome = nome;
	}

	/**
	 * Gets the sapAlboList value for this SchedaAnagraficaProfessionaleDTO.
	 * 
	 * @return sapAlboList
	 */
	public it.eng.sil.mysap.model.ejb.stateless.ws.exportsap.SapAlboDTO[] getSapAlboList() {
		return sapAlboList;
	}

	/**
	 * Sets the sapAlboList value for this SchedaAnagraficaProfessionaleDTO.
	 * 
	 * @param sapAlboList
	 */
	public void setSapAlboList(it.eng.sil.mysap.model.ejb.stateless.ws.exportsap.SapAlboDTO[] sapAlboList) {
		this.sapAlboList = sapAlboList;
	}

	public it.eng.sil.mysap.model.ejb.stateless.ws.exportsap.SapAlboDTO getSapAlboList(int i) {
		return this.sapAlboList[i];
	}

	public void setSapAlboList(int i, it.eng.sil.mysap.model.ejb.stateless.ws.exportsap.SapAlboDTO _value) {
		this.sapAlboList[i] = _value;
	}

	/**
	 * Gets the sapConoscenzeInfoList value for this SchedaAnagraficaProfessionaleDTO.
	 * 
	 * @return sapConoscenzeInfoList
	 */
	public it.eng.sil.mysap.model.ejb.stateless.ws.exportsap.SapConoscenzeInfoDTO[] getSapConoscenzeInfoList() {
		return sapConoscenzeInfoList;
	}

	/**
	 * Sets the sapConoscenzeInfoList value for this SchedaAnagraficaProfessionaleDTO.
	 * 
	 * @param sapConoscenzeInfoList
	 */
	public void setSapConoscenzeInfoList(
			it.eng.sil.mysap.model.ejb.stateless.ws.exportsap.SapConoscenzeInfoDTO[] sapConoscenzeInfoList) {
		this.sapConoscenzeInfoList = sapConoscenzeInfoList;
	}

	public it.eng.sil.mysap.model.ejb.stateless.ws.exportsap.SapConoscenzeInfoDTO getSapConoscenzeInfoList(int i) {
		return this.sapConoscenzeInfoList[i];
	}

	public void setSapConoscenzeInfoList(int i,
			it.eng.sil.mysap.model.ejb.stateless.ws.exportsap.SapConoscenzeInfoDTO _value) {
		this.sapConoscenzeInfoList[i] = _value;
	}

	/**
	 * Gets the sapEsperienzaLavList value for this SchedaAnagraficaProfessionaleDTO.
	 * 
	 * @return sapEsperienzaLavList
	 */
	public it.eng.sil.mysap.model.ejb.stateless.ws.exportsap.SapEsperienzaLavDTO[] getSapEsperienzaLavList() {
		return sapEsperienzaLavList;
	}

	/**
	 * Sets the sapEsperienzaLavList value for this SchedaAnagraficaProfessionaleDTO.
	 * 
	 * @param sapEsperienzaLavList
	 */
	public void setSapEsperienzaLavList(
			it.eng.sil.mysap.model.ejb.stateless.ws.exportsap.SapEsperienzaLavDTO[] sapEsperienzaLavList) {
		this.sapEsperienzaLavList = sapEsperienzaLavList;
	}

	public it.eng.sil.mysap.model.ejb.stateless.ws.exportsap.SapEsperienzaLavDTO getSapEsperienzaLavList(int i) {
		return this.sapEsperienzaLavList[i];
	}

	public void setSapEsperienzaLavList(int i,
			it.eng.sil.mysap.model.ejb.stateless.ws.exportsap.SapEsperienzaLavDTO _value) {
		this.sapEsperienzaLavList[i] = _value;
	}

	/**
	 * Gets the sapFormazioneList value for this SchedaAnagraficaProfessionaleDTO.
	 * 
	 * @return sapFormazioneList
	 */
	public it.eng.sil.mysap.model.ejb.stateless.ws.exportsap.SapFormazioneDTO[] getSapFormazioneList() {
		return sapFormazioneList;
	}

	/**
	 * Sets the sapFormazioneList value for this SchedaAnagraficaProfessionaleDTO.
	 * 
	 * @param sapFormazioneList
	 */
	public void setSapFormazioneList(
			it.eng.sil.mysap.model.ejb.stateless.ws.exportsap.SapFormazioneDTO[] sapFormazioneList) {
		this.sapFormazioneList = sapFormazioneList;
	}

	public it.eng.sil.mysap.model.ejb.stateless.ws.exportsap.SapFormazioneDTO getSapFormazioneList(int i) {
		return this.sapFormazioneList[i];
	}

	public void setSapFormazioneList(int i, it.eng.sil.mysap.model.ejb.stateless.ws.exportsap.SapFormazioneDTO _value) {
		this.sapFormazioneList[i] = _value;
	}

	/**
	 * Gets the sapLinguaList value for this SchedaAnagraficaProfessionaleDTO.
	 * 
	 * @return sapLinguaList
	 */
	public it.eng.sil.mysap.model.ejb.stateless.ws.exportsap.SapLinguaDTO[] getSapLinguaList() {
		return sapLinguaList;
	}

	/**
	 * Sets the sapLinguaList value for this SchedaAnagraficaProfessionaleDTO.
	 * 
	 * @param sapLinguaList
	 */
	public void setSapLinguaList(it.eng.sil.mysap.model.ejb.stateless.ws.exportsap.SapLinguaDTO[] sapLinguaList) {
		this.sapLinguaList = sapLinguaList;
	}

	public it.eng.sil.mysap.model.ejb.stateless.ws.exportsap.SapLinguaDTO getSapLinguaList(int i) {
		return this.sapLinguaList[i];
	}

	public void setSapLinguaList(int i, it.eng.sil.mysap.model.ejb.stateless.ws.exportsap.SapLinguaDTO _value) {
		this.sapLinguaList[i] = _value;
	}

	/**
	 * Gets the sapPatenteList value for this SchedaAnagraficaProfessionaleDTO.
	 * 
	 * @return sapPatenteList
	 */
	public it.eng.sil.mysap.model.ejb.stateless.ws.exportsap.SapPatenteDTO[] getSapPatenteList() {
		return sapPatenteList;
	}

	/**
	 * Sets the sapPatenteList value for this SchedaAnagraficaProfessionaleDTO.
	 * 
	 * @param sapPatenteList
	 */
	public void setSapPatenteList(it.eng.sil.mysap.model.ejb.stateless.ws.exportsap.SapPatenteDTO[] sapPatenteList) {
		this.sapPatenteList = sapPatenteList;
	}

	public it.eng.sil.mysap.model.ejb.stateless.ws.exportsap.SapPatenteDTO getSapPatenteList(int i) {
		return this.sapPatenteList[i];
	}

	public void setSapPatenteList(int i, it.eng.sil.mysap.model.ejb.stateless.ws.exportsap.SapPatenteDTO _value) {
		this.sapPatenteList[i] = _value;
	}

	/**
	 * Gets the sapPatentinoList value for this SchedaAnagraficaProfessionaleDTO.
	 * 
	 * @return sapPatentinoList
	 */
	public it.eng.sil.mysap.model.ejb.stateless.ws.exportsap.SapPatentinoDTO[] getSapPatentinoList() {
		return sapPatentinoList;
	}

	/**
	 * Sets the sapPatentinoList value for this SchedaAnagraficaProfessionaleDTO.
	 * 
	 * @param sapPatentinoList
	 */
	public void setSapPatentinoList(
			it.eng.sil.mysap.model.ejb.stateless.ws.exportsap.SapPatentinoDTO[] sapPatentinoList) {
		this.sapPatentinoList = sapPatentinoList;
	}

	public it.eng.sil.mysap.model.ejb.stateless.ws.exportsap.SapPatentinoDTO getSapPatentinoList(int i) {
		return this.sapPatentinoList[i];
	}

	public void setSapPatentinoList(int i, it.eng.sil.mysap.model.ejb.stateless.ws.exportsap.SapPatentinoDTO _value) {
		this.sapPatentinoList[i] = _value;
	}

	/**
	 * Gets the sapPropensioneList value for this SchedaAnagraficaProfessionaleDTO.
	 * 
	 * @return sapPropensioneList
	 */
	public it.eng.sil.mysap.model.ejb.stateless.ws.exportsap.SapPropensioneDTO[] getSapPropensioneList() {
		return sapPropensioneList;
	}

	/**
	 * Sets the sapPropensioneList value for this SchedaAnagraficaProfessionaleDTO.
	 * 
	 * @param sapPropensioneList
	 */
	public void setSapPropensioneList(
			it.eng.sil.mysap.model.ejb.stateless.ws.exportsap.SapPropensioneDTO[] sapPropensioneList) {
		this.sapPropensioneList = sapPropensioneList;
	}

	public it.eng.sil.mysap.model.ejb.stateless.ws.exportsap.SapPropensioneDTO getSapPropensioneList(int i) {
		return this.sapPropensioneList[i];
	}

	public void setSapPropensioneList(int i,
			it.eng.sil.mysap.model.ejb.stateless.ws.exportsap.SapPropensioneDTO _value) {
		this.sapPropensioneList[i] = _value;
	}

	/**
	 * Gets the sapTitoloStudioList value for this SchedaAnagraficaProfessionaleDTO.
	 * 
	 * @return sapTitoloStudioList
	 */
	public it.eng.sil.mysap.model.ejb.stateless.ws.exportsap.SapTitoloStudioDTO[] getSapTitoloStudioList() {
		return sapTitoloStudioList;
	}

	/**
	 * Sets the sapTitoloStudioList value for this SchedaAnagraficaProfessionaleDTO.
	 * 
	 * @param sapTitoloStudioList
	 */
	public void setSapTitoloStudioList(
			it.eng.sil.mysap.model.ejb.stateless.ws.exportsap.SapTitoloStudioDTO[] sapTitoloStudioList) {
		this.sapTitoloStudioList = sapTitoloStudioList;
	}

	public it.eng.sil.mysap.model.ejb.stateless.ws.exportsap.SapTitoloStudioDTO getSapTitoloStudioList(int i) {
		return this.sapTitoloStudioList[i];
	}

	public void setSapTitoloStudioList(int i,
			it.eng.sil.mysap.model.ejb.stateless.ws.exportsap.SapTitoloStudioDTO _value) {
		this.sapTitoloStudioList[i] = _value;
	}

	/**
	 * Gets the sesso value for this SchedaAnagraficaProfessionaleDTO.
	 * 
	 * @return sesso
	 */
	public java.lang.String getSesso() {
		return sesso;
	}

	/**
	 * Sets the sesso value for this SchedaAnagraficaProfessionaleDTO.
	 * 
	 * @param sesso
	 */
	public void setSesso(java.lang.String sesso) {
		this.sesso = sesso;
	}

	/**
	 * Gets the strAnnotazioniColloquio value for this SchedaAnagraficaProfessionaleDTO.
	 * 
	 * @return strAnnotazioniColloquio
	 */
	public java.lang.String getStrAnnotazioniColloquio() {
		return strAnnotazioniColloquio;
	}

	/**
	 * Sets the strAnnotazioniColloquio value for this SchedaAnagraficaProfessionaleDTO.
	 * 
	 * @param strAnnotazioniColloquio
	 */
	public void setStrAnnotazioniColloquio(java.lang.String strAnnotazioniColloquio) {
		this.strAnnotazioniColloquio = strAnnotazioniColloquio;
	}

	/**
	 * Gets the strCodiceFiscale value for this SchedaAnagraficaProfessionaleDTO.
	 * 
	 * @return strCodiceFiscale
	 */
	public java.lang.String getStrCodiceFiscale() {
		return strCodiceFiscale;
	}

	/**
	 * Sets the strCodiceFiscale value for this SchedaAnagraficaProfessionaleDTO.
	 * 
	 * @param strCodiceFiscale
	 */
	public void setStrCodiceFiscale(java.lang.String strCodiceFiscale) {
		this.strCodiceFiscale = strCodiceFiscale;
	}

	private java.lang.Object __equalsCalc = null;

	public synchronized boolean equals(java.lang.Object obj) {
		if (!(obj instanceof SchedaAnagraficaProfessionaleDTO))
			return false;
		SchedaAnagraficaProfessionaleDTO other = (SchedaAnagraficaProfessionaleDTO) obj;
		if (obj == null)
			return false;
		if (this == obj)
			return true;
		if (__equalsCalc != null) {
			return (__equalsCalc == obj);
		}
		__equalsCalc = obj;
		boolean _equals;
		_equals = super.equals(obj)
				&& ((this.capDomicilio == null && other.getCapDomicilio() == null)
						|| (this.capDomicilio != null && this.capDomicilio.equals(other.getCapDomicilio())))
				&& ((this.capResidenza == null && other.getCapResidenza() == null)
						|| (this.capResidenza != null && this.capResidenza.equals(other.getCapResidenza())))
				&& ((this.cellulare == null && other.getCellulare() == null)
						|| (this.cellulare != null && this.cellulare.equals(other.getCellulare())))
				&& ((this.codComuneDomicilio == null && other.getCodComuneDomicilio() == null)
						|| (this.codComuneDomicilio != null
								&& this.codComuneDomicilio.equals(other.getCodComuneDomicilio())))
				&& ((this.codComuneNascita == null && other.getCodComuneNascita() == null)
						|| (this.codComuneNascita != null && this.codComuneNascita.equals(other.getCodComuneNascita())))
				&& ((this.codComuneResidenza == null && other.getCodComuneResidenza() == null)
						|| (this.codComuneResidenza != null
								&& this.codComuneResidenza.equals(other.getCodComuneResidenza())))
				&& ((this.codNazioneCittadinanza == null && other.getCodNazioneCittadinanza() == null)
						|| (this.codNazioneCittadinanza != null
								&& this.codNazioneCittadinanza.equals(other.getCodNazioneCittadinanza())))
				&& ((this.cognome == null && other.getCognome() == null)
						|| (this.cognome != null && this.cognome.equals(other.getCognome())))
				&& ((this.dataNascita == null && other.getDataNascita() == null)
						|| (this.dataNascita != null && this.dataNascita.equals(other.getDataNascita())))
				&& ((this.descComuneDomicilio == null && other.getDescComuneDomicilio() == null)
						|| (this.descComuneDomicilio != null
								&& this.descComuneDomicilio.equals(other.getDescComuneDomicilio())))
				&& ((this.descComuneNascita == null && other.getDescComuneNascita() == null)
						|| (this.descComuneNascita != null
								&& this.descComuneNascita.equals(other.getDescComuneNascita())))
				&& ((this.descComuneResidenza == null && other.getDescComuneResidenza() == null)
						|| (this.descComuneResidenza != null
								&& this.descComuneResidenza.equals(other.getDescComuneResidenza())))
				&& ((this.descNazioneCittadinanza == null && other.getDescNazioneCittadinanza() == null)
						|| (this.descNazioneCittadinanza != null
								&& this.descNazioneCittadinanza.equals(other.getDescNazioneCittadinanza())))
				&& ((this.email == null && other.getEmail() == null)
						|| (this.email != null && this.email.equals(other.getEmail())))
				&& ((this.fax == null && other.getFax() == null)
						|| (this.fax != null && this.fax.equals(other.getFax())))
				&& ((this.idSap == null && other.getIdSap() == null)
						|| (this.idSap != null && this.idSap.equals(other.getIdSap())))
				&& ((this.indirizzoDomicilio == null && other.getIndirizzoDomicilio() == null)
						|| (this.indirizzoDomicilio != null
								&& this.indirizzoDomicilio.equals(other.getIndirizzoDomicilio())))
				&& ((this.indirizzoResidenza == null && other.getIndirizzoResidenza() == null)
						|| (this.indirizzoResidenza != null
								&& this.indirizzoResidenza.equals(other.getIndirizzoResidenza())))
				&& ((this.nome == null && other.getNome() == null)
						|| (this.nome != null && this.nome.equals(other.getNome())))
				&& ((this.sapAlboList == null && other.getSapAlboList() == null) || (this.sapAlboList != null
						&& java.util.Arrays.equals(this.sapAlboList, other.getSapAlboList())))
				&& ((this.sapConoscenzeInfoList == null && other.getSapConoscenzeInfoList() == null)
						|| (this.sapConoscenzeInfoList != null && java.util.Arrays.equals(this.sapConoscenzeInfoList,
								other.getSapConoscenzeInfoList())))
				&& ((this.sapEsperienzaLavList == null && other.getSapEsperienzaLavList() == null)
						|| (this.sapEsperienzaLavList != null
								&& java.util.Arrays.equals(this.sapEsperienzaLavList, other.getSapEsperienzaLavList())))
				&& ((this.sapFormazioneList == null && other.getSapFormazioneList() == null)
						|| (this.sapFormazioneList != null
								&& java.util.Arrays.equals(this.sapFormazioneList, other.getSapFormazioneList())))
				&& ((this.sapLinguaList == null && other.getSapLinguaList() == null) || (this.sapLinguaList != null
						&& java.util.Arrays.equals(this.sapLinguaList, other.getSapLinguaList())))
				&& ((this.sapPatenteList == null && other.getSapPatenteList() == null) || (this.sapPatenteList != null
						&& java.util.Arrays.equals(this.sapPatenteList, other.getSapPatenteList())))
				&& ((this.sapPatentinoList == null && other.getSapPatentinoList() == null)
						|| (this.sapPatentinoList != null
								&& java.util.Arrays.equals(this.sapPatentinoList, other.getSapPatentinoList())))
				&& ((this.sapPropensioneList == null && other.getSapPropensioneList() == null)
						|| (this.sapPropensioneList != null
								&& java.util.Arrays.equals(this.sapPropensioneList, other.getSapPropensioneList())))
				&& ((this.sapTitoloStudioList == null && other.getSapTitoloStudioList() == null)
						|| (this.sapTitoloStudioList != null
								&& java.util.Arrays.equals(this.sapTitoloStudioList, other.getSapTitoloStudioList())))
				&& ((this.sesso == null && other.getSesso() == null)
						|| (this.sesso != null && this.sesso.equals(other.getSesso())))
				&& ((this.strAnnotazioniColloquio == null && other.getStrAnnotazioniColloquio() == null)
						|| (this.strAnnotazioniColloquio != null
								&& this.strAnnotazioniColloquio.equals(other.getStrAnnotazioniColloquio())))
				&& ((this.strCodiceFiscale == null && other.getStrCodiceFiscale() == null)
						|| (this.strCodiceFiscale != null
								&& this.strCodiceFiscale.equals(other.getStrCodiceFiscale())));
		__equalsCalc = null;
		return _equals;
	}

	private boolean __hashCodeCalc = false;

	public synchronized int hashCode() {
		if (__hashCodeCalc) {
			return 0;
		}
		__hashCodeCalc = true;
		int _hashCode = super.hashCode();
		if (getCapDomicilio() != null) {
			_hashCode += getCapDomicilio().hashCode();
		}
		if (getCapResidenza() != null) {
			_hashCode += getCapResidenza().hashCode();
		}
		if (getCellulare() != null) {
			_hashCode += getCellulare().hashCode();
		}
		if (getCodComuneDomicilio() != null) {
			_hashCode += getCodComuneDomicilio().hashCode();
		}
		if (getCodComuneNascita() != null) {
			_hashCode += getCodComuneNascita().hashCode();
		}
		if (getCodComuneResidenza() != null) {
			_hashCode += getCodComuneResidenza().hashCode();
		}
		if (getCodNazioneCittadinanza() != null) {
			_hashCode += getCodNazioneCittadinanza().hashCode();
		}
		if (getCognome() != null) {
			_hashCode += getCognome().hashCode();
		}
		if (getDataNascita() != null) {
			_hashCode += getDataNascita().hashCode();
		}
		if (getDescComuneDomicilio() != null) {
			_hashCode += getDescComuneDomicilio().hashCode();
		}
		if (getDescComuneNascita() != null) {
			_hashCode += getDescComuneNascita().hashCode();
		}
		if (getDescComuneResidenza() != null) {
			_hashCode += getDescComuneResidenza().hashCode();
		}
		if (getDescNazioneCittadinanza() != null) {
			_hashCode += getDescNazioneCittadinanza().hashCode();
		}
		if (getEmail() != null) {
			_hashCode += getEmail().hashCode();
		}
		if (getFax() != null) {
			_hashCode += getFax().hashCode();
		}
		if (getIdSap() != null) {
			_hashCode += getIdSap().hashCode();
		}
		if (getIndirizzoDomicilio() != null) {
			_hashCode += getIndirizzoDomicilio().hashCode();
		}
		if (getIndirizzoResidenza() != null) {
			_hashCode += getIndirizzoResidenza().hashCode();
		}
		if (getNome() != null) {
			_hashCode += getNome().hashCode();
		}
		if (getSapAlboList() != null) {
			for (int i = 0; i < java.lang.reflect.Array.getLength(getSapAlboList()); i++) {
				java.lang.Object obj = java.lang.reflect.Array.get(getSapAlboList(), i);
				if (obj != null && !obj.getClass().isArray()) {
					_hashCode += obj.hashCode();
				}
			}
		}
		if (getSapConoscenzeInfoList() != null) {
			for (int i = 0; i < java.lang.reflect.Array.getLength(getSapConoscenzeInfoList()); i++) {
				java.lang.Object obj = java.lang.reflect.Array.get(getSapConoscenzeInfoList(), i);
				if (obj != null && !obj.getClass().isArray()) {
					_hashCode += obj.hashCode();
				}
			}
		}
		if (getSapEsperienzaLavList() != null) {
			for (int i = 0; i < java.lang.reflect.Array.getLength(getSapEsperienzaLavList()); i++) {
				java.lang.Object obj = java.lang.reflect.Array.get(getSapEsperienzaLavList(), i);
				if (obj != null && !obj.getClass().isArray()) {
					_hashCode += obj.hashCode();
				}
			}
		}
		if (getSapFormazioneList() != null) {
			for (int i = 0; i < java.lang.reflect.Array.getLength(getSapFormazioneList()); i++) {
				java.lang.Object obj = java.lang.reflect.Array.get(getSapFormazioneList(), i);
				if (obj != null && !obj.getClass().isArray()) {
					_hashCode += obj.hashCode();
				}
			}
		}
		if (getSapLinguaList() != null) {
			for (int i = 0; i < java.lang.reflect.Array.getLength(getSapLinguaList()); i++) {
				java.lang.Object obj = java.lang.reflect.Array.get(getSapLinguaList(), i);
				if (obj != null && !obj.getClass().isArray()) {
					_hashCode += obj.hashCode();
				}
			}
		}
		if (getSapPatenteList() != null) {
			for (int i = 0; i < java.lang.reflect.Array.getLength(getSapPatenteList()); i++) {
				java.lang.Object obj = java.lang.reflect.Array.get(getSapPatenteList(), i);
				if (obj != null && !obj.getClass().isArray()) {
					_hashCode += obj.hashCode();
				}
			}
		}
		if (getSapPatentinoList() != null) {
			for (int i = 0; i < java.lang.reflect.Array.getLength(getSapPatentinoList()); i++) {
				java.lang.Object obj = java.lang.reflect.Array.get(getSapPatentinoList(), i);
				if (obj != null && !obj.getClass().isArray()) {
					_hashCode += obj.hashCode();
				}
			}
		}
		if (getSapPropensioneList() != null) {
			for (int i = 0; i < java.lang.reflect.Array.getLength(getSapPropensioneList()); i++) {
				java.lang.Object obj = java.lang.reflect.Array.get(getSapPropensioneList(), i);
				if (obj != null && !obj.getClass().isArray()) {
					_hashCode += obj.hashCode();
				}
			}
		}
		if (getSapTitoloStudioList() != null) {
			for (int i = 0; i < java.lang.reflect.Array.getLength(getSapTitoloStudioList()); i++) {
				java.lang.Object obj = java.lang.reflect.Array.get(getSapTitoloStudioList(), i);
				if (obj != null && !obj.getClass().isArray()) {
					_hashCode += obj.hashCode();
				}
			}
		}
		if (getSesso() != null) {
			_hashCode += getSesso().hashCode();
		}
		if (getStrAnnotazioniColloquio() != null) {
			_hashCode += getStrAnnotazioniColloquio().hashCode();
		}
		if (getStrCodiceFiscale() != null) {
			_hashCode += getStrCodiceFiscale().hashCode();
		}
		__hashCodeCalc = false;
		return _hashCode;
	}

	// Type metadata
	private static org.apache.axis.description.TypeDesc typeDesc = new org.apache.axis.description.TypeDesc(
			SchedaAnagraficaProfessionaleDTO.class, true);

	static {
		typeDesc.setXmlType(
				new javax.xml.namespace.QName("http://sap.eng.it/xml/sap", "schedaAnagraficaProfessionaleDTO"));
		org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("capDomicilio");
		elemField.setXmlName(new javax.xml.namespace.QName("", "capDomicilio"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
		elemField.setMinOccurs(0);
		elemField.setNillable(false);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("capResidenza");
		elemField.setXmlName(new javax.xml.namespace.QName("", "capResidenza"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
		elemField.setMinOccurs(0);
		elemField.setNillable(false);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("cellulare");
		elemField.setXmlName(new javax.xml.namespace.QName("", "cellulare"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
		elemField.setMinOccurs(0);
		elemField.setNillable(false);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("codComuneDomicilio");
		elemField.setXmlName(new javax.xml.namespace.QName("", "codComuneDomicilio"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
		elemField.setMinOccurs(0);
		elemField.setNillable(false);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("codComuneNascita");
		elemField.setXmlName(new javax.xml.namespace.QName("", "codComuneNascita"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
		elemField.setMinOccurs(0);
		elemField.setNillable(false);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("codComuneResidenza");
		elemField.setXmlName(new javax.xml.namespace.QName("", "codComuneResidenza"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
		elemField.setMinOccurs(0);
		elemField.setNillable(false);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("codNazioneCittadinanza");
		elemField.setXmlName(new javax.xml.namespace.QName("", "codNazioneCittadinanza"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
		elemField.setMinOccurs(0);
		elemField.setNillable(false);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("cognome");
		elemField.setXmlName(new javax.xml.namespace.QName("", "cognome"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
		elemField.setMinOccurs(0);
		elemField.setNillable(false);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("dataNascita");
		elemField.setXmlName(new javax.xml.namespace.QName("", "dataNascita"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "dateTime"));
		elemField.setMinOccurs(0);
		elemField.setNillable(false);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("descComuneDomicilio");
		elemField.setXmlName(new javax.xml.namespace.QName("", "descComuneDomicilio"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
		elemField.setMinOccurs(0);
		elemField.setNillable(false);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("descComuneNascita");
		elemField.setXmlName(new javax.xml.namespace.QName("", "descComuneNascita"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
		elemField.setMinOccurs(0);
		elemField.setNillable(false);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("descComuneResidenza");
		elemField.setXmlName(new javax.xml.namespace.QName("", "descComuneResidenza"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
		elemField.setMinOccurs(0);
		elemField.setNillable(false);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("descNazioneCittadinanza");
		elemField.setXmlName(new javax.xml.namespace.QName("", "descNazioneCittadinanza"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
		elemField.setMinOccurs(0);
		elemField.setNillable(false);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("email");
		elemField.setXmlName(new javax.xml.namespace.QName("", "email"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
		elemField.setMinOccurs(0);
		elemField.setNillable(false);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("fax");
		elemField.setXmlName(new javax.xml.namespace.QName("", "fax"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
		elemField.setMinOccurs(0);
		elemField.setNillable(false);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("idSap");
		elemField.setXmlName(new javax.xml.namespace.QName("", "idSap"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
		elemField.setMinOccurs(0);
		elemField.setNillable(false);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("indirizzoDomicilio");
		elemField.setXmlName(new javax.xml.namespace.QName("", "indirizzoDomicilio"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
		elemField.setMinOccurs(0);
		elemField.setNillable(false);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("indirizzoResidenza");
		elemField.setXmlName(new javax.xml.namespace.QName("", "indirizzoResidenza"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
		elemField.setMinOccurs(0);
		elemField.setNillable(false);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("nome");
		elemField.setXmlName(new javax.xml.namespace.QName("", "nome"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
		elemField.setMinOccurs(0);
		elemField.setNillable(false);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("sapAlboList");
		elemField.setXmlName(new javax.xml.namespace.QName("", "sapAlboList"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://exportsap.ws.stateless.ejb.model.mysap.sil.eng.it/",
				"sapAlboDTO"));
		elemField.setMinOccurs(0);
		elemField.setNillable(true);
		elemField.setMaxOccursUnbounded(true);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("sapConoscenzeInfoList");
		elemField.setXmlName(new javax.xml.namespace.QName("", "sapConoscenzeInfoList"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://exportsap.ws.stateless.ejb.model.mysap.sil.eng.it/",
				"sapConoscenzeInfoDTO"));
		elemField.setMinOccurs(0);
		elemField.setNillable(true);
		elemField.setMaxOccursUnbounded(true);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("sapEsperienzaLavList");
		elemField.setXmlName(new javax.xml.namespace.QName("", "sapEsperienzaLavList"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://exportsap.ws.stateless.ejb.model.mysap.sil.eng.it/",
				"sapEsperienzaLavDTO"));
		elemField.setMinOccurs(0);
		elemField.setNillable(true);
		elemField.setMaxOccursUnbounded(true);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("sapFormazioneList");
		elemField.setXmlName(new javax.xml.namespace.QName("", "sapFormazioneList"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://exportsap.ws.stateless.ejb.model.mysap.sil.eng.it/",
				"sapFormazioneDTO"));
		elemField.setMinOccurs(0);
		elemField.setNillable(true);
		elemField.setMaxOccursUnbounded(true);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("sapLinguaList");
		elemField.setXmlName(new javax.xml.namespace.QName("", "sapLinguaList"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://exportsap.ws.stateless.ejb.model.mysap.sil.eng.it/",
				"sapLinguaDTO"));
		elemField.setMinOccurs(0);
		elemField.setNillable(true);
		elemField.setMaxOccursUnbounded(true);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("sapPatenteList");
		elemField.setXmlName(new javax.xml.namespace.QName("", "sapPatenteList"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://exportsap.ws.stateless.ejb.model.mysap.sil.eng.it/",
				"sapPatenteDTO"));
		elemField.setMinOccurs(0);
		elemField.setNillable(true);
		elemField.setMaxOccursUnbounded(true);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("sapPatentinoList");
		elemField.setXmlName(new javax.xml.namespace.QName("", "sapPatentinoList"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://exportsap.ws.stateless.ejb.model.mysap.sil.eng.it/",
				"sapPatentinoDTO"));
		elemField.setMinOccurs(0);
		elemField.setNillable(true);
		elemField.setMaxOccursUnbounded(true);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("sapPropensioneList");
		elemField.setXmlName(new javax.xml.namespace.QName("", "sapPropensioneList"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://exportsap.ws.stateless.ejb.model.mysap.sil.eng.it/",
				"sapPropensioneDTO"));
		elemField.setMinOccurs(0);
		elemField.setNillable(true);
		elemField.setMaxOccursUnbounded(true);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("sapTitoloStudioList");
		elemField.setXmlName(new javax.xml.namespace.QName("", "sapTitoloStudioList"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://exportsap.ws.stateless.ejb.model.mysap.sil.eng.it/",
				"sapTitoloStudioDTO"));
		elemField.setMinOccurs(0);
		elemField.setNillable(true);
		elemField.setMaxOccursUnbounded(true);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("sesso");
		elemField.setXmlName(new javax.xml.namespace.QName("", "sesso"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
		elemField.setMinOccurs(0);
		elemField.setNillable(false);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("strAnnotazioniColloquio");
		elemField.setXmlName(new javax.xml.namespace.QName("", "strAnnotazioniColloquio"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
		elemField.setMinOccurs(0);
		elemField.setNillable(false);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("strCodiceFiscale");
		elemField.setXmlName(new javax.xml.namespace.QName("", "strCodiceFiscale"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
		elemField.setMinOccurs(0);
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
