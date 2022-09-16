/*
 * Creato il 21-apr-06
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
import it.eng.sil.coop.messages.TestataMessageTO;
import it.eng.sil.coop.queues.IFaceQueue;
import it.eng.sil.coop.wsClient.messageReceiver.MessageReceiver;
import it.eng.sil.coop.wsClient.messageReceiver.MessageReceiverServiceLocator;
import it.eng.sil.coop.wsClient.messageReceiver.ServiceParameters;

/**
 * @author riccardi
 *
 *         Per modificare il modello associato al commento di questo tipo generato, aprire
 *         Finestra&gt;Preferenze&gt;Java&gt;Generazione codice&gt;Codice e commenti
 */
public class AggiornaCompExtraRegioneIRMessage extends AbstractMessage implements IFaceMessage {

	private static final Logger log = Logger.getLogger(AggiornaCompetenzaIRMessage.class.getName());

	private final int maxRedeliveries = 100;

	private String codiceFiscale = null;
	private String codCpi = null;
	private String codMonoTipoCpi = null;
	private String cpiComp = null;
	private String datInizio = null;
	private String dataValidita;
	private String cognome = null;
	private String nome = null;
	private String datNasc = null;
	private String codComNas = null;

	public AggiornaCompExtraRegioneIRMessage() {

	}

