/*
 * Creato il 13-set-05
 *
 * Per modificare il modello associato a questo file generato, aprire
 * Finestra&gt;Preferenze&gt;Java&gt;Generazione codice&gt;Codice e commenti
 */
package it.eng.sil.module.amministrazione;

import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.utils.MessageCodes;
import it.eng.afExt.utils.ReportOperationResult;
import it.eng.afExt.utils.TransactionQueryExecutor;
import it.eng.sil.module.AbstractSimpleModule;

/**
 * @author togna
 * 
 *         Per modificare il modello associato al commento di questo tipo generato, aprire
 *         Finestra&gt;Preferenze&gt;Java&gt;Generazione codice&gt;Codice e commenti
 */
public class DeleteCollocamentoMirato extends AbstractSimpleModule {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger
			.getLogger(DeleteCollocamentoMirato.class.getName());
	private TransactionQueryExecutor transactionExecutor;

	/*
	 * (non Javadoc)
	 * 
	 * @see com.engiweb.framework.dispatching.service.ServiceIFace#service(com.engiweb.framework.base.SourceBean,
	 * com.engiweb.framework.base.SourceBean)
	 */
	public void service(SourceBean serviceRequest, SourceBean serviceResponse) throws Exception {
		ReportOperationResult reportOperation = new ReportOperationResult(this, serviceResponse);
		try {
			transactionExecutor = new TransactionQueryExecutor(getPool(), this);
			transactionExecutor.initTransaction();
			enableTransactions(transactionExecutor);

			// query che annulla la did
			setSectionQueryDelete("QUERY");
			if (!doDelete(serviceRequest, serviceResponse)) {
				throw new Exception("Impossibile eliminare il collocamento mirato");
			}

			// ricalcolo impatti
			// attualmente non previsto
			transactionExecutor.commitTransaction();

			// reportOperation.reportSuccess(MessageCodes.General.OPERATION_SUCCESS);
			reportOperation.reportSuccess(MessageCodes.General.CALCOLO_IMPATTI_MANUALE);
		} catch (Exception e) {
			transactionExecutor.rollBackTransaction();
			reportOperation.reportFailure(MessageCodes.General.OPERATION_FAIL, e, "service", "");
			it.eng.sil.util.TraceWrapper.debug(_logger, "RiapriDid.service()", e);

		}
	}
}