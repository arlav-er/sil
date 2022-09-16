package it.eng.sil.module.anag;

import java.io.File;
import java.io.StringReader;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.rmi.RemoteException;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.datatype.XMLGregorianCalendar;
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

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.configuration.ConfigSingleton;
import com.engiweb.framework.dbaccess.SQLStatements;
import com.engiweb.framework.dbaccess.sql.DataConnection;
import com.engiweb.framework.dbaccess.sql.DataField;
import com.engiweb.framework.error.EMFInternalError;
import com.engiweb.framework.message.MessageBundle;
import com.engiweb.framework.util.QueryExecutor;
import com.engiweb.framework.util.QueryExecutorObject;
import com.jamonapi.Monitor;
import com.jamonapi.MonitorFactory;

import it.eng.afExt.utils.DateUtils;
import it.eng.afExt.utils.MessageCodes;
import it.eng.afExt.utils.MovimentoCollegatoErroreExcpetion;
import it.eng.afExt.utils.ReportOperationResult;
import it.eng.afExt.utils.StringUtils;
import it.eng.afExt.utils.TransactionQueryExecutor;
import it.eng.sil.Values;
import it.eng.sil.coop.DataSourceJNDI;
import it.eng.sil.coop.endpoints.EndPointSingleton;
import it.eng.sil.module.AbstractSimpleModule;
import it.eng.sil.pojo.yg.richiestaSAP.IDSAP;
import it.eng.sil.pojo.yg.sap.Datiinvio;
import it.eng.sil.pojo.yg.sap.Datipersonali;
import it.eng.sil.pojo.yg.sap.LavoratoreType;
import it.eng.sil.pojo.yg.sap.PoliticheAttive;
import it.eng.sil.pojo.yg.sap.due.PoliticheAttiveLst;
import it.eng.sil.pojo.yg.verificaSAP.VerificaSAP;
import it.eng.sil.security.User;
import it.eng.sil.util.xml.XMLValidator;
import it.gov.lavoro.servizi.servizicoapSap.ServizicoapWSProxy;
import it.gov.lavoro.servizi.servizicoapSap.types.Risposta_invioSAP_TypeEsito;
import it.gov.lavoro.servizi.servizicoapSap.types.holders.Risposta_invioSAP_TypeEsitoHolder;
import oracle.jdbc.OracleTypes;

public class GestioneInviaSapYG extends AbstractSimpleModule {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(GestioneInviaSapYG.class.getName());

	private static final String INPUT_XSD = ConfigSingleton.getRootPath() + File.separator + "WEB-INF" + File.separator
			+ "xsd" + File.separator + "sap" + File.separator + "Rev008_SAP.xsd";
	private static final String INPUT_XSD_SAP2 = ConfigSingleton.getRootPath() + File.separator + "WEB-INF"
			+ File.separator + "xsd" + File.separator + "sap" + File.separator + "Rev009_SAP.xsd";
	public static final String END_POINT_NAME_INVIOSAP = "InvioSAP";
	public static final String END_POINT_NAME_VERIFICASAP = "VerificaEsistenzaSAP";
	public static final String END_POINT_NAME_RICHIESTASAP = "InvioRichiestaSAP";

	public static final String VARIAZIONE_PRIMO_INVIO = "01";
	public static final String VARIAZIONE_AGGIORNAMENTO = "02";
	public static final String STATO_SAP_ATTIVA = "01";
	public static final String STATO_SAP_PERDITATITOLARIETA = "04";

	/* Per Batch Invio Automatico SAP */
	private RequestContainer reqContainer;
	private String flgInvioSap;
	private String noteErrori;
	private boolean notOverrideNote;
	private BigDecimal prgEstrazioneSap;
	private SourceBean sbAnLavoratore;
	private SourceBean sbCpiLavoratore;
	private SourceBean sbSpLavoratore;
	private SourceBean sbCpiMinProvincia;
	/* source bean per query catene movimenti */
	private SourceBean sbMovimentiRipetuti;
	private SourceBean sbMovimentiSuccessivi;
	private SourceBean sbMovimentiPrecedenti;
	private SourceBean sbMovimentiSuccApprofondita;
	private SourceBean sbMovimentiPrecApprofondita;
	private SourceBean sbMovimentiNoSuccPuntatoPrec;
	private SourceBean sbMovimentiNoPrecPuntatoSucc;

	private Connection batchConnection;
	private QueryExecutorObject queryExecObj;
	/* Fine */
	/* per gestione sap2.0 */
	// private SourceBean sbDataSap2; -- obsoleto non serve piu'
	private static final boolean isSap2 = true;
	private boolean inviaFromConferimento;
	private boolean inviaFromTrasferimento;
	private String dataTrasferimento;
	private String flgPoloReg;
	private String provinciaSil;
	private String regioneSil;
	private String dataSAP2;
	private BigDecimal indiceProfilingSapMin;
	private XMLGregorianCalendar datEventoStatoInAnagrafe;
	private Vector paramErroreSAP = null;
	private boolean erroreGestito;
	private boolean isRaggiuntoLimiteChiamate;
	private String descrizioneErroreLimite;

