/*
 * Creato il 27-ott-04
 *
 * Per modificare il modello associato a questo file generato, aprire
 * Finestra&gt;Preferenze&gt;Java&gt;Generazione codice&gt;Codice e commenti
 */
package it.eng.sil.module.movimenti.processors;

import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Map;
import java.util.Vector;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.base.SourceBeanException;
import com.engiweb.framework.configuration.ConfigSingleton;
import com.engiweb.framework.error.EMFInternalError;

import it.eng.afExt.utils.MessageCodes;
import it.eng.afExt.utils.SourceBeanUtils;
import it.eng.afExt.utils.StringUtils;
import it.eng.afExt.utils.TransactionQueryExecutor;
import it.eng.sil.module.movimenti.RecordProcessor;
import it.eng.sil.module.movimenti.constant.DeTipoContrattoConstant;
import it.eng.sil.util.Utils;

/**
 * @author savino
 * 
 *         Esegue il controllo sulla presenza di movimenti simili a quello in inserimento/validazione o di movimenti
 *         doppi. Se il movimento e' doppio l'operazione va interrotta (a prenscindere dal contesto). Due movimenti sono
 *         simili se: appartengono allo stesso lavoratore, iniziano nello stesso giorno, sono dello stesso tipi (CES,
 *         AVV etc.) e sono protocollati. Se vengono trovati movimenti del genere il processore ha comportamenti diversi
 *         a seconda del contesto nel quale e' stato chiamato: 1) inserimento e validazione manuale - si chiede
 *         all'operatore se vuole proseguire, tramite una confirm JavaScript nella quale sono mostrate le informazioni
 *         sui movimenti trovati. In questo caso il controllo viene saltato. 2) validazione massiva - viene generato un
 *         warning e l'operazione prosegue.
 * 
 *         Campi chiave per la gestione del controllo: CONTEXT (il contesto in cui viene eseguito il controllo) e
 *         CONFERMA_CONTROLLO_MOV_SIMILI (se true l'operatore ha deciso di inserire/validare il movimento anche se
 *         simile ad un altro presente su db). Nella jsp oltre alla gestione del campo di conferma bisogna includere la
 *         funzione js per la confirm.
 */
public class ControlloMovimentoSimile implements RecordProcessor {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger
			.getLogger(ControlloMovimentoSimile.class.getName());
	private String name;
	private String className = this.getClass().getName();
	private TransactionQueryExecutor trans;
	private static final String MSG_WARNING = "Esiste almeno un movimento protocollato con stessa data inizio e dello stesso tipo per il lavoratore";
	private static final String pageCOMPONENTE = "MovDettaglioGeneraleConsultaPage";
	private SourceBean infoGenerale = null;
	// vettore delle warnings da restituire
	ArrayList warnings = new ArrayList();
	// Vettore dei risultati annidati da restituire
	ArrayList nested = new ArrayList();

	/**
	 * 
	 * @param name
	 * @param transexec
	 */
	public ControlloMovimentoSimile(String name, TransactionQueryExecutor transexec, SourceBean sbGenerale) {
		this.name = name;
		trans = transexec;
		this.infoGenerale = sbGenerale;
	}

