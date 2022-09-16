package it.eng.myportal.rest.services;

import it.eng.myportal.auth.AuthUtil;
import it.eng.myportal.dtos.DeProvinciaDTO;
import it.eng.myportal.dtos.UtenteCompletoDTO;
import it.eng.myportal.entity.home.UtenteInfoHome;
import it.eng.myportal.entity.home.WsEndpointHome;
import it.eng.myportal.enums.MessaggiConfermaPeriodica;
import it.eng.myportal.exception.ConfermaPeriodicaException;
import it.eng.myportal.siler.getConfermaPeriodica.in.ConfermaPeriodica;
import it.eng.myportal.siler.getConfermaPeriodica.in.ObjectFactory;
import it.eng.myportal.siler.getConfermaPeriodica.out.Errore;
import it.eng.myportal.siler.getConfermaPeriodica.out.Esito;
import it.eng.myportal.utils.Utils;
import it.eng.sil.coop.webservices.confermaPeriodica.GetConfermaPeriodicaProxy;

import java.io.StringReader;
import java.io.StringWriter;
import java.rmi.RemoteException;
import java.security.Principal;
import java.util.HashMap;
import java.util.Map;

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

import org.apache.axis.AxisFault;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

@Stateless
@Path("rest/services/")
public class ConfermaPeriodicaService {

	protected static Log log = LogFactory.getLog(ConfermaPeriodicaService.class);

	@EJB
	private UtenteInfoHome utenteInfoHome;

	@EJB
	private WsEndpointHome wsEndpointHome;

	private Map<String, String> errorsMap = MessaggiConfermaPeriodica.asMap();

