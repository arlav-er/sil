package it.eng.myportal.rest.services;

import it.eng.myportal.auth.AuthUtil;
import it.eng.myportal.dtos.DeCpiDTO;
import it.eng.myportal.dtos.DeProvinciaDTO;
import it.eng.myportal.dtos.MsgMessaggioDTO;
import it.eng.myportal.dtos.UtenteCompletoDTO;
import it.eng.myportal.dtos.WsStampaDTO;
import it.eng.myportal.entity.ejb.NotificationBuilder;
import it.eng.myportal.entity.home.DidHome;
import it.eng.myportal.entity.home.UtenteInfoHome;
import it.eng.myportal.entity.home.WsEndpointHome;
import it.eng.myportal.entity.home.WsStampaHome;
import it.eng.myportal.entity.home.decodifiche.DeCpiHome;
import it.eng.myportal.enums.ErroriStipulaDID;
import it.eng.myportal.enums.TipoStampa;
import it.eng.myportal.exception.StipulaDidException;
import it.eng.myportal.utils.CfUtils;
import it.eng.myportal.utils.ConstantsSingleton;
import it.eng.myportal.utils.Utils;
import it.eng.sil.base.business.GamificationRemoteClient;
import it.eng.sil.base.exceptions.GamificationException;
import it.eng.sil.coop.webservices.did.StipulaDidProxy;
import it.eng.sil.coop.webservices.did.StipulaDidSoapBindingStub;

import java.io.File;
import java.security.Principal;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.activation.DataHandler;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.axis.attachments.AttachmentPart;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONObject;
import org.json.XML;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

@Stateless
@Path("rest/services/")
public class StipulaDid {

	protected static Log log = LogFactory.getLog(StipulaDid.class);

	@EJB
	private UtenteInfoHome utenteInfoHome;

	@EJB
	private DeCpiHome deCpiHome;

	@EJB
	private WsStampaHome wsStampaHome;

	@EJB
	private WsEndpointHome wsEndpointHome;

	@EJB
	private DidHome didHome;

	@EJB
	private NotificationBuilder notificationBuilder;

	private final static String EL_DATI_DID = "DatiDid";
	private final static String EL_CODICE_FISC = "CodiceFiscale";
	private final static String EL_DATA_DIC = "DataDichiarazione";
	private final static String EL_TIPO_DIC = "TipoDichiarazione";
	private final static String EL_ID_PROV = "IdProvincia";
	private final static String EL_STAMPA = "Stampa";
	private final static String EL_MODELLO_STAMPA = "ModelloStampa";
	private final static String EL_PROTOCOLLAZIONE = "Protocollazione";
	private final static String EL_INTESTAZIONE = "Intestazione";
	private final static String EL_LAVORATORE = "Lavoratore";
	private final static String EL_NOME = "nome";
	private final static String EL_COGNOME = "cognome";
	private final static String EL_DATA_NASCITA = "dataNascita";
	private final static String EL_COMUNE_NASCITA = "comuneNascita";
	private final static String EL_SESSO = "sesso";
	private final static String EL_CITTADINANZA = "cittadinanza";
	private final static String EL_DOMICILIO = "domicilio";
	private final static String EL_DOMICILIO_COMUNE = "comune";
	private final static String EL_DOMICILIO_INDIRIZZO = "indirizzo";
	private final static String EL_RESIDENZA = "residenza";
	private final static String EL_RESIDENZA_COMUNE = "comune";
	private final static String EL_RESIDENZA_INDIRIZZO = "indirizzo";
	private final static String EL_RISCHIODISOCC = "rischiodisoccupazione";
	private final static String EL_DATA_LICENZIAMENTO = "datalicenziamento";
	private final static String EL_EMAIL = "email";
	private final static String EL_CELLULARE = "cellulare";

	private final static String EL_RISPOSTA = "Risposta";
	private final static String EL_ESITO = "Esito";
	private final static String EL_CODICE = "codice";
	private final static String EL_DESCRIZIONE = "descrizione";
	private final static String EL_FILEID = "fileId";
	private final static String EL_CPI = "CPI";
	private final static String EL_TELCPI = "telCPI";

	private SimpleDateFormat formatter1 = new SimpleDateFormat("yyyy-MM-dd");

