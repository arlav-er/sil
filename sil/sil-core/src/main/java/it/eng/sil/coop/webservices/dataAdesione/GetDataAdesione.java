package it.eng.sil.coop.webservices.dataAdesione;

import java.io.File;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.xml.sax.SAXException;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.base.SourceBeanException;
import com.engiweb.framework.configuration.ConfigSingleton;
import com.engiweb.framework.dbaccess.DataConnectionManager;
import com.engiweb.framework.dbaccess.sql.DataConnection;
import com.engiweb.framework.util.QueryExecutor;

import it.eng.afExt.dispatching.module.impl.ListModule;
import it.eng.afExt.utils.DateUtils;
import it.eng.afExt.utils.MessageCodes;
import it.eng.afExt.utils.ReportOperationResult;
import it.eng.sil.Values;
import it.eng.sil.coop.DataSourceJNDI;
import it.eng.sil.coop.endpoints.EndPointSingleton;
import it.eng.sil.coop.webservices.dataAdesione.input.Giovane;
import it.eng.sil.coop.webservices.dataAdesione.output.Risposta;
import it.eng.sil.coop.webservices.dataAdesione.output.Risposta.Adesioni;
import it.eng.sil.coop.webservices.dataAdesione.output.Risposta.Adesioni.Adesione;
import it.eng.sil.coop.webservices.dataAdesione.output.Risposta.Esito;
import it.eng.sil.coop.webservices.myportal.servizicittadino.utils.Credentials;
import it.eng.sil.coop.webservices.myportal.servizicittadino.utils.WsAuthUtils;
import it.eng.sil.util.Utils;

public class GetDataAdesione extends ListModule {

	private static final long serialVersionUID = 1L;
	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(GetDataAdesione.class.getName());
	protected static final String WS_CODSERVIZIO_DATADESIONE = "MYPORTAL_GG";
	private static final String END_POINT_NAME = "MyPortalDataAdesione";

	protected static final String XSD_PATH = ConfigSingleton.getRootPath() + File.separator + "WEB-INF" + File.separator
			+ "xsd" + File.separator + "DataAdesione" + File.separator;

	protected static final String inputXsd = XSD_PATH + "inputXML_DataAdesione.xsd";
	protected static final String outputXsd = XSD_PATH + "outputXML_DataAdesione.xsd";

	public void service(SourceBean request, SourceBean response) {

		int rowsNumber = 0;
		int totalPages = 0;
		DataConnection dc = null;
		DataConnectionManager dcm = null;
		String className = this.getClass().getName();

		int pagedRows = Integer.parseInt((String) getConfig().getAttribute("ROWS"));

		if (pagedRows < 0) {
			pagedRows = Integer.MAX_VALUE;
		}

		String pool = (String) getConfig().getAttribute("POOL");

		try {
			String strPageNumber = (String) getServiceRequest().getAttribute("LIST_PAGE");
			String strMessage = (String) getServiceRequest().getAttribute("MESSAGE");

			int pageNumber = 1;

			if (strPageNumber != null) {
				pageNumber = new Integer(strPageNumber).intValue();
			} else {
				if ((strMessage != null) && (!strMessage.equalsIgnoreCase("LIST_FIRST"))) {
					pageNumber = -1;
				}
			}

			dcm = DataConnectionManager.getInstance();
			if (dcm == null) {
				_logger.error(className + "::execute: dcm null");
			}
			dc = dcm.getConnection(pool);

			if (dc == null) {
				_logger.error(className + "::service: dc null");
			}

			SourceBean rowsSourceBean = getAdesioni(request, response);

			rowsNumber = 0;
			totalPages = 0;

			response.setAttribute(rowsSourceBean);
		} catch (Exception e) {
			_logger.error(className + "::service: " + e.getMessage());
		} finally {
			com.engiweb.framework.dbaccess.Utils.releaseResources(dc, null, null);
		}
	}

