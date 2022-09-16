package it.eng.sil.coop.bean.blen.constant;

import java.math.BigDecimal;

public class ServiziConstant {
	public static final BigDecimal UTENTE_BLEN = new BigDecimal(230);

	public static final String AZIENDA_PRIVATA = "AZI";
	public static final String FLG_NO_SEDE_LEGALE = "N";
	public static final String SEDE_IN_ATTIVITA = "1";
	public static final String CODIFICA_NON_DISPONIBILE = "NT";

	public static final int OPERAZIONE_OK = 0;
	public static final int OPERAZIONE_KO = -1;

	public static final int SEDE_AZIENDA_NON_TROVATA = 4;

	public static final String MO_EVASIONE_SELEZIONE = "SEL";
	public static final String PUBBLICAZIONE_PALESE = "DFD";
	public static final int STATO_EVASIONE_INSERITA = 1;
	public static final String STATO_RICHIESTA_COMPLETA = "K";

	public static final String SI = "S";

	// per dati obbligatori su SIL, ma non obbligatori nei file xml
	public static final String NON_SPECIFICATO = "Non specificato";

	public static final String CONOSCENZA_INFORMATICA_CODIFICA_INESISTENTE = "100000";

	public static final BigDecimal CONTATTO_DI_PERSONA = new BigDecimal(4);

}
