/**
 * 
 */
package it.eng.sil.coop.messages.jmsmessages;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Map;

import javax.jms.ObjectMessage;

import org.apache.axis.types.Token;
import org.apache.log4j.Logger;

import it.eng.sil.coop.endpoints.EndPoint;
import it.eng.sil.coop.messages.AbstractMessage;
import it.eng.sil.coop.messages.IFaceMessage;
import it.eng.sil.coop.messages.TestataMessageTO;
import it.eng.sil.coop.queues.IFaceQueue;
import it.eng.sil.coop.wsClient.messageReceiver.ServiceParameters;
import it.eng.sil.sifer.client.PortType;
import it.eng.sil.sifer.client.Request;
import it.eng.sil.sifer.client.ServiceLocator;

/**
 * @author girotti
 * 
 */
public class SiferMessage extends AbstractMessage implements IFaceMessage {
	/**
	 * Logger for this class
	 */
	private static final Logger logger = Logger.getLogger(SiferMessage.class);

	/** destinatari **/
	private static final String[] END_POINT_ARR = { "InvioSifer" };

	public String datiXml = null;

	public String userName;
	public String cdnLavoratore;

	public String password;

	private String URL = null;

	private Long maxRedel = null;

	private Long redel = null;

	/*
	 * (non-Javadoc)
	 * 
	 * @see it.eng.sil.coop.messages.IFaceMessage#callWebservice()
	 */
	@Override
	public void callWebservice() throws Exception {
		if (logger.isDebugEnabled()) {
			logger.debug("callWebservice() - start");
		}
		// multipli inviii
		try {
			final Token token = new Token(password);
			for (String endPointKey : END_POINT_ARR) {
				ServiceLocator locator = new ServiceLocator();
				EndPoint endPoint = new EndPoint();
				endPoint.init(dataSourceJndi, endPointKey);
				URL = endPoint.getUrl();
				locator.setrequestServiceEndpointAddress(URL);
				PortType service = locator.getrequestService();
				String metodo = "registraPartecipanteCrisi";
				Request request = new Request(userName, token, metodo, datiXml);

				String retCode = service.requestService(request);
				logger.info("Inviato a Sifer con risultato:" + retCode + " " + toString());
			}
		} catch (RemoteException rEx) {
			if (redel == null || maxRedel == null) {
				final String errorString = "Errore Invio " + toString();
				logger.error(errorString, rEx);
			} else if (redel.longValue() >= maxRedel.longValue()) {
				final String errorString = "Errore Invio " + toString();
				logger.error(errorString, rEx);
			}
			throw rEx;
		} catch (Exception e) {
			final String errorString = "Errore Inizializzazione Messaggio " + toString();
			logger.error(errorString, e);
			throw e;
		}

		if (logger.isDebugEnabled()) {
			logger.debug("callWebservice() - end");
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see it.eng.sil.coop.messages.IFaceMessage#send(it.eng.sil.coop.queues.IFaceQueue )
	 */
	@Override
	public void send(IFaceQueue Q) throws Exception {
		if (logger.isDebugEnabled()) {
			logger.debug("send(IFaceQueue) - start");
		}

		ArrayList appParam = new ArrayList();
		appParam.add(datiXml);
		appParam.add(userName);
		appParam.add(password);
		appParam.add(cdnLavoratore);
		super.send(Q, appParam);

		if (logger.isDebugEnabled()) {
			logger.debug("send(IFaceQueue) - end");
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeit.eng.sil.coop.messages.IFaceMessage#setObjectMessage(javax.jms. ObjectMessage)
	 */
	@Override
	public void setObjectMessage(ObjectMessage msg) throws Exception {
		if (logger.isDebugEnabled()) {
			logger.debug("setObjectMessage(ObjectMessage) - start");
		}

		Serializable arrObj = null;
		try {
			// ora leggo il contenuto applicativo
			arrObj = msg.getObject();

			try {
				maxRedel = Long.valueOf(msg.getLongProperty("MaxRedeliveries"));
				redel = Long.valueOf(msg.getLongProperty("Redeliveries"));
			} catch (Exception e) {
				maxRedel = null;
				redel = null;
			}
			if (arrObj instanceof ArrayList) {
				ArrayList appParam = (ArrayList) arrObj;
				datiXml = (String) appParam.get(0);
				userName = (String) appParam.get(1);
				password = (String) appParam.get(2);
				cdnLavoratore = (String) appParam.get(3);
			}
		} catch (Exception ex) {
			final String errorString = "Errore Recupero dati Mesaggio:" + msg + ", appParam:" + arrObj + ", "
					+ toString();
			logger.error(errorString, ex);
			throw new Exception(errorString, ex);

		}

		if (logger.isDebugEnabled()) {
			logger.debug("setObjectMessage(ObjectMessage) - end");
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see it.eng.sil.coop.messages.IFaceMessage#setServiceParameters(java.util.Map)
	 */
	@Override
	public void setServiceParameters(Map param) {
		if (logger.isDebugEnabled()) {
			logger.debug("setServiceParameters(Map) - start");
		}

		ServiceParameters serviceParam = new ServiceParameters(param);
		datiXml = (String) serviceParam.get("datiXml");

		if (logger.isDebugEnabled()) {
			logger.debug("setServiceParameters(Map) - end");
		}
	}

	@Override
	public void setTestata(TestataMessageTO testata) {
		if (logger.isDebugEnabled()) {
			logger.debug("setTestata(TestataMessageTO) - start");
		}

		// TODO Auto-generated method stub
		testata.setServizio("Sifer");
		super.setTestata(testata);

		if (logger.isDebugEnabled()) {
			logger.debug("setTestata(TestataMessageTO) - end");
		}
	}

	@Override
	public String toString() {
		return String.format("SiferMessage [cdnLavoratore=%s, URL=%s, datiXml=%s, userName=%s, password=%s]",
				cdnLavoratore, URL, datiXml, userName, password);
	}

}
