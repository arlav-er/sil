package it.eng.sil.coop.webservices.agenda.appuntamento;

import java.math.BigDecimal;

public class Constants {

	public final static BigDecimal UTENTE_PORTALE = new BigDecimal("150");

	public final static String UTENTE_SERVIZIO_LAVORATORE = "L";
	public final static String UTENTE_SERVIZIO_AZIENDA = "A";

	public final static String MATTINO = "M";
	public final static String POMERIGGIO = "P";

	public final static String COD_UT = "190";

	public final static String COD_ANNULLA = "5";

	public final static int NUM_CONFIGURAZIONE = 4;

	public class SMS {
		public final static String GARANZIA_GIOVANI = "PROMAPGG";
		public final static String PATRONATO_SINDACATO = "PROMAPSE";
	}

	public class EMAIL {
		public final static String GARANZIA_GIOVANI = "GGPRAP";
		public final static String GENERICO_PROMEMORIA = "GEPRAP";
	}

	public class ESITO {

		public final static String OK = "00";
		public final static String DESC_OK_FISSA = "Appuntamento prenotato";
		public final static String DESC_OK_DISPONIBILITA = "Disponibilità Appuntamento OK";
		public final static String DESC_OK_ANNULLA = "Appuntamento annullato";
		public final static String DESC_OK_PRENOTAZIONI = "Ritornati gli appuntamenti prenotati";

		public final static String ERRORE_GENERICO = "99";
		public final static String DESC_ERRORE_GENERICO = "Errore generico";

		public final static String ERRORE_VALIDAZIONE_INPUT = "02";
		public final static String DESC_ERRORE_VALIDAZIONE_INPUT = "Errore di validazione (XSD)";

		public final static String SERVIZIO_MANCANTE = "03";
		public final static String DESC_SERVIZIO_MANCANTE = "Sul SIL non è stato configurato il servizio relativo per l'appuntamento.";

		public final static String APPUNTAMENTO_NON_PRESENTE = "04";
		public final static String DESC_VERIFICA_APPUNTAMENTO_NON_PRESENTE = "Sul SIL non è presente l'appuntamento di cui si è richiesta la verifica.";
		public final static String DESC_ANNULLA_APPUNTAMENTO_NON_PRESENTE = "Sul SIL non è presente l'appuntamento di cui si è richiesto l'annullamento.";

		public final static String APPUNTAMENTI_MULTIPLI = "05";
		public final static String DESC_APPUNTAMENTI_MULTIPLI = "Sul SIL è presente più di un appuntamento che combacia con i criteri di verifica.";

		public final static String APPUNTAMENTO_STATO_NON_ATTIVO = "06";
		public final static String DESC_APPUNTAMENTO_STATO_NON_ATTIVO = "Sul SIL l'appuntamento che si voleva annullare risulta in stato non attivo.";

		public final static String APPUNTAMENTO_NON_ANNULLABILE = "07";
		public final static String DESC_APPUNTAMENTO_NON_ANNULLABILE = "Sul SIL l'appuntamento che si voleva annullare risulta fuori termine di annullamento.";

		public final static String ERRORE_COERENZA_SLOT_CPI = "10";
		public final static String DESC_ERRORE_COERENZA_SLOT_CPI = "Incoerenza dati di input (SLOT e CPI)";

		public final static String ERRORE_COERENZA_CPI_PROVINCIA = "11";
		public final static String DESC_ERRORE_COERENZA_CPI_PROVINCIA = "Incoerenza dati di input (CPI e Provincia)";

		public final static String ERRORE_COERENZA_COMDOM_PROV_CPI = "12";
		public final static String DESC_ERRORE_COERENZA_COMDOM_PROV_CPI = "Incoerenza dati (domicilio CPI) ";

		public final static String ERRORE_COERENZA_EMAIL = "13";
		public final static String DESC_ERRORE_COERENZA_EMAIL = "Manca l'indirizzo e-mail";

		public final static String ERRORE_COERENZA_SMS = "14";
		public final static String DESC_ERRORE_COERENZA_SMS = "Manca il numero di cell";