	public SourceBean processRecord(Map record) throws SourceBeanException {
		// azzero le liste delle warning e dei nested, altrimenti mi porto
		// dietro i risultatio e le segnalazioni
		// precedenti.
		warnings = new ArrayList();
		nested = new ArrayList();

		SourceBean puResult = null;
		try {

			// Se il record è nullo non lo posso elaborare, ritorno l'errore
			check(record);

			// se il contesto e' validazione allora non bisogna generare la
			// confirm
			String contesto = record.get("CONTEXT").toString().toLowerCase();
			boolean inValidazione = false;
			if (contesto.equalsIgnoreCase("validazionemassiva") || contesto.equalsIgnoreCase("valida")
					|| contesto.equalsIgnoreCase("validaArchivio")) {
				inValidazione = true;
			}
			String confermato = it.eng.sil.util.Utils.notNull(record.get("CONFERMA_CONTROLLO_MOV_SIMILI"));
			// se si proviene dalla conferma positiva dell'operatore bypasso il
			// controllo
			if (confermato.equals("true")) {
				throw new MovimentiSimiliException(null);
			}

			String configbase = ConfigSingleton.getRootPath() + File.separator + "WEB-INF" + File.separator + "conf"
					+ File.separator + "import" + File.separator;
			String configproc = configbase + "processors" + File.separator;
			RecordProcessor insertMissione;
			RecordProcessor deleteMovValida;
			String codTipoMov = (String) record.get("CODTIPOMOV");
			String datInizioMov = (String) record.get("DATINIZIOMOV");
			BigDecimal cdnLav = (BigDecimal) record.get("CDNLAVORATORE");
			String codTipoTrasf = record.get("CODTIPOTRASF") != null ? (String) record.get("CODTIPOTRASF") : "";
			boolean distacco = false;
			if (codTipoMov.equalsIgnoreCase("TRA") && codTipoTrasf.equalsIgnoreCase("DL")) {
				distacco = true;
			}

			SourceBean row = null;
			Object params[] = new Object[5];
			params[0] = cdnLav;
			params[1] = pageCOMPONENTE;
			params[2] = codTipoMov.toUpperCase();
			params[3] = datInizioMov;
			params[4] = cdnLav;
			try {
				// la query estrae movimenti doppi e/o simili
				row = (SourceBean) trans.executeQuery("GET_MOVIMENTI_SIMILI", params, "SELECT");

			} catch (EMFInternalError e) {
				logErr("Impossibile estrarre i movimenti simili a quello in inserimento/validazione");
				puResult = createResponse(MessageCodes.General.GET_ROW_FAIL,
						"Impossibile estrarre i movimenti simili a quello in inserimento/validazione");
				throw new MovimentiSimiliException(puResult);
			}
			BigDecimal user = (BigDecimal) RequestContainer.getRequestContainer().getSessionContainer()
					.getAttribute("_CDUT_");
			Vector rows = row.getAttributeAsVector("ROW");
			// ESISTONO MOVIMENTI SIMILI O DOPPI DEL MOVIMENTO IN
			// INSERIMENTO / VALIDAZIONE
			if (rows.size() > 0) {
				Object nMissione = movimentoMissioneDoppio(record, rows);
				if (nMissione != null) {
					record.put("PRGMOVIMENTO", nMissione);
					try {
						insertMissione = new InsertData("Inserimento Missione", trans,
								configproc + "InsertMovimentoMissione.xml", "INSERT_MOVIMENTO_MISSIONE", user);
						insertMissione.processRecord(record);
					} catch (Exception e) {
						puResult = createResponse(MessageCodes.ImportMov.ERR_DOPPIO_MOV,
								"Impossibile inserire i dati della missione");
						return puResult;
					}

					// se il contesto è la validazione in presenza di movimenti doppi, il record non viene inserito in
					// am_movimento ma deve essere cancellato dalla am_movimento_appoggio altrimenti sarebbe sempre
					// possibile
					// inserire missioni per lo stesso movimento
					if (inValidazione) {
						deleteMovValida = new RemoveMovimentoAppoggio(
								"Rimozione del record dalla tabella AM_MOVIMENTO_APPOGGIO", trans);
						deleteMovValida.processRecord(record);
					}
					warnings.add(new Warning(MessageCodes.ImportMov.WAR_NEW_MISSIONE, ""));
					return ProcessorsUtils.createResponse(name, className, null, null, warnings, nested, true);
				}
				int nMov = movimentoDoppio(record, rows);
				if (nMov == -1) {
					nMov = movimentoSimile(record, rows);
				}
				if (nMov != -1) {
					// a prescindere dal contesto l'operazione va bloccata
					// puResult =
					// createResponse(MessageCodes.ImportMov.ERR_DOPPIO_MOV,
					// "");
					if (codTipoMov.equals("TRA")) {
						if (distacco) {
							puResult = createResponse(MessageCodes.ImportMov.ERR_DOPPIO_MOV,
									"Non è possibile inserire due trasformazioni di distacco lavoratore nello stesso giorno con la stessa data fine distacco");
							return puResult;
						} else {
							puResult = createResponse(MessageCodes.ImportMov.ERR_DOPPIO_MOV,
									"Non è possibile inserire due trasformazioni nello stesso giorno con la stessa tipologia");
							return puResult;
						}
					}

					String s3 = record.containsKey("FLGASSDACESS") ? (String) record.get("FLGASSDACESS") : "";
					boolean assDaCess = false;
					if (codTipoMov.equals("AVV")) {
						assDaCess = s3.equalsIgnoreCase("S");
					}

					if (!assDaCess) {
						SourceBean mov = (SourceBean) rows.get(nMov);
						String s1 = StringUtils.getAttributeStrNotNull(mov, "CODMVCESSAZIONE");
						if (codTipoMov.equals("CES") && s1.equalsIgnoreCase("SC")) {
							String s2 = record.containsKey("CODMVCESSAZIONE") ? ((String) record.get("CODMVCESSAZIONE"))
									: "";
							if (!s1.equals(s2)) {
								Warning w = new Warning(MessageCodes.ImportMov.WAR_MOVIMENTO_SIMILE_CESSAZIONE_SC, "");
								puResult = createResponse(w, false);
								return puResult;
							} else {
								puResult = createResponse(MessageCodes.ImportMov.ERR_DOPPIO_MOV,
										"Non è possibile inserire due cessazioni nello stesso giorno con lo stesso motivo cessazione 'SC'");
								return puResult;
							}

						}

						if (ulterioreConfronto(record, (SourceBean) rows.get(nMov))) {
							// Procedo alle operazioni di TRANSAZIONE
							try {

								String flagBloccaAggMovDich = infoGenerale
										.getAttribute("FLGBLOCCAAGGANCIOMOVDICH") != null
												? infoGenerale.getAttribute("FLGBLOCCAAGGANCIOMOVDICH").toString()
												: "";
								if (flagBloccaAggMovDich.equalsIgnoreCase("S") && inValidazione) {
									puResult = createResponse(MessageCodes.ImportMov.ERR_DOPPIO_MOV,
											"Bisogna annullare il movimento doppio e procedere con la validazione del nuovo");
									return puResult;
								}

								// Se il flag FLGBLOCCAAGGANCIOMOVDICH <> S oppure non sono in validazione,
								// allora aggiorno i dati del movimento doppio in DB
								SourceBean processResult = aggiornaMovimentoDB((SourceBean) rows.get(nMov), record);
								if (processResult != null && ProcessorsUtils.isError(processResult)) {
									return processResult;
								}
								if (inValidazione) {
									// Se tutto è andato bene (E SONO IN
									// VALIDAZIONE MASSIVA O MANUALE)
									// rimuovo il movimento dalla tabella di
									// appoggio
									RecordProcessor removeMovAppoggio = new RemoveMovimentoAppoggio(
											"Rimozione da AM_MOVIMENTO_APPOGGIO", trans);
									SourceBean resRemove = removeMovAppoggio.processRecord(record);
									if (processResult != null && ProcessorsUtils.isError(processResult)) {
										return processResult;
									}
								}
								record.put("PRGMOVIMENTO", ((SourceBean) rows.get(nMov)).getAttribute("PRGMOVIMENTO"));
								// NOTA: poi segnalo l'aggiornamento del
								// movimento in DB e FERMO LA PROCCESSAZIONE!!!
								// I successivi RecordProcessor aggiunti al
								// validatore non verranno eseguiti!!!
								Warning w = new Warning(MessageCodes.ImportMov.WAR_MOV_DOPPIO_AGGIORNATO,
										"Il movimento è stato nuovamente protocollato");
								puResult = createResponse(w, true);

							} catch (Exception e) {
								puResult = createResponse(MessageCodes.ImportMov.ERR_DOPPIO_MOV, "");
								logErr(e.getMessage());
							}
						} else {
							puResult = createResponse(MessageCodes.ImportMov.ERR_DOPPIO_MOV, "");
						}
					} else {
						// ho terminato i controlli
						puResult = createResponse(MessageCodes.ImportMov.ERR_DOPPIO_MOV,
								"L'avviamento esiste già ed è collegato."
										+ "La CES che si sta inserendo non potrà quindi essere collegata a nessun movimento."
										+ "In caso di validazione massiva il sistema non prevede l'inserimento di CES orfane, "
										+ "procedere manualmente.");
					}
				} else { // se i movimenti non sono doppi allora sono simili
							// (data inizio, lavoratore, tipo, PR)
					if ((inValidazione) || (distacco)) { // in validazione oppure se si tratta di una proroga
															// del distacco l'operazione continua
						String msg = creaMessaggio(rows, codTipoMov, datInizioMov, "<BR>", "&nbsp;&nbsp;&nbsp;&nbsp;");
						// msg = "<pre>"+msg+"<//pre>";
						msg = "<TABLE><TR><TD>" + msg + "</TD></TR></TABLE>";
						Warning w = new Warning(MessageCodes.ImportMov.WAR_MOVIMENTO_SIMILE_VALIDAZIONE, msg);
						puResult = createResponse(w);
					} else { // in inserimento si mostra l'alert chiedendo
								// conferma all'operatore
						String msg = creaMessaggioConfirm(rows, codTipoMov, datInizioMov);
						puResult = createResponse(MessageCodes.ImportMov.WAR_MOVIMENTO_SIMILE, MSG_WARNING);
						ProcessorsUtils.addConfirm(puResult, msg, "confermaInserimento",
								new String[] { "CONFERMA_CONTROLLO_MOV_SIMILI", "true" }, true);
					}
				}
			}
		} catch (MovimentiSimiliException e) {
			puResult = e.getResult();
		}
		return puResult;
	}

