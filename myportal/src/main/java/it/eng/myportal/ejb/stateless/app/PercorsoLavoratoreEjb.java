package it.eng.myportal.ejb.stateless.app;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.activation.DataHandler;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.apache.axis.attachments.AttachmentPart;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.XML;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import it.eng.myportal.dtos.DeProvinciaDTO;
import it.eng.myportal.dtos.PercorsoLavoratoreElementDTO;
import it.eng.myportal.dtos.UtenteCompletoDTO;
import it.eng.myportal.entity.PfPrincipal;
import it.eng.myportal.entity.UtenteInfo;
import it.eng.myportal.entity.decodifiche.DeProvincia;
import it.eng.myportal.entity.home.PfPrincipalHome;
import it.eng.myportal.entity.home.UtenteInfoHome;
import it.eng.myportal.entity.home.WsEndpointHome;
import it.eng.myportal.exception.PercorsoLavoratoreException;
import it.eng.myportal.rest.app.CheckerSec;
import it.eng.myportal.utils.ConstantsSingleton;
import it.eng.myportal.utils.Utils;
import it.eng.sil.coop.webservices.percorsolavoratore.GetPercorsoLavoratoreProxy;
import it.eng.sil.coop.webservices.percorsolavoratore.GetPercorsoLavoratoreSoapBindingStub;

@Stateless
public class PercorsoLavoratoreEjb implements Serializable {

	private static final String RPT_PERCORSO_LAVORATORE = "PercorsoLavoratore_CC.rpt";
	private static final String RPT_PERCORSO_LAVORATORE_UMB = "PercorsoLavoratore_UMB_CC.rpt";

	private static final long serialVersionUID = 3173142865193514407L;

	protected static Log log = LogFactory.getLog(PercorsoLavoratoreEjb.class);

	private static final String TAG_JSON_RAPPORTI_DI_LAVORO = "rapportiDiLavoro";
	private static final String TAG_JSON_DIM_RAPPORTI_DI_LAVORO = "dimRapportiDiLavoro";

	private static final String NON_ACCETTATA_INF_PERC_LAV = "Necessaria l'accettazione dell'informativa sul percorso lavoratore";
	public final static String ERRORE_GENERICO = "Ti invitiamo a contattare al tuo Centro per l’impiego di competenza per ricevere maggiori informazioni";

	private static final String XSD_PERCORSO_LAVORATORE = "servizi" + File.separator + "percorsoLavoratore.xsd";
	private final static String EL_PERCORSO_LAVORATORE = "PercorsoLavoratore";
	private final static String EL_CODICE_FISC = "CodiceFiscale";
	private final static String EL_DATA_INIZIO = "DataInizio";
	private final static String EL_DATA_FINE = "DataFine";
	private final static String EL_ID_PROV = "IdProvincia";
	private final static String EL_TIPO_INFO = "TipologiaInformazione";

	private final static String RESP_ELEMENT = "element";
	private final static String RESP_RISPOSTA = "Risposta";
	private final static String RESP_ESITO = "Esito";
	private final static String RESP_CODICE = "codice";
	private final static String RESP_DESCRIZIONE_ESITO = "descrizione";

	private final static String TIPO_INFORMAZIONE = "B_S";

	//////////////////

	private final static String ST_STAMPA = "Stampa";
	private final static String ST_MODELLO = "ModelloStampa";
	private final static String ST_PROTOCOLLAZIONE = "Protocollazione";
	private final static String ST_INTESTAZIONE = "Intestazione";
	private final static String ST_INFO_ST_OCC = "InfoStatoOccupazionale";

	private final static String RESP_DESCRIZIONE = "Descrizione";
	private final static String RESP_TIPO = "Tipo";
	private final static String RESP_DATADA = "DataDa";
	private final static String RESP_DATAA = "DataA";

	private final static int ESITO_POSITIVO = 0;

	@EJB
	PfPrincipalHome pfPrincipalHome;

	@EJB
	UtenteInfoHome utenteInfoHome;

	@EJB
	WsEndpointHome wsEndpointHome;

