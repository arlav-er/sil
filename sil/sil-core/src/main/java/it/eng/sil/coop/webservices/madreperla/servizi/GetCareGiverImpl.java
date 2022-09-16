package it.eng.sil.coop.webservices.madreperla.servizi;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.util.QueryExecutor;

import it.eng.sil.Values;
import it.eng.sil.util.Utils;

public class GetCareGiverImpl extends MadreperlaServiceImpl {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(GetCareGiverImpl.class.getName());
	static final private String GETCAREGIVERINPUTSCHEMA = "getCareGiver_input.xsd";

	static final private String GETCAREGIVEROUTPUTSCHEMA = "getCareGiver_output.xsd";

	public GetCareGiverImpl() {
		super(GETCAREGIVERINPUTSCHEMA, GETCAREGIVEROUTPUTSCHEMA, "GetCareGiverImpl");
	}

	public void esegui() throws CareGiverNotFoundException, ParserConfigurationException, TransformerException {
		NodeList listaNodi = docInput.getElementsByTagName("codiceFiscale");
		Node cfNode = listaNodi.item(0).getFirstChild();
		String codiceFiscale = "";

		if (cfNode != null) {
			codiceFiscale = cfNode.getNodeValue();

			String _response = getCareGiver(codiceFiscale);

			response = _response;
		} else
			response = "";
	}

