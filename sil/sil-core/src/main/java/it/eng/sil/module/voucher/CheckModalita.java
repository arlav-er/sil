package it.eng.sil.module.voucher;

import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.utils.MessageCodes;
import it.eng.afExt.utils.ReportOperationResult;
import it.eng.afExt.utils.SourceBeanUtils;
import it.eng.sil.module.AbstractSimpleModule;

public class CheckModalita extends AbstractSimpleModule {

	private static final long serialVersionUID = -1533726811526662058L;
	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(CheckModalita.class.getName());

	private String className = this.getClass().getName();

	public void service(SourceBean request, SourceBean response) {
		int idSuccess = this.disableMessageIdSuccess();
		int idFail = this.disableMessageIdFail();
		ReportOperationResult reportOperation = new ReportOperationResult(this, response);
		disableMessageIdFail();
		disableMessageIdSuccess();
		try {
			String codSelezModalitaVoucher = SourceBeanUtils.getAttrStrNotNull(request, "CODSELEZMODALITA");
			if (codSelezModalitaVoucher.equalsIgnoreCase(Properties.COD_MODALITA_SOLO_UNA)) {
				SourceBean row = doSelect(request, response);
				Integer numModalitaInserite = new Integer(row.getAttribute("ROW.NUMMODALITAVOUCHER").toString());
				if (numModalitaInserite > 0) {
					reportOperation.reportFailure(MessageCodes.MSGVOUCHER.WARNING_MODALITA_ATTIVE);
					response.setAttribute("MODALITAGIAATTIVA", "TRUE");
				}
			}
		} catch (Exception e) {
			it.eng.sil.util.TraceWrapper.error(_logger, "CheckModalita", e);
		}
	}

}
