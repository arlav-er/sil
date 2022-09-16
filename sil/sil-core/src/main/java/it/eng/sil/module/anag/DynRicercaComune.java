package it.eng.sil.module.anag;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.SessionContainer;
import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.dbaccess.DataConnectionManager;
import com.engiweb.framework.dbaccess.sql.DataConnection;
import com.engiweb.framework.dispatching.module.AbstractModule;
import com.engiweb.framework.tags.Util;
import com.jamonapi.Monitor;
import com.jamonapi.MonitorFactory;

import it.eng.afExt.dbaccess.sql.SmartScrollableDataResult;
import it.eng.afExt.utils.StringUtils;
import it.eng.sil.security.ProfileDataFilter;
import it.eng.sil.security.User;

public class DynRicercaComune extends AbstractModule {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(DynRicercaComune.class.getName());
	private String className = this.getClass().getName();

	public void service(SourceBean request, SourceBean response) {
		DataConnection dc = null;
		DataConnectionManager dcm = null;
		// SQLCommand cmdSelect = null;
		// DataResult dr = null;
		String className = this.getClass().getName();
		SmartScrollableDataResult smartDataResult = null;

		int pagedRows = Integer.parseInt((String) getConfig().getAttribute("ROWS"));

		if (pagedRows < 0) {
			pagedRows = Integer.MAX_VALUE;
		}

		String pool = (String) getConfig().getAttribute("POOL");

		String cosaRic = (String) request.getAttribute("tipo");
		String descr = (String) request.getAttribute("strdenominazione");
		if (descr != null) {
			// Eseguo il raddoppio degli apici per evitare problemi nelle query
			// per descrizione che contengono apici in
			// mezzo alla stringa da cercare (')
			descr = StringUtils.replace(descr, "'", "''");
		}
		String codcom = (String) request.getAttribute("codcom");
		;
		boolean ricXcod = false;

		Monitor monitor = null;
		try {

			SourceBean query = (SourceBean) getConfig().getAttribute("QUERIES.SELECT_QUERY");
			String statementProviderClassName = (String) query.getAttribute("STATEMENT_PROVIDER.CLASS");

			String strPageNumber = (String) getServiceRequest().getAttribute("LIST_PAGE");
			String strMessage = (String) getServiceRequest().getAttribute("MESSAGE");

			int pageNumber = 1;

			if (strPageNumber != null) {
				pageNumber = new Integer(strPageNumber).intValue();
			} else {
				if ((strMessage != null) && (!strMessage.equalsIgnoreCase("LIST_FIRST"))) {
					pageNumber = -1;
				}
			}

			// INSTANZIA LA CLASSE CHE RITORNA LE QUERY
			monitor = MonitorFactory.start("DynamicStatementListModule:[" + statementProviderClassName + "]");
			DynRicercaComuneStatements statementProvider = new DynRicercaComuneStatements();

			String statement = "";
			if (codcom != null && !codcom.equals("")) { // Ricerco il
														// comune/stato per
														// codice
				ricXcod = true;
				statement = statementProvider.getRicercaXcodice(codcom, cosaRic);
			} else { // Ricerco il comune/stato per descrizione
				// partiamo con l'eseguire una ricerca puntuale
				statement = statementProvider.getRicercaXdescrPrecisa(descr, cosaRic);
			}

			/*******************************************************************
			 * *********** Aggancio al filtro delle lista
			 ******************************************************************/
			SessionContainer session = RequestContainer.getRequestContainer().getSessionContainer();

			User user = (User) session.getAttribute(User.USERID);
			String page = (String) request.getAttribute("PAGE");

			if (user != null) {
				statement = addFilter(statement, user, page);
			}

			/*******************************************************************
			 * *********** FINE Aggancio al filtro delle lista
			 ******************************************************************/

			dcm = DataConnectionManager.getInstance();
			if (dcm == null) {
				_logger.error(className + "::service: dcm null");

			}

			dc = dcm.getConnection(pool);
			if (dc == null) {
				_logger.error(className + "::service: dc null");

			}

			Connection connection = dc.getInternalConnection();
			Statement stmt = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			ResultSet rs = stmt.executeQuery(statement);

			rs.last();// Posizinon il cursore in fondo alla lista per contare
						// il num di righe restituite
			if (rs.getRow() == 0 && !ricXcod) { // La ricerca puntuale non ha
												// trovato nessun comune/stato
				// Eseguimo allora una ricerca generica (che può restituire più
				// righe).
				statement = statementProvider.getRicercaXdescrizione(descr, cosaRic);

				if (user != null) {
					statement = addFilter(statement, user, page);
				}

				rs = stmt.executeQuery(statement);

			} // Ricerca generica

			rs.beforeFirst();
			smartDataResult = new SmartScrollableDataResult(dc, rs, true);

			SourceBean rowsSourceBean = smartDataResult.getSourceBean(pageNumber, pagedRows);

			// smartDataResult.close();

			response.setAttribute(rowsSourceBean);

		} catch (Exception e) {
			_logger.error(className + "::service: " + e.getMessage());

		} finally {
			try {
				smartDataResult.close();
				// NB: la "close" rilascia anche la connessione.
			} catch (Exception ex) {
				_logger.error(className + "::service: " + ex.getMessage());

			} finally {
				monitor.stop();
			}
		}

	}// service()

	private String addFilter(String statement, User user, String page) {

		ProfileDataFilter filtroLista = new ProfileDataFilter(user, page);

		_logger.debug(className + " ::Statement prima della sostituzione\r\n" + statement + "]");

		String filtroSQL = filtroLista.getSqlDiLista();
		if (filtroSQL != null) {
			_logger.debug(className + " ::Filtro di lista = [" + filtroSQL + "]");

			if (statement.indexOf("order by") != -1) {
				statement = Util.replace(statement, "order by", " " + filtroSQL + " ORDER BY ");
			} else if (statement.indexOf("ORDER BY") != -1) {
				statement = Util.replace(statement, "ORDER BY", " " + filtroSQL + " ORDER BY ");
			} else {
				statement = statement + " " + filtroSQL;
			}

			_logger.debug(className + " ::Statement dopo della sostituzione\r\n:" + statement + "]");

		}
		return statement;
	}// addFilter(_)

}// end class
