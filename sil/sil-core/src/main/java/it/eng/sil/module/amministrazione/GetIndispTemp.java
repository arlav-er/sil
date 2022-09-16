package it.eng.sil.module.amministrazione;

import com.engiweb.framework.base.ResponseContainer;
import com.engiweb.framework.base.SourceBean;

import it.eng.sil.module.AbstractSimpleModule;
import it.eng.sil.module.patto.StatementFromPatto;

public class GetIndispTemp extends AbstractSimpleModule {
	public void service(SourceBean request, SourceBean response) throws Exception {
		ResponseContainer responseCont = getResponseContainer();
		SourceBean serviceResponse = responseCont.getServiceResponse();
		String config = "0";
		if (serviceResponse.containsAttribute("M_Controlla_Config_Condizione.ROWS.ROW.NUM")) {
			config = serviceResponse.getAttribute("M_Controlla_Config_Condizione.ROWS.ROW.NUM").toString();
		}
		StatementFromPatto pt = new StatementFromPatto("am_indisp_temp", "AM_IND_T", "prgindisptemp");
		if (config.equals("0")) {
			pt.addCondition("AND NVL(am_indisp_temp.datfine, SYSDATE) >= SYSDATE");
		} else {
			pt.addCondition("AND TRUNC(NVL(am_indisp_temp.datfine, SYSDATE)) >= TRUNC(SYSDATE)");
		}
		String cdnLav = (String) request.getAttribute("cdnLavoratore");
		pt.setParameter("cdnLavoratore", cdnLav);
		enableMergeOnSelect(pt);
		if (config.equals("0")) {
			this.setSectionQuerySelect("QUERY_DEFAULT");
		} else {
			this.setSectionQuerySelect("QUERY_CUSTOM");
		}
		doSelect(request, response);
	}
}
