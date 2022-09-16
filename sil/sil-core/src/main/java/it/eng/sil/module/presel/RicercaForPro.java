package it.eng.sil.module.presel;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.dbaccess.sql.IDynamicStatementProvider;

public class RicercaForPro implements IDynamicStatementProvider {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(RicercaForPro.class.getName());
	private static final String SELECT_SQL_BASE = "SELECT CODCORSO AS CODICE, STRDESCRIZIONE || " + "DECODE("
			+ "SYSDATE," + "GREATEST(SYSDATE, DATINIZIOVAL, DATFINEVAL)," + " ' (scaduto)',"
			+ " LEAST(SYSDATE, DATINIZIOVAL, DATFINEVAL)," + " ' (scaduto)'," + " ''" + ") AS DESCRIZIONE, NUMANNO"
			+ " FROM DE_CORSO";

	public String getStatement(RequestContainer requestContainer, SourceBean config) {
		SourceBean req = requestContainer.getServiceRequest();
		// String strCodice = (String) req.getAttribute("CODCORSO");
		String strDescCorso = (String) req.getAttribute("DESCCORSO");
		String strAnno = (String) req.getAttribute("NUMANNO");

		StringBuffer query_totale = new StringBuffer(SELECT_SQL_BASE);

		String buf = "";

		/*
		 * if(strCodice!=null && !strCodice.equals("")) { buf+= " upper(CODCORSO) like upper('%" + strCodice + "%')"; }
		 */
		if (strDescCorso != null && !strDescCorso.equals("")) {
			// if(strCodice!=null && !strCodice.equals("")) buf+= " AND ";
			strDescCorso = strDescCorso.replaceAll("'", "''");
			buf += " upper(trim(STRDESCRIZIONE)) like upper(trim('%" + strDescCorso + "%'))";
		}
		if (strAnno != null && !strAnno.equals("")) {
			if (strDescCorso != null && !strDescCorso.equals(""))
				buf += " AND ";
			buf += " NUMANNO = " + strAnno;
		}

		if (!buf.equals("")) {
			query_totale.append(" WHERE ");
			query_totale.append(buf);
			query_totale.append(" ORDER BY CODCORSO");
		}

		// Debug
		_logger.debug("sil.module.agenda.RicercaIndispAziende" + "::Stringa di ricerca:" + query_totale.toString());

		return query_totale.toString();

	}
}