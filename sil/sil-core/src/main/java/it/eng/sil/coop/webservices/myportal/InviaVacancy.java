/*
 * Created on Jun 24, 2005
 *
 */
package it.eng.sil.coop.webservices.myportal;

import it.eng.afExt.utils.MessageCodes;
import it.eng.afExt.utils.ReportOperationResult;
import it.eng.afExt.utils.StringUtils;
import it.eng.myportal.ws.RicezioneVacancyProxy;
import it.eng.myportal.ws.RicezioneVacancy_PortType;
import it.eng.sil.Values;
import it.eng.sil.coop.DataSourceJNDI;
import it.eng.sil.coop.endpoints.EndPointSingleton;
import it.eng.sil.coop.webservices.myportal.vacancy.risposta.InserisciVacancy;
import it.eng.sil.module.AbstractSimpleModule;
import it.eng.sil.security.User;
import it.eng.sil.util.DBAccess;
import it.eng.sil.util.InfoRegioneSingleton;
import it.eng.sil.util.xml.FieldFormatException;
import it.eng.sil.util.xml.MandatoryFieldException;

import java.io.File;
import java.io.StringReader;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.base.SourceBeanException;
import com.engiweb.framework.configuration.ConfigSingleton;
import com.engiweb.framework.dbaccess.Utils;
import com.engiweb.framework.dbaccess.sql.DataConnection;
import com.engiweb.framework.dbaccess.sql.DataField;
import com.engiweb.framework.dbaccess.sql.mappers.OracleSQLMapper;
import com.engiweb.framework.error.EMFErrorSeverity;
import com.engiweb.framework.error.EMFInternalError;
import com.engiweb.framework.error.EMFUserError;
import com.engiweb.framework.util.QueryExecutorObject;

/**
 * Modulo che esegue la cancellazione e accorpamento del lavoratore. Viene
 * chiamata una stored procedure che provvede a cio'.
 * 
 * @author savino,pegoraro
 */
public class InviaVacancy extends AbstractSimpleModule {

	private static final long serialVersionUID = 13476689L;

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(InviaVacancy.class.getName());
	
	private final static String XSD_ACC_WS = "myportal_ricercapersonale.xsd";
	private static final String INPUT_XSD_VACANCY_SIL = ConfigSingleton.getRootPath() + File.separator + "WEB-INF" + File.separator
			+ "xsd" + File.separator + "myportal" + File.separator + "myportal_ricercapersonale_TN.xsd";
	
	private static final String serviceName = "'SIL_MYPORTAL'";
	
