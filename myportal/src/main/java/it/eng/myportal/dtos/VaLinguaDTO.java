package it.eng.myportal.dtos;

/**
 * DTO per la sezione Lingue della vacancy. La sezione lingue è in relazione 1-n con la vacancy
 * 
 * @author Rodi A.
 * 
 */
public class VaLinguaDTO extends AbstractLinguaDTO implements IVacancySection {
	private static final long serialVersionUID = 7207344715906642774L;

	private Integer idVaDatiVacancy;
	private Boolean opzIndispensabile;

	public VaLinguaDTO() {
		madrelingua = false;
	}

	public Integer getIdVaDatiVacancy() {
		return idVaDatiVacancy;
	}

	public void setIdVaDatiVacancy(Integer idVaDatiVacancy) {
		this.idVaDatiVacancy = idVaDatiVacancy;
	}

	public Boolean getOpzIndispensabile() {
		return opzIndispensabile;
	}

	public void setOpzIndispensabile(Boolean opzIndispensabile) {
		this.opzIndispensabile = opzIndispensabile;
	}
}
