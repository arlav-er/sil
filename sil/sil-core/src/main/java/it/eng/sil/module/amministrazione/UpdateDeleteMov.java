package it.eng.sil.module.amministrazione;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.ResponseContainer;
import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.utils.MessageCodes;
import it.eng.afExt.utils.ReportOperationResult;
import it.eng.afExt.utils.TransactionQueryExecutor;
import it.eng.sil.module.AbstractSimpleModule;
import it.eng.sil.util.Utils;

public class UpdateDeleteMov extends AbstractSimpleModule {

	private TransactionQueryExecutor transExec;

	public void service(SourceBean request, SourceBean response) throws Exception {

		RequestContainer reqCont = getRequestContainer();
		ResponseContainer resCont = getResponseContainer();
		int idSuccess = this.disableMessageIdSuccess();
		int idFail = this.disableMessageIdFail();
		boolean ret = false;
		ReportOperationResult reportOperation = new ReportOperationResult(this, response);

		try {
			transExec = new TransactionQueryExecutor(getPool());
			this.enableTransactions(transExec);

			transExec.initTransaction();

			String prgMovimento = Utils.notNull(request.getAttribute("prgMovimento"));
			String strcodicefiscale = Utils.notNull(request.getAttribute("STRCODICEFISCALE"));

			if (!prgMovimento.equals("")) {
				// setSectionQueryUpdate("QUERY_UPDATE_PRGSUCC");

				SourceBean queryUpdateSucc = (SourceBean) getConfig().getAttribute("QUERY_UPDATE_PRGSUCC");
				Boolean ok = (Boolean) transExec.executeQuery(reqCont, resCont, queryUpdateSucc, "UPDATE");
				if (!ok.booleanValue()) {
					throw new Exception("ERRORE ESEGUENDO L'AGGIORNAMENTO DEL SUCCESSIVO");
				} else {
					SourceBean queryUpdatePrec = (SourceBean) getConfig().getAttribute("QUERY_UPDATE_PRGPREC");
					Boolean ok2 = (Boolean) transExec.executeQuery(reqCont, resCont, queryUpdatePrec, "UPDATE");
					if (!ok2.booleanValue()) {
						throw new Exception("ERRORE ESEGUENDO L'AGGIORNAMENTO DEL PRECEDENTE");
					} else {
						SourceBean queryUpdateDoc = (SourceBean) getConfig().getAttribute("QUERY_ANNULLA_DOC");
						Boolean okDoc = (Boolean) transExec.executeQuery(reqCont, resCont, queryUpdateDoc, "UPDATE");
						if (!okDoc.booleanValue()) {
							throw new Exception("ERRORE ESEGUENDO L'ANNULLAMENTO DEL DOCUMENTO");
						} else {
							SourceBean queryUpdateMov = (SourceBean) getConfig().getAttribute("QUERY_ANNULLA_MOV");
							Boolean okMov = (Boolean) transExec.executeQuery(reqCont, resCont, queryUpdateMov,
									"UPDATE");
							if (!okMov.booleanValue()) {
								throw new Exception("ERRORE ESEGUENDO L'ANNULLAMENTO DEL MOVIMENTO");
							}
						}
					}
				}

				request.setAttribute("strcodicefiscale", strcodicefiscale);
				this.setMessageIdSuccess(MessageCodes.General.UPDATE_SUCCESS);
				transExec.commitTransaction();
				reportOperation.reportSuccess(idSuccess);

				/*
				 * ret = doUpdate(request, response); if (!ret){ transExec.rollBackTransaction();
				 * this.setMessageIdFail(MessageCodes.General.UPDATE_FAIL); return; }
				 * 
				 * setSectionQueryUpdate("QUERY_UPDATE_PRGPREC"); ret = doUpdate(request, response); if (!ret){
				 * transExec.rollBackTransaction(); this.setMessageIdFail(MessageCodes.General.UPDATE_FAIL); return; }
				 * 
				 * setSectionQueryUpdate("QUERY_ANNULLA_DOC"); ret = doUpdate(request, response);
				 * 
				 * if (!ret){ transExec.rollBackTransaction(); this.setMessageIdFail(MessageCodes.General.UPDATE_FAIL);
				 * return; }
				 * 
				 * setSectionQueryUpdate("QUERY_ANNULLA_MOV"); ret = doUpdate(request, response);
				 * 
				 * 
				 * if(ret){ request.setAttribute("strcodicefiscale", strcodicefiscale); transExec.commitTransaction();
				 * this.setMessageIdSuccess(MessageCodes.General.UPDATE_SUCCESS);
				 * reportOperation.reportSuccess(idSuccess); }else{ transExec.rollBackTransaction();
				 * this.setMessageIdFail(MessageCodes.General.UPDATE_FAIL); }
				 */
			}
		} catch (Exception e) {
			transExec.rollBackTransaction();
			this.setMessageIdFail(MessageCodes.General.UPDATE_FAIL);
			reportOperation.reportFailure(idFail, e, "services()", "Errore nella forzatura dei movimenti");

		}
	}
}