	/**
	 * memorizza la risposta del processor. Passando null si saltano i controlli.
	 */
	private class MovimentiSimiliException extends Exception {
		SourceBean result;

		MovimentiSimiliException(SourceBean sb) {
			this.result = sb;
		}

		SourceBean getResult() {
			return this.result;
		}
	}

	/**	 
	 */
	private SourceBean createResponse(int code, String msg) throws SourceBeanException {
		return ProcessorsUtils.createResponse(name, className, new Integer(code), msg, warnings, nested);
	}

	private SourceBean createResponse(Warning w) throws SourceBeanException {
		return createResponse(w, false);
	}

	private SourceBean createResponse(Warning w, boolean stop) throws SourceBeanException {
		warnings.add(w);
		if (stop) {
			return ProcessorsUtils.createResponse(name, className, null, null, warnings, nested, true);
		} else {
			return ProcessorsUtils.createResponse(name, className, null, null, warnings, nested);
		}
	}

	private void logErr(String s) {
		_logger.fatal(className + ":" + s);
	}

	private int movimentoDoppio(Map record, Vector rows) {
		// è possibile avere più movimenti per stessa azienda, stesso
		// lavoratore, stessa data inizio,
		// nessuna azienda utilizzatrice se il codtipocontratto sia uno dei
		// seguenti: B.01.00, B.02.00, B.03.00
		// I.01.00, I.02.00
		int i = 0;
		String _codTipoMov = record.get("CODTIPOMOV") != null ? record.get("CODTIPOMOV").toString() : "";
		String _codTipoTrasf = record.get("CODTIPOTRASF") != null ? (String) record.get("CODTIPOTRASF") : "";
		String _datFineDistacco = record.get("DATFINEDISTACCO") != null ? (String) record.get("DATFINEDISTACCO") : "";
		String codTipoContratto = record.get("CODTIPOASS") != null ? (String) record.get("CODTIPOASS") : "";
		String _codTipoAzienda = record.get("CODAZTIPOAZIENDA") != null ? record.get("CODAZTIPOAZIENDA").toString()
				: "";
		long _prgAzienda = ((BigDecimal) record.get("PRGAZIENDA")).longValue();
		long _prgUnita = ((BigDecimal) record.get("PRGUNITAPRODUTTIVA")).longValue();
		long _prgAzUtil = record.get("PRGAZIENDAUTIL") == null ? -1
				: ((BigDecimal) record.get("PRGAZIENDAUTIL")).longValue();
		long _prgUnitaUtil = record.get("PRGUNITAUTIL") == null ? -1
				: ((BigDecimal) record.get("PRGUNITAUTIL")).longValue();

		while (i < rows.size()) {
			SourceBean mov = (SourceBean) rows.get(i);
			String codTipoTrasf = mov.getAttribute("CODTIPOTRASF") != null ? mov.getAttribute("CODTIPOTRASF").toString()
					: "";
			String datFineDistacco = mov.getAttribute("DATFINEDISTACCO") != null
					? mov.getAttribute("DATFINEDISTACCO").toString()
					: "";
			long prgAzienda = ((BigDecimal) mov.getAttribute("PRGAZIENDA")).longValue();
			long prgUnita = ((BigDecimal) mov.getAttribute("PRGUNITA")).longValue();
			long prgAzUtil = mov.getAttribute("PRGAZIENDAUTILIZ") == null ? -1
					: ((BigDecimal) mov.getAttribute("PRGAZIENDAUTILIZ")).longValue();
			long prgUnitaUtil = mov.getAttribute("PRGUNITAUTILIZ") == null ? -1
					: ((BigDecimal) mov.getAttribute("PRGUNITAUTILIZ")).longValue();

			if (_codTipoMov.equalsIgnoreCase("TRA")) {
				if ((_prgAzienda == prgAzienda) && (_prgUnita == prgUnita) && (_codTipoTrasf.equalsIgnoreCase("DL"))
						&& (codTipoTrasf.equals(_codTipoTrasf))) {
					if (!_datFineDistacco.equals("") && _datFineDistacco.equals(datFineDistacco)) {
						return i;
					} else {
						i++;
					}
				} else {
					if ((_prgAzienda == prgAzienda) && (_prgUnita == prgUnita)
							&& (codTipoTrasf.equals(_codTipoTrasf))) {
						return i;
					} else {
						i++;
					}
				}
			} else {
				if (_prgAzienda == prgAzienda && _prgUnita == prgUnita && _prgAzUtil == prgAzUtil
						&& _prgUnitaUtil == prgUnitaUtil) {
					// nessuna azienda utilizzatrice
					if (_prgAzUtil == -1 && _prgUnitaUtil == -1) {
						if (!DeTipoContrattoConstant.mapContrattiPG_Collaborazione.containsKey(codTipoContratto)
								&& !codTipoContratto.equalsIgnoreCase("I.01.00")
								&& !codTipoContratto.equalsIgnoreCase("I.02.00")
								&& !_codTipoAzienda.equalsIgnoreCase("PA")) {
							return i;
						}
					} else {
						return i;
					}
				}
				i++;
			}
		}
		return -1;
	}

