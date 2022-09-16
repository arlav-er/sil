package it.eng.sil.module.agenda;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.error.EMFErrorHandler;
import com.engiweb.framework.error.EMFErrorSeverity;
import com.engiweb.framework.error.EMFUserError;
import com.engiweb.framework.util.QueryExecutor;

import it.eng.afExt.utils.MessageCodes;
import it.eng.afExt.utils.StringUtils;
import it.eng.afExt.utils.TransactionQueryExecutor;
import it.eng.sil.module.AbstractSimpleModule;
import it.eng.sil.module.patto.AssociazioneImpegni;

public class DeleteLavoratoreAppuntamentoAgenda extends AbstractSimpleModule {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger
			.getLogger(DeleteLavoratoreAppuntamentoAgenda.class.getName());

	public DeleteLavoratoreAppuntamentoAgenda() {
	}

	private String className = this.getClass().getName();

	public void service(SourceBean request, SourceBean response) {
		EMFErrorHandler engErrorHandler = getErrorHandler();
		TransactionQueryExecutor transExec = null;

		try {

			String pool = (String) getConfig().getAttribute("POOL");

			transExec = new TransactionQueryExecutor(pool);
			enableTransactions(transExec);
			transExec.initTransaction();

			String prgLavPattoScelta = StringUtils.getAttributeStrNotNull(request, "PRG_LAV_PATTO_SCELTA");

			AssociazioneImpegni associazioneImpegni = new AssociazioneImpegni("", "AG_LAV", request, transExec);
			associazioneImpegni.cancellaImpegniAssociati(prgLavPattoScelta);

			SourceBean statement = (SourceBean) getConfig().getAttribute("QUERIES.DELETE_QUERY");
			Boolean esito = (Boolean) QueryExecutor.executeQuery(getRequestContainer(), getResponseContainer(), pool,
					statement, "DELETE");

			// Delete del patto collegato all'appuntamento
			if (!prgLavPattoScelta.equals("")) {
				statement = (SourceBean) getConfig().getAttribute("QUERIES.DEL_PATTO_QUERY");
				Boolean esito2 = (Boolean) QueryExecutor.executeQuery(getRequestContainer(), getResponseContainer(),
						pool, statement, "DELETE");
			}

			it.eng.sil.util.TraceWrapper.debug(_logger, className + "::delete: request", request);

			Object rowObject = null;

			if (!esito.booleanValue()) {
				_logger.debug(className + "::delete: nessuna riga eliminata");

				engErrorHandler.addError(new EMFUserError(EMFErrorSeverity.ERROR, MessageCodes.General.OPERATION_FAIL));
				transExec.rollBackTransaction();
				return;
			} // if (rowObject == null)
			transExec.commitTransaction();
		} catch (Exception e) {
			it.eng.sil.util.TraceWrapper.error(_logger, className + "::delete:", e);

			engErrorHandler.addError((EMFUserError) e);
			try {
				transExec.rollBackTransaction();
			} catch (Exception ex) {
				it.eng.sil.util.TraceWrapper.error(_logger, className + "::rollback:", e);

			}
			// MessageAppender.appendMessage(response,ErrorCodes.ERRORE_MODULO);
		}
	}
}