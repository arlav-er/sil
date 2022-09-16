package it.eng.sil.module.patto;

import java.io.File;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.List;
import java.util.Vector;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.xml.sax.SAXException;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.configuration.ConfigSingleton;
import com.engiweb.framework.error.EMFInternalError;

import it.eng.afExt.utils.DateUtils;
import it.eng.afExt.utils.MessageCodes;
import it.eng.afExt.utils.ReportOperationResult;
import it.eng.myportal.ws.DataAdesioneGOProxy;
import it.eng.sil.coop.DataSourceJNDI;
import it.eng.sil.coop.endpoints.EndPointSingleton;
import it.eng.sil.module.AbstractSimpleModule;
import it.eng.sil.module.patto.adesionepa.input.Cittadino;
import it.eng.sil.module.patto.adesionepa.output.Risposta;
import it.eng.sil.module.patto.adesionepa.output.Risposta.Adesioni;
import it.eng.sil.module.patto.adesionepa.output.Risposta.Adesioni.Adesione;
import it.eng.sil.module.patto.adesionepa.output.Risposta.Esito;
import it.eng.sil.util.Utils;

public class GetAdesionePA extends AbstractSimpleModule {

	private static final long serialVersionUID = -1533726811526662058L;
	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(GetAdesionePA.class.getName());

	private String className = this.getClass().getName();

	protected static final String WS_CODSERVIZIO_DATADESIONE = "MYPORTAL_PA";

	protected static final String XSD_PATH = ConfigSingleton.getRootPath() + File.separator + "WEB-INF" + File.separator
			+ "xsd" + File.separator + "adesionePA" + File.separator;

	protected static final String inputXsd = XSD_PATH + "inputXML.xsd";
	protected static final String outputXsd = XSD_PATH + "outputXML.xsd";

	private String END_POINT_NAME = "GetAdesionePA";

	public void service(SourceBean request, SourceBean response) throws EMFInternalError {

		String cdnLavoratore = Utils.notNull(request.getAttribute("CDNLAVORATORE"));
		String inputXml = "";

		ReportOperationResult reportOperation = new ReportOperationResult(this, response);
		disableMessageIdFail();
		disableMessageIdSuccess();

		try {
			_logger.info("CHIAMATA GET ADESIONE PACCHETTO ADULTI, CDNLAVORATORE = " + cdnLavoratore);

			DataSourceJNDI dataSourceJndi = new DataSourceJNDI();
			String dataSourceJndiName = dataSourceJndi.getJndi();
			EndPointSingleton eps = EndPointSingleton.getInstance(dataSourceJndiName);
			// recupera endpoint servizio
			String endpointServizio = eps.getUrl(END_POINT_NAME);

			if (endpointServizio != null) {

				DataAdesioneGOProxy proxy = new DataAdesioneGOProxy();
				proxy.setEndpoint(endpointServizio);

				setSectionQuerySelect("QUERY_SELECT_LAV");
				SourceBean anLavoratoreSB = doSelect(request, response);
				String strCodiceFiscale = (String) anLavoratoreSB.getAttribute("ROW.STRCODICEFISCALE");

				setSectionQuerySelect("QUERY_SELECT_CREDENZIALI");
				SourceBean credenzialiSB = doSelect(request, response);
				String username = (String) credenzialiSB.getAttribute("ROW.struserid");
				String password = (String) credenzialiSB.getAttribute("ROW.strpassword");

				Cittadino cittadino = new Cittadino();
				cittadino.setCodiceFiscale(strCodiceFiscale);

				inputXml = convertCittadinoAdesioneToString(cittadino);

				String xmlOutput = proxy.getDataAdesioneGO(username, password, inputXml);

				if (xmlOutput != null && !xmlOutput.equals("")) {
					Risposta ret = new Risposta();
					ret = convertToRispostaDataAdesione(xmlOutput);
					Esito esito = ret.getEsito();
					String codEsito = esito.getCodice();
					if ("00".equals(codEsito)) {
						Adesioni adesioni = ret.getAdesioni();
						List listaAdesioni = adesioni.getAdesione();
						String dataMaxAdesione = "";
						for (int i = 0; i < listaAdesioni.size(); i++) {
							Adesione adesione = (Adesione) listaAdesioni.get(i);
							String dataCurrAdesione = DateUtils.formatXMLGregorian(adesione.getDataAdesione());
							if (dataMaxAdesione.equals("")) {
								dataMaxAdesione = dataCurrAdesione;
							} else {
								if (DateUtils.compare(dataCurrAdesione, dataMaxAdesione) > 0) {
									dataMaxAdesione = dataCurrAdesione;
								}
							}
						}

						if (!dataMaxAdesione.equals("")) {
							request.setAttribute("DATMAXADESIONEPA", dataMaxAdesione);
							setSectionQueryUpdate("QUERY_UPDATE_PATTO_ADESIONE");
							doUpdate(request, response);
						}

						reportOperation.reportSuccess(MessageCodes.General.OPERATION_SUCCESS);
					} else {
						if ("01".equals(codEsito)) {
							// Adesione PA assente sul portale
							setSectionQueryUpdate("QUERY_UPDATE_PATTO_ADESIONE_VUOTA");
							doUpdate(request, response);

							Vector<String> paramV = new Vector<String>();
							paramV.add(strCodiceFiscale);
							reportOperation.reportFailure(MessageCodes.PacchettoAdulti.ADESIONE_ASSENTE,
									"WS GetAdesionePA", "ERRORE INTERNO", paramV);
						} else {
							if ("03".equals(codEsito)) {
								// Errore nel formato XML
								reportOperation.reportFailure(MessageCodes.PacchettoAdulti.XML_ERRATO);
							} else {
								reportOperation.reportFailure(MessageCodes.General.OPERATION_FAIL);
							}
						}
					}
				} else {
					reportOperation.reportFailure(MessageCodes.Webservices.WS_ERRORE_INVOCAZIONE);
				}
			} else {
				reportOperation.reportFailure(MessageCodes.Webservices.WS_ERRORE_RECUPERO_URL);
			}

		} catch (Exception ex) {
			reportOperation.reportFailure(MessageCodes.Webservices.WS_ERRORE_INVOCAZIONE);
			it.eng.sil.util.TraceWrapper.fatal(_logger, className + ": errore chiamata WS", ex);
		}
	}

	private String convertCittadinoAdesioneToString(Cittadino richiesta) throws JAXBException, SAXException {
		String xmlRichiesta = null;
		JAXBContext jc = JAXBContext.newInstance(Cittadino.class);
		Marshaller marshaller = jc.createMarshaller();
		marshaller.setSchema(getXsdSchema(inputXsd));
		StringWriter writer = new StringWriter();
		marshaller.marshal(richiesta, writer);
		xmlRichiesta = writer.getBuffer().toString();
		return xmlRichiesta;
	}

	private Risposta convertToRispostaDataAdesione(String xmlOutput) throws JAXBException, SAXException {
		Risposta dta = null;
		JAXBContext jaxbContext = JAXBContext.newInstance(Risposta.class);
		Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
		jaxbUnmarshaller.setSchema(getXsdSchema(outputXsd));
		dta = (Risposta) jaxbUnmarshaller.unmarshal(new StringReader(xmlOutput));
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
