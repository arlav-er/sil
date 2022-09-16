package it.eng.sil.module.amministrazione;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.ResponseContainer;
import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.utils.MessageCodes;
import it.eng.afExt.utils.ReportOperationResult;
import it.eng.afExt.utils.TransactionQueryExecutor;
import it.eng.sil.module.AbstractSimpleModule;
import it.eng.sil.util.Utils;

public class RipristinaMov extends AbstractSimpleModule {

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
			String prgMovimentoPrec = Utils.notNull(request.getAttribute("prgMovimentoPrec"));
			String prgMovimentoSucc = Utils.notNull(request.getAttribute("prgMovimentoSucc"));

			if (!prgMovimento.equals("")) {
				if (!prgMovimentoSucc.equals("") && !prgMovimentoSucc.equals("0")) {
					SourceBean queryRipristinaPrec = (SourceBean) getConfig().getAttribute("QUERY_RIPRISTINA_PREC");
					Boolean ok = (Boolean) transExec.executeQuery(reqCont, resCont, queryRipristinaPrec, "UPDATE");
					if (!ok.booleanValue()) {
						throw new Exception("ERRORE ESEGUENDO L'AGGIORNAMENTO DEL PRECEDENTE");
					}
				}

				if (!prgMovimentoPrec.equals("") && !prgMovimentoPrec.equals("0")) {
					SourceBean queryRipristinaSucc = (SourceBean) getConfig().getAttribute("QUERY_RIPRISTINA_SUCC");
					Boolean ok = (Boolean) transExec.executeQuery(reqCont, resCont, queryRipristinaSucc, "UPDATE");
					if (!ok.booleanValue()) {
						throw new Exception("ERRORE ESEGUENDO L'AGGIORNAMENTO DEL SUCCESSIVO");
					}
				}

				SourceBean queryRipristinaDoc = (SourceBean) getConfig().getAttribute("QUERY_RIPRISTINA_DOC");
				Boolean ok = (Boolean) transExec.executeQuery(reqCont, resCont, queryRipristinaDoc, "UPDATE");
				if (!ok.booleanValue()) {
					throw new Exception("ERRORE ESEGUENDO IL RIPRISTINO DEL DOCUMENTO");
				} else {
					SourceBean queryRipristinaMov = (SourceBean) getConfig().getAttribute("QUERY_RIPRISTINA_MOV");
					Boolean ok2 = (Boolean) transExec.executeQuery(reqCont, resCont, queryRipristinaMov, "UPDATE");
					if (!ok2.booleanValue()) {
						throw new Exception("ERRORE ESEGUENDO IL RIPRISTINO DEL MOVIMENTO");
					}
				}

				this.setMessageIdSuccess(MessageCodes.General.UPDATE_SUCCESS);
				transExec.commitTransaction();
				reportOperation.reportSuccess(idSuccess);
			}
		} catch (Exception e) {
			transExec.rollBackTransaction();
			this.setMessageIdFail(MessageCodes.General.UPDATE_FAIL);
			reportOperation.reportFailure(idFail, e, "services()", "Errore nella forzatura dei movimenti");

		}
	}
}