	// elenco codici di risposta del ws che significano che la richiesta è
	// andata a buon fine
	Set<Integer> okCodes = new HashSet<Integer>(Arrays.asList(new Integer[] { 0 }));
	// elenco dei codici di errore che indicano una situazione che può essere
	// sanata solo tramite CPI.
	Map<String, String> errorCodes = ErroriStipulaDID.asMap();
	// elenco di codici di errore di risposta del ws, che indicano che il
	// lavoratore può sanare la propria situazione
	// per rihiedere la did e pertanto bisogna mostrare la pagina di lista dei
	// movimenti
	Set<Integer> elencoMovimentiCodes = new HashSet<Integer>(Arrays.asList(new Integer[] { 10 }));

	/**
	 * Questo webservice viene usato dall'Emilia Romagna da dicembre 2015. Non chiama il servizio del SIL, ma
	 * semplicemente genera e restituisce una stampa all'interno di MyPortal.
	 * 
	 * Aggiornamento, luglio 2016: la RER ha ricominciato ad usare il WS del SIL, quindi questa funzionalità non viene
	 * più usata da nessuno. La lasciamo in caso qualcuno ce la chiedesse in futuro.
	 */
	@GET
	@Path("stipula_did_rer")
	@Produces("application/json; charset=UTF-8")
	public String stipulaDidTemp4Gennaio(@QueryParam("user_id") Integer idPfPrincipal,
			@Context HttpServletRequest request) {

		DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = null;
		try {
			// SICUREZZA. Controllo che l'utente abbia richiesto il proprio
			// stato occupazionale e che ne abbia la facoltà
			Principal princ = request.getUserPrincipal();
			if (princ == null) {
				log.error("StipulaDid fallita: sessione nulla, utente non loggato");
				throw new StipulaDidException();
			}
			String username = AuthUtil.removeSocialPrefix(princ.getName());

			if (idPfPrincipal == null) {
				log.error("StipulaDid fallita: id utente non passato");
				throw new StipulaDidException();
			}

			UtenteCompletoDTO utenteDTO = utenteInfoHome.findDTOCompletoByUsername(username);
			if (utenteDTO == null) {
				log.error("StipulaDid fallita: recupero utente nullo username=" + username);
				throw new StipulaDidException();
			}
			if (utenteDTO.getId().intValue() != idPfPrincipal.intValue()) {
				throw new StipulaDidException("Non puoi richiedere la stipula DID di un altro lavoratore.");
			}
			if (!utenteDTO.getAbilitatoServizi()) {
				throw new StipulaDidException("Non sei abilitato all'utilizzo dei servizi per il cittadino.");
			}
			if (StringUtils.isEmpty(utenteDTO.getCodiceFiscale())) {
				throw new StipulaDidException("Non hai indicato il tuo codice fiscale.");
			}
			DeProvinciaDTO provinciaRif = utenteDTO.getProvinciaRiferimento();

			DeCpiDTO deCpi = deCpiHome.findDTOByCodComune(utenteDTO.getComuneDomicilio().getId());

			JSONObject resp = new JSONObject();
			// JSONObject risposta = resp.getJSONObject(EL_RISPOSTA);
			// JSONObject esito = new JSONObject();
			// Integer codEsito = 0;

			byte[] bytes = IOUtils.toByteArray(didHome.createStampaDidPdfFile(idPfPrincipal));
			WsStampaDTO wsStampa = new WsStampaDTO();
			wsStampa.setContenuto(bytes);
			wsStampa.setCodTipoStampa(TipoStampa.DID.getCodice());
			wsStampa.setIdPfPrincipal(idPfPrincipal);
			wsStampa = wsStampaHome.persistDTO(wsStampa, idPfPrincipal);
			resp.put("status", "success");
			resp.put(EL_FILEID, wsStampa.getId());
			resp.put(EL_CPI, deCpi.getDescrizione());
			resp.put(EL_TELCPI, deCpi.getTel());
			Set<MsgMessaggioDTO> messaggi = notificationBuilder.buildNotifications(deCpi, utenteDTO, new Date());
			notificationBuilder.sendNotification(messaggi);
			return resp.toString();
		} catch (StipulaDidException e) {
			log.error(e);
			return Utils.buildErrorResponse(e.getMessage()).toString();
		} catch (Exception e) {
			log.error(e);
			return Utils.buildErrorResponse("").toString();
		}
	}

