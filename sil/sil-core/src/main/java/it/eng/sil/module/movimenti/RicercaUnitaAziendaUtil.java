package it.eng.sil.module.movimenti;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.SessionContainer;
import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.dbaccess.sql.IDynamicStatementProvider;
import it.eng.sil.util.NavigationCache;

/*
 * Questa classe restituisce la query per la ricerca di un'unità aziendale dato il codice fiscale,
 * comune, cap *
 */

public class RicercaUnitaAziendaUtil implements IDynamicStatementProvider {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(RicercaUnitaAziendaUtil.class.getName());
	private String className = this.getClass().getName();
	private static final String SELECT_SQL_BASE = "SELECT az.prgazienda, AN_UNITA_AZIENDA.PRGUNITA, "
			+ "az.strragionesociale " + "FROM AN_AZIENDA az "
			+ "LEFT JOIN AN_UNITA_AZIENDA on (AN_UNITA_AZIENDA.prgazienda = az.prgazienda) ";

	public String getStatement(RequestContainer requestContainer, SourceBean config) {
		SourceBean req = requestContainer.getServiceRequest();
		SessionContainer sessionContainer = requestContainer.getSessionContainer();
		NavigationCache mov = null;
		mov = (NavigationCache) sessionContainer.getAttribute("MOVIMENTOCORRENTE");
		Object codiceFiscale = null;
		Object codCom = null;
		Object codCap = null;
		String strCodiceFiscale = "";
		String strCodCom = "";
		String strCodCap = "";
		StringBuffer query_totale = new StringBuffer("");
		// Consulto l'oggetto in sessione ripristinando il suo stato di
		// abilitazione alla fine dell'elaborazione
		if (mov != null) {
			boolean wasMovEnabled = mov.isEnabled();
			mov.enable();
			codiceFiscale = mov.getField("strCodiceFiscaleAzUtil");
			codCom = mov.getField("CODUAINTCOM");
			codCap = mov.getField("STRUAINTCAP");

			if (!wasMovEnabled) {
				mov.disable();
			}

			if (codiceFiscale != null) {
				strCodiceFiscale = codiceFiscale.toString().toUpperCase();
			}

			if (codCom != null) {
				strCodCom = codCom.toString().toUpperCase();
			}

			if (codCap != null) {
				strCodCap = codCap.toString().toUpperCase();
			}

			if (!strCodiceFiscale.equals("")) {
				query_totale.append(SELECT_SQL_BASE);
				query_totale.append(" WHERE upper(az.strcodicefiscale) = '");
				query_totale.append(strCodiceFiscale);
				query_totale.append("'");

				if (!strCodCom.equals("")) {
					query_totale.append(" AND upper(AN_UNITA_AZIENDA.codcom) = '");
					query_totale.append(strCodCom);
					query_totale.append("'");
				}

				if (!strCodCap.equals("")) {
					query_totale.append(" AND upper(AN_UNITA_AZIENDA.strcap) = '");
					query_totale.append(strCodCap);
					query_totale.append("'");
				}

				// Debug
				_logger.debug(className + "::Stringa di ricerca:" + query_totale.toString());

			}
		}
		return query_totale.toString();
	}
}// class RicercaUnitaAziendaUtil
