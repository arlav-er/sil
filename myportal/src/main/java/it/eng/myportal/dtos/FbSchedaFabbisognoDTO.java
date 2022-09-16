package it.eng.myportal.dtos;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import it.eng.myportal.utils.ConstantsSingleton;

public class FbSchedaFabbisognoDTO extends AbstractUpdatablePkDTO {
	private static final long serialVersionUID = -135800422098157939L;

	private Integer idFbChecklist;
	private DeStatoFbSchedaDTO deStatoScheda;
	private FbDatiAziendaDTO fbDatiAzienda;
	private String profilo;
	private DeFbTipoTirocinioDTO deFbTipoTirocinio;
	private String attivitaDescr;
	private Integer numLavoratori;
	private DeMansioneDTO deMansione;
	private DeMansioneMinDTO deMansioneMin;
	private DeTitoloDTO deTitolo;
	private Integer etaMin;
	private Integer etaMax;
	private DeComuneDTO nazione;
	private DeComuneDTO deComune;
	private String cap;
	private String indirizzo;
	private String nomeTutor;
	private String cognomeTutor;
	private String emailTutor;
	private String finalitaDescr;
	private String infoCompetenzeRichieste;
	private String infoCompetenzeBase;
	private String infoCompetenzeTecniche;
	private String infoCompetenzeTrasversali;
	private String infoPercorso;
	private String infoIndennita;
	private String infoFacilitazione;
	private String infoOrario;
	private Boolean flgConformita;
	private Date dtPubblicazione;
	private Date dtChiusura;
	private String motivoChiusura;
	private Boolean flgChiusuraManuale;
	private String noteEsito;

	// Campi non presenti sul DB, li aggiungo per comodità
	private String aziendaOspitante;

	private List<FbCandidaturaDTO> candidature;

	public FbSchedaFabbisognoDTO() {
		deStatoScheda = new DeStatoFbSchedaDTO();
		fbDatiAzienda = new FbDatiAziendaDTO();
		candidature = new ArrayList<FbCandidaturaDTO>();
	}

	public boolean isModificabile() {
		return ConstantsSingleton.DeStatoFbScheda.IN_LAVORAZIONE.equals(deStatoScheda.getId());
	}

	public boolean isChiusa() {
		return ConstantsSingleton.DeStatoFbScheda.CHIUSA.equals(deStatoScheda.getId());
	}

	public Integer getIdFbChecklist() {
		return idFbChecklist;
	}

	public void setIdFbChecklist(Integer idFbChecklist) {
		this.idFbChecklist = idFbChecklist;
	}

	public DeStatoFbSchedaDTO getDeStatoScheda() {
		return deStatoScheda;
	}

	public void setDeStatoScheda(DeStatoFbSchedaDTO deStatoScheda) {
		this.deStatoScheda = deStatoScheda;
	}

	public FbDatiAziendaDTO getFbDatiAzienda() {
		return fbDatiAzienda;
	}

	public void setFbDatiAzienda(FbDatiAziendaDTO fbDatiAzienda) {
		this.fbDatiAzienda = fbDatiAzienda;
	}

	public String getProfilo() {
		return profilo;
	}

	public void setProfilo(String profilo) {
		this.profilo = profilo;
	}

	public DeFbTipoTirocinioDTO getDeFbTipoTirocinio() {
		return deFbTipoTirocinio;
	}

	public void setDeFbTipoTirocinio(DeFbTipoTirocinioDTO deFbTipoTirocinio) {
		this.deFbTipoTirocinio = deFbTipoTirocinio;
	}

	public String getAttivitaDescr() {
		return attivitaDescr;
	}

	public void setAttivitaDescr(String attivitaDescr) {
		this.attivitaDescr = attivitaDescr;
	}

	public Integer getNumLavoratori() {
		return numLavoratori;
	}

	public void setNumLavoratori(Integer numLavoratori) {
		this.numLavoratori = numLavoratori;
	}

	public DeMansioneDTO getDeMansione() {
		return deMansione;
	}

	public void setDeMansione(DeMansioneDTO deMansione) {
		this.deMansione = deMansione;
	}

	public DeMansioneMinDTO getDeMansioneMin() {
		return deMansioneMin;
	}

	public void setDeMansioneMin(DeMansioneMinDTO deMansioneMin) {
		this.deMansioneMin = deMansioneMin;
	}

	public DeTitoloDTO getDeTitolo() {
		return deTitolo;
	}

	public void setDeTitolo(DeTitoloDTO deTitolo) {
		this.deTitolo = deTitolo;
	}

	public Integer getEtaMin() {
		return etaMin;
	}

	public void setEtaMin(Integer etaMin) {
		this.etaMin = etaMin;
	}

	public Integer getEtaMax() {
		return etaMax;
	}

