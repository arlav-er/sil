/*
 * Creato il 4-feb-05
 *
 * Per modificare il modello associato a questo file generato, aprire
 * Finestra&gt;Preferenze&gt;Java&gt;Generazione codice&gt;Codice e commenti
 */
package it.eng.sil.module.ido;

import java.sql.Types;
import java.util.ArrayList;

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
public class ResetCopiaRichiesta extends AbstractHttpModule {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(ResetCopiaRichiesta.class.getName());
	private String className = this.getClass().getName();

	/*
	 * (non Javadoc)
	 * 
	 * @see com.engiweb.framework.dispatching.service.ServiceIFace#service(com.engiweb.framework.base.SourceBean,
	 * com.engiweb.framework.base.SourceBean)
	 */
	public void service(SourceBean request, SourceBean response) throws Exception {

		DataConnection conn = null;
		DataResult dr = null;
		StoredProcedureCommand command = null;

		ReportOperationResult result = new ReportOperationResult(this, response);

		try {
			String pool = (String) getConfig().getAttribute("POOL");
			DataConnectionManager dcm = DataConnectionManager.getInstance();
			conn = dcm.getConnection(pool);

			conn.initTransaction();
			SourceBean statementSB = (SourceBean) getConfig().getAttribute("QUERY");
			String statement = (String) statementSB.getAttribute("STATEMENT");
			String sqlStr = SQLStatements.getStatement(statement);
			command = (StoredProcedureCommand) conn.createStoredProcedureCommand(sqlStr);

			// Prelevo i valori dei parametri di Input dalla Request
			String prgRichiestaAz = StringUtils.getAttributeStrNotNull(request, "PRGRICHIESTAAZ");

			RequestContainer requestContainer = getRequestContainer();
			SessionContainer sessionContainer = requestContainer.getSessionContainer();

			int paramIndex = 0;
			ArrayList parameters = new ArrayList(2);
			// Parametro di Ritorno
			parameters.add(conn.createDataField("codiceRit", Types.VARCHAR, null));
			command.setAsOutputParameters(paramIndex++);
			// preparazione dei Parametri di Input

			parameters.add(conn.createDataField("prgRichiestaAz", Types.INTEGER, prgRichiestaAz));
			command.setAsInputParameters(paramIndex++);

			// Chiamata alla Stored Procedure
			dr = command.execute(parameters);
			PunctualDataResult cdr = (PunctualDataResult) dr.getDataObject();
			// cdr.getSourceBean();
			// List outputParams = cdr.getContainedDataResult();

			// Reperisco i valori di output della stored e preparo il SourceBean
			// della response
			// 1. Codice di Ritorno
			PunctualDataResult pdr = (PunctualDataResult) dr.getDataObject();
			DataField df = pdr.getPunctualDatafield();
			String codiceRit = df.getStringValue();

			conn.commitTransaction();

			SourceBean row = new SourceBean("ROW");
			row.setAttribute("CodiceRit", codiceRit);
			response.setAttribute((SourceBean) row);

			response.setAttribute("PRGAZIENDA", request.getAttribute("PRGAZIENDA"));
			response.setAttribute("PRGUNITA", request.getAttribute("PRGUNITA"));

			result.reportSuccess(MessageCodes.General.OPERATION_SUCCESS);

		} catch (Exception e) {
			result.reportSuccess(MessageCodes.General.OPERATION_FAIL);
			conn.rollBackTransaction();
			it.eng.sil.util.TraceWrapper.fatal(_logger, "ResetCopiaRichiesta: ", e);

		} finally {
			Utils.releaseResources(conn, command, dr);
		}

	}

}