package it.eng.sil.module.cig;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.dbaccess.sql.IDynamicStatementProvider;
import it.eng.afExt.utils.SourceBeanUtils;

public class DynamicListaOrPercorsi implements IDynamicStatementProvider {
	private static final String SELECT_SQL_BASE = " select per.prgpercorso, " + "per.prgcolloquio, "
			+ "to_char(coll.datcolloquio,'dd/mm/yyyy') as datcolloquio, "
			+ "to_char(per.datstimata,'dd/mm/yyyy') as datstimata, " + "es.strdescrizione as esito, "
			+ "to_char(per.dateffettiva,'dd/mm/yyyy') as dateffettiva " + "from am_altra_iscr isc "
			+ "left join or_colloquio coll on isc.PRGALTRAISCR = coll.prgaltraiscr "
			+ "left join or_percorso_concordato per on coll.prgcolloquio = per.prgcolloquio "
			+ "left join de_esito es on per.codesito = es.codesito "
			+ "left join ci_corso co on per.PRGPERCORSO = co.PRGPERCORSO " + "where per.prgazioni = 152 "
			+ "and co.prgpercorso is null";

	public String getStatement(RequestContainer requestContainer, SourceBean config) {
		SourceBean req = requestContainer.getServiceRequest();

		String prgAltraIscr = SourceBeanUtils.getAttrStrNotNull(req, "prgAltraIscr");

		StringBuffer query_totale = new StringBuffer(SELECT_SQL_BASE);
		StringBuffer buf = new StringBuffer();

		if (!prgAltraIscr.equals("")) {
			buf.append(" and isc.prgAltraIscr ='" + prgAltraIscr + "' ");
		}

		query_totale.append(buf.toString());
		return query_totale.toString();
	}
}