	public void setEtaMax(Integer etaMax) {
		this.etaMax = etaMax;
	}

	public DeComuneDTO getNazione() {
		return nazione;
	}

	public void setNazione(DeComuneDTO nazione) {
		this.nazione = nazione;
	}

	public DeComuneDTO getDeComune() {
		return deComune;
	}

	public void setDeComune(DeComuneDTO deComune) {
		this.deComune = deComune;
	}

	public String getCap() {
		return cap;
	}

	public void setCap(String cap) {
		this.cap = cap;
	}

	public String getIndirizzo() {
		return indirizzo;
	}

	public void setIndirizzo(String indirizzo) {
		this.indirizzo = indirizzo;
	}

	public String getNomeTutor() {
		return nomeTutor;
	}

	public void setNomeTutor(String nomeTutor) {
		this.nomeTutor = nomeTutor;
	}

	public String getCognomeTutor() {
		return cognomeTutor;
	}

	public void setCognomeTutor(String cognomeTutor) {
		this.cognomeTutor = cognomeTutor;
	}

	public String getEmailTutor() {
		return emailTutor;
	}

	public void setEmailTutor(String emailTutor) {
		this.emailTutor = emailTutor;
	}

	public String getFinalitaDescr() {
		return finalitaDescr;
	}

	public void setFinalitaDescr(String finalitaDescr) {
		this.finalitaDescr = finalitaDescr;
	}

	public String getInfoCompetenzeRichieste() {
		return infoCompetenzeRichieste;
	}

	public void setInfoCompetenzeRichieste(String infoCompetenzeRichieste) {
		this.infoCompetenzeRichieste = infoCompetenzeRichieste;
	}

	public String getInfoCompetenzeBase() {
		return infoCompetenzeBase;
	}

	public void setInfoCompetenzeBase(String infoCompetenzeBase) {
		this.infoCompetenzeBase = infoCompetenzeBase;
	}

	public String getInfoCompetenzeTecniche() {
		return infoCompetenzeTecniche;
	}

	public void setInfoCompetenzeTecniche(String infoCompetenzeTecniche) {
		this.infoCompetenzeTecniche = infoCompetenzeTecniche;
	}

	public String getInfoCompetenzeTrasversali() {
		return infoCompetenzeTrasversali;
	}

	public void setInfoCompetenzeTrasversali(String infoCompetenzeTrasversali) {
		this.infoCompetenzeTrasversali = infoCompetenzeTrasversali;
	}

	public String getInfoPercorso() {
		return infoPercorso;
	}

	public void setInfoPercorso(String infoPercorso) {
		this.infoPercorso = infoPercorso;
	}

	public String getInfoIndennita() {
		return infoIndennita;
	}

	public void setInfoIndennita(String infoIndennita) {
		this.infoIndennita = infoIndennita;
	}

	public String getInfoFacilitazione() {
		return infoFacilitazione;
	}

	public void setInfoFacilitazione(String infoFacilitazione) {
		this.infoFacilitazione = infoFacilitazione;
	}

	public String getInfoOrario() {
		return infoOrario;
	}

	public void setInfoOrario(String infoOrario) {
		this.infoOrario = infoOrario;
	}

	public Boolean getFlgConformita() {
		return flgConformita;
	}

	public void setFlgConformita(Boolean flgConformita) {
		this.flgConformita = flgConformita;
	}

	public List<FbCandidaturaDTO> getCandidature() {
		return candidature;
	}

	public void setCandidature(List<FbCandidaturaDTO> candidature) {
		this.candidature = candidature;
	}

	public Date getDtPubblicazione() {
		return dtPubblicazione;
	}

	public void setDtPubblicazione(Date dtPubblicazione) {
		this.dtPubblicazione = dtPubblicazione;
	}

	public String getMotivoChiusura() {
		return motivoChiusura;
	}

	public void setMotivoChiusura(String motivoChiusura) {
		this.motivoChiusura = motivoChiusura;
	}

	public Boolean getFlgChiusuraManuale() {
		return flgChiusuraManuale;
	}

	public void setFlgChiusuraManuale(Boolean flgChiusuraManuale) {
		this.flgChiusuraManuale = flgChiusuraManuale;
	}

	public String getAziendaOspitante() {
		return aziendaOspitante;
	}

	public void setAziendaOspitante(String aziendaOspitante) {
		this.aziendaOspitante = aziendaOspitante;
	}

	public Date getDtChiusura() {
		return dtChiusura;
	}

	public void setDtChiusura(Date dtChiusura) {
		this.dtChiusura = dtChiusura;
	}

	public String getNoteEsito() {
		return noteEsito;
	}

	public void setNoteEsito(String noteEsito) {
		this.noteEsito = noteEsito;
	}

}
