package it.eng.myportal.rest.services;

import it.eng.myportal.auth.AuthUtil;
import it.eng.myportal.dtos.DatiLavoratoreAppuntamentoDTO;
import it.eng.myportal.dtos.DeProvinciaDTO;
import it.eng.myportal.dtos.UtenteCompletoDTO;
import it.eng.myportal.entity.home.UtenteInfoHome;
import it.eng.myportal.entity.home.WsEndpointHome;
import it.eng.myportal.exception.StatoOccupazionaleException;
import it.eng.myportal.utils.ConstantsSingleton;
import it.eng.myportal.utils.Utils;
import it.eng.sil.coop.webservices.statooccupazionale.GetStatoOccupazionaleProxy;
import it.eng.sil.coop.webservices.statooccupazionale.GetStatoOccupazionaleSoapBindingStub;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.security.Principal;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.activation.DataHandler;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.soap.AttachmentPart;
import javax.xml.transform.TransformerException;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONObject;
import org.json.XML;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

@Stateless
@Path("rest/services/")
public class StatoOccupazionale {

	private static final String RECARSI_AL_CPI = "Ti invitiamo a recarti al tuo Centro per l’Impiego di competenza per verificare la congruenza dei dati";

	protected static Log log = LogFactory.getLog(StatoOccupazionale.class);

	// una serie di costanti che corrispondono ai tag degli xml di input e di
	// risposta.
	private final static int ESITO_POSITIVO = 0;

	private final static String EL_STATO_OCC = "StatoOccupazionale";
	private final static String EL_CODICE_FISC = "CodiceFiscale";
	private final static String EL_DATA_RIF = "DataRiferimento";
	private final static String EL_ID_PROV = "IdProvincia";
	private final static String EL_COMP_AMM = "CompAmm";
	private final static String EL_STAMPA = "Stampa";
	private final static String EL_MODELLO_STAMPA = "ModelloStampa";
	private final static String EL_PROTOCOLLAZIONE = "Protocollazione";

	private final static String EL_RISPOSTA = "Risposta";
	private final static String EL_ESITO = "Esito";
	private final static String EL_CODICE = "codice";
	private final static String EL_DESCRIZIONE = "descrizione";
	private final static String ERRORE_GENERICO = "Ti invitiamo a recarti al tuo Centro per l'Impego di competenza per verificare la congruenza dei dati";

	private SimpleDateFormat formatter1 = new SimpleDateFormat("yyyy-MM-dd");

	@EJB
	UtenteInfoHome utenteInfoHome;

	@EJB
	WsEndpointHome wsEndpointHome;

	private Document buildInputXml(String codiceFiscaleIn, String codiceProvinciaIn, boolean checkCompAmm,
			boolean includiModelloStampa) throws ParserConfigurationException {
		// Abbiamo un modelloStampa separato solo per l'Umbria.
		String modelloStampaString;
		if (ConstantsSingleton.COD_REGIONE == ConstantsSingleton.COD_REGIONE_UMBRIA) {
			modelloStampaString = "SituazioneLavPatronato_UMB_CC.rpt";
		} else {
			modelloStampaString = "SituazioneLavPatronato_RER_CC.rpt";
		}

		// Costruisco il documento XML con l'input.
		DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = builderFactory.newDocumentBuilder();
		Document document = builder.newDocument();
		Element statoOccupazionale = (Element) document.createElement(EL_STATO_OCC);
		document.appendChild(statoOccupazionale);

		// TODO finch non verranno aggiornati tutti i sil
		if (!checkCompAmm) {
			statoOccupazionale.setAttribute(EL_COMP_AMM, checkCompAmm ? "1" : "0");
		}

		Element codiceFiscale = document.createElement(EL_CODICE_FISC);
		codiceFiscale.appendChild(document.createTextNode(codiceFiscaleIn.toUpperCase()));
		Element dataRiferimento = document.createElement(EL_DATA_RIF);
		dataRiferimento.appendChild(document.createTextNode(formatter1.format(new Date())));
		Element idProvincia = document.createElement(EL_ID_PROV);
		idProvincia.appendChild(document.createTextNode(codiceProvinciaIn));

		statoOccupazionale.appendChild(codiceFiscale);
		statoOccupazionale.appendChild(dataRiferimento);
		statoOccupazionale.appendChild(idProvincia);

		if (includiModelloStampa) {
			Element stampa = document.createElement(EL_STAMPA);
			Element modelloStampa = document.createElement(EL_MODELLO_STAMPA);
			modelloStampa.appendChild(document.createTextNode(modelloStampaString));
			Element protocollazione = document.createElement(EL_PROTOCOLLAZIONE);
			protocollazione.appendChild(document.createTextNode("ON"));

			statoOccupazionale.appendChild(stampa);
			stampa.appendChild(modelloStampa);
			stampa.appendChild(protocollazione);
		}
		return document;
	}

