package it.eng.myportal.dtos;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import it.eng.myportal.entity.enums.CodStatoVacancyEnum;

/**
 * @author Rodi A.
 * 
 */
public class VaDatiVacancyDTO extends AbstractUpdatablePkDTO {
	private static final long serialVersionUID = 6679193638681433244L;

	private static final long MAX_PROROGA_PREDELAY = 5;

	@Deprecated
	private DeAmbitoDiffusioneDTO ambitoDiffusione;

	private Integer anno;
	private Integer annoSIL;
	private Boolean anonima;
	private DeAttivitaMinDTO attivitaMin;
	private String attivitaPrincipale;
	private String codAteco;
	private String codComunicazioneSil;
	private String codMansione;
	private String codTrasferta;
	private Date dataAggiornamento;
	private Date dataPubblicazione;
	private String denominazioneIntermediario;
	private DeProvenienzaDTO deProvenienzaVacancyDTO;
	private DeProvenienzaDTO deProvenienzaVacancySILDTO;
	private String descrizione;
	private Date dtScadenza;
	private String esperienzaRichiesta;
	private Boolean flagEliminata;
	private Boolean flagInvioCl;
	private boolean fromClicLavoro;
	private boolean fromMyPortal;
	private boolean fromSIL;
	private Integer idVaContattoAlt;
	private Integer idVaContattoPrinc;
	private String intermediario;
	private boolean intermediata;
	private DeMansioneMinDTO mansioneMin;
	private DeMotivoChiusuraDTO motivoChiusura;

	private String noteAbilitazioni;
	private Integer numCandidati;
	private Integer numeroLavoratoriRicercati;
	private Integer numeroRichiesta;
	private Integer numeroRichiestaSIL;
	/*
	 * originariaSIL e' true se cod_provenienza_vacancy o cod_provenienza_sil inizia con SIL
	 */
	private boolean originariaSIL;
	/*
	 * proprietary e' true se la vacancy e' collegata all'utente azienda che la richiede, sia come azienda princiapel
	 * che come azienda palese
	 */
	private boolean proprietary;
	/*
	 * aziendaPrincipale e' true se l'azienda che richiede la vacancy ne e' collegata come azienda principale
	 */
	private boolean aziendaPrincipale;
	private String pubblicabile;
	private Date scadenzaPubblicazione;
	private Boolean sincronizzataCliclavoro;
	private String strAteco;
	private String strMansione;
	private String strTrasferta;

	private Boolean visibilita;

	private Integer idPfPrincipalAzienda;
	private Integer idPfPrincipalAziendaPalese;
	private String descRagSocialePfPrincipalAziendaPalese;
	
	private String indirizzoLavoro;
	private String codComLavoro;
	private String strComLavoro;
	private Integer idPoi;
	private String ragioneSociale;

	private String opzNullaOsta;
	private String notaOperatore;
	private String opzTipoDecodifiche;
	private boolean flgPacchettoCresco;
	private boolean flgCandidatura = true; // Default true alla creazione di nuova vacancy
	private CodStatoVacancyEnum codStatoVacancyEnum;
	
	private String descrCodMansioneIstat;
	private String codMansioneIstat;
	
	private boolean flgIdo;

	private boolean nonModificabile;

	private boolean aziendaRagSociale;
	
	private String motivazione;
	private Date dtArchiviazione;
	private Integer utenteArchiviazione;
	
	public VaDatiVacancyDTO() {
		super();
		attivitaMin = new DeAttivitaMinDTO();
		mansioneMin = new DeMansioneMinDTO();
	}

	// Una vacancy non è modificabile se proviene dal SIL.
	public boolean isNonModificabile() {
		return nonModificabile;
	}

	public boolean isInLavorazione() {
		return codStatoVacancyEnum.equals(CodStatoVacancyEnum.LAV);
	}

	public boolean isInPubblica() {
		return codStatoVacancyEnum.equals(CodStatoVacancyEnum.PUB);
	}

	public DeAmbitoDiffusioneDTO getAmbitoDiffusione() {
		return ambitoDiffusione;
	}

	public Integer getAnno() {
		return anno;
	}

	public Integer getAnnoSIL() {
		return annoSIL;
	}

	public Boolean getAnonima() {
		return anonima;
	}

	public DeAttivitaMinDTO getAttivitaMin() {
		return attivitaMin;
	}

	public String getAttivitaPrincipale() {
		return attivitaPrincipale;
	}

	public String getAttivitaPrincipaleEscape() {
		return attivitaPrincipale.replace("'", "\\'");
	}

	public String getCodAteco() {
		return codAteco;
	}

	public String getCodComunicazioneSil() {
		return codComunicazioneSil;
	}

	public String getCodMansione() {
		return codMansione;
	}

	public String getCodTrasferta() {
		return codTrasferta;
	}

