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

/**
 * 
 * @see it.eng.sil.sms.SMSOut
 * @author vuoto
 */
public class AmxSmsGw extends BaseGateway {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(AmxSmsGw.class.getName());

	public AmxSmsGw() {
		super();
	}

	/**
	 * Metodo per l'invio di SMS. Si occupa di inserire gli sms in uscita all'interno della coda JMS.
	 */
	public void send() throws SmsException {

		// recupera i parametri da DB
		String queue_name = (String) parameters.get("QUEUE");
		String endpoint = (String) parameters.get("ENDPOINT");
		String sender = (String) parameters.get("SENDER");

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
			queue = (Queue) context.lookup("java:/jms/" + queue_name);
			queueSender = queueSession.createSender(queue); // genera un
															// queuesender per
															// accodare i
															// messaggi
			context.close();
		} catch (JMSException e) {
			throw new SmsException("Eccezione JMS:" + e.getMessage());
		} catch (NamingException e) {
			throw new SmsException("Eccezione Naming" + e.getMessage());
		}

		// scorre tutti i messaggi da inviare

		Iterator iter = smsList.iterator();
		while (iter.hasNext()) {
			Sms sms = (Sms) iter.next();
			String testoSMS = sms.getText();
			String numCell = normalizzaNumCell(sms.getCellNumber());

			try {
				// crea il messaggio inserendo informazioni su endpoint, numero
				// di telefono e testo dell'sms
				message = queueSession.createMapMessage();

				message.setStringProperty("gateway", "AMXSMS");

				message.setStringProperty("endpoint", endpoint);
				message.setStringProperty("sender", sender);

				message.setStringProperty("text", testoSMS);
				message.setStringProperty("number", numCell);

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
	 * La funzione di normalizzazione sostituisce i carattere '00' con '+'
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