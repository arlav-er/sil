package it.eng.sil.module.amministrazione.redditoAttivazione;

/**
 * Ritrova le informazioni di dettaglio relative al record selezionato sulla tabella AM_NUOVO_RA
 *  
 *  @author Giacomo Pandini
 */

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.base.SourceBeanException;
import com.engiweb.framework.dispatching.module.AbstractModule;
import com.engiweb.framework.util.QueryExecutor;

public class DettaglioNRA extends AbstractModule {

	private static final long serialVersionUID = 5863792564864524603L;
	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(DettaglioNRA.class.getName());
	private String className = this.getClass().getName();

	public void service(SourceBean request, SourceBean response) {
		String pool = (String) getConfig().getAttribute("POOL");
		SourceBean statement = null;

		// Ottiene le informazioni di dettaglio per il nuovo reddito attivazione relative all'utente selezionato
		statement = (SourceBean) getConfig().getAttribute("QUERIES.DETTAGLIO_NUOVORA");

		SourceBean rowsSourceBean = (SourceBean) QueryExecutor.executeQuery(getRequestContainer(),
				getResponseContainer(), pool, statement, "SELECT");
		_logger.debug("Ottenute informazioni dettaglio per il Nuovo reddito attivazione dell'utente selezionato");
		try {
			response.setAttribute("NUOVORA", rowsSourceBean);
			SourceBean value = (SourceBean) rowsSourceBean.getAttribute("ROW");
			request.setAttribute("IDDOMANDAWEB", value.getAttribute("IDDOMANDAWEB"));
			request.setAttribute("IDDOMANDAINTRANET", value.getAttribute("IDDOMANDAINTRANET"));
		} catch (SourceBeanException e) {
			it.eng.sil.util.TraceWrapper.fatal(_logger,
					className + "::select: response.setAttribute((SourceBean)rowObject)", e);
		}

		// Ottiene le informazioni sulla residenza presenti sul SIL
		statement = (SourceBean) getConfig().getAttribute("QUERIES.GET_RESIDENZASIL");

		rowsSourceBean = (SourceBean) QueryExecutor.executeQuery(getRequestContainer(), getResponseContainer(), pool,
				statement, "SELECT");
		_logger.debug("Ottenute informazioni relative alla residenza dell'utente dal SIL");
		try {
			response.setAttribute("RESIDENZASIL", rowsSourceBean);
		} catch (SourceBeanException e) {
			it.eng.sil.util.TraceWrapper.fatal(_logger,
					className + "::select: response.setAttribute((SourceBean)rowObject)", e);
		}
	}
}
