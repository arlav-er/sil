package it.eng.sil.coop.webservices.apapi;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.configuration.ConfigSingleton;
import com.engiweb.framework.dbaccess.DataConnectionManager;
import com.engiweb.framework.dbaccess.SQLStatements;
import com.engiweb.framework.dbaccess.Utils;
import com.engiweb.framework.dbaccess.sql.DataConnection;
import com.engiweb.framework.dbaccess.sql.DataField;
import com.engiweb.framework.dbaccess.sql.command.std.StoredProcedureCommand;
import com.engiweb.framework.dbaccess.sql.result.DataResult;
import com.engiweb.framework.dbaccess.sql.result.PunctualDataResult;
import com.engiweb.framework.util.QueryExecutor;

import it.eng.afExt.utils.StringUtils;
import it.eng.sil.Values;
import it.eng.sil.coop.webservices.madreperla.servizi.UtilityXml;
import it.eng.sil.util.xml.FieldFormatException;
import it.eng.sil.util.xml.MandatoryFieldException;
import it.eng.sil.util.xml.XMLValidator;

public class UltimoMovimentoAPAPI {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(UltimoMovimentoAPAPI.class.getName());

	private static final String DELIMETER = "\\|";
	private static final String DELIMETERCatena = "-";
	private static final Pattern codiceFiscaleCheck = Pattern
			.compile("([A-Z]{6}[0-9LMNPQRSTUV]{2}[A-Z][0-9LMNPQRSTUV]{2}[A-Z][0-9LMNPQRSTUV]{3}[A-Z])|([0-9]{11})");

	File inputXML_getUltimoMovimentoAPAPI_SchemaFile = new File(
			ConfigSingleton.getRootPath() + File.separator + "WEB-INF" + File.separator + "xsd" + File.separator
					+ "apapi" + File.separator + "inputXML_getUltimoMovimentoAPAPI.xsd");

	File outputXML_getUltimoMovimentoAPAPI_SchemaFile = new File(
			ConfigSingleton.getRootPath() + File.separator + "WEB-INF" + File.separator + "xsd" + File.separator
					+ "apapi" + File.separator + "outputXML_getUltimoMovimentoAPAPI.xsd");

	/**
	 * Restiruisce l'XML con i dati dell'ultima catena di movimenti
	 * 
	 * @param XML
	 *            codice fiscale e codice provincia
	 * @return xml con i dati lavoratore, azienda, stipulabilità DID, movimenti del lavoratore
	 */
	public String getUltimoMovimentoAPAPI(String inputXML) {

		_logger.info("servizio getUltimoMovimento chiamato");

		String outputXml = "";

		String codiceFiscale = "";

		try {

			///////////////////////////
			// VALIDAZIONE INPUT XML //
			///////////////////////////

			Node codiceFiscaleNode = null;

			DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
			documentBuilderFactory.setNamespaceAware(false);
			DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
			Document document = documentBuilder
					.parse(new InputSource(new ByteArrayInputStream(inputXML.getBytes("utf-8"))));
			Element root = document.getDocumentElement();

			// codice fiscale e data di interrogazione

			XPath xpath = XPathFactory.newInstance().newXPath();
			codiceFiscaleNode = (Node) xpath.evaluate("/UltimoMovimento/CodiceFiscale", root, XPathConstants.NODE);

			codiceFiscale = codiceFiscaleNode.getTextContent();

			// controlla la presenza di errori nei dati di input

			outputXml = getErroriDatiInput(codiceFiscale, inputXML);

			if (!"".equalsIgnoreCase(outputXml)) {
				// è già presente una risposta,
				// riscontrati errore in fase di validazione
				return outputXml;
			}

			boolean inputXmlIsValid = XmlUtils.isXmlValid(inputXML, inputXML_getUltimoMovimentoAPAPI_SchemaFile);
			if (!inputXmlIsValid) {
				_logger.debug("input xml non valido");
				outputXml = XmlUtils.createXMLRisposta("99", "Errore generico");
				return outputXml;
			}

			///////////////////////
			// GESTIONE CHIAMATA //
			///////////////////////

			SourceBean ultimoMovimento = getUltimoMovimento(codiceFiscale);

			// se non ci sono movimenti

			if (ultimoMovimento == null) {
				outputXml = XmlUtils.createXMLRisposta("06",
						"Il lavoratore non ha nessuna posizione lavorativa registrata.");
				return outputXml;
			}

			outputXml = createOutputXml(ultimoMovimento);

			////////////////////////
			// VALIDAZIONE OUTPUT //
			////////////////////////

			boolean outputXmlIsValid = XmlUtils.isXmlValid(outputXml, outputXML_getUltimoMovimentoAPAPI_SchemaFile);
			if (!outputXmlIsValid) {
				_logger.debug("output xml non valido");
				outputXml = XmlUtils.createXMLRisposta("99", "Errore generico");
			}

		} catch (Exception e) {

			try {
				outputXml = XmlUtils.createXMLRisposta("99", "Errore generico");
			} catch (Exception eg) {
				_logger.debug("Errore in fase di generazione di una risposta con errore ", eg);
			}

			_logger.debug("Errore ", e);

		}

		// se non ci sono errore e lo xml è vuoto.

		if ("".equals(outputXml)) {
			try {
				outputXml = XmlUtils.createXMLRisposta("99", "Errore Generico");
			} catch (Exception e) {
				_logger.debug("Errore Mov ", e);
			}
		}

		return outputXml;

	}

