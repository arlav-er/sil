package it.eng.myportal.utils.yg;

public class YgDebugConstants {
	
	// TODO: prima del rilascio mettere IS_DEBUG = false
	public final static boolean IS_DEBUG = false;
	
	// SAP
	
	public final static String DEBUG_IDENTIFICATIVO_SAP_VERIFICA = "0"; // esito verifica (es: AA00000001A) oppure "0" sta per SAP non trovata
	public final static String DEBUG_IDENTIFICATIVO_SAP_INVIO = "AA00000001A"; // esito invio (es: AA00000001A)
	
	public final static boolean DEBUG_ECCEZIONE_SERVIZIO_NON_DISPONIBILE_INVIO_SAP = false;
	public final static boolean DEBUG_ECCEZIONE_ERRORE_GENERICO_INVIO_SAP = false;
	
	// YG
	
	public final static boolean DEBUG_ADESIONE_ESITO_INVIO = true;
	public final static boolean DEBUG_ADESIONE_ESITO_CHECK = false;
	
	public final static boolean DEBUG_ECCEZIONE_SERVIZIO_NON_DISPONIBILE_INVIO_ADESIONE = false;
	public final static boolean DEBUG_ECCEZIONE_ERRORE_GENERICO_INVIO_ADESIONE = false;
	
	public final static boolean DEBUG_ECCEZIONE_SERVIZIO_NON_DISPONIBILE_CHECK_ADESIONE = false;
	public final static boolean DEBUG_ECCEZIONE_ERRORE_GENERICO_CHECK_ADESIONE = false;
	
	// raggiungibile in debug solo con (DEBUG_ADESIONE_ESITO_INVIO = true)
	public final static boolean DEBUG_ADESIONE_ESITO_INVIO_GIA_PRESENTE = false;
	
	public final static String DEBUG_ADESIONE_ESITO_INVIO_GIA_PRESENTE_MSG =  
		"Riscontrato errore nella validazione dell'input :<ListaAnomalie>" +
		"<Anomalia>" +
		"<CodiceAnomalia>P000</CodiceAnomalia>" +
		"<DescrizioneAnomalia>Per la regione UMBRIA e codice fiscale VNTFNC88E20G478O adesione gia effettuata</DescrizioneAnomalia>" +
		"<CampiInErrore>Utente1/AdesioneGiaEffettuata1</CampiInErrore>" +
		"</Anomalia>" +
		"</ListaAnomalie>";
	
}
