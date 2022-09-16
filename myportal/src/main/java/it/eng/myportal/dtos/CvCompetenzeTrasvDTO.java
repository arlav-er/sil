package it.eng.myportal.dtos;


/**
 * Data transfer object del CV,
 * sezione Competenze Trasversali
 *
 * @author Rodi A.
 *
 * @see AbstractUpdatablePkDTO
 * @see ICurriculumSection
 */
public class CvCompetenzeTrasvDTO extends AbstractUpdatablePkDTO implements ICurriculumSection {
	
	private Integer idCvDatiPersonali;
	private String capCompRelInterpersonali;
	private String capCompTecniche;
	private String capCompOrganizzative;

	public Integer getIdCvDatiPersonali() {
		return idCvDatiPersonali;
	}
	public void setIdCvDatiPersonali(Integer idCvDatiPersonali) {
		this.idCvDatiPersonali = idCvDatiPersonali;
	}
	public String getCapCompRelInterpersonali() {
		return capCompRelInterpersonali;
	}
	public void setCapCompRelInterpersonali(String capCompRelInterpersonali) {
		this.capCompRelInterpersonali = capCompRelInterpersonali;
	}
	public String getCapCompTecniche() {
		return capCompTecniche;
	}
	public void setCapCompTecniche(String capCompTecniche) {
		this.capCompTecniche = capCompTecniche;
	}
	public String getCapCompOrganizzative() {
		return capCompOrganizzative;
	}
	public void setCapCompOrganizzative(String capCompOrganizzative) {
		this.capCompOrganizzative = capCompOrganizzative;
	}
}