package it.eng.sil.coop.webservices.tabelleDecodifica;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Set;
import java.util.Vector;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.base.SourceBeanAttribute;
import com.engiweb.framework.configuration.ConfigSingleton;
import com.engiweb.framework.dbaccess.DataConnectionManager;
import com.engiweb.framework.dbaccess.Utils;
import com.engiweb.framework.dbaccess.sql.DataConnection;
import com.engiweb.framework.dbaccess.sql.command.std.StoredProcedureCommand;
import com.engiweb.framework.dbaccess.sql.result.DataResult;
import com.engiweb.framework.error.EMFErrorSeverity;
import com.engiweb.framework.error.EMFInternalError;
import com.engiweb.framework.error.EMFUserError;
import com.engiweb.framework.util.QueryExecutor;

import it.eng.afExt.utils.MessageCodes;
import it.eng.sil.Values;
import it.eng.sil.coop.webservices.madreperla.servizi.UtilityXml;
import it.eng.sil.util.DBAccess;
import it.eng.sil.util.xml.XMLValidator;

public class GetTabellaDecodifica {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(GetTabellaDecodifica.class.getName());

	SimpleDateFormat format = new SimpleDateFormat("dd/mm/yyyy");

	File inputSchemaFile = new File(ConfigSingleton.getRootPath() + File.separator + "WEB-INF" + File.separator + "xsd"
			+ File.separator + "tabellaDecodifica" + File.separator + "inputXML_GetTabelleDecodifica.xsd");

	File outputSchemaFile = new File(ConfigSingleton.getRootPath() + File.separator + "WEB-INF" + File.separator + "xsd"
			+ File.separator + "tabellaDecodifica" + File.separator + "outputXML_GetTabelleDecodifica.xsd");

	private static HashMap<String, String> ALLOWED_TABLE_NAME_TO_XML_ELEMENT;

	static {

		ALLOWED_TABLE_NAME_TO_XML_ELEMENT = new HashMap<String, String>();
		ALLOWED_TABLE_NAME_TO_XML_ELEMENT.put("DE_TIPO_CONTRATTO", "DeTipoContratto");
		ALLOWED_TABLE_NAME_TO_XML_ELEMENT.put("DE_CONTRATTO_COLLETTIVO", "DeCcnl");
		ALLOWED_TABLE_NAME_TO_XML_ELEMENT.put("DE_MANSIONE", "DeMansione");
		ALLOWED_TABLE_NAME_TO_XML_ELEMENT.put("DE_ORARIO", "DeOrario");
		ALLOWED_TABLE_NAME_TO_XML_ELEMENT.put("DE_MV_CESSAZIONE", "DeMvCessazione");
		ALLOWED_TABLE_NAME_TO_XML_ELEMENT.put("DE_QUESTURA", "DeQuestura");
		ALLOWED_TABLE_NAME_TO_XML_ELEMENT.put("DE_STATUS_STRANIERO", "DeStatusStraniero");
		ALLOWED_TABLE_NAME_TO_XML_ELEMENT.put("DE_EX_MOTIVO_RIL", "DeExMotivoRil");
		ALLOWED_TABLE_NAME_TO_XML_ELEMENT.put("DE_CITTADINANZA", "DeCittadinanza");
		ALLOWED_TABLE_NAME_TO_XML_ELEMENT.put("DE_ATTIVITA", "DeAttivita");

	}

