package it.eng.sil.test.coop.clicLavoro.candidatura;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Vector;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.error.EMFUserError;
import com.engiweb.framework.util.QueryExecutor;

import it.eng.sil.Values;
import it.eng.sil.coop.webservices.clicLavoro.candidatura.CLCandidaturaData;
import it.eng.sil.test.coop.clicLavoro.XP;
import it.eng.sil.util.xml.FieldFormatException;
import it.eng.sil.util.xml.MandatoryFieldException;
import it.eng.sil.util.xml.XMLValidator;
import junit.framework.TestCase;

public class CandidaturaMansioneTestCase extends TestCase {
	static Logger _logger = Logger.getLogger(CandidaturaMansioneTestCase.class.getName());
	private String codiceFiscale;
	private BigDecimal cdnLavoratore;
	private String codCPI;
	private String dataInvio;
	private String codMansione;
	private String codMansioneDot;
	private BigDecimal prgMansione;
	private CLCandidaturaData risposta;
	private ArrayList<String> listContratto = new ArrayList<String>();
	// private ArrayList<String> listCodCL = new ArrayList<String>();

	public CandidaturaMansioneTestCase(String codiceFiscale, BigDecimal cdnLavoratore, String codCPI,
			String dataInvio) {
		super();
		this.codiceFiscale = codiceFiscale;
		this.cdnLavoratore = cdnLavoratore;
		this.codCPI = codCPI;
		this.dataInvio = dataInvio;
	}

	@Override
	protected void runTest() throws Throwable {
		risposta = new CLCandidaturaData(codiceFiscale, codCPI, dataInvio, null);
		System.out.println("Test case) codice fiscale: " + codiceFiscale);
		String xml = "";
		// Genero il 1' xml prima di effettuare le modifiche
		try {
			risposta.costruisci();
			xml = risposta.generaXML();

		} catch (MandatoryFieldException e) {
			fail("codice fiscale: " + codiceFiscale + ". Errore nella generazione xml: " + e.getExceptionMessage());
			e.printStackTrace();
		} catch (FieldFormatException e) {
			fail("codice fiscale: " + codiceFiscale + ". Errore nella generazione xml: " + e.getExceptionMessage());
			e.printStackTrace();
		} catch (EMFUserError e) {
			fail("codice fiscale: " + codiceFiscale + ". Errore nella generazione xml: " + e.getSeverity());
			e.printStackTrace();
		}

		try {
			_logger.info("\r\n---------------------------------- INIZIO TEST ----------------------------------");
			xml = testMansione(xml);
			xml = testEsperienzaLavoroContratti(xml);

		} catch (MandatoryFieldException ee) {
			fail("codice fiscale: " + codiceFiscale + ". Errore nella generazione xml: " + ee.getExceptionMessage());
			ee.printStackTrace();
		} catch (FieldFormatException e) {
			fail("codice fiscale: " + codiceFiscale + ". Errore nella generazione xml: " + e.getExceptionMessage());
			e.printStackTrace();
		} catch (EMFUserError e) {
			fail("codice fiscale: " + codiceFiscale + ". Errore nella generazione xml: " + e.getSeverity());
			e.printStackTrace();
		}
	}

	/*
	 * Verifico le Esperienze Lavorative presenti sul lavoratore, Effettuo il controllo sul nuovo xml per verificare la
	 * presenza della nuova Esperienza Lavorativa
	 * 
	 * @param xml stringa xml d'origine
	 */
	private String testEsperienzaLavoroContratti(String xml) throws MandatoryFieldException, FieldFormatException,
			EMFUserError, SAXException, IOException, XPathExpressionException, ParserConfigurationException {
		// Verifico la presenza delle esperienze lavorative

		Document document = XMLValidator.parseXmlFile(xml);
		XP xmlXP = new XP(document.getDocumentElement());

		NodeList esperienzeList = xmlXP.getList(XP.esperienzeXPathExp);
		_logger.info("N. Esperienze Lavorative(prima dell'inserimento) = " + esperienzeList.getLength());

		// Inserisco sulla mansione 2 tipologie contrattuali, 1 con codice ministeriale e l'altra no.
		for (int i = 0; i < listContratto.size(); i++) {
			// Inserisco l'esperienza lavorativa col contratto appena recuperato
			String codContratto = listContratto != null && listContratto.size() > 0 ? listContratto.get(i) : "";

			Object[] inputParameters = new Object[12];
			inputParameters[0] = prgMansione;
			inputParameters[1] = codContratto;
			inputParameters[2] = "Attivita di prova....";
			inputParameters[3] = "1";
			inputParameters[4] = "2011";
			inputParameters[5] = "1";
			inputParameters[6] = "2012";
			inputParameters[7] = "12";
			inputParameters[8] = "Azienda di Test";
			inputParameters[9] = "via tal dei tali di Test";
			inputParameters[10] = "100";
			inputParameters[11] = "100";
			// Effettuo l'inserimento
			Object object = QueryExecutor.executeQuery("CL_INSERT_ESP_LAVORO_CANDIDATURA", inputParameters, "INSERT",
					Values.DB_SIL_DATI);
		}
		String xmlTest = "";
		// Recupero il nuovo xml per fare il match
		risposta.costruisci();
		xmlTest = risposta.generaXML();
		// Verifico la presenza della Esperienza Lavorativa Inserita

		document = XMLValidator.parseXmlFile(xmlTest);
		xmlXP = new XP(document.getDocumentElement());

		esperienzeList = xmlXP.getList(XP.esperienzeXPathExp);
		boolean insert = false;
		String tipoEsperienza = "";

		if (esperienzeList != null) {
			XPath xPath = XPathFactory.newInstance().newXPath();

			int i = 0;
			while (i < esperienzeList.getLength()) {
				Node node = (Node) esperienzeList.item(i);
				try {
					Node singleNode = (Node) xPath.evaluate("cliclavoro:qualificasvolta", node, XPathConstants.NODE);
					String codMansioneDotIn = singleNode.getTextContent();
					if (codMansioneDot.equals(codMansioneDotIn)) {
						singleNode = (Node) xPath.evaluate("cliclavoro:tipoesperienza", node, XPathConstants.NODE);
						tipoEsperienza = singleNode.getTextContent();
						insert = true;
						break;
					}
				} catch (Exception e) {
				}
				++i;
			}
		}
		if (insert) {
			_logger.info("Tipo Esperienza Inserita = " + tipoEsperienza);
		}
		_logger.info("N. Esperienze Lavorative(dopo l'inserimento) = " + (esperienzeList.getLength()));

		return xmlTest;
	}

