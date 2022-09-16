package it.eng.sil.module.voucher;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.SessionContainer;
import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.base.SourceBeanException;

import it.eng.sil.module.AbstractSimpleModule;
import it.eng.sil.security.User;

public class CaricaEnteCollegato extends AbstractSimpleModule {

	private static final long serialVersionUID = -1533726811526662058L;
	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(CaricaEnteCollegato.class.getName());

	private String className = this.getClass().getName();

	public void service(SourceBean request, SourceBean response) {
		RequestContainer requestContainer = getRequestContainer();
		SessionContainer sessionContainer = (SessionContainer) requestContainer.getSessionContainer();
		User user = (User) sessionContainer.getAttribute(User.USERID);
		if (user.getCodTipo().equalsIgnoreCase(Properties.SOGGETTO_ACCREDITATO)) {
			try {
				response.setAttribute("CFENTE", user.getCfUtenteCollegato());
			} catch (SourceBeanException e) {
				// TODO Auto-generated catch block
				it.eng.sil.util.TraceWrapper.debug(_logger, className + ": errore ", e);
			}
		}
	}
}
