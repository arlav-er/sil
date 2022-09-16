package it.eng.sil.module.movimenti;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.SessionContainer;
import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.base.SourceBeanException;
import com.engiweb.framework.error.EMFErrorSeverity;
import com.engiweb.framework.error.EMFInternalError;

import it.eng.afExt.utils.MessageAppender;
import it.eng.afExt.utils.MessageCodes;
import it.eng.afExt.utils.SourceBeanUtils;
import it.eng.afExt.utils.StringUtils;
import it.eng.afExt.utils.TransactionQueryExecutor;
import it.eng.sil.bean.Documento;
import it.eng.sil.bean.protocollo.ProtocolloDocumentoUtil;
import it.eng.sil.module.AbstractSimpleModule;
import it.eng.sil.module.movimenti.constant.Properties;
import it.eng.sil.module.movimenti.processors.ProcessorsUtils;
import it.eng.sil.module.movimenti.processors.Warning;
import it.eng.sil.security.User;
import it.eng.sil.util.Sottosistema;
import it.eng.sil.util.amministrazione.impatti.AnnullaMovimenti;
import it.eng.sil.util.amministrazione.impatti.Controlli;
import it.eng.sil.util.amministrazione.impatti.ControlliException;
import it.eng.sil.util.amministrazione.impatti.MobilitaException;
import it.eng.sil.util.amministrazione.impatti.MovimentoBean;
import it.eng.sil.util.amministrazione.impatti.ProTrasfoException;
import it.eng.sil.util.amministrazione.impatti.StatoOccupazionaleManager;

public class M_MovSalvaConsultaGen extends AbstractSimpleModule {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(M_MovSalvaConsultaGen.class.getName());
	private static final int erroreGenerico = -1;
	private static final int erroreMobilita = -2;
	private static final int erroreControlli = -3;
	private boolean isErroreIntermittenti = false;
	private int codiceErrore = 0;

	public M_MovSalvaConsultaGen() {
	}

