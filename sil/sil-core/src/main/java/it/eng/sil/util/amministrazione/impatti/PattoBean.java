package it.eng.sil.util.amministrazione.impatti;

import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.base.SourceBeanException;
import com.engiweb.framework.configuration.ConfigSingleton;
import com.engiweb.framework.dbaccess.SQLStatements;
import com.engiweb.framework.dbaccess.sql.DataConnection;
import com.engiweb.framework.dbaccess.sql.DataField;
import com.engiweb.framework.error.EMFInternalError;
import com.engiweb.framework.util.QueryExecutor;
import com.engiweb.framework.util.QueryExecutorObject;
import com.inet.report.Engine;

import it.eng.afExt.utils.DateUtils;
import it.eng.afExt.utils.MessageCodes;
import it.eng.afExt.utils.TransactionQueryExecutor;
import it.eng.sil.Values;
import it.eng.sil.action.report.UtilsConfig;
import it.eng.sil.action.report.patto.Patto;
import it.eng.sil.bean.Documento;
import it.eng.sil.module.AccessoSemplificato;
import it.eng.sil.module.movimenti.constant.Properties;
import it.eng.sil.module.patto.bean.PacchettoAdulti;
import it.eng.sil.util.Utils;

public class PattoBean extends SourceBean implements EventoAmministrativo {
	public static final String DB_DAT_INIZIO = "DATSTIPULA";
	public static final String DB_DAT_FINE = "DATFINE";
	public static final String DB_COD_STATO_ATTO = "CODSTATOATTO";
	public static final String PRG_DICH_DISPO = "PRGDICHDISPONIBILITA";
	public static final String PRG_PATTO_LAV = "PRGPATTOLAVORATORE";
	public static final String NUMKLO_PATTO_LAV = "NUMKLOPATTOLAVORATORE";
	public static final String PRG_STATO_OCCUPAZ = "PRGSTATOOCCUPAZ";
	public static final String COD_MOTIVO_FINE_ATTO = "codMotivoFineAtto";
	public static final String DB_PRG_PATTO = "PRGPATTOLAVORATORE";
	public static final String DB_MISURE_GARANZIA_GIOVANI = "MGG";
	public static final String DB_MISURE_NUOVA_GARANZIA_GIOVANI = "NGG";
	public static final String DB_MISURE_DOTE = "DOTE";
	public static final String DB_MISURE_DOTE_IA = "DOTE_IA";
	public static final String STATO_PROTOCOLLATO = "PR";
	public static final String DB_MISURE_OVER_30 = "MGO30";
	public static final String DB_MISURE_OVER_45 = "MGO45";
	public static final String DB_MISURE_L14 = "L14";
	public static final String DB_MISURE_FRD = "FRD";
	public static final String DB_MISURE_INCLUSIONE_ATTIVA = "MINAT";
	public static final String DB_MISURE_GARANZIA_GIOVANI_UMBRIA = "MGGU";
	public static final String DB_MISURE_NUOVA_GARANZIA_GIOVANI_UMBRIA = "NGGU";
	public static final String DB_MISURE_ASSEGNO_RICOLLOCAZIONE = "ADR";
	public static final String DB_MISURE_INTERVENTO_OCCUPAZIONE = "POC";
	public static final String DB_ADESIONE_NUOVO_PROGRAMMA = "ANP";
	public static final BigDecimal ISEE_PACCHETTO_ADULTI = new BigDecimal(6000);
	public static final String OVER30_MONO_TIPO_AZIONE_PATTO = "O3";
	public static final String OVER45_MONO_TIPO_AZIONE_PATTO = "O4";
	public static final String INCLUSIONEATTIVA_MONO_TIPO_AZIONE_PATTO = "IN";
	public static final String AZ_SIFER_C06 = "C06";
	public static final String AZ_SIFER_C07 = "C07";
	public static final String PACCHETTO_A = "PA";
	public static final String PACCHETTO_B = "PB";
	public static final String PACCHETTO_C = "PC";
	public static final String PACCHETTO_D = "PD";
	public static final String PACCHETTO_INCLUSIONE = "IN";
	public static final String PACCHETTO_ORIENTAMENTO = "OR";
	public static final String PACCHETTO_GG_UMBRIA = "GU";
	public static final String AZ_SIFER_A01 = "A01";
	public static final String AZ_SIFER_A02 = "A02";
	public static final String AZ_SIFER_A03 = "A03";
	public static final String AZ_SIFER_A05 = "A05";
	public final static String DE_IMPE = "DE_IMPE";
	public final static String OR_PER = "OR_PER";
	public final static String CONF_COLLEGA_AZ_PATTO = "INS_PTAZ";
	public final static String CONF_COLLEGA_AZ_ENTE = "AZENTE";
	private StatoOccupazionaleBean statoOccupazionale;
	public static final String DB_PATTO_PROGRAMMA_PERSONALIZZATO = "L14";
	public static final String DB_PATTO_PIANO_INTERVENTO_OCC = "POC";
	public static final String DB_COD_SERVIZIO_186 = "186";
	public static final String DB_COD_SERVIZIO_NGG = "NGG";
	public static final String DB_DAT_AVVIO_AZIONE = "datAvvioAzione";
	public static final String DB_PATTO_PROGRAMMA_PERSONALIZZATO_2018 = "L14_2018";
	public static final String ANNO_PROGRAMMA_PERSONALIZZATO_2018 = "2018";
	public static final String DB_PATTO_PROGRAMMA_PERSONALIZZATO_2019 = "L14_2019";
	public static final String ANNO_PROGRAMMA_PERSONALIZZATO_2019 = "2019";
	public static final String BANDO_UMBRIA_ATTIVA = "UMBAT";
	public static final String TIPO_SVANTAGGIO_AREA2 = "17";
	public static final String TIPO_SVANTAGGIO_AREA1 = "18";

	public static String QUERY_STRING_DATA_ADESIONE_PORTALE = "select to_char(max(bd.DATADESIONE), 'dd/mm/yyyy') SDATADESIONE from bd_adesione bd "
			+ " inner join an_lavoratore lav on bd.strcodicefiscale = lav.strcodicefiscale "
			+ " where lav.cdnlavoratore = ? and trunc(bd.DATADESIONE) <= to_date(?, 'dd/mm/yyyy') and bd.codbandoprogramma = ?";

	private static String QUERY_SOGGETTO_ACCREDITATO_PROGRAMMIA_PATTO = "select strentecodicefiscale from am_programma_ente where prgpattolavoratore = ? and prgcolloquio = ? order by dtmins desc";

	private static String QUERY_INFO_PROGRAMMIA_PATTO = "select to_char(datColloquio, 'dd/mm/yyyy') datColloquio, to_char(datFineProgramma, 'dd/mm/yyyy') datFineProgramma from or_colloquio where prgcolloquio = ?";

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(Patto.class.getName());

	public PattoBean(SourceBean sb) throws SourceBeanException {
		super(sb);
	}

	public SourceBean getSource() {
		try {
			return this;
		} catch (Exception e) {
			return null;
		}
	}

	public int getTipoEventoAmministrativo() {
		return EventoAmministrativo.PATTO;
	}

	public StatoOccupazionaleBean getStatoOccupazionale() {
		return this.statoOccupazionale;
	}

	public void setStatoOccupazionale(StatoOccupazionaleBean newStatoOccupazionale) {
		this.statoOccupazionale = newStatoOccupazionale;
	}

	public String getDataInizio() {
		return (String) getAttribute(PattoBean.DB_DAT_INIZIO);
	}

	public String getDataAvvioAzione() {
		return (String) getAttribute(PattoBean.DB_DAT_AVVIO_AZIONE);
	}

	public String getDataFine() {
		return (String) getAttribute(PattoBean.DB_DAT_FINE);
	}

	public String getPrgDichDisponibilita() {
		return getAttribute(PattoBean.PRG_DICH_DISPO).toString();
	}

	public BigDecimal getPrgDichDisponibilitaBigDec() {
		return new BigDecimal(getAttribute(PattoBean.PRG_DICH_DISPO).toString());
	}

	public String getPrgPattoLav() {
		return getAttribute(PattoBean.PRG_PATTO_LAV).toString();
	}

	public BigDecimal getPrgPattoLavBigDec() {
		return new BigDecimal(getAttribute(PattoBean.PRG_PATTO_LAV).toString());
	}

	public BigDecimal getNumklo() {
		Object o = getAttribute(PattoBean.NUMKLO_PATTO_LAV);
		if (o instanceof String)
			return new BigDecimal((String) o);
		else if (o instanceof BigDecimal)
			return (BigDecimal) o;
		else
			return null;
	}

	public BigDecimal getPrgStatoOccupaz() {
		Object o = getAttribute(PattoBean.PRG_STATO_OCCUPAZ);
		if (o instanceof BigDecimal)
			return (BigDecimal) o;
		else if (o instanceof String)
			return new BigDecimal(o.toString());
		else
			return null;
	}

	public String getCodMotivoFineAtto() {
		String codMotivoFine = (String) getAttribute(COD_MOTIVO_FINE_ATTO);
		if (codMotivoFine == null)
			return "";
		else
			return codMotivoFine;
	}

	public boolean isProtocollato() {
		String codStatoAtto = containsAttribute(PattoBean.DB_COD_STATO_ATTO)
				? (String) getAttribute(PattoBean.DB_COD_STATO_ATTO)
				: "";
		if (codStatoAtto.toUpperCase().equals("PR"))
			return true;
		else
			return false;
	}

