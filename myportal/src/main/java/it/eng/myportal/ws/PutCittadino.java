package it.eng.myportal.ws;

import it.eng.myportal.entity.ejb.CittadinoEjb;
import it.eng.myportal.entity.home.PfPrincipalHome;
import it.eng.myportal.entity.home.UtenteInfoHome;
import it.eng.myportal.entity.home.WsEndpointHome;
import it.eng.myportal.entity.home.decodifiche.DeCittadinanzaHome;
import it.eng.myportal.entity.home.decodifiche.DeComuneHome;
import it.eng.myportal.entity.home.decodifiche.DeCpiHome;
import it.eng.myportal.entity.home.decodifiche.DeProvinciaHome;
import it.eng.myportal.enums.TipoServizio;
import it.eng.myportal.exception.MyPortalException;
import it.eng.myportal.siler.putAccountCittadino.in.PutAccountCittadino;
import it.eng.myportal.siler.putAccountCittadino.out.RispostaputAccountCittadino;
import it.eng.myportal.siler.reinvioMailAccreditamento.in.ReinvioMailAccreditamento;
import it.eng.myportal.siler.reinvioMailAccreditamento.out.RispostaputReinvioMail;
import it.eng.myportal.utils.Utils;

import java.io.File;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.List;

import javax.ejb.EJB;
import javax.jws.WebMethod;
import javax.jws.WebService;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.PropertyException;
import javax.xml.bind.Unmarshaller;
import javax.xml.validation.Schema;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.xml.sax.SAXException;

@WebService(name = "PutCittadino", portName = "PutCittadino", serviceName = "PutCittadino", targetNamespace = "http://ws.myportal.eng.it/")
public class PutCittadino {

	@EJB
	WsEndpointHome wsEndpointHome;

	@EJB
	PfPrincipalHome pfPrincipalHome;

	@EJB
	DeComuneHome deComuneHome;

	@EJB
	DeCittadinanzaHome deCittadinanzaHome;

	@EJB
	DeProvinciaHome deProvinciaHome;

	@EJB
	UtenteInfoHome utenteInfoHome;

	@EJB
	CittadinoEjb cittadinoEjb;

	@EJB
	DeCpiHome deCpiHome;

	protected final Log log = LogFactory.getLog(this.getClass());

	public static final String GENERIC_ERROR_PUT = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><RispostaAccountCittadino><Esito><codice>99</codice><descrizione>Errore Generico</descrizione></Esito></RispostaAccountCittadino>";
	public static final String GENERIC_ERROR_MAIL = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><RispostaAccountCittadino><Esito><codice>99</codice><descrizione>Errore Generico</descrizione></Esito></RispostaAccountCittadino>";

	@WebMethod(operationName = "putAccountCittadino")
	public String putAccountCittadino(String username, String password, String xmlAccountCittadino) {

		// Controllo delle credenziali ricevute dal SIL
		try {
			checkCredenziali(username, password);
		} catch (Exception e) {
			log.error("checkUtente " + e);
			return creaXMLoutAccount(creaEsitoAccount("01", e.getMessage()));
		}

		try {

			PutAccountCittadino documentIn = creaAccountCittadinoIn(xmlAccountCittadino);

			if (documentIn.getIdPfPrincipal() != null) {
				// Aggiornamento account esistente
				cittadinoEjb.aggiornaAccount(documentIn);
			} else if (documentIn.getUsername() != null && !documentIn.getUsername().isEmpty()) {
				// Nuovo inserimento
				// Se username già esistente, ritornare errore
				cittadinoEjb.inserisciAccount(documentIn);
			}
			return creaXMLoutAccount(creaEsitoAccount("00", "dati aggiornati correttamente"));
		} catch (MyPortalException e) {
			return creaXMLoutAccount(creaEsitoAccount(e.getCodErrore(), e.getStrMessaggio()));
		}

	}

	@WebMethod(operationName = "reinvioMailAccreditamento")
	public String reinvioMailAccreditamento(String username, String password, String xmlReinvioMail) {
		// Controllo delle credenziali ricevute dal SIL
		try {
			checkCredenziali(username, password);
		} catch (Exception e) {
			log.error("checkUtente " + e);
			return creaXMLoutMail(creaEsitoMail("01", e.getMessage()));
		}
		try {
			ReinvioMailAccreditamento documentIn = creaReinvioMailIn(xmlReinvioMail);

			List<String> listaDestinatari = cittadinoEjb.trovaValidaIndirizzi(documentIn.getDestinatarioCC());

			String y = Utils.getStringFromSimpleXmlElement(documentIn.getIdPfPrincipal());
			Integer z = Integer.parseInt(y);

			cittadinoEjb.reinviaMailAccreditamento(z, listaDestinatari);

			return creaXMLoutMail(creaEsitoMail("00", "Operazione Completata"));

		} catch (MyPortalException e) {
			return creaXMLoutMail(creaEsitoMail(e.getCodErrore(), e.getStrMessaggio()));
		}

	}

