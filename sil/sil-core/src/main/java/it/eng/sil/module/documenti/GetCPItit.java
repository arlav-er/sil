package it.eng.sil.module.documenti;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.SessionContainer;
import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.base.SourceBeanException;

import it.eng.sil.module.AbstractSimpleModule;
import it.eng.sil.security.User;

public class GetCPItit extends AbstractSimpleModule {
	public void service(SourceBean request, SourceBean response) throws SourceBeanException {

		RequestContainer requestContainer = getRequestContainer();
		SessionContainer sessionContainer = requestContainer.getSessionContainer();
		User user = (User) sessionContainer.getAttribute(User.USERID);
		request.setAttribute("CODCPI", user.getCodRif());
		doSelect(request, response);
	}

}// class GetCPItit
