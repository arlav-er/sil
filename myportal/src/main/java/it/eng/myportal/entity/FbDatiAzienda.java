package it.eng.myportal.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.hibernate.annotations.Type;

import it.eng.myportal.entity.decodifiche.DeAttivita;
import it.eng.myportal.entity.decodifiche.DeNaturaGiuridica;
import it.eng.myportal.entity.decodifiche.min.DeAttivitaMin;

@Entity
@Table(name = "fb_dati_azienda", schema = "myportal")
@NamedQueries({
		@NamedQuery(name = "findFbDatiAziendaByIdPrincipalOrderByDate", query = "SELECT da FROM FbDatiAzienda da JOIN da.fbSchedaFabbisogno sf JOIN sf.fbChecklist chk WHERE chk.pfPrincipal.idPfPrincipal = :idPfPrincipal ORDER BY da.dtmIns DESC"),
		@NamedQuery(name = "findByFbSchedaFabbisognoId", query = "SELECT da FROM FbDatiAzienda da WHERE da.fbSchedaFabbisogno.idFbSchedaFabbisogno = :parIdScheda") })
public class FbDatiAzienda extends AbstractEntity {
	private static final long serialVersionUID = -195451946446996589L;

	private Integer idFbDatiAzienda;
	private FbSchedaFabbisogno fbSchedaFabbisogno;
	private String numCciaa;
	private String matricolaInps;
	private DeAttivita deAttivita;
	private DeAttivitaMin deAttivitaMin;
	private String cellRiferimento;
	private String pec;
	private String sitoWeb;
	private Integer numDipendentiTot;
	private Integer numDipendentiIndet;
	private Integer numDipendentiDet;
	private Integer numDipendentiParttime;
	private Boolean flgTirocinio;
	private String esitiTirocini;
	private String nomeLegale;
	private String cognomeLegale;
	private DeNaturaGiuridica deNaturaGiuridica;
	private String noteAttivita;
	private String telRiferimento;
	private String faxRiferimento;
	private String emailRiferimento;
	// private String noteAttivita;

	@Id
	@SequenceGenerator(name = "fb_dati_azienda_id_fb_dati_azienda_seq", sequenceName = "fb_dati_azienda_id_fb_dati_azienda_seq", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "fb_dati_azienda_id_fb_dati_azienda_seq")
	@Column(name = "id_fb_dati_azienda", unique = true, nullable = false)
	public Integer getIdFbDatiAzienda() {
		return idFbDatiAzienda;
	}

	public void setIdFbDatiAzienda(Integer idFbDatiAzienda) {
		this.idFbDatiAzienda = idFbDatiAzienda;
	}

	@OneToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_fb_scheda_fabbisogno", nullable = false)
	public FbSchedaFabbisogno getFbSchedaFabbisogno() {
		return fbSchedaFabbisogno;
	}

	public void setFbSchedaFabbisogno(FbSchedaFabbisogno fbSchedaFabbisogno) {
		this.fbSchedaFabbisogno = fbSchedaFabbisogno;
	}

	@Column(name = "num_cciaa", length = 100)
	public String getNumCciaa() {
		return numCciaa;
	}

	public void setNumCciaa(String numCciaa) {
		this.numCciaa = numCciaa;
	}

	@Column(name = "matricola_inps", length = 100)
	public String getMatricolaInps() {
		return matricolaInps;
	}

