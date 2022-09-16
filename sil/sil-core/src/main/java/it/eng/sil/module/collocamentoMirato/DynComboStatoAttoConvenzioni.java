/*
 * Creato il 16-gen-07
 *
 * Per modificare il modello associato a questo file generato, aprire
 * Finestra&gt;Preferenze&gt;Java&gt;Generazione codice&gt;Codice e commenti
 */

package it.eng.sil.module.collocamentoMirato;

import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.dbaccess.sql.IDynamicStatementProvider2;
import it.eng.afExt.utils.StringUtils;

/**
 * @author riccardi
 * 
 *         Per modificare il modello associato al commento di questo tipo generato, aprire
 *         Finestra&gt;Preferenze&gt;Java&gt;Generazione codice&gt;Codice e commenti
 */
public class DynComboStatoAttoConvenzioni implements IDynamicStatementProvider2 {

	private String SELECT_STATO_ATTO = " select sa.CODSTATOATTO as codice, "
			+ " 	   sa.STRDESCRIZIONE as descrizione " + " from   de_stato_atto sa, "
			+ " 	   de_stato_atto_lst_tab sat " + " where  sat.CODSTATOATTO = sa.CODSTATOATTO "
			+ "    and sat.CODLSTTAB = 'CMDCONV' ";

	public DynComboStatoAttoConvenzioni() {
	}

	public String getStatement(SourceBean req, SourceBean response) {

		boolean nuovo = req.containsAttribute("nuova");

		String codStato = StringUtils.getAttributeStrNotNull(req, "CODSTATOATTO");

		StringBuffer query_totale = new StringBuffer(SELECT_STATO_ATTO);
		StringBuffer buf = new StringBuffer();

		if (nuovo) {
			buf.append(" AND");
			buf.append(" sa.CODSTATOATTO = 'NP' ");
		} else if (((codStato != null) && (!codStato.equals(""))) && codStato.equals("PR")) {
			buf.append(" AND");
			buf.append(" (sa.CODSTATOATTO = 'PR' or sa.CODSTATOATTO = 'AN')");
		}

		query_totale.append(buf.toString());
		return query_totale.toString();

	}

}