	private int movimentoSimile(Map record, Vector rows) {
		int i = 0;
		int m = -1;
		long _prgAzienda = ((BigDecimal) record.get("PRGAZIENDA")).longValue();
		long _prgUnita = ((BigDecimal) record.get("PRGUNITAPRODUTTIVA")).longValue();
		long _prgAzUtil = record.get("PRGAZIENDAUTIL") == null ? -1
				: ((BigDecimal) record.get("PRGAZIENDAUTIL")).longValue();
		long _prgUnitaUtil = record.get("PRGUNITAUTIL") == null ? -1
				: ((BigDecimal) record.get("PRGUNITAUTIL")).longValue();
		String codTipoContratto = record.get("CODTIPOASS") != null ? (String) record.get("CODTIPOASS") : "";
		String _codTipoMov = record.get("CODTIPOMOV") != null ? record.get("CODTIPOMOV").toString() : "";
		String _codTipoAzienda = record.get("CODAZTIPOAZIENDA") != null ? record.get("CODAZTIPOAZIENDA").toString()
				: "";

		if (!_codTipoMov.equalsIgnoreCase("TRA")) {
			while (i < rows.size()) {
				SourceBean mov = (SourceBean) rows.get(i);
				long prgAzienda = ((BigDecimal) mov.getAttribute("PRGAZIENDA")).longValue();
				long prgUnita = ((BigDecimal) mov.getAttribute("PRGUNITA")).longValue();
				if (_prgAzienda == prgAzienda && _prgUnita == prgUnita) {
					if (DeTipoContrattoConstant.mapContrattiPG_Collaborazione.containsKey(codTipoContratto)
							|| codTipoContratto.equalsIgnoreCase("I.01.00")
							|| codTipoContratto.equalsIgnoreCase("I.02.00") || _codTipoAzienda.equalsIgnoreCase("PA")) {
						if ((_prgAzUtil != -1 && _prgUnitaUtil != -1)) {
							return i;
						}
					} else {
						return i;
					}
				}
				i++;
			}
		}
		return m;
	}

