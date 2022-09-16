package it.eng.sil.sms.gateways;

import java.util.Iterator;

import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Queue;
import javax.jms.QueueConnection;
import javax.jms.QueueConnectionFactory;
import javax.jms.QueueSender;
import javax.jms.QueueSession;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import it.eng.sil.sms.Sms;
import it.eng.sil.sms.SmsException;
import it.eng.sil.util.EncryptDecryptUtils;

/**
 * 
 * @see it.eng.sil.sms.SMSOut
 */
public class TimConsip2019Gw extends BaseGateway {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(TimConsip2019Gw.class.getName());

	public TimConsip2019Gw() {
		super();
	}

	/**
	 * Metodo per l'invio di SMS. Si occupa di inserire gli sms in uscita all'interno della coda JMS.
	 */
	public void send() throws SmsException {

		// recupera i parametri da DB
		String queue_name = (String) parameters.get("QUEUE");
		
		
		String username = (String) parameters.get("USERNAME");
		String password = (String) EncryptDecryptUtils.decrypt((String) parameters.get("PASSWORD"));

		String token = (String) parameters.get("TOKEN");
		String alias = (String) parameters.get("ALIAS");

		String proxyHost = (String) parameters.get("PROXY_HOST");
		String proxyPort = null;
		String proxyPassword = null;
		String proxyUsername = null;

		if (proxyHost != null && !"".equals(proxyHost)) {
			proxyPort = (String) parameters.get("PROXY_PORT");
			proxyPassword = (String) parameters.get("PROXY_PASSWORD");
			proxyUsername = (String) parameters.get("PROXY_USERNAME");
		}

		Iterator iter = smsList.iterator();

		Context context = null;
		QueueConnection queueConnection = null;
		QueueSession queueSession = null;
		Queue queue = null;
		QueueSender queueSender = null;
		MapMessage message = null;

		try {
			context = new InitialContext();
			QueueConnectionFactory queueFactory = (QueueConnectionFactory) context.lookup("java:/ConnectionFactory");
			queueConnection = queueFactory.createQueueConnection();
			queueSession = queueConnection.createQueueSession(false, javax.jms.Session.CLIENT_ACKNOWLEDGE);
			queue = (Queue) context.lookup("java:/jms/" +  queue_name);
			queueSender = queueSession.createSender(queue);
			context.close();
		} catch (JMSException e) {
			throw new SmsException("Eccezione JMS:" + e.getMessage());
		} catch (NamingException e) {
			throw new SmsException("Eccezione Naming" + e.getMessage());
		} // scorre tutti i messaggi da inviare
		while (iter.hasNext()) {
			Sms sms = (Sms) iter.next();
			String testoSMS = sms.getText();
			String numCell = normalizzaNumCell(sms.getCellNumber());

			try {
				// crea il messaggio inserendo informazioni su endpoint, numero di telefono e
				// testo dell'sms
				message = queueSession.createMapMessage();
				message.setStringProperty("gateway", "TIM-CONSIP-2019");
				message.setStringProperty("text", testoSMS);
				message.setStringProperty("number", numCell);

				message.setStringProperty("username", username);
				message.setStringProperty("password", password);

				message.setStringProperty("token", token);
				message.setStringProperty("alias", alias);

			} catch (JMSException e1) {
				throw new SmsException("Eccezione JMS durante la creazione del messaggio");
			}

			try {
				_logger.info("Inserisco SMS in coda");
				queueSender.send(message);
			} catch (JMSException e) {
				throw new SmsException("Eccezione JMS durante l'accodamento del messaggio");
			}

		}

		try {
			if (queueSender != null)
				queueSender.close();
		} catch (JMSException jme) {
			_logger.error("JMSException", jme);
		} finally {
			queueSender = null;
		}
		try {
			if (queueSession != null)
				queueSession.close();
		} catch (JMSException jme) {
			_logger.error("JMSException", jme);
		} finally {
			queueSession = null;
		}
		try {
			if (queueConnection != null)
				queueConnection.close();
		} catch (JMSException jme) {
			_logger.error("JMSException", jme);
		} finally {
			queueConnection = null;
		}

	}

	/**
	 * La funzione di normalizzazione sostituisce il carattere '00' con '+'
	 * 
	 * @param cell
	 *            numero non normalizzato
	 * @return numero normalizzato
	 */
	private String normalizzaNumCell(String cell) {
		if (cell.startsWith("00"))
			return "+" + cell.substring(2);

		if (cell.startsWith("+"))
			return cell;

		return "+39" + cell;

	}

	@Override
	public boolean isReachable() {
		return true;
	}

}