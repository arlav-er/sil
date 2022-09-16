package it.eng.sil.security.handlers.services;

import java.util.Iterator;

import javax.xml.soap.SOAPHeader;

import org.apache.axis.AxisFault;
import org.apache.axis.MessageContext;
import org.apache.axis.handlers.BasicHandler;
import org.apache.axis.message.SOAPHeaderElement;
import org.apache.log4j.Logger;

public class ReqServiceClientLogHandlerCustom extends BasicHandler {

	private static final long serialVersionUID = -4585812030626758970L;
	private static final Logger log = Logger.getLogger(ReqServiceClientLogHandlerCustom.class.getName());

	@SuppressWarnings("unchecked")
	public void invoke(MessageContext msgContext) throws AxisFault {

		try {
			SOAPHeader header = msgContext.getMessage().getSOAPHeader();
			String valueStr = null;
			for (Iterator<SOAPHeaderElement> i = header.getChildElements(); i.hasNext();) {
				SOAPHeaderElement h = i.next();
				String name = h.getLocalName();
				if (name != null && name.equalsIgnoreCase("LOG_HANDLER")) {
					valueStr = h.getValue();
					break;
				}
			}
			log.debug("Log ReqServiceClientLogHandlerCustom");
			if (valueStr != null && valueStr.equalsIgnoreCase("FULL")) {
				GenericSendService infoServizio = null;

				try {

					infoServizio = new GenericSendService(msgContext, "REQUEST");
					infoServizio.setDati(true);
					infoServizio.setMittente("SIL");

					new LogServiceClientSoapDB("REQUEST", "SERVER", infoServizio.getServizio(),
							infoServizio.getMittente(), null).invoke(msgContext);

				} catch (Exception ex) {

					log.error("Si e' verificato un errore", ex);

				}

			} else {

				it.eng.sil.security.handlers.ReqClientLogHandler req = new it.eng.sil.security.handlers.ReqClientLogHandler();
				req.init();
				req.invoke(msgContext);
			}
		} catch (Exception ex) {

			log.error("ReqServiceClientLogHandlerCustom: si e' verificato un errore", ex);

		}
	}

}
