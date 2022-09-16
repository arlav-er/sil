/*
 * Creato il 16-gen-07
 *
 * Per modificare il modello associato a questo file generato, aprire
 * Finestra&gt;Preferenze&gt;Java&gt;Generazione codice&gt;Codice e commenti
 */
package it.eng.sil.module.amministrazione;

import java.util.StringTokenizer;
import java.util.Vector;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.error.EMFInternalError;

import it.eng.afExt.dbaccess.sql.IDynamicStatementProvider2;
import it.eng.afExt.utils.StringUtils;
import it.eng.sil.util.Utils;

public class DynSelectRichComputo implements IDynamicStatementProvider2 {
	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(DynSelectRichComputo.class.getName());
	private String className = this.getClass().getName();

	private String SELECT_COMPUTO = "    select distinct " + " 			  az.prgazienda, "
			+ "			  am_documento.CODSTATOATTO as CODSTATOATTO, "
			+ "             az.STRCODICEFISCALE || ' ' || az.STRPARTITAIVA || '<BR/>' || "
			+ "             az.STRRAGIONESOCIALE as AZIENDA,"
			+ " 			  lav.STRNOME || ' ' ||  lav.STRCOGNOME || '<BR/>' ||	lav.STRCODICEFISCALE  as Lavoratore, "
			+ " 			  ct.CODMOTCOMPUTO, ct.datinizio, "
			+ " 			  to_char(ct.DATINIZIO,'dd/mm/yyyy') as DATINIZIOCOMP, "
			+ " 			  to_char(ct.DATASSUNZIONE,'dd/mm/yyyy') as DATASSUNZIONE, "
			// + " decode(CT.CODMONOCATEGORIA,NULL,'','D','Disabile','A','Categoria protetta ex art.18') as
			// CODMONOCATEGORIA, "
			+ " 			  ct.PRGRICHCOMPUTO, " + "             CT.cdnLavoratore, "
			+ " 			  PR.STRDENOMINAZIONE as PROVINCIA_ISCR, "
			+ " 	          de_stato_atto.STRDESCRIZIONE as STATO, " + "			  CT.prgUnita	"
			+ " 	   from                         " + " 			  CM_RICH_COMPUTO CT, "
			+ "             de_provincia pr, " + " 			  an_azienda az, " + "             de_computo, "
			+ " 			  AN_LAVORATORE lav, " + "             am_documento_coll, " + "             de_stato_atto, "
			+ "             am_documento " + " 	  where   az.prgazienda = CT.prgazienda "
			+ " 		  	  and CT.CODPROVINCIA = PR.CODPROVINCIA "
			+ " 		  	  and lav.cdnLavoratore = CT.cdnLavoratore "
			+ "             and am_documento_coll.STRCHIAVETABELLA = to_char(CT.PRGRICHCOMPUTO) "
			+ "             and am_documento.PRGDOCUMENTO = am_documento_coll.PRGDOCUMENTO "
			+ "             and CT.CODMOTCOMPUTO = de_computo.CODMOTCOMPUTO "
			+ "             and am_documento.CODSTATOATTO = de_stato_atto.CODSTATOATTO "
			+ "             and am_documento.CODTIPODOCUMENTO = 'CMDCOMP'";

