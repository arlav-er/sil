package it.eng.sil.module.anag;

import java.io.File;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.rpc.holders.CalendarHolder;
import javax.xml.rpc.holders.StringHolder;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import org.apache.axis.holders.DateHolder;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.configuration.ConfigSingleton;
import com.engiweb.framework.dbaccess.SQLStatements;
import com.engiweb.framework.dbaccess.sql.DataConnection;
import com.engiweb.framework.dbaccess.sql.DataField;
import com.engiweb.framework.util.QueryExecutorObject;

import it.eng.afExt.utils.MessageCodes;
import it.eng.afExt.utils.ReportOperationResult;
import it.eng.sil.action.report.UtilsConfig;
import it.eng.sil.coop.DataSourceJNDI;
import it.eng.sil.coop.endpoints.EndPointSingleton;
import it.eng.sil.coop.webservices.coapadesioneGet.ministero.types.holders.Risposta_getStatoAdesioneYG_TypeEsitoHolder;
import it.eng.sil.coop.webservices.coapadesioneGet.pojo.yg.statoAdesioneYg.DatiStatoAdesione;
import it.eng.sil.module.AbstractSimpleModule;
import it.eng.sil.module.movimenti.constant.Properties;
import it.eng.sil.security.User;
import it.eng.sil.util.Utils;
import it.eng.sil.util.xml.XMLValidator;
import it.eng.sil.utils.gg.GG_Utils;

public class GetStatoAdesioneGG extends AbstractSimpleModule {

	private static final long serialVersionUID = -1533726811526662058L;
	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(GetStatoAdesioneGG.class.getName());

	private String className = this.getClass().getName();
	private String END_POINT_NAME = "GetStatoAdesioneYg";