	private Object movimentoMissioneDoppio(Map record, Vector rows) {
		// Se si tratta di un movimento doppio (più movimenti per stessa azienda, stesso
		// lavoratore e stessa data inizio) ma è una somministrazione i dati di missione devono
		// essere inseriti nella tabella am_movimento_missione.

		int i = 0;
		String _codTipoAzienda = record.get("CODAZTIPOAZIENDA") != null ? record.get("CODAZTIPOAZIENDA").toString()
				: "";
		String _flgAssPropria = record.get("FLGASSPROPRIA") != null ? (String) record.get("FLGASSPROPRIA") : "";
		boolean _notAssPropria = "N".equalsIgnoreCase(_flgAssPropria);
		String datinizioMis = record.get("DATINIZIORAPLAV") != null ? record.get("DATINIZIORAPLAV").toString() : "";

		long _prgAzienda = ((BigDecimal) record.get("PRGAZIENDA")).longValue();
		long _prgUnita = ((BigDecimal) record.get("PRGUNITAPRODUTTIVA")).longValue();
		// controllo pure date misisoni record
		if (_codTipoAzienda.equalsIgnoreCase("INT") && _notAssPropria && !datinizioMis.equals("")) {
			while (i < rows.size()) {
				SourceBean mov = (SourceBean) rows.get(i);
				long prgAzienda = ((BigDecimal) mov.getAttribute("PRGAZIENDA")).longValue();
				long prgUnita = ((BigDecimal) mov.getAttribute("PRGUNITA")).longValue();
				// int prgMovimento = ((BigDecimal) mov.getAttribute("PRGMOVIMENTO")).intValue();
				Object prgMovimento = mov.getAttribute("PRGMOVIMENTO");
				if ((prgMovimento != null) && (_prgAzienda == prgAzienda) && (_prgUnita == prgUnita)) {
					return prgMovimento; // movimento doppio
				}
				i++;
			}
		}
		return null; // non si tratta di somministrazione
	}

