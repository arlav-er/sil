package it.eng.sil.module.movimenti;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.error.EMFInternalError;

import it.eng.afExt.utils.DateUtils;
import it.eng.afExt.utils.MessageCodes;
import it.eng.afExt.utils.ReportOperationResult;
import it.eng.afExt.utils.StringUtils;
import it.eng.afExt.utils.TransactionQueryExecutor;
import it.eng.sil.module.AbstractSimpleModule;
import it.eng.sil.util.amministrazione.impatti.SituazioneAmministrativaFactory;
import it.eng.sil.util.amministrazione.impatti.StatoOccupazionaleBean;

public class DeletePeriodoInterm extends AbstractSimpleModule {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(DeletePeriodoInterm.class.getName());

	public void service(SourceBean request, SourceBean response) throws Exception {
		String periodoValido = StringUtils.getAttributeStrNotNull(request, "VALIDO");
		String datInizioMov = StringUtils.getAttributeStrNotNull(request, "DATINIZIOMOVINTERM");
		String cdnLavoratore = (String) request.getAttribute("CDNLAVORATORE");
		if (periodoValido.equalsIgnoreCase("N")) {
			// cancellazione senza ricalcolo
			doDelete(request, response);
		} else {
			// cancellazione con ricalcolo
			ReportOperationResult reportOperation = new ReportOperationResult(this, response);
			disableMessageIdSuccess();
			disableMessageIdFail();
			TransactionQueryExecutor transExec = null;
			try {
				transExec = new TransactionQueryExecutor(getPool(), this);
				enableTransactions(transExec);
				transExec.initTransaction();
				doDelete(request, response);

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
						.newInstance(cdnLavoratore, DateUtils.giornoPrecedente(datInizioMov), transExec)
						.calcolaImpatti();

				transExec.commitTransaction();
				reportOperation.reportSuccess(MessageCodes.General.UPDATE_SUCCESS);
				response.setAttribute("ESITO", "OK");
			} catch (Exception ex) {
				try {
					if (transExec != null) {
						transExec.rollBackTransaction();
						response.setAttribute("ESITO", "NO");
					}
					reportOperation.reportFailure(MessageCodes.General.OPERATION_FAIL);
				} catch (EMFInternalError ie) {
					it.eng.sil.util.TraceWrapper.debug(_logger, "service()", (Exception) ie);
				}
			}
		}
	}

}
