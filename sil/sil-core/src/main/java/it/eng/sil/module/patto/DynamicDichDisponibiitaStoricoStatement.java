package it.eng.sil.module.patto;

import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.dbaccess.sql.IDynamicStatementProvider2;

public class DynamicDichDisponibiitaStoricoStatement implements IDynamicStatementProvider2 {
	/*
	 * SELECT to_char(did.datdichiarazione, 'dd/mm/yyyy') as DATDICHIARAZIONE, did.datdichiarazione datasort,
	 * lav.strnome , lav.strcognome, lav.strCodiceFiscale, to_char(did.datfine, 'dd/mm/yyyy') as DATFINE,
	 * did.prgDichDisponibilita, lav.cdnLavoratore as CDNLAVORATORE, pg_utils.trunc_desc(define.STRDESCRIZIONE,20,'...')
	 * as MOTIVOFINE, pg_utils.trunc_desc(dedid2.strdescrizione,20,'...') AS TIPODICHIARAZIONE,
	 * to_char(did.datScadConferma, 'dd/mm/yyyy') as DATSCADCONFERMA, to_char(did.datScadErogazServizi, 'dd/mm/yyyy') as
	 * DATSCADEROGAZSERVIZI, desa.strdescrizione as STATOATTO FROM an_lavoratore lav, am_elenco_anagrafico ea,
	 * AM_dich_disponibilita did, de_tipo_dich_disp dedid , an_lav_storia_inf_coll coll, an_lav_storia_inf stlav,
	 * de_motivo_fine_atto define, de_tipo_dich_disp dedid2, de_stato_atto desa WHERE lav.cdnlavoratore =
	 * ea.cdnlavoratore AND ea.prgelencoanagrafico = did.prgelencoanagrafico AND dedid.codtipodichdisp =
	 * did.codtipodichdisp AND ea.prgElencoAnagrafico = coll.strChiaveTabella AND coll.codLstTab = 'EA' AND
	 * coll.prgLavStoriaInf = stlav.prgLavStoriaInf AND did.datFine is not null and
	 * did.CODMOTIVOFINEATTO=define.CODMOTIVOFINEATTO(+) and dedid2.codtipodichdisp=did.codTipoDichDisp and
	 * did.codstatoAtto=desa.codstatoatto and lav.CDNLAVORATORE=12 ORDER BY did.datdichiarazione desc
	 */
	final static String QUERY_BASE = "SELECT                                                                            "
			+ "to_char(did.datdichiarazione, 'dd/mm/yyyy') as DATDICHIARAZIONE,                  "
			+ "did.datdichiarazione datasort,                                                    "
			+ "to_char(did.datfine, 'dd/mm/yyyy') as DATFINE,                                    "
			+ "did.prgDichDisponibilita,                                                         "
			+ "pg_utils.trunc_desc(define.STRDESCRIZIONE,20,'...') as MOTIVOFINE,                "
			+ "pg_utils.trunc_desc(dedid.strdescrizione,20,'...') AS TIPODICHIARAZIONE,         "
			+ "to_char(did.datScadConferma, 'dd/mm/yyyy') as DATSCADCONFERMA,                    "
			+ "to_char(did.datScadErogazServizi, 'dd/mm/yyyy') as DATSCADEROGAZSERVIZI,          "
			+ "desa.strdescrizione || decode(did.codmotannullamentoatto,null,'','<br>' || ann.strdescrizione)  as statoatto "
			+ "FROM                                                                              "
			+ "am_elenco_anagrafico ea,                                                          "
			+ "AM_dich_disponibilita did,                                                        "
			+ "de_tipo_dich_disp dedid ,                                                         "
			+ "de_stato_atto desa,                                                                "
			+ "de_motivo_fine_atto define,                                                       "
			+ "de_mot_annullamento_atto ann														"
			+ "WHERE (                                                                            "
			+ "(nvl(did.datFine,to_date('01/01/2100','DD/MM/YYYY')) <> to_date('01/01/2100','DD/MM/YYYY')) "
			+ "or (did.datFine is null and desa.CODMONOPRIMADOPOINS = 'D') " + ") "
			+ "AND ea.prgelencoanagrafico = did.prgelencoanagrafico                              "
			+ "AND dedid.codtipodichdisp = did.codtipodichdisp                                   "
			+ "and did.CODMOTIVOFINEATTO=define.CODMOTIVOFINEATTO(+)                             "
			+ "and did.codstatoAtto=desa.codstatoatto                                            "
			+ "and did.codmotannullamentoatto = ann.codmotannullamentoatto (+) 					";

	final static String QUERY_TAIL = " ORDER BY did.datdichiarazione desc ";

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
			query.append(" and ea.cdnLavoratore = ");
			query.append(cdnLavoratore);
			query.append(" ");
		}

		query.append(QUERY_TAIL);

		return query.toString();
	}

	/* test di generazione dello statement */
	public static void main(String[] a) {
		DynamicDichDisponibiitaStoricoStatement l = new DynamicDichDisponibiitaStoricoStatement();
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
