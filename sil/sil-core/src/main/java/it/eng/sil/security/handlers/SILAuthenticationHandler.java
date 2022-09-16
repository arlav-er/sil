package it.eng.sil.security.handlers;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.Properties;
import java.util.TreeSet;

import javax.xml.soap.Name;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPFactory;

import org.apache.axis.AxisFault;
import org.apache.axis.MessageContext;
import org.apache.axis.handlers.BasicHandler;
import org.apache.log4j.Logger;

/**
 *
 * @author Franco Vuoto
 * @author Andrea Savino
 */
public class SILAuthenticationHandler extends BasicHandler {

	private static final Logger log = Logger.getLogger(SILAuthenticationHandler.class.getName());
	// private static SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");

	public void invoke(MessageContext msgContext) throws AxisFault {
		log.info("SILAuthenticationHandler::inizio");

		String username = "";
		String password = "";
		try {

			Properties props = Utility.loadProperties();
			SOAPBody sb = msgContext.getCurrentMessage().getSOAPBody();

			// ricerca operazione
			String operazione = Utility.recuperaOperazione(msgContext);

			String url = (String) msgContext.getProperty("transport.url");

			// Controllo credenziali

			boolean ok;

			// Tutte le operaiozni sono con gli handler
			// tranne quelle della protocollazione

			if (Utility.isOperazioneSenzaHandler(operazione)) {
				ok = true;
			} else {

				SOAPElement elem = Utility.find_SILAuthentication_Element(sb);

				// Preleviamo le info che interessano
				// 1. credenziali
				// 2. nomeServizio (operazione)
				// 3. url chiamata

				SOAPFactory factory = SOAPFactory.newInstance();
				Name usernameAttr = factory.createName("username", null, null);
				Name passwordAttr = factory.createName("password", null, null);

				username = elem.getAttributeValue(usernameAttr);
				password = elem.getAttributeValue(passwordAttr);

				String msg = new Date() + " Operazione:" + operazione + " url:" + url + " Utente:" + username;

				if (!"false".equalsIgnoreCase(props.getProperty("log.enabled")))
					// Utility.logInvoke(msg, props.getProperty("log.ws_in"));
					Utility.logInvoke(msg, 'I');

				boolean datiSensibili = Utility.isOperazioneConDatiSens(operazione);
				ok = controllaCredenziali(props, datiSensibili, username, password);
			}

			if (ok) {
				log.debug("SILAuthenticationHandler: richiesta AUTORIZZATA per il servizio [" + operazione + "]");
				return;
			}

		} catch (Exception e) {
			log.error("SILAuthenticationHandler: si e' verificato un errore", e);
			// e.printStackTrace();
			throw new AxisFault(e.getMessage());
		}
		log.info("SILAuthenticationHandler: richiesta NON AUTORIZZATA (" + username + ":" + password + ")");
		throw new AxisFault("(401) NON AUTORIZZATO");

	}

	private boolean controllaCredenziali(Properties props, boolean mittDatiSens, String username, String password)
			throws AxisFault {

		String propDaCercare = ".username";

		if (mittDatiSens) {
			propDaCercare = ".ds" + propDaCercare;
		} else {
			propDaCercare = ".df" + propDaCercare;
		}

		TreeSet keys = new TreeSet(props.keySet());

		Iterator iter = keys.iterator();
		while (iter.hasNext()) {
			String prop = (String) iter.next();
			if (prop.endsWith(propDaCercare)) {
				String l_username = props.getProperty(prop);
				if (username.equals(l_username)) {
					// si recupera la password
					String baseProp = prop.substring(0, prop.length() - 8);

					String propPass = baseProp + "password";
					String l_password = Utility.decrypt(props.getProperty(propPass));

					if (!password.equals(l_password)) {
						log.info("SILAuthenticationHandler: l'utente " + username + " ha fornito una password errata");
						return false;
					}

					String validFrom = props.getProperty(baseProp + "validita.da");
					String validTo = props.getProperty(baseProp + "validita.a");

					try {
						SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
						Date from = df.parse(validFrom);
						Date to = df.parse(validTo);
						Date oggi = new Date();

						if (oggi.before(from) || oggi.after(to)) {
							log.info("Account [" + username + "] scaduto");
							return false;
						}
						df = null;

					} catch (ParseException e) {
						throw new AxisFault(
								"SILAuthenticationHandler: data/e di validità non formattata/e correttamente", e);
					}
					return true;

				}

			}

		}
		log.info("SILAuthenticationHandler: username [" + username + "] non trovato!");
		return false;

	}

}