package it.eng.myportal.ws;

import it.eng.myportal.dtos.DeComuneDTO;
import it.eng.myportal.dtos.EmailDTO;
import it.eng.myportal.dtos.RegisterAziendaDTO;
import it.eng.myportal.dtos.SedeDTO;
import it.eng.myportal.entity.AziendaInfo;
import it.eng.myportal.entity.PfPrincipal;
import it.eng.myportal.entity.home.AziendaInfoHome;
import it.eng.myportal.entity.home.PfPrincipalHome;
import it.eng.myportal.entity.home.WsEndpointHome;
import it.eng.myportal.entity.home.decodifiche.DeComuneHome;
import it.eng.myportal.enums.TipoServizio;
import it.eng.myportal.utils.ConstantsSingleton;
import it.eng.myportal.utils.Mailer;
import it.eng.myportal.utils.Utils;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.jms.ConnectionFactory;
import javax.jms.Queue;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebParam.Mode;
import javax.jws.WebService;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * PointOfAuthentication
 * 
 * @author coticone
 * 
 */
@WebService(serviceName = "AutenticazioneAccount")
public class AutenticazioneAccount {

	protected final Log log = LogFactory.getLog(this.getClass());

	@Resource(name = "RemoteConnectionFactory", mappedName = "java:/RemoteConnectionFactory")
	private ConnectionFactory connectionFactory;

	@Resource(name = "email_queue", mappedName = "java:/queue/email_queue")
	private Queue emailQueue;
	
	@EJB
	private PfPrincipalHome pfPrincipalHome;
	
	@EJB
	AziendaInfoHome aziendaInfoHome;
	
	@EJB
	DeComuneHome deComuneHome;
	
	@EJB
	private WsEndpointHome wsEndpointHome;

