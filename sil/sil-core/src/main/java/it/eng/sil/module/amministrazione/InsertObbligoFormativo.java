package it.eng.sil.module.amministrazione;

import java.math.BigDecimal;

import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.utils.MessageCodes;
import it.eng.afExt.utils.ReportOperationResult;
import it.eng.afExt.utils.TransactionQueryExecutor;
import it.eng.sil.module.AbstractSimpleModule;
import it.eng.sil.module.patto.PattoManager;

public class InsertObbligoFormativo extends AbstractSimpleModule {
	public void service(SourceBean request, SourceBean response) throws Exception {
		boolean ret = false;

		if (PattoManager.withPatto(request)) {
			ReportOperationResult reportOperation = new ReportOperationResult(this, response);
			TransactionQueryExecutor transExec = null;
			int idSuccess = this.disableMessageIdSuccess();
			int idFail = this.disableMessageIdFail();

			try {
				transExec = new TransactionQueryExecutor(getPool());

				PattoManager patto = new PattoManager(this, transExec);
				transExec.initTransaction();
				// this.setSectionQuerySelect("GET_NEW_AM_INDISP_TEMP");

				BigDecimal prgObbFo = getPrgObbFo(request, response);
				// setParameterForIndisp(prgIndispTemp, request);
				// this.setSectionQueryInsert("INSERT_IND_T");
				ret = this.doInsert(request, response);

				if (ret) {
					if (request.getAttribute("PRG_TAB") != null) {
						request.delAttribute("PRG_TAB");
					}

					request.setAttribute("PRG_TAB", prgObbFo);
					ret = patto.execute(request, response);
				} else {
					throw new Exception("");
				}

				//
				if (ret) {
					transExec.commitTransaction();
					this.setMessageIdSuccess(idSuccess);
					this.setMessageIdFail(idFail);
					reportOperation.reportSuccess(idSuccess);
				} else {
					throw new Exception();
				}
			} catch (Exception e) {
				// this.setMessageIdFail(MessageCodes.General.INSERT_FAIL);
				reportOperation.reportFailure(MessageCodes.General.INSERT_FAIL);

				if (transExec != null) {
					transExec.rollBackTransaction();
				}
			}
		} else {
			int idSuccess = this.disableMessageIdSuccess();

			// int idFail = this.disableMessageIdFail();
			BigDecimal prgObbFo = getPrgObbFo(request, response);

			if (prgObbFo != null) {
				// setParameterForIndisp(prgIndispTemp, request);
				this.setMessageIdSuccess(idSuccess);

				// setSectionQueryInsert("INSERT_IND_T");
				doInsert(request, response);
			}
		}
	}

	private BigDecimal getPrgObbFo(SourceBean request, SourceBean response) {
		/*
		 * this.setSectionQuerySelect("GET_NEW_AM_INDISP_TEMP");
		 * 
		 * SourceBean sb = doSelect(request, response);
		 * 
		 * if (sb != null) { return (BigDecimal) sb.getAttribute("ROW.PRGINDISPTEMP"); } else { return null; }
		 */
		return (new BigDecimal((String) request.getAttribute("CDNLAVORATORE")));
	}
	/*
	 * private void setParameterForIndisp(BigDecimal pk, SourceBean request) throws Exception { if
	 * (request.getAttribute("prgIndispTemp") != null) { request.delAttribute("prgIndispTemp"); }
	 * 
	 * request.setAttribute("prgIndispTemp", pk); }
	 */
}

// end service
// end class SaveIndispTemp