	/*
	 * Verifico il numero di mansioni presenti sul lavoratore, inserisco una nuova ed effettuo il controllo sul nuovo
	 * xml
	 * 
	 * @param xml stringa xml d'origine
	 */
	@SuppressWarnings("unchecked")
	private String testMansione(String xml) throws MandatoryFieldException, FieldFormatException, EMFUserError,
			SAXException, IOException, XPathExpressionException, ParserConfigurationException {
		// Verifico la presenza delle Mansioni

		Document document = XMLValidator.parseXmlFile(xml);
		XP xmlXP = new XP(document.getDocumentElement());

		NodeList mansioniList = xmlXP.getList(XP.mansioniXPathExp);
		_logger.info("N. Mansioni(prima dell'inserimento) = " + mansioniList.getLength());

		String xmlTest = "";
		Object[] inputParameters = new Object[2];
		inputParameters[0] = cdnLavoratore;
		inputParameters[1] = dataInvio;
		// Recupero la 1' mansione disponibile
		SourceBean sb = (SourceBean) QueryExecutor.executeQuery("CL_SEARCH_MANSIONE_CANDIDATURA", inputParameters,
				"SELECT", Values.DB_SIL_DATI);
		Vector<SourceBean> rows = sb.getAttributeAsVector("ROW");
		SourceBean row = rows.get(0);
		this.codMansione = (String) row.getAttribute("codmansione");
		this.codMansioneDot = (String) row.getAttribute("codmansionedot");
		String flagEsp = "S", flagEspForm = "S", flagDispForm = "S", flagDisp = "S";

		inputParameters = new Object[8];
		inputParameters[0] = cdnLavoratore;
		inputParameters[1] = codMansione;
		inputParameters[2] = flagEsp;
		inputParameters[3] = flagDisp;
		inputParameters[4] = flagDispForm;
		inputParameters[5] = flagEspForm;
		inputParameters[6] = "100";
		inputParameters[7] = "100";
		// Inserisco la mansione
		Object object = QueryExecutor.executeQuery("CL_INSERT_MANSIONE_CANDIDATURA", inputParameters, "INSERT",
				Values.DB_SIL_DATI);

		// Recupero il prgMansione appena inserito
		inputParameters = new Object[1];
		inputParameters[0] = cdnLavoratore;
		sb = (SourceBean) QueryExecutor.executeQuery("CL_SEARCH_PRGMANSIONE_CANDIDATURA", inputParameters, "SELECT",
				Values.DB_SIL_DATI);
		rows = sb.getAttributeAsVector("ROW");
		row = rows.get(0);
		this.prgMansione = (BigDecimal) row.getAttribute("prgmansione");

		// Recupero il nuovo xml per fare il match
		risposta.costruisci();
		xmlTest = risposta.generaXML();

		// Verifico la presenza della Mansione Inserita
		document = XMLValidator.parseXmlFile(xmlTest);
		xmlXP = new XP(document.getDocumentElement());

		mansioniList = xmlXP.getList(XP.mansioniXPathExp);
		boolean insert = false;
		XPath xPath = XPathFactory.newInstance().newXPath();

		if (mansioniList != null) {
			int i = 0;
			while (i < mansioniList.getLength()) {
				Node node = ((Node) mansioniList.item(i));
				try {
					Node singleNode = (Node) xPath.evaluate("cliclavoro:idprofessione", node, XPathConstants.NODE);
					String codMansioneDotIn = singleNode.getTextContent();
					if (codMansioneDot.equals(codMansioneDotIn)) {
						insert = true;
						break;
					}
				} catch (Exception e) {
				}
				++i;
			}
		}

		if (insert) {
			_logger.info("Mansione Inserita = " + codMansioneDot);
		}
		_logger.info("N. Mansioni(dopo l'inserimento) = " + (mansioniList.getLength()));

		return xmlTest;
	}
}
