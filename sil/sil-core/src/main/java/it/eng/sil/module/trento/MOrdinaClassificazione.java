package it.eng.sil.module.trento;

import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.utils.MessageCodes;
import it.eng.afExt.utils.ReportOperationResult;
import it.eng.afExt.utils.StringUtils;
import it.eng.afExt.utils.TransactionQueryExecutor;
import it.eng.sil.Values;
import it.eng.sil.module.AbstractSimpleModule;

public class MOrdinaClassificazione extends AbstractSimpleModule {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(MOrdinaClassificazione.class.getName());
	private final String className = StringUtils.getClassName(this);

	public MOrdinaClassificazione() {
	}

	public void service(SourceBean request, SourceBean response) throws Exception {
		ReportOperationResult reportOperation = new ReportOperationResult(this, response);

		// Segnalazione soli errori/problemi
		int idSuccess = this.disableMessageIdSuccess();
		int idFail = this.disableMessageIdFail();
		int errorCode = MessageCodes.General.UPDATE_FAIL;

		TransactionQueryExecutor trans = null;

		try {

			String ordinamento = (String) request.getAttribute("ord_classif");

			String delims = ";";
			String[] tokens = ordinamento.split(delims);
			int tokenCount = tokens.length;
			for (int j = 0; j < tokenCount; j++) {
				System.out.println("Split Output: " + tokens[j]);

				String[] tokens2 = tokens[j].split(":", 2);
				Object params[] = new Object[2];

				params[0] = tokens2[0];
				params[1] = tokens2[1];
				_logger.debug(className + "params[0]: " + params[0]);
				_logger.debug(className + "params[1]: " + params[1]);
				trans = new TransactionQueryExecutor(Values.DB_SIL_DATI, this);
				trans.initTransaction();
				Boolean res = (Boolean) trans.executeQuery("UpdateOrdinamentoClassificazione", params, "INSERT");
				_logger.debug(className + "res: " + res);
				if (!res.booleanValue())
					throw new Exception("Impossibile inserire ordinamento");

				if (!res) {
					trans.rollBackTransaction();
					response.setAttribute("aggiornato", "false");
					reportOperation.reportFailure(errorCode);
					return;
				}

				trans.commitTransaction();
				response.setAttribute("aggiornato", "true");
				reportOperation.reportSuccess(MessageCodes.General.OPERATION_SUCCESS);

				// }
			}

		} catch (Throwable ex) {
			if (trans != null) {
				trans.rollBackTransaction();
			}
			it.eng.sil.util.TraceWrapper.debug(_logger, "MOrdinaClassificazione.service()", ex);
			response.setAttribute("aggiornato", "false");
			reportOperation.reportFailure(errorCode);
		}
	}

}
