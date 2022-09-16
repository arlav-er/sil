package it.eng.myportal.dtos;



/**
 * Data transfer object della tabella di decodifica Tipo Contratto
 * 
 * @author iescone
 * 
 * @see GenericDecodeDTO
 * @see ISuggestible
 */

public class DeTipoPraticaDTO extends GenericDecodeDTO implements ISuggestible {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8689334564964054697L;
	private String descrizione;


	public DeTipoPraticaDTO() {
		
	}

	public void setDescrizione(String descrizione) {
		this.descrizione = descrizione;
	}

	@Override
	public String getDescrizione() {
		return this.descrizione;
	}
}
