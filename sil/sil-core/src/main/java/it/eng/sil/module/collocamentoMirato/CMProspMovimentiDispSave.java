package it.eng.sil.module.collocamentoMirato;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.SessionContainer;
import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.base.SourceBeanException;
import com.engiweb.framework.dbaccess.SQLStatements;
import com.engiweb.framework.dbaccess.sql.DataConnection;
import com.engiweb.framework.dbaccess.sql.DataField;
import com.engiweb.framework.dbaccess.sql.command.std.StoredProcedureCommand;
import com.engiweb.framework.dbaccess.sql.result.DataResult;
import com.engiweb.framework.dbaccess.sql.result.PunctualDataResult;
import com.engiweb.framework.dbaccess.sql.result.std.CompositeDataResult;

import it.eng.afExt.utils.MessageCodes;
import it.eng.afExt.utils.ReportOperationResult;
import it.eng.afExt.utils.StringUtils;
import it.eng.afExt.utils.TransactionQueryExecutor;
import it.eng.sil.module.AbstractSimpleModule;
import it.eng.sil.security.User;

public class CMProspMovimentiDispSave extends AbstractSimpleModule {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger
			.getLogger(CMProspMovimentiDispSave.class.getName());

	private String className = this.getClass().getName();

	public void service(SourceBean serviceRequest, SourceBean serviceResponse) throws Exception {

		RequestContainer requestContainer = getRequestContainer();
		SessionContainer sessionContainer = requestContainer.getSessionContainer();

		boolean check = true;
		int idSuccess = this.disableMessageIdSuccess();
		int idFail = this.disableMessageIdFail();
		int msgCode = MessageCodes.General.INSERT_FAIL;
		TransactionQueryExecutor transExec = null;
		ReportOperationResult reportOperation = new ReportOperationResult(this, serviceResponse);
		try {
			transExec = new TransactionQueryExecutor(getPool());
			this.enableTransactions(transExec);

			transExec.initTransaction();

			String prgProspettoInf = StringUtils.getAttributeStrNotNull(serviceRequest, "PRGPROSPETTOINF");
			String message = StringUtils.getAttributeStrNotNull(serviceRequest, "MESSAGE");

			// vettore contente la coppia prg (Movimento o NullaOsta) e un
			// valore che identifica il tipo (Mov o NO)
			// es. 768-M: 768 è il prg e "M" indica che si tratta di un
			// movimento
			// es. 768-N: 768 è il prg della tabella e "N" indica che si tratta
			// di un Nulla Osta
			Vector prgMovVector = serviceRequest.getAttributeAsVector("CK_MOV");

			boolean success = false;

			// ciclo su tutti i movimenti checkati
			for (int i = 0; i < prgMovVector.size(); i++) {

				String prgIesimo = (String) prgMovVector.get(i);

				Vector prgVectorMovNO = StringUtils.split(prgIesimo, "-");
				String prg = (String) prgVectorMovNO.get(0);
				String checkMov = (String) prgVectorMovNO.get(1);

				if (("N").equalsIgnoreCase(checkMov)) {
					success = insertLavNullaOsta(transExec, serviceRequest, serviceResponse, prg, prgProspettoInf);
				} else if (("M").equalsIgnoreCase(checkMov)) {
					success = insertLavMovimentiDisp(transExec, serviceRequest, serviceResponse, prg, prgProspettoInf);
				}

				if (!success) {
					break;
				}
			}

			if (success) {
				// reportOperation.reportSuccess(MessageCodes.General.OPERATION_SUCCESS);
				transExec.commitTransaction();
				reportOperation.reportSuccess(idSuccess);
			} else {
				transExec.rollBackTransaction();
			}

		} catch (Exception e) {
			transExec.rollBackTransaction();
			reportOperation.reportFailure(msgCode, e, "services()", "insert in transazione");

		} finally {
		}
	}