	public boolean isDataFutura() throws Exception {
		String dataStipula = getDataInizio();
		String oggi = DateUtils.getNow();
		int resultCompare = DateUtils.compare(dataStipula, oggi);
		if (resultCompare > 0) {
			return true;
		} else {
			return false;
		}
	}

	public boolean isPrecNormativa(String dataRifNormativa) throws Exception {
		String dataStipula = getDataInizio();
		int resultCompare = DateUtils.compare(dataStipula, dataRifNormativa);
		if (resultCompare < 0) {
			return true;
		} else {
			return false;
		}
	}

	public static boolean is180Giorni_186(String dataInizioProgramma, String dataAvvio) throws Exception {
		if (dataInizioProgramma == null || dataInizioProgramma.equals("") || dataAvvio == null || dataAvvio.equals("")
				|| DateUtils.compare(dataInizioProgramma, dataAvvio) > 0) {
			return false;
		} else {
			int mesi = DateUtils.monthsBetween150(dataInizioProgramma, dataAvvio);
			if (mesi <= 6) {
				return true;
			} else {
				return false;
			}
		}
	}

	public String getDescrizione() {
		return "Patto";
	}

	/**
	 * Cerca un evento amministrativo in una lista di eventi presa in input
	 * 
	 * @param prgEvento
	 *            progressivo dell'evento da ricercare
	 * @param eventi
	 *            eventi dai quali cercare
	 * @return un SourceBean che rappresenta l'evento trovato
	 * @throws ControlliException
	 *             lanciata nel caso in cui il movimento da cercare non e' presente nella lista
	 * @author Togna Cosimo
	 */
	public SourceBean cercaEventoAmministrativo(Object prgEvento, java.util.Collection eventi)
			throws ControlliException {
		PattoBean patto = null;
		for (java.util.Iterator iterator = eventi.iterator(); iterator.hasNext();) {
			Object tmpEvento = iterator.next();
			if (tmpEvento instanceof PattoBean) {
				patto = (PattoBean) tmpEvento;
				BigDecimal prgEventoCorrente = (BigDecimal) patto.getAttribute(PattoBean.DB_PRG_PATTO);
				if (prgEventoCorrente.intValue() == ((BigDecimal) prgEvento).intValue()) {
					return (patto);
				}
			}
		}
		// lancio l'eccezione se non trovo l'evento desiderato
		throw new ControlliException(MessageCodes.EventoAmministrativo.SEARCH_ERROR);
	}

	/**
	 * @param object
	 * @param executor
	 * @return
	 */
	public static List getPatti(Object cdnLavoratore, TransactionQueryExecutor executor) throws Exception {
		Object params[] = { cdnLavoratore, "01/01/0001" };
		SourceBean row = null;

		row = (SourceBean) executor.executeQuery("GET_PATTI_LAV_DA", params, "SELECT");
		if (row == null)
			throw new Exception("impossibile leggere i patti del lavoraotore");
		return new ArrayList(row.getAttributeAsVector("ROW"));
	}

	public static boolean inserisciAzione(BigDecimal prgPercorsoNew, BigDecimal prgcolloquio, String codEsito,
			String codEsitoRendicont, String dataStimata, BigDecimal prgazioni, TransactionQueryExecutor txExec,
			BigDecimal cdnUtente) throws Exception {
		boolean isOK = false;
		Object[] params = new Object[] { prgPercorsoNew, prgcolloquio, dataStimata, prgazioni, codEsito,
				codEsitoRendicont, cdnUtente, cdnUtente };
		Boolean res = (Boolean) txExec.executeQuery("WS_RINNOVO_INSERISCI_AZIONE", params, "INSERT");
		if (res != null && res.booleanValue()) {
			isOK = true;
		}
		return isOK;
	}

	public static BigDecimal getProgressivoPercorso(TransactionQueryExecutor txExec) throws Exception {
		BigDecimal progressivo = null;
		SourceBean row = (SourceBean) txExec.executeQuery("OR_PERCORSO_CONCORDATO_NEXTVAL", null, "SELECT");
		if (row != null) {
			row = (row.containsAttribute("ROW") ? (SourceBean) row.getAttribute("ROW") : row);
			progressivo = (BigDecimal) row.getAttribute("do_nextval");

		}
		return progressivo;
	}

	public static boolean collegaAzionePatto(Object prgPatto, String codLstTab, BigDecimal prgPercorso,
			TransactionQueryExecutor txExec) throws Exception {
		boolean isOK = false;
		Object[] params = new Object[] { prgPatto, codLstTab, prgPercorso };
		Boolean res = (Boolean) txExec.executeQuery("WS_RINNOVO_COLLEGA_AZIONE_PATTO", params, "INSERT");
		if (res != null && res.booleanValue()) {
			isOK = true;
		}
		return isOK;
	}

	public static BigDecimal getProgressivoColloquio(TransactionQueryExecutor txExec) throws Exception {
		BigDecimal progressivo = null;
		SourceBean row = (SourceBean) txExec.executeQuery("OR_COLLOQUIO_NEXTVAL", null, "SELECT");
		if (row != null) {
			row = (row.containsAttribute("ROW") ? (SourceBean) row.getAttribute("ROW") : row);
			progressivo = (BigDecimal) row.getAttribute("do_nextval");

		}
		return progressivo;
	}

	public static boolean inserisciColloquio(BigDecimal prgColloquio, Object cdnLav, String codServizio,
			String dataColloquio, String codCpi, String strNote, TransactionQueryExecutor txExec, BigDecimal cdnUtente,
			BigDecimal operatoreSPI) throws Exception {
		boolean isOK = false;
		Object[] params = new Object[] { prgColloquio, cdnLav, dataColloquio, codServizio, operatoreSPI, codCpi,
				strNote, cdnUtente, cdnUtente };
		Boolean res = (Boolean) txExec.executeQuery("WS_RINNOVO_INSERISCI_COLLOQUIO", params, "INSERT");
		if (res != null && res.booleanValue()) {
			isOK = true;
		}
		return isOK;
	}

	public static String getServizioConfermaPeriodica(String servizioConfermaPeriodica, TransactionQueryExecutor txExec)
			throws Exception {
		String codServizio = null;
		Object[] params = new Object[] { servizioConfermaPeriodica };
		SourceBean row = (SourceBean) txExec.executeQuery("WS_GET_SERVIZIO_AZIONE_CONFERMA_PERIODICA", params,
				"SELECT");
		if (row != null) {
			row = (row.containsAttribute("ROW") ? (SourceBean) row.getAttribute("ROW") : row);
			codServizio = (String) row.getAttribute("codservizio");
		}
		return codServizio;
	}

	public static boolean inserisciSchedaColloquio(BigDecimal prgColloquio, TransactionQueryExecutor txExec)
			throws Exception {
		boolean isOK = false;
		Object[] params = new Object[] { prgColloquio };
		Boolean res = (Boolean) txExec.executeQuery("WS_RINNOVO_INSERISCI_SCHEDA_COLLOQUIO", params, "INSERT");
		if (res != null && res.booleanValue()) {
			isOK = true;
		}
		return isOK;
	}

	public static BigDecimal getOperatoreAdmin(TransactionQueryExecutor txExec) throws Exception {
		BigDecimal progressivo = null;
		SourceBean row = (SourceBean) txExec.executeQuery("GET_OPERATORE_ADMIN_SPI", null, "SELECT");
		if (row != null) {
			row = (row.containsAttribute("ROW") ? (SourceBean) row.getAttribute("ROW") : row);
			progressivo = (BigDecimal) row.getAttribute("prgspi");

		}
		return progressivo;
	}

	public static SourceBean caricaPatto(Object prgPattoLav) throws Exception {
		Object param[] = new Object[1];
		param[0] = prgPattoLav;
		SourceBean rowPatto = (SourceBean) QueryExecutor.executeQuery("SELECT_AM_PATTO_LAV_ST", param, "SELECT",
				Values.DB_SIL_DATI);
		return rowPatto;
	}

	public static SourceBean caricaInfoLavoratore(String encryptKey, Object cdnLavoratore, String dataRif) {
		SourceBean row = null;
		Object params[] = new Object[6];
		params[0] = dataRif;
		params[1] = encryptKey;
		params[2] = dataRif;
		params[3] = dataRif;
		params[4] = cdnLavoratore;
		params[5] = dataRif;
		row = (SourceBean) QueryExecutor.executeQuery("GET_INFO_LAVORATORE_PACCHETTO_ADULTI", params, "SELECT",
				Values.DB_SIL_DATI);
		return row;
	}

	public static SourceBean caricaInfoAnzianitaLavoratore(Object cdnLavoratore, String dataRif) {
		SourceBean row = null;
		Object params[] = new Object[4];
		params[0] = dataRif;
		params[1] = dataRif;
		params[2] = cdnLavoratore;
		params[3] = dataRif;
		row = (SourceBean) QueryExecutor.executeQuery("GET_INFO_LAVORATORE_MGGU", params, "SELECT", Values.DB_SIL_DATI);
		return row;
	}

	public static SourceBean caricaInfoLavoratore(Object cdnLavoratore) {
		SourceBean row = null;
		Object params[] = new Object[1];
		params[0] = cdnLavoratore;
		row = (SourceBean) QueryExecutor.executeQuery("SELECT_DATI_AN_LAVORATORE_GGU", params, "SELECT",
				Values.DB_SIL_DATI);
		return row;
	}

