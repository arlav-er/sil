package it.eng.sil.anag.module;

import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.utils.MessageCodes;
import it.eng.afExt.utils.ReportOperationResult;
import it.eng.afExt.utils.TransactionQueryExecutor;
import it.eng.sil.module.AbstractSimpleModule;

/**
 * Classe che gestisce l'eliminazione di un record dal nucleo familiare collegato ad un utente. Utilizzata dal modulo
 * M_DelNucleoFamiliare
 * 
 * @author Giacomo Pandini
 */

public class DeleteNucleoFamiliare extends AbstractSimpleModule {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(DeleteNucleoFamiliare.class.getName());

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
			setSectionQueryDelete("QUERY_DELETE");
			boolean ok = this.doDelete(request, response);
			if (!ok) {
				throw new Exception("Impossibile eliminare il record");
			}
			reportOperation.reportSuccess(idSuccess);

			transExec.commitTransaction();

		} catch (Exception ex) {
			it.eng.sil.util.TraceWrapper.debug(_logger,
					"DeleteNucleoFamiliare::service(): Impossibile eliminare il record!", ex);

			reportOperation.reportFailure(idFail);
			reportOperation.reportFailure(MessageCodes.General.INSERT_FAIL);
			if (transExec != null) {
				transExec.rollBackTransaction();
			}
			errors = true;
		}

	}
}
