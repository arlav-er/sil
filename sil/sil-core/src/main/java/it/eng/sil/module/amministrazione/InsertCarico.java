/*
 * Creato il 9-mar-07
 *
 * Per modificare il modello associato a questo file generato, aprire
 * Finestra&gt;Preferenze&gt;Java&gt;Generazione codice&gt;Codice e commenti
 */
package it.eng.sil.module.amministrazione;

import java.math.BigDecimal;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.SessionContainer;
import com.engiweb.framework.base.SourceBean;

/**
 * @author melandri
 * 
 * Per modificare il modello associato al commento di questo tipo generato,
 * aprire Finestra&gt;Preferenze&gt;Java&gt;Generazione codice&gt;Codice e
 * commenti
 */

import it.eng.afExt.utils.MessageCodes;
import it.eng.afExt.utils.ReportOperationResult;
import it.eng.afExt.utils.TransactionQueryExecutor;
import it.eng.sil.module.AbstractSimpleModule;
import it.eng.sil.security.User;

public class InsertCarico extends AbstractSimpleModule {
	private TransactionQueryExecutor transExec;
	BigDecimal userid;

	public void service(SourceBean request, SourceBean response) throws Exception {
		RequestContainer requestContainer = getRequestContainer();
		SessionContainer sessionContainer = (SessionContainer) requestContainer.getSessionContainer();
		User user = (User) sessionContainer.getAttribute(User.USERID);
		userid = new BigDecimal(user.getCodut());
		boolean ret = false;
		int idSuccess = this.disableMessageIdSuccess();
		int idFail = this.disableMessageIdFail();
		ReportOperationResult reportOperation = new ReportOperationResult(this, response);
		RequestContainer r = this.getRequestContainer();
		try {
			transExec = new TransactionQueryExecutor(getPool());
			this.enableTransactions(transExec);

			transExec.initTransaction();

			BigDecimal PRGLAVCARICO = doNextVal(request, response);

			if (PRGLAVCARICO == null) {
				throw new Exception("Impossibile leggere S_CM_LAV_CARICO.NEXTVAL");
			}

			request.delAttribute("PRGLAVCARICO");
			request.setAttribute("PRGLAVCARICO", PRGLAVCARICO.toString());

			this.setSectionQueryInsert("QUERY_INSERT_CARICO");
			ret = doInsert(request, response);

			if (!ret) {
				throw new Exception("impossibile inserire in CM_LAV_CARICO in transazione");
			}

			r.setServiceRequest(request);

			transExec.commitTransaction();

			reportOperation.reportSuccess(idSuccess);
		} catch (Exception e) {
			transExec.rollBackTransaction();
			r.setServiceRequest(request);
			reportOperation.reportFailure(MessageCodes.General.INSERT_FAIL, e, "services()", "insert in transazione");
		}
	}
}
