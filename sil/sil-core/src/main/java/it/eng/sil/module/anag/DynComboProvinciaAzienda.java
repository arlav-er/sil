/*
 * Creato il 15-dic-06
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

public class DynComboProvinciaAzienda implements IDynamicStatementProvider2 {

	private String SELECT_PROV_AZIENDA = " select distinct " + " 	   prov.CODPROVINCIA as codice, "
			+ " 	   prov.STRDENOMINAZIONE as descrizione " + " from   CM_RICH_COMP_TERR ct, "
			+ " 	   an_unita_azienda un_az, " + " 	   de_comune com, " + " 	   de_provincia prov "
			+ " where  ct.PRGAZIENDA = un_az.PRGAZIENDA " + "   and  un_az.CODCOM = com.CODCOM "
			+ "   and  com.CODPROVINCIA = prov.CODPROVINCIA ";

	public DynComboProvinciaAzienda() {
	}

	public String getStatement(SourceBean req, SourceBean response) {

		String prgAzienda = StringUtils.getAttributeStrNotNull(req, "prgAzienda");

		StringBuffer query_totale = new StringBuffer(SELECT_PROV_AZIENDA);
		StringBuffer buf = new StringBuffer();

		if ((prgAzienda != null) && (!prgAzienda.equals(""))) {
			buf.append(" AND");
			buf.append(" ct.PRGAZIENDA = " + prgAzienda);
		}

		query_totale.append(buf.toString());
		return query_totale.toString();
	}

}
