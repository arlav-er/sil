package it.eng.sil.module.ido;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.ResponseContainer;
import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.base.SourceBeanException;
import com.engiweb.framework.dbaccess.SQLStatements;
import com.engiweb.framework.dbaccess.sql.DataConnection;
import com.engiweb.framework.dbaccess.sql.DataField;
import com.engiweb.framework.dbaccess.sql.command.std.StoredProcedureCommand;
import com.engiweb.framework.dbaccess.sql.result.DataResult;
import com.engiweb.framework.dbaccess.sql.result.PunctualDataResult;
import com.engiweb.framework.dbaccess.sql.result.std.CompositeDataResult;
import com.engiweb.framework.util.QueryExecutor;

import it.eng.afExt.utils.MessageCodes;
import it.eng.afExt.utils.ReportOperationResult;
import it.eng.afExt.utils.StringUtils;
import it.eng.afExt.utils.TransactionQueryExecutor;
import it.eng.sil.module.AbstractAlternativaSimpleModule;
import it.eng.sil.util.Sottosistema;

public class InsertIdoAlternativa extends AbstractAlternativaSimpleModule {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(InsertIdoAlternativa.class.getName());

	private String className = this.getClass().getName();

	public void service(SourceBean request, SourceBean response) throws Exception {

		ReportOperationResult reportOperation = new ReportOperationResult(this, response);

		disableMessageIdSuccess();

		int idSuccess = this.disableMessageIdSuccess();
		int idFail = this.disableMessageIdFail();
		boolean ok = this.doInsert(request, response, "prgAlternativa");
		if (!ok) {
			throw new Exception("ERRORE ESEGUENDO L'INSERIMENTO DEL NUOVO PROFILO");
		} else {
			boolean errors = false;
			TransactionQueryExecutor transExec = null;
			try {
				transExec = new TransactionQueryExecutor(getPool());
				enableTransactions(transExec);

				transExec.initTransaction();

				matchCreaRosaNominativaAS(request, response, transExec);

				// INIT-PARTE-TEMP
				if (Sottosistema.CM.isOff()) {
					// END-PARTE-TEMP

					// INIT-PARTE-TEMP
				} else {
					// END-PARTE-TEMP

					RequestContainer reqCont = getRequestContainer();
					ResponseContainer resCont = getResponseContainer();

					SourceBean queryRich = (SourceBean) getConfig().getAttribute("QUERY_SELECT_CM");
					SourceBean sourceData = (SourceBean) QueryExecutor.executeQuery(reqCont, resCont, getPool(),
							queryRich, "SELECT");

					String codTipoGrad = sourceData.getAttribute("ROW.CODMONOTIPOGRAD") == null ? ""
							: (String) sourceData.getAttribute("ROW.CODMONOTIPOGRAD");

					matchCreaRosaNominativaCM(request, response, codTipoGrad, transExec);

					// INIT-PARTE-TEMP
				}
				// END-PARTE-TEMP

				transExec.commitTransaction();
				reportOperation.reportSuccess(MessageCodes.General.OPERATION_SUCCESS);

			} catch (Exception ex) {
				it.eng.sil.util.TraceWrapper.debug(_logger,
						"Insert alternativa ::service(): Impossibile inserire il profilo!", ex);

				reportOperation.reportFailure(idFail);
				reportOperation.reportFailure(MessageCodes.General.INSERT_FAIL);
				if (transExec != null) {
					transExec.rollBackTransaction();
				}
				errors = true;
			}
		}

	}

