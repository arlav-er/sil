package it.eng.myportal.auth;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.xml.soap.Name;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPFactory;

import org.apache.axis.AxisFault;
import org.apache.axis.MessageContext;
import org.apache.axis.handlers.BasicHandler;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class SILClientAuthenticationHandler extends BasicHandler {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5294544376040306087L;
	/**
	 * Authenticate the user and password from the msgContext
	 */

	protected final Log log = LogFactory.getLog(this.getClass());
	
	public void invoke(MessageContext msgContext) throws AxisFault {

		try {
			log.debug("SILClientAuthenticationHandler: richiesta da autenticare");
			SOAPBody sb = msgContext.getCurrentMessage().getSOAPBody();

			// ricerca operazione			
	//		String url = (String) msgContext.getProperty("transport.url");

			// Tutte le operaiozni sono con gli handler
			// tranne quelle della protocollazione			
			String[] mittente = getMittente();

		//	String msg = new Date() + " Url:" + url + " Utente:" + mittente[0];
			
			SOAPElement elem = sb.addChildElement("SILAuthentication", "soapenv");

			SOAPFactory factory = SOAPFactory.newInstance();
			Name username = factory.createName("username", null, null);
			elem.addAttribute(username, mittente[0]);

			Name password = factory.createName("password", null, null);
			elem.addAttribute(password, mittente[1]);
			
			log.debug("SILClientAuthenticationHandler: richiesta AUTORIZZATA per il servizio [ IDXREG ]");
			return;

		} catch (Exception e) {
			String msg = "SILClientAuthenticationHandler: errore " + e.getMessage();
			throw new AxisFault(msg);
		}
	}	
	
	/**
	 * @return un vettore di due elementi (username, password)
	 */
	private String[] getMittente() throws AxisFault {

		// utente INDICE REGIONALE
		String username = "idxrer";
		String password = "465UE5U8u";
		String validFrom = "01/01/2005";
		String validTo = "31/12/2020";

		try {
			SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
			Date from = df.parse(validFrom);
			Date to = df.parse(validTo);
			Date oggi = new Date();

			if (oggi.before(from) || oggi.after(to)) {
				throw new AxisFault("SILClientAuthenticationHandler: account " + username + "scaduto");
			}
			df = null;
		} catch (ParseException e) {
			throw new AxisFault("SILClientAuthenticationHandler: data/e di validità non formattata/e correttamente", e);
		}

		String[] mitt = new String[2];

		mitt[0] = username;
		mitt[1] = password;

		return mitt;
	}
	

}