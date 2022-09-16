package it.eng.sil.module.anag;

import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.dbaccess.sql.IDynamicStatementProvider2;
import it.eng.afExt.utils.StringUtils;

public class DynComboIscrListeSpeciali implements IDynamicStatementProvider2 {

	private String SELECT_STATO_ATTO = " select sa.CODSTATOATTO as codice, "
			+ " 	    sa.STRDESCRIZIONE as descrizione " + " from  de_stato_atto sa, "
			+ " 	   de_stato_atto_lst_tab sat " + " where  sat.CODSTATOATTO = sa.CODSTATOATTO "
			+ "    and sat.CODLSTTAB = 'CM_ISCLS' ";

	public DynComboIscrListeSpeciali() {
	}

	public String getStatement(SourceBean req, SourceBean response) {
		String nuovo = StringUtils.getAttributeStrNotNull(req, "nuovo");
		String codStato = StringUtils.getAttributeStrNotNull(req, "CODSTATOATTO");

		StringBuffer query_totale = new StringBuffer(SELECT_STATO_ATTO);
		StringBuffer buf = new StringBuffer();

		if (((codStato != null) && (!codStato.equals(""))) && codStato.equals("PR")) {
			buf.append(" AND");
			buf.append(" sa.codstatoatto in ('AN','PR') ");
		} else if (nuovo.equals("true")) {
			buf.append(" AND");
			buf.append(" sa.codstatoatto in ('NP','PR') ");
		}

		query_totale.append(buf.toString());
		return query_totale.toString();
	}
}