	@GET
	@Path("conferma_periodica")
	@Produces("application/json; charset=UTF-8")
	public String confermaPeriodica(@QueryParam("user_id") Integer idPfPrincipal, @Context HttpServletRequest request) {
		try {
			// SICUREZZA. Controllo che l'utente abbia richiesto il proprio
			// stato occupazionale e che ne abbia la facoltà
			Principal princ = request.getUserPrincipal();
			if (princ == null) {
				log.error("ConfermaPeriodica fallita: sessione nulla, utente non loggato");
				throw new ConfermaPeriodicaException();
			}
			String username = AuthUtil.removeSocialPrefix(princ.getName());

			if (idPfPrincipal == null) {
				log.error("ConfermaPeriodica fallita: id utente non passato");
				throw new ConfermaPeriodicaException();
			}

			UtenteCompletoDTO utenteDTO = utenteInfoHome.findDTOCompletoByUsername(username);
			if (utenteDTO == null) {
				log.error("ConfermaPeriodica fallita: recupero utente nullo username=" + username);
				throw new ConfermaPeriodicaException();
			}

			if (utenteDTO.getId().intValue() != idPfPrincipal.intValue()) {
				throw new ConfermaPeriodicaException("Non puoi eseguire la conferma periodica per un altro lavoratore.");
			}
			if (!utenteDTO.getAbilitatoServizi()) {
				throw new ConfermaPeriodicaException("Non sei abilitato all'utilizzo dei servizi per il cittadino.");
			}
			if (utenteDTO.getCodiceFiscale() == null || utenteDTO.getCodiceFiscale().isEmpty()) {
				throw new ConfermaPeriodicaException("Non hai indicato il tuo codice fiscale.");
			}

			String codiceFiscale = utenteDTO.getCodiceFiscale().toUpperCase();
			String email = utenteDTO.getEmail();
			String cellulare = utenteDTO.getCellulare();
			String inputXml = inputToXml(codiceFiscale, email, cellulare);

			DeProvinciaDTO provinciaRif = utenteDTO.getProvinciaRiferimento();
			String confermaPeriodicaAddress = wsEndpointHome.getConfermaPeriodicaAddress(provinciaRif.getId());
			GetConfermaPeriodicaProxy service = new GetConfermaPeriodicaProxy(confermaPeriodicaAddress);
			String outputXml = service.getConfermaPeriodica(inputXml);
			Esito esito = xmlToOutput(outputXml);

			/* messaggio di operazione completata con successo */
			String codice = esito.getCodice();
			String descrizione = null;
			if (codice.equalsIgnoreCase("OK")) {
				descrizione = MessaggiConfermaPeriodica.OK.getMessaggio();
			} else {
				codice = null;
			}

			/*
			 * chreo la mappa tra i codici di errore ricevuti e i messaggi da
			 * visualizzare.
			 * 
			 * NB Ale: secondo me non va bene, andrebbe wrappato l'errore ricevuto direttamente
			 */
			Map<String, String> resultErrors = new HashMap<String, String>();
			if (esito.getErrori() != null) {
				for (Errore errore : esito.getErrori().getErrore()) {

					// ALE
					if (errorsMap.containsKey(errore.getCodiceErrore()))
						resultErrors.put(errore.getCodiceErrore(), errorsMap.get(errore.getCodiceErrore()));
					/*
					 * for (String errCode : errorsMap.keySet()) { if
					 * (errCode.equals(errore.getCodiceErrore())) {
					 * resultErrors.put(errCode, errorsMap.get(errCode)); break;
					 * } }
					 */
				}
			}

			String result = Utils.buildConfermaPeriodicaResponse(codice, descrizione, resultErrors).toString();
			return result;
		} catch (ConfermaPeriodicaException e) {
			log.error(e.getMessage());
			Map<String, String> resultErrors = new HashMap<String, String>();
			resultErrors.put(e.getClass().getName(), e.getMessage());
			return Utils.buildConfermaPeriodicaResponse("", "", resultErrors).toString().toString();
		} catch (AxisFault e) {
			log.error(e.getMessage());
			Map<String, String> resultErrors = new HashMap<String, String>();
			resultErrors.put(MessaggiConfermaPeriodica.ERR_COMUNICAZIONE.getCodice(),
					MessaggiConfermaPeriodica.ERR_COMUNICAZIONE.getMessaggio());
			return Utils.buildConfermaPeriodicaResponse("", "", resultErrors).toString().toString();
		} catch (RemoteException e) {
			log.error(e.getMessage());
			Map<String, String> resultErrors = new HashMap<String, String>();
			resultErrors.put(MessaggiConfermaPeriodica.ERR_COMUNICAZIONE.getCodice(),
					MessaggiConfermaPeriodica.ERR_COMUNICAZIONE.getMessaggio());
			return Utils.buildConfermaPeriodicaResponse("", "", resultErrors).toString().toString();
		} catch (Exception e) {
			log.error(e.getMessage());
			Map<String, String> resultErrors = new HashMap<String, String>();
			resultErrors.put(MessaggiConfermaPeriodica.ERR_GENERICO.getCodice(),
					MessaggiConfermaPeriodica.ERR_GENERICO.getMessaggio());
			return Utils.buildConfermaPeriodicaResponse("", "", resultErrors).toString().toString();
		}
	}

	private String inputToXml(String codiceFiscale, String email, String cellulare) {
		ObjectFactory factory = new ObjectFactory();
		ConfermaPeriodica confermaPeriodica = factory.createConfermaPeriodica();
		String xml = "";

		if (codiceFiscale != null && !codiceFiscale.isEmpty()) {
			confermaPeriodica.setCodiceFiscale(codiceFiscale);
		}
		if (email != null && !email.isEmpty()) {
			confermaPeriodica.setEmail(email);
		}
		if (cellulare != null && !cellulare.isEmpty()) {
			confermaPeriodica.setCellulare(cellulare);
		}

		try {
			JAXBContext jaxbContext = JAXBContext.newInstance(ConfermaPeriodica.class);
			Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
			jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
			StringWriter writer = new StringWriter();
			jaxbMarshaller.marshal(confermaPeriodica, writer);
			xml = writer.getBuffer().toString();
		} catch (JAXBException e) {
			log.error(e.getMessage());
		}
		return xml;
	}

	private Esito xmlToOutput(String outputXml) {
		Esito esito = null;
		try {
			JAXBContext jaxbContext = JAXBContext.newInstance(Esito.class);
			Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
			JAXBElement<Esito> root = unmarshaller
					.unmarshal(new StreamSource(new StringReader(outputXml)), Esito.class);
			esito = root.getValue();
		} catch (JAXBException e) {
			log.error(e.getMessage());
		}
		return esito;
	}
}
