package it.eng.myportal.dtos;



/**
 * Classe per la registrazione di una provincia.
 * Permette di memorizzare tutte le informazioni ricevute in fase di iscrizione.
 * Oltre ai dati dell'utente permette di avere un campo per la mail di conferma,
 * uno per la password di conferma ed uno per l'accettazione dei termini.
 * 
 */
public class RegisterProvinciaDTO extends RegisterDTO implements IDTO {   
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private DeProvinciaDTO provincia;
	private String flgLavoro;
	private String flgTematica;
	
	public RegisterProvinciaDTO() {
		super();		
		provincia = new DeProvinciaDTO();
		this.setFlgLavoro("N");
		this.setFlgTematica("N");
	}
	
	public DeProvinciaDTO getProvincia() {
		return provincia;
	}
	public void setProvincia(DeProvinciaDTO provincia) {
		this.provincia = provincia;
	}

	public String getFlgLavoro() {
		return flgLavoro;
	}

	public String getFlgTematica() {
		return flgTematica;
	}

	public void setFlgLavoro(String flgLavoro) {
		this.flgLavoro = flgLavoro;
	}

	public void setFlgTematica(String flgTematica) {
		this.flgTematica = flgTematica;
	}

}