		public final static String ERRORE_COERENZA_PROVINCIA_XML = "15";
		public final static String DESC_ERRORE_COERENZA_PROVINCIA_XML = "Errore sulla Provincia";

		public final static String ERRORE_INPUT_SLOT_NON_TROVATO = "16";
		public final static String DESC_ERRORE_INPUT_SLOT_NON_TROVATO = "Lo slot non esiste";

		public final static String ERRORE_INPUT_SLOT_NON_DISPONIBILE = "17";
		public final static String DESC_ERRORE_INPUT_SLOT_NON_DISPONIBILE = "Lo slot non è più disponibile";

		public final static String ERRORE_NESSUNO_SLOT_TROVATO = "18";
		public final static String DESC_ERRORE_NESSUNO_SLOT_TROVATO = "Nessuno slot disponibile";

		public final static String ERRORE_SERVIZIO_MANCANTE = "19";
		public final static String DESC_ERRORE_SERVIZIO_MANCANTE = "Servizio mancante";

		public final static String ERRORE_CODICE_FISCALE = "20";
		public final static String DESC_ERRORE_CODICE_FISCALE = "Codice fiscale errato";

		public final static String ERRORE_CODIFICA = "21";
		public final static String DESC_ERRORE_CODIFICA = "Errore codifica: ";

		public final static String PRENOTAZIONE_ESISTENTE_S = "23";
		public final static String DESC_PRENOTAZIONE_ESISTENTE_S = "Risulta già una prenotazione presso il CPI.";

		public final static String PRENOTAZIONE_ESISTENTE_T = "24";
		public final static String DESC_PRENOTAZIONE_ESISTENTE_T = "Risulta già una prenotazione presso il CPI.";

		public final static String PRENOTAZIONE_CONTEMPORANEA_ESISTENTE = "25";
		public final static String DESC_PRENOTAZIONE_CONTEMPORANEA_ESISTENTE = "Sovrapposizione con altro appuntamento per stesso soggetto.";

		public final static String ERRORE_PRENOTAZIONE_CONTROLLI_AGGIUNTIVI = "26";
		public final static String DESC_PRENOTAZIONE_CONTROLLI_AGGIUNTIVI = "Condizioni aggiuntive per prenotazione non superate";

		public final static String ERRORE_CONCORRENZA = "98";
		public final static String DESC_ERRORE_CONCORRENZA = "Errore di concorrenza: si invita a riprovare";

	}

	public class ESITO_DO_RISULTATO_ISTANZA {

		public final static String CANDIDATURA_DA_ELABORARE = "-1";
		public final static String DESC_CANDIDATURA_DA_ELABORARE = "Candidatura da elaborare";

		public final static String CANDIDATURA_OK = "OK";
		public final static String DESC_CANDIDATURA_OK = "OK";

		public final static String CANDIDATURA_ERR_INS_ANAG = "01";
		public final static String DESC_CANDIDATURA_ERR_INS_ANAG = "Anagrafica non inseribile";

		public final static String CANDIDATURA_ERR_AGG_ANAG = "02";
		public final static String DESC_CANDIDATURA_ERR_AGG_ANAG = "Aggiornamento anagrafica non riuscito";

		public final static String CANDIDATURA_ERR_ISEE = "03";
		public final static String DESC_CANDIDATURA_ERR_ISEE = "Aggiornamento dati ISEE non riuscito";

		public final static String CANDIDATURA_ERR_GRADUATORIA = "04";
		public final static String DESC_CANDIDATURA_ERR_GRADUATORIA = "Errore aggiornamento dati graduatoria";
	}

	public class QUERY {

