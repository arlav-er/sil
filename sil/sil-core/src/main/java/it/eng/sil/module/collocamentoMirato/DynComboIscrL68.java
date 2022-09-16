/*
 * Creato il 10-mag-07
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
public class DynComboIscrL68 implements IDynamicStatementProvider2 {

	private String SELECT_STATO_ATTO = " select sa.CODSTATOATTO as codice, "
			+ " 	    sa.STRDESCRIZIONE as descrizione " + " from  de_stato_atto sa, "
			+ " 	   de_stato_atto_lst_tab sat " + " where  sat.CODSTATOATTO = sa.CODSTATOATTO "
			+ "    and sat.CODLSTTAB = 'AM_CM_IS' ";

	public DynComboIscrL68() {
	}

	public String getStatement(SourceBean req, SourceBean response) {

		boolean nuovaIscrizione = !(req.containsAttribute("prgCMIscr")) || req.getAttribute("prgCMIscr") == null;
		String codStatoAtto = StringUtils.getAttributeStrNotNull(req, "CODSTATOATTO");
		String iscrizionePossibile = "false";
		Object iscrPoss = req.getAttribute("ISCRIZIONEPOSSIBILE");
		if (iscrPoss != null) {
			iscrizionePossibile = (String) iscrPoss;
		}

		StringBuffer query_totale = new StringBuffer(SELECT_STATO_ATTO);
		StringBuffer buf = new StringBuffer();

		if (nuovaIscrizione) {
			buf.append(" AND");
			buf.append(" sa.codstatoatto in ('NP','PR') ");
		} else if (((codStatoAtto != null) && (!codStatoAtto.equals(""))) && codStatoAtto.equals("PR")) {
			buf.append(" AND");
			buf.append(" sa.codstatoatto in ('AN','PR') ");
		} else if ((iscrizionePossibile.equals("false") && ((codStatoAtto != null) && (!codStatoAtto.equals("")))
				&& codStatoAtto.equals("NP"))) {
			buf.append(" AND");
			buf.append(" sa.codstatoatto in ('NP','AN') ");
		}

		query_totale.append(buf.toString());
		return query_totale.toString();
	}
}
