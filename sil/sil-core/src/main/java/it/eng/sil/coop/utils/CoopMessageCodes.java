/*
 * Created on Aug 11, 2006
 */
package it.eng.sil.coop.utils;

/**
 * @author savino
 */
public abstract class CoopMessageCodes {
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

		/**
		 * RIPORTA IL MESSAGGIO DI ERRORE INTERNO DELL'APPLICATIVO (DA PASSARE COME PRIMO PARAMETRO)
		 */
		public static final int RIPORTA_ERRORE_INTERNO = 10101;
	}

	public static abstract class NOTIFICHE_COOP {

		public static final int INVIO_DATI_FAIL = 30001;
		public static final int PRESA_ATTO_TRASFERIMENTO_FAIL = 30002;
		public static final int PRESA_ATTO_COOP_NON_ATTIVA = 30003;
		public static final int PRESA_ATTO_LAV_NON_PRESENTE = 30004;
		public static final int PRESA_ATTO_CPI_NON_COMPETENTE = 30005;
		public static final int PRESA_ATTO_LAV_NON_IN_ELENCO_ANAG = 30006;

	}

	/**
	 * Il messaggio associato e' presente sia in messages_COOP_it_IT.properties del progetto SilCoop che in
	 * messages_it_IT.properties del progetto SilWeb in quanto la classe che lo utilizza puo' essere chiamata sia nel
	 * contesto code che nel contesto web.
	 */
	public static abstract class NOTIFICHE_COMUNI {
		public static final int PRESA_ATTO_INVIO_DATI_AVVENUTO = 60100;
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
		public static final int ERR_CLOSE_CM_ISCR = 80014;
	}
}