	private static final String WS_LOGON = "SELECT prgws, struserid, "
			+ "strpassword AS cln_pwd " + "FROM   ts_ws "
			+ " WHERE  codservizio = " + serviceName;
	
	
	public void service(SourceBean serviceRequest, SourceBean serviceResponse) throws Exception {
		int idSuccess = this.disableMessageIdSuccess();
		
		String xmlGenerato = "";
		
		User objUser = (User) RequestContainer.getRequestContainer().getSessionContainer().getAttribute(User.USERID);		

		String prgRichiesta = serviceRequest.getAttribute("PRGRICHIESTAAZ").toString();
		String prgAlternativa = serviceRequest.getAttribute("PRGALTERNATIVA").toString();		
		
		ReportOperationResult reportOperation = new ReportOperationResult(this, serviceResponse);
		
		try {
			xmlGenerato = buildRichiestaDiPersonaleMyPortal(prgRichiesta,prgAlternativa);			
			if (StringUtils.isFilledNoBlank(xmlGenerato)) {
				serviceResponse.setAttribute("xmlGenerato", xmlGenerato);
				reportOperation.reportSuccess(idSuccess);
			}
		} catch (EMFUserError e) {
			reportOperation.reportFailure(e, "buildRichiestaDiPersonaleMyPortal", e.getDescription());
			_logger.error(e);
		} catch (MandatoryFieldException e) {
			reportOperation.reportFailure(e, "buildRichiestaDiPersonaleMyPortal", e.getExceptionMessage());
			_logger.error(e.getExceptionMessage());
		} catch (FieldFormatException e) {
			reportOperation.reportFailure(e, "buildRichiestaDiPersonaleMyPortal", e.getExceptionMessage());
			_logger.error(e.getExceptionMessage());		
		} catch (SourceBeanException e) {
			reportOperation.reportFailure(e, "buildRichiestaDiPersonaleMyPortal", "Errore interno");
			_logger.error(e);
		}
					
		if (!sendWSMyPortal(xmlGenerato)){		
			_logger.error(MessageCodes.AccorpamentoLavoratore.ACCORPAMENTO_WARN_WS);
		}
			
	}
	
	
	public void batchServiceAvvio(String prgRichiesta, Connection conn) {
		
		String xmlGenerato = "";
		CallableStatement commandXML = null;
		
		try {			
			String statement = "{ call ? := PG_PORTALE.GETXMLVACANCY (?) }";
			
			commandXML = conn.prepareCall(statement);
			
			commandXML.registerOutParameter(1, Types.CLOB);
			commandXML.setBigDecimal(2, new BigDecimal(prgRichiesta));
			commandXML.registerOutParameter(2,Types.BIGINT);
			
			commandXML.executeQuery();
			
			Clob result = commandXML.getClob(1);
			  
		    if (result != null) {  
		    	if (result.length() > 0) {
		    		xmlGenerato = result.getSubString(1, (int)result.length());
		    		if (!sendWSMyPortal(xmlGenerato, conn)) {
						_logger.error("Errore invio vacancy al servizio portale. Progressivo:" + prgRichiesta);
					}
		    		else {
		    			_logger.info("Chiamata portale servizi avvenuta con successo", null);
		    		}
		    	}  
		    }
		    else {
		    	_logger.error("Errore recupero vacancy con progressivo:" + prgRichiesta);
		    }
		} 
		
		catch (SQLException eSQL) {			
			_logger.error(eSQL.getMessage());		
		}
	}
	
	public String inviaVacancySil(String prgRichiesta, Connection conn) throws SQLException, JAXBException {
		String xmlGenerato = "";
		CallableStatement commandXML = null;
		String esito = null;
		InserisciVacancy res = null;
		
		try {
			String statement = "{ call ? := PG_PORTALE.GETXMLVACANCYSIL (?,?) }";
				
			commandXML = conn.prepareCall(statement);
			
			commandXML.registerOutParameter(1, Types.CLOB);
			commandXML.setBigDecimal(2, new BigDecimal(prgRichiesta));
			commandXML.registerOutParameter(2,Types.BIGINT);
			commandXML.setString(3, "N");
			commandXML.registerOutParameter(3,Types.VARCHAR);
			
			commandXML.executeQuery();
			
			Clob result = commandXML.getClob(1);
			  
		    if (result != null) {  
		    	if (result.length() > 0) {
		    		xmlGenerato = result.getSubString(1, (int)result.length());
		    		/* validazione xsd dell'input */
					if (!validazioneXml(xmlGenerato, INPUT_XSD_VACANCY_SIL)) {
						_logger.error("Errore recupero vacancy con progressivo:" + prgRichiesta);
				    	res = new InserisciVacancy();
				    	res.setEsito("-500");
						res.setDettaglio("Errore validazione tracciato vacancy da inviare");
						esito = convertInserisciVacancyToString(res);
					}
					else {
						esito = sendMyPortal(xmlGenerato, conn);
					}
		    	}
		    	else {
		    		_logger.error("Errore recupero vacancy con progressivo:" + prgRichiesta);
		    		res = new InserisciVacancy();
			    	res.setEsito("-100");
					res.setDettaglio("Errore recupero tracciato vacancy da inviare");
					esito = convertInserisciVacancyToString(res);
		    	}
		    }
		    else {
		    	_logger.error("Errore recupero vacancy con progressivo:" + prgRichiesta);
		    	res = new InserisciVacancy();
		    	res.setEsito("-100");
				res.setDettaglio("Errore recupero tracciato vacancy da inviare");
				esito = convertInserisciVacancyToString(res);
		    }
		    return esito;
		}
		catch (Exception e) {
			_logger.error("Errore recupero vacancy con progressivo:" + prgRichiesta + " dettaglio:" + e.getMessage());
			res = new InserisciVacancy();
			res.setEsito("-100");
			res.setDettaglio("Errore recupero tracciato vacancy da inviare");
			esito = convertInserisciVacancyToString(res);
			return esito;
		}
		finally {
			if (commandXML != null) {
				commandXML.close();
			}
		}
	}
	
