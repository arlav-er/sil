/*
 * Creato il 7-giu-06
 *
 * Per modificare il modello associato a questo file generato, aprire
 * Finestra&gt;Preferenze&gt;Java&gt;Generazione codice&gt;Codice e commenti
 */
package it.eng.sil.module.amministrazione;

import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.utils.MessageCodes;
import it.eng.afExt.utils.ReportOperationResult;
import it.eng.afExt.utils.StringUtils;
import it.eng.afExt.utils.TransactionQueryExecutor;
import it.eng.sil.module.AbstractSimpleModule;
import it.eng.sil.util.amministrazione.impatti.SituazioneAmministrativaFactory;
import it.eng.sil.util.amministrazione.impatti.StatoOccupazionaleBean;

/**
 * @author landi
 * 
 *         Per modificare il modello associato al commento di questo tipo generato, aprire
 *         Finestra&gt;Preferenze&gt;Java&gt;Generazione codice&gt;Codice e commenti
 */
public class DeleteMobilitaRicalcolo extends AbstractSimpleModule {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(DeleteMobilitaRicalcolo.class.getName());
	private TransactionQueryExecutor transactionExecutor;

	public void service(SourceBean serviceRequest, SourceBean serviceResponse) throws Exception {
		ReportOperationResult reportOperation = new ReportOperationResult(this, serviceResponse);
		int idSuccess = this.disableMessageIdSuccess();
		int idFail = this.disableMessageIdFail();
		try {
			transactionExecutor = new TransactionQueryExecutor(getPool(), this);
			transactionExecutor.initTransaction();
			enableTransactions(transactionExecutor);
			setSectionQueryDelete("QUERY");
			if (!doDelete(serviceRequest, serviceResponse)) {
				throw new Exception("Impossibile eliminare la mobilità");
			}
			// forzature in ricalcolo impatti
			if (!serviceRequest.containsAttribute("FORZA_INSERIMENTO"))
				serviceRequest.setAttribute("FORZA_INSERIMENTO", "true");
			else
				serviceRequest.updAttribute("FORZA_INSERIMENTO", "true");
			if (!serviceRequest.containsAttribute("CONTINUA_CALCOLO_SOCC"))
				serviceRequest.setAttribute("CONTINUA_CALCOLO_SOCC", "true");
			else
				serviceRequest.updAttribute("CONTINUA_CALCOLO_SOCC", "true");
			// ricalcolo impatti
			StatoOccupazionaleBean statoOccupazionale = SituazioneAmministrativaFactory
					.newInstance(StringUtils.getAttributeStrNotNull(serviceRequest, "cdnLavoratore"),
							StringUtils.getAttributeStrNotNull(serviceRequest, "datInizio"), transactionExecutor)
					.calcolaImpatti();

			transactionExecutor.commitTransaction();
			this.setMessageIdSuccess(idSuccess);
			reportOperation.reportSuccess(MessageCodes.General.OPERATION_SUCCESS);
			// reportOperation.reportSuccess(MessageCodes.General.CALCOLO_IMPATTI_MANUALE);
		} catch (Exception e) {
			transactionExecutor.rollBackTransaction();
			this.setMessageIdFail(idFail);
			reportOperation.reportFailure(MessageCodes.General.OPERATION_FAIL, e, "service", "");
			it.eng.sil.util.TraceWrapper.debug(_logger, "DeleteMobilita.service()", e);

		}
	}

}