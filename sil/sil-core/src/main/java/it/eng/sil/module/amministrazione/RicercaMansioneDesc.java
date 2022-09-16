package it.eng.sil.module.amministrazione;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.dbaccess.sql.IDynamicStatementProvider;
import it.eng.afExt.utils.StringUtils;

public class RicercaMansioneDesc implements IDynamicStatementProvider {
	private String className = this.getClass().getName();

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(RicercaMansioneDesc.class.getName());

	private static final String SELECT_SQL_BASE_IDO = " select man.codMansione, " + "man.STRDESCRIZIONE "
			+ " || DECODE (SYSDATE, GREATEST(SYSDATE, man.DATINIZIOVAL, man.DATFINEVAL), ' (scaduto)', "
			+ "LEAST(SYSDATE, man.DATINIZIOVAL, man.DATFINEVAL), ' (scaduto)', '') as desMansione, "
			+ "  man_p.STRDESCRIZIONE as desTipoMansione, man.flgFrequente " + " from de_mansione man "
			+ " inner join de_mansione man_p on man_p.codMansione=substr(man.codMansione, 0,5) || '00' ";

	private static final String SELECT_SQL_BASE_IDO_FREQUENTE = " select man.codMansione, "
			+ "DECODE( GREATEST(LENGTH(man.strdescrizione),100) , 100,  man.STRDESCRIZIONE, SUBSTR(man.strdescrizione, 1, 100) || '...' ) "
			+ " || DECODE ( SYSDATE, GREATEST(SYSDATE, man.DATINIZIOVAL, man.DATFINEVAL), ' (scaduto)', "
			+ " LEAST(SYSDATE, man.DATINIZIOVAL, man.DATFINEVAL), ' (scaduto)', '') as desMansione, "
			+ " DECODE( GREATEST(LENGTH(man_p.strdescrizione),100) , 100,  man_p.STRDESCRIZIONE, SUBSTR(man_p.strdescrizione, 1, 100) || '...' ) "
			+ " as desTipoMansione, man.flgFrequente " + " from vw_de_mansione_flgFrequente man "
			+ " inner join de_mansione man_p on man_p.codMansione=substr(man.codMansione, 0, 5) || '00' ";

	private static final String SELECT_SQL_BASE = " select man.codMansione, " + "man.STRDESCRIZIONE "
			+ " || DECODE (SYSDATE, GREATEST(SYSDATE, man.DATINIZIOVAL, man.DATFINEVAL), ' (scaduto)', "
			+ "LEAST(SYSDATE, man.DATINIZIOVAL, man.DATFINEVAL), ' (scaduto)', '') as desMansione, "
			+ "  man_p.STRDESCRIZIONE as desTipoMansione, man.flgFrequente " + " from de_mansione man "
			+ " inner join de_mansione man_p on man_p.codmansione=substr(man.codMansione, 0, 5) || '00' ";

	private static final String SELECT_SQL_BASE_FREQUENTE = " select man.codMansione, "
			+ "DECODE( GREATEST(LENGTH(man.strdescrizione),100) , 100,  man.STRDESCRIZIONE, SUBSTR(man.strdescrizione, 1, 100) || '...' ) "
			+ " || DECODE ( SYSDATE, GREATEST(SYSDATE, man.DATINIZIOVAL, man.DATFINEVAL), ' (scaduto)', "
			+ " LEAST(SYSDATE, man.DATINIZIOVAL, man.DATFINEVAL), ' (scaduto)', '') as desMansione, "
			+ " DECODE( GREATEST(LENGTH(man_p.strdescrizione),100) , 100,  man_p.STRDESCRIZIONE, SUBSTR(man_p.strdescrizione, 1, 100) || '...' ) "
			+ " as desTipoMansione, man.flgFrequente " + " from vw_de_mansione_flgFrequente man "
			+ " inner join de_mansione man_p on man_p.codmansione=substr(man.codMansione, 0, 5) || '00' ";

	public String getStatement(RequestContainer requestContainer, SourceBean config) {
		SourceBean req = requestContainer.getServiceRequest();
		String descMansione = (String) req.getAttribute("desMansione");
		String flgIDO = (String) req.getAttribute("FLGIDO");
		boolean flgFrequente = req.containsAttribute("flgFrequente") ? true : false;

		StringBuffer query_totale = null;

		if (flgIDO != null && flgIDO.equals("S")) {
			if (flgFrequente) {
				query_totale = new StringBuffer(SELECT_SQL_BASE_IDO_FREQUENTE);
			} else {
				query_totale = new StringBuffer(SELECT_SQL_BASE_IDO);
			}

			query_totale.append(" where lower(man.strdescrizione) like '%' || lower('"
					+ StringUtils.formatValue4Sql(descMansione) + "') || '%' ");

			if (flgFrequente) {
				query_totale.append(" and UPPER(man.flgfrequente) = 'S' ");
			}

			query_totale.append(" and man.flgido = 'S' ");
		} else {
			if (flgFrequente) {
				query_totale = new StringBuffer(SELECT_SQL_BASE_FREQUENTE);
			} else {
				query_totale = new StringBuffer(SELECT_SQL_BASE);
			}

			query_totale.append(" where lower(man.strdescrizione) like '%' || lower('"
					+ StringUtils.formatValue4Sql(descMansione) + "') || '%' ");

			if (flgFrequente) {
				query_totale.append(" and UPPER(man.flgfrequente) = 'S' ");
			}

			query_totale.append(" and man.flgmov = 'S' ");
		}

		query_totale.append(" order by desMansione ");

		_logger.debug(className + "::Stringa di ricerca:" + query_totale.toString());

		return query_totale.toString();
	}
}