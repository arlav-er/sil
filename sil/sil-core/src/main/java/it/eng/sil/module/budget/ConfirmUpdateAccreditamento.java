/**
 * 
 */
package it.eng.sil.module.budget;

import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import com.engiweb.framework.base.RequestContainer;
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
public class ConfirmUpdateAccreditamento extends AbstractSimpleModule {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger
			.getLogger(ConfirmUpdateAccreditamento.class.getName());

	public void service(SourceBean serviceRequest, SourceBean serviceResponse) throws Exception {

		_logger.debug("Sono dentro la classe ConfirmUpdateAccreditamento ");

		RequestContainer requestContainer = getRequestContainer();

		User user = (User) getRequestContainer().getSessionContainer().getAttribute(User.USERID);

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

			String codiceFiscaleSel = (String) serviceReq.getAttribute("cfSel");
			String codSedeSel = (String) serviceReq.getAttribute("codSedeSel");
			String denominazioneSel = (String) serviceReq.getAttribute("denominazioneSel");
			String codComuneSel = (String) serviceReq.getAttribute("codComuneSel");
			String indirizzoSel = (String) serviceReq.getAttribute("indirizzoSel");
			String noteStampaSel = (String) serviceReq.getAttribute("noteStampaSel");
			String descComune = (String) serviceReq.getAttribute("descComuneSel");

			String statement = SQLStatements.getStatement("STORED_OPERAZIONE_UPDATE_ENTE_ACCREDITAMENTO");

			requestContainer.setAttribute("CODICEFISCALE", codiceFiscaleSel);
			requestContainer.setAttribute("CODSEDE", codSedeSel);
			requestContainer.setAttribute("INDIRIZZO", indirizzoSel);
			requestContainer.setAttribute("DENOMINAZIONE", denominazioneSel);
			requestContainer.setAttribute("codComuni", codComuneSel);
			requestContainer.setAttribute("noteStampa", noteStampaSel);
			requestContainer.setAttribute("descComuneSel", descComune);

			command = (StoredProcedureCommand) conn.createStoredProcedureCommand(statement);

			int paramIndex = 0;
			// imposto i parametri
			ArrayList parameters = new ArrayList(9);

			parameters.add(conn.createDataField("PAR_STRCODICEFISCALE", Types.VARCHAR, codiceFiscaleSel));
			command.setAsInputParameters(paramIndex++);

			parameters.add(conn.createDataField("PAR_CODSEDE", Types.VARCHAR, codSedeSel));
			command.setAsInputParameters(paramIndex++);

			parameters.add(conn.createDataField("PAR_STRDENOMINAZIONE", Types.VARCHAR, denominazioneSel));
			command.setAsInputParameters(paramIndex++);

			parameters.add(conn.createDataField("PAR_STRINDIRIZZO", Types.VARCHAR, indirizzoSel));
			command.setAsInputParameters(paramIndex++);

			parameters.add(conn.createDataField("PAR_CODCOM", Types.VARCHAR, codComuneSel));
			command.setAsInputParameters(paramIndex++);

			parameters.add(conn.createDataField("PAR_STRNOTESTAMPE", Types.VARCHAR, noteStampaSel));
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
					msg = "Aggiorna ente Accreditamento: errore generico";
					break;
				case 1: // errore prospetto duplicato
					msgCode = MessageCodes.General.OPERATION_FAIL;
					msg = "Aggiorna ente Accreditamento: campo duplicato";
					break;
				case 10:
					msgCode = MessageCodes.General.OPERATION_FAIL;
					msg = errCode;
					break;

				case 20:
					msgCode = MessageCodes.General.OPERATION_FAIL;
					msg = errCode;
					break;

				case 30:
					msgCode = MessageCodes.General.OPERATION_FAIL;
					msg = errCode;
					break;
				case 90:
					msgCode = MessageCodes.General.OPERATION_FAIL;
					msg = errCode;
					break;
				case 91:
					msgCode = MessageCodes.General.OPERATION_FAIL;
					msg = errCode;
					break;
				case 100:
					msgCode = MessageCodes.General.OPERATION_FAIL;
					msg = errCode;
					break;

				default:
					msgCode = MessageCodes.General.OPERATION_FAIL;
					msg = "Aggiorna ente Accreditamento: errore di ritorno non ammesso.";
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

		} catch (Exception e) {
			it.eng.sil.util.TraceWrapper.debug(_logger, "ConfermaNuovaOperation: ", (Exception) e);

		} finally {
			Utils.releaseResources(conn, command, null);
		}

	}

}
