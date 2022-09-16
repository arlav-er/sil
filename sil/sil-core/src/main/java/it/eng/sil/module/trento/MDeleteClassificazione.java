package it.eng.sil.module.trento;

import java.math.BigDecimal;

import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.utils.MessageCodes;
import it.eng.afExt.utils.ReportOperationResult;
import it.eng.sil.module.AbstractSimpleModule;

public class MDeleteClassificazione extends AbstractSimpleModule {
	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(MDeleteClassificazione.class.getName());

	public void service(SourceBean request, SourceBean response) {

		int idSuccess = this.disableMessageIdSuccess();
		int idFail = this.disableMessageIdFail();
		ReportOperationResult reportOperation = new ReportOperationResult(this, response);

		setSectionQuerySelect("QUERY_RELATION_TEMPLATE");
		SourceBean rowTemplate = doSelect(request, response, false);

		int esisteRelazioneTemplate = 0;
		if (rowTemplate != null) {
			if (rowTemplate.containsAttribute("ROW")) {
				BigDecimal esisteRelazioneTemplateBD = (BigDecimal) rowTemplate.getAttribute("ROW.NUM_TEMPLATE");
				esisteRelazioneTemplate = esisteRelazioneTemplateBD.intValue();
			}
		}

		if (esisteRelazioneTemplate > 0) {
			// setMessageIdFail(MessageCodes.General.DELETE_FAILED_FK);
			reportOperation.reportFailure(MessageCodes.Classificazione.WARN_CLASSIFICAZIONE_NO_DELETE_EXISTS_TEMPLATE);
			return;
		} else {
			boolean ret = doUpdate(request, response);
			reportOperation.reportSuccess(idSuccess);
		}

	}
}
