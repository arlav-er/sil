package it.eng.sil.module.neet.gg;

import java.math.BigDecimal;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.base.SourceBeanException;
import com.engiweb.framework.dbaccess.SQLStatements;
import com.engiweb.framework.dbaccess.sql.DataConnection;
import com.engiweb.framework.dbaccess.sql.DataField;
import com.engiweb.framework.util.QueryExecutorObject;

import it.eng.afExt.dbaccess.sql.DBKeyGenerator;
import it.eng.afExt.utils.DateUtils;
import it.eng.afExt.utils.MessageCodes;
import it.eng.afExt.utils.ReportOperationResult;
import it.eng.afExt.utils.SourceBeanUtils;
import it.eng.sil.Values;
import it.eng.sil.coop.DataSourceJNDI;
import it.eng.sil.coop.endpoints.EndPointSingleton;
import it.eng.sil.module.AbstractSimpleModule;
import it.eng.sil.security.User;
import it.eng.sil.util.Utils;
import it.eng.sil.utils.gg.GG_Utils;
import it.eng.sil.utils.gg.Properties;
import it.gov.mlps.DataModels.InformationDelivery.VerificaCondizioniNEET._1_0.CondizioneNEET;
import it.gov.mlps.DataModels.InformationDelivery.VerificaCondizioniNEET._1_0.VerificaCondizioniNEET_Input;
import it.gov.mlps.DataModels.InformationDelivery.VerificaCondizioniNEET._1_0.VerificaCondizioniNEET_Output;
import it.gov.mlps.Services.InformationDelivery.VerificaCondizioniNEET._1_0.VerificaCondizioniNEET_PortTypeProxy;

public class GestioneNeetGG extends AbstractSimpleModule {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6257935514513190940L;

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(GestioneNeetGG.class.getName());

	private String END_POINT_NAME = "VerificaCondizioniNeet_YG";

	Object utility = null;
	@Override
	public void service(SourceBean serviceRequest, SourceBean serviceResponse) throws Exception {

		User user = (User) getRequestContainer().getSessionContainer().getAttribute(User.USERID);

		ReportOperationResult reportOperation = new ReportOperationResult(this, serviceResponse);
		BigDecimal p_cdnUtente = new BigDecimal(user.getCodut());


		String cdnLavoratore = Utils.notNull(serviceRequest.getAttribute("CDNLAVORATORE"));
		String strCodiceFiscale = Utils.notNull(serviceRequest.getAttribute("CF"));
		String dataRiferimento = Utils.notNull(serviceRequest.getAttribute("DATRIFERIMENTONEET"));
		String tipoVerifica  = Utils.notNull(serviceRequest.getAttribute("verificaNeet"));
		String codiceRegioneMin = Utils.notNull(serviceRequest.getAttribute("REGIONE"));


		disableMessageIdFail();
		disableMessageIdSuccess();

		QueryExecutorObject qExec = null;
		DataConnection dc = null;

		try{	

			DataSourceJNDI dataSourceJndi = new DataSourceJNDI();
			String dataSourceJndiName = dataSourceJndi.getJndi();
			EndPointSingleton eps = EndPointSingleton.getInstance(dataSourceJndiName);
			
			String endPointProfilingGG =eps.getUrl(END_POINT_NAME);
			VerificaCondizioniNEET_PortTypeProxy serviziWSProxy = new VerificaCondizioniNEET_PortTypeProxy(endPointProfilingGG);
			
			VerificaCondizioniNEET_Input inputVerifica = new VerificaCondizioniNEET_Input();
			inputVerifica.setCodiceFiscale(strCodiceFiscale);
			inputVerifica.setDataRiferimento(DateUtils.datestringToXml(dataRiferimento).toGregorianCalendar()); 
			inputVerifica.setGUIDUtente(codiceRegioneMin);
			inputVerifica.setCodiceFiscaleOperatore(Properties.CF_OPERATORE);
			inputVerifica.setApplicazione(Properties.APPLICAZIONE);
			
			
			VerificaCondizioniNEET_Output outputVerifica =serviziWSProxy.openSPCoop_PD(inputVerifica);
			
			qExec = GG_Utils.getQueryExecutorObject();
			dc = qExec.getDataConnection();
			
			if(insertAmYgNeet(serviceResponse, reportOperation,	p_cdnUtente, cdnLavoratore, qExec,dc, outputVerifica, tipoVerifica)){
				serviceResponse.setAttribute("TIPO_VERIFICA_NEET", tipoVerifica);
				serviceResponse.setAttribute("NEET_GG_WS", outputVerifica);
				reportOperation.reportSuccess(MessageCodes.General.OPERATION_SUCCESS);

			}else{
				reportOperation.reportFailure(Properties.VERIFICA_CONDIZIONI_KO);
			}
		}catch (Throwable e) {
			reportOperation.reportFailure(Properties.VERIFICA_CONDIZIONI_KO);
			_logger.error("Errore: " + e);
		} finally {
			_logger.info("Connessione rilasciata");
			com.engiweb.framework.dbaccess.Utils.releaseResources(dc, null, null);
		}
	}