	// DAVIDE 20/09/2005: metodo per gestire le nuove modalita di controllo di
	// un movimento doppio
	// Stefy 03/01/2006 - Il movimento viene considerato "doppio" solo se non si
	// tratta di un avviamento
	// fittizio dovuto a una cessazione
	private boolean ulterioreConfronto(Map record, SourceBean mov) {
		if (!StringUtils.getAttributeStrNotNull(mov, "CODMONOMOVDICH").equals("O")
				&& ((String) record.get("CODMONOMOVDICH")).equals("O")) {
			String codTipoMov = (String) record.get("CODTIPOMOV");
			String codMonoTmp = (String) record.get("CODMONOTEMPO") == null ? "" : (String) record.get("CODMONOTEMPO");
			// In caso di AVV a tempo determinato o di proroga controllo se le
			// date coincidono
			if (((codTipoMov.equals("AVV")) && (codMonoTmp.equals("D"))) || codTipoMov.equals("PRO")) {
				String s1 = StringUtils.getAttributeStrNotNull(mov, "DATFINEMOV");
				String s2 = record.containsKey("DATFINEMOV") ? (String) record.get("DATFINEMOV") : "";
				String s3 = record.containsKey("FLGASSDACESS") ? (String) record.get("FLGASSDACESS") : "";
				boolean assDaCess = false;
				if (codTipoMov.equals("AVV")) {
					assDaCess = s3.equalsIgnoreCase("S");
				}
				if (s1.equals(s2) && !assDaCess) {
					return true; // In tal caso è doppio
				}
				return false;

			} else if (codTipoMov.equals("CES")) {
				// In caso di cessazione controllo se il motivo di cessazine
				// coincide
				String s1 = StringUtils.getAttributeStrNotNull(mov, "CODMVCESSAZIONE");
				String s2 = record.containsKey("CODMVCESSAZIONE") ? ((String) record.get("CODMVCESSAZIONE")) : "";
				if (s1.equals(s2)) {
					return true;
				}
				return false;

			} else if (((codTipoMov.equals("AVV")) && (codMonoTmp.equals("I"))) || codTipoMov.equals("TRA")) {
				boolean assDaCess = false;
				String s3 = record.containsKey("FLGASSDACESS") ? (String) record.get("FLGASSDACESS") : "";
				if (codTipoMov.equals("AVV")) {
					assDaCess = s3.equalsIgnoreCase("S");
				}
				if (!assDaCess) {
					return true;
				}
				return false;
			}

		}
		return false;
	}

	private String creaMessaggioConfirm(Vector rows, String codTipoMov, String datInizioMov) {
		String msg = "";

		msg = creaMessaggio(rows, codTipoMov, datInizioMov, "\\r\\n", "\\t");
		msg += "   --------------";
		msg += "\\n\\r";
		msg += "Si desidera proseguire nella registrazione del movimento?\\n\\r";
		return msg;
	}

	/**
	 * Controlla che il record non sia nullo e che i campi necessari per il controllo esistano
	 * 
	 * @param record
	 * @throws MovimentiSimiliException
	 *             se non e' possibile proseguire
	 * @throws SourceBeanException
	 */
	private void check(Map record) throws MovimentiSimiliException, SourceBeanException {
		SourceBean puResult = null;
		if (record == null) {
			logErr("Il record da elaborare e' nullo");
			puResult = createResponse(MessageCodes.ImportMov.ERR_INSERT_DATA, "Record da elaborare nullo.");
			throw new MovimentiSimiliException(puResult);
		}

		String codTipoMov = (String) record.get("CODTIPOMOV");
		String datInizioMov = (String) record.get("DATINIZIOMOV");
		BigDecimal cdnLav = (BigDecimal) record.get("CDNLAVORATORE");
		BigDecimal prgAzienda = (BigDecimal) record.get("PRGAZIENDA");
		BigDecimal prgUnita = (BigDecimal) record.get("PRGUNITAPRODUTTIVA");
		String collegato = (String) record.get("COLLEGATO");

		if (cdnLav == null || datInizioMov == null || codTipoMov == null) {
			puResult = createResponse(MessageCodes.ImportMov.ERR_INSERT_DATA,
					"Impossibile elaborare il movimento, dati mancanti.");
			throw new MovimentiSimiliException(puResult);
		}
	}

