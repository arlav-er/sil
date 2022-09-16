package it.eng.sil.module.patto;

import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.dbaccess.sql.IDynamicStatementProvider2;

public class DynamicElencoAnagraficoStoricoStatement implements IDynamicStatementProvider2 {
	/*
	 * SELECT DISTINCT TO_CHAR (ea.datinizio, 'dd/mm/yyyy') AS dataiscr, ea.datinizio, lav.cdnlavoratore,
	 * lav.strcodicefiscale, lav.strcognome, lav.strnome, ea.prgelencoanagrafico, TO_CHAR (ea.datcan, 'dd/mm/yyyy') AS
	 * datacan, pg_utils.trunc_desc (define.strdescrizione, 30, '...' ) AS motivofine, pg_utils.trunc_desc
	 * (cpi.strdescrizione, 20, '...') AS cpi FROM an_lavoratore lav, am_elenco_anagrafico ea, an_lav_storia_inf_coll
	 * coll, an_lav_storia_inf stlav, de_motivo_fine_atto define, de_cpi cpi WHERE lav.cdnlavoratore = ea.cdnlavoratore
	 * AND ea.datcan IS NOT NULL AND ea.prgelencoanagrafico = coll.strchiavetabella AND coll.codlsttab = 'EA' AND
	 * coll.prglavstoriainf = stlav.prglavstoriainf AND cpi.codcpi = stlav.codcpitit AND ea.codtipocan =
	 * define.codmotivofineatto(+) AND lav.cdnlavoratore = 12 ORDER BY ea.datinizio DESC
	 */
	final static String QUERY_BASE = "select " +
	// FV Eliminati gli hint
	// "/*+ Index(ea IX_AM_ELENCO_AN_AN_LAVORATOR) "+
	// "Index(am_ IX_IE_AM_ELENCO_ANAG_DAT_NULL) "+
	// "Index(define PK_DE_MOTIVO_FINE_ATTO) "+
	// "Index(coll IX_AN_LAV_STORI_fk) "+
	// "Index(stlav PK_AN_LAV_STORIA_INF) "+
	// "Index(cpi PK_DE_CPI)*/ "+
			"to_char(ea.datinizio, 'dd/mm/yyyy') as DATAISCR, ea.datinizio,  "
			+ "lav.cdnlavoratore, lav.strcodicefiscale, lav.strcognome, lav.strnome, "
			+ "ea.prgelencoanagrafico, to_char(ea.datcan, 'dd/mm/yyyy') as DATACAN, "
			+ "PG_UTILS.trunc_desc(define.STRDESCRIZIONE,30,'...') as MOTIVOFINE,  "
			+ "PG_UTILS.trunc_desc(cpi.strdescrizione,20,'...') as CPI FROM an_lavoratore lav,    "
			+ "am_elenco_anagrafico ea, an_lav_storia_inf_coll  coll, an_lav_storia_inf stlav,    "
			+ "de_motivo_fine_atto define, de_cpi cpi WHERE lav.cdnlavoratore =      "
			+ "ea.cdnlavoratore AND nvl(ea.datcan,to_date('01/01/2100','DD/MM/YYYY'))  <> to_date('01/01/2100','DD/MM/YYYY') "
			+ "AND ea.prgElencoAnagrafico =  coll.strChiaveTabella AND coll.codLstTab = 'EA' AND coll.prgLavStoriaInf = "
			+ "stlav.prgLavStoriaInf AND cpi.codCPI = stlav.codCPITit  "
			+ "and ea.CODTIPOCAN=define.CODMOTIVOFINEATTO(+) ";

	final static String QUERY_TAIL = " ORDER BY ea.datinizio desc ";

	/**
	 * Mi permette di testare tramite main la query risultante in base ai valori presenti nel sourcebean restituito dal
	 * metodo getTestRequest();
	 */
	private static final boolean DEBUG = false;

	public String getStatement(SourceBean request, SourceBean response) {
		String cdnLavoratore = null;

		if (DEBUG) {
			request = getTestRequest();
		}

		cdnLavoratore = (String) request.getAttribute("cdnLavoratore");

		StringBuffer query = new StringBuffer();
		query.append(QUERY_BASE);

		if ((cdnLavoratore != null) && (cdnLavoratore.length() > 0)) {
			query.append(" and lav.cdnLavoratore = ");
			query.append(cdnLavoratore);
			query.append(" ");
		}

		query.append(QUERY_TAIL);

		return query.toString();
	}

	/* test di generazione dello statement */
	public static void main(String[] a) {
		DynamicElencoAnagraficoStoricoStatement l = new DynamicElencoAnagraficoStoricoStatement();
		System.out.println(l.getStatement(null, null));
	}

	/**
	 * 
	 * genera una request di test
	 */
	private SourceBean getTestRequest() {
		SourceBean s = null;

		try {
			s = new SourceBean("TEST");
			s.setAttribute("cdnLavoratore", "12");
		} catch (Exception e) {
			e.printStackTrace();
		}

		return s;
	}
}
