package it.eng.sil.module.ido;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

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
import com.engiweb.framework.error.EMFInternalError;

import it.eng.afExt.utils.MessageCodes;
import it.eng.afExt.utils.ReportOperationResult;
import it.eng.afExt.utils.StringUtils;
import it.eng.afExt.utils.TransactionQueryExecutor;
import it.eng.sil.module.AbstractSimpleModule;
import it.eng.sil.security.User;

public class ApplicaFiltroCM extends AbstractSimpleModule {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(ApplicaFiltroCM.class.getName());

	private String className = this.getClass().getName();

	public void service(SourceBean request, SourceBean response) {
		TransactionQueryExecutor txExec = null;
		boolean ret = false;

		ReportOperationResult reportOperation = new ReportOperationResult(this, response);
		disableMessageIdSuccess();
		try {
			txExec = new TransactionQueryExecutor(getPool());
			txExec.initTransaction();
			enableTransactions(txExec);
			setSectionQueryUpdate("APPLICA_FILTRO");
			ret = doUpdate(request, response);
			if (!ret) {
				throw new Exception("aggiornamento dell'applicazione del filtro fallito. Operazione interrotta");
			}

			boolean check = updateParamRosaCM(request, response, txExec);

			if (!check) {
				throw new Exception("aggiornamento dell'applicazione del filtro fallito. Operazione interrotta");
			}

			txExec.commitTransaction();
			reportOperation.reportSuccess(MessageCodes.General.OPERATION_SUCCESS);
			_logger.debug("applicazione filtro rosa: ");

		} catch (Exception e) {
			if (getErrorHandler().getErrors().isEmpty())
				reportOperation.reportFailure(getMessageIdFail(), e, "Impossibile ", "");
			it.eng.sil.util.TraceWrapper.debug(_logger, "Impossibile applicare il filtro alla rosa", e);

			if (txExec != null)
				try {
					txExec.rollBackTransaction();
				} catch (EMFInternalError e1) {
					it.eng.sil.util.TraceWrapper.debug(_logger,
							"Impossibile eseguire la rollback nell'applicazione del filtro alla rosa", (Exception) e1);

				}
		}

	}