	/**
	 * Restiruisce l'XML con i dati della tabella di decodifica passata come parametro
	 * 
	 * @param XML
	 *            contente nomeTabella
	 * @return
	 */
	public String getTabellaDecodifica(String inputXML) {

		/**
		 * 1) controllare accesso l'utente 2) inizio operazione recupero tabelle di decodifica
		 * 
		 */

		String outputXML = "";
		DataConnection dataConnection = null;
		StoredProcedureCommand command = null;
		DataResult dataResult = null;
		SourceBean queryResultSourceBean = null;

		try {

			///////////////////////////
			// VALIDAZIONE INPUT XML //
			///////////////////////////

			boolean inputXmlIsValid = isXmlValid(inputXML, inputSchemaFile);
			if (!inputXmlIsValid) {
				_logger.debug("input xml non valido");
				return createSimpleXMLResponse("99", "Errore generico");
			}

			/////////////////////////////
			// LETTURA VALORI DI INPUT //
			/////////////////////////////

			Document doc = XMLValidator.parseXmlFile(inputXML);
			XPath xpath = XPathFactory.newInstance().newXPath();
			XPathExpression pathExpression = null;
			String nomeTabella = "";
			// String idProvincia = "";

			pathExpression = xpath.compile("/TabelleDecodifica/NomeTabella");
			nomeTabella = pathExpression.evaluate(doc, XPathConstants.STRING).toString();

			// pathExpression = xpath.compile("/TabelleDecodifica/IdProvincia");
			// idProvincia = pathExpression.evaluate(doc, XPathConstants.STRING).toString();

			////////////////////////
			// CHECK NOME TABELLA //
			////////////////////////

			boolean isInputTableNameValid = isInputTableNameValid(nomeTabella);
			if (!isInputTableNameValid) {
				_logger.debug("il file xml ricevuto non è valido rispetto all'input xsd");
				return createSimpleXMLResponse("03", "Nome tabella non valido");
			}

			///////////////////////////////
			// LETTURA DATI DI INTERESSE //
			// da questo punto in poi è //
			// necessario rilasciare la //
			// connessione prima di fare //
			// il return del risultato.. //
			///////////////////////////////

			DataConnectionManager dataConnectionManager = DataConnectionManager.getInstance();
			dataConnection = dataConnectionManager.getConnection(Values.DB_SIL_DATI);
			// dataConnection.initTransaction();

			queryResultSourceBean = executeSelect("GET_" + nomeTabella.toUpperCase() + "_WS", null, dataConnection);

			//////////////////////////
			// CREAZIONE OUTPUT XML //
			//////////////////////////

			outputXML = createXMLResponse("00", "OK", queryResultSourceBean, nomeTabella);

			////////////////////////////
			// VALIDAZIONE OUTPUT XML //
			////////////////////////////

			boolean outputXmlIsValid = isXmlValid(outputXML, outputSchemaFile);
			if (!outputXmlIsValid) {
				_logger.debug("il file xml generato non è valido rispetto all'output xsd");
				outputXML = createSimpleXMLResponse("99", "Errore generico");
			}

		} catch (Exception e) {

			_logger.debug("Errore durante la generazione della risposta: ", e);

		} finally {

			// rilascia le risorse utilizzate
			// per la connessione, se esistenti
			Utils.releaseResources(dataConnection, command, dataResult);

		}

		return outputXML;

	}

	private final String createXMLResponse(String codice, String descrizione, SourceBean queryResultSourceBean,
			String tableName) {

		_logger.debug("buildXml() - start - genera xml ");

		String outputXML = "";

		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder parser;

		try {

			parser = factory.newDocumentBuilder();
			Document doc = parser.newDocument(); // blank DOM document

			Element rispostaElement = doc.createElement("risposta"); // root
			rispostaElement.setAttribute("xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");
			rispostaElement.setAttribute("schemaversion", "1");

			doc.appendChild(rispostaElement);
			Element esito = doc.createElement("Esito");
			rispostaElement.appendChild(esito);

			UtilityXml.appendTextChild("codice", codice, esito, doc);
			UtilityXml.appendTextChild("descrizione", descrizione, esito, doc);

			Element tabellaElement = doc.createElement(ALLOWED_TABLE_NAME_TO_XML_ELEMENT.get(tableName));
			rispostaElement.appendChild(tabellaElement);

			Vector rows = queryResultSourceBean.getContainedAttributes();

			for (Object row : rows) {

				// scorre le righe della tabella

				SourceBean rowSourceBean = (SourceBean) ((SourceBeanAttribute) row).getValue();
				Vector rowAttributes = rowSourceBean.getContainedAttributes();

				Element elementElement = doc.createElement("element");
				tabellaElement.appendChild(elementElement);

				for (Object rowAttribute : rowAttributes) {

					// scorre le colonne della riga corrente della tabella

					String elementNameString = ((SourceBeanAttribute) rowAttribute).getKey().toString();
					String elementValueString = ((SourceBeanAttribute) rowAttribute).getValue().toString();
					if (elementValueString.equalsIgnoreCase(" ")) {
						elementValueString = "";
					}
					UtilityXml.appendTextChild(elementNameString.toLowerCase(), elementValueString, elementElement,
							doc);

				}

			}

			outputXML = UtilityXml.domToString(doc);
			_logger.debug("buildXml() - end");

		} catch (ParserConfigurationException parserConfigurationException) {

			_logger.error("Errore nel parsing dell'xml da inviare", parserConfigurationException);

		} catch (TransformerException transformerException) {

			_logger.error("Errore nella trasformazione del xml da inviare", transformerException);

		}

		return outputXML;

	}