	public String callPercorso(UtenteCompletoDTO utenteDTO, Date dataInizio, Date dataFine, String tipoInformazione)
			throws ParserConfigurationException, TransformerException, SAXException, IOException {

		final SimpleDateFormat formatter1 = new SimpleDateFormat("yyyy-MM-dd");
		DeProvinciaDTO provinciaRif = utenteDTO.getProvinciaRiferimento();

		DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = null;
		builder = builderFactory.newDocumentBuilder();
		Document document = builder.newDocument();
		Element percorsoLavoratore = (Element) document.createElement(EL_PERCORSO_LAVORATORE);
		document.appendChild(percorsoLavoratore);

		Element codiceFiscale = document.createElement(EL_CODICE_FISC);
		codiceFiscale.appendChild(document.createTextNode(utenteDTO.getCodiceFiscale().toUpperCase()));
		Element dataIni = document.createElement(EL_DATA_INIZIO);
		dataIni.appendChild(document.createTextNode(formatter1.format(dataInizio)));
		Element dataFin = document.createElement(EL_DATA_FINE);

		/* La data di fine non viene più passata da client, se non presente si imposta il giorno corrente */
		if (dataFine == null)
			dataFin.appendChild(document.createTextNode(formatter1.format(new Date())));
		else
			dataFin.appendChild(document.createTextNode(formatter1.format(dataFine)));

		Element idProvincia = document.createElement(EL_ID_PROV);
		idProvincia.appendChild(document.createTextNode(provinciaRif.getId()));
		Element tipoInfo = document.createElement(EL_TIPO_INFO);
		tipoInfo.appendChild(document.createTextNode(tipoInformazione));
		percorsoLavoratore.appendChild(codiceFiscale);
		percorsoLavoratore.appendChild(dataIni);
		percorsoLavoratore.appendChild(dataFin);
		percorsoLavoratore.appendChild(idProvincia);
		percorsoLavoratore.appendChild(tipoInfo);

		String ret = Utils.domToString(document);

		Utils.validateXml(ret, "servizi" + File.separator + "percorsoLavoratore.xsd");

		GetPercorsoLavoratoreProxy service = new GetPercorsoLavoratoreProxy(
				wsEndpointHome.getPercorsoLavoratoreAddress(provinciaRif.getId()));

		String response = service.getPercorsoLavoratore(ret);
		// Document resp = Utils.stringToDom(response);
		return response;
	}

	public static JSONArray toJSONArray(JSONObject jsonPercLavResponse)
			throws JSONException, PercorsoLavoratoreException {
		JSONObject risposta = jsonPercLavResponse.getJSONObject(RESP_RISPOSTA);
		JSONObject esito = risposta.getJSONObject(RESP_ESITO);
		int codEsito = esito.getInt(RESP_CODICE);
		if (codEsito != ESITO_POSITIVO) {
			throw new PercorsoLavoratoreException(esito.getString(RESP_DESCRIZIONE_ESITO));
		}

		JSONArray elementsArray = new JSONArray();
		if (risposta.has(RESP_ELEMENT)) {
			Object elements = risposta.get(RESP_ELEMENT);
			if (elements instanceof JSONArray) {
				elementsArray = (JSONArray) elements;
			} else {
				elementsArray.put(elements);
			}

		}
		return elementsArray;
	}

	public List<PercorsoLavoratoreElementDTO> toPercorsoLavoratoreElementList(JSONArray elementsArray)
			throws JSONException {
		List<PercorsoLavoratoreElementDTO> listaElementi = new ArrayList<PercorsoLavoratoreElementDTO>(
				elementsArray.length());
		for (int i = 0; i < elementsArray.length(); i++) {
			JSONObject element = (JSONObject) elementsArray.get(i);
			PercorsoLavoratoreElementDTO elemento = new PercorsoLavoratoreElementDTO();
			SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
			try {
				elemento.setDataDa(df.parse(element.getString(RESP_DATADA)));
			} catch (ParseException e) {
				log.warn("formato data DA errato: " + e.getMessage());
			}
			try {
				elemento.setDataA(df.parse(element.getString(RESP_DATAA).replace("{}", "")));
			} catch (ParseException e) {
				log.warn("formato data A errato: " + e.getMessage());
			}
			elemento.setTipo(element.getString(RESP_TIPO));
			elemento.setDescrizione(element.getString(RESP_DESCRIZIONE).replace("{}", ""));
			listaElementi.add(elemento);

		}
		return listaElementi;
	}

