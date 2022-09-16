package it.eng.sil.cig.bean;

public class CigConst {

	// codici motivo per cui un lavoratore non ha la disoccupazione ordinaria
	public final static String MOT_EXDPR602 = "01";
	public final static String MOT_APPR = "02";
	public final static String MOT_NOCONTRIB = "03";

	// tipi possibili di iscrizione CODTIPOISCR-->DE_TIPO_ISCR
	public final static String AM_CIGO = "O";
	public final static String AM_CIGS = "S";
	public final static String AM_MOBILITA = "M";
	public final static String AM_GENERICA = "G";

	/* possibili stati in cui si può trovare un'iscrizione CODSTATOISCR-->DE_STATO_ISCR */
	public final static String ISCR_ANNULLATO = "AA";
	public final static String ISCR_ANNULLAMENTO_PER_ANNULLAMENTO_DOMANDA = "AD";
	public final static String ISCR_ANNULLAMENTO_PER_RETTIFICA = "AR";
	public final static String ISCR_ANNULLAMENTO_PER_NON_APPROVAZIONE = "AN";

	/* possibili stati in cui si può trovare l'accordo CODSTATOATTO-->DE_STATO_ATTO */
	public final static String ACC_VALIDO = "VA";
	public final static String ACC_ANNULLATO = "AN";
	public final static String ACC_RETTIFICATO = "AR";
	public final static String ACC_CHIUSO = "AS";

	/* possibili tipi di concessione per l'accordo CODTIPOCONCESSIONE-->DE_TIPO_CONCESSIONE */
	public final static String CONC_SI = "S";
	public final static String CONC_NO = "N";
	public final static String CONC_PARZIALE = "P";
	public final static String CONC_ISTRUIRE = "I";
	public final static String CONC_ESAMINARE = "E";
}
