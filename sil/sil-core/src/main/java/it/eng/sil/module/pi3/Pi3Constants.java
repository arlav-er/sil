package it.eng.sil.module.pi3;

public interface Pi3Constants {

	public static final String PI3_DOCUMENT_TYPE_DID = "IM";
	public static final String PI3_DOCUMENT_TYPE_DID_ANNUALE = "IMDICANN";
	public static final String PI3_DOCUMENT_TYPE_PATTO = "PT297";
	public static final String PI3_DOCUMENT_TYPE_RICHIESTA_TRASFERIMENTO = "TRCPI";
	public static final String PI3_DOCUMENT_TYPE_RICHIESTA_DOCUMENTO = "TRDOC";
	public static final String PI3_DOCUMENT_TYPE_SICUREZZA_LAVORO = "SSL";
	public static final String PI3_DOCUMENT_TYPE_SI68 = "SI68";

	public static final String PI3_DOCUMENT_TYPE_ENTRATA = "A";
	public static final String PI3_DOCUMENT_TYPE_USCITA = "P";
	public static final String PI3_DOCUMENT_TYPE_INTERNO = "I";
	public static final String PI3_DOCUMENT_TYPE_NON_PROTOCOLLATO = "G";
	public static final String PI3_DOCUMENT_TYPE_PROTOCOLLATO = "P";
	public static final String PI3_DOCUMENT_TYPE_REPERTORIO = "R";
	public static final String PI3_DOCUMENT_TYPE_NON_APPLICABILE = "NA";

	public static final String PI3_DOCUMENT_TYPE_ENTRATA_DESCRIPTION = "IN ENTRATA";
	public static final String PI3_DOCUMENT_TYPE_USCITA_DESCRIPTION = "IN USCITA";
	public static final String PI3_DOCUMENT_TYPE_INTERNO_DESCRIPTION = "INTERNO";
	public static final String PI3_DOCUMENT_TYPE_NON_PROTOCOLLATO_DESCRIPTION = "PROTOCOLLATO";
	public static final String PI3_DOCUMENT_TYPE_REPERTORIO_DESCRIPTION = "REPERTORIO";
	public static final String PI3_DOCUMENT_TYPE_NON_APPLICABILE_DESCRIPTION = "NON APPLICABILE";

	public static final String PI3_DOCUMENT_TYPE_ENTRATA_DESC = "INPUT";
	public static final String PI3_DOCUMENT_TYPE_USCITA_DESC = "OUTPUT";
	public static final String PI3_DOCUMENT_TYPE_INTERNO_DESC = "INTERNAL";
	public static final String PI3_DOCUMENT_TYPE_REPERTORIO_DESC = "REPERTORIO";

	public static final String PI3_DOCUMENT_SEND_STATE_PROTOCOLLATO = "PRO"; // PROTOCOLLATO MAIN DOCUMENT CON SIGNATURE
	public static final String PI3_DOCUMENT_SEND_STATE_PROTOCOLLATO_DESCRIZIONE = "PROTOCOLLATO";
	public static final String PI3_DOCUMENT_SEND_STATE_IN_ATTESA_DI_PROTOCOLLAZIONE = "APR"; // MAIN DOCUMENT INVIATO MA
																								// SENZA SIGNATURE
	public static final String PI3_DOCUMENT_SEND_STATE_IN_ATTESA_DI_PROTOCOLLAZIONE_DESCRIZIONE = "IN ATTESA DI PROTOCOLLAZIONE";
	public static final String PI3_DOCUMENT_SEND_STATE_REPERTORIATO = "REP"; // REPERTORIATO
	public static final String PI3_DOCUMENT_SEND_STATE_REPERTORIATO_DESCRIZIONE = "REPERTORIATO";
	public static final String PI3_DOCUMENT_SEND_STATE_REPERTORIATO_SENZA_DOC_FIRMATO = "RSDF"; // REPERTORIATO SENZA
																								// DOCUMENTO FIRMATO
	public static final String PI3_DOCUMENT_SEND_STATE_REPERTORIATO_SENZA_DOC_FIRMATO_DESCRIZIONE = "REPERTORIATO SENZA DOCUMENTO FIRMATO";

