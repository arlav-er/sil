/*
 * Created on 07-Feb-06
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package it.eng.sil.coop.messages.jmsmessages;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Map;

import javax.jms.ObjectMessage;

import org.apache.log4j.Logger;

import it.eng.sil.coop.endpoints.EndPoint;
import it.eng.sil.coop.messages.IFaceMessage;
import it.eng.sil.coop.wsClient.notificheLavoratore.NotificheLavoratore;
import it.eng.sil.coop.wsClient.notificheLavoratore.NotificheLavoratoreServiceLocator;

/**
 * @author rolfini
 *
 *         Attenzione: questa classe è leggermente più complessa delle altre: estende
 *         AbstractNotificaLavoratoreSILMessage dalla quale deriva: - gli attributi proprietari per la funzionalità - i
 *         setter ed i getter (anche getTestata) - il metodo send
 * 
 * @see AbstractNotificaLavoratoreSILMessage
 *
 */
public class NotificaLavoratoreSILMessage extends AbstractNotificaLavoratoreSILMessage implements IFaceMessage {

	private static final Logger log = Logger.getLogger(NotificaLavoratoreSILMessage.class.getName());

	public NotificaLavoratoreSILMessage() {

	}

	public void setObjectMessage(ObjectMessage msg) throws Exception {

		try {
			// leggo i parametri di autenticazione/instradamento dall'ObjectMessage
			// e li setto nell'oggetto messaggio
			testata.setDestinazione((String) msg.getStringProperty("Destinazione"));
			testata.setServizio("NotificaLavoratoreSIL");
			testata.setPoloMittente((String) msg.getStringProperty("Polomittente"));
			testata.setCdnUtente((String) msg.getStringProperty("cdnUtente"));
			testata.setCdnGruppo((String) msg.getStringProperty("cdnGruppo"));
			testata.setCdnProfilo((String) msg.getStringProperty("cdnProfilo"));
			testata.setStrMittente((String) msg.getStringProperty("strMittente"));
			testata.setMaxRedeliveries(maxRedeliveries);
		} catch (Exception ex) {
			// Eccezione nella lettura dei parametri OBBLIGATORI. Gravissimo!
			// System.out.println("NotificaLavoratoreSILMessage Errore nella lettura dei parametri di instradamento " +
			// "e/o autenticazione.\nMessaggio malformato " + ex.getMessage());
			log.fatal("Errore nella lettura dei parametri di instradamento "
					+ "e/o autenticazione.\nMessaggio malformato " + ex.getMessage());
			ex.printStackTrace();
			// esco subito dalla computazione lanciando un'eccezione (che verrà trappata dal try generale)
			throw new Exception("Messaggio malformato nei dati di instradamento/autenticazione");
		}

		try {
			// ora leggo il contenuto applicativo
			Serializable arrObj = msg.getObject();
			ArrayList appParam = (ArrayList) arrObj;

			codiceFiscale = (String) appParam.get(0);
			contenutoMessaggio = (String) appParam.get(1);
		} catch (Exception ex) {
			// Eccezione nella lettura del contenuto applicativo.
			// System.out.println("NotificaLavoratoreSILMessage Errore nella lettura del contenuto applicativo. " +
			// "Il contenuto potrebbe essere completamente o parzialmente mancante " + ex.getMessage());
			log.fatal("Errore nella lettura del contenuto applicativo. "
					+ "Il contenuto potrebbe essere completamente o parzialmente mancante " + ex.getMessage());
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
	public void setServiceParameters(Object par) {

		// TODO: implementa il metodo!
	}

	/**
	 * setServiceParameters Costruisce il messaggio a partire dai parametri di chiamata del servizio
	 * 
	 * @param param
	 *            HashMap contenente i parametri come coppie key, value
	 */
	public void setServiceParameters(Map par) {

		// TODO: implementa il metodo!
	}

	public void callWebservice() throws Exception {

		NotificheLavoratoreServiceLocator locator = null;

		// EjbEndPointQ endPoint=new EjbEndPointQ();
		EndPoint endPoint = new EndPoint();

		locator = new NotificheLavoratoreServiceLocator();

		try {
			endPoint.init(dataSourceJndi, "SilNotificheLavoratore_" + testata.getDestinazione());
		} catch (Exception ex) { // Non siamo riusciti a trovare l'endpoint.
									// l'endpoint potrebbe non essere valido o non
									// ancora censito. Facciamo in modo che il messaggio venga
									// inserito in deadQ. Non si sa mai. Tanto c'è il maxRedelivery.
			// System.out.println("ServiceCaller putNotificaLavoratore: Endpoint destinatario non trovato.");
			log.fatal("Endpoint destinatario non trovato");
			throw new java.rmi.RemoteException(
					"Endpoint del destinatario: " + testata.getDestinazione() + " non trovato od invalido");

		}

		String address = endPoint.getUrl();
		if (address != null) {
			// System.out.println("IdxRegOutQDispatcher:: endpoint address: "+address);
			log.debug("endpoint address: " + address);
			locator.setNotificheLavoratoreEndpointAddress(address);

			NotificheLavoratore service = locator.getNotificheLavoratore();
			// System.out.println("IdxRegOutQDispatcher: Chiamo il servizio");
			log.debug("Chiamo il servizio");

			String retCode = service.putNotifica(codiceFiscale, contenutoMessaggio, testata.getPoloMittente());
		}

	}

}
