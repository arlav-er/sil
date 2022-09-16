/*
 * Created on 14-nov-07
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
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

/**
 * @author loc_esposito
 *
 *         To change the template for this generated type comment go to Window&gt;Preferences&gt;Java&gt;Code
 *         Generation&gt;Code and Comments
 */
public class GetContrattiImpl extends MadreperlaServiceImpl {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(GetContrattiImpl.class.getName());
	static final private String GETCONTRATTIINPUTSCHEMA = "getContratti_input.xsd";

	static final private String GETCONTRATTIOUTPUTSCHEMA = "getContratti_output.xsd";

	public GetContrattiImpl() {
		super(GETCONTRATTIINPUTSCHEMA, GETCONTRATTIOUTPUTSCHEMA, "GetContrattiImpl");
	}

	public void esegui() throws ContrattoNotFoundException, ParserConfigurationException, TransformerException {
		NodeList listaNodi = docInput.getElementsByTagName("anno");
		Node annoNode = listaNodi.item(0).getFirstChild();

		listaNodi = docInput.getElementsByTagName("protocollo");
		Node protNode = listaNodi.item(0).getFirstChild();

		if ((annoNode != null) && (protNode != null)) {
			String anno = annoNode.getNodeValue();
			String protocollo = protNode.getNodeValue();
			String _response = getContratti(anno, protocollo);

			response = _response;
		} else
			response = "";
	}

	private String getContratti(String anno, String protocollo)
			throws ContrattoNotFoundException, ParserConfigurationException, TransformerException {
		String response = "";

		SourceBean contratto = (SourceBean) QueryExecutor.executeQuery("MP_GET_CONTRATTI",
				new Object[] { protocollo, anno }, "SELECT", Values.DB_SIL_DATI);
		if (contratto == null)
			throw new ContrattoNotFoundException("I dati inviati non corrispondono a nessun contratto.");

		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		Document dom = null;

		try {

			DocumentBuilder db = dbf.newDocumentBuilder();

			dom = db.newDocument();

		} catch (ParserConfigurationException pce) {
			it.eng.sil.util.TraceWrapper.error(_logger, "Madreperla:GetContrattiImpl:getContratti ", pce);

			throw pce;
		}

		Element rootEle = dom.createElement("tns:getContratti_output");
		rootEle.setAttribute("xmlns:tns", "http://www.satanet.it/Madreperla-SIL");
		rootEle.setAttribute("xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");
		rootEle.setAttribute("xsi:schemaLocation", "http://www.satanet.it/Madreperla-SIL getContratti_output.xsd");
		dom.appendChild(rootEle);

		Element esito = dom.createElement("esito");
		esito.setAttribute("ok", "True");
		esito.setAttribute("dettaglio", "");
		rootEle.appendChild(esito);

		/*
		 * Element lcEl = dom.createElement("listaContratti"); rootEle.appendChild(lcEl);
		 */

		String codiceFiscaleCareGiver = Utils.notNull(contratto.getAttribute("ROW.CODICEFISCALECAREGIVER"));
		if (codiceFiscaleCareGiver.equals(""))
			throw new ContrattoNotFoundException("I dati inviati non corrispondono a nessun contratto.");

		String codiceFiscaleLavoratore = Utils.notNull(contratto.getAttribute("ROW.CODICEFISCALELAVORATORE"));
		String dataInizio = Utils.notNull(contratto.getAttribute("ROW.DATAINIZIO"));
		String dataFine = Utils.notNull(contratto.getAttribute("ROW.DATAFINE"));
		String dataInterruzione = Utils.notNull(contratto.getAttribute("ROW.DATAINTERRUZIONE"));
		String motivazione = Utils.notNull(contratto.getAttribute("ROW.MOTIVAZIONE"));
		String spesaDichiarata = Utils.notNull(contratto.getAttribute("ROW.SPESADICHIARATA"));
		String oreSettimanali = Utils.notNull(contratto.getAttribute("ROW.ORESETTIMANALI"));
		String codTipo = Utils.notNull(contratto.getAttribute("ROW.CODTIPO"));
		String tipo = Utils.notNull(contratto.getAttribute("ROW.TIPO"));
		String codTipologia = Utils.notNull(contratto.getAttribute("ROW.CODTIPOLOGIA"));
		String tipologia = Utils.notNull(contratto.getAttribute("ROW.TIPOLOGIA"));
		String codDurata = Utils.notNull(contratto.getAttribute("ROW.CODDURATA"));
		String durata = Utils.notNull(contratto.getAttribute("ROW.DURATA"));

		Element contrEl = dom.createElement("contratto");
		rootEle.appendChild(contrEl);

		UtilityXml.appendTextChild("codiceFiscaleCareGiver", codiceFiscaleCareGiver, contrEl, dom);
		UtilityXml.appendTextChild("codiceFiscaleLavoratore", codiceFiscaleLavoratore, contrEl, dom);
		UtilityXml.appendNullableTextChild("dataInizio", dataInizio, contrEl, dom);
		UtilityXml.appendNullableTextChild("dataFine", dataFine, contrEl, dom);
		UtilityXml.appendNullableTextChild("dataInterruzione", dataInterruzione, contrEl, dom);

		UtilityXml.appendTextChild("motivazione", motivazione, contrEl, dom);
		UtilityXml.appendNullableTextChild("spesaDichiarata", spesaDichiarata, contrEl, dom);
		UtilityXml.appendNullableTextChild("oreSettimanali", oreSettimanali, contrEl, dom);

		Element tipoEl = dom.createElement("tipo");
		contrEl.appendChild(tipoEl);

		UtilityXml.appendTextChild("codice", codTipo, tipoEl, dom);
		UtilityXml.appendTextChild("valore", tipo, tipoEl, dom);

		Element tipologiaEl = dom.createElement("tipologia");
		contrEl.appendChild(tipologiaEl);

		UtilityXml.appendTextChild("codice", codTipologia, tipologiaEl, dom);
		UtilityXml.appendTextChild("valore", tipologia, tipologiaEl, dom);

		Element durataEl = dom.createElement("durata");
		contrEl.appendChild(durataEl);

		UtilityXml.appendTextChild("codice", codDurata, durataEl, dom);
		UtilityXml.appendTextChild("valore", durata, durataEl, dom);

		try {

			response = UtilityXml.domToString(dom);

		} catch (TransformerException e) {
			it.eng.sil.util.TraceWrapper.error(_logger, "Madreperla:GetContrattiImpl:getContratti", e);

			throw e;
		}
		return response;
	}

	public String creaMessaggioErrore(String msg) {
		return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
				+ "<tns:getContratti_output xmlns:tns=\"http://www.satanet.it/Madreperla-SIL\" "
				+ "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" "
				+ "xsi:schemaLocation=\"http://www.satanet.it/Madreperla-SIL getContratti_output.xsd \"> "
				+ "<esito ok=\"False\" dettaglio=\"" + msg + "\"/>" + "</tns:getContratti_output>";
	}
}