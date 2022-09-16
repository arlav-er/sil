package it.eng.sil.coop.webservices.agenda.appuntamento;

import java.io.File;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.datatype.DatatypeConfigurationException;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.SessionContainer;
import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.configuration.ConfigSingleton;
import com.engiweb.framework.dbaccess.DataConnectionManager;
import com.engiweb.framework.dbaccess.sql.DataConnection;

import it.eng.afExt.utils.DateUtils;
import it.eng.afExt.utils.MessageCodes;
import it.eng.afExt.utils.StringUtils;
import it.eng.afExt.utils.TransactionQueryExecutor;
import it.eng.sil.Values;
import it.eng.sil.coop.DataSourceJNDI;
import it.eng.sil.coop.endpoints.EndPointSingleton;
import it.eng.sil.coop.webservices.apapi.XmlUtils;
import it.eng.sil.module.AbstractSimpleModule;
import it.eng.sil.nsr.DataModels.InformationDelivery.NotificaAppuntamentoEsito._1_0.NotificaEsitoAppuntamentoTypeProxy;
import it.eng.sil.nsr.DataModels.InformationDelivery.NotificaAppuntamentoEsito._1_0.NuovoAppuntamentoType;
import it.eng.sil.nsr.DataModels.InformationDelivery.NotificaAppuntamentoEsito._1_0.RichiestaNotificaEsitoType;
import it.eng.sil.nsr.DataModels.InformationDelivery.NotificaAppuntamentoEsito._1_0.RispostaNotificaEsitoType;
import it.eng.sil.security.User;
import it.eng.sil.util.Utils;

public class NotificaEsitoAppuntamentoAnpal extends AbstractSimpleModule {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1756236590338908567L;
	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger
			.getLogger(NotificaEsitoAppuntamentoAnpal.class.getName());

	private String END_POINT_NAME = "EsitoAppAnpal";

	private String ERRORE_NODB = "T099";

	private String ESITO_OK = "A000";

	private File notificaAppuntamento_SchemaFile = new File(ConfigSingleton.getRootPath() + File.separator + "WEB-INF"
			+ File.separator + "xsd" + File.separator + "agenda" + File.separator + "appuntamento" + File.separator
			+ "coapAnpal" + File.separator + "NotificaEsitoAppuntamento-1.0.xsd");

