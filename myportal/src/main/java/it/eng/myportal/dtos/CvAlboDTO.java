package it.eng.myportal.dtos;


/**
 * Data transfer object del CV,
 * sezione Albo
 * 
 * @author Enrico D'Angelo
 *
 * @see AbstractUpdatablePkDTO
 * @see ICurriculumSection
 * @see IHasUniqueValue
 */

public class CvAlboDTO extends AbstractUpdatablePkDTO implements ICurriculumSection, IHasUniqueValue {
	
	private Integer idCvDatiPersonali;
	private String codAlbo;
	private String descrizione;
	
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
		return codAlbo;
	}

	public String getCodAlbo() {
		return codAlbo;
	}

	public void setCodAlbo(String codAlbo) {
		this.codAlbo = codAlbo;
	}

	public String getDescrizione() {
		return descrizione;
	}

	public void setDescrizione(String descrizione) {
		this.descrizione = descrizione;
	}
}