	public static SourceBean caricaPrestazioniAssociate(Object prgPattoLavoratore, String codServizio) {
		SourceBean row = null;

		Object params[] = new Object[9];
		params[0] = codServizio;
		params[1] = codServizio;
		params[2] = prgPattoLavoratore;
		params[3] = codServizio;
		params[4] = prgPattoLavoratore;
		params[5] = codServizio;
		params[6] = prgPattoLavoratore;
		params[7] = codServizio;
		params[8] = codServizio;

		row = (SourceBean) QueryExecutor.executeQuery("SELECT_PRESTAZIONI_ASSOCIATE", params, "SELECT",
				Values.DB_SIL_DATI);
		return row;
	}

	public static SourceBean caricaAzioneA02GGU(Object cdnLavoratore) {
		SourceBean row = null;
		Object params[] = new Object[1];
		params[0] = cdnLavoratore;
		row = (SourceBean) QueryExecutor.executeQuery("SELECT_A02_GGU", params, "SELECT", Values.DB_SIL_DATI);
		return row;
	}

	public static int checkPacchettoAdultiGaranziaGiovani(String codMonoPacchetto, String flgFormazione,
			String codazionesifer) {
		int codice = -1;
		boolean pacchettoOrientamento = false;
		if ((codMonoPacchetto.equalsIgnoreCase(PACCHETTO_ORIENTAMENTO))
				&& (flgFormazione.equalsIgnoreCase(Values.FLAG_TRUE))
				&& (codazionesifer.equalsIgnoreCase(AZ_SIFER_A01) || codazionesifer.equalsIgnoreCase(AZ_SIFER_A02)
						|| codazionesifer.equalsIgnoreCase(AZ_SIFER_A03)
						|| codazionesifer.equalsIgnoreCase(AZ_SIFER_A05))) {
			pacchettoOrientamento = true;
		}

		if (codMonoPacchetto.equalsIgnoreCase(PACCHETTO_A) || codMonoPacchetto.equalsIgnoreCase(PACCHETTO_B)
				|| codMonoPacchetto.equalsIgnoreCase(PACCHETTO_C) || codMonoPacchetto.equalsIgnoreCase(PACCHETTO_D)
				|| codMonoPacchetto.equalsIgnoreCase(PACCHETTO_INCLUSIONE) || pacchettoOrientamento) {
			return MessageCodes.Patto.ERR_POLITICHE_ATTIVE_MISURA;
		}

		return codice;
	}

	public static int checkPacchettoAdultiOver3045INA(String codMonoPacchetto, String flgMisurayei) {
		int codice = -1;
		if (flgMisurayei.equalsIgnoreCase(Values.FLAG_TRUE)
				|| codMonoPacchetto.equalsIgnoreCase(PattoBean.PACCHETTO_GG_UMBRIA)) {
			return MessageCodes.Patto.ERR_POLITICHE_ATTIVE_MISURA;
		}

		return codice;
	}

	public static int checkazioniPattoGGU(String flgMisurayei) {
		int codice = -1;
		if (flgMisurayei.equalsIgnoreCase(Values.FLAG_TRUE)) {
			return MessageCodes.Patto.ERR_POLITICHE_ATTIVE_MISURA;
		}

		return codice;
	}

	public static int checkazioniPattoGG(String codMonoPacchetto) {
		int codice = -1;
		if (codMonoPacchetto.equalsIgnoreCase(PattoBean.PACCHETTO_GG_UMBRIA)) {
			return MessageCodes.Patto.ERR_POLITICHE_ATTIVE_MISURA;
		}

		return codice;
	}

	public static int checkLimiteAzioniPacchettoAdulti(Vector<String> programmi, String codMonoPacchetto,
			String codazionesifer) {
		int codice = -1;
		boolean pacchettoAdulti = false;

		if ((codMonoPacchetto != null) && (codMonoPacchetto.equalsIgnoreCase(PattoBean.PACCHETTO_A)
				|| codMonoPacchetto.equalsIgnoreCase(PattoBean.PACCHETTO_B)
				|| codMonoPacchetto.equalsIgnoreCase(PattoBean.PACCHETTO_C)
				|| codMonoPacchetto.equalsIgnoreCase(PattoBean.PACCHETTO_D)
				|| codMonoPacchetto.equalsIgnoreCase(PattoBean.PACCHETTO_INCLUSIONE))) {
			pacchettoAdulti = true;
		}

		SourceBean row = null;
		row = (SourceBean) QueryExecutor.executeQuery("GET_LIMITI_AZIONI_PATTO_PACCHETTO_ADULTI", null, "SELECT",
				Values.DB_SIL_DATI);
		if (row != null) {
			row = (row.containsAttribute("ROW") ? (SourceBean) row.getAttribute("ROW") : row);
			BigDecimal numC06Over30 = (BigDecimal) row.getAttribute("numC06MGO30");
			BigDecimal numC07Over30 = (BigDecimal) row.getAttribute("numC07MGO30");
			BigDecimal numC06Over45 = (BigDecimal) row.getAttribute("numC06MGO45");
			BigDecimal numC07Over45 = (BigDecimal) row.getAttribute("numC07MGO45");
			BigDecimal numC06MInat = (BigDecimal) row.getAttribute("numC06MINAT");
			BigDecimal numMaxC06Over30 = (BigDecimal) row.getAttribute("NUMC06OVER30");
			BigDecimal numMaxC07Over30 = (BigDecimal) row.getAttribute("NUMC07OVER30");
			BigDecimal numMaxC06Over45 = (BigDecimal) row.getAttribute("NUMC06OVER45");
			BigDecimal numMaxC07Over45 = (BigDecimal) row.getAttribute("NUMC07OVER45");
			BigDecimal numMaxC06MInat = (BigDecimal) row.getAttribute("NUMC06INCATT");

			if (PattoBean.checkMisuraProgramma(programmi, PattoBean.DB_MISURE_OVER_30) && pacchettoAdulti) {
				if (codazionesifer.equalsIgnoreCase(PattoBean.AZ_SIFER_C06)) {
					if (numC06Over30 != null && numMaxC06Over30 != null
							&& numC06Over30.compareTo(numMaxC06Over30) >= 0) {
						return MessageCodes.Patto.ERR_LIMITE_ASSOCIAZIONE_CO6_PATTO_OVER30;
					}
				} else {
					if (codazionesifer.equalsIgnoreCase(PattoBean.AZ_SIFER_C07)) {
						if (numC07Over30 != null && numMaxC07Over30 != null
								&& numC07Over30.compareTo(numMaxC07Over30) >= 0) {
							return MessageCodes.Patto.ERR_LIMITE_ASSOCIAZIONE_CO7_PATTO_OVER30;
						}
					}
				}
			} else {
				if (PattoBean.checkMisuraProgramma(programmi, PattoBean.DB_MISURE_OVER_45) && pacchettoAdulti) {
					if (codazionesifer.equalsIgnoreCase(PattoBean.AZ_SIFER_C06)) {
						if (numC06Over45 != null && numMaxC06Over45 != null
								&& numC06Over45.compareTo(numMaxC06Over45) >= 0) {
							return MessageCodes.Patto.ERR_LIMITE_ASSOCIAZIONE_CO6_PATTO_OVER45;
						}
					} else {
						if (codazionesifer.equalsIgnoreCase(PattoBean.AZ_SIFER_C07)) {
							if (numC07Over45 != null && numMaxC07Over45 != null
									&& numC07Over45.compareTo(numMaxC07Over45) >= 0) {
								return MessageCodes.Patto.ERR_LIMITE_ASSOCIAZIONE_CO7_PATTO_OVER45;
							}
						}
					}
				} else {
					if (PattoBean.checkMisuraProgramma(programmi, PattoBean.DB_MISURE_INCLUSIONE_ATTIVA)
							&& pacchettoAdulti) {
						if (codazionesifer.equalsIgnoreCase(PattoBean.AZ_SIFER_C06)) {
							if (numC06MInat != null && numMaxC06MInat != null
									&& numC06MInat.compareTo(numMaxC06MInat) >= 0) {
								return MessageCodes.Patto.ERR_LIMITE_ASSOCIAZIONE_CO6_PATTO_INATT;
							}
						}
					}
				}
			}
		}
		return codice;
	}

	public static int checkProtocollazionePOC(Object prgPattoLavoratore, Vector serviziProgrammi) throws Exception {
		int codiceErrore = -1;
		int nSize = 0;
		if (serviziProgrammi != null) {
			nSize = serviziProgrammi.size();
			if (nSize <= 0) {
				codiceErrore = MessageCodes.Patto.ERR_PRESTAZIONI_ASSOCIATE_EMPTY;
				return codiceErrore;
			}
		} else {
			codiceErrore = MessageCodes.Patto.ERR_PRESTAZIONI_ASSOCIATE_EMPTY;
			return codiceErrore;
		}
		for (int i = 0; i < nSize; i++) {
			String codServizio = (String) serviziProgrammi.get(i);
			SourceBean rowPrestazioni = PattoBean.caricaPrestazioniAssociate(prgPattoLavoratore, codServizio);
			if (rowPrestazioni != null) {
				rowPrestazioni = (rowPrestazioni.containsAttribute("ROW")
						? (SourceBean) rowPrestazioni.getAttribute("ROW")
						: rowPrestazioni);
				BigDecimal numPrestazioni = (BigDecimal) rowPrestazioni.getAttribute("numPrestazioni");
				if (numPrestazioni != null && numPrestazioni.intValue() == 0) {
					codiceErrore = MessageCodes.Patto.ERR_PRESTAZIONI_ASSOCIATE_EMPTY;
					return codiceErrore;
				}
			} else
				throw new Exception("Errore nel recupero conteggio prestazioni associate al patto");
		}
		return codiceErrore;
	}

