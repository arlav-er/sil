package it.eng.myportal.enums;

import java.util.HashMap;
import java.util.Map;

/**
 * 
 */
public enum ErroriRinnovoPatto {
	ERR_01(
			"01",
			"Servizio temporaneamente non disponibile. Contattare l'amministratore."),
	ERR_02(
			"02",
			"Lavoratore non presente in anagrafica."),
	ERR_03(
			"03",
			"Patto da rinnovare non presente."),
	ERR_04(
			"04",
			"Errore generico."),
	ERR_05(
			"05",
			"Impossibilie  rinnovare il patto perchè è stato fissato già un appuntamento."),
	ERR_06(
			"06",
			"Per le azioni specificate non è possibilie  rinnovare il patto."),
	ERR_07(
			"07",
			"Impossibilie  rinnovare il patto."),
	ERR_08(
			"08",
			"Errore generico."),
	ERR_09(
			"09",
			"Errore generico."),
	ERR_10(
			"10",
			"Errore generico."),
	ERR_11(
			"11",
			"Errore generico."),
	ERR_12(
			"12",
			"Errore generico."),
	ERR_99(
			"99",
			"Errore generico.");

	private String codice;
	private String messaggio;
	private static Map<String, String> mappa;

	private ErroriRinnovoPatto(String codice, String label) {
		this.codice = codice;
		this.messaggio = label;
	}

	static {
		mappa = new HashMap<String, String>();
		for (ErroriRinnovoPatto e : ErroriRinnovoPatto.values()) {
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