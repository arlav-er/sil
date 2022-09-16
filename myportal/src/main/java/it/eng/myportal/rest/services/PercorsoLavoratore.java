package it.eng.myportal.rest.services;

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
import javax.xml.transform.TransformerException;

import org.apache.axis.attachments.AttachmentPart;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONObject;
import org.json.XML;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import it.eng.myportal.auth.AuthUtil;
import it.eng.myportal.dtos.DeProvinciaDTO;
import it.eng.myportal.dtos.UtenteCompletoDTO;
import it.eng.myportal.ejb.stateless.app.PercorsoLavoratoreEjb;
import it.eng.myportal.entity.home.UtenteInfoHome;
import it.eng.myportal.entity.home.WsEndpointHome;
import it.eng.myportal.exception.PercorsoLavoratoreException;
import it.eng.myportal.utils.ConstantsSingleton;
import it.eng.myportal.utils.Utils;
import it.eng.sil.coop.webservices.percorsolavoratore.GetPercorsoLavoratoreProxy;
import it.eng.sil.coop.webservices.percorsolavoratore.GetPercorsoLavoratoreSoapBindingStub;

@Stateless
@Path("rest/services/")
public class PercorsoLavoratore {

	protected static Log log = LogFactory.getLog(PercorsoLavoratore.class);
	// IESCONE recion
	private final static String EL_PERCORSO_LAVORATORE = "PercorsoLavoratore";
	private final static String EL_CODICE_FISC = "CodiceFiscale";
	private final static String EL_DATA_INIZIO = "DataInizio";
	private final static String EL_DATA_FINE = "DataFine";
	private final static String EL_ID_PROV = "IdProvincia";
	private final static String EL_TIPO_INFO = "TipologiaInformazione";

	private final static String ST_STAMPA = "Stampa";
	private final static String ST_MODELLO = "ModelloStampa";
	private final static String ST_PROTOCOLLAZIONE = "Protocollazione";
	private final static String ST_INTESTAZIONE = "Intestazione";
	private final static String ST_INFO_ST_OCC = "InfoStatoOccupazionale";

	private final static String RESP_RISPOSTA = "Risposta";
	private final static String RESP_ESITO = "Esito";
	private final static String RESP_CODICE = "codice";
	private final static String RESP_DESCRIZIONE_ESITO = "descrizione";

	private final static int ESITO_POSITIVO = 0;

	private SimpleDateFormat formatter1 = new SimpleDateFormat("yyyy-MM-dd");
	private SimpleDateFormat formatter2 = new SimpleDateFormat("dd/MM/yyyy");

	/*
	 * MOVIMENTI B_S con qualifica visualizzata e da CO O_S con qualifica visualizzato dichiarati/documentati
	 */
	private static final String TIPO_INFORMAZIONE = "B_S";

	@EJB
	UtenteInfoHome utenteInfoHome;

	@EJB
	WsEndpointHome wsEndpointHome;

