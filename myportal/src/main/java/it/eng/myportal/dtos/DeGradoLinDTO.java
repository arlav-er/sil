package it.eng.myportal.dtos;



/**
 * Data transfer object della tabella di decodifica Grado Lingua
 * 
 * @author Rodi A.
 *
 * @see GenericDecodeDTO
 * @see ISuggestible
 */
public class DeGradoLinDTO extends GenericDecodeDTO implements ISuggestible {

	/**
     * 
     */
    private static final long serialVersionUID = -2035198401998692385L;
	private String descrizione;
	
	public void setDescrizione(String descrizione) {
		this.descrizione = descrizione;
	}
	@Override
	public String getDescrizione() {
		return descrizione;
	}
}
