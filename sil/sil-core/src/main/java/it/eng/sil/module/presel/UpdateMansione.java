package it.eng.sil.module.presel;

import java.util.Vector;

import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.utils.MessageCodes;
import it.eng.afExt.utils.ReportOperationResult;
import it.eng.afExt.utils.TransactionQueryExecutor;
import it.eng.sil.module.AbstractSimpleModule;
import it.eng.sil.module.patto.PattoManager;
import it.eng.sil.module.patto.TracciaModifichePatto;

public class UpdateMansione extends AbstractSimpleModule {
	public void service(SourceBean request, SourceBean response) throws Exception {
		String flgEsperienza = (String) request.getAttribute("FLGESPERIENZA");
		boolean ret = false;

		if ((flgEsperienza.equalsIgnoreCase("N")) && isMansionePresenteInEspLavoro(request, response)) {
			ReportOperationResult reportOperation = new ReportOperationResult(this, response);
			reportOperation.reportFailure(MessageCodes.Mansioni.MANSIONE_PRESENTE_IN_ESP_LAV);

			return;
		}

		if (PattoManager.withPatto(request)) {
			ReportOperationResult reportOperation = new ReportOperationResult(this, response);
			TransactionQueryExecutor transExec = null;
			int idSuccess = this.disableMessageIdSuccess();
			int idFail = this.disableMessageIdFail();

			try {
				transExec = new TransactionQueryExecutor(getPool());

				PattoManager patto = new PattoManager(this, transExec);
				transExec.initTransaction();
				setSectionQuerySelect("QUERY_SELECT_INFO");
				SourceBean amLavPatto = doSelect(request, response, false);
				TracciaModifichePatto.cancellazione(getRequestContainer(), amLavPatto, transExec);
				// this.setSectionQueryUpdate("UPDATE_IND_T");
				ret = this.doUpdate(request, response);

				if (ret) {
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
				// this.setMessageIdFail(MessageCodes.General.UPDATE_FAIL);
				reportOperation.reportFailure(MessageCodes.General.UPDATE_FAIL);

				if (transExec != null) {
					transExec.rollBackTransaction();
				}
			}
		} else {
			// setSectionQueryUpdate("UPDATE_IND_T");
			doUpdate(request, response);
		}
	}

	/**
	 * Verifica se questa mansione esiste già nella tabella PR_ESP_LAVORO.
	 * 
	 * @return true se il record esiste
	 */
	private boolean isMansionePresenteInEspLavoro(SourceBean request, SourceBean response) {
		int prevSuccess = disableMessageIdSuccess();
		SourceBean rowsSelected = doSelectWithStatement(request, response, "QUERY_SELECT_ESP_LAVORO");
		setMessageIdSuccess(prevSuccess);

		Vector vectRows = ((rowsSelected != null) ? rowsSelected.getAttributeAsVector("ROW") : null);

		return (vectRows != null) && (vectRows.size() > 0);
	}
}
