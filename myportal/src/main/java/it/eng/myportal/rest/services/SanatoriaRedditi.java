package it.eng.myportal.rest.services;

import it.eng.myportal.auth.AuthUtil;
import it.eng.myportal.dtos.DeProvinciaDTO;
import it.eng.myportal.dtos.UtenteCompletoDTO;
import it.eng.myportal.entity.home.UtenteInfoHome;
import it.eng.myportal.entity.home.WsEndpointHome;
import it.eng.myportal.exception.SanatoriaRedditiException;
import it.eng.myportal.utils.Utils;
import it.eng.sil.coop.webservices.sanatoria.SanaMovimentoProxy;

import java.io.File;
import java.io.IOException;
import java.security.Principal;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.XML;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

@Stateless
@Path("rest/services/")
public class SanatoriaRedditi {

	protected static Log log = LogFactory.getLog(SanatoriaRedditi.class);

	@EJB
	UtenteInfoHome utenteInfoHome;

	@EJB
	WsEndpointHome wsEndpointHome;

	private final static int ESITO_POSITIVO = 0;

	private final static String EL_SANATORIA_REDDITO = "SanatoriaReddito";
	private final static String EL_CODICE_FISC = "CodiceFiscale";
	private final static String EL_ID_PROV = "IdProvincia";
	private final static String EL_SET_MOVIMENTI = "SetMovimenti";
	private final static String EL_MOVIMENTO = "Mov";
	private final static String EL_PRGMOVIMENTO = "prgMovimento";
	private final static String EL_REDDITOMENSILESANATO = "RedditoMensileSanato";
	private final static String EL_NUMKLOMOV = "numklomov";

	private final static String EL_RISPOSTA = "Risposta";
	private final static String EL_ESITO = "Esito";
	private final static String EL_CODICE = "codice";
	private final static String EL_DESCRIZIONE = "descrizione";

	// elenco codici di risposta del ws che significano che la sanatoria è
	// andata a buon fine
	Set<Integer> okCodes = new HashSet<Integer>(Arrays.asList(new Integer[] { 0 }));

	// elenco dei codici di errore che indicano un errore durante la sanatoria e
	// l'utente
	// è tenuto a recarsi al CPI
	Set<Integer> errorCodes = new HashSet<Integer>(
			Arrays.asList(new Integer[] { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 99 }));

	// elenco di codici di errore di risposta del ws, che indicano che c'è un
	// errore nei dati inseriti
	// dall'utente che può essere corretto inserendo nuovamente i dati.
	Set<Integer> redoCodes = new HashSet<Integer>(Arrays.asList(new Integer[] { 12 }));

	/**
	 * Servizio per sanare i redditi. Il ws vuole in input l'elenco dei
	 * movimenti da sanare. Tale elenco viene fornito, già bello e pronto, dalla
	 * pagina attraverso il metodo sanaReddito
	 * 
	 * @param idPfPrincipal
	 * @param jsonObject
	 * @param request
	 * @return
	 */