	public Date getDataAggiornamento() {
		return dataAggiornamento;
	}

	public Date getDataPubblicazione() {
		return dataPubblicazione;
	}

	public String getDenominazioneIntermediario() {
		return denominazioneIntermediario;
	}

	public DeProvenienzaDTO getDeProvenienzaVacancyDTO() {
		return deProvenienzaVacancyDTO;
	}

	public DeProvenienzaDTO getDeProvenienzaVacancySILDTO() {
		return deProvenienzaVacancySILDTO;
	}

	public String getDescrizione() {
		return descrizione;
	}

	public Date getDtScadenza() {
		return dtScadenza;
	}

	public String getEsperienzaRichiesta() {
		return esperienzaRichiesta;
	}

	public Boolean getFlagEliminata() {
		return flagEliminata;
	}

	public Boolean getFlagInvioCl() {
		return flagInvioCl;
	}

	public Integer getIdVaContattoAlt() {
		return idVaContattoAlt;
	}

	public Integer getIdVaContattoPrinc() {
		return idVaContattoPrinc;
	}

	public String getIntermediario() {
		return intermediario;
	}

	public DeMansioneMinDTO getMansioneMin() {
		return mansioneMin;
	}

	public DeMotivoChiusuraDTO getMotivoChiusura() {
		return motivoChiusura;
	}

	public String getNoteAbilitazioni() {
		return noteAbilitazioni;
	}

	public Integer getNumCandidati() {
		return numCandidati;
	}

	public Integer getNumeroLavoratoriRicercati() {
		return numeroLavoratoriRicercati;
	}

	public Integer getNumeroRichiesta() {
		return numeroRichiesta;
	}

	public Integer getNumeroRichiestaSIL() {
		return numeroRichiestaSIL;
	}

	public boolean getProprietary() {
		return this.proprietary;
	}

	public String getPubblicabile() {
		return pubblicabile;
	}

	public Date getScadenzaPubblicazione() {
		return scadenzaPubblicazione;
	}

	public String getStrAteco() {
		return strAteco;
	}

	public String getStrMansione() {
		return strMansione;
	}

	public String getStrTrasferta() {
		return strTrasferta;
	}

	public Boolean getVisibilita() {
		return visibilita;
	}

	public boolean isFromClicLavoro() {
		return fromClicLavoro;
	}

	public boolean isFromMyPortal() {
		return fromMyPortal;
	}

	public boolean isFromSIL() {
		return fromSIL;
	}

	public boolean isIntermediata() {
		return intermediata;
	}

	public boolean isOriginariaSIL() {
		return originariaSIL;
	}

	public Boolean isSincronizzataCliclavoro() {
		return sincronizzataCliclavoro;
	}

	public void setAmbitoDiffusione(DeAmbitoDiffusioneDTO ambitoDiffusione) {
		this.ambitoDiffusione = ambitoDiffusione;
	}

	public void setAnno(Integer anno) {
		this.anno = anno;
	}

	public void setAnnoSIL(Integer annoSIL) {
		this.annoSIL = annoSIL;
	}

	public void setAnonima(Boolean anonima) {
		this.anonima = anonima;
	}

	public void setAttivitaMin(DeAttivitaMinDTO attivitaMin) {
		this.attivitaMin = attivitaMin;
	}

	public void setAttivitaPrincipale(String attivitaPrincipale) {
		this.attivitaPrincipale = attivitaPrincipale;
	}

	public void setCodAteco(String codAteco) {
		this.codAteco = codAteco;
	}

	public void setCodComunicazioneSil(String codComunicazioneSil) {
		this.codComunicazioneSil = codComunicazioneSil;
	}

	public void setCodMansione(String codMansione) {
		this.codMansione = codMansione;
	}

	public void setCodTrasferta(String codTrasferta) {
		this.codTrasferta = codTrasferta;
	}

	public void setDataAggiornamento(Date dataAggiornamento) {
		this.dataAggiornamento = dataAggiornamento;
	}

	public void setDataPubblicazione(Date dataPubblicazione) {
		this.dataPubblicazione = dataPubblicazione;
	}

	public void setDenominazioneIntermediario(String denominazioneIntermediario) {
		this.denominazioneIntermediario = denominazioneIntermediario;
	}

	public void setDeProvenienzaVacancyDTO(DeProvenienzaDTO deProvenienzaVacancyDTO) {
		this.deProvenienzaVacancyDTO = deProvenienzaVacancyDTO;
	}

	public void setDeProvenienzaVacancySILDTO(DeProvenienzaDTO deProvenienzaVacancySILDTO) {
		this.deProvenienzaVacancySILDTO = deProvenienzaVacancySILDTO;
	}

	public void setDescrizione(String descrizione) {
		this.descrizione = descrizione;
	}

