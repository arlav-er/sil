package it.eng.sil.util.batch;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import com.engiweb.framework.base.ApplicationContainer;
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

import it.eng.afExt.utils.DateUtils;
import it.eng.afExt.utils.StringUtils;
import it.eng.afExt.utils.TransactionQueryExecutor;
import it.eng.sil.Values;
import it.eng.sil.module.movimenti.processors.Warning;
import it.eng.sil.util.amministrazione.impatti.LogBatch;

public class BatchScriptMigrazioni implements BatchRunnable {
	private String[] parametri;
	private LogBatch logBatch;
	private ApplicationContainer applicationContainer;
	private String pathOutput = null;

	public BatchScriptMigrazioni(String args[]) throws Exception {
		String dir = ConfigSingleton.getLogBatchPath();
		String nomeFile = File.separator + "BatchScriptMigrazioni";
		String data = "";

		data = DateUtils.getNow(); // data in cui viene schedulato

		data = data.replace('/', '-');
		nomeFile = nomeFile + data + ".log";
		logBatch = new LogBatch(nomeFile, dir);

		// recupero la cartella di output dalla ts_generale
		String query = "select ts_generale.STRCONTESTOMIGRAZIONI from ts_generale";
		logBatch.writeLog("");
		logBatch.writeLog("select ts_generale.STRCONTESTOMIGRAZIONI from ts_generale");
		logBatch.writeLog("");
		SourceBean row = new SourceBean(executeSelect(query));
		if (row == null) {
			logBatch.writeLog("BatchScriptMigrazioni:impossibile recuperare la cartella di output dalla ts_generale");
			throw new Exception("BatchScriptMigrazioni:impossibile recuperare la cartella di output dalla ts_generale");
		}

		logBatch.writeLog("===============================================");
		logBatch.writeLog("=========== INIZIO ESECUZIONE BATCH ===========");
		logBatch.writeLog("===============================================");

		logBatch.writeLog("");
		logBatch.writeLog(row.toString());
		logBatch.writeLog("");

		pathOutput = StringUtils.getAttributeStrNotNull(row, "ROW.STRCONTESTOMIGRAZIONI");
		if ((pathOutput == null) || (pathOutput.equals(""))) {
			logBatch.writeLog("BatchScriptMigrazioni:impossibile recuperare la cartella di output dalla ts_generale");
			throw new Exception("BatchScriptMigrazioni:impossibile recuperare la cartella di output dalla ts_generale");
		}

		pathOutput = pathOutput + File.separator + "batch" + File.separator;

		applicationContainer = ApplicationContainer.getInstance();
	}

	public static void main(String[] args) {
		BatchScriptMigrazioni scriptMigrazioni = null;
		try {
			scriptMigrazioni = new BatchScriptMigrazioni(args);
			if (!scriptMigrazioni.setParametri(args)) {
				scriptMigrazioni.release();
				scriptMigrazioni.logBatch.writeLog("Errore : il numero di parametri del batch è errato");
				return;
			}
			scriptMigrazioni.start();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			scriptMigrazioni.release();
		}
	}

