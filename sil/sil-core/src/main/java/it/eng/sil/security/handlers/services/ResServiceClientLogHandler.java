package it.eng.sil.security.handlers.services;

import org.apache.axis.AxisFault;
import org.apache.axis.MessageContext;
import org.apache.axis.handlers.BasicHandler;
import org.apache.log4j.Logger;

public class ResServiceClientLogHandler extends BasicHandler {

	private static final long serialVersionUID = -4585812030626758970L;
	private static final Logger log = Logger.getLogger(ResServiceClientLogHandler.class.getName());

	public void invoke(MessageContext msgContext) throws AxisFault {

		GenericSendService infoServizio = null;

		try {

			infoServizio = new GenericSendService(msgContext, "RESPONSE");
			infoServizio.setDati();

			new LogServiceClientSoapDB("RESPONSE", "SERVER", infoServizio.getCodEsito(),
					infoServizio.getCodiceFiscaleReq()).invoke(msgContext);

		} catch (Exception ex) {

			log.error("Si e' verificato un errore", ex);

		}

	}

}