	private String creaMessaggio(Vector rows, String codTipoMov, String datInizioMov, String BR, String TAB) {
		String msg = "";
		if (rows.size() < 2)
			msg = "Esiste già un movimento dello stesso tipo " + codTipoMov + " nella stessa data " + datInizioMov
					+ ". Estremi del movimento:" + BR;
		else
			msg = "Esistono già movimenti dello stesso tipo " + codTipoMov + " nella stessa data " + datInizioMov
					+ ". Estremi dei movimenti:" + BR;

		for (int i = 0; i < rows.size(); i++) {
			SourceBean row = (SourceBean) rows.get(i);
			BigDecimal numProt = SourceBeanUtils.getAttrBigDecimal(row, "numProtocollo", null);
			String dataInizio = Utils.notNull((String) row.getAttribute("datInizioMov"));
			String tipo = Utils.notNull((String) row.getAttribute("CODMONOMOVDICH"));
			String tempo = Utils.notNull((String) row.getAttribute("codMonoTempo"));
			String cognome = Utils.notNull((String) row.getAttribute("strCognome"));
			String nome = Utils.notNull((String) row.getAttribute("strNome"));
			String cf = Utils.notNull((String) row.getAttribute("strCodiceFiscale"));
			String ragioneSociale = Utils.notNull((String) row.getAttribute("strRagioneSociale"));
			String indAzienda = Utils.notNull((String) row.getAttribute("strIndirizzo"));
			String comune = Utils.notNull((String) row.getAttribute("comune"));
			String provincia = Utils.notNull((String) row.getAttribute("provincia"));
			String dataFine = Utils.notNull((String) row.getAttribute("datFineMovEffettiva"));
			String cap = Utils.notNull((String) row.getAttribute("strCap"));
			String localita = Utils.notNull((String) row.getAttribute("strLocalita"));
			String targa = Utils.notNull((String) row.getAttribute("strTarga"));
			String tipoFine = Utils.notNull((String) row.getAttribute("codMonoTipoFine"));

			String deTipo = tipo.equals("D") ? "Documentato dal lavoratore"
					: tipo.equals("C") ? "Dichiarato" : "Da comunicazione obbligatoria";
			String deTempo = tempo.equals("D") ? "Determinato" : "Indeterminato";

			// msg += BR;
			msg += TAB + "n. prot. " + ((numProt == null) ? "assente" : numProt.toString());
			msg += BR + TAB + "Data evento " + dataInizio;
			msg += BR + TAB + "tipo " + deTipo;
			msg += BR + TAB + "tempo " + deTempo;
			msg += BR + TAB + "Cognome nome lav " + cognome + " " + nome;
			msg += "     Codice fiscale lav " + cf;
			msg += BR + TAB + "Rag.Soc. Azienda " + ragioneSociale;
			msg += BR + TAB + "Ind. Azienda " + indAzienda + " , " + comune + " - " + targa + " (" + cap + ")";
			msg += BR + TAB + "Mov. Seg. " + tipoFine;
			msg += BR + TAB + "Data Fine " + dataFine;
			msg += BR;
		}
		return msg;
	}

