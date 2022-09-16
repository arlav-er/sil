package it.eng.myportal.dtos;


/**
 * DTO contenente un allegato. Questi oggetti possono essere collegati ad un
 * Curriculum.
 * 
 * @author Girotti S.
 * 
 */
public class MaGradoConscLinDTO extends AbstractDecodeInfoDTO {

	private static final long serialVersionUID = 5502519125703657262L;

	private String codGrado;
	private String codConoscenza;
	private DeGradoLinDTO gradoLin;
	public String getCodGrado() {
		return codGrado;
	}
	public void setCodGrado(String codGrado) {
		this.codGrado = codGrado;
	}
	public String getCodConoscenza() {
		return codConoscenza;
	}
	public void setCodConoscenza(String codConoscenza) {
		this.codConoscenza = codConoscenza;
	}
	public DeGradoLinDTO getGradoLin() {
		return gradoLin;
	}
	public void setGradoLin(DeGradoLinDTO gradoLin) {
		this.gradoLin = gradoLin;
	}
	
	
}