	public void service(SourceBean request, SourceBean response) throws Exception {
		boolean notFromBatch = true;
		this.isRaggiuntoLimiteChiamate = false;
		setErroreGestito(false);
		if (getReqContainer() == null) {
			reqContainer = getRequestContainer();
		} else {
			notFromBatch = false;
		}
		User user = (User) reqContainer.getSessionContainer().getAttribute("@@USER@@");
		BigDecimal userid = new BigDecimal(user.getCodut());
		ReportOperationResult reportOperation = new ReportOperationResult(this, response);
		TransactionQueryExecutor transExec = null;
		DataConnection dataConnection = null;
		Connection connection = null;
		Monitor monitor = null;

		disableMessageIdFail();
		disableMessageIdSuccess();

		String cdnLavoratore = (String) request.getAttribute("CDNLAVORATORE");
		String rispostaWs = "";
		String codStatoSap = STATO_SAP_ATTIVA;
		String codTipoVariazioneSap = VARIAZIONE_PRIMO_INVIO;
		this.inviaFromConferimento = this.reqContainer.getServiceRequest()
				.containsAttribute("INVIASAPFROMCONFERIMENTO");
		this.inviaFromTrasferimento = this.reqContainer.getServiceRequest()
				.containsAttribute("INVIASAPFROMTRASFERIMENTO");
		if (this.inviaFromTrasferimento) {
			this.dataTrasferimento = (String) this.reqContainer.getServiceRequest().getAttribute("DATATRASFCOMP");
			this.flgPoloReg = (String) this.reqContainer.getServiceRequest().getAttribute("FLGPOLOREGTRASF");
			this.provinciaSil = (String) this.reqContainer.getServiceRequest().getAttribute("PROVINCIASILTRASF");
			this.regioneSil = (String) this.reqContainer.getServiceRequest().getAttribute("REGIONESILTRASF");
			this.dataSAP2 = (String) this.reqContainer.getServiceRequest().getAttribute("DATSAP2");
		}

		try {
			String encrypterKey = System.getProperty("_ENCRYPTER_KEY_");
			String p_cdnUtente = String.valueOf(user.getCodut());

			if (notFromBatch) {
				transExec = new TransactionQueryExecutor(getPool(), this);
				enableTransactions(transExec);
				transExec.initTransaction();

				monitor = MonitorFactory.start(this.getClass().getName() + "::getXmlSAP");
				dataConnection = transExec.getDataConnection();
				connection = dataConnection.getInternalConnection();

				/* ora solo sap 2 */
				// this.setSectionQuerySelect("QUERY_CHECK_SAP2");
				// sbDataSap2 = doSelect(request, response);

				this.setSectionQuerySelect("QUERY_MOV_RIPETUTI");
				sbMovimentiRipetuti = doSelect(request, response);

				this.setSectionQuerySelect("QUERY_MOV_SUCCESSIVI");
				sbMovimentiSuccessivi = doSelect(request, response);

				this.setSectionQuerySelect("QUERY_MOV_PRECEDENTI");
				sbMovimentiPrecedenti = doSelect(request, response);

				this.setSectionQuerySelect("QUERY_MOV_SUCC_APPR");
				sbMovimentiSuccApprofondita = doSelect(request, response);

				this.setSectionQuerySelect("QUERY_MOV_PREC_APPR");
				sbMovimentiPrecApprofondita = doSelect(request, response);

				this.setSectionQuerySelect("QUERY_MOV_NO_SUCC_PUNTATO_PREC");
				sbMovimentiNoSuccPuntatoPrec = doSelect(request, response);

				this.setSectionQuerySelect("QUERY_MOV_NO_PREC_PUNTATO_SUCC");
				sbMovimentiNoPrecPuntatoSucc = doSelect(request, response);

				this.setSectionQuerySelect("QUERY_LAVORATORE");
				sbAnLavoratore = doSelect(request, response);

				this.setSectionQuerySelect("QUERY_CPI_LAVORATORE");
				sbCpiLavoratore = doSelect(request, response);

				this.setSectionQuerySelect("QUERY_EXIST_SAP");
				sbSpLavoratore = doSelect(request, response);

				this.setSectionQuerySelect("QUERY_CODCPIMIN");
				sbCpiMinProvincia = doSelect(request, response);

			} else {
				connection = getBatchConnection();
				/* ora solo sap 2 */
				// sbDataSap2 = getSbDataSap2();
				sbMovimentiRipetuti = getSbMovimentiRipetuti();
				sbMovimentiSuccessivi = getSbMovimentiSuccessivi();
				sbMovimentiPrecedenti = getSbMovimentiPrecedenti();
				sbMovimentiSuccApprofondita = getSbMovimentiSuccApprofondita();
				sbMovimentiPrecApprofondita = getSbMovimentiPrecApprofondita();
				sbMovimentiNoSuccPuntatoPrec = getSbMovimentiNoSuccPuntatoPrec();
				sbMovimentiNoPrecPuntatoSucc = getSbMovimentiNoPrecPuntatoSucc();
				sbAnLavoratore = getSbAnLavoratore();
				sbCpiLavoratore = getSbCpiLavoratore();
				sbSpLavoratore = getSbSpLavoratore();
				sbCpiMinProvincia = getSbCpiMinProvincia();
				setFlgInvioSap(null);
				setNoteErrori(null);
				setNotOverrideNote(false);
			}
			/*
			 * ora solo sap 2 //controllo data per gestione sap 2.0 if(sbDataSap2!=null){ BigDecimal differenzaGiorni =
			 * (BigDecimal) sbDataSap2.getAttribute("ROW.DATEDIFF"); if(differenzaGiorni!= null &&
			 * differenzaGiorni.intValue()>=0){ isSap2 = true; }else{ isSap2 = false; } }else{ isSap2 = false; }
			 */

			/* controllo prioritario: eventuali errori di catene di movimenti bloccano l'invio della sap */
			if (sbMovimentiRipetuti != null || sbMovimentiSuccessivi != null || sbMovimentiPrecedenti != null
					|| sbMovimentiSuccApprofondita != null || sbMovimentiPrecApprofondita != null
					|| sbMovimentiNoSuccPuntatoPrec != null || sbMovimentiNoPrecPuntatoSucc != null) {
				try {
					controlloCateneMovimenti(sbMovimentiRipetuti, "ROW.RIPETIZIONI");
					controlloCateneMovimenti(sbMovimentiSuccessivi, "ROW.CDNLAVORATORE");
					controlloCateneMovimenti(sbMovimentiPrecedenti, "ROW.CDNLAVORATORE");
					controlloCateneMovimenti(sbMovimentiSuccApprofondita, "ROW.CDNLAVORATORE");
					controlloCateneMovimenti(sbMovimentiPrecApprofondita, "ROW.CDNLAVORATORE");
					controlloCateneMovimenti(sbMovimentiNoSuccPuntatoPrec, "ROW.CDNLAVORATORE");
					controlloCateneMovimenti(sbMovimentiNoPrecPuntatoSucc, "ROW.CDNLAVORATORE");
				} catch (MovimentoCollegatoErroreExcpetion mcee) {
					setNotOverrideNote(true);
					setFlgInvioSap("KO");
					setNoteErrori(
							"Non è possibile effettuare l'invio della SAP in quanto esistono catene errate nei movimenti del lavoratore");
					setErroreGestito(true);
					response.setAttribute("ERRORENOLOG", "TRUE");
					if (notFromBatch && !inviaFromConferimento) {
						reportOperation.reportFailure(MessageCodes.CateneMovimenti.CATENE_MOV_ERRATE);
					}
					try {
						if (notFromBatch && transExec != null) {
							transExec.commitTransaction();
						}
					} catch (EMFInternalError ie) {
						if (!isNotOverrideNote())
							setNoteErrori(
									MessageBundle.getMessage(String.valueOf(MessageCodes.General.OPERATION_FAIL)));
						if (notFromBatch && !inviaFromConferimento) {
							reportOperation.reportFailure(MessageCodes.General.OPERATION_FAIL, mcee, "service()",
									"errore invio xml sap");
						}
					}
					if ((this.reqContainer.getServiceRequest().containsAttribute("INVIASAPFROMDID")
							&& this.reqContainer.getServiceRequest().getAttribute("INVIASAPFROMDID").toString()
									.equalsIgnoreCase(Values.FLAG_TRUE))
							|| (this.reqContainer.getServiceRequest().containsAttribute("INVIASAPFROMTRASFERIMENTO")
									&& this.reqContainer.getServiceRequest().getAttribute("INVIASAPFROMTRASFERIMENTO")
											.toString().equalsIgnoreCase(Values.FLAG_TRUE))) {
						throw new Exception("Errore nella gestione invio SAP", mcee);
					} else {
						if (notFromBatch && !inviaFromConferimento) {
							reportOperation.reportFailure(MessageCodes.General.OPERATION_FAIL, mcee, "service()",
									"errore invio xml sap");
						}
					}
					return;
				}
			}
			/* fine controllo catene */

			if (sbSpLavoratore != null && sbSpLavoratore.containsAttribute("ROW.CODMINSAP")) {
				// esiste il codminsap nella tabella SP_LAVORATORE
				String codMinSapSil = (String) sbSpLavoratore.getAttribute("ROW.CODMINSAP");
				String codStatoSapSil = (String) sbSpLavoratore.getAttribute("ROW.CODSTATO");

				if ((STATO_SAP_ATTIVA).equalsIgnoreCase(codStatoSapSil)
						|| (STATO_SAP_PERDITATITOLARIETA).equalsIgnoreCase(codStatoSapSil)) {
					// se attiva o perdita di titolarita
					flussoMergeSap(notFromBatch, codMinSapSil, sbCpiLavoratore, reportOperation, transExec,
							cdnLavoratore, encrypterKey, p_cdnUtente, sbCpiMinProvincia);
				} else {
					// chiamata verificaSAP
					String strCodiceFiscale = (String) sbAnLavoratore.getAttribute("ROW.STRCODICEFISCALE");
					String codMinSapVerificaSap = "";
					try {
						// invoco i WS verificaSAP
						codMinSapVerificaSap = sendVerificaSAP(strCodiceFiscale);
					} catch (JAXRPCException ie) {
						setErroreGestito(true);
						response.setAttribute("ERRORENOLOG", "TRUE");
						if (!isNotOverrideNote())
							setNoteErrori(
									MessageBundle.getMessage(String.valueOf(MessageCodes.YG.ERR_EXEC_WS_VERIFICASAP)));
						if (notFromBatch && !inviaFromConferimento) {
							reportOperation.reportFailure(MessageCodes.YG.ERR_EXEC_WS_VERIFICASAP, ie, "service()",
									"errore chiamata servizio verifica sap");
						}
						throw new Exception();
					} catch (RemoteException ie) {
						setErroreGestito(true);
						response.setAttribute("ERRORENOLOG", "TRUE");
						if (!isNotOverrideNote())
							setNoteErrori(
									MessageBundle.getMessage(String.valueOf(MessageCodes.YG.ERR_EXEC_WS_VERIFICASAP)));
						if (notFromBatch && !inviaFromConferimento) {
							reportOperation.reportFailure(MessageCodes.YG.ERR_EXEC_WS_VERIFICASAP, ie, "service()",
									"errore chiamata servizio verifica sap");
						}
						throw new Exception();
					} catch (JAXBException ie) {
						setErroreGestito(true);
						response.setAttribute("ERRORENOLOG", "TRUE");
						if (!isNotOverrideNote())
							setNoteErrori(
									MessageBundle.getMessage(String.valueOf(MessageCodes.YG.ERR_EXEC_WS_VERIFICASAP)));
						if (notFromBatch && !inviaFromConferimento) {
							reportOperation.reportFailure(MessageCodes.YG.ERR_EXEC_WS_VERIFICASAP, ie, "service()",
									"errore chiamata servizio verifica sap");
						}
						throw new Exception();
					}

					if (codMinSapVerificaSap == null || codMinSapVerificaSap.equals("")
							|| ("0").equalsIgnoreCase(codMinSapVerificaSap)) {
						if (codMinSapVerificaSap != null && ("0").equalsIgnoreCase(codMinSapVerificaSap)) {
							boolean risChiudiZero = VerificaEsistenzaSAP
									.chiudiRecordSapZero(new BigDecimal(cdnLavoratore), userid, transExec);
						}
						// verifica competenza
						inviaNewSap(notFromBatch, sbCpiLavoratore, codTipoVariazioneSap, codStatoSap, cdnLavoratore,
								encrypterKey, p_cdnUtente, connection, transExec, reportOperation);
					} else {
						if (codMinSapVerificaSap.trim().startsWith("X001")) {
							this.isRaggiuntoLimiteChiamate = true;
							setErroreGestito(true);
							response.setAttribute("ERRORENOLOG", "TRUE");
							if (!isNotOverrideNote()) {
								setNoteErrori(MessageBundle
										.getMessage(String.valueOf(MessageCodes.YG.ERR_LIMITE_INVOCAZIONE_SUPERATO)));
							}
							if (notFromBatch && !inviaFromConferimento && !inviaFromTrasferimento) {
								Vector<String> params = new Vector<String>();
								params.add(codMinSapVerificaSap);
								reportOperation.reportFailure(MessageCodes.YG.WS_VERIFICASAP_RISPOSTA_MINISTERO,
										"service", "Errore ministero SAP", params);
							}
						} else {
							if (!codMinSapSil.equalsIgnoreCase(codMinSapVerificaSap)) {
								String datNascita = (String) sbAnLavoratore.getAttribute("ROW.DATNASC");
								String strCodiceFiscaleSil = (String) sbAnLavoratore
										.getAttribute("ROW.STRCODICEFISCALE");
								String codCpiMin = (String) sbCpiLavoratore.getAttribute("ROW.cpicompmin");
								// chiudo record precedente sp_lavoratore
								if (notFromBatch) {
									Object[] paramsUpdate = new Object[2];
									paramsUpdate[0] = p_cdnUtente;
									paramsUpdate[1] = cdnLavoratore;
									Object queryResUpdate = transExec.executeQuery("UPDATE_SP_LAVORATORE_INVIO_SAP",
											paramsUpdate, "UPDATE");
									if (queryResUpdate == null || !(queryResUpdate instanceof Boolean
											&& ((Boolean) queryResUpdate).booleanValue() == true)) {
										setNotOverrideNote(true);
										setNoteErrori("Impossibile eseguire update dei dati in SP_LAVORATORE");
										throw new Exception("Impossibile eseguire update dei dati in SP_LAVORATORE");
									}

									// inserisco il record in SP_LAVORATORE
									Object[] params = new Object[8];
									params[0] = codMinSapVerificaSap;
									params[1] = codCpiMin;
									params[2] = datNascita;
									params[3] = cdnLavoratore;
									params[4] = p_cdnUtente;
									params[5] = p_cdnUtente;
									params[6] = strCodiceFiscaleSil;
									params[7] = STATO_SAP_ATTIVA; // codstatosap ATTIVA
									Object queryRes = transExec.executeQuery("INSERT_SP_LAVORATORE_VERIFICA_SAP",
											params, "INSERT");
									if (queryRes == null || !(queryRes instanceof Boolean
											&& ((Boolean) queryRes).booleanValue() == true)) {
										setNotOverrideNote(true);
										setNoteErrori("Impossibile eseguire insert dei dati in SP_LAVORATORE");
										throw new Exception("Impossibile eseguire insert dei dati in SP_LAVORATORE");
									}
								} else {
									QueryExecutorObject qExec = getQueryExecObj();
									DataConnection dc = qExec.getDataConnection();
									qExec.setStatement(SQLStatements.getStatement("UPDATE_SP_LAVORATORE_INVIO_SAP"));
									qExec.setType(QueryExecutorObject.UPDATE);
									List<DataField> params = new ArrayList<DataField>();
									params.add(
											dc.createDataField("CDNUTMOD", Types.NUMERIC, new BigDecimal(p_cdnUtente)));
									params.add(dc.createDataField("CDNLAVORATORE", Types.NUMERIC,
											new BigDecimal(cdnLavoratore)));
									qExec.setInputParameters(params);
									Object queryResUpdate = qExec.exec();
									if (queryResUpdate == null || !(queryResUpdate instanceof Boolean
											&& ((Boolean) queryResUpdate).booleanValue() == true)) {
										setNotOverrideNote(true);
										setNoteErrori("Impossibile eseguire update dei dati in SP_LAVORATORE");
										throw new Exception("Impossibile eseguire update dei dati in SP_LAVORATORE");
									}

									// inserisco il record in SP_LAVORATORE
									qExec.setStatement(SQLStatements.getStatement("INSERT_SP_LAVORATORE_VERIFICA_SAP"));
									qExec.setType(QueryExecutorObject.INSERT);
									List<DataField> paramsIns = new ArrayList<DataField>();
									paramsIns.add(dc.createDataField("CODMINSAP", Types.VARCHAR, codMinSapVerificaSap));
									paramsIns.add(dc.createDataField("CODENTETIT", Types.VARCHAR, codCpiMin));
									paramsIns.add(dc.createDataField("DATNASC", Types.VARCHAR, datNascita));
									paramsIns.add(dc.createDataField("CDNLAVORATORE", Types.NUMERIC,
											new BigDecimal(cdnLavoratore)));
									paramsIns.add(
											dc.createDataField("CDNUTINS", Types.NUMERIC, new BigDecimal(p_cdnUtente)));
									paramsIns.add(
											dc.createDataField("CDNUTMOD", Types.NUMERIC, new BigDecimal(p_cdnUtente)));
									paramsIns.add(
											dc.createDataField("STRCODICEFISCALE", Types.VARCHAR, strCodiceFiscaleSil));
									paramsIns.add(dc.createDataField("CODSTATO", Types.VARCHAR, STATO_SAP_ATTIVA));
									qExec.setInputParameters(paramsIns);
									Object queryRes = qExec.exec();
									if (queryRes == null || !(queryRes instanceof Boolean
											&& ((Boolean) queryRes).booleanValue() == true)) {
										setNotOverrideNote(true);
										setNoteErrori("Impossibile eseguire insert dei dati in SP_LAVORATORE");
										throw new Exception("Impossibile eseguire insert dei dati in SP_LAVORATORE");
									}
								}

							}
							// esiste la SAP allora si ritorna nel flusso MERGE sezione 6
							flussoMergeSap(notFromBatch, codMinSapVerificaSap, sbCpiLavoratore, reportOperation,
									transExec, cdnLavoratore, encrypterKey, p_cdnUtente, sbCpiMinProvincia);
						}
					}
				}

			} else {
				// non esiste il codminsap nella tabella SP_LAVORATORE
				String strCodiceFiscale = (String) sbAnLavoratore.getAttribute("ROW.STRCODICEFISCALE");
				String codMinSapVerificaSap = "";
				try {
					// invoco i WS verificaSAP
					codMinSapVerificaSap = sendVerificaSAP(strCodiceFiscale);
				} catch (JAXRPCException ie) {
					setErroreGestito(true);
					response.setAttribute("ERRORENOLOG", "TRUE");
					if (!isNotOverrideNote())
						setNoteErrori(
								MessageBundle.getMessage(String.valueOf(MessageCodes.YG.ERR_EXEC_WS_VERIFICASAP)));
					if (notFromBatch && !inviaFromConferimento) {
						reportOperation.reportFailure(MessageCodes.YG.ERR_EXEC_WS_VERIFICASAP, ie, "service()",
								"errore chiamata servizio verifica sap");
					}
					throw new Exception();
				} catch (RemoteException ie) {
					setErroreGestito(true);
					response.setAttribute("ERRORENOLOG", "TRUE");
					if (!isNotOverrideNote())
						setNoteErrori(
								MessageBundle.getMessage(String.valueOf(MessageCodes.YG.ERR_EXEC_WS_VERIFICASAP)));
					if (notFromBatch && !inviaFromConferimento) {
						reportOperation.reportFailure(MessageCodes.YG.ERR_EXEC_WS_VERIFICASAP, ie, "service()",
								"errore chiamata servizio verifica sap");
					}
					throw new Exception();
				}

				if (codMinSapVerificaSap == null || codMinSapVerificaSap.equals("")
						|| ("0").equalsIgnoreCase(codMinSapVerificaSap)) {
					if (codMinSapVerificaSap != null && ("0").equalsIgnoreCase(codMinSapVerificaSap)) {
						boolean risChiudiZero = VerificaEsistenzaSAP.chiudiRecordSapZero(new BigDecimal(cdnLavoratore),
								userid, transExec);
					}
					// non esiste SAP allora viene spedita completa dalla sezione 0 alla 6
					inviaNewSap(notFromBatch, sbCpiLavoratore, codTipoVariazioneSap, codStatoSap, cdnLavoratore,
							encrypterKey, p_cdnUtente, connection, transExec, reportOperation);
				} else {
					if (codMinSapVerificaSap.trim().startsWith("X001")) {
						this.isRaggiuntoLimiteChiamate = true;
						setErroreGestito(true);
						response.setAttribute("ERRORENOLOG", "TRUE");
						if (!isNotOverrideNote()) {
							setNoteErrori(MessageBundle
									.getMessage(String.valueOf(MessageCodes.YG.ERR_LIMITE_INVOCAZIONE_SUPERATO)));
						}
						if (notFromBatch && !inviaFromConferimento && !inviaFromTrasferimento) {
							Vector<String> params = new Vector<String>();
							params.add(codMinSapVerificaSap);
							reportOperation.reportFailure(MessageCodes.YG.WS_VERIFICASAP_RISPOSTA_MINISTERO, "service",
									"Errore ministero SAP", params);
						}
					} else {
						// inserisco nuovo record sp_lavoratore
						String datNascita = (String) sbAnLavoratore.getAttribute("ROW.DATNASC");
						String strCodiceFiscaleSil = (String) sbAnLavoratore.getAttribute("ROW.STRCODICEFISCALE");
						String codCpiMin = (String) sbCpiLavoratore.getAttribute("ROW.cpicompmin");
						Object queryRes = null;
						if (notFromBatch) {
							// inserisco il record in SP_LAVORATORE
							Object[] params = new Object[8];
							params[0] = codMinSapVerificaSap;
							params[1] = codCpiMin;
							params[2] = datNascita;
							params[3] = cdnLavoratore;
							params[4] = p_cdnUtente;
							params[5] = p_cdnUtente;
							params[6] = strCodiceFiscaleSil;
							params[7] = STATO_SAP_ATTIVA; // codstatosap ATTIVA
							queryRes = transExec.executeQuery("INSERT_SP_LAVORATORE_VERIFICA_SAP", params, "INSERT");
						} else {
							QueryExecutorObject qExec = getQueryExecObj();
							DataConnection dc = qExec.getDataConnection();
							// inserisco il record in SP_LAVORATORE
							qExec.setStatement(SQLStatements.getStatement("INSERT_SP_LAVORATORE_VERIFICA_SAP"));
							qExec.setType(QueryExecutorObject.INSERT);
							List<DataField> paramsIns = new ArrayList<DataField>();
							paramsIns.add(dc.createDataField("CODMINSAP", Types.VARCHAR, codMinSapVerificaSap));
							paramsIns.add(dc.createDataField("CODENTETIT", Types.VARCHAR, codCpiMin));
							paramsIns.add(dc.createDataField("DATNASC", Types.VARCHAR, datNascita));
							paramsIns.add(
									dc.createDataField("CDNLAVORATORE", Types.NUMERIC, new BigDecimal(cdnLavoratore)));
							paramsIns.add(dc.createDataField("CDNUTINS", Types.NUMERIC, new BigDecimal(p_cdnUtente)));
							paramsIns.add(dc.createDataField("CDNUTMOD", Types.NUMERIC, new BigDecimal(p_cdnUtente)));
							paramsIns.add(dc.createDataField("STRCODICEFISCALE", Types.VARCHAR, strCodiceFiscaleSil));
							paramsIns.add(dc.createDataField("CODSTATO", Types.VARCHAR, STATO_SAP_ATTIVA));
							qExec.setInputParameters(paramsIns);
							queryRes = qExec.exec();
							if (queryRes == null
									|| !(queryRes instanceof Boolean && ((Boolean) queryRes).booleanValue() == true)) {
								setNotOverrideNote(true);
								setNoteErrori("Impossibile eseguire insert record in SP_LAVORATORE");
								throw new Exception("Impossibile eseguire insert record in SP_LAVORATORE");
							}
						}

						if (queryRes == null
								|| !(queryRes instanceof Boolean && ((Boolean) queryRes).booleanValue() == true)) {
							setNotOverrideNote(true);
							setNoteErrori("Impossibile eseguire update del codice sap in an_yg_dati_invio");
							throw new Exception("Impossibile eseguire update del codice sap in an_yg_dati_invio");
						}

						flussoMergeSap(notFromBatch, codMinSapVerificaSap, sbCpiLavoratore, reportOperation, transExec,
								cdnLavoratore, encrypterKey, p_cdnUtente, sbCpiMinProvincia);
					}
				}
			}
			if (notFromBatch) {
				transExec.commitTransaction();
			}
			setFlgInvioSap(Risposta_invioSAP_TypeEsito._OK);
			setNoteErrori(null);
			if (notFromBatch && !inviaFromConferimento && !inviaFromTrasferimento && !this.isRaggiuntoLimiteChiamate) {
				reportOperation.reportSuccess(MessageCodes.General.OPERATION_SUCCESS);
			}
		} catch (Exception e) {
			_logger.error("Errore nella gestione invio SAP", e);
			try {
				if (notFromBatch && transExec != null) {
					transExec.commitTransaction();
				}
			} catch (EMFInternalError ie) {
				if (!isNotOverrideNote())
					setNoteErrori(MessageBundle.getMessage(String.valueOf(MessageCodes.General.OPERATION_FAIL)));
				if (notFromBatch && !inviaFromConferimento) {
					reportOperation.reportFailure(MessageCodes.General.OPERATION_FAIL, e, "service()",
							"errore invio xml sap");
				}
			}
			if ((this.reqContainer.getServiceRequest().containsAttribute("INVIASAPFROMDID") && this.reqContainer
					.getServiceRequest().getAttribute("INVIASAPFROMDID").toString().equalsIgnoreCase(Values.FLAG_TRUE))
					|| (this.reqContainer.getServiceRequest().containsAttribute("INVIASAPFROMTRASFERIMENTO")
							&& this.reqContainer.getServiceRequest().getAttribute("INVIASAPFROMTRASFERIMENTO")
									.toString().equalsIgnoreCase(Values.FLAG_TRUE))) {
				if (!isNotOverrideNote())
					setNoteErrori("Errore nella gestione invio SAP");
				throw new Exception("Errore nella gestione invio SAP", e);
			} else {
				if (!isNotOverrideNote())
					setNoteErrori(MessageBundle.getMessage(String.valueOf(MessageCodes.General.OPERATION_FAIL)));
				if (notFromBatch && !inviaFromConferimento) {
					reportOperation.reportFailure(MessageCodes.General.OPERATION_FAIL, e, "service()",
							"errore invio xml sap");
				}
			}
		}
	}

