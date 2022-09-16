package it.eng.sil.module.trento;

import it.eng.afExt.utils.MessageCodes;
import it.eng.afExt.utils.ReportOperationResult;
import it.eng.afExt.utils.StringUtils;
import it.eng.afExt.utils.TransactionQueryExecutor;
import it.eng.sil.module.AbstractSimpleModule;
import it.eng.sil.module.agenda.AzioneList;

import com.engiweb.framework.base.SourceBean;

public class MAggiornaEditor  extends AbstractSimpleModule {
	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger
			.getLogger(AzioneList.class.getName());
	SourceBean row = null;
	
	public MAggiornaEditor() {
	}

	public void service(SourceBean request, SourceBean response) throws Exception {
		ReportOperationResult reportOperation = new ReportOperationResult(this, response);
		
		// Segnalazione soli errori/problemi
		int idSuccess = this.disableMessageIdSuccess();
		int idFail = this.disableMessageIdFail();
		boolean msgWarningReinvioSaap = false;
		TransactionQueryExecutor trans = null;

		try {
			trans = new TransactionQueryExecutor(getPool());
			this.enableTransactions(trans);
			trans.initTransaction();
			
			// Dopo ogni operazione raccoglie il risultato
			boolean result = true;
			
			String ambito = StringUtils.getAttributeStrNotNull(request, "ambito");
			String dominio = StringUtils.getAttributeStrNotNull(request, "dominio");
			String dataInizio = StringUtils.getAttributeStrNotNull(request, "DATAINIZIO");
			String dataFine = StringUtils.getAttributeStrNotNull(request, "DATAFINE");
			String note = StringUtils.getAttributeStrNotNull(request, "note");
			String numkloctem = StringUtils.getAttributeStrNotNull(request, "NUMKLOTEMP");
			_logger.debug("sil.module.trento.MAggiornaEditor "+ "::ambito:" + ambito);
			_logger.debug("sil.module.trento.MAggiornaEditor "+ "::dominio:" + dominio);
			_logger.debug("sil.module.trento.MAggiornaEditor "+ "::dataInizio:" + dataInizio);
			_logger.debug("sil.module.trento.MAggiornaEditor "+ "::dataFine:" + dataFine);
			_logger.debug("sil.module.trento.MAggiornaEditor "+ "::note:" + note);
			_logger.debug("sil.module.trento.MAggiornaEditor "+ "::numkloctem:" + numkloctem);
			setSectionQueryUpdate("AggiornaEditor");
			result = doUpdate(request, response);
			if (!result) {
				response.setAttribute("aggiornato", "false");
				reportOperation.reportFailure(MessageCodes.General.CONCORRENZA);
				trans.rollBackTransaction();
				return;
			}
			response.setAttribute("aggiornato", "true");
			reportOperation.reportSuccess(MessageCodes.General.OPERATION_SUCCESS);
	
			trans.commitTransaction();
			
		}
		catch (Throwable ex) {
			if (trans != null) {
				trans.rollBackTransaction();
			}
			it.eng.sil.util.TraceWrapper.debug(_logger, "MAggiornaEditor.service()", ex);
			reportOperation.reportFailure(MessageCodes.General.UPDATE_FAIL);
		}
	}
	
	
}
