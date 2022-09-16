package it.eng.sil.security.handlers.services;

import org.apache.axis.AxisFault;
import org.apache.axis.MessageContext;
import org.apache.axis.handlers.BasicHandler;
import org.apache.log4j.Logger;

public class ReqServiceClientLogHandler extends BasicHandler {

	private static final long serialVersionUID = -4585812030626758970L;
	private static final Logger log = Logger.getLogger(ReqServiceClientLogHandler.class.getName());

	public void invoke(MessageContext msgContext) throws AxisFault {

		GenericSendService infoServizio = null;

		try {

			infoServizio = new GenericSendService(msgContext, "REQUEST");
			infoServizio.setDati();
			infoServizio.setMittente("SIL");

			new LogServiceClientSoapDB("REQUEST", "SERVER", infoServizio.getServizio(), infoServizio.getMittente(),
					infoServizio.getCodiceFiscaleReq()).invoke(msgContext);

		} catch (Exception ex) {

			log.error("Si e' verificato un errore", ex);

		}

	}

}
