package it.eng.myportal.dtos;



/**
 * Data transfer object della tabella di decodifica Tipo Contratto
 * 
 * @author iescone
 * 
 * @see GenericDecodeDTO
 * @see ISuggestible
 */

public class DeTipoQuesitoDTO extends GenericDecodeDTO implements ISuggestible {

	private String descrizione;


	public DeTipoQuesitoDTO() {
	}

	public void setDescrizione(String descrizione) {
		this.descrizione = descrizione;
	}

	@Override
	public String getDescrizione() {
		return this.descrizione;
	}
}
