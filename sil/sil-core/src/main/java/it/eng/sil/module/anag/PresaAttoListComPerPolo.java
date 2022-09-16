/*
 * Creato il 18-ott-04
 */
package it.eng.sil.module.anag;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.SessionContainer;
import com.engiweb.framework.base.SourceBean;

import it.eng.sil.module.AbstractSimpleModule;
import it.eng.sil.security.User;

/**
 * @author rolfini featuring roccetti
 */
public class PresaAttoListComPerPolo extends AbstractSimpleModule {

	public void service(SourceBean request, SourceBean response) throws Exception {
		RequestContainer requestContainer = getRequestContainer();
		SessionContainer session = requestContainer.getSessionContainer();
		User userObj = (User) session.getAttribute("@@USER@@");
		String codCpiUser = userObj.getCodRif();
		request.setAttribute("CODCPIUSER", codCpiUser);
		doSelect(request, response);
	}
}
