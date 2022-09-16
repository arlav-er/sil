package it.eng.sil.module.amministrazione;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Vector;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.utils.DateUtils;
import it.eng.afExt.utils.MessageCodes;
import it.eng.afExt.utils.ReportOperationResult;
import it.eng.afExt.utils.TransactionQueryExecutor;
import it.eng.sil.action.report.UtilsConfig;
import it.eng.sil.coop.utils.MessageBundle;
import it.eng.sil.module.movimenti.processors.ProcessorsUtils;
import it.eng.sil.module.patto.bean.Patto;
import it.eng.sil.util.ExceptionUtils;
import it.eng.sil.util.amministrazione.impatti.Controlli;
import it.eng.sil.util.amministrazione.impatti.ControlliException;
import it.eng.sil.util.amministrazione.impatti.DBLoad;
import it.eng.sil.util.amministrazione.impatti.DBStore;
import it.eng.sil.util.amministrazione.impatti.ListaMobilita;
import it.eng.sil.util.amministrazione.impatti.MobilitaBean;

public class InsertStatoOccupaz extends it.eng.sil.module.AbstractSimpleModule {
	private String className = this.getClass().getName();

	public void service(SourceBean request, SourceBean response) throws Exception {
		ReportOperationResult reportOperation = new ReportOperationResult(this, response);
		enableSimpleQuery(true);
		Object res = null;
		Object res1 = null;
		Object res2 = null;
		boolean ret = false;
		int idSuccess = this.disableMessageIdSuccess();
		this.disableMessageIdFail();
		TransactionQueryExecutor transExec = null;
		RequestContainer requestContainer = RequestContainer.getRequestContainer();
		BigDecimal userID = (BigDecimal) requestContainer.getSessionContainer().getAttribute("_CDUT_");
		String data297 = null;
		String messaggioFail297 = null;
		try {
			UtilsConfig utility = new UtilsConfig("AM_297");
			data297 = utility.getValoreConfigurazione();
			String datInizioSocc = request.containsAttribute("datInizio") ? request.getAttribute("datInizio").toString()
					: "";
			if (!datInizioSocc.equals("") && DateUtils.compare(datInizioSocc, data297) < 0) {
				if (request.containsAttribute("CONTINUA_CALCOLO_SOCC_PREC_297") && request
						.getAttribute("CONTINUA_CALCOLO_SOCC_PREC_297").toString().equalsIgnoreCase("false")) {
					Vector v = new Vector(1);
					v.add(data297);
					messaggioFail297 = MessageBundle
							.getMessage(MessageCodes.StatoOccupazionale.INSERISCI_STATO_OCC_PREC_NORMATIVA_297, v);
					setMessageIdFail(MessageCodes.StatoOccupazionale.STATO_OCC_PREC_DATA_NORMATIVA_297);
					throw new ControlliException(MessageCodes.StatoOccupazionale.STATO_OCC_PREC_DATA_NORMATIVA_297);
				}
			}
			transExec = new TransactionQueryExecutor(getPool());
			enableTransactions(transExec);
			transExec.initTransaction();

			setSectionQuerySelect("QUERY_SELECT");
			SourceBean resultSelect = doSelect(request, response);
			// Esiste già uno stato occuapzionale inserito in quella data
			if (resultSelect.getAttribute("ROW") != null) {
				setMessageIdFail(MessageCodes.General.ERR_STATO_OCCUPAZIONALE_DUPLICATO);
				throw new ControlliException(MessageCodes.General.ERR_STATO_OCCUPAZIONALE_DUPLICATO);
			}

			setSectionQuerySelect("GET_STATO_OCC");
			SourceBean sb = doSelect(request, response, false);
			String nuovoStatoOccupaz = "";
			nuovoStatoOccupaz = request.containsAttribute("nuovoRaggStatoOccupaz")
					? request.getAttribute("nuovoRaggStatoOccupaz").toString()
					: "";

			if (nuovoStatoOccupaz.equals("A") || nuovoStatoOccupaz.equals("O")) {
				// bisogna chiudere la did e il patto aperti
				Object cdnLavoratore = request.getAttribute("cdnLavoratore");
				String codStatoOccupaz = (String) request.getAttribute("codStatoOcc");
				SourceBean deStatoOccupaz = DBLoad.getDeStatoOccupaz(request, codStatoOccupaz, transExec);
				String codMotivoFineAtto = (String) deStatoOccupaz.getAttribute("codMotivoFineAtto");
				if (codMotivoFineAtto == null) {
					codMotivoFineAtto = "AV";
				}

				if (request.containsAttribute("codMotivoFineAtto")) {
					request.updAttribute("codMotivoFineAtto", codMotivoFineAtto);
				} else {
					request.setAttribute("codMotivoFineAtto", codMotivoFineAtto);
				}
				setSectionQueryUpdate("CHIUSURA_DID");
				res = doUpdate(request, response, true);
				if (ExceptionUtils.errorOccurred(res)) {
					getErrorHandler().addError(new com.engiweb.framework.error.EMFUserError(
							com.engiweb.framework.error.EMFErrorSeverity.ERROR,
							MessageCodes.DID.CHIUSURA_NON_POSSIBILE));
					throw new Exception("impossibile chiudere la did");
				}
				setSectionQueryUpdate("CHIUSURA_PATTO");
				res1 = doUpdate(request, response, true);
				if (ExceptionUtils.errorOccurred(res)) {
					getErrorHandler().addError(new com.engiweb.framework.error.EMFUserError(
							com.engiweb.framework.error.EMFErrorSeverity.ERROR,
							MessageCodes.Patto.CHIUSURA_NON_POSSIBILE));
					throw new Exception("impossibile chiudere il patto");
				}
				// eventuale riapertura accordo generico
				Patto ptAccordo = new Patto();
				ptAccordo.controllaAperturaAccordoGenerico(cdnLavoratore, getRequestContainer(), transExec);

				ListaMobilita listaMobilita = new ListaMobilita(cdnLavoratore, transExec);
				Vector mobilita = listaMobilita.getMobilita();
				SourceBean mobilitaAperta = Controlli.eventoInMobilita(mobilita, datInizioSocc);
				if (mobilitaAperta != null) {
					// chiusura iscrizione con motivo decadenza che non fa fare scorrimento e decadenza
					BigDecimal numkloMob = new BigDecimal(mobilitaAperta.getAttribute("NUMKLOMOBISCR").toString());
					BigDecimal prgMobilitaIscr = (BigDecimal) mobilitaAperta
							.getAttribute(MobilitaBean.DB_PRG_MOBILITA_ISCR);
					String dataDecadenza = DateUtils.giornoPrecedente(datInizioSocc);
					numkloMob = DBStore.aggiornaDataFineMobilita(prgMobilitaIscr, dataDecadenza,
							MobilitaBean.MOTIVO_DECADENZA_MOB_SOCC_MANUALE, userID, numkloMob, transExec);
					mobilitaAperta.updAttribute("NUMKLOMOBISCR", numkloMob);
					reportOperation.reportSuccess(MessageCodes.StatoOccupazionale.DECADUTA_MOBILITA_MANUALE);
				}
			} else {
				// eventuale chiusura accordo generico
				Patto ptAccordo = new Patto();
				ptAccordo.controllaChiusuraAccordoGenerico(request.getAttribute("cdnLavoratore"), getRequestContainer(),
						transExec);
			}

			// }

			setSectionQueryInsert("QUERY_INSERT");
			res2 = doInsert(request, response, true);
			ExceptionUtils.controlloDateRecordPrecedente(res2,
					MessageCodes.General.ERR_INTERSEZIONE_DATA_REC_STORICIZZATO);
			if (res != null && ((Boolean) res).booleanValue()) {
				reportOperation.reportSuccess(MessageCodes.DID.CHIUSURA_AVVENUTA);
			}
			if (res1 != null && ((Boolean) res1).booleanValue()) {
				reportOperation.reportSuccess(MessageCodes.Patto.CHIUSURA_AVVENUTA);
			}
			transExec.commitTransaction();
			reportOperation.reportSuccess(MessageCodes.General.INSERT_SUCCESS);
		} catch (it.eng.sil.module.QueryStrategyException qse) {
			if (transExec != null) {
				transExec.rollBackTransaction();
			}
			reportOperation.reportFailure(MessageCodes.General.ERR_INTERSEZIONE_DATA_REC_STORICIZZATO);
		} catch (ControlliException cEx) {
			if (transExec != null) {
				transExec.rollBackTransaction();
			}
			if (cEx.getCode() == MessageCodes.General.ERR_STATO_OCCUPAZIONALE_DUPLICATO) {
				reportOperation.reportFailure(MessageCodes.General.ERR_STATO_OCCUPAZIONALE_DUPLICATO);
			} else {
				if (cEx.getCode() == MessageCodes.StatoOccupazionale.STATO_OCC_PREC_DATA_NORMATIVA_297) {
					addConfirm(request, response, cEx.getCode(), messaggioFail297);
					Vector paramV = new Vector(1);
					paramV.add(data297);
					reportOperation.reportFailure(
							MessageCodes.StatoOccupazionale.INSERISCI_STATO_OCC_PREC_NORMATIVA_297,
							"InsertStatoOccupaz.service", "Data inizio stato occupazionale non valida", paramV);
				}
			}
		}

		catch (Exception e) {
			if (transExec != null) {
				transExec.rollBackTransaction();
			}
			reportOperation.reportFailure(MessageCodes.General.INSERT_FAIL);
		}
	} // end service

	private void addConfirm(SourceBean request, SourceBean response, int code, String messageFail) throws Exception {
		ArrayList warnings = new ArrayList();
		// Vettore dei risultati annidati da restituire
		ArrayList nested = new ArrayList();
		SourceBean puResult = ProcessorsUtils.createResponse("", "", new Integer(code), "Situazione di incongruenza",
				warnings, nested);
		ProcessorsUtils.addConfirm(puResult, messageFail, "continuaInserimentoStatoOccPrec297", new String[] { "true" },
				true);
		SourceBean sb = new SourceBean("RECORD");
		sb.setAttribute("RESULT", "ERROR");
		sb.setAttribute(puResult);
		response.setAttribute(sb);
	}

}
