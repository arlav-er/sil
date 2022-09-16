/*
 * Creato il 17-giu-05
 *
 * Per modificare il modello associato a questo file generato, aprire
 * Finestra&gt;Preferenze&gt;Java&gt;Generazione codice&gt;Codice e commenti
 */
package it.eng.sil.module.patto;

import java.sql.Types;
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
import com.engiweb.framework.dispatching.module.AbstractHttpModule;

import it.eng.afExt.utils.MessageCodes;
import it.eng.afExt.utils.ReportOperationResult;
import it.eng.afExt.utils.StringUtils;

/**
 * @author togna
 * 
 *         Per modificare il modello associato al commento di questo tipo generato, aprire
 *         Finestra&gt;Preferenze&gt;Java&gt;Generazione codice&gt;Codice e commenti
 */
public class CheckRiaperturaDid extends AbstractHttpModule {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(CheckRiaperturaDid.class.getName());

	public void service(SourceBean request, SourceBean response) throws Exception {

		DataConnection conn = null;
		DataResult dr = null;
		StoredProcedureCommand command = null;

		ReportOperationResult result = new ReportOperationResult(this, response);

		try {
			String pool = (String) getConfig().getAttribute("POOL");
			DataConnectionManager dcm = DataConnectionManager.getInstance();
			conn = dcm.getConnection(pool);

			SourceBean statementSB = (SourceBean) getConfig().getAttribute("QUERY");
			String statement = (String) statementSB.getAttribute("STATEMENT");
			String sqlStr = SQLStatements.getStatement(statement);
			command = (StoredProcedureCommand) conn.createStoredProcedureCommand(sqlStr);

			// Prelevo i valori dei parametri di Input dalla Request
			String prgDichDisponibilita = StringUtils.getAttributeStrNotNull(request, "PRGDICHDISPONIBILITA");
			String cdnLavoratore = StringUtils.getAttributeStrNotNull(request, "CDNLAVORATORE");

			int paramIndex = 0;
			ArrayList parameters = new ArrayList(3);
			// Parametro di Ritorno
			parameters.add(conn.createDataField("codiceRit", Types.VARCHAR, null));
			command.setAsOutputParameters(paramIndex++);

			// preparazione dei Parametri di Input
			parameters.add(conn.createDataField("cdnLavoratore", Types.INTEGER, cdnLavoratore));
			command.setAsInputParameters(paramIndex++);

			parameters.add(conn.createDataField("prgDichDisponibilita", Types.INTEGER, prgDichDisponibilita));
			command.setAsInputParameters(paramIndex++);

			// Chiamata alla Stored Procedure
			dr = command.execute(parameters);
			PunctualDataResult cdr = (PunctualDataResult) dr.getDataObject();

			PunctualDataResult pdr = (PunctualDataResult) dr.getDataObject();
			DataField df = pdr.getPunctualDatafield();
			String codiceRit = df.getStringValue();

			SourceBean row = new SourceBean("ROW");
			row.setAttribute("checkRiaperturaDid", codiceRit);
			response.setAttribute((SourceBean) row);

		} catch (Exception e) {
			result.reportSuccess(MessageCodes.General.OPERATION_FAIL);
			it.eng.sil.util.TraceWrapper.fatal(_logger, "ResetCopiaRichiesta: ", e);

		} finally {
			Utils.releaseResources(conn, command, dr);
		}

	}
}