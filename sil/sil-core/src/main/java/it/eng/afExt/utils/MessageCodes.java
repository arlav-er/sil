package it.eng.afExt.utils;

/**
 * Classe contenitore dei codici di messaggi o errori che si trovano nel file ...WEB-INF\classes\messages_x_Y.properties
 * dove x e Y indica language e country scelte.
 * 
 * @author Corrado Vaccari
 */
public abstract class MessageCodes {
	// Quando si aggiungono nuove sezioni, tipo "errori sul lavoratore",
	// partire sempre dal 10000 superiore,
	// Es. ultimo messaggio : 30103, cominciare la nuova sezione da 40000
	// per non rischiare di utilizzare id gia' usati
	public static abstract class General {
		public static final int NO_ROWS_IN_LIST = 10001;
		public static final int DELETE_SUCCESS = 10002;
		public static final int DELETE_FAIL = 10003;
		public static final int UPDATE_SUCCESS = 10004;
		public static final int UPDATE_FAIL = 10005;
		public static final int GET_ROW_FAIL = 10006;
		public static final int INSERT_SUCCESS = 10007;
		public static final int INSERT_FAIL = 10008;
		public static final int OPERATION_SUCCESS = 10009;
		public static final int OPERATION_FAIL = 10010;
		public static final int ELEMENT_DUPLICATED = 10011;
		public static final int DELETE_FAILED_FK = 10012;
		public static final int UNEXPECTED_INTERRUPT = 10014;
		public static final int ERR_AGGIORNAMETO_AMDOCCOLL = 10015;
		public static final int ERR_NO_DB_CONNECTION = 10016;
		public static final int ERR_IN_WRITING_LOG = 10017;
		public static final int REPORT_EMPTY = 10024;
		public static final int ELEMENT_DUPLICATED_WITH_KEY = 10022;
		public static final int ELEMENT_COMUNE_DUPLICATED_WITH_KEY = 10023;

		public static final int CONCORRENZA = 20990;

		public static final int MAX_SIZE_UPLOAD = 10025;

		/**
		 * RIPORTA IL MESSAGGIO DI ERRORE INTERNO DELL'APPLICATIVO (DA PASSARE COME PRIMO PARAMETRO)
		 */
		public static final int RIPORTA_ERRORE_INTERNO = 10101;
		public static final int RIPORTA_ERRORE = 10102;
		public static final int RIPORTA_WARNING = 10103;

		/**
		 * E' il numero restituito dalla procedure gestamm.checkIntersezioneDate() nel caso in cui la data inizio del
		 * record successivo sia minore della data fine del record precedente (il codice della trace non puo' essere
		 * maggiore di -20000)
		 */
		public static final int ERR_INTERSEZIONE_DATA_REC_STORICIZZATO = 20010;
		/**
		 * E' il numero restituito dalla procedure gestamm.checkIntersezioneDate() nel caso in cui la data inizio del
		 * record successivo sia uguale alla data fine del record precedente
		 */
		public static final int WARNING_INTERSEZIONE_DATA_REC_STORICIZZATO = 20011;

		public static final int ERR_INTERSEZIONE_DATA_REC_ESISTENTE = 20012;

		public static final int ERR_STATO_OCCUPAZIONALE_DUPLICATO = 20013;

		public static final int ERR_ESISTE_ISCRIZIONE_NON_PROT = 20014;

		public static final int ERR_ESISTE_INTERSEZIONE_PERIODO = 20016;

		public static final int CALCOLO_IMPATTI_MANUALE = 20100;
		public static final int LOG_VALIDAZIONE_MOBILITA_MASSIVA = 0;

		// public static final String ENCRYPTER_KEY = "(r1Pt@<|";
		public static final String RETTIFICA_COMUNICAZIONE_PREC = "03";
		public static final String ANNULLAMENTO_COMUNICAZIONE_PREC = "04";
		public static final String ANNULLAMENTO_DA_UFFICIO_COMUNICAZIONE_PREC = "10";

		public static final int MAX_NUMERO_RISULTATI = 5000;
		public static final int INVALID_FORMAT = 20015;

		public static final String DATA_LEGGE_FORNERO = "01/01/2100";
		public static final String DATA_DECRETO_FORNERO_2014 = "01/01/2014";
		public static final String DATA_PROFILING_2015 = "01/02/2015";
		public static final String PATTO_GARANZIA_GIOVANI = "MGG";
		public static final String PATTO_GARANZIA_GIOVANI_UMBRIA = "MGGU";
		public static final String PATTO_L14 = "L14";
		public static final String SERVIZIO_L14 = "PL14";
		public static final String PATTO_DOTE = "DOTE";
		public static final String PATTO_DOTE_IA = "DOTE_IA";
		public static final String SERVIZIO_DOTE = "DOTE";
		public static final String SERVIZIO_DOTE_IA = "DOTE_IA";

		public static final String DEFAULT_DATA_FINE_VAL = "01/01/2100";

		public static final String DATA_DECRETO_2019 = "05/11/2019";
	}

	public static abstract class Mansioni {
		public static final int MANSIONE_PRESENTE_IN_ESP_LAV = 20000;
		public static final int MANSIONE_ASS_PATTO_PROTOCOLLATO = 20001;
		public static final int ESPERIENZA_ASS_PATTO_PROTOCOLLATO = 20002;
		public static final int MANSIONE_CON_ESPERIENZA_ASS_PATTO_PROTOCOLLATO = 20003;
	}

	public static abstract class CodiceFiscale {
		public static final int ERR_LUNGHEZZA = 30000;
		public static final int ERR_CARATTERI_NUMERICI = 30001;
		public static final int ERR_CARATTERI_ALFABETICI = 30002;
		public static final int ERR_SESSO = 30003;
		public static final int ERR_CHECK_DIGIT = 30004;
		public static final int ERR_COGNOME = 30005;
		public static final int ERR_NOME = 30006;
		public static final int ERR_MESE = 30007;
		public static final int ERR_ANNO = 30008;
		public static final int ERR_COMUNE = 30009;
		public static final int ERR_CODICE_ILLEGALE = 30010;
		public static final int ERR_CARATTERE_MESE = 30011;
		public static final int ERRPROV_NAN = 31001;
		public static final int ERRPROV_CHECK_DIGIT = 31004;

		public static final boolean isInRange(int msgCode) {
			if ((msgCode < 30000) && (msgCode > 30011)) {
				return false;
			}

			return true;
		}
	}

	// messaggi per la correttezza del codice fiscale di un'azienda
	public static abstract class CodiceFiscaleAzienda {
		public static final int ERR_LUNGHEZZA = 31000;
		public static final int ERR_NAN = 31001;
		public static final int ERR_CARATTERI_NUMERICI = 31002;
		public static final int ERR_CARATTERI_ALFABETICI = 31003;
		public static final int ERR_CHECK_DIGIT = 31004;
		public static final int ERR_GIA_PRESENTE = 31005;

		public static final boolean isInRange(int msgCode) {
			if ((msgCode < 31000) && (msgCode > 31005)) {
				return false;
			}

			return true;
		}
	}

	public static abstract class PartivaIva {
		public static final int ERR_LUNGHEZZA = 32000;
		public static final int ERR_CARATTERI = 32001;
		public static final int ERR_PIVA_FITTIZIA = 32002;
		public static final int ERR_CHECK_DIGIT = 32003;
		public static final int NON_VALORIZZATA = 32004;

		public static final boolean isInRange(int msgCode) {
			if ((msgCode < 32000) && (msgCode > 32003)) {
				return false;
			}

			return true;
		}

	}

	public static abstract class Agenda {
		public static final int APPUNTAMENTI_NEL_GIORNO = 40000;
		public static final int APPUNTAMENTI_NEGLI_ANNI = 40001;
	}

	public static abstract class Patto {
		public static final int LEGAME_RIUSCITO = 33000;
		public static final int ERR_LEGAME = 33001;
		// public static final int DEL_LEGAME_RIUSCITO = 33002;
		// public static final int ERR_DEL_LEGAME = 33003;
		public static final int ERR_AGG_NOTE_IMP = 33004;
		public static final int CHIUSURA_NON_POSSIBILE = 33005;
		public static final int CHIUSURA_AVVENUTA = 33006;
		public static final int AZIONE_PERC_CONCORD_ESISTENTE = 33007;
		public static final int ASSOC_PATTO_PERC_CONCORD = 33008;
		public static final int COLLOQUIO_CON_PERC_CONCORD = 33009;
		public static final int COLLOQUIO_CON_CIG_COLLEGATA = 33010;
		public static final int COLLOQUIO_CANC_CON_CIG_COLL = 33011;
		public static final int PRIMA_AZIONE_PERC_CONCORD_CIG = 33012;
		public static final int PERC_CONCORD_CANC_PIN_CIG = 33013;
		public static final int PERC_CONCORD_UPD_PIN_CIG = 33014;
		public static final int PRESTAZIONICIG_NUMMAXSESSIONI_INS = 33015;
		public static final int PRESTAZIONICIG_NUMMAXSESSIONI_UPD = 33016;
		public static final int PRESTAZIONICIG_EXISTS_CORSOCI = 33017;
		public static final int UNICA_AZIONE_PERC_CONCORD_CIG = 33018;
		public static final int ESISTECORSOPROG = 33019;
		public static final int ERR_ACCORDO_IN_297 = 33020;
		public static final int MOTIVO_CHIUSURA_PATTO_ACCORDO_NON_POSSIBILE = 33021;
		public static final int ERR_CF_ENTE_PROMOTORE = 33022;
		public static final int ADESIONE_PERC_CONCORD_ESISTENTE = 33023;
		public static final int COLLOQUIO_CANC_CON_SERVIZIO_L14 = 33024;
		public static final int DATA_ADESIONE_ASSENTE = 33025;
		public static final int ADESIONE_COLLEGATA_POLITICHE_ATTIVE = 33026;
		public static final int ERR_UPDATE_ADESIONE_COLLEGATA_POLITICHE_ATTIVE = 33027;
		public static final int ERR_CF_ENTE_PROMOTORE_ESITO = 33028;
		public static final int COLLOQUIO_CANC_CON_SERVIZIO_DOTE = 33029;
		public static final int ERR_ETA_TIROCINIO_EXTRACURRICULARE = 33099;
		public static final int PATTO_PROROGATO_OK = 33901;
		public static final int PATTO_PROROGATO_KO = 33902;
		public static final int ERR_DOTE_PROCESSO_RISULTATO = 33903;
		public static final int ERR_CALCOLO_RESIDUO_DOTI = 33904;
		public static final int ERR_ACCORDO_GENERICO_MISURA_CONCORDATA = 33905;
		public static final int ERR_ETA_LAV_PATTO_OVER30 = 33906;
		public static final int ERR_ETA_LAV_PATTO_OVER45 = 33907;
		public static final int ERR_MISURA_INCLUSIONE_ATTIVA_NON_VALIDA = 33908;
		public static final int ERR_MISURA_INCLUSIONE_ATTIVA_ISEE = 33909;
		public static final int ERR_ASSOCIAZIONE_AZIONEOVER30_PATTO = 33910;
		public static final int ERR_ASSOCIAZIONE_AZIONEOVER45_PATTO = 33911;
		public static final int ERR_ASSOCIAZIONE_INCLUSIONEATTIVA_PATTO = 33912;
		public static final int ERR_LIMITE_ASSOCIAZIONE_CO6_PATTO_OVER30 = 33913;
		public static final int ERR_LIMITE_ASSOCIAZIONE_CO7_PATTO_OVER30 = 33914;
		public static final int ERR_LIMITE_ASSOCIAZIONE_CO6_PATTO_OVER45 = 33915;
		public static final int ERR_LIMITE_ASSOCIAZIONE_CO7_PATTO_OVER45 = 33916;
		public static final int ERR_LIMITE_ASSOCIAZIONE_CO6_PATTO_INATT = 33917;
		public static final int ERR_ASSOCIAZIONE_AZIONEPA_PATTO_NO_PA = 33918;
		public static final int ERR_CAMBIO_MISURACONCORDATA_PATTO = 33919;
		public static final int ERR_NO_DATA_ADESIONE_PA = 33920;
		public static final int ERR_NO_DID_APERTA_ADESIONE_PA = 33921;
		public static final int ERR_ETA_LAV_PATTO_MISURA = 33922;
		public static final int ERR_POLITICHE_ATTIVE_MISURA = 33923;
		public static final int ERR_GGU_DATA_ADESIONE = 33924;
		public static final int ERR_GGU_RESIDENZA = 33925;
		public static final int ERR_GGU_A02 = 33926;
		public static final int ERR_ACCORDO_GGU_RESIDENZA = 33927;
		public static final int ERR_MISURA_MGGU_NON_VALIDA = 33928;
		public static final int ERR_PROFILO_LAVORATORE_EMPTY = 33929;
		public static final int ERR_PRESTAZIONI_ASSOCIATE_EMPTY = 33930;
		public static final int ERR_ANZIANITA_POC = 33931;
		public static final int ERR_RIAPERTURA_PATTO_ACCORDO_OCC = 33932;
		public static final int ERR_PATTI_PR_SUCCESSIVI = 33933;
		public static final int WARNING_PREPATTI_SUCCESSIVI = 33934;
		public static final int ERR_PATTI_PR_SUCCESSIVI_AZIONI_COLL = 33935;
		public static final int ERR_PATTI_PR_SUCCESSIVI_DOCUMENTI_DOPPI = 33936;
		public static final int ERR_ASSOCIAZIONE_AZIONI_ENTE = 33937;
		public static final int ERR_RIAPERTURA_PATTO_L14 = 33938;
		public static final int ERR_PATTI_L14_PR_SUCCESSIVI = 33939;
		public static final int COLLOQUIO_PROGRAMMA_APERTO = 33940;
		public static final int ERR_PROGRAMMA_APERTO_NO_ATTIVITA = 33941;
		public static final int COLLOQUIO_PROGRAMMA_GIA_APERTO = 33942;
		public static final int ERR_PATTO_ADR = 33943;
		public static final int ERR_PATTO_ADR_POC = 33944;
		public static final int ERR_PROG_186_AZIONE_DATA_AVVIO = 33945;
		public static final int ERR_OBBLIGOENTE = 33946;
		public static final int ERR_CHECKCONTROLLIPROTOCOLLAZIONE = 33947;
		public static final int ERR_DID_CHIUSA = 33948;

