/*
 * Creato il 20-ott-06
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

public class DynSelectAzienda implements IDynamicStatementProvider2 {

	private String SELECT_AZIENDA = " select " + " az.PRGAZIENDA, " + " az.STRRAGIONESOCIALE, "
			+ " az.STRCODICEFISCALE, " + " az.STRPARTITAIVA " + " from AN_AZIENDA az ";

	public DynSelectAzienda() {
	}

	public String getStatement(SourceBean req, SourceBean response) {

		String ragioneSoc = StringUtils.getAttributeStrNotNull(req, "ragioneSoc");
		String codFisc = StringUtils.getAttributeStrNotNull(req, "CodFisc");
		String pIva = StringUtils.getAttributeStrNotNull(req, "PIVA");

		StringBuffer query_totale = new StringBuffer(SELECT_AZIENDA);
		StringBuffer buf = new StringBuffer();

		if (!((ragioneSoc == null || ragioneSoc.equals("")) && (codFisc == null || codFisc.equals(""))
				&& (pIva == null || pIva.equals("")))) {
			buf.append(" WHERE ");
			int k = 0;
			if ((ragioneSoc != null) && (!ragioneSoc.equals(""))) {
				ragioneSoc = StringUtils.replace(ragioneSoc, "'", "''");
				buf.append(" upper(az.STRRAGIONESOCIALE) like '%" + ragioneSoc.toUpperCase() + "%' ");
				k++;
			}

			if ((codFisc != null) && (!codFisc.equals(""))) {
				if (k != 0)
					buf.append(" AND");
				codFisc = StringUtils.replace(codFisc, "'", "''");
				buf.append(" upper(az.STRCODICEFISCALE) = '" + codFisc.toUpperCase() + "' ");
				k++;
			}

			if ((pIva != null) && (!pIva.equals(""))) {
				if (k != 0)
					buf.append(" AND");
				buf.append(" upper(az.STRPARTITAIVA) = '" + pIva.toUpperCase() + "' ");
				k++;
			}

		}

		buf.append(" ORDER BY upper(az.STRRAGIONESOCIALE)");
		query_totale.append(buf.toString());
		return query_totale.toString();

	}
}
