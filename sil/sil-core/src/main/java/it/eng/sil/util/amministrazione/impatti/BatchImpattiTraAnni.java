package it.eng.sil.util.amministrazione.impatti;

import java.io.File;
import java.math.BigDecimal;
import java.util.Enumeration;
import java.util.Properties;
import java.util.Vector;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.SessionContainer;
import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.configuration.ConfigSingleton;

import it.eng.afExt.utils.DateUtils;
import it.eng.afExt.utils.TransactionQueryExecutor;
import it.eng.sil.Values;
import it.eng.sil.util.Sottosistema;
import it.eng.sil.util.batch.BatchRunnable;
import it.eng.sil.util.batch.mdb.BatchObject;
import it.eng.sil.util.batch.mdb.IBatchMDBConsumer;

public class BatchImpattiTraAnni implements BatchRunnable, IBatchMDBConsumer {
	private BatchObject batchObject;
	private String[] parametri;
	private String dataBatch = "";
	private LogBatch logBatch;

	/**
	 * Costruttore
	 */
	public BatchImpattiTraAnni(BatchObject batchObject) throws Exception {
		this.batchObject = batchObject;
		String dir = ConfigSingleton.getLogBatchPath();
		String nomeFile = File.separator + "BatchImpattiTraAnni";
		String data = "";
		if (this.batchObject.getParams().length == 7) { // viene chiamato dalla pagina
			data = this.batchObject.getParams()[0];
		} else {
			data = "01/01/" + DateUtils.getAnno(DateUtils.getNow()); // viene
																		// schedulato
		}
		dataBatch = data;
		data = data.replace('/', '-');
		nomeFile = nomeFile + data + ".log";
		logBatch = new LogBatch(nomeFile, dir);
	}

