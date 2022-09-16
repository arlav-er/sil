package it.eng.sil.coop.webservices.myportal.servizicittadino.client;

import java.io.StringReader;
import java.io.StringWriter;
import java.rmi.RemoteException;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

import it.eng.afExt.utils.StringUtils;
import it.eng.sil.coop.webservices.myportal.servizicittadino.bean.getaccountcittadino.in.AccountCittadino;
import it.eng.sil.coop.webservices.myportal.servizicittadino.bean.getaccountcittadino.out.RispostaAccountCittadino;
import it.eng.sil.coop.webservices.myportal.servizicittadino.utils.Credentials;
import it.eng.sil.coop.webservices.myportal.servizicittadino.utils.WsAuthUtils;
import it.eng.sil.coop.webservices.myportal.servizicittadino.ws.getcittadino.GetCittadinoProxy;
import it.eng.sil.coop.webservices.myportal.servizicittadino.ws.getcittadino.GetCittadino_PortType;

public class GetAccountCittadinoClient extends ServiziCittadinoAbstractClient {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger
			.getLogger(GetAccountCittadinoClient.class.getName());

	public final RispostaAccountCittadino getAccountCittadino(String cognome, String nome, String codiceFiscale,
			String email) throws ServiziCittadinoClientException {

		String wsCodServizio = WS_CODSERVIZIO_GETCITTADINO;
		String endPointName = ENDPOINT_GETCITTADINO;
		String inputXml, outputXml;
		WsAuthUtils wsAuthUtils = new WsAuthUtils();
		GetCittadinoProxy proxy = new GetCittadinoProxy();
		GetCittadino_PortType getCittadino;
		Credentials credentials;

		JAXBContext jaxbContext;
		StringWriter writer;

		AccountCittadino accountCittadino;
		RispostaAccountCittadino rispostaAccountCittadino;

		/////////////////////
		// GENERAZIONE XML //
		/////////////////////

		accountCittadino = new AccountCittadino();
		if (cognome != null && !("".equals(cognome))) {
			accountCittadino.setCognome(cognome);
		}
		if (nome != null && !("".equals(nome))) {
			accountCittadino.setNome(nome);
		}
		if (codiceFiscale != null && !("".equals(codiceFiscale))) {
			accountCittadino.setCodiceFiscale(codiceFiscale);
		}
		if (email != null && !("".equals(email))) {
			if (StringUtils.isMailCorretta(email)) {
				accountCittadino.setEmail(email);
			}
		}

		writer = new StringWriter();
		try {
			jaxbContext = JAXBContext.newInstance(AccountCittadino.class);
			jaxbContext.createMarshaller().marshal(accountCittadino, writer);
		} catch (JAXBException e1) {
			_logger.error("Errore creazione input XML", e1);
			throw new ServiziCittadinoClientException("Errore generico");
			// return null;
		}

		inputXml = writer.toString();

		if (!isXmlValid(inputXml, inputGetAccountCittadinoSchemaFile)) {
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

		getCittadino = proxy.getGetCittadino_PortType();

		try {

			outputXml = getCittadino.getAccountCittadino(credentials.getUsername(), credentials.getPassword(),
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
		if (!isXmlValid(outputXml, outputGetAccountCittadinoSchemaFile)) {
			_logger.error("xml ricevuto non valido");
			throw new ServiziCittadinoClientException("Errore generico");
			// return null;
		}

		try {

			jaxbContext = JAXBContext.newInstance(RispostaAccountCittadino.class);
			rispostaAccountCittadino = (RispostaAccountCittadino) jaxbContext.createUnmarshaller()
					.unmarshal(new StringReader(outputXml));

		} catch (JAXBException e) {
			_logger.debug("Errore parsing xml ricevuto");
			throw new ServiziCittadinoClientException("Errore generico");
			// return null;
		}

		return rispostaAccountCittadino;

	}

}