	private boolean insertAmYgNeet(SourceBean serviceResponse,
			ReportOperationResult reportOperation, BigDecimal p_cdnUtente,
			String cdnLavoratore, QueryExecutorObject qExec, DataConnection dc, 
			VerificaCondizioniNEET_Output verificaNeet, String tipoVerifica) throws SourceBeanException {
		
		
		qExec.setStatement(SQLStatements.getStatement("GET_FLAG_VERIFICA_NEET"));
		qExec.setType(QueryExecutorObject.SELECT);
		List<DataField> param = new ArrayList<DataField>();
		param.add(dc.createDataField("CODVERIFICANEET", Types.VARCHAR, tipoVerifica));
		qExec.setInputParameters(param);
		SourceBean flgCk = (SourceBean) qExec.exec();
		
		String flgEta = null, flgRes = null, flgDis = null, flgScuola = null, flgUni = null;
		String flgEtaCk = null, flgResCk = null, flgDisCk = null, flgScuolaCk = null, flgUniCk = null;
		boolean isFlgNeet = true;
		CondizioneNEET[] condizioni = verificaNeet.getCondizioniNEET();
		for(int i =0; i < condizioni.length; i++){
			CondizioneNEET cond = condizioni[i];
			String decodifica = cond.getDecodifica();
			if(decodifica.equalsIgnoreCase(Properties.ETA)){
				flgEta = cond.isNEET()? "S" : "N";
				flgEtaCk = SourceBeanUtils.getAttrStrNotNull(flgCk, "ROW.FLG_CK_ETA");
				isFlgNeet = isFlgNeet && (flgEtaCk.equalsIgnoreCase("N") || (flgEtaCk.equalsIgnoreCase("S") && flgEta.equalsIgnoreCase(flgEtaCk)));
			}
			if(decodifica.equalsIgnoreCase(Properties.RESIDENZA)){
				flgRes = cond.isNEET()? "S" : "N";
				flgResCk = SourceBeanUtils.getAttrStrNotNull(flgCk, "ROW.FLG_CK_RES");
				isFlgNeet = isFlgNeet && (flgResCk.equalsIgnoreCase("N") || (flgResCk.equalsIgnoreCase("S") && flgRes.equalsIgnoreCase(flgResCk)));
			}
			if(decodifica.equalsIgnoreCase(Properties.DISOCCUPAZIONE)){
				flgDis = cond.isNEET()? "S" : "N";
				flgDisCk = SourceBeanUtils.getAttrStrNotNull(flgCk, "ROW.FLG_CK_DIS");
				isFlgNeet = isFlgNeet && (flgDisCk.equalsIgnoreCase("N") || (flgDisCk.equalsIgnoreCase("S") && flgDis.equalsIgnoreCase(flgDisCk)));
			}
			if(decodifica.equalsIgnoreCase(Properties.SCUOLA)){
				flgScuola = cond.isNEET()? "S" : "N";
				flgScuolaCk = SourceBeanUtils.getAttrStrNotNull(flgCk, "ROW.FLG_CK_SCU");
				isFlgNeet = isFlgNeet && (flgScuolaCk.equalsIgnoreCase("N") ||(flgScuolaCk.equalsIgnoreCase("S") && flgScuola.equalsIgnoreCase(flgScuolaCk)));
			}
			if(decodifica.equalsIgnoreCase(Properties.UNIVERSITA)){
				flgUni = cond.isNEET()? "S" : "N";
				flgUniCk = SourceBeanUtils.getAttrStrNotNull(flgCk, "ROW.FLG_CK_UNI");
				isFlgNeet = isFlgNeet && (flgUniCk.equalsIgnoreCase("N") || (flgUniCk.equalsIgnoreCase("S") && flgUni.equalsIgnoreCase(flgUniCk)));
			}
		}
		
		//GET PRG AM_YG_VERIFICA_NEET
		BigDecimal prgVerificaNeet = DBKeyGenerator.getNextSequence(Values.DB_SIL_DATI, "S_AM_YG_VERIFICA_NEET");

		// inserisco il record in AM_YG_VERIFICA_NEET
		qExec.setStatement(SQLStatements.getStatement("INSERT_AM_YG_VERIFICA_NEET"));
		qExec.setType(QueryExecutorObject.INSERT);
		List<DataField> params = new ArrayList<DataField>();
		params.add(dc.createDataField("PRGVERIFICANEET", Types.NUMERIC, prgVerificaNeet));
		params.add(dc.createDataField("FLGSCUOLA", Types.CHAR, flgScuola));
		params.add(dc.createDataField("CDNUTINS", Types.NUMERIC, p_cdnUtente));
		params.add(dc.createDataField("CDNLAVORATORE", Types.NUMERIC, new BigDecimal(cdnLavoratore)));
		params.add(dc.createDataField("CODVERIFICANEET", Types.VARCHAR, tipoVerifica));
		params.add(dc.createDataField("CODESITO", Types.VARCHAR, verificaNeet.getEsito()));
		params.add(dc.createDataField("FLGUNI", Types.CHAR, flgUni));
		params.add(dc.createDataField("DATRIFERIMENTO", Types.TIMESTAMP, GG_Utils.getTSorNull(verificaNeet.getDataRiferimento().getTime())));
		params.add(dc.createDataField("FLGDIS", Types.CHAR, flgDis));
		params.add(dc.createDataField("STRCODICEFISCALE", Types.VARCHAR, verificaNeet.getCodiceFiscale()));
		params.add(dc.createDataField("FLGETA", Types.CHAR, flgEta));
		params.add(dc.createDataField("CDNUTMOD", Types.NUMERIC, p_cdnUtente));
		params.add(dc.createDataField("FLGNEET", Types.CHAR, isFlgNeet? "S" : "N"));
		serviceResponse.setAttribute("ESITO_VERIFICA_NEET", isFlgNeet? "OK" : "KO");
		params.add(dc.createDataField("FLGRES", Types.CHAR, flgRes));
		
		qExec.setInputParameters(params);
		Object result = qExec.exec();
		boolean insertAmYgNeet;
		if (result == null || !(result instanceof Boolean && ((Boolean) result).booleanValue() == true)) {
			_logger.error("Impossibile eseguire insert dei dati in AM_YG_VERIFICA_NEET");
			insertAmYgNeet = Boolean.FALSE;
		}else{
			insertAmYgNeet = Boolean.TRUE;
			serviceResponse.setAttribute("PRGVERIFICANEET", prgVerificaNeet);
		}

		return insertAmYgNeet;
	}


}
