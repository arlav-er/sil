package it.eng.sil.coop.webservices.myportal.servizicittadino.client;

import java.io.File;

import com.engiweb.framework.configuration.ConfigSingleton;

import it.eng.sil.coop.DataSourceJNDI;
import it.eng.sil.coop.endpoints.EndPointSingleton;
import it.eng.sil.util.xml.XMLValidator;

public abstract class ServiziCittadinoAbstractClient {

	protected org.apache.log4j.Logger _logger;

	protected static final String WS_CODSERVIZIO_GETCITTADINO = "SIL_GETCITTADINO_MYPORTAL";
	protected static final String WS_CODSERVIZIO_PUTCITTADINO = "SIL_PUTCITTADINO_MYPORTAL";

	protected static final String ENDPOINT_GETCITTADINO = "GetCittadinoMyPortal";
	protected static final String ENDPOINT_PUTCITTADINO = "PutCittadinoMyPortal";

	protected static final String XSD_PATH = ConfigSingleton.getRootPath() + File.separator + "WEB-INF" + File.separator
			+ "xsd" + File.separator + "servizi_cittadino" + File.separator;

	// validazione SIL -> MyPortal
	protected static final File inputGetAccountCittadinoSchemaFile = new File(
			XSD_PATH + "inputXML_getAccountCittadino.xsd");
	protected static final File inputGetDettaglioCittadinoSchemaFile = new File(
			XSD_PATH + "inputXML_getDettaglioCittadino.xsd");
	protected static final File inputPutAccountCittadinoSchemaFile = new File(
			XSD_PATH + "inputXML_putAccountCittadino.xsd");
	protected static final File inputReinvioMailAccreditamentoSchemaFile = new File(
			XSD_PATH + "inputXML_reinvioMailAccreditamento.xsd");

	// validazione MyPortal -> SIL
	protected static final File outputGetAccountCittadinoSchemaFile = new File(
			XSD_PATH + "outputXML_getAccountCittadino.xsd");
	protected static final File outputGetDettaglioCittadinoSchemaFile = new File(
			XSD_PATH + "outputXML_getDettaglioCittadino.xsd");
	protected static final File outputPutAccountCittadinoSchemaFile = new File(
			XSD_PATH + "outputXML_putAccountCittadino.xsd");
	protected static final File outputReinvioMailAccreditamentoSchemaFile = new File(
			XSD_PATH + "outputXML_reinvioMailAccreditamento.xsd");

	protected boolean isXmlValid(String inputXML, File schemaFile) {

		String validityErrors = XMLValidator.getValidityErrors(inputXML, schemaFile);

		if (validityErrors != null) {
			_logger.error("Errori di validazione file XML:\n" + validityErrors);
			return false;
		}
		return true;

	}

	protected String getEndPointUrl(String endPointName) {

		DataSourceJNDI dataSourceJndi = new DataSourceJNDI();
		String dataSourceJndiName = dataSourceJndi.getJndi();
		EndPointSingleton eps = EndPointSingleton.getInstance(dataSourceJndiName);
		String endPointUrl = eps.getUrl(endPointName);

		return endPointUrl;

	}

}