	private void controlloCateneMovimenti(SourceBean sbCatena, String attributeName)
			throws MovimentoCollegatoErroreExcpetion {
		if (sbCatena != null && sbCatena.containsAttribute(attributeName)) {
			BigDecimal value = (BigDecimal) sbCatena.getAttribute(attributeName);
			if (value != null && value.intValue() > 0) {
				throw new MovimentoCollegatoErroreExcpetion("Catene errate nei movimenti del lavoratore");
			}
		}
	}

	private void flussoMergeSap(boolean notFromBatch, String codiceSap, SourceBean sbCpiLavoratore,
			ReportOperationResult reportOperation, TransactionQueryExecutor transExec, String cdnLavoratore,
			String encrypterKey, String p_cdnUtente, SourceBean sbCpiMinProvincia) throws Exception {
		_logger.debug("INIZIO MERGE SEZIONE 6, cdnLavoratore =" + cdnLavoratore);

		// esiste il codice sap ministeriale allora
		// invoco il servizio di richiesta SAP
		DataConnection dataConnection = null;
		Connection connection = null;
		String codErrore = "";
		String codTipoVariazioneSap = VARIAZIONE_AGGIORNAMENTO;
		if (notFromBatch) {
			dataConnection = transExec.getDataConnection();
			connection = dataConnection.getInternalConnection();
		} else {
			connection = getBatchConnection();
		}

		String codMonoTipoCpi = (String) sbCpiLavoratore.getAttribute("ROW.CODMONOTIPOCPI");
		LavoratoreType xmlSapLavoratoreMin = null;
		it.eng.sil.pojo.yg.sap.due.LavoratoreType xmlSapLavoratoreMin2 = null;
		try {
			if (isSap2) {
				xmlSapLavoratoreMin2 = sendRichiestaSAP2(codiceSap);
			} else {
				xmlSapLavoratoreMin = sendRichiestaSAP(codiceSap);
			}
		} catch (JAXRPCException ie) {
			if (!isNotOverrideNote())
				setNoteErrori(MessageBundle.getMessage(String.valueOf(MessageCodes.YG.ERR_EXEC_WS_RICHIESTASAP)));
			if (notFromBatch && !inviaFromConferimento) {
				reportOperation.reportFailure(MessageCodes.YG.ERR_EXEC_WS_RICHIESTASAP, ie, "service()",
						"errore chiamata servizio invio sap");
			}
			throw new Exception();
		} catch (RemoteException ie) {
			if (!isNotOverrideNote())
				setNoteErrori(MessageBundle.getMessage(String.valueOf(MessageCodes.YG.ERR_EXEC_WS_RICHIESTASAP)));
			if (notFromBatch && !inviaFromConferimento) {
				reportOperation.reportFailure(MessageCodes.YG.ERR_EXEC_WS_RICHIESTASAP, ie, "service()",
						"errore chiamata servizio invio sap");
			}
			throw new Exception();
		}

		String xml = "";

		LavoratoreType xmlSapLavoraoreSil = new LavoratoreType();
		List<PoliticheAttive> listaSezione6Sil = new ArrayList<PoliticheAttive>();

		it.eng.sil.pojo.yg.sap.due.LavoratoreType xmlSapLavoraoreSil2 = new it.eng.sil.pojo.yg.sap.due.LavoratoreType();
		List<it.eng.sil.pojo.yg.sap.due.PoliticheAttive> listaSezione6Sil2 = new ArrayList<it.eng.sil.pojo.yg.sap.due.PoliticheAttive>();
		boolean isPoliticheAttive = false;

		if (xmlSapLavoratoreMin != null || xmlSapLavoratoreMin2 != null) {
			XMLGregorianCalendar dtCalendarDataDichiarazione = null;
			String dataDichiarazione = null;
			String codiceentetitSap = null;
			Datiinvio datinvio = null;
			it.eng.sil.pojo.yg.sap.due.Datiinvio datinvio2 = null;
			if (isSap2) {
				datinvio2 = xmlSapLavoratoreMin2.getDatiinvio();
				if (datinvio2 != null && xmlSapLavoratoreMin2.getDatiamministrativi() != null
						&& xmlSapLavoratoreMin2.getDatiamministrativi().getStatoinanagrafe() != null) {
					dtCalendarDataDichiarazione = xmlSapLavoratoreMin2.getDatiamministrativi().getStatoinanagrafe()
							.getDisponibilita();
				}
			} else {
				datinvio = xmlSapLavoratoreMin.getDatiinvio();
				if (datinvio != null && xmlSapLavoratoreMin.getDatiamministrativi() != null
						&& xmlSapLavoratoreMin.getDatiamministrativi().getStatoinanagrafe() != null) {
					dtCalendarDataDichiarazione = xmlSapLavoratoreMin.getDatiamministrativi().getStatoinanagrafe()
							.getDisponibilita();
				}
			}

			if (datinvio != null || datinvio2 != null) {
				XMLGregorianCalendar dtCalendarUltimoAgg = isSap2 ? datinvio2.getDataultimoagg()
						: datinvio.getDataultimoagg();
				if (dtCalendarUltimoAgg != null) {
					String dtUltimoAggiornamento = DateUtils.formatXMLGregorian(dtCalendarUltimoAgg);
					// si verifica esistenza in AN_LAVORATORE_ACCORPA CON DTMINS >= dtUltimoAggiornamento
					SourceBean sbAccorpa = null;
					BigDecimal cndLav = new BigDecimal(cdnLavoratore);
					if (notFromBatch) {
						Object[] fielWhereAcc = new Object[3];
						fielWhereAcc[0] = cndLav;
						fielWhereAcc[1] = cndLav;
						fielWhereAcc[2] = dtUltimoAggiornamento;
						sbAccorpa = (SourceBean) transExec.executeQuery("GET_ACCORPAMENTI_SUCCESSIVI_AGGIORNAMENTO_SAP",
								fielWhereAcc, "SELECT");
					} else {
						QueryExecutorObject qExec = getQueryExecObj();
						DataConnection dc = qExec.getDataConnection();
						qExec.setStatement(SQLStatements.getStatement("GET_ACCORPAMENTI_SUCCESSIVI_AGGIORNAMENTO_SAP"));
						qExec.setType(QueryExecutorObject.SELECT);
						List<DataField> params = new ArrayList<DataField>();
						params.add(dc.createDataField("CDNLAVORATORE", Types.NUMERIC, cndLav));
						params.add(dc.createDataField("CDNLAVORATORE", Types.NUMERIC, cndLav));
						params.add(dc.createDataField("DATULTIMOAGG", Types.VARCHAR, dtUltimoAggiornamento));
						qExec.setInputParameters(params);
						sbAccorpa = (SourceBean) qExec.exec();
					}
					int numAccorpamentiSucc = ((BigDecimal) sbAccorpa.getAttribute("ROW.numAccorpamenti")).intValue();
					// se nessun record (numAccorpamentiSucc = 0) allora TipoVariazione 02
					// se presente almeno un record (numAccorpamentiSucc > 0) allora si confronta il CF del lavoratore
					// con il CF nella SAP
					// se sono uguali allora TipoVariazione 02, se sono diversi allora TipoVariazione 05
					if (numAccorpamentiSucc > 0) {
						String strCodiceFiscale = (String) sbAnLavoratore.getAttribute("ROW.STRCODICEFISCALE");
						String CFSap = null;
						if (isSap2 && xmlSapLavoratoreMin2.getDatianagrafici() != null
								&& xmlSapLavoratoreMin2.getDatianagrafici().getDatipersonali() != null) {
							CFSap = xmlSapLavoratoreMin2.getDatianagrafici().getDatipersonali().getCodicefiscale();
						} else if (!isSap2 && xmlSapLavoratoreMin.getDatianagrafici() != null
								&& xmlSapLavoratoreMin.getDatianagrafici().getDatipersonali() != null) {
							CFSap = xmlSapLavoratoreMin.getDatianagrafici().getDatipersonali().getCodicefiscale();
						}

						if (StringUtils.isFilledNoBlank(CFSap) && !CFSap.equalsIgnoreCase(strCodiceFiscale)) {
							codTipoVariazioneSap = "05";
						}
					}
				}

				codiceentetitSap = isSap2 ? datinvio2.getCodiceentetit() : datinvio.getCodiceentetit();
			}
			List<PoliticheAttive> listaSezione6Min = null;
			List<it.eng.sil.pojo.yg.sap.due.PoliticheAttive> listaSezione6Min2 = null;
			if (isSap2) {
				PoliticheAttiveLst allListaSezione6Min = xmlSapLavoratoreMin2.getPoliticheAttiveLst();
				if (allListaSezione6Min != null) {
					listaSezione6Min2 = allListaSezione6Min.getPoliticheAttive();
				}
			} else {
				listaSezione6Min = xmlSapLavoratoreMin.getPoliticheAttive();
			}

			if (dtCalendarDataDichiarazione != null) {
				dataDichiarazione = DateUtils.formatXMLGregorian(dtCalendarDataDichiarazione);
			}

			if (("C").equalsIgnoreCase(codMonoTipoCpi)) {
				_logger.debug("recupero xml per lavoratore con competenza, cdnLavoratore =" + cdnLavoratore);
				// lavoratore di competenza
				// da inviare la sap dalla sezione 0 alla 6

				// crea XML SAP e recor collegati SP_LAVORATORE
				CallableStatement stmtCreaSap = connection
						.prepareCall("{? = call pg_sap.getXMLSAP(?, ?, ?, ?, ?,?, ?) }");

				stmtCreaSap.registerOutParameter(1, OracleTypes.CLOB);
				stmtCreaSap.setString(2, cdnLavoratore);
				stmtCreaSap.setString(3, encrypterKey);
				stmtCreaSap.setBigDecimal(4, new BigDecimal(p_cdnUtente));
				stmtCreaSap.setString(5, codTipoVariazioneSap);
				stmtCreaSap.setString(6, dataDichiarazione);
				stmtCreaSap.setString(7, codiceentetitSap);
				stmtCreaSap.registerOutParameter(8, OracleTypes.VARCHAR);
				stmtCreaSap.execute();

				xml = (String) stmtCreaSap.getString(1);
				codErrore = (String) stmtCreaSap.getString(8);

			} else {
				_logger.debug("recupero xml per lavoratore senza competenza, cdnLavoratore =" + cdnLavoratore);
				// lavoratore non di competenza
				// da inviare la sap solo la sezione 0 e la 6
				// crea XML SAP e recor collegati SP_LAVORATORE
				CallableStatement stmtCreaSap = connection
						.prepareCall("{? = call pg_sap.getXMLSAPNOCOMPETENZA(?, ?, ?, ?, ?, ?) }");

				stmtCreaSap.registerOutParameter(1, OracleTypes.CLOB);
				stmtCreaSap.setString(2, cdnLavoratore);
				stmtCreaSap.setString(3, encrypterKey);
				stmtCreaSap.setBigDecimal(4, new BigDecimal(p_cdnUtente));
				stmtCreaSap.setString(5, codTipoVariazioneSap);
				stmtCreaSap.setString(6, codiceentetitSap);
				stmtCreaSap.registerOutParameter(7, OracleTypes.VARCHAR);
				stmtCreaSap.execute();

				xml = (String) stmtCreaSap.getString(1);
				codErrore = (String) stmtCreaSap.getString(7);

			}

			if (xml == null || ("").equalsIgnoreCase(xml)) {
				// errore sap nulla
				setFlgInvioSap("X");
				setNotOverrideNote(true);
				setNoteErrori(MessageBundle.getMessage(String.valueOf(MessageCodes.General.OPERATION_FAIL))
						+ " - Impossibie calcolare l'xml SAP per il lavoratore");
				if (notFromBatch && !inviaFromConferimento) {
					reportOperation.reportFailure(MessageCodes.General.OPERATION_FAIL);
				}
				throw new Exception("Impossibile calcolare l'xml SAP per il lavoratore");
			} else if (!("00").equalsIgnoreCase(codErrore)) {

				setFlgInvioSap(Risposta_invioSAP_TypeEsito._KO);

				// errore nella creazione della sap mostrare codici errori
				SourceBean rows = null;

				if (notFromBatch) {
					Object[] fieldWhere = new Object[1];
					fieldWhere[0] = codErrore;
					rows = (SourceBean) transExec.executeQuery("GET_DESC_ERRORE_YG", fieldWhere, "SELECT");
				} else {
					QueryExecutorObject qExec = getQueryExecObj();
					DataConnection dc = qExec.getDataConnection();
					qExec.setStatement(SQLStatements.getStatement("GET_DESC_ERRORE_YG"));
					qExec.setType(QueryExecutorObject.SELECT);
					List<DataField> params = new ArrayList<DataField>();
					params.add(dc.createDataField("CODERRORE", Types.VARCHAR, codErrore));
					qExec.setInputParameters(params);
					rows = (SourceBean) qExec.exec();
				}
				String descErr = (String) rows.getAttribute("ROW.STRDESCRIZIONE");

				Vector paramV = new Vector(1);
				paramV.add(descErr);
				setNotOverrideNote(true);
				setNoteErrori(MessageBundle.getMessage(String.valueOf(MessageCodes.General.RIPORTA_ERRORE_INTERNO))
						+ " - " + descErr);
				if (notFromBatch && !inviaFromConferimento) {
					reportOperation.reportFailure(MessageCodes.General.RIPORTA_ERRORE_INTERNO, "INVIOSAP",
							"ERRORE INTERNO", paramV);
				}
				throw new Exception("la creazione della sap ha restituito un codice di errore");
			} else {
				xmlSapLavoraoreSil2 = null;
				xmlSapLavoraoreSil = null;
				if (StringUtils.isFilledNoBlank(xml)) {
					xml = xml.replaceAll("<indice_profiling>0.000000000</indice_profiling>",
							"<indice_profiling>0.00</indice_profiling>");
				}
				if (isSap2) {
					xmlSapLavoraoreSil2 = convertToLavoratoreSAP2(xml);
					// inserisce il codiceSAP all'interno dell'xmlda inviare come modifica
					xmlSapLavoraoreSil2.getDatiinvio().setIdentificativosap(codiceSap);
					PoliticheAttiveLst allListaSap = xmlSapLavoraoreSil2.getPoliticheAttiveLst();
					if (allListaSap != null) {
						listaSezione6Sil2 = allListaSap.getPoliticheAttive();
						isPoliticheAttive = true;
					} else {
						isPoliticheAttive = false;
					}
				} else {
					xmlSapLavoraoreSil = convertToLavoratoreSAP(xml);
					// inserisce il codiceSAP all'interno dell'xmlda inviare come modifica
					xmlSapLavoraoreSil.getDatiinvio().setIdentificativosap(codiceSap);
					listaSezione6Sil = xmlSapLavoraoreSil.getPoliticheAttive();
					if (listaSezione6Sil != null) {
						isPoliticheAttive = true;
					} else {
						isPoliticheAttive = false;
					}
				}

				// SAP INVIATA ANCHE SE NON CI SONO POLITICHE ATTIVE 15/11/2017 - Borriello Marianna (analisi
				// CONFERIMENTO DID)
				// se non ci sono Politiche Attive non si invia nulla
				// SOLO PER LE SAP DI NON COMPETENZA - MARIA 29/05/2014
				// if (!("C").equalsIgnoreCase(codMonoTipoCpi)) {
				// if ((!isSap2 && listaSezione6Sil.isEmpty()) || (isSap2 && listaSezione6Sil2.isEmpty())) {
				// // errore Lavoratore non di competenza
				// setFlgInvioSap("X");
				// setNotOverrideNote(true);
				// setNoteErrori(MessageBundle.getMessage(String.valueOf(MessageCodes.YG.ERR_SAP_NO_POLITICHEATTIVE)));
				// if(notFromBatch){
				// reportOperation.reportFailure(MessageCodes.YG.ERR_SAP_NO_POLITICHEATTIVE);
				// }
				// throw new Exception("Sap per il lavoratore senza la sezione politiche attive");
				// }
				// }

				// segnalazione redmine #7011 - 18/07/2018
				List<it.eng.sil.pojo.yg.sap.due.PoliticheAttive> sezione6DaInviare2 = mergeSezione6_2(notFromBatch,
						listaSezione6Min2, xmlSapLavoraoreSil2.getPoliticheAttiveLst(), sbCpiMinProvincia, transExec);

				// invia la sap
				int numPoliticheAttiveSil = -1;
				if (isPoliticheAttive) {
					// numPoliticheAttiveSil = isSap2? listaSezione6Sil2.size(): listaSezione6Sil.size();
					numPoliticheAttiveSil = listaSezione6Sil2.size();
					// rimuovo la sezione delle politiche attive dalla sap del sil
					if (numPoliticheAttiveSil > 0) {
						// if(isSap2){
						xmlSapLavoraoreSil2.getPoliticheAttiveLst().getPoliticheAttive().removeAll(listaSezione6Sil2);
						// }else{
						// xmlSapLavoraoreSil.getPoliticheAttive().removeAll(listaSezione6Sil);
						// }
					}

					// aggiungo la sezione delle politiche attive dopo il merge
					// if(isSap2){
					for (it.eng.sil.pojo.yg.sap.due.PoliticheAttive politicheAttiveDainviare : sezione6DaInviare2) {
						xmlSapLavoraoreSil2.getPoliticheAttiveLst().getPoliticheAttive().add(politicheAttiveDainviare);
					}
					// }else{
					// for (PoliticheAttive politicheAttiveDainviare : sezione6DaInviare) {
					// xmlSapLavoraoreSil.getPoliticheAttive().add(politicheAttiveDainviare);
					// }
					// }

				}

				String xmlSapDaInviare = isSap2 ? convertLavoratoreTypeToString2(xmlSapLavoraoreSil2)
						: convertLavoratoreTypeToString(xmlSapLavoraoreSil);

				BigDecimal prgTracciamentoSap = getPrgTracciamentoSap(notFromBatch, transExec);
				// inserisce nella tabella TS_TRACCIAMENTO_SAP
				// inserisce in TS_TRACCIAMENTO_SAP
				Boolean insertTsTraccSap = null;

				if (notFromBatch) {
					Object[] paramsIns = new Object[9];
					paramsIns[0] = prgTracciamentoSap;
					paramsIns[1] = cdnLavoratore;
					paramsIns[2] = xmlSapDaInviare;
					paramsIns[3] = codErrore;
					paramsIns[4] = null;
					paramsIns[5] = null;
					paramsIns[6] = null;
					paramsIns[7] = null;
					paramsIns[8] = codTipoVariazioneSap;
					insertTsTraccSap = (Boolean) transExec.executeQuery("INSERT_TS_TRACCIAMENTO_SAP_FOR_BATCH",
							paramsIns, "INSERT");
				} else {
					QueryExecutorObject qExec = getQueryExecObj();
					DataConnection dc = qExec.getDataConnection();
					qExec.setStatement(SQLStatements.getStatement("INSERT_TS_TRACCIAMENTO_SAP_FOR_BATCH"));
					qExec.setType(QueryExecutorObject.INSERT);
					List<DataField> params = new ArrayList<DataField>();
					params.add(dc.createDataField("PRGESTRAZIONESAP", Types.NUMERIC, prgTracciamentoSap));
					params.add(dc.createDataField("CDNLAVORATORE", Types.NUMERIC, new BigDecimal(cdnLavoratore)));
					params.add(dc.createDataField("STRXMLINVIATO", Types.VARCHAR, xmlSapDaInviare));
					params.add(dc.createDataField("CODERRESTRAZIONE", Types.VARCHAR, codErrore));
					params.add(dc.createDataField("CODMINSAP", Types.VARCHAR, null));
					params.add(dc.createDataField("DATESITOMIN", Types.DATE, null));
					params.add(dc.createDataField("STRMSGERRORE", Types.VARCHAR, null));
					params.add(dc.createDataField("STRESITOMIN", Types.VARCHAR, null));
					params.add(dc.createDataField("CODTIPOVARIAZIONE", Types.VARCHAR, codTipoVariazioneSap));
					qExec.setInputParameters(params);
					Object result = qExec.exec();
					if (result == null || !(result instanceof Boolean && ((Boolean) result).booleanValue() == true)) {
						insertTsTraccSap = Boolean.FALSE;
					} else {
						insertTsTraccSap = Boolean.TRUE;
					}
				}

				if (!insertTsTraccSap.booleanValue()) {
					setNotOverrideNote(true);
					setNoteErrori("Errore nell'inserimento TS_TRACCIAMENTO_SAP");
					throw new Exception("Errore nell'inserimento TS_TRACCIAMENTO_SAP");
				} else {
					setPrgEstrazioneSap(prgTracciamentoSap);

					if (!isSap2) {
						String cfLavoratore = "", dtNascita = "";
						if (xmlSapLavoraoreSil.getDatianagrafici() != null
								&& xmlSapLavoraoreSil.getDatianagrafici().getDatipersonali() != null) {
							Datipersonali dp = xmlSapLavoraoreSil.getDatianagrafici().getDatipersonali();
							cfLavoratore = dp.getCodicefiscale();
							dtNascita = DateUtils.formatXMLGregorian(dp.getDatanascita());
						} else {
							cfLavoratore = (String) sbAnLavoratore.getAttribute("ROW.STRCODICEFISCALE");
							dtNascita = (String) sbAnLavoratore.getAttribute("ROW.DATNASC");
						}
						Datiinvio di = xmlSapLavoraoreSil.getDatiinvio();
						String codiceEnteTit = di.getCodiceentetit();
						invioSap(notFromBatch, xmlSapDaInviare, codTipoVariazioneSap, cdnLavoratore, cfLavoratore,
								codiceEnteTit, dtNascita, encrypterKey, p_cdnUtente, transExec, reportOperation);

					} else {
						String cfLavoratore = "", dtNascita = "";

						if (xmlSapLavoraoreSil2.getDatianagrafici() != null
								&& xmlSapLavoraoreSil2.getDatianagrafici().getDatipersonali() != null) {
							it.eng.sil.pojo.yg.sap.due.Datipersonali dp = xmlSapLavoraoreSil2.getDatianagrafici()
									.getDatipersonali();
							cfLavoratore = dp.getCodicefiscale();
							dtNascita = DateUtils.formatXMLGregorian(dp.getDatanascita());
						} else {
							cfLavoratore = (String) sbAnLavoratore.getAttribute("ROW.STRCODICEFISCALE");
							dtNascita = (String) sbAnLavoratore.getAttribute("ROW.DATNASC");
						}
						it.eng.sil.pojo.yg.sap.due.Datiinvio di = xmlSapLavoraoreSil2.getDatiinvio();
						String codiceEnteTit = di.getCodiceentetit();
						invioSap(notFromBatch, xmlSapDaInviare, codTipoVariazioneSap, cdnLavoratore, cfLavoratore,
								codiceEnteTit, dtNascita, encrypterKey, p_cdnUtente, transExec, reportOperation);

					}
				}
			}
		} else {
			setFlgInvioSap("X");
			setNotOverrideNote(true);
			if (this.isRaggiuntoLimiteChiamate) {
				setNoteErrori(
						MessageBundle.getMessage(String.valueOf(MessageCodes.YG.ERR_LIMITE_INVOCAZIONE_SUPERATO)));
			} else {
				setNoteErrori(MessageBundle.getMessage(String.valueOf(MessageCodes.YG.WS_VERIFICASAP_NON_TROVATO)));
			}
			if (notFromBatch && !inviaFromConferimento) {
				if (!this.isRaggiuntoLimiteChiamate) {
					reportOperation.reportFailure(MessageCodes.YG.WS_VERIFICASAP_NON_TROVATO);
				} else {
					Vector<String> params = new Vector<String>();
					params.add(this.descrizioneErroreLimite);
					reportOperation.reportFailure(MessageCodes.YG.WS_VERIFICASAP_RISPOSTA_MINISTERO, "service",
							"Errore ministero SAP", params);
				}
			}
			throw new Exception("Errore servizio Richiesta SAP non ha ritornato nessun XML SAP");
		}

		_logger.debug("FINE MERGE SEZIONE 6, cdnLavoratore =" + cdnLavoratore);
	}

