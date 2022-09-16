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
import it.eng.afExt.utils.TransactionQueryExecutor;
import it.eng.sil.Values;
import it.eng.sil.module.batch.Constants;
import it.eng.sil.module.movimenti.MultipleTransactionQueryExecutor;
import it.eng.sil.module.movimenti.processors.ProcessorsUtils;
import it.eng.sil.util.amministrazione.impatti.LogBatch;

public class BatchArchiviaMovimentiUmbria {
	private String[] parametri;
	private LogBatch logBatch;
	private ApplicationContainer applicationContainer;

	public BatchArchiviaMovimentiUmbria(String args[]) throws Exception {
		String dir = ConfigSingleton.getLogBatchPath();
		String nomeFile = File.separator + "BatchArchiviaMovimenti";
		String data = DateUtils.getNow();
		data = data.replace('/', '-');
		nomeFile = nomeFile + data + ".log";
		logBatch = new LogBatch(nomeFile, dir);
		applicationContainer = ApplicationContainer.getInstance();
	}

	public static void main(String[] args) {
		BatchArchiviaMovimentiUmbria objBatch = null;
		try {
			objBatch = new BatchArchiviaMovimentiUmbria(args);
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

			//
			String msg = "";
			SourceBean result = null;
			Vector v = null;
			Vector rows = null;
			MultipleTransactionQueryExecutor transConfig = null;
			String selectquery = "";
			try {
				selectquery = "SELECT CODERRORE FROM TS_ARCHIVIA_MOVIMENTI WHERE CODCONTESTO = 'ARCHMOV' AND FLGARCHIVIA = 'S'";
				transConfig = new MultipleTransactionQueryExecutor(Values.DB_SIL_DATI);
				result = ProcessorsUtils.executeSelectQuery(selectquery, transConfig);
				v = result.getAttributeAsVector("ROW");
				int vSize = v.size();
				if (vSize > 0) {
					String strCodErrori = "";
					for (int k = 0; k < vSize; k++) {
						SourceBean sbErrori = (SourceBean) v.get(k);
						String codErrore = sbErrori.getAttribute("CODERRORE").toString();
						if (strCodErrori.equals("")) {
							strCodErrori = "'" + codErrore + "'";
						} else {
							strCodErrori = strCodErrori + ", '" + codErrore + "'";
						}
					}
					selectquery = "SELECT DISTINCT APP.PRGMOVIMENTOAPP, APP.PRGMOVIMENTOAPPCVE, LAV.CDNLAVORATORE "
							+ "FROM AM_MOVIMENTO_APPOGGIO APP, AM_RISULTATO_MOVIMENTO RIS, AN_LAVORATORE LAV "
							+ "WHERE APP.PRGMOVIMENTOAPP = RIS.PRGMOVIMENTOAPP AND LAV.STRCODICEFISCALE = APP.STRCODICEFISCALE "
							+ "AND NVL(FLGASSDACESS, 'N') != 'S' AND RIS.CODERRORE in (" + strCodErrori + ")";
					result = ProcessorsUtils.executeSelectQuery(selectquery, transConfig);
					rows = result.getAttributeAsVector("ROW");
				} else {
					logBatch.writeLog("Il batch di archiviazione per tipologia errore non è configurato.");
					throw new Exception("Il batch di archiviazione per tipologia errore non è configurato.");
				}
			} catch (Exception e1) {
				throw e1;
			} finally {
				if (transConfig != null) {
					transConfig.closeConnection();
				}
			}

			// prgMovAppArray contiene alla fine del ciclo tutti i movimenti che devono essere
			// spostati nella tabella di ARCHIVIO. Nel vettore in caso di cessazione veloce,
			// l'avviamento precede la cessazione.

			// prgMovAVVCVE contiene i prg degli avviamenti da cessazioni veloci.

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
			int cdnut = new Integer(parametri[0]).intValue();
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
					Object cdnLav = prgLavArray.get(i);
					movISAvvCVE = cercaMovimento(prgMov, prgMovAVVCVE);
					if (!movISAvvCVE) {
						msg = "Si sta elaborando il movimento con prg:" + prgMov.toString();
						logBatch.writeLog(msg);
					}
					String statementInsert = Constants.statementInsertArchivio + prgMov;

					txExec.executeQueryByStringStatement(statementInsert, null, TransactionQueryExecutor.INSERT);

					String statementInsertAgevolazioni = "insert into am_mov_agev_app_archivio "
							+ "(prgmovagevapparchivio, prgmovimentoapp, codagevolazione, cdnutins, dtmins, cdnutmod, dtmmod) "
							+ "select s_am_mov_agev_app_archivio.nextval, prgmovimentoapp, codagevolazione, cdnutins, dtmins, cdnutmod, dtmmod "
							+ " from am_mov_agev_app where prgmovimentoapp = " + prgMov;
					txExec.executeQueryByStringStatement(statementInsertAgevolazioni, null,
							TransactionQueryExecutor.INSERT);

					// Controllo se il movimento è un avviamento da cessazione veloce. In tal caso, prima di
					// cancellarlo devo inserire nella tabella di archivio anche la cessazione, e poi cancellare
					// dalla tabella di appoggio la cessazione e l'avviamento(tutto in una sola transazione)
					if (!cercaMovimento(prgMov, prgMovAVVCVE)) {
						String deleteQueryAgevolazioni = "delete from  am_mov_agev_app where PRGMOVIMENTOAPP = "
								+ prgMov;
						txExec.executeQueryByStringStatement(deleteQueryAgevolazioni, null,
								TransactionQueryExecutor.DELETE);
						String deleteQuery = "delete from AM_MOVIMENTO_APPOGGIO where PRGMOVIMENTOAPP = " + prgMov;
						txExec.executeQueryByStringStatement(deleteQuery, null, TransactionQueryExecutor.DELETE);
						if (prgUltimoAvvCVE != null) {
							deleteQueryAgevolazioni = "delete from  am_mov_agev_app where PRGMOVIMENTOAPP = "
									+ prgUltimoAvvCVE;
							txExec.executeQueryByStringStatement(deleteQueryAgevolazioni, null,
									TransactionQueryExecutor.DELETE);
							deleteQuery = "delete from AM_MOVIMENTO_APPOGGIO where PRGMOVIMENTOAPP = "
									+ prgUltimoAvvCVE;
							txExec.executeQueryByStringStatement(deleteQuery, null, TransactionQueryExecutor.DELETE);
							prgUltimoAvvCVE = null;
						}
						boolean trovato = cercaLavoratore(lavoratori, cdnLav);
						if (!trovato) {
							// inserimento evidenza sul lavoratore
							lavoratori.add(cdnLav);
							String statementInsertEv = "insert into AN_EVIDENZA (PRGEVIDENZA, CDNLAVORATORE, "
									+ " DATDATASCAD, STREVIDENZA, PRGTIPOEVIDENZA, CDNUTINS, DTMINS, CDNUTMOD, DTMMOD) "
									+ " values (S_AN_EVIDENZA.nextVal, " + cdnLav
									+ ", to_date('01/01/2100','dd/mm/yyyy'), "
									+ " 'Esistono movimenti NON VALIDATI in archivio', 23," + cdnut + ", sysdate, "
									+ cdnut + ", sysdate)";
							txExec.executeQueryByStringStatement(statementInsertEv, null,
									TransactionQueryExecutor.INSERT);
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
			logBatch.writeLog("Il batch per archiviare i movimenti è terminato.");
		} catch (Exception e) {
			logBatch.writeLog("ERRORE: " + e.toString());
			logBatch.writeLog("Il batch è terminato con degli errori.");
		}
	}// end method

	/**
	 * Metodo per settare i parametri passati da linea di comando
	 */
	public void setParametri(String[] args) {
		parametri = new String[4];
		parametri[0] = args[0]; // user
		parametri[1] = args[1];// profilo
		parametri[2] = args[2]; // gruppo
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