		public static final String FISSA_FLAG_APPUNTAMENTO = ""
				+ " select  to_char(ag_agenda.dtmdataora, 'dd/mm/yyyy') dataSlot, to_char(ag_agenda.dtmdataora, 'hh24:mi') oraSlot,"
				+ " ag_agenda.codcpi," + " de_cpi.strdescrizione descCpi," + " de_cpi.strindirizzo,"
				+ " de_cpi.strindirizzostampa," + " an_spi.strsiglaoperatore," + " de_ambiente.strdescrizione ambiente,"
				+ " ag_agenda.codstatoappuntamento," + " ag_agenda.numminuti" + " from ag_agenda"
				+ " inner join de_cpi on (ag_agenda.codcpi = de_cpi.codcpi)"
				+ " inner join an_spi on (ag_agenda.prgspi = an_spi.prgspi)"
				+ " left join de_ambiente on (ag_agenda.prgambiente = de_ambiente.prgambiente)"
				+ " inner join ag_lavoratore ON (ag_agenda.prgappuntamento = ag_lavoratore.prgappuntamento )"
				+ " inner join an_lavoratore ON (ag_lavoratore.cdnlavoratore = an_lavoratore.cdnlavoratore)"
				+ " INNER JOIN de_stato_appuntamento ON (ag_agenda.codstatoappuntamento = de_stato_appuntamento.codstatoappuntamento)"
				+ " WHERE an_lavoratore.strcodicefiscale = ? and"
				+ " trunc(ag_agenda.dtmdataora) > trunc( SYSDATE ) and" + " ag_agenda.codcpi = ? AND"
				+ " de_stato_appuntamento.flgattivo = 'S'";

		public static final String DISPONIBILITA_SELECT_DATA = ""
				+ "select to_char(ag_slot.dtmdataora, 'dd/mm/yyyy') dataSlot ";

		public static final String DISPONIBILITA_SELECT_RESULT = "" + " select ag_slot.prgslot, " + " ag_slot.codcpi, "
				+ " ag_slot.prgambiente, " + " to_char(ag_slot.dtmdataora, 'dd/mm/yyyy hh24:mi') dataOraSlot, "
				+ " to_char(ag_slot.dtmdataora, 'dd/mm/yyyy') dataSlot, "
				+ " to_char(ag_slot.dtmdataora, 'hh24:mi') oraSlot, " + " an_spi.strsiglaoperatore, "
				+ " de_cpi.strdescrizione descCpi, " + " de_cpi.strindirizzo, " + " de_cpi.strindirizzostampa, "
				+ " de_ambiente.strdescrizione ambiente, " + " ag_slot.numminuti ";

		public static final String DISPONIBILITA_FROM = ""
				+ " from ag_slot inner join de_stato_slot on (ag_slot.codstatoslot = de_stato_slot.codstatoslot) "
				+ " inner join de_cpi on (ag_slot.codcpi = de_cpi.codcpi) "
				+ " inner join ag_spi_slot on (ag_slot.prgslot = ag_spi_slot.prgslot and ag_slot.codcpi = ag_spi_slot.codcpi) "
				+ " inner join an_spi on (ag_spi_slot.prgspi = an_spi.prgspi) ";
		// + " left join de_ambiente on (ag_slot.prgambiente = de_ambiente.prgambiente) ";

		public static final String VERIFICA_APPUNTAMENTO_QUERY = "" + " select  "
				+ " to_char(ag_agenda.dtmdataora, 'dd/mm/yyyy') dataSlot, to_char(ag_agenda.dtmdataora, 'hh24:mi') oraSlot,  "
				+ " ag_agenda.codcpi," + " de_cpi.strdescrizione descCpi," + " de_cpi.strindirizzo,"
				+ " de_cpi.strindirizzostampa," + " an_spi.strsiglaoperatore," + " de_ambiente.strdescrizione ambiente,"
				+ " ag_agenda.codstatoappuntamento, " + " ag_agenda.numminuti" + " from ag_agenda "
				+ " inner join de_cpi on (ag_agenda.codcpi = de_cpi.codcpi)"
				+ " inner join an_spi on (ag_agenda.prgspi = an_spi.prgspi)"
				+ " left join de_ambiente on (ag_agenda.prgambiente = de_ambiente.prgambiente)"
				+ " inner join ag_lavoratore ON (ag_agenda.prgappuntamento = ag_lavoratore.prgappuntamento )"
				+ " inner join an_lavoratore ON (ag_lavoratore.cdnlavoratore = an_lavoratore.cdnlavoratore)"
				+ " where an_lavoratore.strcodicefiscale = ? and"
				+ " trunc(ag_agenda.dtmdataora) = to_date( ? , 'dd/mm/yyyy') and"
				+ " ag_agenda.dtmdataora = to_date(to_char(ag_agenda.dtmdataora, 'dd/mm/yyyy') || ' ' || ? , 'dd/mm/yyyy hh24.mi.ss') and"
				+ " ag_agenda.codcpi = ? and" + " ag_agenda.codservizio IN ( SELECT strvalore" + " FROM TS_CONFIG_LOC "
				+ " WHERE num = 0 AND strcodrif = (select codprovinciasil from ts_generale) AND codtipoconfig = ? ) ";

