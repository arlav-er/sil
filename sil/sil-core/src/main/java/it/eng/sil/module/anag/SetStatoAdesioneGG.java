package it.eng.sil.module.anag;

import java.io.File;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeConstants;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.rpc.holders.StringHolder;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.configuration.ConfigSingleton;

import it.eng.afExt.utils.MessageCodes;
import it.eng.afExt.utils.ReportOperationResult;
import it.eng.sil.action.report.UtilsConfig;
import it.eng.sil.coop.DataSourceJNDI;
import it.eng.sil.coop.endpoints.EndPointSingleton;
import it.eng.sil.coop.webservices.coapadesioneSet.ministero.types.holders.Risposta_setStatoAdesioneYG_TypeEsitoHolder;
import it.eng.sil.coop.webservices.coapadesioneSet.pojo.yg.statoAdesioneYg.DatiStatoAdesione;
import it.eng.sil.module.AbstractSimpleModule;
import it.eng.sil.module.movimenti.constant.Properties;
import it.eng.sil.security.User;
import it.eng.sil.util.Utils;
import it.eng.sil.util.xml.XMLValidator;

public class SetStatoAdesioneGG extends AbstractSimpleModule {

	private static final long serialVersionUID = -1533726811526662058L;
	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(SetStatoAdesioneGG.class.getName());

	private String className = this.getClass().getName();
	private String END_POINT_NAME = "SetStatoAdesioneYg";

