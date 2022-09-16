package it.eng.sil.module.patto;

import java.util.Vector;

import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.utils.MessageCodes;
import it.eng.afExt.utils.ReportOperationResult;
import it.eng.afExt.utils.StringUtils;
import it.eng.afExt.utils.TransactionQueryExecutor;
import it.eng.sil.module.AbstractSimpleModule;

public class PattoSchedaPartecipante extends AbstractSimpleModule {

	/**
	 * 
	 */
	private static final long serialVersionUID = -9147849554811374283L;

	@Override
	public void service(SourceBean request, SourceBean response) throws Exception {
		boolean ret = false;
		int idSuccess = this.disableMessageIdSuccess();
		int idFail = this.disableMessageIdFail();
		TransactionQueryExecutor transExec = null;
		ReportOperationResult reportOperation = new ReportOperationResult(this, response);
		String operazione = StringUtils.getAttributeStrNotNull(request, "inserisciAggiornaScheda");
		try {
			transExec = new TransactionQueryExecutor(getPool());
			this.enableTransactions(transExec);

			transExec.initTransaction();

			if (operazione.equalsIgnoreCase("INSERISCI")) {
				this.setSectionQueryInsert("QUERY_INSERT_PARTECIPANTE");
				ret = doInsert(request, response);

				if (!ret) {
					throw new Exception("impossibile inserire in or_scheda_partecipante in transazione");
				}
			} else if (operazione.equalsIgnoreCase("AGGIORNA")) {
				this.setSectionQueryUpdate("QUERY_UPDATE_PARTECIPANTE");
				ret = doUpdate(request, response);

				if (!ret) {
					throw new Exception("impossibile aggiornare in or_scheda_partecipante in transazione");
				}

				this.setSectionQueryDelete("QUERY_DELETE_SVANTAGGI");
				ret = this.doDelete(request, response);

				if (!ret) {
					throw new Exception("Impossibile eliminare in or_scheda_svantaggio in transazione");
				}
			}

			Vector<String> codiciSvantaggioBean = (Vector<String>) request.getAttributeAsVector("CODSVANTAGGIO");
			for (String codiceSvantaggio : codiciSvantaggioBean) {
				if (!codiceSvantaggio.isEmpty()) {
					request.delAttribute("CODSVANTAGGIO");
					request.setAttribute("CODSVANTAGGIO", codiceSvantaggio);
					this.setSectionQueryInsert("QUERY_INSERT_SVANTAGGIO");
					ret = doInsert(request, response);

					if (!ret) {
						throw new Exception("impossibile inserire in or_scheda_svantaggio in transazione");
					}
				}
			}

			transExec.commitTransaction();
			if (operazione.equalsIgnoreCase("INSERISCI")) {
				this.setMessageIdSuccess(MessageCodes.General.INSERT_SUCCESS);
			} else if (operazione.equalsIgnoreCase("AGGIORNA")) {
				this.setMessageIdSuccess(MessageCodes.General.UPDATE_SUCCESS);
			}
			reportOperation.reportSuccess(idSuccess);
		} catch (Exception e) {
			transExec.rollBackTransaction();
			if (operazione.equalsIgnoreCase("INSERISCI")) {
				this.setMessageIdFail(MessageCodes.General.INSERT_FAIL);
				;
			} else if (operazione.equalsIgnoreCase("AGGIORNA")) {
				this.setMessageIdFail(MessageCodes.General.UPDATE_FAIL);
			}
			reportOperation.reportFailure(e, "service()", "aggiornamento in transazione");
		}
	}

}
