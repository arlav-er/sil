package it.eng.myportal.dtos;


public class RegisterRegioneDTO extends RegisterDTO implements IDTO {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private DeRegioneDTO regione;

	public RegisterRegioneDTO() {
		super();
		regione = new DeRegioneDTO();
	}

	public DeRegioneDTO getRegione() {
		return regione;
	}

	public void setRegione(DeRegioneDTO regione) {
		this.regione = regione;
	}

}
