/*
 * Creato il 18-nov-04
 */
package it.eng.sil.module.movimenti.processors;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Map;
import java.util.Vector;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.SessionContainer;
import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.base.SourceBeanException;
import com.engiweb.framework.error.EMFErrorSeverity;
import com.engiweb.framework.error.EMFInternalError;

import it.eng.afExt.utils.MessageCodes;
import it.eng.afExt.utils.TransactionQueryExecutor;
import it.eng.sil.bean.Documento;
import it.eng.sil.module.movimenti.ControllaMobilitaCollegata;
import it.eng.sil.module.movimenti.RecordProcessor;
import it.eng.sil.security.User;
import it.eng.sil.util.Sottosistema;
import it.eng.sil.util.amministrazione.impatti.AnnullaMovimentiGenerale;
import it.eng.sil.util.amministrazione.impatti.Controlli;
import it.eng.sil.util.amministrazione.impatti.ControlliException;
import it.eng.sil.util.amministrazione.impatti.MobilitaException;
import it.eng.sil.util.amministrazione.impatti.MovimentoBean;
import it.eng.sil.util.amministrazione.impatti.StatoOccupazionaleManager;

/**
 * Processor che esegue la rettifica di un movimento esistente, ne imposta il CODSTATOATTO a "RETTIFICATO" ed
 * eventualmente scollega il movimento dal precedente se presente. Alla fine cambia il contesto da rettifica a
 * inserimento per effettuare l'inserimento del nuovo movimento.
 * <p/>
 * 
 * @author roccetti
 */
public class RettificaMovimento implements RecordProcessor {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(RettificaMovimento.class.getName());

	/** Dati per la risposta */
	private String name;
	private String classname = this.getClass().getName();
	/** TransactionQueryExecutor da utilizzare per la query di select */
	private TransactionQueryExecutor trans;
	/** Identificatore utente */
	private BigDecimal userId;

	private RequestContainer requestContainer;
	private SourceBean request;
	private SourceBean response;

	/**
	 * Costruttore
	 * <p>
	 * 
	 * @param name
	 *            Nome del processore
	 * @param transexec
	 *            TransactionQueryExecutor da utilizzare per le query
	 */
	public RettificaMovimento(String name, TransactionQueryExecutor transexec, BigDecimal user, SourceBean req,
			SourceBean res, RequestContainer reqCont) {
		this.name = name;
		trans = transexec;
		this.userId = user;
		this.request = req;
		this.response = res;
		this.requestContainer = reqCont;
	}

