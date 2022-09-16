package it.eng.sil.module.amministrazione;

import com.engiweb.framework.base.ResponseContainer;
import com.engiweb.framework.base.SourceBean;

import it.eng.sil.module.AbstractSimpleModule;

public class GetIndispStoricizzate extends AbstractSimpleModule {

	public void service(SourceBean request, SourceBean response) {
		ResponseContainer responseCont = getResponseContainer();
		SourceBean serviceResponse = responseCont.getServiceResponse();
		String config = "0";
		if (serviceResponse.containsAttribute("M_Controlla_Config_Condizione.ROWS.ROW.NUM")) {
			config = serviceResponse.getAttribute("M_Controlla_Config_Condizione.ROWS.ROW.NUM").toString();
		}
		if (config.equals("0")) {
			this.setSectionQuerySelect("QUERY_DEFAULT");
		} else {
			this.setSectionQuerySelect("QUERY_CUSTOM");
		}
		doSelect(request, response);
	}
}