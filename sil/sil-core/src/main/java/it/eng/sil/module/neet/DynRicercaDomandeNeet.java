package it.eng.sil.module.neet;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.dbaccess.sql.IDynamicStatementProvider;
import it.eng.afExt.utils.StringUtils;

public class DynRicercaDomandeNeet implements IDynamicStatementProvider {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(DynRicercaDomandeNeet.class.getName());

	private static final String SELECT_SQL_BASE = " SELECT neet.CDNLAVORATORE, to_char(neet.datcanc, 'dd/mm/yyyy') as datcanc, "
			+ " to_char(neet.datdichiarazioneneet, 'dd/mm/yyyy') as datdichiarazioneneet, neet.PRGLAVORATORENEET, "
			+ " utins.strCognome as strcognomeut, utins.strNome as strnomeut, to_char(neet.datstoricizzazione, 'dd/mm/yyyy') as datstoricizzazione, "
			+ " case " + "    when neet.datcanc is null and neet.datstoricizzazione is null " + "	   then 'Attiva' "
			+ "    when neet.datcanc is not null " + "	   then 'Cancellata' "
			+ "    when neet.datstoricizzazione is not null " + "	   then 'Storicizzata' " + "    else '' "
			+ " end as strStato " + " FROM am_lavoratore_neet neet "
			+ " inner join ts_utente utins on (neet.cdnutins = utins.cdnut) "
			+ " inner join ts_utente utmod on (neet.cdnutmod = utmod.cdnut) ";

	public String getStatement(RequestContainer requestContainer, SourceBean config) {

		SourceBean req = requestContainer.getServiceRequest();

		String cdnLavoratore = StringUtils.getAttributeStrNotNull(req, "cdnLavoratore");

		StringBuffer query_totale = new StringBuffer(SELECT_SQL_BASE);
		StringBuffer buf = new StringBuffer();

		if (!cdnLavoratore.equals("")) {
			buf.append(" WHERE neet.cdnlavoratore = " + cdnLavoratore);
		}

		buf.append(
				" ORDER BY DECODE(strStato, 'Attiva', 1, 'Cancellata', 2, 'Storicizzata', 3, 4) asc, neet.datdichiarazioneneet desc");
		query_totale.append(buf.toString());
		return query_totale.toString();
	}

}