	public static int checkProtocollazioneMGGU(Object cdnLavoratore) throws Exception {
		int codiceErrore = -1;
		String dataNascita = "";
		String dataAdesioneGG = "";
		String codRegioneSil = "";
		String codRegioneRes = "";
		BigDecimal numAzioneA02GGU = null;
		SourceBean rowLavInfo = PattoBean.caricaInfoLavoratore(cdnLavoratore);
		if (rowLavInfo != null) {
			Vector rows = rowLavInfo.getAttributeAsVector("ROW");
			if ((rows != null) && !rows.isEmpty()) {
				SourceBean row = (SourceBean) rows.get(0);
				dataNascita = (String) row.getAttribute("DATNASC");
				dataAdesioneGG = (String) row.getAttribute("DATADESIONEGG");
				codRegioneSil = (String) row.getAttribute("CODREGIONESIL");
				codRegioneRes = (String) row.getAttribute("CODREGIONERES");
			}
		}

		if (dataAdesioneGG == null || dataAdesioneGG.equals("")) {
			codiceErrore = MessageCodes.Patto.ERR_GGU_DATA_ADESIONE;
			return codiceErrore;
		}
		if (Controlli.maggioreDiUno(dataNascita, 30, dataAdesioneGG)) {
			codiceErrore = MessageCodes.Patto.ERR_ETA_LAV_PATTO_MISURA;
			return codiceErrore;
		}
		if (!codRegioneSil.equalsIgnoreCase(codRegioneRes)) {
			String mesiAnzianitaDid12 = null;
			SourceBean rowLav = PattoBean.caricaInfoAnzianitaLavoratore(cdnLavoratore, dataAdesioneGG);
			if (rowLav != null) {
				rowLav = rowLav.containsAttribute("ROW") ? (SourceBean) rowLav.getAttribute("ROW") : rowLav;
				mesiAnzianitaDid12 = (String) rowLav.getAttribute("ANZIANITADID12MESI");
			}
			if ((mesiAnzianitaDid12 == null) || (mesiAnzianitaDid12.equals(""))
					|| (new Integer(mesiAnzianitaDid12).intValue() <= 0)) {
				codiceErrore = MessageCodes.Patto.ERR_GGU_RESIDENZA;
				return codiceErrore;
			}
		}
		SourceBean rowA02 = PattoBean.caricaAzioneA02GGU(cdnLavoratore);
		if (rowA02 != null) {
			rowA02 = rowA02.containsAttribute("ROW") ? (SourceBean) rowA02.getAttribute("ROW") : rowA02;
			numAzioneA02GGU = (BigDecimal) rowA02.getAttribute("numA02GGU");
		}

		if (numAzioneA02GGU == null || numAzioneA02GGU.intValue() == 0) {
			codiceErrore = MessageCodes.Patto.ERR_GGU_A02;
			return codiceErrore;
		}

		return codiceErrore;
	}

	public static int checkProtocollazioneAccordoMGGU(Object cdnLavoratore) throws Exception {
		int codiceErrore = -1;
		String dataNascita = "";
		String dataAdesioneGG = "";
		String codRegioneSil = "";
		String codRegioneRes = "";
		BigDecimal numAzioneA02GGU = null;
		SourceBean rowLavInfo = PattoBean.caricaInfoLavoratore(cdnLavoratore);
		if (rowLavInfo != null) {
			Vector rows = rowLavInfo.getAttributeAsVector("ROW");
			if ((rows != null) && !rows.isEmpty()) {
				SourceBean row = (SourceBean) rows.get(0);
				dataNascita = (String) row.getAttribute("DATNASC");
				dataAdesioneGG = (String) row.getAttribute("DATADESIONEGG");
				codRegioneSil = (String) row.getAttribute("CODREGIONESIL");
				codRegioneRes = (String) row.getAttribute("CODREGIONERES");
			}
		}

		if (dataAdesioneGG == null || dataAdesioneGG.equals("")) {
			codiceErrore = MessageCodes.Patto.ERR_GGU_DATA_ADESIONE;
			return codiceErrore;
		}
		if (Controlli.maggioreDiUno(dataNascita, 30, dataAdesioneGG)) {
			codiceErrore = MessageCodes.Patto.ERR_ETA_LAV_PATTO_MISURA;
			return codiceErrore;
		}
		if (!codRegioneSil.equalsIgnoreCase(codRegioneRes)) {
			codiceErrore = MessageCodes.Patto.ERR_ACCORDO_GGU_RESIDENZA;
			return codiceErrore;
		}
		SourceBean rowA02 = PattoBean.caricaAzioneA02GGU(cdnLavoratore);
		if (rowA02 != null) {
			rowA02 = rowA02.containsAttribute("ROW") ? (SourceBean) rowA02.getAttribute("ROW") : rowA02;
			numAzioneA02GGU = (BigDecimal) rowA02.getAttribute("numA02GGU");
		}

		if (numAzioneA02GGU == null || numAzioneA02GGU.intValue() == 0) {
			codiceErrore = MessageCodes.Patto.ERR_GGU_A02;
			return codiceErrore;
		}

		return codiceErrore;
	}

	public static Vector loadProfiliCalcolati(Object cdnLavoratore) throws Exception {
		Object params[] = new Object[1];
		params[0] = cdnLavoratore;
		SourceBean res = (SourceBean) QueryExecutor.executeQuery("GET_PROFILING_PATTO_FROM_PROFILO_LAVORATORE", params,
				"SELECT", Values.DB_SIL_DATI);
		if (res != null)
			return res.getAttributeAsVector("ROW");
		else
			throw new Exception("impossibile leggere i profili calcolati del lavoratore " + cdnLavoratore.toString());
	}

	public static void collegaAzioniImpegniPatto(BigDecimal prgPattoLav, TransactionQueryExecutor transExec)
			throws Exception {
		int codiceErrore;

		Object paramsAz[] = new Object[1];
		paramsAz[0] = prgPattoLav;
		SourceBean sb = (SourceBean) transExec.executeQuery("GET_OR_PER_NON_LEGATE_BY_PRGPATTO", paramsAz, "SELECT");
		Vector rowsAz = sb.getAttributeAsVector("ROW");
		Vector<String> programmi = PattoBean.checkProgrammi(prgPattoLav, transExec);

		if ((rowsAz != null) && !rowsAz.isEmpty()) {
			int nSizeAz = rowsAz.size();
			UtilsConfig utility = new UtilsConfig(PacchettoAdulti.CONFIG_PA);
			String config = utility.getConfigurazioneDefault_Custom();
			for (int i = 0; i < nSizeAz; i++) {
				codiceErrore = -1;
				String flgMisurayei = "";
				String flgAdesioneGG = "";
				String codMonoPacchetto = "";
				SourceBean az = (SourceBean) rowsAz.get(i);
				BigDecimal prgPercorsoCurr = (BigDecimal) az.getAttribute("prgpercorso");
				SourceBean azione = getDataAzione(prgPercorsoCurr, transExec);
				if (azione != null) {
					flgMisurayei = azione.containsAttribute("ROW.flg_misurayei")
							? azione.getAttribute("ROW.flg_misurayei").toString()
							: "";
					flgAdesioneGG = azione.containsAttribute("ROW.flgAdesioneGG")
							? azione.getAttribute("ROW.flgAdesioneGG").toString()
							: "";
					codMonoPacchetto = azione.containsAttribute("ROW.CODMONOPACCHETTO")
							? azione.getAttribute("ROW.CODMONOPACCHETTO").toString()
							: "";
					String flgFormazioneAzione = azione.containsAttribute("ROW.flgFormazione")
							? azione.getAttribute("ROW.flgFormazione").toString()
							: "";
					String codazionesifer = azione.containsAttribute("ROW.codazionesifer")
							? azione.getAttribute("ROW.codazionesifer").toString()
							: "";

					if ((flgMisurayei.equalsIgnoreCase("S") || flgAdesioneGG.equalsIgnoreCase("S"))
							&& (azione.getAttribute("ROW.PRGPATTODISASSOCIATOFORMAZIONE") != null)) {
						BigDecimal prgPattoDiAssociato = (BigDecimal) azione
								.getAttribute("ROW.PRGPATTODISASSOCIATOFORMAZIONE");
						if (prgPattoLav != null && prgPattoDiAssociato.compareTo(prgPattoLav) != 0) {
							codiceErrore = MessageCodes.Patto.ERR_AZIONE_PRECEDENTEMENTE_ASSOCIATA_PATTO;
						}
					}

					if (codiceErrore < 0) {
						if (PattoBean.checkMisuraProgramma(programmi, PattoBean.DB_MISURE_GARANZIA_GIOVANI) || PattoBean
								.checkMisuraProgramma(programmi, PattoBean.DB_MISURE_GARANZIA_GIOVANI_UMBRIA)) {
							if (config.equals(Properties.CUSTOM_CONFIG)) {
								codiceErrore = PattoBean.checkPacchettoAdultiGaranziaGiovani(codMonoPacchetto,
										flgFormazioneAzione, codazionesifer);
							}
							if (PattoBean.checkMisuraProgramma(programmi,
									PattoBean.DB_MISURE_GARANZIA_GIOVANI_UMBRIA)) {
								if (codiceErrore < 0) {
									codiceErrore = PattoBean.checkazioniPattoGGU(flgMisurayei);
								}
							} else {
								// patto con misura MGG
								if (codiceErrore < 0) {
									codiceErrore = PattoBean.checkazioniPattoGG(codMonoPacchetto);
									if (codiceErrore > 0) {
									}
								}
							}
						}
					}

					if (codiceErrore < 0) {
						if (PattoBean.checkMisuraProgramma(programmi, PattoBean.DB_MISURE_OVER_30)
								|| PattoBean.checkMisuraProgramma(programmi, PattoBean.DB_MISURE_OVER_45)
								|| PattoBean.checkMisuraProgramma(programmi, PattoBean.DB_MISURE_INCLUSIONE_ATTIVA)) {
							codiceErrore = PattoBean.checkPacchettoAdultiOver3045INA(codMonoPacchetto, flgMisurayei);
							/*
							 * if (codiceErrore < 0) { if ( (flgFormazioneAzione.equalsIgnoreCase(Values.FLAG_TRUE)) &&
							 * (codazionesifer.equalsIgnoreCase(PattoBean.AZ_SIFER_C06) ||
							 * codazionesifer.equalsIgnoreCase(PattoBean.AZ_SIFER_C07)) ) { codiceErrore =
							 * PattoBean.checkLimiteAzioniPacchettoAdulti(programmi, codMonoPacchetto, codazionesifer);
							 * } }
							 */
						}
					}

					if (codiceErrore < 0) {

						Object params[] = new Object[5];
						params[0] = prgPattoLav;
						params[1] = OR_PER;
						params[2] = prgPercorsoCurr;
						params[3] = null;
						params[4] = null;

						Boolean res = (Boolean) transExec.executeQuery("INS_LAV_PATTO_SCELTA_COMPLETO", params,
								"INSERT");
						if (!res.booleanValue()) {
							throw new Exception("inserimento fallito");
						}
						insertImpegniAssociati(prgPercorsoCurr, prgPattoLav, transExec);
					}
				}
			}
		}
	}