	public boolean insertLavMovimentiDisp(TransactionQueryExecutor txExecutor, SourceBean request, SourceBean response,
			String p_prgMovimentoIesimo, String p_prgProspettoInf) {

		User user = (User) getRequestContainer().getSessionContainer().getAttribute("@@USER@@");
		boolean checkSuccess = false;
		DataConnection conn = null;
		DataResult dr = null;
		DataResult dr2 = null;
		StoredProcedureCommand command = null;
		StoredProcedureCommand command2 = null;
		ReportOperationResult ror = new ReportOperationResult(this, response);

		try {
			String pool = (String) getConfig().getAttribute("POOL");
			conn = txExecutor.getDataConnection();
			SourceBean statementSB = null;
			String statement = "";
			String sqlStr = "";
			String codiceRit = "";
			String errCode = "";

			int paramIndex = 0;
			ArrayList parameters = null;

			statementSB = (SourceBean) getConfig().getAttribute("CM_INSERT_LAV_MOVIMENTI");
			statement = statementSB.getAttribute("STATEMENT").toString();
			sqlStr = SQLStatements.getStatement(statement);
			command = (StoredProcedureCommand) conn.createStoredProcedureCommand(sqlStr);

			String p_prgMov = p_prgMovimentoIesimo;
			String p_cdnUtente = String.valueOf(user.getCodut());

			String encryptKey = (String) getRequestContainer().getSessionContainer().getAttribute("_ENCRYPTER_KEY_");

			parameters = new ArrayList(6);
			// 1.Parametro di Ritorno
			parameters.add(conn.createDataField("codiceRit", java.sql.Types.VARCHAR, null));
			command.setAsOutputParameters(paramIndex++);
			// preparazione dei Parametri di Input
			// 2. p_prgMov
			parameters.add(conn.createDataField("p_prgMov", java.sql.Types.BIGINT, new BigInteger(p_prgMov)));
			command.setAsInputParameters(paramIndex++);
			// 3. p_prgProspettoInf
			parameters.add(conn.createDataField("p_prgProspettoInf", java.sql.Types.BIGINT,
					new BigInteger(p_prgProspettoInf)));
			command.setAsInputParameters(paramIndex++);
			// 4. p_cdnUtente
			parameters.add(conn.createDataField("p_cdnUtente", java.sql.Types.BIGINT, new BigInteger(p_cdnUtente)));
			command.setAsInputParameters(paramIndex++);
			// 5. p_key
			parameters.add(conn.createDataField("p_key", java.sql.Types.VARCHAR, encryptKey));
			command.setAsInputParameters(paramIndex++);

			// parametri di Output
			// 5. p_errCode
			parameters.add(conn.createDataField("p_errCode", java.sql.Types.VARCHAR, null));
			command.setAsOutputParameters(paramIndex++);

			// Chiamata alla Stored Procedure
			dr = command.execute(parameters);
			CompositeDataResult cdr = (CompositeDataResult) dr.getDataObject();
			List outputParams = cdr.getContainedDataResult();
			// Reperisco i valori di output della stored
			// 0. Codice di Ritorno
			PunctualDataResult pdr = (PunctualDataResult) outputParams.get(0);
			DataField df = pdr.getPunctualDatafield();
			codiceRit = df.getStringValue();
			// 1. ErrCodeOut
			pdr = (PunctualDataResult) outputParams.get(1);
			df = pdr.getPunctualDatafield();
			errCode = df.getStringValue();

			SourceBean row = new SourceBean("ROW");
			row.setAttribute("CodiceRit", codiceRit);
			// Predispongo la Response
			if (!codiceRit.equals("0")) {
				int msgCode = 0, debugLevel = 0;
				String msg = null;
				switch (Integer.parseInt(codiceRit)) {
				case -1: // errore generico sql
					msgCode = MessageCodes.General.ELEMENT_DUPLICATED;
					msg = "Inserimento Lavoratore in riserva: sqlCode=" + errCode;
					// TracerSingleton.CRITICAL;
					break;
				default:
					msgCode = MessageCodes.General.INSERT_FAIL;
					msg = "Inserimento Lavoratore in riserva: errore di ritorno non ammesso. SqlCode=" + errCode;
					// TracerSingleton.CRITICAL;
				}
				response.setAttribute("error", "true");
				ror.reportFailure(msgCode);
				_logger.debug(msg);

			} else {
				checkSuccess = true;
			}
			response.setAttribute((SourceBean) row);

		} catch (Exception e) {
			String msg = "Errore nella chiamata alla Stored Procedure ";
			try {
				response.setAttribute("row.codiceRit", "-1");
			} catch (SourceBeanException e1) {
			}
			ror.reportFailure(MessageCodes.General.ELEMENT_DUPLICATED, e, className, msg);
		} finally {

		}

		return checkSuccess;
	}