	public SourceBean processRecord(Map record) throws SourceBeanException {

		// vettore delle warnings da restituire
		ArrayList warnings = new ArrayList();
		// Vettore dei risultati annidati da restituire
		ArrayList nested = new ArrayList();

		boolean esitoDoc = true;
		boolean nuovoDocumento = false;
		boolean gestioneAnnullamentoMov = true;

		// Controllo che i parametri necessario per la query di update ci siano
		BigDecimal prgMovRett = (BigDecimal) record.get("PRGMOVIMENTORETT");
		BigDecimal numKloMovRett = (BigDecimal) record.get("NUMKLOMOVRETT");
		String codMonoMovDichRett = (String) record.get("CODMONOMOVDICHRETT");
		String codStatoAttoRett = (String) record.get("CODSTATOATTORETT");
		String datComunicazRett = (String) record.get("DATCOMUNICAZRETT");
		BigDecimal numGgTraMovComRett = (BigDecimal) record.get("NUMGGTRAMOVCOMUNICAZRETT");
		String codMotAnnullRett = (String) record.get("CODMOTANNULLAMENTORETT");
		if (numKloMovRett == null || prgMovRett == null || codMonoMovDichRett == null || codStatoAttoRett == null
				|| datComunicazRett == null) {
			return ProcessorsUtils.createResponse(name, classname, new Integer(MessageCodes.ImportMov.ERR_INSERT_DATA),
					"Impossibile rettificare il movimento esistente, dati mancanti.", warnings, nested);
		}
		// Bisogna riallineare eventuali periodi intermittenti
		String codTipoMov = request.containsAttribute("CODTIPOMOV") ? request.getAttribute("CODTIPOMOV").toString()
				: "";
		String dataFineNewMov = null;
		if (codTipoMov.equalsIgnoreCase("AVV")) {
			dataFineNewMov = (String) record.get("DATFINEMOV");
		} else {
			if (codTipoMov.equalsIgnoreCase("PRO")) {
				dataFineNewMov = (String) record.get("DATFINEMOVPRO");
			} else {
				if (codTipoMov.equalsIgnoreCase("TRA")) {
					dataFineNewMov = (String) record.get("DATFINEMOVEFFETTIVA");
				}
			}
		}
		String datInizioNewMov = (String) record.get("DATINIZIOMOV");
		boolean esitoOperazione = MovimentoBean.riallineamenoPeriodiIntermittenti(prgMovRett, datInizioNewMov,
				dataFineNewMov, codTipoMov, warnings, userId, "RETTIFICA", trans, record);
		if (!esitoOperazione) {
			int code = MessageCodes.ImportMov.ERR_RIALLINEAMENTO_PERIODI_INTERMITTENTI;
			SourceBean puResult = ProcessorsUtils.createResponse(name, classname, new Integer(code),
					"Impossibile calcolare il nuovo stato occupazionale", warnings, nested);
			return puResult;
		}
		// Query per registrazione della rettifica
		Object[] args = new Object[8];

		args[0] = userId.toString();
		args[1] = numKloMovRett.toString();
		args[2] = codMonoMovDichRett;
		args[3] = codStatoAttoRett;
		args[4] = datComunicazRett;
		args[5] = numGgTraMovComRett;
		args[6] = codMotAnnullRett;
		args[7] = prgMovRett;

		Object result = null;
		try {
			result = trans.executeQuery("SALVA_GENERALE_RETTIFICA", args, TransactionQueryExecutor.UPDATE);
		} catch (Exception e) {
			it.eng.sil.util.TraceWrapper.debug(_logger, "Impossibile rettificare il movimento esistente. ", e);

			return ProcessorsUtils.createResponse(name, classname, new Integer(MessageCodes.ImportMov.ERR_UPDATE_DATA),
					"Impossibile rettificare il movimento esistente. ", warnings, nested);
		}

		// Se ho un'eccezione nel risultato lo segnalo
		if (result instanceof Exception) {
			it.eng.sil.util.TraceWrapper.debug(_logger, "Impossibile rettificare il movimento esistente. ",
					(Exception) result);

			return ProcessorsUtils.createResponse(name, classname, new Integer(MessageCodes.ImportMov.ERR_UPDATE_DATA),
					"Impossibile rettificare il movimento esistente. Dettagli: " + result.toString(), warnings, nested);
		} else if (!(result instanceof Boolean && ((Boolean) result).booleanValue() == true)) {
			_logger.debug("Impossibile rettificare il movimento esistente. ");

			return ProcessorsUtils.createResponse(name, classname, new Integer(MessageCodes.ImportMov.ERR_UPDATE_DATA),
					"Impossibile rettificare il movimento esistente. ", warnings, nested);
		} else {
			// L'annullamento del movimento è andato a buon fine

			// Annullo quindi anche il documento associato
			Object[] argsAnnullamento = new Object[3];

			SessionContainer ses = requestContainer.getSessionContainer();

			// Eseguo l'annullamento del documento associato solo nel caso in
			// cui il documento ci sia.
			if (ses.getAttribute("PRGDOCUMENTI") != null) {
				Vector prgDocs = (Vector) (ses.getAttribute("PRGDOCUMENTI"));
				User user = (User) ses.getAttribute(User.USERID);
				BigDecimal userid = new BigDecimal(user.getCodut());
				Documento doc = null;
				for (int k = 0; k < prgDocs.size(); k++) {
					BigDecimal prgDoc = (BigDecimal) prgDocs.get(k);
					try {
						doc = new Documento(prgDoc);
						doc.setCodStatoAtto(codStatoAttoRett);
						// recupero utente di modifica documento
						doc.setCdnUtMod(userid);
						doc.setCodMotAnnullamentoAtto((String) record.get("CODMOTANNULLAMENTORETT"));
						doc.setNumKloDocumento(doc.getNumKloDocumento().add(new BigDecimal("1")));
						try {
							doc.update(trans);
						} catch (Exception ex) {
							throw new EMFInternalError(EMFErrorSeverity.BLOCKING,
									"Annullamento Documento non possibile");
						}

					} catch (EMFInternalError ie) {
						it.eng.sil.util.TraceWrapper.debug(_logger,
								"Errore nella rettifica del documento associato al movimento. ", (Exception) ie);

						return ProcessorsUtils.createResponse(name, classname,
								new Integer(MessageCodes.ImportMov.ERR_UPDATE_DATA),
								"Errore nella rettifica del documento associato al movimento. ", warnings, nested);
					}
				}
			}

			record.put("CONTEXT", "inserisci");
		} // if(ses.getAttribute("PRGDOCUMENTI") != null)

		try {
			// Bisogna cancellare l'eventuale associazione della mobilità al movimento che si sta rettificando
			if (Sottosistema.MO.isOn()) {
				int risultato = 0;
				ControllaMobilitaCollegata updateMobColl = new ControllaMobilitaCollegata(this.trans);
				risultato = updateMobColl.esegui(prgMovRett);
				if (risultato == 1) {
					warnings.add(new Warning(MessageCodes.Mobilita.CANCELLA_ASSOCIAZIONE_MOVIMENTO_RETTIFICATO, ""));
				} else {
					if (risultato < 0) {
						throw new Exception("Errore nella cancellazione del collegamento della mobilità al movimento.");
					}
				}
			}
			// Esecuzione impatti rettifica (per annullamento)
			AnnullaMovimentiGenerale annullaMov = new AnnullaMovimentiGenerale(prgMovRett, request, response,
					requestContainer, trans);
			gestioneAnnullamentoMov = annullaMov.generaImpatti();

			if (!gestioneAnnullamentoMov && annullaMov.codiceFallimento != 0
					&& !Controlli.eseguiImpattiInPresenzaMovOrfani()) {

				int code = annullaMov.codiceFallimento;
				if (code == MessageCodes.CollegaMov.ERR_PRO_NON_COLLEGATA) {
					warnings.add(new Warning(MessageCodes.CollegaMov.ERR_PRO_NON_COLLEGATA,
							"Impossibile calcolare il nuovo stato occupazionale:proroga senza movimento collegato"
									+ "<BR/><strong>SI CONSIGLIA DI SISTEMARE LA SITUAZIONE E DI RICALCOLARE GLI IMPATTI</strong>"));
				} else if (code == MessageCodes.CollegaMov.ERR_TRASFO_TD_NON_COLLEGATA) {
					warnings.add(new Warning(MessageCodes.CollegaMov.ERR_TRASFO_TD_NON_COLLEGATA,
							"Impossibile calcolare il nuovo stato occupazionale:trasformazione t.d. senza movimento collegato"
									+ "<BR/><strong>SI CONSIGLIA DI SISTEMARE LA SITUAZIONE E DI RICALCOLARE GLI IMPATTI</strong>"));
				}

			}
			return ProcessorsUtils.createResponse(name, classname, null, null, warnings, nested);
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
}