	public static void insertImpegniAssociati(BigDecimal prgpercorso, BigDecimal prgPattoLav,
			TransactionQueryExecutor transExec) throws Exception {
		Set impegni = null;
		String codiceImpegno = "";
		impegni = getImpegni(prgpercorso, prgPattoLav, transExec);
		if (!impegni.isEmpty()) {
			Iterator impegniDaInserire = impegni.iterator();
			while (impegniDaInserire.hasNext()) {
				codiceImpegno = (String) impegniDaInserire.next();
				Object params[] = new Object[5];
				params[0] = prgPattoLav;
				params[1] = DE_IMPE;
				params[2] = codiceImpegno;
				params[3] = null;
				params[4] = null;
				Boolean res = (Boolean) transExec.executeQuery("INS_LAV_PATTO_SCELTA_COMPLETO", params, "INSERT");
				if (!res.booleanValue()) {
					throw new Exception("Inserimento impegno associato fallito");
				}
			}
		}
	}

	private static Set getImpegni(BigDecimal prg, BigDecimal prgPattoLav, TransactionQueryExecutor transExec)
			throws Exception {
		List impegniAggiunti = new ArrayList();
		Vector rows = null;
		Set codici = null;
		Object params[] = new Object[1];
		params[0] = prg;
		SourceBean sb = (SourceBean) transExec.executeQuery("GET_IMPEGNI_AZIONI_COL", params, "SELECT");
		rows = sb.getAttributeAsVector("ROW");
		codici = new HashSet(rows.size());
		SourceBean s = null;
		int k = 0;
		for (int i = 0; i < rows.size(); i++) {
			s = (SourceBean) rows.get(i);
			String cod = s.getAttribute("CODIMPEGNO").toString();
			if (!impegnoPresente(cod, prgPattoLav, transExec)) {
				codici.add(cod);
			}
		}
		codici.removeAll(impegniAggiunti);
		impegniAggiunti.addAll(codici);

		return codici;
	}

	private static SourceBean getDataAzione(BigDecimal prgPercorso, TransactionQueryExecutor transExec)
			throws Exception {
		Object params[] = new Object[1];
		params[0] = prgPercorso;
		SourceBean sb = (SourceBean) transExec.executeQuery("GET_DATI_AZIONE_COLLEGATO_PATTO", params, "SELECT");
		if (sb != null) {
			return sb;
		}
		return null;
	}

	private static boolean impegnoPresente(String codImpegno, BigDecimal prgPattoLav,
			TransactionQueryExecutor transExec) throws Exception {
		boolean impegnoPresente = false;
		Object params[] = new Object[2];
		params[0] = prgPattoLav;
		params[1] = codImpegno;

		SourceBean sb = (SourceBean) transExec.executeQuery("GET_IMPEGNO_PATTO", params, "SELECT");
		Vector rows = sb.getAttributeAsVector("ROW");

		if ((rows != null) && !rows.isEmpty()) {
			impegnoPresente = true;
		}
		return impegnoPresente;
	}

	public static int checkMappaturaAzioneEnte(Object prgPattoLavoratore, String[] descrizioniAzioni) throws Exception {
		// codiceErrore = 0 se tutto OK
		int codiceErrore = 0;
		SourceBean row = null;
		String descrAzione = "";
		Object params[] = new Object[1];
		params[0] = prgPattoLavoratore;
		row = (SourceBean) QueryExecutor.executeQuery("SELECT_AZIONI_ASSOCIATE_PATTO_ENTE", params, "SELECT",
				Values.DB_SIL_DATI);
		if (row != null) {
			Vector rows = row.getAttributeAsVector("ROW");
			if ((rows != null) && !rows.isEmpty()) {
				codiceErrore = MessageCodes.Patto.ERR_ASSOCIAZIONE_AZIONI_ENTE;
				int nSize = rows.size();
				for (int i = 0; i < nSize; i++) {
					SourceBean az = (SourceBean) rows.get(i);
					if (descrAzione.equals("")) {
						descrAzione = Utils.notNull(az.getAttribute("strdescrizione")) + " (Programma: "
								+ Utils.notNull(az.getAttribute("programma")) + ")";
					} else {
						descrAzione = descrAzione + "; " + Utils.notNull(az.getAttribute("strdescrizione"))
								+ " (Programma: " + Utils.notNull(az.getAttribute("programma")) + ")";
					}
				}
				descrizioniAzioni[0] = descrAzione;
			}
		} else {
			codiceErrore = -1;
		}
		return codiceErrore;
	}

	public static Vector<String> checkProgrammi(BigDecimal prgPattoLavoratore, TransactionQueryExecutor transExec)
			throws Exception {
		Vector<String> programmi = new Vector<String>();
		SourceBean row = null;
		Object params[] = new Object[1];
		params[0] = prgPattoLavoratore;
		if (transExec != null) {
			row = (SourceBean) transExec.executeQuery("GET_PROGRAMMI_ASSOCIATI_PATTO", params, "SELECT");
		} else {
			row = (SourceBean) QueryExecutor.executeQuery("GET_PROGRAMMI_ASSOCIATI_PATTO", params, "SELECT",
					Values.DB_SIL_DATI);
		}
		if (row != null) {
			Vector rows = row.getAttributeAsVector("ROW");
			if ((rows != null) && !rows.isEmpty()) {
				int nSize = rows.size();
				for (int i = 0; i < nSize; i++) {
					SourceBean programma = (SourceBean) rows.get(i);
					String servizio = programma.getAttribute("codmonoprogramma") != null
							? programma.getAttribute("codmonoprogramma").toString()
							: "";
					if (!servizio.equals("")) {
						programmi.add(servizio);
					}
				}
			}
		}
		return programmi;
	}

	public static Vector<String> checkServiziProgrammi(BigDecimal prgPattoLavoratore,
			TransactionQueryExecutor transExec, String codMonoProgramma) throws Exception {
		Vector<String> serviziProgrammi = new Vector<String>();
		SourceBean row = null;
		Object params[] = new Object[2];
		params[0] = prgPattoLavoratore;
		params[1] = codMonoProgramma;
		if (transExec != null) {
			row = (SourceBean) transExec.executeQuery("GET_SERVIZIO_PROGRAMMI_ASSOCIATI_PATTO", params, "SELECT");
		} else {
			row = (SourceBean) QueryExecutor.executeQuery("GET_SERVIZIO_PROGRAMMI_ASSOCIATI_PATTO", params, "SELECT",
					Values.DB_SIL_DATI);
		}
		if (row != null) {
			Vector rows = row.getAttributeAsVector("ROW");
			if ((rows != null) && !rows.isEmpty()) {
				int nSize = rows.size();
				for (int i = 0; i < nSize; i++) {
					SourceBean programma = (SourceBean) rows.get(i);
					String servizio = programma.getAttribute("codservizio") != null
							? programma.getAttribute("codservizio").toString()
							: "";
					if (!servizio.equals("")) {
						serviziProgrammi.add(servizio);
					}
				}
			}
		}
		return serviziProgrammi;
	}

	public static Vector<String> checkProgrammaColloquio(Object prgColloquio, TransactionQueryExecutor transExec)
			throws Exception {
		Vector<String> programmi = new Vector<String>();
		SourceBean row = null;
		Object params[] = new Object[1];
		params[0] = prgColloquio;
		if (transExec != null) {
			row = (SourceBean) transExec.executeQuery("GET_PROGRAMMA_ASSOCIATO_COLLOQUIO", params, "SELECT");
		} else {
			row = (SourceBean) QueryExecutor.executeQuery("GET_PROGRAMMA_ASSOCIATO_COLLOQUIO", params, "SELECT",
					Values.DB_SIL_DATI);
		}
		if (row != null) {
			Vector rows = row.getAttributeAsVector("ROW");
			if ((rows != null) && !rows.isEmpty()) {
				int nSize = rows.size();
				for (int i = 0; i < nSize; i++) {
					SourceBean programma = (SourceBean) rows.get(i);
					String servizio = programma.getAttribute("codmonoprogramma") != null
							? programma.getAttribute("codmonoprogramma").toString()
							: "";
					if (!servizio.equals("")) {
						programmi.add(servizio);
					}
				}
			}
		}
		return programmi;
	}

