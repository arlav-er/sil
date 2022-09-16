package it.eng.myportal.dtos;


/**
 * 
 * DTO associato alla sezione COMPETENZE TRASVERSALI della vacancy.
 * 1-1
 * @author iescone, Rodi
 *
 */
public class VaCompetenzeTrasvDTO extends AbstractUpdatablePkDTO implements IVacancySection {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1357024845830728002L;
	private String capCompRelInterpersonali;
	private String capCompTecniche;
	private String capCompOrganizzative;
	private String capCompSintesiCl;
	
	public Integer getIdVaDatiVacancy() {
		return id;
	}
	public void setIdVaDatiVacancy(Integer idVaDatiVacancy) {
		this.id = idVaDatiVacancy;
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
	public String getCapCompSintesiCl() {
		return capCompSintesiCl;
	}
	public void setCapCompSintesiCl(String capCompSintesiCl) {
		this.capCompSintesiCl = capCompSintesiCl;
	}
	
}
