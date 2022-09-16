/*
 * Creato il Feb 4, 2005
 *
 * Per modificare il modello associato a questo file generato, aprire
 * Finestra&gt;Preferenze&gt;Java&gt;Generazione codice&gt;Codice e commenti
 */
package it.eng.sil.module.ido;

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

/**
 * @author savino
 * 
 */
public class DelFisicaLavoratoreDaRosaNominativa extends AbstractModule {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger
			.getLogger(DelFisicaLavoratoreDaRosaNominativa.class.getName());

	public void service(SourceBean request, SourceBean response) {
		// if (true)return;
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
			User user = (User) getRequestContainer().getSessionContainer().getAttribute("@@USER@@");
			String statement = "";
			String sqlStr = "";
			String codiceRit = "";
			String errCode = "";
			String prgIncrocio = StringUtils.getAttributeStrNotNull(request, "prgIncrocio");
			String prgRosa = StringUtils.getAttributeStrNotNull(request, "prgRosa");
			String cdnLavoratore = StringUtils.getAttributeStrNotNull(request, "cdnLavoratore");
			String prgSpiContatto = StringUtils.getAttributeStrNotNull(request, "prgSpiContatto");
			String prgTipoContatto = StringUtils.getAttributeStrNotNull(request, "prgTipoContatto");
			// prgrichiestaAz originale con numStorico=0
			String prgRichiestaAz = StringUtils.getAttributeStrNotNull(request, "PRGORIGINALE");

			String cdnUtente = String.valueOf(user.getCodut());
			String codCpi = user.getCodRif();
			int paramIndex = 0;
			ArrayList parameters = null;
			conn = dcm.getConnection(pool);

			statementSB = (SourceBean) getConfig().getAttribute("QUERY");
			statement = statementSB.getAttribute("STATEMENT").toString();
			sqlStr = SQLStatements.getStatement(statement);
			command = (StoredProcedureCommand) conn.createStoredProcedureCommand(sqlStr);

			parameters = new ArrayList(6);
			// 1.Parametro di Ritorno
			parameters.add(conn.createDataField("codiceRit", java.sql.Types.VARCHAR, null));
			command.setAsOutputParameters(paramIndex++);
			// preparazione dei Parametri di Input
			// 2. p_prgRichiestaAz
			parameters.add(
					conn.createDataField("p_prgRichiestaAz", java.sql.Types.BIGINT, new BigInteger(prgRichiestaAz)));
			command.setAsInputParameters(paramIndex++);
			// 3. p_cdnUtente
			parameters.add(conn.createDataField("p_cdnUtente", java.sql.Types.VARCHAR, cdnUtente));
			command.setAsInputParameters(paramIndex++);
			// 4. p_prgRosa
			parameters.add(conn.createDataField("p_prgRosa", java.sql.Types.VARCHAR, prgRosa));
			command.setAsInputParameters(paramIndex++);
			// 5. p_cdnLavoratore
			parameters.add(conn.createDataField("p_cdnLavoratore", java.sql.Types.VARCHAR, cdnLavoratore));
			command.setAsInputParameters(paramIndex++);
			// parametri di Output
			// 6. p_errCode
			parameters.add(conn.createDataField("p_errCode", java.sql.Types.VARCHAR, null));
			command.setAsOutputParameters(paramIndex++);
			boolean pluto = false;
			if (pluto)
				throw new Exception();
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
			//
			SourceBean row = new SourceBean("ROW");
			row.setAttribute("CodiceRit", codiceRit);
			if (codiceRit.equals("-1")) {
				// si e' verificato un errore col db
				ror.reportFailure(MessageCodes.General.DELETE_FAIL);
				_logger.debug("Impossibile cancellare il lavoratore dalla rosa nominativa. Codice SQL:" + errCode);

			}
			// Predispongo la Response
			response.setAttribute((SourceBean) row);
		} catch (Exception e) {
			String msg = "Errore nella chiamata alla Stored Procedure ";
			ror.reportFailure(MessageCodes.General.OPERATION_FAIL, e, getClass().getName(), msg);
		} finally {
			Utils.releaseResources(null, command2, dr2);
			Utils.releaseResources(conn, command, dr);
		}
	}
}