	/**
	 * Questo servizio chiama il WS del SIL per stipulare la DID. E' stato disattivato per la RER da dicembre 2015 a
	 * luglio 2016.
	 */
	@GET
	@Path("stipula_did")
	@Produces("application/json; charset=UTF-8")
	public String stipulaDid(@QueryParam("user_id") Integer idPfPrincipal,
			@QueryParam("rischio_disoccupazione") String rischioDisoccupazione,
			@QueryParam("data_licenziamento") String dataLicenziamentoString,
			@QueryParam("data_lettera_licenziamento") String dataLetteraLicenziamentoString,
			@Context HttpServletRequest request) {

		DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = null;
		SimpleDateFormat formatterInputDate = new SimpleDateFormat("dd/MM/yyyy");

		try {
			// SICUREZZA. Controllo che l'utente abbia richiesto il proprio
			// stato occupazionale e che ne abbia la facoltà
			Principal princ = request.getUserPrincipal();
			if (princ == null) {
				log.error("StipulaDid fallita: sessione nulla, utente non loggato");
				throw new StipulaDidException();
			}
			String username = AuthUtil.removeSocialPrefix(princ.getName());

			if (idPfPrincipal == null) {
				log.error("StipulaDid fallita: id utente non passato");
				throw new StipulaDidException();
			}

			UtenteCompletoDTO utenteDTO = utenteInfoHome.findDTOCompletoByUsername(username);
			if (utenteDTO == null) {
				log.error("StipulaDid fallita: recupero utente nullo username=" + username);
				throw new StipulaDidException();
			}
			if (utenteDTO.getId().intValue() != idPfPrincipal.intValue()) {
				throw new StipulaDidException("Non puoi richiedere la stipula DID di un altro lavoratore.");
			}
			if (!utenteDTO.getAbilitatoServizi()) {
				throw new StipulaDidException("Non sei abilitato all'utilizzo dei servizi per il cittadino.");
			}
			if (StringUtils.isEmpty(utenteDTO.getCodiceFiscale())) {
				throw new StipulaDidException("Non hai indicato il tuo codice fiscale.");
			}
			DeProvinciaDTO provinciaRif = utenteDTO.getProvinciaRiferimento();

			DeCpiDTO deCpi = deCpiHome.findDTOByCodComune(utenteDTO.getComuneDomicilio().getId());

			builder = builderFactory.newDocumentBuilder();
			Document document = builder.newDocument();
			Element datiDid = (Element) document.createElement(EL_DATI_DID);
			document.appendChild(datiDid);

			Element codiceFiscale = document.createElement(EL_CODICE_FISC);
			codiceFiscale.appendChild(document.createTextNode(utenteDTO.getCodiceFiscale().toUpperCase()));
			Element dataDichiarazione = document.createElement(EL_DATA_DIC);
			dataDichiarazione.appendChild(document.createTextNode(formatter1.format(new Date())));
			Element tipoDichiarazione = document.createElement(EL_TIPO_DIC);
			tipoDichiarazione.appendChild(document.createTextNode("ID"));
			Element idProvincia = document.createElement(EL_ID_PROV);
			idProvincia.appendChild(document.createTextNode(provinciaRif.getId()));
			Element stampa = document.createElement(EL_STAMPA);
			Element modelloStampa = document.createElement(EL_MODELLO_STAMPA);
			modelloStampa.appendChild(document.createTextNode("DichImmDisp_CC.rpt"));
			Element protocollazione = document.createElement(EL_PROTOCOLLAZIONE);
			protocollazione.appendChild(document.createTextNode("ON"));
			Element intestazione = document.createElement(EL_INTESTAZIONE);
			intestazione.appendChild(document.createTextNode("ON"));

			datiDid.appendChild(codiceFiscale);
			datiDid.appendChild(dataDichiarazione);
			datiDid.appendChild(tipoDichiarazione);
			datiDid.appendChild(idProvincia);
			datiDid.appendChild(stampa);
			stampa.appendChild(modelloStampa);
			stampa.appendChild(protocollazione);
			stampa.appendChild(intestazione);

			// Luglio 2016: per tutte le regioni, allego i dati del lavoratore.
			// In questo modo il SIL può creare l'anagrafica se questo non esiste.
			if (utenteDTO.getNome() == null || utenteDTO.getCognome() == null || utenteDTO.getDataNascita() == null
					|| utenteDTO.getComuneNascita() == null || utenteDTO.getCittadinanza() == null
					|| utenteDTO.getComuneDomicilio() == null || utenteDTO.getIndirizzoDomicilio() == null
					|| utenteDTO.getComuneResidenza() == null || utenteDTO.getIndirizzoResidenza() == null) {
				log.error("Mancano dati per l'anagrafica.");
				return Utils.buildErrorResponse("errorAnagrafica", "Mancano dati per l'anagrafica").toString();
			}

			Element lavoratore = document.createElement(EL_LAVORATORE);

			Element nome = document.createElement(EL_NOME);
			nome.appendChild(document.createTextNode(utenteDTO.getNome()));
			Element cognome = document.createElement(EL_COGNOME);
			cognome.appendChild(document.createTextNode(utenteDTO.getCognome()));
			Element dataNascita = document.createElement(EL_DATA_NASCITA);
			dataNascita.appendChild(document.createTextNode(formatter1.format(utenteDTO.getDataNascita())));
			Element comuneNascita = document.createElement(EL_COMUNE_NASCITA);
			comuneNascita.appendChild(document.createTextNode(utenteDTO.getComuneNascita().getId()));
			Element sesso = document.createElement(EL_SESSO);
			if (utenteDTO.getGenere() != null) {
				sesso.appendChild(document.createTextNode(utenteDTO.getGenere().getId()));
			} else {
				sesso.appendChild(document.createTextNode(CfUtils.getSesso(utenteDTO.getCodiceFiscale())));
			}

			Element cittadinanza = document.createElement(EL_CITTADINANZA);
			cittadinanza.appendChild(document.createTextNode(utenteDTO.getCittadinanza().getId()));

			Element domicilio = document.createElement(EL_DOMICILIO);
			Element comuneDomicilio = document.createElement(EL_DOMICILIO_COMUNE);
			comuneDomicilio.appendChild(document.createTextNode(utenteDTO.getComuneDomicilio().getId()));
			Element indirizzoDomicilio = document.createElement(EL_DOMICILIO_INDIRIZZO);
			indirizzoDomicilio.appendChild(document.createTextNode(utenteDTO.getIndirizzoDomicilio()));
			domicilio.appendChild(comuneDomicilio);
			domicilio.appendChild(indirizzoDomicilio);

			Element residenza = document.createElement(EL_RESIDENZA);
			Element comuneResidenza = document.createElement(EL_RESIDENZA_COMUNE);
			comuneResidenza.appendChild(document.createTextNode(utenteDTO.getComuneResidenza().getId()));
			Element indirizzoResidenza = document.createElement(EL_RESIDENZA_INDIRIZZO);
			indirizzoResidenza.appendChild(document.createTextNode(utenteDTO.getIndirizzoResidenza()));
			residenza.appendChild(comuneResidenza);
			residenza.appendChild(indirizzoResidenza);

			Element rischioDisoccupazioneElement = document.createElement(EL_RISCHIODISOCC);
			rischioDisoccupazioneElement.appendChild(document.createTextNode(rischioDisoccupazione));

			Element dataLicenziamentoElement = null;
			if (dataLicenziamentoString != null && !dataLicenziamentoString.isEmpty()
					&& !dataLicenziamentoString.toUpperCase().equals("NULL")) {
				Date dataLicenziamento = formatterInputDate.parse(dataLicenziamentoString);
				dataLicenziamentoElement = document.createElement(EL_DATA_LICENZIAMENTO);
				dataLicenziamentoElement.appendChild(document.createTextNode(formatter1.format(dataLicenziamento)));
			}

			Element emailElement = null;
			if (utenteDTO.getEmail() != null && !utenteDTO.getEmail().trim().isEmpty()) {
				emailElement = document.createElement(EL_EMAIL);
				emailElement.appendChild(document.createTextNode(utenteDTO.getEmail()));
			}

			Element cellulareElement = null;
			if (utenteDTO.getCellulare() != null && !utenteDTO.getCellulare().trim().isEmpty()) {
				cellulareElement = document.createElement(EL_CELLULARE);
				cellulareElement.appendChild(document.createTextNode(utenteDTO.getCellulare()));
			}

			lavoratore.appendChild(nome);
			lavoratore.appendChild(cognome);
			lavoratore.appendChild(dataNascita);
			lavoratore.appendChild(comuneNascita);
			lavoratore.appendChild(sesso);
			lavoratore.appendChild(cittadinanza);
			lavoratore.appendChild(domicilio);
			lavoratore.appendChild(residenza);
			lavoratore.appendChild(rischioDisoccupazioneElement);
			if (dataLicenziamentoElement != null)
				lavoratore.appendChild(dataLicenziamentoElement);
			if (emailElement != null)
				lavoratore.appendChild(emailElement);
			if (cellulareElement != null)
				lavoratore.appendChild(cellulareElement);
			datiDid.appendChild(lavoratore);

			// Creo l'XML di input del servizio e lo invoco.
			String ret = Utils.domToString(document);
			Utils.validateXml(ret, "servizi" + File.separator + "did.xsd");

			String stipulaDidAddress = wsEndpointHome.getStipulaDidAddress(provinciaRif.getId());
			StipulaDidProxy service = new StipulaDidProxy(stipulaDidAddress);
			// "<Risposta><Esito><codice>8</codice></Esito></Risposta>"
			String preRet = service.putCreaDID(ret);
			log.debug("DID da SIL preEncoding: " + preRet);

			String response = new String(preRet.getBytes(), "UTF-8");

			log.debug("DID da SIL postEncodig: " + response);

			JSONObject resp = XML.toJSONObject(response);
			JSONObject risposta = resp.getJSONObject(EL_RISPOSTA);
			JSONObject esito = risposta.getJSONObject(EL_ESITO);
			Integer codEsito = esito.getInt(EL_CODICE);
			if (okCodes.contains(codEsito.intValue())) {
				StipulaDidSoapBindingStub stub = (StipulaDidSoapBindingStub) service.getStipulaDid();
				Object[] rr = stub.getAttachments();
				DataHandler pdf = ((AttachmentPart) rr[0]).getDataHandler();

				byte[] bytes = Utils.getBytesFromDataHandler(pdf);
				WsStampaDTO wsStampa = new WsStampaDTO();
				wsStampa.setContenuto(bytes);
				wsStampa.setCodTipoStampa(TipoStampa.DID.getCodice());
				wsStampa.setIdPfPrincipal(idPfPrincipal);
				wsStampa.setFlgDidRischioDisoccupazione(rischioDisoccupazione.equals("SI"));
				if (wsStampa.getFlgDidRischioDisoccupazione()) {
					wsStampa.setDtDidLicenziamento(formatterInputDate.parse(dataLicenziamentoString));
					wsStampa.setDtDidLetteraLicenziamento(formatterInputDate.parse(dataLetteraLicenziamentoString));
				}
				wsStampa = wsStampaHome.persistDTO(wsStampa, idPfPrincipal);
				resp.put(EL_FILEID, wsStampa.getId());
				resp.put(EL_CPI, deCpi.getDescrizione());
				resp.put(EL_TELCPI, deCpi.getTel());
				Set<MsgMessaggioDTO> messaggi = notificationBuilder.buildNotifications(deCpi, utenteDTO, new Date());
				notificationBuilder.sendNotification(messaggi);

				// Inserisco il badge 'did completa' con valore 1 (se l'ha già, questa richiesta verrà ignorata)
				GamificationRemoteClient gamificationClient = new GamificationRemoteClient();
				try {
					if (gamificationClient.isGamificationEnabled(ConstantsSingleton.Gamification.IS_ENABLED_ENDPOINT)) {
						gamificationClient.assignBadge(ConstantsSingleton.Gamification.ASSIGN_BADGE_ENDPOINT, "DIDREQ",
								idPfPrincipal, 1.0);
					}
				} catch (GamificationException e) {
					log.error("Errore durante la assignBadge in StipulaDid: " + e.toString());
				}

				return resp.toString();
			} else if (ConstantsSingleton.COD_REGIONE.equals(ConstantsSingleton.COD_REGIONE_UMBRIA)
					&& (codEsito.intValue() == 10 || codEsito.intValue() == 19)) {
				// N.B: Questa è una pezza. SOLO se sono in Umbria e il codErrore è 10 o 19, segnalo al
				// front-end (implementato in services.js) che devo stampare un messaggio specifico.
				return Utils.buildErrorResponse("errorUmbria", errorCodes.get(codEsito.toString())).toString();
			} else if (elencoMovimentiCodes.contains(codEsito.intValue())) {
				return Utils.buildErrorResponse("movimenti", esito.getString(EL_DESCRIZIONE)).toString();
			} else if (codEsito.intValue() == 15) {
				return Utils.buildErrorResponseDIDpresente(errorCodes.get(codEsito.toString())).toString();
			} else {
				return Utils.buildErrorResponse(errorCodes.get(codEsito.toString())).toString();
			}
		} catch (StipulaDidException e) {
			log.error(e);
			return Utils.buildErrorResponse(e.getMessage()).toString();
		} catch (Exception e) {
			log.error(e);
			// return
			// Utils.buildErrorResponse("Errore durante il recupero dei dati").toString();
			return Utils.buildErrorResponse("").toString();
		}
	}
}