	public boolean updateParamRosaCM(SourceBean request, SourceBean response, TransactionQueryExecutor txExecutor) {
		DataConnection conn = null;
		DataResult dr = null;
		StoredProcedureCommand command = null;
		ReportOperationResult ror = new ReportOperationResult(this, response);
		boolean returnValue = true;

		try {
			SourceBean serviceResponse = getResponseContainer().getServiceResponse();
			String configDiagnFunz = serviceResponse.containsAttribute("M_GetConfigDiagnosiFunz.ROWS.ROW.NUM")
					? serviceResponse.getAttribute("M_GetConfigDiagnosiFunz.ROWS.ROW.NUM").toString()
					: "0";
			User user = (User) getRequestContainer().getSessionContainer().getAttribute("@@USER@@");
			String pool = (String) getConfig().getAttribute("POOL");
			SourceBean statementSB = null;
			String statement = "";
			String sqlStr = "";
			String codiceRit = "";
			String errCode = "";
			int paramIndex = 0;
			ArrayList parameters = null;
			conn = txExecutor.getDataConnection();

			String p_prgRosa = StringUtils.getAttributeStrNotNull(request, "prgRosa");
			String p_cdnUtente = String.valueOf(user.getCodut());

			SessionContainer session = getRequestContainer().getSessionContainer();
			String encryptKey = (String) session.getAttribute("_ENCRYPTER_KEY_");

			String codMonoTipoAzineda = StringUtils.getAttributeStrNotNull(request, "CODMONOTIPOAZIENDA");
			String flgEscFisica = StringUtils.getAttributeStrNotNull(request, "FLGESCFISICA");
			String flgEscPsichica = StringUtils.getAttributeStrNotNull(request, "FLGESCPSICHICA");
			String flgEscSensoriale = StringUtils.getAttributeStrNotNull(request, "FLGESCSENSORIALE");
			String flgEscIntellettiva = StringUtils.getAttributeStrNotNull(request, "FLGESCINTELLETTIVA");
			String flgEscNonDeterminato = StringUtils.getAttributeStrNotNull(request, "FLGESCNONDETERMINATO");

			if (configDiagnFunz.equals("2")) {
				statementSB = (SourceBean) getConfig().getAttribute("UPDATE_ROSA_WITH_PARAM_VDA");
				statement = statementSB.getAttribute("STATEMENT").toString();
				sqlStr = SQLStatements.getStatement(statement);
				command = (StoredProcedureCommand) conn.createStoredProcedureCommand(sqlStr);

				String in4 = StringUtils.getAttributeStrNotNull(request, "gradoCapacita_IN4");
				String in5 = StringUtils.getAttributeStrNotNull(request, "gradoCapacita_IN5");
				String in6 = StringUtils.getAttributeStrNotNull(request, "gradoCapacita_IN6");
				String po8 = StringUtils.getAttributeStrNotNull(request, "gradoCapacita_PO8");
				String po9 = StringUtils.getAttributeStrNotNull(request, "gradoCapacita_PO9");
				String po10 = StringUtils.getAttributeStrNotNull(request, "gradoCapacita_PO10");
				String po11 = StringUtils.getAttributeStrNotNull(request, "gradoCapacita_PO11");
				String po12 = StringUtils.getAttributeStrNotNull(request, "gradoCapacita_PO12");
				String po13 = StringUtils.getAttributeStrNotNull(request, "gradoCapacita_PO13");
				String po14 = StringUtils.getAttributeStrNotNull(request, "gradoCapacita_PO14");
				String lo4 = StringUtils.getAttributeStrNotNull(request, "gradoCapacita_LO4");
				String lo5 = StringUtils.getAttributeStrNotNull(request, "gradoCapacita_LO5");
				String lo6 = StringUtils.getAttributeStrNotNull(request, "gradoCapacita_LO6");
				String mv5 = StringUtils.getAttributeStrNotNull(request, "gradoCapacita_MV5");
				String mv6 = StringUtils.getAttributeStrNotNull(request, "gradoCapacita_MV6");
				String mv7 = StringUtils.getAttributeStrNotNull(request, "gradoCapacita_MV7");
				String ac5 = StringUtils.getAttributeStrNotNull(request, "gradoCapacita_AC5");
				String ac6 = StringUtils.getAttributeStrNotNull(request, "gradoCapacita_AC6");
				String fa7 = StringUtils.getAttributeStrNotNull(request, "gradoCapacita_FA7");
				String fa8 = StringUtils.getAttributeStrNotNull(request, "gradoCapacita_FA8");
				String sl5 = StringUtils.getAttributeStrNotNull(request, "gradoCapacita_SL5");
				String sl6 = StringUtils.getAttributeStrNotNull(request, "gradoCapacita_SL6");
				String sl7 = StringUtils.getAttributeStrNotNull(request, "gradoCapacita_SL7");
				String sl8 = StringUtils.getAttributeStrNotNull(request, "gradoCapacita_SL8");
				String sl9 = StringUtils.getAttributeStrNotNull(request, "gradoCapacita_SL9");
				String ca1 = StringUtils.getAttributeStrNotNull(request, "gradoCapacita_CA1");
				String ca2 = StringUtils.getAttributeStrNotNull(request, "gradoCapacita_CA2");
				String ca3 = StringUtils.getAttributeStrNotNull(request, "gradoCapacita_CA3");
				String ca4 = StringUtils.getAttributeStrNotNull(request, "gradoCapacita_CA4");

				parameters = new ArrayList(42);
				// 1.Parametro di Ritorno
				parameters.add(conn.createDataField("codiceRit", java.sql.Types.VARCHAR, null));
				command.setAsOutputParameters(paramIndex++);
				// preparazione dei Parametri di Input
				// 2. codMonoTipoAzineda
				parameters.add(conn.createDataField("codMonoTipoAzienda", java.sql.Types.VARCHAR, codMonoTipoAzineda));
				command.setAsInputParameters(paramIndex++);
				// 3
				parameters.add(conn.createDataField("in4", java.sql.Types.VARCHAR, in4));
				command.setAsInputParameters(paramIndex++);
				// 4
				parameters.add(conn.createDataField("in5", java.sql.Types.VARCHAR, in5));
				command.setAsInputParameters(paramIndex++);
				// 5
				parameters.add(conn.createDataField("in6", java.sql.Types.VARCHAR, in6));
				command.setAsInputParameters(paramIndex++);
				// 6
				parameters.add(conn.createDataField("po8", java.sql.Types.VARCHAR, po8));
				command.setAsInputParameters(paramIndex++);
				// 7
				parameters.add(conn.createDataField("po9", java.sql.Types.VARCHAR, po9));
				command.setAsInputParameters(paramIndex++);
				// 8
				parameters.add(conn.createDataField("po10", java.sql.Types.VARCHAR, po10));
				command.setAsInputParameters(paramIndex++);
				// 9
				parameters.add(conn.createDataField("po11", java.sql.Types.VARCHAR, po11));
				command.setAsInputParameters(paramIndex++);
				// 10
				parameters.add(conn.createDataField("po12", java.sql.Types.VARCHAR, po12));
				command.setAsInputParameters(paramIndex++);
				// 11
				parameters.add(conn.createDataField("po13", java.sql.Types.VARCHAR, po13));
				command.setAsInputParameters(paramIndex++);
				// 12
				parameters.add(conn.createDataField("po14", java.sql.Types.VARCHAR, po14));
				command.setAsInputParameters(paramIndex++);
				// 13
				parameters.add(conn.createDataField("lo4", java.sql.Types.VARCHAR, lo4));
				command.setAsInputParameters(paramIndex++);
				// 14
				parameters.add(conn.createDataField("lo5", java.sql.Types.VARCHAR, lo5));
				command.setAsInputParameters(paramIndex++);
				// 15
				parameters.add(conn.createDataField("lo6", java.sql.Types.VARCHAR, lo6));
				command.setAsInputParameters(paramIndex++);
				// 16
				parameters.add(conn.createDataField("mv5", java.sql.Types.VARCHAR, mv5));
				command.setAsInputParameters(paramIndex++);
				// 17
				parameters.add(conn.createDataField("mv6", java.sql.Types.BIGINT, new BigInteger(mv6)));
				command.setAsInputParameters(paramIndex++);
				// 18
				parameters.add(conn.createDataField("mv7", java.sql.Types.VARCHAR, mv7));
				command.setAsInputParameters(paramIndex++);
				// 19
				parameters.add(conn.createDataField("ac5", java.sql.Types.VARCHAR, ac5));
				command.setAsInputParameters(paramIndex++);
				// 20
				parameters.add(conn.createDataField("ac6", java.sql.Types.VARCHAR, ac6));
				command.setAsInputParameters(paramIndex++);
				// 21
				parameters.add(conn.createDataField("fa7", java.sql.Types.VARCHAR, fa7));
				command.setAsInputParameters(paramIndex++);
				// 22
				parameters.add(conn.createDataField("fa8", java.sql.Types.VARCHAR, fa8));
				command.setAsInputParameters(paramIndex++);
				// 23
				parameters.add(conn.createDataField("sl5", java.sql.Types.VARCHAR, sl5));
				command.setAsInputParameters(paramIndex++);
				// 24
				parameters.add(conn.createDataField("sl6", java.sql.Types.VARCHAR, sl6));
				command.setAsInputParameters(paramIndex++);
				// 25
				parameters.add(conn.createDataField("sl7", java.sql.Types.VARCHAR, sl7));
				command.setAsInputParameters(paramIndex++);
				// 26
				parameters.add(conn.createDataField("sl8", java.sql.Types.VARCHAR, sl8));
				command.setAsInputParameters(paramIndex++);
				// 27
				parameters.add(conn.createDataField("sl9", java.sql.Types.VARCHAR, sl9));
				command.setAsInputParameters(paramIndex++);
				// 28
				parameters.add(conn.createDataField("ca1", java.sql.Types.VARCHAR, ca1));
				command.setAsInputParameters(paramIndex++);
				// 29
				parameters.add(conn.createDataField("ca2", java.sql.Types.VARCHAR, ca2));
				command.setAsInputParameters(paramIndex++);
				// 30
				parameters.add(conn.createDataField("ca3", java.sql.Types.VARCHAR, ca3));
				command.setAsInputParameters(paramIndex++);
				// 31
				parameters.add(conn.createDataField("ca4", java.sql.Types.VARCHAR, ca4));
				command.setAsInputParameters(paramIndex++);
				// 32
				parameters.add(conn.createDataField("flgescfisica", java.sql.Types.VARCHAR, flgEscFisica));
				command.setAsInputParameters(paramIndex++);
				// 33
				parameters.add(conn.createDataField("flgescpsichica", java.sql.Types.VARCHAR, flgEscPsichica));
				command.setAsInputParameters(paramIndex++);
				// 34
				parameters.add(conn.createDataField("flgescsensoriale", java.sql.Types.VARCHAR, flgEscSensoriale));
				command.setAsInputParameters(paramIndex++);
				// 35
				parameters.add(conn.createDataField("flgescintellettiva", java.sql.Types.VARCHAR, flgEscIntellettiva));
				command.setAsInputParameters(paramIndex++);
				// 36
				parameters.add(
						conn.createDataField("flgescnondeterminato", java.sql.Types.VARCHAR, flgEscNonDeterminato));
				command.setAsInputParameters(paramIndex++);
				// 37
				parameters.add(conn.createDataField("p_prgRosa", java.sql.Types.BIGINT, new BigInteger(p_prgRosa)));
				command.setAsInputParameters(paramIndex++);
				// 38
				parameters.add(conn.createDataField("encryptKey", java.sql.Types.VARCHAR, encryptKey));
				command.setAsInputParameters(paramIndex++);
				// 39
				parameters.add(conn.createDataField("p_cdnUtente", java.sql.Types.BIGINT, new BigInteger(p_cdnUtente)));
				command.setAsInputParameters(paramIndex++);
				// 40
				parameters.add(conn.createDataField("configDiagnFunz", java.sql.Types.VARCHAR, configDiagnFunz));
				command.setAsInputParameters(paramIndex++);

				// parametri di Output
				// 41. p_errCode
				parameters.add(conn.createDataField("p_errCode", java.sql.Types.VARCHAR, null));
				command.setAsOutputParameters(paramIndex++);
				// 42. query
				parameters.add(conn.createDataField("p_query", java.sql.Types.VARCHAR, null));
				command.setAsOutputParameters(paramIndex++);
			} else {
				statementSB = (SourceBean) getConfig().getAttribute("UPDATE_ROSA_WITH_PARAM");
				statement = statementSB.getAttribute("STATEMENT").toString();
				sqlStr = SQLStatements.getStatement(statement);
				command = (StoredProcedureCommand) conn.createStoredProcedureCommand(sqlStr);

				String mr5 = StringUtils.getAttributeStrNotNull(request, "gradoCapacita_MR5");
				String in2 = StringUtils.getAttributeStrNotNull(request, "gradoCapacita_IN2");
				String po1 = StringUtils.getAttributeStrNotNull(request, "gradoCapacita_PO1");
				String po2 = StringUtils.getAttributeStrNotNull(request, "gradoCapacita_PO2");
				String po6 = StringUtils.getAttributeStrNotNull(request, "gradoCapacita_PO6");
				String po7 = StringUtils.getAttributeStrNotNull(request, "gradoCapacita_PO7");
				String lo2 = StringUtils.getAttributeStrNotNull(request, "gradoCapacita_LO2");
				String lo3 = StringUtils.getAttributeStrNotNull(request, "gradoCapacita_LO3");
				String mv1 = StringUtils.getAttributeStrNotNull(request, "gradoCapacita_MV1");
				String mv2 = StringUtils.getAttributeStrNotNull(request, "gradoCapacita_MV2");
				String mv3 = StringUtils.getAttributeStrNotNull(request, "gradoCapacita_MV3");
				String mv4 = StringUtils.getAttributeStrNotNull(request, "gradoCapacita_MV4");
				String ac1 = StringUtils.getAttributeStrNotNull(request, "gradoCapacita_AC1");
				String fa2 = StringUtils.getAttributeStrNotNull(request, "gradoCapacita_FA2");
				String fa3 = StringUtils.getAttributeStrNotNull(request, "gradoCapacita_FA3");
				String sl1 = StringUtils.getAttributeStrNotNull(request, "gradoCapacita_SL1");
				String sl2 = StringUtils.getAttributeStrNotNull(request, "gradoCapacita_SL2");

				parameters = new ArrayList(30);
				// 1.Parametro di Ritorno
				parameters.add(conn.createDataField("codiceRit", java.sql.Types.VARCHAR, null));
				command.setAsOutputParameters(paramIndex++);
				// preparazione dei Parametri di Input
				// 2. codMonoTipoAzineda
				parameters.add(conn.createDataField("codMonoTipoAzienda", java.sql.Types.VARCHAR, codMonoTipoAzineda));
				command.setAsInputParameters(paramIndex++);
				// 3
				parameters.add(conn.createDataField("mr5", java.sql.Types.BIGINT, new BigInteger(mr5)));
				command.setAsInputParameters(paramIndex++);
				// 4
				parameters.add(conn.createDataField("in2", java.sql.Types.BIGINT, new BigInteger(in2)));
				command.setAsInputParameters(paramIndex++);
				// 5
				parameters.add(conn.createDataField("po1", java.sql.Types.BIGINT, new BigInteger(po1)));
				command.setAsInputParameters(paramIndex++);
				// 6
				parameters.add(conn.createDataField("po2", java.sql.Types.BIGINT, new BigInteger(po2)));
				command.setAsInputParameters(paramIndex++);
				// 7
				parameters.add(conn.createDataField("po6", java.sql.Types.BIGINT, new BigInteger(po6)));
				command.setAsInputParameters(paramIndex++);
				// 8
				parameters.add(conn.createDataField("po7", java.sql.Types.BIGINT, new BigInteger(po7)));
				command.setAsInputParameters(paramIndex++);
				// 9
				parameters.add(conn.createDataField("lo2", java.sql.Types.BIGINT, new BigInteger(lo2)));
				command.setAsInputParameters(paramIndex++);
				// 10
				parameters.add(conn.createDataField("lo3", java.sql.Types.BIGINT, new BigInteger(lo3)));
				command.setAsInputParameters(paramIndex++);
				// 11
				parameters.add(conn.createDataField("mv1", java.sql.Types.BIGINT, new BigInteger(mv1)));
				command.setAsInputParameters(paramIndex++);
				// 12
				parameters.add(conn.createDataField("mv2", java.sql.Types.BIGINT, new BigInteger(mv2)));
				command.setAsInputParameters(paramIndex++);
				// 13
				parameters.add(conn.createDataField("mv3", java.sql.Types.BIGINT, new BigInteger(mv3)));
				command.setAsInputParameters(paramIndex++);
				// 14
				parameters.add(conn.createDataField("mv4", java.sql.Types.BIGINT, new BigInteger(mv4)));
				command.setAsInputParameters(paramIndex++);
				// 15
				parameters.add(conn.createDataField("ac1", java.sql.Types.BIGINT, new BigInteger(ac1)));
				command.setAsInputParameters(paramIndex++);
				// 16
				parameters.add(conn.createDataField("fa2", java.sql.Types.BIGINT, new BigInteger(fa2)));
				command.setAsInputParameters(paramIndex++);
				// 17
				parameters.add(conn.createDataField("fa3", java.sql.Types.BIGINT, new BigInteger(fa3)));
				command.setAsInputParameters(paramIndex++);
				// 18
				parameters.add(conn.createDataField("sl1", java.sql.Types.BIGINT, new BigInteger(sl1)));
				command.setAsInputParameters(paramIndex++);
				// 19
				parameters.add(conn.createDataField("sl2", java.sql.Types.BIGINT, new BigInteger(sl2)));
				command.setAsInputParameters(paramIndex++);
				// 20
				parameters.add(conn.createDataField("flgescfisica", java.sql.Types.VARCHAR, flgEscFisica));
				command.setAsInputParameters(paramIndex++);
				// 21
				parameters.add(conn.createDataField("flgescpsichica", java.sql.Types.VARCHAR, flgEscPsichica));
				command.setAsInputParameters(paramIndex++);
				// 22
				parameters.add(conn.createDataField("flgescsensoriale", java.sql.Types.VARCHAR, flgEscSensoriale));
				command.setAsInputParameters(paramIndex++);
				// 23
				parameters.add(conn.createDataField("flgescintellettiva", java.sql.Types.VARCHAR, flgEscIntellettiva));
				command.setAsInputParameters(paramIndex++);
				// 24
				parameters.add(
						conn.createDataField("flgescnondeterminato", java.sql.Types.VARCHAR, flgEscNonDeterminato));
				command.setAsInputParameters(paramIndex++);
				// 25
				parameters.add(conn.createDataField("p_prgRosa", java.sql.Types.BIGINT, new BigInteger(p_prgRosa)));
				command.setAsInputParameters(paramIndex++);
				// 26
				parameters.add(conn.createDataField("encryptKey", java.sql.Types.VARCHAR, encryptKey));
				command.setAsInputParameters(paramIndex++);
				// 27
				parameters.add(conn.createDataField("p_cdnUtente", java.sql.Types.BIGINT, new BigInteger(p_cdnUtente)));
				command.setAsInputParameters(paramIndex++);
				// 28
				parameters.add(conn.createDataField("configDiagnFunz", java.sql.Types.VARCHAR, configDiagnFunz));
				command.setAsInputParameters(paramIndex++);

				// parametri di Output
				// 29. p_errCode
				parameters.add(conn.createDataField("p_errCode", java.sql.Types.VARCHAR, null));
				command.setAsOutputParameters(paramIndex++);
				// 30. query
				parameters.add(conn.createDataField("p_query", java.sql.Types.VARCHAR, null));
				command.setAsOutputParameters(paramIndex++);
			}

			// Chiamata alla Stored Procedure
			dr = command.execute(parameters);
			CompositeDataResult cdr = (CompositeDataResult) dr.getDataObject();
			List outputParams = cdr.getContainedDataResult();
			// Reperisco i valori di output della stored
			// 0. Codice di Ritorno
			PunctualDataResult pdr = (PunctualDataResult) outputParams.get(0);
			DataField df = pdr.getPunctualDatafield();
			codiceRit = df.getStringValue();

			PunctualDataResult pdr1 = (PunctualDataResult) outputParams.get(1);
			DataField df1 = pdr1.getPunctualDatafield();
			String errorCode = df1.getStringValue();

			PunctualDataResult pdr2 = (PunctualDataResult) outputParams.get(2);
			DataField df2 = pdr2.getPunctualDatafield();
			String query = df2.getStringValue();

			SourceBean row = new SourceBean("ROW");
			row.setAttribute("CodiceRit", codiceRit);
			// Predispongo la Response
			if (!codiceRit.equals("0")) {
				int msgCode = 0;
				String msg = null;
				switch (Integer.parseInt(codiceRit)) {
				case -1: // errore generico sql
					msgCode = MessageCodes.General.OPERATION_FAIL;
					msg = "applicazione filtro rosa: sqlCode=" + errCode;
					break;
				default:
					msgCode = MessageCodes.General.OPERATION_FAIL;
					msg = "applicazione filtro rosa: errore di ritorno non ammesso. SqlCode=" + errCode;
				}
				response.setAttribute("error", "true");
				ror.reportFailure(msgCode);
				_logger.debug(msg);

				returnValue = false;
			} else {
				response.setAttribute((SourceBean) row);
			}
		} catch (Exception e) {
			String msg = "Errore nella chiamata alla Stored Procedure ";
			try {
				response.setAttribute("row.codiceRit", "-1");
			} catch (SourceBeanException e1) {
			}
			returnValue = false;
		} finally {
			return returnValue;
		}
	}

} // class ApplicaFiltro