	public void service(SourceBean request, SourceBean response) throws SourceBeanException {
		// vettore delle warnings da restituire
		ArrayList warnings = new ArrayList();
		// Vettore dei risultati annidati da restituire
		ArrayList nested = new ArrayList();
		String name = "M_MovSalvaConsultaGen";
		String classname = this.getClass().getName();
		SourceBean records = new SourceBean("RECORDS");
		SourceBean record = new SourceBean("RECORD");
		RequestContainer requestContainer = getRequestContainer();
		SessionContainer sessionContainer = (SessionContainer) requestContainer.getSessionContainer();
		User user = (User) sessionContainer.getAttribute(User.USERID);
		BigDecimal userid = new BigDecimal(user.getCodut());

		TransactionQueryExecutor transExec = null;
		boolean esitoDoc = true;
		boolean nuovoDocumento = false;
		int gestioneAnnullamentoMov = 0;

		try {
			int idFail = disableMessageIdFail();
			int idSuccess = disableMessageIdSuccess();

			transExec = new TransactionQueryExecutor(getPool(), this);
			enableTransactions(transExec);
			transExec.initTransaction();

			if (request.getAttribute("NUOVODOCUMENTO") != null && request.getAttribute("NUOVODOCUMENTO").equals("1"))
				nuovoDocumento = true;

			boolean esito = annullaMovimentoSenzaRicostruzione(request, response, transExec);
			String prgMovimento = request.getAttribute("prgMovimento").toString();
			// Bisogna cancellare l'eventuale associazione della mobilità al movimento che si sta annullando
			if (Sottosistema.MO.isOn() && request.containsAttribute("CODSTATOATTO")
					&& !request.getAttribute("CODSTATOATTO").equals("PR")) {
				int risultato = 0;
				ControllaMobilitaCollegata updateMobColl = new ControllaMobilitaCollegata(transExec);
				risultato = updateMobColl.esegui(new BigDecimal(prgMovimento));
				if (risultato == 1) {
					warnings.add(new Warning(MessageCodes.Mobilita.CANCELLA_ASSOCIAZIONE_MOVIMENTO_ANNULLATO, ""));
				} else {
					if (risultato < 0) {
						throw new Exception("Errore nella cancellazione del collegamento della mobilità al movimento.");
					}
				}
			}
			// Bisogna riallineare i periodi intermittenti nel caso di lavoro intermittente
			if (request.containsAttribute("CODSTATOATTO") && !request.getAttribute("CODSTATOATTO").equals("PR")) {
				String codContratto = request.containsAttribute("CODCONTRATTO")
						? request.getAttribute("CODCONTRATTO").toString()
						: "";
				if (codContratto.equalsIgnoreCase(Properties.CONTRATTO_LAVORO_INTERMITTENTE)) {
					// Bisogna riallineare eventuali periodi intermittenti
					String codTipoMov = request.containsAttribute("CODTIPOMOV")
							? request.getAttribute("CODTIPOMOV").toString()
							: "";
					boolean esitoOperazione = MovimentoBean.riallineamenoPeriodiIntermittenti(
							new BigDecimal(prgMovimento), null, null, codTipoMov, warnings, userid, "ANNULLA",
							transExec, null);
					if (!esitoOperazione) {
						isErroreIntermittenti = true;
						throw new Exception("Errore riallineamento periodi intermittenti.");
					}
				}
			}

			annullaDocumento(request, response, transExec, esito);

			if (esito && nuovoDocumento) {
				try {
					// Inserimento documento da eseguire solo se è andato a buon fine l'inserimento del movimento
					Documento doc = new Documento();
					doc = settaDocumento(request);
					doc.setCdnUtMod(userid);
					doc.setCdnUtIns(userid);
					doc.setChiaveTabella(prgMovimento);

					doc.insert(transExec);
					// 02/04/2007: nel caso di docarea bisogna cancellare il
					// file inviato (per i movimenti si tratta di un file rtf
					// vuoto)
					ProtocolloDocumentoUtil.cancellaFileDocarea(doc);
				} catch (Exception emf) {
					esitoDoc = false;
				}
			}

			// Gestione Impatti a seguito dell'annullamento del movimento
			if (esito && (esitoDoc || !nuovoDocumento) && !request.getAttribute("CODSTATOATTO").equals("PR")) {
				AnnullaMovimenti annullaMov = new AnnullaMovimenti(request, response, getRequestContainer(), transExec);
				SourceBean mApp = annullaMov.getMovimento();
				gestioneAnnullamentoMov = annullaMov.generaImpatti();
				// Gestione visualizzazione alert e confirm nella JSP
				if (request.containsAttribute(StatoOccupazionaleManager.WARNING_STATO_OCCUPAZIONALE)) {
					ArrayList sOccWar = (ArrayList) request
							.getAttribute(StatoOccupazionaleManager.WARNING_STATO_OCCUPAZIONALE);
					for (int i = 0; i < sOccWar.size(); i++) {
						warnings.add((Warning) sOccWar.get(i));
					}
				}
				if (request.containsAttribute(StatoOccupazionaleManager.ALERT_STATO_OCCUPAZIONALE)) {
					List alerts = (List) request.getAttribute(StatoOccupazionaleManager.ALERT_STATO_OCCUPAZIONALE);
					boolean showAlert = false;
					SourceBean sb = ProcessorsUtils.createResponse(name, classname, null, "", null, null);
					for (int i = 0; i < alerts.size(); i++) {
						ProcessorsUtils.addAlert(sb, (String) alerts.get(i), showAlert);
					}
					nested.add(sb);
				}
			}

			if (esito && gestioneAnnullamentoMov == 0 && (esitoDoc || !nuovoDocumento)) {
				transExec.commitTransaction();
				MessageAppender.appendMessage(response, MessageCodes.General.UPDATE_SUCCESS);

				if ((warnings.size() > 0) || (nested.size() > 0)) {
					record.setAttribute(ProcessorsUtils.createResponse(name, classname, null, null, warnings, nested));
					record.setAttribute("RESULT", "WARNING");
					records.setAttribute(record);
					response.setAttribute(records);
				} else {
					record.setAttribute("RESULT", "OK");
					records.setAttribute(record);
					response.setAttribute(records);
				}
			} else if (!(gestioneAnnullamentoMov == 0)) {
				transExec.rollBackTransaction();
				transExec = null;
				if (gestioneAnnullamentoMov == MessageCodes.CollegaMov.ERR_PRO_NON_COLLEGATA)
					throw new ProTrasfoException(MessageCodes.CollegaMov.ERR_PRO_NON_COLLEGATA);
				else if (gestioneAnnullamentoMov == MessageCodes.CollegaMov.ERR_TRASFO_TD_NON_COLLEGATA)
					throw new ProTrasfoException(MessageCodes.CollegaMov.ERR_TRASFO_TD_NON_COLLEGATA);
				else if (gestioneAnnullamentoMov == MessageCodes.CollegaMov.ERR_TRASFO_TI_NON_COLLEGATA_DOPO_297)
					throw new ProTrasfoException(MessageCodes.CollegaMov.ERR_TRASFO_TI_NON_COLLEGATA_DOPO_297);
				else if (gestioneAnnullamentoMov == MessageCodes.Mobilita.USCITA_MOBILITA)
					throw new MobilitaException(MessageCodes.Mobilita.USCITA_MOBILITA);
				else if (gestioneAnnullamentoMov == MessageCodes.Mobilita.MOVIMENTO_NON_COMPATIBILE_CON_MOBILITA)
					throw new MobilitaException(MessageCodes.Mobilita.MOVIMENTO_NON_COMPATIBILE_CON_MOBILITA);
				else if (gestioneAnnullamentoMov == MessageCodes.StatoOccupazionale.STATO_OCCUPAZIONALE_MANUALE)
					throw new ControlliException(MessageCodes.StatoOccupazionale.STATO_OCCUPAZIONALE_MANUALE);
				else if (gestioneAnnullamentoMov == MessageCodes.StatoOccupazionale.CONSERVA_STATO_OCCUPAZIONALE_MANUALE)
					throw new ControlliException(MessageCodes.StatoOccupazionale.CONSERVA_STATO_OCCUPAZIONALE_MANUALE);
				else if (gestioneAnnullamentoMov == MessageCodes.StatoOccupazionale.STATO_OCC_CON_DATA_ANZIANITA_ERRATA)
					throw new ControlliException(MessageCodes.StatoOccupazionale.STATO_OCC_CON_DATA_ANZIANITA_ERRATA);
				else if (gestioneAnnullamentoMov == MessageCodes.StatoOccupazionale.STATO_OCC_CON_MESI_SOSP_ANZ_NON_CALCOLATI)
					throw new ControlliException(
							MessageCodes.StatoOccupazionale.STATO_OCC_CON_MESI_SOSP_ANZ_NON_CALCOLATI);
				else
					throw new Exception();
			} else {
				transExec.rollBackTransaction();
			}
		}

		catch (ProTrasfoException ex) {
			try {
				if (transExec != null) {
					transExec.rollBackTransaction();
				}
			} catch (EMFInternalError e1) {
				it.eng.sil.util.TraceWrapper.debug(_logger, "service()", (Exception) e1);
			}

			if (Controlli.eseguiImpattiInPresenzaMovOrfani()) {
				int code = ex.getCode();
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
				record.setAttribute(puResult);
				record.setAttribute("RESULT", "ERROR");
				records.setAttribute(record);
				response.setAttribute(records);

				it.eng.sil.util.TraceWrapper.debug(_logger, "service()", (Exception) ex);

				response.delAttribute("INSERT_OK");
				response.setAttribute("INSERT_OK", "FALSE");
			}
			// comunque annullo il movimento
			else {
				TransactionQueryExecutor transEx = null;
				try {
					transEx = new TransactionQueryExecutor(getPool(), this);
					enableTransactions(transEx);
					transEx.initTransaction();
					boolean esito = annullaMovimentoSenzaRicostruzione(request, response, transEx);
					annullaDocumento(request, response, transEx, esito);
					AnnullaMovimenti annullaMovim = new AnnullaMovimenti(request, response, getRequestContainer(),
							transEx);
					annullaMovim.gestisciPrgCollegati();
					transEx.commitTransaction();
					String msgError = "Impossibile calcolare il nuovo stato occupazionale:proroga senza movimento collegato"
							+ "<BR/><strong>SI CONSIGLIA DI SISTEMARE LA SITUAZIONE E DI RICALCOLARE GLI IMPATTI</strong>";
					SourceBean puResult = ProcessorsUtils.createResponse(name, classname, null, msgError, warnings,
							nested);
					int code = ex.getCode();

					if (code == MessageCodes.CollegaMov.ERR_PRO_NON_COLLEGATA) {
						warnings.add(new Warning(MessageCodes.CollegaMov.ERR_PRO_NON_COLLEGATA,
								"Impossibile calcolare il nuovo stato occupazionale:proroga senza movimento collegato"
										+ "<BR/><strong>SI CONSIGLIA DI SISTEMARE LA SITUAZIONE E DI RICALCOLARE GLI IMPATTI</strong>"));
					} else if (code == MessageCodes.CollegaMov.ERR_TRASFO_TD_NON_COLLEGATA) {
						warnings.add(new Warning(MessageCodes.CollegaMov.ERR_TRASFO_TD_NON_COLLEGATA,
								"Impossibile calcolare il nuovo stato occupazionale:trasformazione t.d. senza movimento collegato"
										+ "<BR/><strong>SI CONSIGLIA DI SISTEMARE LA SITUAZIONE E DI RICALCOLARE GLI IMPATTI</strong>"));
					}
					record.setAttribute(ProcessorsUtils.createResponse(name, classname, null, null, warnings, nested));
					record.setAttribute("RESULT", "WARNING");
					records.setAttribute(record);
					response.setAttribute(records);

				} catch (Exception e2) {
					try {
						transEx.rollBackTransaction();
					} catch (EMFInternalError e1) {
						it.eng.sil.util.TraceWrapper.debug(_logger, "service()", (Exception) e2);
					}
				}
			}

		}

		catch (MobilitaException me) {
			try {
				if (transExec != null) {
					transExec.rollBackTransaction();
				}
				if (gestioneAnnullamentoMov == MessageCodes.Mobilita.USCITA_MOBILITA) {
					MessageAppender.appendMessage(response, MessageCodes.Mobilita.USCITA_MOBILITA);
				} else {
					if (gestioneAnnullamentoMov == MessageCodes.Mobilita.MOVIMENTO_NON_COMPATIBILE_CON_MOBILITA) {
						MessageAppender.appendMessage(response,
								MessageCodes.Mobilita.MOVIMENTO_NON_COMPATIBILE_CON_MOBILITA);
					}
				}
			} catch (EMFInternalError ie) {
				it.eng.sil.util.TraceWrapper.debug(_logger, "service()", (Exception) ie);
			}

			int code = MessageCodes.StatoOccupazionale.ERRORE_GENERICO;
			if (me instanceof MobilitaException)
				code = ((MobilitaException) me).getCode();
			SourceBean puResult = ProcessorsUtils.createResponse(name, classname, new Integer(code),
					"Impossibile calcolare il nuovo stato occupazionale", warnings, nested);
			if (RequestContainer.getRequestContainer().getServiceRequest()
					.containsAttribute(StatoOccupazionaleManager.CONFIRM_STATO_OCCUPAZIONALE)) {
				String valoreFlagMsgStatoOccMan = RequestContainer.getRequestContainer().getServiceRequest()
						.containsAttribute("FLAG_MESSAGGI_STATO_OCC_MANUALE")
								? RequestContainer.getRequestContainer().getServiceRequest()
										.getAttribute("FLAG_MESSAGGI_STATO_OCC_MANUALE").toString()
								: "";
				ProcessorsUtils
						.addConfirm(puResult, "Vuoi forzare l' operazione?", "ripetiInserimentoDaAnnullamento",
								new String[] { "aggiorna", StringUtils.getAttributeStrNotNull(
										RequestContainer.getRequestContainer().getServiceRequest(), "CODSTATOATTO"),
										StringUtils.getAttributeStrNotNull(
												RequestContainer.getRequestContainer().getServiceRequest(),
												"CODMOTANNULLAMENTO"),
										valoreFlagMsgStatoOccMan },
								true);
			}
			record.setAttribute(puResult);
			record.setAttribute("RESULT", "ERROR");
			records.setAttribute(record);
			response.setAttribute(records);
			response.delAttribute("INSERT_OK");
			response.setAttribute("INSERT_OK", "FALSE");
		}

		// Rilevo l'eccezione nel caso in cui l'annullamento del documento non
		// sia riuscita
		catch (EMFInternalError ie) {
			if (transExec != null) {
				try {
					transExec.rollBackTransaction();
					it.eng.sil.util.TraceWrapper.debug(_logger, "service()", (Exception) ie);
				} catch (EMFInternalError e1) {
					it.eng.sil.util.TraceWrapper.debug(_logger, "service()", (Exception) e1);

				}
				MessageAppender.appendMessage(response, MessageCodes.ImportMov.ERR_ANNULLA_DOC);
			}
		}

		catch (Exception e) {
			if (transExec != null) {
				try {
					transExec.rollBackTransaction();
				} catch (EMFInternalError e1) {
					it.eng.sil.util.TraceWrapper.debug(_logger, "service()", (Exception) e1);
				}
			}
			int code = 0;
			if (isErroreIntermittenti) {
				code = MessageCodes.ImportMov.ERR_RIALLINEAMENTO_PERIODI_INTERMITTENTI;
			} else {
				code = MessageCodes.StatoOccupazionale.ERRORE_GENERICO;
				if (e instanceof it.eng.sil.util.amministrazione.impatti.ControlliException)
					code = ((it.eng.sil.util.amministrazione.impatti.ControlliException) e).getCode();

				if (code == MessageCodes.StatoOccupazionale.STATO_OCCUPAZIONALE_MANUALE
						|| code == MessageCodes.StatoOccupazionale.CONSERVA_STATO_OCCUPAZIONALE_MANUALE
						|| code == MessageCodes.StatoOccupazionale.STATO_OCC_CON_MESI_SOSP_ANZ_NON_CALCOLATI
						|| code == MessageCodes.StatoOccupazionale.STATO_OCC_CON_DATA_ANZIANITA_ERRATA) {
					MessageAppender.appendMessage(response, code);
				} else {
					if (gestioneAnnullamentoMov == 1) {
						MessageAppender.appendMessage(response, MessageCodes.ImportMov.ERR_IPOSSIBILE_ANNULLARE_MOV);
					} else {
						MessageAppender.appendMessage(response, MessageCodes.General.UPDATE_FAIL);
					}
				}
			}

			SourceBean puResult = ProcessorsUtils.createResponse(name, classname, new Integer(code),
					"Impossibile calcolare il nuovo stato occupazionale", warnings, nested);
			if (RequestContainer.getRequestContainer().getServiceRequest()
					.containsAttribute(StatoOccupazionaleManager.CONFIRM_STATO_OCCUPAZIONALE)) {
				String valoreFlagMsgStatoOccMan = RequestContainer.getRequestContainer().getServiceRequest()
						.containsAttribute("FLAG_MESSAGGI_STATO_OCC_MANUALE")
								? RequestContainer.getRequestContainer().getServiceRequest()
										.getAttribute("FLAG_MESSAGGI_STATO_OCC_MANUALE").toString()
								: "";
				ProcessorsUtils
						.addConfirm(puResult, "Vuoi forzare l' operazione?", "ripetiInserimentoDaAnnullamento",
								new String[] { "aggiorna", StringUtils.getAttributeStrNotNull(
										RequestContainer.getRequestContainer().getServiceRequest(), "CODSTATOATTO"),
										StringUtils.getAttributeStrNotNull(
												RequestContainer.getRequestContainer().getServiceRequest(),
												"CODMOTANNULLAMENTO"),
										valoreFlagMsgStatoOccMan },
								true);
			}
			record.setAttribute(puResult);
			record.setAttribute("RESULT", "ERROR");
			records.setAttribute(record);
			response.setAttribute(records);

			it.eng.sil.util.TraceWrapper.debug(_logger, "service()", e);

			response.delAttribute("INSERT_OK");
			response.setAttribute("INSERT_OK", "FALSE");
		}
	}