	private SourceBean getAdesioni(SourceBean request, SourceBean response) throws Exception {
		ReportOperationResult rep = new ReportOperationResult(this, response);

		String codiceFiscale = (String) request.getAttribute("strCodiceFiscale");
		String cercaIn = (String) request.getAttribute("cercaIn");

		SourceBean sbTsGenerale = (SourceBean) QueryExecutor.executeQuery("GET_CODMIN_REGIONE_TS_GENERALE", null,
				"SELECT", Values.DB_SIL_DATI);
		String codRegione = (String) sbTsGenerale.getAttribute("ROW.CODMIN");

		Risposta ret = new Risposta();
		SourceBean rowsAdesioni = new SourceBean("ROWS");
		try {
			_logger.info("Chiamata GetDataAdesioneYG, codice fiscale = " + codiceFiscale);

			DataSourceJNDI dataSourceJndi = new DataSourceJNDI();
			String dataSourceJndiName = dataSourceJndi.getJndi();

			EndPointSingleton eps = EndPointSingleton.getInstance(dataSourceJndiName);
			String datAdesioneEP = eps.getUrl(END_POINT_NAME);

			DataAdesioneYGProxy proxy = new DataAdesioneYGProxy();
			proxy.setEndpoint(datAdesioneEP);

			Giovane xmlDataAdesione = new Giovane();
			xmlDataAdesione.setCodiceFiscale(codiceFiscale);
			if (cercaIn.equals("MiaReg")) {
				xmlDataAdesione.setCodRegione(codRegione);
			}

			WsAuthUtils wsAuthUtils = new WsAuthUtils();
			Credentials credentials = wsAuthUtils.getCredentials(WS_CODSERVIZIO_DATADESIONE);
			String inputXml = convertRichDataAdesioneToString(xmlDataAdesione);
			String xmlDTA = proxy.getDataAdesioneYG(credentials.getUsername(), credentials.getPassword(), inputXml);
			_logger.info("Chiamata GetDataAdesioneYG, codice fiscale = " + codiceFiscale + " - risposta = " + xmlDTA);

			if (xmlDTA != null && !("").equalsIgnoreCase(xmlDTA)) {
				ret = convertToRispostaDataAdesione(xmlDTA);
				Esito esito = ret.getEsito();
				String codEsito = esito.getCodice();
				if ("00".equals(codEsito)) {
					Adesioni adesioni = ret.getAdesioni();
					rowsAdesioni = creaRowsAdesioni(adesioni);
				} else if ("01".equals(codEsito)) {
					rep.reportFailure(MessageCodes.YG.WS_DATADESIONE_REG_YG_NON_TROVATA);
				} else if ("02".equals(codEsito)) {
					rep.reportFailure(MessageCodes.YG.WS_DATADESIONE_SOG_YG_NON_TROVATA);
				} else {
					_logger.error(esito.getDescrizione());
					rep.reportFailure(MessageCodes.General.OPERATION_FAIL);
				}
			} else
				throw new Exception("GetDataAdesioneYG, codice fiscale = " + codiceFiscale + " - risposta = Xml vuoto");
		} // try
		catch (java.rmi.RemoteException re) {
			it.eng.sil.util.TraceWrapper.debug(_logger,
					"GetDataAdesioneYG: errore nell'esecuzione remota del Web Service. Codice fiscale= "
							+ codiceFiscale,
					re);

			rep.reportFailure(MessageCodes.YG.ERR_EXEC_WS_DATADESIONE_YG);
		} catch (SourceBeanException ex) {
			rep.reportFailure(MessageCodes.General.OPERATION_FAIL);
			it.eng.sil.util.TraceWrapper.debug(_logger,
					"GetDataAdesioneYG: errore costruzione delle righe della lista adesioni. Codice fiscale= "
							+ codiceFiscale,
					ex);
		} catch (JAXBException ex) {
			rep.reportFailure(MessageCodes.General.OPERATION_FAIL);
			it.eng.sil.util.TraceWrapper.debug(_logger,
					"GetDataAdesioneYG: errore durante la costruzione xml. Codice fiscale= " + codiceFiscale, ex);
		} catch (SAXException ex) {
			rep.reportFailure(MessageCodes.General.OPERATION_FAIL);
			it.eng.sil.util.TraceWrapper.debug(_logger,
					"GetDataAdesioneYG: errore durante la validazione xml. Codice fiscale= " + codiceFiscale, ex);
		} catch (Exception ex) {
			rep.reportFailure(MessageCodes.General.OPERATION_FAIL);
			it.eng.sil.util.TraceWrapper.debug(_logger,
					"GetDataAdesioneYG: errore generico. Codice fiscale= " + codiceFiscale, ex);
		}
		return rowsAdesioni;
	}

