/*
 * Creato il 14-nov-06
 *
 * Per modificare il modello associato a questo file generato, aprire
 * Finestra&gt;Preferenze&gt;Java&gt;Generazione codice&gt;Codice e commenti
 */
package it.eng.sil.module.anag;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.base.SourceBeanException;
import com.engiweb.framework.dbaccess.SQLStatements;
import com.engiweb.framework.dbaccess.sql.DataConnection;
import com.engiweb.framework.dbaccess.sql.DataField;
import com.engiweb.framework.dbaccess.sql.command.std.StoredProcedureCommand;
import com.engiweb.framework.dbaccess.sql.result.DataResult;
import com.engiweb.framework.dbaccess.sql.result.PunctualDataResult;

/**
 * @author melandri
 *
 * Per modificare il modello associato al commento di questo tipo generato, aprire
 * Finestra&gt;Preferenze&gt;Java&gt;Generazione codice&gt;Codice e commenti
 */
import it.eng.afExt.utils.MessageCodes;
import it.eng.afExt.utils.ReportOperationResult;
import it.eng.afExt.utils.TransactionQueryExecutor;
import it.eng.sil.module.AbstractSimpleModule;

public class InsertNumDis extends AbstractSimpleModule {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(InsertNumDis.class.getName());

	private String className = this.getClass().getName();

	public void service(SourceBean request, SourceBean response) throws Exception {

		boolean ret = false;
		int idSuccess = this.disableMessageIdSuccess();
		int idFail = this.disableMessageIdFail();
		TransactionQueryExecutor transExec = null;
		ReportOperationResult reportOperation = new ReportOperationResult(this, response);
		RequestContainer r = this.getRequestContainer();
		try {
			transExec = new TransactionQueryExecutor(getPool());
			this.enableTransactions(transExec);

			transExec.initTransaction();

			BigDecimal prgRichEsonDisabili = doNextVal(request, response);

			if (prgRichEsonDisabili == null) {
				throw new Exception("Impossibile leggere S_CM_RICH_ESON_DISABILI.NEXTVAL");
			}

			request.delAttribute("prgRichEsonDisabili");
			request.setAttribute("prgRichEsonDisabili", prgRichEsonDisabili.toString());

			this.setSectionQueryInsert("QUERY_INSERT_NUMDIS");
			ret = doInsert(request, response);

			if (!ret) {
				throw new Exception("impossibile inserire in CM_RICH_ESON_DISABILI in transazione");
			}
			r.setServiceRequest(request);

			// aggiorno il prospetto solamente se l'utente ha agganciato un prospetto nella maschera
			Object prgProspettoInf = request.getAttribute("PRGPROSPETTOINF");
			String codMonoStatoProspetto = (String) request.getAttribute("CODMONOSTATOPROSPETTO");
			if (prgProspettoInf != null && !("").equalsIgnoreCase(prgProspettoInf.toString())
					&& !codMonoStatoProspetto.equals("S") && !codMonoStatoProspetto.equals("U")) {
				ret = updateProspetto(transExec, request, response);
				if (!ret) {
					throw new Exception(
							"impossibile aggiornare il prospetto dalla richiesta di esonero in transazione");
				}
			}

			transExec.commitTransaction();

			response.delAttribute("prgRichEsonDisabili");
			response.setAttribute("prgRichEsonDisabili", prgRichEsonDisabili);

			reportOperation.reportSuccess(idSuccess);

		} catch (Exception e) {
			transExec.rollBackTransaction();

			reportOperation.reportFailure(MessageCodes.General.INSERT_FAIL, e, "services()", "insert in transazione");
		} finally {
		}
	}

	/**
	 * aggiorno il prospetto con i dati della richiesta di esonero i dati aggiornati sono la percentuale di esonero e la
	 * data di esonero fino al
	 * 
	 * @param txExecutor
	 * @param request
	 * @param response
	 * @return
	 */
	public boolean updateProspetto(TransactionQueryExecutor txExecutor, SourceBean request, SourceBean response) {

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
			int paramIndex = 0;
			ArrayList parameters = null;
			SourceBean row = new SourceBean("ROW");

			statementSB = (SourceBean) getConfig().getAttribute("AGGIORNA_PROSPETTO");
			statement = statementSB.getAttribute("STATEMENT").toString();
			sqlStr = SQLStatements.getStatement(statement);
			command = (StoredProcedureCommand) conn.createStoredProcedureCommand(sqlStr);

			String p_prgProspettoInf = (String) request.getAttribute("PRGPROSPETTOINF");
			String p_prgRichEsoneroDisabili = (String) request.getAttribute("PRGRICHESONDISABILI");
			String p_prgRichEsonero = (String) request.getAttribute("prgRichEsonero");

			parameters = new ArrayList(4);
			// 1.Parametro di Ritorno
			parameters.add(conn.createDataField("codiceRit", java.sql.Types.VARCHAR, null));
			command.setAsOutputParameters(paramIndex++);
			// preparazione dei Parametri di Input
			// 2. p_prgRosa
			parameters.add(conn.createDataField("p_prgProspettoInf", java.sql.Types.BIGINT,
					new BigInteger(p_prgProspettoInf)));
			command.setAsInputParameters(paramIndex++);
			// 3. p_cdnLavoratore
			parameters.add(conn.createDataField("p_prgRichEsoneroDisabili", java.sql.Types.BIGINT,
					new BigInteger(p_prgRichEsoneroDisabili)));
			command.setAsInputParameters(paramIndex++);
			// 4. p_mesiAnzianita
			parameters.add(
					conn.createDataField("p_prgRichEsonero", java.sql.Types.BIGINT, new BigInteger(p_prgRichEsonero)));
			command.setAsInputParameters(paramIndex++);

			// Chiamata alla Stored Procedure
			dr = command.execute(parameters);
			PunctualDataResult pdr = (PunctualDataResult) dr.getDataObject();
			DataField df = pdr.getPunctualDatafield();
			codiceRit = df.getStringValue();

			row.setAttribute("CodiceRit", codiceRit);

			if (!codiceRit.equals("0")) {
				int msgCode = 0;
				String msg = null;
				switch (Integer.parseInt(codiceRit)) {
				case -1: // errore generico sql
					msgCode = MessageCodes.General.INSERT_FAIL;
					msg = "Aggiornamento prospetto da esonero";
					break;
				default:
					msgCode = MessageCodes.General.INSERT_FAIL;
					msg = "Aggiornamento prospetto da esonero.";

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
			ror.reportFailure(MessageCodes.General.OPERATION_FAIL, e, className, msg);
		}

		return checkSuccess;
	}

}