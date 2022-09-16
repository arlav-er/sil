package it.eng.sil.module.cig;

import com.engiweb.framework.base.SourceBean;

import it.eng.sil.module.ido.GenericSelect;

public class GenericTestSelect extends GenericSelect {

	public void service(SourceBean request, SourceBean response) {

		String codCPI = (String) request.getAttribute("CODCPI");
		if ((codCPI != null) && (!"".equalsIgnoreCase(codCPI)))
			super.service(request, response);
	}
}
