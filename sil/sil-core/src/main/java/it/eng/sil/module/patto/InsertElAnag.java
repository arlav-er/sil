package it.eng.sil.module.patto;

import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.utils.MessageCodes;
import it.eng.afExt.utils.ReportOperationResult;
import it.eng.afExt.utils.TransactionQueryExecutor;
import it.eng.sil.module.AbstractSimpleModule;
import it.eng.sil.module.QueryStrategyException;
import it.eng.sil.util.ExceptionUtils;

public class InsertElAnag extends AbstractSimpleModule {
	public void service(SourceBean request, SourceBean response) throws Exception {
		Object res = null;
		boolean ret = false;

		// apri la transazione
		TransactionQueryExecutor transExec = null;
		ReportOperationResult reportOperation = null;

		try {
			reportOperation = new ReportOperationResult(this, response);
			int idSuccess = this.disableMessageIdSuccess();
			int idFail = this.disableMessageIdFail();

			transExec = new TransactionQueryExecutor(getPool());
			enableTransactions(transExec);
			transExec.initTransaction();

			/*
			 * non effettua più l'inserimento o l'aggiornamento nella tabella am_privacy if
			 * (PrivacyManager.hasNuovaAutorizzazione(request)) { PrivacyManager.newPrivacy(request, response, this); }
			 * else { if (PrivacyManager.hasUpdateAutorizzazione(request)) { PrivacyManager.updatePrivacy(request,
			 * response, this); } }
			 */

			setSectionQueryInsert("QUERY_INSERT");

			res = this.doInsert(request, response, true);
			ExceptionUtils.controlloDateRecordPrecedente(res,
					MessageCodes.General.ERR_INTERSEZIONE_DATA_REC_STORICIZZATO);
			ret = ((Boolean) res).booleanValue();

			if (ret) {
				transExec.commitTransaction();
				this.setMessageIdSuccess(idSuccess);
				this.setMessageIdFail(idFail);
				reportOperation.reportSuccess(idSuccess);
			} else {
				throw new Exception();
			}

		} catch (QueryStrategyException qse) {
			reportOperation.reportFailure(MessageCodes.General.ERR_INTERSEZIONE_DATA_REC_STORICIZZATO);
			if (transExec != null) {
				transExec.rollBackTransaction();
			}
		}

		catch (Exception e) {
			transExec.rollBackTransaction();
			reportOperation.reportFailure(MessageCodes.General.INSERT_FAIL, e, "execute()",
					"aggiornamento am_anagrafica in transazione con am_privacy");
		}
	}
	// end service
}
// InsertElAnag
