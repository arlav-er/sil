package it.eng.myportal.dtos;


/**
 * Data transfer object della tabella di decodifica Mansione
 * 
 * @author iescone
 *
 * @see GenericDecodeDTO
 * @see ITreeable
 */

public class DeAttivitaSilDTO extends GenericDecodeDTO {

	private String codAteco;
	
	public DeAttivitaSilDTO() {
	}

	/**
	 * @return the codAteco
	 */
	public String getCodAteco() {
		return codAteco;
	}


	/**
	 * @param codAteco the codAteco to set
	 */
	public void setCodAteco(String codAteco) {
		this.codAteco = codAteco;
	}

	
}
