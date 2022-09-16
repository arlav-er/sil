package it.eng.sil.module.ido;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

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

/**
 * 
 * eliminazione dell'adesione alla rosa per il lavoratore corrente
 * 
 * @author coticone
 * 
 */
public class ASDeleteRichiestaAdesione extends AbstractSimpleModule {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger
			.getLogger(ASDeleteRichiestaAdesione.class.getName());
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

			deleteAdesioneAS(request, response);

			transExec.commitTransaction();
			ror.reportSuccess(idSuccess);
		} catch (Exception ex) {
			it.eng.sil.util.TraceWrapper.debug(_logger,
					"ASDeleteRichiestaAdesione::service(): Impossibile cancellare l'adesione!", ex);

			ror.reportFailure(idFail);
			ror.reportFailure(MessageCodes.General.INSERT_FAIL);
			if (transExec != null) {
				transExec.rollBackTransaction();
			}
			errors = true;
		}
	}

	public void deleteAdesioneAS(SourceBean request, SourceBean response) {
		DataConnection conn = null;
		DataResult dr = null;
		DataResult dr2 = null;
		StoredProcedureCommand command = null;
		StoredProcedureCommand command2 = null;
		ReportOperationResult ror = new ReportOperationResult(this, response);

		try {
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

			statementSB = (SourceBean) getConfig().getAttribute("AS_DELETE_ADESIONE");
			statement = statementSB.getAttribute("STATEMENT").toString();
			sqlStr = SQLStatements.getStatement(statement);
			command = (StoredProcedureCommand) conn.createStoredProcedureCommand(sqlStr);

			String p_prgRosa = StringUtils.getAttributeStrNotNull(request, "PRGROSA");
			;
			String p_cdnLavoratore = StringUtils.getAttributeStrNotNull(request, "CDNLAVORATORE");
			String p_prgNominatico = StringUtils.getAttributeStrNotNull(request, "PRGNOMINATIVO");

			parameters = new ArrayList(5);
			// 1.Parametro di Ritorno
			parameters.add(conn.createDataField("codiceRit", java.sql.Types.VARCHAR, null));
			command.setAsOutputParameters(paramIndex++);
			// preparazione dei Parametri di Input
			// 2. p_prgRosa
			parameters.add(conn.createDataField("p_prgRosa", java.sql.Types.BIGINT, new BigInteger(p_prgRosa)));
			command.setAsInputParameters(paramIndex++);
			// 3. p_cdnLavoratore
			parameters.add(
					conn.createDataField("p_cdnLavoratore", java.sql.Types.BIGINT, new BigInteger(p_cdnLavoratore)));
			command.setAsInputParameters(paramIndex++);
			// 4. p_prgNominatico
			parameters.add(
					conn.createDataField("p_prgNominatico", java.sql.Types.BIGINT, new BigInteger(p_prgNominatico)));
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
				int msgCode = 0;
				String msg = null;
				switch (Integer.parseInt(codiceRit)) {
				case 1: // errore la rosa non è grezza impossibile eliminare
						// l'adesione
					msgCode = MessageCodes.IDO.ERR_DEL_ADES_TPROSA;
					msg = "Delete Adesione: errore tipo rosa non valido per la cancellazione";

					break;
				case 2: // errore update rosa, ci sono altre adesioni
					msgCode = MessageCodes.IDO.ERR_DEL_ADES_ROSA;
					msg = "Delete Adesione: errore update rosa a grezza ci sono altre adesioni ";
					break;
				case -1: // errore generico sql
					msgCode = MessageCodes.General.OPERATION_FAIL;
					msg = "Delete Adesion: sqlCode=" + errCode;
					break;
				default:
					msgCode = MessageCodes.General.OPERATION_FAIL;
					msg = "Delete Adesion: errore di ritorno non ammesso. SqlCode=" + errCode;
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