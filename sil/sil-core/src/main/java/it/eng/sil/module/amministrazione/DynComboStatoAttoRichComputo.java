/*
 * Creato il 1-feb-07
 *
 * Per modificare il modello associato a questo file generato, aprire
 * Finestra&gt;Preferenze&gt;Java&gt;Generazione codice&gt;Codice e commenti
 */
package it.eng.sil.module.amministrazione;

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

public class DynComboStatoAttoRichComputo implements IDynamicStatementProvider2 {

	private String SELECT_STATO_ATTO = " select sa.CODSTATOATTO as codice, "
			+ " 	   sa.STRDESCRIZIONE as descrizione " + " from   de_stato_atto sa, "
			+ " 	   de_stato_atto_lst_tab sat " + " where  sat.CODSTATOATTO = sa.CODSTATOATTO "
			+ "    and sat.CODLSTTAB = 'CM_COMPU' ";

	public DynComboStatoAttoRichComputo() {
	}

	public String getStatement(SourceBean req, SourceBean response) {
		String nuovoComputo = StringUtils.getAttributeStrNotNull(req, "nuovoComputo");
		String codStato = StringUtils.getAttributeStrNotNull(req, "CODSTATOATTO");

		StringBuffer query_totale = new StringBuffer(SELECT_STATO_ATTO);
		StringBuffer buf = new StringBuffer();

		if (((codStato != null) && (!codStato.equals(""))) && codStato.equals("PR")) {
			buf.append(" AND");
			buf.append(" sa.codstatoatto in ('AN','PR') ");
		} else if (nuovoComputo.equals("1")) {
			buf.append(" AND");
			buf.append(" sa.codstatoatto in ('NP','PR') ");
		}

		query_totale.append(buf.toString());
		return query_totale.toString();

	}
}