		public static final String SEARCH_APPUNTAMENTO_QUERY = "" + " select  " + " ag_agenda.codcpi,"
				+ " ag_agenda.PRGAPPUNTAMENTO," + " ag_agenda.codstatoappuntamento,"
				+ " de_stato_appuntamento.flgattivo," + " ag_agenda.numkloagenda" + " from ag_agenda "
				+ " inner join ag_lavoratore ON (ag_agenda.prgappuntamento = ag_lavoratore.prgappuntamento )"
				+ " inner join an_lavoratore ON (ag_lavoratore.cdnlavoratore = an_lavoratore.cdnlavoratore)"
				+ " inner join de_stato_appuntamento ON (ag_agenda.CODSTATOAPPUNTAMENTO = de_stato_appuntamento.CODSTATOAPPUNTAMENTO)"
				+ " where an_lavoratore.strcodicefiscale = ? and"
				+ " trunc(ag_agenda.dtmdataora) = to_date( ? , 'dd/mm/yyyy') and"
				+ " ag_agenda.dtmdataora = to_date(to_char(ag_agenda.dtmdataora, 'dd/mm/yyyy') || ' ' || ? , 'dd/mm/yyyy hh24.mi.ss') and"
				+ " ag_agenda.codcpi = ? and" + " ag_agenda.codservizio IN ( SELECT strvalore" + " FROM TS_CONFIG_LOC "
				+ " WHERE num = 0 AND strcodrif = (select codprovinciasil from ts_generale) AND codtipoconfig = ? ) ";

		public static final String DELETE_APPUNTAMENTO_QUERY = "" + " UPDATE AG_AGENDA" + " SET NUMKLOAGENDA = ?,"
				+ " CODSTATOAPPUNTAMENTO = ?," + " CDNUTMOD = ?," + " DTMMOD =  trunc(SYSDATE)"
				+ " WHERE CODCPI = ? AND " + " PRGAPPUNTAMENTO = ? ";

		public static final String GET_PRENOTAZIONI_QUERY = "" + " SELECT " + " DE_CPI.codprovincia, "
				+ " to_char(AG_AGENDA.dtmdataora, 'dd/mm/yyyy') dataSlot, "
				+ " to_char(AG_AGENDA.dtmdataora, 'hh24:mi') oraSlot," + " AG_AGENDA.codcpi, "
				+ " DE_CPI.strdescrizione, " + " DE_CPI.strindirizzo, " + " DE_CPI.strIndirizzoStampa, "
				+ " AN_SPI.strSiglaOperatore, " + " DE_AMBIENTE.strdescrizione ambiente, "
				+ " AG_AGENDA.codstatoappuntamento, " + " AG_AGENDA.numMinuti, " + " AG_AGENDA.strTelMobileRif, "
				+ " AG_AGENDA.strEmailRif, " + " nvl( " + " (select tl.codtipoconfig from ts_config_loc tl "
				+ " where tl.strcodrif = (select codprovinciasil from ts_generale) and tl.num=0 and tl.strvalore=ag_agenda.codservizio and rownum=1), "
				+ " 'ALTRO') as codtipoconfig, " + " AG_AGENDA.prgappuntamento " + " FROM TS_GENERALE, AG_AGENDA "
				+ " INNER JOIN ag_lavoratore ON (ag_agenda.prgappuntamento = ag_lavoratore.prgappuntamento ) "
				+ " INNER JOIN an_lavoratore ON (ag_lavoratore.cdnlavoratore = an_lavoratore.cdnlavoratore) "
				+ " INNER JOIN DE_CPI ON (AG_AGENDA.codcpi = DE_CPI.codcpi) "
				+ " INNER JOIN DE_PROVINCIA ON (DE_CPI.codprovincia = DE_PROVINCIA.codprovincia) "
				+ " LEFT JOIN AN_SPI ON (AG_AGENDA.prgspi = AN_SPI.prgspi) "
				+ " LEFT JOIN DE_AMBIENTE ON (AG_AGENDA.prgambiente = DE_AMBIENTE.prgambiente)" + " WHERE "
				+ " AN_LAVORATORE.strcodicefiscale = ? AND "
				+ " ((NVL(TS_GENERALE.FLGPOLOREG, 'N') = 'N' AND DE_CPI.codprovincia = ts_generale.codprovinciasil) OR "
				+ "  (NVL(TS_GENERALE.FLGPOLOREG, 'N') = 'S' AND DE_PROVINCIA.codregione = ts_generale.codregionesil) ) AND "
				+ " trunc(ag_agenda.dtmdataora) > trunc( SYSDATE) ";

