/*
 * Creato il 13-dic-06
 *
 * Per modificare il modello associato a questo file generato, aprire
 * Finestra&gt;Preferenze&gt;Java&gt;Generazione codice&gt;Codice e commenti
 */
package it.eng.sil.module.anag;

import com.engiweb.framework.base.SourceBean;

/**
 * @author melandri
 * 
 * Per modificare il modello associato al commento di questo tipo generato,
 * aprire Finestra&gt;Preferenze&gt;Java&gt;Generazione codice&gt;Codice e
 * commenti
 */

import it.eng.afExt.dbaccess.sql.IDynamicStatementProvider2;
import it.eng.afExt.utils.StringUtils;

public class DynComboStatoRichCmCT implements IDynamicStatementProvider2 {

	private String SELECT_STATO_ATTO = " select sa.CODSTATOATTO as codice, "
			+ " 	   sa.STRDESCRIZIONE as descrizione " + " from   de_stato_atto sa, "
			+ " 	   de_stato_atto_lst_tab sat " + " where  sat.CODSTATOATTO = sa.CODSTATOATTO "
			+ "    and sat.CODLSTTAB = 'CM_RI_CT' ";

	public DynComboStatoRichCmCT() {
	}

	public String getStatement(SourceBean req, SourceBean response) {

		String nuovaRichiesta = StringUtils.getAttributeStrNotNull(req, "nuovaRichiesta");
		String codStato = StringUtils.getAttributeStrNotNull(req, "CODSTATORICHIESTA");

		StringBuffer query_totale = new StringBuffer(SELECT_STATO_ATTO);
		StringBuffer buf = new StringBuffer();

		if ((nuovaRichiesta != null) && (!nuovaRichiesta.equals(""))) {
			buf.append(" AND");
			buf.append(" sa.CODSTATOATTO = 'DA' ");
		} else if (((codStato != null) && (!codStato.equals(""))) && codStato.equals("NA")) {
			buf.append(" AND");
			buf.append(" (sa.CODSTATOATTO = 'NA' or sa.CODSTATOATTO = 'AN') ");
		} else if (((codStato != null) && (!codStato.equals(""))) && codStato.equals("AP")) {
			buf.append(" AND");
			buf.append(" (sa.CODSTATOATTO = 'AP' or sa.CODSTATOATTO = 'AN') ");
		}

		query_totale.append(buf.toString());
		return query_totale.toString();

	}

}
