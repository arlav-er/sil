package it.eng.sil.coop.webservices.sap;

import java.io.File;
import java.io.StringReader;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.rmi.RemoteException;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.namespace.QName;
import javax.xml.rpc.JAXRPCException;
import javax.xml.rpc.holders.StringHolder;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.configuration.ConfigSingleton;
import com.engiweb.framework.error.EMFInternalError;
import com.engiweb.framework.util.QueryExecutor;

import it.eng.afExt.utils.MessageCodes;
import it.eng.afExt.utils.StringUtils;
import it.eng.afExt.utils.TransactionQueryExecutor;
import it.eng.sil.Values;
import it.eng.sil.coop.DataSourceJNDI;
import it.eng.sil.coop.endpoints.EndPointSingleton;
import it.eng.sil.pojo.yg.richiestaSAP.IDSAP;
import it.eng.sil.pojo.yg.sap.due.LavoratoreType;
import it.eng.sil.pojo.yg.sap.due.ObjectFactory;
import it.eng.sil.util.xml.XMLValidator;
import it.gov.lavoro.servizi.servizicoapSap.ServizicoapWSProxy;
import it.gov.lavoro.servizi.servizicoapSap.types.Risposta_invioSAP_TypeEsito;
import it.gov.lavoro.servizi.servizicoapSap.types.holders.Risposta_invioSAP_TypeEsitoHolder;

public class BeanSap {
	/**
	 * Bean di supporto per SAP 2
	 */

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(BeanSap.class.getName());
	private static final String END_POINT_NAME_RICHIESTASAP = "InvioRichiestaSAP";
	private static final String END_POINT_NAME_INVIOSAP = "InvioSAP";

	private static final String INPUT_XSD_SAP = ConfigSingleton.getRootPath() + File.separator + "WEB-INF"
			+ File.separator + "xsd" + File.separator + "sap" + File.separator + "Rev009_SAP.xsd";

	public static final String STATO_SAP_ATTIVA = "01";

	public static LavoratoreType sendRichiestaSAP(String codMinSap)
			throws JAXRPCException, RemoteException, JAXBException {
		_logger.debug("INIZIO CHIAMATA RICHIESTA SAP, codMinSap =" + codMinSap);

		LavoratoreType sapLav = null;

		DataSourceJNDI dataSourceJndi = new DataSourceJNDI();
		String dataSourceJndiName = dataSourceJndi.getJndi();
		EndPointSingleton eps = EndPointSingleton.getInstance(dataSourceJndiName);
		String invioSapEP = eps.getUrl(END_POINT_NAME_RICHIESTASAP);
		// String invioSapEP = "http://localhost:28080/sil";
		ServizicoapWSProxy proxy = new ServizicoapWSProxy();
		proxy.setEndpoint(invioSapEP);

		IDSAP xmlCodiceSAP = new IDSAP();
		xmlCodiceSAP.setIdentificativoSap(codMinSap);

		String xmlSAP = proxy.richiestaSAP(convertRichiestaSapToString(xmlCodiceSAP));
		if (xmlSAP != null && !("").equalsIgnoreCase(xmlSAP) && !xmlSAP.trim().startsWith("X001")) {
			sapLav = convertToLavoratoreSAP(xmlSAP);
		}
		_logger.debug("FINE CHIAMATA RICHIESTA SAP, codMinSap =" + codMinSap);

		return sapLav;
	}

