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
import it.eng.sil.security.TransactionProfileDataFilter;
import it.eng.sil.security.User;
import it.eng.sil.util.Sottosistema;
import it.eng.sil.util.batch.BatchRunnable;
import it.eng.sil.util.batch.mdb.BatchObject;
import it.eng.sil.util.batch.mdb.IBatchMDBConsumer;

/**
 * Classe per il batch dei movimenti in data futura. Vengono processati tutti i movimenti con data inizio Movimento
 * uguale alla data passata come argomento.
 */
public class MovimentiGiornalieri implements BatchRunnable, IBatchMDBConsumer {
	private BatchObject batchObject;
	private String[] parametri;
	private LogBatch logBatch;
	private String dataBatch = "";

	/**
	 * Costruttore
	 */
	public MovimentiGiornalieri(BatchObject batchObject) throws Exception {
		this.batchObject = batchObject;
		String dir = ConfigSingleton.getLogBatchPath();
		String nomeFile = File.separator + "BatchMovFuturi";
		String data = "";
		if (this.batchObject.getParams().length == 6) { // viene chiamato dalla pagina
			data = this.batchObject.getParams()[0];
		} else {
			data = DateUtils.getNow(); // viene schedulato
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

	public void start() {
		try {
			logBatch.writeLog("=========== Avvio batch per i movimenti in data futura ===========");

			String msgParametri = "";
			logBatch.writeLog(msgParametri);
			logBatch.writeLog("========================== PARAMETRI BATCH ============================");
			msgParametri = "";
			logBatch.writeLog(msgParametri);
			if (parametri[0] != null) {
				msgParametri = "Data lancio batch: " + parametri[0].toString();
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

			// devo crearmi l'oggetto User
			String cdnUser = parametri[3].toString();
			String cdnProfilo = parametri[4].toString();
			String cdnGruppo = parametri[5].toString();
			UserBatch userBatch = new UserBatch();
			User user = userBatch.getUser(cdnUser, cdnProfilo, cdnGruppo);

			// leggo dalla ts_generale se fare o meno la commit del batch
			SourceBean sbGenerale = DBLoad.getInfoGenerali();
			String flagCommitBatch = "S";
			if (sbGenerale != null && sbGenerale.containsAttribute("FLGCOMMITBATCH")) {
				flagCommitBatch = sbGenerale.getAttribute("FLGCOMMITBATCH").toString();
			}

			StatoOccupazionaleBean statoOcc = null;
			SourceBean row = null;
			String msg = "";
			BigDecimal prgMovimento = null;
			SourceBean movGestito = null;
			Object cdnlavoratoreCurr = null;
			Object cdnlavoratorePrec = null;

			// leggi i movimenti terminati
			String dataInizio = "";
			dataInizio = parametri[0];

			Vector rows = DBLoad.getMovimentiFuturi(dataInizio);
			msg = "Sono stati letti i movimenti con validità " + dataInizio;
			logBatch.writeLog(msg);
			msg = "Numero di movimenti letti " + rows.size();
			logBatch.writeLog(msg);

			TransactionQueryExecutor txExec = null;

			// per ognuno di questi
			logBatch.writeLog("Avvio batch");

			// filter consente di verificare se si hanno i diritti sul
			// lavoratore
			// per far scattare gli impatti
			String _pageDaValutare = "";
			_pageDaValutare = "MovDettaglioAvviamentoInserisciPage";
			TransactionProfileDataFilter filter = null;
			boolean canEditLav = false;
			String flag = (String) ConfigSingleton.getInstance().getAttribute("INTERRUTTORI.MO.attivo");
			boolean interruttoreMobilita = (flag.equalsIgnoreCase("true"));
			Sottosistema.setMO(interruttoreMobilita);
			for (int i = 0; i < rows.size(); i++) {
				try {
					txExec = new TransactionQueryExecutor(Values.DB_SIL_DATI);
					txExec.initTransaction();
					filter = new TransactionProfileDataFilter(user, _pageDaValutare, txExec.getDataConnection());
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
						filter.setCdnLavoratore((BigDecimal) cdnlavoratoreCurr);
						canEditLav = filter.canEditLavoratore();
					}
					cdnlavoratorePrec = cdnlavoratoreCurr;
					prgMovimento = (BigDecimal) row.getAttribute(MovimentoBean.DB_PRG_MOVIMENTO);

					msg = "Si sta elaborando il movimento con prg:" + prgMovimento;
					msg = msg + ";data inizio: " + row.getAttribute("DATINIZIOMOV") + ";data fine: "
							+ (row.containsAttribute("DATFINEMOV") ? (String) row.getAttribute("DATFINEMOV") : "");
					msg = msg + ";tipo movimento: " + row.getAttribute("CODTIPOMOV");
					String codTipoAss = row.containsAttribute("CODTIPOASS") ? row.getAttribute("CODTIPOASS").toString()
							: "";
					msg = msg + ";tipo ass: " + codTipoAss + ";tempo: " + row.getAttribute("CODMONOTEMPO");
					msg = msg + ";Azienda: " + row.getAttribute("STRRAGIONESOCIALE") + ";Indirizzo: "
							+ row.getAttribute("STRINDIRIZZO");
					logBatch.writeLog(msg);

					if (!canEditLav) {
						txExec.rollBackTransaction();
						txExec = null;
						filter = null;
						msg = "";
						msg = "Il movimento non viene trattato dal batch in quanto l'utente non ha i diritti sul lavoratore";
						logBatch.writeLog(msg);
					} else {
						// prendo lo stato occupazionale aperto del lavoratore
						StatoOccupazionaleBean tmpSO = DBLoad.getStatoOccupazionale(cdnlavoratoreCurr);
						msg = "";
						msg = msg + "Lo stato occupazionale del lavoratore risulta essere: "
								+ tmpSO.getDescrizioneCompleta();
						logBatch.writeLog(msg);
						msg = "";
						// se il movimento è gia stato trattato dal batch e sono
						// interessato solo ai movimenti non trattati allora lo
						// salto.
						if ((row.getAttribute("CODMONOBATCH") != null) && row.getAttribute("CODMONOBATCH").equals("F")
								&& parametri[1].equals("nonTrattati")) {
							txExec.rollBackTransaction();
							txExec = null;
							filter = null;
							msg = "Il movimento prg:" + row.getAttribute(MovimentoBean.DB_PRG_MOVIMENTO)
									+ " è già terminato e trattato";
							logBatch.writeLog(msg);
							continue;
						}

						statoOcc = StatoOccupazionaleManager.aggiornaStatoPerBatch(row, requestContainer, null, txExec,
								logBatch);
						// leggo lo stato occupazioanale aperto (bisognerà
						// gestire diversamente il batch (problemi
						// caricamento dei movimenti nel vettore di gestione
						// della situazione amministrativa del
						// lavoratore: VENGONO CARICATI DUE VOLTE SE PRESI IN
						// CONSIDERAZIONE DAL BATCH.))

						StatoOccupazionaleBean statoOccCorrente = DBLoad.getStatoOccupazionale(cdnlavoratoreCurr,
								txExec);
						msg = "Il nuovo stato occupazionale è: " + statoOccCorrente.getDescrizioneCompleta();
						logBatch.writeLog(msg);
						movGestito = DBLoad.getMovimento(prgMovimento, txExec);
						if (movGestito.containsAttribute("CODMONOBATCH"))
							movGestito.updAttribute("CODMONOBATCH", "F");
						else
							movGestito.setAttribute("CODMONOBATCH", "F");

						DBStore.aggiornaMovimentoPerBatch(movGestito, statoOcc, requestContainer, txExec);

						if (flagCommitBatch.equals("S")) {
							txExec.commitTransaction();
							msg = "";
							msg = "Eseguita commit: le modifiche sono state salvate nel DB";
							logBatch.writeLog(msg);
						}

						else {
							txExec.rollBackTransaction();
							msg = "";
							msg = "Eseguito rollback: le modifiche non sono state salvate nel DB";
							logBatch.writeLog(msg);
						}
						txExec = null;
						filter = null;
					}

				} catch (Exception ee) {
					if (txExec != null) {
						txExec.rollBackTransaction();
						txExec = null;
					}
					if (filter != null) {
						filter = null;
					}
					logBatch.writeLog("ERRORE: " + ee.getMessage());
					logBatch.writeLog("L'elaborazione del movimento è terminata con degli errori.");
				}
			} // end for
			msg = "";
			logBatch.writeLog(msg);
			logBatch.writeLog("Il batch MovimentiGiornalieri è terminato.");
		} catch (Exception e) {
			logBatch.writeLog("ERRORE: " + e.toString());
			logBatch.writeLog("Il batch è terminato con degli errori.");
		}
	}// end method

	/**
	 * Metodo per settare i parametri passati da linea di comando
	 */
	public void setParametri() {
		parametri = new String[6];
		parametri[0] = dataBatch; // data fine range (usata)

		String[] args = this.batchObject.getParams();
		if (args.length == 6) { // viene chiamato dalla pagina
			parametri[1] = args[1]; // non trattati - anche trattati
			parametri[2] = args[2];// forzaReinserimento
			parametri[3] = args[3];// user //Se avviati da .bat impostarlo di
									// default
			parametri[4] = args[4]; // profilo user //Se avviati da .bat
									// impostarlo di default
			parametri[5] = args[5]; // gruppo user //Se avviati da .bat
									// impostarlo di default
		} else { // viene schedulato
			parametri[1] = args[0]; // non trattati - anche trattati
			parametri[2] = args[1];// forzaReinserimento
			parametri[3] = args[2];// user //Se avviati da .bat impostarlo di
									// default
			parametri[4] = args[3]; // profilo user //Se avviati da .bat
									// impostarlo di default
			parametri[5] = args[4]; // gruppo user //Se avviati da .bat
									// impostarlo di default
		}
	}
}