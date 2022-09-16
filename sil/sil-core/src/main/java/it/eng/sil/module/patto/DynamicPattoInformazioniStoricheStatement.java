package it.eng.sil.module.patto;

import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.dbaccess.sql.IDynamicStatementProvider2;

public class DynamicPattoInformazioniStoricheStatement implements IDynamicStatementProvider2 {
	/*
	 * select distinct to_char(patto.datstipula, 'dd/mm/yyyy') as DATSTIPULA, patto.datstipula datasort,
	 * lav.strcodicefiscale, lav.strcognome, lav.strnome, patto.prgpattolavoratore, patto.codcpi, patto.cdnlavoratore,
	 * patto.prgdichdisponibilita, patto.codstatoatto, pg_utils.trunc_desc(desa.strdescrizione,15,'...') as STATOPATTO,
	 * patto.prgstatooccupaz, patto.flgcomunicazesiti, patto.codmotivofineatto,
	 * pg_utils.trunc_desc(define.STRDESCRIZIONE,20,'...') as motivofine, to_char(patto.datscadconferma, 'dd/mm/yyyy')
	 * as DATSCADCONFERMA, to_char(patto.datfine, 'dd/mm/yyyy') as DATFINE, patto.strnote, patto.cdnutins, patto.dtmins,
	 * patto.cdnutmod, patto.dtmmod, patto.numklopattolavoratore, decode(patto.flgpatto297,'S','Patto 150','N','Accordo
	 * Generico') as TIPOPATTO FROM an_lavoratore lav, am_patto_lavoratore patto, am_elenco_anagrafico ea,
	 * an_lav_storia_inf_coll coll, an_lav_storia_inf stlav, de_motivo_fine_atto define, de_stato_atto desa WHERE
	 * lav.cdnlavoratore = patto.cdnlavoratore AND lav.cdnlavoratore = ? AND patto.datFine is not null AND
	 * lav.CDNLAVORATORE = ea.CDNLAVORATORE AND ea.prgElencoAnagrafico = coll.strChiaveTabella AND coll.codLstTab = 'EA'
	 * AND coll.prgLavStoriaInf = stlav.prgLavStoriaInf and patto.CODMOTIVOFINEATTO = define.CODMOTIVOFINEATTO(+) and
	 * desa.codstatoatto=patto.codstatoatto ORDER BY patto.datstipula desc
	 */
	// NOTA 11/09/2006 Savino: query patto lista storico. tolte sia
	// an_lav_storia_inf_coll che an_lav_storia_inf (controllare forse e'
	// sbagliato)
	// TODO Savino: attenzione alla presenza di due am_elenco_anagrafico (da
	// trasferimento). E in caso di mobilita'?
	final static String QUERY_BASE = "select distinct to_char(patto.datstipula, 'dd/mm/yyyy') as DATSTIPULA,  patto.datstipula datasort, lav.strcodicefiscale,"
			+ "lav.strcognome, lav.strnome, patto.prgpattolavoratore, patto.codcpi,  patto.cdnlavoratore, patto.prgdichdisponibilita, "
			+ "patto.codstatoatto, pg_utils.trunc_desc(desa.strdescrizione,15,'...') as STATOPATTO, patto.prgstatooccupaz, patto.flgcomunicazesiti, "
			+ "patto.codmotivofineatto, pg_utils.trunc_desc(define.STRDESCRIZIONE,20,'...') as motivofine, to_char(patto.datscadconferma, 'dd/mm/yyyy') as DATSCADCONFERMA,"
			+ "to_char(patto.datfine, 'dd/mm/yyyy') as DATFINE, patto.strnote, patto.cdnutins, patto.dtmins, patto.cdnutmod, patto.dtmmod, "
			+ "patto.numklopattolavoratore, nvl(de_codifica_patto.strdescrizione, decode(patto.flgpatto297,'S','Patto 150','N','Accordo Generico')) as TIPOPATTO "
			+ "FROM " + "an_lavoratore lav,  am_patto_lavoratore patto,  am_elenco_anagrafico ea,"
			+ "de_motivo_fine_atto define, de_stato_atto desa, de_codifica_patto   WHERE  "
			+ "lav.cdnlavoratore           = patto.cdnlavoratore AND  patto.datFine  is not  null "
			+ "AND lav.CDNLAVORATORE       = ea.CDNLAVORATORE   "
			+ "and patto.CODMOTIVOFINEATTO = define.CODMOTIVOFINEATTO(+) "
			+ "and patto.CODCODIFICAPATTO = de_codifica_patto.CODCODIFICAPATTO(+) "
			+ "and desa.codstatoatto=patto.codstatoatto  ";
	final static String QUERY_TAIL = " ORDER BY patto.datstipula desc ";

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
		DynamicPattoInformazioniStoricheStatement l = new DynamicPattoInformazioniStoricheStatement();
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
