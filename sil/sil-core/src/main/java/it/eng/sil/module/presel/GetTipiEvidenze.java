package it.eng.sil.module.presel;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.SessionContainer;
import com.engiweb.framework.base.SourceBean;

import it.eng.sil.module.AbstractSimpleModule;
import it.eng.sil.security.User;

public class GetTipiEvidenze extends AbstractSimpleModule {
	public void service(SourceBean request, SourceBean response) {

		// prelevo l'utente dalla session
		RequestContainer requestContainer = getRequestContainer();
		SessionContainer sessionContainer = requestContainer.getSessionContainer();
		User user = (User) sessionContainer.getAttribute(User.USERID);
		// prelevo il gruppo ed il profilo
		Object cdnGruppo = new Integer(user.getCdnGruppo());
		Object cdnProfilo = new Integer(user.getCdnProfilo());

		// riempio la request come vuole il modulo
		try {
			setKeyinRequest("CDNGRUPPO", cdnGruppo, request);
			setKeyinRequest("CDNPROFILO", cdnProfilo, request);
		} catch (Exception ex) {
			// do nothing
		}
		doSelect(request, response);

	}

	private void setKeyinRequest(String keyName, Object key, SourceBean request) throws Exception {
		if (request.getAttribute(keyName) != null) {
			request.delAttribute(keyName);
		}
		request.setAttribute(keyName, key);
	}

}