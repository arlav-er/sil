package it.eng.sil.module.cig;

import java.math.BigDecimal;
import java.util.ArrayList;

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

import it.eng.afExt.utils.MessageCodes;
import it.eng.afExt.utils.ReportOperationResult;
import it.eng.afExt.utils.StringUtils;
import it.eng.sil.module.AbstractSimpleModule;
import it.eng.sil.security.User;

public class AccorpaIscrizione extends AbstractSimpleModule {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(AccorpaIscrizione.class.getName());

	public void service(SourceBean serviceRequest, SourceBean serviceResponse) throws Exception {
		DataConnection conn = null;
		DataResult dr = null;
		StoredProcedureCommand command = null;
		ArrayList parameters = null;
		String codiceRit = null, errCode = null;
		Boolean catenaRettifica = false;
		ReportOperationResult resultOperation = new ReportOperationResult(this, serviceResponse);
		try {
			String prgAltraIscrDaAccorpare = (String) serviceRequest.getAttribute("prgAltraIscrDaAccorpare");
			String prgAltraIscrAccorpante = (String) serviceRequest.getAttribute("prgAltraIscrAccorpante");
			String cdnLavoratore = (String) serviceRequest.getAttribute("cdnLavoratore");

			String codStato1 = StringUtils.getAttributeStrNotNull(serviceRequest, "codStato1");
			String codStato2 = StringUtils.getAttributeStrNotNull(serviceRequest, "codStato2");
			String codAccordo1 = StringUtils.getAttributeStrNotNull(serviceRequest, "codAccordo1");
			String codAccordo2 = StringUtils.getAttributeStrNotNull(serviceRequest, "codAccordo2");

			/*
			 * prima di accorpare due iscrizioni controllo se entrambe hanno lo stato valido e il codice domanda
			 * Potrebbe capitare che l'iscrizione dell'accorpato non sia sulla stessa catena di rettifica di quella
			 * dell'accorpante. In questo caso la procedura di accorpamento annulla lo stato dell'accorpato e poi
			 * accorpa. Devo tenere traccia della situazione prima che venga invocata la procedura per gestire il
			 * messaggio da dare all'utente
			 */
			if (("".equals(codStato1) && "".equals(codStato2) && !"".equals(codAccordo1) && !"".equals(codAccordo2))) {
				catenaRettifica = true;
			}

			SessionContainer sessionContainer = getRequestContainer().getSessionContainer();
			User user = (User) sessionContainer.getAttribute(User.USERID);

			_logger.debug("Si sta per eseguire l'accorpamento dell'iscrizione =" + prgAltraIscrDaAccorpare
					+ " all'iscrizione=" + prgAltraIscrAccorpante + ", che verra' cancellata....");

			String pool = (String) getConfig().getAttribute("POOL");
			DataConnectionManager dcm = DataConnectionManager.getInstance();
			conn = dcm.getConnection(pool);

			SourceBean statementSB = (SourceBean) getConfig().getAttribute("PROC_ACCORPA_ISCRIZIONE");
			String statement = statementSB.getAttribute("STATEMENT").toString();
			String sqlStr = SQLStatements.getStatement(statement);
			command = (StoredProcedureCommand) conn.createStoredProcedureCommand(sqlStr);

			// si impostano i parametri della procedure
			parameters = new ArrayList(20);

			// 1.Parametro di Ritorno
			parameters.add(conn.createDataField("codiceRit", java.sql.Types.NUMERIC, null));
			command.setAsOutputParameters(0);

			// 2. cdnLavoratore
			parameters.add(conn.createDataField("cdnLavoratore", java.sql.Types.VARCHAR, cdnLavoratore));
			command.setAsInputParameters(1);

			// 3. iscrizione da accorpare e cancellare
			parameters.add(
					conn.createDataField("prgAltraIscrDaAccorpare", java.sql.Types.VARCHAR, prgAltraIscrDaAccorpare));
			command.setAsInputParameters(2);

			// 4. iscrizione accorpante
			parameters.add(
					conn.createDataField("prgAltraIscrAccorpante", java.sql.Types.VARCHAR, prgAltraIscrAccorpante));
			command.setAsInputParameters(3);

			// 5. utente di modifica
			String utente = Integer.toString(user.getCodut(), 10);
			parameters.add(conn.createDataField("cdnUtMod", java.sql.Types.VARCHAR, utente));
			command.setAsInputParameters(4);

			// la procedure viene eseguita
			dr = command.execute(parameters);

			PunctualDataResult pdr = (PunctualDataResult) dr.getDataObject();
			DataField df = pdr.getPunctualDatafield();
			codiceRit = ((BigDecimal) df.getObjectValue()).toString();

			if (!codiceRit.equals("0")) {
				serviceResponse.setAttribute("ESITO_ACCORPAMENTO", "FAIL");
				serviceResponse.setAttribute("ERR_CODE", codiceRit);
				if (codiceRit.equals("-2")) {
					resultOperation.reportFailure(MessageCodes.AccorpamentoIscrizione.ACCORPAMENTO_ISCR_FAIL_LOGGING);
				} else if (codiceRit.equals("-3")) {
					resultOperation.reportFailure(MessageCodes.AccorpamentoIscrizione.ACCORPAMENTO_ISCR_FAIL_CORSI);
				} else if (codiceRit.equals("-4")) {
					resultOperation.reportFailure(MessageCodes.AccorpamentoIscrizione.ACCORPAMENTO_ISCR_FAIL_COLLOQUI);
				} else if (codiceRit.equals("-6")) {
					serviceResponse.setAttribute("ESITO_ACCORPAMENTO", "OK");
					resultOperation.reportSuccess(MessageCodes.AccorpamentoIscrizione.ACCORPAMENTOISCR_SUCCESS);
					if (catenaRettifica)
						resultOperation.reportSuccess(MessageCodes.AccorpamentoIscrizione.ACC_ISCR_NO_STESSA_CATENA);
					else
						resultOperation.reportSuccess(MessageCodes.AccorpamentoIscrizione.ACCORPAMENTO_ISCR_ANNULLATO);
				} else {
					resultOperation.reportFailure(MessageCodes.AccorpamentoIscrizione.ACCORPAMENTOISCR_FAIL);
				}
			} else {
				// operazione completata con successo
				serviceResponse.setAttribute("ESITO_ACCORPAMENTO", "OK");
				resultOperation.reportSuccess(MessageCodes.AccorpamentoIscrizione.ACCORPAMENTOISCR_SUCCESS);
			}
		} catch (com.engiweb.framework.error.EMFInternalError ex) {
			int messageCode = MessageCodes.AccorpamentoIscrizione.ACCORPAMENTOISCR_FAIL;
			serviceResponse.setAttribute("ESITO_ACCORPAMENTO", "FAIL");
			resultOperation.reportFailure(messageCode, ex, this.getClass().toString(), ex.getMessage());
			it.eng.sil.util.TraceWrapper.debug(_logger, "", ex.getNativeException());

		} catch (Exception e) {
			serviceResponse.setAttribute("ESITO_ACCORPAMENTO", "FAIL");
			resultOperation.reportFailure(MessageCodes.AccorpamentoIscrizione.ACCORPAMENTOISCR_FAIL, e,
					this.getClass().toString(), e.getMessage());
			it.eng.sil.util.TraceWrapper.debug(_logger, "", e);

		} finally {
			Utils.releaseResources(conn, command, dr);
		}
	}
}
