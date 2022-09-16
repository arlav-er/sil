package it.eng.sil.module.patto;

import java.math.BigDecimal;

import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.utils.MessageCodes;
import it.eng.afExt.utils.ReportOperationResult;
import it.eng.afExt.utils.TransactionQueryExecutor;
import it.eng.sil.module.AbstractSimpleModule;
import it.eng.sil.util.Utils;

public class SaveColloquio extends AbstractSimpleModule {
	public void service(SourceBean request, SourceBean response) throws Exception {
		boolean ret = false;
		int idSuccess = this.disableMessageIdSuccess();
		int idFail = this.disableMessageIdFail();
		TransactionQueryExecutor transExec = null;
		ReportOperationResult reportOperation = new ReportOperationResult(this, response);
		boolean fail = false;

		try {
			this.setSectionQuerySelect("QUERY_SELECT_EXIST_PROGRAMMA_APERTO");
			SourceBean rowsP = doSelect(request, response);
			BigDecimal numCollAperti = (BigDecimal) rowsP.getAttribute("ROW.numProgrammiAperti");
			if (numCollAperti.intValue() > 0) {
				reportOperation.reportFailure(MessageCodes.Patto.COLLOQUIO_PROGRAMMA_APERTO);
				fail = true;
			}

			if (!fail) {
				transExec = new TransactionQueryExecutor(getPool());
				this.enableTransactions(transExec);

				//
				transExec.initTransaction();

				// Riccardi : non è possibile aggiornare un colloquio con una iscrizione CIG associata ad un altro
				// colloquio già esistente
				String prgAltraIscr = Utils.notNull(request.getAttribute("PRGALTRAISCR"));
				if (prgAltraIscr.equals("")) {
					aggiorna(transExec, request, response);
					reportOperation.reportSuccess(idSuccess);
				} else {
					this.setSectionQuerySelect("QUERY_SELECT_UNICO_CIG_COLLOQUIO");
					SourceBean rows = doSelect(request, response);
					BigDecimal numCigColl = (BigDecimal) rows.getAttribute("ROW.numCigColl");

					if (numCigColl.intValue() > 0) {
						transExec.commitTransaction();
						response.setAttribute("numCigColl", "1");
						reportOperation.reportFailure(MessageCodes.Patto.COLLOQUIO_CON_CIG_COLLEGATA);
					} else {
						aggiorna(transExec, request, response);
						reportOperation.reportSuccess(idSuccess);
					}
				}
			}
		} catch (Exception e) {
			if (transExec != null) {
				transExec.rollBackTransaction();
			}
			reportOperation.reportFailure(MessageCodes.General.UPDATE_FAIL, e, "services()", "insert in transazione");
		} finally {
		}
	}

	private void aggiorna(TransactionQueryExecutor transExec, SourceBean request, SourceBean response)
			throws Exception {
		boolean ret = false;
		this.setSectionQueryUpdate("QUERY_UPDATE_COLLOQUIO");
		ret = doUpdate(request, response);

		if (!ret) {
			throw new Exception("impossibile aggiornare in or_colloquio in transazione");
		}

		this.setSectionQueryUpdate("QUERY_UPDATE_SCHEDA_COLLOQUIO");
		ret = doUpdate(request, response);

		if (!ret) {
			throw new Exception("impossibile aggiornare in or_scheda_colloquio in transazione");
		}

		transExec.commitTransaction();
	}
}