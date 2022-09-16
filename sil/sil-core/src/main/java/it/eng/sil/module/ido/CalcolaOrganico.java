package it.eng.sil.module.ido;

import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.utils.StringUtils;
import it.eng.sil.module.AbstractSimpleModule;

public class CalcolaOrganico extends AbstractSimpleModule {

	public void service(SourceBean request, SourceBean response) throws Exception {
		String strDataCalcolo = StringUtils.getAttributeStrNotNull(request, "dataCalcolo");
		if (strDataCalcolo.equals("")) {
			String codprovinciaOrg = StringUtils.getAttributeStrNotNull(request, "CODPROVORGANICO");
			if (codprovinciaOrg.equals("")) {
				setSectionQuerySelect("QUERY_DEFAULT");
			} else {
				setSectionQuerySelect("QUERY_PROVINCIA");
			}
		} else {
			setSectionQuerySelect("QUERY_CUSTOM");
		}
		doSelect(request, response);
	}
}