	private String SELECT_COMPUTO_2 = " select distinct az.prgazienda, "
			+ "                 (select am_documento.CODSTATOATTO as CODSTATOATTO "
			+ "                    from am_documento_coll, " + "                         de_stato_atto, "
			+ "                         de_computo, " + "                         am_documento "
			+ "                   where am_documento_coll.STRCHIAVETABELLA = "
			+ "                         to_char(CT.PRGRICHCOMPUTO) "
			+ "                     and am_documento.PRGDOCUMENTO = "
			+ "                         am_documento_coll.PRGDOCUMENTO "
			+ "                     and CT.CODMOTCOMPUTO = de_computo.CODMOTCOMPUTO "
			+ "                     and am_documento.CODSTATOATTO = "
			+ "                         de_stato_atto.CODSTATOATTO "
			+ "                     and am_documento.CODTIPODOCUMENTO = 'CMDCOMP') as CODSTATOATTO, "
			+ "                 az.STRCODICEFISCALE || ' ' || az.STRPARTITAIVA || '<BR/>' || "
			+ "                 az.STRRAGIONESOCIALE as AZIENDA, "
			+ "                 lav.STRNOME || ' ' || lav.STRCOGNOME || '<BR/>' || "
			+ "                 lav.STRCODICEFISCALE as Lavoratore, " + "                 ct.CODMOTCOMPUTO, "
			+ "                 ct.datinizio, "
			+ "                 to_char(ct.DATINIZIO, 'dd/mm/yyyy') as DATINIZIOCOMP, "
			+ "                 to_char(ct.DATASSUNZIONE, 'dd/mm/yyyy') as DATASSUNZIONE, "
			+ "                 ct.PRGRICHCOMPUTO, " + "                 CT.cdnLavoratore, "
			+ "                 CT.prgUnita, "
			+ "                 (select nvl(de_stato_atto.STRDESCRIZIONE,'Non presente')  "
			+ "                    from am_documento_coll, " + "                         de_stato_atto, "
			+ "                         de_computo, " + "                         am_documento "
			+ "                   where am_documento_coll.STRCHIAVETABELLA = "
			+ "                         to_char(CT.PRGRICHCOMPUTO) "
			+ "                     and am_documento.PRGDOCUMENTO = "
			+ "                         am_documento_coll.PRGDOCUMENTO "
			+ "                     and CT.CODMOTCOMPUTO = de_computo.CODMOTCOMPUTO "
			+ "                     and am_documento.CODSTATOATTO = "
			+ "                         de_stato_atto.CODSTATOATTO "
			+ "                     and am_documento.CODTIPODOCUMENTO = 'CMDCOMP') as STATO, "
			+ "   ( select nvl((select de_stato_atto.STRDESCRIZIONE "
			+ "        from am_documento_coll, de_stato_atto, am_documento  "
			+ "       where am_documento_coll.STRCHIAVETABELLA =  " + "                to_char(CT.PRGRICHCOMPUTO)  "
			+ "         and am_documento.PRGDOCUMENTO =  " + "             am_documento_coll.PRGDOCUMENTO "
			+ "         and am_documento.CODSTATOATTO =  " + "             de_stato_atto.CODSTATOATTO "
			+ "         and am_documento.CODTIPODOCUMENTO = 'CMPCOMP'),'NON PRESENTE') from dual)as STATOPROV "
			+ "   from CM_RICH_COMPUTO CT, an_azienda az, AN_LAVORATORE lav " + "  where az.prgazienda = CT.prgazienda "
			+ "    and lav.cdnLavoratore = CT.cdnLavoratore ";

	public DynSelectRichComputo() {
	}

