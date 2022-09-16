package it.eng.sil.module.amministrazione;

import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.utils.MessageCodes;
import it.eng.afExt.utils.ReportOperationResult;
import it.eng.afExt.utils.TransactionQueryExecutor;
import it.eng.sil.module.AbstractSimpleModule;
import it.eng.sil.module.patto.PattoManager;

public class SaveIndispTemp extends AbstractSimpleModule {

	public void service(SourceBean request, SourceBean response) throws Exception {
		boolean ret = false;
		TransactionQueryExecutor transExec = null;
		ReportOperationResult reportOperation = new ReportOperationResult(this, response);
		int idSuccess = this.disableMessageIdSuccess();
		int idFail = this.disableMessageIdFail();
		boolean scorrimento = false;
		String codiceIndisp = request.containsAttribute("codIndTemp") ? request.getAttribute("codIndTemp").toString()
				: "";
		String cdnLav = request.containsAttribute("cdnLavoratore") ? request.getAttribute("cdnLavoratore").toString()
				: "";
		String datInizioIndisp = request.containsAttribute("datInizio") ? request.getAttribute("datInizio").toString()
				: "";
		if (PattoManager.withPatto(request)) {
			try {
				transExec = new TransactionQueryExecutor(getPool());
				enableTransactions(transExec);
				PattoManager patto = new PattoManager(this, transExec);
				transExec.initTransaction();
				this.setSectionQueryUpdate("UPDATE_IND_T");
				ret = this.doUpdate(request, response);
				if (ret) {
					ret = patto.execute(request, response);
				} else {
					throw new Exception("");
				}
				//
				if (ret) {
					// chiamata per eventuale scorrimento
					scorrimento = UtilsMobilita.scorrimentoPerIndisp(request, codiceIndisp, cdnLav, datInizioIndisp,
							transExec);
					transExec.commitTransaction();
					this.setMessageIdSuccess(idSuccess);
					this.setMessageIdFail(idFail);
					reportOperation.reportSuccess(idSuccess);
					if (scorrimento) {
						reportOperation.reportSuccess(MessageCodes.ScorrimentoMobilita.MATERNITA);
					}
				} else {
					throw new Exception();
				}
			} catch (Exception e) {
				reportOperation.reportFailure(MessageCodes.General.UPDATE_FAIL);
				if (transExec != null)
					transExec.rollBackTransaction();
			}
		} else {
			try {
				transExec = new TransactionQueryExecutor(getPool());
				enableTransactions(transExec);
				transExec.initTransaction();
				setSectionQueryUpdate("UPDATE_IND_T");
				ret = doUpdate(request, response);
				if (!ret) {
					throw new Exception();
				}
				// chiamata per eventuale scorrimento
				scorrimento = UtilsMobilita.scorrimentoPerIndisp(request, codiceIndisp, cdnLav, datInizioIndisp,
						transExec);
				transExec.commitTransaction();
				this.setMessageIdSuccess(idSuccess);
				this.setMessageIdFail(idFail);
				reportOperation.reportSuccess(idSuccess);
				if (scorrimento) {
					reportOperation.reportSuccess(MessageCodes.ScorrimentoMobilita.MATERNITA);
				}
			} catch (Exception e) {
				reportOperation.reportFailure(MessageCodes.General.UPDATE_FAIL);
				if (transExec != null) {
					transExec.rollBackTransaction();
				}
			}
		}
	}
	// end service
}

// end class SaveIndispTemp
