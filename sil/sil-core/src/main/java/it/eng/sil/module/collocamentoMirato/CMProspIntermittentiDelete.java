package it.eng.sil.module.collocamentoMirato;

import java.math.BigDecimal;

import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.utils.MessageCodes;
import it.eng.afExt.utils.ReportOperationResult;
import it.eng.afExt.utils.TransactionQueryExecutor;
import it.eng.sil.module.AbstractSimpleModule;

public class CMProspIntermittentiDelete extends AbstractSimpleModule {

	public void service(SourceBean serviceRequest, SourceBean serviceResponse) throws Exception {

		boolean check = true;
		int idSuccess = this.disableMessageIdSuccess();
		int msgCode = MessageCodes.General.DELETE_FAIL;
		TransactionQueryExecutor transExec = null;
		ReportOperationResult reportOperation = new ReportOperationResult(this, serviceResponse);
		try {
			transExec = new TransactionQueryExecutor(getPool());
			this.enableTransactions(transExec);

			transExec.initTransaction();

			check = doDelete(serviceRequest, serviceResponse);
			if (!check) {
				throw new Exception("Errore durante la cancellazione del record. Operazione interrotta");
			}

			SourceBean sb = null;
			BigDecimal numinterm = new BigDecimal(0);

			this.setSectionQuerySelect("GET_INTERMITTENTI");
			sb = doSelect(serviceRequest, serviceResponse);
			if (sb != null) {
				numinterm = (BigDecimal) sb.getAttribute("ROW.NUMINTERM");
				if (numinterm == null) {
					check = false;
					throw new Exception("Operazione interrotta");
				}
			}

			serviceRequest.delAttribute("NUMINTERM");
			serviceRequest.setAttribute("NUMINTERM", numinterm.toString());

			this.setSectionQueryUpdate("CALCOLA_INTERMITTENTI");
			check = doUpdate(serviceRequest, serviceResponse);
			if (!check) {
				throw new Exception(
						"Errore durante l'aggiornamento del numero di intermittenti. Operazione interrotta");
			}

			transExec.commitTransaction();

			reportOperation.reportSuccess(idSuccess);

		} catch (Exception e) {
			transExec.rollBackTransaction();
			reportOperation.reportFailure(msgCode, e, "services()", "delete in transazione");

		} finally {
		}

	}

}
