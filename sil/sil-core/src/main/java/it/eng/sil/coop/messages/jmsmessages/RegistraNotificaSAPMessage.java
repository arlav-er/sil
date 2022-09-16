package it.eng.sil.coop.messages.jmsmessages;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Map;

import javax.jms.ObjectMessage;

import org.apache.log4j.Logger;

import it.eng.sil.coop.messages.AbstractMessage;
import it.eng.sil.coop.messages.IFaceMessage;
import it.eng.sil.coop.queues.IFaceQueue;
import it.eng.sil.coop.wsClient.messageReceiver.ServiceParameters;

public class RegistraNotificaSAPMessage extends AbstractMessage implements IFaceMessage {

	private String XMLMessage;
	private final int maxRedeliveries = 10;
	private static final Logger log = Logger.getLogger(RegistraNotificaSAPMessage.class.getName());

	/*
	 * (non-Javadoc)
	 * 
	 * @see it.eng.sil.coop.messages.IFaceMessage#callWebservice()
	 */
	public void callWebservice() throws Exception {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see it.eng.sil.coop.messages.IFaceMessage#setObjectMessage(javax.jms.ObjectMessage)
	 */
	public void setObjectMessage(ObjectMessage msg) throws Exception {
		try {
			testata.setDestinazione(msg.getStringProperty("Destinazione"));
			testata.setServizio(msg.getStringProperty("Servizio"));

			testata.setPoloMittente(msg.getStringProperty("Polomittente"));
			testata.setCdnUtente(msg.getStringProperty("cdnUtente"));
			testata.setCdnGruppo(msg.getStringProperty("cdnGruppo"));
			testata.setCdnProfilo(msg.getStringProperty("cdnProfilo"));
			testata.setStrMittente(msg.getStringProperty("strMittente"));
			Serializable arrObj = msg.getObject();
			String datixml = (String) arrObj;
			setXMLMessage(datixml);
			testata.setMaxRedeliveries(maxRedeliveries);
		} catch (Exception ex) {
			// Eccezione nella lettura dei parametri OBBLIGATORI. Gravissimo!
			log.fatal("Errore nella lettura dei parametri di instradamento e/o autenticazione.\nMessaggio malformato ",
					ex);
			ex.printStackTrace();
			// esco subito dalla computazione lanciando un'eccezione (che verrà trappata dal try generale)
			throw new Exception("Messaggio malformato nei dati di instradamento/autenticazione");
		}

	}

	public void setServiceParameters(Map param) {
		ServiceParameters serviceParam = new ServiceParameters(param);

		testata.setDestinazione(serviceParam.getDestinazione());
		testata.setServizio(serviceParam.getServizio());
		testata.setPoloMittente(serviceParam.getPoloMittente());
		testata.setCdnUtente(serviceParam.getCdnUtente());
		testata.setCdnGruppo(serviceParam.getCdnGruppo());
		testata.setCdnProfilo(serviceParam.getCdnProfilo());
		testata.setStrMittente(serviceParam.getStrMittente());
		testata.setMaxRedeliveries(maxRedeliveries);

		setXMLMessage((String) serviceParam.get("dati_xml"));

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see it.eng.sil.coop.messages.IFaceMessage#send(it.eng.sil.coop.queues.IFaceQueue)
	 */
	public void send(IFaceQueue Q) throws Exception {
		ArrayList appParam = new ArrayList();
		appParam.add(getXMLMessage());
		super.send(Q, appParam);

	}

	public String getXMLMessage() {
		return this.XMLMessage;
	}

	public void setXMLMessage(String s) {
		this.XMLMessage = s;
	}
}
