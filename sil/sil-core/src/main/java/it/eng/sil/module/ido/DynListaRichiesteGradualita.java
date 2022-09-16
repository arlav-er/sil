/*
 * Creato il 17-ott-06
 *
 * Per modificare il modello associato a questo file generato, aprire
 * Finestra&gt;Preferenze&gt;Java&gt;Generazione codice&gt;Codice e commenti
 */
package it.eng.sil.module.ido;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.dbaccess.sql.IDynamicStatementProvider;
import it.eng.sil.util.Utils;

/**
 * @author riccardi
 * 
 *         Per modificare il modello associato al commento di questo tipo generato, aprire
 *         Finestra&gt;Preferenze&gt;Java&gt;Generazione codice&gt;Codice e commenti
 */
public class DynListaRichiesteGradualita implements IDynamicStatementProvider {
	private final static String stm = "SELECT prgrichgradualita, an_azienda.strcodicefiscale, de_stato_atto.STRDESCRIZIONE as STATORICHIESTA, "
			+ "an_azienda.strpartitaiva, an_azienda.strragionesociale, codstatorichiesta, an_azienda.prgazienda, " +

			"		 (select " + "			doc.CODSTATOATTO " + "			from am_documento doc "
			+ "			inner join am_documento_coll docColl on docColl.prgdocumento = doc.prgdocumento "
			+ "			where docColl.STRCHIAVETABELLA = to_char(cm_rich_gradualita.prgrichgradualita) "
			+ "			and doc.CODTIPODOCUMENTO = 'RICHGRAD' " + "		 ) as CODSTATOATTO, " +

			" 	(SELECT sa.STRDESCRIZIONE "
			+ " 	   FROM de_stato_atto sa, am_documento doc INNER JOIN am_documento_coll doccoll ON doccoll.prgdocumento = doc.prgdocumento "
			+ " 	  WHERE doccoll.strchiavetabella = TO_CHAR(cm_rich_gradualita.prgrichgradualita) "
			+ " 	 	 AND doc.codtipodocumento = 'RICHGRAD' " + "		 and sa.CODSTATOATTO = doc.CODSTATOATTO "
			+ " 	) AS statoatto, " +

			"to_char(datrichiesta, 'dd/mm/yyyy') AS datRichiesta, "
			+ "to_char(datpassaggio, 'dd/mm/yyyy') AS datPassaggio, " + "numlavdopopassaggio "
			+ "  FROM cm_rich_gradualita, an_azienda, de_stato_atto "
			+ " WHERE cm_rich_gradualita.prgazienda = an_azienda.prgazienda "
			+ "   and de_stato_atto.CODSTATOATTO = cm_rich_gradualita.CODSTATORICHIESTA ";

	private final static String selectCodStatoAtto = " select codstatoatto, "
			+ "		 prgrichgradualita, strcodicefiscale, STATORICHIESTA, "
			+ "		 strpartitaiva, strragionesociale, codstatorichiesta, prgazienda, statoatto, "
			+ "		 datRichiesta, datPassaggio, numlavdopopassaggio " + "   from ( ";

	public String getStatement(RequestContainer requestContainer, SourceBean config) {
		SourceBean serviceRequest = requestContainer.getServiceRequest();

		String prgAzienda = (String) serviceRequest.getAttribute("PRGAZIENDA");

		/*
		 * String prgAziendaApp = ""+serviceRequest.getAttribute("PRGAZIENDAAPP"); if (prgAziendaApp.equals("null") ||
		 * prgAziendaApp.equals("") ){ prgAzienda = null; }else if (!prgAziendaApp.equals("")){ prgAzienda =
		 * prgAziendaApp; }
		 */

		// se e' presente il prgAzienda allora provengo dal menu contestuale
		// dell'unità azienda
		String codiceFiscale = Utils.notNull(serviceRequest.getAttribute("STRCODICEFISCALE"));
		String piva = Utils.notNull(serviceRequest.getAttribute("STRPARTITAIVA"));
		String ragSociale = Utils.notNull(serviceRequest.getAttribute("STRRAGIONESOCIALE"));
		String statoAtto = Utils.notNull(serviceRequest.getAttribute("STATOATTORICH"));
		String dataRichiestaDa = Utils.notNull(serviceRequest.getAttribute("DATRICHIESTA_DA"));
		String dataRichiestaA = Utils.notNull(serviceRequest.getAttribute("DATRICHIESTA_A"));
		String statoAttoDocRich = Utils.notNull(serviceRequest.getAttribute("statoAttoDocRich"));

		String whereCodStatoAtto = " ) where CODSTATOATTO = '" + statoAttoDocRich + "'";

		String queryStatoAtto = selectCodStatoAtto + stm;

		StringBuffer query = null;
		if (statoAttoDocRich.equals("")) {
			query = new StringBuffer(stm);
		} else {
			query = new StringBuffer(queryStatoAtto);
		}

		if (prgAzienda == null) {
			String prefissoRicerca = " like upper('";
			String suffissoRicerca = "%')";
			if (!"".equals(codiceFiscale)) {
				query.append(" AND an_azienda.STRCODICEFISCALE ");
				query.append(prefissoRicerca);
				query.append(codiceFiscale);
				query.append(suffissoRicerca);
			}
			if (!"".equals(piva)) {
				query.append(" AND an_azienda.STRPARTITAIVA ");
				query.append(prefissoRicerca);
				query.append(piva);
				query.append(suffissoRicerca);
			}
			if (!"".equals(ragSociale)) {
				query.append(" AND an_azienda.STRRAGIONESOCIALE ");
				query.append(prefissoRicerca);
				query.append(ragSociale);
				query.append(suffissoRicerca);
			}

		}
		if (prgAzienda != null) {
			query.append(" AND cm_rich_gradualita.prgazienda = ");
			query.append(prgAzienda);
		}
		if (!"".equals(statoAtto)) {
			query.append(" AND cm_rich_gradualita.codstatorichiesta = '");
			query.append(statoAtto);
			query.append("'");
		}
		if (!"".equals(dataRichiestaDa) && !"".equals(dataRichiestaA)) {
			query.append(" AND trunc(cm_rich_gradualita.datRichiesta) between to_date('");
			query.append(dataRichiestaDa);
			query.append("', 'dd/mm/yyyy') and to_date('");
			query.append(dataRichiestaA);
			query.append("', 'dd/mm/yyyy')");
		} else {
			if (!"".equals(dataRichiestaDa)) {
				query.append(" AND trunc(cm_rich_gradualita.datRichiesta) >= to_date('");
				query.append(dataRichiestaDa);
				query.append("', 'dd/mm/yyyy')");
			}
			if (!"".equals(dataRichiestaA)) {
				query.append(" AND trunc(cm_rich_gradualita.datRichiesta) <= to_date('");
				query.append(dataRichiestaA);
				query.append("', 'dd/mm/yyyy')");
			}
		}
		query.append(" ORDER BY cm_rich_gradualita.datRichiesta desc, an_azienda.strragionesociale ");

		if (!statoAttoDocRich.equals("")) {
			query.append(whereCodStatoAtto);
		}
		// String prova = query.toString();
		return query.toString();
	}

}