	public String getStatement(SourceBean req, SourceBean response) {

		String codFiscaleAzienda = StringUtils.getAttributeStrNotNull(req, "codFiscaleAzienda");
		String codiceFiscaleLavoratore = StringUtils.getAttributeStrNotNull(req, "codiceFiscaleLavoratore");
		String DATASSUNZIONE_DAL = StringUtils.getAttributeStrNotNull(req, "DATASSUNZIONE_DAL");
		String DATASSUNZIONE_AL = StringUtils.getAttributeStrNotNull(req, "DATASSUNZIONE_AL");
		String CODSTATOATTO = StringUtils.getAttributeStrNotNull(req, "CODSTATODOCUMENTO");
		String ambitoTerritoriale = StringUtils.getAttributeStrNotNull(req, "PROVINCIA_ISCR");
		String fromRicerca = StringUtils.getAttributeStrNotNull(req, "fromRicerca");
		String prgAzienda = StringUtils.getAttributeStrNotNull(req, "prgAzienda");
		String prgUnita = StringUtils.getAttributeStrNotNull(req, "prgUnita");
		String cdnLavoratore = StringUtils.getAttributeStrNotNull(req, "cdnLavoratore");

		// Configurazione
		SourceBean conf_b = null;
		try {
			conf_b = Utils.getConfigValue("CMCOMP");

		} catch (EMFInternalError e1) {
			it.eng.sil.util.TraceWrapper.fatal(_logger, className + "::getConfigSourceBean()", e1);

		}

		String str_conf_CMCOMP = (String) conf_b.getAttribute("row.num");

		StringBuffer query_totale = null;

		if ("1".equals(str_conf_CMCOMP)) {
			query_totale = new StringBuffer(SELECT_COMPUTO_2);
		} else {
			query_totale = new StringBuffer(SELECT_COMPUTO);
		}

		StringBuffer buf = new StringBuffer();

		if ((prgAzienda != null) && (!prgAzienda.equals("")) && (prgUnita != null) && (!prgUnita.equals(""))) {
			buf.append(" AND CT.prgAzienda = " + prgAzienda + " AND CT.prgUnita = " + prgUnita);
		}

		if ((cdnLavoratore != null) && (!cdnLavoratore.equals(""))) {
			buf.append(" AND ");
			buf.append(" CT.cdnLavoratore = " + cdnLavoratore);
		}

		if ((codFiscaleAzienda != null) && (!codFiscaleAzienda.equals(""))) {
			buf.append(" AND");
			codFiscaleAzienda = StringUtils.replace(codFiscaleAzienda, "'", "''");
			buf.append(" upper(az.STRCODICEFISCALE) = '" + codFiscaleAzienda.toUpperCase() + "' ");
		}

		if ((codiceFiscaleLavoratore != null) && (!codiceFiscaleLavoratore.equals(""))) {
			buf.append(" AND");
			codiceFiscaleLavoratore = StringUtils.replace(codiceFiscaleLavoratore, "'", "''");
			buf.append(" upper(lav.STRCODICEFISCALE) = '" + codiceFiscaleLavoratore.toUpperCase() + "' ");
		}

		if (((DATASSUNZIONE_DAL != null) && (!DATASSUNZIONE_DAL.equals("")))
				&& ((DATASSUNZIONE_AL == null) || (DATASSUNZIONE_AL.equals("")))) {
			buf.append(" AND");
			buf.append(" TO_DATE('" + DATASSUNZIONE_DAL + "','DD/MM/YYYY') ");
			buf.append(" <= ");
			buf.append(" CT.DATASSUNZIONE ");
		}

		if (((DATASSUNZIONE_AL != null) && (!DATASSUNZIONE_AL.equals("")))
				&& ((DATASSUNZIONE_DAL == null) || (DATASSUNZIONE_DAL.equals("")))) {
			buf.append(" AND");
			buf.append(" TO_DATE('" + DATASSUNZIONE_AL + "','DD/MM/YYYY') ");
			buf.append(" >= ");
			buf.append(" CT.DATASSUNZIONE ");
		}

		if (((DATASSUNZIONE_DAL != null) && (!DATASSUNZIONE_DAL.equals("")))
				&& ((DATASSUNZIONE_AL != null) && (!DATASSUNZIONE_AL.equals("")))) {
			buf.append(" AND");
			buf.append(" TO_DATE('" + DATASSUNZIONE_AL + "','DD/MM/YYYY') ");
			buf.append(" >= ");
			buf.append(" CT.DATASSUNZIONE ");
			buf.append(" AND");
			buf.append(" TO_DATE('" + DATASSUNZIONE_DAL + "','DD/MM/YYYY') ");
			buf.append(" <= ");
			buf.append(" CT.DATASSUNZIONE ");
		}

		Vector tipiComp = new Vector();
		String tipiCompStr = "";
		try {
			tipiCompStr = StringUtils.getAttributeStrNotNull(req, "CODMOTCOMPUTO");
			StringTokenizer st = new StringTokenizer(tipiCompStr, ",");
			for (; st.hasMoreTokens();) {
				tipiComp.add(st.nextToken());
			}
		} catch (Exception e) {
			it.eng.sil.util.TraceWrapper.fatal(_logger, className + "::getConfigSourceBean()", e);
			tipiComp = req.getAttributeAsVector("CODMOTCOMPUTO");
		}

		if ((tipiComp != null && tipiComp.size() > 0)) {
			buf.append(" AND ( ");
			StringBuffer sqlTemp = new StringBuffer("ct.CODMOTCOMPUTO IN (");
			for (int i = 0; i < tipiComp.size(); i++) {
				sqlTemp.append('\'');
				String elem = tipiComp.get(i).toString();
				sqlTemp.append(elem);
				sqlTemp.append("',");
			}
			sqlTemp.setCharAt(sqlTemp.length() - 1, ')');
			buf.append(sqlTemp.toString());
			buf.append(" ) ");
		}

		if ((CODSTATOATTO != null) && (!CODSTATOATTO.equals(""))) {
			buf.append(" AND de_stato_atto.CODSTATOATTO ='" + CODSTATOATTO + "' ");
		}

		if ((ambitoTerritoriale != null) && (!ambitoTerritoriale.equals(""))) {
			buf.append(" AND CT.CODPROVINCIA ='" + ambitoTerritoriale + "' ");
		}

		buf.append(" order by ct.DATINIZIO DESC");
		query_totale.append(buf.toString());
		return query_totale.toString();

	}

}
