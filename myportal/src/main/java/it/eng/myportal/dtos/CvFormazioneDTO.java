package it.eng.myportal.dtos;

/**
 * Data transfer object del CV, sezione FORMAZIONE
 *
 * @author pegoraro
 *
 */
public class CvFormazioneDTO extends AbstractUpdatablePkDTO implements ICurriculumSection, IHasUniqueValue {
	private static final long serialVersionUID = 1319417320115150630L;

	private Integer idCvDatiPersonali;
	private DeCorsoDTO corso;
	private String titoloCorso;
	private String principaliTematiche;
	private String nomeIstituto;
	private String livelloClassificazione;
	private Integer anno;
	private String durata;
	private String descrizione;
	private DeComuneDTO deComuneEnte;
	private Boolean flgCompletato;
	private DeTipoCorsoSilDTO deTipoCorso;
	private DeAmbitoDisciplinareDTO deAmbitoDisciplinare;

	public CvFormazioneDTO() {
		super();
		corso = new DeCorsoDTO();
		deComuneEnte = new DeComuneDTO();
		deTipoCorso = new DeTipoCorsoSilDTO();
		deAmbitoDisciplinare = new DeAmbitoDisciplinareDTO();
	}

	public Integer getIdCvDatiPersonali() {
		return idCvDatiPersonali;
	}

	public void setIdCvDatiPersonali(Integer idCvDatiPersonali) {
		this.idCvDatiPersonali = idCvDatiPersonali;
	}

	public String getDurata() {
		return durata;
	}

	public void setDurata(String durata) {
		this.durata = durata;
	}

	public String getPrincipaliTematiche() {
		return principaliTematiche;
	}

	public void setPrincipaliTematiche(String principaliTematiche) {
		this.principaliTematiche = principaliTematiche;
	}

	public String getNomeIstituto() {
		return nomeIstituto;
	}

	public void setNomeIstituto(String nomeIstituto) {
		this.nomeIstituto = nomeIstituto;
	}

	public String getLivelloClassificazione() {
		return livelloClassificazione;
	}

	public void setLivelloClassificazione(String livelloClassificazione) {
		this.livelloClassificazione = livelloClassificazione;
	}

	@Override
	public String getUniqueValue() {
		return corso.getId();
	}

	/**
	 * @return l'anno
	 */
	public Integer getAnno() {
		return anno;
	}

	public void setAnno(Integer anno) {
		this.anno = anno;
	}

	public DeCorsoDTO getCorso() {
		return corso;
	}

	public void setCorso(DeCorsoDTO corso) {
		this.corso = corso;
	}

	public String getTitoloCorso() {
		return titoloCorso;
	}

	public void setTitoloCorso(String titoloCorso) {
		this.titoloCorso = titoloCorso;
	}

	@Override
	public String toString() {
		return String
				.format("CvFormazioneDTO [idCvDatiPersonali=%s, codCorso=%s, strCorso=%s, numAnno=%s, durata=%s, principaliTematiche=%s, nomeIstituto=%s, livelloClassificazione=%s]",
						idCvDatiPersonali, corso.getId(), corso.getDescrizione(), anno, durata, principaliTematiche,
						nomeIstituto, livelloClassificazione);
	}

	public String getDescrizione() {
		return descrizione;
	}

	public void setDescrizione(String descrizione) {
		this.descrizione = descrizione;
	}

	public DeComuneDTO getDeComuneEnte() {
		return deComuneEnte;
	}

	public void setDeComuneEnte(DeComuneDTO deComuneEnte) {
		this.deComuneEnte = deComuneEnte;
	}

	public Boolean getFlgCompletato() {
		return flgCompletato;
	}

	public void setFlgCompletato(Boolean flgCompletato) {
		this.flgCompletato = flgCompletato;
	}

	public DeTipoCorsoSilDTO getDeTipoCorso() {
		return deTipoCorso;
	}

	public void setDeTipoCorso(DeTipoCorsoSilDTO deTipoCorso) {
		this.deTipoCorso = deTipoCorso;
	}

	public DeAmbitoDisciplinareDTO getDeAmbitoDisciplinare() {
		return deAmbitoDisciplinare;
	}

	public void setDeAmbitoDisciplinare(DeAmbitoDisciplinareDTO deAmbitoDisciplinare) {
		this.deAmbitoDisciplinare = deAmbitoDisciplinare;
	}

}
