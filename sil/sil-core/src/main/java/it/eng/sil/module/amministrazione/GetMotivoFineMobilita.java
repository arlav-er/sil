package it.eng.sil.module.amministrazione;

import com.engiweb.framework.base.SourceBean;

import it.eng.sil.action.report.UtilsConfig;
import it.eng.sil.module.AbstractSimpleModule;

public class GetMotivoFineMobilita extends AbstractSimpleModule {

	public void service(SourceBean serviceRequest, SourceBean serviceResponse) throws Exception {
		UtilsConfig objConfig = new UtilsConfig("MOB_DEC");
		String config = objConfig.getConfigurazioneDefault_Custom();
		if (config.equals("0")) {
			setSectionQuerySelect("QUERY_SELECT1");
		} else {
			setSectionQuerySelect("QUERY_SELECT2");
		}
		doSelect(serviceRequest, serviceResponse);
	}

}
