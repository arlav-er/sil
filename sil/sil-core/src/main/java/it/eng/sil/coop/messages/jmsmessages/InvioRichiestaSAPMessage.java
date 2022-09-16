package it.eng.sil.coop.messages.jmsmessages;

import java.io.Serializable;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Map;

import javax.jms.ObjectMessage;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.apache.log4j.Logger;

import it.eng.sil.coop.endpoints.EndPoint;
import it.eng.sil.coop.messages.AbstractMessage;
import it.eng.sil.coop.messages.IFaceMessage;
import it.eng.sil.coop.messages.TestataMessageTO;
import it.eng.sil.coop.queues.IFaceQueue;
import it.eng.sil.coop.wsClient.messageReceiver.ServiceParameters;
import it.eng.sil.pojo.yg.richiestaSAP.IDSAP;
import it.eng.sil.pojo.yg.sap.LavoratoreType;
import it.gov.lavoro.servizi.servizicoapSap.ServizicoapWSProxy;

public class InvioRichiestaSAPMessage extends AbstractMessage implements IFaceMessage {

	public static final String END_POINT_NAME = "InvioRichiestaSAP";

	private static final Logger log = Logger.getLogger(InvioRichiestaSAPMessage.class.getName());

	private final int maxRedeliveries = 100;

	private String codMinSap;
	private LavoratoreType sapLavoratore;

	public InvioRichiestaSAPMessage() {
	}

	public InvioRichiestaSAPMessage(String codMinSap) {
		this.codMinSap = codMinSap;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeit.eng.sil.coop.messages.IFaceMessage#setObjectMessage(javax.jms. ObjectMessage)
	 */
	public void setObjectMessage(ObjectMessage msg) throws Exception {

		try {
			// leggo i parametri di autenticazione/instradamento
			// dall'ObjectMessage
			// e li setto nell'oggetto messaggio
			testata.setDestinazione(msg.getStringProperty("Destinazione"));
			testata.setServizio("InvioRichiestaSAP");
			testata.setMaxRedeliveries(maxRedeliveries);
			Serializable arrObj = msg.getObject();

		} catch (Exception ex) {
			log.fatal("Errore nella lettura dei parametri di instradamento "
					+ "e/o autenticazione.\nMessaggio malformato " + ex.getMessage());

			throw new Exception("Messaggio malformato nei dati di instradamento/autenticazione");
		}

		try {
			// ora leggo il contenuto applicativo
			Serializable arrObj = msg.getObject();
			if (arrObj instanceof ArrayList) {
				ArrayList<String> appParam = (ArrayList<String>) arrObj;
				codMinSap = (String) appParam.get(0);
			}
		} catch (Exception ex) {
			log.fatal("Errore nella lettura del contenuto applicativo. "
					+ "Il contenuto potrebbe essere completamente o parzialmente mancante " + ex.getMessage());
			// non esco dalla computazione. Accetto anche un contenuto nullo.
		}

	}

	public void send(IFaceQueue Q) throws Exception {
		ArrayList appParam = new ArrayList();
		appParam.add(codMinSap);

		super.send(Q, appParam);
	}

	public void callWebservice() throws Exception {
		LavoratoreType sapLav = null;
		ServiceParameters param = new ServiceParameters();
		param.setServizio("InvioRichiestaSAP");

		ServizicoapWSProxy proxy = new ServizicoapWSProxy();
		EndPoint endPoint = new EndPoint();
		endPoint.init(dataSourceJndi, END_POINT_NAME);
		String address = endPoint.getUrl();
		log.debug("Endpoint address: " + address);
		proxy.setEndpoint(address);
		try {
			IDSAP xmlCodiceSAP = new IDSAP();
			xmlCodiceSAP.setIdentificativoSap(codMinSap);

			String xmlSAP = proxy.richiestaSAP(convertRichiestaSapToString(xmlCodiceSAP));
			if (xmlSAP != null && !("").equalsIgnoreCase(xmlSAP)) {
				sapLav = convertToLavoratoreSAP(xmlSAP);
			}

			setSapLavoratore(sapLav);
		} catch (Exception e) {
			log.error("Errore nel web service di richiesta SAP", e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see it.eng.sil.coop.messages.AbstractMessage#setTestata(it.eng.sil.coop.messages .TestataMessageTO)
	 */
	public void setTestata(TestataMessageTO _testata) {
		super.setTestata(_testata);
		testata.setMaxRedeliveries(maxRedeliveries);
	}

	public void setServiceParameters(Map param) {
		ServiceParameters serviceParam = new ServiceParameters(param);
		serviceParam.put("codMinSap", codMinSap);
		testata.setServizio(serviceParam.getServizio());
	}

	public String getCodMinSap() {
		return codMinSap;
	}

	public void setCodMinSap(String codMinSap) {
		this.codMinSap = codMinSap;
	}

	public LavoratoreType getSapLavoratore() {
		return sapLavoratore;
	}

	public void setSapLavoratore(LavoratoreType sapLavoratore) {
		this.sapLavoratore = sapLavoratore;
	}

	private LavoratoreType convertToLavoratoreSAP(String xmlSAP) throws JAXBException {
		JAXBContext jaxbContext;
		LavoratoreType sap = null;
		try {

			jaxbContext = JAXBContext.newInstance(it.eng.sil.pojo.yg.sap.ObjectFactory.class);
			Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
			JAXBElement<LavoratoreType> root = (JAXBElement<LavoratoreType>) jaxbUnmarshaller
					.unmarshal(new StringReader(xmlSAP));
			sap = root.getValue();

		} catch (JAXBException e) {
			log.error("Errore durante la costruzione dell'oggetto dall'xml");
		}
		return sap;
	}

	public String convertRichiestaSapToString(IDSAP richiestaSAP) throws JAXBException {
		JAXBContext jc = JAXBContext.newInstance(IDSAP.class);
		Marshaller marshaller = jc.createMarshaller();
		StringWriter writer = new StringWriter();
		marshaller.marshal(richiestaSAP, writer);
		String xmlRichiestaSAP = writer.getBuffer().toString();
		return xmlRichiestaSAP;
	}

}