		public static final String GET_PRENOTAZIONI_QUERY_ON_LINE = "" + " AND (select tl.codtipoconfig "
				+ " from ts_config_loc tl " + " where tl.strcodrif = (select codprovinciasil from ts_generale) and "
				+ " tl.num = 0 and " + " tl.strvalore = ag_agenda.codservizio and " + " rownum = 1) IS NOT NULL";

		public static final String CHECK_PRESENZA_APPUNTAMENTI_CONTEMPORANEI = "" + " SELECT "
				+ " AG_LAVORATORE.PRGAPPUNTAMENTO " + " FROM "
				+ " AG_SLOT INNER JOIN AG_AGENDA ON (AG_SLOT.dtmdataora = AG_AGENDA.dtmdataora) "
				+ " INNER JOIN AG_LAVORATORE ON (AG_AGENDA.PRGAPPUNTAMENTO = AG_LAVORATORE.PRGAPPUNTAMENTO ) "
				+ " INNER JOIN DE_STATO_APPUNTAMENTO ON (AG_AGENDA.CODSTATOAPPUNTAMENTO = DE_STATO_APPUNTAMENTO.CODSTATOAPPUNTAMENTO) "
				+ " WHERE AG_SLOT.PRGSLOT = ? AND " + " AG_LAVORATORE.CDNLAVORATORE = ? AND"
				+ " DE_STATO_APPUNTAMENTO.FLGATTIVO = 'S' ";

		public static final String CONTROLLO_COERENZA_SIL = ""
				+ " SELECT codprovinciasil, codregionesil, nvl(flgpoloreg, 'N') as flgpoloreg " + " FROM ts_generale";

		public static final String CONTROLLO_COERENZA_SIL_REGIONALE = " SELECT codregione " + " from de_provincia "
				+ " where codprovincia = ?";

		public static final String SEARCH_ANPAL_APPUNTAMENTO_QUERY = "  SELECT   " + "  ag_agenda.codcpi, "
				+ "  ag_agenda.PRGAPPUNTAMENTO, " + "  ag_agenda.codstatoappuntamento, "
				+ "  de_stato_appuntamento.flgattivo, " + "  ag_agenda.numkloagenda, "
				+ "  to_char(ag_agenda.DTMDATAORA, 'dd/mm/yyyy') as dataAppuntamento" + "  from ag_agenda  "
				+ "  inner join ag_lavoratore ON (ag_agenda.prgappuntamento = ag_lavoratore.prgappuntamento ) "
				+ "  inner join an_lavoratore ON (ag_lavoratore.cdnlavoratore = an_lavoratore.cdnlavoratore) "
				+ "  inner join de_stato_appuntamento ON (ag_agenda.CODSTATOAPPUNTAMENTO = de_stato_appuntamento.CODSTATOAPPUNTAMENTO) "
				+ "  where  " + "  	ag_agenda.codcpi = ? and "
				+ "  	lower(nvl(ag_agenda.STRIDCOAP, '')) = lower(?) and " + "		ag_agenda.PRGAPPUNTAMENTO = ?";

	}
}
