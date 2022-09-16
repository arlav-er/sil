package it.eng.myportal.dtos;

import java.util.Date;


/**
 * Classe per la registrazione di un certificatore.
 * 
 */
public class RegisterSoggettoPubblicoDTO extends RegisterDTO implements IDTO {   
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private Date dtmInizio;
	private Date dtmFine;
		
	private String ragioneSociale;
	private String codiceFiscale;
	private String telefono;
	
	public RegisterSoggettoPubblicoDTO() {
		super();		
			
	}

	public Date getDtmInizio() {
		return dtmInizio;
	}

	public void setDtmInizio(Date dtmInizio) {
		this.dtmInizio = dtmInizio;
	}

	public Date getDtmFine() {
		return dtmFine;
	}

	public void setDtmFine(Date dtmFine) {
		this.dtmFine = dtmFine;
	}


	public String getRagioneSociale() {
		return ragioneSociale;
	}

	public void setRagioneSociale(String ragioneSociale) {
		this.ragioneSociale = ragioneSociale;
	}

	public String getCodiceFiscale() {
		return codiceFiscale;
	}

	public void setCodiceFiscale(String codiceFiscale) {
		this.codiceFiscale = codiceFiscale;
	}

	public String getTelefono() {
		return telefono;
	}

	public void setTelefono(String telefono) {
		this.telefono = telefono;
	}
}