	@Override
	public void service(SourceBean serviceRequest, SourceBean serviceResponse) throws Exception {
		// ReportOperationResult reportOperation = new ReportOperationResult(this, serviceResponse);
		DataConnection conn = null;
		TransactionQueryExecutor trans = null;
		boolean res;

		try {
			DataSourceJNDI dataSourceJndi = new DataSourceJNDI();
			String dataSourceJndiName = dataSourceJndi.getJndi();
			EndPointSingleton eps = EndPointSingleton.getInstance(dataSourceJndiName);

			String endPoint = eps.getUrl(END_POINT_NAME);

			NotificaEsitoAppuntamentoTypeProxy proxy = new NotificaEsitoAppuntamentoTypeProxy(endPoint);

			RichiestaNotificaEsitoType richiesta = new RichiestaNotificaEsitoType();

			String idAppuntamento = Utils.notNull(serviceRequest.getAttribute("IDAPPUNTAMENTOCOAP"));
			String idAppuntamentoAR = Utils.notNull(serviceRequest.getAttribute("PRGAPPUNTAMENTO"));
			String idCPI = Utils.notNull(serviceRequest.getAttribute("CODCPIANPAL"));
			String idEsitoAppuntamento = Utils.notNull(serviceRequest.getAttribute("CODESITOMINAPP"));
			String codEsitoAppuntamento = Utils.notNull(serviceRequest.getAttribute("CODESITOAPPUNT"));

			richiesta.setIdAppuntamento(idAppuntamento);
			richiesta.setIdAppuntamentoAR(idAppuntamentoAR);
			richiesta.setIdCPI(idCPI);
			richiesta.setIdEsitoAppuntamento(idEsitoAppuntamento);

			if (StringUtils.isFilledNoBlank(idEsitoAppuntamento) && "04".equalsIgnoreCase(idEsitoAppuntamento)) {
				String idCausa = "C01";
				richiesta.setIdCausa(idCausa);
				String codiceIntermediario = Utils.notNull(serviceRequest.getAttribute("CODCPIMIN"));
				NuovoAppuntamentoType nuovoAppto = new NuovoAppuntamentoType();
				nuovoAppto.setIdAppuntamentoAR(idAppuntamentoAR);
				nuovoAppto.setCodiceIntermediario(codiceIntermediario);
				String dataStr = Utils.notNull(serviceRequest.getAttribute("data_app"));
				Calendar cal = Calendar.getInstance();
				SimpleDateFormat sdf = new SimpleDateFormat(DateUtils.FORMATO_DATA);
				cal.setTime(sdf.parse(dataStr));
				nuovoAppto.setDataOraAppuntamento(cal);
				richiesta.setNuovoAppuntamento(nuovoAppto);
			}

			// setto un attribute sul request container per customizzare l'handler
			RequestContainer requestContainer = RequestContainer.getRequestContainer();
			requestContainer.setAttribute(MessageCodes.AGENDA_ANAPL.HANDLER, MessageCodes.AGENDA_ANAPL.HANDLER);

			// Trovo l'utente connesso
			SessionContainer sessionContainer = requestContainer.getSessionContainer();
			// Recupero utente
			User user = (User) sessionContainer.getAttribute(User.USERID);
			String cdnParUtente = Integer.toString(user.getCodut());
			RispostaNotificaEsitoType risposta = proxy.notifica(richiesta);

			it.eng.sil.nsr.DataModels.InformationDelivery.xsd.NotificaAppuntamentoEsito._1_0.RispostaNotificaEsitoType rispostaXsd = new it.eng.sil.nsr.DataModels.InformationDelivery.xsd.NotificaAppuntamentoEsito._1_0.RispostaNotificaEsitoType();
			if (risposta != null && risposta.getIdEsito() != null) {
				rispostaXsd.setIdEsito(risposta.getIdEsito());
				rispostaXsd.setDescrizioneEsitoNegativo(risposta.getDescrizioneEsitoNegativo());
				rispostaXsd.setIdNuovoAppuntamento(risposta.getIdNuovoAppuntamento());
			}

			String outputXML = createXmlRisposta(rispostaXsd);

			boolean outputXmlIsValid = XmlUtils.isXmlValid(outputXML, notificaAppuntamento_SchemaFile);
			if (!outputXmlIsValid) {
				_logger.error("Notifica appuntamento anpal: Validazione fallita xml response");
				serviceResponse.setAttribute("ESITO_APPUNTAMENTO_ANPAL_KO", "Esito invio ad ANPAL: Operazione fallita");
				return;
			}

			DataConnectionManager dataConnectionManager = DataConnectionManager.getInstance();
			conn = dataConnectionManager.getConnection(Values.DB_SIL_DATI);

			trans = new TransactionQueryExecutor(conn, null, null);

			String esitoAnpal = (String) risposta.getIdEsito();

			if (StringUtils.isEmptyNoBlank(risposta.getDescrizioneEsitoNegativo())) {
				Object[] argsDescr = new Object[1];
				argsDescr[0] = esitoAnpal.toUpperCase();
				SourceBean queryDescrizione = (SourceBean) trans.executeQuery(
						"SELECT_DESCRIZIONE_ESITO_APPUNTAMENTO_ANPAL", argsDescr, TransactionQueryExecutor.SELECT);
				if (queryDescrizione != null) {
					String descrizione = (String) queryDescrizione.getAttribute("ROW.DESCRIZIONE");
					risposta.setDescrizioneEsitoNegativo(descrizione);
				}
			}

			if (!esitoAnpal.equalsIgnoreCase(ERRORE_NODB)) {

				String codiceEsitoMin = "";
				Object[] argEsito = new Object[1];
				argEsito[0] = codEsitoAppuntamento;
				SourceBean queryEsitoMin = (SourceBean) trans.executeQuery("GET_MN_ESITO_APP", argEsito,
						TransactionQueryExecutor.SELECT);
				if (queryEsitoMin != null) {
					codiceEsitoMin = (String) queryEsitoMin.getAttribute("ROW.CODESITOAPPMIN");
				}

				////////////////////////
				// INIZIO TRANSAZIONE //
				////////////////////////

				trans.initTransaction();

				Object[] args = new Object[8];
				args[0] = risposta.getIdNuovoAppuntamento();
				args[1] = codEsitoAppuntamento;
				args[2] = new BigDecimal(cdnParUtente);
				args[3] = codiceEsitoMin; // codice esito ministeriale inviato
				args[4] = idCPI;
				args[5] = risposta.getIdEsito();
				args[6] = risposta.getDescrizioneEsitoNegativo();
				args[7] = idAppuntamentoAR;
				Object objRes = trans.executeQuery("REGISTRA_ESITO_APPUNTAMENTO_ANPAL", args,
						TransactionQueryExecutor.INSERT);
				res = Boolean.TRUE.equals(objRes);
				if (res) {
					_logger.info("Inserimento in AG_INVIO_ESITO avvenuto con successo");
				}
				trans.commitTransaction();
			}
			String lastEsitoAnpal = "Esito invio ad ANPAL: ";
			lastEsitoAnpal += (String) risposta.getIdEsito();
			lastEsitoAnpal += " - " + risposta.getDescrizioneEsitoNegativo();

			if (esitoAnpal.equalsIgnoreCase(ESITO_OK)) {
				serviceResponse.setAttribute("ESITO_APPUNTAMENTO_ANPAL_OK", lastEsitoAnpal);
			} else {
				serviceResponse.setAttribute("ESITO_APPUNTAMENTO_ANPAL_KO", lastEsitoAnpal);
			}

		} catch (Throwable e) {
			try {
				if (trans != null) {
					trans.rollBackTransaction();
				}
				_logger.error("Errore: " + e);
				serviceResponse.setAttribute("ESITO_APPUNTAMENTO_ANPAL_KO", "Esito invio ad ANPAL: Operazione fallita");
			} catch (Exception e1) {
				_logger.error("Errore: " + e1);
				serviceResponse.setAttribute("ESITO_APPUNTAMENTO_ANPAL_KO", "Esito invio ad ANPAL: Operazione fallita");
			}
		} finally {
			if (trans != null) {
				trans.closeConnTransaction();
			}
			if (conn != null) {
				conn.close();
			}
		}
	}

