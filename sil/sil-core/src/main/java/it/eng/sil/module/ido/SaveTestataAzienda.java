package it.eng.sil.module.ido;

import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.utils.CF_utils;
import it.eng.afExt.utils.CfAZException;
import it.eng.afExt.utils.MessageCodes;
import it.eng.afExt.utils.PIVA_utils;
import it.eng.afExt.utils.PatInailUtils;
import it.eng.afExt.utils.PivaException;
import it.eng.afExt.utils.ReportOperationResult;
import it.eng.sil.module.AbstractSimpleModule;

public class SaveTestataAzienda extends AbstractSimpleModule {
	public void service(SourceBean request, SourceBean response) {

		String strCodiceFiscale = (String) request.getAttribute("strCodiceFiscale");
		// check partita Iva super-FLUO
		String strPartitaIva = (String) request.getAttribute("strPartitaIva");
		String strPatInail = request.containsAttribute("strPatInail") ? request.getAttribute("strPatInail").toString()
				: "";

		ReportOperationResult reportOperation = new ReportOperationResult(this, response);
		String strFlgDatiOk = (String) request.getAttribute("FLGDATIOK");
		if (!strFlgDatiOk.equals("S")) {
			try {
				CF_utils.verificaCFAzienda(strCodiceFiscale);
			} catch (CfAZException cfAZEx) {
				reportOperation.reportFailure(cfAZEx.getMessageIdFail());
			}
			// check partita Iva super-FLUO+
			try {
				PIVA_utils.verifyPartitaIva(strPartitaIva);
			} catch (PivaException PEx) {
				reportOperation.reportFailure(PEx.getMessageIdFail());
			}
		}
		if (!strPatInail.equals("")) {
			if (!(PatInailUtils.controllaInail(strPatInail))) {
				reportOperation.reportFailure(MessageCodes.ImportMov.WAR_NUM_INAIL_ERRATO);
			}
		} else {
			reportOperation.reportFailure(MessageCodes.ImportMov.WAR_NUM_INAIL_NOVALORIZ);
		}
		doUpdate(request, response);
	}
}