	public void start() {
		String configbase = ConfigSingleton.getRootPath() + File.separator + "WEB-INF" + File.separator + "conf"
				+ File.separator + "import" + File.separator + "processors" + File.separator;

		try {
			String msgParametri = "";
			logBatch.writeLog("========================== PARAMETRI BATCH ============================");
			logBatch.writeLog(msgParametri);

			if (parametri[0] != null) {
				msgParametri = "File di mapping : " + parametri[0].toString();
				logBatch.writeLog(msgParametri);
			}

			if (parametri[1] != null) {
				msgParametri = "Data inizio range migrazioni : " + parametri[1].toString();
				logBatch.writeLog(msgParametri);
			}

			if (parametri[2] != null) {
				msgParametri = "Data fine range migrazioni : " + parametri[2].toString();
				logBatch.writeLog(msgParametri);
			}

			if (parametri[3] != null) {
				msgParametri = "Provincia di destinazione : " + parametri[3].toString();
				logBatch.writeLog(msgParametri);
			}

			if (parametri[4] != null) {
				msgParametri = "Tipo migrazioni da considerare : " + parametri[4].toString();
				logBatch.writeLog(msgParametri);
			}

			if (parametri[5] != null) {
				msgParametri = "Numero max di movimenti per file : " + parametri[5].toString();
				logBatch.writeLog(msgParametri);
			}

			msgParametri = "";
			logBatch.writeLog(msgParametri);
			logBatch.writeLog("========================================================================");
			msgParametri = "";
			logBatch.writeLog(msgParametri);

			// Stampa delle variabili java
			logBatch.writeLogVarJava(DateUtils.getNow());
			logBatch.writeLogVarJava("====== Variabili di configurazione java ======");
			Properties p = java.lang.System.getProperties();
			Enumeration e = p.propertyNames();
			while (e.hasMoreElements()) {
				String key = (String) e.nextElement();
				String val = p.getProperty(key);
				logBatch.writeLogVarJava(key + ": " + val + ";");
			}
			logBatch.writeLogVarJava("====== Fine sezione di configurazione ========");

			logBatch.writeLog("");
			logBatch.writeLog("============================= QUERY RECUPERO MOVIMENTI ==================");
			logBatch.writeLog("");
			logBatch.writeLog(this.getQueryScriptMigrazioni());
			logBatch.writeLog("");

			SourceBean beanRows = new SourceBean(executeSelect(this.getQueryScriptMigrazioni()));

			List rows = beanRows.getAttributeAsVector("ROW");

			int size = rows.size();

			if (size == 0) {
				logBatch.writeLog("Non sono stati trovati movimenti compatibili con i parametri del Batch");
				logBatch.writeLog("===========================================");
				logBatch.writeLog("========== FINE ESECUZIONE BATCH ==========");
				logBatch.writeLog("===========================================");
				logBatch.writeLog("");
				return;
			}

			logBatch.writeLog(beanRows.toString());

			logBatch.writeLog("");
			logBatch.writeLog("=========================================================================");
			logBatch.writeLog("");
			logBatch.writeLog("========== INIZIO A SCRIVERE GLI SCRIPT SU FILE =========================");
			logBatch.writeLog("");

			int countScriptPerFile = 0;
			int maxScriptPerFile = Integer.parseInt(parametri[5]);
			int countFile = 1;

			String provDestPrec = "";
			FileAppender fApp = null;
			String provDest = null;
			String provSorg = null;
			for (int i = 0; i < size; i++) {
				SourceBean riga = (SourceBean) rows.get(i);
				provDest = StringUtils.getAttributeStrNotNull(riga, "PROVDEST");
				provSorg = StringUtils.getAttributeStrNotNull(riga, "PROVSORG");

				logBatch.writeLog(i + " " + provSorg + " " + provDest);

				if (i == 0) {
					logBatch.writeLog("creo l'oggetto FileAppender.....");
					fApp = new FileAppender("work", pathOutput + File.separator + provDest, "sql");
					fApp.appendTextToCurrentFile("set define off");
					logBatch.writeLog("creato l'oggetto FileAppender");
				}

				// criterio di cambio file
				if ((countScriptPerFile == maxScriptPerFile)) {
					logBatch.writeLog("raggiunto il numero max di script per il file corrente");
					fApp.appendTextToCurrentFile("set define on");
					fApp.appendTextToCurrentFile("COMMIT;");
					fApp.doneAppendCurrentFile(countFile + "_" + provSorg + "_X_" + provDest + "_" + countScriptPerFile,
							pathOutput + File.separator + provDest);
					fApp.changeWorkFile("work", pathOutput + File.separator + provDest);
					fApp.appendTextToCurrentFile("set define off");
					countFile++;
					countScriptPerFile = 0;
				}

				// criterio di cambio file
				if (!provDestPrec.equals("") && !provDest.equals(provDestPrec)) {
					logBatch.writeLog("cambio provincia");
					fApp.appendTextToCurrentFile("set define on");
					fApp.appendTextToCurrentFile("COMMIT;");
					fApp.doneAppendCurrentFile(
							countFile + "_" + provSorg + "_X_" + provDestPrec + "_" + countScriptPerFile,
							pathOutput + File.separator + provDestPrec);
					fApp.changeWorkFile("work", pathOutput + File.separator + provDest);
					fApp.appendTextToCurrentFile("set define off");
					countFile = 1;
					countScriptPerFile = 0;
				}

				logBatch.writeLog("genero lo script e lo scrivo sul file.....");

				/*
				 * String tipoMov = StringUtils.getAttributeStrNotNull(riga, "evento"); if(tipoMov.equals("CES")){
				 * String script = generaScriptInsertDaSourceBean(riga, configbase +
				 * parametri[0],"INSERT_MOVIMENTO_APPOGGIO","Inserimento Movimento Appoggio",true); String avvCve =
				 * generaScriptInsertDaSourceBean(riga, configbase +
				 * "insertMovimentoAppPerCVE.xml","INSERT_AVVIAMENTO_PER_CVE","Inserisci_Avviamento_per_CVE",true);
				 * fApp.appendTextToCurrentFile("declare "); fApp.appendTextToCurrentFile("prg1 number; ");
				 * fApp.appendTextToCurrentFile("prg2 number; "); fApp.appendTextToCurrentFile("temp number; ");
				 * fApp.appendTextToCurrentFile("Begin ");
				 * fApp.appendTextToCurrentFile("select S_AM_MOVIMENTO_APPOGGIO.NEXTVAL into prg1 from dual; ");
				 * fApp.appendTextToCurrentFile("prg2 := null; ");
				 * 
				 * fApp.appendTextToCurrentFile(avvCve);
				 * 
				 * fApp.appendTextToCurrentFile("select S_AM_MOVIMENTO_APPOGGIO.NEXTVAL into prg2 from dual; ");
				 * fApp.appendTextToCurrentFile("temp := prg1; "); fApp.appendTextToCurrentFile("prg1 := prg2; ");
				 * fApp.appendTextToCurrentFile("prg2 := temp; ");
				 * 
				 * fApp.appendTextToCurrentFile(script);
				 * 
				 * fApp.appendTextToCurrentFile("COMMIT; "); fApp.appendTextToCurrentFile("exception ");
				 * fApp.appendTextToCurrentFile("when others then ");
				 * fApp.appendTextToCurrentFile("dbms_output.put_line('Errore= ' || sqlcode || ' ' || sqlerrm); ");
				 * fApp.appendTextToCurrentFile("rollback; "); fApp.appendTextToCurrentFile("end; ");
				 * fApp.appendTextToCurrentFile("/ "); }else{
				 */
				String script = generaScriptInsertDaSourceBean(riga, configbase + parametri[0],
						"INSERT_MOVIMENTO_APPOGGIO", "Inserimento Movimento Appoggio", false,
						new BigDecimal(parametri[6]));
				fApp.appendTextToCurrentFile(script);
				/*
				 * fApp.appendTextToCurrentFile("/ "); }
				 */

				logBatch.writeLog("scritto");

				countScriptPerFile++;

				provDestPrec = provDest;
			}

			if (countScriptPerFile <= maxScriptPerFile) {
				fApp.appendTextToCurrentFile("set define on");
				fApp.appendTextToCurrentFile("COMMIT;");
				fApp.doneAppendCurrentFile(countFile + "_" + provSorg + "_X_" + provDest + "_" + countScriptPerFile,
						pathOutput + File.separator + provDest);
			}

			logBatch.writeLog("Il batch è terminato correttamente");
			logBatch.writeLog("===========================================");
			logBatch.writeLog("========== FINE ESECUZIONE BATCH ==========");
			logBatch.writeLog("===========================================");
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
	 */
	public boolean setParametri(String[] args) {
		// C:\EclipseJBoss\SilWeb\WebContent\WEB-INF\batch\avviaBatchScriptMigrazioni.bat
		// C:\EclipseJBoss\SilWeb\WebContent\WEB-INF\classes

		if (args.length != 9) {
			return false;
		}

		parametri = new String[9];
		parametri[0] = args[0]; // File xml per il mapping tra i campi di AM_MOVIMENTO e AM_MOVIMENTO_APPOGGIO
		parametri[1] = args[1]; // Data inizio range in cui prendere i movimenti.
		parametri[2] = args[2]; // Data fine range in cui prendere i movimenti
		parametri[3] = args[3]; // Provincia di destinazione migrazioni
		parametri[4] = args[4]; // Tipo migrazioni da considerare
		parametri[5] = args[5]; // Numero max di movimenti per file .sql da generare

		parametri[6] = args[6]; // user
		parametri[7] = args[7]; // profilo user
		parametri[8] = args[8]; // gruppo user

		return true;
	}

	public void release() {
		if (applicationContainer != null)
			applicationContainer.release();
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
				+ "m.FLGLEGGE68 as FLGLEGGE68," + "m.FLGASSOBBL as FLGASSOBBL, m.CODCATASSOBBL as CODCATASSOBBL,"
				+ "m.FLGLAVDOMICILIO as flgLavDom," + "m.CODAGEVOLAZIONE as codBenefici," + "m.CODORARIO as codOrario,"
				+ "m.NUMORESETT as numOreSett," + "m.CODMVCESSAZIONE as MotivoCess," + "m.DATFINEMOV as DataFineMov,"
				+ "m.STRMATRICOLA as MatricolaAvv," + "m.CODTIPOCONTRATTO as CodTipoAvv," + "null as CODNORMATIVA,"
				+ "m.CODGRADO as codGrado," + "movApp.FLGARTIGIANA as flgAzArtigiana,"
				+ "movApp.NUMMESIAPPRENDISTATO as DurataAppAvv," + "'C' as CODMONOPROV,"
				+ "m.STRPOSINPS as MatricolaINPS," + "m.STRPATINAIL as PATPosINAIL," + "'DV' AS CODSTATOATTO,"
				+ "m.FLGINTERASSPROPRIA as FlgAssPropria,"
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