		public static final int ERR_AZIONE_PRECEDENTEMENTE_ASSOCIATA_PATTO = 33900;

		public static final int ERR_INVIO_PT_ONLINE = 33949;
		public static final int SCADENZA_TERMINI_PT_ONLINE = 33950;
		public static final int NO_SCADENZA_TERMINI_PT_ONLINE = 33951;
		public static final int ACCETTAZIONE_PT_ONLINE = 33952;
		public static final int ERR_STORICO_PT_ONLINE = 33953;

		public static final boolean isInRange(int msgCode) {
			if ((msgCode < 33000) && (msgCode > 33009)) {
				return false;
			}

			return true;
		}

		public static final String DATA_CHECK_COD_SERVIZIO = "18/12/2018";
		public static final String DATA_CHECK_COD_SERVIZIO_2020 = "11/02/2020";

		public static final String COD_SERVIZIO_URI = "URI";
		public static final String STR_AZIONE_B2_1 = "B2.1 - Laboratorio competenze ricerca attiva di lavoro";
		public static final String COD_SERVIZIO_NGG = "NGG";
	}

	public static abstract class StatoOccupazionale {
		public static final int ERRORE_GENERICO = 34000;
		public static final int MOV_SENZA_STATO_OCC = 34001;
		public static final int MOV_CESSATO_NON_TROVATO = 34002;
		public static final int MOV_PROROGATO_NON_TROVATO = 34003;
		public static final int MOV_TRASFORMATO_NON_TROVATO = 34004;
		public static final int ERRORE_GENERATO_MOV_NON_PREVISTO = 34005;
		public static final int MOV_SENZA_RETRIBUZIONE = 34006;
		public static final int ASSOCIATO_A_DID = 34007;
		public static final int ASSOCIATO_A_PATTO = 34008;
		public static final int MOVIMENTO_PREC_NORMATIVA = 34009;
		public static final int GESTIONE_FUTURA = 34010;
		public static final int CESSAZIONE_IN_DATA_ODIERNA = 34011;
		public static final int STATO_NON_COMPATIBILE_COL_MOVIMENTO = 34012;
		public static final int CHIUSURA_DID_PATTO = 34013;
		public static final int REDDITO_SUPERIORE_OP_SANARE = 34014;
		public static final int SITUAZIONE_SANATA = 34015;
		public static final int REDDITO_INFERIORE_OP_SANARE = 34016;
		public static final int PROROGA_NON_COLLEGATA = 34017;
		public static final int FALLITO_CAMBIO_STATO_OCCUPAZIONALE = 34018;
		public static final int STATO_SOSPESO_PER_CONTRAZIONE_NON_GENERATO = 34019;
		public static final int TIROCINIO_DURANTE_ATTIVITA_LAVORATIVA = 34020;
		public static final int STATO_OCCUPAZIONALE_MANUALE = 34021;
		public static final int STATO_OCC_CON_MESI_SOSP_ANZ_NON_CALCOLATI = 34022;
		public static final int STATO_OCC_CON_DATA_ANZIANITA_ERRATA = 34023;
		public static final int STATO_OCC_PREC_NORMATIVA_297 = 34024;
		public static final int ERRORE_INS_STATO_OCC = 34025;
		public static final int ERRORE_AGG_DATA_FINE_MOV_PREC = 34026;
		public static final int CONSERVA_STATO_OCCUPAZIONALE_MANUALE = 34027;
		public static final int DATA_ST_OCCUPAZ_MANUALE_UGUALE_EVENTO = 34028;
		public static final int DECADUTA_MOBILITA_MANUALE = 34029;
		public static final int INSERISCI_STATO_OCC_PREC_NORMATIVA_297 = 34030;
		public static final int INSERT_CALCOLA_STATO_OCC_PREC_NORMATIVA_297 = 34031;
		public static final int STATO_OCC_PREC_DATA_NORMATIVA_297 = 34032;
		public static final int MOVIMENTO_PREC_DATA_NORMATIVA = 34033;
		public static final int ESEGUITO_RICALCOLO = 34034;
	}

	public static abstract class DID {
		public static final int REDDITO_SUPERIORE_LIMITE = 35000;
		public static final int TERMINI_DID_NON_SCADUTI = 35001;
		public static final int CONTROLLI_INSERIMENTO_NON_SUPERATI = 35002;
		public static final int LAVORATORE_OCCUPATO = 35003;
		public static final int ESISTE_DID_FUTURA = 35004;
		public static final int LAVORATORE_GIA_DISOCCUPATO = 35005;
		public static final int CHIUSURA_NON_POSSIBILE = 35006;
		public static final int CHIUSURA_AVVENUTA = 35007;
		public static final int OCCUPATO_SENZA_MOVIMENTI = 35008;
		public static final int STATO_OCCUPAZIONALE_NON_CONGRUENTE = 35009;
		public static final int DATA_DID_PRECEDENTE_AL_30012003 = 35010;
		public static final int ESISTE_PERIODO_MOBILITA = 35011;
		public static final int RIAPERTURA_AVVENUTA = 35012;
		public static final int MOTIVO_RIAPERTURA_OBBLIGATORIO = 35013;
		public static final int DID_GIA_CHIUSA_NON_PER_AVVIAMENTO = 35014;
		public static final int ANNULLADID_CHIUSURADID_NON_POSSIBILE = 35015;
		public static final int CHIUSURA_DID_DA_PR_A_PA_NON_AMMESSA = 35016;
		public static final int ESISTONO_MOVIMENTI = 35017;
		public static final int NUOVA_DID_VA18_OK = 35018;
		public static final int NUOVA_DID_VA18_KO = 35019;
		public static final int ERR_MOV_RISCHIO_DISOCCUPAZIONE = 35020;
		public static final int DATA_DID_PRECEDENTE_NORMATIVA_297 = 35021;
	}

	public static abstract class Mobilita {
		public static final int USCITA_MOBILITA = 38000;
		public static final int MOVIMENTO_NON_COMPATIBILE_CON_MOBILITA = 38001;
		public static final int MOBILITA_PREC_297 = 38002;
		public static final int CANCELLA_ASSOCIAZIONE_MOVIMENTO_ANNULLATO = 38003;
		public static final int CANCELLA_ASSOCIAZIONE_MOVIMENTO_RETTIFICATO = 38004;
		public static final int ERR_PERIODO_LAVORATIVO_MOBILITA = 38005;
		public static final int ERR_DATA_INIZIO_MOBILITA_FUTURA = 38006;
		public static final int ERR_DATA_INIZIO_MOBILITA_ERRATA = 38007;
		public static final int ERR_INTERSEZIONE_MOBILITA = 38008;
		public static final int ERR_DATFINEMOBORIG_DATINIZIO = 38009;
		public static final int ERR_DATFINEMOB_DATINIZIO = 38010;
		public static final int ERR_DATAMAXDIFF_DATFINEMOB = 38012;
		public static final int WARNING_RIAPRI_SOCC_MANUALE = 38013;
		public static final int ERR_DATAMAXDIFF_DATFINEINDENNITA = 38014;
		public static final int MOBILITA_PREC_DATA_297 = 38015;
	}

	public static abstract class InfoMovimento {
		public static final int DURATA_MESI_MOVIMENTO = 27000;
		public static final int DURATA_MESI_MOVIMENTO_PRIMO_ANNO = 27001;
		public static final int REDDITO_MOVIMENTO_PRIMO_ANNO = 27002;
		public static final int REDDITO_MOVIMENTO_ANNO_SUCC = 27003;
		public static final int REDDITO_MOVIMENTO_NULLO = 27004;
		public static final int ERR_DURATA_MIN_TD = 27005;
		public static final int ERR_DATA_FINE = 27006;
	}

	public static abstract class Privacy {
		public static final int INSERT_FAIL = 36000;
	}

	public static abstract class ElencoAnagrafico {
		public static final int INSERT_FAIL = 37000;
	}

	public static abstract class EventoAmministrativo {
		public static final int SEARCH_ERROR = 39000;
	}

	public static abstract class ImportMov {
		public static final int ERR_FORMATO_FILE = 50000;
		public static final int ERR_NOME_FILE = 50001;
		public static final int ERR_NULL_RECORD = 50002;
		public static final int WAR_COD_CONTR_NULL = 50003;
		public static final int WAR_TIPO_MOV_NOVALORIZ = 50004;
		public static final int WAR_TIPO_MOV_NOCODIF = 50005;
		public static final int WAR_INIZIO_AVV = 50006;
		public static final int WAR_FINE_AVV_DET = 50007;
		public static final int WAR_INIZIO_TRASF = 50008;
		public static final int WAR_COD_TEMPO_AVV = 50009;
		public static final int WAR_INIZIO_PRO = 50010;
		public static final int WAR_COD_TEMPO_TRASF = 50011;
		public static final int WAR_FINE_PRO = 50012;
		public static final int WAR_COD_TEMPO_CES = 50013;
		public static final int WAR_DATA_CES = 50014;
		public static final int WAR_COD_MANS = 50015;
		public static final int WAR_COD_CCNL = 50016;
		public static final int WAR_NUM_LIV = 50017;
		public static final int WAR_COD_GRADO = 50018;
		public static final int WAR_COD_ORARIO = 50019;
		public static final int WAR_NUM_ORESETT = 50020;
		public static final int WAR_TIPO_ASS_NOVALORIZ = 50021;
		public static final int WAR_NO_CFLAV = 50022;
		public static final int WAR_NO_CFAZ = 50023;
		public static final int WAR_NO_INDIRAZ = 50024;
		public static final int IDMOV = 50025;

		public static final int WAR_LAV_PROV_CIGS = 50026;
		public static final int WAR_LAV_SOSP = 50027;
		public static final int WAR_FINE_TRA_DET = 50028;
		public static final int ERR_TIPO_TEMPO_MOVIMENTO = 50029;

