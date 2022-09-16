package it.eng.sil.module.anag;

import java.math.BigDecimal;

import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.utils.MessageCodes;
import it.eng.afExt.utils.ReportOperationResult;
import it.eng.sil.module.AbstractSimpleModule;
import it.eng.sil.security.User;
import it.eng.sil.util.Utils;

public class InviaSAPCruscotto extends AbstractSimpleModule {

	private static final long serialVersionUID = -1533726811526662058L;
	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(InviaSAPCruscotto.class.getName());

	private String className = this.getClass().getName();
	private String END_POINT_NAME = "InvioSAP";

	public void service(SourceBean request, SourceBean response) {
		User user = (User) getRequestContainer().getSessionContainer().getAttribute("@@USER@@");
		BigDecimal p_cdnUtente = new BigDecimal(user.getCodut());

		String cdnLavoratore = Utils.notNull(request.getAttribute("CDNLAVORATORE"));
		String operazione = Utils.notNull(request.getAttribute("OPERAZIONE"));

		ReportOperationResult reportOperation = new ReportOperationResult(this, response);
		disableMessageIdFail();
		disableMessageIdSuccess();

		try {

			SourceBean serviceResponse = getResponseContainer().getServiceResponse();

			if ((serviceResponse.containsAttribute("M_CheckStatoPoliticheAttiveGG.ESITO_CHECK") && serviceResponse
					.getAttribute("M_CheckStatoPoliticheAttiveGG.ESITO_CHECK").toString().equals("OK"))
					|| (operazione.equals("CONFERMA_INVIA_SAP"))) {

				_logger.info("CHIAMATA INVIA SAP, CDNLAVORATORE = " + cdnLavoratore);

				reportOperation.reportSuccess(MessageCodes.General.OPERATION_SUCCESS);
			}

		} catch (Exception ex) {
			reportOperation.reportSuccess(MessageCodes.General.OPERATION_FAIL);
			it.eng.sil.util.TraceWrapper.debug(_logger, className + ": errore chiamata WS", ex);
		}
	}

}