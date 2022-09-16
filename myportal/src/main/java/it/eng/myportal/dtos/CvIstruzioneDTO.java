package it.eng.myportal.dtos;

/**
 * DataTransferObject della sezione ISTRUZIONE del CurriculumVitae
 * 
 * @author Rodi A.
 *
 * @see AbstractUpdatablePkDTO
 * @see ICurriculumSection
 * @see IHasUniqueValue
 */
public class CvIstruzioneDTO extends AbstractUpdatablePkDTO implements ICurriculumSection, IHasUniqueValue {
	private static final long serialVersionUID = 5548402569962233295L;

	private Integer idCvDatiPersonali;
	private DeTitoloDTO titolo;
	private String comuneIstitutoCod;
	private String comuneIstitutoDenominazione;

	private String tematicheTrattate;
	private String nomeIstituto;
	private String votazione;
	private Integer anno;
	private String specifica;
	private DeStatoTitoloDTO deStatoTitolo;

	public CvIstruzioneDTO() {
		titolo = new DeTitoloDTO();
		deStatoTitolo = new DeStatoTitoloDTO();
	}

	public String getTematicheTrattate() {
		return tematicheTrattate;
	}

	public void setTematicheTrattate(String tematicheTrattate) {
		this.tematicheTrattate = tematicheTrattate;
	}

	public String getNomeIstituto() {
		return nomeIstituto;
	}

	public void setNomeIstituto(String nomeIstituto) {
		this.nomeIstituto = nomeIstituto;
	}

	public String getVotazione() {
		return votazione;
	}

	public void setVotazione(String votazione) {
		this.votazione = votazione;
	}

	@Override
	public Integer getIdCvDatiPersonali() {
		return idCvDatiPersonali;
	}

	@Override
	public void setIdCvDatiPersonali(Integer idCvDatiPersonali) {
		this.idCvDatiPersonali = idCvDatiPersonali;
	}

	@Override
	public String getUniqueValue() {
		return titolo.getId();
	}

	public DeTitoloDTO getTitolo() {
		return titolo;
	}

	public void setTitolo(DeTitoloDTO titolo) {
		this.titolo = titolo;
	}

	public String getComuneIstitutoCod() {
		return comuneIstitutoCod;
	}

	public void setComuneIstitutoCod(String comuneIstitutoCod) {
		this.comuneIstitutoCod = comuneIstitutoCod;
	}

	public String getComuneIstitutoDenominazione() {
		return comuneIstitutoDenominazione;
	}

	public void setComuneIstitutoDenominazione(String comuneIstitutoDenominazione) {
		this.comuneIstitutoDenominazione = comuneIstitutoDenominazione;
	}

	public Integer getAnno() {
		return anno;
	}

	public void setAnno(Integer anno) {
		this.anno = anno;
	}

	public String getSpecifica() {
		return specifica;
	}

	public void setSpecifica(String specifica) {
		this.specifica = specifica;
	}

	public DeStatoTitoloDTO getDeStatoTitolo() {
		return deStatoTitolo;
	}

	public void setDeStatoTitolo(DeStatoTitoloDTO deStatoTitolo) {
		this.deStatoTitolo = deStatoTitolo;
	}
}