	/**
	 * metodo per il merge della sezione 6 della SAP2 ciclo su tutte le azioni locali vs quelle ministeriali
	 * 
	 * Nota: non viene utilizzato piu'
	 * 
	 * @throws EMFInternalError
	 */
	private List<it.eng.sil.pojo.yg.sap.due.PoliticheAttive> mergeSezione6_2(boolean notFromBatch,
			List<it.eng.sil.pojo.yg.sap.due.PoliticheAttive> listaXmlSezione6Ministero,
			PoliticheAttiveLst politicheAttiveLst, SourceBean sbCpiMinProvincia, TransactionQueryExecutor transExec)
			throws EMFInternalError {
		_logger.debug("INIZIO METODO mergeSezione6_2 per SAP 2.0");

		List<it.eng.sil.pojo.yg.sap.due.PoliticheAttive> listaPolAttiveDaInviare = new ArrayList<it.eng.sil.pojo.yg.sap.due.PoliticheAttive>();
		if (politicheAttiveLst != null) {
			listaPolAttiveDaInviare.addAll(politicheAttiveLst.getPoliticheAttive());
		}

		// segnalazione redmine #7011 - 18/07/2018
		if (listaXmlSezione6Ministero != null) {
			List<it.eng.sil.pojo.yg.sap.due.PoliticheAttive> listaXmlSezione6MinisteroBck = listaXmlSezione6Ministero;

			// alle politiche attive appena create dal SIL
			// vengono aggiunte quelle provenienti dal ministero ma con codentepromotore
			// fuori provincia
			for (it.eng.sil.pojo.yg.sap.due.PoliticheAttive politicheAttiveMin : listaXmlSezione6MinisteroBck) {

				String codEntePromPolAttMin = politicheAttiveMin.getCodiceEntePromotore();

				Vector vectCpiProv = sbCpiMinProvincia.getAttributeAsVector("ROW");
				boolean checkEnteInProv = false;
				for (int i = 0; i < vectCpiProv.size(); i++) {
					String codCpiMinProv = (String) ((SourceBean) vectCpiProv.get(i)).getAttribute("CODCPIMIN");
					if (codEntePromPolAttMin.equalsIgnoreCase(codCpiMinProv)) {
						checkEnteInProv = true;
						break;
					}
				}

				// if (!checkEnteInProv) {
				if (checkEnteInProv) {
					listaPolAttiveDaInviare.add(politicheAttiveMin);
				} else {
					// verifico se ente è del ministero ma non presente su mn_st_cpi nè su de_cpi_validita_min
					SourceBean countSB = null;
					if (notFromBatch) {
						Object[] param = new Object[2];
						param[0] = codEntePromPolAttMin;
						param[1] = codEntePromPolAttMin;
						countSB = (SourceBean) transExec.executeQuery("CHECK_ESISTENZA_CODCPI_SAPMINISTERO", param,
								"SELECT");
					} else {
						QueryExecutorObject qExec = getQueryExecObj();
						DataConnection dc = qExec.getDataConnection();
						qExec.setStatement(SQLStatements.getStatement("CHECK_ESISTENZA_CODCPI_SAPMINISTERO"));
						qExec.setType(QueryExecutorObject.SELECT);
						List<DataField> params = new ArrayList<DataField>();
						params.add(dc.createDataField("CODCPIMIN1", Types.VARCHAR, codEntePromPolAttMin));
						params.add(dc.createDataField("CODCPIMIN2", Types.VARCHAR, codEntePromPolAttMin));
						qExec.setInputParameters(params);
						countSB = (SourceBean) qExec.exec();
					}
					BigDecimal count = null;
					if (countSB != null) {
						Vector results = countSB.getAttributeAsVector("ROW");
						for (int i = 0; i < results.size(); i++) {
							count = (BigDecimal) ((SourceBean) results.get(i)).getAttribute("NUM");
							if (i > 0) {
								count = count.add(count);
							}
						}
					}
					// in questo caso va reinviato con la merge
					if (count != null && count.intValue() == 0) {
						listaPolAttiveDaInviare.add(politicheAttiveMin);
					}
				}

			}
		}

		_logger.debug("FINE METODO mergeSezione6");
		return listaPolAttiveDaInviare;
	}

