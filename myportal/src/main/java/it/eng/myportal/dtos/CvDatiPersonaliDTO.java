package it.eng.myportal.dtos;

import it.eng.myportal.validator.Telefono;

import java.util.Date;

public class CvDatiPersonaliDTO extends AbstractUpdatablePkDTO {
	private static final long serialVersionUID = 8513716274538214151L;

	private String codComDomicilio;
	private String codComNascita;
	private String codComunicazioneSil;
	private DeAmbitoDiffusioneDTO deAmbitoDiffusione;
	private String denominazioneIntermediario;
	private String descrizione;
	private Date dtNascita;
	private Date dtScadenza;
	private String email;
	@Telefono
	private String fax;
	private String filename;
	private String fileNameTmp;
	private Boolean flagEliminato;
	private Boolean flagInviato;
	private Boolean fromCliclavoro;
	private Boolean fromMyPortal;
	// CvAllegato
	private Integer idCvAllegato;
	private int idMessaggioAzienda;
	private int idMessaggioUtente;

	private Integer idPfPrincipal;
	private Integer idPfPrincipalPalese;

	private Integer idPoiDomicilio;
	private String indirizzoDomicilio;
	private String intermediario;

	private DeMotivoChiusuraDTO motivoChiusura;

	private String noteAbilitazioni;

	private String opzTipoDecodifiche;
	private DeProvenienzaDTO provenienzaCurriculum;
	private String titolo;
	private String descrizioneCv;

	// true se il CV e' referenziato in almento un messaggio
	private Boolean referenziatoInMsg;
	private Boolean sincronizzatoCliclavoro;
	// true se le aziende possono vederlo
	private Boolean ricercabile;

	private String strComDomicilio;

	private String strComNascita;
	@Telefono
	private String tel1;

	@Telefono
	private String tel2;

	/**
	 * descrizione dell'ambito diffusione del cv
	 */
	private String descAmbDiff;

	private Boolean visibilita;

	private boolean intermediato;
	
	private Boolean valido;

	private Boolean flgIdo;
	
	public CvDatiPersonaliDTO() {
		super();
		deAmbitoDiffusione = new DeAmbitoDiffusioneDTO();
	}

	/**
	 * @return the codComDomicilio
	 */
	public String getCodComDomicilio() {
		return codComDomicilio;
	}

	/**
	 * @return the codComNascita
	 */
	public String getCodComNascita() {
		return codComNascita;
	}

	public String getCodComunicazioneSil() {
		return codComunicazioneSil;
	}

	public DeAmbitoDiffusioneDTO getDeAmbitoDiffusione() {
		return deAmbitoDiffusione;
	}

	public String getDenominazioneIntermediario() {
		return denominazioneIntermediario;
	}

	public String getDescrizione() {
		return descrizione;
	}

	public Date getDtNascita() {
		return dtNascita;
	}

	public Date getDtScadenza() {
		return dtScadenza;
	}

	public String getEmail() {
		return email;
	}

	public String getFax() {
		return fax;
	}

	public String getFilename() {
		return filename;
	}

	public String getFileNameTmp() {
		return fileNameTmp;
	}

	public Boolean getFlagEliminato() {
		return flagEliminato;
	}

	public Boolean getFlagInviato() {
		return flagInviato;
	}

	public Boolean getFromCliclavoro() {
		return fromCliclavoro;
	}

	public Boolean getFromMyPortal() {
		return fromMyPortal;
	}

	public Integer getIdCvAllegato() {
		return idCvAllegato;
	}

	public int getIdMessaggioAzienda() {
		return idMessaggioAzienda;
	}

	public int getIdMessaggioUtente() {
		return idMessaggioUtente;
	}

	public Integer getIdPfPrincipal() {
		return idPfPrincipal;
	}

	public Integer getIdPfPrincipalPalese() {
		return idPfPrincipalPalese;
	}

	public Integer getIdPoiDomicilio() {
		return idPoiDomicilio;
	}

	public String getIndirizzoDomicilio() {
		return indirizzoDomicilio;
	}

	public String getIntermediario() {
		return intermediario;
	}

	public DeMotivoChiusuraDTO getMotivoChiusura() {
		return motivoChiusura;
	}