	private SourceBean getUltimoMovimento(String codiceFiscale) {

		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
		SourceBean ultimoMovimento = null;
		SourceBean primoMovimento = null;
		DataConnection dataConnection = null;
		StoredProcedureCommand command = null;
		DataResult dataResult = null;

		try {

			DataConnectionManager dataConnectionManager = DataConnectionManager.getInstance();
			dataConnection = dataConnectionManager.getConnection(Values.DB_SIL_DATI);

			// Dati lavoratore

			Object[] inputParameters = null;

			// Rapporto lavorativo

			// --------------------------------

			String result = "";
			String sqlStr = "";

			sqlStr = SQLStatements.getStatementInit("GET_ULT_CATENA_MOV");
			command = (StoredProcedureCommand) dataConnection.createStoredProcedureCommand(sqlStr);
			List<DataField> inputParametersA = new ArrayList<DataField>(2);
			;

			int paramIndex = 0;

			// 1.Parametro di Ritorno
			inputParametersA.add(dataConnection.createDataField("codiceRit", java.sql.Types.VARCHAR, null));
			command.setAsOutputParameters(paramIndex++);

			// 2.codicefiscale
			inputParametersA
					.add(dataConnection.createDataField("codiceFiscale", java.sql.Types.VARCHAR, codiceFiscale));
			command.setAsInputParameters(paramIndex++);
			DataResult dr = command.execute(inputParametersA);
			PunctualDataResult pdr = (PunctualDataResult) dr.getDataObject();
			DataField df = pdr.getPunctualDatafield();
			result = df.getStringValue();
			result = result.trim();

			// --------------------------------

			// n movimenti generale
			String[] tempList = result.split(DELIMETER);

			// Il primo campo di tempList ci dici quanti catene aperte ci sono:
			// 0: = nessuno aperto,
			// 1..N: N catene aperte

			String strTipoMovimento = "";

			String[] tempListNumCatenaPrgmov = null;

			String prgMov = null;
			String previousCodCatena = null;
			String currentCodCatena = null;
			SourceBean movimentoCorrente = null;
			SourceBean ultimoMovimentoCatena = null;
			SourceBean primoMovimentoCatena = null;
			List<List<SourceBean>> rapportiTI = new ArrayList<List<SourceBean>>();
			List<List<SourceBean>> rapportiTD = new ArrayList<List<SourceBean>>();
			List<SourceBean> currentCatena = new ArrayList<SourceBean>();
			String strCodMonoTempo = "";

			// si parte 1 invece che da 0
			// il primo indica numero di catene aperte
			// i successivi sono le catene, contenenti a loro volta
			// dei movimenti deparati da un delimitatore

			for (int i = 1; i < tempList.length; i++) {

				tempListNumCatenaPrgmov = tempList[i].split(DELIMETERCatena);
				currentCodCatena = tempListNumCatenaPrgmov[0];
				prgMov = tempListNumCatenaPrgmov[1];

				// Recupero il movimento corrente
				inputParameters = new Object[1];
				inputParameters[0] = prgMov;
				movimentoCorrente = DBUtils.executeSelect("GET_MOV_WS", inputParameters, dataConnection);

				if (currentCodCatena == null || previousCodCatena == null
						|| !previousCodCatena.equals(currentCodCatena)) {

					// nuova catena
					currentCatena = new ArrayList<SourceBean>();
					// si determina il tipo di movimento
					strCodMonoTempo = StringUtils.getAttributeStrNotNull(movimentoCorrente, "row.codmonotempo");
					if ("D".equalsIgnoreCase(strCodMonoTempo)) {
						rapportiTD.add(currentCatena);
					} else if ("I".equalsIgnoreCase(strCodMonoTempo)) {
						rapportiTI.add(currentCatena);
					}

				}

				// i movimenti vengono aggiunti dal più recente al più remoto
				currentCatena.add(movimentoCorrente);

				previousCodCatena = currentCodCatena;

			}

			// System.out.println("rapportiTD.size: " + rapportiTD.size());
			// for (int i=0;i<rapportiTD.size();i++) {
			// System.out.println("TD catena [" + i + "].size: " + rapportiTD.get(i).size());
			// }

			// System.out.println("rapportiTI.size: " + rapportiTI.size());
			// for (int i=0;i<rapportiTI.size();i++) {
			// System.out.println("TI catena [" + i + "].size: " + rapportiTI.get(i).size());
			// }

			//

			Date maxDataInizioRapporto = null;
			Date maxDataFineRapporto = null;
			Date dataInizioRapporto = null;
			Date dataFineRapporto = null;
			String dataInizioRapportoString = null;
			String dataFineRapportoString = null;
			Date dataInizioRapportoUltimoMovimento = null;

			// nel caso in cui io abbia più catene TI e una di esse
			// ha una cessazione alla data di oggi allora si prende
			// l'ultimo movimento di un'altra catena anche se la data
			// di oggi sarebbe; se ci sono due TI che finiscono oggi
			// si prende quello dell'ultima catena TI ritornata dalla
			// procedura di giovanni
			boolean isCessazioneUltimoMovimentoCatena = false;
			boolean isCessazioneUltimoMovimento = false;
			boolean isNuovoUltimoMovimento = false;

			// ricerca dell'ultimo movimento tra i movimenti a TEMPO INDETERMINATO

			for (int i = 0; i < rapportiTI.size(); i++) {

				isNuovoUltimoMovimento = false;

				// il primo movimento di ogni catena è l'ultimo
				// e contiene sempre la data di inizio (ipotesi)

				primoMovimentoCatena = rapportiTI.get(i).get(rapportiTI.get(i).size() - 1);
				ultimoMovimentoCatena = rapportiTI.get(i).get(0);

				dataInizioRapportoString = StringUtils.getAttributeStrNotNull(primoMovimentoCatena, "row.datiniziomov");
				dataInizioRapporto = simpleDateFormat.parse(dataInizioRapportoString);

				// controlla se l'ultimo movimento inserito è una cessazione
				strTipoMovimento = StringUtils.getAttributeStrNotNull(ultimoMovimentoCatena, "row.CODTIPOMOV");
				if ("CES".equals(strTipoMovimento)) {
					isCessazioneUltimoMovimentoCatena = true;
				} else {
					isCessazioneUltimoMovimentoCatena = false;
				}

				// logica di identificazione dell'ultimo movimento TI

				if (isCessazioneUltimoMovimento) {

					// se l'ultimo movimento TI registrato
					// era una cessazione (sicuramente alla data di oggi)
					// allora prendiamo un'altro ultimo movimento da un'altra catena TI
					isNuovoUltimoMovimento = true;

				} else {

					// altrimenti (eventualmente) si prende la catena corrente
					if (ultimoMovimento == null || maxDataInizioRapporto == null
							|| dataInizioRapporto.after(maxDataInizioRapporto)) {

						isNuovoUltimoMovimento = true;
					}
				}

				if (isNuovoUltimoMovimento) {
					primoMovimento = primoMovimentoCatena;
					ultimoMovimento = ultimoMovimentoCatena;
					maxDataInizioRapporto = dataInizioRapporto;
					isCessazioneUltimoMovimento = isCessazioneUltimoMovimentoCatena;
				}

			}

			// eventuale ricerca dell'ultimo movimento tra i movimenti a TEMPO DETERMINATO

			if (ultimoMovimento == null) {

				for (int i = 0; i < rapportiTD.size(); i++) {

					// il primo movimento di ogni catena è l'ultimo
					// e contiene sempre la data di inizio (ipotesi)

					primoMovimentoCatena = rapportiTD.get(i).get(rapportiTD.get(i).size() - 1);
					ultimoMovimentoCatena = rapportiTD.get(i).get(0);

					strCodMonoTempo = StringUtils.getAttributeStrNotNull(ultimoMovimentoCatena, "row.CODMONOTEMPO");
					strTipoMovimento = StringUtils.getAttributeStrNotNull(ultimoMovimentoCatena, "row.CODTIPOMOV");
					if ("CES".equals(strTipoMovimento)) {
						dataFineRapportoString = StringUtils.getAttributeStrNotNull(ultimoMovimentoCatena,
								"row.DATINIZIOMOV");
					} else if ("PRO".equals(strTipoMovimento) && "D".equals(strCodMonoTempo)) {
						dataFineRapportoString = StringUtils.getAttributeStrNotNull(ultimoMovimentoCatena,
								"row.datfinemoveffettiva");
					} else if ("AVV".equals(strTipoMovimento)) {
						dataFineRapportoString = StringUtils.getAttributeStrNotNull(ultimoMovimentoCatena,
								"row.datfinemoveffettiva");
					} else {
						dataFineRapportoString = StringUtils.getAttributeStrNotNull(ultimoMovimentoCatena,
								"row.datfinemoveffettiva");
					}

					dataInizioRapportoString = StringUtils.getAttributeStrNotNull(primoMovimentoCatena,
							"row.datiniziomov");
					dataInizioRapporto = simpleDateFormat.parse(dataInizioRapportoString);

					dataFineRapporto = simpleDateFormat.parse(dataFineRapportoString);
					if (maxDataFineRapporto == null || dataFineRapporto.after(maxDataFineRapporto)
							|| (dataFineRapporto.equals(maxDataFineRapporto)
									&& dataInizioRapporto.after(dataInizioRapportoUltimoMovimento))) {
						primoMovimento = primoMovimentoCatena;
						ultimoMovimento = ultimoMovimentoCatena;
						maxDataFineRapporto = dataFineRapporto;
						dataInizioRapportoUltimoMovimento = dataInizioRapporto;
					}

				}

			}

			// si integrano le informazioni dell'eventuale movimento con quelle relative a:
			// - codice fiscale lavoratore
			// - codmansionedot
			// - data inizio avviamento
			if (ultimoMovimento != null) {

				ultimoMovimento.setAttribute("strCodiceFiscaleLavoratore", codiceFiscale);

				String codMansioneDot = null;
				String codMansione = StringUtils.getAttributeStrNotNull(ultimoMovimento, "row.codmansione");
				inputParameters = new Object[1];
				inputParameters[0] = codMansione;
				SourceBean mansioneSourceBean = DBUtils.executeSelect("GET_CODMANSIONEDOT_WS_APAPI", inputParameters,
						dataConnection);
				codMansioneDot = StringUtils.getAttributeStrNotNull(mansioneSourceBean, "row.codmansionedot");
				ultimoMovimento.setAttribute("codMansioneDot", codMansioneDot);

				ultimoMovimento.setAttribute("dataInizioAvviamento",
						StringUtils.getAttributeStrNotNull(primoMovimento, "row.DATINIZIOAVV"));

			}

		} catch (Exception e) {

			_logger.debug("Errore ", e);
			ultimoMovimento = null;

		} finally {
			Utils.releaseResources(dataConnection, command, dataResult);
		}

		return ultimoMovimento;

	}

