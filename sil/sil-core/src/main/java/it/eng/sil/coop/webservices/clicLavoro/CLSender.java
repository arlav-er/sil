package it.eng.sil.coop.webservices.clicLavoro;

import java.rmi.RemoteException;

import javax.xml.rpc.ServiceException;

import org.apache.log4j.Logger;

import it.eng.sil.coop.DataSourceJNDI;
import it.eng.sil.coop.endpoints.EndPoint;

public class CLSender {
	private static final long serialVersionUID = 1L;
	private static Logger _logger = Logger.getLogger(CLSender.class.getName());

	private static final String NOME_SERVIZIO = "ComunicazioneClicLavoro";
	private static final String NOME_METODO = "importaClCandidatura";
	private static final String TOKEN = "";
	private static final String MITTENTE = "SIL";
	private static final String DESTINATARIO = "MYPORTAL";

	private String address;

	public EndPoint getEndPointByDs(String dataSourceJndiName) throws Exception {
		EndPoint endPoint = new EndPoint();
		endPoint.init(dataSourceJndiName, NOME_SERVIZIO);
		return endPoint;
	}

	public void popolaAddressByDs(String dataSourceJndiName) throws Exception {
		address = getEndPointByDs(dataSourceJndiName).getUrl();
	}

	public void popolaAddress() throws Exception {
		address = getEndPointByDs(new DataSourceJNDI().getJndi()).getUrl();
	}

	/**
	 * Metodo per l'invio dell'XML della comunicazione di Candidatura all'NCR
	 * 
	 * @param xmlIscrizione
	 *            String contente l'XML da inviare
	 * @return <code>true</code> se l'invio va a buon fine
	 * @throws ServiceException
	 * @throws RemoteException
	 */

	public boolean sendComunicazioneToNCR(String username, String password, String xmlString)
			throws ServiceException, RemoteException {

		return false;

	}

}
