/*
 * Creato il 28-set-04
 * Author: vuoto
 * 
 */
package it.eng.sil.module.profil;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.base.SourceBeanException;

import it.eng.sil.module.AbstractSimpleModule;

public class TestataUtente extends AbstractSimpleModule {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(TestataUtente.class.getName());
	private String className = this.getClass().getName();

	public void service(SourceBean request, SourceBean response) {

		String cdut = (String) request.getAttribute("cdut");

		try {

			if ((cdut == null) || (cdut.equals(""))) {
				cdut = (String) getServiceResponse().getAttribute("PROFSALVAUTENTE.cdut");
				request.updAttribute("cdut", cdut);
			}

		} catch (SourceBeanException ex) {

			it.eng.sil.util.TraceWrapper.debug(_logger, className + "::service()", ex);

		}

		doSelect(request, response);

	}
}