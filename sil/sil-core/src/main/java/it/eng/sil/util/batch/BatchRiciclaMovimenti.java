package it.eng.sil.util.batch;

import java.io.File;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Properties;
import java.util.Vector;

import com.engiweb.framework.base.ApplicationContainer;
import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.configuration.ConfigSingleton;

import it.eng.afExt.utils.DateUtils;
import it.eng.afExt.utils.MessageCodes;
import it.eng.afExt.utils.TransactionQueryExecutor;
import it.eng.sil.Values;
import it.eng.sil.util.amministrazione.impatti.DBLoad;
import it.eng.sil.util.amministrazione.impatti.LogBatch;

/**
 * @author Landi
 * 
 */

public class BatchRiciclaMovimenti implements BatchRunnable {
	private String[] parametri;
	private LogBatch logBatch;
	private ApplicationContainer applicationContainer;

	/**
	 * Costruttore
	 */
	public BatchRiciclaMovimenti(String args[]) throws Exception {
		String dir = ConfigSingleton.getLogBatchPath();
		String nomeFile = File.separator + "BatchRiciclaMovimenti";
		String data = DateUtils.getNow();
		data = data.replace('/', '-');
		nomeFile = nomeFile + data + ".log";
		logBatch = new LogBatch(nomeFile, dir);
		applicationContainer = ApplicationContainer.getInstance();
	}

	public static void main(String[] args) {
		BatchRiciclaMovimenti objBatch = null;
		try {
			objBatch = new BatchRiciclaMovimenti(args);
			objBatch.setParametri(args);
			objBatch.start();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			objBatch.release();
		}
	}

