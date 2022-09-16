package it.eng.sil.module.patto;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.dbaccess.sql.IDynamicStatementProvider;
import it.eng.sil.util.Utils;

public class DynamicElencoAppuntamentiStatement implements IDynamicStatementProvider {

	final static String QUERY_BASE = "SELECT to_char(ag_.DTMDATAORA,'dd/mm/yyyy') as data,"
			+ " to_char(ag_.DTMDATAORA, 'hh24:mi') as orario," + " ag_.numminuti as Durata,"
			+ " de_.STRDESCRIZIONE AS DesServizio," + " ( nvl(esito.STRDESCRIZIONE,' ') ) AS DesEsito,"
			+ " ps_.PRGLAVPATTOSCELTA, to_char(ps_.datProtocollo , 'dd/mm/yyyy') as datProtocollo"
			+ " FROM ag_agenda ag_," + " de_servizio de_," + " ag_lavoratore lav," + " de_esito_appunt esito,"
			+ " am_lav_patto_scelta ps_ ," + " am_patto_lavoratore apm" + " WHERE (    (lav.codcpi = ag_.codcpi)"
			+ " AND (lav.prgappuntamento = ag_.prgappuntamento)" + " and lav.codcpi = ag_.codcpi"
			+ " AND (ag_.codservizio = de_.codservizio)" + " AND (ag_.CODESITOAPPUNT = esito.CODESITOAPPUNT (+))"
			+ " and apm.cdnlavoratore = lav.cdnlavoratore" + " and apm.prgpattolavoratore = ps_.prgpattolavoratore"
			// + " AND (lav.cdnLavoratore = ?)"
			+ " AND (lav.CDNLAVORATORE = ps_.strchiavetabella)" + " AND (lav.codcpi = ps_.strchiavetabella2))"
			+ " and lav.PRGAPPUNTAMENTO = ps_.STRCHIAVETABELLA3" + " AND (ps_.codlsttab = 'AG_LAV')"
			+ " and apm.datfine is null";

	public String getStatement(RequestContainer requestContainer, SourceBean config) {
		SourceBean request = requestContainer.getServiceRequest();
		String cdnLavoratore = null;
		String nonFiltrare = Utils.notNull(request.getAttribute("NONFILTRARE"));

		cdnLavoratore = (String) request.getAttribute("cdnLavoratore");

		StringBuffer query = new StringBuffer();
		query.append(QUERY_BASE);

		if ((cdnLavoratore != null) && (cdnLavoratore.length() > 0)) {
			query.append(" AND (lav.cdnLavoratore = " + cdnLavoratore + ")");
		}

		if (!nonFiltrare.equalsIgnoreCase("TRUE")) {
			query.append(" AND ( to_date(ag_.DTMDATAORA,'dd/mm/yyyy') >= to_date(SYSDATE,'dd/mm/yyyy') )");
		}

		return query.toString();
	}

}
