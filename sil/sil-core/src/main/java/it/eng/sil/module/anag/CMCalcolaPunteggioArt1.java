
package it.eng.sil.module.anag;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.ResponseContainer;
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

public class CMCalcolaPunteggioArt1 extends AbstractSimpleModule {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(CMCalcolaPunteggioArt1.class.getName());

	private String className = this.getClass().getName();

	public void service(SourceBean request, SourceBean response) throws EMFInternalError {

		RequestContainer reqCont = getRequestContainer();
		ResponseContainer resCont = getResponseContainer();
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

			calcolaPunteggio(request, response);

			transExec.commitTransaction();
			ror.reportSuccess(idSuccess);
		} catch (Exception ex) {
			it.eng.sil.util.TraceWrapper.debug(_logger,
					"CMCalcolaPunteggio::service(): Impossibile calcolare il punteggio!", ex);

			ror.reportFailure(idFail);
			// ror.reportFailure(MessageCodes.General.INSERT_FAIL);
			if (transExec != null) {
				transExec.rollBackTransaction();
			}
			errors = true;
		}
	}

	private void calcolaPunteggio(SourceBean request, SourceBean response) {

		User user = (User) getRequestContainer().getSessionContainer().getAttribute("@@USER@@");
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
			SourceBean row = new SourceBean("ROW");

			statementSB = (SourceBean) getConfig().getAttribute("CM_CALCOLO_PUNTEGGIO_CANDIDATO");
			statement = statementSB.getAttribute("STATEMENT").toString();
			sqlStr = SQLStatements.getStatement(statement);
			command = (StoredProcedureCommand) conn.createStoredProcedureCommand(sqlStr);

			String cdnLavoratore = StringUtils.getAttributeStrNotNull(request, "CDNLAVORATORE");
			String prgIscrArt1 = StringUtils.getAttributeStrNotNull(request, "PRGISCRART1");
			String p_numAnnoRedditoCM = StringUtils.getAttributeStrNotNull(request, "numAnnoRedditoCM");
			String p_cdnUtente = String.valueOf(user.getCodut());

			parameters = new ArrayList(11);
			// 1.Parametro di Ritorno
			parameters.add(conn.createDataField("codiceRit", java.sql.Types.VARCHAR, null));
			command.setAsOutputParameters(paramIndex++);
			// 2. prgNominativo
			parameters.add(conn.createDataField("cdnLavoratore", java.sql.Types.BIGINT, new BigInteger(cdnLavoratore)));
			command.setAsInputParameters(paramIndex++);
			// 3. p_tipoGraduatoria
			parameters.add(conn.createDataField("prgIscrArt1", java.sql.Types.BIGINT, new BigInteger(prgIscrArt1)));
			command.setAsInputParameters(paramIndex++);
			// 4. p_cdnUtente
			parameters.add(conn.createDataField("p_cdnUtente", java.sql.Types.BIGINT, new BigInteger(p_cdnUtente)));
			command.setAsInputParameters(paramIndex++);
			// 5 p_numAnnoRedditoCM
			parameters.add(conn.createDataField("p_numAnnoRedditoCM", java.sql.Types.BIGINT,
					new BigInteger(p_numAnnoRedditoCM)));
			command.setAsInputParameters(paramIndex++);
			// 6. p_errCode
			parameters.add(conn.createDataField("out_punteggioIniziale", java.sql.Types.VARCHAR, null));
			command.setAsOutputParameters(paramIndex++);
			parameters.add(conn.createDataField("out_punteggioAnzianita", java.sql.Types.VARCHAR, null));
			command.setAsOutputParameters(paramIndex++);
			parameters.add(conn.createDataField("out_punteggioInvalidita", java.sql.Types.VARCHAR, null));
			command.setAsOutputParameters(paramIndex++);
			parameters.add(conn.createDataField("out_punteggioPersoneCarico", java.sql.Types.VARCHAR, null));
			command.setAsOutputParameters(paramIndex++);
			parameters.add(conn.createDataField("out_punteggioReddito", java.sql.Types.VARCHAR, null));
			command.setAsOutputParameters(paramIndex++);

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
			String punteggio = df.getStringValue();
			// 1. punteggioIniziale
			pdr = (PunctualDataResult) outputParams.get(1);
			df = pdr.getPunctualDatafield();
			String punteggioIniziale = df.getStringValue();
			// 2. out_punteggioAnzianita
			pdr = (PunctualDataResult) outputParams.get(2);
			df = pdr.getPunctualDatafield();
			String punteggioAnzianita = df.getStringValue();
			// 2. out_punteggioAnzianita
			pdr = (PunctualDataResult) outputParams.get(3);
			df = pdr.getPunctualDatafield();
			String punteggioInvalidita = df.getStringValue();
			// 3. out_punteggioPersoneCarico
			pdr = (PunctualDataResult) outputParams.get(4);
			df = pdr.getPunctualDatafield();
			String punteggioPersoneCarico = df.getStringValue();
			// 4. out_punteggioReddito
			pdr = (PunctualDataResult) outputParams.get(5);
			df = pdr.getPunctualDatafield();
			String punteggioReddito = df.getStringValue();

			row.setAttribute("punteggio", punteggio);
			row.setAttribute("punteggioIniziale", punteggioIniziale);
			row.setAttribute("punteggioAnzianita", punteggioAnzianita);
			row.setAttribute("punteggioInvalidita", punteggioInvalidita);
			row.setAttribute("punteggioPersoneCarico", punteggioPersoneCarico);
			row.setAttribute("punteggioReddito", punteggioReddito);
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