package it.eng.sil.module.presel;

import java.util.Vector;

import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.utils.MessageCodes;
import it.eng.afExt.utils.ReportOperationResult;
import it.eng.afExt.utils.TransactionQueryExecutor;
import it.eng.sil.module.AbstractSimpleModule;

public class InsertValidCur extends AbstractSimpleModule {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(InsertValidCur.class.getName());

	public void service(SourceBean request, SourceBean response) throws Exception {
		boolean ret = false;
		ReportOperationResult reportOperation = new ReportOperationResult(this, response);
		int idSuccess = this.disableMessageIdSuccess();
		int idFail = this.disableMessageIdFail();
		TransactionQueryExecutor transExec = null;

		try {
			transExec = new TransactionQueryExecutor(getPool());
			enableTransactions(transExec);
			transExec.initTransaction();

			this.setSectionQuerySelect("QUERY_SELECT");
			SourceBean beanSelect = doSelect(request, response, false);
			Vector vect = beanSelect.getAttributeAsVector("ROW");
			boolean isElemEsistente = (beanSelect != null) && (vect.size() > 0);

			if (isElemEsistente) {
				_logger.fatal(this.getClass().getName() + "::service: fallito inserimento validità curriculum ");

				transExec.rollBackTransaction();
				reportOperation.reportFailure(MessageCodes.General.INSERT_FAIL);
				reportOperation.reportFailure(MessageCodes.General.ELEMENT_DUPLICATED);
				response.setAttribute("ERR_INSERT_CURR", "TRUE");
			} else {
				this.setSectionQueryInsert("QUERY_INSERT");
				ret = doInsert(request, response);
				if (ret) {
					transExec.commitTransaction();
					reportOperation.reportSuccess(idSuccess);
				} else {
					_logger.debug(this.getClass().getName() + "::service "
							+ "Errore durante inserimento validità curriculum");

					transExec.rollBackTransaction();
				}
			}
		} catch (Exception ex) {
			it.eng.sil.util.TraceWrapper.debug(_logger,
					this.getClass().getName() + "::service " + "Errore durante inserimento validità curriculum: ", ex);

			transExec.rollBackTransaction();
		}
	}
}