		public static final int ERR_MATRICOLA_AVV = 50100;
		public static final int ERR_ORE_SETT_AVV = 50101;
		public static final int ERR_GRADO_AVV = 50102;
		public static final int ERR_SCADENZA_AVV = 50103;
		public static final int ERR_DATACESSAZIONE_AVV = 50104;
		public static final int WAR_VISITA = 50105;
		public static final int ERR_MOTIVO_CESSAZIONE = 50106;
		public static final int ERR_DATAFINE_TRASFPRO = 50107;
		public static final int ERR_NUM_ISCR_ALBO = 50108;
		public static final int ERR_DATA_INIZIO_PRO = 50109;
		public static final int ERR_DATA_INIZIO_TRA = 50110;
		public static final int ERR_INTERINALE = 50111;
		public static final int ERR_DOMICILIO = 50112;
		public static final int WAR_OBBL_NORMATIVA = 50113;
		public static final int ERR_AZ_UTILIZ = 50114;
		public static final int WAR_GRUPPI_SEC_LIV = 50115;
		public static final int WAR_NUM_INPS_NOVALORIZ = 50116;
		public static final int WAR_NUM_INAIL_NOVALORIZ = 50117;
		public static final int WAR_NUM_INPS_ERRATO = 50118;
		public static final int WAR_NUM_INAIL_ERRATO = 50119;
		public static final int ERR_DATI_VALIDA = 50120;
		public static final int ERR_GLOBALE = 50121;
		public static final int ERR_NO_DATA_FINEAVV = 50122;
		public static final int ERR_DATA_FINE_PRO = 50123;
		public static final int ERR_NO_DATA_INIZIOAVV = 50124;
		public static final int ERR_EFFETTUA_PRO = 50125;
		public static final int ERR_PRO_DET = 50126;
		public static final int ERR_PRO_INT = 50127;
		public static final int ERR_DATA_CESSAZIONE_DET = 50128;
		public static final int ERR_DATA_CESSAZIONE_IND = 50129;
		public static final int WAR_ORESETT_TDPT_AVV = 50130;
		public static final int ERR_REC_DATI_DB = 50131;
		public static final int ERR_MANS_TRA = 50132;
		public static final int ERR_DOPPIO_MOV = 50133;
		public static final int ERR_NO_MODIFY_DATAFINE_TRA = 50134;
		public static final int ERR_MOV_DICH = 50135;
		public static final int WAR_PIU_LAV_APERTI = 50136;
		public static final int WAR_DATA_COMUNICAZ_PRECEDENTE = 50137;
		public static final int ERR_DATA_COMUNICAZ_FUTURA = 50138;
		public static final int WAR_NUMGG_SUP = 50139;
		public static final int ERR_INSERT_DOC = 50140;
		public static final int ERR_TIPOCFL = 50141;
		public static final int ERR_ORESETT = 50142;
		public static final int ERR_NODATAAUT = 50143;
		public static final int ERR_NONUMEROAUT = 50144;
		public static final int WAR_MINORENNE = 50145;
		public static final int ERR_INSERT_APPRENDISTATO = 50146;
		public static final int ERR_FINE_PREC_INIZIO = 50147;
		public static final int ERR_FINE_CESS_PREC_INIZIO = 50148;
		public static final int ERR_BENE_ISCR_I_CLS = 50149;
		public static final int ERR_BENE_LAV_APP = 50150;
		public static final int WAR_BENE_CFL = 50151;
		public static final int ERR_BENE_LSU = 50152;
		public static final int ERR_BENE_MOB = 50153;
		public static final int ERR_AVV_COLL = 50154;
		public static final int ERR_UNITA_GIA_PRESENTE = 50155;
		public static final int ERR_NO_DATA_INIZIOMOVPREC = 50156;
		public static final int ERR_NO_DATA_FINEMOVPREC = 50157;
		public static final int ERR_IPOSSIBILE_ANNULLARE_MOV = 50158;
		public static final int ERR_NO_COGNOME_APP = 50159;
		public static final int ERR_NO_NOME_APP = 50160;
		public static final int ERR_NO_CF_APP = 50161;
		public static final int ERR_NO_NUMMESI_APP = 50162;
		public static final int ERR_TEMPO_PIENO = 50163;
		public static final int ERR_TEMPOPARZ_ESISTENTE = 50164;
		public static final int WAR_NO_COMPETENZA_LAV = 50165;
		public static final int WAR_CASO_NON_GESTITO = 50166;
		public static final int ERR_NO_PERMESSI_LAV_AZ = 50167;
		public static final int WAR_MOVIMENTO_SIMILE = 50168;
		public static final int WAR_MOVIMENTO_SIMILE_VALIDAZIONE = 50169;
		public static final int ERR_DATA_INIZIO_TRA_DET_AFTER = 50170;
		public static final int ERR_DATA_INIZIO_TRA_IND_AFTER = 50171;
		public static final int ERR_ANNULLA_DOC = 50172;
		public static final int ERR_LAVORATORE_APP = 50173;
		public static final int WAR_PERIODO_APP = 50174;
		public static final int ERR_NMESI_APP = 50175;
		public static final int ERR_LAV_NO_TITOLO = 50176;
		public static final int ERR_CAMPI_APP = 50177;
		public static final int ERR_INSERT_TIROCINIO = 50178;
		public static final int ERR_DATA_INIZIO_TRA_IND_AFTER_FORZAVAL = 50179;
		public static final int ERR_NO_CAT_ASS_OBBL = 50180;

		// FASE 2
		public static final int ERR_LAVORAZIONE_NUMGGAGR_NULL = 50181;
		public static final int ERR_AZISOMM_AZIUT_DATMIS = 50182;
		public static final int ERR_DATFINE_MISSIONE = 50183;
		public static final int ERR_CCNL_LIVELLO_COMPENSO = 50184;
		public static final int ERR_TEMPO_PAR_NUM_ORE = 50185;
		public static final int ERR_LAV_NUMGGAGREFF_NULL = 50186;
		public static final int ERR_CONTRATTO_INSERIMENTO = 50187;
		public static final int WAR_MOVIMENTO_SIMILE_CESSAZIONE_SC = 50188;
		public static final int ERR_TRASF_DA_DET_A_IND = 50189;
		public static final int ERR_TRASF_DA_PARZ_A_PIENO = 50190;
		public static final int ERR_TRASF_NT = 50191;
		public static final int ERR_CESS_ORFANA_O_VELOCE = 50192;
		public static final int ERR_TEMPO_MOV_PREC = 50193;
		public static final int ERR_AZ_UTILIZ_ENTE = 50194;
		public static final int ERR_NUM_DAT_CONV_ENTE = 50195;

		// FASE 3
		public static final int WAR_NEW_MISSIONE = 50196;
		public static final int ERR_NOT_PREC_DATFINE = 50197;
		public static final int ERR_PREC_DATFINE = 50198;
		public static final int WAR_CAMPO_LAVORAZIONE = 50199;
		public static final int ERR_CONTRATTO_AGR_NUMGG = 50200;
		public static final int ERR_FLAG_LAVAGR = 50201;
		public static final int ERR_DIST_PERSONALE_INT = 50202;

		// DECRETO 30/04/2011
		public static final int ERR_BENE_LAV_APP_ISTRUZ_FORMAZ = 50203;
		public static final int WAR_BENE_LAV_APP_ISTRUZ_FORMAZ = 50204;

		public static final int ERR_RICERCA_LAV = 50301;
		public static final int ERR_RICERCA_AZ = 50302;
		public static final int ERR_RICERCA_UNITA = 50303;
		public static final int ERR_INSERT_LAV = 50304;
		public static final int ERR_INSERT_AZ = 50305;
		public static final int ERR_INSERT_UNITA = 50306;
		public static final int VALIDAZIONE_SUCCESS = 50307;
		public static final int VALIDAZIONE_FAIL = 50308;
		public static final int ERR_DELETE_MOV_APP = 50309;
		public static final int ERR_RICERCA_SEDE = 50310;
		public static final int ERR_INSERT_SEDE = 50311;
		public static final int ERR_INSERT_DATA = 50312;
		public static final int AGGIUNTA_AZ = 50313;
		public static final int AGGIUNTA_UAZ = 50314;
		public static final int AGGIUNTO_LAV = 50315;
		public static final int WAR_FIND_COMDOMLAV = 50316;
		public static final int WAR_CODCPILAV = 50317;
		public static final int ERR_UPDATE_DATA = 50319;

		public static final int ERR_INS_SCOLLEGATO = 50320;
		public static final int ERR_INS_SCOLL_NON_TI = 50321;
		public static final int ERR_INS_SCOLL_PRO = 50322;
		public static final int ERR_QUERY_INS_SCOLL = 50323;

		public static final int ERR_FIND_CODUAZCOM = 50324;
		public static final int WAR_FIND_CPIUAZ = 50325;
		public static final int ERR_QUAL_MOV = 50326;
		public static final int ERR_FIND_MOV_PREC = 50327;

		public static final int ERR_FIND_MOV_SUCC = 50328;
		public static final int WAR_SUCC_COMP = 50329;
		public static final int ERR_MOV_PREC_TIROCINIO = 50330;
		public static final int WAR_LAVORO_AUTONOMO = 50331;
		public static final int ERR_LAVORO_TIROCINIO = 50332;
		public static final int ERR_LAVORO_APPRENDISTATO = 50333;
		public static final int ERR_LIVELLO_NULLO = 50334;

		public static final int ERR_INSERT_AVV_CEV = 50340;

		public static final int ERR_CALCOLO_MESI_LAVORO = 50350;
		public static final int WAR_DURATA_MESI_LAVORO = 50351;
		public static final int ERR_RICHIESTA_CONFERMA_MESI_LAVORO = 50352;

		public static final int WAR_ETA_LAV_APPRENDISTA = 50353;
		public static final int WAR_SEDE_NON_COINCIDENTE = 50354;

		public static final int WAR_STESSO_GRADO = 50355;

		public static final int ERR_CODMANSIONE_NULL = 50357;
		public static final int ERR_CODMANSIONE_INESISTENTE = 50358;
		public static final int WAR_MOV_DOPPIO_AGGIORNATO = 50359;
		public static final int ERR_CATEGORIA_NULL = 50360;
		public static final int ERR_CATEGORIA_INESISTENTE = 50361;
		public static final int ERR_LAVORAZIONE_NULL = 50362;
		public static final int ERR_LAVORAZIONE_INESISTENTE = 50363;
		public static final int ERR_CODLIVELLOAGR_NULL = 50364;
		public static final int ERR_CODLIVELLOAGR_INESITENTE = 50365;
		public static final int ERR_CODLIVELLOAGR = 50366;
		public static final int ERR_VERSIONE_TRACCIATO = 50367;

		public static final int WAR_MOV_PRIMA_UA = 50368;
		public static final int WAR_MOV_PRIMA_UA_UTIL = 50369;
		public static final int ERR_TIPO_ASS_INCOMPATIBILE = 50370;

		public static final int ERR_CODQUALIFICASRQ_ASSENTE = 50371;
		public static final int ERR_CODNORMATIVA_ASSENTE = 50372;
		public static final int ERR_INCENTIVI_ART_13 = 50373;

		public static final int WAR_MOV_PRIMA_UA_INDIRIZZO = 50374;
		public static final int WAR_MOV_PRIMA_UA_UTIL_INDIRIZZO = 50375;
		public static final int ERR_CODCOMUNICAZ_NOT_FOUND = 50376;
		public static final int ERR_MOV_DOPPI_CODCOMUNICAZ = 50377;
		public static final int WAR_CODCOMUNICAZ_NOT_FOUND = 50378;
		public static final int WAR_MOV_APP_CODCOMUNICAZPREC_CANC = 50379;
		public static final int WAR_MOV_APP_CODCOMUNICAZ_CANC = 50380;
		public static final int ERR_ANNULLAMENTO_COMUNICAZ_PREC = 50381;
		public static final int WAR_ANNULLAMENTO_COMUNICAZ_PREC = 50382;
		public static final int WAR_VALORE_NON_CORRETTO = 50383;
		public static final int ERR_ANNULLAMENTO_COMUNICAZ_PREC_SANATA = 50384;
		public static final int ERR_RETTIFICA_COMUNICAZ_PREC_SANATA = 50385;
		public static final int WAR_CODORARIO_NULL = 50386;
		public static final int WAR_RETTIFICA_COMUNICAZ_PREC = 50387;
		public static final int ERR_RETTIFICA_COMUNICAZ_PREC = 50388;

		// fase 2
		public static final int ERR_TIPO_CONTRATTO_INCOMPATIBILE = 50389;
		public static final int ERR_DATAVISITAMEDICA_ASSENTE = 50390;
		public static final int WAR_CCNL_LIVELLO_COMPENSO_NULL = 50391;

		public static final int ERR_TRASFORMAZIONI_LAV_AUTONOMO = 50392;
		public static final int ERR_TRASFORMAZIONI_ASS_PARTECIPAZIONE = 50393;

		public static final int WAR_CANC_RETTIFICA_NO_COMPETENZA = 50394;

		public static final int ERR_MOTIVO_CES_APPRENDISTATO = 50395;
		public static final int WAR_RETTIFICA_CONTRATTO_APPRENDISTATO = 50396;

		public static final int WAR_STRCODFISCPROMOTORETIR_ASSENTE = 50397;

		public static final int ERR_DATA_FINE_PRO_INIZIO_MIS = 50398;
		public static final int ERR_DATA_FINE_PRO_FINE_CONTRATTO_SOMM = 50399;
		public static final int ERR_TIPO_TRASFORMAZIONE_PA = 50400;
		public static final int ERR_DATA_INIZIO_TRA_INIZIO_MIS = 50401;
		public static final int ERR_DATA_INIZIO_TRA_FINE_CONTRATTO_SOMM = 50402;
		public static final int ERR_DATA_CESSAZIONE_INIZIO_MIS = 50403;
		public static final int ERR_DATA_CESSAZIONE_FINE_CONTRATTO_SOMM = 50404;
		public static final int ERR_PERIODO_MISSIONE_RAPPORTO = 50405;
		public static final int ERR_PERIODO_MISSIONE_RAPPORTO_SOMM_UTILIZ = 50406;

		public static final int ERR_NO_CAT_LAV_ASS_OBBL = 50407;
		public static final int ERR_CAT_LAV_ASS_OBBL_UNISOMM = 50408;
		public static final int ERR_CAT_LAV_ASS_OBBL_ASSENTE_VECCHIO_TRACCIATO = 50409;
		public static final int ERR_TRASFORMAZIONE_APPRENDISTATO_PROFESSIONALIZZANTE = 50410;

