/*
 * Creato il 18-nov-04
 */
package it.eng.sil.module.movimenti.processors;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.base.SourceBeanException;

import it.eng.afExt.utils.DateUtils;
import it.eng.afExt.utils.MessageCodes;
import it.eng.afExt.utils.TransactionQueryExecutor;
import it.eng.sil.action.report.UtilsConfig;
import it.eng.sil.module.movimenti.RecordProcessor;
import it.eng.sil.util.amministrazione.impatti.Controlli;
import it.eng.sil.util.amministrazione.impatti.ControlliException;
import it.eng.sil.util.amministrazione.impatti.MobilitaException;
import it.eng.sil.util.amministrazione.impatti.MovimentiNonCollegati;
import it.eng.sil.util.amministrazione.impatti.ProTrasfoException;
import it.eng.sil.util.amministrazione.impatti.StatoOccupazionaleManager;

/**
 * Esegue gli impatti amministrativi a seguito dell'inserimento del movimento. Gli impatti scattano se nella map è
 * contenuto l'attributo String PERMETTIIMPATTI al valore true.
 * <p>
 * 
 * @author roccetti
 */
public class EseguiImpatti implements RecordProcessor {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(EseguiImpatti.class.getName());

	private String name;
	private String classname = this.getClass().getName();
	/** TransactionQueryExecutor da utilizzare per le transazioni */
	private TransactionQueryExecutor trans;
	/** Identificatore utente */
	private BigDecimal userId;
	private SourceBean sbInfoGenerale = null;
	private String flagScattanoImpatti = "S";

	public EseguiImpatti(String name, SourceBean sb, TransactionQueryExecutor transqueryexec, BigDecimal user) {
		this.name = name;
		if (transqueryexec == null) {
			throw new NullPointerException("TransactionQueryExecutor nullo");
		}
		this.trans = transqueryexec;
		if (user == null) {
			throw new NullPointerException("Identificatore utente nullo");
		}
		this.userId = user;
		this.sbInfoGenerale = sb;
		if (sbInfoGenerale != null && sbInfoGenerale.containsAttribute("FLGSCATTANOIMPATTI")) {
			flagScattanoImpatti = sbInfoGenerale.getAttribute("FLGSCATTANOIMPATTI").toString();
		}
	}

