package it.eng.sil.module.pattoonline;

import java.io.File;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Connection;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Vector;

import javax.xml.rpc.holders.StringHolder;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.SessionContainer;
import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.configuration.ConfigSingleton;
import com.engiweb.framework.dbaccess.Utils;
import com.engiweb.framework.dbaccess.sql.DataConnection;
import com.engiweb.framework.util.QueryExecutor;
import com.engiweb.framework.util.QueryExecutorObject;

import it.eng.afExt.utils.MessageCodes;
import it.eng.afExt.utils.ReportOperationResult;
import it.eng.afExt.utils.StringUtils;
import it.eng.afExt.utils.TransactionQueryExecutor;
import it.eng.myportal.ws.pattoonline.AccettazionePattoType;
import it.eng.myportal.ws.pattoonline.EsitoTypeEsito;
import it.eng.myportal.ws.pattoonline.GestionePattoOnlineProxy;
import it.eng.myportal.ws.pattoonline.PattoPortaleType;
import it.eng.myportal.ws.pattoonline.PattoType;
import it.eng.sil.Values;
import it.eng.sil.coop.DataSourceJNDI;
import it.eng.sil.coop.endpoints.EndPointSingleton;
import it.eng.sil.module.AbstractSimpleModule;
import it.eng.sil.module.patto.bean.PattoOnLine;
import it.eng.sil.security.User;

public class PattoOnLineModule extends AbstractSimpleModule {

	private static final long serialVersionUID = 3061138757643286957L;

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(PattoOnLineModule.class.getName());

	private String className = this.getClass().getName();

	private RequestContainer reqContainer;	
	private Connection batchConnection;
	private QueryExecutorObject queryExecObj;
	private BigDecimal prgPattoLavoratore;


	@SuppressWarnings("unused")
	private File pattoOnline_schema = new File(ConfigSingleton.getRootPath()
			+ File.separator + "WEB-INF" + File.separator + "xsd"
			+ File.separator + "ptonline" + File.separator + "ptonline.xsd");

	public static final String END_POINT_NAME = "ptonline"; 
	private ResultSet pattoRS;

