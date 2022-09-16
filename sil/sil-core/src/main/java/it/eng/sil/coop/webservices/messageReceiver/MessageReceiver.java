/*
 * Creato il 6-lug-06
 *
 *  con il famoso metodo CAP (Copy And Paste) dalla medesima classe contenuta 
 *  nel progetto IdxRegWeb &gt;package it.eng.sil.coop.webservices.messageReceiver
 */
package it.eng.sil.coop.webservices.messageReceiver;

import java.util.Map;

import it.eng.sil.coop.DataSourceJNDI;
import it.eng.sil.coop.messages.IFaceMessage;
import it.eng.sil.coop.queues.InQ;

/**
 * @author giliani (copy from rolfini)
 * 
 *         Per modificare il modello associato al commento di questo tipo generato, aprire
 *         Finestra&gt;Preferenze&gt;Java&gt;Generazione codice&gt;Codice e commenti
 */
public class MessageReceiver {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(MessageReceiver.class.getName());

	public String receive(String serviceName, Map param) {

		try {
			_logger.debug("MessageReceiver:receive()  serviceName=" + serviceName + ", parametri=" + param);

			DataSourceJNDI dataSourceJndi = new DataSourceJNDI();
			String dataSourceJndiName = dataSourceJndi.getJndi();

			InQ inQ = new InQ();

			IFaceMessage message = null;

			try {
				message = (IFaceMessage) Class
						.forName("it.eng.sil.coop.messages.jmsmessages." + serviceName + "Message").newInstance();
			} catch (ClassNotFoundException cnfex) {
				// classe non trovata
				it.eng.sil.util.TraceWrapper.debug(_logger,
						"MessageReceiver:receive Classe del messaggio " + serviceName + "Message non trovata!!  ",
						cnfex);

				cnfex.printStackTrace();
				return "-1"; // ritorno un codice di errore
			}

			message.setDataSourceJndi(dataSourceJndiName);

			message.setServiceParameters(param);

			message.send(inQ);

		} catch (Exception exc) {
			exc.printStackTrace();
			it.eng.sil.util.TraceWrapper.debug(_logger, "MessageReceiver:receive ", exc);

			return "-1";

		}

		return "0";
	}

}