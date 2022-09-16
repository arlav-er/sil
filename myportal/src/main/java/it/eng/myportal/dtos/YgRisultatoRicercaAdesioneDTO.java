package it.eng.myportal.dtos;

import it.eng.myportal.entity.decodifiche.DeProvincia;
import it.eng.myportal.utils.ConstantsSingleton;

import java.util.Date;

public class YgRisultatoRicercaAdesioneDTO {

	private Integer idYgAdesione;
	private String codiceFiscale;
	private String cognome;
	private String nome;
	private String email;
	Date dtAdesione;
	private Boolean flgPresoInCarico;
	Date dtPresoInCarico;

	private Boolean modificaFlgPresoInCarico;
	Date modificaDtPresoInCarico;

	String codRegioneAdesione;

	String denominazioneProvincia;
	String denominazioneRegioneAdesione;

	private String codMonoProv;
	private String denominazioneCpiAssegnazione;
	private String flgProcessatoBatch;
	String codMonoRecuperoCpi;
	private String username;

	private String cognomeRifNotifica;
	private String nomeRifNotifica;
	private String emailRifNotifica;

	private String nomeUtente;
	private String cognomeUtente;
	private String emailUtente;

	private String codStatoAdesione;
	private String descrizioneStatoAdesione;
	private Date dtFineStatoAdesione;
	private String note;
	private Date modificaDtFineStatoAdesione;
	private String modificaCodStatoAdesione;
	private String modificaNote;
	private boolean noteStatoAdesioneVisible;
	private DeProvincia deProvinciaRifNotifica;
	private String codProvinciaRif;

	private String codStatoAdesioneMin;
	private String codMonoAttivaMin;
	private String descStatoAdesioneMin;
	private Date dtStatoAdesioneMin;
	/* true se l'utente adesione puo' riattivare l'adesione */
	private boolean canReactivate;

	public YgRisultatoRicercaAdesioneDTO(Date dtAdesione, String codRegioneAdesione, String codProvinciaRif,
			String codStatoAdesioneMin, Date dtStatoAdesioneMin) {
		this.dtAdesione = dtAdesione;

		this.codRegioneAdesione = codRegioneAdesione;
		this.codProvinciaRif = codProvinciaRif;
		this.codStatoAdesioneMin = codStatoAdesioneMin;
		this.dtStatoAdesioneMin = dtStatoAdesioneMin;
		/* true se l'utente adesione puo' riattivare l'adesione */
		this.canReactivate = setReactivateOption(codRegioneAdesione, codStatoAdesioneMin);
	}

	public YgRisultatoRicercaAdesioneDTO(Integer idYgAdesione, String codiceFiscale, String cognome, String nome,
			String cognomeRifNotifica, String nomeRifNotifica, String email, String emailRifNotifica, Date dtAdesione,
			Date dtAdesioneMin, Boolean flgPresoInCarico, Date dtPresoInCarico, String denominazioneProvincia,
			String codRegioneAdesione, String denominazioneRegioneAdesione, String codMonoProv,
			String denominazioneCpiAssegnazione, String codMonoRecuperoCpi, String username, String codStatoAdesione,
			String descrizioneStatoAdesione, String statoMin, String codMonoMin, String descMin,
			Date dtFineStatoAdesione, String note, String codProvinciaRif) {

		this.idYgAdesione = idYgAdesione;
		this.codiceFiscale = codiceFiscale;
		this.cognome = cognome;
		this.nome = nome;
		this.email = email;
		this.codStatoAdesioneMin = statoMin;
		this.codMonoAttivaMin = codMonoMin;
		this.descStatoAdesioneMin = descMin;
		this.cognomeRifNotifica = cognomeRifNotifica;
		this.nomeRifNotifica = nomeRifNotifica;
		this.emailRifNotifica = emailRifNotifica;

		this.dtAdesione = dtAdesione;
		this.dtStatoAdesioneMin = dtAdesioneMin;
		this.flgPresoInCarico = flgPresoInCarico;
		this.dtPresoInCarico = dtPresoInCarico;

		this.modificaFlgPresoInCarico = flgPresoInCarico;
		this.modificaDtPresoInCarico = dtPresoInCarico;

		if (flgPresoInCarico == null) {
			this.flgPresoInCarico = false;
			this.modificaFlgPresoInCarico = false;
		}

		if (denominazioneProvincia != null) {
			this.denominazioneProvincia = denominazioneProvincia.toUpperCase();
		}

		this.codRegioneAdesione = codRegioneAdesione;

		if (denominazioneRegioneAdesione != null) {
			this.denominazioneRegioneAdesione = denominazioneRegioneAdesione;
		}

		this.codMonoProv = codMonoProv;

		if (denominazioneCpiAssegnazione != null) {
			this.denominazioneCpiAssegnazione = denominazioneCpiAssegnazione;
		}

		if (codMonoRecuperoCpi != null) {
			if ("N".equalsIgnoreCase(codMonoRecuperoCpi)) {
				this.flgProcessatoBatch = "SAP non recuperabile";
			} else {
				this.flgProcessatoBatch = "SI";
			}
		} else {
			this.flgProcessatoBatch = "";
		}

		this.username = username;

		if ("I".equalsIgnoreCase(codMonoProv)) {
			this.nomeUtente = nome;
			this.cognomeUtente = cognome;
			this.emailUtente = email;
		} else if ("N".equalsIgnoreCase(codMonoProv)) {
			this.nomeUtente = nomeRifNotifica;
			this.cognomeUtente = cognomeRifNotifica;
			this.emailUtente = emailRifNotifica;
		}

		this.codStatoAdesione = codStatoAdesione;
		this.descrizioneStatoAdesione = descrizioneStatoAdesione;
		this.dtFineStatoAdesione = dtFineStatoAdesione;
		this.note = note;

		this.modificaCodStatoAdesione = codStatoAdesione;
		this.modificaDtFineStatoAdesione = dtFineStatoAdesione;
		this.modificaNote = note;

		this.noteStatoAdesioneVisible = false;

		this.codProvinciaRif = codProvinciaRif;

		// se I -> sempre visibile
		// se N -> visibile solo se la provincia è valorizzata
		if (this.codMonoProv != null && "I".equalsIgnoreCase(this.codMonoProv)) {
			this.noteStatoAdesioneVisible = true;
		} else if (this.codMonoProv != null && "N".equalsIgnoreCase(this.codMonoProv)) {
			this.noteStatoAdesioneVisible = false;
			if (this.codProvinciaRif != null && !"".equalsIgnoreCase(this.codProvinciaRif)) {
				this.noteStatoAdesioneVisible = true;
			}
		}

		/* true se l'utente adesione puo' riattivare l'adesione */
		this.canReactivate = setReactivateOption(codRegioneAdesione, codStatoAdesioneMin);
	}

