package it.eng.sil.module.ido;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.base.SourceBeanException;

import it.eng.afExt.dispatching.module.impl.ListModule;

public class CMDettaglioPunteggioGradAnnuale extends ListModule {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger
			.getLogger(CMDettaglioPunteggioGradAnnuale.class.getName());

	private String className = this.getClass().getName();

	public void service(SourceBean request, SourceBean response) {

		try {
			request.delAttribute("LIST_PAGE");
		} catch (SourceBeanException e) {
			it.eng.sil.util.TraceWrapper.fatal(_logger, className + "::service()", e);
		}

		super.service(request, response);
	}

}