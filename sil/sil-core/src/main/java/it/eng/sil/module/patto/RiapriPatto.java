package it.eng.sil.module.patto;

import java.math.BigDecimal;

import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.utils.MessageCodes;
import it.eng.afExt.utils.ReportOperationResult;
import it.eng.afExt.utils.TransactionQueryExecutor;
import it.eng.sil.module.AbstractSimpleModule;
import it.eng.sil.module.patto.bean.Patto;
import it.eng.sil.security.User;

public class RiapriPatto extends AbstractSimpleModule {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(RiapriPatto.class.getName());

	public void service(SourceBean serviceRequest, SourceBean serviceResponse) throws Exception {
		ReportOperationResult reportOperation = new ReportOperationResult(this, serviceResponse);
		disableMessageIdFail();
		disableMessageIdSuccess();
		User user = (User) getRequestContainer().getSessionContainer().getAttribute(User.USERID);
		BigDecimal cdnParUtente = new BigDecimal(user.getCodut());
		TransactionQueryExecutor txExecutor = null;
		Patto patto = null;
		BigDecimal prgPattoLav = new BigDecimal(serviceRequest.getAttribute("PRGPATTOLAVORATORE").toString());
		int forzatura = 0;
		if (serviceRequest.containsAttribute("FORZATURA")) {
			forzatura = 1;
		}
		try {
			txExecutor = new TransactionQueryExecutor(getPool(), this);
			txExecutor.initTransaction();
			patto = new Patto();

			Integer esito = patto.riapriPatto(txExecutor.getDataConnection(), prgPattoLav, forzatura, cdnParUtente);

			if (esito.intValue() == 0) {
				txExecutor.commitTransaction();
				reportOperation.reportSuccess(MessageCodes.General.OPERATION_SUCCESS);
				serviceResponse.setAttribute("ESITORIAPERTURA", "OK");
			} else {
				txExecutor.rollBackTransaction();
				switch (esito.intValue()) {
				case 1:
					reportOperation.reportFailure(MessageCodes.Patto.ERR_RIAPERTURA_PATTO_ACCORDO_OCC);
					break;
				case 2:
					reportOperation.reportFailure(MessageCodes.Patto.ERR_PATTI_PR_SUCCESSIVI);
					break;
				case 3:
					reportOperation.reportFailure(MessageCodes.Patto.WARNING_PREPATTI_SUCCESSIVI);
					serviceResponse.setAttribute("CONFERMARIAPERTURA",
							"Attenzione: esistono prepatti successivi e/o aperti. Si desidera continuare?");
					break;
				case 4:
					reportOperation.reportFailure(MessageCodes.Patto.ERR_PATTI_PR_SUCCESSIVI_AZIONI_COLL);
					break;
				case 5:
					reportOperation.reportFailure(MessageCodes.Patto.ERR_PATTI_PR_SUCCESSIVI);
					serviceResponse.setAttribute("CONFERMARIAPERTURA",
							"Attenzione: esiste un patto successivo chiuso e senza azioni collegate. Procedere con la cancellazione del patto successivo e la riapertura di quello storicizzato?");
					break;
				case 6:
					reportOperation.reportFailure(MessageCodes.Patto.ERR_PATTI_PR_SUCCESSIVI_DOCUMENTI_DOPPI);
					break;
				case 7:
					reportOperation.reportFailure(MessageCodes.Patto.ERR_RIAPERTURA_PATTO_L14);
					break;
				case 8:
					reportOperation.reportFailure(MessageCodes.Patto.ERR_PATTI_L14_PR_SUCCESSIVI);
					break;
				case 9:
					reportOperation.reportFailure(MessageCodes.Patto.ERR_DID_CHIUSA);
					break;
				default:
					reportOperation.reportFailure(MessageCodes.General.OPERATION_FAIL);
				}
			}
		} catch (Exception e) {
			if (txExecutor != null) {
				txExecutor.rollBackTransaction();
			}
			reportOperation.reportFailure(MessageCodes.General.OPERATION_FAIL, e, "service", "");
			it.eng.sil.util.TraceWrapper.debug(_logger, "RiapriPatto.service()", e);

		}
	}

}