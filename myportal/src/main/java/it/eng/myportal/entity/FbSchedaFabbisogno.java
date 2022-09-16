package it.eng.myportal.entity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.hibernate.annotations.Type;

import it.eng.myportal.entity.decodifiche.DeComune;
import it.eng.myportal.entity.decodifiche.DeFbTipoTirocinio;
import it.eng.myportal.entity.decodifiche.DeMansione;
import it.eng.myportal.entity.decodifiche.DeStatoFbScheda;
import it.eng.myportal.entity.decodifiche.DeTitolo;
import it.eng.myportal.entity.decodifiche.min.DeMansioneMin;

@Entity
@Table(name = "fb_scheda_fabbisogno", schema = "myportal")
public class FbSchedaFabbisogno extends AbstractEntity {
	private static final long serialVersionUID = -4420116014203683208L;

	private Integer idFbSchedaFabbisogno;
	private FbChecklist fbChecklist;
	private DeStatoFbScheda deStatoScheda;
	private FbDatiAzienda fbDatiAzienda;
	private String profilo;
	private DeFbTipoTirocinio deFbTipoTirocinio;
	private String attivitaDescr;
	private Integer numLavoratori;
	private DeMansione deMansione;
	private DeMansioneMin deMansioneMin;
	private DeTitolo deTitolo;
	private Integer etaMin;
	private Integer etaMax;
	private DeComune deComune;
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
	private Date dtChisura;

	private String motivoChiusura;
	private Boolean flgChiusuraManuale;

	private String noteEsito;

	// Rev. Lookup
	private List<FbCandidatura> fbCandidaturaList = new ArrayList<FbCandidatura>();

	@Id
	@SequenceGenerator(name = "fb_scheda_fabbisogno_id_fb_scheda_fabbisogno_seq", sequenceName = "fb_scheda_fabbisogno_id_fb_scheda_fabbisogno_seq", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "fb_scheda_fabbisogno_id_fb_scheda_fabbisogno_seq")
	@Column(name = "id_fb_scheda_fabbisogno", unique = true, nullable = false)
	public Integer getIdFbSchedaFabbisogno() {
		return idFbSchedaFabbisogno;
	}

	public void setIdFbSchedaFabbisogno(Integer idFbSchedaFabbisogno) {
		this.idFbSchedaFabbisogno = idFbSchedaFabbisogno;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_fb_checklist", nullable = false)
	public FbChecklist getFbChecklist() {
		return fbChecklist;
	}

	public void setFbChecklist(FbChecklist fbChecklist) {
		this.fbChecklist = fbChecklist;
	}

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "cod_fb_stato_scheda", nullable = false)
	public DeStatoFbScheda getDeStatoScheda() {
		return deStatoScheda;
	}

	public void setDeStatoScheda(DeStatoFbScheda deStatoScheda) {
		this.deStatoScheda = deStatoScheda;
	}

	@OneToOne(fetch = FetchType.EAGER, mappedBy = "fbSchedaFabbisogno")
	public FbDatiAzienda getFbDatiAzienda() {
		return fbDatiAzienda;
	}

	public void setFbDatiAzienda(FbDatiAzienda fbDatiAzienda) {
		this.fbDatiAzienda = fbDatiAzienda;
	}

	@Column(name = "profilo", length = 255, nullable = false)
	public String getProfilo() {
		return profilo;
	}

