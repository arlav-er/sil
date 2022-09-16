package it.eng.sil.module.ido;

import java.math.BigDecimal;

import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.utils.StringUtils;
import it.eng.sil.module.AbstractAlternativaSimpleModule;

public class SaveIdoMansioni extends AbstractAlternativaSimpleModule {
	public void service(SourceBean request, SourceBean response) throws Exception {
		Boolean updateFlg = new Boolean(false);
		String flgInvioCL = StringUtils.getAttributeStrNotNull(request, "flgInvioCL");
		Object prgRequest = request.getAttribute("PRGMANSIONE");
		if ("S".equals(flgInvioCL)) {
			this.setSectionQuerySelect("QUERY_SELECT");
			SourceBean sbQueryDuplicate = doSelect(request, response);
			Object prgSelect = sbQueryDuplicate.getAttribute("ROW.PRGMANSIONE"); // recupera il prgmansione di
																					// un'eventuale altra mansione con
																					// stesso codice.
			if (prgSelect != null && prgSelect.toString().equals(prgRequest)) {
				this.setSectionQuerySelect("VERIFICA_FLGCL");
				SourceBean sbFlgCL = doSelect(request, response);
				BigDecimal numFlag = (BigDecimal) sbFlgCL.getAttribute("ROW.CONTAFLAG");
				if (numFlag.intValue() > 0) {
					setSectionQueryUpdate("QUERY_FLGCL");
					updateFlg = doUpdate(request, response);

					if (!updateFlg.booleanValue())
						throw new Exception("Errore nell'aggiornamento della mansione");
				}
			}
			response.delAttribute("USER_MESSAGE");
		}
		setSectionQueryUpdate("QUERY_UPDATE");
		setSectionQuerySelect("QUERY_SELECT");
		doUpdateNoDuplicate(request, response, "PRGMANSIONE");
	}
}