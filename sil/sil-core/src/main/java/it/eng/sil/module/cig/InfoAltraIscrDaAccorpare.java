package it.eng.sil.module.cig;

import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.utils.MessageCodes;
import it.eng.afExt.utils.ReportOperationResult;
import it.eng.sil.module.AbstractSimpleModule;

public class InfoAltraIscrDaAccorpare extends AbstractSimpleModule {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger
			.getLogger(InfoAltraIscrDaAccorpare.class.getName());

	public void service(SourceBean serviceRequest, SourceBean serviceResponse) throws Exception {
		try {
			String prgaltraiscr1 = (String) serviceRequest.getAttribute("prgaltraiscr1");
			String prgaltraiscr2 = (String) serviceRequest.getAttribute("prgaltraiscr2");
			Object prgDichDisponibilita = null;
			disableMessageIdSuccess();
			// Iscrizione 1
			serviceRequest.updAttribute("prgaltraiscr", prgaltraiscr1);
			SourceBean Iscr1 = new SourceBean("ISCR1");
			executeQuery("GET_INFO_ALTRA_ISCR", Iscr1, serviceRequest, serviceResponse);

			// Iscrizione 2
			serviceRequest.updAttribute("prgaltraiscr", prgaltraiscr2);
			SourceBean Iscr2 = new SourceBean("ISCR2");
			executeQuery("GET_INFO_ALTRA_ISCR", Iscr2, serviceRequest, serviceResponse);

			serviceResponse.setAttribute(Iscr1);
			serviceResponse.setAttribute(Iscr2);
		} catch (Exception e) {
			ReportOperationResult reportOperation = new ReportOperationResult(this, serviceResponse);
			reportOperation.reportFailure(MessageCodes.General.GET_ROW_FAIL);
			it.eng.sil.util.TraceWrapper.debug(_logger, "", e);

		}

	}

	private void executeQuery(String query, SourceBean lav, SourceBean serviceRequest, SourceBean serviceResponse)
			throws Exception {
		setSectionQuerySelect(query);
		SourceBean row = doSelect(serviceRequest, serviceResponse, false);
		if (row == null)
			throw new Exception(
					"impossibile recuperare le informazioni relative a " + query + " per iscrizione " + lav.getName());
		lav.setAttribute(query, row);
	}
}
