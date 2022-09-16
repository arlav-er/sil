/*
 * Creato il 6-lug-06
 *
 * Per modificare il modello associato a questo file generato, aprire
 * Finestra&gt;Preferenze&gt;Java&gt;Generazione codice&gt;Codice e commenti
 */
package it.eng.sil.coop.messages.jmsmessages;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Map;

import javax.jms.ObjectMessage;

import org.apache.log4j.Logger;

import it.eng.sil.coop.endpoints.EndPoint;
import it.eng.sil.coop.messages.AbstractMessage;
import it.eng.sil.coop.messages.IFaceMessage;
import it.eng.sil.coop.queues.IFaceQueue;
import it.eng.sil.coop.wsClient.messageReceiver.MessageReceiver;
import it.eng.sil.coop.wsClient.messageReceiver.MessageReceiverServiceLocator;
import it.eng.sil.coop.wsClient.messageReceiver.ServiceParameters;

/**
 * @author giuliani
 *
 *         Per modificare il modello associato al commento di questo tipo generato, aprire
 *         Finestra&gt;Preferenze&gt;Java&gt;Generazione codice&gt;Codice e commenti
 */
public class InviaMigrazioniMessage extends AbstractMessage implements IFaceMessage {

	private String XMLMessage;
	private static final Logger log = Logger.getLogger(InviaMigrazioniMessage.class.getName());
	// private Map movimento = new HashMap();
	private String destinazione = "";
	// private String sbParameter = "";
	private String sourceBeanAsString = "";

	// Costruttore
	public InviaMigrazioniMessage() {
	}

	// ====================== Metodi per i'invio del messaggio ================================

	public void callWebservice() throws Exception {

		// Recuper l'URL del WebService "dipspatcher" del polo provinciale destinatario
		EndPoint endPoint = new EndPoint();
		endPoint.init(dataSourceJndi, "MessageReceiver_" + testata.getDestinazione());
		String address = endPoint.getUrl();
		log.debug("Endpoint address: " + address);

		MessageReceiverServiceLocator locator = null;
		locator = new MessageReceiverServiceLocator();
		locator.setMessageReceiverEndpointAddress(address);
		MessageReceiver service = locator.getMessageReceiver();

		ServiceParameters param = new ServiceParameters();
		param.setCdnGruppo(testata.getCdnGruppo());
		param.setCdnProfilo(testata.getCdnProfilo());
		param.setCdnUtente(testata.getCdnUtente());
		param.setDestinazione(testata.getDestinazione());
		param.setPoloMittente(testata.getPoloMittente());
		param.setStrMittente(testata.getStrMittente());

		param.setServizio(testata.getServizio());

		param.put("xmlRequest", getXMLMessage());

		// System.out.println("InviaMigrazioniMessage -> callWebservice() param.put(...)::" + param);

		// Chiamo il servizio con relativi parametri
		String retCode = service.receive(testata.getServizio(), param);
	}

	public void setObjectMessage(ObjectMessage msg) throws Exception {
		try {
			// leggo i parametri di autenticazione/instradamento dall'ObjectMessage
			// e li setto nell'oggetto messaggio
			// testata.setDestinazione("IR");
			testata.setDestinazione(msg.getStringProperty("Destinazione"));
			testata.setServizio(msg.getStringProperty("Servizio"));
			testata.setPoloMittente(msg.getStringProperty("Polomittente"));
			testata.setCdnUtente(msg.getStringProperty("cdnUtente"));
			testata.setCdnGruppo(msg.getStringProperty("cdnGruppo"));
			testata.setCdnProfilo(msg.getStringProperty("cdnProfilo"));
			testata.setStrMittente(msg.getStringProperty("strMittente"));
			// testata.setMaxRedeliveries(maxRedeliveries);
		} catch (Exception ex) {
			// Eccezione nella lettura dei parametri OBBLIGATORI. Gravissimo!
			log.fatal("Errore nella lettura dei parametri di instradamento e/o autenticazione.\nMessaggio malformato ",
					ex);
			ex.printStackTrace();
			// esco subito dalla computazione lanciando un'eccezione (che verrà trappata dal try generale)
			throw new Exception("Messaggio malformato nei dati di instradamento/autenticazione");
		}

		try {
			// ora leggo il contenuto applicativo
			// recupero i parametri che ho impostato nella send
			Serializable arrObj = msg.getObject();
			ArrayList appParam = (ArrayList) arrObj;
			// System.out.println("InviaMigrazioniMessage -> setObjectMessage() appParam::" + appParam);
			setXMLMessage((String) appParam.get(0));

		} catch (Exception ex) {
			// Eccezione nella lettura del contenuto applicativo.
			log.fatal(
					"Errore nella lettura del contenuto applicativo. Il contenuto potrebbe essere completamente o parzialmente mancante ",
					ex);
			ex.printStackTrace();
			// non esco dalla computazione. Accetto anche un contenuto nullo.
		}
	}
	// ====================== FINE Metodi per i'invio del messaggio ================================

	// Utilizzato sia in invio che in ricezione del messaggio
	public void send(IFaceQueue Q) throws Exception {

		// Controllo se è attiva la l'interoperabilità
		// in caso contrario nonfaccio niente.
		String coopAttiva = System.getProperty("cooperazione.enabled");

		if ((coopAttiva == null) || (!coopAttiva.equalsIgnoreCase("true"))) {
			// Se NON sono in cooperazione non faccio nulla
			// Se la variabile di ambiente cooperazione.enabled non esiste
			// a default considero la cooperazione NON attiva
			return;
		}

		ArrayList appParam = new ArrayList();
		appParam.add(getXMLMessage());
		// appParam.add(sourceBeanAsString);

		super.send(Q, appParam);
		// System.out.println("InviaMigrazioniMessage -> send() appParam::" + appParam);
	}

	// ====================== Metodi per la ricezione del messaggio ================================

	public void setServiceParameters(Map param) {
		ServiceParameters serviceParam = new ServiceParameters(param);

		testata.setDestinazione(serviceParam.getDestinazione());
		testata.setServizio(serviceParam.getServizio());
		testata.setPoloMittente(serviceParam.getPoloMittente());
		testata.setCdnUtente(serviceParam.getCdnUtente());
		testata.setCdnGruppo(serviceParam.getCdnGruppo());
		testata.setCdnProfilo(serviceParam.getCdnProfilo());
		testata.setStrMittente(serviceParam.getStrMittente());
		// testata.setMaxRedeliveries(maxRedeliveries);

		// System.out.println("InviaMigrazioniMessage -> setServiceParameters() serviceParam::" + serviceParam);
		setXMLMessage((String) serviceParam.get("xmlRequest"));
	}

	// ====================== Metodi per la ricezione del messaggio ================================

	/**
	 * @return
	 */
	public String getDestinazione() {
		return destinazione;
	}

	/**
	 * @param string
	 */
	public void setDestinazione(String string) {
		destinazione = string;
	}

	/**
	 * @return
	 */
	public String getXMLMessage() {
		return this.XMLMessage;
	}

	public void setXMLMessage(String s) {
		this.XMLMessage = s;
	}

}// END Class
