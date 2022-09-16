/*
 * Creato il 22-gen-08
 *
 * Per modificare il modello associato a questo file generato, aprire
 * Finestra&gt;Preferenze&gt;Java&gt;Generazione codice&gt;Codice e commenti
 */
package it.eng.sil.module.patto;

import java.math.BigDecimal;
import java.util.ArrayList;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.SessionContainer;
import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.dbaccess.SQLStatements;
import com.engiweb.framework.dbaccess.Utils;
import com.engiweb.framework.dbaccess.sql.DataConnection;
import com.engiweb.framework.dbaccess.sql.DataField;
import com.engiweb.framework.dbaccess.sql.command.std.StoredProcedureCommand;
import com.engiweb.framework.dbaccess.sql.result.DataResult;
import com.engiweb.framework.dbaccess.sql.result.PunctualDataResult;

import it.eng.afExt.utils.StringUtils;
import it.eng.sil.module.AbstractSimpleModule;

/**
 * @author riccardi
 *
 *         Per modificare il modello associato al commento di questo tipo generato, aprire
 *         Finestra&gt;Preferenze&gt;Java&gt;Generazione codice&gt;Codice e commenti
 */
public class CMCheckIscrLavL68Chiuse extends AbstractSimpleModule {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(CMCheckIscrLavL68Chiuse.class.getName());

	private String className = this.getClass().getName();

	public void service(SourceBean serviceRequest, SourceBean serviceResponse) throws Exception {

		RequestContainer requestContainer = getRequestContainer();
		SessionContainer sessionContainer = (SessionContainer) requestContainer.getSessionContainer();

		DataConnection conn = null;

		try {

			String cdnLavoratore = StringUtils.getAttributeStrNotNull(serviceRequest, "cdnLavoratore");
			String datdichiarazione = StringUtils.getAttributeStrNotNull(serviceRequest, "datdichiarazione");
			String encryptKey = (String) sessionContainer.getAttribute("_ENCRYPTER_KEY_");

			DataResult dr = null;
			StoredProcedureCommand command = null;

			conn = getDataConnection();
			SourceBean statementSB = null;
			String statement = "";
			String sqlStr = "";
			String checkIscrLavL68Chiuse = "";

			int paramIndex = 0;
			ArrayList parameters = null;

			statementSB = (SourceBean) getConfig().getAttribute("QUERY_PROC");
			statement = statementSB.getAttribute("STATEMENT").toString();
			sqlStr = SQLStatements.getStatement(statement);
			command = (StoredProcedureCommand) conn.createStoredProcedureCommand(sqlStr);

			parameters = new ArrayList(4);
			// 1.Parametro di Ritorno
			parameters.add(conn.createDataField("p_checkIscrLavL68Chiuse", java.sql.Types.VARCHAR, null));
			command.setAsOutputParameters(paramIndex++);
			// preparazione dei Parametri di Input
			// 2. p_cdnLavoratore
			parameters
					.add(conn.createDataField("p_cdnLavoratore", java.sql.Types.BIGINT, new BigDecimal(cdnLavoratore)));
			command.setAsInputParameters(paramIndex++);
			// 3. p_encryptKey
			parameters.add(conn.createDataField("p_datDichiarazione", java.sql.Types.VARCHAR, datdichiarazione));
			command.setAsInputParameters(paramIndex++);
			// 4. p_encryptKey
			parameters.add(conn.createDataField("p_encryptKey", java.sql.Types.VARCHAR, encryptKey));
			command.setAsInputParameters(paramIndex++);

			// Chiamata alla Stored Procedure
			dr = command.execute(parameters);
			PunctualDataResult pdr = (PunctualDataResult) dr.getDataObject();

			// Reperisco il valore di output della stored
			DataField df = pdr.getPunctualDatafield();
			checkIscrLavL68Chiuse = df.getStringValue();

			SourceBean row = new SourceBean("ROW");
			row.setAttribute("checkIscrLavL68Chiuse", checkIscrLavL68Chiuse);
			serviceResponse.setAttribute(row);

		} catch (Exception e) {

			it.eng.sil.util.TraceWrapper.debug(_logger, "CMCheckIscrLavL68Chiuse::service()", e);

		} finally {

			Utils.releaseResources(conn, null, null);

		}
	}
}