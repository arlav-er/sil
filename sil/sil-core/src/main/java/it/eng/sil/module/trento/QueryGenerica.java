package it.eng.sil.module.trento;

/*
 * Questa classe ricava il nome della query da eseguire dall'attributo
 * statement definito nel file di configurazione MODULES.XML:
 * 
 * <MODULE name="MSel_TipoVista_Giorni" class="it.eng.sil.module.agenda.QueryGenerica">
 *   <CONFIG pool="SIL_DATI" title="">
 *     <QUERIES>
 *       <QUERY statement="SEL_TIPOVISTA_GIORNI"/>
 *     </QUERIES>
 *   </CONFIG>
 * </MODULE>
 *
 * ed esegue lo statement specificato in STATEMENTS.XML:
 * <STATEMENT name="SEL_TIPOVISTA_GIORNI" 
 *          query="select PRGVISTA as CODICE, STRDESCRIZIONE as DESCRIZIONE 
 *                 from de_vista_giorni where PRGVISTA &lt;&gt; 0 " 
 * />
 * 
 * Restituisce al modulo che la invoca le righe risultato della query:
 * <MSEL_TIPOVISTA_GIORNI>
 *   <ROWS>
 *     <ROW CODICE="1" DESCRIZIONE="giorni con prenotazioni"/>
 *     <ROW CODICE="2" DESCRIZIONE="giorni incongruenti"/>
 *   </ROWS>
 * </MSEL_TIPOVISTA_GIORNI>
 * 
 * @author: Stefania Orioli
 * 
 */

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.dispatching.module.AbstractModule;
import com.engiweb.framework.util.QueryExecutor;

public class QueryGenerica extends AbstractModule {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(QueryGenerica.class.getName());

	public QueryGenerica() {
	}

	private String className = this.getClass().getName();

	public void service(SourceBean request, SourceBean response) {
		String pool = (String) getConfig().getAttribute("POOL");
		SourceBean statement = (SourceBean) getConfig().getAttribute("QUERIES.QUERY");

		SourceBean rowsSourceBean = (SourceBean) QueryExecutor.executeQuery(getRequestContainer(),
				getResponseContainer(), pool, statement, "SELECT");

		it.eng.sil.util.TraceWrapper.debug(_logger, className + "::select: rowsSourceBean", rowsSourceBean);

		// Object rowObject = null;
		// if (rowsSourceBean != null) rowObject =
		// rowsSourceBean.getAttribute(DataRow.ROW_TAG);

		try {
			response.setAttribute((SourceBean) rowsSourceBean);
		} // try
		catch (Exception ex) {
			it.eng.sil.util.TraceWrapper.fatal(_logger,
					className + "::select: response.setAttribute((SourceBean)rowsSourceBean)", ex);

		} // catch (Exception ex)
	}

}