		public static final int ERR_GESTIONE_DATE_PERIODI_INTERMITTENTI = 50411;
		public static final int ERR_GESTIONE_PERIODI_INTERMITTENTI = 50412;
		public static final int WAR_ANN_RIALLINEAMENTO_PERIODI_INTERMITTENTI = 50413;
		public static final int WAR_RETT_RIALLINEAMENTO_PERIODI_INTERMITTENTI = 50414;
		public static final int ERR_RIALLINEAMENTO_PERIODI_INTERMITTENTI = 50415;
		public static final int WAR_RIALLINEAMENTO_PERIODI_INTERMITTENTI = 50416;
		public static final int ERR_VALIDAZIONE_CATENE_MOVIMENTI = 50417;
		public static final int WAR_CHECK_CATENE_MOVIMENTI = 50418;
	}

	public static abstract class Coop {
		public static final int ERR_CONNESSIONE_IR = 60000;
		public static final int ERR_TROPPI_RISULTATI = 60001;
		public static final int ERR_INSERIMENTO_IR = 60002;
		public static final int INSERIMENTO_IR_SUCCESS = 60003;
		public static final int ERR_IR_LAV_NOTFOUND = 60010;
		public static final int ERR_MODIFICA_CPI_IR = 60011;
		public static final int ERR_AGGIORNA_ANAGRAFICA_LAV_IR = 60012;
		public static final int ERR_MODIFICA_CF_IR = 60013;
		public static final int RICH_PRESA_ATTO_SUCCESS = 60014;
		public static final int ERR_RICH_PRESA_ATTO = 60015;
		public static final int ERR_SCHEDA_LAVORATORE = 60016;
		public static final int ERR_SCHEDA_LAVORATORE_LAV_NOT_FOUND = 60017;
		public static final int ERR_SCHEDA_LAVORATORE_XML_MALFORMATO = 60018;
		public static final int ERR_CPI_MASTER_IR = 60020;
		public static final int ERR_RESPONSE_XML_MALFORMATO = 60021;
		public static final int NOTIFICA_TRASFERIMENTO_LAV = 60023;
		public static final int CHIAMATA_A_SE_STESSI = 60024;
		public static final int PRESA_ATTO_CHIUSA_RICHIESTA = 60025;
		public static final int ERR_VERSIONING = 60026;
		public static final int RURER_ERR_CHIUSURA = 60027;
		public static final int RURER_WAR_DATI_ASSENTI = 60028;
		public static final int RURER_ERR_CONNESSIONE = 60029;
		public static final int RURER_ERR_AGGIORNAMENTO_FALLITO = 60030;
	}

	public static abstract class YG {
		public static final int ERR_CONNESSIONE_YG = 61000;
		public static final int ERR_EXEC_WS_YG = 61001;
		public static final int ADESIONE_PRESENTE_YG = 61002;
		public static final int ADESIONE_ASSENTE_YG = 61003;

		public static final int ERR_EXEC_WS_VERIFICASAP = 61004;
		public static final int ERR_EXEC_WS_RICHIESTASAP = 61005;
		public static final int ERR_EXEC_WS_INVIOSAP = 61006;

		public static final int WS_VERIFICASAP_NON_TROVATO = 61007;
		public static final int WS_VERIFICASAP_TROVATO_NO_MODIFICA = 61008;
		public static final int WS_VERIFICASAP_TROVATO_MODIFICA = 61009;
		public static final int ERR_WS_VERIFICASAP = 61010;
		public static final int ERR_EXEC_WS_ANNULLASAP = 61011;
		public static final int WS_ANNULLASAP_OK = 61012;
		public static final int ERR_SAP_NO_POLITICHEATTIVE = 61013;
		public static final int ERR_LAVORATORE_NO_COMPENTE = 61014;
		public static final int ERR_SAP_ANOMALIE = 61015;

		public static final int ERR_WS_PARTECIPANTE_COND_OBB = 61016;
		public static final int ERR_WS_PARTECIPANTE_GG_DATI_LAVORATORE = 61017;
		public static final int WS_PARTECIPANTE_OK = 61018;
		public static final int ERR_WS_PARTECIPANTE_GG_INPUT = 61019;
		public static final int ERR_WS_PARTECIPANTE_GG_OUTPUT = 61020;
		public static final int ERR_EXEC_WS_DATADESIONE_YG = 61021;
		public static final int WS_DATADESIONE_REG_YG_NON_TROVATA = 61022;
		public static final int WS_DATADESIONE_SOG_YG_NON_TROVATA = 61023;
		public static final int ERR_WS_PARTECIPANTE_GG_NO_POLITICHE_ATTIVE = 61024;
		public static final int ERR_WS_PARTECIPANTE_GG_POLITICA_ATTIVA = 61025;
		public static final int ERR_WS_PARTECIPANTE_GG_EMAIL = 61026;
		public static final int ERR_WS_PARTECIPANTE_GG_POLITICHE_ATTIVE_SOGGACC = 61027;
		public static final int ERR_KO_WS_PARTECIPANTE_GG_OUTPUT = 61028;
		public static final int ERR_WS_PARTECIPANTE_GG_INDICE_SVAN = 61029;
		public static final int ERR_WS_STATO_ADESIONE_YG = 61031;
		public static final int CONFIRM_OPERAZIONE_CRUSCOTTO_ADESIONE_YG = 61032;
		public static final int ERR_WS_STATO_ADESIONE_YG_MINISTERO = 61033;
		public static final int ERR_WS_PARTECIPANTE_GG_DATA_ADESIONE_DIVERSE = 61035;
		public static final int ERR_WS_PARTECIPANTE_GG_DATA_ADESIONE_NULLA = 61036;
		public static final int ERR_WS_PARTECIPANTE_GG_DATA_ADESIONE_GENERICO = 61037;
		public static final int ERR_WS_PARTECIPANTE_GG_NO_PATTI = 61038;
		public static final int ERR_WS_PARTECIPANTE_GG_RER_SOTTOAZIONE = 61039;

		public static final int ERR_WS_PARTECIPANTE_GG_INDICE_SVAN_NEW = 61040;
		public static final int ERR_WS_PARTECIPANTE_GG_INDICE_SVAN_OLD = 61041;

		public static final int ERR_WS_POLITICA_ATTIVA_MULTIPLA_MYSTAGE = 61042;

		public static final int ERR_WS_PARTECIPANTE_NO_PATTOGG_A2R = 61043;
		public static final int ERR_WS_POLITICA_ATTIVA_MYSTAGE_NO_DATADESIONE = 61044;

		public static final int ERR_WS_VERIFICASAP_PORTALE_FUNZ_NON_ATTIVA = 61045;
		public static final int ERR_WS_VERIFICASAP_PORTALE_SERVIZIO_NON_ATTIVO = 61046;
		public static final int ERR_WS_VERIFICASAP_PORTALE_DA_DETTAGLIARE = 61047;
		public static final int ERR_WS_PARTECIPANTE_GG_DATA_ADESIONE_PA_NULLA = 61048;
		public static final int ERR_WS_PARTECIPANTE_GG_NO_PATTI_UMBRIA = 61049;
		public static final int ERR_WS_PARTECIPANTE_GG_AZIONI_GU_NON_ASSOCIATE = 61050;
		public static final int ERR_WS_PARTECIPANTE_GG_AZIONI_C06_PRO_AVV = 61051;
		public static final int WS_VERIFICASAP_RISPOSTA_MINISTERO = 61052;
		public static final int ERR_LIMITE_INVOCAZIONE_SUPERATO = 61053;
		public static final int ERR_WS_PARTECIPANTE_GG_AZIONI_C02_PRO_AVV = 61054;
		public static final int ERR_WS_PARTECIPANTE_GG_AZIONI_C05_PRO_AVV = 61055;
		public static final int ERR_WS_PARTECIPANTE_GG_AZIONI_C07_PRO_AVV = 61056;
		public static final int ERR_WS_PARTECIPANTE_GG_NO_PATTI_PROGRAMMA = 61057;
		public static final int ERR_WS_PARTECIPANTE_GG_DATA_ADESIONE_PROGRAMMA_NULLA = 61058;
		public static final int ERR_WS_PARTECIPANTE_GG_DATA_ADESIONE_PROGRAMMA_DIVERSE = 61059;
		public static final int ERR_WS_PARTECIPANTE_NO_PATTO_PROGRAMMA_GG_A2R = 61060;
	}

	public static abstract class ImportaSAP {
		public static final int INSERT_LAV_OK = 62000;
		public static final int INSERT_LAV_FAIL = 62001;
		public static final int INSERT_TITOLO_STUDIO_LAV_OK = 62002;
		public static final int INSERT_TITOLO_STUDIO_LAV_FAIL = 62003;
		public static final int INSERT_FOR_PRO_LAV_OK = 62004;
		public static final int INSERT_FOR_PRO_LAV_FAIL = 62005;
		public static final int INSERT_LINGUA_LAV_OK = 62006;
		public static final int INSERT_LINGUA_LAV_FAIL = 62007;
		public static final int INSERT_CON_INFO_LAV_OK = 62008;
		public static final int INSERT_CON_INFO_LAV_FAIL = 62009;
		public static final int INSERT_PATENTE_LAV_OK = 62010;
		public static final int INSERT_PATENTE_LAV_FAIL = 62011;
		public static final int INSERT_PATENTE_LAV_FAIL_NO_MAP = 62012;
		public static final int INSERT_PATENTINO_LAV_OK = 62013;
		public static final int INSERT_PATENTINO_LAV_FAIL = 62014;
		public static final int INSERT_PATENTINO_LAV_FAIL_NO_MAP = 62015;
		public static final int INSERT_ALBO_LAV_OK = 62016;
		public static final int INSERT_ALBO_LAV_FAIL = 62017;
		public static final int INSERT_ALBO_LAV_FAIL_NO_MAP = 62018;
		public static final int INSERT_ESPLAV_LAV_OK = 62019;
		public static final int INSERT_DIS_MANSIONE_LAV_OK = 62020;
		public static final int INSERT_DIS_ORARIO_LAV_OK = 62021;
		public static final int INSERT_DIS_ORARIO_LAV_FAIL = 62022;
		public static final int INSERT_DIS_ORARIO_LAV_FAIL_NO_MAP = 62023;
		public static final int INSERT_DIS_TURNO_LAV_OK = 62024;
		public static final int INSERT_DIS_TURNO_LAV_FAIL = 62025;
		public static final int INSERT_DIS_COMUNE_LAV_OK = 62026;
		public static final int INSERT_DIS_COMUNE_LAV_FAIL = 62027;
		public static final int INSERT_DIS_PROVINCIA_LAV_OK = 62028;
		public static final int INSERT_DIS_PROVINCIA_LAV_FAIL = 62029;
		public static final int INSERT_DIS_REGIONE_LAV_OK = 62030;
		public static final int INSERT_DIS_REGIONE_LAV_FAIL = 62031;
		public static final int INSERT_DIS_STATO_LAV_OK = 62032;
		public static final int INSERT_DIS_STATO_LAV_FAIL = 62033;
		public static final int INSERT_DIS_MOB_GEO_LAV_OK = 62034;
		public static final int INSERT_DIS_MOB_GEO_LAV_FAIL = 62035;
		public static final int TITOLO_STUDIO_NON_SPEC_LAV_OK = 62036;
		public static final int MANSIONE_NON_SPEC_LAV_OK = 62037;
		public static final int ERR_NOME_FILE = 62038;
	}

	public static abstract class Protocollazione {
		public static final int ERR_GENERICO_NELLA_SP = 70000;
		public static final int NUM_PROT_TROPPO_GRANDE = 70001;
		public static final int NUM_PROT_TROPPO_PICCOLO = 70002;
		public static final int NUM_PROT_GIA_INSERTITO = 70003;
		public static final int ERR_DOC_DID_ESISTENTE = 70004;
		public static final int ERR_DOC_PATTO_ESISTENTE = 70005;
		public static final int DATA_PROT_NULLA = 70010;
		public static final int DATA_PROT_ERRATA = 70011;
		public static final int TIPO_PROT_NULL = 70020;
		public static final int ERR_PARAMETRO_ASSENTE = 70021;
		// errori registrazione documento docarea pantarei Ver. 1.6.1
		public static final int PANTAREI_161_ERRORE_WS = 70022;
		public static final int PANTAREI_161_ERRORE_REPORT_DA_INVIARE = 70023;
		public static final int PANTAREI_161_ERRORE_RENAME_FILE = 70024;
		public static final int PANTAREI_161_ERRORE_DOPO_PROTOCOLLO = 70025;
		// errori protocollo docarea tipo sare
		public static final int DOCAREA_ERRORE_CONNESSIONE_HOST_SCONOSCIUTO = 70026;
		public static final int DOCAREA_ERRORE_CONNESSIONE = 70027;
		public static final int DOCAREA_ERRORE_CONNESSIONE_GENERICO = 70028;
		public static final int DOCAREA_ERRORE_CONNESSIONE_CONFIGURA_URL = 70029;
		public static final int DOCAREA_ERRORE_RISPOSTA_XML = 70030;
		public static final int DOCAREA_ERRORE_RISPOSTA_ASSENTE = 70031;
		public static final int DOCAREA_ERRORE_OPERAZIONE = 70032;
		public static final int DOCAREA_ERRORE = 70033;
		public static final int DOCAREA_ERRORE_RENAME_FILE = 70034;
		public static final int ERR_STAMPA_ADR_MISURA = 70035;
	}