	private boolean setReactivateOption(String codRegioneAdesione, String codStatoAdesioneMin) {
		boolean res = false;

		if (ConstantsSingleton.COD_REGIONE.toString().equals(codRegioneAdesione)
				&& ("C".equals(codStatoAdesioneMin) || "R".equals(codStatoAdesioneMin)
						|| "N".equals(codStatoAdesioneMin) || "U".equals(codStatoAdesioneMin))) {
			res = true;
		}

		return res;
	}

	public Integer getIdYgAdesione() {
		return idYgAdesione;
	}

	public void setIdYgAdesione(Integer idYgAdesione) {
		this.idYgAdesione = idYgAdesione;
	}

	public String getCodiceFiscale() {
		return codiceFiscale;
	}

	public void setCodiceFiscale(String codiceFiscale) {
		this.codiceFiscale = codiceFiscale;
	}

	public String getCognome() {
		return cognome;
	}

	public void setCognome(String cognome) {
		this.cognome = cognome;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Date getDtAdesione() {
		return dtAdesione;
	}

	public void setDtAdesione(Date dtAdesione) {
		this.dtAdesione = dtAdesione;
	}

	public Boolean getFlgPresoInCarico() {
		return flgPresoInCarico;
	}

	public void setFlgPresoInCarico(Boolean flgPresoInCarico) {
		this.flgPresoInCarico = flgPresoInCarico;
	}

	public Date getDtPresoInCarico() {
		return dtPresoInCarico;
	}

	public void setDtPresoInCarico(Date dtPresoInCarico) {
		this.dtPresoInCarico = dtPresoInCarico;
	}

	public Boolean getModificaFlgPresoInCarico() {
		return modificaFlgPresoInCarico;
	}

	public void setModificaFlgPresoInCarico(Boolean modificaFlgPresoInCarico) {
		this.modificaFlgPresoInCarico = modificaFlgPresoInCarico;
	}

	public Date getModificaDtPresoInCarico() {
		return modificaDtPresoInCarico;
	}

	public void setModificaDtPresoInCarico(Date modificaDtPresoInCarico) {
		this.modificaDtPresoInCarico = modificaDtPresoInCarico;
	}

	public String getDenominazioneProvincia() {
		return denominazioneProvincia;
	}

	public void setDenominazioneProvincia(String denominazioneProvincia) {
		this.denominazioneProvincia = denominazioneProvincia;
	}

	public String getDenominazioneRegioneAdesione() {
		return denominazioneRegioneAdesione;
	}

	public void setDenominazioneRegioneAdesione(String denominazioneRegioneAdesione) {
		this.denominazioneRegioneAdesione = denominazioneRegioneAdesione;
	}

	public String getCodMonoProv() {
		return codMonoProv;
	}

	public void setCodMonoProv(String codMonoProv) {
		this.codMonoProv = codMonoProv;
	}

	public String getDenominazioneCpiAssegnazione() {
		return denominazioneCpiAssegnazione;
	}

	public void setDenominazioneCpiAssegnazione(String denominazioneCpiAssegnazione) {
		this.denominazioneCpiAssegnazione = denominazioneCpiAssegnazione;
	}

	public String getFlgProcessatoBatch() {
		return flgProcessatoBatch;
	}

	public void setFlgProcessatoBatch(String flgProcessatoBatch) {
		this.flgProcessatoBatch = flgProcessatoBatch;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getCognomeRifNotifica() {
		return cognomeRifNotifica;
	}

	public void setCognomeRifNotifica(String cognomeRifNotifica) {
		this.cognomeRifNotifica = cognomeRifNotifica;
	}

	public String getNomeRifNotifica() {
		return nomeRifNotifica;
	}

	public void setNomeRifNotifica(String nomeRifNotifica) {
		this.nomeRifNotifica = nomeRifNotifica;
	}

	public String getEmailRifNotifica() {
		return emailRifNotifica;
	}

	public void setEmailRifNotifica(String emailRifNotifica) {
		this.emailRifNotifica = emailRifNotifica;
	}

	public String getNomeUtente() {
		return nomeUtente;
	}

	public void setNomeUtente(String nomeUtente) {
		this.nomeUtente = nomeUtente;
	}

	public String getCognomeUtente() {
		return cognomeUtente;
	}

	public void setCognomeUtente(String cognomeUtente) {
		this.cognomeUtente = cognomeUtente;
	}

	public String getEmailUtente() {
		return emailUtente;
	}

	public void setEmailUtente(String emailUtente) {
		this.emailUtente = emailUtente;
	}

	public String getCodStatoAdesione() {
		return codStatoAdesione;
	}

	public void setCodStatoAdesione(String codStatoAdesione) {
		this.codStatoAdesione = codStatoAdesione;
	}

	public String getDescrizioneStatoAdesione() {
		return descrizioneStatoAdesione;
	}

	public void setDescrizioneStatoAdesione(String descrizioneStatoAdesione) {
		this.descrizioneStatoAdesione = descrizioneStatoAdesione;
	}

	public Date getDtFineStatoAdesione() {
		return dtFineStatoAdesione;
	}

	public void setDtFineStatoAdesione(Date dtFineStatoAdesione) {
		this.dtFineStatoAdesione = dtFineStatoAdesione;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	public Date getModificaDtFineStatoAdesione() {
		return modificaDtFineStatoAdesione;
	}

	public void setModificaDtFineStatoAdesione(Date modificaDtFineStatoAdesione) {
		this.modificaDtFineStatoAdesione = modificaDtFineStatoAdesione;
	}

	public String getModificaCodStatoAdesione() {
		return modificaCodStatoAdesione;
	}

	public void setModificaCodStatoAdesione(String modificaCodStatoAdesione) {
		this.modificaCodStatoAdesione = modificaCodStatoAdesione;
	}

	public String getModificaNote() {
		return modificaNote;
	}

	public void setModificaNote(String modificaNote) {
		this.modificaNote = modificaNote;
	}

	public boolean isNoteStatoAdesioneVisible() {
		return noteStatoAdesioneVisible;
	}

	public void setNoteStatoAdesioneVisible(boolean noteStatoAdesioneVisible) {
		this.noteStatoAdesioneVisible = noteStatoAdesioneVisible;
	}

	public DeProvincia getDeProvinciaRifNotifica() {
		return deProvinciaRifNotifica;
	}

	public void setDeProvinciaRifNotifica(DeProvincia deProvinciaRifNotifica) {
		this.deProvinciaRifNotifica = deProvinciaRifNotifica;
	}

	public String getCodProvinciaRif() {
		return codProvinciaRif;
	}

	public void setCodProvinciaRif(String codProvinciaRif) {
		this.codProvinciaRif = codProvinciaRif;
	}

	public String getCodRegioneAdesione() {
		return codRegioneAdesione;
	}

	public void setCodRegioneAdesione(String codRegioneAdesione) {
		this.codRegioneAdesione = codRegioneAdesione;
	}

	public Date getDtStatoAdesioneMin() {
		return dtStatoAdesioneMin;
	}

	public void setDtStatoAdesioneMin(Date dtStatoAdesioneMin) {
		this.dtStatoAdesioneMin = dtStatoAdesioneMin;
	}

	public String getCodStatoAdesioneMin() {
		return codStatoAdesioneMin;
	}

	public String getCodMonoAttivaMin() {
		return codMonoAttivaMin;
	}

	public void setCodMonoAttivaMin(String codMonoAttivaMin) {
		this.codMonoAttivaMin = codMonoAttivaMin;
	}

	public String getDescStatoAdesioneMin() {
		return descStatoAdesioneMin;
	}

	public void setDescStatoAdesioneMin(String descStatoAdesioneMin) {
		this.descStatoAdesioneMin = descStatoAdesioneMin;
	}

	public void setCodStatoAdesioneMin(String codStatoAdesioneMin) {
		this.codStatoAdesioneMin = codStatoAdesioneMin;
	}

	public boolean getCanReactivate() {
		return canReactivate;
	}

	public void setCanReactivate(boolean canReactivate) {
		this.canReactivate = canReactivate;
	}
}
