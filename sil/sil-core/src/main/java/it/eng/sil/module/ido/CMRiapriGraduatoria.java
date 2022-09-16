package it.eng.sil.module.ido;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import com.engiweb.framework.base.SessionContainer;
import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.base.SourceBeanException;
import com.engiweb.framework.dbaccess.DataConnectionManager;
import com.engiweb.framework.dbaccess.SQLStatements;
import com.engiweb.framework.dbaccess.Utils;
import com.engiweb.framework.dbaccess.sql.DataConnection;
import com.engiweb.framework.dbaccess.sql.DataField;
import com.engiweb.framework.dbaccess.sql.command.std.StoredProcedureCommand;
import com.engiweb.framework.dbaccess.sql.result.DataResult;
import com.engiweb.framework.dbaccess.sql.result.PunctualDataResult;
import com.engiweb.framework.dbaccess.sql.result.std.CompositeDataResult;
import com.engiweb.framework.error.EMFInternalError;

import it.eng.afExt.utils.MessageCodes;
import it.eng.afExt.utils.ReportOperationResult;
import it.eng.afExt.utils.StringUtils;
import it.eng.afExt.utils.TransactionQueryExecutor;
import it.eng.sil.module.AbstractSimpleModule;
import it.eng.sil.security.User;

/**
 * 
 * 
 * @author coticone
 * 
 */
public class CMRiapriGraduatoria extends AbstractSimpleModule {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(CMRiapriGraduatoria.class.getName());
	private String className = this.getClass().getName();

	public void service(SourceBean request, SourceBean response) throws EMFInternalError {

		ReportOperationResult ror = new ReportOperationResult(this, response);

		disableMessageIdSuccess();

		int idSuccess = this.disableMessageIdSuccess();
		int idFail = this.disableMessageIdFail();
		boolean errors = false;
		TransactionQueryExecutor transExec = null;
		try {
			transExec = new TransactionQueryExecutor(getPool());
			enableTransactions(transExec);

			transExec.initTransaction();

			// riapriGraduatoria(request, response);
			riaperturaGraduatorie(request, response);

			transExec.commitTransaction();
			ror.reportSuccess(idSuccess);
		} catch (Exception ex) {
			it.eng.sil.util.TraceWrapper.debug(_logger,
					"CMRiapriGraduatoria::service(): Impossibile riaprire la graduatoria!", ex);

			ror.reportFailure(idFail);
			ror.reportFailure(MessageCodes.General.INSERT_FAIL);
			if (transExec != null) {
				transExec.rollBackTransaction();
			}
			errors = true;
		}
	}

