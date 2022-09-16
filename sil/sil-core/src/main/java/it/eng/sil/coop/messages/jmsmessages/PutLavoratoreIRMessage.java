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
import it.eng.sil.coop.messages.AbstractMessage;
import it.eng.sil.coop.messages.IFaceMessage;
import it.eng.sil.coop.messages.TestataMessageTO;
import it.eng.sil.coop.queues.IFaceQueue;
import it.eng.sil.coop.wsClient.messageReceiver.MessageReceiver;
import it.eng.sil.coop.wsClient.messageReceiver.MessageReceiverServiceLocator;
import it.eng.sil.coop.wsClient.messageReceiver.ServiceParameters;

/**
 * @author rolfini
 *
 *         To change the template for this generated type comment go to Window&gt;Preferences&gt;Java&gt;Code
 *         Generation&gt;Code and Comments
 */
public class PutLavoratoreIRMessage extends AbstractMessage implements IFaceMessage {

	private static final Logger log = Logger.getLogger(PutLavoratoreIRMessage.class.getName());

	private final int maxRedeliveries = 100;

	private String codiceFiscale = null;
	private String nome = null;
	private String cognome = null;
	private String dataNascita = null;
	private String comune = null;
	// Aggiunto parametro data inizio=data inizio storia corrente lavoratore
	private String dataInizio = null;
	private String codCpi = null;
	private String codMonoTipoCpi = null;
	private String cpiComp = null;

	public PutLavoratoreIRMessage() {

	}

	public void setObjectMessage(ObjectMessage msg) throws Exception {

		try {
			// leggo i parametri di autenticazione/instradamento dall'ObjectMessage
			// e li setto nell'oggetto messaggio

			testata.setDestinazione("IR");
			testata.setServizio("PutLavoratoreIR");
			testata.setPoloMittente((String) msg.getStringProperty("Polomittente"));
			testata.setCdnUtente((String) msg.getStringProperty("cdnUtente"));
			testata.setCdnGruppo((String) msg.getStringProperty("cdnGruppo"));
			testata.setCdnProfilo((String) msg.getStringProperty("cdnProfilo"));
			testata.setStrMittente((String) msg.getStringProperty("strMittente"));
			testata.setMaxRedeliveries(maxRedeliveries);
		} catch (Exception ex) {
			// Eccezione nella lettura dei parametri OBBLIGATORI. Gravissimo!
			// System.out.println("PutLavoratoreIRMessage Errore nella lettura dei parametri di instradamento " +
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
			nome = (String) appParam.get(1);
			cognome = (String) appParam.get(2);
			dataNascita = (String) appParam.get(3);
			comune = (String) appParam.get(4);
			dataInizio = (String) appParam.get(5);
			codCpi = (String) appParam.get(6);
			codMonoTipoCpi = (String) appParam.get(7);
			cpiComp = (String) appParam.get(8);
		} catch (Exception ex) {
			// Eccezione nella lettura del contenuto applicativo.
			// System.out.println("PutLavoratoreIRMessage Errore nella lettura del contenuto applicativo. " +
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
		testata.setServizio("PutLavoratoreIR");
		testata.setPoloMittente(serviceParam.getPoloMittente());
		testata.setCdnUtente(serviceParam.getCdnUtente());
		testata.setCdnGruppo(serviceParam.getCdnGruppo());
		testata.setCdnProfilo(serviceParam.getCdnProfilo());
		testata.setStrMittente(serviceParam.getStrMittente());
		testata.setMaxRedeliveries(maxRedeliveries);

		codiceFiscale = (String) serviceParam.get("codiceFiscale");
		nome = (String) serviceParam.get("nome");
		cognome = (String) serviceParam.get("cognome");
		dataNascita = (String) serviceParam.get("dataNascita");
		comune = (String) serviceParam.get("comune");
		dataInizio = (String) serviceParam.get("dataInizio");
		codCpi = (String) serviceParam.get("codCpi");
		codMonoTipoCpi = (String) serviceParam.get("codMonoTipoCpi");
		cpiComp = (String) serviceParam.get("cpiComp");

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
		appParam.add(nome);
		appParam.add(cognome);
		appParam.add(dataNascita);
		appParam.add(comune);
		appParam.add(dataInizio);
		appParam.add(codCpi);
		appParam.add(codMonoTipoCpi);
		appParam.add(cpiComp);

		super.send(Q, appParam);
	}

	public void callWebservice() throws Exception {

		// vecchia chiamata
		/*
		 * ServiziLavoratoreServiceLocator locator=null; locator=new ServiziLavoratoreServiceLocator();
		 * 
		 * 
		 * EndPoint endPoint=new EndPoint(); //EjbEndPointQ endPoint=new EjbEndPointQ("IdxRegServiziLavoratore");
		 * //System.out.println("SilOutQDispatcher:: EjbEndPointQ istanziato"); log.debug("EjbEndPointQ istanziato");
		 * endPoint.init(dataSourceJndi, "IdxRegServiziLavoratore"); String address=endPoint.getUrl();//String
		 * address=endPoint.getAddress(); //System.out.println("SilOutQDispatcher:: endpoint address: "+address);
		 * log.debug("Endpoint address: "+address); locator.setServiziLavoratoreEndpointAddress(address);
		 * ServiziLavoratore service = locator.getServiziLavoratore();
		 * //System.out.println("SilOutQDispatcher: Chiamo il servizio"); log.debug("Chiamo il servizio");
		 * 
		 * 
		 * 
		 * 
		 * String gruppo=testata.getCdnGruppo(); String profilo=testata.getCdnProfilo(); String
		 * strMittente=testata.getStrMittente(); String utente=testata.getCdnUtente(); String
		 * poloMittente=testata.getPoloMittente();
		 * 
		 * String retCode = service.putLavoratoreIR(codiceFiscale, nome, cognome, dataNascita, comune, dataInizio,
		 * gruppo , profilo, strMittente, utente, codCpi, codMonoTipoCpi, cpiComp, poloMittente);
		 */

		// nuova chiamata

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
		param.put("nome", nome);
		param.put("cognome", cognome);
		param.put("dataNascita", dataNascita);
		param.put("comune", comune);
		param.put("dataInizio", dataInizio);
		param.put("codCpi", codCpi);
		param.put("codMonoTipoCpi", codMonoTipoCpi);
		param.put("cpiComp", cpiComp);

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
	public void setCognome(String string) {
		cognome = string;
	}

	/**
	 * @param string
	 */
	public void setComune(String string) {
		comune = string;
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
	public void setDataNascita(String string) {
		dataNascita = string;
	}

	/**
	 * @param string
	 */
	public void setNome(String string) {
		nome = string;
	}

	public void setTestata(TestataMessageTO _testata) {

		super.setTestata(_testata);
		testata.setDestinazione("IR");
		testata.setServizio("PutLavoratoreIR");
		testata.setMaxRedeliveries(maxRedeliveries);

	}

	/**
	 * @return
	 */
	public String getDataInizio() {
		return dataInizio;
	}

	/**
	 * @param string
	 */
	public void setDataInizio(String string) {
		dataInizio = string;
	}

}
