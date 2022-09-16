package it.eng.sil.module.delega;

import com.engiweb.framework.base.SessionContainer;
import com.engiweb.framework.base.SourceBean;

import it.eng.sil.module.AbstractSimpleModule;
import it.eng.sil.security.User;

public class DelegaAttivaTipoGruppo extends AbstractSimpleModule {

	public void service(SourceBean request, SourceBean response) throws Exception {
		SessionContainer sessionContainer = getRequestContainer().getSessionContainer();
		User user = (User) sessionContainer.getAttribute(User.USERID);
		int cdnTipoGruppo = user.getCdnTipoGruppo();
		request.setAttribute("TIPOGRUPPOUSER", cdnTipoGruppo);
		doSelect(request, response);
	}

}
