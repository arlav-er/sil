package it.eng.sil.module.movimenti;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.SessionContainer;
import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.dbaccess.sql.IDynamicStatementProvider;
import it.eng.sil.util.NavigationCache;

/*
 * Questa classe restituisce la query per la ricerca di un'azienda dato il codice fiscale*
 */

public class RicercaAziendaUtil implements IDynamicStatementProvider {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(RicercaAziendaUtil.class.getName());
	private String className = this.getClass().getName();
	private static final String SELECT_SQL_BASE = "SELECT az.prgazienda, " + "az.strragionesociale "
			+ "FROM AN_AZIENDA az ";

	public String getStatement(RequestContainer requestContainer, SourceBean config) {
		SourceBean req = requestContainer.getServiceRequest();
		SessionContainer sessionContainer = requestContainer.getSessionContainer();
		NavigationCache mov = null;
		mov = (NavigationCache) sessionContainer.getAttribute("MOVIMENTOCORRENTE");
		Object codiceFiscale = null;
		String strCodiceFiscale = "";
		StringBuffer query_totale = new StringBuffer("");
		// Consulto l'oggetto in sessione ripristinando il suo stato di
		// abilitazione alla fine dell'elaborazione
		if (mov != null) {
			boolean wasMovEnabled = mov.isEnabled();
			mov.enable();
			codiceFiscale = mov.getField("strCodiceFiscaleAzUtil");

			if (!wasMovEnabled) {
				mov.disable();
			}

			if (codiceFiscale != null) {
				strCodiceFiscale = codiceFiscale.toString().toUpperCase();
				query_totale.append(SELECT_SQL_BASE);
				query_totale.append(" WHERE upper(az.strcodicefiscale) = '");
				query_totale.append(strCodiceFiscale);
				query_totale.append("'");
				// Debug
				_logger.debug(className + "::Stringa di ricerca:" + query_totale.toString());

			}
		}
		return query_totale.toString();
	}
}// class RicercaAziendaUtil
