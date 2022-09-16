package it.eng.sil.module.amministrazione.redditoAttivazione;

import java.text.ParseException;

import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.utils.MessageCodes;
import it.eng.afExt.utils.ReportOperationResult;
import it.eng.sil.module.AbstractSimpleModule;

public class AggiornaMSG extends AbstractSimpleModule {

	private static final long serialVersionUID = 5863792564864524603L;

	public void service(SourceBean request, SourceBean response) throws ParseException {

		ReportOperationResult reportOperation = new ReportOperationResult(this, response);
		reportOperation.reportSuccess(MessageCodes.General.OPERATION_SUCCESS);
	}
}
