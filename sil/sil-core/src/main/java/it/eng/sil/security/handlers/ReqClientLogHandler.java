package it.eng.sil.security.handlers;

import org.apache.axis.AxisFault;
import org.apache.axis.MessageContext;
import org.apache.axis.handlers.BasicHandler;
import org.apache.log4j.Logger;

/**
 * 
 * @author Franco Vuoto
 * 
 */

public class ReqClientLogHandler extends BasicHandler {

	private static final String TABELLA = "TABELLA";
	private static final String TABELLA_DEFAULT = "TS_WS_TRACCIAMENTO";

	private static final long serialVersionUID = -1720733860405128797L;
	private static final Logger log = Logger.getLogger(ReqClientLogHandler.class.getName());

	public void invoke(MessageContext msgContext) throws AxisFault {

		String tabella = (String) getOption(TABELLA);
		if (tabella == null) {
			tabella = TABELLA_DEFAULT;
		}
		// log.debug("Tabella=" + tabella );

		new LogSoapDB("REQUEST", "CLIENT", tabella).invoke(msgContext);

	}
}