	@Override
	public void execBatch() {
		try {
			this.setParametri();
			this.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Metodo che implementa la logica del batch per gli impatti a cavallo di due anni
	 */
	public void start() {

		try {
			logBatch.writeLog("=========== Avvio Batch per gli impatti a cavallo di due anni ===========");
			String msgParametri = "";
			logBatch.writeLog(msgParametri);
			logBatch.writeLog("========================== PARAMETRI BATCH ============================");
			msgParametri = "";
			logBatch.writeLog(msgParametri);
			if (parametri[0] != null) {
				msgParametri = "Data di riferimento batch: " + parametri[0].toString();
				logBatch.writeLog(msgParametri);
			}
			if (parametri[1] != null) {
				msgParametri = "Considera solo i movimenti non ancora trattati dal batch: " + parametri[1].toString();
				logBatch.writeLog(msgParametri);
			}
			if (parametri[2] != null) {
				msgParametri = "Forza inserimento in caso di presenza DID/MOBILITA' e stato occupazionale manuale: "
						+ parametri[2].toString();
				logBatch.writeLog(msgParametri);
			}
			String sOccAnnoBatchAnni = "";
			if (parametri[6] != null) {
				sOccAnnoBatchAnni = parametri[6].toString();
				msgParametri = "Ricalcola in mancanza di stato occupazionale nell'anno: " + sOccAnnoBatchAnni;
				logBatch.writeLog(msgParametri);
			}

			msgParametri = "";
			logBatch.writeLog(msgParametri);
			logBatch.writeLog("=======================================================================");
			msgParametri = "";
			logBatch.writeLog(msgParametri);

			RequestContainer requestContainer = new RequestContainer();
			SessionContainer sessionContainer = new SessionContainer(true);
			sessionContainer.setAttribute("_CDUT_", new BigDecimal(parametri[3]));
			sessionContainer.setAttribute("_ENCRYPTER_KEY_", System.getProperty("_ENCRYPTER_KEY_"));
			requestContainer.setSessionContainer(sessionContainer);
			SourceBean s = new SourceBean("SERVICE_REQUEST");
			s.setAttribute("FORZA_INSERIMENTO", parametri[2]);
			s.setAttribute("CONTINUA_CALCOLO_SOCC", parametri[2]);
			requestContainer.setServiceRequest(s);
			RequestContainer.setRequestContainer(requestContainer);

			// Stampa delle variabili java
			logBatch.writeLogVarJava(DateUtils.getNow());
			logBatch.writeLogVarJava("====== Variabili di configurazione java ======");
			Properties p = java.lang.System.getProperties();
			Enumeration<?> e = p.propertyNames();
			while (e.hasMoreElements()) {
				String key = (String) e.nextElement();
				String val = p.getProperty(key);
				logBatch.writeLogVarJava(key + ": " + val + ";");
			}
			logBatch.writeLogVarJava("====== Fine sezione di configurazione ======");

			// devo crearmi l'oggetto User per vedere se l'utente ha i diritti
			// String cdnUser = parametri[3].toString();
			// String cdnProfilo = parametri[4].toString();
			String cdnGruppo = parametri[5].toString();
			// leggo dalla ts_generale se fare o meno la commit del batch
			SourceBean sbGenerale = DBLoad.getInfoGenerali();
			String flagCommitBatch = "S";

			if (sbGenerale != null && sbGenerale.containsAttribute("FLGCOMMITBATCH")) {
				flagCommitBatch = sbGenerale.getAttribute("FLGCOMMITBATCH").toString();
			}

			StatoOccupazionaleBean statoOcc = null;
			SourceBean row = null;
			String msg = "";
			Object cdnlavoratoreCurr = null;
			Object cdnlavoratorePrec = null;

			// leggi i movimenti terminati
			Vector rows = null;
			BigDecimal prgMovimento = null;
			SourceBean movGestito = null;
			// lettura movimenti a cavallo di anni
			rows = DBLoad.getMovimentiTraAnniNew(dataBatch, cdnGruppo);

			logBatch.writeLog("Sono stati letti i movimenti a cavallo della data: " + dataBatch);
			logBatch.writeLog("Numero di movimenti letti: " + rows.size());
			// per ognuno di questi
			logBatch.writeLog("Avvio batch sui movimenti letti.");
			TransactionQueryExecutor txExec = null;
			String flag = (String) ConfigSingleton.getInstance().getAttribute("INTERRUTTORI.MO.attivo");
			boolean interruttoreMobilita = (flag.equalsIgnoreCase("true"));
			Sottosistema.setMO(interruttoreMobilita);
			for (int i = 0; i < rows.size(); i++) {
				try {
					txExec = new TransactionQueryExecutor(Values.DB_SIL_DATI);
					txExec.initTransaction();
					msg = "";
					row = (SourceBean) rows.get(i);
					cdnlavoratoreCurr = row.getAttribute("CDNLAVORATORE");

					if (cdnlavoratorePrec == null || !cdnlavoratoreCurr.equals(cdnlavoratorePrec)) {
						logBatch.writeLog(msg);
						msg = "Lavoratore con identificativo " + cdnlavoratoreCurr + " : Cognome "
								+ row.getAttribute("STRCOGNOME") + " Nome " + row.getAttribute("STRNOME");
						msg = msg + " Codice fiscale : " + row.getAttribute("STRCODICEFISCALE") + " Data di nascita : "
								+ row.getAttribute("DATNASC");
						logBatch.writeLog(msg);
						msg = "";
					}

					cdnlavoratorePrec = cdnlavoratoreCurr;
					prgMovimento = (BigDecimal) row.getAttribute(MovimentoBean.DB_PRG_MOVIMENTO);

					msg = "Si sta elaborando il movimento con prg:" + prgMovimento;
					msg = msg + ";data inizio: " + row.getAttribute("DATINIZIOMOV") + ";data fine: "
							+ (row.containsAttribute("DATFINEMOVEFFETTIVA")
									? (String) row.getAttribute("DATFINEMOVEFFETTIVA")
									: "");
					msg = msg + ";tipo movimento: " + row.getAttribute("CODTIPOMOV");
					String codTipoAss = row.containsAttribute("CODTIPOASS") ? row.getAttribute("CODTIPOASS").toString()
							: "";
					msg = msg + ";tipo ass: " + codTipoAss + ";tempo: " + row.getAttribute("CODMONOTEMPO");
					msg = msg + ";Azienda: " + row.getAttribute("STRRAGIONESOCIALE") + ";Indirizzo: "
							+ row.getAttribute("STRINDIRIZZO");
					logBatch.writeLog(msg);

					// prendo lo stato occupazionale aperto del lavoratore
					StatoOccupazionaleBean tmpSO = DBLoad.getStatoOccupazionale(cdnlavoratoreCurr);
					msg = "";
					msg = "Lo stato occupazionale del lavoratore risulta essere: " + tmpSO.getDescrizioneCompleta();
					logBatch.writeLog(msg);
					msg = "";
					// Si controlla se il mov sia stato già trattato dal batch
					if (row.getAttribute("CODMONOBATCH") != null && row.getAttribute("CODMONOBATCH").equals("T")
							&& parametri[1].equals("nonTrattati")) {
						txExec.rollBackTransaction();
						txExec = null;
						msg = "Il movimento prg:" + row.getAttribute(MovimentoBean.DB_PRG_MOVIMENTO)
								+ " è già terminato e trattato";
						logBatch.writeLog(msg);
						// il movimento e' gia' terminato e trattato
						continue;
					}

					statoOcc = SituazioneAmministrativaFactory
							.newInstance(cdnlavoratoreCurr.toString(), dataBatch, txExec, logBatch, requestContainer)
							.calcolaImpatti();
					msg = "Il nuovo stato occupazionale è: " + statoOcc.getDescrizioneCompleta();
					logBatch.writeLog(msg);
					movGestito = DBLoad.getMovimento(prgMovimento, txExec);
					if (movGestito.containsAttribute("CODMONOBATCH"))
						movGestito.updAttribute("CODMONOBATCH", "T");
					else
						movGestito.setAttribute("CODMONOBATCH", "T");
					DBStore.aggiornaMovimentoPerBatch(movGestito, statoOcc, requestContainer, txExec);

					if (flagCommitBatch.equals("S")) {
						txExec.commitTransaction();
						msg = "";
						msg = "Eseguita commit: le modifiche sono state salvate nel DB";
						logBatch.writeLog(msg);
					} else {
						txExec.rollBackTransaction();
						msg = "";
						msg = "Eseguito rollback: le modifiche non sono state salvate nel DB";
						logBatch.writeLog(msg);
					}
					txExec = null;
				} catch (Exception ee) {
					if (txExec != null) {
						txExec.rollBackTransaction();
						txExec = null;
					}
					logBatch.writeLog("ERRORE: " + ee.getMessage());
					logBatch.writeLog("Il batch è terminato con degli errori.");
				}
			} // end for
			msg = "";
			logBatch.writeLog(msg);
			logBatch.writeLog("Il batch calcolo impatti a cavallo di due anni è terminato.");
		} catch (Exception e) {
			logBatch.writeLog("ERRORE: " + e.toString());
			logBatch.writeLog("Il batch è terminato con degli errori.");
		}
	}// end method

	/**
	 * Metodo per settare i parametri passati da linea di comando
	 */
	public void setParametri() {
		parametri = new String[7];
		parametri[0] = dataBatch; // data inizio nuovo anno (movimenti a
									// cavallo di questa data)

		String[] args = this.batchObject.getParams();
		if (args.length == 7) { // viene chiamato dalla pagina
			parametri[1] = args[1]; // non trattati - anche trattati
			parametri[2] = args[2];// forzaReinserimento
			parametri[3] = args[3];// user //Se avviati da .bat impostarlo di
									// default
			parametri[4] = args[4]; // profilo user //Se avviati da .bat
									// impostarlo di default
			parametri[5] = args[5]; // gruppo user //Se avviati da .bat
									// impostarlo di default
			parametri[6] = args[6]; // considera quelli che non hanno lo stato
									// occupazionale nell'anno
		} else { // viene schedulato
			parametri[1] = args[0]; // non trattati - anche trattati
			parametri[2] = args[1];// forzaReinserimento
			parametri[3] = args[2];// user //Se avviati da .bat impostarlo di
									// default
			parametri[4] = args[3]; // profilo user //Se avviati da .bat
									// impostarlo di default
			parametri[5] = args[4]; // gruppo user //Se avviati da .bat
									// impostarlo di default
			parametri[6] = args[5]; // considera quelli che non hanno lo stato
									// occupazionale nell'anno
		}
	}
}
