package it.eng.myportal.dtos;


/**
 * Data transfer object della tabella di decodifica Mansione
 * 
 * @author iescone
 *
 * @see GenericDecodeDTO
 * @see ITreeable
 */

public class DeMansioneSilDTO extends GenericDecodeDTO {

	private String codMansione;	

	public DeMansioneSilDTO() {
	}

	/**
	 * @return the codMansione
	 */
	public String getCodMansione() {
		return codMansione;
	}

	/**
	 * @param codMansione the codMansione to set
	 */
	public void setCodMansione(String codMansione) {
		this.codMansione = codMansione;
	}

	
}