	public void setMatricolaInps(String matricolaInps) {
		this.matricolaInps = matricolaInps;
	}

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "cod_attivita")
	public DeAttivita getDeAttivita() {
		return deAttivita;
	}

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "cod_natura")
	public DeNaturaGiuridica getDeNaturaGiuridica() {
		return deNaturaGiuridica;
	}

	public void setDeAttivita(DeAttivita deAttivita) {
		this.deAttivita = deAttivita;
	}

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "cod_attivita_min")
	public DeAttivitaMin getDeAttivitaMin() {
		return deAttivitaMin;
	}

	public void setDeAttivitaMin(DeAttivitaMin deAttivitaMin) {
		this.deAttivitaMin = deAttivitaMin;
	}

	@Column(name = "cell_riferimento", length = 20)
	public String getCellRiferimento() {
		return cellRiferimento;
	}

	public void setCellRiferimento(String cellRiferimento) {
		this.cellRiferimento = cellRiferimento;
	}

	@Column(name = "pec", length = 255)
	public String getPec() {
		return pec;
	}

	public void setPec(String pec) {
		this.pec = pec;
	}

	@Column(name = "sito_web", length = 255)
	public String getSitoWeb() {
		return sitoWeb;
	}

	public void setSitoWeb(String sitoWeb) {
		this.sitoWeb = sitoWeb;
	}

	@Column(name = "num_dipendenti_tot", nullable = false)
	public Integer getNumDipendentiTot() {
		return numDipendentiTot;
	}

	public void setNumDipendentiTot(Integer numDipendentiTot) {
		this.numDipendentiTot = numDipendentiTot;
	}

	@Column(name = "num_dipendenti_indet")
	public Integer getNumDipendentiIndet() {
		return numDipendentiIndet;
	}

	public void setNumDipendentiIndet(Integer numDipendentiIndet) {
		this.numDipendentiIndet = numDipendentiIndet;
	}

	@Column(name = "num_dipendenti_det")
	public Integer getNumDipendentiDet() {
		return numDipendentiDet;
	}

	public void setNumDipendentiDet(Integer numDipendentiDet) {
		this.numDipendentiDet = numDipendentiDet;
	}

	@Column(name = "num_dipendenti_parttime")
	public Integer getNumDipendentiParttime() {
		return numDipendentiParttime;
	}

	public void setNumDipendentiParttime(Integer numDipendentiParttime) {
		this.numDipendentiParttime = numDipendentiParttime;
	}

	@Type(type = "yes_no")
	@Column(name = "flg_tirocinio", length = 1)
	public Boolean getFlgTirocinio() {
		return flgTirocinio;
	}

	public void setFlgTirocinio(Boolean flgTirocinio) {
		this.flgTirocinio = flgTirocinio;
	}

	@Column(name = "esiti_tirocini", length = 2000)
	public String getEsitiTirocini() {
		return esitiTirocini;
	}

	@Column(name = "nome_legale_rappr", length = 50)
	public String getNomeLegale() {
		return nomeLegale;
	}

	@Column(name = "cognome_legale_rappr", length = 50)
	public String getCognomeLegale() {
		return cognomeLegale;
	}

	@Column(name = "note_attivita", length = 2000)
	public String getNoteAttivita() {
		return noteAttivita;
	}

	@Column(name = "tel_riferimento", length = 50)
	public String getTelRiferimento() {
		return telRiferimento;
	}

	public void setTelRiferimento(String telRiferimento) {
		this.telRiferimento = telRiferimento;
	}

	@Column(name = "fax_riferimento", length = 50)
	public String getFaxRiferimento() {
		return faxRiferimento;
	}

	public void setFaxRiferimento(String faxRiferimento) {
		this.faxRiferimento = faxRiferimento;
	}

	public void setEsitiTirocini(String esitiTirocini) {
		this.esitiTirocini = esitiTirocini;
	}

	public void setNomeLegale(String nomeLegale) {
		this.nomeLegale = nomeLegale;
	}

	public void setCognomeLegale(String cognomeLegale) {
		this.cognomeLegale = cognomeLegale;
	}

	public void setDeNaturaGiuridica(DeNaturaGiuridica deNaturaGiuridica) {
		this.deNaturaGiuridica = deNaturaGiuridica;
	}

	public void setNoteAttivita(String noteAttivita) {
		this.noteAttivita = noteAttivita;
	}

	@Column(name = "email_riferimento", length = 200)
	public String getEmailRiferimento() {
		return emailRiferimento;
	}

	public void setEmailRiferimento(String emailRiferimento) {
		this.emailRiferimento = emailRiferimento;
	}

}