	public void start() {
		try {
			// avviamenti da cessazioni veloce
			ArrayList prgMovAVVCVE = new ArrayList();
			ArrayList prgMovAppArray = new ArrayList();
			logBatch.writeLog("=========== Avvio Batch per riempire la tabella di riciclaggio ===========");
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
			logBatch.writeLogVarJava("====== Fine sezione di configurazione ======");
			String msg = "";
			Vector rows = DBLoad.getMovimentiRiciclaggio(MessageCodes.Batch.GIORNI_TRASCORSI_DA_IMPORTAZIONE);

			// prgMovAppArray contiene alla fine del ciclo tutti i movimenti che
			// devono essere
			// spostati nella tabella di riciclaggio. Nel vettore in caso di
			// cessazione veloce,
			// l'avviamento precede la cessazione.

			// prgMovAVVCVE contiene i prg degli avviamenti da cessazioni
			// veloci.
			for (int i = 0; i < rows.size(); i++) {
				SourceBean mov = (SourceBean) rows.get(i);
				Object prgMovAppCVE = mov.getAttribute("PRGMOVIMENTOAPPCVE");
				Object prgMovApp = mov.getAttribute("PRGMOVIMENTOAPP");
				if (prgMovAppCVE != null) {
					prgMovAppArray.add(prgMovAppCVE);
					prgMovAVVCVE.add(prgMovAppCVE);
				}
				prgMovAppArray.add(prgMovApp);
			}

			msg = "Saranno processati " + rows.size() + " movimenti";
			logBatch.writeLog(msg);
			TransactionQueryExecutor txExec = null;
			// per ognuno di questi
			logBatch.writeLog("Avvio batch");
			Object prgMov = null;
			boolean nuovaTransazione = true;
			Object prgUltimoAvvCVE = null;
			boolean movISAvvCVE = false;
			int processedOK = 0;
			int processedError = 0;
			for (int i = 0; i < prgMovAppArray.size(); i++) {
				try {
					if (nuovaTransazione) {
						txExec = new TransactionQueryExecutor(Values.DB_SIL_DATI);
						txExec.initTransaction();
					}
					msg = "";
					prgMov = prgMovAppArray.get(i);
					movISAvvCVE = cercaMovimento(prgMov, prgMovAVVCVE);
					if (!movISAvvCVE) {
						msg = "Si sta elaborando il movimento con prg:" + prgMov.toString();
						logBatch.writeLog(msg);
					}
					String statementInsert = "insert into AM_MOV_APP_RICICLAGGIO (PRGMOVIMENTOAPP, STRAZCODICEFISCALE, STRAZPARTITAIVA, "
							+ " STRAZRAGIONESOCIALE, CODAZATECO, CODAZCCNL, NUMAZDIPENDENTI, STRPATINAIL, STRUAINDIRIZZO, "
							+ " CODUACOM,STRUACAP,STRUATEL,STRUAFAX,STRUAEMAIL,STRUALINDIRIZZO,CODUALCOM,STRCODICEFISCALE, "
							+ " STRCOGNOME,STRNOME,STRSESSO,DATNASC,CODCOMNASC,CODCITTADINANZA,CODCOMDOM,STRINDIRIZZODOM, "
							+ " STRCAPDOM,DATSCADENZA,CODTIPOTITOLO,CODCPI,DATCOMUNICAZ,DATINIZIOMOV,CODTIPOMOV, "
							+ " CODMANSIONE,STRDESATTIVITA,CODCONTRATTO,CODMONOTEMPO,FLGSOCIO,CODCCNL,DECRETRIBUZIONEMEN, "
							+ " NUMLIVELLO,FLGLEGGE68,FLGLAVDOMICILIO,CODAGEVOLAZIONE,CODORARIO,CODMVCESSAZIONE,DATFINEMOV, "
							+ " STRNOTE,CDNUTINS,DTMINS,CDNUTMOD,DTMMOD,STRMATRICOLA,CODTIPOASS,CODNORMATIVA,CODGRADO, "
							+ " FLGARTIGIANA,DATCFL,STRNUMCFL,STRTIPOCFL,NUMMESIAPPRENDISTATO,CODMONOPROV,STRPOSINPS,CODSTATOATTO, "
							+ " FLGASSPROPRIA,STRAZINTCODICEFISCALE,STRAZINTPARTITAIVA,STRAZINTRAGIONESOCIALE,CODAZINTATECO,NUMAZINTDIPENDENTI, "
							+ " CODAZINTTIPOAZIENDA,STRUAINTINDIRIZZO,CODUAINTCOM,STRUAINTCAP,STRAZNUMALBOINTERINALI,STRNUMREGISTROCOMMITT, "
							+ " DATREGISTROCOMMITT,FLGRETRIBUZIONEMENCCNL,STRLUOGODILAVORO,CODCPILAV,DATVISITAMEDICA,STRAPPCODICEFISCALETUTORE, "
							+ " STRAPPCOGNOMETUTORE,STRAPPNOMETUTORE,NUMAPPANNIESPTUTORE,STRAPPLIVELLOTUTORE,CODAPPMANSIONETUTORE,CODAZTIPOAZIENDA, "
							+ " STRAZINTNUMCONTRATTO,DATAZINTINIZIOCONTRATTO,DATAZINTFINECONTRATTO,STRAZINTRAP,NUMAZINTSOGGETTI,NUMGGTRAMOVCOMUNICAZIONE, "
							+ " STRREFERENTE,STRNUMPRTPROVINCIA,NUMGGPREVISTIAGR,NUMGGEFFETTUATIAGR,PRGMOVIMENTOAPPCVE,FLGASSDACESS,FLGTITOLARETUTORE, "
							+ " NUMORESETT,CODCOMNASCDI,DATNASCDI,STRSESSODI,CODLAVORAZIONE,CODLIVELLOAGR,CODCATEGORIA,STRVERSIONETRACCIATO, "
							+ " CODQUALIFICASRQ,CODTIPOCOMUNIC,CODCOMUNICAZIONE,CODTIPODOCEX,STRNUMDOCEX,CODMOTIVOPERMSOGGEX,CODTIPOENTEPREV, "
							+ " STRCODICEENTEPREV,CODTIPOTRASF,CODTIPOCONTRATTO,STRCODICEFISCALEAZPREC,STRRAGIONESOCIALEAZPREC,CODCOMAZPREC,STRINDIRIZZOAZPREC, "
							+ " STRCAPAZPREC,CODATECOAZPREC,STRPATAZPREC,STRTELAZPREC,STRFAXAZPREC,STREMAILAZPREC, "
							+ " DATINIZIORAPLAV,DATFINERAPLAV,STRVOCETARIFFA1,STRVOCETARIFFA2,STRVOCETARIFFA3,CODSOGGETTO, "
							+ " CODCOMUNICAZIONEPREC,DATFINEPRORAPLAV,DATCESSRAPLAV,CODTIPOSOMM, NUMPROTESTERNO, NUMANNOPROTESTERNO, NUMCONVENZIONE, "
							+ " DATCONVENZIONE, NUMKLOMOVAPP, DECRETRIBUZIONEANN, STRTRACCIATO) "
							+ " SELECT PRGMOVIMENTOAPP, STRAZCODICEFISCALE, STRAZPARTITAIVA,STRAZRAGIONESOCIALE,CODAZATECO, "
							+ " CODAZCCNL,NUMAZDIPENDENTI,STRPATINAIL,STRUAINDIRIZZO,CODUACOM,STRUACAP,STRUATEL,STRUAFAX,STRUAEMAIL, "
							+ " STRUALINDIRIZZO,CODUALCOM,STRCODICEFISCALE,STRCOGNOME,STRNOME,STRSESSO,DATNASC,CODCOMNASC,CODCITTADINANZA, "
							+ " CODCOMDOM,STRINDIRIZZODOM,STRCAPDOM,DATSCADENZA,CODTIPOTITOLO,CODCPI,DATCOMUNICAZ,DATINIZIOMOV,CODTIPOMOV, "
							+ " CODMANSIONE,STRDESATTIVITA,CODCONTRATTO,CODMONOTEMPO,FLGSOCIO,CODCCNL,DECRETRIBUZIONEMEN,NUMLIVELLO,FLGLEGGE68, "
							+ " FLGLAVDOMICILIO,CODAGEVOLAZIONE,CODORARIO,CODMVCESSAZIONE,DATFINEMOV,STRNOTE,CDNUTINS,DTMINS,CDNUTMOD,DTMMOD, "
							+ " STRMATRICOLA,CODTIPOASS,CODNORMATIVA,CODGRADO,FLGARTIGIANA,DATCFL,STRNUMCFL,STRTIPOCFL,NUMMESIAPPRENDISTATO, "
							+ " CODMONOPROV,STRPOSINPS,CODSTATOATTO,FLGASSPROPRIA,STRAZINTCODICEFISCALE,STRAZINTPARTITAIVA,STRAZINTRAGIONESOCIALE, "
							+ " CODAZINTATECO,NUMAZINTDIPENDENTI,CODAZINTTIPOAZIENDA,STRUAINTINDIRIZZO,CODUAINTCOM,STRUAINTCAP,STRAZNUMALBOINTERINALI, "
							+ " STRNUMREGISTROCOMMITT,DATREGISTROCOMMITT,FLGRETRIBUZIONEMENCCNL,STRLUOGODILAVORO,CODCPILAV,DATVISITAMEDICA,STRAPPCODICEFISCALETUTORE, "
							+ " STRAPPCOGNOMETUTORE,STRAPPNOMETUTORE,NUMAPPANNIESPTUTORE,STRAPPLIVELLOTUTORE,CODAPPMANSIONETUTORE,CODAZTIPOAZIENDA,STRAZINTNUMCONTRATTO,DATAZINTINIZIOCONTRATTO, "
							+ " DATAZINTFINECONTRATTO,STRAZINTRAP,NUMAZINTSOGGETTI,NUMGGTRAMOVCOMUNICAZIONE,STRREFERENTE,STRNUMPRTPROVINCIA,NUMGGPREVISTIAGR,NUMGGEFFETTUATIAGR,PRGMOVIMENTOAPPCVE, "
							+ " FLGASSDACESS,FLGTITOLARETUTORE,NUMORESETT,CODCOMNASCDI,DATNASCDI,STRSESSODI,CODLAVORAZIONE,CODLIVELLOAGR,CODCATEGORIA,STRVERSIONETRACCIATO,CODQUALIFICASRQ, "
							+ " CODTIPOCOMUNIC,CODCOMUNICAZIONE,CODTIPODOCEX,STRNUMDOCEX,CODMOTIVOPERMSOGGEX,CODTIPOENTEPREV,STRCODICEENTEPREV,CODTIPOTRASF,CODTIPOCONTRATTO,STRCODICEFISCALEAZPREC, "
							+ " STRRAGIONESOCIALEAZPREC,CODCOMAZPREC,STRINDIRIZZOAZPREC,STRCAPAZPREC,CODATECOAZPREC,STRPATAZPREC,STRTELAZPREC,STRFAXAZPREC,STREMAILAZPREC,"
							+ " DATINIZIORAPLAV,DATFINERAPLAV,CODSOGGETTO,CODCOMUNICAZIONEPREC,DATFINEPRORAPLAV,DATCESSRAPLAV,CODTIPOSOMM, "
							+ " NUMPROTESTERNO, NUMANNOPROTESTERNO, NUMCONVENZIONE, DATCONVENZIONE, NUMKLOMOVAPP, DECRETRIBUZIONEANN, STRTRACCIATO "
							+ " from AM_MOVIMENTO_APPOGGIO WHERE PRGMOVIMENTOAPP = " + prgMov;
					txExec.executeQueryByStringStatement(statementInsert, null, TransactionQueryExecutor.INSERT);
					// Controllo se il movimento è un avviamento da cessazione
					// veloce. In tal caso, prima di
					// cancellarlo devo inserire nella tabella di riciclaggio
					// anche la cessazione, e poi cancellare
					// dalla tabella di appoggio la cessazione e l'avviamento
					// (tutto in una sola transazione)
					if (!movISAvvCVE) {
						String deleteQuery = "delete from AM_MOVIMENTO_APPOGGIO where PRGMOVIMENTOAPP = " + prgMov;
						txExec.executeQueryByStringStatement(deleteQuery, null, TransactionQueryExecutor.DELETE);
						if (prgUltimoAvvCVE != null) {
							deleteQuery = "delete from AM_MOVIMENTO_APPOGGIO where PRGMOVIMENTOAPP = "
									+ prgUltimoAvvCVE;
							txExec.executeQueryByStringStatement(deleteQuery, null, TransactionQueryExecutor.DELETE);
							prgUltimoAvvCVE = null;
						}
						txExec.commitTransaction();
						msg = "Elaborazione movimento con prg:" + prgMov.toString() + " terminata con successo.";
						logBatch.writeLog(msg);
						nuovaTransazione = true;
						processedOK = processedOK + 1;
					} else {
						nuovaTransazione = false;
						prgUltimoAvvCVE = prgMov;
					}
				} catch (Exception ex) {
					if (txExec != null) {
						txExec.rollBackTransaction();
						txExec = null;
					}
					nuovaTransazione = true;
					prgUltimoAvvCVE = null;
					if (!movISAvvCVE) {
						processedError = processedError + 1;
						logBatch.writeLog("ERRORE: " + ex.getMessage());
						logBatch.writeLog("L'elaborazione del movimento con prg:" + prgMov.toString()
								+ " è terminata con degli errori.");
					}
				}
			} // end for
			msg = "";
			logBatch.writeLog(msg);
			logBatch.writeLog("Movimenti processati con successo: " + processedOK);
			logBatch.writeLog("Movimenti processati con errore: " + processedError);
			msg = "";
			logBatch.writeLog(msg);
			logBatch.writeLog("Il batch per riempire la tabella di riciclaggio è terminato.");
		} catch (Exception e) {
			logBatch.writeLog("ERRORE: " + e.toString());
			logBatch.writeLog("Il batch è terminato con degli errori.");
		}
	}// end method

	/**
	 * Metodo per settare i parametri passati da linea di comando
	 */
	public void setParametri(String[] args) {
		parametri = new String[3];
		parametri[0] = args[0]; // user
		parametri[1] = args[1];// profilo
		parametri[2] = args[2]; // gruppo
	}

	public void release() {
		if (applicationContainer != null)
			applicationContainer.release();
	}

	public boolean cercaMovimento(Object prg, ArrayList listMov) {
		for (int i = 0; i < listMov.size(); i++) {
			Object prgCurr = listMov.get(i);
			if (prgCurr.equals(prg))
				return true;
		}
		return false;
	}
}