	public int eseguiAnnullamento(RequestContainer reqCont, SourceBean request, SessionContainer sessione,
			TransactionQueryExecutor transExec) throws SourceBeanException {
		SourceBean response = null;
		// vettore delle warnings da restituire
		ArrayList warnings = new ArrayList();
		// Vettore dei risultati annidati da restituire
		ArrayList nested = new ArrayList();
		String name = "M_MovSalvaConsultaGen:esegui";
		String classname = this.getClass().getName();
		int error = 0;

		int gestioneAnnullamentoMov = 0;

		try {
			User user = (User) sessione.getAttribute(User.USERID);
			BigDecimal userid = new BigDecimal(user.getCodut());
			AnnullaMovimenti annullaMov = new AnnullaMovimenti(request, reqCont, transExec);
			BigDecimal numkloMov = new BigDecimal(annullaMov.getMovimento().getAttribute("numklomov").toString());
			numkloMov = numkloMov.add(new BigDecimal("1"));
			boolean esito = annullaMovimentoSenzaRicostruzioneBis(request, userid, numkloMov, transExec);
			if (!esito) {
				return erroreGenerico;
			}
			SourceBean movimento = annullaMov.getMovimento();
			if (movimento != null) {
				movimento.updAttribute("numklomov", numkloMov);
				annullaMov.setMovimento(movimento);
			}
			String prgMovimento = request.getAttribute("prgMovimento").toString();
			// Bisogna cancellare l'eventuale associazione della mobilità al
			// movimento che si sta annullando
			if (Sottosistema.MO.isOn()) {
				int risultato = 0;
				ControllaMobilitaCollegata updateMobColl = new ControllaMobilitaCollegata(transExec);
				risultato = updateMobColl.esegui(new BigDecimal(prgMovimento));
				if (risultato == 1) {
					warnings.add(new Warning(MessageCodes.Mobilita.CANCELLA_ASSOCIAZIONE_MOVIMENTO_ANNULLATO, ""));
				} else {
					if (risultato < 0) {
						throw new Exception("Errore nella cancellazione del collegamento della mobilità al movimento.");
					}
				}
			}

			annullaDocumento(request, response, transExec, esito);
			// Gestione Impatti a seguito dell'annullamento del movimento
			if (esito && !request.getAttribute("CODSTATOATTO").equals("PR")) {
				gestioneAnnullamentoMov = annullaMov.generaImpatti();
				if (!(gestioneAnnullamentoMov == 0)) {
					if (gestioneAnnullamentoMov == MessageCodes.CollegaMov.ERR_PRO_NON_COLLEGATA)
						throw new ProTrasfoException(MessageCodes.CollegaMov.ERR_PRO_NON_COLLEGATA);
					else if (gestioneAnnullamentoMov == MessageCodes.CollegaMov.ERR_TRASFO_TD_NON_COLLEGATA)
						throw new ProTrasfoException(MessageCodes.CollegaMov.ERR_TRASFO_TD_NON_COLLEGATA);
					else if (gestioneAnnullamentoMov == MessageCodes.CollegaMov.ERR_TRASFO_TI_NON_COLLEGATA_DOPO_297)
						throw new ProTrasfoException(MessageCodes.CollegaMov.ERR_TRASFO_TI_NON_COLLEGATA_DOPO_297);
					else if (gestioneAnnullamentoMov == MessageCodes.Mobilita.USCITA_MOBILITA)
						throw new MobilitaException(MessageCodes.Mobilita.USCITA_MOBILITA);
					else if (gestioneAnnullamentoMov == MessageCodes.Mobilita.MOVIMENTO_NON_COMPATIBILE_CON_MOBILITA)
						throw new MobilitaException(MessageCodes.Mobilita.MOVIMENTO_NON_COMPATIBILE_CON_MOBILITA);
					else if (gestioneAnnullamentoMov == MessageCodes.StatoOccupazionale.STATO_OCCUPAZIONALE_MANUALE)
						throw new ControlliException(MessageCodes.StatoOccupazionale.STATO_OCCUPAZIONALE_MANUALE);
					else if (gestioneAnnullamentoMov == MessageCodes.StatoOccupazionale.CONSERVA_STATO_OCCUPAZIONALE_MANUALE)
						throw new ControlliException(
								MessageCodes.StatoOccupazionale.CONSERVA_STATO_OCCUPAZIONALE_MANUALE);
					else if (gestioneAnnullamentoMov == MessageCodes.StatoOccupazionale.STATO_OCC_CON_DATA_ANZIANITA_ERRATA)
						throw new ControlliException(
								MessageCodes.StatoOccupazionale.STATO_OCC_CON_DATA_ANZIANITA_ERRATA);
					else if (gestioneAnnullamentoMov == MessageCodes.StatoOccupazionale.STATO_OCC_CON_MESI_SOSP_ANZ_NON_CALCOLATI)
						throw new ControlliException(
								MessageCodes.StatoOccupazionale.STATO_OCC_CON_MESI_SOSP_ANZ_NON_CALCOLATI);
					else if (gestioneAnnullamentoMov == MessageCodes.DID.REDDITO_SUPERIORE_LIMITE)
						throw new ControlliException(MessageCodes.DID.REDDITO_SUPERIORE_LIMITE);
					else if (gestioneAnnullamentoMov == MessageCodes.DID.CONTROLLI_INSERIMENTO_NON_SUPERATI)
						throw new ControlliException(MessageCodes.DID.CONTROLLI_INSERIMENTO_NON_SUPERATI);
					else if (gestioneAnnullamentoMov == MessageCodes.DID.TERMINI_DID_NON_SCADUTI)
						throw new ControlliException(MessageCodes.DID.TERMINI_DID_NON_SCADUTI);
					else
						throw new Exception();
				}
			}
		}

		catch (ProTrasfoException ex) {
			error = erroreGenerico;
			it.eng.sil.util.TraceWrapper.debug(_logger, "service()", (Exception) ex);

		}

		catch (MobilitaException me) {
			error = erroreMobilita;
			it.eng.sil.util.TraceWrapper.debug(_logger, "service()", (Exception) me);
		}

		catch (ControlliException ce) {
			error = erroreControlli;
			setCodiceErrore(ce.getCode());
			it.eng.sil.util.TraceWrapper.debug(_logger, "service()", (Exception) ce);
		}

		// Rilevo l'eccezione nel caso in cui l'annullamento del documento non
		// sia riuscita
		catch (EMFInternalError ie) {
			error = erroreGenerico;
			it.eng.sil.util.TraceWrapper.debug(_logger, "service()", (Exception) ie);

		}

		catch (Exception e) {
			error = erroreGenerico;
			it.eng.sil.util.TraceWrapper.debug(_logger, "service()", e);

		}
		return error;
	}

