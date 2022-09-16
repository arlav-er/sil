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
public class ModificaAnagraficaLavoratoreIRMessage extends AbstractMessage implements IFaceMessage {

	private static final Logger log = Logger.getLogger(ModificaAnagraficaLavoratoreIRMessage.class.getName());

	private final int maxRedeliveries = 10;

	private String codiceFiscale;
	private String cognome;
	private String nome;
	private String dataNascita;
	private String codComNascita;
	private String codCpi;
	private String codProvinciaPolo;
	private String dataInizio;
	private String codMonoTipoCpi;

	/**
	 * @param msg
	 */
	public void setObjectMessage(ObjectMessage msg) throws Exception {
		try {
			// leggo i parametri di autenticazione/instradamento dall'ObjectMessage
			// e li setto nell'oggetto messaggio
			testata.setDestinazione("IR");
			testata.setServizio("ModificaAnagraficaLavoratoreIR");
			testata.setPoloMittente((String) msg.getStringProperty("Polomittente"));
			testata.setCdnUtente((String) msg.getStringProperty("cdnUtente"));
			testata.setCdnGruppo((String) msg.getStringProperty("cdnGruppo"));
			testata.setCdnProfilo((String) msg.getStringProperty("cdnProfilo"));
			testata.setStrMittente((String) msg.getStringProperty("strMittente"));
			testata.setMaxRedeliveries(maxRedeliveries);
		} catch (Exception ex) {
			// Eccezione nella lettura dei parametri OBBLIGATORI. Gravissimo!
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
			cognome = (String) appParam.get(1);
			nome = (String) appParam.get(2);
			dataNascita = (String) appParam.get(3);
			codComNascita = (String) appParam.get(4);
			codCpi = (String) appParam.get(5);
			codProvinciaPolo = (String) appParam.get(6);
			dataInizio = (String) appParam.get(7);
			codMonoTipoCpi = (String) appParam.get(8);

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
		testata.setServizio("ModificaAnagraficaLavoratoreIR");
		testata.setPoloMittente(serviceParam.getPoloMittente());
		testata.setCdnUtente(serviceParam.getCdnUtente());
		testata.setCdnGruppo(serviceParam.getCdnGruppo());
		testata.setCdnProfilo(serviceParam.getCdnProfilo());
		testata.setStrMittente(serviceParam.getStrMittente());
		testata.setMaxRedeliveries(maxRedeliveries);

		codiceFiscale = (String) serviceParam.get("codiceFiscale");
		cognome = (String) serviceParam.get("cognome");
		nome = (String) serviceParam.get("nome");
		dataNascita = (String) serviceParam.get("dataNascita");
		codComNascita = (String) serviceParam.get("codComNascita");
		codCpi = (String) serviceParam.get("codCpi");
		codProvinciaPolo = (String) serviceParam.get("codProvinciaPolo");
		dataInizio = (String) serviceParam.get("dataInizio");
		codMonoTipoCpi = (String) serviceParam.get("codMonoTipoCpi");

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
		appParam.add(cognome);
		appParam.add(nome);
		appParam.add(dataNascita);
		appParam.add(codComNascita);
		appParam.add(codCpi);
		appParam.add(codProvinciaPolo);
		appParam.add(dataInizio);
		appParam.add(codMonoTipoCpi);
		super.send(Q, appParam);
	}

	public void callWebservice() throws Exception {

		// vecchia chiamata
		/*
		 * ServiziLavoratoreServiceLocator locator = null; locator = new ServiziLavoratoreServiceLocator();
		 * 
		 * EndPoint endPoint=new EndPoint(); log.debug("EjbEndPointQ istanziato"); endPoint.init(dataSourceJndi,
		 * "IdxRegServiziLavoratore"); String address = endPoint.getUrl(); log.debug("Endpoint address: "+address);
		 * locator.setServiziLavoratoreEndpointAddress(address); ServiziLavoratore service =
		 * locator.getServiziLavoratore(); log.debug("Chiamo il servizio");
		 * 
		 * String retCode = service.modificaAnagraficaLavoratoreIR( codiceFiscale, cognome, nome, dataNascita,
		 * codComNascita, codCpi, codProvinciaPolo, dataInizio, codMonoTipoCpi, testata.getCdnGruppo(),
		 * testata.getCdnProfilo(), testata.getStrMittente(), testata.getPoloMittente(), testata.getCdnUtente() );
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
		param.put("cognome", cognome);
		param.put("nome", nome);
		param.put("dataNascita", dataNascita);
		param.put("codComNascita", codComNascita);
		param.put("codCpi", codCpi);
		param.put("codProvinciaPolo", codProvinciaPolo);
		param.put("dataInizio", dataInizio);
		param.put("codMonoTipoCpi", codMonoTipoCpi);

		String retCode = service.receive(testata.getServizio(), param);

	}

	public void setTestata(TestataMessageTO _testata) {

		super.setTestata(_testata);
		testata.setDestinazione("IR");
		testata.setServizio("ModificaAnagraficaLavoratoreIR");
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
	public String getCodCpi() {
		return codCpi;
	}

	/**
	 * @return
	 */
	public String getCodiceFiscale() {
		return codiceFiscale;
	}

	/**
	 * @return
	 */
	public String getCodProvinciaPolo() {
		return codProvinciaPolo;
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
	public void setCodProvinciaPolo(String string) {
		codProvinciaPolo = string;
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
	public void setNome(String string) {
		nome = string;
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

	/**
	 * @return
	 */
	public String getCodMonoTipoCpi() {
		return codMonoTipoCpi;
	}

	/**
	 * @param string
	 */
	public void setCodMonoTipoCpi(String string) {
		codMonoTipoCpi = string;
	}

}
