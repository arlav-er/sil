package it.eng.sil.module.mobilita;

/**
 * Eccezione da lanciare in caso il campo dell'XML non abbia il formato richiesto
 * 
 * @see it.eng.sil.module.mobilita.XMLValidator
 * @author uberti
 */
public class FieldFormatException extends Exception {

	private static final long serialVersionUID = -8901971297333990766L;

	private String messageParameter;

	public FieldFormatException(String messageParameter) {
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
		sb.append("Nell'XML della domanda manca il campo ").append(getMessageParameter());
		sb.append(" non presenta il formato corretto.");
		return sb.toString();
	}
}