	public void service(SourceBean request, SourceBean response) throws Exception {
		User user = (User) getRequestContainer().getSessionContainer().getAttribute("@@USER@@");
		BigDecimal p_cdnUtente = new BigDecimal(user.getCodut());

		boolean erroreServizioPortale = false;
		boolean erroreServizioMinistero = false;
		String cdnLavoratore = Utils.notNull(request.getAttribute("CDNLAVORATORE"));
		String strCodiceFiscale = Utils.notNull(request.getAttribute("CF"));
		String regione = Utils.notNull(request.getAttribute("REGIONE"));
		String operazione = Utils.notNull(request.getAttribute("OPERAZIONE"));

		ReportOperationResult reportOperation = new ReportOperationResult(this, response);
		disableMessageIdFail();
		disableMessageIdSuccess();

		DateHolder dataAdesione = null;
		StringHolder statoAdesione = null;

		try {
			_logger.info("CHIAMATA GET STATO ADESIONE, CDNLAVORATORE = " + cdnLavoratore + " CF = " + strCodiceFiscale);

			UtilsConfig utility = new UtilsConfig("WSADESGG");
			String tipoConfig = utility.getConfigurazioneDefault_Custom();

			DataSourceJNDI dataSourceJndi = new DataSourceJNDI();
			String dataSourceJndiName = dataSourceJndi.getJndi();
			EndPointSingleton eps = EndPointSingleton.getInstance(dataSourceJndiName);

			// recupera endpoint servizio
			String urlAdesioneYg = eps.getUrl(END_POINT_NAME);

			StringHolder esitoPortale = null;
			Risposta_getStatoAdesioneYG_TypeEsitoHolder esitoMin = null;
			StringHolder messaggioErrore = null;
			String esito = null;

			// configurazione Default -- invoco il servizio sul portale
			if (tipoConfig.equals(Properties.DEFAULT_CONFIG)) {
				it.eng.sil.coop.webservices.coapadesioneGet.portale.ServizicoapWSProxy servizicoapWSProxy = new it.eng.sil.coop.webservices.coapadesioneGet.portale.ServizicoapWSProxy(
						urlAdesioneYg);

				esitoPortale = new StringHolder();
				messaggioErrore = new StringHolder();

				dataAdesione = new DateHolder();
				statoAdesione = new StringHolder();
				CalendarHolder dataStatoAdesione = new CalendarHolder();

				DatiStatoAdesione dsa = new DatiStatoAdesione();
				dsa.setCodiceFiscale(strCodiceFiscale);
				dsa.setRegioneAdesione(regione);

				try {
					servizicoapWSProxy.getStatoAdesioneYG(creaXMLoutAccount(dsa), esitoPortale, messaggioErrore,
							dataAdesione, statoAdesione, dataStatoAdesione);
					esito = esitoPortale.value;
				} catch (Exception exServizio) {
					erroreServizioPortale = true;
					throw new Exception("Errore: Il servizio del Portale non è al momento disponibile");
				}
			} else {
				it.eng.sil.coop.webservices.coapadesioneGet.ministero.ServizicoapWSProxy servizicoapWSProxy = new it.eng.sil.coop.webservices.coapadesioneGet.ministero.ServizicoapWSProxy(
						urlAdesioneYg);

				esitoMin = new Risposta_getStatoAdesioneYG_TypeEsitoHolder();
				messaggioErrore = new StringHolder();

				dataAdesione = new DateHolder();
				statoAdesione = new StringHolder();
				CalendarHolder dataStatoAdesione = new CalendarHolder();

				DatiStatoAdesione dsa = new DatiStatoAdesione();
				dsa.setCodiceFiscale(strCodiceFiscale);
				dsa.setRegioneAdesione(regione);

				try {
					servizicoapWSProxy.getStatoAdesioneYG(creaXMLoutAccount(dsa), esitoMin, messaggioErrore,
							dataAdesione, statoAdesione, dataStatoAdesione);
					esito = esitoMin.value.getValue();
				} catch (Exception exServizio) {
					erroreServizioMinistero = true;
					throw new Exception("Errore: Il servizio del Ministero non è al momento disponibile");
				}
			}

			// Verificare risposta della chiamata al servizio
			if (esito.equalsIgnoreCase("OK")) {
				if (operazione.equals("GET_STATO")) {
					reportOperation.reportSuccess(MessageCodes.General.OPERATION_SUCCESS);
				}
				Date dtApp = dataAdesione.value;
				String strDataAdesione = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(dtApp);
				response.setAttribute("YG_STATO", statoAdesione.value);
				response.setAttribute("YG_DATA_ADESIONE", strDataAdesione);
				response.setAttribute("YG_STATO_CODMONOATTIVA", getCodoMonoAttiva(statoAdesione.value));
			} else {
				if (operazione.equals("GET_STATO")) {
					reportOperation.reportFailure(MessageCodes.General.OPERATION_FAIL);
				}
				String descrizioneAnomalia = "";
				try {
					String strListaAnomalie = (messaggioErrore.value)
							.split("Riscontrato errore nella validazione dell'input :")[1];
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
						Node anomaliaDescrNode = anomalieDescrList.item(i);
						String codiceAnomalia = anomaliaNode.getFirstChild().getNodeValue();
						String descrAnomaliaXml = anomaliaDescrNode.getFirstChild().getNodeValue();
						if (!codiceAnomalia.equals("") || !descrAnomaliaXml.equals("")) {
							descrAnomaliaXml = "Errore: (" + codiceAnomalia + ") - (" + descrAnomaliaXml + ")";
							if (descrizioneAnomalia != null && !descrizioneAnomalia.equals("")) {
								descrizioneAnomalia = descrizioneAnomalia + "<br/>" + descrAnomaliaXml;
							} else {
								descrizioneAnomalia = descrAnomaliaXml;
							}
						}
					}
					response.setAttribute("YG_STATO_ANOMALIE", descrizioneAnomalia);
				} catch (Exception eMess) {
					descrizioneAnomalia = messaggioErrore.value;
					if (!descrizioneAnomalia.equals("")) {
						response.setAttribute("YG_STATO_ANOMALIE", descrizioneAnomalia);
					}
				}
			}

		} catch (Exception ex) {
			if (operazione.equals("GET_STATO")) {
				if (erroreServizioPortale) {
					reportOperation.reportFailure(MessageCodes.YG.ERR_WS_STATO_ADESIONE_YG);
				} else {
					if (erroreServizioMinistero) {
						reportOperation.reportFailure(MessageCodes.YG.ERR_WS_STATO_ADESIONE_YG_MINISTERO);
					} else {
						reportOperation.reportFailure(MessageCodes.General.OPERATION_FAIL);
					}
				}
			}
			response.setAttribute("ERRORE_CHIAMATA_WS",
					"Non e' stato possibie invocare il servizio di verifica stato adesione");
			it.eng.sil.util.TraceWrapper.debug(_logger, className + ": errore chiamata WS", ex);
		}
	}

	private String creaXMLoutAccount(DatiStatoAdesione account) {
		try {
			String schemaLang = "http://www.w3.org/2001/XMLSchema";
			JAXBContext jc = JAXBContext.newInstance(DatiStatoAdesione.class);
			Marshaller marshaller = jc.createMarshaller();
			// get validation driver:
			SchemaFactory factory = SchemaFactory.newInstance(schemaLang);

			File schemaFile = new File(ConfigSingleton.getRootPath() + File.separator + "WEB-INF" + File.separator
					+ "xsd" + File.separator + "statoAdesione" + File.separator + "Rev001GetStatoAdesione.xsd");
			StreamSource streamSource = new StreamSource(schemaFile);
			Schema schema = factory.newSchema(streamSource);

			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, false);

			marshaller.setSchema(schema);
			StringWriter writer = new StringWriter();
			marshaller.marshal(account, writer);
			String xmlRichiesta = writer.getBuffer().toString();
			if (xmlRichiesta.contains("<?xml")) {
				xmlRichiesta = xmlRichiesta.substring(xmlRichiesta.indexOf("?>") + 2);
			}
			return xmlRichiesta;
		} catch (Exception e) {
			_logger.error("creaXMLoutAccount: " + e);
			return "";
		}
	}

	private String getCodoMonoAttiva(String statoAdesione) throws Exception {
		QueryExecutorObject qExec = null;
		DataConnection dc = null;
		String result = null;
		try {
			qExec = GG_Utils.getQueryExecutorObject();
			dc = qExec.getDataConnection();
			qExec.setStatement(SQLStatements.getStatement("GetCodMonoAttiva_StatoAdesione"));
			qExec.setType(QueryExecutorObject.SELECT);
			List<DataField> params = new ArrayList<DataField>();
			params.add(dc.createDataField("codstatoadesionemin", Types.VARCHAR, statoAdesione));

			qExec.setInputParameters(params);
			SourceBean sbCodMonoAttiva = (SourceBean) qExec.exec();

			if (sbCodMonoAttiva.containsAttribute("ROW")) {
				return (String) sbCodMonoAttiva.getAttribute("ROW.CODMONOATTIVA");
			}
		} catch (Exception e) {
			throw e;
		} finally {
			if (qExec != null) {
				com.engiweb.framework.dbaccess.Utils.releaseResources(qExec.getDataConnection(), null, null);
			}
		}
		return result;
	}

}