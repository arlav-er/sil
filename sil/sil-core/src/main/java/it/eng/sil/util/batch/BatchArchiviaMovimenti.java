package it.eng.sil.util.batch;

import java.io.File;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Properties;
import java.util.Vector;

import com.engiweb.framework.base.ApplicationContainer;
import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.configuration.ConfigSingleton;
import com.engiweb.framework.error.EMFInternalError;

import it.eng.afExt.utils.DateUtils;
import it.eng.afExt.utils.MessageCodes;
import it.eng.afExt.utils.TransactionQueryExecutor;
import it.eng.sil.Values;
import it.eng.sil.module.batch.Constants;
import it.eng.sil.util.amministrazione.impatti.DBLoad;
import it.eng.sil.util.amministrazione.impatti.LogBatch;

/**
 * @author Landi
 * 
 */
public class BatchArchiviaMovimenti {
	private String[] parametri;
	private LogBatch logBatch;
	private ApplicationContainer applicationContainer;

	/**
	 * Costruttore
	 */
	public BatchArchiviaMovimenti(String args[]) throws Exception {
		String dir = ConfigSingleton.getLogBatchPath();
		String nomeFile = File.separator + "BatchArchiviaMovimenti";
		String data = DateUtils.getNow();
		data = data.replace('/', '-');
		nomeFile = nomeFile + data + ".log";
		logBatch = new LogBatch(nomeFile, dir);
		applicationContainer = ApplicationContainer.getInstance();
	}