	public void setObjectMessage(ObjectMessage msg) throws Exception {

		try {
			// leggo i parametri di autenticazione/instradamento dall'ObjectMessage
			// e li setto nell'oggetto messaggio
			testata.setDestinazione("IR");
			testata.setServizio("AggiornaCompExtraRegioneIR");
			testata.setPoloMittente((String) msg.getStringProperty("Polomittente"));
			testata.setCdnUtente((String) msg.getStringProperty("cdnUtente"));
			testata.setCdnGruppo((String) msg.getStringProperty("cdnGruppo"));
			testata.setCdnProfilo((String) msg.getStringProperty("cdnProfilo"));
			testata.setStrMittente((String) msg.getStringProperty("strMittente"));
			testata.setMaxRedeliveries(maxRedeliveries);
		} catch (Exception ex) {
			// Eccezione nella lettura dei parametri OBBLIGATORI. Gravissimo!
			// System.out.println("AggiornaCompetenzaIRMessage Errore nella lettura dei parametri di instradamento " +
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
			codCpi = (String) appParam.get(1);
			codMonoTipoCpi = (String) appParam.get(2);
			cpiComp = (String) appParam.get(3);
			datInizio = (String) appParam.get(4);
			dataValidita = (String) appParam.get(5);
			cognome = (String) appParam.get(6);
			nome = (String) appParam.get(7);
			datNasc = (String) appParam.get(8);
			codComNas = (String) appParam.get(9);
		} catch (Exception ex) {
			// Eccezione nella lettura del contenuto applicativo.
			// System.out.println("AggiornaCompetenzaIRMessage Errore nella lettura del contenuto applicativo. " +
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
	public void setServiceParameters(Map par) {

		ServiceParameters serviceParam = new ServiceParameters(par);

		testata.setDestinazione("IR");
		testata.setServizio("AggiornaCompExtraRegioneIR");
		testata.setPoloMittente(serviceParam.getPoloMittente());
		testata.setCdnUtente(serviceParam.getCdnUtente());
		testata.setCdnGruppo(serviceParam.getCdnGruppo());
		testata.setCdnProfilo(serviceParam.getCdnProfilo());
		testata.setStrMittente(serviceParam.getStrMittente());
		testata.setMaxRedeliveries(maxRedeliveries);

		codiceFiscale = (String) serviceParam.get("codiceFiscale");
		codCpi = (String) serviceParam.get("codCpi");
		codMonoTipoCpi = (String) serviceParam.get("codMonoTipoCpi");
		cpiComp = (String) serviceParam.get("cpiComp");
		datInizio = (String) serviceParam.get("datInizio");
		dataValidita = (String) serviceParam.get("dataValidita");
		cognome = (String) serviceParam.get("cognome");
		nome = (String) serviceParam.get("nome");
		datNasc = (String) serviceParam.get("datNasc");
		codComNas = (String) serviceParam.get("codComNas");

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

		appParam.add(codiceFiscale);
		appParam.add(codCpi);
		appParam.add(codMonoTipoCpi);
		appParam.add(cpiComp);
		appParam.add(datInizio);
		appParam.add(dataValidita);
		appParam.add(cognome);
		appParam.add(nome);
		appParam.add(datNasc);
		appParam.add(codComNas);

		super.send(Q, appParam);
	}

	public void callWebservice() throws Exception {

		// vecchia chiamata
		/*
		 * ServiziLavoratoreServiceLocator locator=null; locator = new ServiziLavoratoreServiceLocator();
		 * 
		 * EndPoint endPoint = new EndPoint(); endPoint.init(dataSourceJndi, "IdxRegServiziLavoratore");
		 * log.debug("SilOutQDispatcherBean:: EjbEndPointQ istanziato"); String address=endPoint.getUrl();
		 * log.debug("SilOutQDispatcherBean:: endpoint address: "+address);
		 * locator.setServiziLavoratoreEndpointAddress(address); ServiziLavoratore service =
		 * locator.getServiziLavoratore(); log.debug("SilOutQDispatcherBean: Chiamo il servizio"); String retCode=
		 * service.aggiornaCompExtraRegioneIR(codiceFiscale, codCpi, codMonoTipoCpi, cpiComp, datInizio, dataValidita,
		 * cognome, nome, datNasc, codComNas, testata.getCdnGruppo(), testata.getCdnProfilo(), testata.getStrMittente(),
		 * testata.getCdnUtente(), testata.getPoloMittente());
		 */

		// nuova chiamata con servizio "dispatcher"
		MessageReceiverServiceLocator locator = null;
		locator = new MessageReceiverServiceLocator();

		EndPoint endPoint = new EndPoint();
		endPoint.init(dataSourceJndi, "MessageReceiver_" + testata.getDestinazione());
		String address = endPoint.getUrl();

		log.debug("Endpoint address: " + address);
		locator.setMessageReceiverEndpointAddress(address);
		MessageReceiver service = locator.getMessageReceiver();

		ServiceParameters param = new ServiceParameters();
		param.setCdnGruppo(testata.getCdnGruppo());
		param.setCdnProfilo(testata.getCdnProfilo());
		param.setCdnUtente(testata.getCdnUtente());
		param.setDestinazione(testata.getDestinazione());
		param.setPoloMittente(testata.getPoloMittente());
		param.setStrMittente(testata.getStrMittente());

		param.put("codiceFiscale", codiceFiscale);
		param.put("codCpi", codCpi);
		param.put("codMonoTipoCpi", codMonoTipoCpi);
		param.put("cpiComp", cpiComp);
		param.put("datInizio", datInizio);
		param.put("dataValidita", dataValidita);
		param.put("cognome", cognome);
		param.put("nome", nome);
		param.put("datNasc", datNasc);
		param.put("codComNas", codComNas);

		String retCode = service.receive(testata.getServizio(), param);

	}

	/**
	 * @param string
	 */
	public void setCodCpi(String string) {
		codCpi = string;
	}

	/**
	 * @param string
	 */
	public void setCodiceFiscale(String string) {
		codiceFiscale = string;
	}

	/**
	 * @param string
	 */
	public void setCodMonoTipoCpi(String string) {
		codMonoTipoCpi = string;
	}

	/**
	 * @param string
	 */
	public void setCpiComp(String string) {
		cpiComp = string;
	}

	/**
	 * @param string
	 */
	public void setDatInizio(String string) {
		datInizio = string;
	}

	/**
	 * @param string
	 */
	public void setCognome(String string) {
		cognome = string;
	}

	/**
	 * @param string
	 */
	public void setNome(String string) {
		nome = string;
	}

	/**
	 * @param string
	 */
	public void setDatNasc(String string) {
		datNasc = string;
	}

	/**
	 * @param string
	 */
	public void setCodComNas(String string) {
		codComNas = string;
	}

	public void setTestata(TestataMessageTO _testata) {

		super.setTestata(_testata);
		testata.setDestinazione("IR");
		testata.setServizio("AggiornaCompExtraRegioneIR");
		testata.setMaxRedeliveries(maxRedeliveries);

	}

	/**
	 * @return
	 */
	public String getDataValidita() {
		return dataValidita;
	}

	/**
	 * @param string
	 */
	public void setDataValidita(String string) {
		dataValidita = string;
	}

}