	@GET
	@Path("stampa_percorso_lavoratore")
	public Response getStampaPercorsoLavoratore(@QueryParam("user_id") Integer idPfPrincipal,
			@QueryParam("data_inizio") String dataInizioStr, @QueryParam("data_fine") String dataFineStr,
			@Context HttpServletRequest request) {
		try {
			Date dataInizio = formatter2.parse(dataInizioStr);

			/* La data di fine non viene più passata da client, se non presente si imposta il giorno corrente */
			Date dataFine = null;
			if (dataFineStr != null)
				dataFine = formatter2.parse(dataFineStr);
			else
				dataFine = new Date();

			String tipoString = TIPO_INFORMAZIONE;

			DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = null;
			Response response;
			// SICUREZZA. Controllo che l'utente abbia richiesto il proprio
			// stato occupazionale e che ne abbia la facoltà
			Principal princ = request.getUserPrincipal();
			if (princ == null) {
				log.error("PercorsoLavoratore fallita: sessione nulla, utente non loggato");
				throw new PercorsoLavoratoreException();
			}
			String username = AuthUtil.removeSocialPrefix(princ.getName());

			if (idPfPrincipal == null) {
				log.error("PercorsoLavoratore fallita: id utente non passato");
				throw new PercorsoLavoratoreException();
			}

			UtenteCompletoDTO utenteDTO = utenteInfoHome.findDTOCompletoByUsername(username);
			if (utenteDTO == null) {
				log.error("PercorsoLavoratore fallita: recupero utente nullo username=" + username);
				throw new PercorsoLavoratoreException();
			}
			if (utenteDTO.getId().intValue() != idPfPrincipal.intValue()) {
				throw new PercorsoLavoratoreException(
						"Non puoi richiedere la stampa dello stato occupazionale di un altro lavoratore.");
			}
			if (!utenteDTO.getAbilitatoServizi()) {
				throw new PercorsoLavoratoreException("Non sei abilitato all'utilizzo dei servizi per il cittadino.");
			}
			if (StringUtils.isEmpty(utenteDTO.getCodiceFiscale())) {
				throw new PercorsoLavoratoreException("Non hai indicato il tuo codice fiscale.");
			}
			DeProvinciaDTO provinciaRif = utenteDTO.getProvinciaRiferimento();

			builder = builderFactory.newDocumentBuilder();
			Document document = builder.newDocument();
			Element percorsoLavoratore = (Element) document.createElement(EL_PERCORSO_LAVORATORE);
			document.appendChild(percorsoLavoratore);

			Element codiceFiscale = document.createElement(EL_CODICE_FISC);
			codiceFiscale.appendChild(document.createTextNode(utenteDTO.getCodiceFiscale().toUpperCase()));
			Element dataIni = document.createElement(EL_DATA_INIZIO);
			dataIni.appendChild(document.createTextNode(formatter1.format(dataInizio)));
			Element dataFin = document.createElement(EL_DATA_FINE);
			dataFin.appendChild(document.createTextNode(formatter1.format(dataFine)));
			Element idProvincia = document.createElement(EL_ID_PROV);
			idProvincia.appendChild(document.createTextNode(provinciaRif.getId()));
			Element tipoInfo = document.createElement(EL_TIPO_INFO);
			tipoInfo.appendChild(document.createTextNode(tipoString));

			Element stampa = document.createElement(ST_STAMPA);
			Element modello = document.createElement(ST_MODELLO);
			//TK: ESL4SIL-1408
			if (ConstantsSingleton.COD_REGIONE == ConstantsSingleton.COD_REGIONE_UMBRIA) {
				modello.appendChild(document.createTextNode("PercorsoLavoratore_UMB_CC.rpt"));
			}else {	
				modello.appendChild(document.createTextNode("PercorsoLavoratore_CC.rpt"));
			}
			Element protocollazione = document.createElement(ST_PROTOCOLLAZIONE);
			protocollazione.appendChild(document.createTextNode("ON"));
			Element intestazione = document.createElement(ST_INTESTAZIONE);
			intestazione.appendChild(document.createTextNode("ON"));
			Element infoSO = document.createElement(ST_INFO_ST_OCC);
			infoSO.appendChild(document.createTextNode("ON"));

			stampa.appendChild(modello);
			stampa.appendChild(protocollazione);
			stampa.appendChild(intestazione);
			stampa.appendChild(infoSO);

			percorsoLavoratore.appendChild(codiceFiscale);
			percorsoLavoratore.appendChild(dataIni);
			percorsoLavoratore.appendChild(dataFin);
			percorsoLavoratore.appendChild(idProvincia);
			percorsoLavoratore.appendChild(tipoInfo);
			percorsoLavoratore.appendChild(stampa);

			String inputRequest = Utils.domToString(document);
			Utils.validateXml(inputRequest, "servizi" + File.separator + "percorsoLavoratore.xsd");

			GetPercorsoLavoratoreProxy service = new GetPercorsoLavoratoreProxy(
					wsEndpointHome.getPercorsoLavoratoreAddress(provinciaRif.getId()));

			String ret = service.getStampaPercorsoLavoratore(inputRequest).toString();

			JSONObject resp = XML.toJSONObject(ret);
			JSONObject risposta = resp.getJSONObject(RESP_RISPOSTA);
			JSONObject esito = risposta.getJSONObject(RESP_ESITO);
			Integer codEsito = esito.getInt(RESP_CODICE);
			if (codEsito.intValue() == ESITO_POSITIVO) {
				GetPercorsoLavoratoreSoapBindingStub stub = (GetPercorsoLavoratoreSoapBindingStub) service
						.getGetPercorsoLavoratore();
				Object[] rr = stub.getAttachments();
				DataHandler pdf = ((AttachmentPart) rr[0]).getDataHandler();
				InputStream in = null;

				in = pdf.getInputStream();

				ResponseBuilder responseBuilder = Response.ok(in).type("application/pdf");
				String fname = "attachment; filename=percorsoLavoratore.pdf";
				responseBuilder.header("Content-Disposition", fname);
				response = responseBuilder.build();

				return response;
			}
			throw new PercorsoLavoratoreException(esito.getString(RESP_DESCRIZIONE_ESITO));
		} catch (ParserConfigurationException e) {
			log.error(e);
			return Utils.fileErrorResponseBuilder(PercorsoLavoratoreEjb.ERRORE_GENERICO);
		} catch (TransformerException e) {
			log.error(e);
			return Utils.fileErrorResponseBuilder(PercorsoLavoratoreEjb.ERRORE_GENERICO);
		} catch (SAXException e) {
			log.error(e);
			return Utils.fileErrorResponseBuilder(PercorsoLavoratoreEjb.ERRORE_GENERICO);
		} catch (IOException e) {
			log.error(e);
			return Utils.fileErrorResponseBuilder(PercorsoLavoratoreEjb.ERRORE_GENERICO);
		} catch (PercorsoLavoratoreException e) {
			log.error(e);
			return Utils.fileErrorResponseBuilder(e.getMessage());
		} catch (Exception e) {
			log.error(e);
			return Utils.fileErrorResponseBuilder(PercorsoLavoratoreEjb.ERRORE_GENERICO);
		}
	}
}
