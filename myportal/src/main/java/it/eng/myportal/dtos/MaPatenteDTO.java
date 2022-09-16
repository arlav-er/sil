package it.eng.myportal.dtos;


/**
 * DTO contenente un allegato. Questi oggetti possono essere collegati ad un
 * Curriculum.
 * 
 * @author Girotti S.
 * 
 */
public class MaPatenteDTO extends AbstractDecodeInfoDTO {

	private static final long serialVersionUID = 5502519125703657262L;

	private String codPatente;
	private String codPatenteMin;
	private DePatenteDTO patente;
	
	public String getCodPatente() {
		return codPatente;
	}
	public void setCodPatente(String codPatente) {
		this.codPatente = codPatente;
	}
	public String getCodPatenteMin() {
		return codPatenteMin;
	}
	public void setCodPatenteMin(String codPatenteMin) {
		this.codPatenteMin = codPatenteMin;
	}
	public DePatenteDTO getPatente() {
		return patente;
	}
	public void setPatente(DePatenteDTO patente) {
		this.patente = patente;
	}
	
	
}
