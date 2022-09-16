/*
 * Creato il 1-ago-05
 *
 * Per modificare il modello associato a questo file generato, aprire
 * Finestra&gt;Preferenze&gt;Java&gt;Generazione codice&gt;Codice e commenti
 */
package it.eng.sil.util.batch;

import java.io.File;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Vector;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.SessionContainer;
import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.base.SourceBeanException;
import com.engiweb.framework.configuration.ConfigSingleton;
import com.engiweb.framework.util.QueryExecutor;

import it.eng.afExt.utils.DateUtils;
import it.eng.afExt.utils.StringUtils;
import it.eng.sil.Values;
import it.eng.sil.module.movimenti.BackGroundValidator;
import it.eng.sil.module.movimenti.HtmlResultLogger;
import it.eng.sil.module.movimenti.MultipleResultLogger;
import it.eng.sil.module.movimenti.ResultLogFormatter;
import it.eng.sil.module.movimenti.ResultLogger;
import it.eng.sil.module.movimenti.Validator;
import it.eng.sil.module.movimenti.ValidazioneMovimentiBean;
import it.eng.sil.security.User;
import it.eng.sil.util.Sottosistema;
import it.eng.sil.util.amministrazione.impatti.LogBatch;
import it.eng.sil.util.amministrazione.impatti.UserBatch;
import it.eng.sil.util.batch.mdb.BatchObject;
import it.eng.sil.util.batch.mdb.IBatchMDBConsumer;

/**
 * @author D'Auria, Togna
 * 
 */
public class BatchValidaMovimenti implements IBatchMDBConsumer {
	private BatchObject batchObject;
	private String[] parametri;
	private String dataBatch = "";
	private LogBatch logBatch = null;
	private int numeroMovimentiDaValidare = 0;
	private int nGiorniPrimaValidazione = 0;
	private String orarioFineBatch = "";
	private int oraFineBatch = 0;
	private int minFineBatch = 0;

	public BatchValidaMovimenti(BatchObject batchObject) throws Exception {
		this.batchObject = batchObject;
		String dir = ConfigSingleton.getLogBatchPath();
		String nomeFile = File.separator + "BatchValidaMovimenti";
		String data = "";
		data = DateUtils.getNow();
		dataBatch = data;
		data = data.replace('/', '-');
		nomeFile = nomeFile + data + ".log";
		logBatch = new LogBatch(nomeFile, dir);
	}

