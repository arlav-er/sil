package it.eng.sil.module.presel;

import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.utils.MessageCodes;
import it.eng.afExt.utils.ReportOperationResult;
import it.eng.afExt.utils.TransactionQueryExecutor;
import it.eng.sil.module.AbstractSimpleModule;

/*
 * 
 * @author rolfini
 *
 */

public class MCancTipoEvidenza extends AbstractSimpleModule {
	public void service(SourceBean request, SourceBean response) throws Exception {

		int idSuccess = this.disableMessageIdSuccess();
		int idFail = this.disableMessageIdFail();

		ReportOperationResult result = new ReportOperationResult(this, response);

		boolean utilizzato = false;

		TransactionQueryExecutor transExec = null;

		// verifico che non sia già stato usato
		this.setSectionQuerySelect("QUERY_VERIFICA_UTILIZZO");
		SourceBean tmp = doSelect(request, response);
		if ((tmp != null) && (tmp.getAttribute("ROW.prgTipoEvidenza") != null)) { // è
																					// utilizzato
			utilizzato = true;
			result.reportFailure(MessageCodes.Evidenze.EVID_DEL_FAILURE);
		}

		if (!utilizzato) {
			try {
				transExec = new TransactionQueryExecutor(getPool());
				enableTransactions(transExec);
				boolean ok = true;

				transExec.initTransaction();
				// cancello la visibilità dell'evidenza
				this.setSectionQueryDelete("QUERY_DELETE_VIS");
				ok = doDelete(request, response);
				if (ok) { // se sono riuscito a cancellare la visibilità,
							// cancello l'evidenza
					this.setSectionQueryDelete("QUERY_DELETE");
					ok = doDelete(request, response);
				}

				if (ok) {
					transExec.commitTransaction();
					result.reportSuccess(idSuccess);
				} else {
					result.reportFailure(MessageCodes.Evidenze.EVID_DEL_FAILURE_VIS);
					transExec.rollBackTransaction();
				}
			} catch (Exception ex) {
				if (transExec != null) {
					transExec.rollBackTransaction();
					result.reportFailure(MessageCodes.General.DELETE_FAIL);
				}

			}
		}

	}

}