	private Documento settaDocumento(SourceBean req) {
		Documento doc = new Documento();

		doc.setCodCpi(StringUtils.getAttributeStrNotNull(req, "codCpi"));
		doc.setCdnLavoratore(new BigDecimal(req.getAttribute("cdnLavoratore").toString()));
		doc.setPrgAzienda(new BigDecimal(req.getAttribute("PRGAZIENDA").toString()));
		doc.setPrgUnita(new BigDecimal(req.getAttribute("PRGUNITA").toString()));
		if (req.getAttribute("CODTIPOMOV").toString().equalsIgnoreCase("AVV")) {
			doc.setCodTipoDocumento("MVAVV");
		}
		if (req.getAttribute("CODTIPOMOV").toString().equalsIgnoreCase("CES")) {
			doc.setCodTipoDocumento("MVCES");
		}
		if (req.getAttribute("CODTIPOMOV").toString().equalsIgnoreCase("TRA")) {
			doc.setCodTipoDocumento("MVTRA");
		}
		if (req.getAttribute("CODTIPOMOV").toString().equalsIgnoreCase("PRO")) {
			doc.setCodTipoDocumento("MVPRO");
		}

		doc.setFlgAutocertificazione(StringUtils.getAttributeStrNotNull(req, "flgAutocertificazione"));
		doc.setStrDescrizione(StringUtils.getAttributeStrNotNull(req, "strDescrizione"));
		doc.setDatInizio(StringUtils.getAttributeStrNotNull(req, "DATAINIZIO"));
		doc.setStrEnteRilascio(StringUtils.getAttributeStrNotNull(req, "StrEnteRilascio"));
		// doc.setCodMonoIO(StringUtils.getAttributeStrNotNull(req,"FlgCodMonoIO"));
		doc.setDatAcqril(StringUtils.getAttributeStrNotNull(req, "DATCOMUNICAZ"));
		doc.setNumAnnoProt(new BigDecimal(req.getAttribute("numAnnoProt").toString()));
		doc.setNumProtocollo(SourceBeanUtils.getAttrBigDecimal(req, "numProtocollo", null));
		doc.setDatProtocollazione(StringUtils.getAttributeStrNotNull(req, "dataProt") + " "
				+ StringUtils.getAttributeStrNotNull(req, "oraProt"));
		doc.setNumKeyLock(new BigDecimal(req.getAttribute("KLOCKPROT").toString()));
		doc.setCodMonoIO("I");
		doc.setPagina("MovDettaglioGeneraleConsultaPage");
		if (StringUtils.getAttributeStrNotNull(req, "TIPOPROTOCOLLO").equals("")
				|| StringUtils.getAttributeStrNotNull(req, "TIPOPROTOCOLLO").equals("AUTOMATICO")) {
			doc.setTipoProt("S");
		} else {
			doc.setTipoProt("N");
		}

		return doc;
	}