	/**
	 * metodo per il merge della sezione 6 della SAP ciclo su tutte le azioni locali vs quelle ministeriali
	 * 
	 * Nota: non viene utilizzato piu'
	 */
	@SuppressWarnings("unused")
	private List<PoliticheAttive> mergeSezione6(List<PoliticheAttive> listaXmlSezione6Ministero,
			List<PoliticheAttive> listaXmlSezione6Sil, SourceBean sbCpiMinProvincia) {
		_logger.debug("INIZIO METODO mergeSezione6");

		List<PoliticheAttive> listaPolAttiveDaInviare = new ArrayList<PoliticheAttive>();
		if (listaXmlSezione6Sil != null) {
			listaPolAttiveDaInviare.addAll(listaXmlSezione6Sil);
		}

		if (listaXmlSezione6Ministero != null) {
			List<PoliticheAttive> listaXmlSezione6MinisteroBck = listaXmlSezione6Ministero;

			// alle politiche attive appena create dal SIL
			// vengono aggiunte quelle provenienti dal ministero ma con codentepromotore
			// fuori provincia
			for (PoliticheAttive politicheAttiveMin : listaXmlSezione6MinisteroBck) {
				String codEntePromPolAttMin = politicheAttiveMin.getCodiceEntePromotore();
				Vector vectCpiProv = sbCpiMinProvincia.getAttributeAsVector("ROW");
				boolean checkEnteInProv = false;
				for (int i = 0; i < vectCpiProv.size(); i++) {
					String codCpiMinProv = (String) ((SourceBean) vectCpiProv.get(i)).getAttribute("CODCPIMIN");
					if (codEntePromPolAttMin.equalsIgnoreCase(codCpiMinProv)) {
						checkEnteInProv = true;
						break;
					}
				}

				if (!checkEnteInProv) {
					listaPolAttiveDaInviare.add(politicheAttiveMin);
				}
			}
		}

		_logger.debug("FINE METODO mergeSezione6");
		return listaPolAttiveDaInviare;
	}

	/**
	 * metodo per il merge della sezione 6 della SAP ciclo su tutte le azioni locali vs quelle ministeriali
	 * 
	 */
	@SuppressWarnings("unused")
	private List<PoliticheAttive> old_mergeSezione6(List<PoliticheAttive> listaXmlSezione6Ministero,
			List<PoliticheAttive> listaXmlSezione6Sil) {
		_logger.debug("INIZIO METODO mergeSezione6");

		List<PoliticheAttive> listaXmlSezione6MinisteroBck = listaXmlSezione6Ministero;
		// ciclo sulla sezione 6 del SIL
		// se non esiste lo stesso codice nella lista di quelle ministeriali viene aggiunta
		// se esiste con lo stesso codice viene sovrascritta
		for (PoliticheAttive politicheAttiveSil : listaXmlSezione6Sil) {
			int idxPoliticaAttiva = checkPoliticaAttivaMin(listaXmlSezione6Ministero, politicheAttiveSil);
			if (idxPoliticaAttiva >= 0) {
				// rimuove
				listaXmlSezione6MinisteroBck.remove(idxPoliticaAttiva);
				// sostituisce
				listaXmlSezione6MinisteroBck.add(politicheAttiveSil);
			} else {
				listaXmlSezione6MinisteroBck.add(politicheAttiveSil);
			}
		}

		_logger.debug("FINE METODO mergeSezione6");
		return listaXmlSezione6MinisteroBck;
	}

	private int checkPoliticaAttivaMin(List<PoliticheAttive> listaXmlSezione6Ministero,
			PoliticheAttive politicheAttiveSil) {
		String codEntePromotore = politicheAttiveSil.getCodiceEntePromotore();
		String dataProposta = DateUtils.formatXMLGregorian(politicheAttiveSil.getDataProposta());
		String tipoAttivita = politicheAttiveSil.getTipoAttivita();
		for (PoliticheAttive politicheAttive : listaXmlSezione6Ministero) {
			if (codEntePromotore.equalsIgnoreCase(politicheAttive.getCodiceEntePromotore())) {
				if (dataProposta.equalsIgnoreCase(DateUtils.formatXMLGregorian(politicheAttive.getDataProposta()))) {
					if (tipoAttivita.equalsIgnoreCase(politicheAttive.getTipoAttivita())) {
						return listaXmlSezione6Ministero.indexOf(politicheAttive);
					}
				}
			}
		}
		return -1;
	}