	/**
	 * Il metodo gestisce la chiamata alla storedProcedure PG_INCROCIO.AScreaRosaNomGrezza per la creazione
	 * dell'incrocio per ogni profilo per i tipi di incrocio relativi all'Art. 16 (interruttore AS) e della rosa
	 * corrispondente. In caso affermativo la funzione SQL ritorna 0. In caso di errore vengono gestiti i relativi
	 * messaggi: -1 -> operazione fallita 2 -> la richiesta e' chiusa 3 -> la richiesta e' chius
	 * 
	 * @param request
	 * @param response
	 */
	public void matchCreaRosaNominativaAS(SourceBean request, SourceBean response, TransactionQueryExecutor transExec) {
		DataConnection conn = null;
		DataResult dr = null;
		DataResult dr2 = null;
		StoredProcedureCommand command = null;
		StoredProcedureCommand command2 = null;
		ReportOperationResult ror = new ReportOperationResult(this, response);

		try {
			String pool = (String) getConfig().getAttribute("POOL");
			SourceBean statementSB = null;
			String statement = "";
			String sqlStr = "";
			String codiceRit = "";
			String errCode = "";
			String prgIncrocio = "";
			String prgRosa = "";
			int paramIndex = 0;
			ArrayList parameters = null;
			conn = transExec.getDataConnection();

			statementSB = (SourceBean) getConfig().getAttribute("AS_CREA_ROSA");
			statement = statementSB.getAttribute("STATEMENT").toString();
			sqlStr = SQLStatements.getStatement(statement);
			command = (StoredProcedureCommand) conn.createStoredProcedureCommand(sqlStr);

			String p_prgRichiestaAz = StringUtils.getAttributeStrNotNull(request, "PRGRICHIESTAAZ");
			String p_cdnUtente = StringUtils.getAttributeStrNotNull(request, "P_CDNUTENTE");
			String codEvasione = null;

			parameters = new ArrayList(7);
			// 1.Parametro di Ritorno
			parameters.add(conn.createDataField("codiceRit", java.sql.Types.VARCHAR, null));
			command.setAsOutputParameters(paramIndex++);
			// preparazione dei Parametri di Input
			// 2. p_prgRichiestaAz
			parameters.add(
					conn.createDataField("p_prgRichiestaAz", java.sql.Types.BIGINT, new BigInteger(p_prgRichiestaAz)));
			command.setAsInputParameters(paramIndex++);
			// 3. p_codEvasione
			parameters.add(conn.createDataField("p_codEvasione", java.sql.Types.VARCHAR, codEvasione));
			command.setAsInputParameters(paramIndex++);
			// 4. p_cdnUtente
			parameters.add(conn.createDataField("p_cdnUtente", java.sql.Types.VARCHAR, p_cdnUtente));
			command.setAsInputParameters(paramIndex++);
			// parametri di Output
			// 5. p_errCode
			parameters.add(conn.createDataField("p_errCode", java.sql.Types.VARCHAR, null));
			command.setAsOutputParameters(paramIndex++);
			// 6. p_out_prgIncrocio
			parameters.add(conn.createDataField("p_out_prgIncrocio", java.sql.Types.VARCHAR, null));
			command.setAsOutputParameters(paramIndex++);
			// 7. p_out_prgRosa
			parameters.add(conn.createDataField("p_out_prgRosa", java.sql.Types.VARCHAR, null));
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
			// 1. p_out_prgIncrocio
			pdr = (PunctualDataResult) outputParams.get(2);
			df = pdr.getPunctualDatafield();
			prgIncrocio = (String) df.getStringValue();
			if (prgIncrocio == null) {
				prgIncrocio = "";
			}
			// 2. p_out_prgRosa
			pdr = (PunctualDataResult) outputParams.get(3);
			df = pdr.getPunctualDatafield();
			prgRosa = (String) df.getStringValue();

			SourceBean row = new SourceBean("ROW");
			row.setAttribute("CodiceRit", codiceRit);
			// Predispongo la Response
			if (!codiceRit.equals("0")) {
				int msgCode = 0;
				String msg = null;
				switch (Integer.parseInt(codiceRit)) {
				case 1: // non è stata inserita nessuna mansione nella richiesta
					msgCode = MessageCodes.IDO.ERR_CRNG_NO_MANSIONE;
					msg = "Creazione rosa nominativa grezza fallita: manca la mansione nella richiesta";

					break;
				case 2: // la richiesta è chiusa totalmente
					msgCode = MessageCodes.IDO.ERR_CRNG_RICH_CHIUSA_TOT;
					msg = "Creazione rosa nominativa grezza fallita: la richiesta e' chiusa totalmente";

					break;
				case 3: // la richiesta è chiusa totalmente
					msgCode = MessageCodes.IDO.ERR_CRNG_RICH_CHIUSA;
					msg = "Creazione rosa nominativa grezza fallita: la richiesta e' chiusa";

					break;
				case -1: // errore generico sql
					msgCode = MessageCodes.General.OPERATION_FAIL;
					msg = "Creazione rosa nominativa grezza fallita: sqlCode=" + errCode;

					break;
				default:
					msgCode = MessageCodes.General.OPERATION_FAIL;
					msg = "Creazione rosa nominativa grezza fallita: errore di ritorno non ammesso. SqlCode=" + errCode;

				}
				response.setAttribute("error", "true");
				ror.reportFailure(msgCode);
				_logger.debug(msg);

			} else {
				// response.setAttribute("MATCH_PRGROSA", prgRosa);
				// ror.reportSuccess(MessageCodes.General.OPERATION_SUCCESS);
			}
			response.setAttribute((SourceBean) row);
		} catch (Exception e) {
			String msg = "Errore nella chiamata alla Stored Procedure ";
			try {
				response.setAttribute("row.codiceRit", "-1");
			} catch (SourceBeanException e1) {
			}
			ror.reportFailure(MessageCodes.General.OPERATION_FAIL, e, className, msg);
		}
	}

