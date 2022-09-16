package it.eng.sil.module.trento;

import java.math.BigDecimal;

import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.utils.MessageCodes;
import it.eng.afExt.utils.ReportOperationResult;
import it.eng.afExt.utils.TransactionQueryExecutor;
import it.eng.sil.module.AbstractSimpleModule;

public class MAggiornaClassificazione extends AbstractSimpleModule {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(AbstractSimpleModule.class.getName());

	public MAggiornaClassificazione() {
	}

	public void service(SourceBean request, SourceBean response) throws Exception {
		// doUpdateNoDuplicate(request, response, "1");

		int idSuccess = this.disableMessageIdSuccess();
		int idFail = this.disableMessageIdFail();
		ReportOperationResult reportOperation = new ReportOperationResult(this, response);
		TransactionQueryExecutor trans = null;

		setSectionQuerySelect("QUERY_SELECT_DUPLICATI");
		SourceBean rowExistClassificazione = doSelect(request, response, false);

		int esisteClassificazione = 0;
		if (rowExistClassificazione != null) {
			if (rowExistClassificazione.containsAttribute("ROW")) {
				BigDecimal esisteClassificazioneBD = (BigDecimal) rowExistClassificazione.getAttribute("ROW.1");
				esisteClassificazione = esisteClassificazioneBD.intValue();
			}
		}

		if (esisteClassificazione > 0) {
			// setMessageIdFail(MessageCodes.General.DELETE_FAILED_FK);
			reportOperation.reportFailure(MessageCodes.General.ELEMENT_DUPLICATED);
			return;
		} else {

			try {
				trans = new TransactionQueryExecutor(getPool());
				this.enableTransactions(trans);
				trans.initTransaction();

				setSectionQuerySelect("QUERY_SELECT_DETTAGLIO");
				SourceBean rowClassificazione = doSelect(request, response, false);
				if (rowClassificazione != null) {
					String codTipoDominioDB = (String) rowClassificazione.getAttribute("row.CODTIPODOMINIO");
					String codTipoDominio = (String) request.getAttribute("prgTipoDominio");

					if (!codTipoDominio.equalsIgnoreCase(codTipoDominioDB)) {
						setSectionQueryUpdate("QUERY_UPDATE_TEMPLATE_ASSOCIATI");
						boolean retAssociati = doUpdate(request, response);

						if (retAssociati) {
							setSectionQueryUpdate("QUERY_UPDATE");
							boolean ret = doUpdate(request, response);
						}

					} else {

						setSectionQueryUpdate("QUERY_UPDATE");
						boolean ret = doUpdate(request, response);

					}

				}

				trans.commitTransaction();
				reportOperation.reportSuccess(idSuccess);
			} catch (Throwable ex) {
				if (trans != null) {
					trans.rollBackTransaction();
				}
				it.eng.sil.util.TraceWrapper.debug(_logger, "AbstractSimpleModule.service()", ex);
				reportOperation.reportFailure(MessageCodes.General.INSERT_FAIL);
			}
		}
	}
}