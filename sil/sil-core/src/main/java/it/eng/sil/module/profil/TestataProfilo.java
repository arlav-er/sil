/*
 * Creato il 28-set-04
 * Author: vuoto
 * 
 */
package it.eng.sil.module.profil;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.base.SourceBeanException;

import it.eng.sil.module.AbstractSimpleModule;

public class TestataProfilo extends AbstractSimpleModule {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(TestataProfilo.class.getName());
	private String className = this.getClass().getName();

	public void service(SourceBean request, SourceBean response) {

		String cdnprofilo = null;

		try {

			cdnprofilo = (String) getServiceResponse().getAttribute("PROFNUOVOPROFILO.row.cdnProfNuovo");

			if (cdnprofilo == null) {
				cdnprofilo = (String) getServiceResponse().getAttribute("PROFCLONAPROFILO.row.cdnProfClonato");
			}

			if (cdnprofilo != null)
				// cdnprofilo=(String) request.getAttribute("cdnprofilo");
				request.updAttribute("cdnprofilo", cdnprofilo);

		} catch (SourceBeanException ex) {

			it.eng.sil.util.TraceWrapper.debug(_logger, className + "::service()", ex);

		}

		doSelect(request, response);

	}
}