	private String buildAndValidateInputXml(String codiceFiscaleIn, String codiceProvinciaIn,
			boolean checkCompetenzaAmministrativa, boolean includiModelloStampa) throws TransformerException,
			ParserConfigurationException, SAXException, IOException {
		Document document = buildInputXml(codiceFiscaleIn, codiceProvinciaIn, checkCompetenzaAmministrativa,
				includiModelloStampa);
		String ret = Utils.domToString(document);
		Utils.validateXml(ret, "servizi" + File.separator + "statoOccupazionale.xsd");
		return ret;
	}

	private UtenteCompletoDTO securityCheck(Integer idPfPrincipal, HttpServletRequest request)
			throws StatoOccupazionaleException {
		// SICUREZZA. Controllo che l'utente abbia richiesto il proprio
		// stato occupazionale e che ne abbia la facoltà
		Principal princ = request.getUserPrincipal();
		if (princ == null) {
			log.error("StatoOccupazionale fallita: sessione nulla, utente non loggato");
			throw new StatoOccupazionaleException();
		}
		String username = AuthUtil.removeSocialPrefix(princ.getName());

		if (idPfPrincipal == null) {
			log.error("StatoOccupazionale fallita: id utente non passato");
			throw new StatoOccupazionaleException();
		}

		UtenteCompletoDTO utenteDTO = utenteInfoHome.findDTOCompletoByUsername(username);
		if (utenteDTO == null) {
			log.error("StatoOccupazionale fallita: recupero utente nullo username=" + username);
			throw new StatoOccupazionaleException();
		}
		if (utenteDTO.getId().intValue() != idPfPrincipal.intValue()) {
			throw new StatoOccupazionaleException("Non puoi richiedere lo stato occupazionale di un altro lavoratore.");
		}
		if (!utenteDTO.getAbilitatoServizi()) {
			throw new StatoOccupazionaleException("Non sei abilitato all'utilizzo dei servizi per il cittadino.");
		}
		if (StringUtils.isEmpty(utenteDTO.getCodiceFiscale())) {
			throw new StatoOccupazionaleException("Non hai indicato il tuo codice fiscale.");
		}
		return utenteDTO;
	}

	public JSONObject getStatoOccupazionaleInternal(Integer idPfPrincipal, String codProvincia)
			throws StatoOccupazionaleException {
		String response = "";
		try {
			UtenteCompletoDTO utente = utenteInfoHome.findDTOCompletoById(idPfPrincipal);
			String input = buildAndValidateInputXml(utente.getCodiceFiscale(),
					utente.getProvinciaRiferimento().getId(), true, false);
			GetStatoOccupazionaleProxy service = new GetStatoOccupazionaleProxy(
					wsEndpointHome.getStatoOccupazionaleAddress(codProvincia));
			response = service.getStatoOccupazionale(input);
			JSONObject resp = XML.toJSONObject(response);
			JSONObject risposta = resp.getJSONObject(EL_RISPOSTA);
			JSONObject esito = risposta.getJSONObject(EL_ESITO);
			Integer codEsito = esito.getInt(EL_CODICE);
			if (codEsito.intValue() == ESITO_POSITIVO) {
				return risposta;
			} else {
				throw new StatoOccupazionaleException(esito.getString(EL_DESCRIZIONE));
			}
		} catch (Exception e) {
			String errorMsg = "Errore durante la chiamata a GetStatoOccupazionale: " + e.toString();
			log.error(errorMsg);
			throw new StatoOccupazionaleException(errorMsg);
		}
	}

	@GET
	@Path("stato_occupazionale")
	public String getStatoOccupazionale(@QueryParam("user_id") Integer idPfPrincipal,
			@Context HttpServletRequest request) {
		String response = null;
		try {
			UtenteCompletoDTO utenteDTO = securityCheck(idPfPrincipal, request);
			DeProvinciaDTO provinciaRif = utenteDTO.getProvinciaRiferimento();
			boolean checkCompetenzaAmministrativa = true;
			String ret = buildAndValidateInputXml(utenteDTO.getCodiceFiscale(), provinciaRif.getId(),
					checkCompetenzaAmministrativa, true);

			GetStatoOccupazionaleProxy service = new GetStatoOccupazionaleProxy(
					wsEndpointHome.getStatoOccupazionaleAddress(provinciaRif.getId()));

			response = service.getStatoOccupazionale(ret);

			JSONObject resp = XML.toJSONObject(response);
			JSONObject risposta = resp.getJSONObject(EL_RISPOSTA);
			JSONObject esito = risposta.getJSONObject(EL_ESITO);
			Integer codEsito = esito.getInt(EL_CODICE);
			if (codEsito.intValue() == ESITO_POSITIVO) {
				return risposta.toString();
			}
			throw new StatoOccupazionaleException(esito.getString(EL_DESCRIZIONE));
		} catch (ParserConfigurationException e) {
			log.error("response=" + response);
			log.error("ParserConfigurationException: " + e.getMessage());
			return Utils.buildErrorResponse(RECARSI_AL_CPI).toString();
		} catch (TransformerException e) {
			log.error("response=" + response);
			log.error("TransformerException: " + e.getMessage());
			return Utils.buildErrorResponse(RECARSI_AL_CPI).toString();
		} catch (SAXException e) {
			log.error("response=" + response);
			log.error("SAXException: " + e.getMessage());
			return Utils.buildErrorResponse(RECARSI_AL_CPI).toString();
		} catch (IOException e) {
			log.error("response=" + response);
			log.error("IOException: " + e.getMessage());
			return Utils.buildErrorResponse(RECARSI_AL_CPI).toString();
		} catch (StatoOccupazionaleException e) {
			log.error("response=" + response);
			log.error("StatoOccupazionaleException: " + e.getMessage());
			return Utils.buildErrorResponse(e.getMessage()).toString();
		} catch (Exception e) {
			log.error("Exception: " + e.getMessage());
			return Utils.buildErrorResponse(RECARSI_AL_CPI).toString();
		}
	}

