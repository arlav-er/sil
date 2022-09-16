package it.eng.myportal.dtos;


/**
 * Data transfer object del CV,
 * sezione Patentino
 * 
 * @author Enrico D'Angelo
 *
 * @see AbstractUpdatablePkDTO
 * @see ICurriculumSection
 * @see IHasUniqueValue
 */

public class CvPatentinoDTO extends AbstractUpdatablePkDTO implements ICurriculumSection, IHasUniqueValue {

	private Integer idCvDatiPersonali;
	private String codPatentino;
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
		return codPatentino;
	}

	public String getCodPatentino() {
		return codPatentino;
	}

	public void setCodPatentino(String codPatentino) {
		this.codPatentino = codPatentino;
	}

	public String getDescrizione() {
		return descrizione;
	}

	public void setDescrizione(String descrizione) {
		this.descrizione = descrizione;
	}
}
