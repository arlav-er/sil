package it.eng.sil.security.handlers.services;

import org.apache.axis.AxisFault;
import org.apache.axis.MessageContext;
import org.apache.axis.handlers.BasicHandler;
import org.apache.log4j.Logger;

public class ReqServiceServerLogHandler extends BasicHandler {

	private static final Logger log = Logger.getLogger(ReqServiceServerLogHandler.class.getName());

	public void invoke(MessageContext msgContext) throws AxisFault {
		SendService infoServizio = null;
		try {
			infoServizio = new SendService(msgContext, "REQUEST");
			infoServizio.setDati();
			log.debug("Log ReqServiceServerLogHandler");
			new LogServiceSoapDB("REQUEST", "SERVER", infoServizio.getServizio(), infoServizio.getMittente(),
					infoServizio.getCodiceFiscaleReq()).invoke(msgContext);
		} catch (Exception ex) {
			log.error("ReqServiceServerLogHandler: si e' verificato un errore", ex);
		}
	}

}