	public byte[] getStampaPercorsoLavoratore(String username, String dataInizio, String dataFine) throws Exception {
		try {
			String strDa = changeDateformat(dataInizio);
			String strA = changeDateformat(dataFine);

			String tipoString = TIPO_INFORMAZIONE;

			PfPrincipal pfPrincipal = pfPrincipalHome.findByUsername(username);
			if (pfPrincipal != null) {

				if (!pfPrincipal.getFlagAbilitatoServizi()) {
					throw new Exception(CheckerSec.NON_ABILITATO_AI_SERVIZI_AMMINISTRATIVI);
				}

				if (!pfPrincipal.getUtenteInfo().getFlgAcceptedInformativaPercLav()) {
					throw new Exception(NON_ACCETTATA_INF_PERC_LAV);
				}

				DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
				DocumentBuilder builder = null;
				UtenteInfo utenteInfo = utenteInfoHome.findByUsername(username);

				DeProvincia provinciaRif = utenteInfo.getDeProvincia();

				builder = builderFactory.newDocumentBuilder();
				Document document = builder.newDocument();
				Element percorsoLavoratore = (Element) document.createElement(EL_PERCORSO_LAVORATORE);
				document.appendChild(percorsoLavoratore);

				Element codiceFiscale = document.createElement(EL_CODICE_FISC);
				codiceFiscale.appendChild(document.createTextNode(utenteInfo.getCodiceFiscale().toUpperCase()));
				Element dataIni = document.createElement(EL_DATA_INIZIO);
				dataIni.appendChild(document.createTextNode(strDa));
				Element dataFin = document.createElement(EL_DATA_FINE);
				dataFin.appendChild(document.createTextNode(strA));
				Element idProvincia = document.createElement(EL_ID_PROV);
				idProvincia.appendChild(document.createTextNode(provinciaRif.getCodProvincia()));
				Element tipoInfo = document.createElement(EL_TIPO_INFO);
				tipoInfo.appendChild(document.createTextNode(tipoString));

				Element stampa = document.createElement(ST_STAMPA);
				Element modello = document.createElement(ST_MODELLO);
				modello.appendChild(
						document.createTextNode(getModelloStampaPercLavByRegione()));
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
				Utils.validateXml(inputRequest, XSD_PERCORSO_LAVORATORE);

				GetPercorsoLavoratoreProxy service = new GetPercorsoLavoratoreProxy(
						wsEndpointHome.getPercorsoLavoratoreAddress(provinciaRif.getCodProvincia()));

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
					InputStream in = pdf.getInputStream();
					byte[] byteArray = org.apache.commons.io.IOUtils.toByteArray(in);

					in.close();
					return byteArray;
				} else {
					throw new Exception(RESP_DESCRIZIONE_ESITO);

				}
			} else {

				throw new Exception(CheckerSec.TOKEN_NON_VALIDO_O_SCADUTO);
			}
		} catch (Exception e) {
			throw new Exception(CheckerSec.ERRORE_DI_SISTEMA);
		}

	}

	public JSONObject getPercorsoLavoratore(String username, String dataInizio, String dataFine, int maxRappLavori,
			int offset) {

		JSONObject root = new JSONObject();

		try {
			String strDa = changeDateformat(dataInizio);
			String strA = changeDateformat(dataFine);

			PfPrincipal pfPrincipal = pfPrincipalHome.findByUsername(username);
			if (pfPrincipal != null) {

				if (!pfPrincipal.getFlagAbilitatoServizi()) {
					root.put(CheckerSec.STATUS, CheckerSec.KO);
					root.put(CheckerSec.ERROR, CheckerSec.NON_ABILITATO_AI_SERVIZI_AMMINISTRATIVI);
					return root;
				}

				if (!pfPrincipal.getUtenteInfo().getFlgAcceptedInformativaPercLav()) {
					root.put(CheckerSec.STATUS, CheckerSec.KO);
					root.put(CheckerSec.ERROR, NON_ACCETTATA_INF_PERC_LAV);
					return root;
				}
				// TODO refactor: qui chiamare callPercorso()
				DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
				DocumentBuilder builder = null;
				UtenteInfo utenteInfo = utenteInfoHome.findByUsername(username);

				DeProvincia provinciaRif = utenteInfo.getDeProvincia();

				builder = builderFactory.newDocumentBuilder();
				Document document = builder.newDocument();
				Element percorsoLavoratore = (Element) document.createElement(EL_PERCORSO_LAVORATORE);
				document.appendChild(percorsoLavoratore);

				Element codiceFiscale = document.createElement(EL_CODICE_FISC);
				codiceFiscale.appendChild(document.createTextNode(utenteInfo.getCodiceFiscale().toUpperCase()));
				Element dataIni = document.createElement(EL_DATA_INIZIO);
				dataIni.appendChild(document.createTextNode(strDa));
				Element dataFin = document.createElement(EL_DATA_FINE);
				dataFin.appendChild(document.createTextNode(strA));
				Element idProvincia = document.createElement(EL_ID_PROV);
				idProvincia.appendChild(document.createTextNode(provinciaRif.getCodProvincia()));
				Element tipoInfo = document.createElement(EL_TIPO_INFO);
				tipoInfo.appendChild(document.createTextNode(TIPO_INFORMAZIONE));
				percorsoLavoratore.appendChild(codiceFiscale);
				percorsoLavoratore.appendChild(dataIni);
				percorsoLavoratore.appendChild(dataFin);
				percorsoLavoratore.appendChild(idProvincia);
				percorsoLavoratore.appendChild(tipoInfo);

				String ret = Utils.domToString(document);

				Utils.validateXml(ret, XSD_PERCORSO_LAVORATORE);

				GetPercorsoLavoratoreProxy service = new GetPercorsoLavoratoreProxy(
						wsEndpointHome.getPercorsoLavoratoreAddress(provinciaRif.getCodProvincia()));

				String response = service.getPercorsoLavoratore(ret);

				JSONObject resp = XML.toJSONObject(response);
				JSONObject risposta = resp.getJSONObject(RESP_RISPOSTA);
				JSONObject esito = risposta.getJSONObject(RESP_ESITO);
				int codEsito = esito.getInt(RESP_CODICE);

				if (codEsito == ESITO_POSITIVO) {
					root.put(CheckerSec.STATUS, CheckerSec.OK);

					JSONArray elementsArray = new JSONArray();
					if (risposta.has(RESP_ELEMENT)) {
						Object elements = risposta.get(RESP_ELEMENT);
						if (elements instanceof JSONArray) {
							elementsArray = (JSONArray) elements;
						} else {
							elementsArray.put(elements);
						}
					}

					int lunghezza = elementsArray.length();
					root.put(TAG_JSON_DIM_RAPPORTI_DI_LAVORO, lunghezza);

					JSONArray rappLavArr = new JSONArray();
					for (int i = offset; i < maxRappLavori + offset; i++) {
						if (i >= lunghezza)
							continue;
						rappLavArr.put(elementsArray.get(i));
					}
					root.put(TAG_JSON_RAPPORTI_DI_LAVORO, rappLavArr);

				} else {
					root.put(CheckerSec.STATUS, CheckerSec.KO);
					root.put(CheckerSec.ERROR, esito.getString(RESP_DESCRIZIONE_ESITO));
				}
			} else {
				root.put(CheckerSec.STATUS, CheckerSec.KO);
				root.put(CheckerSec.ERROR, CheckerSec.TOKEN_NON_VALIDO_O_SCADUTO);
			}
		} catch (Exception e) {

			try {
				root.put(CheckerSec.STATUS, CheckerSec.KO);
				root.put(CheckerSec.ERROR, ERRORE_GENERICO);
			} catch (JSONException e1) {
				log.error(CheckerSec.ERRORE_DI_SISTEMA, e1);
			}
		}
		return root;

	}

	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// **** private methods ****
	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	private String changeDateformat(String src) throws ParseException {

		SimpleDateFormat formatterSource = new SimpleDateFormat("dd/MM/yyyy");
		SimpleDateFormat formatterTarget = new SimpleDateFormat("yyyy-MM-dd");

		Date target = formatterSource.parse(src);
		return formatterTarget.format(target);
	}

	private String getModelloStampaPercLavByRegione() {
		String ret = null;

		if (ConstantsSingleton.COD_REGIONE == ConstantsSingleton.COD_REGIONE_UMBRIA)
			// Modello custom Umbria
			ret = RPT_PERCORSO_LAVORATORE_UMB;
		else
			// Modello generico
			ret = RPT_PERCORSO_LAVORATORE;

		return ret;

	}

}
