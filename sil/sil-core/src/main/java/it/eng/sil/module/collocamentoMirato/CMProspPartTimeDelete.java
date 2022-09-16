package it.eng.sil.module.collocamentoMirato;

import java.math.BigDecimal;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.SessionContainer;
import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.utils.MessageCodes;
import it.eng.afExt.utils.ReportOperationResult;
import it.eng.afExt.utils.StringUtils;
import it.eng.afExt.utils.TransactionQueryExecutor;
import it.eng.sil.module.AbstractSimpleModule;

public class CMProspPartTimeDelete extends AbstractSimpleModule {

	/*
	 * (non Javadoc)
	 * 
	 * @see com.engiweb.framework.dispatching.service.ServiceIFace#service(com.engiweb.framework.base.SourceBean,
	 * com.engiweb.framework.base.SourceBean)
	 */
	public void service(SourceBean serviceRequest, SourceBean serviceResponse) throws Exception {

		RequestContainer requestContainer = getRequestContainer();
		SessionContainer sessionContainer = requestContainer.getSessionContainer();

		boolean check = true;
		int idSuccess = this.disableMessageIdSuccess();
		int idFail = this.disableMessageIdFail();
		int msgCode = MessageCodes.General.DELETE_FAIL;
		TransactionQueryExecutor transExec = null;
		ReportOperationResult reportOperation = new ReportOperationResult(this, serviceResponse);
		try {
			transExec = new TransactionQueryExecutor(getPool());
			this.enableTransactions(transExec);

			transExec.initTransaction();

			String prgProspettoInf = StringUtils.getAttributeStrNotNull(serviceRequest, "PRGPROSPETTOINF");
			String prgPartTimeProspetto = StringUtils.getAttributeStrNotNull(serviceRequest, "PRGPARTTIMEPROSPETTO");

			check = doDelete(serviceRequest, serviceResponse);
			if (!check) {
				throw new Exception("Errore durante la cancellazioen del record. Operazione interrotta");
			}

			SourceBean pippo = null;
			BigDecimal numpt = new BigDecimal(0);

			this.setSectionQuerySelect("GET_PARTTIME");
			pippo = doSelect(serviceRequest, serviceResponse);
			if (pippo != null) {
				numpt = (BigDecimal) pippo.getAttribute("ROW.NUMPT");
				if (numpt == null) {
					check = false;
					throw new Exception("Operazione interrotta");
				}
			}

			serviceRequest.delAttribute("NUMPT");
			serviceRequest.setAttribute("NUMPT", numpt.toString());

			this.setSectionQueryUpdate("CALCOLA_PARTTIME");
			check = doUpdate(serviceRequest, serviceResponse);
			if (!check) {
				throw new Exception("Errore durante l'aggiornamento del numero di part-time. Operazione interrotta");
			}

			transExec.commitTransaction();

			reportOperation.reportSuccess(idSuccess);

		} catch (Exception e) {
			transExec.rollBackTransaction();
			reportOperation.reportFailure(msgCode, e, "services()", "insert in transazione");

		} finally {
		}

	}

}
