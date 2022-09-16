/*
 * Creato il 1-lug-04
 *
 * Per modificare il modello associato a questo file generato, aprire
 * Finestra&gt;Preferenze&gt;Java&gt;Generazione codice&gt;Codice e commenti
 */
package it.eng.sil.module.anag;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.SessionContainer;
import com.engiweb.framework.base.SourceBean;

import it.eng.sil.module.AbstractSimpleModule;
import it.eng.sil.security.User;

/**
 * @author roccetti
 * 
 *         Recupera la descrizione del CPI dell'utente collegato
 */
public class GetDescrCpiUser extends AbstractSimpleModule {

	/**
	 * @see com.engiweb.framework.dispatching.module.AbstractModule#service(com.engiweb.framework.base.SourceBean,
	 *      com.engiweb.framework.base.SourceBean)
	 */
	public void service(SourceBean request, SourceBean response) throws Exception {

		RequestContainer requestContainer = getRequestContainer();
		SessionContainer session = requestContainer.getSessionContainer();
		User userObj = (User) session.getAttribute("@@USER@@");
		String codCpiUser = userObj.getCodRif();
		request.setAttribute("CODCPIUSER", codCpiUser);
		doSelect(request, response);
	}
}