	public void batchService(String prgRichiesta, String prgAlternativa, DataConnection dc) {
		
		String xmlGenerato = "";
		
		try {
			xmlGenerato = buildRichiestaDiPersonaleMyPortal(prgRichiesta,prgAlternativa, dc);	
			
			if (!sendWSMyPortal(xmlGenerato, dc)){		
				_logger.error(MessageCodes.AccorpamentoLavoratore.ACCORPAMENTO_WARN_WS);
			}
			
		} catch (EMFUserError e) {			
			_logger.error(e);
		} catch (MandatoryFieldException e) {			
			_logger.error(e.getExceptionMessage());
		} catch (FieldFormatException e) {			
			_logger.error(e.getExceptionMessage());		
		} 

	}
	
	private String buildRichiestaDiPersonaleMyPortal(String prgRichiesta,String prgAlternativa) throws EMFUserError, MandatoryFieldException, FieldFormatException {
				
		BigDecimal bdRichiesta;
		BigDecimal bdAlternativa;
		try {
		 bdRichiesta = new BigDecimal(prgRichiesta);
		 bdAlternativa = new BigDecimal(prgAlternativa);
		} catch (NumberFormatException ex) {
			throw new EMFUserError(EMFErrorSeverity.ERROR,MessageCodes.ClicLavoro.CODE_INPUT_ERRATO,"I valori passati prgRichiesta e prgAlternativa non sono numerici: prgRichiesta:"+prgRichiesta + ", prgAlternativa:" + prgAlternativa);
		}
		MyPortalRicercaPersonaleData risposta = new MyPortalRicercaPersonaleData(bdRichiesta,bdAlternativa);
		risposta.costruisci();

		return risposta.generaXML();
	}
	
	
	private String buildRichiestaDiPersonaleMyPortal(String prgRichiesta,String prgAlternativa, DataConnection dc) throws EMFUserError, MandatoryFieldException, FieldFormatException {
		
		BigDecimal bdRichiesta;
		BigDecimal bdAlternativa;
		try {
		 bdRichiesta = new BigDecimal(prgRichiesta);
		 bdAlternativa = new BigDecimal(prgAlternativa);
		} catch (NumberFormatException ex) {
			throw new EMFUserError(EMFErrorSeverity.ERROR,MessageCodes.ClicLavoro.CODE_INPUT_ERRATO,"I valori passati prgRichiesta e prgAlternativa non sono numerici: prgRichiesta:"+prgRichiesta + ", prgAlternativa:" + prgAlternativa);
		}
		MyPortalRicercaPersonaleData risposta = new MyPortalRicercaPersonaleData(bdRichiesta,bdAlternativa);
		risposta.costruisci(dc);

		return risposta.generaXML();
	}
	