	public void setDtScadenza(Date dtScadenza) {
		this.dtScadenza = dtScadenza;
	}

	public void setEsperienzaRichiesta(String esperienzaRichiesta) {
		this.esperienzaRichiesta = esperienzaRichiesta;
	}

	public void setFlagEliminata(Boolean flagEliminata) {
		this.flagEliminata = flagEliminata;
	}

	public void setFlagInvioCl(Boolean flagInvioCl) {
		this.flagInvioCl = flagInvioCl;
	}

	public void setFromClicLavoro(boolean fromClicLavoro) {
		this.fromClicLavoro = fromClicLavoro;
	}

	public void setFromMyPortal(boolean fromMyPortal) {
		this.fromMyPortal = fromMyPortal;
	}

	public void setFromSIL(boolean fromSIL) {
		this.fromSIL = fromSIL;
	}

	public void setIdVaContattoAlt(Integer idVaContattoAlt) {
		this.idVaContattoAlt = idVaContattoAlt;
	}

	public void setIdVaContattoPrinc(Integer idVaContattoPrinc) {
		this.idVaContattoPrinc = idVaContattoPrinc;
	}

	public void setIntermediario(String intermediario) {
		this.intermediario = intermediario;
	}

	public void setIntermediata(boolean intermediata) {
		this.intermediata = intermediata;
	}

	public void setMansioneMin(DeMansioneMinDTO mansioneMin) {
		this.mansioneMin = mansioneMin;
	}

	public void setMotivoChiusura(DeMotivoChiusuraDTO motivoChiusura) {
		this.motivoChiusura = motivoChiusura;
	}

	public void setNoteAbilitazioni(String noteAbilitazioni) {
		this.noteAbilitazioni = noteAbilitazioni;
	}

	public void setNumCandidati(Integer numCandidati) {
		this.numCandidati = numCandidati;
	}

	public void setNumeroLavoratoriRicercati(Integer numeroLavoratoriRicercati) {
		this.numeroLavoratoriRicercati = numeroLavoratoriRicercati;
	}

	public void setNumeroRichiesta(Integer numeroRichiesta) {
		this.numeroRichiesta = numeroRichiesta;
	}

	public void setNumeroRichiestaSIL(Integer numeroRichiestaSIL) {
		this.numeroRichiestaSIL = numeroRichiestaSIL;
	}

	public void setOriginariaSIL(boolean originariaSIL) {
		this.originariaSIL = originariaSIL;
	}

	public void setProprietary(boolean proprietary) {
		this.proprietary = proprietary;
	}

	public void setPubblicabile(String pubblicabile) {
		this.pubblicabile = pubblicabile;
	}

	public void setScadenzaPubblicazione(Date scadenzaPubblicazione) {
		this.scadenzaPubblicazione = scadenzaPubblicazione;
	}

	public void setSincronizzataCliclavoro(Boolean sincronizzataCliclavoro) {
		this.sincronizzataCliclavoro = sincronizzataCliclavoro;
	}

	public void setStrAteco(String strAteco) {
		this.strAteco = strAteco;
	}

	public void setStrMansione(String strMansione) {
		this.strMansione = strMansione;
	}

	public void setStrTrasferta(String strTrasferta) {
		this.strTrasferta = strTrasferta;
	}

	public void setVisibilita(Boolean visibilita) {
		this.visibilita = visibilita;
	}

	public boolean isAziendaPrincipale() {
		return aziendaPrincipale;
	}

	public void setAziendaPrincipale(boolean aziendaPrincipale) {
		this.aziendaPrincipale = aziendaPrincipale;
	}

	public Integer getIdPfPrincipalAzienda() {
		return idPfPrincipalAzienda;
	}

	public void setIdPfPrincipalAzienda(Integer idAzienda) {
		this.idPfPrincipalAzienda = idAzienda;
	}

	public Integer getIdPfPrincipalAziendaPalese() {
		return idPfPrincipalAziendaPalese;
	}

	public void setIdPfPrincipalAziendaPalese(Integer idAziendaInfoPalese) {
		this.idPfPrincipalAziendaPalese = idAziendaInfoPalese;
	}

	public String getIndirizzoLavoro() {
		return indirizzoLavoro;
	}

	public void setIndirizzoLavoro(String indirizzoLavoro) {
		this.indirizzoLavoro = indirizzoLavoro;
	}

	public String getCodComLavoro() {
		return codComLavoro;
	}

	public void setCodComLavoro(String codComLavoro) {
		this.codComLavoro = codComLavoro;
	}

	public String getStrComLavoro() {
		return strComLavoro;
	}

	public void setStrComLavoro(String strComLavoro) {
		this.strComLavoro = strComLavoro;
	}

	public Integer getIdPoi() {
		return idPoi;
	}

	public void setIdPoi(Integer idPoi) {
		this.idPoi = idPoi;
	}

