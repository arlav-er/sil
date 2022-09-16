package it.eng.sil.module.movimenti;

import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.utils.StringUtils;
import it.eng.sil.module.AbstractSimpleModule;

public class M_MovGetDettaglioCollegato extends AbstractSimpleModule {
	public M_MovGetDettaglioCollegato() {
	}

	public void service(SourceBean request, SourceBean response) {
		String prgMov = StringUtils.getAttributeStrNotNull(request, "PRGMOVIMENTOCOLL");
		if (!(prgMov.equals("") || prgMov.equalsIgnoreCase("CORRENTE"))) {
			doSelect(request, response);
		}
	}
}