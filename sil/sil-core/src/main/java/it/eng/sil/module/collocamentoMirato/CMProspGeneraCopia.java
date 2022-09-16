package it.eng.sil.module.collocamentoMirato;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

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
import com.engiweb.framework.dispatching.module.AbstractModule;

import it.eng.afExt.utils.MessageCodes;
import it.eng.afExt.utils.ReportOperationResult;
import it.eng.afExt.utils.StringUtils;
import it.eng.sil.security.User;

public class CMProspGeneraCopia extends AbstractModule {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(CMProspGeneraCopia.class.getName());

	public CMProspGeneraCopia() {
	}

	private String className = this.getClass().getName();

	public void service(SourceBean request, SourceBean response) {
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
			int paramIndex = 0;
			ArrayList parameters = null;
			conn = dcm.getConnection(pool);

			String p_prgProspettoInf = StringUtils.getAttributeStrNotNull(request, "PRGPROSPETTOINF");
			String p_cdnUtente = String.valueOf(user.getCodut());
			String p_codProvincia = StringUtils.getAttributeStrNotNull(request, "CODPROVINCIA");
			String p_numAnnoRifProspetto = StringUtils.getAttributeStrNotNull(request, "NUMANNORIFPROSPETTO");

			statementSB = (SourceBean) getConfig().getAttribute("QUERY");
			statement = statementSB.getAttribute("STATEMENT").toString();
			sqlStr = SQLStatements.getStatement(statement);
			command = (StoredProcedureCommand) conn.createStoredProcedureCommand(sqlStr);
			parameters = new ArrayList(6);

			// Parametro di Ritorno
			parameters.add(conn.createDataField("codiceRit", java.sql.Types.VARCHAR, null));
			command.setAsOutputParameters(paramIndex++);
			// preparazione dei Parametri di Input
			// 2. p_prgProspettoInf
			parameters.add(conn.createDataField("p_prgProspettoInf", java.sql.Types.BIGINT,
					new BigInteger(p_prgProspettoInf)));
			command.setAsInputParameters(paramIndex++);
			// 3. p_codProvincia
			parameters
					.add(conn.createDataField("p_codProvincia", java.sql.Types.BIGINT, new BigInteger(p_codProvincia)));
			command.setAsInputParameters(paramIndex++);
			// 4. p_numAnnoRifProspetto
			parameters.add(conn.createDataField("p_numAnnoRifProspetto", java.sql.Types.BIGINT,
					new BigInteger(p_numAnnoRifProspetto)));
			command.setAsInputParameters(paramIndex++);
			// 5. p_cdnUtente
			parameters.add(conn.createDataField("p_cdnUtente", java.sql.Types.BIGINT, new BigInteger(p_cdnUtente)));
			command.setAsInputParameters(paramIndex++);
			// 6. p_out_prgIncrocio
			parameters.add(conn.createDataField("p_out_prgProspettoInf", java.sql.Types.BIGINT, null));
			command.setAsOutputParameters(paramIndex++);
			// Chiamata alla Stored Procedure
			dr = command.execute(parameters);

			CompositeDataResult cdr = (CompositeDataResult) dr.getDataObject();
			List outputParams = cdr.getContainedDataResult();
			PunctualDataResult pdr = (PunctualDataResult) outputParams.get(0);
			DataField df = pdr.getPunctualDatafield();
			codiceRit = df.getStringValue();

			// 1. p_out_prgProspettoInf
			pdr = (PunctualDataResult) outputParams.get(1);
			df = pdr.getPunctualDatafield();
			String outPrgProsepttoInf = df.getStringValue();
			if (outPrgProsepttoInf == null) {
				outPrgProsepttoInf = p_prgProspettoInf;
			}

			request.updAttribute("PRGPROSPETTOINF", outPrgProsepttoInf);

			SourceBean row = new SourceBean("ROW");
			row.setAttribute("CodiceRit", codiceRit);
			row.setAttribute("PRGPROSPETTOINF", outPrgProsepttoInf);

			// Predispongo la Response
			if (!codiceRit.equals("0")) {
				int msgCode = 0;
				String msg = null;
				switch (Integer.parseInt(codiceRit)) {
				case -1: // errore generico sql
					msgCode = MessageCodes.General.OPERATION_FAIL;
					msg = "Generazione copia prospetto: errore generico";
					break;
				case 1: // errore prospetto duplicato
					msgCode = MessageCodes.CollocamentoMirato.ERROR_COPIA_PROSPETTO_DUPLICATO;
					msg = "Generazione copia prospetto: prospetto duplicato";
					break;
				default:
					msgCode = MessageCodes.General.OPERATION_FAIL;
					msg = "Generazione copia prospetto: errore di ritorno non ammesso.";
				}
				response.setAttribute("error", "true");
				ror.reportFailure(msgCode);
				_logger.debug(msg);

			} else {
				ror.reportSuccess(MessageCodes.General.OPERATION_SUCCESS);
			}
			response.setAttribute((SourceBean) row);

		} catch (Exception e) {
			String msg = "Errore nella generazione copia del prospetto ";
			ror.reportFailure(e, className, msg);
		} finally {
			Utils.releaseResources(conn, command, dr);
		}
	}
}