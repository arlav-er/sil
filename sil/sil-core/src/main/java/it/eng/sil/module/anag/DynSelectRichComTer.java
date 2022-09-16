/*
 * Creato il 14-dic-06
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

public class DynSelectRichComTer implements IDynamicStatementProvider2 {
	private String SELECT_COD_STATO = " select statoRichiesta," + " CODSTATOATTO," + " strStato," + " prgazienda,"
			+ " STRRAGIONESOCIALE," + " STRCODICEFISCALE," + " STRPARTITAIVA," + " DATRICHIESTA," + " DATFINE,"
			+ " CODSTATORICHIESTA," + " CDNUTINS," + " CDNUTMOD," + " NUMKLOCOMPTERR," + " DTMMOD," + " DTMINS, "
			+ " PRGRICHCOMPTERR," + " STRMOTIVAZIONE " + " from ( ";

	private String SELECT_RICHIESTA = " select distinct " + " 	 		stato.strdescrizione as statoRichiesta,  "
			+ "			(select " + "			doc.CODSTATOATTO " + "			from am_documento doc "
			+ "			inner join am_documento_coll docColl on docColl.prgdocumento = doc.prgdocumento "
			+ "			where docColl.STRCHIAVETABELLA = to_char(CT.PRGRICHCOMPTERR) "
			+ "			and doc.CODTIPODOCUMENTO = 'RICHCOMT' " + "			) as CODSTATOATTO, "
			+ "	        (SELECT stato.STRDESCRIZIONE "
			+ "	           FROM am_documento doc INNER JOIN am_documento_coll doccoll ON doccoll.prgdocumento = doc.prgdocumento, "
			+ "			        de_stato_atto stato "
			+ "	          WHERE doccoll.strchiavetabella = TO_CHAR (ct.prgrichcompterr) "
			+ "		        AND doc.codtipodocumento = 'RICHCOMT' "
			+ "		        and doc.CODSTATOATTO = stato.CODSTATOATTO) AS strStato, " + " 	 		az.prgazienda,  "
			+ " 	 		az.STRRAGIONESOCIALE, " + " 	 		az.STRCODICEFISCALE,  "
			+ " 	 		az.STRPARTITAIVA, " + " 	 		to_char(CT.DATRICHIESTA,'dd/mm/yyyy') as DATRICHIESTA, "
			+ " 	 		to_char(CT.DATFINE,'dd/mm/yyyy') as DATFINE,  " + " 	 		CT.CODSTATORICHIESTA,  "
			+ " 	 		CT.CDNUTINS,  " + " 	 		CT.CDNUTMOD,  " + " 	 		CT.NUMKLOCOMPTERR, "
			+ " 	 		to_char(CT.DTMMOD,'dd/mm/yyyy') as DTMMOD, "
			+ " 	 		to_char(CT.DTMINS,'dd/mm/yyyy') as DTMINS,  " + " 	 		CT.PRGRICHCOMPTERR,  "
			+ " 	 		CT.STRMOTIVAZIONE  " + " from        " + " 	 		CM_RICH_COMP_TERR CT, "
			+ " 	 		an_azienda az,  " + " 	 		de_stato_atto stato "
			+ " where 	stato.CODSTATOATTO = CT.CODSTATORICHIESTA " + " 	    and az.prgazienda = CT.prgazienda ";

	public DynSelectRichComTer() {
	}

	public String getStatement(SourceBean req, SourceBean response) {

		String STRRAGIONESOCIALE = StringUtils.getAttributeStrNotNull(req, "ragioneSoc");
		String STRCODICEFISCALE = StringUtils.getAttributeStrNotNull(req, "CodFisc");
		String STRPARTITAIVA = StringUtils.getAttributeStrNotNull(req, "PIVA");
		String DATAINIZIOVALIDITA_DAL = StringUtils.getAttributeStrNotNull(req, "DATAINIZIOVALIDITA_DAL");
		String DATAINIZIOVALIDITA_AL = StringUtils.getAttributeStrNotNull(req, "DATAINIZIOVALIDITA_AL");
		String CODSTATORICHIESTA = StringUtils.getAttributeStrNotNull(req, "CODSTATORICHIESTA");
		String fromRicerca = StringUtils.getAttributeStrNotNull(req, "fromRicerca");
		String prgAzienda = StringUtils.getAttributeStrNotNull(req, "prgAzienda");
		String CODSTATOATTO = StringUtils.getAttributeStrNotNull(req, "CODSTATOATTO");

		StringBuffer query_totale = null;
		if ((CODSTATOATTO != null) && (!CODSTATOATTO.equals(""))) {
			query_totale = new StringBuffer(SELECT_COD_STATO + SELECT_RICHIESTA);
		} else {
			query_totale = new StringBuffer(SELECT_RICHIESTA);
		}

		StringBuffer buf = new StringBuffer();

		if ((prgAzienda != null) && (!prgAzienda.equals(""))) {
			buf.append(" AND ");
			buf.append(" CT.prgAzienda = " + prgAzienda);
		}

		if ((STRRAGIONESOCIALE != null) && (!STRRAGIONESOCIALE.equals(""))) {
			buf.append(" AND");
			STRRAGIONESOCIALE = StringUtils.replace(STRRAGIONESOCIALE, "'", "''");
			buf.append(" upper(az.STRRAGIONESOCIALE) like '%" + STRRAGIONESOCIALE.toUpperCase() + "%' ");
		}

		if ((STRCODICEFISCALE != null) && (!STRCODICEFISCALE.equals(""))) {
			buf.append(" AND");
			STRCODICEFISCALE = StringUtils.replace(STRCODICEFISCALE, "'", "''");
			buf.append(" upper(az.STRCODICEFISCALE) = '" + STRCODICEFISCALE.toUpperCase() + "' ");
		}

		if ((STRPARTITAIVA != null) && (!STRPARTITAIVA.equals(""))) {
			buf.append(" AND");
			buf.append(" upper(az.STRPARTITAIVA) = '" + STRPARTITAIVA.toUpperCase() + "' ");
		}

		if (((DATAINIZIOVALIDITA_DAL != null) && (!DATAINIZIOVALIDITA_DAL.equals("")))
				&& ((DATAINIZIOVALIDITA_AL == null) || (DATAINIZIOVALIDITA_AL.equals("")))) {
			buf.append(" AND");
			buf.append(" TO_DATE('" + DATAINIZIOVALIDITA_DAL + "','DD/MM/YYYY') ");
			buf.append(" <= ");
			buf.append(" CT.DATRICHIESTA ");
		}

		if (((DATAINIZIOVALIDITA_AL != null) && (!DATAINIZIOVALIDITA_AL.equals("")))
				&& ((DATAINIZIOVALIDITA_DAL == null) || (DATAINIZIOVALIDITA_DAL.equals("")))) {
			buf.append(" AND");
			buf.append(" TO_DATE('" + DATAINIZIOVALIDITA_AL + "','DD/MM/YYYY') ");
			buf.append(" >= ");
			buf.append(" CT.DATRICHIESTA ");
		}

		if (((DATAINIZIOVALIDITA_DAL != null) && (!DATAINIZIOVALIDITA_DAL.equals("")))
				&& ((DATAINIZIOVALIDITA_AL != null) && (!DATAINIZIOVALIDITA_AL.equals("")))) {
			buf.append(" AND");
			buf.append(" TO_DATE('" + DATAINIZIOVALIDITA_AL + "','DD/MM/YYYY') ");
			buf.append(" >= ");
			buf.append(" CT.DATRICHIESTA ");
			buf.append(" AND");
			buf.append(" TO_DATE('" + DATAINIZIOVALIDITA_DAL + "','DD/MM/YYYY') ");
			buf.append(" <= ");
			buf.append(" CT.DATRICHIESTA ");
		}

		if ((CODSTATORICHIESTA != null) && (!CODSTATORICHIESTA.equals(""))) {
			buf.append(" AND");
			buf.append(" upper(CT.CODSTATORICHIESTA) = '" + CODSTATORICHIESTA.toUpperCase() + "' ");
		}

		if ((CODSTATOATTO != null) && (!CODSTATOATTO.equals(""))) {
			buf.append(" ) WHERE");
			buf.append(" upper(CODSTATOATTO) = '" + CODSTATOATTO.toUpperCase() + "' ");
		}

		query_totale.append(buf.toString());
		return query_totale.toString();

	}

}
