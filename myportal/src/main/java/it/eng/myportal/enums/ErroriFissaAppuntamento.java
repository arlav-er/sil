package it.eng.myportal.enums;

import java.util.HashMap;
import java.util.Map;

/**
 * 
 */
public enum ErroriFissaAppuntamento {
	APPUNTAMENTO_PRENOTATO("00", "<p>Ti &egrave; stato assegnato il seguente appuntamento:"), 
	ERRORE_AUTENTICAZIONE(
			"01",
			"<p>Servizio temporaneamente non disponibile.</p><p>Si invita a riprovare in un momento successivo.</p>"), 
	ERRORE_VALIDAZIONE_XSD(
			"02",
			"<p>Servizio temporaneamente non disponibile.</p><p>Si invita a riprovare in un momento successivo.</p>"), 
	ERRORE_PROVINCIA(
			"15",
			"<p>Servizio temporaneamente non disponibile.</p><p>Si invita a riprovare in un momento successivo.</p>"), 
	NET_ERROR(
			"-",
			"<p>Servizio temporaneamente non disponibile.</p><p>Si invita a riprovare in un momento successivo.</p>"), 
	INCOERENZA_INPUT_SLOT_CPI(
			"10",
			"<p>Siamo spiacenti, l'appuntamento scelto non &egrave; più disponibile.</p><p>&Eacute; possibile effettuare un nuovo tentantivo.</p>"), 
	SLOT_INESISTENTE(
			"16",
			"<p>Siamo spiacenti, l'appuntamento scelto non &egrave; più disponibile.</p><p>&Eacute; possibile effettuare un nuovo tentantivo.</p>"), 
	SLOT_NON_DISPONIBILE(
			"17",
			"<p>Siamo spiacenti, l'appuntamento scelto non &egrave; più disponibile</p><p>&Eacute; possibile effettuare un nuovo tentantivo.</p>"), 
	INCOERENZA_INPUT_CPI_PROVINCIA(
			"11", "<p>Errore generico.</p>"), 
	INCOERENZA_INPUT_DOMICILIO_CPI("12",
			"<p>Sei domiciliato in regione, il CPI e la provincia devono essere coerenti con il comune del tuo domicilio.</p>"), 
	NO_EMAIL(
			"13", "<p>Errore generico.</p>"), 
	NO_CELLULARE("14", "<p>Errore generico.</p>"), 
	SERVIZIO_MANCANTE("19",
			"<p>Errore generico.</p>"), 
	ERRORE_CODICE_FISCALE("20", "<p>Errore generico.</p>"), 
	ERRORE_CODIFICE("21",
			"<p>Errore generico.</p>"), 
	ERRORE_CONCORRENZA("98", "<p>Errore generico.</p>"), 
	ERRORE_GENERICO("99",
			"<p>Errore generico.</p>"), 
	NESSUNO_SLOT_DISPONIBILE_DATE(
			"18_DATE",
			"<p>Siamo spiacenti ma non risultano disponibilita' nel periodo da te indicato. Riprova modificando le date impostate.</p>"),
	NESSUNO_SLOT_DISPONIBILE_NO_DATE(
			"18_NO_DATE",
			"<p>Siamo spiacenti ma non risultano disponibilita'.</p>");

	private String codice;
	private String messaggio;
	private static Map<String, String> mappa;

	private ErroriFissaAppuntamento(String codice, String label) {
		this.codice = codice;
		this.messaggio = label;
	}

	static {
		mappa = new HashMap<String, String>();
		for (ErroriFissaAppuntamento e : ErroriFissaAppuntamento.values()) {
			mappa.put(e.getCodice(), e.getMessaggio());
		}
	}

	public static Map<String, String> asMap() {
		return mappa;
	}

	public String getCodice() {
		return codice;
	}

	public String getMessaggio() {
		return messaggio;
	}
}