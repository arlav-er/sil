package it.eng.myportal.dtos;

import java.util.Date;


/**
 * Classe per la registrazione di un coordinatore.
 * 
 */
public class RegisterCoordinatoreDTO extends RegisterDTO implements IDTO {   
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private Date dtmInizio;
	private Date dtmFine;
	private String opzPrivilegio;
	
	public RegisterCoordinatoreDTO() {
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

	public String getOpzPrivilegio() {
		return opzPrivilegio;
	}

	public void setOpzPrivilegio(String opzPrivilegio) {
		this.opzPrivilegio = opzPrivilegio;
	}
	
	
		
}
