package it.eng.sil.util.batch;

import java.io.File;
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.Properties;
import java.util.Vector;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.SessionContainer;
import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.configuration.ConfigSingleton;

import it.eng.afExt.utils.DateUtils;
import it.eng.afExt.utils.StringUtils;
import it.eng.afExt.utils.TransactionQueryExecutor;
import it.eng.sil.Values;
import it.eng.sil.security.TransactionProfileDataFilter;
import it.eng.sil.security.User;
import it.eng.sil.util.Sottosistema;
import it.eng.sil.util.amministrazione.impatti.DBLoad;
import it.eng.sil.util.amministrazione.impatti.LogBatch;
import it.eng.sil.util.amministrazione.impatti.SituazioneAmministrativaFactory;
import it.eng.sil.util.amministrazione.impatti.StatoOccupazionaleBean;
import it.eng.sil.util.amministrazione.impatti.UserBatch;
import it.eng.sil.util.batch.mdb.BatchObject;
import it.eng.sil.util.batch.mdb.IBatchMDBConsumer;

public class BatchRicalcolaImpatti implements BatchRunnable, IBatchMDBConsumer {
	private BatchObject batchObject;
	private String[] parametri;
	private LogBatch logBatch;
	private String dataBatch = "";
	private String orarioFineBatch = "";
	private int oraFineBatch = 0;
	private int minFineBatch = 0;
	private int numLavoratoriDaProcessare = 0;

