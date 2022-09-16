package it.eng.sil.module.modelli.tda;

import com.engiweb.framework.base.SourceBean;

import it.eng.sil.module.AbstractSimpleModule;

public class GetObiettiviMisureFromServizio extends AbstractSimpleModule {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8467484079791010316L;

	@Override
	public void service(SourceBean request, SourceBean response) throws Exception {

		String codServizio = (String) request.getAttribute("codServizioSel");
		if (codServizio != null && !codServizio.equals("")) {
			doSelect(request, response);
		}
	}

}
