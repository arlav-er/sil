/*
 * Creato il 23-nov-07
 *
 * Per modificare il modello associato a questo file generato, aprire
 * Finestra&gt;Preferenze&gt;Java&gt;Generazione codice&gt;Codice e commenti
 */
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

/**
 * @author riccardi
 *
 *         Per modificare il modello associato al commento di questo tipo generato, aprire
 *         Finestra&gt;Preferenze&gt;Java&gt;Generazione codice&gt;Codice e commenti
 */

public class CMProspMovimentiDispSaveNew extends AbstractSimpleModule {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger
			.getLogger(CMProspMovimentiDispSaveNew.class.getName());

	private String className = this.getClass().getName();

	public void service(SourceBean serviceRequest, SourceBean serviceResponse) throws Exception {

		RequestContainer requestContainer = getRequestContainer();
		SessionContainer sessionContainer = requestContainer.getSessionContainer();

		boolean check = true;
		int idSuccess = this.disableMessageIdSuccess();
		int idFail = this.disableMessageIdFail();
		int msgCode = MessageCodes.General.OPERATION_FAIL;
		TransactionQueryExecutor transExec = null;
		ReportOperationResult reportOperation = new ReportOperationResult(this, serviceResponse);
		try {
			transExec = new TransactionQueryExecutor(getPool());
			this.enableTransactions(transExec);

			transExec.initTransaction();

			String prgProspettoInf = StringUtils.getAttributeStrNotNull(serviceRequest, "PRGPROSPETTOINF");

			// vettore contenente la coppia prg (Movimento) e un valore che identifica se è da Inserire o da Modificare
			// es. 768-I: 768 è il prg e "I" indica un movimento da inserire
			// es. 768-U: 768 è il prg della tabella e "U" indica un movimento da aggiornare
			Vector prgMovVector = serviceRequest.getAttributeAsVector("CK_MOV");

			boolean success = false;

			// ciclo su tutti i movimenti checkati
			for (int i = 0; i < prgMovVector.size(); i++) {

				String prgIesimo = (String) prgMovVector.get(i);

				Vector prgVectorMovIU = StringUtils.split(prgIesimo, "-");
				String prg = (String) prgVectorMovIU.get(0);
				String checkMov = (String) prgVectorMovIU.get(1);

				success = aggiornaLavMovimentiDisp(transExec, serviceRequest, serviceResponse, prg, prgProspettoInf,
						checkMov);

				if (!success) {
					break;
				}
			}

			if (success) {
				transExec.commitTransaction();
				reportOperation.reportSuccess(idSuccess);
			} else {
				transExec.rollBackTransaction();
			}

		} catch (Exception e) {
			transExec.rollBackTransaction();
			reportOperation.reportFailure(msgCode, e, "services()", "insert o update in transazione");

		} finally {
		}
	}

	public boolean aggiornaLavMovimentiDisp(TransactionQueryExecutor txExecutor, SourceBean request,
			SourceBean response, String p_prgMovimentoIesimo, String p_prgProspettoInf, String p_checkMov) {

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

			statementSB = (SourceBean) getConfig().getAttribute("CM_AGG_LAV_MOV_DISP");
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
				if (p_checkMov.equals("I")) {
					switch (Integer.parseInt(codiceRit)) {
					case -1: // errore generico sql
						msgCode = MessageCodes.General.ELEMENT_DUPLICATED;
						msg = "Inserimento Lavoratore in riserva: sqlCode=" + errCode;
						break;
					default:
						msgCode = MessageCodes.General.INSERT_FAIL;
						msg = "Inserimento Lavoratore in riserva: errore di ritorno non ammesso. SqlCode=" + errCode;
					}
				} else {
					switch (Integer.parseInt(codiceRit)) {
					case -1: // errore generico sql
						msgCode = MessageCodes.General.UPDATE_FAIL;
						msg = "Aggiornamento Lavoratore in riserva: sqlCode=" + errCode;
						break;
					default:
						msgCode = MessageCodes.General.OPERATION_FAIL;
						msg = "Aggiornamento Lavoratore in riserva: errore di ritorno non ammesso. SqlCode=" + errCode;
					}
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
			if (p_checkMov.equals("I")) {
				ror.reportFailure(MessageCodes.General.ELEMENT_DUPLICATED, e, className, msg);
			} else {
				ror.reportFailure(MessageCodes.General.UPDATE_FAIL, e, className, msg);
			}
		} finally {

		}

		return checkSuccess;
	}

}