	/**
	 * Invia l'xml al web service	
	 * @return true se tutto va a buon fine
	 */
	private final boolean sendWSMyPortal(String xmlToSend) {
		// recupero le credenziali per il WS
		DataConnection dc = null;
		QueryExecutorObject qExec = null;
		String username = "", password = "";
		try {
			qExec = getQueryExecutorObject();
			List<DataField> inPars = new ArrayList<DataField>();
			dc = qExec.getDataConnection();			
			qExec.setInputParameters(inPars);
			qExec.setType(QueryExecutorObject.SELECT);
			qExec.setStatement(WS_LOGON);
			Object result = qExec.exec();
			if (result instanceof SourceBean) {
				SourceBean logon = (SourceBean) result;
				password = (String) logon.getAttribute("ROW.cln_pwd");
				username = (String) logon.getAttribute("ROW.struserid");
			}
			if (password == null || username == null) {
				throw new Exception("Record non trovato");
			}
		} 
		catch (Exception e) {
			_logger.error("Impossibile trovare in TS_WS username per il servizio:" + serviceName, e);
			return false;
		}
		finally {
			if (qExec != null){
				Utils.releaseResources(qExec.getDataConnection(), null, null);
			}
		}
				
		RicezioneVacancyProxy proxy = new RicezioneVacancyProxy();

		DataSourceJNDI dataSourceJndi = new DataSourceJNDI();
		String dataSourceJndiName = dataSourceJndi.getJndi();
		EndPointSingleton eps = EndPointSingleton.getInstance(dataSourceJndiName);

		/* Cerco l'endpoint */
		InfoRegioneSingleton reg = InfoRegioneSingleton.getInstance();		
		String endPoint = eps.getUrl("InvioVacancyMyPortal");
	
		/* Se non trovo l'endpoint, tutto ok, magari siamo in umbria */
		if (endPoint==null ){
			_logger.error("Nessun EndPoint definito per la comunicazione a MyPortal");
			return true;
		}
		
		proxy.setEndpoint(endPoint);

		RicezioneVacancy_PortType risposta = proxy.getRicezioneVacancy_PortType();
		
		try {		
			String returned = risposta.inserisciVacancy(username, password, xmlToSend);
			_logger.debug("Valore tornato dal WS myportal: "+returned);
			
			if (returned.startsWith("-")){
				//questo invia una mail
				_logger.error("Valore negativo tornato dal WS myportal: "+returned+"\n\n"+xmlToSend);
				return false;
			}
			
		} catch (Exception e) {
			//questo invia una mail
			_logger.error("Errore nel web service di myportal", e);
			return false;
		}
		return true;
	}
	
	/**
	 * Invia l'xml al web service	
	 * @return true se tutto va a buon fine
	 */
	private final boolean sendWSMyPortal(String xmlToSend, DataConnection dc) {
		// recupero le credenziali per il WS		
		String username = "", password = "";
		Object[] inputParameters = new Object[0];
		try {
			
			DBAccess dbAcc = new DBAccess();    						
			Object result = dbAcc.selectToSourceBean(WS_LOGON, dc);
			if (result instanceof SourceBean) {
				SourceBean logon = (SourceBean) result;
				password = (String) logon.getAttribute("ROW.cln_pwd");
				username = (String) logon.getAttribute("ROW.struserid");
			}
			if (password == null || username == null) {
				throw new Exception("Record non trovato");
			}
		} 
		catch (Exception e) {
			_logger.error("Impossibile trovare in TS_WS username per il servizio:" + serviceName, e);
			return false;
		} 		
		
		
		RicezioneVacancyProxy proxy = new RicezioneVacancyProxy();

		DataSourceJNDI dataSourceJndi = new DataSourceJNDI();
		String dataSourceJndiName = dataSourceJndi.getJndi();
		EndPointSingleton eps = EndPointSingleton.getInstance(dataSourceJndiName);

		/* Cerco l'endpoint */
		InfoRegioneSingleton reg = InfoRegioneSingleton.getInstance();		
		String endPoint = eps.getUrl("InvioVacancyMyPortal");
	
		/* Se non trovo l'endpoint, tutto ok, magari siamo in umbria */
		if (endPoint==null ){
			_logger.error("Nessun EndPoint definito per la comunicazione a MyPortal");
			return true;
		}
		
		proxy.setEndpoint(endPoint);

		RicezioneVacancy_PortType risposta = proxy.getRicezioneVacancy_PortType();
		
		try {		
			String returned = risposta.inserisciVacancy(username, password, xmlToSend);
			_logger.debug("Valore tornato dal WS myportal: "+returned);
			
			if (returned.startsWith("-")){				
				_logger.error("Valore negativo tornato dal WS myportal: "+returned+"\n\n"+xmlToSend);
				return false;
			}
			
		} catch (Exception e) {		
			_logger.error("Errore nel web service di myportal", e);
			return false;
		}
		return true;
	}
	
