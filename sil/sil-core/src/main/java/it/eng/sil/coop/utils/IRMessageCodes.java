/*
 * Created on May 29, 2006
 *
 */
package it.eng.sil.coop.utils;

/**
 *
 */
public abstract class IRMessageCodes {

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
		public static final int ELEMENT_DUPLICATED_WITH_KEY = 10022;
		public static final int ELEMENT_COMUNE_DUPLICATED_WITH_KEY = 10023;
		public static final int REPORT_EMPTY = 10024;
		public static final int UNKNOWN_ERROR = 10025;
		/**
		 * RIPORTA IL MESSAGGIO DI ERRORE INTERNO DELL'APPLICATIVO (DA PASSARE COME PRIMO PARAMETRO)
		 */
		public static final int RIPORTA_ERRORE_INTERNO = 10101;

	}

	public static abstract class NOTIFICHE_IR {
		public static final int GENERIC_ERROR = 30000;
		public static final int ACCORPAMENTO_LAV_FAIL = 30001;
		public static final int ACCORPAMENTO_LAV_CANCELLATO = 30002;
		public static final int MODIFICA_CF_FAIL = 30003;
		public static final int MODIFICA_CF_CANCELLATO = 30004;
		public static final int AGGIONA_COMPETENZA_FAIL = 30005;
		public static final int AGGIONA_COMPETENZA_ER_FAIL = 30006;
		public static final int MODIFICA_ANAG_FAIL = 30007;
		public static final int MODIFICA_CPI_FAIL = 30008;
		public static final int INSERIMENTO_LAV_PRESENTE = 30009;
		public static final int INSERIMENTO_LAV_FAIL = 30010;
	}

}
