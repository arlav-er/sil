package it.eng.sil.coop.webservices.myportal.servizicittadino.client;

import java.io.StringReader;
import java.io.StringWriter;
import java.rmi.RemoteException;
import java.text.ParseException;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.datatype.DatatypeConfigurationException;

import it.eng.afExt.utils.StringUtils;
import it.eng.sil.coop.webservices.myportal.servizicittadino.bean.putaccountcittadino.in.PutAccountCittadino;
import it.eng.sil.coop.webservices.myportal.servizicittadino.bean.putaccountcittadino.out.RispostaputAccountCittadino;
import it.eng.sil.coop.webservices.myportal.servizicittadino.utils.Credentials;
import it.eng.sil.coop.webservices.myportal.servizicittadino.utils.DateUtils;
import it.eng.sil.coop.webservices.myportal.servizicittadino.utils.WsAuthUtils;
import it.eng.sil.coop.webservices.myportal.servizicittadino.ws.putcittadino.PutCittadinoProxy;
import it.eng.sil.coop.webservices.myportal.servizicittadino.ws.putcittadino.PutCittadino_PortType;

public class PutAccountCittadinoClient extends ServiziCittadinoAbstractClient {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger
			.getLogger(PutAccountCittadinoClient.class.getName());

	private final static String EXAMPLE_MAIL = "info@example.com";

	public final RispostaputAccountCittadino putAccountCittadino(String idPfPrincipal, String username, String cognome,
			String nome, String email, String comuneNascita, String comuneDomicilio, String indirizzoDomicilio,
			String codiceFiscale, String dataNascita, String cittadinanza, String codProvinciaSil,
			String documentoIdentita, String numeroDocumento, String dtScadenzaDocumento)
			throws ServiziCittadinoClientException {

		PutAccountCittadino accountCittadino = new PutAccountCittadino();

		if (idPfPrincipal != null && !"".equals(idPfPrincipal)) {
			accountCittadino.setIdPfPrincipal(idPfPrincipal);
		}
		if (username != null && !"".equals(username)) {
			accountCittadino.setUsername(username);
		}
		if (cognome != null && !"".equals(cognome)) {
			accountCittadino.setCognome(cognome);
		}
		if (nome != null && !"".equals(nome)) {
			accountCittadino.setNome(nome);
		}
		if (email != null && !"".equals(email)) {
			if (idPfPrincipal != null) {
				// modifica
				if (StringUtils.isMailCorretta(email)) {
					accountCittadino.setEmail(email);
				} else {
					accountCittadino.setEmail(EXAMPLE_MAIL);
				}
			} else {
				// inserimento, si da per scontato che la mail sia OK
				accountCittadino.setEmail(email);
			}
		}
		if (comuneNascita != null && !"".equals(comuneNascita)) {
			accountCittadino.setComuneNascita(comuneNascita);
		}
		if (comuneDomicilio != null && !"".equals(comuneDomicilio)) {
			accountCittadino.setComuneDomicilio(comuneDomicilio);
		}
		if (indirizzoDomicilio != null && !"".equals(indirizzoDomicilio)) {
			accountCittadino.setIndirizzoDomicilio(indirizzoDomicilio);
		}
		if (codiceFiscale != null && !"".equals(codiceFiscale)) {
			accountCittadino.setCodiceFiscale(codiceFiscale);
		}
		if (dataNascita != null && !"".equals(dataNascita)) {
			try {
				accountCittadino.setDataNascita(DateUtils.stringToGregorianDate(dataNascita));
			} catch (DatatypeConfigurationException e) {
				_logger.error("Errore parsing data nascita (DatatypeConfigurationException)", e);
				throw new ServiziCittadinoClientException("Errore generico");
			} catch (ParseException e) {
				_logger.error("Errore parsing data nascita (ParseException)", e);
				throw new ServiziCittadinoClientException("Errore generico");
			}
		}
		if (cittadinanza != null && !"".equals(cittadinanza)) {
			accountCittadino.setCittadinanza(cittadinanza);
		}
		if (codProvinciaSil != null && !"".equals(codProvinciaSil)) {
			accountCittadino.setCodProvinciaSil(codProvinciaSil);
		}
		if (documentoIdentita != null && !"".equals(documentoIdentita)) {
			accountCittadino.setDocumentoIdentita(documentoIdentita);
		}
		if (numeroDocumento != null && !"".equals(numeroDocumento)) {
			accountCittadino.setNumeroDocumento(numeroDocumento);
		}
		if (dtScadenzaDocumento != null && !"".equals(dtScadenzaDocumento)) {
			try {
				accountCittadino.setDtScadenzaDocumento(DateUtils.stringToGregorianDate(dtScadenzaDocumento));
			} catch (DatatypeConfigurationException e) {
				_logger.error("Errore parsing data scadenza documento (DatatypeConfigurationException)", e);
				throw new ServiziCittadinoClientException("Errore generico");
			} catch (ParseException e) {
				_logger.error("Errore parsing data scadenza documento (ParseException)", e);
				throw new ServiziCittadinoClientException("Errore generico");
			}
		}
		return putAccountCittadino(accountCittadino);

	}

	public final RispostaputAccountCittadino putAccountCittadino(PutAccountCittadino accountCittadino)
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

		RispostaputAccountCittadino rispostaPutAccountCittadino;

		/////////////////////
		// GENERAZIONE XML //
		/////////////////////

		if (accountCittadino == null) {
			_logger.error("Account Cittadino Nullo");
			throw new ServiziCittadinoClientException("Errore generico");
			// return null;
		}

		writer = new StringWriter();
		try {
			jaxbContext = JAXBContext.newInstance(PutAccountCittadino.class);
			jaxbContext.createMarshaller().marshal(accountCittadino, writer);
		} catch (JAXBException e1) {
			_logger.error("Errore durante la creazione del xml da inviare");
			throw new ServiziCittadinoClientException("Errore generico");
			// return null;
		}

		inputXml = writer.toString();

		if (!isXmlValid(inputXml, inputPutAccountCittadinoSchemaFile)) {
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

			outputXml = putCittadino.putAccountCittadino(credentials.getUsername(), credentials.getPassword(),
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
		if (!isXmlValid(outputXml, outputPutAccountCittadinoSchemaFile)) {
			_logger.error("XML ricevuto non valido");
			throw new ServiziCittadinoClientException("Errore generico");
			// return null;
		}

		try {

			jaxbContext = JAXBContext.newInstance(RispostaputAccountCittadino.class);
			rispostaPutAccountCittadino = (RispostaputAccountCittadino) jaxbContext.createUnmarshaller()
					.unmarshal(new StringReader(outputXml));

		} catch (JAXBException e) {
			_logger.debug("Errore parsing xml ricevuto");
			throw new ServiziCittadinoClientException("Errore generico");
			// return null;
		}

		return rispostaPutAccountCittadino;

	}

}
