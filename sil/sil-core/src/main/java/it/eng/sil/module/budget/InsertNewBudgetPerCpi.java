/**
 * 
 */
package it.eng.sil.module.budget;

import java.math.BigInteger;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.SessionContainer;
import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.dbaccess.DataConnectionManager;
import com.engiweb.framework.dbaccess.SQLStatements;
import com.engiweb.framework.dbaccess.Utils;
import com.engiweb.framework.dbaccess.sql.DataConnection;
import com.engiweb.framework.dbaccess.sql.DataField;
import com.engiweb.framework.dbaccess.sql.command.std.StoredProcedureCommand;
import com.engiweb.framework.dbaccess.sql.result.DataResult;
import com.engiweb.framework.dbaccess.sql.result.PunctualDataResult;
import com.engiweb.framework.dbaccess.sql.result.std.CompositeDataResult;

import it.eng.afExt.utils.MessageCodes;
import it.eng.afExt.utils.ReportOperationResult;
import it.eng.sil.module.AbstractSimpleModule;
import it.eng.sil.security.User;

/**
 * @author Fatale
 *
 */
public class InsertNewBudgetPerCpi extends AbstractSimpleModule {

	public InsertNewBudgetPerCpi() {
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = -7845833745098744578L;

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(InsertNewBudgetPerCpi.class.getName());

	public void service(SourceBean serviceRequest, SourceBean serviceResponse) throws Exception {

		RequestContainer requestContainer = getRequestContainer();
		SessionContainer sessionContainer = (SessionContainer) requestContainer.getSessionContainer();
		User user = (User) getRequestContainer().getSessionContainer().getAttribute(User.USERID);

		_logger.debug("Sono dentro la classe  InsertNewBudgetPerCpi ");

		DataConnection conn = null;
		StoredProcedureCommand command = null;
		DataResult dr = null;
		ReportOperationResult ror = new ReportOperationResult(this, serviceResponse);
		try {
			// imposto la chiamata per la stored procedure
			String pool = (String) getConfig().getAttribute("POOL");
			DataConnectionManager dcm = DataConnectionManager.getInstance();
			conn = dcm.getConnection(pool);

			SourceBean serviceReq = requestContainer.getServiceRequest();

			String codiceCpiSel = (String) serviceReq.getAttribute("codiceCPISel");
			String annoSel = (String) serviceReq.getAttribute("AnnoSel");
			String importoSel = (String) serviceReq.getAttribute("importoSel");

			_logger.debug("I valore di input sono::: annoSel " + annoSel + " codiceCpisel " + codiceCpiSel + " importo "
					+ importoSel);

			String statement = SQLStatements.getStatement("STORED_ATTIVA_BUDGET");

			command = (StoredProcedureCommand) conn.createStoredProcedureCommand(statement);

			int paramIndex = 0;
			// imposto i parametri
			ArrayList parameters = new ArrayList(6);

			parameters.add(conn.createDataField("PAR_NUMANNOBUDGET", Types.BIGINT, new BigInteger(annoSel)));
			command.setAsInputParameters(paramIndex++);

			parameters.add(conn.createDataField("PAR_CODCPI", Types.VARCHAR, codiceCpiSel));
			command.setAsInputParameters(paramIndex++);

			parameters.add(conn.createDataField("PAR_DECTOTBUDGET", Types.BIGINT, new BigInteger(importoSel)));
			command.setAsInputParameters(paramIndex++);

			parameters.add(conn.createDataField("PAR_cdnUtente", Types.BIGINT, user.getCodut()));
			command.setAsInputParameters(paramIndex++);

			// I parametri di output
			parameters.add(conn.createDataField("errCodeOut", Types.BIGINT, null));
			command.setAsOutputParameters(paramIndex++);

			parameters.add(conn.createDataField("messaggioErr", Types.VARCHAR, null));
			command.setAsOutputParameters(paramIndex++);

			// eseguo!!
			dr = command.execute(parameters);

			CompositeDataResult cdr = (CompositeDataResult) dr.getDataObject();
			List outputParams = cdr.getContainedDataResult();
			// Reperisco i valori di output della stored
			// 0. Codice di Ritorno
			PunctualDataResult pdr = (PunctualDataResult) outputParams.get(0);
			DataField df = pdr.getPunctualDatafield();
			String codiceRit = df.getStringValue();
			// 1. ErrCodeOut
			pdr = (PunctualDataResult) outputParams.get(1);
			df = pdr.getPunctualDatafield();
			String errCode = df.getStringValue();
			if (errCode == null) {
				errCode = "";
			} else {
				errCode = errCode.trim();
			}
			_logger.debug(errCode);

			// Predispongo la Response
			if (!codiceRit.equals("0")) {
				int msgCode = 0;
				String msg = null;
				switch (Integer.parseInt(codiceRit)) {
				case -1: // errore generico sql
					msgCode = MessageCodes.General.OPERATION_FAIL;
					msg = "Inserisci nuovo budget: errore generico";
					break;
				case 1: // errore prospetto duplicato
					msgCode = MessageCodes.General.OPERATION_FAIL;
					msg = "Inserisci nuovo budget: valore duplicato";
					break;
				case 10:
					msgCode = MessageCodes.General.OPERATION_FAIL;
					msg = "Il budget che si è richiesto di creare esiste già";
					break;

				case 20:
					msgCode = MessageCodes.General.OPERATION_FAIL;
					msg = "Budget Insufficiente sull'altro CPI Coinvolto";
					break;

				case 100:
					msgCode = MessageCodes.General.OPERATION_FAIL;
					msg = "Errore di concorrenza";
					break;

				default:
					msgCode = MessageCodes.General.OPERATION_FAIL;
					msg = "Inserisci nuovo budget: errore di ritorno non ammesso.";
				}
				serviceResponse.setAttribute("error", "true");
				ror.reportFailure(MessageCodes.General.OPERATION_FAIL, true, msg);

				_logger.debug(msg);

			} else {
				ror.reportSuccess(MessageCodes.General.OPERATION_SUCCESS);
			}

			// response.setAttribute(cdr.getSourceBean());
			SourceBean row = new SourceBean("ROW");
			row.setAttribute("CodiceRit", codiceRit);
			row.setAttribute("ErrCodeOut", errCode);
			serviceResponse.setAttribute((SourceBean) row);

			// serviceResponse.reportSuccess(MessageCodes.General.UPDATE_SUCCESS);

		} catch (Exception e) {
			it.eng.sil.util.TraceWrapper.debug(_logger, "InserisciNuovoBudget: ", (Exception) e);

		} finally {
			Utils.releaseResources(conn, command, null);
		}

	}

}
