package it.eng.sil.module.amministrazione;

import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.utils.MessageCodes;
import it.eng.afExt.utils.ReportOperationResult;
import it.eng.afExt.utils.TransactionQueryExecutor;
import it.eng.sil.module.AbstractSimpleModule;
import it.eng.sil.module.patto.PattoManager;

public class DeleteIndispTemp extends AbstractSimpleModule {

	public void service(SourceBean request, SourceBean response) throws Exception {
		TransactionQueryExecutor transExec = null;
		boolean ret = false;
		boolean scorrimento = false;
		ReportOperationResult reportOperation = new ReportOperationResult(this, response);
		int idSuccess = this.disableMessageIdSuccess();
		int idFail = this.disableMessageIdFail();
		String codiceIndisp = "";
		String cdnLav = request.containsAttribute("cdnLavoratore") ? request.getAttribute("cdnLavoratore").toString()
				: "";
		String datInizioIndisp = "";
		setSectionQuerySelect("GET_INDISP_DETT");
		SourceBean rowIndisp = doSelect(request, response);
		if (rowIndisp != null) {
			codiceIndisp = rowIndisp.containsAttribute("row.codindisptemp")
					? rowIndisp.getAttribute("row.codindisptemp").toString()
					: "";
			datInizioIndisp = rowIndisp.containsAttribute("row.datInizio")
					? rowIndisp.getAttribute("row.datInizio").toString()
					: "";
		}
		if (PattoManager.withPatto(request)) {
			try {
				transExec = new TransactionQueryExecutor(getPool());
				enableTransactions(transExec);
				PattoManager patto = new PattoManager(this, transExec);
				transExec.initTransaction();
				this.setSectionQueryDelete("DELETE_IND_T");
				ret = this.doDelete(request, response);
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
						reportOperation.reportSuccess(MessageCodes.ScorrimentoMobilita.DELETE_MATERNITA);
					}
				} else {
					throw new Exception();
				}
			} catch (Exception e) {
				reportOperation.reportFailure(MessageCodes.General.DELETE_FAIL);
				if (transExec != null)
					transExec.rollBackTransaction();
			}
		} else {
			try {
				transExec = new TransactionQueryExecutor(getPool());
				enableTransactions(transExec);
				transExec.initTransaction();
				setSectionQueryDelete("DELETE_IND_T");
				ret = doDelete(request, response);
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
					reportOperation.reportSuccess(MessageCodes.ScorrimentoMobilita.DELETE_MATERNITA);
				}
			} catch (Exception e) {
				reportOperation.reportFailure(MessageCodes.General.DELETE_FAIL);
				if (transExec != null) {
					transExec.rollBackTransaction();
				}
			}
		}
	}

}
