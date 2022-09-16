package it.eng.myportal.dtos;


/**
 * Data transfer object della tabella Regione
 * 
 * @author Rodi A.
 * 
 * @see GenericDecodeDTO 
 * @see ISuggestible
 */
public class DeTipoFormazioneDTO extends GenericDecodeDTO implements ITreeable {

	private String denominazione;

	public String getDenominazione() {
		return denominazione;
	}

	public void setDenominazione(String denominazione) {
		this.denominazione = denominazione;
	}



}