	public static final String PI3_DOCUMENT_SEND_STATE_MANUALE = "MAN"; // PRATICA NON PROTOCOLLATA PI3 PER QUALCHE
																		// MOTIVO, DA GESTIRE MANUALMENTE
	public static final String PI3_DOCUMENT_SEND_STATE_MANUALE_DESCRIZIONE = "PRATICA NON PROTOCOLLATA PI3 DA GESTIRE MANUALMENTE";
	public static final String PI3_DOCUMENT_SEND_STATE_ALLEGATO_AGGIUNTO = "ADD";
	public static final String PI3_DOCUMENT_SEND_STATE_ALLEGATO_AGGIUNTO_DESCRIZIONE = "ALLEGATO AGGIUNTO";
	public static final String PI3_DOCUMENT_SEND_STATE_ERRORE_INDISPONIBILITA_SERVIZIO = "TIM";
	public static final String PI3_DOCUMENT_SEND_STATE_ERRORE_INDISPONIBILITA_SERVIZIO_DESCRIZIONE = "ERRORE PER INDISPONIBILITA' DI SERVIZIO";
	public static final String PI3_DOCUMENT_SEND_STATE_ERRORE_NEI_DATI_INVIATI = "ERR";
	public static final String PI3_DOCUMENT_SEND_STATE_ERRORE_NEI_DATI_INVIATI_DESCRIZIONE = "ERRORE NEI DATI INVIATI";
	public static final String PI3_DOCUMENT_SEND_STATE_PREDISPOSED = "PRE";
	public static final String PI3_DOCUMENT_SEND_STATE_PREDISPOSED_DESCRIZIONE = "PREDISPOSTO";
	public static final String PI3_DOCUMENT_SEND_STATE_ALLEGATO_NOTA_PRESA_VISIONE = "APV";
	public static final String PI3_DOCUMENT_SEND_STATE_ALLEGATO_NOTA_PRESA_VISIONE_DESCRIZIONE = "ALLEGATO NOTA CON PRESA VISIONE";
	public static final String PI3_DOCUMENT_SEND_STATE_ALLEGATO_NOTA_CARICAMENTO_SUCCESSIVO = "ACS";
	public static final String PI3_DOCUMENT_SEND_STATE_ALLEGATO_NOTA_CARICAMENTO_SUCCESSIVO_DESCRIZIONE = "ALLEGATO NOTA CON CARICAMENTO SUCCESSIVO";

	public static final String PI3_DOCUMENT_WS_URL_SERVICE_NAME = "Pi3WsEndpoint";

	public static final String PI3_DOCUMENT_WS_INPUT_CODE_AMN = "PI3CODAM";
	public static final String PI3_DOCUMENT_WS_INPUT_USERNAME = "PI3USERN";
	public static final String PI3_DOCUMENT_WS_INPUT_CODE_APPLICATION = "PI3CODAP";
	public static final String PI3_DOCUMENT_WS_INPUT_CODE_ROLE_LOGIN = "PI3CODRL";
	public static final String PI3_DOCUMENT_WS_INPUT_CODE_RF = "PI3CODRF";
	public static final String PI3_DOCUMENT_WS_INPUT_CODE_REGISTER_OR_RF = "PI3CRGRF"; // non piu' utilizzato, si usa il
																						// CODE_RF...

	public static final String PI3_DOCUMENT_WS_INPUT_CODE_REGISTER = "PI3CODRG"; // da usare?
	public static final String PI3_DOCUMENT_WS_INPUT_REPERTORIO = "PI3REPRT";

	public static final String PI3_DOCUMENT_WS_INPUT_CODE_RAGIONE_TRASMISSIONE = "COMPETENZA";
	public static final String PI3_DOCUMENT_WS_INPUT_TYPE_S_TRASMISSIONE = "S";

	public static final String PI3_SPIL_UTENTE_BEAN_LAVORATORE = "LAVORATORE";
	public static final String PI3_SPIL_UTENTE_BEAN_AZIENDA = "AZIENDA";
	public static final String PI3_SPIL_UTENTE_BEAN_UNITA_AZIENDA = "UNITA_AZIENDA";
	public static final String PI3_SPIL_UTENTE_BEAN_CPI = "CPI";

	public static final String PI3_SPIL_CORRESPONDENT_TYPE_PERSONA = "P";
	public static final String PI3_SPIL_CORRESPONDENT_TYPE_UNITA_ORGANIZZATIVA = "U";

	public static final String PI3_PROTOCOLLO_DOCUMENTO_FLG_PRINCIPALE_SI = "S";
	public static final String PI3_PROTOCOLLO_DOCUMENTO_FLG_PRINCIPALE_NO = "N";
	public static final String PI3_PROTOCOLLO_DOCUMENTO_FLG_PRINCIPALE_XML = "X";
	public static final String PI3_PROTOCOLLO_DOCUMENTO_ALLEGATO_XML = "XML";
	public static final String PI3_PROTOCOLLO_DOCUMENTO_ALLEGATO_RICEVUTA_FIRMA_GRAFOMETRICA = "RICEVUTA";
	public static final String PI3_PROTOCOLLO_DOCUMENTO_ALLEGATO_RICEVUTA_FIRMA_GRAFOMETRICA_CODICE_TIPO = "DFG";

	public static final String PI3_CODE_PAT_FASCICOLO_TITOLARIO_DEFAULT_VALUE = "24.3";

	public static final String PI3_INVIO_PROTOCOLLAZIONE_DIFFERITA_STATE_DA_ELABORARE = "DAELAB";
	public static final String PI3_INVIO_PROTOCOLLAZIONE_DIFFERITA_STATE_ELABORATA = "ELAB";
	public static final String PI3_INVIO_PROTOCOLLAZIONE_DIFFERITA_NOME_BATCH = "PROTOCOLLAZIONE DIFFERITA";

	public static final String PI3_SPIL_CODE_TYPE_DOMINIO_LAVORATORE = "DL";
	public static final String PI3_SPIL_CODE_TYPE_DOMINIO_AZIENDA = "DA";

}