	public DatiLavoratoreAppuntamentoDTO getStatoOccupazionale(String codProvincia, String codiceFiscale,
			boolean checkCompetenzaAmministrativa) throws Exception {
		String input = null;
		String statoOccupazionale = null;
		DatiLavoratoreAppuntamentoDTO res = DatiLavoratoreAppuntamentoDTO.makeEmpty();
		try {
			input = buildAndValidateInputXml(codiceFiscale, codProvincia, checkCompetenzaAmministrativa, true);

			GetStatoOccupazionaleProxy service = new GetStatoOccupazionaleProxy(
					wsEndpointHome.getStatoOccupazionaleAddress(codProvincia));

			statoOccupazionale = service.getStatoOccupazionale(input);
			JSONObject resp = XML.toJSONObject(statoOccupazionale);
			JSONObject risposta = resp.getJSONObject(EL_RISPOSTA);
			JSONObject esito = risposta.getJSONObject(EL_ESITO);
			Integer codEsito = esito.getInt(EL_CODICE);
			if (codEsito.intValue() == ESITO_POSITIVO) {
				res = DatiLavoratoreAppuntamentoDTO.makeFromStatoOccupazionaleJSON(resp);
			}
		} catch (Exception e) {
			log.error("getStatoOccupazionale(" + codProvincia + ", " + codiceFiscale + "): " + e.getMessage());
			throw e;
		}
		return res;
	}

	@GET
	@Path("stampa_stato_occupazionale")
	public Response getStampaStatoOccupazionale(@QueryParam("user_id") Integer idPfPrincipal,
			@Context HttpServletRequest request) {
		Response response;
		try {
			UtenteCompletoDTO utenteDTO = securityCheck(idPfPrincipal, request);
			DeProvinciaDTO provinciaRif = utenteDTO.getProvinciaRiferimento();
			boolean checkCompetenzaAmministrativa = true;
			String inputRequest = buildAndValidateInputXml(utenteDTO.getCodiceFiscale(), provinciaRif.getId(),
					checkCompetenzaAmministrativa, true);

			GetStatoOccupazionaleProxy service = new GetStatoOccupazionaleProxy(
					wsEndpointHome.getStatoOccupazionaleAddress(provinciaRif.getId()));

			String ret = service.getStampaStatoOccupazionale(inputRequest).toString();

			JSONObject resp = XML.toJSONObject(ret);
			JSONObject risposta = resp.getJSONObject(EL_RISPOSTA);
			JSONObject esito = risposta.getJSONObject(EL_ESITO);
			Integer codEsito = esito.getInt(EL_CODICE);
			if (codEsito.intValue() == ESITO_POSITIVO) {
				GetStatoOccupazionaleSoapBindingStub stub = (GetStatoOccupazionaleSoapBindingStub) service
						.getGetStatoOccupazionale();
				Object[] rr = stub.getAttachments();
				DataHandler pdf = ((AttachmentPart) rr[0]).getDataHandler();
				InputStream in = null;
				in = pdf.getInputStream();

				ResponseBuilder responseBuilder = Response.ok(in).type("application/pdf");
				String fname = "attachment; filename=statoOccupazionale.pdf";
				responseBuilder.header("Content-Disposition", fname);
				response = responseBuilder.build();

				return response;
			}
			throw new StatoOccupazionaleException(esito.getString(EL_DESCRIZIONE));
		} catch (ParserConfigurationException e) {
			log.error(e);
			return Utils.fileErrorResponseBuilder(ERRORE_GENERICO);
		} catch (TransformerException e) {
			log.error(e);
			return Utils.fileErrorResponseBuilder(ERRORE_GENERICO);
		} catch (SAXException e) {
			log.error(e);
			return Utils.fileErrorResponseBuilder(ERRORE_GENERICO);
		} catch (IOException e) {
			log.error(e);
			return Utils.fileErrorResponseBuilder(ERRORE_GENERICO);
		} catch (StatoOccupazionaleException e) {
			log.error(e);
			return Utils.fileErrorResponseBuilder(e.getMessage());
		} catch (Exception e) {
			log.error(e);
			return Utils.fileErrorResponseBuilder(ERRORE_GENERICO);
		}
	}

}
