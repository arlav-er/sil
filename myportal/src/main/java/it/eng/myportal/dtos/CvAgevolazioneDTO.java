package it.eng.myportal.dtos;


/**
 * Data transfer object del CV,
 * sezione Agevolazione
 * 
 * @author Enrico D'Angelo
 *
 * @see AbstractUpdatablePkDTO
 * @see ICurriculumSection
 * @see IHasUniqueValue
 */

public class CvAgevolazioneDTO extends AbstractUpdatablePkDTO implements ICurriculumSection, IHasUniqueValue {
	
	private Integer idCvDatiPersonali;
	private String codAgevolazione;
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
		return codAgevolazione;
	}

	public String getCodAgevolazione() {
		return codAgevolazione;
	}

	public void setCodAgevolazione(String codAgevolazione) {
		this.codAgevolazione = codAgevolazione;
	}

	public String getDescrizione() {
		return descrizione;
	}

	public void setDescrizione(String descrizione) {
		this.descrizione = descrizione;
	}
}
