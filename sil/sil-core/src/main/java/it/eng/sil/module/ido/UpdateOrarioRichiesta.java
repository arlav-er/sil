package it.eng.sil.module.ido;

import java.math.BigDecimal;

import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.utils.StringUtils;
import it.eng.sil.module.AbstractSimpleModule;

public class UpdateOrarioRichiesta extends AbstractSimpleModule {
	public void service(SourceBean request, SourceBean response) throws Exception {
		Boolean updateFlg = new Boolean(false);
		String flgInvioCL = StringUtils.getAttributeStrNotNull(request, "flgInvioCL");
		if ("S".equals(flgInvioCL)) {
			setSectionQuerySelect("VERIFICA_FLGCL_ORARIO");
			SourceBean sbFlgCL = doSelect(request, response);
			BigDecimal numFlag = (BigDecimal) sbFlgCL.getAttribute("ROW.CONTAFLAG");
			if (numFlag.intValue() > 0) {
				setSectionQueryUpdate("UPDATE_FLGCL_ORARIO");
				updateFlg = doUpdate(request, response);

				if (!updateFlg.booleanValue())
					throw new Exception("Errore nell'aggiornamento dell'orario");
			}
		}

		response.delAttribute("USER_MESSAGE");
		setSectionQueryUpdate("QUERY_UPDATE");
		doUpdate(request, response);
	}
}