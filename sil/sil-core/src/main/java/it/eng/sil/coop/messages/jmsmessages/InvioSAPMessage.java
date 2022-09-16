package it.eng.sil.coop.messages.jmsmessages;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Map;

import javax.jms.ObjectMessage;
import javax.xml.rpc.holders.StringHolder;

import org.apache.log4j.Logger;

import it.eng.sil.coop.endpoints.EndPoint;
import it.eng.sil.coop.messages.AbstractMessage;
import it.eng.sil.coop.messages.IFaceMessage;
import it.eng.sil.coop.messages.TestataMessageTO;
import it.eng.sil.coop.queues.IFaceQueue;
import it.eng.sil.coop.wsClient.messageReceiver.ServiceParameters;
import it.gov.lavoro.servizi.servizicoapSap.ServizicoapWSProxy;
import it.gov.lavoro.servizi.servizicoapSap.types.holders.Risposta_invioSAP_TypeEsitoHolder;

public class InvioSAPMessage extends AbstractMessage implements IFaceMessage {

	public static final String END_POINT_NAME = "InvioSAP";

	private static final Logger log = Logger.getLogger(InvioSAPMessage.class.getName());

	private final int maxRedeliveries = 100;

	private String datiSapXml = null;

	private String prgSap;

	public InvioSAPMessage() {
	}

	public InvioSAPMessage(String prgSap) {
		this.prgSap = prgSap;
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
			testata.setServizio("InvioSAP");
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
				prgSap = (String) appParam.get(0);
				datiSapXml = (String) appParam.get(4);
			}
		} catch (Exception ex) {
			log.fatal("Errore nella lettura del contenuto applicativo. "
					+ "Il contenuto potrebbe essere completamente o parzialmente mancante " + ex.getMessage());
			// non esco dalla computazione. Accetto anche un contenuto nullo.
		}

	}

	public void send(IFaceQueue Q) throws Exception {
		ArrayList appParam = new ArrayList();

		appParam.add(prgSap);
		appParam.add(datiSapXml);

		super.send(Q, appParam);
	}

	public void callWebservice() throws Exception {

		ServiceParameters param = new ServiceParameters();
		param.setServizio("InvioSAP");
		param.put("xmlRequest", datiSapXml);

		ServizicoapWSProxy proxy = new ServizicoapWSProxy();
		EndPoint endPoint = new EndPoint();
		endPoint.init(dataSourceJndi, END_POINT_NAME);
		String address = endPoint.getUrl();
		log.debug("Endpoint address: " + address);
		proxy.setEndpoint(address);
		try {
			Risposta_invioSAP_TypeEsitoHolder esito = new Risposta_invioSAP_TypeEsitoHolder();
			StringHolder messaggioErrore = new StringHolder();
			StringHolder codiceSAP = new StringHolder();

			proxy.invioSAP(datiSapXml, esito, messaggioErrore, codiceSAP);
			log.debug("Valore tornato dal WS invioSAP: " + esito);

		} catch (Exception e) {
			log.error("Errore nel web service di invioSAP", e);
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
		serviceParam.put("prgSap", prgSap);
		testata.setServizio(serviceParam.getServizio());
		setDatiSapXml((String) serviceParam.get("dati"));
	}

	public String getDatiSapXml() {
		return datiSapXml;
	}

	public void setDatiSapXml(String datiSapXml) {
		this.datiSapXml = datiSapXml;
	}

	public String getPrgSap() {
		return prgSap;
	}

	public void setPrgSap(String prgSap) {
		this.prgSap = prgSap;
	}

}
