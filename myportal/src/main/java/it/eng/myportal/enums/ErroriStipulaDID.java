package it.eng.myportal.enums;

import java.util.HashMap;
import java.util.Map;

/**
 * 
 */
public enum ErroriStipulaDID {
	AUTENTICAZIONE("1", "Errore in fase di autenticazione"), 
	CREDENZIALI("2", "Credenziali di accesso al servizio non valide"), 
	NO_COD_FISC("3", "Codice fiscale non trovato"), 
	COD_FISC_NON_VALIDO("4", "Codice fiscale non valido"), 
	DATA_NON_VALIDA("5", "Data non valida"), 
	DATA_FUTURA("6", "Data errata perch&egrave; futura"), 
	DATA_PREC("7", "Data dichiarazione precedente al 30/01/2003"), 
	NON_COMP("8", "Lavoratore non competente"), 
	SO_NON_COMP("9", "Stato Occupazionale non compatibile"), 
	SUPERAM_REDDITO("10", "Superamento reddito"), 
	DECORRENZA_TERMINI("11", "Decorrenza termini"), 
	DOCUMENTO_INDENT("12","Errore nell'inserimento del documento di identificazione"), 
	PRIVACY("13", "Errore nell'inserimento della presa visione della privacy"), 
	ELENCO_ANAG("14", "Errore relativo all'elenco anagrafico"), 
	DID_PRESENTE("15", "Hai gi&agrave; una DID attiva."), 
	INSERIMENTO_DID("16", "Errore inserimento DID"), 
	ASSOC_DOC_IDENT("17", "Errore associazione documento di identificazione"), 
	PROTOCOLLAZIONE("18", "Errore di protocollazione"), 
	DID_NON_STIPULABILE("19", "Did non stipulabile"), 
	ALTRO_RAPPORTO_LAVORO("20", "DID non valida, è in corso un altro rapporto di lavoro"), 
	GENERICO("99", "Errore generico");

	private String codice;
	private String messaggio;
	private static Map<String, String> mappa;

	private ErroriStipulaDID(String codice, String label) {
		this.codice = codice;
		this.messaggio = label;
	}

	static {
		mappa = new HashMap<String, String>();
		for (ErroriStipulaDID e : ErroriStipulaDID.values()) {
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