	/**
	 * @return the noteAbilitazioni
	 */
	public String getNoteAbilitazioni() {
		return noteAbilitazioni;
	}

	public DeProvenienzaDTO getProvenienzaCurriculum() {
		return provenienzaCurriculum;
	}

	public Boolean getReferenziatoInMsg() {
		return referenziatoInMsg;
	}

	/**
	 * @return the strComDomicilio
	 */
	public String getStrComDomicilio() {
		return strComDomicilio;
	}

	/**
	 * @return the strComNascita
	 */
	public String getStrComNascita() {
		return strComNascita;
	}

	public String getTel1() {
		return tel1;
	}

	public String getTel2() {
		return tel2;
	}

	public Boolean getVisibilita() {
		return visibilita;
	}

	/**
	 * Il proprietario del CV e' il pfprincipal palese se esiste, altrimenti il pfprincipal
	 */
	public boolean isProprietario(Integer idPfPrincipal) {
		boolean result = false;

		if (idPfPrincipalPalese != null) {
			result = idPfPrincipalPalese.equals(idPfPrincipal);
		} else {
			result = this.idPfPrincipal.equals(idPfPrincipal);
		}

		return result;
	}

	public Boolean isSincronizzatoCliclavoro() {
		return sincronizzatoCliclavoro;
	}

	/**
	 * @param codComDomicilio
	 *            the codComDomicilio to set
	 */
	public void setCodComDomicilio(String codComDomicilio) {
		this.codComDomicilio = codComDomicilio;
	}

	/**
	 * @param codComNascita
	 *            the codComNascita to set
	 */
	public void setCodComNascita(String codComNascita) {
		this.codComNascita = codComNascita;
	}

	public void setCodComunicazioneSil(String codComunicazioneSil) {
		this.codComunicazioneSil = codComunicazioneSil;
	}

	public void setDeAmbitoDiffusione(DeAmbitoDiffusioneDTO deAmbitoDiffusione) {
		this.deAmbitoDiffusione = deAmbitoDiffusione;
	}

	public void setDenominazioneIntermediario(String denominazioneIntermediario) {
		this.denominazioneIntermediario = denominazioneIntermediario;
	}

	public void setDescrizione(String descrizione) {
		this.descrizione = descrizione;
	}

	public void setDtNascita(Date dtNascita) {
		this.dtNascita = dtNascita;
	}

