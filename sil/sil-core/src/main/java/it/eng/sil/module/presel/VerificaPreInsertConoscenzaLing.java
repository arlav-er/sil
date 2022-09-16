package it.eng.sil.module.presel;

import java.math.BigDecimal;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.base.SourceBeanException;

import it.eng.afExt.utils.MessageCodes;
import it.eng.afExt.utils.ReportOperationResult;
import it.eng.sil.module.AbstractSimpleModule;

public class VerificaPreInsertConoscenzaLing extends AbstractSimpleModule {
	private static final String VERIFICA_OK = "VERIFICA_OK";

	private int messageIdSuccess = MessageCodes.General.OPERATION_SUCCESS;
	private int messageIdFail = MessageCodes.General.OPERATION_FAIL;

	public void service(SourceBean request, SourceBean response) {

		ReportOperationResult reportOperation = new ReportOperationResult(this, response);

		SourceBean result = doSelect(request, response);
		// String a=result.toXML();
		// SourceBean s=(SourceBean)result.getAttribute("ROW");
		BigDecimal conteggio = (BigDecimal) result.getAttribute("ROW.CONT");
		if (conteggio != null) {
			// int conteggio=Integer.parseInt(strCont);
			if (conteggio.intValue() == 0) {
				try {
					response.setAttribute(VERIFICA_OK, "TRUE");
				} catch (SourceBeanException ex) {
					reportOperation.reportFailure(this.messageIdFail, ex, "VerificaPreInsertConoscenzaLing",
							"Cannot assign VERIFICA_OK in response");
				}
			} else { // di conteggio.intValue()==0
				try {
					response.setAttribute(VERIFICA_OK, "FALSE");
				} catch (SourceBeanException ex) {
					reportOperation.reportFailure(this.messageIdFail, ex, "VerificaPreInsertConoscenzaLing",
							"Cannot assign VERIFICA_OK in response");
				}
			}
		} else { // di if (conteggio==null)
			try {
				response.setAttribute(VERIFICA_OK, "FALSE");
			} catch (SourceBeanException ex) {
				reportOperation.reportFailure(this.messageIdFail, ex, "VerificaPreInsertConoscenzaLing",
						"Cannot assign VERIFICA_OK in response");
			}
		}
	}
}