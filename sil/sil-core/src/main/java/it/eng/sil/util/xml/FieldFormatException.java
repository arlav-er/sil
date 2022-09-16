package it.eng.sil.util.xml;

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
		sb.append("Il formato del campo ").append(getMessageParameter()).append(" è errato.");
		return sb.toString();
	}
}
