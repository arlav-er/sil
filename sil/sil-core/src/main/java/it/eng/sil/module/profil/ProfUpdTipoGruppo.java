package it.eng.sil.module.profil;

import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.utils.CF_utils;
import it.eng.afExt.utils.CfException;
import it.eng.afExt.utils.MessageCodes;
import it.eng.afExt.utils.ReportOperationResult;
import it.eng.afExt.utils.StringUtils;
import it.eng.sil.module.AbstractSimpleModule;

public class ProfUpdTipoGruppo extends AbstractSimpleModule {

	public void service(SourceBean serviceRequest, SourceBean serviceResponse) {
		ReportOperationResult reportOperation = new ReportOperationResult(this, serviceResponse);
		String codTipoGruppo = StringUtils.getAttributeStrNotNull(serviceRequest, "CODTIPO");
		if (codTipoGruppo.equalsIgnoreCase(Properties.TTPO_GRUPPO_SOGGETTI_ACCREDITATI)) {
			// controllo su codice fiscale
			String codiceFiscale = StringUtils.getAttributeStrNotNull(serviceRequest, "strCodiceFiscale");
			if (codiceFiscale.equals("")) {
				reportOperation.reportFailure(MessageCodes.CodiceFiscale.ERR_LUNGHEZZA);
				return;
			} else {
				try {
					if (CF_utils.verificaParzialeCF(codiceFiscale) != 0) {
						return;
					}
				} catch (CfException eCF) {
					reportOperation.reportFailure(eCF.getMessageIdFail());
					return;
				}
			}
		}
		doUpdate(serviceRequest, serviceResponse);
	}

}
