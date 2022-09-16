package it.eng.myportal.dtos;


/**
 * Data transfer object del CV,
 * sezione Patente
 * 
 * @author Enrico D'Angelo
 *
 * @see AbstractUpdatablePkDTO
 * @see ICurriculumSection
 * @see IHasUniqueValue
 */

public class CvPatenteDTO extends AbstractUpdatablePkDTO implements ICurriculumSection, IHasUniqueValue {
	
	private Integer idCvDatiPersonali;
	private String codPatente;
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
		return codPatente;
	}

	public String getCodPatente() {
		return codPatente;
	}

	public void setCodPatente(String codPatente) {
		this.codPatente = codPatente;
	}

	public String getDescrizione() {
		return descrizione;
	}

	public void setDescrizione(String descrizione) {
		this.descrizione = descrizione;
	}
}
