package it.eng.myportal.rest.services;

import java.io.File;
import java.io.StringReader;
import java.io.StringWriter;
import java.security.Principal;
import java.util.Map;

import javax.activation.DataHandler;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;

import org.apache.axis.attachments.AttachmentPart;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONException;
import org.json.JSONObject;
import org.xml.sax.SAXException;

import it.eng.myportal.auth.AuthUtil;
import it.eng.myportal.dtos.DeProvinciaDTO;
import it.eng.myportal.dtos.UtenteCompletoDTO;
import it.eng.myportal.dtos.WsStampaDTO;
import it.eng.myportal.entity.home.UtenteInfoHome;
import it.eng.myportal.entity.home.WsEndpointHome;
import it.eng.myportal.entity.home.WsStampaHome;
import it.eng.myportal.enums.ErroriRinnovoPatto;
import it.eng.myportal.enums.TipoStampa;
import it.eng.myportal.exception.RinnovoPattoException;
import it.eng.myportal.utils.Utils;
import it.eng.sil.coop.webservices.rinnovaPatto.RinnovaPattoProxy;
import it.eng.sil.coop.webservices.rinnovaPatto.RinnovaPattoSoapBindingStub;

@Stateless
@Path("rest/services/")
public class RinnovoPatto {

	protected static Log log = LogFactory.getLog(RinnovoPatto.class);

	@EJB
	private UtenteInfoHome utenteInfoHome;

	@EJB
	private WsStampaHome wsStampaHome;

	@EJB
	private WsEndpointHome wsEndpointHome;

	// codice di esito positivo
	private final String OK = "00";
	// elenco errori possibili
	Map<String, String> errorCodes = ErroriRinnovoPatto.asMap();

	@GET
	@Path("rinnovo_patto")
	@Produces("application/json; charset=UTF-8")
	public String rinnovoPatto(@QueryParam("user_id") Integer idPfPrincipal, @Context HttpServletRequest request) {
		try {
			// SICUREZZA. Controllo che l'utente abbia richiesto il proprio
			// stato occupazionale e che ne abbia la facoltà
			Principal princ = request.getUserPrincipal();
			if (princ == null) {
				log.error("RinnovoPatto fallito: sessione nulla, utente non loggato");
				throw new RinnovoPattoException();
			}
			String username = AuthUtil.removeSocialPrefix(princ.getName());

			if (idPfPrincipal == null) {
				log.error("RinnovoPatto fallito: id utente non passato");
				throw new RinnovoPattoException();
			}

			UtenteCompletoDTO utenteDTO = utenteInfoHome.findDTOCompletoByUsername(username);
			if (utenteDTO == null) {
				log.error("RinnovoPatto fallito: recupero utente nullo username=" + username);
				throw new RinnovoPattoException();
			}
			if (utenteDTO.getId().intValue() != idPfPrincipal.intValue()) {
				throw new RinnovoPattoException("Non puoi richiedere il rinnovo del patto di un altro lavoratore.");
			}
			if (!utenteDTO.getAbilitatoServizi()) {
				throw new RinnovoPattoException("Non sei abilitato all'utilizzo dei servizi per il cittadino.");
			}
			if (StringUtils.isEmpty(utenteDTO.getCellulare())) {
				throw new RinnovoPattoException("Non hai indicato il tuo numero di cellulare.");
			}
			if (StringUtils.isEmpty(utenteDTO.getCodiceFiscale())) {
				throw new RinnovoPattoException("Non hai indicato il tuo codice fiscale.");
			}
			if (StringUtils.isEmpty(utenteDTO.getEmail())) {
				throw new RinnovoPattoException("Non hai indicato il tuo indirizzo e-mail.");
			}
			DeProvinciaDTO provinciaRif = utenteDTO.getProvinciaRiferimento();
			if (provinciaRif == null) {
				throw new RinnovoPattoException("Non hai indicato la tua provincia di riferimento.");
			}

			String inputXML = marshallInputXML(utenteDTO.getCellulare(), utenteDTO.getCodiceFiscale(),
					utenteDTO.getEmail());
			Utils.validateXml(inputXML, "rinnovaPatto" + File.separator + "rinnovaPatto_in.xsd");

			String wsAddress = wsEndpointHome.getRinnovoPattoAddress(provinciaRif.getId());
			RinnovaPattoProxy service = new RinnovaPattoProxy(wsAddress);
			String outputXML = service.putRinnovaPatto(inputXML);
			it.eng.myportal.ws.rinnovoPatto.output.Esito esito = unmarshallOutputXML(outputXML);

			if (esito.getCodice().equals(OK)) {
				RinnovaPattoSoapBindingStub stub = (RinnovaPattoSoapBindingStub) service.getRinnovaPatto();
				Object[] attachments = stub.getAttachments();
				DataHandler pdf = ((AttachmentPart) attachments[0]).getDataHandler();

				byte[] bytes = Utils.getBytesFromDataHandler(pdf);
				WsStampaDTO wsStampa = new WsStampaDTO();
				wsStampa.setContenuto(bytes);
				wsStampa.setCodTipoStampa(TipoStampa.RINNOVO_PATTO.getCodice());
				wsStampa.setIdPfPrincipal(idPfPrincipal);
				wsStampa = wsStampaHome.persistDTO(wsStampa, idPfPrincipal);

				String data = makeResponse(wsStampa.getId()).toString();
				return data;
			} else {
				return Utils.buildErrorResponse(errorCodes.get(esito.getCodice())).toString();
			}
		} catch (RinnovoPattoException e) {
			log.error(e);
			return Utils.buildErrorResponse(e.getMessage()).toString();
		} catch (Exception e) {
			log.error(e);
			return Utils.buildErrorResponse("").toString();
		}
	}

