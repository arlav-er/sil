/*
 * Creato il 25-ott-06
 *
 * Per modificare il modello associato a questo file generato, aprire
 * Finestra&gt;Preferenze&gt;Java&gt;Generazione codice&gt;Codice e commenti
 */
package it.eng.sil.module.anag;

import java.util.StringTokenizer;
import java.util.Vector;

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

public class DynSelectRichSosp implements IDynamicStatementProvider2 {

	private String SELECT_RICHIESTA = " select " + " 	   stato.strdescrizione as statoRichiesta, "
			+ "			(select doc.CODSTATOATTO " + "			   from am_documento doc "
			+ "			        inner join am_documento_coll docColl on docColl.prgdocumento = doc.prgdocumento "
			+ "			  where docColl.STRCHIAVETABELLA = to_char(sosp.PRGRICHSOSPENSIONE) "
			+ "			    and doc.CODTIPODOCUMENTO = 'RICHSOSP' " + "			) as CODSTATOATTO, "
			+ "			(SELECT sa.STRDESCRIZIONE "
			+ "	   		   FROM de_stato_atto sa, am_documento doc INNER JOIN am_documento_coll doccoll ON doccoll.prgdocumento = doc.prgdocumento "
			+ "	  		  WHERE doccoll.strchiavetabella = TO_CHAR (sosp.prgrichsospensione) "
			+ "				AND doc.codtipodocumento = 'RICHSOSP' "
			+ "				and sa.CODSTATOATTO = doc.CODSTATOATTO " + "			) AS statoatto, "
			+ " 	   mot.strdescrizione as motivoSosp, " + "        az.prgazienda, " + " 	   az.STRRAGIONESOCIALE, "
			+ " 	   az.STRCODICEFISCALE, " + " 	   az.STRPARTITAIVA, "
			+ " 	   to_char(sosp.DATRICHIESTA,'dd/mm/yyyy') as DATRICHIESTA, "
			+ " 	   to_char(sosp.DATINIZIOSOSP,'dd/mm/yyyy') as DATINIZIOSOSP, "
			+ "    	   to_char(sosp.DATFINESOSP,'dd/mm/yyyy') as DATFINESOSP, " + "  	   sosp.CODSTATORICHIESTA, "
			+ " 	   sosp.CODMOTIVOSOSP, " + " 	   sosp.FLGPROVVEDIMENTO, " + " 	   sosp.FLGPROROGA, "
			+ " 	   to_char(sosp.DATFINETEMPORANEA,'dd/mm/yyyy') as DATFINETEMPORANEA, " + " 	   sosp.CDNUTINS, "
			+ " 	   sosp.CDNUTMOD, " + " 	   sosp.NUMKLOSOSPENSIONE, "
			+ "        to_char(sosp.DTMMOD,'dd/mm/yyyy') as DTMMOD, "
			+ "        to_char(sosp.DTMINS,'dd/mm/yyyy') as DTMINS, " + "        sosp.PRGRICHSOSPENSIONE "
			+ " from   CM_RICH_SOSPENSIONE sosp, " + "        an_azienda az, " + "        de_stato_atto stato, "
			+ "        de_cm_motivo_sosp mot " + " where  stato.CODSTATOATTO = sosp.CODSTATORICHIESTA "
			+ "   and  mot.CODMOTIVOSOSP = sosp.CODMOTIVOSOSP " + "   and  az.prgazienda = sosp.prgazienda ";

	private final static String selectCodStatoAtto = " select statoRichiesta, "
			+ "		 CODSTATOATTO, statoatto, motivoSosp, "
			+ "		 prgazienda, STRRAGIONESOCIALE, STRCODICEFISCALE, STRPARTITAIVA, DATRICHIESTA, "
			+ "		 DATINIZIOSOSP, DATFINESOSP, CODSTATORICHIESTA, CODMOTIVOSOSP, FLGPROVVEDIMENTO, "
			+ "		 FLGPROROGA, DATFINETEMPORANEA, CDNUTINS, CDNUTMOD, NUMKLOSOSPENSIONE, "
			+ "		 DTMMOD, DTMINS, PRGRICHSOSPENSIONE " + "   from ( ";

	public DynSelectRichSosp() {
	}