	public void service(SourceBean request, SourceBean response) throws Exception {

		ReportOperationResult reportOperation = new ReportOperationResult(this, response);
		disableMessageIdFail();
		disableMessageIdSuccess();

		SessionContainer sessionContainer = null;
		boolean notFromBatch = true;
		if(getReqContainer()==null){
			reqContainer = getRequestContainer();
			sessionContainer = getRequestContainer().getSessionContainer();
		}else{
			notFromBatch = false;
			sessionContainer = getReqContainer().getSessionContainer();
		}
		User user = (User) sessionContainer.getAttribute(User.USERID);
		BigDecimal userid = new BigDecimal(user.getCodut());

		DataConnection dataConnection = null;		
		Connection connection = null;		
		TransactionQueryExecutor transExec = null;

		BigDecimal numkLoPattoLavoratore  = null;

		String codiceFiscale = null;
		String codServiziAmministrativi = null;
		String numProtocollo = null;			
		String codProvinciaProv = null;
		String annoProtocollo = null;	
		String dataPatto = null;
		String dtmInvioPortale = null;
		String statoPtOnline = null;
		String dtmAccettazione = null;
		String tipoAccettazione = null;
		BigDecimal prgDocumento = null;
		byte[] documentoPatto = null;

		PattoOnLine pattoBean = new PattoOnLine();
		PattoType pattoType = new PattoType();
		boolean isInvio = false;
		boolean isReinvio = false;
		boolean isRichiesta = false;
		if(request.containsAttribute("operazionePtOnline")){
			isInvio = true;
			notFromBatch = true;
			//invio patto online al portale
			String strPrgPattoLav = (String )request.getAttribute("PRGPATTOLAVORATORE");
			prgPattoLavoratore = new BigDecimal(strPrgPattoLav);
			if(request.containsAttribute("controllaStato")){
				String checkstato =    (String) request.getAttribute("controllaStato");
				if(StringUtils.isFilledNoBlank(checkstato) && checkstato.equalsIgnoreCase("REINVIO")){
					isRichiesta = false;
					isInvio = false;
					isReinvio = true;
				}else if(StringUtils.isFilledNoBlank(checkstato) && checkstato.equalsIgnoreCase("STATO")){
					isRichiesta = true;
					isInvio = false;
					isReinvio = false;
				}
			}

		}else{
			prgPattoLavoratore = getPrgPattoLavoratore();
		}

		try {

			if(!notFromBatch){
				connection = getBatchConnection();
			}

			if(notFromBatch){
				SourceBean queryPatto = null;
				Object[] args = new Object[1];
				args[0]= prgPattoLavoratore;
				if(isInvio){
					//recupero i dati del patto da inviare
					queryPatto = (SourceBean) QueryExecutor.executeQuery("GET_PT_ONLINE_PER_INVIO", args, QueryExecutorObject.SELECT, Values.DB_SIL_DATI);
				}else if(isReinvio){
					//recupero i dati del patto da reinviare
					queryPatto  = (SourceBean) QueryExecutor.executeQuery("GET_PT_ONLINE_PER_REINVIO", args, QueryExecutorObject.SELECT, Values.DB_SIL_DATI);
				}else if(isRichiesta){
					//recupero i dati del patto da verificare
					queryPatto  = (SourceBean) QueryExecutor.executeQuery("GET_PT_ONLINE_PER_RICHIESTA", args, QueryExecutorObject.SELECT, Values.DB_SIL_DATI);
				}
				if(queryPatto!=null && queryPatto.containsAttribute("ROW")){
					numkLoPattoLavoratore = (BigDecimal)  queryPatto.getAttribute("ROW.numklopattolavoratore");
					codiceFiscale = (String) queryPatto.getAttribute("ROW.codiceFiscale");
					codServiziAmministrativi = (String) queryPatto.getAttribute("ROW.codServiziAmministrativi");
					numProtocollo = (String) queryPatto.getAttribute("ROW.numProtocollo");							
					codProvinciaProv = (String) queryPatto.getAttribute("ROW.codProvinciaProv");
					annoProtocollo = (String) queryPatto.getAttribute("ROW.annoProtocollo");		
					dataPatto = (String) queryPatto.getAttribute("ROW.dataPatto");
					prgDocumento = (BigDecimal) queryPatto.getAttribute("ROW.prgdocumento");
					if(!isInvio){
						dtmInvioPortale = (String) queryPatto.getAttribute("ROW.dataInvioPortale");
					}
					if(isReinvio){
						statoPtOnline = (String) queryPatto.getAttribute("ROW.codmonoaccettazione");
						dtmAccettazione = (String) queryPatto.getAttribute("ROW.dataAccettazione");
						tipoAccettazione = (String) queryPatto.getAttribute("ROW.STRTIPOACCETTAZIONE");
					}
					
				}else {
					Vector<String> params = new Vector<String>();
					params.add("nessun patto on line trovato");
					reportOperation.reportFailure(MessageCodes.Patto.ERR_INVIO_PT_ONLINE, "PattoOnLineModule","", params );
					return;
				}
			}else{
				//recupero i dati del patto da richiedere al portale
				pattoRS = pattoBean.getDatiPattoPerRichiesta(connection, prgPattoLavoratore);

				if (pattoRS.next()) {

					codiceFiscale = pattoRS.getString("codiceFiscale");
					codServiziAmministrativi = pattoRS.getString("codServiziAmministrativi");
					numProtocollo = pattoRS.getString("numProtocollo");						
					codProvinciaProv = pattoRS.getString("codProvinciaProv");
					annoProtocollo = pattoRS.getString("annoProtocollo");
					dataPatto = pattoRS.getString("dataPatto");
					dtmInvioPortale = pattoRS.getString("dataInvioPortale");
				} 
			}

			pattoType.setAnnoProtocollo(new BigInteger(annoProtocollo));
			pattoType.setCodiceFiscale(codiceFiscale);
			pattoType.setCodProvinciaProv(codProvinciaProv);
			pattoType.setCodServiziAmministrativi(codServiziAmministrativi);
			Date dataPattoDate=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(dataPatto);  
			pattoType.setDataPatto(dataPattoDate);
			pattoType.setNumProtocollo(numProtocollo);

			DataSourceJNDI dataSourceJndi = new DataSourceJNDI();
			String dataSourceJndiName = dataSourceJndi.getJndi();
			EndPointSingleton eps = EndPointSingleton.getInstance(dataSourceJndiName);
			//recupera endpoint servizio
			String endpointServizio = eps.getUrl(END_POINT_NAME);

			if (endpointServizio != null) {
				GestionePattoOnlineProxy proxy = new GestionePattoOnlineProxy();
				proxy.setEndpoint(endpointServizio);

				if(notFromBatch){
					transExec = new TransactionQueryExecutor(getPool());
					enableTransactions(transExec);
					transExec.initTransaction();
					dataConnection = transExec.getDataConnection();
					connection = dataConnection.getInternalConnection();
				}

				if(isInvio || isReinvio){
					Boolean inserimentoStorico = true;
					Boolean updatePatto = false;
					if(isReinvio)
					{
						Object[] paramsStorico = new Object[7];

						paramsStorico[0] = dtmAccettazione;
						paramsStorico[1] = codServiziAmministrativi;
						paramsStorico[2] = statoPtOnline;
						paramsStorico[3] = userid;
						paramsStorico[4] = dtmInvioPortale;
						paramsStorico[5] = prgPattoLavoratore;
						paramsStorico[6] = tipoAccettazione;
						
						inserimentoStorico = (Boolean) transExec.executeQuery("INSERT_PTONLINE_STORICO",
								paramsStorico, "INSERT");
						
						if (inserimentoStorico == null || !inserimentoStorico.booleanValue()) {
							reportOperation.reportFailure(MessageCodes.Patto.ERR_STORICO_PT_ONLINE);
							transExec.rollBackTransaction();
							return;
						}
						else {
							_logger.debug("Inserimento in AM_PT_ONLINE_STORICO effettuato con successo");
						}
					}
					_logger.info("CHIAMATA invoca il WS PattoOnline operation InvioPatto per presa visione, codiceFiscale = " +codiceFiscale );
				
					
					StringHolder esitoInvio = new StringHolder();
					StringHolder descrizioneEsitoInvio = new StringHolder();

					documentoPatto = pattoBean.getBlobDocumento(connection, prgDocumento);

					proxy.invioPatto(pattoType, documentoPatto,esitoInvio, descrizioneEsitoInvio);

					if(esitoInvio.value.equalsIgnoreCase(EsitoTypeEsito.OK.getValue())){

						String queryName = "UPDATE_PTONLINE_DA_ACCETTARE";
						
						if(isReinvio){
							queryName = "UPDATE_PTONLINE_REINVIATO";
						}

						Object[] paramsUpdate = new Object[3];

						paramsUpdate[0] = numkLoPattoLavoratore;
						paramsUpdate[1] = userid;
						paramsUpdate[2] = prgPattoLavoratore;
						
						updatePatto = (Boolean) transExec.executeQuery(queryName,paramsUpdate, "UPDATE");

						if (updatePatto == null || !updatePatto.booleanValue()) {
							reportOperation.reportFailure(MessageCodes.General.OPERATION_FAIL);
							transExec.rollBackTransaction();
						}
						else {
							transExec.commitTransaction();
							_logger.debug("Invio patto per presa visione effettuato con successo");
							reportOperation.reportSuccess(MessageCodes.General.OPERATION_SUCCESS);
						}
						return;

					}else{
						transExec.rollBackTransaction();
						Vector<String> params = new Vector<String>();
						params.add(descrizioneEsitoInvio.value);
						reportOperation.reportFailure(MessageCodes.Patto.ERR_INVIO_PT_ONLINE, "PattoOnLineModule","", params );
						_logger.debug("Invio patto per presa visione fallito: " + descrizioneEsitoInvio.value);
						return;
					}

				}else{
					/*	String inputXML = pattoBean.requestToXML(pattoType) ;

					//valida la request rispetto allo schema xsd
					boolean inputXMLIsValid = XmlUtils.isXmlValid(inputXML, pattoOnline_schema);
					if(!inputXMLIsValid){
						_logger.error("RichiestaPatto on line: Validazione fallita xml request");
						return;
					} 
					String outputXML = pattoBean.responseToXML(richiestaPatto) ;

					boolean outputXmlIsValid = XmlUtils.isXmlValid(outputXML, pattoOnline_schema);
					if (!outputXmlIsValid) {
						_logger.error("RichiestaPatto on line: Validazione fallita xml response");
						return;
					}*/
					_logger.info("CHIAMATA invoca il WS PattoOnline operation RequestPatto per verificare dato patto è stato accettato per presa visione, codiceFiscale = " +codiceFiscale );

					PattoPortaleType richiestaPatto = proxy.richiestaPatto(pattoType);				
					AccettazionePattoType accettazionePatto = richiestaPatto.getAccettazionePatto();

					if( accettazionePatto != null && accettazionePatto.getDtmAccettazione() != null  ) {
						//ho i dati del patto accettato
						int result = pattoBean.aggiornaAccettazionePatto( connection , richiestaPatto, PattoOnLine.ACCETTATO);
						if(result==0){
							_logger.info("Accettazione patto on line avvenuta con successo. Codice fiscale = " +codiceFiscale  );

							if(notFromBatch){
								transExec.commitTransaction();
								reportOperation.reportSuccess(MessageCodes.Patto.ACCETTAZIONE_PT_ONLINE);
								return;
							}else{
								response.setAttribute("ESITO", new String("OK"));
								return;

							}
						}else{
							if(	notFromBatch){
								transExec.rollBackTransaction();
								reportOperation.reportFailure(MessageCodes.General.OPERATION_FAIL);
								return;
							}else{
								response.setAttribute("ESITO", new String("KO"));
								return;
							}
						}

					}else if( accettazionePatto == null || accettazionePatto.getDtmAccettazione() == null  ) {

						Date dataInvioPortale=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(dtmInvioPortale); 
						long diff = Calendar.getInstance().getTime().getTime() - dataInvioPortale.getTime();
						long diffHours = diff / (60 * 60 * 1000) ;

						int differenzaOre = pattoBean.getConfigOreScadenzaAccettazione(connection);

						if(diffHours > differenzaOre) {
							//i termini per l'accettazione del patto sono scaduti si chiude il patto
							int result = pattoBean.aggiornaAccettazionePatto(connection , richiestaPatto, PattoOnLine.SCADENZA_TERMINI);		
							if(result==0){
								_logger.info("I termini per l'accettazione del patto sono scaduti, patto chiuso. Codice fiscale = " +codiceFiscale  );

								if(	notFromBatch){
									transExec.commitTransaction();
									reportOperation.reportSuccess(MessageCodes.Patto.SCADENZA_TERMINI_PT_ONLINE);
									return;
								}else{
									response.setAttribute("ESITO", new String("OK"));
									return;
								}
							}else{
								if(	notFromBatch){
									transExec.rollBackTransaction();
									reportOperation.reportFailure(MessageCodes.General.OPERATION_FAIL);
									return;
								}else{
									response.setAttribute("ESITO", new String("KO"));
									return;
								}
							}
						}else{
							// altrimenti non fa nulla
							_logger.info("I termini per l'accettazione del patto non sono scaduti. Codice fiscale = " +codiceFiscale  );

							if(	notFromBatch){
								transExec.commitTransaction();
								reportOperation.reportSuccess(MessageCodes.Patto.NO_SCADENZA_TERMINI_PT_ONLINE);
								return;
							}else{
								response.setAttribute("ESITO", new String("OK"));
								return;
							}
						}
					}

				}
			}
			else {
				if(notFromBatch){
					reportOperation.reportFailure(MessageCodes.Webservices.WS_ERRORE_RECUPERO_URL);
				}else {
					response.setAttribute("ESITO", new String("KO"));
				}
			}

		} catch (Exception ex) {
			if(notFromBatch){
				if (transExec!=null) {
					transExec.rollBackTransaction();  
				}
				reportOperation.reportFailure(MessageCodes.Webservices.WS_ERRORE_INVOCAZIONE);
			}else {
				response.setAttribute("ESITO", new String("KO"));
			}
			it.eng.sil.util.TraceWrapper.fatal(_logger, className + ": errore chiamata WS END_POINT_NAME = " + END_POINT_NAME, ex);
		}
		finally  {
			if(notFromBatch){
				try{
					Utils.releaseResources(dataConnection, null, null);
				}catch(Exception e){
					
				}
				transExec = null;
			} 
		}

	}

	public RequestContainer getReqContainer() {
		return reqContainer;
	}

	public void setReqContainer(RequestContainer reqContainer) {
		this.reqContainer = reqContainer;
	}

	public Connection getBatchConnection() {
		return batchConnection;
	}

	public void setBatchConnection(Connection batchConnection) {
		this.batchConnection = batchConnection;
	}

	public QueryExecutorObject getQueryExecObj() {
		return queryExecObj;
	}

	public void setQueryExecObj(QueryExecutorObject queryExecObj) {
		this.queryExecObj = queryExecObj;
	}

	public BigDecimal getPrgPattoLavoratore() {
		return prgPattoLavoratore;
	}

	public void setPrgPattoLavoratore(
			BigDecimal prgPattoLavoratoreFromBatch) {
		this.prgPattoLavoratore = prgPattoLavoratoreFromBatch;
	}
}