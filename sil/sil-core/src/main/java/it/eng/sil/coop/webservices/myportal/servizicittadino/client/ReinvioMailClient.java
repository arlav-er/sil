package it.eng.sil.coop.webservices.myportal.servizicittadino.client;

import java.io.StringReader;
import java.io.StringWriter;
import java.rmi.RemoteException;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

import it.eng.sil.coop.webservices.myportal.servizicittadino.bean.reinviomailaccreditamento.in.ReinvioMailAccreditamento;
import it.eng.sil.coop.webservices.myportal.servizicittadino.bean.reinviomailaccreditamento.out.RispostaputReinvioMail;
import it.eng.sil.coop.webservices.myportal.servizicittadino.utils.Credentials;
import it.eng.sil.coop.webservices.myportal.servizicittadino.utils.WsAuthUtils;
import it.eng.sil.coop.webservices.myportal.servizicittadino.ws.putcittadino.PutCittadinoProxy;
import it.eng.sil.coop.webservices.myportal.servizicittadino.ws.putcittadino.PutCittadino_PortType;

public class ReinvioMailClient extends ServiziCittadinoAbstractClient {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(ReinvioMailClient.class.getName());

	public final RispostaputReinvioMail reinvioMail(String idPfPrincipal, String destinatarioCC)
			throws ServiziCittadinoClientException {

		String wsCodServizio = WS_CODSERVIZIO_PUTCITTADINO;
		String endPointName = ENDPOINT_PUTCITTADINO;
		String inputXml, outputXml;
		WsAuthUtils wsAuthUtils = new WsAuthUtils();
		PutCittadinoProxy proxy = new PutCittadinoProxy();
		PutCittadino_PortType putCittadino;
		Credentials credentials;

		JAXBContext jaxbContext;
		StringWriter writer;

		ReinvioMailAccreditamento reinvioMailAccreditamento;
		RispostaputReinvioMail rispostaputReinvioMail;

		/////////////////////
		// GENERAZIONE XML //
		/////////////////////

		reinvioMailAccreditamento = new ReinvioMailAccreditamento();
		if (idPfPrincipal != null && !("".equals(idPfPrincipal))) {
			reinvioMailAccreditamento.setIdPfPrincipal(idPfPrincipal);
		}
		if (destinatarioCC != null && !("".equals(destinatarioCC))) {
			reinvioMailAccreditamento.setDestinatarioCC(destinatarioCC);
		}

		writer = new StringWriter();
		try {
			jaxbContext = JAXBContext.newInstance(ReinvioMailAccreditamento.class);
			jaxbContext.createMarshaller().marshal(reinvioMailAccreditamento, writer);
		} catch (JAXBException e1) {
			_logger.error("Errore durante la creazione del xml da inviare");
			throw new ServiziCittadinoClientException("Errore generico");
			// return null;
		}

		inputXml = writer.toString();

		if (!isXmlValid(inputXml, inputReinvioMailAccreditamentoSchemaFile)) {
			_logger.error("XML creato non valido");
			throw new ServiziCittadinoClientException("Errore generico");
			// return null;
		}

		/////////////////////////////
		// RECUPERA CREDENZIALI WS //
		/////////////////////////////

		credentials = wsAuthUtils.getCredentials(wsCodServizio);
		if (credentials == null) {
			_logger.error("Impossibile trovare in TS_WS l'username per il servizio: " + wsCodServizio);
			throw new ServiziCittadinoClientException("Errore generico");
			// return null;
		}

		//////////////////////
		// RICERCA ENDPOINT //
		//////////////////////

		String endPointUrl = getEndPointUrl(endPointName);

		if (endPointUrl == null) {
			_logger.error("Nessun ENDPOINT definito: " + endPointName);
			throw new ServiziCittadinoClientException("Errore generico");
			// return null;
		}

		proxy.setEndpoint(endPointUrl);

		////////////////////
		// CHIAMATA AL WS //
		////////////////////

		putCittadino = proxy.getPutCittadino_PortType();

		try {

			outputXml = putCittadino.reinvioMailAccreditamento(credentials.getUsername(), credentials.getPassword(),
					inputXml);
			// String xmlDettaglioCittadino = proxy.getDettaglioCittadino("", "", ""); // metodo alternativo

		} catch (RemoteException e) {
			_logger.error("Errore WS", e);
			throw new ServiziCittadinoClientException("Servizio non disponibile");
			// return null;
		}

		if (outputXml.startsWith("-")) {
			// _logger.error("Valore negativo ritornato dal WS myportal: " + outputXml);
			throw new ServiziCittadinoClientException("Errore generico");
			// return null;
		}

		// validazione risposta
		if (!isXmlValid(outputXml, outputReinvioMailAccreditamentoSchemaFile)) {
			_logger.error("XML ricevuto non valido");
			throw new ServiziCittadinoClientException("Errore generico");
			// return null;
		}

		try {

			jaxbContext = JAXBContext.newInstance(RispostaputReinvioMail.class);
			rispostaputReinvioMail = (RispostaputReinvioMail) jaxbContext.createUnmarshaller()
					.unmarshal(new StringReader(outputXml));

		} catch (JAXBException e) {
			_logger.debug("Errore parsing XML ricevuto");
			throw new ServiziCittadinoClientException("Errore generico");
			// return null;
		}

		return rispostaputReinvioMail;

	}
}
