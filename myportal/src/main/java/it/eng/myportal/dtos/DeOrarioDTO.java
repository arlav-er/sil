package it.eng.myportal.dtos;


/**
 * 
 * @author Rodi A.
 * 
 */
public class DeOrarioDTO extends GenericDecodeDTO {

	private String descrizione;
	private String flagTempoPieno;

	public String getDescrizione() {
		return descrizione;
	}

	public void setDescrizione(String descrizione) {
		this.descrizione = descrizione;
	}

	public String getFlagTempoPieno() {
		return flagTempoPieno;
	}

	public void setFlagTempoPieno(String flagTempoPieno) {
		this.flagTempoPieno = flagTempoPieno;
	}

}