	public void riaperturaGraduatorie(SourceBean request, SourceBean response) {

		SessionContainer session = this.getRequestContainer().getSessionContainer();
		DataConnection conn = null;
		DataResult dr = null;
		DataResult dr2 = null;
		StoredProcedureCommand command = null;
		StoredProcedureCommand command2 = null;
		ReportOperationResult ror = new ReportOperationResult(this, response);

		try {
			User user = (User) getRequestContainer().getSessionContainer().getAttribute("@@USER@@");
			String pool = (String) getConfig().getAttribute("POOL");
			DataConnectionManager dcm = DataConnectionManager.getInstance();
			SourceBean statementSB = null;
			String statement = "";
			String sqlStr = "";
			String codiceRit = "";
			String errCode = "";
			String prgIncrocio = "";
			String prgRosa = "";
			int paramIndex = 0;
			ArrayList parameters = null;
			conn = dcm.getConnection(pool);

			statementSB = (SourceBean) getConfig().getAttribute("CM_RIAPRI_GRADUATORIA");
			statement = statementSB.getAttribute("STATEMENT").toString();
			sqlStr = SQLStatements.getStatement(statement);
			command = (StoredProcedureCommand) conn.createStoredProcedureCommand(sqlStr);

			String motivo = StringUtils.getAttributeStrNotNull(request, "MotRiapertura");
			String p_motivo = StringUtils.formatValue4Sql(motivo);
			String p_prgRichiestaAz = StringUtils.getAttributeStrNotNull(request, "PRGRICHIESTAAZ");
			String p_prgRosa = StringUtils.getAttributeStrNotNull(request, "PRGROSA");
			String p_prgTipoIncrocio = StringUtils.getAttributeStrNotNull(request, "PRGTIPOINCROCIO");
			String p_cdnUtente = String.valueOf(user.getCodut());

			parameters = new ArrayList(9);
			// 1.Parametro di Ritorno
			parameters.add(conn.createDataField("codiceRit", java.sql.Types.VARCHAR, null));
			command.setAsOutputParameters(paramIndex++);
			// preparazione dei Parametri di Input
			// 2. p_prgRichiestaAz
			parameters.add(
					conn.createDataField("p_prgRichiestaAz", java.sql.Types.BIGINT, new BigInteger(p_prgRichiestaAz)));
			command.setAsInputParameters(paramIndex++);
			// 3. p_prgTipoIncrocio
			parameters.add(conn.createDataField("p_prgTipoIncrocio", java.sql.Types.BIGINT,
					new BigInteger(p_prgTipoIncrocio)));
			command.setAsInputParameters(paramIndex++);
			// 4. p_prgRosa
			parameters.add(conn.createDataField("p_prgRosa", java.sql.Types.BIGINT, new BigInteger(p_prgRosa)));
			command.setAsInputParameters(paramIndex++);
			// 5. p_motivo
			parameters.add(conn.createDataField("p_motivo", java.sql.Types.VARCHAR, p_motivo));
			command.setAsInputParameters(paramIndex++);
			// 6. p_cdnUtente
			parameters.add(conn.createDataField("p_cdnUtente", java.sql.Types.BIGINT, new BigInteger(p_cdnUtente)));
			command.setAsInputParameters(paramIndex++);

			// parametri di Output
			// 7. p_out_prgIncrocio
			parameters.add(conn.createDataField("p_out_prgIncrocio", java.sql.Types.BIGINT, null));
			command.setAsOutputParameters(paramIndex++);
			// 8. p_out_prgRosa
			parameters.add(conn.createDataField("p_out_prgRosa", java.sql.Types.BIGINT, null));
			command.setAsOutputParameters(paramIndex++);
			// 9. p_errCode
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
			// 1. p_out_prgIncrocio
			pdr = (PunctualDataResult) outputParams.get(1);
			df = pdr.getPunctualDatafield();
			String outPrgIncrocio = df.getStringValue();
			// 2 p_out_prgRosa
			pdr = (PunctualDataResult) outputParams.get(2);
			df = pdr.getPunctualDatafield();
			String outPrgRosa = df.getStringValue();
			// errCode
			pdr = (PunctualDataResult) outputParams.get(3);
			df = pdr.getPunctualDatafield();
			errCode = df.getStringValue();

			request.updAttribute("PRGROSA", outPrgRosa);
			request.updAttribute("PRGINCROCIO", outPrgIncrocio);

			SourceBean row = new SourceBean("ROW");
			row.setAttribute("CodiceRit", codiceRit);
			row.setAttribute("PRGROSA", outPrgRosa);
			row.setAttribute("PRGINCROCIO", outPrgIncrocio);
			// Predispongo la Response
			if (!codiceRit.equals("0")) {
				int msgCode = 0;
				String msg = null;
				switch (Integer.parseInt(codiceRit)) {
				case -1: // errore generico sql
					msgCode = MessageCodes.General.OPERATION_FAIL;
					msg = "Riapri Graduatoria: sqlCode=" + errCode;

					break;
				default:
					msgCode = MessageCodes.General.OPERATION_FAIL;
					msg = "Riapri Graduatoria: errore di ritorno non ammesso. SqlCode=" + errCode;

				}
				response.setAttribute("error", "true");
				ror.reportFailure(msgCode);
				_logger.debug(msg);

			} else {

				String key1 = p_prgRichiestaAz + "_5";
				String key2 = p_prgRichiestaAz + "_6";
				String key3 = p_prgRichiestaAz + "_7";
				String key4 = p_prgRichiestaAz + "_8";
				String key5 = p_prgRichiestaAz + "_9";

				// elimino dalla sessione il SB dei candidati selezionati
				session.delAttribute(key1);
				session.delAttribute(key2);
				session.delAttribute(key3);
				session.delAttribute(key4);
				session.delAttribute(key5);

				ror.reportSuccess(MessageCodes.General.OPERATION_SUCCESS);
			}
			response.setAttribute((SourceBean) row);
		} catch (Exception e) {
			String msg = "Errore nella chiamata alla Stored Procedure ";
			try {
				response.setAttribute("row.codiceRit", "-1");
			} catch (SourceBeanException e1) {
			}
			ror.reportFailure(MessageCodes.General.OPERATION_FAIL, e, className, msg);
		} finally {
			Utils.releaseResources(null, command2, dr2);
			Utils.releaseResources(conn, command, dr);
		}

	}

