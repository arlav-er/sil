package it.eng.sil.module.mobilita;

/**
 * Eccezione da lanciare nel caso il campo dell'XML non sia valorizzato
 * 
 * @see it.eng.sil.module.mobilita.XMLValidator
 * @author uberti
 */
public class MandatoryFieldException extends Exception {

	private static final long serialVersionUID = -5591576919827048395L;

	private String messageParameter;

	public MandatoryFieldException(String messageParameter) {
		this.messageParameter = messageParameter;
	}

	public String getMessageParameter() {
		return messageParameter;
	}

	/**
	 * Restituisce il messaggio dell'eccezione da stampare
	 * 
	 * @return String
	 */
	public String getExceptionMessage() {
		StringBuffer sb = new StringBuffer();
		sb.append("Nell'XML della domanda manca il campo obbligatorio: ").append(getMessageParameter());
		return sb.toString();
	}

}
