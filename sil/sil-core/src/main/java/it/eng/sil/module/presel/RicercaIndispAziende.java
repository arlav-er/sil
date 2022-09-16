package it.eng.sil.module.presel;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.dbaccess.sql.IDynamicStatementProvider;

public class RicercaIndispAziende implements IDynamicStatementProvider {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(RicercaIndispAziende.class.getName());
	private static final String SELECT_SQL_BASE = "SELECT STRRAGIONESOCIALE,PRGAZIENDA " + "FROM AN_AZIENDA ";

	public String getStatement(RequestContainer requestContainer, SourceBean config) {
		SourceBean req = requestContainer.getServiceRequest();
		String strRagioneSociale = (String) req.getAttribute("STRRAGIONESOCIALE");

		StringBuffer query_totale = new StringBuffer(SELECT_SQL_BASE);

		String buf = "";

		if (strRagioneSociale != null && !strRagioneSociale.equals("")) {
			buf += " upper(STRRAGIONESOCIALE) like upper('%" + strRagioneSociale + "%')";
		}

		if (!buf.equals("")) {
			query_totale.append(" WHERE ");
			query_totale.append(buf);
		}

		// Debug
		_logger.debug("sil.module.agenda.RicercaIndispAziende" + "::Stringa di ricerca:" + query_totale.toString());

		return query_totale.toString();

	}
}