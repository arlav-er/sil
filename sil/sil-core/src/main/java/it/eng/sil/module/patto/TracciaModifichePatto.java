/*
 * Creato il 9-set-04
 *
 * Per modificare il modello associato a questo file generato, aprire
 * Finestra&gt;Preferenze&gt;Java&gt;Generazione codice&gt;Codice e commenti
 */
package it.eng.sil.module.patto;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.utils.TransactionQueryExecutor;

/**
 * @author savino
 * 
 *         Per modificare il modello associato al commento di questo tipo generato, aprire
 *         Finestra&gt;Preferenze&gt;Java&gt;Generazione codice&gt;Codice e commenti
 */
public class TracciaModifichePatto {

	public static void cancellazione(RequestContainer requestContainer, SourceBean amPattoScelta,
			TransactionQueryExecutor tx) throws Exception {
		String pref = null;
		java.sql.Connection connection = tx.getDataConnection().getInternalConnection();
		java.sql.CallableStatement stmt = connection
				.prepareCall("{call pg_gestamm.tracciaDelAssAzioniPattoLav (?,?,?,?,?,?) }");
		Object user = requestContainer.getSessionContainer().getAttribute("_CDUT_");
		if (amPattoScelta.containsAttribute("rows"))
			pref = "ROWS.";
		else
			pref = "";
		String codLstTab = (String) amPattoScelta.getAttribute(pref + "row.codLstTab");
		java.math.BigDecimal prgPattoLavoratore = (java.math.BigDecimal) amPattoScelta
				.getAttribute(pref + "row.prgPattoLavoratore");
		String strChiaveTabella = (String) amPattoScelta.getAttribute(pref + "row.strChiaveTabella");
		String strChiaveTabella2 = (String) amPattoScelta.getAttribute(pref + "row.strChiaveTabella2");
		String strChiaveTabella3 = (String) amPattoScelta.getAttribute(pref + "row.strChiaveTabella2");
		// stmt.registerOutParameter(1, OracleTypes.VARCHAR);
		stmt.setString(1, codLstTab);
		stmt.setBigDecimal(2, prgPattoLavoratore);
		stmt.setString(3, strChiaveTabella);
		stmt.setString(4, strChiaveTabella2);
		stmt.setString(5, strChiaveTabella3);
		stmt.setString(6, user.toString());
		stmt.execute();
	}
}
