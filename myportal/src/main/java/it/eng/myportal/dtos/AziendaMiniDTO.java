package it.eng.myportal.dtos;

/**
 * Classe contenitore delle informazioni su un'Azienda in sessione.<br/>
 * Inserire in questo DTO <b>solo le informazioni che devono essere mantenute in
 * sessione</b>
 * 
 * @author iescone, Rodi A.
 * @see AbstractUpdatableDTO
 * 
 */

public class AziendaMiniDTO extends AbstractUpdatablePkDTO {
	
	private static final long serialVersionUID = -2698965085713786797L;

	private String denominazioneComune;
	private String ragioneSociale;
	private Boolean vetrina;

	public AziendaMiniDTO() {
		super();
	}
	
	/**
	 * Costruttore per una query che seleziona anche il numero di vetrine per ogni azienda.
	 * (Per ora questa query non esiste, ma tengo il costruttore perchè potrebbe servire in futuro).
	 */
	public AziendaMiniDTO(Integer idPfPrincipal, String ragioneSociale, 
			String denominazioneComune, Long numVetrine) {
		super();
		this.id = idPfPrincipal;
		this.ragioneSociale = ragioneSociale;
		this.denominazioneComune = denominazioneComune;
		if (numVetrine > 0)
			this.vetrina = true;
		else
			this.vetrina = false;
	}
	
	/**
	 * Costruttore utilizzato dalla query di AziendaInfoHome.findMiniDTOByFilter 
	 * (Questa query seleziona SOLO aziende che hanno una vetrina, quindi vetrina viene settato a true).
	 */
	public AziendaMiniDTO(Integer idPfPrincipal, String ragioneSociale, 
			String denominazioneComune) {
		super();
		this.id = idPfPrincipal;
		this.ragioneSociale = ragioneSociale;
		this.denominazioneComune = denominazioneComune;
		this.vetrina = true;
	}
	
	/*
	 * DA QUI IN POI: GETTER E SETTER
	 */
	
	public String getDenominazioneComune() {
		return denominazioneComune;
	}

	public String getRagioneSociale() {
		return ragioneSociale;
	}

	public void setDenominazioneComune(String denominazioneComune) {
		this.denominazioneComune = denominazioneComune;
	}

	public void setRagioneSociale(String ragioneSociale) {
		this.ragioneSociale = ragioneSociale;
	}

	public Boolean getVetrina() {
		return vetrina;
	}

	public void setVetrina(Boolean vetrina) {
		this.vetrina = vetrina;
	}
}