	private void checkCredenziali(String login, String pwd) throws Exception {
		String user[] = null;
		user = wsEndpointHome.getWebServiceUser(TipoServizio.PUT_CITTATINO);

		String userLocal = user[0];
		String pwdLocal = user[1];

		if (!login.equals(userLocal)) {
			throw new Exception("Username o Password errati");
		}

		if (!pwd.equals(pwdLocal)) {
			throw new Exception("Username o Password errati");
		}
	}

	private RispostaputAccountCittadino creaEsitoAccount(String cod, String msg) {

		RispostaputAccountCittadino r = new RispostaputAccountCittadino();
		it.eng.myportal.siler.putAccountCittadino.out.RispostaputAccountCittadino.Esito e = new it.eng.myportal.siler.putAccountCittadino.out.RispostaputAccountCittadino.Esito();
		e.setCodice(cod);
		e.setDescrizione(msg);
		r.setEsito(e);
		return r;
	}

	private RispostaputReinvioMail creaEsitoMail(String cod, String msg) {

		RispostaputReinvioMail r = new RispostaputReinvioMail();
		RispostaputReinvioMail.Esito e = new RispostaputReinvioMail.Esito();
		e.setCodice(cod);
		e.setDescrizione(msg);
		r.setEsito(e);
		return r;
	}

	private String creaXMLoutAccount(RispostaputAccountCittadino account) {
		try {
			JAXBContext jc = JAXBContext.newInstance(RispostaputAccountCittadino.class);
			Marshaller marshaller = jc.createMarshaller();
			Schema schema = Utils.getXsdSchema("servizi_cittadino" + File.separator
					+ "outputXML_putAccountCittadino.xsd");

			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, false);

			marshaller.setSchema(schema);
			StringWriter writer = new StringWriter();
			marshaller.marshal(account, writer);
			String xmlRichiesta = writer.getBuffer().toString();
			return xmlRichiesta;
		} catch (PropertyException e) {
			log.error("creaXMLoutAccount: " + e);
			return GENERIC_ERROR_PUT;
		} catch (SAXException e) {
			log.error("creaXMLoutAccount: " + e);
			return GENERIC_ERROR_PUT;
		} catch (JAXBException e) {
			log.error("creaXMLoutAccount: " + e);
			return GENERIC_ERROR_PUT;
		}
	}

	private String creaXMLoutMail(RispostaputReinvioMail mail) {
		try {
			JAXBContext jc = JAXBContext.newInstance(RispostaputReinvioMail.class);
			Marshaller marshaller = jc.createMarshaller();
			Schema schema = Utils.getXsdSchema("servizi_cittadino" + File.separator
					+ "outputXML_reinvioMailAccreditamento.xsd");

			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, false);

			marshaller.setSchema(schema);
			StringWriter writer = new StringWriter();
			marshaller.marshal(mail, writer);
			String xmlRichiesta = writer.getBuffer().toString();
			return xmlRichiesta;
		} catch (PropertyException e) {
			log.error("creaXMLoutAccount: " + e);
			return GENERIC_ERROR_MAIL;
		} catch (SAXException e) {
			log.error("creaXMLoutAccount: " + e);
			return GENERIC_ERROR_MAIL;
		} catch (JAXBException e) {
			log.error("creaXMLoutAccount: " + e);
			return GENERIC_ERROR_MAIL;
		}
	}

	private PutAccountCittadino creaAccountCittadinoIn(String xmlAccountCittadino) throws MyPortalException {
		JAXBContext jaxbContext;
		PutAccountCittadino account = null;
		try {
			jaxbContext = JAXBContext.newInstance(PutAccountCittadino.class);
			Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
			account = (PutAccountCittadino) jaxbUnmarshaller.unmarshal(new StringReader(xmlAccountCittadino));
		} catch (JAXBException e) {
			throw new MyPortalException("02", "InputXML non valido");
		}
		return account;
	}

	private ReinvioMailAccreditamento creaReinvioMailIn(String xmlreinvioMail) throws MyPortalException {
		JAXBContext jaxbContext;
		ReinvioMailAccreditamento account = null;
		try {
			jaxbContext = JAXBContext.newInstance(ReinvioMailAccreditamento.class);
			Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
			account = (ReinvioMailAccreditamento) jaxbUnmarshaller.unmarshal(new StringReader(xmlreinvioMail));
		} catch (JAXBException e) {
			throw new MyPortalException("02", "InputXML non valido");
		}
		return account;
	}

}