	private final boolean sendWSMyPortal(String xmlToSend, Connection conn) {
		//recupero le credenziali per il WS		
		String username = "", password = "";
		PreparedStatement ps = null;
		PreparedStatement psUrl = null;
		String endPoint = null;
		ResultSet rs = null;
		ResultSet rsUrl = null;
		
		try {
			ps = conn.prepareStatement(WS_LOGON);
			rs = ps.executeQuery();
			
			if (rs.next()) {
				password = rs.getString("cln_pwd");
				username = rs.getString("struserid");
			}
			rs.close();
			ps.close();
			
			if (password == null || username == null) {
				_logger.info("Username o Password null sendWSMyPortal", null);
				throw new Exception("Record non trovato");
			}
		} 
		catch (Exception e) {
			_logger.error("Impossibile trovare in TS_WS username per il servizio:" + serviceName, e);
			return false;
		} 		
		
		RicezioneVacancyProxy proxy = new RicezioneVacancyProxy();
		
		try {
			String statementUrl = "SELECT strName, strUrl, codProvincia, flgpoloattivo FROM TS_ENDPOINT WHERE strName = 'InvioVacancyMyPortal'";
			psUrl = conn.prepareStatement(statementUrl);
			
			rsUrl = psUrl.executeQuery();
			
			if (rsUrl.next()) {
				endPoint = rsUrl.getString("strUrl");
			}
			rsUrl.close();
			psUrl.close();
		
			/* Se non trovo l'endpoint, tutto ok, magari siamo in umbria */
			if (endPoint==null ){
				_logger.error("Nessun EndPoint definito per la comunicazione a MyPortal");
				return true;
			}
			
			proxy.setEndpoint(endPoint);
	
			RicezioneVacancy_PortType risposta = proxy.getRicezioneVacancy_PortType();
				
			String returned = risposta.inserisciVacancy(username, password, xmlToSend);
			_logger.debug("Valore tornato dal WS myportal: "+returned);
			
			if (returned.startsWith("-")){				
				_logger.error("Valore negativo tornato dal WS myportal: "+returned+"\n\n"+xmlToSend);
				return false;
			}
				
		} catch (Exception e) {		
			_logger.error("Errore nel web service di myportal", e);
			return false;
		}
		return true;
	}
	
	private final String sendMyPortal(String xmlToSend, Connection conn) throws SQLException, JAXBException {
		//recupero le credenziali per il WS		
		String username = null, password = null;
		PreparedStatement ps = null;
		PreparedStatement psUrl = null;
		String endPoint = null;
		ResultSet rs = null;
		ResultSet rsUrl = null;
		InserisciVacancy res = null;
		String esito = null;
		
		try {
			ps = conn.prepareStatement(WS_LOGON);
			rs = ps.executeQuery();
			
			if (rs.next()) {
				password = rs.getString("cln_pwd");
				username = rs.getString("struserid");
			}
			if (password == null || username == null) {
				_logger.info("Username o Password null sendMyPortal", null);
				throw new Exception("Record non trovato");
			}
		} 
		catch (Exception e) {
			_logger.error("Impossibile trovare in TS_WS username per il servizio:" + serviceName, e);
			res = new InserisciVacancy();
	    	res.setEsito("-200");
			res.setDettaglio("Errore recupero Username o Password per inviare la vacancy");
			esito = convertInserisciVacancyToString(res);
			return esito;
		}
		finally {
			if (rs != null) {
				rs.close();
			}
			if (ps != null) {
				ps.close();
			}
		}
		
		String returned = null;
		try {
			String statementUrl = "SELECT strName, strUrl, codProvincia, flgpoloattivo FROM TS_ENDPOINT WHERE strName = 'InvioVacancyMyPortal'";
			psUrl = conn.prepareStatement(statementUrl);
			
			rsUrl = psUrl.executeQuery();
			
			if (rsUrl.next()) {
				endPoint = rsUrl.getString("strUrl");
			}
		
			/* Se non trovo l'endpoint, tutto ok, magari siamo in umbria */
			if (endPoint==null ){
				_logger.error("Nessun EndPoint definito per la comunicazione a MyPortal");
				res = new InserisciVacancy();
		    	res.setEsito("-300");
				res.setDettaglio("Nessun EndPoint definito per la comunicazione a MyPortal");
				esito = convertInserisciVacancyToString(res);
				return esito;
			}
			
			RicezioneVacancyProxy proxy = new RicezioneVacancyProxy();
			proxy.setEndpoint(endPoint);
	
			RicezioneVacancy_PortType risposta = proxy.getRicezioneVacancy_PortType();
			
			returned = risposta.inserisciVacancySil(username, password, xmlToSend);			
			
			_logger.debug("Valore tornato dal WS myportal: "+returned);
			return returned;
		} catch (Exception e) {		
			_logger.error("Errore nel web service di myportal", e);
			res = new InserisciVacancy();
	    	res.setEsito("-400");
			res.setDettaglio("Errore nel web service di myportal");
			esito = convertInserisciVacancyToString(res);
			return esito;
		}
		finally {
			if (rsUrl != null) {
				rsUrl.close();
			}
			if (psUrl != null) {
				psUrl.close();
			}
		}
	}
	
