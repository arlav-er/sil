package it.eng.myportal.dtos;



/**
 * Data transfer object della tabella di decodifica Transferta
 * 
 * @author iescone
 * 
 * @see GenericDecodeDTO
 * @see ISuggestible
 */

public class DeTrasfertaDTO extends GenericDecodeDTO implements ISuggestible {

	private String descrizione;

	@Override
	public String getDescrizione() {	
		return descrizione;
	}

	public void setDescrizione(String descrizione) {
		this.descrizione = descrizione;
	}
}
