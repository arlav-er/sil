package it.eng.myportal.dtos;


/**
 * Data transfer object della tabella di decodifica Corso
 * 
 * @author iescone
 * 
 * @see GenericDecodeDTO
 * @see ISuggestible
 */
public class DeCorsoDTO extends GenericDecodeDTO implements ISuggestible {

	private String descrizione;
	
	public DeCorsoDTO() {
	}

//	public DeCorsoDTO(String codCorso, String descrizione) {
//		id = codCorso;
//		this.descrizione = descrizione;
//	}

	public void setDescrizione(String descrizione) {
		this.descrizione = descrizione;
	}

	@Override
	public String getDescrizione() {		
		return this.descrizione;
	}

}