	public void service(SourceBean request, SourceBean response) {
		ReportOperationResult reportOperation = null;
		boolean erroreServizioPortale = false;
		boolean erroreServizioMinistero = false;
		try {
			User user = (User) getRequestContainer().getSessionContainer().getAttribute("@@USER@@");
			BigDecimal p_cdnUtente = new BigDecimal(user.getCodut());

			SourceBean serviceResponse = getResponseContainer().getServiceResponse();
			reportOperation = new ReportOperationResult(this, response);
			disableMessageIdFail();
			disableMessageIdSuccess();

			String cdnLavoratore = Utils.notNull(request.getAttribute("CDNLAVORATORE"));
			String strCodiceFiscale = Utils.notNull(request.getAttribute("CF"));
			String regione = Utils.notNull(request.getAttribute("REGIONE"));
			String nuovoStato = "";
			String operazione = Utils.notNull(request.getAttribute("OPERAZIONE"));
			String dataAdesioneMin = Utils.notNull(request.getAttribute("datAdesioneGG"));
			String dataAdesioneSil = Utils.notNull(request.getAttribute("datAdesioneSIL"));
			String dataAdesione = "";
			if (dataAdesioneMin != null && !dataAdesioneMin.equals("")) {
				dataAdesione = dataAdesioneMin;
			} else {
				if (dataAdesioneSil != null && !dataAdesioneSil.equals("")) {
					dataAdesione = dataAdesioneSil;
				}
			}

			if ((serviceResponse.containsAttribute("M_CheckStatoPoliticheAttiveGG.ESITO_CHECK") && serviceResponse
					.getAttribute("M_CheckStatoPoliticheAttiveGG.ESITO_CHECK").toString().equals("OK"))
					|| (operazione.equals("CONFERMA_STATO"))) {

				if (!dataAdesione.equals("")) {

					if (operazione.equals("SET_STATO")) {
						nuovoStato = Utils.notNull(request.getAttribute("nuovoStato"));
					} else {
						nuovoStato = Utils.notNull(request.getAttribute("nuovoStatoConferma"));
					}

					_logger.info("CHIAMATA SET STATO ADESIONE, CDNLAVORATORE = " + cdnLavoratore);

					UtilsConfig utility = new UtilsConfig("WSADESGG");
					String tipoConfig = utility.getConfigurazioneDefault_Custom();

					DataSourceJNDI dataSourceJndi = new DataSourceJNDI();
					String dataSourceJndiName = dataSourceJndi.getJndi();
					EndPointSingleton eps = EndPointSingleton.getInstance(dataSourceJndiName);

					String statoYgEndpoint = eps.getUrl(END_POINT_NAME);

					StringHolder esitoPortale = null;
					Risposta_setStatoAdesioneYG_TypeEsitoHolder esitoMin = null;
					StringHolder messaggioErrore = null;
					String esito = null;

					// configurazione Default -- invoco il servizio sul portale
					if (tipoConfig.equals(Properties.DEFAULT_CONFIG)) {
						it.eng.sil.coop.webservices.coapadesioneSet.portale.ServizicoapWSProxy servizicoapWSProxy = new it.eng.sil.coop.webservices.coapadesioneSet.portale.ServizicoapWSProxy(
								statoYgEndpoint);

						esitoPortale = new StringHolder();
						messaggioErrore = new StringHolder();

						DatiStatoAdesione dsa = new DatiStatoAdesione();
						dsa.setStatoAdesione(nuovoStato);
						dsa.setDataAdesione(toXMLGregorianCalendarDate(dataAdesione));
						dsa.setCodiceFiscale(strCodiceFiscale);
						dsa.setRegioneAdesione(regione);

						_logger.info("STO PER INVOCARE WS CON NUOVO STATO = " + nuovoStato);

						try {
							servizicoapWSProxy.setStatoAdesioneYG(creaXMLoutAccount(dsa), esitoPortale,
									messaggioErrore);
							esito = esitoPortale.value;
						} catch (Exception exServizio) {
							erroreServizioPortale = true;
							throw new Exception("Errore: Il servizio del Portale non è al momento disponibile");
						}
					} else {
						it.eng.sil.coop.webservices.coapadesioneSet.ministero.ServizicoapWSProxy servizicoapWSProxy = new it.eng.sil.coop.webservices.coapadesioneSet.ministero.ServizicoapWSProxy(
								statoYgEndpoint);

						esitoMin = new Risposta_setStatoAdesioneYG_TypeEsitoHolder();
						messaggioErrore = new StringHolder();

						DatiStatoAdesione dsa = new DatiStatoAdesione();
						dsa.setStatoAdesione(nuovoStato);
						dsa.setDataAdesione(toXMLGregorianCalendarDate(dataAdesione));
						dsa.setCodiceFiscale(strCodiceFiscale);
						dsa.setRegioneAdesione(regione);

						_logger.info("STO PER INVOCARE WS CON NUOVO STATO = " + nuovoStato);

						try {
							servizicoapWSProxy.setStatoAdesioneYG(creaXMLoutAccount(dsa), esitoMin, messaggioErrore);
							esito = esitoMin.value.getValue();
						} catch (Exception exServizio) {
							erroreServizioMinistero = true;
							throw new Exception("Errore: Il servizio del Ministero non è al momento disponibile");
						}
					}

					// Verificare risposta della chiamata al servizio
					if (esito.equalsIgnoreCase("OK")) {
						reportOperation.reportSuccess(MessageCodes.General.OPERATION_SUCCESS);
					} else {
						reportOperation.reportFailure(MessageCodes.General.OPERATION_FAIL);
						String descrizioneAnomalia = "";
						try {
							String strListaAnomalie = (messaggioErrore.value)
									.split("Riscontrato errore nella validazione dell'input :")[1];
							Document doc = XMLValidator.parseXmlFile(strListaAnomalie);
							XPath xpath = XPathFactory.newInstance().newXPath();
							// recupero valori
							XPathExpression exprCodAnomalia = xpath.compile("/ListaAnomalie/Anomalia/CodiceAnomalia");
							XPathExpression exprDescrAnomalia = xpath
									.compile("/ListaAnomalie/Anomalia/DescrizioneAnomalia");
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
							response.setAttribute("SET_YG_STATO_ANOMALIE", descrizioneAnomalia);
						} catch (Exception eMess) {
							descrizioneAnomalia = messaggioErrore.value;
							if (!descrizioneAnomalia.equals("")) {
								response.setAttribute("SET_YG_STATO_ANOMALIE", descrizioneAnomalia);
							}
						}
					}
					response.setAttribute("SET_YG_STATO_ESITO", esito);
				} else {
					reportOperation.reportFailure(MessageCodes.YG.ERR_WS_PARTECIPANTE_GG_DATA_ADESIONE_NULLA);
				}
			}
		} catch (Exception ex) {
			if (erroreServizioPortale) {
				reportOperation.reportFailure(MessageCodes.YG.ERR_WS_STATO_ADESIONE_YG);
			} else {
				if (erroreServizioMinistero) {
					reportOperation.reportFailure(MessageCodes.YG.ERR_WS_STATO_ADESIONE_YG_MINISTERO);
				} else {
					reportOperation.reportFailure(MessageCodes.General.OPERATION_FAIL);
				}
			}
			it.eng.sil.util.TraceWrapper.fatal(_logger, className + ": errore chiamata WS", ex);
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
					+ "xsd" + File.separator + "statoAdesione" + File.separator + "Rev001SetStatoAdesione.xsd");
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

	private static XMLGregorianCalendar toXMLGregorianCalendarDate(String dateString)
			throws DatatypeConfigurationException, ParseException {
		GregorianCalendar gc = new GregorianCalendar();
		Date date = new SimpleDateFormat("dd/MM/yyyy").parse(dateString);
		gc.setTime(date);

		XMLGregorianCalendar xc = null;

		xc = DatatypeFactory.newInstance().newXMLGregorianCalendarDate(gc.get(Calendar.YEAR),
				gc.get(Calendar.MONTH) + 1, gc.get(Calendar.DAY_OF_MONTH), DatatypeConstants.FIELD_UNDEFINED);

		return xc;
	}

}