	public static int invioSap(String xmlSap, String cdnLavoratore) {
		_logger.debug("INIZIO CHIAMATA INVIO SAP, cdnLavoratore =" + cdnLavoratore);
		try {

			it.gov.lavoro.servizi.servizicoapSap.types.holders.Risposta_invioSAP_TypeEsitoHolder esito = new Risposta_invioSAP_TypeEsitoHolder();
			javax.xml.rpc.holders.StringHolder messaggioErrore = new StringHolder();
			javax.xml.rpc.holders.StringHolder codiceSAP = new StringHolder("");

			DataSourceJNDI dataSourceJndi = new DataSourceJNDI();
			String dataSourceJndiName = dataSourceJndi.getJndi();
			EndPointSingleton eps = EndPointSingleton.getInstance(dataSourceJndiName);
			String invioSapEP = eps.getUrl(END_POINT_NAME_INVIOSAP);

			ServizicoapWSProxy proxy = new ServizicoapWSProxy();
			proxy.setEndpoint(invioSapEP);

			String nomeXsd = INPUT_XSD_SAP;

			if (!validazioneXml(xmlSap, nomeXsd)) {
				return MessageCodes.YG.ERR_WS_PARTECIPANTE_GG_INPUT;
			}

			_logger.debug("send invio la sap= " + xmlSap);
			proxy.invioSAP(xmlSap, esito, messaggioErrore, codiceSAP);

			_logger.debug("risposta invio sap   cdnLavoratore =" + cdnLavoratore + " - codice=" + codiceSAP.value);
			_logger.debug("risposta invio la sap messaggio di errore=" + messaggioErrore.value);
			_logger.debug("risposta invio la sap esito=" + esito.value);

			if ((Risposta_invioSAP_TypeEsito.KO).equals(esito.value)
					|| (Risposta_invioSAP_TypeEsito.X001).equals(esito.value)) {
				// visualizzo le anomalie nel messaggio a video
				String descrizioneAnomalia = "";
				String strListaAnomalie = messaggioErrore.value;
				if ((Risposta_invioSAP_TypeEsito.X001).equals(esito.value)) {
					descrizioneAnomalia = strListaAnomalie;
				} else {
					Document doc = XMLValidator.parseXmlFile(strListaAnomalie);
					XPath xpath = XPathFactory.newInstance().newXPath();
					// recupero valori
					XPathExpression exprCodAnomalia = xpath.compile("/ListaAnomalie/Anomalia/CodiceAnomalia");
					XPathExpression exprDescrAnomalia = xpath.compile("/ListaAnomalie/Anomalia/DescrizioneAnomalia");
					Object anomalie = exprCodAnomalia.evaluate(doc, XPathConstants.NODESET);
					Object anomalieDescr = exprDescrAnomalia.evaluate(doc, XPathConstants.NODESET);
					NodeList anomalieList = (NodeList) anomalie;
					NodeList anomalieDescrList = (NodeList) anomalieDescr;
					for (int i = 0; i < anomalieList.getLength(); i++) {
						Node anomaliaNode = anomalieList.item(i);
						String codiceAnomalia = anomaliaNode.getFirstChild().getNodeValue();
						// 17 marzo 2017 segnalazioni 5198 e 5199 - gestione per nuovo modo di inviare errore dal
						// ministero
						boolean searchDescription = false;
						Node anomaliaDescrNode = anomalieDescrList.item(i);
						if (anomaliaDescrNode != null && anomaliaDescrNode.getFirstChild() != null) {
							String descrAnomaliaXml = anomaliaDescrNode.getFirstChild().getNodeValue();
							if (StringUtils.isFilledNoBlank(descrAnomaliaXml)) {
								descrizioneAnomalia = descrizioneAnomalia + " " + codiceAnomalia + " - "
										+ descrAnomaliaXml + "<br/>";
							} else {
								searchDescription = true;
							}
						} else {
							searchDescription = true;
						}
						if (searchDescription) {
							SourceBean rowsAnomalia = null;
							Object[] fieldWhere = new Object[1];
							fieldWhere[0] = codiceAnomalia;
							rowsAnomalia = (SourceBean) QueryExecutor.executeQuery("GET_DESC_ERRORE_YG", fieldWhere,
									"SELECT", Values.DB_SIL_DATI);
							String descAnomalia = (String) rowsAnomalia.getAttribute("ROW.STRDESCRIZIONE");
							if (descAnomalia != null && !("").equalsIgnoreCase(descAnomalia)) {
								descrizioneAnomalia = descrizioneAnomalia + " " + codiceAnomalia + " - " + descAnomalia
										+ "<br/>";
							}
						}
					}
				}

				return MessageCodes.YG.ERR_EXEC_WS_INVIOSAP;
			} else {
				return 0;
			}
		} catch (Exception e) {
			return MessageCodes.YG.ERR_EXEC_WS_INVIOSAP;
		} finally {
			_logger.debug("FINE CHIAMATA INVIO SAP, cdnLavoratore =" + cdnLavoratore);
		}
	}

	public static String convertRichiestaSapToString(IDSAP richiestaSAP) throws JAXBException {
		JAXBContext jc = JAXBContext.newInstance(IDSAP.class);
		Marshaller marshaller = jc.createMarshaller();
		StringWriter writer = new StringWriter();
		marshaller.marshal(richiestaSAP, writer);
		String xmlRichiestaSAP = writer.getBuffer().toString();
		return xmlRichiestaSAP;
	}

	public static LavoratoreType convertToLavoratoreSAP(String xmlSAP) throws JAXBException {
		JAXBContext jaxbContext;
		LavoratoreType sap = null;

		jaxbContext = JAXBContext.newInstance(ObjectFactory.class);
		Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
		JAXBElement<LavoratoreType> root = (JAXBElement<LavoratoreType>) jaxbUnmarshaller
				.unmarshal(new StringReader(xmlSAP));
		sap = root.getValue();

		return sap;
	}

	public static String convertLavoratoreTypeToString(LavoratoreType lavoratoreSAP) throws JAXBException {
		StringWriter stringWriter = new StringWriter();

		JAXBContext jaxbContext = JAXBContext.newInstance(LavoratoreType.class);
		Marshaller jaxbMarshaller = jaxbContext.createMarshaller();

		QName qName = new QName("", "lavoratore");
		JAXBElement<LavoratoreType> root = new JAXBElement<LavoratoreType>(qName, LavoratoreType.class, lavoratoreSAP);

		jaxbMarshaller.marshal(root, stringWriter);

		String result = stringWriter.toString();

		return result;
	}

	/* Valida l'xml passato in input con l'xsd passato come secondo parametro */
	public static boolean validazioneXml(String xml, String xsdPath) {
		try {
			SchemaFactory factory = SchemaFactory.newInstance("http://www.w3.org/2001/XMLSchema");
			if (StringUtils.isEmptyNoBlank(xsdPath)) {
				xsdPath = INPUT_XSD_SAP;
			}
			File schemaFile = new File(xsdPath);
			StreamSource streamSource = new StreamSource(schemaFile);
			Schema schema = factory.newSchema(streamSource);
			Validator validator = schema.newValidator();
			StreamSource datiXmlStreamSource = new StreamSource(new StringReader(xml));
			validator.validate(datiXmlStreamSource);
			return true;
		} catch (Exception e) {
			_logger.error(e);
			return false;
		}
	}

	public static BigDecimal getPrgTracciamentoSap(TransactionQueryExecutor transExec) throws EMFInternalError {
		SourceBean sbPrgTracciamentoSap = null;
		sbPrgTracciamentoSap = (SourceBean) transExec.executeQuery("GET_TS_TRACCIAMENTO_SAP_NEXTVAL", null, "SELECT");
		BigDecimal prgTracciamentoSap = (BigDecimal) sbPrgTracciamentoSap.getAttribute("ROW.CHIAVE");
		return prgTracciamentoSap;
	}

}