	public void setProfilo(String profilo) {
		this.profilo = profilo;
	}

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "cod_fb_tipo_tirocinio", nullable = false)
	public DeFbTipoTirocinio getDeFbTipoTirocinio() {
		return deFbTipoTirocinio;
	}

	public void setDeFbTipoTirocinio(DeFbTipoTirocinio deFbTipoTirocinio) {
		this.deFbTipoTirocinio = deFbTipoTirocinio;
	}

	@Column(name = "attivita_descr", length = 2000, nullable = false)
	public String getAttivitaDescr() {
		return attivitaDescr;
	}

	public void setAttivitaDescr(String attivitaDescr) {
		this.attivitaDescr = attivitaDescr;
	}

	@Column(name = "num_lavoratori", nullable = false)
	public Integer getNumLavoratori() {
		return numLavoratori;
	}

	public void setNumLavoratori(Integer numLavoratori) {
		this.numLavoratori = numLavoratori;
	}

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "cod_mansione")
	public DeMansione getDeMansione() {
		return deMansione;
	}

	public void setDeMansione(DeMansione deMansione) {
		this.deMansione = deMansione;
	}

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "cod_mansione_min")
	public DeMansioneMin getDeMansioneMin() {
		return deMansioneMin;
	}

	public void setDeMansioneMin(DeMansioneMin deMansioneMin) {
		this.deMansioneMin = deMansioneMin;
	}

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "cod_titolo")
	public DeTitolo getDeTitolo() {
		return deTitolo;
	}

	public void setDeTitolo(DeTitolo deTitolo) {
		this.deTitolo = deTitolo;
	}

	@Column(name = "eta_min")
	public Integer getEtaMin() {
		return etaMin;
	}

	public void setEtaMin(Integer etaMin) {
		this.etaMin = etaMin;
	}

	@Column(name = "eta_max")
	public Integer getEtaMax() {
		return etaMax;
	}

	public void setEtaMax(Integer etaMax) {
		this.etaMax = etaMax;
	}

	@Column(name = "dt_chiusura")
	public Date getDtChisura() {
		return dtChisura;
	}

	public void setDtChisura(Date dtChisura) {
		this.dtChisura = dtChisura;
	}

	/*
	 * @ManyToOne(fetch = FetchType.EAGER)
	 * 
	 * @JoinColumn(name = "cod_comune_nazione", nullable = false) public DeComune getNazione() { return nazione; }
	 * 
	 * public void setNazione(DeComune nazione) { this.nazione = nazione; }
	 */

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "cod_comune", nullable = true)
	public DeComune getDeComune() {
		return deComune;
	}

	public void setDeComune(DeComune deComune) {
		this.deComune = deComune;
	}

	@Column(name = "cap", length = 5, nullable = true)
	public String getCap() {
		return cap;
	}

	public void setCap(String cap) {
		this.cap = cap;
	}

	@Column(name = "indirizzo", length = 255, nullable = true)
	public String getIndirizzo() {
		return indirizzo;
	}

	public void setIndirizzo(String indirizzo) {
		this.indirizzo = indirizzo;
	}

	@Column(name = "nome_tutor", length = 50, nullable = false)
	public String getNomeTutor() {
		return nomeTutor;
	}

	public void setNomeTutor(String nomeTutor) {
		this.nomeTutor = nomeTutor;
	}

	@Column(name = "cognome_tutor", length = 50, nullable = false)
	public String getCognomeTutor() {
		return cognomeTutor;
	}

	public void setCognomeTutor(String cognomeTutor) {
		this.cognomeTutor = cognomeTutor;
	}

	@Column(name = "email_tutor", length = 255)
	public String getEmailTutor() {
		return emailTutor;
	}

	public void setEmailTutor(String emailTutor) {
		this.emailTutor = emailTutor;
	}

	@Column(name = "finalita_descr", length = 2000)
	public String getFinalitaDescr() {
		return finalitaDescr;
	}

	public void setFinalitaDescr(String finalitaDescr) {
		this.finalitaDescr = finalitaDescr;
	}

	@Column(name = "info_competenze_richieste", length = 2000)
	public String getInfoCompetenzeRichieste() {
		return infoCompetenzeRichieste;
	}

	public void setInfoCompetenzeRichieste(String infoCompetenzeRichieste) {
		this.infoCompetenzeRichieste = infoCompetenzeRichieste;
	}

	@Column(name = "info_competenze_base", length = 2000)
	public String getInfoCompetenzeBase() {
		return infoCompetenzeBase;
	}

	public void setInfoCompetenzeBase(String infoCompetenzeBase) {
		this.infoCompetenzeBase = infoCompetenzeBase;
	}

	@Column(name = "info_competenze_tecniche", length = 2000)
	public String getInfoCompetenzeTecniche() {
		return infoCompetenzeTecniche;
	}

	public void setInfoCompetenzeTecniche(String infoCompetenzeTecniche) {
		this.infoCompetenzeTecniche = infoCompetenzeTecniche;
	}

	@Column(name = "info_competenze_trasversali", length = 2000)
	public String getInfoCompetenzeTrasversali() {
		return infoCompetenzeTrasversali;
	}

	public void setInfoCompetenzeTrasversali(String infoCompetenzeTrasversali) {
		this.infoCompetenzeTrasversali = infoCompetenzeTrasversali;
	}

	@Column(name = "info_percorso", length = 2000)
	public String getInfoPercorso() {
		return infoPercorso;
	}

	public void setInfoPercorso(String infoPercorso) {
		this.infoPercorso = infoPercorso;
	}

	@Column(name = "info_indennita", length = 2000)
	public String getInfoIndennita() {
		return infoIndennita;
	}

	public void setInfoIndennita(String infoIndennita) {
		this.infoIndennita = infoIndennita;
	}

	@Column(name = "info_facilitazione", length = 2000)
	public String getInfoFacilitazione() {
		return infoFacilitazione;
	}

	public void setInfoFacilitazione(String infoFacilitazione) {
		this.infoFacilitazione = infoFacilitazione;
	}

	@Column(name = "info_orario", length = 2000)
	public String getInfoOrario() {
		return infoOrario;
	}

	public void setInfoOrario(String infoOrario) {
		this.infoOrario = infoOrario;
	}

	@Type(type = "yes_no")
	@Column(name = "flg_conformita", length = 1)
	public Boolean getFlgConformita() {
		return flgConformita;
	}

	public void setFlgConformita(Boolean flgConformita) {
		this.flgConformita = flgConformita;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "fbSchedaFabbisogno")
	public List<FbCandidatura> getFbCandidaturaList() {
		return fbCandidaturaList;
	}

	public void setFbCandidaturaList(List<FbCandidatura> fbCandidaturaList) {
		this.fbCandidaturaList = fbCandidaturaList;
	}

	@Column(name = "dt_pubblicazione")
	public Date getDtPubblicazione() {
		return dtPubblicazione;
	}

	public void setDtPubblicazione(Date dtPubblicazione) {
		this.dtPubblicazione = dtPubblicazione;
	}

	@Column(name = "motivo_chiusura", length = 2000)
	public String getMotivoChiusura() {
		return motivoChiusura;
	}

	public void setMotivoChiusura(String motivoChiusura) {
		this.motivoChiusura = motivoChiusura;
	}

	@Type(type = "yes_no")
	@Column(name = "flg_chiusura_manuale", length = 1)
	public Boolean getFlgChiusuraManuale() {
		return flgChiusuraManuale;
	}

	public void setFlgChiusuraManuale(Boolean flgChiusuraManuale) {
		this.flgChiusuraManuale = flgChiusuraManuale;
	}

	@Column(name = "note_esito", length = 2000)
	public String getNoteEsito() {
		return noteEsito;
	}

	public void setNoteEsito(String noteEsito) {
		this.noteEsito = noteEsito;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((idFbSchedaFabbisogno == null) ? 0 : idFbSchedaFabbisogno.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		FbSchedaFabbisogno other = (FbSchedaFabbisogno) obj;
		if (idFbSchedaFabbisogno == null) {
			if (other.idFbSchedaFabbisogno != null)
				return false;
		} else if (!idFbSchedaFabbisogno.equals(other.idFbSchedaFabbisogno))
			return false;
		return true;
	}

}
