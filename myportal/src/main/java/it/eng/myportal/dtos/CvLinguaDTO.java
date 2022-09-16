package it.eng.myportal.dtos;

/**
 * Data transfer object del CV, sezione Lingua
 * 
 * @author Rodi A
 *
 * @see AbstractUpdatablePkDTO
 * @see ICurriculumSection
 * @see IHasUniqueValue
 */

public class CvLinguaDTO extends AbstractLinguaDTO implements ICurriculumSection {
	private static final long serialVersionUID = -1811585714201774349L;
	private Integer idCvDatiPersonali;
	private Boolean flgCertificata;
	private String altraModalita;
	private DeModalitaLinguaDTO deModalitaLinguaDTO;

	public CvLinguaDTO() {
		madrelingua = false;
		deModalitaLinguaDTO = new DeModalitaLinguaDTO();
	}

	public Integer getIdCvDatiPersonali() {
		return idCvDatiPersonali;
	}

	public void setIdCvDatiPersonali(Integer idCvDatiPersonali) {
		this.idCvDatiPersonali = idCvDatiPersonali;
	}

	public Boolean getFlgCertificata() {
		return flgCertificata;
	}

	public void setFlgCertificata(Boolean flgCertificata) {
		this.flgCertificata = flgCertificata;
	}

	public String getAltraModalita() {
		return altraModalita;
	}

	public void setAltraModalita(String altraModalita) {
		this.altraModalita = altraModalita;
	}

	public DeModalitaLinguaDTO getDeModalitaLinguaDTO() {
		return deModalitaLinguaDTO;
	}

	public void setDeModalitaLinguaDTO(DeModalitaLinguaDTO deModalitaLinguaDTO) {
		this.deModalitaLinguaDTO = deModalitaLinguaDTO;
	}

}
