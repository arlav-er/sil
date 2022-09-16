package it.eng.sil.module.anag;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.base.SourceBeanException;

import it.eng.afExt.utils.MessageAppender;
import it.eng.afExt.utils.MessageCodes;
import it.eng.sil.coop.webservices.myportal.servizicittadino.ServiziCittadino;
import it.eng.sil.coop.webservices.myportal.servizicittadino.ServiziCittadinoException;
import it.eng.sil.module.AbstractSimpleModule;

public class GetLavoratoreAccount extends AbstractSimpleModule {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(GetLavoratoreAccount.class.getName());

	public void service(SourceBean request, SourceBean response) {
		SourceBean listaSourceBean;
		try {
			String cdnLavoratore = (String) request.getAttribute("cdnLavoratore");

			listaSourceBean = ServiziCittadino.getAccountCittadino(cdnLavoratore);
			response.setAttribute(listaSourceBean);
		} catch (ServiziCittadinoException e) {
			_logger.error(e.getMessage(), e);
			MessageAppender.appendMessage(response, e.getMessage(), null);
			if (ServiziCittadinoException.SERVIZIO_NON_DISPONIBILE.equals(e.getMessage())) {
				try {
					response.setAttribute("ERRORE", "true");
				} catch (SourceBeanException e1) {
					_logger.error(e1.getMessage(), e1);
				}
			}
		} catch (Exception e) {
			_logger.error(e.getMessage(), e);
			MessageAppender.appendMessage(response, MessageCodes.General.OPERATION_FAIL, null);
		} // catch (Exception ex)
	}
}