package it.eng.sil.module.movimenti.constant;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

public class Properties {

	public static final String PATTO_PROVENIENZA_STIPULA_SO = "S"; // D quando patto stipulato in presenza di DID, M in
																	// presenza di Mobilita, S in presenza di stato
																	// occupazionale D o I
	public static final String DEFAULT_CONFIG = "0";
	public static final String CUSTOM_CONFIG = "1";

	public static final String FLAG_1 = "1";
	public static final String FLAG_S = "S";

	public static final String MOTIVO_FINE_ATTO_VA18 = "APS";
	public static final String STATO_ATTO_VA18 = "AU";
	public static final String MOTIVO_FINE_ATTO_DOC_VA18 = "UFF";
	public static final String STATO_ATTO_PROTOC = "PR";
	public static final String STATO_ATTO_NON_PROTOCOLLATO = "NP";
	public static final String TIPO_DICHIARAZIONE_DID = "ID";
	public static final String INTESTAZIONE_STAMPA_DID = "OFF";
	public static final String DECADUTO_PER_AVVIAMENTO = "AV";
	public static final String DISOCCUPATO = "A21";
	public static final String PROVENIENZA_STATO_OCC_DID = "D";
	public static final String NOTE_DID_VA18 = "D.I.D inserita da sistema";
	public static final String DEFAULT_DOCUMENTO_PATTO = "PT297";
	public static final String DOCUMENTO_PATTO_GENERICO = "ACLA";
	public static final String RAGG_DISOCCUPATO = "D";
	public static final String RAGG_INOCCUPATO = "I";
	public static final String PROVVEDIMENTO_UFFICIO = "PU";
	public static final String CHIUSURA_AUTOMATICA_ACCORDO = "CAA";
	public static final String PROVENIENZA_STATO_OCC_MANUALE = "O";
	public static final String CM_RAGG_DISABILE = "D";
	public static final String CONTRATTO_LAVORO_INTERMITTENTE = "LI";

	public static final String CODMONOTIPOCPI_COMP = "C";
	public static final String CODMONOTIPOCPI_TIT = "T";

	public static final int LUNGHEZZA_NOTE_DID = 1000;
	public static final int MESI_SOSP_DECRETO150 = 6;
	public static final int GIORNI_SOSP_DECRETO150 = 180;
	public static final int MESI_COMMERCIALI = 30;
	public static final String REGIONE_CALABRIA = "18";
	public static final String REGIONE_TRENTO_MIN = "18";

	public static final BigDecimal UT_OPERATORE_IMPOSTAZIONI = new BigDecimal(365);
	public static final String RER = "8";
	public static final String UMB = "10";
	public static final String CAL = "18";
	public static final String VDA = "2";
	public static final String TN = "22";
	public static final Map<String, String> Tirocini_Apprendistati_Azioni = new HashMap<String, String>();
	static {
		Tirocini_Apprendistati_Azioni.put("C06", "S");
		Tirocini_Apprendistati_Azioni.put("D01", "S");
		Tirocini_Apprendistati_Azioni.put("E01", "S");
		Tirocini_Apprendistati_Azioni.put("E02", "S");
		Tirocini_Apprendistati_Azioni.put("E03", "S");
	}
}