	private void invioSap(boolean notFromBatch, String xmlSap, String codTipoVariazioneSap, String cdnLavoratore,
			String cfLavoratore, String codiceEnteTit, String dtNascita, String encrypterKey, String p_cdnUtente,
			TransactionQueryExecutor transExec, ReportOperationResult reportOperation) throws Exception {
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

			String nomeXsd = isSap2 ? INPUT_XSD_SAP2 : INPUT_XSD;
			if (StringUtils.isFilledNoBlank(xmlSap)) {
				xmlSap = xmlSap.replaceAll("<indice_profiling>0.000000000</indice_profiling>",
						"<indice_profiling>0.00</indice_profiling>");
			}
			if (isSap2) {
				it.eng.sil.pojo.yg.sap.due.LavoratoreType xmlSapLavoraoreSil2 = convertToLavoratoreSAP2(xmlSap);
				boolean xmlModify = false;
				if (xmlSapLavoraoreSil2 != null && xmlSapLavoraoreSil2.getDatiamministrativi() != null
						&& xmlSapLavoraoreSil2.getDatiamministrativi().getStatoinanagrafe() != null) {
					if (this.indiceProfilingSapMin != null) {
						xmlSapLavoraoreSil2.getDatiamministrativi().getStatoinanagrafe()
								.setIndiceprofiling(this.indiceProfilingSapMin);
						xmlModify = true;
					}
					if (this.datEventoStatoInAnagrafe != null) {
						xmlSapLavoraoreSil2.getDatiamministrativi().getStatoinanagrafe()
								.setDataevento(this.datEventoStatoInAnagrafe);
						xmlModify = true;
					}
				}

				if (inviaFromTrasferimento) {
					xmlModify = true;
					if (xmlSapLavoraoreSil2 != null && xmlSapLavoraoreSil2.getDatiinvio() != null) {
						xmlSapLavoraoreSil2.getDatiinvio()
								.setDataultimoagg(DateUtils.toXMLGregorianCalendarDate(DateUtils.getNow()));
						xmlSapLavoraoreSil2.getDatiinvio()
								.setTipovariazione(GestioneInviaSapYG.VARIAZIONE_AGGIORNAMENTO);
					}
					if (xmlSapLavoraoreSil2 != null) {
						PoliticheAttiveLst allListaSap = xmlSapLavoraoreSil2.getPoliticheAttiveLst();
						if (allListaSap != null) {
							List<it.eng.sil.pojo.yg.sap.due.PoliticheAttive> listaMinisteroBck = allListaSap
									.getPoliticheAttive();
							for (it.eng.sil.pojo.yg.sap.due.PoliticheAttive politicaAttiva : listaMinisteroBck) {
								String codTipoAttivita = politicaAttiva.getTipoAttivita();
								String codProgetto = politicaAttiva.getTitoloProgetto();
								String codEntePromotore = politicaAttiva.getCodiceEntePromotore();
								boolean checkTitolare = false;
								String evento = "";
								String dataProposta = "";
								String dataAvvio = "";
								String dataFine = "";
								if (politicaAttiva.getDataProposta() != null) {
									dataProposta = DateUtils.formatXMLGregorian(politicaAttiva.getDataProposta());
								}
								if (politicaAttiva.getData() != null) {
									dataAvvio = DateUtils.formatXMLGregorian(politicaAttiva.getData());
								}
								if (politicaAttiva.getDataFine() != null) {
									dataFine = DateUtils.formatXMLGregorian(politicaAttiva.getDataFine());
								}
								if (politicaAttiva.getUltimoEvento() != null) {
									evento = politicaAttiva.getUltimoEvento().getEvento();
								}
								if (codTipoAttivita != null && codProgetto != null
										&& codTipoAttivita.equalsIgnoreCase("A02") && codProgetto.equalsIgnoreCase("05")
										&& ((this.dataSAP2 != null && !this.dataSAP2.equals("") && dataProposta != null
												&& DateUtils.compare(dataProposta, this.dataSAP2) < 0)
												|| (evento != null && evento.equalsIgnoreCase("02")))) {
									if (dataProposta != null && dataAvvio != null && dataFine != null
											&& this.dataTrasferimento != null
											&& DateUtils.compare(dataProposta, dataAvvio) == 0
											&& DateUtils.compare(dataAvvio, dataFine) == 0
											&& DateUtils.compare(dataProposta, this.dataTrasferimento) == 0) {
										if (this.flgPoloReg != null
												&& this.flgPoloReg.equalsIgnoreCase(Values.FLAG_FALSE)) {
											String provinciaCPI = "";
											Object params[] = new Object[1];
											params[0] = codEntePromotore;
											SourceBean row = (SourceBean) transExec.executeQuery(
													"SELECT_PROVINCIA_CODCPI_COMPETENZA_MINISTERIALE", params,
													"SELECT");
											if (row != null) {
												row = row.containsAttribute("ROW")
														? (SourceBean) row.getAttribute("ROW")
														: row;
												provinciaCPI = row.getAttribute("provinciacpi") != null
														? row.getAttribute("provinciacpi").toString()
														: "";
												if (this.provinciaSil != null
														&& provinciaCPI.equalsIgnoreCase(this.provinciaSil)) {
													checkTitolare = true;
												}
											}
										} else {
											if (this.flgPoloReg != null
													&& this.flgPoloReg.equalsIgnoreCase(Values.FLAG_TRUE)) {
												String regioneCPI = "";
												Object params[] = new Object[1];
												params[0] = codEntePromotore;
												SourceBean row = (SourceBean) transExec.executeQuery(
														"SELECT_REGIONE_CODCPI_COMPETENZA_MINISTERIALE", params,
														"SELECT");
												if (row != null) {
													row = row.containsAttribute("ROW")
															? (SourceBean) row.getAttribute("ROW")
															: row;
													regioneCPI = row.getAttribute("codregione") != null
															? row.getAttribute("codregione").toString()
															: "";
													if (this.regioneSil != null
															&& regioneCPI.equalsIgnoreCase(this.regioneSil)) {
														checkTitolare = true;
													}
												}
											}
										}
										if (checkTitolare) {
											politicaAttiva.setDescrizione("Presa in carico per trasferimento");
										}
									}
								}
							}
						}
					}
				}

				if (xmlModify) {
					xmlSap = convertLavoratoreTypeToString2(xmlSapLavoraoreSil2);
					if (notFromBatch) {
						Object[] paramsUpdTracciato = new Object[2];
						paramsUpdTracciato[0] = xmlSap;
						paramsUpdTracciato[1] = getPrgEstrazioneSap();

						Object queryResUpdTracciato = transExec.executeQuery("UPDATE_TS_TRACCIAMENTO_SAP_TRACCIATO",
								paramsUpdTracciato, "UPDATE");
						if (queryResUpdTracciato == null || !(queryResUpdTracciato instanceof Boolean
								&& ((Boolean) queryResUpdTracciato).booleanValue() == true)) {
							_logger.debug("Errore aggiornamento TS_TRACCIAMENTO_SAP");
						}
					} else {
						QueryExecutorObject qExec = getQueryExecObj();
						DataConnection dc = qExec.getDataConnection();

						qExec.setStatement(SQLStatements.getStatement("UPDATE_TS_TRACCIAMENTO_SAP_TRACCIATO"));
						qExec.setType(QueryExecutorObject.UPDATE);
						List<DataField> params = new ArrayList<DataField>();
						params.add(dc.createDataField("STRXMLINVIATO", Types.VARCHAR, xmlSap));
						params.add(dc.createDataField("PRGESTRAZIONESAP", Types.NUMERIC, getPrgEstrazioneSap()));
						qExec.setInputParameters(params);
						Object queryResUpdateTracc = qExec.exec();
						if (queryResUpdateTracc == null || !(queryResUpdateTracc instanceof Boolean
								&& ((Boolean) queryResUpdateTracc).booleanValue() == true)) {
							_logger.debug("Errore aggiornamento TS_TRACCIAMENTO_SAP");
						}
					}
				}
			} else {
				LavoratoreType xmlSapLavoraoreSil = convertToLavoratoreSAP(xmlSap);
				boolean xmlModify = false;
				if (xmlSapLavoraoreSil != null && xmlSapLavoraoreSil.getDatiamministrativi() != null
						&& xmlSapLavoraoreSil.getDatiamministrativi().getStatoinanagrafe() != null) {
					if (this.indiceProfilingSapMin != null) {
						xmlSapLavoraoreSil.getDatiamministrativi().getStatoinanagrafe()
								.setIndiceprofiling(this.indiceProfilingSapMin);
						xmlModify = true;
					}
					if (this.datEventoStatoInAnagrafe != null) {
						xmlSapLavoraoreSil.getDatiamministrativi().getStatoinanagrafe()
								.setDataevento(this.datEventoStatoInAnagrafe);
						xmlModify = true;
					}
				}

				if (xmlModify) {
					xmlSap = convertLavoratoreTypeToString(xmlSapLavoraoreSil);
				}
			}

			if (!validazioneXml(xmlSap, nomeXsd)) {
				setFlgInvioSap("X");
				setNotOverrideNote(true);
				setNoteErrori(MessageBundle.getMessage(String.valueOf(MessageCodes.YG.ERR_WS_PARTECIPANTE_GG_INPUT)));
				if (notFromBatch && !inviaFromConferimento) {
					reportOperation.reportFailure(MessageCodes.YG.ERR_WS_PARTECIPANTE_GG_INPUT);
				}
				throw new Exception("BatchInvioMassivoSAP: Errore in validazione input.");
			}

			_logger.debug("send invio la sap= " + xmlSap);
			proxy.invioSAP(xmlSap, esito, messaggioErrore, codiceSAP);

			_logger.debug("risposta invio sap   cdnLavoratore =" + cdnLavoratore + " - codice=" + codiceSAP.value);
			_logger.debug("risposta invio la sap messaggio di errore=" + messaggioErrore.value);
			_logger.debug("risposta invio la sap esito=" + esito.value);

			String codMinSapInvioSap = codiceSAP.value;

			// chiudo record precedente di SP_LAVORATORE
			// aggiorno il record
			// solo se mi viene restiruito un codice SAP valido
			if (codMinSapInvioSap != null && !("").equalsIgnoreCase(codMinSapInvioSap)) {
				if (notFromBatch) {
					Object[] paramsUpdate = new Object[2];
					paramsUpdate[0] = p_cdnUtente;
					paramsUpdate[1] = cdnLavoratore;
					Object queryResUpdate = transExec.executeQuery("UPDATE_SP_LAVORATORE_INVIO_SAP", paramsUpdate,
							"UPDATE");
					if (queryResUpdate == null || !(queryResUpdate instanceof Boolean
							&& ((Boolean) queryResUpdate).booleanValue() == true)) {
						setNotOverrideNote(true);
						setNoteErrori("Impossibile eseguire update del record in SP_LAVORATORE");
						throw new Exception("Impossibile eseguire update del record in SP_LAVORATORE");
					}

					// inserisco il record in SP_LAVORATORE
					Object[] params = new Object[8];
					params[0] = codMinSapInvioSap;
					params[1] = codiceEnteTit;
					params[2] = dtNascita;
					params[3] = cdnLavoratore;
					params[4] = p_cdnUtente;
					params[5] = p_cdnUtente;
					params[6] = cfLavoratore;
					params[7] = STATO_SAP_ATTIVA; // codstatosap ATTIVA
					Object queryRes = transExec.executeQuery("INSERT_SP_LAVORATORE_INVIO_SAP", params, "INSERT");
					if (queryRes == null
							|| !(queryRes instanceof Boolean && ((Boolean) queryRes).booleanValue() == true)) {
						setNotOverrideNote(true);
						setNoteErrori("Impossibile eseguire insert del record in SP_LAVORATORE");
						throw new Exception("Impossibile eseguire insert record in SP_LAVORATORE");
					}

					Object[] paramsEstr = new Object[5];
					paramsEstr[0] = codMinSapInvioSap;
					paramsEstr[1] = codTipoVariazioneSap; // codTipoVariazione
					paramsEstr[2] = messaggioErrore.value;
					paramsEstr[3] = esito.value;
					paramsEstr[4] = getPrgEstrazioneSap();

					Object queryResEstr = transExec.executeQuery("UPDATE_TS_TRACCIAMENTO_SAP_CURR", paramsEstr,
							"UPDATE");
					if (queryResEstr == null
							|| !(queryResEstr instanceof Boolean && ((Boolean) queryResEstr).booleanValue() == true)) {
						setNotOverrideNote(true);
						setNoteErrori("Impossibile eseguire update del codice sap in ts_tracciamento_sap");
						throw new Exception("Impossibile eseguire update del codice sap in ts_tracciamento_sap");
					}

				} else {

					QueryExecutorObject qExec = getQueryExecObj();
					DataConnection dc = qExec.getDataConnection();

					qExec.setStatement(SQLStatements.getStatement("UPDATE_SP_LAVORATORE_INVIO_SAP"));
					qExec.setType(QueryExecutorObject.UPDATE);
					List<DataField> params = new ArrayList<DataField>();
					params.add(dc.createDataField("CDNUTMOD", Types.NUMERIC, new BigDecimal(p_cdnUtente)));
					params.add(dc.createDataField("CDNLAVORATORE", Types.NUMERIC, new BigDecimal(cdnLavoratore)));
					qExec.setInputParameters(params);
					Object queryResUpdate = qExec.exec();
					if (queryResUpdate == null || !(queryResUpdate instanceof Boolean
							&& ((Boolean) queryResUpdate).booleanValue() == true)) {
						setNotOverrideNote(true);
						setNoteErrori("Impossibile eseguire update del codice sap in an_yg_dati_invio");
						throw new Exception("Impossibile eseguire update del codice sap in an_yg_dati_invio");
					}

					// inserisco il record in SP_LAVORATORE
					qExec.setStatement(SQLStatements.getStatement("INSERT_SP_LAVORATORE_INVIO_SAP"));
					qExec.setType(QueryExecutorObject.INSERT);
					List<DataField> paramsIns = new ArrayList<DataField>();
					paramsIns.add(dc.createDataField("CODMINSAP", Types.VARCHAR, codMinSapInvioSap));
					paramsIns.add(dc.createDataField("CODENTETIT", Types.VARCHAR, codiceEnteTit));
					paramsIns.add(dc.createDataField("DATNASC", Types.VARCHAR, dtNascita));
					paramsIns.add(dc.createDataField("CDNLAVORATORE", Types.NUMERIC, new BigDecimal(cdnLavoratore)));
					paramsIns.add(dc.createDataField("CDNUTINS", Types.NUMERIC, new BigDecimal(p_cdnUtente)));
					paramsIns.add(dc.createDataField("CDNUTMOD", Types.NUMERIC, new BigDecimal(p_cdnUtente)));
					paramsIns.add(dc.createDataField("STRCODICEFISCALE", Types.VARCHAR, cfLavoratore));
					paramsIns.add(dc.createDataField("CODSTATO", Types.VARCHAR, STATO_SAP_ATTIVA));
					qExec.setInputParameters(paramsIns);
					Object queryRes = qExec.exec();
					if (queryRes == null
							|| !(queryRes instanceof Boolean && ((Boolean) queryRes).booleanValue() == true)) {
						setNotOverrideNote(true);
						setNoteErrori("Impossibile eseguire insert record in SP_LAVORATORE");
						throw new Exception("Impossibile eseguire insert record in SP_LAVORATORE");
					}

					qExec.setStatement(SQLStatements.getStatement("UPDATE_TS_TRACCIAMENTO_SAP_CURR"));
					qExec.setType(QueryExecutorObject.UPDATE);
					List<DataField> paramsEstr = new ArrayList<DataField>();
					paramsEstr.add(dc.createDataField("CODMINSAP", Types.VARCHAR, codMinSapInvioSap));
					paramsEstr.add(dc.createDataField("CODTIPOVARIAZIONE", Types.VARCHAR, codTipoVariazioneSap));
					paramsEstr.add(dc.createDataField("STRMSGERRORE", Types.VARCHAR, messaggioErrore.value));
					if (esito.value != null) {
						paramsEstr.add(dc.createDataField("STRESITOMIN", Types.VARCHAR, esito.value.getValue()));
					} else {
						paramsEstr.add(dc.createDataField("STRESITOMIN", Types.VARCHAR, esito.value));
					}
					paramsEstr.add(dc.createDataField("PRGESTRAZIONESAP", Types.BIGINT, getPrgEstrazioneSap()));
					qExec.setInputParameters(paramsEstr);
					Object queryResEstr = qExec.exec();
					if (queryResEstr == null
							|| !(queryResEstr instanceof Boolean && ((Boolean) queryResEstr).booleanValue() == true)) {
						setNotOverrideNote(true);
						setNoteErrori("Impossibile eseguire update del codice sap in ts_tracciamento_sap");
						throw new Exception("Impossibile eseguire update del codice sap in ts_tracciamento_sap");
					}
				}

			}

			if ((Risposta_invioSAP_TypeEsito.KO).equals(esito.value)
					|| (Risposta_invioSAP_TypeEsito.X001).equals(esito.value)) {
				// aggiorno la ts_tracciamento con il
				Object queryResEstr = null;
				if ((Risposta_invioSAP_TypeEsito.X001).equals(esito.value)) {
					this.isRaggiuntoLimiteChiamate = true;
					esito.value = Risposta_invioSAP_TypeEsito.KO;
				}
				if (notFromBatch) {
					Object[] paramsEstr = new Object[5];
					paramsEstr[0] = codMinSapInvioSap;
					paramsEstr[1] = codTipoVariazioneSap; // codTipoVariazione
					paramsEstr[2] = messaggioErrore.value;
					paramsEstr[3] = esito.value;
					paramsEstr[4] = getPrgEstrazioneSap();
					queryResEstr = transExec.executeQuery("UPDATE_TS_TRACCIAMENTO_SAP_CURR", paramsEstr, "UPDATE");
				} else {
					QueryExecutorObject qExec = getQueryExecObj();
					DataConnection dc = qExec.getDataConnection();
					qExec.setStatement(SQLStatements.getStatement("UPDATE_TS_TRACCIAMENTO_SAP_CURR"));
					qExec.setType(QueryExecutorObject.UPDATE);
					List<DataField> paramsUpd = new ArrayList<DataField>();
					paramsUpd.add(dc.createDataField("CODMINSAP", Types.VARCHAR, codMinSapInvioSap));
					paramsUpd.add(dc.createDataField("CODTIPOVARIAZIONE", Types.VARCHAR, codTipoVariazioneSap));
					paramsUpd.add(dc.createDataField("STRMSGERRORE", Types.VARCHAR, messaggioErrore.value));
					if (esito.value != null) {
						paramsUpd.add(dc.createDataField("STRESITOMIN", Types.VARCHAR, esito.value.getValue()));
					} else {
						paramsUpd.add(dc.createDataField("STRESITOMIN", Types.VARCHAR, esito.value));
					}
					paramsUpd.add(dc.createDataField("PRGESTRAZIONESAP", Types.NUMERIC, getPrgEstrazioneSap()));
					qExec.setInputParameters(paramsUpd);
					queryResEstr = qExec.exec();
				}

				if (queryResEstr == null
						|| !(queryResEstr instanceof Boolean && ((Boolean) queryResEstr).booleanValue() == true)) {
					setNotOverrideNote(true);
					setNoteErrori("Impossibile eseguire update del record in TS_TRACCIAMENTO_SAP");
					throw new Exception("Impossibile eseguire update del record in TS_TRACCIAMENTO_SAP");
				}

				// visualizzo le anomalie nel messaggio a video
				String descrizioneAnomalia = "";
				String strListaAnomalie = messaggioErrore.value;
				if (this.isRaggiuntoLimiteChiamate) {
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
						// 17 marzo 2017
						// segnalazioni 5198 e 5199 - gestione per nuovo modo di inviare errore dal ministero
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
							if (notFromBatch) {
								Object[] fieldWhere = new Object[1];
								fieldWhere[0] = codiceAnomalia;
								rowsAnomalia = (SourceBean) transExec.executeQuery("GET_DESC_ERRORE_YG", fieldWhere,
										"SELECT");
							} else {
								QueryExecutorObject qExec = getQueryExecObj();
								DataConnection dc = qExec.getDataConnection();
								qExec.setStatement(SQLStatements.getStatement("GET_DESC_ERRORE_YG"));
								qExec.setType(QueryExecutorObject.SELECT);
								List<DataField> params = new ArrayList<DataField>();
								params.add(dc.createDataField("CODERRORE", Types.VARCHAR, codiceAnomalia));
								qExec.setInputParameters(params);
								rowsAnomalia = (SourceBean) qExec.exec();
							}

							String descAnomalia = (String) rowsAnomalia.getAttribute("ROW.STRDESCRIZIONE");
							if (descAnomalia != null && !("").equalsIgnoreCase(descAnomalia)) {
								descrizioneAnomalia = descrizioneAnomalia + " " + codiceAnomalia + " - " + descAnomalia
										+ "<br/>";
							}
						}

					}
				}

