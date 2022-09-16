package it.eng.sil.module.anag;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.base.SourceBeanException;

import it.eng.afExt.utils.CF_utils;
import it.eng.afExt.utils.CfException;
import it.eng.sil.module.AbstractSimpleModule;

public class InsertDataEComune extends AbstractSimpleModule {

	public void service(SourceBean request, SourceBean response) {

		String codicefiscale = (String) request.getAttribute("strcodicefiscale");
		try {
			String ncodicefiscale = CF_utils.verificaCF(codicefiscale);
			if (!ncodicefiscale.equalsIgnoreCase(codicefiscale)) {
				request.delAttribute("strcodicefiscale");
				request.setAttribute("strcodicefiscale", ncodicefiscale);
			}
			doSelect(request, response);
		} catch (CfException e) {
			setError(response);
		} catch (SourceBeanException e) {
			// TODO Auto-generated catch block
			setError(response);
		}

	}

	private void setError(SourceBean response) {
		try {
			response.setAttribute("ERROR_ID", "1");
		} catch (SourceBeanException e) {
		}
	}
}