package it.eng.myportal.ws;

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

import it.eng.myportal.entity.PfPrincipal;
import it.eng.myportal.entity.ejb.CittadinoEjb;
import it.eng.myportal.entity.home.PfPrincipalHome;
import it.eng.myportal.entity.home.WsEndpointHome;
import it.eng.myportal.entity.home.decodifiche.DeCittadinanzaHome;
import it.eng.myportal.entity.home.decodifiche.DeComuneHome;
import it.eng.myportal.entity.home.decodifiche.DeTitoloSoggiornoHome;
import it.eng.myportal.enums.TipoServizio;
import it.eng.myportal.exception.MyPortalException;
import it.eng.myportal.siler.getAccountCittadino.in.AccountCittadino;
import it.eng.myportal.siler.getAccountCittadino.out.RispostaAccountCittadino;
import it.eng.myportal.siler.getAccountCittadino.out.RispostaAccountCittadino.DatiAccount;
import it.eng.myportal.siler.getAccountCittadino.out.RispostaAccountCittadino.Esito;
import it.eng.myportal.siler.getDettaglioCittadino.in.DettaglioCittadino;
import it.eng.myportal.siler.getDettaglioCittadino.out.RispostaDettaglioCittadino;
import it.eng.myportal.utils.Utils;


@WebService(name = "GetCittadino",
		portName = "GetCittadino",
		serviceName = "GetCittadino",
		targetNamespace = "http://ws.myportal.eng.it/")        
public class GetCittadino {

	@EJB
	WsEndpointHome wsEndpointHome;

	@EJB
	PfPrincipalHome pfPrincipalHome;

	@EJB
	DeComuneHome deComuneHome;

	@EJB
	DeCittadinanzaHome deCittadinanzaHome;

	@EJB
	DeTitoloSoggiornoHome deTitoloSoggiornoHome;

	@EJB
	CittadinoEjb cittadinoEjb;

	protected final Log log = LogFactory.getLog(this.getClass());

	public static final String GENERIC_ERROR_ACCOUNT = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><RispostaAccountCittadino><Esito><codice>99</codice><descrizione>Errore Generico</descrizione></Esito></RispostaAccountCittadino>";
	public static final String GENERIC_ERROR_DETTAGLIO = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><RispostaDettaglioCittadino><Esito><codice>99</codice><descrizione>Errore Generico</descrizione></Esito></RispostaDettaglioCittadino>";

	@WebMethod(operationName = "getAccountCittadino")
	public String getAccountCittadino(String username, String password, String xmlAccountCittadino) {

		// Controllo delle credenziali ricevute dal SIL
		try {
			checkCredenziali(username, password);
		} catch (Exception e) {
			log.error("checkUtente " + e);
			return creaXMLoutAccount(creaEsitoAccount("01", e.getMessage()));

		}

		AccountCittadino accountCittadinoIn = null;
		try {
			accountCittadinoIn = creaAccountCittadinoIn(xmlAccountCittadino);
		} catch(MyPortalException e) {
			return creaXMLoutAccount(creaEsitoAccount("02", "InputXML non valido"));
		}
		List<PfPrincipal> listaPrincipal = cittadinoEjb.trovaUtenti(accountCittadinoIn);

		if (listaPrincipal == null || listaPrincipal.isEmpty()) {
			return creaXMLoutAccount(creaEsitoAccount("03", "Cittadino non trovato"));
		}
		RispostaAccountCittadino documentOut;

		String codiceStr = "00";
		String descrStr = "Nessun Errore";

		documentOut = creaEsitoAccount(codiceStr, descrStr);
		DatiAccount datiAccount = new DatiAccount();
		documentOut.setDatiAccount(datiAccount);
		for (PfPrincipal principal : listaPrincipal) {
			datiAccount.getAccountCittadino().add(cittadinoEjb.creaDocumentDatiAccount(principal));
		}

		return creaXMLoutAccount(documentOut);

	}

	@WebMethod(operationName = "getDettaglioCittadino")
	public String getDettaglioCittadino(String username, String password, String xmlDettaglioCittadino) {

		// Controllo delle credenziali ricevute dal SIL
		try {
			checkCredenziali(username, password);
		} catch (MyPortalException e) {
			log.error("ERRORE di autenticazione checkUtente " + e);
			return creaXMLoutDettaglio(creaEsitoDettaglio("01", e.getMessage()));
		}catch (Exception e) {
			log.error("ERRORE GRAVE checkUtente: " + e);
			return creaXMLoutDettaglio(creaEsitoDettaglio("01", "Servizio non disponibile"));
		}
		DettaglioCittadino dettaglioIn = null;
		try {
			dettaglioIn = creaDettaglioCittadinoIn(xmlDettaglioCittadino);
		} catch (MyPortalException e )  {
			return creaXMLoutDettaglio(creaEsitoDettaglio(e.getCodErrore(), e.getStrMessaggio()));
		}

		PfPrincipal principal = null;
		if (dettaglioIn.getIdPfPrincipal() != null) {
		
			String y = Utils.getStringFromSimpleXmlElement(dettaglioIn.getIdPfPrincipal());
			
			if (y==null || "".equals(y) || "null".equalsIgnoreCase(y)){
				return creaXMLoutDettaglio(creaEsitoDettaglio("02", "Cittadino non trovato"));
			}	
			
			Integer z = Integer.parseInt(y);
			principal = pfPrincipalHome.findById(z);
	
			if (principal == null || principal.getIdPfPrincipal() == null) {
				return creaXMLoutDettaglio(creaEsitoDettaglio("02", "Cittadino non trovato"));
			}
		}
		else {
			return creaXMLoutDettaglio(creaEsitoDettaglio("02", "Cittadino non trovato"));
		}

		String codiceStr = "00";
		String descrStr = "Nessun Errore";

		RispostaDettaglioCittadino documentOut = creaEsitoDettaglio(codiceStr, descrStr);

		it.eng.myportal.siler.getDettaglioCittadino.out.RispostaDettaglioCittadino.DettaglioCittadino datiDettaglio = cittadinoEjb.creaDocumentDettaglioAccount(principal);

		documentOut.setDettaglioCittadino(datiDettaglio);

		log.debug("XML getDettaglioCittadino da SIL -- " + xmlDettaglioCittadino);
		return creaXMLoutDettaglio(documentOut);

	}

