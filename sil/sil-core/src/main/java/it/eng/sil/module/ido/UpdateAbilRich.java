package it.eng.sil.module.ido;

import java.math.BigDecimal;

import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.utils.StringUtils;
import it.eng.sil.module.AbstractSimpleModule;

public class UpdateAbilRich extends AbstractSimpleModule {
	public void service(SourceBean request, SourceBean response) throws Exception {
		Boolean updateFlg = new Boolean(false);
		String flgInvioCL = StringUtils.getAttributeStrNotNull(request, "flgInvioCL");
		if ("S".equals(flgInvioCL)) {
			setSectionQuerySelect("VERIFICA_FLGCL");
			SourceBean sbFlgCL = doSelect(request, response);
			BigDecimal numFlag = (BigDecimal) sbFlgCL.getAttribute("ROW.CONTAFLAG");
			if (numFlag.intValue() > 0) {
				setSectionQueryUpdate("QUERY_UPDATE_FLGCL");
				updateFlg = doUpdate(request, response);

				if (!updateFlg.booleanValue())
					throw new Exception("Errore nell'aggiornamento della mansione");
			}
		}

		response.delAttribute("USER_MESSAGE");
		setSectionQueryUpdate("QUERY_UPDATE");
		doUpdate(request, response);
	}
}