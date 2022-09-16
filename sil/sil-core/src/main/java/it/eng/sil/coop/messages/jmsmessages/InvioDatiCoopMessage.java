/*
 * Created on 07-Feb-06
 *
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
import it.eng.sil.coop.messages.TestataMessageTO;
import it.eng.sil.coop.queues.IFaceQueue;
import it.eng.sil.coop.wsClient.messageReceiver.MessageReceiver;
import it.eng.sil.coop.wsClient.messageReceiver.MessageReceiverServiceLocator;
import it.eng.sil.coop.wsClient.messageReceiver.ServiceParameters;

/**
 * @author savino
 */
public class InvioDatiCoopMessage extends AbstractMessage implements IFaceMessage {

	private byte[] pdfdata;
	private String XMLMessage;

	private static final Logger log = Logger.getLogger(InvioDatiCoopMessage.class.getName());

	private final int maxRedeliveries = 10;

	public InvioDatiCoopMessage() {
	}

	/**
	 * @param msg
	 */
	public void setObjectMessage(ObjectMessage msg) throws Exception {
		try {
			// leggo i parametri di autenticazione/instradamento dall'ObjectMessage
			// e li setto nell'oggetto messaggio
			/*
			 * testata.setDestinazione("IR"); testata.setServizio("AccorpaLavoratoriIR"); NOTA 11/09/2006 Savino:
			 * riprendo i dati destinazione e servizio dal messaggio jms
			 */
			testata.setDestinazione(msg.getStringProperty("Destinazione"));
			testata.setServizio(msg.getStringProperty("Servizio"));

			testata.setPoloMittente(msg.getStringProperty("Polomittente"));
			testata.setCdnUtente(msg.getStringProperty("cdnUtente"));
			testata.setCdnGruppo(msg.getStringProperty("cdnGruppo"));
			testata.setCdnProfilo(msg.getStringProperty("cdnProfilo"));
			testata.setStrMittente(msg.getStringProperty("strMittente"));
			testata.setMaxRedeliveries(maxRedeliveries);
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
			Serializable arrObj = msg.getObject();
			ArrayList appParam = (ArrayList) arrObj;
			setPdfData((byte[]) appParam.get(0));
			setXMLMessage((String) appParam.get(1));
		} catch (Exception ex) {
			// Eccezione nella lettura del contenuto applicativo.
			log.fatal(
					"Errore nella lettura del contenuto applicativo. Il contenuto potrebbe essere completamente o parzialmente mancante ",
					ex);
			ex.printStackTrace();
			// non esco dalla computazione. Accetto anche un contenuto nullo.
		}
	}

	/**
	 * setServiceParameters Costruisce il messaggio a partire dai parametri di chiamata del servizio
	 * 
	 * @param param
	 *            HashMap contenente i parametri come coppie key, value
	 */
	public void setServiceParameters(Map par) {

		ServiceParameters serviceParam = new ServiceParameters(par);
		/*
		 * testata.setDestinazione("IR"); testata.setServizio("AccorpaLavoratoriIR"); NOTA 11/09/2006 Savino: riprendo i
		 * valori dal ServiceParameters
		 */
		testata.setDestinazione(serviceParam.getDestinazione());
		// testata.setServizio("InvioDatiCoop");
		testata.setServizio(serviceParam.getServizio());
		// NOTA 11/09/2006 Savino: creato metodo get/setServizio in ServiceParameter?
		// testata.setServizio(serviceParam.get);

		testata.setPoloMittente(serviceParam.getPoloMittente());
		testata.setCdnUtente(serviceParam.getCdnUtente());
		testata.setCdnGruppo(serviceParam.getCdnGruppo());
		testata.setCdnProfilo(serviceParam.getCdnProfilo());
		testata.setStrMittente(serviceParam.getStrMittente());
		testata.setMaxRedeliveries(maxRedeliveries);

		setPdfData((byte[]) serviceParam.get("PdfData"));
		setXMLMessage((String) serviceParam.get("xmlRequest"));
	}

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
		appParam.add(getPdfData());
		appParam.add(getXMLMessage());
		super.send(Q, appParam);
	}

	public void callWebservice() throws Exception {

		// nuova chiamata con servizio "dispatcher"
		MessageReceiverServiceLocator locator = null;
		locator = new MessageReceiverServiceLocator();

		EndPoint endPoint = new EndPoint();
		endPoint.init(dataSourceJndi, "MessageReceiver_" + testata.getDestinazione());
		String address = endPoint.getUrl();

		log.debug("Endpoint address: " + address);
		locator.setMessageReceiverEndpointAddress(address);
		MessageReceiver service = locator.getMessageReceiver();

		ServiceParameters serviceParam = new ServiceParameters();
		serviceParam.setCdnGruppo(testata.getCdnGruppo());
		serviceParam.setCdnProfilo(testata.getCdnProfilo());
		serviceParam.setCdnUtente(testata.getCdnUtente());
		serviceParam.setDestinazione(testata.getDestinazione());
		serviceParam.setPoloMittente(testata.getPoloMittente());
		serviceParam.setStrMittente(testata.getStrMittente());
		// NOTA 11/09/2006 Savino: serviceParam.setServizio
		serviceParam.setServizio(testata.getServizio());

		serviceParam.put("PdfData", getPdfData());
		serviceParam.put("xmlRequest", getXMLMessage());

		String retCode = service.receive(testata.getServizio(), serviceParam);

	}

	public void setTestata(TestataMessageTO _testata) {

		super.setTestata(_testata);
		/*
		 * testata.setDestinazione("IR"); testata.setServizio("AccorpaLavoratoriIR");
		 * 
		 * NOTA 11/09/2006 Savino: testata.setDestinazione("36")/setServizio("InvioDati") impostati nel modulo chiamante
		 */
		testata.setMaxRedeliveries(maxRedeliveries);

	}

	/**
	 * @return
	 */
	public byte[] getPdfData() {
		return pdfdata;
	}

	/**
	 * @param string
	 */
	public void setPdfData(byte[] b) {
		pdfdata = b;
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

}