	public static void main(String[] args) {
		BatchArchiviaMovimenti objBatch = null;
		try {
			objBatch = new BatchArchiviaMovimenti(args);
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
			ArrayList prgLavArray = new ArrayList();
			Vector lavoratori = new Vector();
			logBatch.writeLog("=========== Avvio Batch per archiviare i movimenti ===========");
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
			int numGiorniDaUltimaValidazione = new Integer(parametri[3]).intValue();
			Vector rows;
			// parametri[3] < 0 effettuo pulizia movimenti con data < 01/01/2008 altramenti proseguo come prima
			if (numGiorniDaUltimaValidazione < 0) {
				logBatch.writeLog("Pulizia Movimenti Pre2008");
				rows = DBLoad.getMovimentiPre2008();
			} else {
				logBatch.writeLog("Archiviazione movimenti giorni " + numGiorniDaUltimaValidazione);
				rows = DBLoad.getMovimentiDaArchiviare(MessageCodes.Batch.USER_RICICLAGGIO,
						numGiorniDaUltimaValidazione);
			}

			// prgMovAppArray contiene alla fine del ciclo tutti i movimenti che
			// devono essere
			// spostati nella tabella di ARCHIVIO. Nel vettore in caso di
			// cessazione veloce,
			// l'avviamento precede la cessazione.

			// prgMovAVVCVE contiene i prg degli avviamenti da cessazioni
			// veloci.

			msg = "Saranno processati " + rows.size() + " movimenti";
			logBatch.writeLog(msg);

			for (int i = 0; i < rows.size(); i++) {
				SourceBean mov = (SourceBean) rows.get(i);
				Object prgMovApp = mov.getAttribute("PRGMOVIMENTOAPP");
				Object prgMovAppCVE = mov.getAttribute("PRGMOVIMENTOAPPCVE");
				prgLavArray.add(mov.getAttribute("CDNLAVORATORE"));
				if (prgMovAppCVE != null) {
					prgMovAppArray.add(prgMovAppCVE);
					prgMovAVVCVE.add(prgMovAppCVE);
					prgLavArray.add(mov.getAttribute("CDNLAVORATORE"));
				}
				prgMovAppArray.add(prgMovApp);
			}

			logBatch.writeLog("numero movimenti:" + prgMovAppArray.size());
			logBatch.writeLog("numero movimenti veloci:" + prgMovAVVCVE.size());
			logBatch.writeLog("numero lavoratori:" + prgLavArray.size());

			msg = "Inizio  archiviazione.";
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
					Object cdnLav = prgLavArray.get(i);
					movISAvvCVE = cercaMovimento(prgMov, prgMovAVVCVE);
					if (!movISAvvCVE) {
						msg = "Si sta elaborando il movimento con prg:" + prgMov.toString();
						logBatch.writeLog(msg);
					}
					insertMovAppArchivio(txExec, prgMov);

					insertMovAgevAppArchivio(txExec, prgMov);

					// Controllo se il movimento è un avviamento da cessazione veloce. In tal caso, prima di
					// cancellarlo devo inserire nella tabella di archivio anche la cessazione, e poi cancellare
					// dalla tabella di appoggio la cessazione e l'avviamento (tutto in una sola transazione)
					if (!movISAvvCVE) {
						String deleteQueryAgevolazioni = "delete from  am_mov_agev_app where PRGMOVIMENTOAPP = "
								+ prgMov;
						txExec.executeQueryByStringStatement(deleteQueryAgevolazioni, null,
								TransactionQueryExecutor.DELETE);
						logBatch.writeLog("delete from  am_mov_agev_app where PRGMOVIMENTOAPP = " + prgMov);

						String deleteQuery = "delete from AM_MOVIMENTO_APPOGGIO where PRGMOVIMENTOAPP = " + prgMov;
						txExec.executeQueryByStringStatement(deleteQuery, null, TransactionQueryExecutor.DELETE);
						logBatch.writeLog("delete from AM_MOVIMENTO_APPOGGIO where PRGMOVIMENTOAPP = " + prgMov);

						if (prgUltimoAvvCVE != null) {
							deleteQueryAgevolazioni = "delete from  am_mov_agev_app where PRGMOVIMENTOAPP = "
									+ prgUltimoAvvCVE;
							txExec.executeQueryByStringStatement(deleteQueryAgevolazioni, null,
									TransactionQueryExecutor.DELETE);
							logBatch.writeLog(
									"delete from  am_mov_agev_app where PRGMOVIMENTOAPP = " + prgUltimoAvvCVE);
							deleteQuery = "delete from AM_MOVIMENTO_APPOGGIO where PRGMOVIMENTOAPP = "
									+ prgUltimoAvvCVE;
							txExec.executeQueryByStringStatement(deleteQuery, null, TransactionQueryExecutor.DELETE);
							logBatch.writeLog(
									"delete from AM_MOVIMENTO_APPOGGIO where PRGMOVIMENTOAPP = " + prgUltimoAvvCVE);
							prgUltimoAvvCVE = null;
						}
						insertEvidenza(lavoratori, txExec, cdnLav);
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

			// Se sto facendo la pulizia dei movimenti ante 2008 cancello cessazioni orfane (in cui manca l'avviamento)
			puliziaCessazioniOrfanePre2008(prgMovAVVCVE, numGiorniDaUltimaValidazione, txExec, prgMov);

			// Se sto facendo la pulizia dei movimenti ante 2008 cancello avviamenti veloci orfani (in cui manca la
			// cessazione)
			if (numGiorniDaUltimaValidazione < 0) {
				puliziaAvvVelociPre2008();
			}

			logBatch.writeLog("Il batch per archiviare i movimenti è terminato.");
		} catch (Exception e) {
			logBatch.writeLog("ERRORE: " + e.toString());
			logBatch.writeLog("Il batch è terminato con degli errori.");
		}
	}// end method

	private void puliziaCessazioniOrfanePre2008(ArrayList prgMovAVVCVE, int numGiorniDaUltimaValidazione,
			TransactionQueryExecutor txExec, Object prgMov) throws Exception, EMFInternalError {
		String msg;
		Vector rows;
		int processedOK = 0;
		int processedError = 0;
		ArrayList prgMovAppArray = new ArrayList();
		ArrayList prgLavArray = new ArrayList();
		Vector lavoratori = new Vector();

		if (numGiorniDaUltimaValidazione < 0) {
			logBatch.writeLog("Pulizia Movimenti Pre2008 - pulisco cessazioni orfane");
			rows = DBLoad.getCessazioniOrfanePre2008();

			for (int i = 0; i < rows.size(); i++) {
				SourceBean mov = (SourceBean) rows.get(i);
				Object prgMovApp = mov.getAttribute("PRGMOVIMENTOAPP");
				prgLavArray.add(mov.getAttribute("CDNLAVORATORE"));
				prgMovAppArray.add(prgMovApp);
			}

			for (int i = 0; i < prgMovAppArray.size(); i++) {
				try {
					prgMov = prgMovAppArray.get(i);
					msg = "Si sta elaborando il movimento con prg:" + prgMov.toString();
					logBatch.writeLog(msg);

					txExec = new TransactionQueryExecutor(Values.DB_SIL_DATI);
					txExec.initTransaction();

					insertMovAppArchivioOrfane(txExec, prgMov);

					String deleteQuery = "delete from AM_MOVIMENTO_APPOGGIO where PRGMOVIMENTOAPP = " + prgMov;
					txExec.executeQueryByStringStatement(deleteQuery, null, TransactionQueryExecutor.DELETE);
					logBatch.writeLog("delete from AM_MOVIMENTO_APPOGGIO where PRGMOVIMENTOAPP = " + prgMov);

					Object cdnLav = prgLavArray.get(i);
					insertEvidenza(lavoratori, txExec, cdnLav);
					txExec.commitTransaction();
					processedOK = processedOK + 1;
					msg = "Elaborazione movimento con prg:" + prgMov.toString() + " terminata con successo.";

				} catch (Exception ex) {
					if (txExec != null) {
						txExec.rollBackTransaction();
						txExec = null;
					}
					processedError = processedError + 1;
					logBatch.writeLog("ERRORE: " + ex.getMessage());
					logBatch.writeLog("L'elaborazione del movimento con prg:" + prgMov.toString()
							+ " è terminata con degli errori.");
				}
			}

			msg = "";
			logBatch.writeLog(msg);
			logBatch.writeLog("Movimenti orfani processati con successo: " + processedOK);
			logBatch.writeLog("Movimenti orfani processati con errore: " + processedError);
			msg = "";
			logBatch.writeLog(msg);
		}
	}

	private void puliziaAvvVelociPre2008() throws Exception, EMFInternalError {
		String msg;
		Vector rows;
		Object prgMovApp = null;
		TransactionQueryExecutor txExec = null;
		int processedOK = 0;
		int processedError = 0;

		logBatch.writeLog("Pulizia Avviamenti Veloci Pre2008 - pulisco avviamenti orfani");
		rows = DBLoad.getQueryByStatement("GET_AVV_VELOCI_ORFANI_PRE_2008");
		int numAvvVelociOrfani = rows.size();
		for (int i = 0; i < numAvvVelociOrfani; i++) {
			try {
				SourceBean mov = (SourceBean) rows.get(i);
				prgMovApp = mov.getAttribute("PRGMOVIMENTOAPP");
				msg = "Si sta elaborando il movimento con prg:" + prgMovApp.toString();
				logBatch.writeLog(msg);

				txExec = new TransactionQueryExecutor(Values.DB_SIL_DATI);
				txExec.initTransaction();
				String deleteQuery = "delete from AM_MOVIMENTO_APPOGGIO where PRGMOVIMENTOAPP = " + prgMovApp;
				txExec.executeQueryByStringStatement(deleteQuery, null, TransactionQueryExecutor.DELETE);
				txExec.commitTransaction();

				logBatch.writeLog("delete from AM_MOVIMENTO_APPOGGIO where PRGMOVIMENTOAPP = " + prgMovApp);
				processedOK = processedOK + 1;
				msg = "Elaborazione movimento con prg:" + prgMovApp.toString() + " terminata con successo.";
			} catch (Exception ex) {
				if (txExec != null) {
					txExec.rollBackTransaction();
					txExec = null;
				}
				processedError = processedError + 1;
				logBatch.writeLog("ERRORE: " + ex.getMessage());
				logBatch.writeLog("L'elaborazione del movimento con prg:" + prgMovApp.toString()
						+ " è terminata con degli errori.");
			}
		}
		msg = "";
		logBatch.writeLog(msg);
		logBatch.writeLog("Movimenti avviamenti veloci orfani processati con successo: " + processedOK);
		logBatch.writeLog("Movimenti avviamenti veloci orfani processati con errore: " + processedError);
		msg = "";
		logBatch.writeLog(msg);
	}

	private void insertEvidenza(Vector lavoratori, TransactionQueryExecutor txExec, Object cdnLav)
			throws EMFInternalError {
		boolean trovato = cdnLav == null || cercaLavoratore(lavoratori, cdnLav);
		if (!trovato) {
			// inserimento evidenza sul lavoratore
			lavoratori.add(cdnLav);
			String statementInsertEv = "insert into AN_EVIDENZA (PRGEVIDENZA, CDNLAVORATORE, "
					+ " DATDATASCAD, STREVIDENZA, PRGTIPOEVIDENZA, CDNUTINS, DTMINS, CDNUTMOD, DTMMOD) "
					+ " values (S_AN_EVIDENZA.nextVal, " + cdnLav + ", to_date('01/01/2100','dd/mm/yyyy'), "
					+ " 'Esistono movimenti NON VALIDATI in archivio', 23," + MessageCodes.Batch.USER_RICICLAGGIO
					+ ", sysdate, " + MessageCodes.Batch.USER_RICICLAGGIO + ", sysdate)";
			txExec.executeQueryByStringStatement(statementInsertEv, null, TransactionQueryExecutor.INSERT);
			logBatch.writeLog("insert  into into AN_EVIDENZA per cdnLav" + cdnLav);
		}
	}

	private void insertMovAgevAppArchivio(TransactionQueryExecutor txExec, Object prgMov) throws EMFInternalError {
		String statementInsertAgevolazioni = "insert into am_mov_agev_app_archivio "
				+ "(prgmovagevapparchivio, prgmovimentoapp, codagevolazione, cdnutins, dtmins, cdnutmod, dtmmod) "
				+ "select s_am_mov_agev_app_archivio.nextval, prgmovimentoapp, codagevolazione, cdnutins, dtmins, cdnutmod, dtmmod "
				+ " from am_mov_agev_app where prgmovimentoapp = " + prgMov;
		txExec.executeQueryByStringStatement(statementInsertAgevolazioni, null, TransactionQueryExecutor.INSERT);
		logBatch.writeLog("insert  into am_mov_agev_app_archivio " + prgMov);
	}

	private void insertMovAppArchivio(TransactionQueryExecutor txExec, Object prgMov) throws EMFInternalError {
		String statementInsert = Constants.statementInsertArchivio + prgMov;
		txExec.executeQueryByStringStatement(statementInsert, null, TransactionQueryExecutor.INSERT);
		logBatch.writeLog("insert  into AM_MOV_APP_ARCHIVIO " + prgMov);
	}

	private void insertMovAppArchivioOrfane(TransactionQueryExecutor txExec, Object prgMov) throws EMFInternalError {
		String statementInsert = Constants.statementInsertArchivio + prgMov;
		txExec.executeQueryByStringStatement(statementInsert, null, TransactionQueryExecutor.INSERT);
		logBatch.writeLog("insert  into AM_MOV_APP_ARCHIVIO " + prgMov);
	}

	/**
	 * Metodo per settare i parametri passati da linea di comando
	 */
	public void setParametri(String[] args) {
		parametri = new String[4];
		parametri[0] = args[0]; // user
		parametri[1] = args[1];// profilo
		parametri[2] = args[2]; // gruppo
		// parametri[3] contiene (n) giorni necessari ad archiviare i movimenti
		// che non vengono validati da più di n giorni

		// parametri[3] < 0 effettuo pulizia movimenti con data < 01/01/2008
		if (args.length == 4) {
			parametri[3] = args[3];
		} else {
			parametri[3] = new Integer(MessageCodes.Batch.GIORNI_TRASCORSI_DA_IMPORTAZIONE).toString();
		}
	}

	private boolean cercaLavoratore(Vector lavoratori, Object cdnLav) {
		boolean trovato = false;
		for (int i = 0; i < lavoratori.size(); i++) {
			Object lavCurr = lavoratori.get(i);
			if (lavCurr.equals(cdnLav)) {
				trovato = true;
				break;
			}
		}
		return trovato;
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