	public static Vector<String> checkAllProgrammi(BigDecimal prgPattoLavoratore, TransactionQueryExecutor transExec)
			throws Exception {
		Vector<String> programmi = new Vector<String>();
		SourceBean row = null;
		Object params[] = new Object[1];
		params[0] = prgPattoLavoratore;
		if (transExec != null) {
			row = (SourceBean) transExec.executeQuery("GET_ALL_PROGRAMMI_ASSOCIATI_PATTO", params, "SELECT");
		} else {
			row = (SourceBean) QueryExecutor.executeQuery("GET_ALL_PROGRAMMI_ASSOCIATI_PATTO", params, "SELECT",
					Values.DB_SIL_DATI);
		}
		if (row != null) {
			Vector rows = row.getAttributeAsVector("ROW");
			if ((rows != null) && !rows.isEmpty()) {
				int nSize = rows.size();
				for (int i = 0; i < nSize; i++) {
					SourceBean programma = (SourceBean) rows.get(i);
					String servizio = programma.getAttribute("codmonoprogramma") != null
							? programma.getAttribute("codmonoprogramma").toString()
							: "";
					if (!servizio.equals("")) {
						programmi.add(servizio);
					}
				}
			}
		}
		return programmi;
	}

	public static HashMap<BigDecimal, String> checkAllProgrammiColloquio(BigDecimal prgPattoLavoratore,
			TransactionQueryExecutor transExec) throws Exception {
		HashMap<BigDecimal, String> programmi = new HashMap<BigDecimal, String>();
		SourceBean row = null;
		Object params[] = new Object[1];
		params[0] = prgPattoLavoratore;
		if (transExec != null) {
			row = (SourceBean) transExec.executeQuery("GET_ALL_PROGRAMMI_ASSOCIATI_PATTO_PRGCOLLOQUIO", params,
					"SELECT");
		} else {
			row = (SourceBean) QueryExecutor.executeQuery("GET_ALL_PROGRAMMI_ASSOCIATI_PATTO_PRGCOLLOQUIO", params,
					"SELECT", Values.DB_SIL_DATI);
		}
		if (row != null) {
			Vector rows = row.getAttributeAsVector("ROW");
			if ((rows != null) && !rows.isEmpty()) {
				int nSize = rows.size();
				for (int i = 0; i < nSize; i++) {
					SourceBean programma = (SourceBean) rows.get(i);
					BigDecimal prgColl = (BigDecimal) programma.getAttribute("prgcolloquio");
					String servizio = programma.getAttribute("codmonoprogramma") != null
							? programma.getAttribute("codmonoprogramma").toString()
							: "";
					programmi.put(prgColl, servizio);
				}
			}
		}
		return programmi;
	}

	public static Vector<String> checkProgrammiInserimentoPatto(BigDecimal cdnlavoratore, String dataStipula,
			String datScadConferma, TransactionQueryExecutor transExec) throws Exception {
		Vector<String> programmi = new Vector<String>();
		SourceBean row = null;
		Object params[] = new Object[4];
		params[0] = cdnlavoratore;
		params[1] = dataStipula;
		params[2] = datScadConferma;
		params[3] = datScadConferma;
		if (transExec != null) {
			row = (SourceBean) transExec.executeQuery("GET_PROGRAMMI_INSERIMENTO_PATTO", params, "SELECT");
		} else {
			row = (SourceBean) QueryExecutor.executeQuery("GET_PROGRAMMI_INSERIMENTO_PATTO", params, "SELECT",
					Values.DB_SIL_DATI);
		}
		if (row != null) {
			Vector rows = row.getAttributeAsVector("ROW");
			if ((rows != null) && !rows.isEmpty()) {
				int nSize = rows.size();
				for (int i = 0; i < nSize; i++) {
					SourceBean programma = (SourceBean) rows.get(i);
					String servizio = programma.getAttribute("codmonoprogramma") != null
							? programma.getAttribute("codmonoprogramma").toString()
							: "";
					if (!servizio.equals("")) {
						programmi.add(servizio);
					}
				}
			}
		}
		return programmi;
	}

	public static BigDecimal checkProgrammiApertiSenzaAzioni(BigDecimal cdnlavoratore,
			TransactionQueryExecutor transExec) throws Exception {
		BigDecimal numProgrammiKO = null;
		SourceBean row = null;
		Object params[] = new Object[1];
		params[0] = cdnlavoratore;
		if (transExec != null) {
			row = (SourceBean) transExec.executeQuery("GET_PROGRAMMI_APERTI_SENZA_AZIONI", params, "SELECT");
		} else {
			row = (SourceBean) QueryExecutor.executeQuery("GET_PROGRAMMI_APERTI_SENZA_AZIONI", params, "SELECT",
					Values.DB_SIL_DATI);
		}
		if (row != null) {
			row = (row.containsAttribute("ROW") ? (SourceBean) row.getAttribute("ROW") : row);
			numProgrammiKO = (BigDecimal) row.getAttribute("numprogrammiapertinoaz");
		}
		return numProgrammiKO;
	}

	public static boolean checkMisuraProgramma(Vector<String> programmi, String tipoPatto) {
		if (programmi != null && !programmi.isEmpty()) {
			for (int j = 0; j < programmi.size(); j++) {
				String progCurr = programmi.get(j);
				if (progCurr.equalsIgnoreCase(tipoPatto)) {
					return true;
				}
			}
		}
		return false;
	}

	public static boolean checkMisureProgramma(Vector<String> programmi, String[] misure) {
		if (programmi != null && !programmi.isEmpty()) {
			for (int j = 0; j < programmi.size(); j++) {
				String progCurr = programmi.get(j);
				for (int k = 0; k < misure.length; k++) {
					if (progCurr.equalsIgnoreCase(misure[k])) {
						return true;
					}
				}
			}
		}
		return false;
	}

	public static boolean checkNoMisuraProgrammaGGPA(Vector<String> programmi) {
		if (programmi != null && !programmi.isEmpty()) {
			for (int j = 0; j < programmi.size(); j++) {
				String progCurr = programmi.get(j);
				if (!progCurr.equalsIgnoreCase(PattoBean.DB_MISURE_GARANZIA_GIOVANI_UMBRIA)
						&& !progCurr.equalsIgnoreCase(PattoBean.DB_MISURE_OVER_30)
						&& !progCurr.equalsIgnoreCase(PattoBean.DB_MISURE_OVER_45)
						&& !progCurr.equalsIgnoreCase(PattoBean.DB_MISURE_INCLUSIONE_ATTIVA)) {
					return true;
				}
			}
		}
		return false;
	}

	public static Engine eseguiStampaPattoConApi(TransactionQueryExecutor txExec, String codCpi,
			BigDecimal cdnLavoratore, String dataStipula, BigDecimal prgPattoLav, BigDecimal numAnnoProt,
			BigDecimal numProtocollo, String datProtocollazione, RequestContainer requestContainer, SourceBean request,
			SourceBean response, Documento doc, String tipoDoc, BigDecimal userID, String dataDid) throws Exception {

		doc.setChiaveTabella(prgPattoLav.toString());
		doc.setTipoProt("S");
		String docInOut = "O";

		doc.setNumAnnoProt(numAnnoProt);
		doc.setNumProtocollo(numProtocollo);
		doc.setDatProtocollazione(datProtocollazione);
		doc.setCodStatoAtto(STATO_PROTOCOLLATO);
		doc.setDatInizio(dataStipula);
		doc.setCodTipoDocumento(tipoDoc);
		doc.setCodCpi(codCpi);
		doc.setCdnLavoratore(cdnLavoratore);
		doc.setPrgAzienda(null);
		doc.setPrgUnita(null);
		doc.setStrDescrizione("");
		doc.setFlgDocAmm("S");
		doc.setFlgDocIdentifP("N");
		doc.setStrNumDoc(null);
		doc.setStrEnteRilascio(codCpi);
		doc.setCodMonoIO(docInOut);
		doc.setDatAcqril(dataStipula);
		doc.setCodModalitaAcqril(null);
		doc.setCodTipoFile(null);
		doc.setStrNomeDoc("patto.pdf");
		doc.setDatFine(null);
		doc.setStrNote("");
		doc.setStrDescrizione("Patto lavoratore");
		doc.setFlgAutocertificazione("N");
		doc.setPagina("PattoLavDettaglioPage");
		doc.setCdnUtIns(userID);
		doc.setCdnUtMod(userID);

		request.setAttribute("docInOut", docInOut);
		request.setAttribute("codStatoAtto", STATO_PROTOCOLLATO);

		com.inet.report.Engine eng = makeEngine(request, response, tipoDoc, userID, dataDid, prgPattoLav, txExec);

		return eng;
	}

