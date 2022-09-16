/*
 * Created on Oct 10, 2005
 *
 */
package it.eng.sil.module.amministrazione;

import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.utils.MessageCodes;
import it.eng.afExt.utils.ReportOperationResult;
import it.eng.afExt.utils.TransactionQueryExecutor;
import it.eng.sil.module.AbstractSimpleModule;
import it.eng.sil.module.patto.PattoManager;

/**
 * @author Savino
 */
public class DeletePermSoggiorno extends AbstractSimpleModule {

	public void service(SourceBean request, SourceBean response) throws Exception {
		if (PattoManager.withPatto(request)) {
			ReportOperationResult reportOperation = new ReportOperationResult(this, response);
			TransactionQueryExecutor transExec = null;
			int idSuccess = this.disableMessageIdSuccess();
			int idFail = this.disableMessageIdFail();
			try {
				transExec = new TransactionQueryExecutor(getPool());
				// l'abilitazione della transazione per il modulo avviene nel
				// costruttore del patto manager
				PattoManager patto = new PattoManager(this, transExec);
				transExec.initTransaction();
				if (doDelete(request, response) && patto.execute(request, response)) {
					transExec.commitTransaction();
					// this.setMessageIdSuccess();
					// this.setMessageIdFail(idFail);
					reportOperation.reportSuccess(MessageCodes.General.DELETE_SUCCESS);
				} else
					throw new Exception(
							"Impossibile cancellare in transazione il permesso di soggiorno ed il suo legame col patto");
			} catch (Exception e) {
				reportOperation.reportFailure(MessageCodes.General.DELETE_FAIL);
				if (transExec != null)
					transExec.rollBackTransaction();
			}
		} else {// il legame col patto non c'e': basta cancellare il record
				// senza transazioni
			setMessageIdSuccess(MessageCodes.General.DELETE_SUCCESS);
			doDelete(request, response);
		}
	}
}