	private boolean annullaMovimentoSenzaRicostruzione(SourceBean request, SourceBean response,
			TransactionQueryExecutor transactionQueryExecutor) {
		setSectionQueryUpdate("QUERY_UPDATE_ANNULLA_SENZA_IMPATTI");
		return doUpdate(request, response);
	}

	private boolean annullaMovimentoSenzaRicostruzione(SourceBean request, BigDecimal userid, BigDecimal numkloMov,
			TransactionQueryExecutor transactionQueryExecutor) throws Exception {
		Object prgMov = request.getAttribute("PRGMOVIMENTO");
		Object codStatoAtto = request.getAttribute("CODSTATOATTO");
		Object codMotivo = request.getAttribute("CODMOTANNULLAMENTO");
		Object result = null;
		numkloMov = numkloMov.add(new BigDecimal("1"));
		String updateQuery = "UPDATE AM_MOVIMENTO SET cdnUtMod = " + userid + ", dtmMod = SYSDATE, "
				+ "CODMOTANNULLAMENTO = '" + codMotivo + "', numKloMov = " + numkloMov + ", CODSTATOATTO = '"
				+ codStatoAtto + "', PRGSTATOOCCUPAZ = null " + "WHERE PrgMovimento = " + prgMov;
		result = transactionQueryExecutor.executeQueryByStringStatement(updateQuery, null,
				TransactionQueryExecutor.UPDATE);
		if (!((result instanceof Boolean) && (((Boolean) result).booleanValue() == true))) {
			return false;
		} else {
			return true;
		}

	}