	@Override
	public void execBatch() {
		try {
			this.setParametri();

			if (this.checkOrarioValido()) {
				this.start();
			} else {
				this.setLog(
						"=========== Validazione interrotta per superamento dell'orario massimo consentito ===========");
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public boolean start() {
		try {
			logBatch.writeLog("=========== Avvio Batch per validare tutti i movimenti  ===========");
			RequestContainer requestContainer = new RequestContainer();
			SessionContainer sessionContainer = new SessionContainer(true);
			sessionContainer.setAttribute("_CDUT_", new BigDecimal(parametri[1]));
			sessionContainer.setAttribute("_ENCRYPTER_KEY_", System.getProperty("_ENCRYPTER_KEY_"));
			User user = new UserBatch().getUser(parametri[1], parametri[2], parametri[3]);
			sessionContainer.setAttribute(User.USERID, user);
			requestContainer.setSessionContainer(sessionContainer);
			SourceBean s = new SourceBean("SERVICE_REQUEST");
			createRequest(s);
			requestContainer.setServiceRequest(s);
			RequestContainer.setRequestContainer(requestContainer);
			ArrayList prgMovAppArray = null;
			SessionContainer session = RequestContainer.getRequestContainer().getSessionContainer();
			String flag = (String) ConfigSingleton.getInstance().getAttribute("INTERRUTTORI.MO.attivo");
			boolean interruttoreMobilita = (flag.equalsIgnoreCase("true"));
			Sottosistema.setMO(interruttoreMobilita);

			// dichiarazioni variabili utilizzate nel ciclo di validazione
			boolean batchTerminato = false;
			int numCicloBatch = 1;
			int numeroMaxMovNonValidati = 0;
			int numeroMaxMovGiaValidati = 0;
			int numeroMovimentiEstrattiNonValidati = 0;
			int numeroMovimentiEstrattiGiaValidati = 0;
			Object result = null;
			String strCpi = null;
			int sizeVettNonValidati = 0;
			int sizeVettGiaValidati = 0;
			Vector vettNonValidati = null;
			Vector vettGiaValidati = null;

			// Partizione 70% non validati e 30% già validati.
			if (nGiorniPrimaValidazione > 0) {
				if (numeroMovimentiDaValidare > 0) {
					numeroMaxMovNonValidati = (((numeroMovimentiDaValidare * 70) / 100));
					numeroMaxMovGiaValidati = numeroMovimentiDaValidare - numeroMaxMovNonValidati;
				}
			} else {
				// nGiorniPrimaValidazione = 0 allora considero solo i movimenti mai validati
				if (numeroMovimentiDaValidare > 0) {
					numeroMaxMovNonValidati = numeroMovimentiDaValidare;
				}
			}
			strCpi = parametri[4];
			if (strCpi.equalsIgnoreCase("null")) {
				if (nGiorniPrimaValidazione > 0) {
					// Estraggo l'intera lista dei prgMovimentoApp dalla tabella am_movimento_appoggio che hanno come
					// DATA DI PRIMA VALIDAZIONE una che non sia più vecchia di N giorni fa
					boolean movimentiGiaValidati = true;
					try {
						Object[] params = new Object[1];
						params[0] = Integer.valueOf(nGiorniPrimaValidazione);
						result = QueryExecutor.executeQuery("GET_PROGRESSIVI_MOVIMENTI_APPOGGIO_VALIDATI_FINO_N_GIORNI",
								params, "SELECT", Values.DB_SIL_DATI);
						if (result == null) {
							movimentiGiaValidati = false;
							logBatch.writeLog("=========== Non ci sono movimenti validati fino a "
									+ nGiorniPrimaValidazione + " giorni ===========");
							nGiorniPrimaValidazione = 0;
							if (numeroMovimentiDaValidare > 0) {
								numeroMaxMovNonValidati = numeroMovimentiDaValidare;
							}
						}
					} catch (Throwable e) {
						logBatch.writeLog(
								"=========== Errore nel recupero dei progressivi dei movimenti validati fino a "
										+ nGiorniPrimaValidazione + " giorni. \n " + e.getMessage() + " ===========");
						return false;
					}
					// Esamino il risultato
					if (movimentiGiaValidati) {
						if (result instanceof SourceBean) {
							// Estraggo le righe estratte dalla query
							vettGiaValidati = ((SourceBean) result).getAttributeAsVector("ROW");
							sizeVettGiaValidati = vettGiaValidati.size();
						} else if (result instanceof Throwable) {
							logBatch.writeLog(
									"=========== Errore nel recupero dei progressivi dei movimenti validati fino a "
											+ nGiorniPrimaValidazione + " giorni. " + ((Throwable) result).getMessage()
											+ " ===========");
							return false;
						} else {
							logBatch.writeLog("===========" + result.toString() + "===========");
							logBatch.writeLog(
									"=========== Errore nel recupero dei progressivi dei movimenti validati fino a "
											+ nGiorniPrimaValidazione + " giorni: result non definito ===========");
							return false;
						}
					}
				}
			}

			// Commento Giovanni Landi 19/11/2008
			// Nel primo ciclo vengono validati un numero max di movimenti = numeroMovimentiDaValidare, suddivisi
			// in questo modo: 70% di movimenti mai validati e 30% di movimenti già validati.
			// Negli eventuali cicli successivi vengono validati un numero max di movimenti = numeroMovimentiDaValidare
			// prendendo solo i movimenti mai validati. La validazione si interrompe o quando non ci sono
			// più movimenti mai validati o quando si raggiunge l'orario massimo di esecuzione della validazione
			while (!batchTerminato) {
				prgMovAppArray = new ArrayList();
				try {
					if (!strCpi.equalsIgnoreCase("null")) {
						logBatch.writeLog("=========== Filtro sul CPI con codice: " + strCpi + " ===========");
						try {
							Object[] params = new Object[1];
							params[0] = strCpi;
							result = QueryExecutor.executeQuery("SELECT_MOVIMENTI_APPOGGIO_FILTRO_CPI", params,
									"SELECT", Values.DB_SIL_DATI);
							if (result == null) {
								logBatch.writeLog(
										"=========== Errore nel recupero dei progressivi dei movimenti da validare.\n"
												+ " Filtro sul CPI con codice: " + strCpi
												+ " ha restituito un risultato null ===========");
								return false;
							}
						} catch (Throwable e) {
							logBatch.writeLog(
									"=========== Errore nel recupero dei progressivi dei movimenti da validare: Filtro sul CPI con codice: "
											+ strCpi + ". \n " + e.getMessage() + " ===========");
							return false;
						}
						// Esamino il risultato
						if (result instanceof SourceBean) {
							//Estraggo i progressivi ritrovati
							Vector v1 = ((SourceBean) result).getAttributeAsVector("ROW");
							logBatch.writeLog("Sono stati estratti " + v1.size() + " movimenti ");

							for (int i = 0; i < v1.size(); i++) {
								prgMovAppArray.add(((SourceBean) v1.get(i)).getAttribute("PRGMOVAPP"));
							}
						} else if (result instanceof Throwable) {
							logBatch.writeLog(
									"=========== Errore nel recupero dei progressivi dei movimenti da validare. "
											+ ((Throwable) result).getMessage() + " ===========");
							return false;
						} else {
							logBatch.writeLog("===========" + result.toString() + "===========");
							logBatch.writeLog(
									"=========== Errore nel recupero dei progressivi dei movimenti da validare.\n"
											+ " Filtro sul CPI con codice: " + strCpi
											+ " ha restituito un risultato non definito ===========");
							return false;
						}
					} else {
						logBatch.writeLog("=========== Nessun filtro sul CPI  ===========");
						// Primo ciclo considero anche i movimenti che hanno come
						// DATA DI PRIMA VALIDAZIONE una che non sia più vecchia di N giorni fa
						if (numCicloBatch == 1 && nGiorniPrimaValidazione > 0) {
							if (numeroMovimentiDaValidare > 0) {
								if (sizeVettGiaValidati > numeroMaxMovGiaValidati) {
									numeroMovimentiEstrattiGiaValidati = numeroMaxMovGiaValidati;
								} else {
									numeroMovimentiEstrattiGiaValidati = sizeVettGiaValidati;
									// il numero di movimenti già validati è minore del numero max movimenti già
									// validati
									// da validare al primo ciclo ---> si aggiunge la differenza al numero max di
									// movimenti
									// non validati da validare al primo ciclo
									numeroMaxMovNonValidati = numeroMaxMovNonValidati
											+ (numeroMaxMovGiaValidati - sizeVettGiaValidati);
								}
							} else {
								numeroMovimentiEstrattiGiaValidati = sizeVettGiaValidati;
							}

							for (int i = numeroMovimentiEstrattiGiaValidati - 1; i >= 0; i--) {
								SourceBean mov = (SourceBean) vettGiaValidati.get(i);
								prgMovAppArray.add(mov.getAttribute("PrgMovimentoApp"));
							}
							logBatch.writeLog("Sono stati estratti " + (numeroMovimentiEstrattiGiaValidati)
									+ " movimenti già validati");
						}

						// Dal secondo ciclo in avanti prendo solo quelli non validati
						if (numCicloBatch > 1) {
							if (numeroMovimentiDaValidare > 0) {
								numeroMaxMovNonValidati = numeroMovimentiDaValidare;
							}
						}
						// Estraggo la lista dei prgMovimentoApp dalla tabella am_movimento_appoggio
						// che non sono stati mai validati
						try {
							result = QueryExecutor.executeQuery("GET_PROGRESSIVI_MOVIMENTI_APPOGGIO", null, "SELECT",
									Values.DB_SIL_DATI);
							if (result == null) {
								logBatch.writeLog(
										"=========== Errore nel recupero dei progressivi dei movimenti da validare.\n"
												+ " Nessun filtro sul CPI ha restituito un risultato null ===========");
								return false;
							}
						} catch (Throwable e) {
							logBatch.writeLog(
									"=========== Errore nel recupero dei progressivi dei movimenti da validare: nessun filtro sul CPI. \n "
											+ e.getMessage() + " ===========");
							return false;
						}
						// Esamino il risultato
						if (result instanceof SourceBean) {
							// Estraggo le righe estratte dalla query
							vettNonValidati = ((SourceBean) result).getAttributeAsVector("ROW");
							sizeVettNonValidati = vettNonValidati.size();
						} else if (result instanceof Throwable) {
							logBatch.writeLog(
									"=========== Errore nel recupero dei progressivi dei movimenti da validare. "
											+ ((Throwable) result).getMessage() + " ===========");
							return false;
						} else {
							logBatch.writeLog("===========" + result.toString() + "===========");
							logBatch.writeLog(
									"=========== Errore nel recupero dei progressivi dei movimenti da validare.\n"
											+ " Nessun filtro sul CPI ha restituito un risultato non definito ===========");
							return false;
						}
						// Considero i movimenti che non sono stati mai validati
						if (numeroMovimentiDaValidare > 0) {
							if (sizeVettNonValidati > numeroMaxMovNonValidati) {
								numeroMovimentiEstrattiNonValidati = numeroMaxMovNonValidati;
							} else {
								numeroMovimentiEstrattiNonValidati = sizeVettNonValidati;
								batchTerminato = true;
							}
						} else {
							numeroMovimentiEstrattiNonValidati = sizeVettNonValidati;
							batchTerminato = true;
						}
						for (int i = numeroMovimentiEstrattiNonValidati - 1; i >= 0; i--) {
							SourceBean mov = (SourceBean) vettNonValidati.get(i);
							prgMovAppArray.add(mov.getAttribute("PrgMovimentoApp"));
						}
						logBatch.writeLog("Sono stati estratti " + numeroMovimentiEstrattiNonValidati
								+ " movimenti non validati");
					} // nessun filtro su cpi
				}

				catch (Throwable e2) {
					logBatch.writeLog("=========== \n" + e2.getMessage() + "  ===========");
					return false;
				}


				// Oggetto per validazione dei movimenti
				Validator validator = new Validator();
				validator.setLogBatch(logBatch);
				validator.setOrarioFineBatch(getOrarioFineBatch());

				// creo l'oggetto che esegue il log dei risultati
				ResultLogger resultLogger = null;
				BigDecimal prgValidazioneMassiva = null;
				try {
					resultLogger = new HtmlResultLogger();
					MultipleResultLogger dbLogger = new MultipleResultLogger(prgMovAppArray.size(), session);
					// può generare LogException
					prgValidazioneMassiva = dbLogger.getPrgValidazioneMassiva();

					resultLogger.addChildResultLogger(dbLogger); // (x)->Html->DB

				} catch (Throwable e) {
					// Segnalo l'impossibilità di scrivere il log e ritorno
					logBatch.writeLog("=========== Impossibile inizalizzare il logger per registrare i risultati "
							+ e.getMessage() + " ===========");
					return false;
				}

				// Creo l'oggetto per il recupero dei risultati
				ResultLogFormatter risultatiCorrenti = null;
				try {
					risultatiCorrenti = new ResultLogFormatter(prgValidazioneMassiva);
				} catch (Throwable e) {
					// Segnalo l'impossibilità di istanziare l'oggetto per il
					// recupero del log
					logBatch.writeLog("=========== Impossibile inizalizzare il logger per il recupero dei risultati "
							+ ((Exception) e).getMessage() + "===========");
					return false;
				}

				BackGroundValidator t = new BackGroundValidator(RequestContainer.getRequestContainer(), validator,
						resultLogger, prgMovAppArray);

				// registro in sessione gli oggetti che effettuano la validazione
				session.setAttribute("VALIDATOREMASSIVOCORRENTE", t);
				session.setAttribute("RISULTATICORRENTI", risultatiCorrenti);
				session.setAttribute("VALIDATORCORRENTE", validator);

				// Avvio la validazione massiva
				logBatch.writeLog("=========== Validazione avviata ===========");
				t.run();
				logBatch.writeLog("=========== Validazione terminata ===========");

				// controllo interrompere il batch se la validazione è stata lanciata con il filtro su cpi oppure
				// è stato superato il tempo max di esecuzione
				if (!strCpi.equalsIgnoreCase("null")) {
					batchTerminato = true;
				} else {
					// controllo sul tempo solo se il batch non deve essere già stoppato perché non
					// ci sono più movimenti da validare
					if (!batchTerminato) {
						// Controllo sul tempo max di esecuzione
						batchTerminato = checkStopValidazioneOrario();
						if (batchTerminato) {
							logBatch.writeLog(
									"=========== Validazione terminata per superamento dell'orario massimo consentito ===========");
						}
						numCicloBatch = numCicloBatch + 1;
					}
				}
			} // end while
			return true;

		} catch (SourceBeanException e1) {
			logBatch.writeLog("=========== EXCEPTION:  \n" + e1.getMessage() + "===========");
			return false;
		} catch (Throwable e2) {
			logBatch.writeLog("=========== \n" + e2.getMessage() + "  ===========");
			return false;
		}
	}

	/**
	 * @param request
	 */
	private void createRequest(SourceBean request) throws SourceBeanException {
		SourceBean result = (SourceBean) QueryExecutor.executeQuery("GET_PROTOCOLLAZIONE", null, "SELECT",
				Values.DB_SIL_DATI);
		SourceBean row = (SourceBean) result.getAttribute("ROW");
		request.setAttribute("numProt", row.getAttribute("NUMPROTOCOLLO").toString());
		request.setAttribute("numAnnoProt", row.getAttribute("NUMANNOPROT").toString());
		request.setAttribute("dataProt", (new SimpleDateFormat("dd/MM/yyyy").format(new Date())).toString());
		request.setAttribute("oraProt", (new SimpleDateFormat("HH:mm").format(new Date())).toString());
		request.setAttribute("tipoProt", (row.getAttribute("FLGPROTOCOLLOAUT")).toString());
		request.setAttribute("CONTEXT", "validazioneMassiva");
	}

	public void setParametri() {
		String[] args = this.batchObject.getParams();

		if (logBatch == null) {
			String dir = ConfigSingleton.getLogBatchPath();
			String nomeFile = File.separator + "BatchValidaMovimenti";
			String data = "";
			data = DateUtils.getNow();
			dataBatch = data;
			data = data.replace('/', '-');
			nomeFile = nomeFile + data + ".log";
			logBatch = new LogBatch(nomeFile, dir);
		}
		parametri = new String[5];
		parametri[0] = dataBatch; // data fine range (usata)
		parametri[1] = args[0]; // user //Se avviati da .bat impostarlo di default
		parametri[2] = args[1]; // profilo user //Se avviati da .bat impostarlo di default
		parametri[3] = args[2]; // gruppo user //Se avviati da .bat impostarlo di default
		parametri[4] = args[3]; // cpi di competenza //Se schedulato prendo il cpi passato
		// args[4] se presente contiene il numero di movimenti da validare
		// args[5] se presente contiene il numero massimo di giorni dalla prima validazione, oltre cui il
		// movimento non va più considerato
		// args[6] orario di interruzione del batch
		int nArgs = args.length;
		if (nArgs >= 5) {
			numeroMovimentiDaValidare = Integer.valueOf(args[4]).intValue();
		}
		if (nArgs >= 6) {
			nGiorniPrimaValidazione = Integer.valueOf(args[5]).intValue();
		}
		if (nArgs == 7) {
			setOrarioFineBatch(args[6]);
			if (!getOrarioFineBatch().equals("")) {
				Vector risOrario = StringUtils.split(getOrarioFineBatch(), ":");
				setOraFineBatch(Integer.valueOf(risOrario.get(0).toString()).intValue());
				setMinFineBatch(Integer.valueOf(risOrario.get(1).toString()).intValue());
			}
		}
	}

	public boolean checkStopValidazioneOrario() {
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

	public boolean checkOrarioValido() {
		if (!getOrarioFineBatch().equals("")) {
			Date dataCurr = new Date();
			Calendar cal = Calendar.getInstance();
			cal.setTime(dataCurr);
			int oraCurr = cal.get(Calendar.HOUR_OF_DAY);
			int minCurr = cal.get(Calendar.MINUTE);
			if ((oraCurr > getOraFineBatch()) || (oraCurr == getOraFineBatch() && minCurr >= getMinFineBatch())) {
				return false;
			} else {
				return true;
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

	public int getOraFineBatch() {
		return this.oraFineBatch;
	}

	public int getMinFineBatch() {
		return this.minFineBatch;
	}

	public void setLog(String message) {
		logBatch.writeLog(message);
	}

}