	public boolean insertLavNullaOsta(TransactionQueryExecutor txExecutor, SourceBean request, SourceBean response,
			String p_prgNullaOstaIesimo, String p_prgProspettoInf) {

		User user = (User) getRequestContainer().getSessionContainer().getAttribute("@@USER@@");
		boolean checkSuccess = false;
		DataConnection conn = null;
		DataResult dr = null;
		DataResult dr2 = null;
		StoredProcedureCommand command = null;
		StoredProcedureCommand command2 = null;
		ReportOperationResult ror = new ReportOperationResult(this, response);

		try {
			String pool = (String) getConfig().getAttribute("POOL");
			conn = txExecutor.getDataConnection();
			SourceBean statementSB = null;
			String statement = "";
			String sqlStr = "";
			String codiceRit = "";
			String errCode = "";

			int paramIndex = 0;
			ArrayList parameters = null;

			statementSB = (SourceBean) getConfig().getAttribute("CM_INSERT_LAV_NULLAOSTA");
			statement = statementSB.getAttribute("STATEMENT").toString();
			sqlStr = SQLStatements.getStatement(statement);
			command = (StoredProcedureCommand) conn.createStoredProcedureCommand(sqlStr);

			String p_prgNo = p_prgNullaOstaIesimo;
			String p_cdnUtente = String.valueOf(user.getCodut());

			parameters = new ArrayList(5);
			// 1.Parametro di Ritorno
			parameters.add(conn.createDataField("codiceRit", java.sql.Types.VARCHAR, null));
			command.setAsOutputParameters(paramIndex++);
			// preparazione dei Parametri di Input
			// 2. p_prgMov
			parameters.add(conn.createDataField("p_prgNO", java.sql.Types.BIGINT, new BigInteger(p_prgNo)));
			command.setAsInputParameters(paramIndex++);
			// 3. p_prgProspettoInf
			parameters.add(conn.createDataField("p_prgProspettoInf", java.sql.Types.BIGINT,
					new BigInteger(p_prgProspettoInf)));
			command.setAsInputParameters(paramIndex++);
			// 4. p_cdnUtente
			parameters.add(conn.createDataField("p_cdnUtente", java.sql.Types.BIGINT, new BigInteger(p_cdnUtente)));
			command.setAsInputParameters(paramIndex++);

			// parametri di Output
			// 5. p_errCode
			parameters.add(conn.createDataField("p_errCode", java.sql.Types.VARCHAR, null));
			command.setAsOutputParameters(paramIndex++);

			// Chiamata alla Stored Procedure
			dr = command.execute(parameters);
			CompositeDataResult cdr = (CompositeDataResult) dr.getDataObject();
			List outputParams = cdr.getContainedDataResult();
			// Reperisco i valori di output della stored
			// 0. Codice di Ritorno
			PunctualDataResult pdr = (PunctualDataResult) outputParams.get(0);
			DataField df = pdr.getPunctualDatafield();
			codiceRit = df.getStringValue();
			// 1. ErrCodeOut
			pdr = (PunctualDataResult) outputParams.get(1);
			df = pdr.getPunctualDatafield();
			errCode = df.getStringValue();

			SourceBean row = new SourceBean("ROW");
			row.setAttribute("CodiceRit", codiceRit);
			// Predispongo la Response
			if (!codiceRit.equals("0")) {
				int msgCode = 0; // , debugLevel = 0;
				String msg = null;
				switch (Integer.parseInt(codiceRit)) {
				case -1: // errore generico sql
					msgCode = MessageCodes.General.ELEMENT_DUPLICATED;
					msg = "Inserimento Lavoratore in riserva: sqlCode=" + errCode;
					// TracerSingleton.CRITICAL;
					break;
				default:
					msgCode = MessageCodes.General.INSERT_FAIL;
					msg = "Inserimento Lavoratore in riserva: errore di ritorno non ammesso. SqlCode=" + errCode;
					// TracerSingleton.CRITICAL;
				}
				response.setAttribute("error", "true");
				ror.reportFailure(msgCode);
				_logger.debug(msg);

			} else {
				checkSuccess = true;
			}
			response.setAttribute((SourceBean) row);

		} catch (Exception e) {
			String msg = "Errore nella chiamata alla Stored Procedure ";
			try {
				response.setAttribute("row.codiceRit", "-1");
			} catch (SourceBeanException e1) {
			}
			ror.reportFailure(MessageCodes.General.ELEMENT_DUPLICATED, e, className, msg);
		} finally {

		}

		return checkSuccess;
	}

}