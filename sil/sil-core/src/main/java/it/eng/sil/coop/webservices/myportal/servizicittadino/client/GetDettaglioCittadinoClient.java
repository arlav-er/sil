package it.eng.sil.coop.webservices.myportal.servizicittadino.client;

import java.io.StringReader;
import java.io.StringWriter;
import java.rmi.RemoteException;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

import it.eng.sil.coop.webservices.myportal.servizicittadino.ServiziCittadinoException;
import it.eng.sil.coop.webservices.myportal.servizicittadino.bean.getdettagliocittadino.in.DettaglioCittadino;
import it.eng.sil.coop.webservices.myportal.servizicittadino.bean.getdettagliocittadino.out.RispostaDettaglioCittadino;
import it.eng.sil.coop.webservices.myportal.servizicittadino.utils.Credentials;
import it.eng.sil.coop.webservices.myportal.servizicittadino.utils.WsAuthUtils;
import it.eng.sil.coop.webservices.myportal.servizicittadino.ws.getcittadino.GetCittadinoProxy;
import it.eng.sil.coop.webservices.myportal.servizicittadino.ws.getcittadino.GetCittadino_PortType;

public class GetDettaglioCittadinoClient extends ServiziCittadinoAbstractClient {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger
			.getLogger(GetDettaglioCittadinoClient.class.getName());

	public final RispostaDettaglioCittadino getDettaglioCittadino(String idPfPrincipal)
			throws ServiziCittadinoClientException {

		String wsCodServizio = WS_CODSERVIZIO_GETCITTADINO;
		String endPointName = ENDPOINT_GETCITTADINO;
		String inputXml, outputXml;
		WsAuthUtils wsAuthUtils = new WsAuthUtils();
		GetCittadinoProxy proxy = new GetCittadinoProxy();
		GetCittadino_PortType getCittadino;
		Credentials credentials;

		JAXBContext jaxbContext;
		StringWriter writer;

		DettaglioCittadino dettaglioCittadino;
		RispostaDettaglioCittadino rispostaDettaglioCittadino;

		/////////////////////
		// GENERAZIONE XML //
		/////////////////////

		dettaglioCittadino = new DettaglioCittadino();
		if (idPfPrincipal != null && !("".equals(idPfPrincipal))) {
			dettaglioCittadino.setIdPfPrincipal(idPfPrincipal);
		}

		writer = new StringWriter();
		try {
			jaxbContext = JAXBContext.newInstance(DettaglioCittadino.class);
			jaxbContext.createMarshaller().marshal(dettaglioCittadino, writer);
		} catch (JAXBException e1) {
			_logger.error("Errore creazione input XML");
			throw new ServiziCittadinoClientException(ServiziCittadinoException.ERRORE_GENERICO);
			// return null;
		}

		inputXml = writer.toString();

		if (!isXmlValid(inputXml, inputGetDettaglioCittadinoSchemaFile)) {
			_logger.error("XML creato non valido");
			throw new ServiziCittadinoClientException(ServiziCittadinoException.ERRORE_GENERICO);
			// return null;
		}

		/////////////////////////////
		// RECUPERA CREDENZIALI WS //
		/////////////////////////////

		credentials = wsAuthUtils.getCredentials(wsCodServizio);
		if (credentials == null) {
			_logger.error("Impossibile trovare in TS_WS l'username per il servizio: " + wsCodServizio);
			throw new ServiziCittadinoClientException(ServiziCittadinoException.ERRORE_GENERICO);
			// return null;
		}

		//////////////////////
		// RICERCA ENDPOINT //
		//////////////////////

		String endPointUrl = getEndPointUrl(endPointName);

		if (endPointUrl == null) {
			_logger.error("Nessun ENDPOINT definito: " + endPointName);
			throw new ServiziCittadinoClientException(ServiziCittadinoException.ERRORE_GENERICO);
			// return null;
		}

		proxy.setEndpoint(endPointUrl);

		////////////////////
		// CHIAMATA AL WS //
		////////////////////

		getCittadino = proxy.getGetCittadino_PortType();

		try {

			outputXml = getCittadino.getDettaglioCittadino(credentials.getUsername(), credentials.getPassword(),
					inputXml);
			// String xmlDettaglioCittadino = proxy.getDettaglioCittadino("", "", ""); // metodo alternativo
		} catch (RemoteException e) {
			_logger.error("Errore WS", e);
			throw new ServiziCittadinoClientException(ServiziCittadinoException.SERVIZIO_NON_DISPONIBILE);
			// return null;
		}

		if (outputXml.startsWith("-")) {
			// _logger.error("Valore negativo ritornato dal WS myportal: " + outputXml);
			throw new ServiziCittadinoClientException(ServiziCittadinoException.ERRORE_GENERICO);
			// return null;
		}

		// validazione risposta
		if (!isXmlValid(outputXml, outputGetDettaglioCittadinoSchemaFile)) {
			_logger.error("xml ricevuto non valido");
			throw new ServiziCittadinoClientException(ServiziCittadinoException.ERRORE_GENERICO);
			// return null;
		}

		try {

			jaxbContext = JAXBContext.newInstance(RispostaDettaglioCittadino.class);
			rispostaDettaglioCittadino = (RispostaDettaglioCittadino) jaxbContext.createUnmarshaller()
					.unmarshal(new StringReader(outputXml));

		} catch (JAXBException e) {
			_logger.debug("Errore parsing xml ricevuto");
			throw new ServiziCittadinoClientException(ServiziCittadinoException.ERRORE_GENERICO);
			// return null;
		}

		return rispostaDettaglioCittadino;

	}

}
