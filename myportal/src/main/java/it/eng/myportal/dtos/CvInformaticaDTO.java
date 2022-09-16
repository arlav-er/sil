package it.eng.myportal.dtos;


/**
  * Data transfer object del CV,
  * 
  * @author Rodi A.
  * 
  * @see AbstractUpdatablePkDTO
  * @see ICurriculumSection
  * 
  */
public class CvInformaticaDTO extends AbstractUpdatablePkDTO implements ICurriculumSection {

	private String descrizione;
	public Integer getIdCvDatiPersonali() {
		return id;
	}
	public void setIdCvDatiPersonali(Integer idCvDatiPersonali) {
		this.id = idCvDatiPersonali;
	}
	public String getDescrizione() {
		return descrizione;
	}
	public void setDescrizione(String descrizione) {
		this.descrizione = descrizione;
	}
	@Override
	public String toString() {
		return String.format(
				"CvInformaticaDTO [descrizione=%s, id=%s, dtmIns=%s, dtmMod=%s, idPrincipalIns=%s, idPrincipalMod=%s]",
				descrizione, id, dtmIns, dtmMod, idPrincipalIns, idPrincipalMod);
	}
	
}