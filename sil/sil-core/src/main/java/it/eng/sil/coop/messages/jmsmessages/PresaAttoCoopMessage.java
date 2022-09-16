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
public class PresaAttoCoopMessage extends AbstractMessage implements IFaceMessage {

	private String xmlRequest;

	private static final Logger log = Logger.getLogger(PresaAttoCoopMessage.class.getName());

	private final int maxRedeliveries = 10;

	/*
	 * private String codiceFiscale;
	 * 
	 * private String codCpi; private String codProvinciaOp; private String codMonoTipoCpi; private String
	 * codComNascita; private String dataNascita; private String nome; private String cognome; private String
	 * codiceFiscaleNuovo; private String dataInizio;
	 */
	public PresaAttoCoopMessage() {
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
			xmlRequest = (String) appParam.get(0);
			/*
			 * codiceFiscale = (String) appParam.get(0); codiceFiscaleNuovo = (String) appParam.get(1); codProvinciaOp =
			 * (String) appParam.get(2); codCpi = (String) appParam.get(3); cognome = (String) appParam.get(4); nome =
			 * (String) appParam.get(5); dataNascita = (String) appParam.get(6); codComNascita = (String)
			 * appParam.get(7); codMonoTipoCpi = (String) appParam.get(8); dataInizio = (String) appParam.get(9);
			 */
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
		// testata.setServizio("PresaAttoCoop");
		testata.setServizio(serviceParam.getServizio());
		// TODO Savino: creato metodo get/setServizio in ServiceParameter? testata.setServizio(serviceParam.get);

		testata.setPoloMittente(serviceParam.getPoloMittente());
		testata.setCdnUtente(serviceParam.getCdnUtente());
		testata.setCdnGruppo(serviceParam.getCdnGruppo());
		testata.setCdnProfilo(serviceParam.getCdnProfilo());
		testata.setStrMittente(serviceParam.getStrMittente());
		testata.setMaxRedeliveries(maxRedeliveries);

		setXmlRequest((String) serviceParam.get("xmlRequest"));

		/*
		 * codiceFiscale=(String) serviceParam.get("codiceFiscale"); codiceFiscaleNuovo=(String)
		 * serviceParam.get("codiceFiscaleNuovo"); cognome=(String) serviceParam.get("cognome"); nome=(String)
		 * serviceParam.get("nome"); dataNascita=(String) serviceParam.get("dataNascita"); codComNascita=(String)
		 * serviceParam.get("codComNascita"); codProvinciaOp=(String) serviceParam.get("codProvinciaOp");
		 * codMonoTipoCpi=(String) serviceParam.get("codMonoTipoCpi"); codCpi=(String) serviceParam.get("codCpi");
		 * dataInizio=(String) serviceParam.get("dataInizio");
		 */

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
		appParam.add(xmlRequest);
		/*
		 * appParam.add(codiceFiscale); appParam.add(codiceFiscaleNuovo); appParam.add(codProvinciaOp);
		 * appParam.add(codCpi); appParam.add(cognome); appParam.add(nome); appParam.add(dataNascita);
		 * appParam.add(codComNascita); appParam.add(codMonoTipoCpi); appParam.add(dataInizio);
		 */
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
		// TODO Savino: aggiunto parametro servizio in ServiceParameters
		serviceParam.setServizio(testata.getServizio());
		/*
		 * param.put("codiceFiscale", codiceFiscale); param.put("codiceFiscaleNuovo", codiceFiscaleNuovo);
		 * param.put("cognome", cognome); param.put("nome", nome); param.put("dataNascita", dataNascita);
		 * param.put("codComNascita", codComNascita); param.put("codProvinciaOp", codProvinciaOp);
		 * param.put("codMonoTipoCpi", codMonoTipoCpi); param.put("codCpi", codCpi); param.put("dataInizio",
		 * dataInizio);
		 */
		serviceParam.put("xmlRequest", getXmlRequest());

		String retCode = service.receive(testata.getServizio(), serviceParam);

	}

	public void setTestata(TestataMessageTO _testata) {

		super.setTestata(_testata);
		/*
		 * testata.setDestinazione("IR"); testata.setServizio("AccorpaLavoratoriIR");
		 * 
		 * NOTA 11/09/2006 Savino: testata.setDestinazione("36")/setServizio("PresaAtto") impostati nel modulo chiamante
		 */
		testata.setMaxRedeliveries(maxRedeliveries);

	}

	/**
	 * @return
	 */
	public String getXmlRequest() {
		return xmlRequest;
	}

	/**
	 * @param string
	 */
	public void setXmlRequest(String string) {
		xmlRequest = string;
	}

}
