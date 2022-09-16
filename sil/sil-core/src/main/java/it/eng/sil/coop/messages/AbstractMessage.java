/*
 * Created on 07-Feb-06
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package it.eng.sil.coop.messages;

import java.util.ArrayList;
import java.util.Properties;

import javax.jms.ObjectMessage;
import javax.jms.Queue;
import javax.jms.QueueConnection;
import javax.jms.QueueConnectionFactory;
import javax.jms.QueueSender;
import javax.jms.QueueSession;
import javax.jms.Session;
import javax.naming.Context;
import javax.naming.InitialContext;

import org.apache.log4j.Logger;

import it.eng.sil.ConfigurazioneJMS;
import it.eng.sil.coop.queues.IFaceQueue;

/**
 * @author rolfini
 *
 *         To change the template for this generated type comment go to Window&gt;Preferences&gt;Java&gt;Code
 *         Generation&gt;Code and Comments
 */
public abstract class AbstractMessage {

	private static final Logger log = Logger.getLogger(AbstractMessage.class.getName());

	protected TestataMessageTO testata = new TestataMessageTO();

	// Jndi del datasource
	protected String dataSourceJndi = null;

	// variabili necessarie all'invio
	protected Context context = null; // contesto standard per chiamata semplice JMS
	protected QueueConnection queueConnection = null; // connessione alle code
	protected QueueSession queueSession = null;
	protected Queue queue = null; // coda
	protected QueueSender queueSender = null;
	protected ObjectMessage message = null; // messaggio -> contenuto applicativo

	/**
	 * COSTRUTTORI
	 *
	 */
	public AbstractMessage() {
	}

	/**
	 * SEND inizializza l'ambiente e invia il messaggio
	 * 
	 * 
	 * @param qFactoryJndi
	 *            jndi della connection factory della coda di destinazione
	 * @param qJndi
	 *            jndi della coda
	 */
	protected void send(IFaceQueue Q, ArrayList appParam) throws Exception {

		try {
			/*
			 * boolean isRunningInJboss = System.getProperty("jboss.home.dir")!=null; String HaJndiPort =
			 * System.getProperty("HA-JNDI.port"); boolean isHA_JndiEnabled = HaJndiPort!=null;
			 * 
			 * if (isRunningInJboss && isHA_JndiEnabled){
			 * 
			 * Properties p = new Properties(); p.put(Context.INITIAL_CONTEXT_FACTORY,
			 * "org.jnp.interfaces.NamingContextFactory"); p.put(Context.URL_PKG_PREFIXES,
			 * "jboss.naming:org.jnp.interfaces"); p.put(Context.PROVIDER_URL, "localhost:" + HaJndiPort); // HA-JNDI
			 * port.
			 * 
			 * context=new InitialContext(p);
			 * 
			 * }else{ context=new InitialContext(); }
			 */
			ConfigurazioneJMS config = ConfigurazioneJMS.getInstance();

			Properties p = new Properties();
			if (config.getProperty(Context.PROVIDER_URL) != null
					&& !config.getProperty(Context.PROVIDER_URL).equals("")) {
				p.put(Context.INITIAL_CONTEXT_FACTORY, config.getProperty(Context.INITIAL_CONTEXT_FACTORY));
				p.put(Context.URL_PKG_PREFIXES, config.getProperty(Context.URL_PKG_PREFIXES));
				p.put(Context.PROVIDER_URL, config.getProperty(Context.PROVIDER_URL));
			}

			log.info("Properties della lookup HAJNDI: " + p.toString());

			if (p.isEmpty()) {

				StringBuffer buf = new StringBuffer();
				buf.append(
						"Nel file di configurazione JMS.properties non sono state impostate le seguenti proprietà: \r\n");
				buf.append(Context.INITIAL_CONTEXT_FACTORY + "\r\n");
				buf.append(Context.URL_PKG_PREFIXES + "\r\n");
				buf.append(Context.PROVIDER_URL + "\r\n");

				log.warn(buf.toString());

				// throw new Exception(buf.toString());
			}
			context = new InitialContext(p);
			// inizializzo la coda
			Q.init(dataSourceJndi);

			QueueConnectionFactory queueFactory = (QueueConnectionFactory) context.lookup(Q.getFactory());
			queueConnection = queueFactory.createQueueConnection();
			queueSession = queueConnection.createQueueSession(false, Session.AUTO_ACKNOWLEDGE);
			queue = (Queue) context.lookup(Q.getQueue());
			queueSender = queueSession.createSender(queue);
			message = queueSession.createObjectMessage();
			message.setStringProperty("Destinazione", testata.getDestinazione());
			message.setStringProperty("Servizio", testata.getServizio());

			message.setStringProperty("Polomittente", testata.getPoloMittente());
			message.setStringProperty("cdnUtente", testata.getCdnUtente());
			message.setStringProperty("cdnGruppo", testata.getCdnGruppo());
			message.setStringProperty("cdnProfilo", testata.getCdnProfilo());
			message.setStringProperty("strMittente", testata.getStrMittente());

			message.setIntProperty("MaxRedeliveries", testata.getMaxRedeliveries());
			message.setIntProperty("Redeliveries", testata.getRedeliveries());

			message.setObject(appParam);
			queueSender.send(message);
			context.close();
		} catch (Exception exc) {
			// System.out.println("Errore scrittura in coda: " + qJndi + "\nfactory: " + qFactoryJndi);
			log.fatal("Errore scrittura in coda: " + Q.getQueue() + "\nfactory: " + Q.getFactory());
			exc.printStackTrace();
			throw new Exception("Errore di scrittura in coda");
		} finally {
			try {
				if (queueSession != null) {
					queueSession.close();
				}
				if (queueConnection != null) {
					queueConnection.close();
				}
				if (context != null)
					context.close();
			} catch (Exception ex) {
				log.fatal("Errore nella chiusura delle connessioni alla coda: " + Q.getQueue() + "\nfactory: "
						+ Q.getFactory(), ex);
			}
		}

	}

	public String toString() {

		String stringa = "";

		stringa = "Messaggio -- " + " Destinazione: " + testata.getDestinazione() + " Servizio: "
				+ testata.getServizio() + " PoloMittente: " + testata.getPoloMittente() + " cdnUtente: "
				+ testata.getCdnUtente() + " cdnGruppo: " + testata.getCdnGruppo() + " cdnProfilo: "
				+ testata.getCdnProfilo() + " strMittente: " + testata.getStrMittente();

		return stringa;

	}

	// getter e setter

	public void setTestata(TestataMessageTO _testata) {
		this.testata = _testata;

	}

	public void setDataSourceJndi(String _dataSourceJndi) {
		this.dataSourceJndi = _dataSourceJndi;
	}

}
