package it.eng.sil.module.amministrazione;

import java.util.StringTokenizer;
import java.util.Vector;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.dbaccess.sql.IDynamicStatementProvider;
import it.eng.afExt.utils.StringUtils;

public class DynLavTipoCondizione implements IDynamicStatementProvider {

	private static final String SELECT_SQL_BASE = "select lav.STRCOGNOME, lav.STRNOME, lav.STRCODICEFISCALE, to_char(lav.DATNASC,'dd/mm/yyyy') datNasc,"
			+ "	com.STRDENOMINAZIONE || ' (' || prov.STRTARGA || ')' comNasc, " +
			// " citt.STRDESCRIZIONE cittadinanza," +
			" lav.strindirizzodom, " + " dom.STRDENOMINAZIONE || ' (' || prov2.STRTARGA || ')' comDom, "
			+ "	cond.STRDESCRIZIONE tipoCond, lav.CDNLAVORATORE, cpi.STRDESCRIZIONE cpi"
			+ " from am_obbligo_istruzione ob "
			+ " inner join an_lavoratore lav on (ob.CDNLAVORATORE = lav.CDNLAVORATORE) "
			+ " inner join de_tipo_condizione cond ON (ob.codtipocondizione = cond.codtipocondizione)"
			+ " inner join de_comune com ON (com.codcom = lav.codcomnas)"
			+ " left join am_obbligo_formativo forma ON (forma.cdnlavoratore = ob.cdnlavoratore)"
			+ " left join de_provincia prov on (prov.CODPROVINCIA = com.CODPROVINCIA)"
			+ " inner join de_cittadinanza citt on (citt.CODCITTADINANZA = lav.CODCITTADINANZA)"
			+ " inner join an_lav_storia_inf inf on (lav.cdnLavoratore=inf.cdnLavoratore and inf.DATFINE is null ) "
			+ " inner join de_cpi cpi on (cpi.codCpi=inf.codCpiTit) "
			+ " inner join de_comune dom ON (dom.codcom = lav.codcomdom)  "
			+ " left join de_provincia prov2 on (prov2.CODPROVINCIA = dom.CODPROVINCIA)  "
			+ " where forma.CODMOTIVOARCHIVIAZIONE is null "
			+ " and trunc (months_between(sysdate,lav.DATNASC)/12) > 0 and trunc(months_between(sysdate,lav.DATNASC)/12) < 18 ";

	public String getStatement(RequestContainer requestContainer, SourceBean config) {
		SourceBean req = requestContainer.getServiceRequest();

		String tipoCondAperteCheck = StringUtils.getAttributeStrNotNull(req, "tipoCondAperteCheck");
		String codCpi = StringUtils.getAttributeStrNotNull(req, "codCpi");

		Vector codtipoCondizione = new Vector();
		String tipoCond = "";

		try {
			tipoCond = StringUtils.getAttributeStrNotNull(req, "tipoCondizione");
			StringTokenizer st = new StringTokenizer(tipoCond, ",");

			for (; st.hasMoreTokens();) {
				codtipoCondizione.add(st.nextToken());
			}
		} catch (Exception e) {
			codtipoCondizione = req.getAttributeAsVector("tipoCondizione");
		}

		StringBuffer query_totale = new StringBuffer(SELECT_SQL_BASE);

		StringBuffer buf = new StringBuffer();

		if ((codtipoCondizione != null && codtipoCondizione.size() > 0)) {
			buf.append(" AND ( ");
			StringBuffer sqlTemp = new StringBuffer("cond.CODTIPOCONDIZIONE IN (");
			for (int i = 0; i < codtipoCondizione.size(); i++) {
				sqlTemp.append('\'');
				String elem = codtipoCondizione.get(i).toString();
				sqlTemp.append(elem);
				sqlTemp.append("',");
			}
			sqlTemp.setCharAt(sqlTemp.length() - 1, ')');
			buf.append(sqlTemp.toString());
			buf.append(" ) ");

			/*
			 * in caso di selezione di più tipi condizione devono uscire SOLO le persone che hanno TUTTE le condizioni
			 * selezionate;
			 * 
			 * ERRATA CORRIGE : DEVONO ESSERE ESTRATTI I LAVORATORI CON ALMENO UNO DEI TIPI DI CONDIZIONE SELEZIONATI
			 */

			/*
			 * int k = 1; for (int j = 0; j < codtipoCondizione.size(); j++) { String tabella =
			 * "am_obbligo_istruzione ob" + k; String alias = "ob" + k; StringBuffer sqlTmp = new
			 * StringBuffer(" and exists (select prgobbligoistruzione " + "from " + tabella +
			 * " where codtipocondizione = '" + codtipoCondizione.get(j).toString() + "' " + "and " + alias +
			 * ".cdnlavoratore = ob.cdnlavoratore) "); buf.append(sqlTmp.toString()); k++; }
			 */
		}

		if ((tipoCondAperteCheck != null) && (!tipoCondAperteCheck.equals("")) && (tipoCondAperteCheck.equals("on"))) {
			buf.append(
					" and ( cond.STRDESCRIZIONE is not null and (ob.DATAFINEOBBLIGO is null or trunc(ob.DATAFINEOBBLIGO) > trunc(sysdate) ) )");
		}

		if ((codCpi != null) && (!codCpi.equals(""))) {
			buf.append(" and inf.CODCPITIT = '" + codCpi + "' ");
		}

		buf.append(" order by lav.STRCOGNOME, lav.STRNOME ");
		query_totale.append(buf.toString());
		return query_totale.toString();

	}

}
