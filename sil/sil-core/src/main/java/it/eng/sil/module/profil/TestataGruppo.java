package it.eng.sil.module.profil;

import java.math.BigDecimal;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.base.SourceBeanException;

import it.eng.sil.module.AbstractSimpleModule;

public class TestataGruppo extends AbstractSimpleModule {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(TestataGruppo.class.getName());
	private String className = this.getClass().getName();

	public void service(SourceBean request, SourceBean response) {

		BigDecimal cdngruppo = null;

		try {

			cdngruppo = (BigDecimal) getServiceResponse().getAttribute("M_PROFNUOVOGRUPPO.cdnGruppo");

			/*
			 * if (cdngruppo == null) { cdngruppo = (String)
			 * getServiceResponse().getAttribute("PROFCLONAGRUPPO.row.cdnProfClonato"); }
			 */

			if (cdngruppo != null)
				// cdnprofilo=(String) request.getAttribute("cdnprofilo");
				request.updAttribute("cdngruppo", cdngruppo);

		} catch (SourceBeanException ex) {

			it.eng.sil.util.TraceWrapper.debug(_logger, className + "::service()", ex);

		}

		doSelect(request, response);

	}
}