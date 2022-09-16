package it.eng.sil.module.agenda;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.dbaccess.DataConnectionManager;
import com.engiweb.framework.dbaccess.Utils;
import com.engiweb.framework.dbaccess.sql.DataConnection;
import com.engiweb.framework.dbaccess.sql.SQLCommand;
import com.engiweb.framework.dbaccess.sql.result.DataResult;
import com.engiweb.framework.dbaccess.sql.result.DataResultInterface;
import com.engiweb.framework.dbaccess.sql.result.ScrollableDataResult;
import com.engiweb.framework.dispatching.module.AbstractModule;

/*
 * Questa classe esegue uno statement dinamico definito 
 * come segue (in modules.xml):
 * <QUERIES>
 *     <SELECT_QUERY>   
 *       <STATEMENT_PROVIDER CLASS="it.eng.sil.module.agenda.GiorniNL"/>
 *     </SELECT_QUERY>
 *   </QUERIES>
 *
 * prelevando il nome della classe che fornisce il codice SQL da 
 * eseguire, poi lo esegue e restituisce al modulo che la invoca
 * le righe risultato della query.
 * 
 * 
 * @autore: Stefania Orioli - August 2003
 *
 */

import it.eng.afExt.dbaccess.sql.IDynamicStatementProvider;

public class DynamicQueryGenerica extends AbstractModule {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(DynamicQueryGenerica.class.getName());

	private String className = this.getClass().getName();

	public DynamicQueryGenerica() {
	}

	public void service(SourceBean request, SourceBean response) {
		DataConnection dc = null;
		DataConnectionManager dcm = null;
		SQLCommand cmdSelect = null;
		DataResult dr = null;
		String pool = (String) getConfig().getAttribute("POOL");

		// Recupera il nome della classe che fornisce la stringa SQL da eseguire
		SourceBean query = (SourceBean) getConfig().getAttribute("QUERIES.SELECT_QUERY");
		String stmClassName = (String) query.getAttribute("STATEMENT_PROVIDER.CLASS");
		try {
			// Istanzia la classe che ritorna il contenuto della query
			IDynamicStatementProvider statementProvider = (IDynamicStatementProvider) Class.forName(stmClassName)
					.newInstance();
			String statement = statementProvider.getStatement(getRequestContainer(), getConfig());

			dcm = DataConnectionManager.getInstance();
			dc = dcm.getConnection(pool);
			cmdSelect = dc.createSelectCommand(statement);

			// Messaggio di Debug
			_logger.debug(className + "::DynamicQueryGenerica: eseguo query " + statement);

			// esegue la query
			dr = cmdSelect.execute();
			// Recupera il risultato della query
			ScrollableDataResult sdr = null;
			if (dr.getDataResultType().equals(DataResultInterface.SCROLLABLE_DATA_RESULT)) {
				sdr = (ScrollableDataResult) dr.getDataObject();
			}

			SourceBean rowsSourceBean = sdr.getSourceBean();
			// Messaggio di Debug
			it.eng.sil.util.TraceWrapper.debug(_logger, className + "::select: rowsSourceBean", rowsSourceBean);

			if (rowsSourceBean != null) {
				response.setAttribute((SourceBean) rowsSourceBean);
			} else {
				_logger.debug(className + "::select: nessuna riga");

			}
		} // try
		catch (Exception ex) {
			it.eng.sil.util.TraceWrapper.fatal(_logger,
					className + "::select: response.setAttribute((SourceBean)rowsSourceBean)", ex);

		} finally {
			Utils.releaseResources(dc, cmdSelect, dr);
		}

	}
}