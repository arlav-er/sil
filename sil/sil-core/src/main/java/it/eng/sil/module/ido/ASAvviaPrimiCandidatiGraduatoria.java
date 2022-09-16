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
import it.eng.sil.security.User;

/*
 * Modulo che si occupa di avviare a selezione i primi N candidati
 * presenti nella graduatoria che si sta visualizzando 
 * Il numero al massimo può essere la somma di tutti i candidati 
 * che hanno aderito alle graduatorie per ogni profilo dello stesso 
 * prgTipoIncrocio   
 * 
 * @author coticone
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class ASAvviaPrimiCandidatiGraduatoria extends AbstractSimpleModule {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger
			.getLogger(ASAvviaPrimiCandidatiGraduatoria.class.getName());

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

			avviaPrimiNCandidati(request, response);

			transExec.commitTransaction();
			ror.reportSuccess(idSuccess);
		} catch (Exception ex) {
			it.eng.sil.util.TraceWrapper.debug(_logger,
					"ASAvviaPrimiCandidatiGraduatoria::service(): Impossibile riaprire la graduatoria!", ex);

			ror.reportFailure(idFail);
			ror.reportFailure(MessageCodes.General.INSERT_FAIL);
			if (transExec != null) {
				transExec.rollBackTransaction();
			}
			errors = true;
		}

	}

	public void avviaPrimiNCandidati(SourceBean request, SourceBean response) {
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

			statementSB = (SourceBean) getConfig().getAttribute("AS_AVVIA_PRIMI_N_CANDIDATI");
			statement = statementSB.getAttribute("STATEMENT").toString();
			sqlStr = SQLStatements.getStatement(statement);
			command = (StoredProcedureCommand) conn.createStoredProcedureCommand(sqlStr);

			String p_numCandidati = StringUtils.getAttributeStrNotNull(request, "NUMCANDIDATI");
			String p_prgTipoIncrocio = StringUtils.getAttributeStrNotNull(request, "PRGTIPOINCROCIO");
			String p_prgRichiesta = StringUtils.getAttributeStrNotNull(request, "PRGRICHIESTAAZ");
			String p_cdnUtente = String.valueOf(user.getCodut());

			parameters = new ArrayList(6);
			// 1.Parametro di Ritorno
			parameters.add(conn.createDataField("codiceRit", java.sql.Types.VARCHAR, null));
			command.setAsOutputParameters(paramIndex++);
			// preparazione dei Parametri di Input
			// 2. p_numCandidati
			parameters
					.add(conn.createDataField("p_numCandidati", java.sql.Types.BIGINT, new BigInteger(p_numCandidati)));
			command.setAsInputParameters(paramIndex++);
			// 3. p_prgRichiesta
			parameters
					.add(conn.createDataField("p_prgRichiesta", java.sql.Types.BIGINT, new BigInteger(p_prgRichiesta)));
			command.setAsInputParameters(paramIndex++);
			// 4. p_prgIncrocio
			parameters.add(conn.createDataField("p_prgTipoIncrocio", java.sql.Types.BIGINT,
					new BigInteger(p_prgTipoIncrocio)));
			command.setAsInputParameters(paramIndex++);
			// 5. p_cdnUtente
			parameters.add(conn.createDataField("p_cdnUtente", java.sql.Types.BIGINT, new BigInteger(p_cdnUtente)));
			command.setAsInputParameters(paramIndex++);

			// parametri di Output
			// 6. p_errCode
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

			SourceBean row = new SourceBean("ROW");
			row.setAttribute("CodiceRit", codiceRit);
			// Predispongo la Response
			if (!codiceRit.equals("0")) {
				int msgCode = 0;
				String msg = null;
				switch (Integer.parseInt(codiceRit)) {
				case -1: // errore generico sql
					msgCode = MessageCodes.General.OPERATION_FAIL;
					msg = "Avvia i primi n candidati: sqlCode=" + errCode;
					// debugLevel = TracerSingleton.CRITICAL;
					break;
				case 1: // errore data pubblicazione
					msgCode = MessageCodes.General.OPERATION_FAIL;
					msg = "Avvia i primi n candidati: la data di pubbllicazione è anteriore a 6 mesi";
					// debugLevel = TracerSingleton.CRITICAL;
					break;
				default:
					msgCode = MessageCodes.General.OPERATION_FAIL;
					msg = "Avvia i primi n candidati: errore di ritorno non ammesso. SqlCode=" + errCode;
					// debugLevel = TracerSingleton.CRITICAL;
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