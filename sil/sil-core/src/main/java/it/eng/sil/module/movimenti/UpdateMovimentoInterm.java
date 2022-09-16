package it.eng.sil.module.movimenti;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.error.EMFInternalError;

import it.eng.afExt.utils.MessageCodes;
import it.eng.afExt.utils.ReportOperationResult;
import it.eng.afExt.utils.TransactionQueryExecutor;
import it.eng.sil.module.AbstractSimpleModule;
import it.eng.sil.util.amministrazione.impatti.SituazioneAmministrativaFactory;
import it.eng.sil.util.amministrazione.impatti.StatoOccupazionaleBean;

public class UpdateMovimentoInterm extends AbstractSimpleModule {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(UpdateMovimentoInterm.class.getName());

	public void service(SourceBean serviceRequest, SourceBean serviceResponse) throws Exception {
		String dataInizioAttuale = (String) serviceRequest.getAttribute("datInizioAttuale");
		String cdnLavoratore = (String) serviceRequest.getAttribute("CDNLAVORATORE");
		TransactionQueryExecutor transExec = null;
		disableMessageIdFail();
		disableMessageIdSuccess();
		try {
			transExec = new TransactionQueryExecutor(getPool(), this);
			enableTransactions(transExec);
			transExec.initTransaction();

			setSectionQueryUpdate("QUERY_AGGIORNA_PERIODI_VALIDI");
			boolean res = doUpdate(serviceRequest, serviceResponse);
			if (!res) {
				throw new Exception("Errore aggiornamento periodi lavorativi validi");
			}
			if (!getRequestContainer().getServiceRequest().containsAttribute("FORZA_INSERIMENTO")) {
				getRequestContainer().getServiceRequest().setAttribute("FORZA_INSERIMENTO", "true");
			}
			if (!getRequestContainer().getServiceRequest().containsAttribute("CONTINUA_CALCOLO_SOCC")) {
				getRequestContainer().getServiceRequest().setAttribute("CONTINUA_CALCOLO_SOCC", "true");
			}
			if (!getRequestContainer().getServiceRequest().containsAttribute("FORZA_CHIUSURA_MOBILITA")) {
				getRequestContainer().getServiceRequest().setAttribute("FORZA_CHIUSURA_MOBILITA", "true");
			}

			StatoOccupazionaleBean statoOccupazionale = SituazioneAmministrativaFactory
					.newInstance(cdnLavoratore, dataInizioAttuale, transExec).calcolaImpatti();

			transExec.commitTransaction();
			ReportOperationResult reportOperation = new ReportOperationResult(this, serviceResponse);
			reportOperation.reportSuccess(MessageCodes.General.UPDATE_SUCCESS);
			serviceResponse.setAttribute("ESITO", "OK");

		} catch (Exception ex) {
			try {
				if (transExec != null) {
					transExec.rollBackTransaction();
					serviceResponse.setAttribute("ESITO", "NO");
				}
				ReportOperationResult reportOperation = new ReportOperationResult(this, serviceResponse);
				reportOperation.reportFailure(MessageCodes.General.OPERATION_FAIL);
			} catch (EMFInternalError ie) {
				it.eng.sil.util.TraceWrapper.debug(_logger, "service()", (Exception) ie);
			}
		}

	}

}