	private final QueryExecutorObject getQueryExecutorObject() {
		InitialContext ctx;
		try {
			ctx = new InitialContext();
			return getQueryExecutorObject(ctx);
		} catch (Exception e) {
			_logger.error("Errore nel web service di myportal", e);
			return null;
		}
	}

	private final QueryExecutorObject getQueryExecutorObject(InitialContext ctx)
			throws NamingException, SQLException, EMFInternalError {
		Object objs = ctx.lookup(Values.JDBC_JNDI_NAME);
		DataConnection dc = null;
		QueryExecutorObject qExec;
		if (objs instanceof DataSource) {
			DataSource ds = (DataSource) objs;
			Connection conn = ds.getConnection();
			dc = new DataConnection(conn, "2", new OracleSQLMapper());
			qExec = getQueryExecutorObject(dc);
		} else {
			//_logger.error("Impossibile ottenere una connessione");
			throw new SQLException();
		}
		return qExec;
	}

	private static QueryExecutorObject getQueryExecutorObject(DataConnection dc) {
			_logger.debug("getQueryExecutorObject(DataConnection) - start");

		QueryExecutorObject qExec = new QueryExecutorObject();

		qExec.setRequestContainer(null);
		qExec.setResponseContainer(null);
		qExec.setDataConnection(dc);
		qExec.setType(QueryExecutorObject.SELECT);
		qExec.setTransactional(true);
		qExec.setDontForgetException(false);

		_logger.debug("getQueryExecutorObject(DataConnection) - end");
		return qExec;
	}
	
	/* Valida l'xml passato in input con l'xsd passato come secondo parametro */
	private static boolean validazioneXml(String xml, String xsdPath) {
		try {
			SchemaFactory factory = SchemaFactory.newInstance("http://www.w3.org/2001/XMLSchema");

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
	
	public InserisciVacancy convertRispostaXMLToObject(String xml) throws JAXBException {
		JAXBContext jaxbContext;
		InserisciVacancy resVacancy = null;
		
		jaxbContext = JAXBContext.newInstance(it.eng.sil.coop.webservices.myportal.vacancy.risposta.ObjectFactory.class);
		Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();			
		JAXBElement<InserisciVacancy> root = (JAXBElement<InserisciVacancy>) jaxbUnmarshaller.unmarshal(new StringReader(xml));
		resVacancy = root.getValue();
					
		return resVacancy;
	}
	
	public InserisciVacancy convertRispostaXMLToObjectNew(String xml) throws JAXBException {
		JAXBContext jaxbContext;
		InserisciVacancy resVacancy = null;	
		jaxbContext = JAXBContext.newInstance(it.eng.sil.coop.webservices.myportal.vacancy.risposta.ObjectFactory.class);
		Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();	
		resVacancy = (InserisciVacancy)  jaxbUnmarshaller.unmarshal(new StringReader(xml));			
		
		return resVacancy;
	}
	public String convertInserisciVacancyToString(InserisciVacancy resVacancy) throws JAXBException {
		JAXBContext jc = JAXBContext.newInstance(InserisciVacancy.class);
		Marshaller marshaller = jc.createMarshaller();		
		StringWriter writer = new StringWriter();
		marshaller.marshal(resVacancy, writer);
		String xmlRisposta = writer.getBuffer().toString();
		return xmlRisposta;
	}
}