	private void checkCredenziali(String login, String pwd) throws Exception {
		String user[] = null;
		user = wsEndpointHome.getWebServiceUser(TipoServizio.GET_CITTATINO);

		String userLocal = user[0];
		String pwdLocal = user[1];

		if (!login.equals(userLocal)) {
			throw new MyPortalException("Username o Password errati");
		}

		if (!pwd.equals(pwdLocal)) {
			throw new MyPortalException("Username o Password errati");
		}
	}

	private DettaglioCittadino creaDettaglioCittadinoIn(String xmlAccountCittadino) {
		JAXBContext jaxbContext;
		DettaglioCittadino account = null;
		try {
			jaxbContext = JAXBContext.newInstance(DettaglioCittadino.class);
			Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
			account = (DettaglioCittadino) jaxbUnmarshaller.unmarshal(new StringReader(xmlAccountCittadino));
		} catch (JAXBException e) {
			throw new MyPortalException("02","InputXML non valido");
		}
		return account;
	}

	private AccountCittadino creaAccountCittadinoIn(String xmlAccountCittadino) {
		JAXBContext jaxbContext;
		AccountCittadino account = null;
		try {
			jaxbContext = JAXBContext.newInstance(AccountCittadino.class);
			Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
			account = (AccountCittadino) jaxbUnmarshaller.unmarshal(new StringReader(xmlAccountCittadino));
		} catch (JAXBException e) {
			throw new MyPortalException("02","InputXML non valido");
		}
		return account;
	}

	private RispostaAccountCittadino creaEsitoAccount(String cod, String msg) {

		RispostaAccountCittadino r = new RispostaAccountCittadino();
		Esito e = new Esito();
		e.setCodice(cod);
		e.setDescrizione(msg);
		r.setEsito(e);
		return r;
	}

	private RispostaDettaglioCittadino creaEsitoDettaglio(String cod, String msg) {

		RispostaDettaglioCittadino r = new RispostaDettaglioCittadino();
		it.eng.myportal.siler.getDettaglioCittadino.out.RispostaDettaglioCittadino.Esito e = new it.eng.myportal.siler.getDettaglioCittadino.out.RispostaDettaglioCittadino.Esito();
		e.setCodice(cod);
		e.setDescrizione(msg);
		r.setEsito(e);
		return r;
	}

	private String creaXMLoutAccount(RispostaAccountCittadino account) {
		try {
			JAXBContext jc = JAXBContext
					.newInstance(it.eng.myportal.siler.getAccountCittadino.out.RispostaAccountCittadino.class);
			Marshaller marshaller = jc.createMarshaller();
			Schema schema = Utils.getXsdSchema("servizi_cittadino" + File.separator
					+ "outputXML_getAccountCittadino.xsd");

			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, false);

			marshaller.setSchema(schema);
			StringWriter writer = new StringWriter();
			marshaller.marshal(account, writer);
			String xmlRichiesta = writer.getBuffer().toString();
			return xmlRichiesta;
		} catch (PropertyException e) {
			log.error("creaXMLoutAccount RispostaAccountCittadino: " + e);
			return GENERIC_ERROR_ACCOUNT;
		} catch (SAXException e) {
			log.error("creaXMLoutAccount RispostaAccountCittadino: " + e);
			return GENERIC_ERROR_ACCOUNT;
		} catch (JAXBException e) {
			log.error("creaXMLoutDettaglio RispostaAccountCittadino: " + e);
			return GENERIC_ERROR_ACCOUNT;
		}
	}

	private String creaXMLoutDettaglio(RispostaDettaglioCittadino account) {
		try {
			JAXBContext jc = JAXBContext
					.newInstance(RispostaDettaglioCittadino.class);
			Marshaller marshaller = jc.createMarshaller();
			Schema schema = Utils.getXsdSchema("servizi_cittadino" + File.separator
					+ "outputXML_getDettaglioCittadino.xsd");

			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, false);

			marshaller.setSchema(schema);
			StringWriter writer = new StringWriter();
			marshaller.marshal(account, writer);
			String xmlRichiesta = writer.getBuffer().toString();
			return xmlRichiesta;
		} catch (PropertyException e) {
			log.error("creaXMLoutDettaglio: " + e);
			return GENERIC_ERROR_ACCOUNT;
		} catch (SAXException e) {
			log.error("creaXMLoutDettaglio: " + e);
			return GENERIC_ERROR_ACCOUNT;
		} catch (JAXBException e) {
			log.error("creaXMLoutDettaglio: " + e);
			return GENERIC_ERROR_ACCOUNT;
		}
	}

}