	private String createXmlRisposta(
			it.eng.sil.nsr.DataModels.InformationDelivery.xsd.NotificaAppuntamentoEsito._1_0.RispostaNotificaEsitoType res)
			throws DatatypeConfigurationException, ParseException, Exception {
		String xml = null;
		try {
			JAXBContext jaxbContext = JAXBContext.newInstance(
					it.eng.sil.nsr.DataModels.InformationDelivery.xsd.NotificaAppuntamentoEsito._1_0.RispostaNotificaEsitoType.class);
			Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
			StringWriter writer = new StringWriter();
			// format the XML output
			it.eng.sil.nsr.DataModels.InformationDelivery.xsd.NotificaAppuntamentoEsito._1_0.ObjectFactory obj = new it.eng.sil.nsr.DataModels.InformationDelivery.xsd.NotificaAppuntamentoEsito._1_0.ObjectFactory();
			jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, false);
			JAXBElement<it.eng.sil.nsr.DataModels.InformationDelivery.xsd.NotificaAppuntamentoEsito._1_0.RispostaNotificaEsitoType> root = obj
					.createRispostaNotificaEsito(res);
			jaxbMarshaller.marshal(root, writer);
			xml = writer.getBuffer().toString();
		} catch (JAXBException e) {
			_logger.error("Errore creazione output XML", e);
			throw new Exception(e);
		}
		return xml;
	}

}
