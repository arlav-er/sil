package it.eng.afExt.utils;

/**
 * Classe contenitore dei codici di messaggi o errori che si trovano nel file
 * ...WEB-INF\classes\messages_x_Y.properties dove x e Y indica language e
 * country scelte.
 * 
 * @author Corrado Vaccari
 */
public abstract class MessageCodes {
	// Quando si aggiungono nuove sezioni, tipo "errori sul lavoratore",
	// partire sempre dal 10000 superiore,
	// Es. ultimo messaggio : 30103, cominciare la nuova sezione da 40000
	// per non rischiare di utilizzare id già usati
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

		/**
		 * E' il numero restituito dalla procedure
		 * gestamm.checkIntersezioneDate() nel caso in cui la data inizio del
		 * record successivo sia minore della data fine del record precedente
		 * (il codice della trace non puo' essere maggiore di -20000)
		 */
		public static final int ERR_INTERSEZIONE_DATA_REC_STORICIZZATO = 20010;
		/**
		 * E' il numero restituito dalla procedure
		 * gestamm.checkIntersezioneDate() nel caso in cui la data inizio del
		 * record successivo sia uguale alla data fine del record precedente
		 */
		public static final int WARNING_INTERSEZIONE_DATA_REC_STORICIZZATO = 20011;
	}

	public static abstract class Mansioni {
		public static final int MANSIONE_PRESENTE_IN_ESP_LAV = 20000;
		public static final int MANSIONE_ASS_PATTO_PROTOCOLLATO = 20001;
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

		public static final boolean isInRange(int msgCode) {
			if ((msgCode < 30000) && (msgCode > 30010)) {
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

		public static final boolean isInRange(int msgCode) {
			if ((msgCode < 33000) && (msgCode > 33008)) {
				return false;
			}

			return true;
		}
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
	}

	public static abstract class Privacy {
		public static final int INSERT_FAIL = 36000;
	}

	public static abstract class ElencoAnagrafico {
		public static final int INSERT_FAIL = 37000;
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
		public static final int ERR_PRO_INT = 50126;
		public static final int ERR_PRO_DET = 50127;
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
	}

	public static abstract class Coop {
		public static final int ERR_CONNESSIONE_IR = 60000;
		public static final int ERR_TROPPI_RISULTATI = 60001;
		public static final int ERR_INSERIMENTO_IR = 60002;
		public static final int INSERIMENTO_IR_SUCCESS = 60003;
	}

	public static abstract class Protocollazione {
		public static final int ERR_GENERICO_NELLA_SP = 70000;
		public static final int NUM_PROT_TROPPO_GRANDE = 70001;
		public static final int NUM_PROT_TROPPO_PICCOLO = 70002;
		public static final int NUM_PROT_GIA_INSERTITO = 70003;
		public static final int DATA_PROT_NULLA = 70010;
		public static final int DATA_PROT_ERRATA = 70011;
		public static final int TIPO_PROT_NULL = 70020;
	}

	public static abstract class Trasferimento {
		public static final int ERR_PIU_PATTI_APERTI = 80000;
		public static final int ERR_CLOSE_AN_LAV_S = 80001;
		public static final int ERR_CLOSE_AM_EL_ANAG = 80002;
		public static final int ERR_OPEN_AM_EL_ANAG = 80003;
		public static final int ERR_CLOSE_AM_PATTO_LAV = 80004;
		public static final int ERR_UPDATE_DOM_LAV = 80005;
		public static final int ERR_UPD_AM_DICH_DISP = 80006;
	}

	// Messaggi sull'unita azienda
	public static abstract class UnitaAzienda {
		public static final int WAR_SEDE_LEGALE_MOD = 81000;
	}

	// Messaggio per l'anagrafica
	public static abstract class Anag {
		public static final int INSERT_FAIL_CF_ESISTENTE = 90000;
	}

	// Messaggi di accorpamento
	public static abstract class Accorpamento {
		public static final int ACCORPAMENTO_SUCCESS = 100000;
		public static final int ACCORPAMENTO_FAIL = 100010;
		public static final int ACCORPAMENTO_TESTATA_SUCCESS = 100001;
		public static final int ACCORPAMENTO_TESTATA_FAIL = 100011;
	}

	// Messaggi per trasferimento ramo aziendale
	public static abstract class TrasferimentoRamoAzienda {
		public static final int ERR_INSER_CES = 110000;
		public static final int ERR_UPDATE_PREC = 110001;
		public static final int ERR_INSER_AVV = 110002;
		public static final int ERR_PROT_AVV = 110003;
		public static final int ERR_PROT_CES = 110004;
	}

	public static abstract class Login {

		public static final int ERR_PWD_TOO_SHORT = 110100;
		public static final int ERR_PWD_WITHOUT_DIGITS = 110101;
		public static final int ERR_PWD_CASE_ALT = 110102;
		public static final int ERR_PWD_NOT_DIFFERENT = 110103;
		public static final int ERR_WRONG_OLD_PWD = 110105;

	}

	public static abstract class Pubblicazioni {
		public static final int ERR_PUBB = 90100;

	}

}