	public String getRagioneSociale() {
		return ragioneSociale;
	}

	public void setRagioneSociale(String ragioneSociale) {
		this.ragioneSociale = ragioneSociale;
	}

	public String getOpzNullaOsta() {
		return opzNullaOsta;
	}

	public void setOpzNullaOsta(String opzNullaOsta) {
		this.opzNullaOsta = opzNullaOsta;
	}

	public String getNotaOperatore() {
		return notaOperatore;
	}

	public void setNotaOperatore(String notaOperatore) {
		this.notaOperatore = notaOperatore;
	}

	public String getOpzTipoDecodifiche() {
		return opzTipoDecodifiche;
	}

	public void setOpzTipoDecodifiche(String opzTipoDecodifiche) {
		this.opzTipoDecodifiche = opzTipoDecodifiche;
	}

	public boolean getFlgPacchettoCresco() {
		return flgPacchettoCresco;
	}

	public void setFlgPacchettoCresco(boolean flgPacchettoCresco) {
		this.flgPacchettoCresco = flgPacchettoCresco;
	}

	public boolean getFlgCandidatura() {
		return flgCandidatura;
	}

	public void setFlgCandidatura(boolean flgCandidatura) {
		this.flgCandidatura = flgCandidatura;
	}

	public CodStatoVacancyEnum getCodStatoVacancyEnum() {
		return codStatoVacancyEnum;
	}

	public void setCodStatoVacancyEnum(CodStatoVacancyEnum codStatoVacancyEnum) {
		this.codStatoVacancyEnum = codStatoVacancyEnum;
	}

	public boolean isInScadenza() {
		if (getScadenzaPubblicazione() != null) {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
			long diffInMillies, diff = 0;

			try {
				// Date scadezaDate = sdf.parse(new SimpleDateFormat("yyyy-MM-dd").format(getDtScadenza()));
				Date nowDate = sdf.parse(sdf.format(new Date()));
				diffInMillies = getScadenzaPubblicazione().getTime() - nowDate.getTime();
				diff = TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS);

			} catch (ParseException e) {
				e.printStackTrace();
			}

			if (diff <= MAX_PROROGA_PREDELAY && diff >= 0)
				return true;
			else {
				return false;
			}

		} else {
			return false;
		}
	}

	public boolean canDoAnotherProroga() {
		Calendar scadenza = Calendar.getInstance();
		long diffInMillies, diff = 0;
		// add 30 days to date of scadenza
		scadenza.setTime(getScadenzaPubblicazione());
		scadenza.add(Calendar.DAY_OF_MONTH, 30);

		Date newScadezaDate = scadenza.getTime();
		Date pubblicaDate = getDataPubblicazione();
		diffInMillies = newScadezaDate.getTime() - pubblicaDate.getTime();
		diff = TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS);

		if (diff < 60)
			return true;
		else
			return false;
	}

	public boolean getFlgIdo() {
		return flgIdo;
	}

	public void setFlgIdo(boolean flgIdo) {
		this.flgIdo = flgIdo;
	}

	public String getDescrCodMansioneIstat() {
		return descrCodMansioneIstat;
	}

	public void setDescrCodMansioneIstat(String descrCodMansioneIstat) {
		this.descrCodMansioneIstat = descrCodMansioneIstat;
	}

	public String getCodMansioneIstat() {
		return codMansioneIstat;
	}

	public void setCodMansioneIstat(String codMansioneIstat) {
		this.codMansioneIstat = codMansioneIstat;
	}

	public void setNonModificabile(boolean nonModificabile) {
		this.nonModificabile = nonModificabile;
	}

	public boolean isAziendaRagSociale() {
		return aziendaRagSociale;
	}

	public void setAziendaRagSociale(boolean aziendaRagSociale) {
		this.aziendaRagSociale = aziendaRagSociale;
	}

	public String getDescRagSocialePfPrincipalAziendaPalese() {
		return descRagSocialePfPrincipalAziendaPalese;
	}

	public void setDescRagSocialePfPrincipalAziendaPalese(String descRagSocialePfPrincipalAziendaPalese) {
		this.descRagSocialePfPrincipalAziendaPalese = descRagSocialePfPrincipalAziendaPalese;
	}

	public String getMotivazione() {
		return motivazione;
	}

	public void setMotivazione(String motivazione) {
		this.motivazione = motivazione;
	}

	public Date getDtArchiviazione() {
		return dtArchiviazione;
	}

	public void setDtArchiviazione(Date dtArchiviazione) {
		this.dtArchiviazione = dtArchiviazione;
	}

	public Integer getUtenteArchiviazione() {
		return utenteArchiviazione;
	}

	public void setUtenteArchiviazione(Integer utenteArchiviazione) {
		this.utenteArchiviazione = utenteArchiviazione;
	}
	
}
