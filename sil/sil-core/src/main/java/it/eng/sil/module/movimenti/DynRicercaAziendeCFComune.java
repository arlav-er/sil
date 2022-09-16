/*
 * Creato il 21-ott-04
 */
package it.eng.sil.module.movimenti;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.dbaccess.sql.IDynamicStatementProvider;

/**
 * @author roccetti Effettua la ricerca delle unita aziendali basandosi su CF e codCom, se quest'ultimo non è indicato
 *         ritorna tutte le unita aziendali legate al CF indicato.
 */
public class DynRicercaAziendeCFComune implements IDynamicStatementProvider {

	public String getStatement(RequestContainer requestContainer, SourceBean config) {
		SourceBean req = requestContainer.getServiceRequest();

		String cf = (String) req.getAttribute("strCodiceFiscale");
		String com = (String) req.getAttribute("codComune");

		return "SELECT az.prgazienda, az.strcodicefiscale, az.strpartitaiva, "
				+ " az.codnatgiuridica, DE_NAT_GIURIDICA.STRDESCRIZIONE as descGiuridica, "
				+ " az.codtipoazienda, DE_TIPO_AZIENDA.STRDESCRIZIONE as descTipoAzienda, "
				+ " az.strpartitaiva, AN_UNITA_AZIENDA.codccnl, de_contratto_collettivo.STRDESCRIZIONE as descCCNL, "
				+ " AN_UNITA_AZIENDA.PRGUNITA, " + " AN_UNITA_AZIENDA.strnumeroinps, " + " AN_UNITA_AZIENDA.strtel, "
				+ " AN_UNITA_AZIENDA.strfax, " + " AN_UNITA_AZIENDA.codcom, " + " AN_UNITA_AZIENDA.strcap, "
				+ " AN_UNITA_AZIENDA.STREMAIL, " + " AN_UNITA_AZIENDA.codateco, " + " AN_UNITA_AZIENDA.strindirizzo, "
				+ " AN_UNITA_AZIENDA.strlocalita, " + " AN_UNITA_AZIENDA.STRNUMREGISTROCOMMITT,  "
				+ " COM.STRDENOMINAZIONE strDescrComune,  " + " ATT.STRDESCRIZIONE strDescrAteco "
				+ " FROM AN_AZIENDA az "
				+ " LEFT JOIN AN_UNITA_AZIENDA on (AN_UNITA_AZIENDA.prgazienda = az.prgazienda) "
				+ " LEFT JOIN DE_NAT_GIURIDICA on (DE_NAT_GIURIDICA.codnatgiuridica = az.codnatgiuridica) "
				+ " INNER JOIN DE_TIPO_AZIENDA on (DE_TIPO_AZIENDA.codtipoazienda = az.codtipoazienda) "
				+ " LEFT JOIN de_contratto_collettivo on (de_contratto_collettivo.codccnl = AN_UNITA_AZIENDA.codccnl), "
				+ " DE_COMUNE COM, " + " DE_ATTIVITA ATT " + " WHERE AN_UNITA_AZIENDA.codCom = COM.CODCOM AND "
				+ " AN_UNITA_AZIENDA.codAteco = ATT.codAteco AND " + " az.strcodicefiscale = '" + cf + "' "
				+ ((com != null && !com.equals("")) ? " AND AN_UNITA_AZIENDA.codcom = '" + com + "' " : "");
	}
}