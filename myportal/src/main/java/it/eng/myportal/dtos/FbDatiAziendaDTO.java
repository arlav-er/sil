package it.eng.myportal.dtos;

public class FbDatiAziendaDTO extends AbstractUpdatablePkDTO {
	private static final long serialVersionUID = -1848179406577611829L;

	private Integer idFbSchedaFabbisogno;
	private String numCciaa;
	private String matricolaInps;
	private DeAttivitaDTO deAttivita;
	private DeAttivitaMinDTO deAttivitaMin;
	private String cellRiferimento;
	private String faxRiferimento;
	private String telRiferimento;
	private String emailRiferimento;
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
	private DeNaturaGiuridicaDTO deNaturaGiuridicaDTO;
	private String noteAttivita;
	// private Integer tipoSoggetto;

	public Integer getIdFbSchedaFabbisogno() {
		return idFbSchedaFabbisogno;
	}

	public void setIdFbSchedaFabbisogno(Integer idFbSchedaFabbisogno) {
		this.idFbSchedaFabbisogno = idFbSchedaFabbisogno;
	}

	public String getNumCciaa() {
		return numCciaa;
	}

	public void setNumCciaa(String numCciaa) {
		this.numCciaa = numCciaa;
	}

	public String getMatricolaInps() {
		return matricolaInps;
	}

	public void setMatricolaInps(String matricolaInps) {
		this.matricolaInps = matricolaInps;
	}

	public DeAttivitaDTO getDeAttivita() {
		return deAttivita;
	}

	public void setDeAttivita(DeAttivitaDTO deAttivita) {
		this.deAttivita = deAttivita;
	}

	public DeAttivitaMinDTO getDeAttivitaMin() {
		return deAttivitaMin;
	}

	public void setDeAttivitaMin(DeAttivitaMinDTO deAttivitaMin) {
		this.deAttivitaMin = deAttivitaMin;
	}

	public String getCellRiferimento() {
		return cellRiferimento;
	}

	public void setCellRiferimento(String cellRiferimento) {
		this.cellRiferimento = cellRiferimento;
	}

	public String getPec() {
		return pec;
	}

	public void setPec(String pec) {
		this.pec = pec;
	}

	public String getSitoWeb() {
		return sitoWeb;
	}

	public void setSitoWeb(String sitoWeb) {
		this.sitoWeb = sitoWeb;
	}

	public Integer getNumDipendentiTot() {
		return numDipendentiTot;
	}

	public void setNumDipendentiTot(Integer numDipendentiTot) {
		this.numDipendentiTot = numDipendentiTot;
	}

	public Integer getNumDipendentiIndet() {
		return numDipendentiIndet;
	}

	public void setNumDipendentiIndet(Integer numDipendentiIndet) {
		this.numDipendentiIndet = numDipendentiIndet;
	}

	public Integer getNumDipendentiDet() {
		return numDipendentiDet;
	}

	public void setNumDipendentiDet(Integer numDipendentiDet) {
		this.numDipendentiDet = numDipendentiDet;
	}

	public Integer getNumDipendentiParttime() {
		return numDipendentiParttime;
	}

	public void setNumDipendentiParttime(Integer numDipendentiParttime) {
		this.numDipendentiParttime = numDipendentiParttime;
	}

	public Boolean getFlgTirocinio() {
		return flgTirocinio;
	}

	public void setFlgTirocinio(Boolean flgTirocinio) {
		this.flgTirocinio = flgTirocinio;
	}

	public String getEsitiTirocini() {
		return esitiTirocini;
	}

	public void setEsitiTirocini(String esitiTirocini) {
		this.esitiTirocini = esitiTirocini;
	}

	public String getNomeLegale() {
		return nomeLegale;
	}

	public void setNomeLegale(String nomeLegale) {
		this.nomeLegale = nomeLegale;
	}

	public String getCognomeLegale() {
		return cognomeLegale;
	}

	public void setCognomeLegale(String cognomeLegale) {
		this.cognomeLegale = cognomeLegale;
	}

	public DeNaturaGiuridicaDTO getDeNaturaGiuridicaDTO() {
		return deNaturaGiuridicaDTO;
	}

	public void setDeNaturaGiuridicaDTO(DeNaturaGiuridicaDTO deNaturaGiuridicaDTO) {
		this.deNaturaGiuridicaDTO = deNaturaGiuridicaDTO;
	}

	public String getNoteAttivita() {
		return noteAttivita;
	}

	public void setNoteAttivita(String noteAttivita) {
		this.noteAttivita = noteAttivita;
	}

	public String getFaxRiferimento() {
		return faxRiferimento;
	}

	public void setFaxRiferimento(String faxRiferimento) {
		this.faxRiferimento = faxRiferimento;
	}

	public String getTelRiferimento() {
		return telRiferimento;
	}

	public void setTelRiferimento(String telRiferimento) {
		this.telRiferimento = telRiferimento;
	}

	public String getEmailRiferimento() {
		return emailRiferimento;
	}

	public void setEmailRiferimento(String emailRiferimento) {
		this.emailRiferimento = emailRiferimento;
	}

}