	/**
	 * Il metodo gestisce la chiamata alla storedProcedure PG_COLL_MIRATO.CMcreaRosaNomGrezza per la creazione
	 * dell'incrocio per ogni profilo per i tipi di incrocio relativi al CM (interruttore CM) e della rosa
	 * corrispondente. In caso affermativo la funzione SQL ritorna 0. In caso di errore vengono gestiti i relativi
	 * messaggi: -1 -> operazione fallita 2 -> la richiesta e' chiusa 3 -> la richiesta e' chius
	 * 
	 * @param request
	 * @param response
	 */
	public boolean matchCreaRosaNominativaCM(SourceBean request, SourceBean response, String codTipoGrad,
			TransactionQueryExecutor transExec) {
		DataConnection conn = null;
		DataResult dr = null;
		DataResult dr2 = null;
		StoredProcedureCommand command = null;
		StoredProcedureCommand command2 = null;
		ReportOperationResult ror = new ReportOperationResult(this, response);

		try {
			String pool = (String) getConfig().getAttribute("POOL");
			SourceBean statementSB = null;
			String statement = "";
			String sqlStr = "";
			String codiceRit = "";
			String errCode = "";
			String prgIncrocio = "";
			String prgRosa = "";
			int paramIndex = 0;
			ArrayList parameters = null;
			conn = transExec.getDataConnection();

			statementSB = (SourceBean) getConfig().getAttribute("CM_CREA_ROSA");
			statement = statementSB.getAttribute("STATEMENT").toString();
			sqlStr = SQLStatements.getStatement(statement);
			command = (StoredProcedureCommand) conn.createStoredProcedureCommand(sqlStr);

			String p_prgRichiestaAz = StringUtils.getAttributeStrNotNull(request, "PRGRICHIESTAAZ");
			String codEvasione = null;
			String p_cdnUtente = StringUtils.getAttributeStrNotNull(request, "P_CDNUTENTE");

			parameters = new ArrayList(8);
			// 1.Parametro di Ritorno
			parameters.add(conn.createDataField("codiceRit", java.sql.Types.VARCHAR, null));
			command.setAsOutputParameters(paramIndex++);
			// preparazione dei Parametri di Input
			// 2. p_prgRichiestaAz
			parameters.add(
					conn.createDataField("p_prgRichiestaAz", java.sql.Types.BIGINT, new BigInteger(p_prgRichiestaAz)));
			command.setAsInputParameters(paramIndex++);
			// 3. p_codMonoTipoGrad
			parameters.add(conn.createDataField("p_codMonoTipoGrad", java.sql.Types.VARCHAR, codTipoGrad));
			command.setAsInputParameters(paramIndex++);
			// 4. p_codEvasione
			parameters.add(conn.createDataField("p_codEvasione", java.sql.Types.VARCHAR, codEvasione));
			command.setAsInputParameters(paramIndex++);
			// 5. p_cdnUtente
			parameters.add(conn.createDataField("p_cdnUtente", java.sql.Types.VARCHAR, p_cdnUtente));
			command.setAsInputParameters(paramIndex++);
			// parametri di Output
			// 6. p_errCode
			parameters.add(conn.createDataField("p_errCode", java.sql.Types.VARCHAR, null));
			command.setAsOutputParameters(paramIndex++);
			// 7. p_out_prgIncrocio
			parameters.add(conn.createDataField("p_out_prgIncrocio", java.sql.Types.VARCHAR, null));
			command.setAsOutputParameters(paramIndex++);
			// 8. p_out_prgRosa
			parameters.add(conn.createDataField("p_out_prgRosa", java.sql.Types.VARCHAR, null));
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
			// 1. p_out_prgIncrocio
			pdr = (PunctualDataResult) outputParams.get(2);
			df = pdr.getPunctualDatafield();
			prgIncrocio = (String) df.getStringValue();
			if (prgIncrocio == null) {
				prgIncrocio = "";
			}
			// 2. p_out_prgRosa
			pdr = (PunctualDataResult) outputParams.get(3);
			df = pdr.getPunctualDatafield();
			prgRosa = (String) df.getStringValue();

			SourceBean row = new SourceBean("ROW");
			row.setAttribute("CodiceRit", codiceRit);
			// Predispongo la Response
			if (!codiceRit.equals("0")) {
				int msgCode = 0;
				String msg = null;
				switch (Integer.parseInt(codiceRit)) {
				case 1: // non è stata inserita nessuna mansione nella richiesta
					msgCode = MessageCodes.IDO.ERR_CRNG_NO_MANSIONE;
					msg = "Creazione rosa nominativa grezza fallita: manca la mansione nella richiesta";

					break;
				case 2: // la richiesta è chiusa totalmente
					msgCode = MessageCodes.IDO.ERR_CRNG_RICH_CHIUSA_TOT;
					msg = "Creazione rosa nominativa grezza fallita: la richiesta e' chiusa totalmente";

					break;
				case -1: // errore generico sql
					msgCode = MessageCodes.General.OPERATION_FAIL;
					msg = "Creazione rosa nominativa grezza fallita: sqlCode=" + errCode;

					break;
				default:
					msgCode = MessageCodes.General.OPERATION_FAIL;
					msg = "Creazione rosa nominativa grezza fallita: errore di ritorno non ammesso. SqlCode=" + errCode;

				}
				response.setAttribute("error", "true");
				ror.reportFailure(msgCode);
				_logger.debug(msg);

			} else {
				// response.setAttribute("MATCH_PRGROSA", prgRosa);
				// ror.reportSuccess(MessageCodes.General.OPERATION_SUCCESS);
			}
			response.setAttribute((SourceBean) row);
			return true;
		} catch (Exception e) {
			String msg = "Errore nella chiamata alla Stored Procedure ";
			try {
				response.setAttribute("row.codiceRit", "-1");
			} catch (SourceBeanException e1) {
			}
			ror.reportFailure(MessageCodes.General.OPERATION_FAIL, e, className, msg);
			return false;
		}
	}

}