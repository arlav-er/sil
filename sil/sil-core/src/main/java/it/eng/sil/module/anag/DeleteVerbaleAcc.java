package it.eng.sil.module.anag;

import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.utils.MessageCodes;
import it.eng.afExt.utils.ReportOperationResult;
import it.eng.afExt.utils.TransactionQueryExecutor;
import it.eng.sil.module.AbstractSimpleModule;

public class DeleteVerbaleAcc extends AbstractSimpleModule {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(DeleteVerbaleAcc.class.getName());
	private String className = this.getClass().getName();
	private static final String TRUE = "TRUE";
	private int messageIdFail = MessageCodes.General.OPERATION_FAIL;

	public void service(SourceBean request, SourceBean response) throws Exception {

		ReportOperationResult reportOperation = new ReportOperationResult(this, response);

		int idSuccess = this.disableMessageIdSuccess();
		int idFail = this.disableMessageIdFail();
		boolean errors = false;
		TransactionQueryExecutor transExec = null;
		try {
			transExec = new TransactionQueryExecutor(getPool());
			enableTransactions(transExec);
			transExec.initTransaction();
			setSectionQuerySelect("QUERY_SELECT");
			SourceBean iscrCm = this.doSelect(request, response);
			if (!iscrCm.containsAttribute("ROW")) {
				setSectionQueryDelete("QUERY_DELETE");
				boolean ok = this.doDelete(request, response);
				if (!ok) {
					throw new Exception("Impossibile eliminare il verbale");
				}

				reportOperation.reportSuccess(idSuccess);
			} else {
				_logger.debug("DeleteVerbaleAcc::service(): Impossibile eliminare il verbale!");

				reportOperation.reportFailure(MessageCodes.CollocamentoMirato.ERROR_DELETE_VERBALE);
			}

			transExec.commitTransaction();

		} catch (Exception ex) {
			it.eng.sil.util.TraceWrapper.debug(_logger,
					"DeleteVerbaleAcc::service(): Impossibile eliminare il verbale!", ex);

			reportOperation.reportFailure(idFail);
			reportOperation.reportFailure(MessageCodes.General.INSERT_FAIL);
			if (transExec != null) {
				transExec.rollBackTransaction();
			}
			errors = true;
		}

	}

}