	public String getStatement(SourceBean req, SourceBean response) {

		String STRRAGIONESOCIALE = StringUtils.getAttributeStrNotNull(req, "ragioneSoc");
		String STRCODICEFISCALE = StringUtils.getAttributeStrNotNull(req, "CodFisc");
		String STRPARTITAIVA = StringUtils.getAttributeStrNotNull(req, "PIVA");
		String DATINIZIOSOSP_DAL = StringUtils.getAttributeStrNotNull(req, "DATINIZIOSOSP_DAL");
		String DATINIZIOSOSP_AL = StringUtils.getAttributeStrNotNull(req, "DATINIZIOSOSP_AL");
		String CODSTATORICHIESTA = StringUtils.getAttributeStrNotNull(req, "CODSTATORICHIESTA");
		String CODSTATOATTORICH = StringUtils.getAttributeStrNotNull(req, "CODSTATOATTORICH");

		String fromRicerca = StringUtils.getAttributeStrNotNull(req, "fromRicerca");
		String prgAzienda = StringUtils.getAttributeStrNotNull(req, "prgAzienda");

		String whereCodStatoAtto = " ) where CODSTATOATTO = '" + CODSTATOATTORICH + "'";

		StringBuffer buf = new StringBuffer();
		StringBuffer query_totale = null;
		if (CODSTATOATTORICH.equals("")) {
			query_totale = new StringBuffer(SELECT_RICHIESTA);
		} else {
			query_totale = new StringBuffer(selectCodStatoAtto + SELECT_RICHIESTA);
		}

		if ((STRRAGIONESOCIALE != null) && (!STRRAGIONESOCIALE.equals(""))) {
			buf.append(" AND");
			STRRAGIONESOCIALE = StringUtils.replace(STRRAGIONESOCIALE, "'", "''");
			buf.append(" upper(az.STRRAGIONESOCIALE) like '%" + STRRAGIONESOCIALE.toUpperCase() + "%' ");
		}

		if (prgAzienda != null && !prgAzienda.equals("")) {
			buf.append(" AND sosp.prgazienda = ");
			buf.append(prgAzienda);
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

		Vector codMotivi = new Vector();
		String motiviStr = "";
		try {
			motiviStr = StringUtils.getAttributeStrNotNull(req, "CODMOTIVOSOSP");
			StringTokenizer st = new StringTokenizer(motiviStr, ",");
			for (; st.hasMoreTokens();) {
				codMotivi.add(st.nextToken());
			}
		} catch (Exception e) {
			codMotivi = req.getAttributeAsVector("CODMOTIVOSOSP");
		}

		if (((DATINIZIOSOSP_DAL != null) && (!DATINIZIOSOSP_DAL.equals("")))
				&& ((DATINIZIOSOSP_AL == null) || (DATINIZIOSOSP_AL.equals("")))) {
			buf.append(" AND");
			buf.append(" TO_DATE('" + DATINIZIOSOSP_DAL + "','DD/MM/YYYY') ");
			buf.append(" <= ");
			buf.append(" sosp.DATINIZIOSOSP ");
		}

		if (((DATINIZIOSOSP_AL != null) && (!DATINIZIOSOSP_AL.equals("")))
				&& ((DATINIZIOSOSP_DAL == null) || (DATINIZIOSOSP_DAL.equals("")))) {
			buf.append(" AND");
			buf.append(" TO_DATE('" + DATINIZIOSOSP_AL + "','DD/MM/YYYY') ");
			buf.append(" >= ");
			buf.append(" sosp.DATINIZIOSOSP ");
		}

		if (((DATINIZIOSOSP_DAL != null) && (!DATINIZIOSOSP_DAL.equals("")))
				&& ((DATINIZIOSOSP_AL != null) && (!DATINIZIOSOSP_AL.equals("")))) {
			buf.append(" AND");
			buf.append(" TO_DATE('" + DATINIZIOSOSP_AL + "','DD/MM/YYYY') ");
			buf.append(" >= ");
			buf.append(" sosp.DATINIZIOSOSP ");
			buf.append(" AND");
			buf.append(" TO_DATE('" + DATINIZIOSOSP_DAL + "','DD/MM/YYYY') ");
			buf.append(" <= ");
			buf.append(" sosp.DATINIZIOSOSP ");
		}

		if ((CODSTATORICHIESTA != null) && (!CODSTATORICHIESTA.equals(""))) {
			buf.append(" AND");
			buf.append(" upper(sosp.CODSTATORICHIESTA) = '" + CODSTATORICHIESTA.toUpperCase() + "' ");
		}

		if ((codMotivi != null && codMotivi.size() > 0)) {
			buf.append(" AND ( ");
			StringBuffer sqlTemp = new StringBuffer("sosp.CODMOTIVOSOSP IN (");
			for (int i = 0; i < codMotivi.size(); i++) {
				sqlTemp.append('\'');
				String elem = codMotivi.get(i).toString();
				sqlTemp.append(elem);
				sqlTemp.append("',");
			}
			sqlTemp.setCharAt(sqlTemp.length() - 1, ')');
			buf.append(sqlTemp.toString());
			buf.append(" ) ");
		}

		if (!CODSTATOATTORICH.equals("")) {
			buf.append(whereCodStatoAtto);
		}

		query_totale.append(buf.toString());
		return query_totale.toString();

	}

}
