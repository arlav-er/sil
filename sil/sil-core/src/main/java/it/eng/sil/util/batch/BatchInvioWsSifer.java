package it.eng.sil.util.batch;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.base.SourceBeanException;
import com.engiweb.framework.configuration.ConfigSingleton;
import com.engiweb.framework.dbaccess.Utils;
import com.engiweb.framework.dbaccess.sql.DataConnection;
import com.engiweb.framework.dbaccess.sql.SQLCommand;
import com.engiweb.framework.dbaccess.sql.result.DataResult;
import com.engiweb.framework.dbaccess.sql.result.DataResultInterface;
import com.engiweb.framework.dbaccess.sql.result.ScrollableDataResult;
import com.engiweb.framework.error.EMFInternalError;
import com.engiweb.framework.util.QueryExecutorObject;

import it.eng.afExt.utils.DateUtils;
import it.eng.afExt.utils.TransactionQueryExecutor;
import it.eng.sil.Values;
import it.eng.sil.coop.webservices.corsoCIG.InvioWsSifer;
import it.eng.sil.module.movimenti.processors.Warning;
import it.eng.sil.util.amministrazione.impatti.LogBatch;
import it.eng.sil.util.batch.mdb.BatchObject;
import it.eng.sil.util.batch.mdb.IBatchMDBConsumer;

/**
 * @author girotti
 * 
 */
public class BatchInvioWsSifer implements BatchRunnable, IBatchMDBConsumer {
	// FIXME
	private BatchObject batchObject;
	private String[] parametri;
	Date dataSiferDa;
	Date dataSiferA;
	private String codiceFiscale;
	private LogBatch logBatch;
	private String pathOutput = null;

	public BatchInvioWsSifer(BatchObject batchObject) throws Exception {
		this.batchObject = batchObject;
		String dir = ConfigSingleton.getLogBatchPath();
		String nomeFile = File.separator + "BatchInvioSifer";
		String data = "";

		data = DateUtils.getNow(); // data in cui viene schedulato

		data = data.replace('/', '-');
		nomeFile = nomeFile + data + ".log";
		logBatch = new LogBatch(nomeFile, dir);

		// recupero la cartella di output dalla ts_generale
		// String query = "select ts_generale.STRCONTESTOMIGRAZIONI from ts_generale";
		logBatch.writeLog("");
		logBatch.writeLog("select ts_generale.STRCONTESTOMIGRAZIONI from ts_generale");
		logBatch.writeLog("");
		// SourceBean row = new SourceBean(executeSelect(query));
		// if(row==null){
		// logBatch.writeLog("BatchScriptMigrazioni:impossibile recuperare la cartella di output dalla ts_generale");
		// throw new
		// Exception("BatchScriptMigrazioni:impossibile recuperare la cartella di output dalla ts_generale");
		// }

		logBatch.writeLog("===============================================");
		logBatch.writeLog("=========== INIZIO ESECUZIONE BATCH ===========");
		logBatch.writeLog("===============================================");

		// logBatch.writeLog("");
		// logBatch.writeLog(row.toString());
		// logBatch.writeLog("");

		// pathOutput = StringUtils.getAttributeStrNotNull(row,
		// "ROW.STRCONTESTOMIGRAZIONI");
		// if ((pathOutput == null) || (pathOutput.equals(""))) {
		// logBatch.writeLog("BatchScriptMigrazioni:impossibile recuperare la cartella di output dalla ts_generale");
		// throw new
		// Exception("BatchScriptMigrazioni:impossibile recuperare la cartella di output dalla ts_generale");
		// }

		pathOutput = pathOutput + File.separator + "batch" + File.separator;
	}

	public static QueryExecutorObject getQueryExecutorObject(DataConnection dc) {

		QueryExecutorObject qExec = new QueryExecutorObject();

		qExec.setRequestContainer(null);
		qExec.setResponseContainer(null);
		qExec.setDataConnection(dc);
		qExec.setType(QueryExecutorObject.SELECT);
		qExec.setTransactional(true);
		qExec.setDontForgetException(true);

		return qExec;
	}