	public BatchRicalcolaImpatti(BatchObject batchObject) throws Exception {
		this.numLavoratoriDaProcessare = 0;
		this.batchObject = batchObject;
		String dir = ConfigSingleton.getLogBatchPath();
		String nomeFile = File.separator + "BatchRicalcolaImpatti";
		String data = DateUtils.getNow();
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
			logBatch.writeLog("=========== Avvio batch per il ricalcolo impatti ===========");

			String msgParametri = "";
			logBatch.writeLog(msgParametri);
			logBatch.writeLog("========================== PARAMETRI BATCH ============================");
			msgParametri = "";
			logBatch.writeLog(msgParametri);
			if (parametri[0] != null) {
				msgParametri = "Data lancio batch: " + parametri[0].toString();
				logBatch.writeLog(msgParametri);
			}
			msgParametri = "";
			logBatch.writeLog(msgParametri);
			logBatch.writeLog("=======================================================================");
			msgParametri = "";
			logBatch.writeLog(msgParametri);

			RequestContainer requestContainer = new RequestContainer();
			SessionContainer sessionContainer = new SessionContainer(true);
			sessionContainer.setAttribute("_CDUT_", new BigDecimal(parametri[1]));
			sessionContainer.setAttribute("_ENCRYPTER_KEY_", System.getProperty("_ENCRYPTER_KEY_"));
			requestContainer.setSessionContainer(sessionContainer);
			SourceBean s = new SourceBean("SERVICE_REQUEST");
			s.setAttribute("FORZA_INSERIMENTO", "true");
			s.setAttribute("CONTINUA_CALCOLO_SOCC", "true");
			requestContainer.setServiceRequest(s);
			RequestContainer.setRequestContainer(requestContainer);

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

			// devo crearmi l'oggetto User
			String cdnUser = parametri[1].toString();
			String cdnProfilo = parametri[2].toString();
			String cdnGruppo = parametri[3].toString();
			UserBatch userBatch = new UserBatch();
			User user = userBatch.getUser(cdnUser, cdnProfilo, cdnGruppo);

			SourceBean row = null;
			String msg = "";
			Object cdnlavoratoreCurr = null;
			String dataCalcolo = "";
			Vector rows = null;

			if (getNumLavoratoriDaProcessare() > 0) {
				rows = DBLoad.getLavoratoriRicalcolo(getNumLavoratoriDaProcessare());
			}
			else {
				rows = DBLoad.getLavoratoriRicalcolo();
			}

			msg = "Sono stati letti i lavoratori a cui sarà ricalcolo lo stato occupazionale.";
			logBatch.writeLog(msg);
			msg = "Numero di lavoratori letti " + rows.size();
			logBatch.writeLog(msg);

			TransactionQueryExecutor txExec = null;

			// per ognuno di questi
			logBatch.writeLog("Avvio batch");

			// filter consente di verificare se si hanno i diritti sul lavoratore per far scattare gli impatti
			String _pageDaValutare = "";
			_pageDaValutare = "MovDettaglioAvviamentoInserisciPage";
			TransactionProfileDataFilter filter = null;
			boolean canEditLav = false;
			String flag = (String) ConfigSingleton.getInstance().getAttribute("INTERRUTTORI.MO.attivo");
			boolean interruttoreMobilita = (flag.equalsIgnoreCase("true"));
			Sottosistema.setMO(interruttoreMobilita);
			boolean batchTerminato = false;

			for (int i = 0; (i < rows.size() && !batchTerminato); i++) {
				try {
					txExec = new TransactionQueryExecutor(Values.DB_SIL_DATI);
					txExec.initTransaction();
					filter = new TransactionProfileDataFilter(user, _pageDaValutare, txExec.getDataConnection());
					msg = "";
					row = (SourceBean) rows.get(i);
					cdnlavoratoreCurr = row.getAttribute("CDNLAVORATORE");
					dataCalcolo = row.getAttribute("DATACALCOLO").toString();
					logBatch.writeLog(msg);
					msg = "Lavoratore con identificativo : " + cdnlavoratoreCurr + " Codice fiscale : "
							+ row.getAttribute("STRCODICEFISCALE");
					logBatch.writeLog(msg);
					msg = "";
					filter.setCdnLavoratore((BigDecimal) cdnlavoratoreCurr);
					canEditLav = filter.canEditLavoratore();

					if (!canEditLav) {
						String msgDB = "Il lavoratore non viene trattato dal batch in quanto l''utente non ha i diritti sul lavoratore";
						msg = "Il lavoratore non viene trattato dal batch in quanto l'utente non ha i diritti sul lavoratore";
						String updatequery = "UPDATE AM_LAV_RICALCOLA_IMPATTI SET STRNOTE = '" + msgDB
								+ "' WHERE CDNLAVORATORE = " + cdnlavoratoreCurr;
						Object resultUpdate = txExec.executeQueryByStringStatement(updatequery, null,
								TransactionQueryExecutor.UPDATE);
						if (resultUpdate instanceof Exception) {
							throw new Exception("impossibile aggiornare la tabella dei lavoratori trattati dal batch");
						} else {
							if (((resultUpdate instanceof Boolean)
									&& (((Boolean) resultUpdate).booleanValue() == true))) {
								txExec.commitTransaction();
							} else {
								throw new Exception(
										"impossibile aggiornare la tabella dei lavoratori trattati dal batch");
							}
						}

						logBatch.writeLog(msg);
						txExec = null;
						filter = null;
					} else {
						StatoOccupazionaleBean statoOccFinale = SituazioneAmministrativaFactory
								.newInstance(cdnlavoratoreCurr.toString(), dataCalcolo, txExec).calcolaImpatti();
						msg = "";
						msg = "Calcolato stato occupazionale del lavoratore:" + statoOccFinale.getDescrizioneCompleta();
						logBatch.writeLog(msg);
						//
						String deletequery = "DELETE FROM AM_LAV_RICALCOLA_IMPATTI WHERE CDNLAVORATORE = "
								+ cdnlavoratoreCurr;
						Object result = txExec.executeQueryByStringStatement(deletequery, null,
								TransactionQueryExecutor.DELETE);
						if (result instanceof Exception) {
							throw new Exception("impossibile cancellare il lavoratore trattato dal batch");
						} else if ((result instanceof Boolean) && (((Boolean) result).booleanValue() == true)) {
							txExec.commitTransaction();
							msg = "";
							msg = "Eseguita commit: le modifiche sono state salvate nel DB";
							logBatch.writeLog(msg);
						} else {
							throw new Exception("impossibile cancellare il lavoratore trattato dal batch");
						}

						txExec = null;
						filter = null;
					}
				}

				catch (Exception ee) {
					if (txExec != null) {
						txExec.rollBackTransaction();
						txExec = null;
					}
					if (filter != null) {
						filter = null;
					}
					String messaggio = ee.getMessage();
					String messaggioDB = "";
					if (messaggio == null) {
						messaggio = "Errore generico. Controllare i movimenti del lavoratore.";
						messaggioDB = messaggio;
					} else {
						if (messaggio.length() > 1000) {
							messaggioDB = messaggio.substring(0, 1000);
						} else {
							messaggioDB = messaggio;
						}
					}
					logBatch.writeLog("ERRORE: " + messaggio);
					logBatch.writeLog("L'elaborazione del lavoratore è terminata con degli errori.");

					TransactionQueryExecutor queryExecutor = null;
					try {
						queryExecutor = new TransactionQueryExecutor(Values.DB_SIL_DATI);
						queryExecutor.initTransaction();
						String updatequery = "UPDATE AM_LAV_RICALCOLA_IMPATTI SET STRNOTE = '" + messaggioDB
								+ "' WHERE CDNLAVORATORE = " + cdnlavoratoreCurr;
						Object resultUpdate = queryExecutor.executeQueryByStringStatement(updatequery, null,
								TransactionQueryExecutor.UPDATE);
						if (resultUpdate instanceof Exception) {
							queryExecutor.rollBackTransaction();
						} else {
							if (((resultUpdate instanceof Boolean)
									&& (((Boolean) resultUpdate).booleanValue() == true))) {
								queryExecutor.commitTransaction();
							} else {
								queryExecutor.rollBackTransaction();
							}
						}
					} catch (Exception ex) {
						if (queryExecutor != null) {
							queryExecutor.rollBackTransaction();
							queryExecutor = null;
						}
					}
				}
				
				if (getNumLavoratoriDaProcessare() <= 0) {
					batchTerminato = checkStopBatchOrario();
					if (batchTerminato) {
						logBatch.writeLog(
								"=========== Batch terminato per superamento dell'orario massimo consentito ===========");
					}
				}
			} // end for

			msg = "";
			logBatch.writeLog(msg);
			logBatch.writeLog("Il batch ricalcolo impatti è terminato.");
		} catch (Exception e) {
			logBatch.writeLog("ERRORE: " + e.toString());
			logBatch.writeLog("Il batch è terminato con degli errori.");
		}
	}

	public void setParametri() {
		String[] args = this.batchObject.getParams();
		if (this.batchObject.getParams().length == 5) {
			parametri = new String[4];
			parametri[0] = dataBatch; // data batch
			parametri[1] = args[2]; // user
			parametri[2] = args[3]; // profilo
			parametri[3] = args[4]; // gruppo
			setNumLavoratoriDaProcessare(Integer.valueOf(this.batchObject.getParams()[1]).intValue());
		}
		else {
			parametri = new String[4];
			parametri[0] = dataBatch; // data batch
			parametri[1] = args[0]; // user
			parametri[2] = args[1]; // profilo
			parametri[3] = args[2]; // gruppo
			setOrarioFineBatch(args[3]);
			if (!getOrarioFineBatch().equals("")) {
				Vector risOrario = StringUtils.split(getOrarioFineBatch(), ":");
				setOraFineBatch(Integer.valueOf(risOrario.get(0).toString()).intValue());
				setMinFineBatch(Integer.valueOf(risOrario.get(1).toString()).intValue());
			}
		}
	}

	public boolean checkStopBatchOrario() {
		if (!getOrarioFineBatch().equals("")) {
			Date dataCurr = new Date();
			Calendar cal = Calendar.getInstance();
			cal.setTime(dataCurr);
			int oraCurr = cal.get(Calendar.HOUR_OF_DAY);
			int minCurr = cal.get(Calendar.MINUTE);
			if ((oraCurr > getOraFineBatch()) || (oraCurr == getOraFineBatch() && minCurr >= getMinFineBatch())) {
				return true;
			} else {
				return false;
			}
		} else {
			return true;
		}
	}

	public String getOrarioFineBatch() {
		return this.orarioFineBatch;
	}

	public void setOrarioFineBatch(String orario) {
		this.orarioFineBatch = orario;
	}

	public void setOraFineBatch(int ora) {
		this.oraFineBatch = ora;
	}

	public void setMinFineBatch(int min) {
		this.minFineBatch = min;
	}
	
	public void setNumLavoratoriDaProcessare(int nlav) {
		this.numLavoratoriDaProcessare = nlav;
	}

	public int getOraFineBatch() {
		return this.oraFineBatch;
	}

	public int getMinFineBatch() {
		return this.minFineBatch;
	}
	public int getNumLavoratoriDaProcessare() {
		return this.numLavoratoriDaProcessare;
	}
}