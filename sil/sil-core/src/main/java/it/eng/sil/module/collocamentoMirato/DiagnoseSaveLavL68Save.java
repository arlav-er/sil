package it.eng.sil.module.collocamentoMirato;

import java.math.BigInteger;
import java.util.ArrayList;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.ResponseContainer;
import com.engiweb.framework.base.SessionContainer;
import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.dbaccess.SQLStatements;
import com.engiweb.framework.dbaccess.sql.DataConnection;
import com.engiweb.framework.dbaccess.sql.DataField;
import com.engiweb.framework.dbaccess.sql.command.std.StoredProcedureCommand;
import com.engiweb.framework.dbaccess.sql.result.DataResult;
import com.engiweb.framework.dbaccess.sql.result.PunctualDataResult;
import com.engiweb.framework.util.QueryExecutor;

import it.eng.afExt.utils.MessageCodes;
import it.eng.afExt.utils.ReportOperationResult;
import it.eng.afExt.utils.StringUtils;
import it.eng.afExt.utils.TransactionQueryExecutor;
import it.eng.sil.module.AbstractSimpleModule;

public class DiagnoseSaveLavL68Save extends AbstractSimpleModule {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(DiagnoseSaveLavL68Save.class.getName());

	private String className = this.getClass().getName();

	public void service(SourceBean serviceRequest, SourceBean serviceResponse) throws Exception {

		RequestContainer requestContainer = getRequestContainer();
		ResponseContainer responseContainer = getResponseContainer();
		SessionContainer sessionContainer = requestContainer.getSessionContainer();

		boolean check = true;
		String resultUpdateL68 = "1";
		int idSuccess = this.disableMessageIdSuccess();
		int idFail = this.disableMessageIdFail();
		int msgCode = MessageCodes.General.INSERT_FAIL;
		TransactionQueryExecutor transExec = null;
		ReportOperationResult reportOperation = new ReportOperationResult(this, serviceResponse);
		try {
			transExec = new TransactionQueryExecutor(getPool());
			this.enableTransactions(transExec);

			transExec.initTransaction();

			String cdnLavoratore = StringUtils.getAttributeStrNotNull(serviceRequest, "cdnLavoratore");
			String flgdisplim = StringUtils.getAttributeStrNotNull(serviceRequest, "flgdisplim");

			// insert o update
			String message = StringUtils.getAttributeStrNotNull(serviceRequest, "message");

			// String prgDiagnosiFunzionale = (String)
			// getRequestContainer().getSessionContainer().getAttribute("prgDiagnosiFunzionale");

			String cdut = getRequestContainer().getSessionContainer().getAttribute("_CDUT_").toString();

			// true - update, false - insert

			if (("false").equalsIgnoreCase(message)) {
				this.setSectionQueryInsert("QUERY_INSERT");
				check = doInsert(serviceRequest, serviceResponse);
			} else {
				this.setSectionQueryUpdate("QUERY_UPDATE");
				check = doUpdate(serviceRequest, serviceResponse);
			}

			if (!check) {
				throw new Exception("Errore durante l'aggiornamento del la l68. Operazione interrotta");
			} else {

				SourceBean rowscSourceBean = (SourceBean) QueryExecutor.executeQuery("ST_GetConfig_CMPART", null,
						"SELECT", "SIL_DATI");
				String Config_CMPART = (String) rowscSourceBean.getAttribute("ROW.NUM");
				if (Config_CMPART == null) {
					Config_CMPART = "0";
				}

				if ("1".equals(Config_CMPART)) {
					if (cdnLavoratore != null && !("").equals(cdnLavoratore)) {
						resultUpdateL68 = UpdateScoperturaL68(transExec, serviceRequest, serviceResponse, cdnLavoratore,
								flgdisplim, cdut);
						if (!"0".equals(resultUpdateL68)) {
							throw new Exception("Errore durante l'aggiornamento del la l68. Operazione interrotta");
						}
					}
				}
			}

			transExec.commitTransaction();

			reportOperation.reportSuccess(idSuccess);

		} catch (Exception e) {
			transExec.rollBackTransaction();
			reportOperation.reportFailure(msgCode, e, "services()", "insert in transazione");

		} finally {
		}

	}

	public String UpdateScoperturaL68(TransactionQueryExecutor txExecutor, SourceBean request, SourceBean response,
			String p_cdnLavoratore, String flgdisplim, String cdut) {
		DataConnection conn = null;
		DataResult dr = null;
		DataResult dr2 = null;
		StoredProcedureCommand command = null;
		StoredProcedureCommand command2 = null;
		ReportOperationResult ror = new ReportOperationResult(this, response);
		String codiceRit = "";
		try {
			String pool = (String) getConfig().getAttribute("POOL");
			SourceBean statementSB = null;
			String statement = "";
			String sqlStr = "";
			String errCode = "";
			int paramIndex = 0;
			ArrayList parameters = null;
			conn = txExecutor.getDataConnection();
			// INSERIRE MOD E STATEMENT
			statementSB = (SourceBean) getConfig().getAttribute("AGGIORNASCOPERTURA_L68");
			statement = statementSB.getAttribute("STATEMENT").toString();
			sqlStr = SQLStatements.getStatement(statement);
			command = (StoredProcedureCommand) conn.createStoredProcedureCommand(sqlStr);
			String encryptKey = (String) getRequestContainer().getSessionContainer().getAttribute("_ENCRYPTER_KEY_");
			// String cdn = StringUtils.getAttributeStrNotNull(request,"CDNUTMOD");

			parameters = new ArrayList(5);

			// 1.Parametro di Ritorno
			parameters.add(conn.createDataField("codiceRit", java.sql.Types.VARCHAR, null));
			command.setAsOutputParameters(paramIndex++);

			// 3. cdnlavoratore
			parameters.add(conn.createDataField("p_cdnLavoratore", java.sql.Types.VARCHAR, p_cdnLavoratore));
			command.setAsInputParameters(paramIndex++);

			// 2.Flag Diagno Funs
			parameters.add(conn.createDataField("flgdisplim", java.sql.Types.VARCHAR, flgdisplim));
			command.setAsInputParameters(paramIndex++);

			// 4. p_key
			parameters.add(conn.createDataField("p_encrypterKey", java.sql.Types.VARCHAR, encryptKey));
			command.setAsInputParameters(paramIndex++);

			// 5. cdn
			parameters.add(conn.createDataField("utenza", java.sql.Types.BIGINT, new BigInteger(cdut)));
			command.setAsInputParameters(paramIndex++);

			// Chiamata alla Stored Procedure
			dr = command.execute(parameters);
			PunctualDataResult pc = (PunctualDataResult) dr.getDataObject();
			DataField df = pc.getPunctualDatafield();
			codiceRit = df.getStringValue();

			// Predispongo la Response
			if (!codiceRit.equals("0")) {
				int msgCode = 0;
				String msg = null;
				switch (Integer.parseInt(codiceRit)) {
				case -1: // errore generico sql
					msgCode = MessageCodes.General.OPERATION_FAIL;
					msg = "Problema lavoratore L68. sqlCode=" + errCode;
					break;
				default:
					msgCode = MessageCodes.General.OPERATION_FAIL;
					msg = "Problema lavoratore L68. SqlCode=" + errCode;
				}
				_logger.debug(msg);
				return "-1";
			}
		} catch (Exception e) {
			String msg = "Errore nella chiamata alla Stored Procedure Aggiorna L68 - flag Diagnosi Funs";
			_logger.debug(msg);
			return "-1";
		}

		return codiceRit;

	}
}
