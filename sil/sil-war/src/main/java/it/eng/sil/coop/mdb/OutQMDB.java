package it.eng.sil.coop.mdb;

import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;

import org.apache.log4j.Logger;

import it.eng.sil.coop.DataSourceJNDI;
import it.eng.sil.coop.messages.IFaceMessage;

/**
 * Bean implementation class for Enterprise Bean: SilOutQDispatcher
 */
@MessageDriven(activationConfig = {
		@ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue"),
		@ActivationConfigProperty(propertyName = "destination", propertyValue = "java:/jms/queue/SilOut"),
		@ActivationConfigProperty(propertyName = "maxSession", propertyValue = "1"),
		@ActivationConfigProperty(propertyName = "maxMessages", propertyValue = "1") })
@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
public class OutQMDB implements MessageListener {

	private String dataSourceName = null;

	private String getDataSourceName() {

		if (dataSourceName == null) {
			dataSourceName = new DataSourceJNDI().getJndi();
		}
		return dataSourceName;

	}

	/**
	 * onMessage
	 */
	public void onMessage(javax.jms.Message msg) {

		Logger log = Logger.getLogger(OutQMDB.class.getName());
		log.debug("onMessage invocato");

		try {
			ObjectMessage message = (ObjectMessage) msg;
			String servizio = (String) message.getStringProperty("Servizio");

			IFaceMessage silMessage = null;

			try {
				silMessage = (IFaceMessage) Class
						.forName("it.eng.sil.coop.messages.jmsmessages." + servizio + "Message")
						.getDeclaredConstructor().newInstance();
			} catch (ClassNotFoundException cnfex) {
				log.fatal("Classe [it.eng.sil.coop.messages.jmsmessages." + servizio + "] del servizio non trovata!",
						cnfex);
				return;
			}

			silMessage.setObjectMessage(message);
			silMessage.setDataSourceJndi(getDataSourceName());

			try {
				// chiamo il webservice
				silMessage.callWebservice();
			} catch (java.rmi.RemoteException rEx) {
				log.warn("Impossibile recapitare il messaggio: " + rEx.getMessage(), rEx);

				// Redelivery
				throw new RuntimeException(rEx.getMessage());

			} catch (Exception ex) {
				log.fatal("Errore nel contenuto del messaggio", ex);
			}

		} catch (Exception generalEx) {
			// Eccezione generale.
			// In genere si verifica quando il messaggio è malformato.
			// il messaggio viene automagicamente scartato.

			log.fatal("Eccezione generale del messaggio. " + generalEx.getMessage() + ". Messaggio scartato.",
					generalEx);
		}
	}

}