	@Override
	public void execBatch() {
		try {
			if (!this.setParametri()) {
				this.logBatch.writeLog("Errore : il numero di parametri del batch è errato");
				return;
			}
			this.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see it.eng.sil.util.batch.BatchRunnable#start()
	 */
	public void start() {
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		try {
			// String msgParametri = "";
			logBatch.writeLog("========================== PARAMETRI BATCH ============================");
			logBatch.writeLog("\r\nData Inizio");
			logBatch.writeLog(sdf.format(dataSiferDa));
			logBatch.writeLog("\r\nData Fine");
			logBatch.writeLog(sdf.format(dataSiferDa));
			logBatch.writeLog("\r\nCodice Fiscale");
			logBatch.writeLog(codiceFiscale);
			// msgParametri = "";
			// logBatch.writeLog(msgParametri);
			logBatch.writeLog("========================================================================");
			// msgParametri = "";
			// logBatch.writeLog(msgParametri);

			// Stampa delle variabili java
			logBatch.writeLogVarJava(DateUtils.getNow());
			logBatch.writeLogVarJava("====== Variabili di configurazione java ======");
			Properties p = System.getProperties();
			Iterator<Object> iter = p.keySet().iterator();
			while (iter.hasNext()) {
				String key = iter.next().toString();
				String val = p.getProperty(key);
				logBatch.writeLogVarJava(key + ": " + val + ";");
			}
			logBatch.writeLogVarJava("====== Fine sezione di configurazione ========");
			InvioWsSifer invioWsSifer = new InvioWsSifer();
			String wsSiferInviati = invioWsSifer.inviaWsLavAll(dataSiferDa, dataSiferA, codiceFiscale);

			logBatch.writeLog("\r\nwsSiferInviati:");
			logBatch.writeLog(wsSiferInviati);

			logBatch.writeLog("Il batch è terminato correttamente");
			logBatch.writeLog("===========================================");
			logBatch.writeLog("========== FINE ESECUZIONE BATCH ==========");
			logBatch.writeLog("===========================================");
			logBatch.writeLog("");
			return;

		} catch (Exception e) {
			logBatch.writeLog("ERRORE: " + e.toString());
			logBatch.writeLog("Il batch è terminato con degli errori.");
			logBatch.writeLog("===========================================");
			logBatch.writeLog("========== FINE ESECUZIONE BATCH ==========");
			logBatch.writeLog("===========================================");
		}
	}

	/**
	 * Metodo per settare i parametri passati da linea di comando
	 * 
	 * @throws ParseException
	 */
	public boolean setParametri() throws ParseException {
		// C:\EclipseJBoss\SilWeb\WebContent\WEB-INF\batch\avviaBatchScriptMigrazioni.bat
		// C:\EclipseJBoss\SilWeb\WebContent\WEB-INF\classes

		String[] args = this.batchObject.getParams();
		if (args.length < 2) {
			return false;
		}
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		dataSiferDa = sdf.parse(args[0]);
		dataSiferA = sdf.parse(args[1]);
		if (args.length > 2) {
			codiceFiscale = args[2];
		}

		return true;
	}

	public String getQueryScriptMigrazioni() {
		String queryFromBase = "SELECT p.strtarga as PROVDEST," + "ps.STRTARGA as PROVSORG,"
				+ "lav.STRCODICEFISCALE as CodFiscLav," + "lav.STRCOGNOME as CognomeLav," + "lav.STRNOME as NomeLav,"
				+ "lav.STRSESSO as SessoLav," + "lav.DATNASC as DataNascitaLav," + "lav.CODCOMNAS as CodComNascitaLav,"
				+ "lav.CODCITTADINANZA as CodCittadinanzaLav," + "lav.CODCOMDOM as CodComResidenzaLav,"
				+ "lav.STRINDIRIZZODOM as IndirizzoLav," + "lav.STRCAPDOM as CapLav,"
				+ "m.DATSCADENZA as ScadenzaPSLav," + "m.CODTITOLO as TitoloStudioLav,"
				+ "az.STRCODICEFISCALE as CodFiscAz," + "az.STRPARTITAIVA as PartIvaAz,"
				+ "az.STRRAGIONESOCIALE as RagSocAz," + "uaz.CODATECO as CodAtecoAz," + "uaz.CODCCNL as CcnlAz,"
				+ "az.NUMDIPENDENTI as NumeroDipAz," + "uaz.STRINDIRIZZO as IndirAz," + "uaz.CODCOM as CodComAz,"
				+ "uaz.STRCAP as CapAz," + "uaz.STRTEL as TelAz," + "uaz.STRFAX as FaxAz," + "uaz.STREMAIL as EmailAz,"
				+ "uazL.STRINDIRIZZO as IndirSede," + "uazL.CODCOM as CodComSede," + "m.DATCOMUNICAZ as dataEvento,"
				+ "m.DATINIZIOMOV as DataInizioMov," + "m.CODTIPOMOV as evento," + "miss.CODTIPOMIS as CODTIPOMIS,"
				+ "m.CODMANSIONE as codMansione," + "m.STRDESATTIVITA as MansioneAvv,"
				+ "m.CODCONTRATTO as codContratto," + "m.CODMONOTEMPO as CodTempo," + "m.CODCCNL as codCCNL,"
				+ "m.DECRETRIBUZIONEMEN as DECRETRIBUZIONEMEN," + "m.NUMLIVELLO as numLivello,"
				+ "m.FLGLEGGE68 as FLGLEGGE68," + "m.FLGLAVDOMICILIO as flgLavDom," + "m.FLGASSOBBL as FLGASSOBBL,"
				+ "m.CODCATASSOBBL as CODCATASSOBBL," + "m.CODAGEVOLAZIONE as codBenefici,"
				+ "m.CODORARIO as codOrario," + "m.NUMORESETT as numOreSett," + "m.CODMVCESSAZIONE as MotivoCess,"
				+ "m.DATFINEMOV as DataFineMov," + "m.STRMATRICOLA as MatricolaAvv,"
				+ "m.CODTIPOCONTRATTO as CodTipoAvv," + "null as CODNORMATIVA," + "m.CODGRADO as codGrado,"
				+ "movApp.FLGARTIGIANA as flgAzArtigiana," + "movApp.NUMMESIAPPRENDISTATO as DurataAppAvv,"
				+ "'C' as CODMONOPROV," + "m.STRPOSINPS as MatricolaINPS," + "m.STRPATINAIL as PATPosINAIL,"
				+ "'DV' AS CODSTATOATTO," + "m.FLGINTERASSPROPRIA as FlgAssPropria,"
				+ "decode(miss.PRGMOVIMENTO,null,azUti.STRCODICEFISCALE,azUtiMiss.STRCODICEFISCALE) as CodFiscAzUtil,"
				+ "decode(miss.PRGMOVIMENTO,null,azUti.STRPARTITAIVA,azUtiMiss.STRPARTITAIVA) as PartIvaAzUtil,"
				+ "decode(miss.PRGMOVIMENTO,null,azUti.STRRAGIONESOCIALE,azUtiMiss.STRRAGIONESOCIALE) as RagSocAzUtil,"
				+ "decode(miss.PRGMOVIMENTO,null,uazUti.CODATECO,uazUtiMiss.CODATECO) as CodAtecoAzUtil,"
				+ "decode(miss.PRGMOVIMENTO,null,azUti.NUMDIPENDENTI,azUtiMiss.NUMDIPENDENTI) as NumeroDipAzUtil,"
				+ "decode(miss.PRGMOVIMENTO,null,uazUti.STRINDIRIZZO,uazUtiMiss.STRINDIRIZZO) as IndirAzUtil,"
				+ "decode(miss.PRGMOVIMENTO,null,uazUti.CODCOM,uazUtiMiss.CODCOM) as CodComAzUtil,"
				+ "decode(miss.PRGMOVIMENTO,null,uazUti.STRCAP,uazUtiMiss.STRCAP) as CapAzUtil,"
				+ "az.STRNUMALBOINTERINALI as NumAlboInterinaliAz,"
				+ "uaz.STRNUMREGISTROCOMMITT as NumRegistroCommittDom," + "m.FLGRETRIBUZIONEMENCCNL as InBaseContrNaz,"
				+ "m.STRLUOGODILAVORO as LuogoSvolgAvv," + "movApp.DATVISITAMEDICA as VisitaMedicaAppr,"
				+ "i.CODCPITIT as CPILav,"
				+ "decode(miss.PRGMOVIMENTO,null,azUti.CODTIPOAZIENDA,azUtiMiss.CODTIPOAZIENDA) as CodTipoAzUtil,"
				+ "movApp.STRCODICEFISCALETUTORE as CodFiscTutoreAppr,"
				+ "movApp.STRCOGNOMETUTORE as CognomeTutoreAppr," + "movApp.STRNOMETUTORE as NomeTutoreAppr,"
				+ "movApp.NUMANNIESPTUTORE as EspLavTutoreAppr," + "movApp.FLGTITOLARETUTORE as TitImpresaTutoreAppr,"
				+ "movApp.STRLIVELLOTUTORE as LivInqTutoreAppr," + "movApp.CODMANSIONETUTORE as QualificaTutoreAppr,"
				+ "az.CODTIPOAZIENDA as CodTipoAz," + "m.STRAZINTNUMCONTRATTO as NumContrattoAzUtil,"
				+ "m.DATAZINTINIZIOCONTRATTO as DataInizioContrAzUtil,"
				+ "m.DATAZINTFINECONTRATTO as DataFineContrAzUtil," + "m.STRAZINTRAP as LegRapprAzUtil,"
				+ "m.NUMAZINTSOGGETTI as SoggInteressAzUtil,"
				+ "m.NUMGGTRAMOVCOMUNICAZIONE as NUMGGTRAMOVCOMUNICAZIONE," + "m.STRREFERENTE as Referente,"
				+ "m.NUMGGPREVISTIAGR as GgPrevistiAgric," + "m.NUMGGEFFETTUATIAGR as GgEffettuatiAgric,"
				+ "m.FLGLAVOROAGR as FLGLAVOROAGR," + "'N' as FLGASSDACESS," + "m.CODLAVORAZIONE as CodLavorazione,"
				+ "m.CODCATEGORIA as CodCategoria," + "NULL AS versioneTracciato," + "az.CODCOMNASCDI as CodComNascDI,"
				+ "az.DATNASCDI as DatNascDI," + "az.STRSESSODI as StrSessoDI,"
				+ "movApp.CODQUALIFICASRQ as QualificaSRQ," + "d.NUMPROTOCOLLO as NumProt,"
				+ "d.NUMANNOPROT as AnnoProt,"
				+ "decode(m.CODCOMUNICAZIONE,NULL,NULL,decode(m.PRGMOVIMENTORETT,null,'01','03')) as CodTipoComunic,"
				+ "m.CODCOMUNICAZIONE as CodComunicazione," + "m.CODTIPODOCEX as CODTIPODOCEX,"
				+ "m.STRNUMDOCEX as STRNUMDOCEX," + "m.CODMOTIVOPERMSOGGEX as CODMOTIVOPERMSOGGEX,"
				+ "m.CODENTE as CODTIPOENTEPREV," + "m.STRCODICEENTEPREV as STRCODICEENTEPREV,"
				+ "m.FLGSOCIO as FLGSOCIO," + "m.CODTIPOTRASF as CODTIPOTRASF," + "case " + "when m.CODTIPOMOV = 'TRA' "
				+ "then case " + "when m.CODTIPOTRASF in ('01','02','03','04','05','06') "
				+ "then azPrec.STRCODICEFISCALE " + "else null " + "end " + "else null "
				+ "end as STRCODICEFISCALEAZPREC, " + "case " + "when m.CODTIPOMOV = 'TRA' " + "then case "
				+ "when m.CODTIPOTRASF in ('01','02','03','04','05','06') " + "then azPrec.STRRAGIONESOCIALE "
				+ "else null " + "end " + "else null " + "end as STRRAGIONESOCIALEAZPREC, " + "case "
				+ "when m.CODTIPOMOV = 'TRA' " + "then case "
				+ "when m.CODTIPOTRASF in ('01','02','03','04','05','06') " + "then uazPrec.CODCOM " + "else null "
				+ "end " + "else null " + "end as CODCOMAZPREC, " + "case " + "when m.CODTIPOMOV = 'TRA' "
				+ "then case " + "when m.CODTIPOTRASF in ('01','02','03','04','05','06') "
				+ "then uazPrec.STRINDIRIZZO " + "else null " + "end " + "else null " + "end as STRINDIRIZZOAZPREC, "
				+ "case " + "when m.CODTIPOMOV = 'TRA' " + "then case "
				+ "when m.CODTIPOTRASF in ('01','02','03','04','05','06') " + "then uazPrec.STRCAP " + "else null "
				+ "end " + "else null " + "end as STRCAPAZPREC, " + "case " + "when m.CODTIPOMOV = 'TRA' "
				+ "then case " + "when m.CODTIPOTRASF in ('01','02','03','04','05','06') " + "then uazPrec.CODATECO "
				+ "else null " + "end " + "else null " + "end as CODATECOAZPREC, " + "case "
				+ "when m.CODTIPOMOV = 'TRA' " + "then case "
				+ "when m.CODTIPOTRASF in ('01','02','03','04','05','06') " + "then azPrec.STRPATINAIL " + "else null "
				+ "end " + "else null " + "end as STRPATAZPREC, " + "case " + "when m.CODTIPOMOV = 'TRA' "
				+ "then case " + "when m.CODTIPOTRASF in ('01','02','03','04','05','06') " + "then uazPrec.STRTEL "
				+ "else null " + "end " + "else null " + "end as STRTELAZPREC, " + "case "
				+ "when m.CODTIPOMOV = 'TRA' " + "then case "
				+ "when m.CODTIPOTRASF in ('01','02','03','04','05','06') " + "then uazPrec.STRFAX " + "else null "
				+ "end " + "else null " + "end as STRFAXAZPREC, " + "case " + "when m.CODTIPOMOV = 'TRA' "
				+ "then case " + "when m.CODTIPOTRASF in ('01','02','03','04','05','06') " + "then uazPrec.STREMAIL "
				+ "else null " + "end " + "else null " + "end as STREMAILAZPREC, "
				+ "decode(miss.PRGMOVIMENTO,null,m.DATINIZIORAPLAV,miss.DATINIZIOMIS) as DATINIZIORAPLAV,"
				+ "decode(miss.PRGMOVIMENTO,null,m.DATFINERAPLAV,miss.DATFINEMIS) as DATFINERAPLAV,"
				+ "m.CODSOGGETTO as CODSOGGETTO," + "m.CODCOMUNICAZIONEPREC as CODCOMUNICAZIONEPREC,"
				+ "m.CODTIPOSOMM as CODTIPOSOMM," + "m.DATCONVENZIONE as DataConvL68Avv,"
				+ "m.NUMCONVENZIONE as NumConvL68Avv," + "m.dtmins," + "cpi.codmonotipofile " + "FROM am_movimento m,"
				+ "an_lav_storia_inf i," + "de_cpi cpi," + "ts_generale ts," + "de_provincia p," + "de_provincia ps,"
				+ "an_lavoratore lav," + "an_azienda az," + "an_unita_azienda uaz," + "an_unita_azienda uazL,"
				+ "am_movimento_apprendist movApp," + "am_documento_coll c," + "am_documento d,"
				+ "am_movimento_missione miss," + "an_azienda azUti," + "an_unita_azienda uazUti,"
				+ "an_azienda azUtiMiss," + "an_unita_azienda uazUtiMiss," + "am_movimento movPrec,"
				+ "an_azienda azPrec," + "an_unita_azienda uazPrec";

		String queryWhereBase = " WHERE i.cdnlavoratore = m.cdnlavoratore " + "AND i.codcpiorig = cpi.codcpi "
				+ "AND i.codmonotipocpi = 'T' " + "AND i.datfine IS NULL "
				+ "AND cpi.codprovincia <> ts.codprovinciasil " + "AND cpi.codprovincia = p.codprovincia "
				+ "AND p.codregione = 8 " + "AND m.CODSTATOATTO in ('PR','AR') "
				+ "AND m.PRGMOVIMENTO = miss.PRGMOVIMENTO(+) " + "AND miss.PRGUNITAUTILIZ = uazUtiMiss.PRGUNITA(+) "
				+ "AND miss.PRGAZIENDAUTILIZ = azUtiMiss.PRGAZIENDA(+) "
				+ "AND miss.PRGAZIENDAUTILIZ = uazUtiMiss.PRGAZIENDA(+) "
				+ "AND m.PRGMOVIMENTOPREC = movPrec.PRGMOVIMENTO(+) " + "AND movPrec.PRGUNITA = uazPrec.PRGUNITA(+) "
				+ "AND movPrec.PRGAZIENDA = azPrec.PRGAZIENDA(+) " + "AND movPrec.PRGAZIENDA = uazPrec.PRGAZIENDA(+) "
				+ "AND m.prgunitautiliz = uazuti.prgunita(+) " + "AND m.prgaziendautiliz = azuti.prgazienda(+) "
				+ "AND m.prgaziendautiliz = uazuti.prgazienda(+) " + "AND m.prgmovimento = c.strchiavetabella(+) "
				+ "AND c.prgdocumento = d.prgdocumento(+) "
				+ "AND d.codtipodocumento IN ('MVAVV', 'MVTRA', 'MVPRO', 'MVCES') "
				+ "AND m.CDNLAVORATORE = lav.CDNLAVORATORE " + "AND M.PRGAZIENDA = az.prgazienda "
				+ "AND uaz.prgazienda = az.prgazienda " + "AND M.PRGUNITA = UAZ.PRGUNITA "
				+ "AND M.PRGAZIENDA = uazL.prgazienda " + "AND uazL.flgsede ='S' "
				+ "AND m.PRGMOVIMENTO = movApp.PRGMOVIMENTO(+) " + "AND ts.CODPROVINCIASIL = ps.CODPROVINCIA";

		String queryOrderBase = " order by p.strtarga, m.dtmins";

		String dataInizio = parametri[1];
		String dataFine = parametri[2];
		String provinciaDest = parametri[3];
		String tipoMigrazioni = parametri[4];

		if (!dataInizio.equals("00/00/0000"))
			queryWhereBase = queryWhereBase + " AND M.DTMINS >= to_date('" + dataInizio + "','dd/mm/yyyy') ";
		if (!dataFine.equals("00/00/0000"))
			queryWhereBase = queryWhereBase + " AND M.DTMINS <= to_date('" + dataFine + "','dd/mm/yyyy') ";
		if (!provinciaDest.toUpperCase().equals("XX"))
			queryWhereBase = queryWhereBase + " AND P.STRTARGA = '" + provinciaDest + "' ";

		if (!tipoMigrazioni.toUpperCase().equals("E") && tipoMigrazioni.toUpperCase().equals("M"))
			queryWhereBase = queryWhereBase + " AND M.CODMONOPROV = 'M' ";
		if (!tipoMigrazioni.toUpperCase().equals("E") && tipoMigrazioni.toUpperCase().equals("C"))
			queryWhereBase = queryWhereBase + " AND (M.CODMONOPROV = 'S' OR M.CODMONOPROV = 'F') ";

		return queryFromBase + queryWhereBase + queryOrderBase;
	}

	String generaScriptInsertDaSourceBean(SourceBean sb, String mappingFileName, String name, String desc,
			Boolean isContextAvvCVE, BigDecimal userId)
			throws ParserConfigurationException, SAXException, IOException, SQLException, SourceBeanException {

		ScriptGenerator scg = new ScriptGenerator(mappingFileName, name, desc, userId);

		Map mov = null;
		mov = scg.sbToHash(sb);
		String script = scg.generaScript(mov, isContextAvvCVE) + ";";
		ArrayList warnings = new ArrayList(scg.getWarnings());
		logBatch.writeLog("Si sono verificati " + warnings.size() + " warnings");
		for (int i = 0; i < warnings.size(); i++) {
			Warning w = (Warning) warnings.get(i);
			logBatch.writeLog(w.getMessage());
		}
		return script;
	}

	SourceBean executeSelect(String query) throws SourceBeanException, EMFInternalError {
		SQLCommand command = null;
		DataConnection conn = null;
		SourceBean beanRows = null;
		TransactionQueryExecutor trans = null;
		DataResult dr = null;

		try {
			trans = new TransactionQueryExecutor(Values.DB_SIL_DATI);

			conn = trans.getDataConnection();

			command = (SQLCommand) conn.createSelectCommand(query);

			dr = command.execute();

			ScrollableDataResult sdr = null;

			if (dr.getDataResultType().equals(DataResultInterface.SCROLLABLE_DATA_RESULT)) {
				sdr = (ScrollableDataResult) dr.getDataObject();
			}

			beanRows = new SourceBean(sdr.getSourceBean());

		} finally {
			Utils.releaseResources(conn, command, dr);
		}

		return beanRows;
	}
}
