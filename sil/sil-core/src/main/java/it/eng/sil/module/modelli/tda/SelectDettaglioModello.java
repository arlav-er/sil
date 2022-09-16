package it.eng.sil.module.modelli.tda;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.base.SourceBeanException;

import it.eng.afExt.utils.StringUtils;
import it.eng.sil.module.AbstractSimpleModule;

public class SelectDettaglioModello extends AbstractSimpleModule {
	/**
	 * 
	 */
	private static final long serialVersionUID = -4216758994812731277L;

	public void service(SourceBean request, SourceBean response) {
		try {
			if (!request.containsAttribute("PRGMODVOUCHER")
					|| StringUtils.isEmptyNoBlank((String) request.getAttribute("PRGMODVOUCHER"))) {
				SourceBean serResponse = getResponseContainer().getServiceResponse();
				if (serResponse.containsAttribute("M_INSERTUPDATEMODELLITDA.PRGMODVOUCHER")) {

					request.setAttribute("PRGMODVOUCHER",
							serResponse.getAttribute("M_INSERTUPDATEMODELLITDA.PRGMODVOUCHER"));

				}
			}
			doSelect(request, response);
		} catch (SourceBeanException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