	public static void updateInfoProtocolloPatto(SourceBean request, SourceBean response,
			TransactionQueryExecutor txExec) throws Exception {
		String dataProtocollo = (String) request.getAttribute("dataOraProt");

		Object cdnLav = request.getAttribute("cdnLavoratore");
		Object[] params = new Object[] { cdnLav };
		SourceBean pattoAperto = (SourceBean) txExec.executeQuery("GET_PATTO_APERTO", params, "SELECT");
		pattoAperto = (pattoAperto.containsAttribute("ROW") ? (SourceBean) pattoAperto.getAttribute("ROW")
				: pattoAperto);
		Object prgPatto = pattoAperto.getAttribute("row.prgPattoLavoratore");

		Object[] paramsUpd = new Object[] { dataProtocollo, prgPatto };
		Boolean res = (Boolean) txExec.executeQuery("UPDATE_DAT_PROT_INF_LEGATE", paramsUpd, "UPDATE");

		Object[] paramsUpdProt = new Object[] { dataProtocollo, request.getAttribute("PRGSTATOOCCUPAZ"), cdnLav };
		res = (Boolean) txExec.executeQuery("UPDATE_PATTO_DAT_ULTIMO_PROT", paramsUpdProt, "UPDATE");
	}

	private static Engine makeEngine(SourceBean request, SourceBean response, String tipoDoc, BigDecimal userID,
			String dataDid, BigDecimal prgPattoLav, TransactionQueryExecutor txExec) throws Exception {

		Object cdnLav = request.getAttribute("cdnLavoratore");

		Object[] params = new Object[] { cdnLav, cdnLav, cdnLav, cdnLav, cdnLav };
		SourceBean statoOccupazionale = (SourceBean) txExec.executeQuery("GET_STATO_OCCUPAZ", params, "SELECT");
		request.setAttribute("PRGSTATOOCCUPAZ", statoOccupazionale.getAttribute("ROW.PRGSTATOOCCUPAZ"));

		SourceBean operatore = (SourceBean) txExec.executeQuery("GET_OPERATORE", new Object[] { userID }, "SELECT");
		SourceBean appuntamenti = (SourceBean) txExec.executeQuery("GET_APPUNTAMENTI_ADR", new Object[] { cdnLav },
				"SELECT");
		SourceBean azioniConcordate = (SourceBean) txExec.executeQuery("GET_PRESTAZIONI_AZIONI",
				new Object[] { prgPattoLav }, "SELECT");
		SourceBean soggetti = (SourceBean) txExec.executeQuery("GET_SOGGACCREDITATO_REPORT", new Object[] { cdnLav },
				"SELECT");
		SourceBean ambitoProfessionale = (SourceBean) txExec.executeQuery("GET_MANSIONI",
				new Object[] { cdnLav, cdnLav, cdnLav, cdnLav, cdnLav, cdnLav, cdnLav, cdnLav, cdnLav }, "SELECT");
		SourceBean ambitoDocumento = (SourceBean) txExec.executeQuery("GET_TIPODOC", new Object[] { tipoDoc },
				"SELECT");
		SourceBean impegni = (SourceBean) txExec.executeQuery("GET_IMPEGNI_LEGATI_AL_PATTO", new Object[] { cdnLav },
				"SELECT");
		SourceBean infoGenerali = null;
		if (tipoDoc.equalsIgnoreCase("PT297")) {
			infoGenerali = (SourceBean) txExec.executeQuery("GET_PATTO_INFO_GENERALI", new Object[] { cdnLav },
					"SELECT");
		} else {
			infoGenerali = (SourceBean) txExec.executeQuery("GET_ACCORDO_GENERICO_INFO_GENERALI",
					new Object[] { cdnLav }, "SELECT");
		}
		SourceBean cat181 = (SourceBean) txExec.executeQuery("GET_181_CAT", new Object[] { cdnLav }, "SELECT");
		SourceBean laurea = (SourceBean) txExec.executeQuery("GET_LAUREA_X_CAT181", new Object[] { cdnLav }, "SELECT");
		SourceBean movimenti = (SourceBean) txExec.executeQuery("GET_MOVIMENTI_LAVORATORE", new Object[] { cdnLav },
				"SELECT");
		SourceBean documentiIdentitaSourceBean = (SourceBean) txExec.executeQuery("GET_PATTO_DOCUMENTO_IDENTITA",
				new Object[] { cdnLav }, "SELECT");
		SourceBean lastConferimentoBean = null;
		if (dataDid != null) {
			lastConferimentoBean = (SourceBean) txExec.executeQuery("GET_LAST_CONFERIMENTO_DID",
					new Object[] { cdnLav, dataDid }, "SELECT");
		}
		SourceBean configPrivacy = (SourceBean) txExec.executeQuery("CONFIG_PRIVACY", null, "SELECT");
		String privacy = (String) configPrivacy.getAttribute("ROW.VALORENUM");

		Vector documentiIdentitaVector = documentiIdentitaSourceBean.getAttributeAsVector("ROW");
		SourceBean documentoIdentita = null;
		if (documentiIdentitaVector != null && documentiIdentitaVector.size() > 0) {
			documentoIdentita = (SourceBean) documentiIdentitaVector.get(0);
		}

		Vector appuntaments = appuntamenti.getAttributeAsVector("ROW");
		Vector azioniConcordats = azioniConcordate.getAttributeAsVector("ROW");
		Vector ambitoProfs = ambitoProfessionale.getAttributeAsVector("ROW");
		Vector entiAccreditati = soggetti.getAttributeAsVector("ROW");
		Vector infoGeneraliV = infoGenerali.getAttributeAsVector("ROW");
		if (infoGeneraliV.size() == 0) {
			// recupero le info generali considerando l'associazione patto con la mobilita
			infoGenerali = (SourceBean) txExec.executeQuery("GET_PATTO_MOBILITA_INFO_GENERALI", new Object[] { cdnLav },
					"SELECT");
			infoGeneraliV = infoGenerali.getAttributeAsVector("ROW");
		}

		Vector impegniV = impegni.getAttributeAsVector("ROW");
		// generazione del report tramite api crystalclear
		String tipoFile = "PDF";
		SourceBean beanRows = (SourceBean) txExec.executeQuery("GET_CODREGIONE", null, "SELECT");
		String regione = (String) beanRows.getAttribute("ROW.CODREGIONE");

		Class report = null;
		Method inizializzaMethod = null;

		if (tipoDoc.equalsIgnoreCase("PT297")) {
			report = Class.forName("it.eng.sil.action.report.patto.ApiPatto");
			inizializzaMethod = report.getMethod("inizializza",
					new Class[] { SourceBean.class, SourceBean.class, Vector.class, SourceBean.class, Vector.class,
							Vector.class, Vector.class, String.class, String.class, SourceBean.class, Vector.class,
							Vector.class, String.class, String.class, String.class, String.class, SourceBean.class,
							SourceBean.class, String.class, Vector.class, TransactionQueryExecutor.class });
		} else {
			report = Class.forName("it.eng.sil.action.report.patto.ApiAccordoGenerico_RER_CAL");
			inizializzaMethod = report.getMethod("inizializza",
					new Class[] { SourceBean.class, SourceBean.class, Vector.class, SourceBean.class, Vector.class,
							Vector.class, Vector.class, String.class, String.class, SourceBean.class, Vector.class,
							Vector.class, String.class, String.class, String.class, String.class, SourceBean.class,
							String.class, Vector.class, TransactionQueryExecutor.class });
		}

		Method getEngineMethod = report.getMethod("getEngine", new Class[] {});

		// Method setTransactioneMethod = report.getMethod("setTransaction", new Class[] { });

		SourceBean infoGen = (SourceBean) infoGeneraliV.get(0);
		SourceBean statoOcc = (SourceBean) statoOccupazionale.getAttribute("ROW");
		SourceBean categoria181 = it.eng.sil.util.amministrazione.impatti.DBLoad.getRowAttribute(cat181);
		String ambito = (String) ambitoDocumento.getAttribute("ROW.RIFERIMENTO");
		Vector titoloStudio = laurea.getAttributeAsVector("ROW");
		Vector mov = movimenti.getAttributeAsVector("ROW");
		String installAppPath = ConfigSingleton.getRootPath() + java.io.File.separatorChar;

		String strParam = null;
		strParam = (String) request.getAttribute("dataOraProt");

		String docInOut = null;
		docInOut = (String) request.getAttribute("docInOut");

		Object o = report.newInstance();

		if (tipoDoc.equalsIgnoreCase("PT297")) {
			inizializzaMethod.invoke(o,
					new Object[] { infoGen, statoOcc, appuntaments, operatore, azioniConcordats, ambitoProfs, impegniV,
							installAppPath, tipoFile, categoria181, titoloStudio, mov, ambito, strParam, docInOut,
							regione, documentoIdentita, lastConferimentoBean, privacy, entiAccreditati, txExec });
		} else {
			inizializzaMethod.invoke(o,
					new Object[] { infoGen, statoOcc, appuntaments, operatore, azioniConcordats, ambitoProfs, impegniV,
							installAppPath, tipoFile, categoria181, titoloStudio, mov, ambito, strParam, docInOut,
							regione, documentoIdentita, privacy, entiAccreditati, txExec });
		}

		return (Engine) getEngineMethod.invoke(o, new Object[] {});
	}

