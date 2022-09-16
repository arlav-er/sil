package it.eng.sil.module.movimenti.constant;

import java.util.HashMap;
import java.util.Map;

public class DeTipoContrattoConstant {

	public static final String LAVORO_A_TEMPO_INDETERMINATO = "A.01.00";

	public static final String APPRENDISTATO_EX_ART16 = "A.03.00";
	public static final String APPRENDISTATO_DIRITTO_DOVERE_ISTRUZIONE = "A.03.01";
	public static final String APPRENDISTATO_PROFESSIONALIZZANTE = "A.03.02";
	public static final String APPRENDISTATO_ALTA_FORMAZIONE = "A.03.03";
	public static final String CONTRATTO_FORMAZIONE_LAVORO = "A.03.04";
	public static final String CONTRATTO_DI_INSERIMENTO_LAVORATIVO = "A.03.07";
	public static final String APPRENDISTATO_QUALIFICA_DIPLOMA_PROFESSIONALE = "A.03.08";
	public static final String APPRENDISTATO_PROFESSIONALIZZANTE_O_CONTRATTO_DI_MESTIERE = "A.03.09";
	public static final String APPRENDISTATO_ALTA_FORMAZIONE_RICERCA = "A.03.10";
	public static final String APPRENDISTATO_QUALIFICA_DIPLOMA_PROFESSIONALE_IN_MOBILITA = "A.03.11";
	public static final String APPRENDISTATO_CONTRATTO_DI_MESTIERE_IN_MOBILITA = "A.03.12";
	public static final String APPRENDISTATO_ALTA_FORMAZIONE_RICERCA_IN_MOBILITA = "A.03.13";
	public static final String APPRENDISTATO_PROFESSIONALIZZANTE_O_MESTIERE_STAGIONALI = "A.03.14";

	public static final String LAVORO_INTERMITTENTE = "A.05.02";

	public static final String CONTRATTO_INTERINALE_TI = "A.06.00";
	public static final String CONTRATTO_INTERINALE_TD = "A.06.01";

	public static final String LAVORO_A_DOMICILIO = "A.08.02";

	public static final String LAVORO_A_PROGETTO_COLLABORAZIONE_COORDINATA_CONTINUATIVA = "B.01.00";
	public static final String LAVORO_OCCASIONALE = "B.02.00";
	public static final String LAVORO_COLLABORAZIONE_COORDINATA_CONTINUATIVA = "B.03.00";

	public static final String TIROCINIO = "C.01.00";
	public static final String LAVORO_ATTIVITA_SOCIALMENTE_UTILE_LSU_ASU = "C.03.00";

	public static final String LAVORO_AUTONOMO_NELLO_SPETTACOLO = "G.03.00";

	public static final String ASSOCIAZIONE_IN_PARTECIPAZIONE = "L.02.00";

	public static final String CONTRATTO_DI_AGENZIA = "M.02.00";

	public static final Map<String, String> mapContratti_TD_TI = new HashMap<String, String>();
	static {
		mapContratti_TD_TI.put("A.04.02", "S");
		mapContratti_TD_TI.put("A.05.02", "S");
		mapContratti_TD_TI.put("A.07.02", "S");
		mapContratti_TD_TI.put("A.08.02", "S");
		mapContratti_TD_TI.put("L.02.00", "S");
		mapContratti_TD_TI.put("M.02.00", "S");
		mapContratti_TD_TI.put("A.03.09", "S");
	}

	public static final Map<String, String> mapContrattiAut_Tra = new HashMap<String, String>();
	static {
		mapContrattiAut_Tra.put("L.01.00", "S");
		mapContrattiAut_Tra.put("L.01.01", "S");
		mapContrattiAut_Tra.put("L.02.00", "S");
	}

	public static final Map<String, String> mapContrattiAgenzia = new HashMap<String, String>();
	static {
		mapContrattiAgenzia.put("M.01.00", "S");
		mapContrattiAgenzia.put("M.01.01", "S");
		mapContrattiAgenzia.put("M.02.00", "S");
	}

	public static final Map<String, String> mapContratti_Tirocini = new HashMap<String, String>();
	static {
		mapContratti_Tirocini.put("C.01.00", "S");
		mapContratti_Tirocini.put("C.02.00", "S");
	}

	public static final Map<String, String> mapContrattiPG_Collaborazione = new HashMap<String, String>();
	static {
		mapContrattiPG_Collaborazione.put("B.01.00", "S");
		mapContrattiPG_Collaborazione.put("B.02.00", "S");
		mapContrattiPG_Collaborazione.put("B.03.00", "S");
	}

	public static final String listaContrattiAut_Tra = "'L.01.00' , 'L.01.01', 'L.02.00'";
}
