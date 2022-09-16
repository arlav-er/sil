package it.eng.sil.module.admin;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.dbaccess.sql.IDynamicStatementProvider;

public class ListTsWsTracciamento implements IDynamicStatementProvider {

	@Override
	public String getStatement(RequestContainer requestContainer, SourceBean config) {

		SourceBean sb = requestContainer.getServiceRequest();

		String prgWsTracciamento = (String) sb.getAttribute("prgwstracciamento");
		String strOperazione = (String) sb.getAttribute("stroperazione");

		String strVerso = (String) sb.getAttribute("strverso");
		String strUrl = (String) sb.getAttribute("strurl");
		String strTipo = (String) sb.getAttribute("strtipo");
		String dataDa = (String) sb.getAttribute("datada");
		String dataA = (String) sb.getAttribute("dataa");
		String testoC = (String) sb.getAttribute("testo");

		String select = " select prgwstracciamento, " + " stroperazione, " + " strurl, " + " strverso, " + " strtipo, "
				+ " to_char(dtmins,'dd/mm/yyyy') dtmins " + " from ts_ws_tracciamento " + " where 1 = 1 ";

		if (prgWsTracciamento != null && !"".equals(prgWsTracciamento)) {
			select += " AND prgwstracciamento = " + prgWsTracciamento.replaceAll("'", "''") + " ";
		}

		if (strOperazione != null && !"".equals(strOperazione)) {
			select += " AND stroperazione = '" + strOperazione + "' ";
		}

		if (strVerso != null && !"".equals(strVerso)) {
			select += " AND strverso = '" + strVerso + "' ";
		}

		if (strUrl != null && !"".equals(strUrl)) {
			select += " AND strurl = '" + strUrl + "' ";
		}

		if (strTipo != null && !"".equals(strTipo)) {
			select += " AND strtipo = '" + strTipo + "' ";
		}

		if (dataDa != null && !"".equals(dataDa)) {
			select += " AND dtmins >= to_date('" + dataDa + "','dd/mm/yyyy') ";
		}

		if (dataA != null && !"".equals(dataA)) {
			select += " AND dtmins <= to_date('" + dataA + "','dd/mm/yyyy') ";
		}

		if (testoC != null && !"".equals(testoC)) {
			select += " AND strmessaggiosoap like '%" + testoC.replaceAll("'", "''") + "%' ";
		}

		select += " ORDER BY dtmins asc, prgwstracciamento desc ";
		return select;
	}
}