	public static String soggettoAccreditatoProgramma(QueryExecutorObject qExec, DataConnection dc,
			BigDecimal prgPattoLavoratore, BigDecimal prgColloquio) throws Exception {
		SourceBean programmiBeanRows = null;
		String soggAccreditatoCF = null;

		List<DataField> paramProg = new ArrayList<DataField>();
		paramProg = new ArrayList<DataField>();
		paramProg.add(dc.createDataField("", Types.BIGINT, prgPattoLavoratore));
		paramProg.add(dc.createDataField("", Types.BIGINT, prgColloquio));
		qExec.setInputParameters(paramProg);
		qExec.setType(QueryExecutorObject.SELECT);
		qExec.setStatement(QUERY_SOGGETTO_ACCREDITATO_PROGRAMMIA_PATTO);
		programmiBeanRows = (SourceBean) qExec.exec();

		if (programmiBeanRows != null && !programmiBeanRows.getAttributeAsVector("ROW").isEmpty()) {
			Vector programmiVector = programmiBeanRows.getAttributeAsVector("ROW");
			int size = programmiVector.size();
			if (size > 0) {
				SourceBean programma = (SourceBean) programmiVector.elementAt(0);
				soggAccreditatoCF = (String) programma.getAttribute("strentecodicefiscale");
			}
		}
		return soggAccreditatoCF;
	}

	public static SourceBean getInfoProgramma(QueryExecutorObject qExec, DataConnection dc, BigDecimal prgColloquio)
			throws Exception {
		SourceBean programmiBeanRow = null;

		List<DataField> paramProg = new ArrayList<DataField>();
		paramProg = new ArrayList<DataField>();
		paramProg.add(dc.createDataField("", Types.BIGINT, prgColloquio));
		qExec.setInputParameters(paramProg);
		qExec.setType(QueryExecutorObject.SELECT);
		qExec.setStatement(QUERY_INFO_PROGRAMMIA_PATTO);
		programmiBeanRow = (SourceBean) qExec.exec();

		return programmiBeanRow;
	}

	public static Vector checkObbligoEnte(BigDecimal prgPattoLavoratore, TransactionQueryExecutor transExec)
			throws Exception {
		Vector programmiObbligoEnte = null;
		SourceBean row = null;
		Object params[] = new Object[1];
		params[0] = prgPattoLavoratore;
		if (transExec != null) {
			row = (SourceBean) transExec.executeQuery("GET_FLGOBBLIGOENTE", params, "SELECT");
		} else {
			row = (SourceBean) QueryExecutor.executeQuery("GET_FLGOBBLIGOENTE", params, "SELECT", Values.DB_SIL_DATI);
		}
		if (row != null) {
			programmiObbligoEnte = row.getAttributeAsVector("ROW");

		}
		return programmiObbligoEnte;
	}

	public static SourceBean getProfilingDid150(QueryExecutorObject qExec, DataConnection dc, BigDecimal cdnLavoratore,
			String datProgramma) throws Exception {
		SourceBean patto150BeanRow = null;
		List<DataField> paramsPatti150 = new ArrayList<DataField>();
		paramsPatti150.add(dc.createDataField("CDNLAVORATORE", Types.NUMERIC, cdnLavoratore));
		paramsPatti150.add(dc.createDataField("DATPROGRAMMA", Types.VARCHAR, datProgramma));
		qExec.setInputParameters(paramsPatti150);
		qExec.setStatement(SQLStatements.getStatement("GET_DATI_PROFILING_DID_150"));
		qExec.setType(QueryExecutorObject.SELECT);
		patto150BeanRow = (SourceBean) qExec.exec();
		return patto150BeanRow;
	}

	public static void bonificaDocumentiDoppi(AccessoSemplificato _db, String strChiaveTabella, String codTipoDoc) {
		Object[] objDoc = new Object[3];
		objDoc[0] = codTipoDoc;
		objDoc[1] = new BigDecimal("14");
		objDoc[2] = strChiaveTabella;
		TransactionQueryExecutor txExec = null;
		SourceBean DocDoppiRows = null;

		DocDoppiRows = (SourceBean) QueryExecutor.executeQuery("GET_DOCUMENTO_DOPPIO_X_BONIFICA", objDoc, "SELECT",
				Values.DB_SIL_DATI);

		if (DocDoppiRows != null && !DocDoppiRows.getAttributeAsVector("ROW").isEmpty()
				&& DocDoppiRows.getAttributeAsVector("ROW").size() > 1) {

			try {

				txExec = new TransactionQueryExecutor(_db.getPool());
				txExec.initTransaction();
				Vector docDoppiVector = DocDoppiRows.getAttributeAsVector("ROW");
				// parto dal secondo eventuale elemento del vector
				for (int i = 1; i < docDoppiVector.size(); i++) {
					SourceBean docDoppioBeanRow = (SourceBean) docDoppiVector.elementAt(i);
					objDoc = new Object[4];
					objDoc[0] = "AU";
					objDoc[1] = ((BigDecimal) docDoppioBeanRow.getAttribute("NUMKLODOCUMENTO"))
							.add(new BigDecimal("1"));
					objDoc[2] = "ERR";
					objDoc[3] = (BigDecimal) docDoppioBeanRow.getAttribute("prgdocumento");
					txExec.executeQuery("BONIFICA_DOCUMENTO_DOPPIO", objDoc, "UPDATE");
				}
				txExec.commitTransaction();

			} catch (EMFInternalError e) {
				if (txExec != null) {
					try {
						txExec.rollBackTransaction();
					} catch (EMFInternalError e1) {
						it.eng.sil.util.TraceWrapper.fatal(_logger,
								"Impossibile eseguire la rollBack nella transazione della bonifica dei documenti doppi",
								(Exception) e1);
					}
				}
			}

		}

	}

	public static SourceBean getDataAdesioneGG(QueryExecutorObject qExec, DataConnection dc, BigDecimal prgColloquio)
			throws Exception {
		SourceBean pattoBeanRow = null;
		List<DataField> params = new ArrayList<DataField>();
		params.add(dc.createDataField("PRGCOLLOQUIO", Types.BIGINT, prgColloquio));
		qExec.setInputParameters(params);
		qExec.setStatement(SQLStatements.getStatement("GET_DATA_ADESIONE_GG"));
		qExec.setType(QueryExecutorObject.SELECT);
		pattoBeanRow = (SourceBean) qExec.exec();
		return pattoBeanRow;
	}

	public static void gestisciConcorrenza(SourceBean request, TransactionQueryExecutor tx)
			throws EMFInternalError, SourceBeanException {
		Object params[] = new Object[1];
		params[0] = request.getAttribute("cdnLavoratore");
		SourceBean pattoAperto = (SourceBean) tx.executeQuery("GET_PATTO_APERTO", params, "SELECT");
		BigDecimal numklopattolav = ((BigDecimal) pattoAperto.getAttribute("ROW.NUMKLOPATTOLAVORATORE"))
				.add(new BigDecimal("1"));
		params = new Object[4];
		params[0] = null;
		params[1] = null;
		params[2] = numklopattolav;
		params[3] = request.getAttribute("cdnLavoratore");
		tx.executeQuery("UPDATE_PATTO_DAT_ULTIMO_PROT_STAMPA", params, "UPDATE");
	}

	public static int getSospensioni(QueryExecutorObject qExec, DataConnection dc, BigDecimal cdnLavoratore,
			String dataDid, String datProgramma) throws Exception {
		SourceBean sospBeanRow = null;
		int giorniSosp = 0;
		List<DataField> params = new ArrayList<DataField>();
		params.add(dc.createDataField("DATADID1", Types.VARCHAR, dataDid));
		params.add(dc.createDataField("DATADID2", Types.VARCHAR, dataDid));
		params.add(dc.createDataField("DATAPROGRAMMA1", Types.VARCHAR, datProgramma));
		params.add(dc.createDataField("DATAPROGRAMMA2", Types.VARCHAR, datProgramma));
		params.add(dc.createDataField("CDNLAVORATORE", Types.NUMERIC, cdnLavoratore));
		params.add(dc.createDataField("DATAPROGRAMMA3", Types.VARCHAR, datProgramma));
		params.add(dc.createDataField("DATADID3", Types.VARCHAR, dataDid));
		qExec.setInputParameters(params);
		qExec.setStatement(SQLStatements.getStatement("GET_SOSPENSIONI_DID_PROGRAMMA"));
		qExec.setType(QueryExecutorObject.SELECT);
		sospBeanRow = (SourceBean) qExec.exec();
		if (sospBeanRow != null && !sospBeanRow.getAttributeAsVector("ROW").isEmpty()) {
			Vector sospensioniVector = sospBeanRow.getAttributeAsVector("ROW");
			int sizeSospensioni = sospensioniVector.size();
			for (int i = 0; i < sizeSospensioni; i++) {
				SourceBean sospensione = (SourceBean) sospensioniVector.elementAt(i);
				String datainizio = (String) sospensione.getAttribute("datainizio");
				String datafine = (String) sospensione.getAttribute("datafine");
				giorniSosp = giorniSosp + (DateUtils.daysBetween(datainizio, datafine) + 1);
			}
		}
		return giorniSosp;
	}

	public static SourceBean getDatiYgProfiling(QueryExecutorObject qExec, DataConnection dc, BigDecimal cdnLavoratore,
			String datProgramma) throws Exception {
		SourceBean pattoBeanRow = null;
		List<DataField> params = new ArrayList<DataField>();
		params.add(dc.createDataField("CDNLAVORATORE", Types.BIGINT, cdnLavoratore));
		params.add(dc.createDataField("DATAPROGRAMMA", Types.VARCHAR, datProgramma));
		qExec.setInputParameters(params);
		qExec.setStatement(SQLStatements.getStatement("GET_DATI_YG_PROFILING"));
		qExec.setType(QueryExecutorObject.SELECT);
		pattoBeanRow = (SourceBean) qExec.exec();
		return pattoBeanRow;
	}

}