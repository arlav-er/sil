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
import it.eng.afExt.utils.StringUtils;
import it.eng.sil.module.AbstractSimpleModule;
import it.eng.sil.security.User;

/**
 * @author Fatale
 *
 */
public class ConfirmInsertSogAccreditamento extends AbstractSimpleModule {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger
			.getLogger(ConfirmInsertSogAccreditamento.class.getName());

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public void service(SourceBean serviceRequest, SourceBean serviceResponse) throws Exception {

		_logger.debug("Sono dentro ConfirmInsertSogAccreditamento ");

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
			String indirizzo = (String) serviceReq.getAttribute("indirizzoSel");
			String denominazione = (String) serviceReq.getAttribute("denominazioneSel");
			String codcomune = (String) serviceReq.getAttribute("codComuneSel");
			String descComune = (String) serviceReq.getAttribute("descComuneSel");
			String noteStampa = "";

			String statement = SQLStatements.getStatement("STORED_OPERAZIONE_INSERT_ENTE_ACCREDITAMENTO");

			command = (StoredProcedureCommand) conn.createStoredProcedureCommand(statement);

			int paramIndex = 0;
			// imposto i parametri
			ArrayList parameters = new ArrayList(9);

			parameters.add(conn.createDataField("PAR_STRCODICEFISCALE", Types.VARCHAR, codiceFiscaleSel));
			command.setAsInputParameters(paramIndex++);

			codSedeSel = StringUtils.isFilledNoBlank(codSedeSel) ? codSedeSel.toUpperCase() : codSedeSel;
			parameters.add(conn.createDataField("PAR_CODSEDE", Types.VARCHAR, codSedeSel));
			command.setAsInputParameters(paramIndex++);

			parameters.add(conn.createDataField("PAR_STRDENOMINAZIONE", Types.VARCHAR, denominazione));
			command.setAsInputParameters(paramIndex++);

			parameters.add(conn.createDataField("PAR_STRINDIRIZZO", Types.VARCHAR, indirizzo));
			command.setAsInputParameters(paramIndex++);

			parameters.add(conn.createDataField("PAR_CODCOM", Types.VARCHAR, codcomune));
			command.setAsInputParameters(paramIndex++);

			parameters.add(conn.createDataField("PAR_STRNOTESTAMPE", Types.VARCHAR, null));
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
					msgCode = MessageCodes.BUDGETTDA.ERR_GENERICO;
					msg = "";
					break;
				case 1: // errore prospetto duplicato
					msgCode = MessageCodes.BUDGETTDA.ERR_ACCREDITAMENTO_DUPLICATO;
					msg = "";
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
					msgCode = MessageCodes.BUDGETTDA.ERR_SOGGETTO_DUPLICATO;
					msg = "";
					break;
				case 100:
					msgCode = MessageCodes.General.OPERATION_FAIL;
					msg = errCode;
					break;

				default:
					msgCode = MessageCodes.General.OPERATION_FAIL;
					msg = "Inserisci nuovo Accreditamento Soggetto: errore di ritorno non ammesso." + errCode;
				}
				serviceResponse.setAttribute("error", "true");

				ror.reportFailure(msgCode, true, msg);
				_logger.debug(msg);

			} else {
				serviceResponse.setAttribute("CF_ENTE", codiceFiscaleSel);
				serviceResponse.setAttribute("SEDE_ENTE", codSedeSel);
				ror.reportSuccess(MessageCodes.General.OPERATION_SUCCESS);
			}

			// response.setAttribute(cdr.getSourceBean());
			SourceBean row = new SourceBean("ROW");
			row.setAttribute("CodiceRit", codiceRit);
			row.setAttribute("ErrCodeOut", errCode);
			serviceResponse.setAttribute((SourceBean) row);

			requestContainer.setAttribute("CODICEFISCALE", codiceFiscaleSel);
			requestContainer.setAttribute("CODSEDE", codSedeSel);
			requestContainer.setAttribute("INDIRIZZO", indirizzo);
			requestContainer.setAttribute("DENOMINAZIONE", denominazione);
			requestContainer.setAttribute("COMUNE", codcomune);
			requestContainer.setAttribute("noteStampa", noteStampa);
			requestContainer.setAttribute("descComuneSel", descComune);

		} catch (Exception e) {
			it.eng.sil.util.TraceWrapper.debug(_logger, "ConfirmInsertSogAccreditamento: ", (Exception) e);

		} finally {
			Utils.releaseResources(conn, command, null);
		}

	}

}
