package it.eng.myportal.dtos;

/**
 * Data transfer object della tabella di decodifca Contratto
 * 
 * @author iescone
 * 
 * @see GenericDecodeDTO
 * @see ISuggestible
 */

public class DeContrattoDTO extends GenericDecodeDTO implements ISuggestible {
	private static final long serialVersionUID = 1333493737940791556L;

	private boolean flagAtipico;
	private Boolean flagTirocini;

	public DeContrattoDTO() {
	}

	public DeContrattoDTO(String id, String descrizione) {
		this.id = id;
		this.descrizione = descrizione;
	}

	public boolean isFlagAtipico() {
		return flagAtipico;
	}

	public void setFlagAtipico(boolean flagAtipico) {
		this.flagAtipico = flagAtipico;
	}

	public Boolean getFlagTirocini() {
		return flagTirocini;
	}

	public void setFlagTirocini(Boolean flagTirocini) {
		this.flagTirocini = flagTirocini;
	}

}