	/**
	 * Esegue una select su db sfruttando la connessione aperta e restituisce il risultato.
	 * 
	 * @param query_name
	 * @param inputParameters
	 * @param dc
	 * @return
	 * @throws EMFUserError
	 */
	private SourceBean executeSelect(String query_name, Object[] inputParameters, DataConnection dc)
			throws EMFUserError {

		SourceBean ret = null;

		if (dc != null) {
			DBAccess dbAcc = new DBAccess();
			try {
				ret = dbAcc.selectToSourceBean(query_name, inputParameters, dc);
			} catch (EMFInternalError e) {
				_logger.error("Errore nell'esecuzione della query: " + query_name);
				throw new EMFUserError(EMFErrorSeverity.ERROR, MessageCodes.ClicLavoro.CODE_ERR_INTERNO, new Vector());
			}
		} else {
			ret = (SourceBean) QueryExecutor.executeQuery(query_name, inputParameters, "SELECT", Values.DB_SIL_DATI);
		}

		return ret;

	}

	/**
	 * 
	 * Controlla se il nome della tabella ricercata dall'utente è presente tra quelle esposte dal presente servizio web
	 * 
	 * @param tableName
	 * @return
	 */
	private boolean isInputTableNameValid(String tableName) {

		Set<String> allowedTableNames = ALLOWED_TABLE_NAME_TO_XML_ELEMENT.keySet();
		for (String allowedTableName : allowedTableNames) {
			if (allowedTableName.equalsIgnoreCase(tableName)) {
				return true;
			}
		}

		return false;

	}

	/**
	 * Crea una risposta in formato xml contenente solamente un codice e una descrizione.
	 * 
	 * Es: "99", "Errore generico"
	 * 
	 * @param codice
	 * @param descrizione
	 * @return
	 * @throws TransformerException
	 * @throws ParserConfigurationException
	 */
	private String createSimpleXMLResponse(String codice, String descrizione)
			throws TransformerException, ParserConfigurationException {

		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder parser;
		String returnString = "";
		parser = factory.newDocumentBuilder();
		Document doc = parser.newDocument();
		Element rispostaElement = doc.createElement("risposta");
		rispostaElement.setAttribute("xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");
		rispostaElement.setAttribute("schemaversion", "1");
		doc.appendChild(rispostaElement);

		Element esito = doc.createElement("Esito");
		rispostaElement.appendChild(esito);

		UtilityXml.appendTextChild("codice", codice, esito, doc);
		UtilityXml.appendTextChild("descrizione", descrizione, esito, doc);

		returnString = UtilityXml.domToString(doc);

		return returnString;

	}

	/**
	 * Controlla se il documento XML fornito in input è valido rispetto al xsd
	 * 
	 * @param inputXML
	 * @return
	 */
	private boolean isXmlValid(String inputXML, File schemaFile) {

		String validityErrors = XMLValidator.getValidityErrors(inputXML, schemaFile);
		if (validityErrors != null) {
			_logger.error("XML di input: " + inputXML);
			_logger.error("Errore nella validazione del file xml: " + validityErrors);
			return false;
		}
		return true;

	}
}