	public void riapriGraduatoria(SourceBean request, SourceBean response) {
		DataConnection conn = null;
		DataResult dr = null;
		DataResult dr2 = null;
		StoredProcedureCommand command = null;
		StoredProcedureCommand command2 = null;
		ReportOperationResult ror = new ReportOperationResult(this, response);

		try {
			User user = (User) getRequestContainer().getSessionContainer().getAttribute("@@USER@@");
			String pool = (String) getConfig().getAttribute("POOL");
			DataConnectionManager dcm = DataConnectionManager.getInstance();
			SourceBean statementSB = null;
			String statement = "";
			String sqlStr = "";
			String codiceRit = "";
			String errCode = "";
			String prgIncrocio = "";
			String prgRosa = "";
			int paramIndex = 0;
			ArrayList parameters = null;
			conn = dcm.getConnection(pool);

			statementSB = (SourceBean) getConfig().getAttribute("AS_RIAPRI_GRADUATORIA");
			statement = statementSB.getAttribute("STATEMENT").toString();
			sqlStr = SQLStatements.getStatement(statement);
			command = (StoredProcedureCommand) conn.createStoredProcedureCommand(sqlStr);

			String p_prgRosa = StringUtils.getAttributeStrNotNull(request, "PRGROSA");
			String p_prgIncrocio = StringUtils.getAttributeStrNotNull(request, "PRGINCROCIO");
			String p_numklorosa = StringUtils.getAttributeStrNotNull(request, "NUMKLOROSA");
			String p_cdnUtente = String.valueOf(user.getCodut());

			parameters = new ArrayList(8);
			// 1.Parametro di Ritorno
			parameters.add(conn.createDataField("codiceRit", java.sql.Types.VARCHAR, null));
			command.setAsOutputParameters(paramIndex++);
			// preparazione dei Parametri di Input
			// 2. p_prgRosa
			parameters.add(conn.createDataField("p_prgRosa", java.sql.Types.BIGINT, new BigInteger(p_prgRosa)));
			command.setAsInputParameters(paramIndex++);
			// 3. p_numklorosa
			parameters.add(conn.createDataField("p_numklorosa", java.sql.Types.BIGINT, new BigInteger(p_numklorosa)));
			command.setAsInputParameters(paramIndex++);
			// 4. p_prgIncrocio
			parameters.add(conn.createDataField("p_prgIncrocio", java.sql.Types.BIGINT, new BigInteger(p_prgIncrocio)));
			command.setAsInputParameters(paramIndex++);
			// 5. p_cdnUtente
			parameters.add(conn.createDataField("p_cdnUtente", java.sql.Types.BIGINT, new BigInteger(p_cdnUtente)));
			command.setAsInputParameters(paramIndex++);

			// parametri di Output
			// 6. p_out_prgIncrocio
			parameters.add(conn.createDataField("p_out_prgIncrocio", java.sql.Types.BIGINT, null));
			command.setAsOutputParameters(paramIndex++);
			// 7. p_out_prgRosa
			parameters.add(conn.createDataField("p_out_prgRosa", java.sql.Types.BIGINT, null));
			command.setAsOutputParameters(paramIndex++);
			// 8. p_errCode
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
			// 1. p_out_prgIncrocio
			pdr = (PunctualDataResult) outputParams.get(1);
			df = pdr.getPunctualDatafield();
			String outPrgIncrocio = df.getStringValue();
			// 2 p_out_prgRosa
			pdr = (PunctualDataResult) outputParams.get(2);
			df = pdr.getPunctualDatafield();
			String outPrgRosa = df.getStringValue();
			// errCode
			pdr = (PunctualDataResult) outputParams.get(3);
			df = pdr.getPunctualDatafield();
			errCode = df.getStringValue();

			request.updAttribute("PRGROSA", outPrgRosa);
			request.updAttribute("PRGINCROCIO", outPrgIncrocio);

			SourceBean row = new SourceBean("ROW");
			row.setAttribute("CodiceRit", codiceRit);
			row.setAttribute("PRGROSA", outPrgRosa);
			row.setAttribute("PRGINCROCIO", outPrgIncrocio);
			// Predispongo la Response
			if (!codiceRit.equals("0")) {
				int msgCode = 0;
				String msg = null;
				switch (Integer.parseInt(codiceRit)) {
				case -1: // errore generico sql
					msgCode = MessageCodes.General.OPERATION_FAIL;
					msg = "Riapri Graduatoria: sqlCode=" + errCode;

					break;
				default:
					msgCode = MessageCodes.General.OPERATION_FAIL;
					msg = "Riapri Graduatoria: errore di ritorno non ammesso. SqlCode=" + errCode;

				}
				response.setAttribute("error", "true");
				ror.reportFailure(msgCode);
				_logger.debug(msg);

			} else {
				ror.reportSuccess(MessageCodes.General.OPERATION_SUCCESS);
			}
			response.setAttribute((SourceBean) row);
		} catch (Exception e) {
			String msg = "Errore nella chiamata alla Stored Procedure ";
			try {
				response.setAttribute("row.codiceRit", "-1");
			} catch (SourceBeanException e1) {
			}
			ror.reportFailure(MessageCodes.General.OPERATION_FAIL, e, className, msg);
		} finally {
			Utils.releaseResources(null, command2, dr2);
			Utils.releaseResources(conn, command, dr);
		}

	}
}