package it.eng.sil.security.handlers.services;

import org.apache.axis.AxisFault;
import org.apache.axis.MessageContext;
import org.apache.axis.handlers.BasicHandler;
import org.apache.log4j.Logger;

/**
 * Creata apposta per gestire il ws RequisitiAdesione per Garanzia Over
 * 
 * @author Giacomo Pandini
 *
 */
public class ResServiceServerLogHandlerNoXML extends BasicHandler {
	private static final Logger log = Logger.getLogger(ResServiceServerLogHandler.class.getName());

	public void invoke(MessageContext msgContext) throws AxisFault {
		SendService infoServizio = null;
		try {
			infoServizio = new SendService(msgContext, "RESPONSE");
			infoServizio.setDatiNoXML();

			log.debug("Log ResServiceServerLogHandler");

			new LogServiceSoapDB("RESPONSE", "SERVER", infoServizio.getCodEsito()).invoke(msgContext);
		} catch (Exception ex) {
			log.error("ResServiceServerLogHandler: si e' verificato un errore", ex);
		}
	}
}
