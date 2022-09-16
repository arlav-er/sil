/*
 * Created on 14-nov-07
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package it.eng.sil.coop.webservices.madreperla.servizi;

import java.util.Iterator;
import java.util.Vector;

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

/**
 * @author loc_esposito
 *
 *         To change the template for this generated type comment go to Window&gt;Preferences&gt;Java&gt;Code
 *         Generation&gt;Code and Comments
 */
public class GetLavoratoreImpl extends MadreperlaServiceImpl {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(GetLavoratoreImpl.class.getName());
	static final private String GETLAVORATOREINPUTSCHEMA = "getLavoratore_input.xsd";

	static final private String GETLAVORATOREOUTPUTSCHEMA = "getLavoratore_output.xsd";

	public GetLavoratoreImpl() {
		super(GETLAVORATOREINPUTSCHEMA, GETLAVORATOREOUTPUTSCHEMA, "GetLavoratoreImpl");
	}

	public void esegui() throws LavoratoreNotFoundException, ParserConfigurationException, TransformerException {
		NodeList listaNodi = docInput.getElementsByTagName("codiceFiscale");
		Node cfNode = listaNodi.item(0).getFirstChild();

		if (cfNode != null) {
			String codiceFiscale = cfNode.getNodeValue();

			String _response = getLavoratore(codiceFiscale);

			response = _response;
		} else
			response = "";
	}

