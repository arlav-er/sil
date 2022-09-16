package it.eng.sil.module.mobilita;

import java.util.HashMap;

/**
 * Eccezione per gestire i messaggi di errore durante la spedizione dell'XML di domanda individuale e renderli leggibili
 * all'utente finale.<br>
 * 
 * @author uberti
 */
public class InvioComunicazioneException extends Exception {

	private static final long serialVersionUID = -937496658569831061L;

	private String keyField;

	private static final HashMap<String, String> fieldToReadableText = new HashMap<String, String>();

	static {

		fieldToReadableText.put("ROW.indirizzoDom", "\"indirizzo di domicilio del lavoratore\"");
		fieldToReadableText.put("ROW.indirizzoRes", "\"indirizzo di residenza del lavoratore\"");
		fieldToReadableText.put("ROW.dataAssunzione", "\"data di assunzione\"");
		fieldToReadableText.put("ROW.categoria", "\"categoria\"");
		fieldToReadableText.put("ROW.tipoContratto", "\"tipo contratto\"");
		fieldToReadableText.put("ROW.qualifica", "\"qualifica\"");
		fieldToReadableText.put("ROW.dataLicenziamento", "\"data di licenziamento del lavoratore\"");
		fieldToReadableText.put("ROW.codiceFiscaleLav", "\"codice fiscale del lavoratore\"");
		fieldToReadableText.put("ROW.cognome", "\"cognome del lavoratore\"");
		fieldToReadableText.put("ROW.nome", "\"nome del lavoratore\"");
		fieldToReadableText.put("ROW.sesso", "\"sesso del lavoratore\"");
		fieldToReadableText.put("ROW.dataNascita", "\"data di nascita del lavoratore\"");
		fieldToReadableText.put("ROW.comuneNascita", "\"comune di nascita del lavoratore\"");
		fieldToReadableText.put("ROW.cittadinanza", "\"cittadinanza del lavoratore\"");
		fieldToReadableText.put("ROW.settore", "\"settore della sede aziendale\"");
		fieldToReadableText.put("ROW.codAzStato", "\"codice stato dell'azienda\"");
		fieldToReadableText.put("ROW.tipoIscrMbo", "\"tipo iscrizione della domanda\"");
		fieldToReadableText.put("ROW.dataInizioIscrMbo", "\"data inizio dell'iscrizione\"");
		fieldToReadableText.put("ROW.dataFineIscrMbo", "\"data fine dell'iscrizione\"");
		fieldToReadableText.put("ROW.qualificazione", "\"qualificazione\"");
		fieldToReadableText.put("ROW.codiceFiscale", "\"codice fiscale dell'azienda\"");
		fieldToReadableText.put("ROW.ragioneSociale", "\"ragione sociale dell'azienda\"");
		fieldToReadableText.put("ROW.tipologiaAzienda", "\"tipologia dell'azienda\"");
		fieldToReadableText.put("ROW.tipoComunicazione", "\"tipo di domanda\"");
		fieldToReadableText.put("ROW.codiceComunicazione", "\"codice della domanda\"");
		fieldToReadableText.put("ROW.dataInvio", "\"data invio della domanda\"");

	};

	public InvioComunicazioneException(String keyField) {
		this.keyField = keyField;
	}

	/**
	 * Restituisce il campo decodificato per il messaggio di errore da mostrare all'utente
	 * 
	 * @return String
	 */
	public String getReadableField() {
		return fieldToReadableText.get(keyField);
	}

	/**
	 * Restituisce il messaggio dell'eccezione da stampare
	 * 
	 * @return String
	 */
	public String getExceptionMessage() {
		StringBuffer sb = new StringBuffer();
		sb.append("Nell'XML della domanda manca il campo obbligatorio: ").append(getReadableField());
		return sb.toString();
	}
}
