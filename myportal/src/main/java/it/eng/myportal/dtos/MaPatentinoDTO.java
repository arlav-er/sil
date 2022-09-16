package it.eng.myportal.dtos;


/**
 * DTO contenente un allegato. Questi oggetti possono essere collegati ad un
 * Curriculum.
 * 
 * @author Girotti S.
 * 
 */
public class MaPatentinoDTO extends AbstractDecodeInfoDTO {

	private static final long serialVersionUID = 5502519125703657262L;

	private String codPatentino;
	private String codPatentinoMin;
	private DePatentinoDTO patentino;
	
	public String getCodPatentino() {
		return codPatentino;
	}
	public void setCodPatentino(String codPatentino) {
		this.codPatentino = codPatentino;
	}
	public String getCodPatentinoMin() {
		return codPatentinoMin;
	}
	public void setCodPatentinoMin(String codPatentinoMin) {
		this.codPatentinoMin = codPatentinoMin;
	}
	public DePatentinoDTO getPatentino() {
		return patentino;
	}
	public void setPatentino(DePatentinoDTO patentino) {
		this.patentino = patentino;
	}
	
	
}