	private String marshallInputXML(String cellulare, String codiceFiscale, String email) {
		String inputXML = "";

		it.eng.myportal.ws.rinnovoPatto.input.ObjectFactory objectFactory = new it.eng.myportal.ws.rinnovoPatto.input.ObjectFactory();
		it.eng.myportal.ws.rinnovoPatto.input.RinnovoPatto rinnovoPatto = objectFactory.createRinnovoPatto();
		rinnovoPatto.setCellulare(cellulare);
		rinnovoPatto.setCodiceFiscale(codiceFiscale);
		rinnovoPatto.setEmail(email);

		try {
			JAXBContext jaxbContext = JAXBContext.newInstance(it.eng.myportal.ws.rinnovoPatto.input.RinnovoPatto.class);
			Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
			jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
			Schema schema = Utils.getXsdSchema("rinnovaPatto" + File.separator + "rinnovaPatto_in.xsd");
			jaxbMarshaller.setSchema(schema);
			StringWriter writer = new StringWriter();
			jaxbMarshaller.marshal(rinnovoPatto, writer);
			inputXML = writer.getBuffer().toString();
		} catch (JAXBException e) {
			log.error(e);
		} catch (SAXException e) {
			log.error(e);
		}

		return inputXML;
	}

	private it.eng.myportal.ws.rinnovoPatto.output.Esito unmarshallOutputXML(String outputXML) {
		it.eng.myportal.ws.rinnovoPatto.output.Esito esito = null;
		try {
			JAXBContext jaxbContext = JAXBContext.newInstance(it.eng.myportal.ws.rinnovoPatto.output.Esito.class);
			Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
			JAXBElement<it.eng.myportal.ws.rinnovoPatto.output.Esito> root = unmarshaller.unmarshal(new StreamSource(
					new StringReader(outputXML)), it.eng.myportal.ws.rinnovoPatto.output.Esito.class);
			esito = root.getValue();
		} catch (JAXBException e) {
			log.error(e.getMessage());
		}
		return esito;
	}

	private JSONObject makeResponse(Integer fileId) {
		JSONObject obj = new JSONObject();
		try {
			obj.put("status", "success");
			obj.put("fileId", fileId);
			return obj;
		} catch (JSONException ex) {
			log.error("Errore durante la costruzione dell'errore. o_O: " + ex.getMessage());
			return null;
		}
	}
}
