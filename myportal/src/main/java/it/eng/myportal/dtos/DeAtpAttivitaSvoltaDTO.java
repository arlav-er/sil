package it.eng.myportal.dtos;



/**
 * Data transfer object della tabella di decodifica Tipo Contratto
 * 
 * @author iescone
 * 
 * @see GenericDecodeDTO
 * @see ISuggestible
 */

public class DeAtpAttivitaSvoltaDTO extends GenericDecodeDTO implements ISuggestible {

	private String descrizione;


	public DeAtpAttivitaSvoltaDTO() {
	}

	public void setDescrizione(String descrizione) {
		this.descrizione = descrizione;
	}

	@Override
	public String getDescrizione() {
		return this.descrizione;
	}
}
