package it.eng.sil.module.anag;

import java.math.BigDecimal;

import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.utils.MessageCodes;
import it.eng.afExt.utils.ReportOperationResult;
import it.eng.afExt.utils.TransactionQueryExecutor;
import it.eng.sil.module.AbstractSimpleModule;

public class InsertServizio extends AbstractSimpleModule {
	public void service(SourceBean request, SourceBean response) throws Exception {
		// System.out.println("InsertColloquio chiamato");
		// if (1==1) return ;
		boolean ret = false;
		int idSuccess = this.disableMessageIdSuccess();
		int idFail = this.disableMessageIdFail();
		TransactionQueryExecutor transExec = null;
		ReportOperationResult reportOperation = new ReportOperationResult(this, response);

		try {

			transExec = new TransactionQueryExecutor(getPool());
			this.enableTransactions(transExec);

			transExec.initTransaction();

			int numRows = Integer.valueOf("" + request.getAttribute("numRows")).intValue();

			for (int i = 1; i <= numRows; i++) {

				String j = Integer.toString(i);
				request.delAttribute("codServizio");
				String codServizio = "" + request.getAttribute("codServizio" + j);

				if (!codServizio.equals("") && !codServizio.equals("null")) {

					BigDecimal prgProgSocioAss = doNextVal(request, response);

					if (prgProgSocioAss == null) {
						throw new Exception("Impossibile leggere S_CM_PROGETTO_SOCIOASS.NEXTVAL");
					}

					request.setAttribute("codServizio", codServizio);

					request.delAttribute("prgProgSocioAss");
					request.setAttribute("prgProgSocioAss", prgProgSocioAss.toString());
					this.setSectionQueryInsert("QUERY_INSERT_SERVIZI");
					ret = doInsert(request, response);

					if (!ret) {
						throw new Exception("impossibile inserire in CM_PROGETTO_SOCIOASSIST in transazione");
					}

					// response.delAttribute("prgProgSocioAss");
					// response.setAttribute("prgProgSocioAss",
					// prgProgSocioAss);

					// response.delAttribute("codServizio");
					// response.setAttribute("codServizio", codServizio);

				}
			}
			transExec.commitTransaction();

			reportOperation.reportSuccess(idSuccess);

		} catch (Exception e) {
			transExec.rollBackTransaction();
			// this.setMessageIdFail(MessageCodes.General.INSERT_FAIL);
			reportOperation.reportFailure(MessageCodes.General.INSERT_FAIL, e, "services()", "insert in transazione");
		} finally {
		}
	}
}