	/**
	 * 
	 */
	public SourceBean processRecord(Map record) throws SourceBeanException {
		// vettore delle warnings da restituire
		ArrayList warnings = new ArrayList();
		// Vettore dei risultati annidati da restituire
		ArrayList nested = new ArrayList();

		Boolean permettiImpatti = new Boolean(true);
		if (record.get("PERMETTIIMPATTI") != null) {
			permettiImpatti = new Boolean((String) record.get("PERMETTIIMPATTI"));
		}

		String dataInizioMov = (String) record.get("DATINIZIOMOV");
		// lettura dalla ts_generale del flag FLGSCATTANOIMPATTI
		try {
			// 04-04-05 D'Auria, Togna
			// Controllo che la data inizio movimento sia precedente alla data normativa 297
			// in tal caso il sistema non prevede automastismi per la gestione degli impatti
			UtilsConfig utility = new UtilsConfig("AM_297");
			String dataNormativa297 = utility.getValoreConfigurazione();

			if (DateUtils.compare(dataInizioMov, dataNormativa297) < 0) {
				permettiImpatti = new Boolean(false);
			}
			if (!flagScattanoImpatti.equals("S")) {
				permettiImpatti = new Boolean(false);
			}
		}

		catch (Exception e) {
			it.eng.sil.util.TraceWrapper.debug(_logger, "", e);

			int code = MessageCodes.StatoOccupazionale.ERRORE_GENERICO;
			SourceBean puResult = ProcessorsUtils.createResponse(name, classname, new Integer(code),
					"Fallita lettura flag per far scattare o meno gli impatti", warnings, nested);
			return puResult;
		}

		// Se vi sono i diritti di edit sul lav, scattano gli impatti
		if (permettiImpatti.booleanValue() && (record.get("COLLEGATO") != null)
				&& (record.get("COLLEGATO").toString().equalsIgnoreCase("PRECEDENTE")
						&& (record.get("CODTIPOMOV").equals("PRO") || record.get("CODTIPOMOV").equals("CES")
								|| record.get("CODTIPOMOV").equals("TRA"))
						|| (record.get("COLLEGATO").toString().equalsIgnoreCase("NESSUNO")
								&& record.get("CODTIPOMOV").equals("AVV")))) {
			it.eng.sil.util.amministrazione.impatti.StatoOccupazionaleBean statoOccupazionale = null;
			try {
				statoOccupazionale = StatoOccupazionaleManager.aggiorna(record, this.userId, this.trans);
				SourceBean request = RequestContainer.getRequestContainer().getServiceRequest();
				if (statoOccupazionale != null && statoOccupazionale.getProgressivoDB() != null) {
					record.put("PRGSTATOOCCUPAZ", statoOccupazionale.getProgressivoDB());
					// request.setAttribute("stato_occupazionale",
					// statoOccupazionale);
				}
				if (request.containsAttribute(
						it.eng.sil.util.amministrazione.impatti.MovimentoBean.REQ_NUM_K_LO_MOV_PREC_CAMBIATO)) {
					record.put(it.eng.sil.util.amministrazione.impatti.MovimentoBean.REQ_NUM_K_LO_MOV_PREC,
							request.getAttribute(
									it.eng.sil.util.amministrazione.impatti.MovimentoBean.REQ_NUM_K_LO_MOV_PREC_CAMBIATO));
					request.delAttribute(
							it.eng.sil.util.amministrazione.impatti.MovimentoBean.REQ_NUM_K_LO_MOV_PREC_CAMBIATO);
				}
				if (request.containsAttribute(StatoOccupazionaleManager.WARNING_STATO_OCCUPAZIONALE)) {
					ArrayList sOccWar = (ArrayList) request
							.getAttribute(StatoOccupazionaleManager.WARNING_STATO_OCCUPAZIONALE);
					for (int i = 0; i < sOccWar.size(); i++) {
						warnings.add((Warning) sOccWar.get(i));
					}
					request.delAttribute(StatoOccupazionaleManager.WARNING_STATO_OCCUPAZIONALE);
				}
				if (request.containsAttribute(StatoOccupazionaleManager.ALERT_STATO_OCCUPAZIONALE)) {
					if (!(record.get(it.eng.sil.util.amministrazione.impatti.MovimentoBean.CONTESTO_OPERATIVO) != null
							&& record.get(it.eng.sil.util.amministrazione.impatti.MovimentoBean.CONTESTO_OPERATIVO)
									.equals(it.eng.sil.util.amministrazione.impatti.MovimentoBean.CONTESTO_VALIDAZIONE_MASSIVA))) {
						List alerts = (List) request.getAttribute(StatoOccupazionaleManager.ALERT_STATO_OCCUPAZIONALE);
						boolean showAlert = (record.get("CONTEXT") != null
								&& record.get("CONTEXT").equals("validazioneMassiva"));
						SourceBean sb = ProcessorsUtils.createResponse(name, classname, null, "", null, null);
						for (int i = 0; i < alerts.size(); i++)
							ProcessorsUtils.addAlert(sb, (String) alerts.get(i), showAlert);
						//
						nested.add(sb);
					}
				}
			} catch (ProTrasfoException e) {

				if (Controlli.eseguiImpattiInPresenzaMovOrfani()) {
					int code = e.getCode();
					String msgError = "";
					if (code == MessageCodes.CollegaMov.ERR_PRO_NON_COLLEGATA) {
						msgError = "Impossibile calcolare il nuovo stato occupazionale:proroga senza movimento collegato"
								+ "<BR/><strong>SI CONSIGLIA DI SISTEMARE LA SITUAZIONE E DI RICALCOLARE GLI IMPATTI</strong>";
					} else {
						if (code == MessageCodes.CollegaMov.ERR_TRASFO_TD_NON_COLLEGATA) {
							msgError = "Impossibile calcolare il nuovo stato occupazionale:trasformazione t.d. senza movimento collegato"
									+ "<BR/><strong>SI CONSIGLIA DI SISTEMARE LA SITUAZIONE E DI RICALCOLARE GLI IMPATTI</strong>";
						}
					}
					SourceBean puResult = ProcessorsUtils.createResponse(name, classname, new Integer(code), msgError,
							warnings, nested);
					return puResult;
				} else {
					int code = e.getCode();
					String msgError = "";
					if (code == MessageCodes.CollegaMov.ERR_PRO_NON_COLLEGATA) {
						warnings.add(new Warning(MessageCodes.CollegaMov.ERR_PRO_NON_COLLEGATA,
								"Impossibile calcolare il nuovo stato occupazionale:proroga senza movimento collegato"
										+ "<BR/><strong>SI CONSIGLIA DI SISTEMARE LA SITUAZIONE E DI RICALCOLARE GLI IMPATTI</strong>"));
					} else if (code == MessageCodes.CollegaMov.ERR_TRASFO_TD_NON_COLLEGATA) {
						warnings.add(new Warning(MessageCodes.CollegaMov.ERR_TRASFO_TD_NON_COLLEGATA,
								"Impossibile calcolare il nuovo stato occupazionale:trasformazione t.d. senza movimento collegato"
										+ "<BR/><strong>SI CONSIGLIA DI SISTEMARE LA SITUAZIONE E DI RICALCOLARE GLI IMPATTI</strong>"));
					}
					return (ProcessorsUtils.createResponse(name, classname, null, null, warnings, nested));
				}
			}

			catch (MobilitaException e) {
				it.eng.sil.util.TraceWrapper.debug(_logger, "", (Exception) e);

				int code = e.getCode();
				String msgError = "";
				if (code == MessageCodes.Mobilita.USCITA_MOBILITA) {
					msgError = "Impossibile calcolare il nuovo stato occupazionale";
				} else {
					msgError = "Impossibile calcolare il nuovo stato occupazionale";
				}
				SourceBean puResult = ProcessorsUtils.createResponse(name, classname, new Integer(code), msgError,
						warnings, nested);
				if (RequestContainer.getRequestContainer().getServiceRequest()
						.containsAttribute(StatoOccupazionaleManager.CONFIRM_STATO_OCCUPAZIONALE)
						&& (record.get("CONTEXT") != null && !record.get("CONTEXT").equals("validazioneMassiva"))) {
					String valoreFlagMsgStatoOccMan = RequestContainer.getRequestContainer().getServiceRequest()
							.containsAttribute("FLAG_MESSAGGI_STATO_OCC_MANUALE")
									? RequestContainer.getRequestContainer().getServiceRequest()
											.getAttribute("FLAG_MESSAGGI_STATO_OCC_MANUALE").toString()
									: "";
					ProcessorsUtils.addConfirm(puResult, "Vuoi forzare l' operazione?", "ripetiInserimento",
							new String[] { "", "", valoreFlagMsgStatoOccMan }, true);
				}
				return puResult;
			}

			catch (Exception e) {
				int code = MessageCodes.StatoOccupazionale.ERRORE_GENERICO;
				if (e instanceof it.eng.sil.util.amministrazione.impatti.ControlliException) {
					it.eng.sil.util.TraceWrapper.debug(_logger, "", e);

					code = ((it.eng.sil.util.amministrazione.impatti.ControlliException) e).getCode();
				} else {
					it.eng.sil.util.TraceWrapper.debug(_logger, "", e);

				}
				SourceBean puResult = ProcessorsUtils.createResponse(name, classname, new Integer(code),
						"Impossibile calcolare il nuovo stato occupazionale", warnings, nested);
				if (RequestContainer.getRequestContainer().getServiceRequest()
						.containsAttribute(StatoOccupazionaleManager.CONFIRM_STATO_OCCUPAZIONALE)
						&& (record.get("CONTEXT") != null && !record.get("CONTEXT").equals("validazioneMassiva"))) {
					if (code == MessageCodes.StatoOccupazionale.STATO_OCCUPAZIONALE_MANUALE
							|| code == MessageCodes.StatoOccupazionale.CONSERVA_STATO_OCCUPAZIONALE_MANUALE
							|| code == MessageCodes.StatoOccupazionale.STATO_OCC_CON_MESI_SOSP_ANZ_NON_CALCOLATI
							|| code == MessageCodes.StatoOccupazionale.STATO_OCC_CON_DATA_ANZIANITA_ERRATA) {
						String forzaRicostruzione = RequestContainer.getRequestContainer().getServiceRequest()
								.getAttribute(StatoOccupazionaleManager.CONFIRM_STATO_OCCUPAZIONALE).toString();
						ProcessorsUtils.addConfirm(puResult, "Vuoi forzare l' operazione?", "continuaRicalcolo",
								new String[] { forzaRicostruzione }, true);
					} else {
						String valoreFlagMsgStatoOccMan = RequestContainer.getRequestContainer().getServiceRequest()
								.containsAttribute("FLAG_MESSAGGI_STATO_OCC_MANUALE")
										? RequestContainer.getRequestContainer().getServiceRequest()
												.getAttribute("FLAG_MESSAGGI_STATO_OCC_MANUALE").toString()
										: "";
						ProcessorsUtils.addConfirm(puResult, "Vuoi forzare l' operazione?", "ripetiInserimento",
								new String[] { "", "", valoreFlagMsgStatoOccMan }, true);
					}
				}
				return puResult;
			}
		}

		/*
		 * Genera gli impatti per i movimenti non collegati
		 */
		if (permettiImpatti.booleanValue() && (record.get("COLLEGATO") != null)
				&& ((String) record.get("COLLEGATO")).equalsIgnoreCase("NESSUNO") && (record.get("CODTIPOMOV") != null)
				&& (record.get("CODTIPOMOV").equals("CES") || record.get("CODTIPOMOV").equals("TRA"))) {
			it.eng.sil.util.amministrazione.impatti.StatoOccupazionaleBean statoOccupazionale = null;
			MovimentiNonCollegati movNonCol = new MovimentiNonCollegati(record, RequestContainer.getRequestContainer(),
					trans);
			try {

				statoOccupazionale = movNonCol.generaImpatti();
				SourceBean request = RequestContainer.getRequestContainer().getServiceRequest();
				if (request.containsAttribute(StatoOccupazionaleManager.WARNING_STATO_OCCUPAZIONALE)) {
					ArrayList sOccWar = (ArrayList) request
							.getAttribute(StatoOccupazionaleManager.WARNING_STATO_OCCUPAZIONALE);
					for (int i = 0; i < sOccWar.size(); i++) {
						warnings.add((Warning) sOccWar.get(i));
					}
				}
				if (request.containsAttribute(StatoOccupazionaleManager.ALERT_STATO_OCCUPAZIONALE)) {
					if (!(record.get(it.eng.sil.util.amministrazione.impatti.MovimentoBean.CONTESTO_OPERATIVO) != null
							&& record.get(it.eng.sil.util.amministrazione.impatti.MovimentoBean.CONTESTO_OPERATIVO)
									.equals(it.eng.sil.util.amministrazione.impatti.MovimentoBean.CONTESTO_VALIDAZIONE_MASSIVA))) {
						List alerts = (List) request.getAttribute(StatoOccupazionaleManager.ALERT_STATO_OCCUPAZIONALE);
						boolean showAlert = (record.get("CONTEXT") != null
								&& record.get("CONTEXT").equals("validazioneMassiva"));
						SourceBean sb = ProcessorsUtils.createResponse(name, classname, null, "", null, null);
						for (int i = 0; i < alerts.size(); i++)
							ProcessorsUtils.addAlert(sb, alerts.get(i).toString(), showAlert);
						//
						nested.add(sb);
					}
				}

				if (statoOccupazionale != null && statoOccupazionale.getProgressivoDB() != null) // {
					record.put("PRGSTATOOCCUPAZ", statoOccupazionale.getProgressivoDB());
				else if (request.containsAttribute("PRG_STATO_OCC_MOV_NON_COLLEGATO")
						&& !request.getAttribute("PRG_STATO_OCC_MOV_NON_COLLEGATO").equals(""))
					record.put("PRGSTATOOCCUPAZ", request.getAttribute("PRG_STATO_OCC_MOV_NON_COLLEGATO"));
				else if (!request.containsAttribute("MOV_IS_PREC_NORMATIVA")
						&& !request.containsAttribute("CASO_NON_GESTITO"))
					throw new Exception("Errore nel calcolo dello stato occupazionale");

			} catch (ProTrasfoException e) {
				if (Controlli.eseguiImpattiInPresenzaMovOrfani()) {
					int code = e.getCode();
					String msgError = "";
					if (code == MessageCodes.CollegaMov.ERR_PRO_NON_COLLEGATA) {
						msgError = "Impossibile calcolare il nuovo stato occupazionale:proroga senza movimento collegato"
								+ "<BR/><strong>SI CONSIGLIA DI SISTEMARE LA SITUAZIONE E DI RICALCOLARE GLI IMPATTI</strong>";
					} else {
						if (code == MessageCodes.CollegaMov.ERR_TRASFO_TD_NON_COLLEGATA) {
							msgError = "Impossibile calcolare il nuovo stato occupazionale:trasformazione t.d. senza movimento collegato"
									+ "<BR/><strong>SI CONSIGLIA DI SISTEMARE LA SITUAZIONE E DI RICALCOLARE GLI IMPATTI</strong>";
						} else {
							if (code == MessageCodes.CollegaMov.ERR_TRASFO_TI_NON_COLLEGATA_DOPO_297) {
								msgError = "Impossibile calcolare il nuovo stato occupazionale:trasformazione senza movimento collegato dopo un periodo in cui il lavoratore si trova in 150";
							}
						}
					}
					SourceBean puResult = ProcessorsUtils.createResponse(name, classname, new Integer(code), msgError,
							warnings, nested);
					return puResult;
				} else {
					int code = e.getCode();
					String msgError = "";
					if (code == MessageCodes.CollegaMov.ERR_PRO_NON_COLLEGATA) {
						warnings.add(new Warning(MessageCodes.CollegaMov.ERR_PRO_NON_COLLEGATA,
								"Impossibile calcolare il nuovo stato occupazionale:proroga senza movimento collegato"
										+ "<BR/><strong>SI CONSIGLIA DI SISTEMARE LA SITUAZIONE E DI RICALCOLARE GLI IMPATTI</strong>"));

					} else if (code == MessageCodes.CollegaMov.ERR_TRASFO_TD_NON_COLLEGATA) {
						warnings.add(new Warning(MessageCodes.CollegaMov.ERR_TRASFO_TD_NON_COLLEGATA,
								"Impossibile calcolare il nuovo stato occupazionale:trasformazione t.d. senza movimento collegato"
										+ "<BR/><strong>SI CONSIGLIA DI SISTEMARE LA SITUAZIONE E DI RICALCOLARE GLI IMPATTI</strong>"));
					}
					return (ProcessorsUtils.createResponse(name, classname, null, null, warnings, nested));
				}
			}

			catch (MobilitaException me) {
				it.eng.sil.util.TraceWrapper.debug(_logger, "", (Exception) me);

				int code = me.getCode();
				SourceBean puResult = ProcessorsUtils.createResponse(name, classname, new Integer(code),
						"Impossibile calcolare il nuovo stato occupazionale", warnings, nested);
				if (RequestContainer.getRequestContainer().getServiceRequest()
						.containsAttribute(StatoOccupazionaleManager.CONFIRM_STATO_OCCUPAZIONALE)) {
					String valoreFlagMsgStatoOccMan = RequestContainer.getRequestContainer().getServiceRequest()
							.containsAttribute("FLAG_MESSAGGI_STATO_OCC_MANUALE")
									? RequestContainer.getRequestContainer().getServiceRequest()
											.getAttribute("FLAG_MESSAGGI_STATO_OCC_MANUALE").toString()
									: "";
					ProcessorsUtils.addConfirm(puResult, "Vuoi forzare l' operazione?", "ripetiInserimento",
							new String[] { "", "", valoreFlagMsgStatoOccMan }, true);
				}
				return puResult;
			}

			catch (ControlliException ce) {
				it.eng.sil.util.TraceWrapper.debug(_logger, "", (Exception) ce);

				int code = ce.getCode();
				SourceBean puResult = ProcessorsUtils.createResponse(name, classname, new Integer(code),
						"Impossibile calcolare il nuovo stato occupazionale", warnings, nested);
				if (RequestContainer.getRequestContainer().getServiceRequest()
						.containsAttribute(StatoOccupazionaleManager.CONFIRM_STATO_OCCUPAZIONALE)
						&& (record.get("CONTEXT") != null && !record.get("CONTEXT").equals("validazioneMassiva"))) {
					if (code == MessageCodes.StatoOccupazionale.STATO_OCCUPAZIONALE_MANUALE
							|| code == MessageCodes.StatoOccupazionale.CONSERVA_STATO_OCCUPAZIONALE_MANUALE
							|| code == MessageCodes.StatoOccupazionale.STATO_OCC_CON_MESI_SOSP_ANZ_NON_CALCOLATI
							|| code == MessageCodes.StatoOccupazionale.STATO_OCC_CON_DATA_ANZIANITA_ERRATA) {
						String forzaRicostruzione = RequestContainer.getRequestContainer().getServiceRequest()
								.getAttribute(StatoOccupazionaleManager.CONFIRM_STATO_OCCUPAZIONALE).toString();
						ProcessorsUtils.addConfirm(puResult, "Vuoi forzare l' operazione?", "continuaRicalcolo",
								new String[] { forzaRicostruzione }, true);
					} else {
						String valoreFlagMsgStatoOccMan = RequestContainer.getRequestContainer().getServiceRequest()
								.containsAttribute("FLAG_MESSAGGI_STATO_OCC_MANUALE")
										? RequestContainer.getRequestContainer().getServiceRequest()
												.getAttribute("FLAG_MESSAGGI_STATO_OCC_MANUALE").toString()
										: "";
						ProcessorsUtils.addConfirm(puResult, "Vuoi forzare l' operazione?", "ripetiInserimento",
								new String[] { "", "", valoreFlagMsgStatoOccMan }, true);
					}
				}
				return puResult;
			}

			catch (Exception e) {
				it.eng.sil.util.TraceWrapper.debug(_logger, "", e);

				int code = MessageCodes.StatoOccupazionale.ERRORE_GENERICO;
				SourceBean puResult = ProcessorsUtils.createResponse(name, classname, new Integer(code),
						"Impossibile calcolare il nuovo stato occupazionale", warnings, nested);
				return puResult;
			}
		}
		// Se ho warning o risultati annidati li inserisco nella risposta,
		// altrimenti non ritorno nulla.
		if ((warnings.size() > 0) || (nested.size() > 0)) {
			return ProcessorsUtils.createResponse(name, classname, null, null, warnings, nested);
		} else {
			return null;
		}
	}
}