package it.eng.myportal.dtos;


/**
 * Data transfer object della tabella di decodifica Qualifica SRQ
 *  
 * @author turrini
 * 
 * @see GenericDecodeDTO
 * @see ISuggestible
 */

public class DeQualificaSrqDTO extends GenericDecodeDTO implements ISuggestible {

	 private String codQualificaSrq;
     private String descrizione;
     
	public DeQualificaSrqDTO() {
	}

		@Override
	public String getDescrizione() {
		return this.descrizione;
	}

	public String getCodQualificaSrq() {
		return codQualificaSrq;
	}


	public void setCodQualificaSrq(String codQualificaSrq) {
		this.codQualificaSrq = codQualificaSrq;
	}


	public void setDescrizione(String descrizione) {
		this.descrizione = descrizione;
	}

}