	public static abstract class Trasferimento {
		public static final int ERR_PIU_PATTI_APERTI = 80000;
		public static final int ERR_CLOSE_AN_LAV_S = 80001;
		public static final int ERR_CLOSE_AM_EL_ANAG = 80002;
		public static final int ERR_OPEN_AM_EL_ANAG = 80003;
		public static final int ERR_CLOSE_AM_PATTO_LAV = 80004;
		public static final int ERR_UPDATE_DOM_LAV = 80005;
		public static final int ERR_UPD_AM_DICH_DISP = 80006;
		public static final int STAMPA_TRASF_ESEGUITA = 80007;
		public static final int STAMPA_RICH_DOC_TRASF = 80008;
		public static final int STAMPA_RICH_DOC_PRESA_ATTO = 80009;
		public static final int ERR_OPEN_STATO_OCC = 80010;
		public static final int ERR_PRESA_ATTO_CHIUSURA_RICHIESTE = 80011;
		public static final int ERR_PRESA_ATTO_AUTOMATICA = 80012;
		public static final int ERRORE_INS_STATO_OCC = 80013;
		public static final int SAP_INVIATA = 80015;
		public static final int SAP_INVIATA_SOLO_TITOLARIETA = 80016;
	}

	// Messaggi sull'unita azienda
	public static abstract class UnitaAzienda {
		public static final int WAR_SEDE_LEGALE_MOD = 81000;
		public static final int ATECO_NON_VALIDO = 81001;
	}

	// Messaggio per l'anagrafica
	public static abstract class Anag {
		public static final int INSERT_FAIL_CF_ESISTENTE = 90000;
		public static final int UPDATE_FAIL_CF = 90010;
	}

	// Messaggi di accorpamento
	public static abstract class Accorpamento {
		public static final int ACCORPAMENTO_SUCCESS = 100000;
		public static final int ACCORPAMENTO_FAIL = 100010;
		public static final int ACCORPAMENTO_FAIL_UNI_ISCRIZIONE_CIG = 100020;
		public static final int ACCORPAMENTO_TESTATA_SUCCESS = 100001;
		public static final int ACCORPAMENTO_TESTATA_FAIL = 100011;
		public static final int ACCORPAMENTO_FAIL_TESTATA_ISCRIZIONE_CIG = 100021;
	}

	// Messaggi per trasferimento ramo aziendale
	public static abstract class TrasferimentoRamoAzienda {
		public static final int ERR_INSER_CES = 110000;
		public static final int ERR_UPDATE_PREC = 110001;
		public static final int ERR_INSER_AVV = 110002;
		public static final int ERR_PROT_AVV = 110003;
		public static final int ERR_PROT_CES = 110004;
		public static final int ERR_DATE_TRASF = 110005;
		public static final int ERR_DATA_TRASF = 110006;
		public static final int ERR_INSER_TRA = 110007;
		public static final int ERR_PROT_TRA = 110008;
	}

	public static abstract class Login {
		public static final int ERR_PWD_TOO_SHORT = 110100;
		public static final int ERR_PWD_WITHOUT_DIGITS = 110101;
		public static final int ERR_PWD_CASE_ALT = 110102;
		public static final int ERR_PWD_NOT_DIFFERENT = 110103;
		public static final int ERR_WRONG_OLD_PWD = 110105;
		public static final int ERR_LOGIN_IN_USE = 110106;
		public static final int ERR_PWD_NOT_DIFFERENT_STORICO = 110107;
		public static final String COD_TIPO_GRUPPO_PATRONATO = "P";
		public static final String COD_TIPO_GRUPPO_SOGGETTI_ACCREDITATI = "S";
	}

	// Messaggi per collegamento movimenti orfani
	public static abstract class CollegaMov {
		public static final int ERR_INIZIO_ORIG = 120000;
		public static final int ERR_INIZIO_DEST = 120001;
		public static final int ERR_INIZIO_DEST_PRIMA_INIZIO_ORIG = 120002;
		public static final int ERR_FINE_ORIG = 120003;
		public static final int ERR_INIZIO_DEST_DOPO_FINE_ORIG = 120004;
		public static final int ERR_COD_MONO_TEMPO_ORIG = 120005;
		public static final int ERR_UPDATE_ORIG = 120006;
		public static final int ERR_UPDATE_DEST = 120007;
		public static final int ERR_PRO_NON_COLLEGATA = 120008;
		public static final int ERR_TRASFO_TD_NON_COLLEGATA = 120009;
		public static final int ERR_TRASFO_TI_NON_COLLEGATA_DOPO_297 = 120010;
		public static final int ERR_TRA_PRO_TD = 120011;
		public static final int WARNING_PRO_NON_COLLEGATA = 120012;
		public static final int WARNING_TRASFO_TD_NON_COLLEGATA = 120013;
		public static final int ERR_CES_NO_SUCCESSIVO = 120014;
	}

	/** Messaggi per l'esportazione di migrazioni */
	public static abstract class EsportaMigrazioni {
		public static final int ERR_GIA_FATTO_IN_DATA = 123001;
		public static final int ERR_GIA_IN_CORSO = 123002;
		public static final int ERR_CANT_GET_STATUS = 123003;
		public static final int ERR_CANT_UPDATE_STATUS = 123004;
		public static final int ERR_CANT_INITIALIZE = 123005;
		public static final int ERR_NON_ANCORA_INVIATI = 123007;
		public static final int ERR_PATH_NOT_DEFINED = 123011;
		public static final int ERR_PATH_NOT_EMPTY = 123012;
		public static final int ERR_NO_NEXT_CPI = 123013;
		public static final int ERR_DURANTE_INIZIO = 123015;
		public static final int ERR_DURANTE_ESPORTA_CPI_F0 = 123017;
		public static final int ERR_DURANTE_ESPORTA_CPI_F1 = 123018;
		public static final int ERR_DURANTE_ESPORTA_CPI_F2 = 123019;
		public static final int ERR_SETINVIO_FLGINCORSO = 123021;
		public static final int ERR_SETINVIO_PATH_NOT_EMPTY = 123023;
		public static final int ERR_SETINVIO_PATH_EMPTY = 123024;
		public static final int ERR_SETINVIO_CONTROLLO = 123026;
		public static final int ERR_SETINVIO_CANTDOIT = 123028;
		public static final int ERR_SETINVIO_NULLA_DA_FARE = 123029;
		public static final int OK_ANNULLA_FATTO = 123031;
		public static final int ERR_ANNULLA_NONINCORSO = 123033;
		public static final int ERR_ANNULLA_ENTRO_TIMEOUT = 123035;
		public static final int ERR_ANNULLA_CANTDELRECORD = 123037;
		public static final int ERR_ANNULLA_CANTDELFILES = 123038;
		public static final int ERR_ANNULLA_NONINVIATO = 123039;

		public static final int ERR_INVMAIL_MIGRAZ_INCORSO = 123041;
		public static final int ERR_INVMAIL_GIA_INVIATA = 123042;
		public static final int ERR_DATA_FINE_GT_SYSTEM = 123043;
		public static final int ERR_DATA_FINE_LT_ULTIMA = 123044;

		public static final int ERR_INVIO_MIGRAZ_IN_COOP = 123100;
		public static final int ERR_CHECK_SEND_IN_COOP = 123101;
		public static final int ERR_POLONONATTIVO_IN_COOP = 123102;

	}

	/**
	 * Messaggi per il log dei risultati nella validazione e inserimento movimenti
	 */
	public static abstract class LogOperazioniMovimenti {
		public static final int VALIDAZIONE_MASSIVA_IN_ATTO = 130000;
		public static final int VALIDAZIONE_MASSIVA_INTERROTTA = 130001;
		public static final int VALIDAZIONE_MASSIVA_GIA_TERMINATA = 130002;
		public static final int ERRORE_SCRITTURA_LOG = 130003;
	}

	public static abstract class DatiLavoratore {
		public static final int ETA_LT_15 = 131000;
		public static final int IN_MOBILITA = 131001;
		public static final int IN_COLLOCAMENTO_MIRATO = 131002;
		public static final int ETA_LT_15_AND_AVV_SPETTACOLO = 131003;
		public static final int ETA_LT_15_AND_AVV_SPETAC_TIROC = 131004;
		public static final int IN_MOBILITA_SOSPESO = 131005;
		public static final int ETA_LT_16 = 131006;
		public static final int ETA_LT_16_AND_AVV_SPETTACOLO = 131007;
		public static final int ETA_LT_15_GT_25 = 131008;
		public static final int ETA_LT_17_GT_29 = 131009;
		public static final int ETA_LT_18_GT_29 = 131010;
	}

	// Messaggi LOG EVIDENZE
	public static abstract class Evidenze {
		public static final int EVID_COD_UPD_FAILURE = 132000;
		public static final int EVID_DEL_FAILURE = 122001;
		public static final int EVID_DUP_COD_FAILURE = 122002;
		public static final int EVID_DUP_UPD_COD_FAILURE = 122003;
		public static final int EVID_DEL_FAILURE_VIS = 122004;
	}

	public static abstract class MailMigrazioni {
		public static final int ERR_ZIP = 51000;
		public static final int ERR_DEL = 51001;
		public static final int ERR_MKDIR = 51002;
		public static final int ERR_MV = 51003;
		public static final int ERR_MITTENTE = 51004;
		public static final int ERR_PARAMS = 51005;
		public static final int ERR_UPDATE_STATO = 51006;
	}

	public static abstract class DichSosp {
		public static final int ERR_DICH_SOSP_GIA_PRESENTE = 140000;

	}

	public static abstract class IDO {
		public static final int ERR_INS_LAV_RNG = 141000;
		public static final int ERR_INS_LAV_RNG_DEL_LOGICA = 141001;
		public static final int ERR_CRNG_NO_MANSIONE = 141002;
		public static final int ERR_CRNG_RICH_CHIUSA_TOT = 141003;
		public static final int FILTRA_ETA = 141004;
		public static final int FILTRA_NAZIONE = 141005;
		public static final int FILTRA_SESSO = 141006;
		public static final int CHECK_DATA_CHIAMATA = 141007;
		public static final int ERR_CRNG_RICH_CHIUSA = 141008;
		public static final int ERR_DPRNG_ADESIONE_NO_DEL_PROFILO = 141009;
		public static final int ERR_DPRNG_DEL_PROFILO_RICH_CHIUSA_TOT = 141010;
		public static final int ERR_DPRNG_DEL_PROFILO_RICH_CHIUSA = 141011;
		public static final int ERR_DEL_ADES_TPROSA = 141012;
		public static final int ERR_DEL_ADES_ROSA = 141013;
		public static final int ERR_UPD_RICH_ADESIONI = 141014;
		public static final int ERR_INS_STORIA_STATO_OCC = 141015;
		public static final int ERR_INS_ADESIONE_MORE_PROFILO = 141016;
		public static final int CHECK_CM_TIPO_GRAD = 141017;
		public static final int CHECK_CM_DATA_CHIAMATA = 141018;
		public static final int CHECK_CM_TIPO_LISTA = 141019;
		public static final int ERR_INS_ADESIONE_ART8 = 141020;
		public static final int ERR_INS_ADESIONE_ART18 = 141021;
		public static final int ERR_INS_ADESIONE_NO_REDDITO = 141022;
		public static final int ERR_INS_ADESIONE_NO_DISOC_INOC = 141023;
		public static final int ERR_MESI_ANZ_CALC_PUNTEGGIO = 141024;
		public static final int ERR_DATAPUBBLICAZIONE_DATACHIAMATA = 141025;
		public static final int FILTRA_ISCRIZIONI = 141026;
		public static final int FILTRA_DISABILI = 141027;
		public static final int ERR_INS_ADESIONE_NO_ESITO = 141028;
		public static final int ERR_INS_ADESIONE_ISEE = 141029;
		public static final int ERR_MAX_COMUNI = 141030;
		public static final int ERR_MAX_CPI = 141031;
		public static final int ERR_MAX_PROV = 141032;
		public static final int FILTRA_ALTRE_ISCRIZIONI = 141033;
		public static final int ERR_CHECK_VACANCY_SIL_PORTAL = 141034;
		public static final int ERR_CHECK_VACANCY_SIL_PORTAL_SCADUTA = 141035;
		public static final int ERR_CHECK_VACANCY_SIL_PORTAL_PUBB_SCADUTA = 141036;
		public static final int ERR_CHECK_VACANCY_SIL_PORTAL_MOD_EVASIONE = 141037;
		public static final int ERR_CHECK_VACANCY_SIL_PORTAL_NO_PUBBLICATA = 141038;
		public static final int ERR_CHECK_VACANCY_SIL_PORTAL_STATO_EVASIONE = 141039;
		public static final int ERR_CHECK_VACANCY_SIL_PORTAL_PUBB_WEB = 141040;
		public static final int ERR_CHECK_VACANCY_SIL_PORTAL_INVIO_MANSIONE = 141041;
		public static final int ERR_CHECK_VACANCY_SIL_PORTAL_INVIO_COMUNE = 141042;
		public static final int ERR_CHECK_VACANCY_SIL_PORTAL_MANSIONE = 141043;
		public static final int ERR_CHECK_VACANCY_SIL_PORTAL_CONTENUTO_LAVORO = 141044;
		public static final int ERR_CHECK_VACANCY_ANTEPRIMA = 141045;
		public static final int ERR_CHECK_VACANCY_PALESE_CANDIDATURA = 141046;
		public static final int ERR_CHECK_VACANCY_PALESE_DATI_AZ_REF = 141047;
		public static final int ERR_AGGIORNAMENTO_ESCLUSIONE_LOGICA = 141048;
		public static final int ERR_AGGIORNAMENTO_ESCLUSIONE_LOGICA_POSIZIONE = 141049;
		// Aste art 16 on line
		public static final int ERR_ASONLINE_SCARICO_INCORSO = 141050;
		public static final int ERR_ASONLINE_RACC_NON_TERM = 141051;
		public static final int ERR_ASONLINE_RICH_ASSENTE = 141052;
		public static final int ERR_ASONLINE_NO_CANDIDAT = 141053;
		public static final int ERR_ASONLINE_NO_AS_O_NULL = 141054;
		public static final int ERR_ASONLINE_CANDIDATI = 141055;

	}

