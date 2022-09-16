/**
 * 
 */
package it.eng.sil.module.budget;

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

/**
 * @author Fatale
 *
 */
public class ConfirmDeleteAccreditamento extends AbstractSimpleModule {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1420921068900711322L;

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger
			.getLogger(ConfirmDeleteAccreditamento.class.getName());

	public void service(SourceBean serviceRequest, SourceBean serviceResponse) throws Exception {

		_logger.debug("Sono dentro la classe ConfirmDeleteAccreditamento ");

		// Modifica per impostazioni ***

		RequestContainer requestContainer = getRequestContainer();

		SessionContainer session = requestContainer.getSessionContainer();

		DataConnection conn = null;
		StoredProcedureCommand command = null;
		DataResult dr = null;
		ReportOperationResult ror = new ReportOperationResult(this, serviceResponse);
		try {

			String pool = (String) getConfig().getAttribute("POOL");
			DataConnectionManager dcm = DataConnectionManager.getInstance();
			conn = dcm.getConnection(pool);

			SourceBean serviceReq = requestContainer.getServiceRequest();

			String codiceFiscaleSel = (String) serviceReq.getAttribute("cfSel");
			String codSedeSel = (String) serviceReq.getAttribute("codSedeSel");
			if (codiceFiscaleSel == null || codiceFiscaleSel.isEmpty()) {
				codiceFiscaleSel = (String) serviceReq.getAttribute("CODICEFISCALE");
			}
			if (codSedeSel == null || codSedeSel.isEmpty()) {
				codSedeSel = (String) serviceReq.getAttribute("CODSEDE");
			}
			String denominazioneSel = (String) serviceReq.getAttribute("denominazioneSel");
			if (denominazioneSel == null || denominazioneSel.isEmpty()) {
				denominazioneSel = (String) serviceReq.getAttribute("DENOMINAZIONE");
				if (denominazioneSel == null || denominazioneSel.isEmpty()) {
					denominazioneSel = (String) session.getAttribute("denominazione");
				}
			}
			String codComuneSel = (String) serviceReq.getAttribute("codComuneSel");
			if (codComuneSel == null || codComuneSel.isEmpty()) {
				codComuneSel = (String) serviceReq.getAttribute("codComuni");
				if (codComuneSel == null || codComuneSel.isEmpty()) {
					codComuneSel = (String) session.getAttribute("codiceComune");
				}
			}
			String indirizzoSel = (String) serviceReq.getAttribute("indirizzoSel");
			if (indirizzoSel == null || indirizzoSel.isEmpty()) {
				indirizzoSel = (String) serviceReq.getAttribute("INDIRIZZO");
				if (indirizzoSel == null || indirizzoSel.isEmpty()) {
					indirizzoSel = (String) session.getAttribute("indirizzo");
				}
			}
			String noteStampaSel = (String) serviceReq.getAttribute("noteStampaSel");
			if (noteStampaSel == null || noteStampaSel.isEmpty()) {
				noteStampaSel = (String) serviceReq.getAttribute("noteStampa");
			}
			String descComune = (String) serviceReq.getAttribute("descComuneSel");
			if (descComune == null || descComune.isEmpty()) {
				descComune = (String) session.getAttribute("DesComune");
			}

			String prgDelaccRedi = (String) serviceReq.getAttribute("PRGMISURA");

			String statement = SQLStatements.getStatement("STORED_OPERAZIONE_DELETE_ENTE_ACCREDITAMENTO");

			requestContainer.setAttribute("CODICEFISCALE", codiceFiscaleSel);
			requestContainer.setAttribute("CODSEDE", codSedeSel);
			requestContainer.setAttribute("INDIRIZZO", indirizzoSel);
			requestContainer.setAttribute("DENOMINAZIONE", denominazioneSel);
			requestContainer.setAttribute("codComuni", codComuneSel);
			requestContainer.setAttribute("descComuneSel", descComune);
			requestContainer.setAttribute("noteStampa", noteStampaSel);
			requestContainer.setAttribute("denominazioneSel", denominazioneSel);
			requestContainer.setAttribute("indirizzoSel", indirizzoSel);

			command = (StoredProcedureCommand) conn.createStoredProcedureCommand(statement);

			int paramIndex = 0;
			// imposto i parametri
			ArrayList parameters = new ArrayList(3);

			parameters.add(conn.createDataField("PAR_PRGENTEACCREDITATO", Types.VARCHAR, prgDelaccRedi));
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
