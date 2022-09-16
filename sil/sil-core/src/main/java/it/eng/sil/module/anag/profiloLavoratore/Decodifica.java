package it.eng.sil.module.anag.profiloLavoratore;

import java.math.BigDecimal;

public abstract class Decodifica {

	public static abstract class DomandaProfilo {
		public static final String D07 = "D07";
		public static final String D08 = "D08";
		public static final String D44_OLD = "D44_OLD";
		public static final String D20 = "D20";
		public static final String D20_1 = "D20_1";
		public static final String D45 = "D45";
		public static final String D45_2 = "D45_2";
		public static final String D21 = "D21";
		public static final String D22 = "D22";
		public static final String D23 = "D23";
		public static final String D24 = "D24";
		public static final String D25 = "D25";
		public static final String D26 = "D26";
		public static final String D27 = "D27";
		public static final String D28 = "D28";
		public static final String D29 = "D29";
		public static final String RD08 = "D08_1";
		public static final String E_15_24 = "E1";
		public static final String E_25_34 = "E2";
		public static final String E_35_44 = "E3";
		public static final String E_45_54 = "E4";
		public static final String E_55 = "E5";
		public static final String RD06_NO = "D06_1";
		public static final String RD06_SI = "D06_2";

	}

	public static abstract class StatoProfilo {
		public static final String IN_CORSO = "I";
		public static final String CHIUSO_CALCOLATO = "C";
	}

	public static final String CALCOLA_SALVA_PROFILO = "CALCOLA_SALVA";
	public static final String CALCOLA_PERSONALITA = "CALCOLA_PERSONALITA";
	public static final String SALVA_FLG_LINGUA = "SALVA_FLGLINGUA";

	public static abstract class StatoOccupazionale {
		public static final String OCCUPATO = "O";
		public static final String DISOCCUPATO_I = "I";
		public static final String DISOCCUPATO_D = "D";
		public static final String RD04_DISOCC_12 = "SO1";
		public static final String RD04_DISOCC = "SO2";
		public static final String RD04_OCC = "SO3";
	}

	public static abstract class TipoDomanda {
		public static final String RADIO_ALTRO = "RAL";
		public static final String TEXT_AREA = "TA";
	}

	public static final BigDecimal NUM_MIN_RISPOSTE = new BigDecimal(27);

	public static abstract class ScorePersonalita {
		public static final String COMPLETEZZA_PROFILO = "profiloCompleto";
		public static final String ESITO_COMPLETO = "COMPLETO";
		public static final String ESITO_INCOMPLETO = "INCOMPLETO";
		public static final String AMICALITA = "scoreAmicalita";
		public static final String COSCIENZOSITA = "scoreCoscienzosita";
		public static final String STAB_EMOTIVA = "scoreStabEmotiva";
		public static final String EXTRAVERSIONE = "scoreExtraVersione";
		public static final String APERTURA = "scoreApertura";
	}

	public static abstract class IndiceProfilatura {
		public static final String MOLTO_ALTA = "MA";
		public static final String ALTA = "A";
		public static final String MEDIA = "M";
		public static final String BASSA = "B";
	}

}