	private String createOutputXml(SourceBean ultimoMovimento) {

		String outputXml = "";

		try {

			_logger.debug("buildXml() - begin");

			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder documentBuilder = factory.newDocumentBuilder();
			Document doc = documentBuilder.newDocument();

			Element rootElement = doc.createElement("Risposta");
			// rootElement.setAttribute("schemaVersion", "1");
			rootElement.setAttribute("xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");
			doc.appendChild(rootElement);

			// Esito
			Element esito = doc.createElement("Esito");
			rootElement.appendChild(esito);
			UtilityXml.appendTextChild("codice", "00", esito, doc);
			UtilityXml.appendTextChild("descrizione", "Nessun Errore", esito, doc);

			// DatiUltimoMovimentoAPAPI

			String codiceFiscale = StringUtils.getAttributeStrNotNull(ultimoMovimento, "strCodiceFiscaleLavoratore");
			String dataInizioAvv = StringUtils.getAttributeStrNotNull(ultimoMovimento, "ROW.datinizioavv");

			if (dataInizioAvv == null || "".equalsIgnoreCase(dataInizioAvv)) {
				dataInizioAvv = StringUtils.getAttributeStrNotNull(ultimoMovimento, "dataInizioAvviamento");
			}

			String dataFine = null;
			String strCodMonoTempo = StringUtils.getAttributeStrNotNull(ultimoMovimento, "row.CODMONOTEMPO");
			String strTipoMovimento = StringUtils.getAttributeStrNotNull(ultimoMovimento, "row.CODTIPOMOV");
			if ("CES".equals(strTipoMovimento)) {
				dataFine = StringUtils.getAttributeStrNotNull(ultimoMovimento, "row.DATINIZIOMOV");
			} else if ("PRO".equals(strTipoMovimento) && "D".equals(strCodMonoTempo)) {
				dataFine = StringUtils.getAttributeStrNotNull(ultimoMovimento, "row.datfinemoveffettiva");
			} else if ("AVV".equals(strTipoMovimento)) {
				dataFine = StringUtils.getAttributeStrNotNull(ultimoMovimento, "row.datfinemoveffettiva");
			} else {
				dataFine = StringUtils.getAttributeStrNotNull(ultimoMovimento, "row.datfinemoveffettiva");
			}

			String motivoCessazione = StringUtils.getAttributeStrNotNull(ultimoMovimento, "ROW.codmvcessazione");
			String codTipoContratto = StringUtils.getAttributeStrNotNull(ultimoMovimento, "ROW.codtipocontratto");
			String codQualificaProfessionale = StringUtils.getAttributeStrNotNull(ultimoMovimento, "codMansioneDot");
			String codCCNL = StringUtils.getAttributeStrNotNull(ultimoMovimento, "ROW.codccnl");
			String codOrario = StringUtils.getAttributeStrNotNull(ultimoMovimento, "ROW.codorario");
			String numOrePartTime = StringUtils.getAttributeStrNotNull(ultimoMovimento, "ROW.numoresett");

			if (codOrario == null || "".equalsIgnoreCase(codOrario)) {
				codOrario = "N"; // non definito
			}

			codTipoContratto = StringUtils.getStringValueNotNull(codTipoContratto);
			codQualificaProfessionale = StringUtils.getStringValueNotNull(codQualificaProfessionale);
			codCCNL = StringUtils.getStringValueNotNull(codCCNL);
			codOrario = StringUtils.getStringValueNotNull(codOrario);
			numOrePartTime = StringUtils.getStringValueNotNull(numOrePartTime);

			Element datiUltimoMovimentoAPAPI = doc.createElement("datiUltimoMovimentoAPAPI");
			UtilityXml.appendTextChild("CodiceFiscale", codiceFiscale, datiUltimoMovimentoAPAPI, doc);
			UtilityXml.appendTextChild("DataInizio", dataInizioAvv, datiUltimoMovimentoAPAPI, doc);
			UtilityXml.appendNullableTextChild("DataFine", dataFine, datiUltimoMovimentoAPAPI, doc);
			UtilityXml.appendNullableTextChild("MotivoCessazione", motivoCessazione, datiUltimoMovimentoAPAPI, doc);
			UtilityXml.appendTextChild("CodTipologiaContrattuale", codTipoContratto, datiUltimoMovimentoAPAPI, doc);
			UtilityXml.appendTextChild("CodQualificaProfessionale", codQualificaProfessionale, datiUltimoMovimentoAPAPI,
					doc);
			UtilityXml.appendNullableTextChild("CodCcnl", codCCNL, datiUltimoMovimentoAPAPI, doc);

			UtilityXml.appendTextChild("CodOrario", codOrario, datiUltimoMovimentoAPAPI, doc);
			UtilityXml.appendNullableTextChild("NumOrePartTime", numOrePartTime, datiUltimoMovimentoAPAPI, doc);

			rootElement.appendChild(datiUltimoMovimentoAPAPI);

			try {
				outputXml = UtilityXml.domToString(doc);
			} catch (TransformerException e) {
				try {
					outputXml = XmlUtils.createXMLRisposta("99", "Errore generico");
				} catch (Exception ex) {
					_logger.error("Errore nella creazione del xml da inviare in caso di errore", ex);
					outputXml = "";
				}
				_logger.error("Errore nella trasformazione del xml da inviare", e);
			}

			_logger.debug("buildXml() - end");

		} catch (ParserConfigurationException e) {
			_logger.error("Errore nel parsing dell'xml da inviare", e);
		}

		return outputXml;

	}

