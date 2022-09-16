package it.eng.sil.module.ido;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.SessionContainer;
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

import it.eng.afExt.utils.MessageCodes;
import it.eng.afExt.utils.ReportOperationResult;
import it.eng.afExt.utils.StringUtils;
import it.eng.sil.module.AbstractAlternativaSimpleModule;

public class DeleteIdoEtaEsperienza extends AbstractAlternativaSimpleModule {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(DeleteIdoEtaEsperienza.class.getName());

	private String className = this.getClass().getName();

	public void service(SourceBean request, SourceBean response) {

		deleteProfiloRosaNomGrezzaAS(request, response);

		RequestContainer requestContainer = getRequestContainer();
		SessionContainer sessionContainer = (SessionContainer) requestContainer.getSessionContainer();
		sessionContainer.delAttribute("prgAlternativa");

	}

	public void deleteProfiloRosaNomGrezzaAS(SourceBean request, SourceBean response) {
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

			statementSB = (SourceBean) getConfig().getAttribute("QUERY_AS_DELETE_PROFILO");
			statement = statementSB.getAttribute("STATEMENT").toString();
			sqlStr = SQLStatements.getStatement(statement);
			command = (StoredProcedureCommand) conn.createStoredProcedureCommand(sqlStr);

			String p_prgRichiestaAz = StringUtils.getAttributeStrNotNull(request, "PRGRICHIESTAAZ");
			String p_prgAlternativa = StringUtils.getAttributeStrNotNull(request, "PRGALTERNATIVA");

			parameters = new ArrayList(4);
			// 1.Parametro di Ritorno
			parameters.add(conn.createDataField("codiceRit", java.sql.Types.VARCHAR, null));
			command.setAsOutputParameters(paramIndex++);
			// preparazione dei Parametri di Input
			// 2. p_prgRichiestaAz
			parameters.add(
					conn.createDataField("p_prgRichiestaAz", java.sql.Types.BIGINT, new BigInteger(p_prgRichiestaAz)));
			command.setAsInputParameters(paramIndex++);
			// 3. p_p_prgAlternativa
			parameters.add(
					conn.createDataField("p_prgAlternativa", java.sql.Types.BIGINT, new BigInteger(p_prgAlternativa)));
			command.setAsInputParameters(paramIndex++);
			// parametri di Output
			// 4. p_errCode
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
				switch (Integer.parseInt(codiceRit)) {
				case 1: // non si può eliminare il profilo, vi è almeno una
						// adesione
					msgCode = MessageCodes.IDO.ERR_DPRNG_ADESIONE_NO_DEL_PROFILO;
					msg = "Eliminazione profilo: vi è almeno una adesione";

					break;
				case 2: // la richiesta è chiusa totalmente
					msgCode = MessageCodes.IDO.ERR_DPRNG_DEL_PROFILO_RICH_CHIUSA_TOT;
					msg = "Eliminazione profilo: la richiesta e' chiusa totalmente";

					break;
				case 3: // la richiesta è chiusa totalmente
					msgCode = MessageCodes.IDO.ERR_DPRNG_DEL_PROFILO_RICH_CHIUSA;
					msg = "Eliminazione profilo: la richiesta e' chiusa";

					break;
				case -1: // errore generico sql
					msgCode = MessageCodes.General.OPERATION_FAIL;
					msg = "Eliminazione profilo: sqlCode=" + errCode;

					break;
				default:
					msgCode = MessageCodes.General.OPERATION_FAIL;
					msg = "Eliminazione profilo: errore di ritorno non ammesso. SqlCode=" + errCode;

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