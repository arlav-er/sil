package it.eng.sil.coop.webservices.assister;

import it.eng.sil.coop.webservices.myportal.servizicittadino.utils.Credentials;
import it.eng.sil.coop.webservices.myportal.servizicittadino.utils.WsAuthUtils;

public class GestoreAutenticazione {

	public boolean checkCredenziali(String username, String password) {
		String usernameProp = "";
		String passwordProp = "";

		// recupera le credenziali dal db
		WsAuthUtils wsAuthUtils = new WsAuthUtils();
		Credentials credentials = wsAuthUtils.getCredentials("GetLavoratoreAssistER");
		if (credentials != null) {
			usernameProp = credentials.getUsername();
			passwordProp = credentials.getPassword();
		}
		if (username.equals(usernameProp) && password.equals(passwordProp)) {
			return true;
		} else {
			return false;
		}
	}
}
