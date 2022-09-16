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
public class ModificaCPICompIRMessage extends AbstractMessage implements IFaceMessage {

	private static final Logger log = Logger.getLogger(ModificaCPICompIRMessage.class.getName());

	private final int maxRedeliveries = 100;

	private String codiceFiscale;
	private String codProvinciaOp;
	private String codCpiNuovo;

	private String dataRichiesta;
	private String cognome;
	private String nome;
	private String dataNascita;
	private String codComNascita;
	private String codCpiPrec;

	public ModificaCPICompIRMessage() {
	}

	/**
	 * @param msg
	 */
	public void setObjectMessage(ObjectMessage msg) throws Exception {
		try {
			// leggo i parametri di autenticazione/instradamento dall'ObjectMessage
			// e li setto nell'oggetto messaggio
			testata.setDestinazione("IR");
			testata.setServizio("ModificaCPICompIR");
			testata.setPoloMittente((String) msg.getStringProperty("Polomittente"));
			testata.setCdnUtente((String) msg.getStringProperty("cdnUtente"));
			testata.setCdnGruppo((String) msg.getStringProperty("cdnGruppo"));
			testata.setCdnProfilo((String) msg.getStringProperty("cdnProfilo"));
			testata.setStrMittente((String) msg.getStringProperty("strMittente"));
			testata.setMaxRedeliveries(maxRedeliveries);
		} catch (Exception ex) {
			// Eccezione nella lettura dei parametri OBBLIGATORI. Gravissimo!
			log.fatal("Errore nella lettura dei parametri di instradamento "
					+ "e/o autenticazione.\nMessaggio malformato ", ex);
			ex.printStackTrace();
			// esco subito dalla computazione lanciando un'eccezione (che verrà trappata dal try generale)
			throw new Exception("Messaggio malformato nei dati di instradamento/autenticazione");
		}

		try {
			// ora leggo il contenuto applicativo
			Serializable arrObj = msg.getObject();
			ArrayList appParam = (ArrayList) arrObj;

			this.codiceFiscale = (String) appParam.get(0);
			this.codProvinciaOp = (String) appParam.get(1);
			this.codCpiNuovo = (String) appParam.get(2);
			this.dataRichiesta = (String) appParam.get(3);
			this.cognome = (String) appParam.get(4);
			this.nome = (String) appParam.get(5);
			this.codComNascita = (String) appParam.get(6);
			this.dataNascita = (String) appParam.get(7);
			this.codCpiPrec = (String) appParam.get(8);
		} catch (Exception ex) {
			// Eccezione nella lettura del contenuto applicativo.
			log.fatal("Errore nella lettura del contenuto applicativo. "
					+ "Il contenuto potrebbe essere completamente o parzialmente mancante ", ex);
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
		testata.setServizio("ModificaCPICompIR");
		testata.setPoloMittente(serviceParam.getPoloMittente());
		testata.setCdnUtente(serviceParam.getCdnUtente());
		testata.setCdnGruppo(serviceParam.getCdnGruppo());
		testata.setCdnProfilo(serviceParam.getCdnProfilo());
		testata.setStrMittente(serviceParam.getStrMittente());
		testata.setMaxRedeliveries(maxRedeliveries);

		codiceFiscale = (String) serviceParam.get("codiceFiscale");
		codProvinciaOp = (String) serviceParam.get("codProvinciaOp");
		codCpiNuovo = (String) serviceParam.get("codCpiNuovo");
		dataRichiesta = (String) serviceParam.get("dataRichiesta");
		cognome = (String) serviceParam.get("cognome");
		nome = (String) serviceParam.get("nome");
		dataNascita = (String) serviceParam.get("dataNascita");
		codComNascita = (String) serviceParam.get("codComNascita");
		codCpiPrec = (String) serviceParam.get("codCpiPrec");

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
		appParam.add(codProvinciaOp);
		appParam.add(codCpiNuovo);

		appParam.add(dataRichiesta);
		appParam.add(cognome);
		appParam.add(nome);
		appParam.add(codComNascita);
		appParam.add(dataNascita);
		appParam.add(codCpiPrec);

		super.send(Q, appParam);

	}

	public void callWebservice() throws Exception {

		// Vecchia chiamata
		/*
		 * ServiziLavoratoreServiceLocator locator = null; locator = new ServiziLavoratoreServiceLocator();
		 * 
		 * EndPoint endPoint=new EndPoint(); log.debug("EjbEndPointQ istanziato"); endPoint.init(dataSourceJndi,
		 * "IdxRegServiziLavoratore"); String address = endPoint.getUrl(); log.debug("Endpoint address: "+address);
		 * locator.setServiziLavoratoreEndpointAddress(address); ServiziLavoratore service =
		 * locator.getServiziLavoratore(); log.debug("Chiamo il servizio");
		 * 
		 * String retCode = service.modificaCPICompIR( codiceFiscale, codProvinciaOp,
		 * 
		 * dataRichiesta , cognome, nome , dataNascita , codComNascita , codCpiPrec ,
		 * 
		 * codCpiNuovo, testata.getCdnGruppo(), testata.getCdnProfilo(), testata.getStrMittente(),
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
		param.put("codProvinciaOp", codProvinciaOp);
		param.put("codCpiNuovo", codCpiNuovo);
		param.put("dataRichiesta", dataRichiesta);
		param.put("cognome", cognome);
		param.put("nome", nome);
		param.put("dataNascita", dataNascita);
		param.put("codComNascita", codComNascita);
		param.put("codCpiPrec", codCpiPrec);

		String retCode = service.receive(testata.getServizio(), param);

	}

	/**
	 * @param string
	 */
	public void setCodProvinciaOp(String string) {
		codProvinciaOp = string;
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
	public void setCodCpiNuovo(String string) {
		codCpiNuovo = string;
	}

	public void setTestata(TestataMessageTO _testata) {

		super.setTestata(_testata);
		testata.setDestinazione("IR");
		testata.setServizio("ModificaCPICompIR");
		testata.setMaxRedeliveries(maxRedeliveries);

	}

	/**
	 * @return
	 */
	public String getCodComNascita() {
		return codComNascita;
	}

	/**
	 * @return
	 */
	public String getCognome() {
		return cognome;
	}

	/**
	 * @return
	 */
	public String getDataNascita() {
		return dataNascita;
	}

	/**
	 * @return
	 */
	public String getDataRichiesta() {
		return dataRichiesta;
	}

	/**
	 * @return
	 */
	public String getNome() {
		return nome;
	}

	/**
	 * @param string
	 */
	public void setCodComNascita(String string) {
		codComNascita = string;
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
	public void setDataNascita(String string) {
		dataNascita = string;
	}

	/**
	 * @param string
	 */
	public void setDataRichiesta(String string) {
		dataRichiesta = string;
	}

	/**
	 * @param string
	 */
	public void setNome(String string) {
		nome = string;
	}

	/**
	 * @return
	 */
	public String getCodCpiPrec() {
		return codCpiPrec;
	}

	/**
	 * @param string
	 */
	public void setCodCpiPrec(String string) {
		codCpiPrec = string;
	}

}
