package it.eng.sil.module.presel;

import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.utils.ReportOperationResult;
import it.eng.afExt.utils.TransactionQueryExecutor;
import it.eng.sil.module.AbstractSimpleModule;

public class UpdateValidCur extends AbstractSimpleModule {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(UpdateValidCur.class.getName());

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

			this.setSectionQueryUpdate("QUERY_UPDATE");
			ret = doUpdate(request, response);
			if (ret) {
				transExec.commitTransaction();
				reportOperation.reportSuccess(idSuccess);
			} else {
				_logger.debug(
						this.getClass().getName() + "::service " + "Errore durante aggiornamento validità curriculum ");

				transExec.rollBackTransaction();
			}
		}

		catch (Exception ex) {
			it.eng.sil.util.TraceWrapper.debug(_logger,
					this.getClass().getName() + "::service " + "Errore durante aggiornamento validità curriculum: ",
					ex);

			transExec.rollBackTransaction();
		}
	}
}