	// Messaggi di accorpamento
	public static abstract class AccorpamentoLavoratore {
		public static final int ACCORPAMENTO_SUCCESS = 150000;
		public static final int ACCORPAMENTO_FAIL = 150001;
		public static final int ACCORPAMENTO_FAIL_CONCORRENZA = 150002;
		public static final int ACCORPAMENTO_FAIL_INTEGRITA_REF = 150003;
		public static final int ACCORPAMENTO_FAIL_LOGGING = 150004;
		public static final int ACCORPAMENTO_FAIL_CORSI = 150005;
		public static final int ACCORPAMENTO_FAIL_COLLOQUI = 150006;
		public static final int ACCORPAMENTO_FAIL_ISCRIZIONI = 150007;
		public static final int ACCORPAMENTO_WARN_WS = 150008;
		public static final int ACCORPAMENTO_FAIL_SAP = 150009;
		public static final int WAR_ACCORPAMENTO_SUCCESS_INVIA_SAP = 150010;
	}

	public static abstract class SMS {
		public static final int TESTO_TROPPO_LUNGO = 160000;
		public static final int TESTO_NULLO = 160001;
		public static final int CELL_NON_VALIDO = 160002;
		public static final int INSERT_CONTATTO_SMS_FALLITO = 160003;
		public static final int SPI_MANCANTE_SMS_FALLITO = 160004;
		public static final int MANCATO_CONSENSO_SMS = 160005;
		public static final int IMPOSSIBILE_REPERIRE_CONSENSO = 160006;
		public static final int ERRORE_INVIO_SERVER = 160007;
		public static final int ERRORE_UPDATE_AG_CONTATTO = 160008;
	}

	/** Messaggi per il log dei risultati nella validazione della mobilita */
	public static abstract class LogOperazioniValidazioneMobilita {
		public static final int INSERT_MOB_VALIDAZIONE_MASSIVA = 170000;
		public static final int UPDATE_MOB_VALIDAZIONE_MASSIVA = 170001;
		public static final int INSERT_MOVIMENTO_CESS_VELOCE_VALIDAZIONE_MASSIVA_MOBILITA = 170002;
		public static final int VALIDAZIONE_MASSIVA_INTERROTTA = 170003;
		public static final int VALIDAZIONE_MASSIVA_GIA_TERMINATA = 170004;
		public static final int VALIDAZIONE_MASSIVA_IN_ATTO = 170005;
		public static final int NUM_MAX_MOBILITA_VALIDAZIONE_SUPERATO = 170006;
		public static final int RECORD_VALIDAZIONE_NULLO = 170007;
		public static final int INSERT_MOV_ASSOCIATO_MOB_FAIL = 170008;
		public static final int FALLITA_GESTIONE_MOVIMENTO = 170009;
		public static final int TROVATE_MOBILITA_COMPATIBILI = 170010;
		public static final int FALLITA_CANCELLAZIONE_MOBILITA_APP = 170011;
		public static final int FALLITA_GESTIONE_MOBILITA = 170012;
		public static final int ERRORE_SCRITTURA_LOG = 170013;
		public static final int FALLITA_DETERMINAZIONE_MOVIMENTO_DA_COLLEGARE_MOBILITA = 170014;
		public static final int INS_MOVIMENTO_NON_PREVISTO_IN_VALIDAZIONE_MOBILITA = 170015;
		public static final int INSERT_MOVIMENTO_CESS_VALIDAZIONE_MASSIVA_MOBILITA = 170016;
		public static final int ERRORE_DATA_INIZIO_MOBILITA = 170017;
		public static final int AGGIORNATA_DATA_INIZIO_MOBILITA = 170018;
		public static final int PRESENTE_MOBILITA_STATO_VALORIZZATO = 170019;
	}

	/** Messaggi per lo scorrimento della mobilita */
	public static abstract class ScorrimentoMobilita {
		public static final int OPERAZIONE_OK = 180000;
		public static final int INTERSEZIONE_MOBILITA = 180001;
		public static final int MATERNITA = 180002;
		public static final int NO_OPERAZIONE = 180003;
		public static final int DELETE_MATERNITA = 180004;
	}

	/** Messaggi per il collocamento mirato */
	public static abstract class CollocamentoMirato {
		public static final int ERROR_DATA_ASSUNZ_FASCIA_C = 190000;
		public static final int ERROR_NO_PERC_ESONERO = 190001;
		public static final int ERROR_NO_COMPENSAZIONE_TERRITORIALE = 190002;
		public static final int ERROR_DATA_CONSEGNA_OBBLIGATORIA = 190003;
		public static final int ERROR_PROSPETTO_STORICIZ_DUPLICATO = 190004;
		public static final int ERROR_COPIA_PROSPETTO_DUPLICATO = 190005;
		public static final int ERROR_DELETE_VERBALE = 190006;
		public static final int ERROR_NO_EXIST_ISCR = 190007;
		public static final int ERROR_LAVORATORE_NON_DISOCCUPATO_INOCCUPATO = 190008;
		public static final int ERROR_DATANZIANITA_COLL_ORDINARIO_NON_PRESENTE = 190009;
		public static final int ERROR_DAT_ANZ_CM_MINORE_DAT_ANZ_COLL_ORDINARIO = 190010;
		public static final int ERROR_DAT_SOSP_CM_MINORE_DAT_ANZ_CM = 190011;
		public static final int ERROR_ADESIONE_NO_DATA_PUBBLICAZIONE = 190012;
		public static final int WARNING_NUMDISABEFFETT_MAGGIORE_NUMDISABCALC = 190013;
		public static final int ERROR_DATANZIANITA_COLL_MIRATO_NON_PRESENTE = 190014;
		public static final int ERROR_DATA_RIFERIMENTO_OBBLIGATORIA = 190015;
		public static final int ERROR_DATA_PROSPETTO_OBBLIGATORIA = 190016;
		public static final int ERROR_COERENZA_CATEGORIA_COMP_TERR = 190017;
		public static final int ERROR_COERENZA_CATEGORIA_COMP_TERR_ART18 = 190018;
		public static final int WARNING_L68_PROSPETTO_AGGIORNATO = 190019;
		public static final int ERRORE_AGG_L68_PROSPETTO_INESISTENTE = 190020;
		public static final int ERRORE_AGGIORNAMENTO_L68_PROSPETTO = 190021;
		public static final int ERRORE_AGG_L68_PROSPETTO_MOV_INESISTENTE = 190022;
		public static final int WARNING_DAT_VERB_ACC_MINORE_DAT_ODIERNA = 190023;
		public static final int ERROR_ADESIONE_NO_DATA_CHIAMATA = 190024;
		public static final int ERROR_GRAD_ANNUALE_PRESENTE = 190025;
		public static final int ERROR_PROT_PROSP_ANNO = 190026;
		public static final int WARNING_L68_CONV_LAV_PROSPETTO_AGGIORNATO = 190027;
		public static final int ERROR_BATTISTONI_17012000_ALTRA_CAT_ART18 = 190028;
		public static final int ERROR_BATTISTONI_17012000_ALTRA_CAT = 190029;
		public static final int WARNING_DISABILI_IN_FORZA = 190030;
		public static final int WARNING_CENTRALINISTI = 190031;
		public static final int WARNING_MASSOFISIOTERAPISTI = 190032;
		public static final int WARNING_DISABILI_SOMMINISTRATI = 190033;
		public static final int WARNING_DISABILI_CONVENZIONE = 190034;
		public static final int ERR_MISSIONE_TIPO_ASSUNZIONE_PROTETTA_SOMM = 190035;
		public static final int ERR_TEMPO_PARZIALE_DUPLICATO = 190036;
		public static final int ERR_NUMERAZIONE_AMBITO_TERRITORIALE = 190037;
	}

	/** Messaggi per i CIG */
	public static abstract class CIG {
		public static final int ERROR_DOMANDA_ANNULLATA = 200000;
		public static final int ERROR_DOMANDA_NON_APPROVATA = 200001;
		public static final int ERROR_DOMANDA_COMPATIBILE = 200002;
		public static final int ERROR_PERIODI_NO_COMPATIBILI = 200003;
		public static final int ERROR_RIFERIMENTO_DOMANDA_CIG = 200004;
		public static final int ERROR_INVIO_SMS_PROMEMORIA_CIG = 200005;
		public static final int ERROR_INSERIMENTO_CORSO_ORIENTER = 200006;
	}

	/** Messaggi per approvazione mobilita' */
	public static abstract class ApprovazioneMobilita {
		public static final int ERROR_CAMPI_OBBLIGATORI = 300000;
		public static final int ERROR_ENTE_APPROVAZIONE_OBBLIGATORIO = 300001;
		public static final int ERROR_ENTE_APPROVAZIONE_REG_PROV = 300002;
	}

	public static abstract class Batch {
		public static final int NUM_MOVIMENTI_DA_IMPORTARE_IN_APPOGGIO_FESTIVO = 10000;
		public static final int NUM_MOVIMENTI_DA_IMPORTARE_IN_APPOGGIO_FERIALE = 5000;
		public static final int GIORNI_TRASCORSI_DA_IMPORTAZIONE = 7;
		public static final int USER_RICICLAGGIO = 250;
		public static final int NUM_MAX_MOVIMENTI_DA_ELABORARE = 500;
		public static final int SABATO = 6;
		public static final int DOMENICA = 0;
	}

	public static abstract class Webservices {
		public static final int WS_ERRORE_RECUPERO_URL = 26001;
		public static final int WS_ERRORE_INIZIALIZZAZIONE = 26002;
		public static final int WS_ERRORE_INVOCAZIONE = 26003;
		public static final int WS_TRACCIATO_INVARIATO = 26004;
	}

	// Messaggi di accorpamento iscrizione
	public static abstract class AccorpamentoIscrizione {
		public static final int ACCORPAMENTOISCR_SUCCESS = 280000;
		public static final int ACCORPAMENTOISCR_FAIL = 280001;
		public static final int ACCORPAMENTO_ISCR_ANNULLATO = 280002;
		public static final int ACCORPAMENTO_ISCR_FAIL_LOGGING = 280003;
		public static final int ACCORPAMENTO_ISCR_FAIL_CORSI = 280004;
		public static final int ACCORPAMENTO_ISCR_FAIL_COLLOQUI = 280005;
		public static final int ACC_ISCR_NO_STESSA_CATENA = 280006;
	}

	public static abstract class ConfermaAnnualeDid {
		public static final int DICH_ESISTENTE_ANNO = 290000;
		public static final int DICH_FUTURA = 290001;
		public static final int DICH_ANNO_DID = 290002;
		public static final int DICH_PREGRESSA = 290003;
	}

	public static abstract class InvioDomandaMobilita {
		public static final int ERRORE_CREAZIONE_XML = 290004;
		public static final int INVIO_OK = 290005;
		public static final int INVIO_KO = 290006;
		public static final int ERRORE_RECUPERO_DATI_DOMANDA = 290007;
		public static final int ERRORE_CAMPO_OBBLIGATORIO = 290008;
		public static final int ERRORE_VALIDAZIONE_XML = 290009;
		public static final int ERRORE_FORMATO_CAMPO = 290010;
		public static final int ERRORE_PERMESSI_INVIO = 290011;
		public static final int ERRORE_DATA_FINE_MOBILITA = 290012;
		public static final int ERRORE_RECUPERO_ALLEGATO_INVIO_DOMANDA = 290013;
		public static final int ERRORE_ALLEGATO_ASSENTE_INVIO_DOMANDA = 290014;
	}