	private SourceBean creaRowsAdesioni(Adesioni ades) throws SourceBeanException {
		SourceBean rowsAdes = new SourceBean("ROWS");
		List listaAdesioni = ades.getAdesione();
		for (int i = 0; i < listaAdesioni.size(); i++) {
			Adesione adesione = (Adesione) listaAdesioni.get(i);
			SourceBean rowAdesione = new SourceBean("ROW");
			rowAdesione.setAttribute("dataAdesione", DateUtils.formatXMLGregorian(adesione.getDataAdesione()));

			SourceBean sbDeRegione = (SourceBean) QueryExecutor.executeQuery("GET_DENOMINAZIONE_REGIONE",
					new Object[] { Utils.notNull(adesione.getCodRegioneAdesione()) }, "SELECT", Values.DB_SIL_DATI);
			String descRegioneAdesione = Utils.notNull(sbDeRegione.getAttribute("ROW.descRegioneAdesione"));
			rowAdesione.setAttribute("descRegioneAdesione", descRegioneAdesione);

			SourceBean sbDeProvincia = (SourceBean) QueryExecutor.executeQuery("GET_DESC_PROVINCIA_FROM_CODICE",
					new Object[] { Utils.notNull(adesione.getCodProvinciaAssegnazione()) }, "SELECT",
					Values.DB_SIL_DATI);
			String descProvinciaAdesione = Utils.notNull(sbDeProvincia.getAttribute("ROW.DESCRIZIONE"));
			rowAdesione.setAttribute("codProvinciaAssegnazione", Utils.notNull(descProvinciaAdesione));

			SourceBean sbDeStatoAdesioneMin = (SourceBean) QueryExecutor.executeQuery(
					"GET_DESCRIZIONE_STATO_ADESIONE_MIN",
					new Object[] { Utils.notNull(adesione.getCodStatoAdesioneMin()) }, "SELECT", Values.DB_SIL_DATI);
			String descStatoAdesioneMin = Utils.notNull(sbDeStatoAdesioneMin.getAttribute("ROW.descStatoAdesioneMin"));
			rowAdesione.setAttribute("descStatoAdesioneMin", descStatoAdesioneMin);

			rowAdesione.setAttribute("dataStatoAdesioneMin",
					DateUtils.formatXMLGregorian(adesione.getDataStatoAdesioneMin()));

			rowsAdes.setAttribute(rowAdesione);
		}
		return rowsAdes;
	}

	private String convertRichDataAdesioneToString(Giovane richDataAdesione) throws JAXBException, SAXException {
		String xmlRichDataAdesione = null;
		JAXBContext jc = JAXBContext.newInstance(Giovane.class);
		Marshaller marshaller = jc.createMarshaller();
		marshaller.setSchema(getXsdSchema(inputXsd));
		StringWriter writer = new StringWriter();
		marshaller.marshal(richDataAdesione, writer);
		xmlRichDataAdesione = writer.getBuffer().toString();
		return xmlRichDataAdesione;
	}

	private Risposta convertToRispostaDataAdesione(String xmlDTA) throws JAXBException, SAXException {
		Risposta dta = null;
		JAXBContext jaxbContext = JAXBContext.newInstance(Risposta.class);
		Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
		jaxbUnmarshaller.setSchema(getXsdSchema(outputXsd));
		dta = (Risposta) jaxbUnmarshaller.unmarshal(new StringReader(xmlDTA));
		return dta;
	}

	public static Schema getXsdSchema(String xsdPath) throws SAXException {
		String schemaLang = "http://www.w3.org/2001/XMLSchema";
		SchemaFactory factory = SchemaFactory.newInstance(schemaLang);
		File schemaFile = new File(xsdPath);
		StreamSource streamSource = new StreamSource(schemaFile);
		Schema schema = factory.newSchema(streamSource);
		return schema;
	}

}