	private boolean annullaMovimentoSenzaRicostruzioneBis(SourceBean request, BigDecimal userid, BigDecimal numkloMov,
			TransactionQueryExecutor transactionQueryExecutor) throws Exception {
		Object prgMov = request.getAttribute("PRGMOVIMENTO");
		Object codStatoAtto = request.getAttribute("CODSTATOATTO");
		Object codMotivo = request.getAttribute("CODMOTANNULLAMENTO");
		Object result = null;
		String updateQuery = "UPDATE AM_MOVIMENTO SET cdnUtMod = " + userid + ", dtmMod = SYSDATE, "
				+ "CODMOTANNULLAMENTO = '" + codMotivo + "', numKloMov = " + numkloMov + ", CODSTATOATTO = '"
				+ codStatoAtto + "', PRGSTATOOCCUPAZ = null " + "WHERE PrgMovimento = " + prgMov;
		result = transactionQueryExecutor.executeQueryByStringStatement(updateQuery, null,
				TransactionQueryExecutor.UPDATE);
		if (!((result instanceof Boolean) && (((Boolean) result).booleanValue() == true))) {
			return false;
		} else {
			return true;
		}

	}

	private void annullaDocumento(SourceBean request, SourceBean response,
			TransactionQueryExecutor transactionQueryExecutor, boolean esito) throws EMFInternalError {
		// Se ho il prgdocumento (per alcuni movimenti provenienti da porting
		// non ho il documento associato)
		RequestContainer req = RequestContainer.getRequestContainer();
		SessionContainer ses = req.getSessionContainer();
		Vector prgDocs = (Vector) ses.getAttribute("PRGDOCUMENTI");

		if (prgDocs != null && prgDocs instanceof Vector) {
			// Annullo il documento associato al movimento solo se
			// l'annullamneto del movimento è riuscito
			if (esito && (!request.getAttribute("CODSTATOATTO").equals("PR"))) {
				User user = (User) ses.getAttribute(User.USERID);
				BigDecimal userid = new BigDecimal(user.getCodut());
				for (int k = 0; k < prgDocs.size(); k++) {
					BigDecimal prgDocumento = (BigDecimal) prgDocs.get(k);
					Documento doc = new Documento(prgDocumento);
					doc.setCodStatoAtto((String) request.getAttribute("CODSTATOATTO"));
					doc.setCodMotAnnullamentoAtto((String) request.getAttribute("CODMOTANNULLAMENTO"));
					doc.setNumKloDocumento(doc.getNumKloDocumento().add(new BigDecimal("1")));
					// recupero utente di modifica documento
					doc.setCdnUtMod(userid);
					try {
						// 02/04/2007: qui non e' necessario cancellare il file
						// inviato a docarea perche' il documento non viene
						// protocollato ma annullato
						doc.update(transactionQueryExecutor);
						// pero' meglio fare lo stesso la chiamata....
						ProtocolloDocumentoUtil.cancellaFileDocarea(doc);
					} catch (Exception ex) {
						throw new EMFInternalError(EMFErrorSeverity.BLOCKING, "Annullamento Documento non possibile");
					}
				}
			}
		}

	}

	public void setCodiceErrore(int val) {
		this.codiceErrore = val;
	}

	public int getCodiceErrore() {
		return this.codiceErrore;
	}

}