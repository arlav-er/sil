package it.eng.sil.module.collocamentoMirato;

import java.math.BigInteger;
import java.util.ArrayList;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.dbaccess.DataConnectionManager;
import com.engiweb.framework.dbaccess.SQLStatements;
import com.engiweb.framework.dbaccess.Utils;
import com.engiweb.framework.dbaccess.sql.DataConnection;
import com.engiweb.framework.dbaccess.sql.DataField;
import com.engiweb.framework.dbaccess.sql.command.std.StoredProcedureCommand;
import com.engiweb.framework.dbaccess.sql.result.DataResult;
import com.engiweb.framework.dbaccess.sql.result.PunctualDataResult;
import com.engiweb.framework.dispatching.module.AbstractModule;

import it.eng.afExt.utils.MessageCodes;
import it.eng.afExt.utils.ReportOperationResult;
import it.eng.afExt.utils.StringUtils;
import it.eng.sil.security.User;

public class CMProspStoricizza extends AbstractModule {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(CMProspStoricizza.class.getName());

	public CMProspStoricizza() {
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

			statementSB = (SourceBean) getConfig().getAttribute("QUERY");
			statement = statementSB.getAttribute("STATEMENT").toString();
			sqlStr = SQLStatements.getStatement(statement);
			command = (StoredProcedureCommand) conn.createStoredProcedureCommand(sqlStr);
			parameters = new ArrayList(3);

			// Parametro di Ritorno
			parameters.add(conn.createDataField("codiceRit", java.sql.Types.VARCHAR, null));
			command.setAsOutputParameters(paramIndex++);
			// preparazione dei Parametri di Input
			// 2. p_prgProspettoInf
			parameters.add(conn.createDataField("p_prgProspettoInf", java.sql.Types.BIGINT,
					new BigInteger(p_prgProspettoInf)));
			command.setAsInputParameters(paramIndex++);
			// 3. p_cdnUtente
			parameters.add(conn.createDataField("p_cdnUtente", java.sql.Types.BIGINT, new BigInteger(p_cdnUtente)));
			command.setAsInputParameters(paramIndex++);

			// Chiamata alla Stored Procedure
			dr = command.execute(parameters);

			PunctualDataResult pdr = (PunctualDataResult) dr.getDataObject();
			DataField df = pdr.getPunctualDatafield();
			codiceRit = df.getStringValue();

			SourceBean row = new SourceBean("ROW");
			row.setAttribute("CodiceRit", codiceRit);

			// Predispongo la Response
			if (!codiceRit.equals("0")) {
				int msgCode = 0; // ;
				String msg = null;
				switch (Integer.parseInt(codiceRit)) {
				case -1: // errore generico sql
					msgCode = MessageCodes.General.OPERATION_FAIL;
					msg = "Storicizza prospetto: errore generico";
					//
					break;
				// 11/05/2007 dona: eliminato controllo data prima assunzione
				// case 1: // errore mancanza data prima assunzione se FASCIA C
				// msgCode =
				// MessageCodes.CollocamentoMirato.ERROR_DATA_ASSUNZ_FASCIA_C;
				// msg = "Storicizza prospetto: mancanza data prima assunzione";
				//
				// break;
				case 2: // errore mancanza percentuale esonero
					msgCode = MessageCodes.CollocamentoMirato.ERROR_NO_PERC_ESONERO;
					msg = "Storicizza prospetto: mancanza percentuale esonero";
					//
					break;
				case 3: // errore mancanza di compensazione territoriale
					msgCode = MessageCodes.CollocamentoMirato.ERROR_NO_COMPENSAZIONE_TERRITORIALE;
					msg = "Storicizza prospetto: mancanza di compensazione territoriale";
					//
					break;
				case 4: // errore data consegna prospetto obbligatoria
					msgCode = MessageCodes.CollocamentoMirato.ERROR_DATA_CONSEGNA_OBBLIGATORIA;
					msg = "Storicizza prospetto: data consegna prospetto assente";
					//
					break;
				case 5: // errore verifica che azienda, provincia e anno non
						// siano ripetuti per prospetti storicizzati
					msgCode = MessageCodes.CollocamentoMirato.ERROR_PROSPETTO_STORICIZ_DUPLICATO;
					msg = "Storicizza prospetto: prospetto storicizzato esistente";
					//
					break;
				default:
					msgCode = MessageCodes.General.OPERATION_FAIL;
					msg = "Storicizza prospetto: errore di ritorno non ammesso.";
					//
				}
				response.setAttribute("error", "true");
				ror.reportFailure(msgCode);
				_logger.debug(msg);

			} else {
				ror.reportSuccess(MessageCodes.General.OPERATION_SUCCESS);
			}
			response.setAttribute((SourceBean) row);

		} catch (Exception e) {
			String msg = "Errore nella storicizzazione del prospetto ";
			ror.reportFailure(e, className, msg);
		} finally {
			Utils.releaseResources(conn, command, dr);
		}
	}
}