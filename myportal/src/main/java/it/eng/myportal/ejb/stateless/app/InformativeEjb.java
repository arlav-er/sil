package it.eng.myportal.ejb.stateless.app;

import java.io.Serializable;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONObject;

import it.eng.myportal.entity.PfPrincipal;
import it.eng.myportal.entity.UtenteInfo;
import it.eng.myportal.entity.ejb.TextSingletonEjb;
import it.eng.myportal.entity.home.PfPrincipalHome;
import it.eng.myportal.entity.home.UtenteInfoHome;
import it.eng.myportal.rest.app.CheckerSec;
import it.eng.myportal.rest.app.exception.AppEjbException;
import it.eng.myportal.rest.app.exception.EmptyParameterException;
import it.eng.myportal.rest.app.exception.GenericException;
import it.eng.myportal.rest.app.exception.InformativaNotFoundException;
import it.eng.myportal.rest.app.exception.UserNotEnabledException;
import it.eng.myportal.rest.app.exception.UserNotFoundException;
import it.eng.myportal.rest.app.exception.UserServAmmNotEnabledException;

@Stateless
public class InformativeEjb implements Serializable {

	private static final long serialVersionUID = -1849064266266950380L;
	protected static Log log = LogFactory.getLog(InformativeEjb.class);

	@EJB
	PfPrincipalHome pfPrincipalHome;

	@EJB
	UtenteInfoHome utenteInfoHome;

	@EJB
	TextSingletonEjb textMap;

	private static final String INFORMATIVA = "informativa";

	public String setInformativeLav(String username, Boolean acceptInforPercorsoLav, Boolean acceptInforDid)
			throws AppEjbException {

		String ret = null;

		try {
			// Validazione parametri
			if ((username == null || username.isEmpty())
					|| (acceptInforPercorsoLav == null && acceptInforDid == null)) {
				throw new EmptyParameterException("username", "acceptInforPercorsoLav", "acceptInforDid");
			}

			PfPrincipal pfPrincipal = pfPrincipalHome.findByUsername(username);
			if (pfPrincipal != null) {
				if (!pfPrincipal.getFlagAbilitatoServizi()) {
					throw new UserServAmmNotEnabledException(username);
				}

				UtenteInfo utenteInfo = pfPrincipal.getUtenteInfo();
				if (utenteInfo != null) {
					if (acceptInforPercorsoLav != null)
						utenteInfo.setFlgAcceptedInformativaPercLav(acceptInforPercorsoLav);
					if (acceptInforDid != null)
						utenteInfo.setFlgAcceptedInformativaDid(acceptInforDid);
					utenteInfoHome.merge(utenteInfo);

					JSONObject json = new JSONObject();
					json.put(CheckerSec.STATUS, CheckerSec.OK);

					ret = json.toString();
				} else {
					// Utente non abilitato - non cittadino
					throw new UserNotEnabledException(username);
				}
			} else {
				throw new UserNotFoundException(username);
			}
		} catch (AppEjbException e) {
			throw e;
		} catch (Exception e) {
			throw new GenericException("Errore durante il set dell'informativa");
		}
		return ret;
	}

	public String getInformativaUtente(String username, String txtInformativaKey) throws AppEjbException {
		String ret = null;

		try {

			// Validazione parametri
			if (username == null || username.isEmpty() || txtInformativaKey == null || txtInformativaKey.isEmpty()) {
				throw new EmptyParameterException("username", "txtInformativaKey");
			}

			PfPrincipal pfPrincipal = pfPrincipalHome.findByUsername(username);

			if (pfPrincipal != null) {
				String txt = textMap.get(txtInformativaKey);

				if (txt != null && !txt.contains(txtInformativaKey)) {
					JSONObject json = new JSONObject();
					json.put(CheckerSec.STATUS, CheckerSec.OK);
					json.put(INFORMATIVA, txt);

					ret = json.toString();
				} else {
					throw new InformativaNotFoundException(txtInformativaKey);
				}

			} else {
				throw new UserNotFoundException(username);
			}
		} catch (AppEjbException e) {
			throw e;
		} catch (Exception e) {
			throw new GenericException("Errore durante il recupero dell'informativa");
		}
		return ret;
	}

}