	private String getLavoratore(String codiceFiscale)
			throws LavoratoreNotFoundException, ParserConfigurationException, TransformerException {
		String response = null;

		SourceBean result = (SourceBean) QueryExecutor.executeQuery("MP_ANAGRAFICA_LAVORATORE",
				new Object[] { codiceFiscale }, "SELECT", Values.DB_SIL_DATI);

		// Se result è null,la query non ha restituito nessuna riga, il lavoratore non è presente nell'anagrafica del
		// SIL
		if (result == null)
			throw new LavoratoreNotFoundException("Il codice fiscale non corrisponde a nessun elemento nel db");

		Vector righe = result.getAttributeAsVector("ROW");
		Iterator iter = righe.iterator();

		if (!iter.hasNext())
			throw new LavoratoreNotFoundException("Il codice fiscale non corrisponde a nessun elemento nel db");

		SourceBean anag;
		SourceBean item;
		boolean isDoc = false; // questo flag, all'uscita del ciclo while sotto, indica se tra tutti i documenti del
								// lavoratore c'è la carta di identità, il passaporto o la carta di soggiorno.
		anag = (SourceBean) iter.next();

		// In questo ciclo trovo,se presente, la carta di identità del lavoratore nel caso sia comunitario, oppure,
		// se extracomunitario, preferibilmente la carta di soggiorno o se non presente, il passaporto.
		while (iter.hasNext()) {
			item = (SourceBean) iter.next();

			String tipoDoc = (String) item.getAttribute("CODTIPODOCUMENTO");
			String comunitario = (String) item.getAttribute("COMUNITARIO");

			if (comunitario.equals("S") && tipoDoc.equals("CI")) {
				isDoc = true; // se il lavoratore è comunitario e trovo la carta di identità,
								// setto il flag a true ed esco
				anag = item;
				break;
			}

			if (!comunitario.equals("S") && tipoDoc.equals("PS")) {
				isDoc = true; // se il lavoratore è extracomunitario quando trovo il passaporto,
								// setto il flag a true, ma continuo nella ricerca perché se trovo
								// la carta di soggiorno scelgo quest'ultima
				anag = item;
			}

			if (!comunitario.equals("S") && tipoDoc.equals("CS")) {
				isDoc = true; // se il lavoratore è extracomunitario quando trovo la carta di soggiorno,
								// setto il flag a true ed esco
				anag = item;
				break;
			}
		}

		codiceFiscale = Utils.notNull(anag.getAttribute("CODICEFISCALE"));

		String nome = Utils.notNull(anag.getAttribute("NOME"));
		String cognome = Utils.notNull(anag.getAttribute("COGNOME"));
		String sesso = Utils.notNull(anag.getAttribute("SESSO"));
		String dataNascita = Utils.notNull(anag.getAttribute("DATANASCITA"));
		String codComNascita = Utils.notNull(anag.getAttribute("CODCOMNASCITA"));
		String comuneNascita = Utils.notNull(anag.getAttribute("COMUNENASCITA"));
		String luogoNascita = comuneNascita;
		String codProvNascita = Utils.notNull(anag.getAttribute("CODPROVNASCITA"));
		String provNascita = Utils.notNull(anag.getAttribute("PROVNASCITA"));
		String codCittadinanza = Utils.notNull(anag.getAttribute("CODCITTADINANZA"));
		String cittadinanza = Utils.notNull(anag.getAttribute("CITTADINANZA"));
		String codStatoNascita = Utils.notNull(anag.getAttribute("CODSTATONASCITA"));
		String statoNascita = Utils.notNull(anag.getAttribute("STATONASCITA"));
		String indResidenza = Utils.notNull(anag.getAttribute("INDRESIDENZA"));
		String locResidenza = Utils.notNull(anag.getAttribute("LOCRESIDENZA"));
		String codComResidenza = Utils.notNull(anag.getAttribute("CODCOMRESIDENZA"));
		String comuneResidenza = Utils.notNull(anag.getAttribute("COMUNERESIDENZA"));
		String codProvResidenza = Utils.notNull(anag.getAttribute("CODPROVRESIDENZA"));
		String provResidenza = Utils.notNull(anag.getAttribute("PROVRESIDENZA"));
		String capResidenza = Utils.notNull(anag.getAttribute("CAPRESIDENZA"));
		String strIndirizzoDom = Utils.notNull(anag.getAttribute("STRINDIRIZZODOM"));
		String strLocalitaDom = Utils.notNull(anag.getAttribute("STRLOCALITADOM"));
		String codComDomicilio = Utils.notNull(anag.getAttribute("CODCOMDOMICILIO"));
		String comuneDomicilio = Utils.notNull(anag.getAttribute("COMUNEDOMICILIO"));
		String codProvDomicilio = Utils.notNull(anag.getAttribute("CODPROVDOMICILIO"));
		String provDomicilio = Utils.notNull(anag.getAttribute("PROVDOMICILIO"));
		String capDomicilio = Utils.notNull(anag.getAttribute("CAPDOMICILIO"));
		String telefono = Utils.notNull(anag.getAttribute("TELEFONO"));
		String cellulare = Utils.notNull(anag.getAttribute("CELLULARE"));
		String fax = Utils.notNull(anag.getAttribute("FAX"));
		String email = Utils.notNull(anag.getAttribute("EMAIL"));
		String codStatoCivile = Utils.notNull(anag.getAttribute("CODSTATOCIVILE"));
		String statoCivile = Utils.notNull(anag.getAttribute("STATOCIVILE"));

		String documentoIdentita = "";
		if (isDoc)
			documentoIdentita = Utils.notNull(anag.getAttribute("DOCUMENTOIDENTITA"));

		String numPermSoggiorno = Utils.notNull(anag.getAttribute("NUMPERMSOGGIORNO"));
		String motivoPermSoggiorno = Utils.notNull(anag.getAttribute("MOTIVOPERMSOGGIORNO"));
		// String rilascioPermSoggiorno = Utils.notNull(anag.getAttribute("RILASCIOPERMSOGGIORNO"));
		String scadPermSoggiorno = Utils.notNull(anag.getAttribute("SCADPERMSOGGIORNO"));
		String statoImpiego = Utils.notNull(anag.getAttribute("STATOIMPIEGO"));
		String dataStatoImpiego = Utils.notNull(anag.getAttribute("DATASTATOIMPIEGO"));

		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		Document dom = null;

		try {

			DocumentBuilder db = dbf.newDocumentBuilder();

			dom = db.newDocument();

		} catch (ParserConfigurationException pce) {
			it.eng.sil.util.TraceWrapper.error(_logger, "Madreperla:GetLavoratoreImpl:getLavoratore ", pce);

			throw pce;
		}

		Element rootEle = dom.createElement("tns:getLavoratore_output");
		rootEle.setAttribute("xmlns:tns", "http://www.satanet.it/Madreperla-SIL");
		rootEle.setAttribute("xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");
		rootEle.setAttribute("xsi:schemaLocation", "http://www.satanet.it/Madreperla-SIL getLavoratore_output.xsd");
		dom.appendChild(rootEle);

		Element esito = dom.createElement("esito");
		esito.setAttribute("ok", "True");
		esito.setAttribute("dettaglio", "");
		rootEle.appendChild(esito);

		Element lavEl = dom.createElement("lavoratore");
		rootEle.appendChild(lavEl);

		UtilityXml.appendTextChild("codiceFiscale", codiceFiscale, lavEl, dom);
		UtilityXml.appendTextChild("nome", nome, lavEl, dom);
		UtilityXml.appendTextChild("cognome", cognome, lavEl, dom);
		UtilityXml.appendTextChild("sesso", sesso, lavEl, dom);
		UtilityXml.appendNullableTextChild("dataNascita", dataNascita, lavEl, dom);

		Element cNasc = dom.createElement("comuneNascita");
		lavEl.appendChild(cNasc);

		UtilityXml.appendTextChild("codice", codComNascita, cNasc, dom);
		UtilityXml.appendTextChild("valore", comuneNascita, cNasc, dom);

		Element pNasc = dom.createElement("provinciaNascita");
		lavEl.appendChild(pNasc);

		UtilityXml.appendTextChild("codice", codProvNascita, pNasc, dom);
		UtilityXml.appendTextChild("valore", provNascita, pNasc, dom);

		Element stNasc = dom.createElement("statoNascita");
		lavEl.appendChild(stNasc);

		UtilityXml.appendTextChild("codice", codStatoNascita, stNasc, dom);
		UtilityXml.appendTextChild("valore", statoNascita, stNasc, dom);

		Element citt = dom.createElement("cittadinanza");
		lavEl.appendChild(citt);

		UtilityXml.appendTextChild("codice", codCittadinanza, citt, dom);
		UtilityXml.appendTextChild("valore", cittadinanza, citt, dom);

		UtilityXml.appendTextChild("indirizzo", indResidenza, lavEl, dom);
		UtilityXml.appendTextChild("localita", "", lavEl, dom);

		Element com = dom.createElement("comune");
		lavEl.appendChild(com);

		UtilityXml.appendTextChild("codice", codComResidenza, com, dom);
		UtilityXml.appendTextChild("valore", comuneResidenza, com, dom);

		Element prov = dom.createElement("provincia");
		lavEl.appendChild(prov);

		UtilityXml.appendTextChild("codice", codProvResidenza, prov, dom);
		UtilityXml.appendTextChild("valore", provResidenza, prov, dom);

		UtilityXml.appendTextChild("cap", capResidenza, lavEl, dom);
		UtilityXml.appendTextChild("indirizzoDomicilio", strIndirizzoDom, lavEl, dom);
		UtilityXml.appendTextChild("localitaDomicilio", strLocalitaDom, lavEl, dom);

		Element comDom = dom.createElement("comuneDomicilio");
		lavEl.appendChild(comDom);

		UtilityXml.appendTextChild("codice", codComDomicilio, comDom, dom);
		UtilityXml.appendTextChild("valore", comuneDomicilio, comDom, dom);

		Element provDom = dom.createElement("provDomicilio");
		lavEl.appendChild(provDom);

		UtilityXml.appendTextChild("codice", codProvDomicilio, provDom, dom);
		UtilityXml.appendTextChild("valore", provDomicilio, provDom, dom);

		UtilityXml.appendTextChild("capDomicilio", capDomicilio, lavEl, dom);
		UtilityXml.appendTextChild("telefono", telefono, lavEl, dom);
		UtilityXml.appendTextChild("cell", cellulare, lavEl, dom);

		UtilityXml.appendTextChild("fax", fax, lavEl, dom);
		UtilityXml.appendTextChild("email", email, lavEl, dom);
		UtilityXml.appendTextChild("documentoIdentita", documentoIdentita, lavEl, dom);

		Element stCiv = dom.createElement("statoCivile");
		lavEl.appendChild(stCiv);

		UtilityXml.appendTextChild("codice", codStatoCivile, stCiv, dom);
		UtilityXml.appendTextChild("valore", statoCivile, stCiv, dom);

		// UtilityXml.appendTextChild("iscrittoAdc","",lavEl,dom);
		// UtilityXml.appendTextChild("numLibrettoLavoro","",lavEl,dom);
		UtilityXml.appendTextChild("numPermSoggiorno", numPermSoggiorno, lavEl, dom);

		UtilityXml.appendTextChild("motivoPermSoggiorno", motivoPermSoggiorno, lavEl, dom);
		UtilityXml.appendNullableTextChild("scadPermSoggiorno", scadPermSoggiorno, lavEl, dom);

		UtilityXml.appendNullableTextChild("statoImpiego", statoImpiego, lavEl, dom);
		UtilityXml.appendNullableTextChild("dataStatoImpiego", dataStatoImpiego, lavEl, dom);

		try {
			response = UtilityXml.domToString(dom);
		} catch (TransformerException e) {
			it.eng.sil.util.TraceWrapper.error(_logger, "Madreperla:GetLavoratoreImpl:getLavoratore", e);

			throw e;
		}

		return response;
	}

	public String creaMessaggioErrore(String msg) {
		return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
				+ "<tns:getLavoratore_output xmlns:tns=\"http://www.satanet.it/Madreperla-SIL\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://www.satanet.it/Madreperla-SIL getLavoratore_output.xsd\">"
				+ "<esito ok=\"False\" dettaglio=\"" + msg + "\"/>" + "</tns:getLavoratore_output>";
	}
}