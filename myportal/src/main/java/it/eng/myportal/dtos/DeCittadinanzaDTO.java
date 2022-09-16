package it.eng.myportal.dtos;


/**
 * Data transfer object della tabella di decodifica Cittadinanza
 * 
 * @author Turrini
 * 
 * @see GenericDecodeDTO
 * @see ISuggestible
 */

public class DeCittadinanzaDTO extends GenericDecodeDTO implements ISuggestible {
	private static final long serialVersionUID = -7436513411537599591L;

	private String flgCee;

	public String getFlgCee() {
		return flgCee;
	}

	public void setFlgCee(String flgCee) {
		this.flgCee = flgCee;
	}

	/** Ritorna TRUE se la cittadinanza è di un paese dell'Unione Europea */
	public Boolean isCittadinoUE() {
		return (flgCee != null && flgCee.equalsIgnoreCase("S"));
	}

}
