/*
 * Creato il 14-gen-05 
 *
 * Per modificare il modello associato a questo file generato, aprire
 * Finestra&gt;Preferenze&gt;Java&gt;Generazione codice&gt;Codice e commenti
 */
package it.eng.sil.module.login;

import java.math.BigDecimal;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.dbaccess.DataConnectionManager;
import com.engiweb.framework.dbaccess.Utils;
import com.engiweb.framework.dbaccess.sql.DataConnection;
import com.engiweb.framework.dbaccess.sql.DataField;
import com.engiweb.framework.dbaccess.sql.command.std.StoredProcedureCommand;
import com.engiweb.framework.dbaccess.sql.result.DataResult;
import com.engiweb.framework.dbaccess.sql.result.PunctualDataResult;

import it.eng.afExt.dbaccess.sql.IDynamicStatementProvider2;
import it.eng.afExt.utils.StringUtils;
import it.eng.sil.Values;

/**
 * @author Togna Cosimo
 * @author D'Auria Giovanni
 * 
 *         Questa classe fornisce la query dinamica per la ricerca degli utenti connessi su un dato host (server)
 */
public class DynamicUtentiConnessiQuery implements IDynamicStatementProvider2 {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger
			.getLogger(DynamicUtentiConnessiQuery.class.getName());

	/*
	 * (non Javadoc)
	 * 
	 * @see it.eng.afExt.dbaccess.sql.IDynamicStatementProvider2#getStatement(com.engiweb.framework.base.SourceBean,
	 * com.engiweb.framework.base.SourceBean)
	 */
	public String getStatement(SourceBean request, SourceBean response) {

		String hostname = "";
		try {
			hostname = InetAddress.getLocalHost().getHostName();
		} catch (UnknownHostException e) {
			it.eng.sil.util.TraceWrapper.fatal(_logger, "DynamicUtentiConnessiQuery: ", (Exception) e);

		}

		// Recupero il tempo dell'utente connesso da più tempo sul server
		StoredProcedureCommand command = null;
		DataResult dr = null;
		DataConnection conn = null;
		BigDecimal maxMinutiConnesso = new BigDecimal(0);
		String max = "";
		try {
			// imposto la chiamata per la stored procedure

			DataConnectionManager dcm = DataConnectionManager.getInstance();
			conn = dcm.getConnection(Values.DB_SIL_DATI);

			command = (StoredProcedureCommand) conn
					.createStoredProcedureCommand("{ call ? := PG_ACCESSO.maxMinutiConnesso(?) }");

			// imposto i parametri
			List parameters = new ArrayList(2);
			parameters.add(conn.createDataField("maxMinutiConnesso", Types.NUMERIC, maxMinutiConnesso));
			command.setAsOutputParameters(0);
			parameters.add(conn.createDataField("HostName", Types.VARCHAR, hostname));
			command.setAsInputParameters(1);

			// eseguo!!
			dr = command.execute(parameters);
			PunctualDataResult pc = (PunctualDataResult) dr.getDataObject();
			DataField df = pc.getPunctualDatafield();
			max = df.getStringValue();

		} catch (Exception e) {
			it.eng.sil.util.TraceWrapper.fatal(_logger, "DynamicUtentiConnessiQuery: ", (Exception) e);

		} finally {
			Utils.releaseResources(conn, command, null);

		}

		String query = "select a.strjsessionid id,a.cdnutente cdut, to_char(a.dtminiziosessione, 'dd/mm/yyyy hh24:mi:ss' ) dtminiziosessione, "
				+ "ut.strlogin, initcap(ut.strcognome ||' ' || ut.strnome) nominativo, a.strip, "
				+ "pg_accesso.generaHtmlMinutiNorm(a.dtminiziosessione," + max + ") time "
				+ "from TS_ACCESSO_UTENTE a, TS_UTENTE ut " + "where a.dtmfinesessione is null "
				+ "and a.cdnutente = ut.cdnut " + "and strhostname='" + StringUtils.formatValue4Sql(hostname) + "' "
				+ "order by a.dtminiziosessione desc";

		return query;
	}

}