	public static abstract class AffittoRamoAzienda {
		public static final int WARNING_DT_FINE_AFFITTO_NULL = 400000;
		public static final int ERR_DT_FINE_AFFITTO_PREC_TRA = 400001;
		public static final int WARNING_NO_DT_FINE_AFFITTO_ = 400002;
	}

	public static abstract class ClicLavoro {
		public final static int CODE_XML_VALIDATION = 410000;
		public static final int CODE_INPUT_ERRATO = 410001;
		public static final int CODE_CAMPOOBBLIGATORIO_NOTFOUND = 410002;
		public static final int CODE_ERR_INTERNO = 410003;
		public static final int CODE_DATI_SPORCHI = 410004;
		public static final int PERIODO_CANDIDATURA_NON_VALIDO = 410005;
		public static final int NON_ESISTE_CV_VALIDO_PERIODO_CANDIDATURA = 410006;
		public static final int NON_ESISTE_CV_VALIDO = 410007;
		public static final int LAVORATORE_NON_CANDIDABILE = 410008;
		public static final int NON_ESISTE_LAV_PROFESSIONE_DES = 410009;
		public static final int CITTADINANZA_NON_VALIDA = 410010;
		public static final int MSG_CV_ERRORE_OBBL = 410011;
		public static final int CV_NON_INVIABILE_UNVIO_MASSIVO = 410012;
		public static final int MSG_CV_CF_ERRORE = 410013;
		public static final int MSG_CV_ERRORE_UPDATE_TMP_INVIO_MASSIVO = 410014;
		public static final int PERIODO_CANDIDATURA_NON_VALIDO_INVIO_MASSIVO = 410015;
		public static final int CODE_RESPONSE_WS_ERRATO = 410016;
	}

	public static abstract class MyPortal {
		public final static int CODE_XML_VALIDATION = 411000;
		public final static int SUCCESS_VACANCY = 411001;
		public final static int ERROR_VACANCY = 411002;
	}

	public static abstract class Corso {
		public final static int ESISTEPERCORSO = 421000;
	}

	public static abstract class Programmi {
		public final static int ESISTECORSO = 431000;
	}

	public static abstract class InvioMovimentiByMail {
		public final static int CODE_XML_VALIDATION = 441000;
		public final static int ERR_INVIO_MAIL = 441001;
	}

	// controlli per decreto gennaio 2013
	public static abstract class ControlliMovimentiDecreto {
		public static final Integer ERRORE_GENERICO = 451000;
		public final static Integer ERR_COD_VARIAZIONE = 451001;
		public static final Integer ERR_DAT_FINE_PF_NON_VALORIZZATA = 451002;
		public final static Integer ERR_INDETERMINATO_DATA_FINE = 451003;
		public static final Integer ERR_APPRENDISTATO_DATA_FINE_PERIODO_FORM = 451004;
		public static final Integer ERR_PROROGA_SU_I = 451005;
		public static final Integer ERR_CF_PROMOTORE_TIROCINIO = 451006;
		public static final Integer ERR_TRASFORMAZIONE_SU_TD = 451007;
		public static final Integer ERR_DATA_FINE_PF_MINORE_DATA_INIZIO = 451008;
		public static final Integer ERR_CF_PROMOTORE_TIROCINIO_NON_VALIDO = 451009;
		public static final Integer ERR_TIPO_ORARIO = 451010;
		public static final Integer ERR_NUMERO_ORE_SETTIMANALI = 451011;
		public static final Integer ERR_DATA_FINE_PF_DATA_CESSAZIONE = 451012;

		// gennaio 2014
		public static final Integer ERR_CF_PROMOTORE_OBBLIGATORIO = 451013;
		// public static final Integer ERR_IF_CATEGORIA_TIR_THEN_TIPOLOGIA_TIR =
		// 451014;
		// public static final Integer ERR_IF_TIPOLOGIA_TIR_THEN_CATEGORIA_TIR =
		// 451015;
		public static final Integer ERR_TIPOLOGIA_E_CATEGORIA_NON_COERENTI = 451016;
		public static final Integer ERR_DATA_FINE_MOV_OBBL_SE_STAGIONALE = 451017;
		public static final Integer ERR_TIPOLOGIA_CONTRATTUALE_NON_STAGIONALE = 451018;
		// public static final Integer ERR_LIVELLO_OBBLIGATORIO = 451019;
		// public static final Integer ERR_RETRIBUZIONE_OBBLIGATORIO = 451020;
		// public static final Integer ERR_CCNL_OBBLIGATORIO = 451021;
		public static final Integer WRN_CATEGORIA_TIR_6_MESI = 451022;
		public static final Integer WRN_CATEGORIA_TIR_12_MESI = 451023;
		public static final Integer WRN_CATEGORIA_TIR_24_MESI = 451024;
		public static final Integer ERR_DENOMINAZIONE_TIR_OBBLIGATORIO = 451030;
		public static final Integer ERR_CODSOGGPROMOTORE_OBBLIGATORIO = 451031;
		public static final Integer ERR_CATEGORIA_TIR_OBBLIGATORIO = 451032;
		public static final Integer ERR_TIPOLOGIA_TIR_OBBLIGATORIO = 451033;
		public static final Integer ERR_APPRENDISTATO_INDETERMINATO = 451034;
		public static final Integer ERR_APPRENDISTATO_DETERMINATO = 451035;
		public static final Integer ERR_CONTRATTO_X_DETERMINATO = 451036;

		// gennaio 2014 unipi
		public static final Integer ERR_COMPENSAZIONI_ECCEDENZA_O_RIDUZIONE_SENZA_CF = 451025;
		public static final Integer ERR_COMPENSAZIONI_ECCEDENZA_O_RIDUZIONE_CON_CF = 451026;
		public static final Integer ERR_COMPENSAZIONI_ECCEDENZA_CON_ESONERO = 451027;
		public static final Integer ERR_STESSA_PROVINCIA = 451028;
		public static final Integer ERR_STESSA_REGIONE = 451029;

		// decreto 2019
		public static final Integer ERR_PARAMETRI_INPUT_MANCANTI = 451037;
		public static final Integer ERR_PARAMETRI_INPUT_DB_MANCANTI = 451038;
		public static final Integer ERR_PARAMETRI_RETRIBUZIONE_NON_CONGRUENTI = 451039;
		public static final Integer ERR_COMPENSO_RETRIBUZIONE = 451040;

		public static final Integer ERR_COMPENSO_RETRIBUZIONE_SANATORIA = 451041;
		public static final Integer ERR_COMPENSO_RETRIBUZIONE_SANATORIA_CALCOLO = 451042;
		public static final Integer ERR_PARAMETRI_INPUT_MANCANTI_DECRETO_2019 = 451043;
		public static final Integer ERR_CCNL_NON_COMPATIBILE = 451044;
		public static final Integer ERR_COMPENSO_ANNUALE_NON_VALIDO = 451045;
	}

	public static abstract class LogOperazioniCopiaProspetti {
		public static final int RECORD_COPIA_PROSPETTO_NULLO = 461000;
		public static final int ERRORE_SCRITTURA_LOG = 461001;
		public static final int ERRORE_UPDATE_PROSPETTO_DA_COPIARE = 461002;
		public static final int ERRORE_RICALCOLO_RIEPILOGO = 461003;
		public static final int ERRORE_VERIFICA = 461004;
		public static final int ERRORE_GENERAZIONE_COPIA = 461005;
		public static final int WARNING_VERIFICA = 461006;
	}

	public static abstract class ChiusuraDidMultipla {
		public static final int WARNING_NON_PRESENTE_DID = 500000;
		public static final int WARNING_DOPPIA_DID = 500001;
		public static final int ERRORE_GENERICO_CHIUSURA_DID = 500002;
		public static final int WARNING_NESSUNA_SELEZIONE_EFFETTUATA = 500003;
		public static final int PARAMETRI_MANCANTI = 500004;
		public static final int SUCCESS_CHIUSURA_DID = 500005;
	}

	public static abstract class RedditoAttivazione {
		public static final int ERRORE_DATA_DECADENZA = 510000;
	}

	public static abstract class InserisciServizioPrestazioneAttivita {
		public static final int DE_SERVIZIO_INSERT_SUCCESS = 550001;
		public static final int DE_SERVIZIO_INSERT_FAIL = 550002;
		public static final int MA_SERVIZIO_PRESTAZIONE_INSERT_SUCCESS = 550003;
		public static final int MA_SERVIZIO_PRESTAZIONE_INSERT_FAIL = 550004;
		public static final int MA_SERVIZIO_TIPOATTIVITA_INSERT_SUCCESS = 550005;
		public static final int MA_SERVIZIO_TIPOATTIVITA_INSERT_FAIL = 550006;
	}

	public static abstract class MsgWarningReinvioSaap {
		public static final int MA_SERVIZIO_TIPOATTIVITA_UPDATE_SUCCESS_WARN = 560000;
		public static final int MA_SERVIZIO_TIPOATTIVITA_UPDATE_FAIL_WARN = 560001;
	}

	public static abstract class ProgrammazioneBatch {
		public static final int MAX_NUM_BATCH_ATTIVI_APP = 570000;
		public static final int ERR_EXISTS_BATCH_APPUNTAMENTI_SERVIZIO = 570001;
		public static final int ERR_EXISTS_BATCH_AZIONI_AZIONE = 570002;
		public static final int MAX_NUM_BATCH_ATTIVI_AZIONE = 570003;
		public static final int MAX_NUM_BATCH_ATTIVI_DID = 570004;
		public static final int ERR_EXISTS_BATCH_PERDDISOCC_MOTFINEATTO = 570005;
		public static final int MAX_NUM_BATCH_ATTIVI_PERDDISOCC = 570006;
		public static final int ERR_EXISTS_BATCH_PERDDISOCC_MOTFINEATTO_NULL = 570007;
		public static final int ERR_EXISTS_BATCH_PERDDISOCC_CPI = 570008;
		public static final int ERR_EXISTS_BATCH_DID_CPI = 570009;
	}

	public static abstract class MSGVOUCHER {
		public static final int MODELLO_ASSENTE = 580000;
		public static final int ERRORE_DOTERESIDUAPATTO_ASSEGNAZIONE = 580001;
		public static final int ERRORE_DOTERESIDUACPI_ASSEGNAZIONE = 580002;
		public static final int ERRORE_ESITOAZIONE_ASSEGNAZIONE = 580003;
		public static final int ERRORE_ESISTE_VOUCHER_AZIONE_PATTO = 580004;
		public static final int ERRORE_ESISTE_VOUCHER_DELETE_PERCORSO = 580005;
		public static final int ERRORE_DATE_PROROGA_VOUCHER = 580006;
		public static final int ERRORE_DOTERESIDUACPI_PROROGA = 580007;
		public static final int ERRORE_CODICE_ATT_INESISTENTE = 580008;
		public static final int ERRORE_CODICE_ATT_LAV_INESISTENTE = 580009;
		public static final int ERRORE_ATTIVAZIONE_SCADUTA = 580010;
		public static final int ERRORE_VOUCHER_NON_ATTIVABILE = 580011;
		public static final int ERRORE_ENTE_ATTIVAZIONE_SERVIZI = 580012;
		public static final int ERRORE_GENERICO_ATTIVAZIONE = 580013;
		public static final int WARNING_MODALITA_ATTIVE = 580014;
		public static final int ERRORE_DATA_CHIUSURA_FUTURA = 580015;
		public static final int ERRORE_DATA_CHIUSURA_SCADENZA_VOUCHER = 580016;
		public static final int ERRORE_CHIUSURA_STATO_VOUCHER = 580017;
		public static final int ERRORE_CHIUSURA_VOUCHER_ENTE_COLLEGATO = 580018;
		public static final int ERRORE_CHIUSURA_EV_ATT_OBIETT = 580019;
		public static final int ERRORE_CHIUSURA_MODALITA_ASSENTE = 580020;
		public static final int ERRORE_CHIUSURA_MODALITA_TIPO_S = 580021;
		public static final int ERRORE_CHIUSURA_MODALITA_TIPO_T = 580022;
		public static final int ERRORE_MODALITA_SPESAEFFETTIVA = 580023;
		public static final int ERRORE_STATO_VCH_AGG_PAGAMENTI = 580024;
		public static final int ERRORE_STATOPAG_VCH_ASSENTE_AGG_PAGAMENTI = 580025;
		public static final int ERRORE_IMPORTO_PAGATO_AGG_PAGAMENTI = 580026;
		public static final int ERRORE_IMPORTO_NON_PAGATO_AGG_PAGAMENTI = 580027;
		public static final int ERRORE_IMPORTO_SPESOVCH_AGG_PAGAMENTI = 580028;
		public static final int ERRORE_RESIDUO_AGG_PAGAMENTI = 580029;
		public static final int ERRORE_STATO_VCH_RIAPRI = 580030;
		public static final int ERRORE_RESIDUO_VCH_RIAPRI = 580031;
		public static final int ERRORE_CAMBIO_DI_STATO_PAGAMENTO = 580032;
		public static final int ERRORE_VOUCHER_GIA_PRESENTE_ASSEGNAZIONE = 580033;
		public static final int ERRORE_STATO_VCH = 580034;
		public static final int ERRORE_TIPOLOGIA_PRG = 580035;
		public static final int ERRORE_REITERAZIONE_PRG_ATT = 580036;
		public static final int ERRORE_REITERAZIONE_PRG_CHI = 580037;
		public static final int ERRORE_MODALITA_TIPOSERVIZIO = 580038;
		public static final int ERRORE_QUALIFICA_CHIUSURA = 580039;
		public static final int ERRORE_AGGIORNA_IBAN = 580040;
		public static final int ERRORE_CHIUSURA_IMPORTI_SR = 580041;
	}