	/**
	 * 
	 * esempio datiUtente <?xml version="1.0" encoding="UTF-8"?> <Utente
	 * xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
	 * <username>username0</username> <password>password0</password> </Utente>
	 * 
	 * 
	 * @param username
	 * @param password
	 * @param datiUtente
	 * @return
	 */
	@WebMethod(operationName = "checkUtente")
	public String checkUtente(
			@WebParam(name = "username", mode = Mode.IN) String username,
			@WebParam(name = "password", mode = Mode.IN) String password,
			@WebParam(name = "datiUtente", mode = Mode.IN) String datiUtente) {
		// verifico le credenziali del WS
		try {
			checkCredenziali(username, password);
		} catch (Exception e) {
			log.error("checkUtente " + e);
			return creaMessaggio("checkUtente", "false", e.getMessage());
		}

		if (datiUtente == null || ("").equals(datiUtente)) {
			return creaMessaggio("checkUtente", "false", "Dati utente nulli");
		}

		// verifico l'xml datiUtente tramite XSD
		try {
			validateXml("checkUtente.xsd", datiUtente);
		} catch (Exception e) {
			log.error("checkUtente " + e);
			return creaMessaggio("checkUtente", "false", e.getMessage());
		}

		// verifico l'xml spedito
		String usernameXml = null;
		String passwordXml = null;
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			InputSource source = new InputSource(new StringReader(datiUtente));
			Document document = builder.parse(source);

			XPath xpath = XPathFactory.newInstance().newXPath();
			XPathExpression exprUsername = xpath.compile("/Utente/username[1]");
			XPathExpression exprPassword = xpath.compile("/Utente/password[1]");

			usernameXml = (String) exprUsername.evaluate(document);
			passwordXml = (String) exprPassword.evaluate(document);

			if (usernameXml == null) {
				throw new Exception("Username o Password errati");
			}
			if (passwordXml == null) {
				throw new Exception("Username o Password errati");
			}

			// AUTENTICAZIONE OK
			return creaMessaggio("checkUtente", "true", "AUTENTICAZIONE OK");
		} catch (Exception e) {
			return creaMessaggio("checkUtente", "false", e.getMessage());
		}
	}

	@WebMethod(operationName = "inserisciUtente")
	public String inserisciUtente(
			@WebParam(name = "username", mode = Mode.IN) String username,
			@WebParam(name = "password", mode = Mode.IN) String password,
			@WebParam(name = "datiUtente", mode = Mode.IN) String datiUtente) {

		log.info("WS AutenticazioneAccount inserisciUtente AZIENDA da SARE - MYSTAGE - INIZIO");
		
		String ret = "-1";
		try {
			ret = creaAzienda(datiUtente);
		} catch (Exception e) {
			log.error(e);
		}
		
		log.info("WS AutenticazioneAccount inserisciUtente AZIENDA id_pf_principal="+ret);		
		log.info("WS AutenticazioneAccount inserisciUtente AZIENDA da SARE - MYSTAGE - FINE");
		
		return ret;
	}

	/**
	 * verifica le credenziali di accesso per il WS di autenticazione
	 * 
	 * da gestire il recupero dei dati TABELLA o file di PROPERTIES
	 * 
	 * @param _login
	 * @param _pwd
	 * @throws Exception
	 */
	private void checkCredenziali(String login, String pwd) throws Exception {
		String user[] = null;
		user = wsEndpointHome.getWebServiceUser(TipoServizio.MYSTAGE);

		String userLocal = user[0];
		String pwdLocal = user[1];

		if (!login.equals(userLocal)) {
			throw new Exception("Username o Password errati");
		}

		if (!pwd.equals(pwdLocal)) {
			throw new Exception("Username o Password errati");
		}
	}

	private void validateXml(String schemaXSD, String xml) throws SAXException,
			IOException {

		// XXX verificare percorso ROOT_PATH????
		File schemaFile = new File("WEB-INF" + File.separator + "xsd"
				+ File.separator + "autenticazione" + File.separator
				+ schemaXSD);

		String schemaLang = "http://www.w3.org/2001/XMLSchema";
		SchemaFactory factory = SchemaFactory.newInstance(schemaLang);
		StreamSource streamSource = new StreamSource(schemaFile);

		Schema schema = factory.newSchema(streamSource);
		Validator validator = schema.newValidator();
		// at last perform validation:
		StringReader datiXmlReader = new StringReader(xml);
		StreamSource datiXmlStreamSource = new StreamSource(datiXmlReader);
		validator.validate(datiXmlStreamSource);

	}

	private String creaMessaggio(String operazione, String esito, String msg) {
		return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + "<Utente>" + "<"
				+ operazione + "> " + "<esito ok=\"" + esito
				+ "\" dettaglio=\"" + msg + "\"/>" + "</" + operazione + "> "
				+ "</Utente>";
	}

	
	private String creaAzienda(String xmlTirocinio) throws Exception {
		String username = "";
		
		Date now = new Date();
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		InputSource source = new InputSource(new StringReader(xmlTirocinio));
		Document document = builder.parse(source);
		XPath xpath = XPathFactory.newInstance().newXPath();
		
		AziendaInfo aziendaInfoRichiedente = null;
		
		String cfAzienda = xpath.compile("/Tirocinio/Utente/DatiAzienda/DatiSedeAzienda/codiceFiscale").evaluate(document);		
		String emailRegAzRich = xpath.compile("/Tirocinio/Utente/DatiAzienda/DatiRichiedente/emailRegistrazione").evaluate(document);

		// verifica se esiste l'azienda per CF e EMAIL
		aziendaInfoRichiedente = aziendaInfoHome.findByCFAndMailReferente(cfAzienda, emailRegAzRich);
		if (aziendaInfoRichiedente == null) {
			// verifico se esistono azienda con lo stesso CF
			List<AziendaInfo> listAzInfoRichiedente = aziendaInfoHome.findListByCodiceFiscale(cfAzienda);
			// se non esistono inserisco l'azienda
			if (listAzInfoRichiedente.size() == 0) {
				// inserisce un nuovo utente per l'azienda richiedente
				// inserimento dei principal delle aziende
				RegisterAziendaDTO registerAzRichiedente = new RegisterAziendaDTO();

				// String[] arrEmail = emailRegAzRich.split("@");
				registerAzRichiedente.setUsername(cfAzienda);
				registerAzRichiedente.setPassword(Utils.randomString(ConstantsSingleton.TOKEN_PASSWORD_LENGTH));
				registerAzRichiedente.setPasswordConfirm(Utils.randomString(ConstantsSingleton.TOKEN_LENGTH));
				registerAzRichiedente.setEmail(emailRegAzRich);
				registerAzRichiedente.setEmailConfirm(emailRegAzRich);

				registerAzRichiedente.setActivateToken(Utils.randomString(ConstantsSingleton.TOKEN_LENGTH));
				registerAzRichiedente.setAttivo(false);
				registerAzRichiedente.setAcceptInformativa(true);
				registerAzRichiedente.setNome(xpath.compile("/Tirocinio/Utente/DatiAzienda/DatiRichiedente/nome").evaluate(document));
				registerAzRichiedente.setCognome(xpath.compile("/Tirocinio/Utente/DatiAzienda/DatiRichiedente/cognome").evaluate(document));
				registerAzRichiedente.setCodiceFiscale(cfAzienda);
				registerAzRichiedente.setIndirizzo(null);
				registerAzRichiedente.setComune(null);
				registerAzRichiedente.setCap(null);
				registerAzRichiedente.setTelefono(null);
				registerAzRichiedente.setDomanda("Il codice fiscale dell'azienda?");
				registerAzRichiedente.setRisposta(cfAzienda);

				registerAzRichiedente.setRagioneSociale(xpath.compile("/Tirocinio/Utente/DatiAzienda/DatiSedeAzienda/ragionesociale").evaluate(document));
				SedeDTO sedeAzRichiedente = new SedeDTO();
				sedeAzRichiedente.setIndirizzo(xpath.compile("/Tirocinio/Utente/DatiAzienda/DatiSedeAzienda/indirizzoSedeLegale").evaluate(document));
				DeComuneDTO comuneSedeAzRich = deComuneHome.findDTOById(xpath.compile("/Tirocinio/Utente/DatiAzienda/DatiSedeAzienda/codComuneSedeLegale").evaluate(document));
				sedeAzRichiedente.setComune(comuneSedeAzRich);
				sedeAzRichiedente.setCap(xpath.compile("/Tirocinio/Utente/DatiAzienda/DatiSedeAzienda/capSedeLegale").evaluate(
								document));
				sedeAzRichiedente
						.setFax(xpath.compile(
								"/Tirocinio/Utente/DatiAzienda/DatiSedeAzienda/faxSedeLegale").evaluate(
								document));
				sedeAzRichiedente.setTelefono(xpath.compile(
						"/Tirocinio/Utente/DatiAzienda/DatiSedeAzienda/telefonoSedeLegale").evaluate(
						document));
				sedeAzRichiedente.setIndirizzo(xpath.compile(
						"/Tirocinio/Utente/DatiAzienda/DatiSedeAzienda/indirizzoSedeLegale").evaluate(
						document));
				registerAzRichiedente.setSedeOperativa(sedeAzRichiedente);

				AziendaInfo azPalese = aziendaInfoHome.register(registerAzRichiedente, true);
				PfPrincipal pfPrinc = azPalese.getPfPrincipal();
				username = pfPrinc.getUsername();
				
				EmailDTO registerAzRichEmail = EmailDTO.buildRegistrationEmailRichiestaPersonale(registerAzRichiedente);
				Mailer.getInstance().putInQueue(connectionFactory, emailQueue, registerAzRichEmail);
								
			} else if (listAzInfoRichiedente.size() == 1) {
				aziendaInfoRichiedente = listAzInfoRichiedente.get(0);
				PfPrincipal pfPrinc = aziendaInfoRichiedente.getPfPrincipal(); 
				username = pfPrinc.getUsername();
			}
		}
			
			
		return username;	
	}
}
