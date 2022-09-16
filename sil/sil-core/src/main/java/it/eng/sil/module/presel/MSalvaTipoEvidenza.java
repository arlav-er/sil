package it.eng.sil.module.presel;

import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.utils.MessageCodes;
import it.eng.afExt.utils.ReportOperationResult;
import it.eng.sil.module.AbstractSimpleModule;

/*
 * 
 * @author rolfini
 *
 */

public class MSalvaTipoEvidenza extends AbstractSimpleModule {
	public void service(SourceBean request, SourceBean response) {
		int idSuccess = this.disableMessageIdSuccess();
		int idFail = this.disableMessageIdFail();

		ReportOperationResult result = new ReportOperationResult(this, response);

		boolean utilizzato = false;

		// verifico che non sia già stato usato
		this.setSectionQuerySelect("QUERY_VERIFICA_UTILIZZO");
		SourceBean tmp = doSelect(request, response);
		if ((tmp != null) && (tmp.getAttribute("ROW.prgTipoEvidenza") != null)) { // è
																					// utilizzato
			utilizzato = true;
			result.reportFailure(MessageCodes.Evidenze.EVID_COD_UPD_FAILURE);
		}

		if (!utilizzato) {
			this.setSectionQuerySelect("QUERY_SELECT");
			if (doUpdateNoDuplicate(request, response, "CODTIPOEVIDENZA")) {
				result.reportSuccess(idSuccess);
			} else {
				result.reportFailure(MessageCodes.Evidenze.EVID_DUP_UPD_COD_FAILURE);
			}
		}

	}

}