	private final String getErroriDatiInput(String codiceFiscale, String inputXML) throws Exception {

		String outputXml = "";

		try {
			XMLValidator.checkObjectFieldExists(codiceFiscale, "codicefiscale", true, codiceFiscaleCheck,
					"\"Codice fiscale\"");
		} catch (MandatoryFieldException e1) {
			outputXml = XmlUtils.createXMLRisposta("04", "Codice fiscale non valido");
			return outputXml;
		} catch (FieldFormatException e2) {
			outputXml = XmlUtils.createXMLRisposta("04", "Codice fiscale non valido");
			return outputXml;
		}

		BigDecimal cdnLavoratore = null;
		String dataCalcolo = it.eng.afExt.utils.DateUtils.getNow();
		Object[] inputParameters = new Object[1];
		inputParameters[0] = codiceFiscale;
		SourceBean lavoratore = (SourceBean) QueryExecutor.executeQuery("SELECT_AN_LAVORATORE", inputParameters,
				"SELECT", Values.DB_SIL_DATI);

		if (!lavoratore.containsAttribute("ROW")) {
			outputXml = XmlUtils.createXMLRisposta("03", "Codice fiscale non trovato");
			return outputXml;
		} else {
			cdnLavoratore = (BigDecimal) lavoratore.getAttribute("ROW.CDNLAVORATORE");
		}

		Object[] inputParametersCpi = new Object[3];
		inputParametersCpi[0] = cdnLavoratore;
		inputParametersCpi[1] = dataCalcolo;
		inputParametersCpi[2] = dataCalcolo;
		SourceBean cpiLav = (SourceBean) QueryExecutor.executeQuery("GET_CPI_AN_LAVORATORE_COMPETENTE_DATA",
				inputParametersCpi, "SELECT", Values.DB_SIL_DATI);
		String codCpiLav = (String) cpiLav.getAttribute("ROW.CPICOMP");
		String codTipoCpi = (String) cpiLav.getAttribute("ROW.codmonotipocpi");

		if (codCpiLav == null) {
			outputXml = XmlUtils.createXMLRisposta("99", "Errore generico");
			return outputXml;
		} else {
			if (codTipoCpi != null && !"C".equalsIgnoreCase(codTipoCpi)) {
				outputXml = XmlUtils.createXMLRisposta("05", "Lavoratore non competente");
				return outputXml;
			}
		}

		return outputXml;

	}

}
