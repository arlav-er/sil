package it.eng.myportal.dtos;


/**
 * Data transfer object della tabella Lingua
 * 
 * @author Rodi A.
 * 
 * @see GenericDecodeDTO
 * @see ITreeable
 */

public class DeLinguaDTO extends GenericDecodeDTO implements ITreeable, Comparable<DeLinguaDTO> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1589877346507763660L;
	private String denominazione;

	public String getDenominazione() {
		return denominazione;
	}

	public void setDenominazione(final String denominazione) {
		this.denominazione = denominazione;
	}

	@Override
	public String getDescrizione() {
		return denominazione;
	}
	
	@Override
	public int compareTo(DeLinguaDTO other) {
		return this.getDenominazione().compareTo(other.getDenominazione());
	}

}
