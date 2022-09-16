package it.eng.sil.module.collocamentoMirato.constant;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

public class ProspettiConstant {
	public static final String DATA_CHECK_2011 = "01/01/2012";

	public static final String CATEGORIA_NULLA = "Z";
	public static final String STATO_IN_CORSO_ANNO = "A";
	public static final String STATO_STORICIZZATO = "S";
	public static final String STATO_ANNULLATO = "N";
	public static final String CATEG_AZ_MAGGIORE_50 = "A";
	public static final String CATEG_AZ_TRA_36_50 = "B";
	public static final String CATEG_AZ_TRA_15_35 = "C";
	public static final String DESC_CATEG_AZ_MAGGIORE_50 = "più di 50 dipendenti";
	public static final String DESC_CATEG_AZ_TRA_36_50 = "da 36 a 50 dipendenti";
	public static final String DESC_CATEG_AZ_TRA_15_35 = "da 15 a 35 dipendenti";
	public static final String DESC_CATEG_AZ_NULLA = "Categoria nulla";

	public static final String FIELD_CODICE = "CODRISULTATO";
	public static final String FIELD_DESC_RISULTATO = "DESCRISULTATO";
	public static final String FIELD_PROGRESSIVO = "PRGPROSPETTOINF";
	public static final String FIELD_PROGRESSIVO_NEW = "PRGPROSPETTONEW";
	public static final String FIELD_NUMKLO = "NUMKLOPROSP";
	public static final String FIELD_NUMANNORIF = "NUMANNORIFPROSP";
	public static final String FIELD_PROVINCIA = "CODPROVINCIA";
	public static final String FLAG_CONVENZIONE = "S";
	public static final String FLAG_ANNULLA = "1";

	public static final String COD_ERRORE_RICALCOLO = "ER";
	public static final String DESC_ERRORE_RICALCOLO = "ERRORE: errore nel ricalcolo del riepilogo";

	public static final String COD_STORICIZZATO_COPIATO = "SC";
	public static final String DESC_STORICIZZATO_COPIATO = "PI storicizzato e copia eseguita";

	public static final String COD_ANNULLATO_COPIATO = "AC";
	public static final String DESC_ANNULLATO_COPIATO = "PI annullato e copia eseguita";

	public static final String COD_WARNING_VERIFICA = "WV";
	public static final String DESC_WARNING_VERIFICA = "ATTENZIONE: problema nella verifica del prospetto.";

	public static final String COD_ERRORE_VERIFICA = "EV";
	public static final String DESC_ERRORE_VERIFICA = "ERRORE: errore durante la verifica del prospetto";

	public static final String COD_ERRORE_COPIA = "EC";
	public static final String DESC_ERRORE_COPIA = "ERRORE: errore nella generazione della copia";

	public static final String COD_ERRORE_GENERICO = "EG";
	public static final String DESC_ERRORE_GENERICO = "ERRORE: errore generico";

	public static final int ANNO_INIZIO_COPIA_IN_CORSO = 2009;
	public static final int NUM_MAX_COPIA_IN_CORSO = 300;
	public static final int MAX_LENGTH_DESC_ERRORE_COPIA = 500;

	public static final BigDecimal USER_MODIFICA = new BigDecimal("100");

	public static final Map<String, String> mapResultSuccess = new HashMap<String, String>();
	static {
		mapResultSuccess.put("SC", "PI storicizzato e copia eseguita");
	}

	public static final Map<String, String> mapResultWarning = new HashMap<String, String>();
	static {
		mapResultWarning.put("AC", "PI annullato e copia eseguita");
	}

	public static final Map<String, String> mapResultError = new HashMap<String, String>();
	static {
		mapResultError.put("EC", "errore nella generazione della copia");
		mapResultError.put("EG", "errore generico");
		mapResultError.put("EV", "errore durante la verifica del prospetto");
		mapResultError.put("ER", "errore nel ricalcolo del riepilogo");
	}

	public static final String WARNING_DISABILI_IN_FORZA = "Il numero di lavoratori L. 68 con categoria disabile inseriti non corrisponde al totale dei disabili in forza dichiarato per le esclusioni<BR/>(si considera la somma dei disabili, dei centralinisti telefonici e dei massofisioterapisti).";
	public static final String WARNING_CENTRALINISTI = "Il numero di lavoratori L. 68 con categoria disabile e tipologia di assunzione protetta D - Centralinista (L. 113/85) inseriti<BR/>non corrisponde al totale dei centralinisti telefonici non vedenti a tempo pieno e a tempo parziale dichiarato per le esclusioni.";
	public static final String WARNING_MASSOFISIOTERAPISTI = "Il numero di lavoratori L. 68 con categoria disabile e tipologia di assunzione protetta E - Massofisioterapista (403/71) oppure O - Terapisti della riabilitazione (L.29/94)<BR/>inseriti non corrisponde al totale dei terapisti emassofisioterapisti non vedenti a tempo pieno e a tempo parziale dichiarato per le esclusioni.";
	public static final String WARNING_DISABILI_SOMMINISTRATI = "Il numero di lavoratori L. 68 con categoria disabile e tipologia di assunzione protetta M - Somministrazione Lavoratore Disabile con missione a TD<BR/>oppure N - Somministrazione Lavoratore Disabile con missione a TI inseriti non corrisponde al totale dei disabili somministrati a tempo pieno<BR/>e a tempo parziale dichiarato nella pagina del personale non dipendente computabile.";
	public static final String WARNING_DISABILI_CONVENZIONE = "Il numero di lavoratori L. 68 con categoria disabile e tipologia di assunzione protetta  H - Convenzione art. 12bis oppure I - Convenzione art. 14<BR/>inseriti non corrisponde al totale dei disabili in convenzione a tempo pieno e a tempo parziale<BR/>dichiarati nella pagina del personale non dipendente computabile.";

	public static final String CATEGORIA_ASS_PROTETTA_SOMM = "M";

	public static final String AZIENDA_DATORE_LAVORO_PUBBLICO = "PA";
	public static final String FLAG_TRUE = "S";
	public static final String FLAG_FALSE = "N";

	public static final String CATEGORIA_DISABILE_TELELAVORO = "T";

}
