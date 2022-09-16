package it.eng.myportal.dtos;

/**
 * Data transfer object della tabella di decodifica Comune
 * 
 * @author iescone
 * 
 * @see GenericDecodeDTO
 * @see ISuggestible
 */

public class DeComuneDTO extends GenericDecodeDTO implements ISuggestible {
	private static final long serialVersionUID = -2542894351463425362L;

	private String codProvincia;
	private String cap;
	private boolean stato;

	public DeComuneDTO() {
	}

	public String getIdProvincia() {
		return this.codProvincia;
	}

	public void setIdProvincia(String idProvincia) {
		this.codProvincia = idProvincia;
	}

	public String getCap() {
		return this.cap;
	}

	public void setCap(String cap) {
		this.cap = cap;
	}

	public boolean isStato() {
		return stato;
	}

	public void setStato(boolean stato) {
		this.stato = stato;
	}
	
	//wrapper buono e giusto
	public String getDenominazione() {
		return descrizione;
	}

}