				Vector paramV = new Vector(1);
				paramV.add("<br/>" + descrizioneAnomalia);
				setFlgInvioSap(esito.value.getValue());
				setNotOverrideNote(true);
				if (messaggioErrore.value.length() > 3000) {
					setNoteErrori(messaggioErrore.value.substring(0, 3000));
				} else {
					setNoteErrori(messaggioErrore.value);
				}
				if (notFromBatch && !inviaFromConferimento) {
					reportOperation.reportFailure(MessageCodes.YG.ERR_SAP_ANOMALIE, "WS INVIOSAP", "INVIO SAP ANOMALIE",
							paramV);
				}
				throw new Exception("Risposta invio sap KO");
			}

		} catch (JAXRPCException ie) {
			if (!isNotOverrideNote())
				setNoteErrori(MessageBundle.getMessage(String.valueOf(MessageCodes.YG.ERR_EXEC_WS_INVIOSAP)));
			if (notFromBatch && !inviaFromConferimento) {
				reportOperation.reportFailure(MessageCodes.YG.ERR_EXEC_WS_INVIOSAP, ie, "service()",
						"errore chiamata servizio invio sap");
			}
			throw new Exception();
		} catch (RemoteException ie) {
			if (!isNotOverrideNote())
				setNoteErrori(MessageBundle.getMessage(String.valueOf(MessageCodes.YG.ERR_EXEC_WS_INVIOSAP)));
			if (notFromBatch && !inviaFromConferimento) {
				reportOperation.reportFailure(MessageCodes.YG.ERR_EXEC_WS_INVIOSAP, ie, "service()",
						"errore chiamata servizio invio sap");
			}
			throw new Exception();
		}

		_logger.debug("FINE CHIAMATA INVIO SAP, cdnLavoratore =" + cdnLavoratore);
	}

	private void inviaNewSap(boolean notFromBatch, SourceBean sbCpiLavoratore, String codTipoVariazioneSap,
			String codStatoSap, String cdnLavoratore, String encrypterKey, String p_cdnUtente, Connection connection,
			TransactionQueryExecutor transExec, ReportOperationResult reportOperation) throws Exception {
		String dataDichiarazione = null;
		String codMonoTipoCpi = (String) sbCpiLavoratore.getAttribute("ROW.CODMONOTIPOCPI");
		if (("C").equalsIgnoreCase(codMonoTipoCpi)) {
			// crea XML SAP e recor collegati SP_LAVORATORE
			CallableStatement stmtCreaSap = connection.prepareCall("{? = call pg_sap.getXMLSAP(?, ?, ?, ?, ?, ?, ?) }");

			stmtCreaSap.registerOutParameter(1, OracleTypes.CLOB);
			stmtCreaSap.setString(2, cdnLavoratore);
			stmtCreaSap.setString(3, encrypterKey);
			stmtCreaSap.setBigDecimal(4, new BigDecimal(p_cdnUtente));
			stmtCreaSap.setString(5, VARIAZIONE_PRIMO_INVIO);
			stmtCreaSap.setString(6, dataDichiarazione);
			stmtCreaSap.setString(7, null);
			stmtCreaSap.registerOutParameter(8, OracleTypes.VARCHAR);
			stmtCreaSap.execute();

			String xml = (String) stmtCreaSap.getString(1);
			String codErrore = (String) stmtCreaSap.getString(8);
			LavoratoreType xmlSapLavoraoreSil = null;
			it.eng.sil.pojo.yg.sap.due.LavoratoreType xmlSapLavoraoreSil2 = null;

			if (StringUtils.isFilledNoBlank(xml)) {
				xml = xml.replaceAll("<indice_profiling>0.000000000</indice_profiling>",
						"<indice_profiling>0.00</indice_profiling>");
			}
			if (isSap2) {
				xmlSapLavoraoreSil2 = convertToLavoratoreSAP2(xml);
			} else {
				xmlSapLavoraoreSil = convertToLavoratoreSAP(xml);
			}

			Boolean insertTsTraccSap = null;

			BigDecimal prgTracciamentoSap = getPrgTracciamentoSap(notFromBatch, transExec);

			// inserisce in TS_TRACCIAMENTO_SAP
			if (notFromBatch) {
				Object[] paramsIns = new Object[9];
				paramsIns[0] = prgTracciamentoSap;
				paramsIns[1] = cdnLavoratore;
				paramsIns[2] = xml;
				paramsIns[3] = codErrore;
				paramsIns[4] = null;
				paramsIns[5] = null;
				paramsIns[6] = null;
				paramsIns[7] = null;
				paramsIns[8] = codTipoVariazioneSap;
				insertTsTraccSap = (Boolean) transExec.executeQuery("INSERT_TS_TRACCIAMENTO_SAP_FOR_BATCH", paramsIns,
						"INSERT");
			} else {
				QueryExecutorObject qExec = getQueryExecObj();
				DataConnection dc = qExec.getDataConnection();
				qExec.setStatement(SQLStatements.getStatement("INSERT_TS_TRACCIAMENTO_SAP_FOR_BATCH"));
				qExec.setType(QueryExecutorObject.INSERT);
				List<DataField> params = new ArrayList<DataField>();
				params.add(dc.createDataField("PRGESTRAZIONESAP", Types.NUMERIC, prgTracciamentoSap));
				params.add(dc.createDataField("CDNLAVORATORE", Types.NUMERIC, new BigDecimal(cdnLavoratore)));
				params.add(dc.createDataField("STRXMLINVIATO", Types.VARCHAR, xml));
				params.add(dc.createDataField("CODERRESTRAZIONE", Types.VARCHAR, codErrore));
				params.add(dc.createDataField("CODMINSAP", Types.VARCHAR, null));
				params.add(dc.createDataField("DATESITOMIN", Types.DATE, null));
				params.add(dc.createDataField("STRMSGERRORE", Types.VARCHAR, null));
				params.add(dc.createDataField("STRESITOMIN", Types.VARCHAR, null));
				params.add(dc.createDataField("CODTIPOVARIAZIONE", Types.VARCHAR, codTipoVariazioneSap));
				qExec.setInputParameters(params);
				Object result = qExec.exec();
				if (result == null || !(result instanceof Boolean && ((Boolean) result).booleanValue() == true)) {
					insertTsTraccSap = Boolean.FALSE;
				} else {
					insertTsTraccSap = Boolean.TRUE;
				}
			}
			if (!insertTsTraccSap.booleanValue()) {
				setNotOverrideNote(true);
				setNoteErrori("Errore nell'inserimento TS_TRACCIAMENTO_SAP");
				throw new Exception("Errore nell'inserimento TS_TRACCIAMENTO_SAP");
			} else {
				setPrgEstrazioneSap(prgTracciamentoSap);
				if (xml == null || ("").equalsIgnoreCase(xml)) {
					// errore sap nulla
					setFlgInvioSap("X");
					setNotOverrideNote(true);
					setNoteErrori(MessageBundle.getMessage(String.valueOf(MessageCodes.General.OPERATION_FAIL))
							+ "- Impossibile calcolare l'xml SAP per il lavoratore");
					if (notFromBatch && !inviaFromConferimento) {
						reportOperation.reportFailure(MessageCodes.General.OPERATION_FAIL);
					}
					throw new Exception("Impossibile calcolare l'xml SAP per il lavoratore");
				} else if (!("00").equalsIgnoreCase(codErrore)) {

					setFlgInvioSap(Risposta_invioSAP_TypeEsito._KO);

					// errore nella creazione della sap mostrare codici errori
					SourceBean rows = null;
					if (notFromBatch) {
						Object[] fieldWhere = new Object[1];
						fieldWhere[0] = codErrore;
						rows = (SourceBean) transExec.executeQuery("GET_DESC_ERRORE_YG", fieldWhere, "SELECT");
					} else {
						QueryExecutorObject qExec = getQueryExecObj();
						DataConnection dc = qExec.getDataConnection();
						qExec.setStatement(SQLStatements.getStatement("GET_DESC_ERRORE_YG"));
						qExec.setType(QueryExecutorObject.SELECT);
						List<DataField> params = new ArrayList<DataField>();
						params.add(dc.createDataField("CODERRORE", Types.VARCHAR, codErrore));
						qExec.setInputParameters(params);
						rows = (SourceBean) qExec.exec();
					}
					String descErr = (String) rows.getAttribute("ROW.STRDESCRIZIONE");

					Vector paramV = new Vector(1);
					paramV.add(descErr);
					setNotOverrideNote(true);
					setNoteErrori(MessageBundle.getMessage(String.valueOf(MessageCodes.General.RIPORTA_ERRORE_INTERNO))
							+ "- " + descErr);
					if (notFromBatch && !inviaFromConferimento) {
						reportOperation.reportFailure(MessageCodes.General.RIPORTA_ERRORE_INTERNO, "INVIOSAP",
								"ERRORE INTERNO", paramV);
					}
					throw new Exception("la creazione della sap ha restituito un codice di errore");
				} else {
					// inviare SAP
					if (isSap2) {
						it.eng.sil.pojo.yg.sap.due.Datipersonali dp = xmlSapLavoraoreSil2.getDatianagrafici()
								.getDatipersonali();
						it.eng.sil.pojo.yg.sap.due.Datiinvio di = xmlSapLavoraoreSil2.getDatiinvio();
						String cfLavoratore = dp.getCodicefiscale();
						String dtNascita = DateUtils.formatXMLGregorian(dp.getDatanascita());
						String codiceEnteTit = di.getCodiceentetit();
						invioSap(notFromBatch, xml, codTipoVariazioneSap, cdnLavoratore, cfLavoratore, codiceEnteTit,
								dtNascita, encrypterKey, p_cdnUtente, transExec, reportOperation);
					} else {
						Datipersonali dp = xmlSapLavoraoreSil.getDatianagrafici().getDatipersonali();
						Datiinvio di = xmlSapLavoraoreSil.getDatiinvio();
						String cfLavoratore = dp.getCodicefiscale();
						String dtNascita = DateUtils.formatXMLGregorian(dp.getDatanascita());
						String codiceEnteTit = di.getCodiceentetit();
						invioSap(notFromBatch, xml, codTipoVariazioneSap, cdnLavoratore, cfLavoratore, codiceEnteTit,
								dtNascita, encrypterKey, p_cdnUtente, transExec, reportOperation);
					}

				}
			}
		} else {
			// errore Lavoratore non di competenza
			setFlgInvioSap("X");
			setNotOverrideNote(true);
			setNoteErrori(MessageBundle.getMessage(String.valueOf(MessageCodes.YG.ERR_LAVORATORE_NO_COMPENTE)));
			if (notFromBatch && !inviaFromConferimento) {
				reportOperation.reportFailure(MessageCodes.YG.ERR_LAVORATORE_NO_COMPENTE);
			}
			throw new Exception("Lavoratore non competente");
		}

	}

	public int invioSap(String xmlSap, String cdnLavoratore) {
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

			String nomeXsd = INPUT_XSD_SAP2;

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

				this.paramErroreSAP = new Vector(1);
				paramErroreSAP.add("<br/>" + descrizioneAnomalia);

				return MessageCodes.YG.ERR_SAP_ANOMALIE;
			} else {
				return 0;
			}
		} catch (Exception e) {
			return MessageCodes.YG.ERR_EXEC_WS_INVIOSAP;
		} finally {
			_logger.debug("FINE CHIAMATA INVIO SAP, cdnLavoratore =" + cdnLavoratore);
		}
	}

	private BigDecimal getPrgTracciamentoSap(boolean notFromBatch, TransactionQueryExecutor transExec)
			throws EMFInternalError {
		SourceBean sbPrgTracciamentoSap = null;
		if (notFromBatch) {
			sbPrgTracciamentoSap = (SourceBean) transExec.executeQuery("GET_TS_TRACCIAMENTO_SAP_NEXTVAL", null,
					"SELECT");
		} else {
			QueryExecutorObject qExec = getQueryExecObj();
			DataConnection dc = qExec.getDataConnection();
			qExec.setStatement(SQLStatements.getStatement("GET_TS_TRACCIAMENTO_SAP_NEXTVAL"));
			qExec.setType(QueryExecutorObject.SELECT);
			List<DataField> params = new ArrayList<DataField>();
			qExec.setInputParameters(params);
			sbPrgTracciamentoSap = (SourceBean) qExec.exec();
		}
		BigDecimal prgTracciamentoSap = (BigDecimal) sbPrgTracciamentoSap.getAttribute("ROW.CHIAVE");
		return prgTracciamentoSap;
	}

	/**
	 * invoca il WS per verificare dato un codice fiscale lavoratore se esiste una sap valida restituisce un codice sap
	 * ministeriale
	 * 
	 * @param strCodiceFiscale
	 * @return
	 * @throws JAXRPCException
	 * @throws RemoteException
	 * @throws JAXBException
	 */
	private String sendVerificaSAP(String strCodiceFiscale) throws JAXRPCException, RemoteException, JAXBException {
		_logger.debug("INIZIO CHIAMATA VERIFICA SAP, strCodiceFiscale =" + strCodiceFiscale);

		DataSourceJNDI dataSourceJndi = new DataSourceJNDI();
		String dataSourceJndiName = dataSourceJndi.getJndi();
		EndPointSingleton eps = EndPointSingleton.getInstance(dataSourceJndiName);
		String invioSapEP = eps.getUrl(END_POINT_NAME_VERIFICASAP);
		// String invioSapEP = "http://localhost:28080/sil";
		ServizicoapWSProxy proxy = new ServizicoapWSProxy();
		proxy.setEndpoint(invioSapEP);

		VerificaSAP xmlVerifica = new VerificaSAP();
		xmlVerifica.setCodiceFiscale(strCodiceFiscale);

		String codMinSap = proxy.verificaEsistenzaSAP(convertVerificaSapToString(xmlVerifica));
		_logger.debug("FINE CHIAMATA VERIFICA SAP, strCodiceFiscale =" + strCodiceFiscale);

		return codMinSap;
	}

	/**
	 * invoca il WS per recuperare l'xml della SAP dato un codice sap ministeriale
	 * 
	 * @param codMinSap
	 * @return
	 * @throws JAXRPCException
	 * @throws RemoteException
	 * @throws JAXBException
	 */
	private LavoratoreType sendRichiestaSAP(String codMinSap) throws JAXRPCException, RemoteException, JAXBException {
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
		if (xmlSAP != null && !("").equalsIgnoreCase(xmlSAP)) {
			sapLav = convertToLavoratoreSAP(xmlSAP);
			if (sapLav != null && sapLav.getDatiamministrativi() != null
					&& sapLav.getDatiamministrativi().getStatoinanagrafe() != null) {
				this.indiceProfilingSapMin = sapLav.getDatiamministrativi().getStatoinanagrafe().getIndiceprofiling();
				this.datEventoStatoInAnagrafe = sapLav.getDatiamministrativi().getStatoinanagrafe().getDataevento();
			}
		}

		_logger.debug("FINE CHIAMATA RICHIESTA SAP, codMinSap =" + codMinSap);

		return sapLav;
	}

	/**
	 * invoca il WS per recuperare l'xml della SAP2.0 dato un codice sap ministeriale
	 * 
	 * @param codMinSap
	 * @return
	 * @throws JAXRPCException
	 * @throws RemoteException
	 * @throws JAXBException
	 */
	private it.eng.sil.pojo.yg.sap.due.LavoratoreType sendRichiestaSAP2(String codMinSap)
			throws JAXRPCException, RemoteException, JAXBException {
		_logger.debug("INIZIO CHIAMATA RICHIESTA SAP, codMinSap =" + codMinSap);

		it.eng.sil.pojo.yg.sap.due.LavoratoreType sapLav = null;

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
			xmlSAP = xmlSAP.replaceAll("<indice_profiling>0.000000000</indice_profiling>",
					"<indice_profiling>0.00</indice_profiling>");
			sapLav = convertToLavoratoreSAP2(xmlSAP);
			if (sapLav != null && sapLav.getDatiamministrativi() != null
					&& sapLav.getDatiamministrativi().getStatoinanagrafe() != null) {
				this.indiceProfilingSapMin = sapLav.getDatiamministrativi().getStatoinanagrafe().getIndiceprofiling();
				this.datEventoStatoInAnagrafe = sapLav.getDatiamministrativi().getStatoinanagrafe().getDataevento();
			}
		} else {
			if (xmlSAP != null && xmlSAP.trim().startsWith("X001")) {
				this.isRaggiuntoLimiteChiamate = true;
				this.descrizioneErroreLimite = xmlSAP.trim();
			}
		}
		_logger.debug("FINE CHIAMATA RICHIESTA SAP, codMinSap =" + codMinSap);

		return sapLav;
	}

	private LavoratoreType convertToLavoratoreSAP(String xmlSAP) throws JAXBException {
		JAXBContext jaxbContext;
		LavoratoreType sap = null;

		jaxbContext = JAXBContext.newInstance(it.eng.sil.pojo.yg.sap.ObjectFactory.class);
		Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
		JAXBElement<LavoratoreType> root = (JAXBElement<LavoratoreType>) jaxbUnmarshaller
				.unmarshal(new StringReader(xmlSAP));
		sap = root.getValue();

		return sap;
	}

	private it.eng.sil.pojo.yg.sap.due.LavoratoreType convertToLavoratoreSAP2(String xmlSAP) throws JAXBException {
		JAXBContext jaxbContext;
		it.eng.sil.pojo.yg.sap.due.LavoratoreType sap = null;

		jaxbContext = JAXBContext.newInstance(it.eng.sil.pojo.yg.sap.due.ObjectFactory.class);
		Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
		JAXBElement<it.eng.sil.pojo.yg.sap.due.LavoratoreType> root = (JAXBElement<it.eng.sil.pojo.yg.sap.due.LavoratoreType>) jaxbUnmarshaller
				.unmarshal(new StringReader(xmlSAP));
		sap = root.getValue();

		return sap;
	}

	public String convertLavoratoreTypeToString(LavoratoreType lavoratoreSAP) throws JAXBException {
		StringWriter stringWriter = new StringWriter();

		JAXBContext jaxbContext = JAXBContext.newInstance(LavoratoreType.class);
		Marshaller jaxbMarshaller = jaxbContext.createMarshaller();

		QName qName = new QName("", "lavoratore");
		JAXBElement<LavoratoreType> root = new JAXBElement<LavoratoreType>(qName, LavoratoreType.class, lavoratoreSAP);

		jaxbMarshaller.marshal(root, stringWriter);

		String result = stringWriter.toString();

		return result;
	}

	public String convertLavoratoreTypeToString2(it.eng.sil.pojo.yg.sap.due.LavoratoreType lavoratoreSAP)
			throws JAXBException {
		StringWriter stringWriter = new StringWriter();

		JAXBContext jaxbContext = JAXBContext.newInstance(it.eng.sil.pojo.yg.sap.due.LavoratoreType.class);
		Marshaller jaxbMarshaller = jaxbContext.createMarshaller();

		QName qName = new QName("", "lavoratore");
		JAXBElement<it.eng.sil.pojo.yg.sap.due.LavoratoreType> root = new JAXBElement<it.eng.sil.pojo.yg.sap.due.LavoratoreType>(
				qName, it.eng.sil.pojo.yg.sap.due.LavoratoreType.class, lavoratoreSAP);

		jaxbMarshaller.marshal(root, stringWriter);

		String result = stringWriter.toString();

		return result;
	}

	private String convertRichiestaSapToString(IDSAP richiestaSAP) throws JAXBException {
		JAXBContext jc = JAXBContext.newInstance(IDSAP.class);
		Marshaller marshaller = jc.createMarshaller();
		StringWriter writer = new StringWriter();
		marshaller.marshal(richiestaSAP, writer);
		String xmlRichiestaSAP = writer.getBuffer().toString();
		return xmlRichiestaSAP;
	}

	private String convertVerificaSapToString(VerificaSAP verifica) throws JAXBException {
		JAXBContext jc = JAXBContext.newInstance(VerificaSAP.class);
		Marshaller marshaller = jc.createMarshaller();
		StringWriter writer = new StringWriter();
		marshaller.marshal(verifica, writer);
		String xmlVerificaSAP = writer.getBuffer().toString();
		return xmlVerificaSAP;
	}

	/**
	 * committa e ripristina la transazione
	 * 
	 * @param trans
	 * @return
	 * @throws EMFInternalError
	 */
	private TransactionQueryExecutor getCommitTransaction(TransactionQueryExecutor trans) throws EMFInternalError {
		trans.commitTransaction();
		try {
			trans = new TransactionQueryExecutor(getPool(), this);
		} catch (NullPointerException e) {
			trans = new TransactionQueryExecutor(Values.DB_SIL_DATI, this);
		}

		enableTransactions(trans);
		trans.initTransaction();

		return trans;
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

	public RequestContainer getReqContainer() {
		return reqContainer;
	}

	public void setReqContainer(RequestContainer reqContainer) {
		this.reqContainer = reqContainer;
	}

	public String getFlgInvioSap() {
		return flgInvioSap;
	}

	public void setFlgInvioSap(String flgInvioSap) {
		this.flgInvioSap = flgInvioSap;
	}

	public String getNoteErrori() {
		return noteErrori;
	}

	public void setNoteErrori(String noteErrori) {
		this.noteErrori = noteErrori;
	}

	public boolean isNotOverrideNote() {
		return notOverrideNote;
	}

	public void setNotOverrideNote(boolean notOverrideNote) {
		this.notOverrideNote = notOverrideNote;
	}

	public BigDecimal getPrgEstrazioneSap() {
		return prgEstrazioneSap;
	}

	public void setPrgEstrazioneSap(BigDecimal prgEstrazioneSap) {
		this.prgEstrazioneSap = prgEstrazioneSap;
	}

	/*
	 * ora solo sap 2 public SourceBean getSbDataSap2() { return sbDataSap2; }
	 * 
	 * public void setSbDataSap2(SourceBean sbDataSap2) { this.sbDataSap2 = sbDataSap2; }
	 * 
	 */
	public SourceBean getSbAnLavoratore() {
		return sbAnLavoratore;
	}

	public void setSbAnLavoratore(SourceBean sbAnLavoratore) {
		this.sbAnLavoratore = sbAnLavoratore;
	}

	public SourceBean getSbCpiLavoratore() {
		return sbCpiLavoratore;
	}

	public void setSbCpiLavoratore(SourceBean sbCpiLavoratore) {
		this.sbCpiLavoratore = sbCpiLavoratore;
	}

	public SourceBean getSbSpLavoratore() {
		return sbSpLavoratore;
	}

	public void setSbSpLavoratore(SourceBean sbSpLavoratore) {
		this.sbSpLavoratore = sbSpLavoratore;
	}

	public SourceBean getSbCpiMinProvincia() {
		return sbCpiMinProvincia;
	}

	public void setSbCpiMinProvincia(SourceBean sbCpiMinProvincia) {
		this.sbCpiMinProvincia = sbCpiMinProvincia;
	}

	public SourceBean getSbMovimentiRipetuti() {
		return sbMovimentiRipetuti;
	}

	public void setSbMovimentiRipetuti(SourceBean sbMovimentiRipetuti) {
		this.sbMovimentiRipetuti = sbMovimentiRipetuti;
	}

	public SourceBean getSbMovimentiSuccessivi() {
		return sbMovimentiSuccessivi;
	}

	public void setSbMovimentiSuccessivi(SourceBean sbMovimentiSuccessivi) {
		this.sbMovimentiSuccessivi = sbMovimentiSuccessivi;
	}

	public SourceBean getSbMovimentiPrecedenti() {
		return sbMovimentiPrecedenti;
	}

	public void setSbMovimentiPrecedenti(SourceBean sbMovimentiPrecedenti) {
		this.sbMovimentiPrecedenti = sbMovimentiPrecedenti;
	}

	public SourceBean getSbMovimentiSuccApprofondita() {
		return sbMovimentiSuccApprofondita;
	}

	public void setSbMovimentiSuccApprofondita(SourceBean sbMovimentiSuccApprofondita) {
		this.sbMovimentiSuccApprofondita = sbMovimentiSuccApprofondita;
	}

	public SourceBean getSbMovimentiPrecApprofondita() {
		return sbMovimentiPrecApprofondita;
	}

	public void setSbMovimentiPrecApprofondita(SourceBean sbMovimentiPrecApprofondita) {
		this.sbMovimentiPrecApprofondita = sbMovimentiPrecApprofondita;
	}

	public SourceBean getSbMovimentiNoSuccPuntatoPrec() {
		return sbMovimentiNoSuccPuntatoPrec;
	}

	public void setSbMovimentiNoSuccPuntatoPrec(SourceBean sbMovimentiNoSuccPuntatoPrec) {
		this.sbMovimentiNoSuccPuntatoPrec = sbMovimentiNoSuccPuntatoPrec;
	}

	public SourceBean getSbMovimentiNoPrecPuntatoSucc() {
		return sbMovimentiNoPrecPuntatoSucc;
	}

	public void setSbMovimentiNoPrecPuntatoSucc(SourceBean sbMovimentiNoPrecPuntatoSucc) {
		this.sbMovimentiNoPrecPuntatoSucc = sbMovimentiNoPrecPuntatoSucc;
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

	public Vector getParamErroreSAP() {
		return this.paramErroreSAP;
	}

	public boolean isErroreGestito() {
		return erroreGestito;
	}

	public void setErroreGestito(boolean flag) {
		this.erroreGestito = flag;
	}

}