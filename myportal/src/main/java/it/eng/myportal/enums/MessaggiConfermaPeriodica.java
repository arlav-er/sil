package it.eng.myportal.enums;

import java.util.HashMap;
import java.util.Map;

/**
 * 
 */
public enum MessaggiConfermaPeriodica {
	OK("OK", "Aggiornamento avvenuto con successo: hai confermato la tua Dichiarazione di Immediata Disponibilità. La prossima scadenza è quella indicata sul Patto di Servizio da te sottoscritto e in tuo possesso. Entro la data indicata nel Patto dovrai recarti personalmente al Centro per L'impiego per il rinnovo dello stesso."),
	KO("KO", "Operazione fallita"),
	ERR_COMUNICAZIONE("01", "Errore di comunicazione"),
	ERR_REQUISITI_DID("02a", "Impossibile elaborare la richiesta. Non risulta una Dichiarazione di Immediata Disponibilità rilasciata. Per verificare la tua situazione puoi recarti al tuo Centro per l'Impiego di competenza."),
	ERR_REQUISITI_PATTO("02b", "Impossibile elaborare la richiesta. Non risulta un Patto di Servizio stipulato. Per verificare la tua situazione puoi recarti al tuo Centro per l'Impiego di competenza."),
	ERR_REQUISITI_IN_CORSO("02c", "Impossibile elaborare la richiesta. Non risulta una Conferma Periodica in corso. Per verificare la tua situazione puoi recarti al tuo Centro per l'Impiego di competenza."),
	ERR_REQUISITI_PATTO_SCADENZA("02d", "Impossibile elaborare la richiesta. Il tuo Patto di Servizio risulta in scadenza o è già scaduto. Devi recarti personalmente al tuo Centro per l'Impiego di competenza per il rinnovo."),
	ERR_RANGE_TEMP("03", "Impossibile elaborare la richiesta. La conferma della Dichiarazione di Immediata Disponibilità deve essere resa entro il termine indicato nel Patto di Servizio da te sottoscritto e in tuo possesso. Per verificare la tua situazione puoi recarti al tuo Centro per l'Impiego di competenza."),
	ERR_GENERICO_SIL("04", "Errore generico durante l'operazione"),
	ERR_AGG_DATI("05", "Errore nell'aggiornamento dei dati dell'utente"),
	ERR_UT_NON_TROVATO("06", "Lavoratore non trovato sul sistema SIL"),
	ERR_VALIDAZIONE_XML("07", "Errore nella validazione dell'XML"),
	ERR_GENERICO("11", "Errore generico");

	private String codice;
	private String messaggio;
	private static Map<String, String> mappa;

	private MessaggiConfermaPeriodica(String codice, String label) {
		this.codice = codice;
		this.messaggio = label;
	}

	static {
		mappa = new HashMap<String, String>();
		for (MessaggiConfermaPeriodica e : MessaggiConfermaPeriodica.values()) {
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