	@POST
	@Path("sana_reddito")
	public String sanatoriaReddito(@QueryParam("user_id") Integer idPfPrincipal, @FormParam("json") String jsonObject,
			@Context HttpServletRequest request) {

		DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = null;
		try {
			// SICUREZZA. Controllo che l'utente abbia richiesto la sanatoria
			// per i propri redditi
			Principal princ = request.getUserPrincipal();
			if (princ == null) {
				log.error("SanatoriaRedditi fallita: sessione nulla, utente non loggato");
				throw new SanatoriaRedditiException();
			}
			String username = AuthUtil.removeSocialPrefix(princ.getName());
			
			if (idPfPrincipal == null) {
				log.error("SanatoriaRedditi fallita: id utente non passato");
				throw new SanatoriaRedditiException();
			}
			
			UtenteCompletoDTO utenteDTO = utenteInfoHome.findDTOCompletoByUsername(username);
			if (utenteDTO == null) {
				log.error("SanatoriaRedditi fallita: recupero utente nullo username="+username);
				throw new SanatoriaRedditiException();
			}
			if (utenteDTO.getId().intValue() != idPfPrincipal.intValue()) {
				throw new SanatoriaRedditiException("Non puoi richiedere la sanatoria redditi di un altro lavoratore.");
			}
			if (!utenteDTO.getAbilitatoServizi()) {
				throw new SanatoriaRedditiException("Non sei abilitato all'utilizzo dei servizi per il cittadino.");
			}
			if (StringUtils.isEmpty(utenteDTO.getCodiceFiscale())) {
				throw new SanatoriaRedditiException("Non hai indicato il tuo codice fiscale.");
			}
			DeProvinciaDTO provinciaRif = utenteDTO.getProvinciaRiferimento();

			builder = builderFactory.newDocumentBuilder();

			JSONObject jsonInput = new JSONObject(jsonObject);
			
			JSONArray movimenti = jsonInput.getJSONArray("Mov");
			
			Document document = builder.newDocument();
			
			Element sanatoriaReddito = (Element) document.createElement(EL_SANATORIA_REDDITO);
			document.appendChild(sanatoriaReddito);
			Element codiceFiscale = document.createElement(EL_CODICE_FISC);
			codiceFiscale.appendChild(document.createTextNode(utenteDTO.getCodiceFiscale().toUpperCase()));			
			Element idProvincia = document.createElement(EL_ID_PROV);
			idProvincia.appendChild(document.createTextNode(provinciaRif.getId()));
			sanatoriaReddito.appendChild(codiceFiscale);
			sanatoriaReddito.appendChild(idProvincia);
			
			Element movs = document.createElement(EL_SET_MOVIMENTI);
			sanatoriaReddito.appendChild(movs);
			for (int i = 0 ; i < movimenti.length() ; i++) {
				JSONObject movimento = (JSONObject) movimenti.get(i);
				
				Element mov = document.createElement(EL_MOVIMENTO);
				Element prgMovimento = document.createElement(EL_PRGMOVIMENTO);
				Element reddito = document.createElement(EL_REDDITOMENSILESANATO);
				Element numklo = document.createElement(EL_NUMKLOMOV);
				prgMovimento.appendChild(document.createTextNode(movimento.getString(EL_PRGMOVIMENTO)));
				
				try {
					Double dd = Double.valueOf(movimento.getString(EL_REDDITOMENSILESANATO));
					String redditoStr = String.format(Locale.ITALIAN,"%1$.2f", dd).replace(',', '.'); //XXX max security!! :D
					reddito.appendChild(document.createTextNode(redditoStr));
				} catch (NumberFormatException e) {
					return Utils.buildErrorResponse("redo","Il valore inserito per la retribuzione non è valido.").toString();
				}
				
				numklo.appendChild(document.createTextNode(movimento.getString(EL_NUMKLOMOV)));

				mov.appendChild(prgMovimento);
				mov.appendChild(reddito);
				mov.appendChild(numklo);
				movs.appendChild(mov);
			}
						
			sanatoriaReddito.appendChild(movs);
			
			String xmlInput = Utils.domToString(document);
			//xmlInput = xmlInput.substring(xmlInput.indexOf("<SanatoriaReddito"));
			// validazione dell'input prima di inviare
			Utils.validateXml(xmlInput, "servizi" + File.separator + "SanatoriaReddito.xsd");

			SanaMovimentoProxy service = new SanaMovimentoProxy(wsEndpointHome.getSanaRedditiAddress(provinciaRif
					.getId()));

			String response = service.putSanatoriaReddito(xmlInput);

			JSONObject resp = XML.toJSONObject(response);
			JSONObject risposta = resp.getJSONObject(EL_RISPOSTA);
			JSONObject esito = risposta.getJSONObject(EL_ESITO);
			Integer codEsito = esito.getInt(EL_CODICE);
			if (okCodes.contains(codEsito)) {
				return resp.toString();
			} else if (redoCodes.contains(codEsito)) {
				return Utils.buildErrorResponse("redo", esito.getString(EL_DESCRIZIONE)).toString();
			} else {
				return Utils.buildErrorResponse(esito.getString(EL_DESCRIZIONE)).toString();
			}
		} catch (ParserConfigurationException e) {
			log.error(e);
			return Utils.buildErrorResponse("Errore durante il recupero dei dati").toString();
		} catch (SAXException e) {
			log.error(e);
			return Utils.buildErrorResponse("Errore durante il recupero dei dati").toString();
		} catch (IOException e) {
			log.error(e);
			return Utils.buildErrorResponse("Errore durante il recupero dei dati").toString();
		} catch (SanatoriaRedditiException e) {
			log.error(e);
			return Utils.buildErrorResponse(e.getMessage()).toString();
		} catch (Exception e) {
			log.error(e);
			return Utils.buildErrorResponse("Errore durante il recupero dei dati").toString();
		}
	}

}
