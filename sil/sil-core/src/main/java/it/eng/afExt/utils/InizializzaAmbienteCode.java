/*
 * Created on Sep 12, 2006
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package it.eng.afExt.utils;

import java.util.Properties;

import javax.jms.ObjectMessage;
import javax.jms.Queue;
import javax.jms.QueueConnection;
import javax.jms.QueueConnectionFactory;
import javax.jms.QueueSender;
import javax.jms.QueueSession;
import javax.naming.Context;
import javax.naming.InitialContext;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.init.InitializerIFace;

import it.eng.sil.ConfigurazioneJMS;
import it.eng.sil.coop.DataSourceJNDI;
import it.eng.sil.coop.queues.InQ;

/**
 * @author savino
 * 
 *         To change the template for this generated type comment go to Window&gt;Preferences&gt;Java&gt;Code
 *         Generation&gt;Code and Comments
 */
public class InizializzaAmbienteCode implements InitializerIFace {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(InizializzaAmbienteCode.class.getName());

	private SourceBean config;

	public void init(SourceBean config) {

		System.out.println("Inizializzazione ambiente code:");

		Context context = null; // contesto standard per chiamata semplice JMS
		QueueConnection queueConnection = null; // connessione alle code
		QueueSession queueSession = null;
		Queue queue = null; // coda
		QueueSender queueSender = null;
		ObjectMessage message = null; // messaggio -> contenuto applicativo

		try {
			DataSourceJNDI dataSourceJndi = new DataSourceJNDI();
			String dataSourceJndiName = dataSourceJndi.getJndi();

			ConfigurazioneJMS configJms = ConfigurazioneJMS.getInstance();

			Properties p = new Properties();
			if (configJms.getProperty(Context.PROVIDER_URL) != null
					&& !configJms.getProperty(Context.PROVIDER_URL).equals("")) {
				p.put(Context.INITIAL_CONTEXT_FACTORY, configJms.getProperty(Context.INITIAL_CONTEXT_FACTORY));
				p.put(Context.URL_PKG_PREFIXES, configJms.getProperty(Context.URL_PKG_PREFIXES));
				p.put(Context.PROVIDER_URL, configJms.getProperty(Context.PROVIDER_URL));
			}

			_logger.info("Properties della lookup HAJNDI: " + p.toString());

			if (p.isEmpty()) {

				StringBuffer buf = new StringBuffer();
				buf.append(
						"Nel file di configurazione jms.properties non sono state impostate le seguenti proprietà: \r\n");
				buf.append(Context.INITIAL_CONTEXT_FACTORY + "\r\n");
				buf.append(Context.URL_PKG_PREFIXES + "\r\n");
				buf.append(Context.PROVIDER_URL + "\r\n");

				_logger.warn(buf.toString());

				// throw new Exception(buf.toString());
			}

			context = new InitialContext(p);

			InQ Q = new InQ();
			Q.init(dataSourceJndiName);
			QueueConnectionFactory queueFactory = (QueueConnectionFactory) context.lookup(Q.getFactory());
			queueConnection = queueFactory.createQueueConnection();
			queueSession = queueConnection.createQueueSession(false, javax.jms.Session.AUTO_ACKNOWLEDGE);
			queue = (Queue) context.lookup(Q.getQueue());
			queueSender = queueSession.createSender(queue);
			message = queueSession.createObjectMessage();
			message.setStringProperty("Servizio", "InitContestoCode");

			queueSender.send(message);
			context.close();

		} catch (Exception e) {

			it.eng.sil.util.TraceWrapper.fatal(_logger, "InizializzaAmbienteCode::init: FAIL", e);

		}

		_logger.info("InizializzaAmbienteCode::init: Messaggio inizializzazione code inviato correttamente");

	}

	public SourceBean getConfig() {
		return config;
	}

}