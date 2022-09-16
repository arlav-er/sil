package it.eng.myportal.dtos;



/**
 * Data transfer object della tabella di decodifica Patente
 * 
 * @author iescone
 * 
 * @see GenericDecodeDTO
 * @see ISuggestible
 */

public class DePatenteDTO extends GenericDecodeDTO implements ISuggestible {

	private String descrizione;

	public void setDescrizione(String descrizione) {
		this.descrizione = descrizione;
	}
	@Override
	public String getDescrizione() {
		return descrizione;
	}
	
}
