package it.eng.sil.module.patto;

import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.utils.MessageCodes;
import it.eng.afExt.utils.ReportOperationResult;
import it.eng.afExt.utils.TransactionQueryExecutor;
import it.eng.sil.module.AbstractSimpleModule;
import it.eng.sil.util.Utils;
import it.eng.sil.util.amministrazione.impatti.PattoBean;

/**
 * Gestisce la cancellazione di un record dalla tabella or_colloquio e nella stessa transazione del record collegato
 * (solo logicamente) or_scheda_colloquio
 */
public class DeleteColloquio extends AbstractSimpleModule {
	public void service(SourceBean request, SourceBean response) throws Exception {
		boolean ret = false;
		int idSuccess = this.disableMessageIdSuccess();
		int idFail = this.disableMessageIdFail();
		TransactionQueryExecutor transExec = null;
		ReportOperationResult reportOperation = new ReportOperationResult(this, response);

		try {
			transExec = new TransactionQueryExecutor(getPool());
			this.enableTransactions(transExec);

			//
			transExec.initTransaction();

			boolean failDelete = false;
			this.setSectionQuerySelect("QUERY_SELECT_COLLOQUIO");
			SourceBean rows = doSelect(request, response);

			if (rows.containsAttribute("ROW.PRGALTRAISCR")) {
				if (!Utils.notNull(rows.getAttribute("ROW.PRGALTRAISCR")).equals("")) {
					failDelete = true;
					reportOperation.reportFailure(MessageCodes.Patto.COLLOQUIO_CANC_CON_CIG_COLL);
				}
			}

			if (Utils.notNull(rows.getAttribute("ROW.codmonoprogramma")).equals(PattoBean.DB_MISURE_L14)) {
				failDelete = true;
				reportOperation.reportFailure(MessageCodes.Patto.COLLOQUIO_CANC_CON_SERVIZIO_L14);
			}
			if (Utils.notNull(rows.getAttribute("ROW.codmonoprogramma")).equals(PattoBean.DB_MISURE_DOTE)
					|| Utils.notNull(rows.getAttribute("ROW.codmonoprogramma")).equals(PattoBean.DB_MISURE_DOTE_IA)) {
				failDelete = true;
				reportOperation.reportFailure(MessageCodes.Patto.COLLOQUIO_CANC_CON_SERVIZIO_DOTE);
			}

			if (failDelete) {
				transExec.commitTransaction();
			} else {
				/*
				 * IGNORO IL RISULTATO DELLA DELETE DEI PERCORSI_CONCORDATI EVITANDO COSI' DI ESEGUIRE UNA SELECT. SE
				 * FALLIRA' LA DELETE SUCCESSIVA DEL RECORD MASTER ALLORA, CON TUTTA PROBABILITA' LA DELETE PRECEDENTE
				 * E' FALLITA
				 */
				/*
				 * this.setSectionQueryDelete("QUERY_DELETE_PERCORSI"); doDelete(request, response);
				 */
				/*
				 * if (!ret) { throw new Exception(); }
				 */
				this.setSectionQueryDelete("QUERY_DELETE_SCHEDA_COLLOQUIO");
				ret = doDelete(request, response);

				if (!ret) {
					throw new Exception();
				}

				this.setSectionQueryDelete("QUERY_DELETE_COLLOQUIO");
				ret = doDelete(request, response);

				if (!ret) {
					throw new Exception("impossibile cancellare da or_colloquio in transazione");
				}

				transExec.commitTransaction();
				reportOperation.reportSuccess(MessageCodes.General.DELETE_SUCCESS);
			}
		} catch (Exception e) {
			transExec.rollBackTransaction();
			reportOperation.reportFailure(MessageCodes.Patto.COLLOQUIO_CON_PERC_CONCORD, e, "services()",
					"delete in transazione");
		} finally {
		}
	}
}
