package it.eng.myportal.utils.yg;

public class YgConstants {
	
	
	public static int AGE_MIN = 15;
	public static int AGE_MAX = 29;
	
	//Classi di adesione
	public static final String CHIUSA = "C";
	public static final String APERTA = "A";
	public static final String RIFIUTATA = "R";
	
	public final static String COD_ERRORE_GENERICO = "99";
	public final static String COD_ERRORE_SERVIZIO_NON_DISPONIBILE = "88";
	public final static String COD_ERRORE_IDENTIFICATIVO_SAP_NON_PRESENTE = "77";
	public final static String COD_ERRORE_ADESIONE_GIA_PRESENTE = "66";
	public final static String COD_ERRORE_VALIDAZIONE_XSD = "98";
	public final static String COD_ERRORE_ADESIONE_NON_POSSIBILE = "97";
	
	public final static String DESCRIZIONE_ERRORE_GENERICO = "Errore generico";
	public final static String DESCRIZIONE_ERRORE_SERVIZIO_NON_DISPONIBILE = "Servizio non disponibile";
	public final static String DESCRIZIONE_ERRORE_IDENTIFICATIVO_SAP_NON_PRESENTE = "Errore generico";
	public final static String DESCRIZIONE_ERRORE_ADESIONE_PRESENTE = "Risulta una politica attiva \"Patto di servizio\" erogata";
	
	public final static String CODICE_ANOMALIA_ADESIONE_GIA_PRESENTE = "P000";
	
	public final static String COD_FILTRO_RICERCA_ADESIONE_SENZA_STATO = "SENZA_STATO";
	public final static String LABEL_FILTRO_RICERCA_ADESIONE_SENZA_STATO = "Senza stato";
	
}
