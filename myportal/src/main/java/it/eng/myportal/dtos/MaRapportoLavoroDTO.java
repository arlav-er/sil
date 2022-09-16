package it.eng.myportal.dtos;


/**
 * DTO contenente un allegato. Questi oggetti possono essere collegati ad un
 * Curriculum.
 * 
 * @author Girotti S.
 * 
 */
public class MaRapportoLavoroDTO extends AbstractDecodeInfoDTO {

	private static final long serialVersionUID = 5502519125703657262L;

	private String codcontratto;
	private DeRapportoLavoroDTO deRapportoLavoroDTO;
	public String getCodcontratto() {
		return codcontratto;
	}
	public void setCodcontratto(String codcontratto) {
		this.codcontratto = codcontratto;
	}
	public DeRapportoLavoroDTO getDeRapportoLavoroDTO() {
		return deRapportoLavoroDTO;
	}
	public void setDeRapportoLavoroDTO(DeRapportoLavoroDTO deRapportoLavoroDTO) {
		this.deRapportoLavoroDTO = deRapportoLavoroDTO;
	}
	
	
	
	
}
