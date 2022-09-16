package it.eng.sil.module.presel;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.error.EMFErrorSeverity;
import com.engiweb.framework.error.EMFUserError;

import it.eng.afExt.utils.MessageCodes;
import it.eng.afExt.utils.ReportOperationResult;
import it.eng.sil.module.AbstractSimpleModule;
import it.eng.sil.module.patto.PattoFacade;

public class DeleteEspLav extends AbstractSimpleModule {
	public void service(SourceBean request, SourceBean response) throws Exception {
		ReportOperationResult reportOperation = new ReportOperationResult(this, response);
		int idSuccess = this.disableMessageIdSuccess();
		int idFail = this.disableMessageIdFail();

		try {
			PattoFacade facade = new PattoFacade();
			if (facade.withPatto(request)) {

				this.setSectionQuerySelect("GET_PATTO");
				SourceBean row = doSelect(request, response, false);
				String codStatoAtto = (String) row.getAttribute("ROW.codStatoAtto");
				if (codStatoAtto != null && codStatoAtto.equals("PR"))
					throw new EMFUserError(EMFErrorSeverity.ERROR,
							MessageCodes.Mansioni.ESPERIENZA_ASS_PATTO_PROTOCOLLATO);

				facade.doDelete(this, getPool(), request, response);
			} else {
				doDelete(request, response);
			}
		} catch (EMFUserError ue) {
			if (ue.getCode() == MessageCodes.Mansioni.ESPERIENZA_ASS_PATTO_PROTOCOLLATO)
				reportOperation.reportFailure(ue.getCode());
			else
				reportOperation.reportFailure(MessageCodes.General.DELETE_FAIL);
		} catch (Exception e) {
			reportOperation.reportFailure(MessageCodes.General.DELETE_FAIL);
		}

	}
}