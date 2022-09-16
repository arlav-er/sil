package it.eng.sil.util.batch.mdb;

import javax.jms.JMSException;
import javax.jms.ObjectMessage;
import javax.jms.Queue;
import javax.jms.QueueConnection;
import javax.jms.QueueConnectionFactory;
import javax.jms.QueueSender;
import javax.jms.QueueSession;
import javax.jms.Session;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class BatchMDBHelper {

	private final Log log = LogFactory.getLog(this.getClass());

	private final String CO_CNN_FACTORY = "java:/ConnectionFactory";
	private final String CO_QUEUE_NAME = "java:/jms/queue/BatchQueue";
	private QueueConnectionFactory coQueueConnectionFactory;
	private QueueConnection coQueueConnection;
	private QueueSession coQueueSession;
	private Queue coQueue;
	private QueueSender coQueueSender;

	private void initQueue() throws Exception {
		try {
			InitialContext ctx = new InitialContext();
			coQueueConnectionFactory = (QueueConnectionFactory) ctx.lookup(CO_CNN_FACTORY);
			coQueueConnection = coQueueConnectionFactory.createQueueConnection();
			coQueueSession = coQueueConnection.createQueueSession(false, Session.AUTO_ACKNOWLEDGE);
			coQueue = (Queue) ctx.lookup(CO_QUEUE_NAME);
			coQueueSender = coQueueSession.createSender(coQueue);
			ctx.close();

		} catch (NamingException | JMSException e) {
			log.error("Errore in inizializzazione coda dei batch");
			throw new Exception("Errore in inizializzazione coda dei batch");
		}
	}

	private void closeQueue() throws Exception {
		try {
			coQueueSession.close();
			coQueueSender.close();
			coQueueConnection.close();
		} catch (JMSException e) {
			log.error("Errore in chiusura coda dei batch: " + e);
			throw new Exception("Errore in chiusura coda dei batch");
		}
	}

	public void enqueue(BatchObject batchObject) throws Exception {
		initQueue();

		try {
			log.info("Inizio inserimento in coda batch di " + batchObject);

			ObjectMessage objectMessage = coQueueSession.createObjectMessage();
			objectMessage.setObject(batchObject);
			coQueueConnection.start();
			coQueueSender.send(objectMessage);

			log.info("Fine inserimento in coda batch di " + batchObject);
		} catch (JMSException e) {
			log.error("Errore in inserimento in coda dei batch");
			throw new Exception("Errore in inserimento in coda dei batch");
		} finally {
			closeQueue();

		}
	}
}
