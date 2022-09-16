/*
 * Creato il 3-nov-06
 *
 * Per modificare il modello associato a questo file generato, aprire
 * Finestra&gt;Preferenze&gt;Java&gt;Generazione codice&gt;Codice e commenti
 */
package it.eng.sil.module.anag;

import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.dbaccess.sql.IDynamicStatementProvider2;
import it.eng.afExt.utils.StringUtils;

public class DynSelectRichEsonero implements IDynamicStatementProvider2 {
	private final static String SELECT_RICHIESTA = "select * from "
			+ "		( select az.prgazienda, az.STRRAGIONESOCIALE, "
			+ " 	(az.STRCODICEFISCALE || ' - ' || az.STRPARTITAIVA) as CFPIVA,  "
			+ " 	to_char(es.DATRICHIESTA,'dd/mm/yyyy') as DATRICHIESTA, "
			+ " 	to_char(es.DATINIZIOVALIDITA,'dd/mm/yyyy') as DATINIZIOVALIDITA, "
			+ " 	to_char(es.DATFINE,'dd/mm/yyyy') as DATFINE,  "
			+ " 	(to_char(es.DATINIZIOVALIDITA,'dd/mm/yyyy') || ' - ' || to_char(es.DATFINE,'dd/mm/yyyy')) as PERIODOVAL,  "
			+ " 	es.CODSTATORICHIESTA,  " + "   stato.strdescrizione as statoRichiesta, " + " 	es.CDNUTINS,  "
			+ " 	es.CDNUTMOD,  " + " 	es.NUMKLOESONERO, " + " 	to_char(es.DTMMOD,'dd/mm/yyyy') as DTMMOD, "
			+ " 	to_char(es.DTMINS,'dd/mm/yyyy') as DTMINS,  " + " 	es.PRGRICHESONERO,  "
			+ " 	PR.STRDENOMINAZIONE as PROVINCIA_ISCR,  "
			+ "     PG_UTILS.TRUNC_DESC(es.STRMOTIVAZIONE,200,'...') as STRMOTIVAZIONE, "
			+ "	  (select doc.CODSTATOATTO " + "        from am_documento doc "
			+ "	     inner join am_documento_coll docColl on docColl.prgdocumento = doc.prgdocumento "
			+ "		 where docColl.STRCHIAVETABELLA = to_char(es.prgrichesonero) "
			+ "				and doc.CODTIPODOCUMENTO = 'RICHESON') as CODSTATOATTO,"
			+ "		 (SELECT sa.STRDESCRIZIONE " + "		  FROM de_stato_atto sa, am_documento doc "
			+ "		  INNER JOIN am_documento_coll doccoll ON doccoll.prgdocumento = doc.prgdocumento "
			+ "		  WHERE docColl.STRCHIAVETABELLA = to_char(es.prgrichesonero) "
			+ "				AND doc.codtipodocumento = 'RICHESON' and sa.CODSTATOATTO = doc.CODSTATOATTO ) AS STATO "
			+ " from CM_RICH_ESONERO es, " + " 	   an_azienda az,  " + " 	   DE_PROVINCIA PR,  "
			+ " 	   de_stato_atto stato " + " where stato.CODSTATOATTO = es.CODSTATORICHIESTA "
			+ " 	    and az.prgazienda = es.prgazienda " + " 	    and PR.CODPROVINCIA = es.CODPROVINCIA ";

	public DynSelectRichEsonero() {
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
		String AMBITO_TERR = StringUtils.getAttributeStrNotNull(req, "PROVINCIA_ISCR");

		String whereCodStatoAtto = " where CODSTATOATTO = '" + CODSTATOATTO + "'";

		StringBuffer query_totale = new StringBuffer(SELECT_RICHIESTA);
		StringBuffer buf = new StringBuffer();

		if ((AMBITO_TERR != null) && (!AMBITO_TERR.equals(""))) {
			buf.append(" AND ");
			buf.append(" ES.codprovincia = " + AMBITO_TERR);
		}

		// if(fromRicerca.equals("") || fromRicerca==null){
		if ((prgAzienda != null) && (!prgAzienda.equals(""))) {
			buf.append(" AND ");
			buf.append(" ES.prgAzienda = " + prgAzienda);
		}
		// }
		// else{

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
			buf.append(" ES.DATINIZIOVALIDITA ");
		}

		if (((DATAINIZIOVALIDITA_AL != null) && (!DATAINIZIOVALIDITA_AL.equals("")))
				&& ((DATAINIZIOVALIDITA_DAL == null) || (DATAINIZIOVALIDITA_DAL.equals("")))) {
			buf.append(" AND");
			buf.append(" TO_DATE('" + DATAINIZIOVALIDITA_AL + "','DD/MM/YYYY') ");
			buf.append(" >= ");
			buf.append(" ES.DATINIZIOVALIDITA ");
		}

		if (((DATAINIZIOVALIDITA_DAL != null) && (!DATAINIZIOVALIDITA_DAL.equals("")))
				&& ((DATAINIZIOVALIDITA_AL != null) && (!DATAINIZIOVALIDITA_AL.equals("")))) {
			buf.append(" AND");
			buf.append(" TO_DATE('" + DATAINIZIOVALIDITA_AL + "','DD/MM/YYYY') ");
			buf.append(" >= ");
			buf.append(" ES.DATINIZIOVALIDITA ");
			buf.append(" AND");
			buf.append(" TO_DATE('" + DATAINIZIOVALIDITA_DAL + "','DD/MM/YYYY') ");
			buf.append(" <= ");
			buf.append(" ES.DATINIZIOVALIDITA ");
		}

		if ((CODSTATORICHIESTA != null) && (!CODSTATORICHIESTA.equals(""))) {
			buf.append(" AND");
			buf.append(" upper(ES.CODSTATORICHIESTA) = '" + CODSTATORICHIESTA.toUpperCase() + "' ");
		}
		buf.append(" ORDER BY upper(az.STRRAGIONESOCIALE) ASC, es.DATRICHIESTA DESC ");
		buf.append(" ) ");

		if (!CODSTATOATTO.equals("")) {
			buf.append(whereCodStatoAtto);
		}

		// }

		query_totale.append(buf.toString());
		return query_totale.toString();

	}

}
