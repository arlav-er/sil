package it.eng.sil.module.budget;

import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.dbaccess.sql.IDynamicStatementProvider2;
import it.eng.afExt.utils.StringUtils;

public class DynComboAzioniMisura implements IDynamicStatementProvider2 {

	public DynComboAzioniMisura() {

	}

	private static final String QUERY_GET_AZIONI_BASE = "select distinct a.prgAzioni as codice, a.strDescrizione as descrizione "
			+ "  from de_azione a "
			+ "  inner join vch_modello_voucher on (a.prgazioni = vch_modello_voucher.prgazioni) ";

	public String getStatement(SourceBean request, SourceBean response) {

		String cfSel = (String) request.getAttribute("CODICEFISCALE");
		String codSede = (String) request.getAttribute("CODSEDE");

		if (StringUtils.isEmptyNoBlank(cfSel)) {
			cfSel = (String) request.getAttribute("cfSel");
		}
		if (StringUtils.isEmptyNoBlank(codSede)) {
			codSede = (String) request.getAttribute("codSedeSel");
		}

		StringBuffer query_totale = new StringBuffer(QUERY_GET_AZIONI_BASE);
		if (StringUtils.isFilledNoBlank(cfSel) && StringUtils.isFilledNoBlank(codSede)) {

			query_totale.append(" where not exists  ")
					.append(" (select 1  from vch_ente_accreditato e where a.prgazioni = e.prgazioni ");
			query_totale.append("and  e.strcodicefiscale='").append(cfSel).append("' and e.codsede='").append(codSede)
					.append("')");
		}
		query_totale.append(" order by a.strdescrizione ");
		return query_totale.toString();

	}

}