	/**
	 * 17/05/2007 Se si inserisce um movimento simile potrebbe essere aggiornato il movimento simile registrato nel db
	 * anzicche' essere inserito il nuovo. In questo caso l'operazione di inserimento TERMINA con questo processor; gli
	 * altri processors registrati nel validator non verranno eseguiti. (Vedere commento NOTa #1) Quindi in questo
	 * metodo si gestisce direttamente l'inserimento del documento.
	 * 
	 * N.B. anche se si aggiorna il movimento simile il documento viene INSERITO --> al movimento saranno associati due
	 * documenti.<br>
	 * ATTENZIONE: nel caso di validazione con movimenti gia' protocollati da SARE, il numero di protocollo da usare e'
	 * quello registrato nel movimento ricevuto da sare.
	 */
	private SourceBean aggiornaMovimentoDB(SourceBean movDB, Map movINS) throws EMFInternalError, SourceBeanException {
		BigDecimal user = (BigDecimal) RequestContainer.getRequestContainer().getSessionContainer()
				.getAttribute("_CDUT_");
		RecordProcessor insDoc = new InsertDocumento(user, trans);
		SourceBean rowTMP = null;
		Object par[] = null;
		String strDescrizione = null;
		String codTipoMov = null;

		// Aggiorno il movimento in DB
		par = new Object[1];
		par[0] = movDB.getAttribute("PRGMOVIMENTO");
		rowTMP = (SourceBean) trans.executeQuery("GET_NKLOCK_MOV", par, "SELECT");
		par = new Object[6];
		par[0] = "O";
		par[1] = user;
		par[2] = movINS.get("DATCOMUNICAZ");
		par[3] = rowTMP.getAttribute("ROW.NUMKLOMOV");
		par[4] = movINS.get("CODCOMUNICAZIONE") != null ? movINS.get("CODCOMUNICAZIONE").toString() : "";
		par[5] = movDB.getAttribute("PRGMOVIMENTO");
		Object res = this.trans.executeQuery("UPDATE_MOV_DOPPIO", par, TransactionQueryExecutor.UPDATE);
		if (res instanceof EMFInternalError) {
			throw ((EMFInternalError) res);
		} else if (res instanceof Exception) {
			throw new EMFInternalError(EMFInternalError.INTERNAL_ERROR_ELEMENT,
					"Impossibile aggiornare il movimeneto in banca dati con progressivo "
							+ movDB.getAttribute("PRGMOVIMENTO"));
		}
		// Inserisco un nuovo documento associato che differisce dal vecchio per
		// data, protocollo e untente di ins/mod
		par = new Object[3];
		par[0] = movINS.get("CDNLAVORATORE");
		par[1] = ControlloMovimentoSimile.pageCOMPONENTE;
		par[2] = movDB.getAttribute("PRGMOVIMENTO");
		rowTMP = null;
		rowTMP = (SourceBean) trans.executeQuery("GET_DOCUMENTO", par, "SELECT");
		if (rowTMP == null) {
			throw new EMFInternalError(EMFInternalError.INTERNAL_ERROR_ELEMENT,
					"Impossibile reperire il documento associato al movimento che si sta processando");
		} else {
			rowTMP = (SourceBean) rowTMP.getAttribute("ROW");
		}

		// Stefy - 27/12/2005 - Valorizzazione del campo DESCRIZIONE
		codTipoMov = movINS.get("CODTIPOMOV").toString(); // movDB.getAttribute("CODTIPOMOV").toString();
		if (movINS.get("CODMONOMOVDICH").toString().equalsIgnoreCase("C")) {
			strDescrizione = "Dichiarato Lavoratore";
		} else {
			if (movINS.get("CODMONOMOVDICH").toString().equalsIgnoreCase("D")) {
				strDescrizione = "Documentato Lavoratore";
			} else {
				if (movINS.get("CODMONOMOVDICH").toString().equalsIgnoreCase("O")) {
					strDescrizione = "COMUNICAZIONE OBBLIGATORIA";
				}
			}
		}
		// --> end Stefy

		if (rowTMP != null) {
			// Costruisco la Map da passare al record processor che si occupa di
			// inserire i documenti
			Map movTMP = new Hashtable(movINS); // Hashtable implementa
												// l'interfaccia Map.
			movTMP.put("PRGMOVIMENTO", movDB.getAttribute("PRGMOVIMENTO"));// Il
																			// movimento
																			// diventa
																			// quello
																			// reperito
																			// nel
																			// db
			movTMP.put("CODCPI", rowTMP.getAttribute("CODCPI"));
			movTMP.put("CDNLAVORATORE", rowTMP.getAttribute("CDNLAVORATORE"));
			movTMP.put("PRGAZIENDA", rowTMP.getAttribute("PRGAZIENDA"));
			movTMP.put("PRGUNITA", rowTMP.getAttribute("PRGUNITA"));
			// movTMP.put("CODTIPOMOV",rowTMP.getAttribute("CODTIPOMOV"));
			movTMP.put("FLGAUTOCERTIFICAZIONE", StringUtils.getAttributeStrNotNull(rowTMP, "FLGAUTOCERTIFICAZIONE"));
			movTMP.put("STRDESCRIZIONE", StringUtils.getAttributeStrNotNull(rowTMP, "STRDESCRIZIONE"));
			movTMP.put("DATINIZIOMOV", rowTMP.getAttribute("DATINIZIOMOV"));
			movTMP.put("STRENTERILASCIO", StringUtils.getAttributeStrNotNull(rowTMP, "STRENTERILASCIO"));
			movTMP.put("FLGCODMONOIO", StringUtils.getAttributeStrNotNull(rowTMP, "FLGCODMONOIO"));

			movTMP.put("DATCOMUNICAZ", movINS.get("DATCOMUNICAZ"));
			movTMP.put("NUMANNOPROT", movINS.get("NUMANNOPROT"));
			movTMP.put("DATAPROT", movINS.get("DATAPROT"));
			movTMP.put("ORAPROT", movINS.get("ORAPROT"));
			movTMP.put("TIPOPROT", movINS.get("TIPOPROT"));
			movTMP.put("STRDESCRIZIONE", strDescrizione);
			// movTMP.put("KLOCKPROT",movINS.get("KLOCKPROT"));

			SourceBean processResult = insDoc.processRecord(movTMP);
			return processResult;
		} else {
			return null;
		}
	}

}
