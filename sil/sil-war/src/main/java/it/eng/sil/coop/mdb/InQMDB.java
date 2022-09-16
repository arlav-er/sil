// SIL INQ

package it.eng.sil.coop.mdb;

import java.io.Serializable;

import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.jms.JMSException;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;

import org.apache.log4j.Logger;

import com.engiweb.framework.configuration.ConfigSingleton;

import it.eng.sil.coop.CoopApplicationException_Lavoratore;
import it.eng.sil.coop.DataSourceJNDI;
import it.eng.sil.coop.messages.TestataMessageTO;
import it.eng.sil.coop.messages.jmsmessages.NotificaLavoratoreSILMessage;
import it.eng.sil.coop.queues.OutQ;
import it.eng.sil.coop.services.IFaceService;

/**
 * Bean implementation class for Enterprise Bean: InQMDBeanMDBean
 */
@MessageDriven(activationConfig = {
		@ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue"),
		@ActivationConfigProperty(propertyName = "destination", propertyValue = "java:/jms/queue/SilIn"),
		@ActivationConfigProperty(propertyName = "maxSession", propertyValue = "1"),
		@ActivationConfigProperty(propertyName = "maxMessages", propertyValue = "1") })
@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
public class InQMDB implements MessageListener {

	/**
	 * onMessage
	 */
	@Override
	public void onMessage(javax.jms.Message msg) {

		// alla prima chiamata si avvia ed inizializza la configurazione del contesto EJB
		ConfigSingleton.getInstanceAndInit();

		String operazione = null;
		// solo dopo la inizializzazione del log riprendo il logger
		Logger log = Logger.getLogger(InQMDB.class.getName());
		try {

			if (msg.getJMSRedelivered()) { // se il messaggio è in fase di redelivery,
											// qualcosa è andato storto ed è stato rimesso in coda
											// quindi, lo scarichiamo.
				log.warn("Coda SIL: Messaggio in redelivery, lo scarichiamo. Messaggio JMS:" + msg);
				return;
			}

			// System.out.println("InQMDB::onMessage invocato");
			log.debug("Coda SIL: onMessage invocato. Messaggio JMS:" + msg);

			ObjectMessage message = (ObjectMessage) msg;
			Serializable arrObj = message.getObject();

			log.debug("Coda SIL: onMessage invocato. Oggetto messaggio JMS: " + arrObj);

			operazione = message.getStringProperty("Servizio");

			IFaceService servizio = null;

			try {
				servizio = (IFaceService) Class.forName("it.eng.sil.coop.services." + operazione).getDeclaredConstructor().newInstance();
			} catch (ClassNotFoundException cnfex) {
				// classe non trovata
				log.fatal("Coda SIL: classe service it.eng.sil.coop.services." + operazione
						+ " non trovata. Messaggio JMS:" + msg, cnfex);
				cnfex.printStackTrace();
				return;
			}

			try {
				servizio.send(msg);
			} catch (CoopApplicationException_Lavoratore cex) {
				// questa eccezione esegue il layer delle notifiche
				log.warn("Coda SIL: catturata CoopApplicationException_Lavoratore. Chiamato il layer delle notifiche:"
						+ cex.getMessage());

				// String dataSourceJndi = new EjbDbConnection().getDataSourceJndi();
				// TODO Savino: NEW JNDI
				String dataSourceJndi = new DataSourceJNDI().getJndi();
				String poloDestinatario = message.getStringProperty("Polomittente");
				String poloMittente = message.getStringProperty("Destinatario");
				TestataMessageTO testataMessaggio = new TestataMessageTO();

				testataMessaggio.setDestinazione(poloDestinatario);
				testataMessaggio.setPoloMittente(poloMittente);

				NotificaLavoratoreSILMessage notificaLavoratoreSILMessage = new NotificaLavoratoreSILMessage();
				notificaLavoratoreSILMessage.setTestata(testataMessaggio);
				notificaLavoratoreSILMessage.setDataSourceJndi(dataSourceJndi);

				String contenutoMsg = cex.getMessage();

				notificaLavoratoreSILMessage.setCodiceFiscale(cex.getCodiceFiscale());
				notificaLavoratoreSILMessage.setContenutoMessaggio(contenutoMsg);

				OutQ outQ = new OutQ();

				notificaLavoratoreSILMessage.send(outQ);

			} catch (JMSException ex) {
				log.fatal("Coda SIL: Errore nella chiamata del servizio " + operazione + ". Messaggio JMS:" + msg, ex);
				ex.printStackTrace();
			}
		} catch (Exception ex) {
			log.fatal("Coda SIL: Errore generico nell'esecuzione del servizio " + operazione + ". Messaggio JMS:" + msg,
					ex);
			ex.printStackTrace();
		}
	}

}