	private String getCareGiver(String codiceFiscale)
			throws CareGiverNotFoundException, ParserConfigurationException, TransformerException {
		String response = "";

		SourceBean anag = (SourceBean) QueryExecutor.executeQuery("MP_ANAGRAFICA_CAREGIVER",
				new Object[] { codiceFiscale }, "SELECT", Values.DB_SIL_DATI);

		if (anag == null)
			throw new CareGiverNotFoundException("Il codice fiscale inviato non corrisponde a nessun elemento nel db");

		codiceFiscale = Utils.notNull(anag.getAttribute("ROW.CODICEFISCALE"));

		if (codiceFiscale.equals(""))
			throw new CareGiverNotFoundException("Il codice fiscale inviato non corrisponde a nessun elemento nel db");

		String ragioneSociale = Utils.notNull(anag.getAttribute("ROW.RAGIONESOCIALE"));
		// String nome = Utils.notNull(anag.getAttribute("NOME"));
		// String cognome = Utils.notNull(anag.getAttribute("COGNOME"));
		String sesso = Utils.notNull(anag.getAttribute("ROW.SESSO"));
		String dataNascita = Utils.notNull(anag.getAttribute("ROW.DATANASCITA"));
		String codComNascita = Utils.notNull(anag.getAttribute("ROW.CODCOMUNENASCITA"));
		String comuneNascita = Utils.notNull(anag.getAttribute("ROW.COMUNENASCITA"));
		// String luogoNascita = comuneNascita;
		String codProvNascita = Utils.notNull(anag.getAttribute("ROW.CODPROVINCIANASCITA"));
		String provNascita = Utils.notNull(anag.getAttribute("ROW.PROVINCIANASCITA"));
		String codStatoNascita = Utils.notNull(anag.getAttribute("ROW.CODSTATONASCITA"));
		String statoNascita = Utils.notNull(anag.getAttribute("ROW.STATONASCITA"));
		String indirizzo = Utils.notNull(anag.getAttribute("ROW.INDIRIZZO"));
		String localita = Utils.notNull(anag.getAttribute("ROW.LOCALITA"));
		String codComune = Utils.notNull(anag.getAttribute("ROW.CODCOMUNE"));
		String comune = Utils.notNull(anag.getAttribute("ROW.COMUNE"));
		String codProvincia = Utils.notNull(anag.getAttribute("ROW.CODPROVINCIA"));
		String provincia = Utils.notNull(anag.getAttribute("ROW.PROVINCIA"));
		String cap = Utils.notNull(anag.getAttribute("ROW.CAP"));
		/*
		 * String codCittadinanza = Utils.notNull(anag.getAttribute("CODCITTADINANZA")); String cittadinanza =
		 * Utils.notNull(anag.getAttribute("CITTADINANZA")); String statoNascita =
		 * Utils.notNull(anag.getAttribute("STATONASCITA")); String indResidenza =
		 * Utils.notNull(anag.getAttribute("INDRESIDENZA")); String locResidenza =
		 * Utils.notNull(anag.getAttribute("LOCRESIDENZA")); String codComResidenza =
		 * Utils.notNull(anag.getAttribute("CODCOMRESIDENZA")); String comuneResidenza =
		 * Utils.notNull(anag.getAttribute("COMUNERESIDENZA")); String codProvResidenza =
		 * Utils.notNull(anag.getAttribute("CODPROVRESIDENZA")); String provResidenza =
		 * Utils.notNull(anag.getAttribute("PROVRESIDENZA")); String capResidenza =
		 * Utils.notNull(anag.getAttribute("CAPRESIDENZA")); String strIndirizzoDom =
		 * Utils.notNull(anag.getAttribute("STRINDIRIZZODOM")); String strLocalitaDom =
		 * Utils.notNull(anag.getAttribute("STRLOCALITADOM")); String codComDomicilio =
		 * Utils.notNull(anag.getAttribute("CODCOMDOMICILIO")); String comuneDomicilio =
		 * Utils.notNull(anag.getAttribute("COMUNEDOMICILIO")); String codProvDomicilio =
		 * Utils.notNull(anag.getAttribute("CODPROVDOMICILIO")); String provDomicilio =
		 * Utils.notNull(anag.getAttribute("PROVDOMICILIO")); String capDomicilio =
		 * Utils.notNull(anag.getAttribute("CAPDOMICILIO"));
		 */
		String telefono = Utils.notNull(anag.getAttribute("ROW.TELEFONO"));
		// String cellulare = Utils.notNull(anag.getAttribute("CELLULARE"));
		String fax = Utils.notNull(anag.getAttribute("ROW.FAX"));
		String email = Utils.notNull(anag.getAttribute("ROW.EMAIL"));
		/*
		 * String codStatoCivile = Utils.notNull(anag.getAttribute("CODSTATOCIVILE")); String statoCivile =
		 * Utils.notNull(anag.getAttribute("STATOCIVILE"));
		 */

		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		Document dom = null;

		try {

			DocumentBuilder db = dbf.newDocumentBuilder();

			dom = db.newDocument();

		} catch (ParserConfigurationException pce) {
			it.eng.sil.util.TraceWrapper.error(_logger, "Madreperla:GetCareGiverImpl:getCareGiver ", pce);

			throw pce;
		}

		Element rootEle = dom.createElement("tns:getCareGiver_output");
		rootEle.setAttribute("xmlns:tns", "http://www.satanet.it/Madreperla-SIL");
		rootEle.setAttribute("xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");
		rootEle.setAttribute("xsi:schemaLocation", "http://www.satanet.it/Madreperla-SIL getCareGiver_output.xsd");
		dom.appendChild(rootEle);

		Element esito = dom.createElement("esito");
		esito.setAttribute("ok", "True");
		esito.setAttribute("dettaglio", "");
		rootEle.appendChild(esito);

		Element cgEl = dom.createElement("careGiver");
		rootEle.appendChild(cgEl);

		UtilityXml.appendTextChild("codiceFiscale", codiceFiscale, cgEl, dom);
		UtilityXml.appendTextChild("ragioneSociale", ragioneSociale, cgEl, dom);
		UtilityXml.appendNullableTextChild("sesso", sesso, cgEl, dom);
		UtilityXml.appendNullableTextChild("dataNascita", dataNascita, cgEl, dom);

		Element cNasc = dom.createElement("comuneNascita");
		cgEl.appendChild(cNasc);

		UtilityXml.appendTextChild("codice", codComNascita, cNasc, dom);
		UtilityXml.appendTextChild("valore", comuneNascita, cNasc, dom);

		Element pNasc = dom.createElement("provinciaNascita");
		cgEl.appendChild(pNasc);

		UtilityXml.appendTextChild("codice", codProvNascita, pNasc, dom);
		UtilityXml.appendTextChild("valore", provNascita, pNasc, dom);

		Element stNasc = dom.createElement("statoNascita");
		cgEl.appendChild(stNasc);

		UtilityXml.appendTextChild("codice", ""/* codStatoNascita */, stNasc, dom);
		UtilityXml.appendTextChild("valore", ""/* statoNascita */, stNasc, dom);

		UtilityXml.appendTextChild("indirizzo", indirizzo, cgEl, dom);
		UtilityXml.appendTextChild("localita", localita, cgEl, dom);

		Element com = dom.createElement("comune");
		cgEl.appendChild(com);

		UtilityXml.appendTextChild("codice", codComune, com, dom);
		UtilityXml.appendTextChild("valore", comune, com, dom);

		Element prov = dom.createElement("provincia");
		cgEl.appendChild(prov);

		UtilityXml.appendTextChild("codice", codProvincia, prov, dom);
		UtilityXml.appendTextChild("valore", provincia, prov, dom);

		UtilityXml.appendTextChild("cap", cap, cgEl, dom);
		UtilityXml.appendTextChild("telefono", telefono, cgEl, dom);
		UtilityXml.appendTextChild("fax", fax, cgEl, dom);
		UtilityXml.appendTextChild("email", email, cgEl, dom);

		try {

			response = UtilityXml.domToString(dom);

		} catch (TransformerException e) {
			it.eng.sil.util.TraceWrapper.error(_logger, "Madreperla:GetCareGiverImpl:getCareGiver", e);

			throw e;
		}

		return response;
	}

	public String creaMessaggioErrore(String msg) {
		return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
				+ "<tns:getCareGiver_output xmlns:tns=\"http://www.satanet.it/Madreperla-SIL\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://www.satanet.it/Madreperla-SIL getCareGiver_output.xsd \">"
				+ "<esito ok=\"False\" dettaglio=\"" + msg + "\"/>" + "</tns:getCareGiver_output>";
	}
}