	public static abstract class Consenso {
		public static final int MSG_STAMPA_PARAMETRICA_TEMPLATE = 590003;
		public static final int MSG_STAMPA_PARAMETRICA_TEMPLATE_NODOC = 590004;
	}

	/* MATTEO: GESTIONE CONSENSO */
	public static abstract class GestioneConsenso {
		public static final int CONSENSO_ASSENTE = 600000;
		public static final int CONSENSO_REVOCATO = 600001;
		public static final int CONSENSO_ATTIVO = 600002;
		public static final int CONSENSO_NON_DISPONIBILE = 600003;
	}

	/* MARIANNA: GESTIONE FIRMA GRAFOMETRICA */
	public static abstract class FirmaGrafometrica {
		public static final int CONSENSO_ASSENTE = 600100;
		public static final int CONSENSO_REVOCATO = 600101;
		public static final int CONSENSO_ATTIVO = 600102;
		public static final int CONSENSO_NON_DISPONIBILE = 600103;
		public static final int CONSENSO_ATTIVO_HREF = 600104;
		public static final int WS_OPERATION_FAIL = 600105;
		public static final int WS_NON_RAGGIUNGIBILE = 600106;
	}

	public static abstract class PacchettoAdulti {
		public static final int ADESIONE_ASSENTE = 600200;
		public static final int XML_ERRATO = 600201;
		public static final int ERR_ISCRIZIONE_DISABILE_PRESENTE = 600202;
	}

	public static abstract class Classificazione {
		public static final int WARN_CLASSIFICAZIONE_NO_DELETE_EXISTS_TEMPLATE = 600300;
	}

	public static abstract class NuovoRedditoAttivazione {
		public static final int COM_RIC_CORRETTA = 700000;
		public static final int COGNOME_ASSENTE = 700001;
		public static final int NOME_ASSENTE = 700002;
		public static final int CODICE_FISCALE_ASSENTE = 700003;
		public static final int DATA_VAR_RES_ASSENTE = 700004;
		public static final int NUOVO_IND_RES_ASSENTE = 700005;
		public static final int NUOVO_CAP_RES_ASSENTE = 700006;
		public static final int CODICE_REIEZIONE_ASSENTE = 700007;
		public static final int AZIONE_RICHIESTA_NON_ESISTENTE = 700008;
		public static final int ID_DOMANDA_INTRANET_NON_PRESENTE = 700009;
		public static final int ID_DOMANDA_WEB_NON_PRESENTE = 700010;
		public static final int ALTRI_ERRORI_NON_PRESENTI = 700999;
	}

	public static abstract class MODELLITDA {
		public static final int ERRORE_MODELLO_ATTIVO = 800000;
		public static final int ERRORE_MODALITA_TOT_VAL = 800001;
		public static final int ERRORE_MODELLO_CALC_VAL = 800002;
		public static final int MODELLO_CALC_VAL_IMPUTABILE = 800003;
		public static final int MODELLO_CALC_VAL_CALCOLATO = 800004;
		public static final int ERRORE_VALTOT_MODTDA_NONCALC = 800005;
		public static final int MODELLO_INS_OK = 800006;
		public static final int MODELLO_UPD_OK = 800007;
		public static final int MODALITA_MOD_INS_OK = 800008;
		public static final int MODALITA_MOD_UPD_OK = 800009;
		public static final int MODALITA_MOD_DEL_OK = 800010;
		public static final int ERRORE_NON_BLOCCANTE_ATTIVITA = 800011;
		public static final int ERRORE_BLOCCANTE_MODELLO = 800012;
		public static final int ERRORE_BLOCCANTE_MODALITA = 800013;
		public static final int MODELLO_ATTIVATO = 800014;
		public static final int MODELLO_DISATTIVATO = 800015;
	}

	public static abstract class ProfiloLavoratore {
		public static final int CNE_PRGPROFILO_NOT_EXISTS = 810000;
		public static final int ERRORE_RISPOSTE_INSUFFICIENTI = 810001;
		public static final int ERRORE_CALCOLO_ESISTENTE = 810002;
		public static final int ERRORE_CALCOLO = 810005;
		public static final int ERRORE_21 = 810021;
		public static final int ERRORE_44 = 810044;
		public static final int ERRORE_45 = 810045;
		public static final int ERRORE_45_INV = 810054;
		public static final int SCORE_PERSONALITA_SUCCESSO = 810098;
		public static final int CALCOLO_ESEGUITO_SUCCESSO = 810099;
		public static final int ERRORE_17_18 = 811718;
		public static final int PERS_MISSING = 800097;
		public static final int PERS_INCOMPLETA = 800096;
	}

	public static abstract class ISEE {
		public static final int ERR_INIZIO_VALIDITA_FUTURA = 820000;
		public static final int ERR_FINE_VALIDITA_FUTURA = 820001;
		public static final int ERR_AGG_PERIODI_SOVRAPPOSTI = 820002;
		public static final int ERR_INS_PERIODI_SOVRAPPOSTI = 820003;
		public static final int ERR_INZIO_FINE_NON_CONGRUENTI = 820004;
	}

	public static abstract class CateneMovimenti {
		public static final int CATENE_MOV_ERRATE = 888888;
	}

	public static abstract class BUDGETTDA {
		public static final int ERR_GENERICO = 900000;
		public static final int ERR_ACCREDITAMENTO_DUPLICATO = 900001;
		public static final int ERR_SOGGETTO_DUPLICATO = 900002;
	}

	public static abstract class CONFERIMENTO_DID {
		public static final int ERR_ETA = 910000;
		public static final int SUCCESS_DID_INSERITA = 910001;
		public static final int SUCCESS_PRESA_CARICO150 = 910002;
		public static final int SUCCESS_INVIO_SAP = 910003;
		public static final int DID_PRESENTE = 910004;
		public static final int DID_PRESENTE_NON_COERENTE = 910005;
		public static final int ERR_CONTROLLI_STIPULA_DID = 910006;
		public static final int ERR_GENERICO_STIPULA_DID = 910007;
		public static final int ERR_GENERICO_INSERT_DID = 910008;
		public static final int ERR_GENERICO_PROTOCOLLA_DID = 910009;
		public static final int ERR_GENERICO_DOCUMENTO_IDENTIFICAZIONE = 910010;
		public static final int ERR_RICALCOLA_IMPATTI = 910011;
		public static final int ERR_INVOCAZIONE_WS = 910012;
		public static final int PRESA_CARICO150_PRESENTE = 910013;
		public static final int ERR_PRESA_CARICO150 = 910014;
		public static final int ERR_GENERICO_PRESA_CARICO150 = 910015;
		public static final int ERR_LOG_AM_CONFERIMENTO_DID = 910016;
		public static final int CONFERIMENTO_OK = 910017;
		public static final int KO = 910018;
		public static final int KO_INVIO_SAP = 910019;
		public static final int KO_ERR_MINISTERO = 910020;
		public static final int ERR_GENERICO_AUTOMATISMI_DID = 910021;
		public static final int SUCCESS_GENERICO_AUTOMATISMI_DID = 910022;
		public static final int CONVALIDA_OK = 910023;
		public static final int REVOCA_OK = 910024;
		public static final int CONFERIMENTO_OK_ANPAL = 910025;
		public static final int CONVALIDA_OK_ANPAL = 910026;
		public static final int REVOCA_OK_ANPAL = 910027;
		public static final int KO_ANPAL = 910028;
	}

	public static abstract class SIFERACCREDITAMENTO {
		public static final int ERR_INPUT = 920000;
		public static final int ERR_RECUPERO_ANAGRAFICA_LAVORATORE = 920001;
		public static final int ERR_WS_EMAIL_LAV = 920002;
		public static final int ERR_WS_DATI_LAVORATORE = 920003;
		public static final int ERR_WS_NO_PATTI = 920004;
		public static final int ERR_WS_RECUPERO_AZIONI = 920005;
		public static final int ERR_WS_NO_ATTIVITA_PATTO = 920006;
		public static final int ERR_WS_SVANTAGGI_PATTO = 920007;
		public static final int ERR_WS_PROGRAMMI_PATTO = 920008;
		public static final int ERR_WS_SCHEDA_PARTECIPANTE_PATTO = 920009;
		public static final int WS_OK = 920010;
		public static final int ERR_WS_NO_PATTI_POC = 920011;
		public static final int WS_KO = 920012;
		public static final int ERR_WS_DATA_ADESIONE_GG = 920013;
		public static final int ERR_GENERICO = 920099;
	}

	public static abstract class RENDICONTAZIONE {
		public static final int ERR_WS_DATI_LAVORATORE = 920100;
		public static final int ERR_WS_EMAIL = 920101;
		public static final int ERR_WS_DATA_ADESIONE_DIVERSE = 920102;
		public static final int ERR_WS_DATA_ADESIONE_NULLA = 920103;
		public static final int ERR_WS_DATA_ADESIONE_GENERICO = 920104;
		public static final int ERR_WS_INDICE_SVAN_NEW = 920105;
		public static final int ERR_WS_INDICE_SVAN_OLD = 920106;
		public static final int ERR_WS_NO_PATTI = 920107;
		public static final int ERR_RECUPERO_POLITICHE = 920108;
		public static final int ERR_WS_NO_PATTOGG_A02 = 920109;
		public static final int ERR_WS_NO_POLITICHE_ATTIVE = 920110;
		public static final int ERR_KO_WS_OUTPUT = 920111;
		public static final int ERR_WS_INPUT = 920112;
		public static final int ERR_GENERICO_TRACCIATO = 920113;
	}

	public static abstract class SIFERFORMAZIONE {
		public static final int ERR_POLITICACONCLUSA_SENZACORSI = 960020;
		public static final int ERR_INSERIMENTOCORSO = 960021;
		public static final int ERR_INSERIMENTOCORSOPOLITICA = 960022;
	}

	public static abstract class AGENDA_ANAPL {
		public static final String HANDLER = "LOG_HANDLER";
	}

	public static abstract class ForzaturaMovimenti {
		public static final int FORZATURA_OK = 970000;
		public static final int CATENE_1 = 970001;
		public static final int CATENE_2 = 970002;
		public static final int CATENE_3 = 970003;
		public static final int CATENE_4 = 970004;
		public static final int CATENE_5 = 970005;
		public static final int CATENE_6 = 970006;
		public static final int CATENE_7 = 970007;
		public static final int CATENE_8 = 970008;
		public static final int CATENE_9 = 970021;
		public static final int MOV_SUCC_TD_TI = 970009;
		public static final int MOV_PREC_TI_TD = 970010;
		public static final int MOV_SUCC_CES = 970011;
		public static final int DATE_ERR = 970012;
		public static final int MOV_PREC_AVV = 970013;
		public static final int ERR_INS_DOC = 970014;
		public static final int ERR_MOD_DOC_AN_PR = 970015;
		public static final int ERR_MOV_FORZATO = 970016;
		public static final int ERR_AVV_SUCC = 970017;
		public static final int ERR_CES_PREC = 970018;
		public static final int DTIN_DTFEFF = 970019;
		public static final int DTEFF_DTFINE = 970020;
		public static final int ERROR_DEFAULT = 970022;
	}

	public static abstract class RDC {
		public static final int NO_RESULT_FOUND = 950003;
		public static final int WS_NUCLEOFAM_RDC = 951001;
		public static final int NUCLEOFAM_RDC_AN_AGG = 951002;
		public static final int NUCLEOFAM_RDC_AN_PRES = 951003;
		public static final int NUCLEOFAM_RDC_AN_ERR = 951004;
		public static final int NUCLEOFAM_RDC_NOTIFICA_AGG = 951005;
		public static final int NUCLEOFAM_RDC_NOTIFICA_PRES = 951006;
		public static final int NUCLEOFAM_RDC_NOTIFICA_UPD = 951007;
	}
}
