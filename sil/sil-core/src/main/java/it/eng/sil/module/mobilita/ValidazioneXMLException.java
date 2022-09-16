package it.eng.sil.module.mobilita;

/**
 * Eccezione per gestire i messaggi di errore durante la spedizione dell'XML di domanda individuale e renderli leggibili
 * all'utente finale.<br>
 * 
 * @author uberti
 */
public class ValidazioneXMLException extends Exception {

	private static final long serialVersionUID = -937496658569831061L;

	private String erroreMsg;

	public ValidazioneXMLException(String erroreMsg) {
		this.erroreMsg = erroreMsg;
	}

	/**
	 * Restituisce il messaggio dell'eccezione da stampare
	 * 
	 * @return String
	 */
	public String getExceptionMessage() {
		StringBuffer sb = new StringBuffer();
		sb.append("Errore durante la validazione della domanda.").append(" Si sono verificati i seguenti errori: ")
				.append(erroreMsg);
		return sb.toString();
	}

	public String getErroreMsg() {
		return erroreMsg;
	}
}