	public void setDtScadenza(Date dtScadenza) {
		this.dtScadenza = dtScadenza;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public void setFax(String fax) {
		this.fax = fax;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public void setFileNameTmp(String fileNameTmp) {
		this.fileNameTmp = fileNameTmp;
	}

	public void setFlagEliminato(Boolean flagEliminato) {
		this.flagEliminato = flagEliminato;
	}

	public void setFlagInviato(Boolean flagInviato) {
		this.flagInviato = flagInviato;
	}

	public void setFromCliclavoro(Boolean fromCliclavoro) {
		this.fromCliclavoro = fromCliclavoro;
	}

	public void setFromMyPortal(Boolean fromMyPortal) {
		this.fromMyPortal = fromMyPortal;
	}

	public void setIdCvAllegato(Integer idCvAllegato) {
		this.idCvAllegato = idCvAllegato;
	}

	public void setIdMessaggioAzienda(int idMessaggioAzienda) {
		this.idMessaggioAzienda = idMessaggioAzienda;
	}

	public void setIdMessaggioUtente(int idMessaggioUtente) {
		this.idMessaggioUtente = idMessaggioUtente;
	}

	public void setIdPfPrincipal(Integer idPfPrincipal) {
		this.idPfPrincipal = idPfPrincipal;
	}

	public void setIdPfPrincipalPalese(Integer idPfPrincipalPalese) {
		this.idPfPrincipalPalese = idPfPrincipalPalese;
	}

	public void setIdPoiDomicilio(Integer idPoiDomicilio) {
		this.idPoiDomicilio = idPoiDomicilio;
	}

	public void setIndirizzoDomicilio(String indirizzoDomicilio) {
		this.indirizzoDomicilio = indirizzoDomicilio;
	}

	public void setIntermediario(String intermediario) {
		this.intermediario = intermediario;
	}

	public void setMotivoChiusura(DeMotivoChiusuraDTO motivoChiusura) {
		this.motivoChiusura = motivoChiusura;
	}

	/**
	 * @param noteAbilitazioni
	 *            the noteAbilitazioni to set
	 */
	public void setNoteAbilitazioni(String noteAbilitazioni) {
		this.noteAbilitazioni = noteAbilitazioni;
	}

	public void setProvenienzaCurriculum(DeProvenienzaDTO provenienzaCurriculum) {
		this.provenienzaCurriculum = provenienzaCurriculum;
	}

	public void setReferenziatoInMsg(Boolean referenziatoInMsg) {
		this.referenziatoInMsg = referenziatoInMsg;
	}

	public void setSincronizzatoCliclavoro(Boolean sincronizzatoCliclavoro) {
		this.sincronizzatoCliclavoro = sincronizzatoCliclavoro;
	}

	/**
	 * @param strComDomicilio
	 *            the strComDomicilio to set
	 */
	public void setStrComDomicilio(String strComDomicilio) {
		this.strComDomicilio = strComDomicilio;
	}

	public Boolean isRicercabile() {
		return ricercabile;
	}

	public Boolean getRicercabile() {
		return ricercabile;
	}

	public void setRicercabile(Boolean isRicercabile) {
		this.ricercabile = isRicercabile;
	}

	/**
	 * @param strComNascita
	 *            the strComNascita to set
	 */
	public void setStrComNascita(String strComNascita) {
		this.strComNascita = strComNascita;
	}

	public void setTel1(String tel1) {
		this.tel1 = tel1;
	}

	public void setTel2(String tel2) {
		this.tel2 = tel2;
	}

	public void setVisibilita(Boolean visibilita) {
		this.visibilita = visibilita;
	}

	public boolean isIntermediato() {
		return intermediato;
	}

	public void setIntermediato(boolean intermediato) {
		this.intermediato = intermediato;
	}

	@Override
	public String toString() {
		return String
				.format("CvDatiPersonaliDTO [idPfPrincipal=%s, dtNascita=%s, codComNascita=%s, strComNascita=%s, codComDomicilio=%s, strComDomicilio=%s, indirizzoDomicilio=%s, email=%s, descrizione=%s, flagInviato=%s, flagEliminato=%s, idCvDatiPersonali=%s, filename=%s, tel1=%s, tel2=%s, fax=%s, idPoiDomicilio=%s, referenziatoInMsg=%s]",
						idPfPrincipal, dtNascita, codComNascita, strComNascita, codComDomicilio, strComDomicilio,
						indirizzoDomicilio, email, descrizione, flagInviato, flagEliminato, idCvAllegato, filename,
						tel1, tel2, fax, idPoiDomicilio, referenziatoInMsg);
	}

	public String getDescAmbDiff() {
		descAmbDiff = "Regionale";
		if (deAmbitoDiffusione == null || "01".equalsIgnoreCase(deAmbitoDiffusione.getId())) {
			descAmbDiff = "Regionale";
		}
		if ("02".equalsIgnoreCase(deAmbitoDiffusione.getId())) {
			descAmbDiff = "Nazionale";
		}
		if ("03".equalsIgnoreCase(deAmbitoDiffusione.getId())) {
			descAmbDiff = "Europeo";
		}
		return descAmbDiff;
	}

	public void setDescAmbDiff(String descAmbDiff) {
		this.descAmbDiff = descAmbDiff;
	}

	public String getOpzTipoDecodifiche() {
		return opzTipoDecodifiche;
	}

	public void setOpzTipoDecodifiche(String opzTipoDecodifiche) {
		this.opzTipoDecodifiche = opzTipoDecodifiche;
	}

	public String getTitolo() {
		return titolo;
	}

	public void setTitolo(String titolo) {
		this.titolo = titolo;
	}

	public String getDescrizioneCv() {
		return descrizioneCv;
	}

	public void setDescrizioneCv(String descrizioneCv) {
		this.descrizioneCv = descrizioneCv;
	}

	public Boolean getValido() {
		return valido;
	}

	public void setValido(Boolean valido) {
		this.valido = valido;
	}

	public Boolean getFlgIdo() {
		return flgIdo;
	}

	public void setFlgIdo(Boolean flgIdo) {
		this.flgIdo = flgIdo;
	}
}
