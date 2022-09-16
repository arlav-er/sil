package it.eng.sil.module.movimenti;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.dbaccess.sql.IDynamicStatementProvider;

/*
 * Questa classe restituisce la query per il dettaglio delle informazioni
 * sull'apprendistato.
*/

public class GetApprendistato implements IDynamicStatementProvider {
	private String className = this.getClass().getName();
	private static final String SELECT_SQL_BASE = "SELECT STRCOGNOMETUTORE, STRNOMETUTORE, STRCODICEFISCALETUTORE, STRCODFISCPROMOTORETIR,"
			+ "FLGTITOLARETUTORE, CODMANSIONETUTORE, srq.CODQUALIFICASRQ, srq.STRDESCRIZIONE as DESCQUALIFICASRQ,"
			+ "TO_CHAR(NUMANNIESPTUTORE) NUMANNIESPTUTORE, "
			+ "TO_CHAR(DATVISITAMEDICA,'DD/MM/YYYY') DATVISITAMEDICA, PRGAZIENDAINPSCOMPETENTE, "
			+ "PRGUNITAINPSCOMPETENTE, amp.STRNOTE, amp.CDNUTINS, amp.DTMINS, "
			+ "amp.CDNUTMOD, amp.DTMMOD, FLGARTIGIANA, STRLIVELLOTUTORE ,mans.strdescrizione as STRMANSIONETUTORE, "
			+ " PG_UTILS.TROVA_TIPO_MANSIONE( amp.codmansionetutore ) AS strtipomansioneTutore "
			+ " , amp.STRDENOMINAZIONETIR, amp.CODCATEGORIATIR, amp.CODTIPOLOGIATIR, mov.codtipoentepromotore, mov.CODSOGGPROMOTOREMIN "
			+ " FROM AM_MOVIMENTO_APPRENDIST amp, DE_MANSIONE mans, de_qualifica_srq srq, am_movimento mov ";

	private static final String SELECT_SQL_APP = "SELECT amp.STRAPPCOGNOMETUTORE STRCOGNOMETUTORE, amp.STRAPPNOMETUTORE STRNOMETUTORE, amp.STRCODFISCPROMOTORETIR, "
			+ "amp.STRAPPCODICEFISCALETUTORE STRCODICEFISCALETUTORE, amp.CODQUALIFICASRQ, "
			+ "amp.CODAPPMANSIONETUTORE CODMANSIONETUTORE, TO_CHAR(amp.NUMAPPANNIESPTUTORE) NUMANNIESPTUTORE, "
			+ "TO_CHAR(amp.DATVISITAMEDICA,'DD/MM/YYYY') DATVISITAMEDICA, "
			+ "amp.STRAPPLIVELLOTUTORE STRLIVELLOTUTORE, amp.FLGARTIGIANA, amp.FLGTITOLARETUTORE "
			+ " , amp.STRDENOMINAZIONETIR, amp.CODCATEGORIATIR, amp.CODTIPOLOGIATIR, amp.codtipoentepromotore, amp.CODSOGGPROMOTOREMIN ";

	public String getStatement(RequestContainer requestContainer, SourceBean config) {

		String where = "";
		String from = "";
		SourceBean req = requestContainer.getServiceRequest();
		String context = (String) req.getAttribute("CURRENTCONTEXT");
		if (context == null) {
			context = (String) req.getAttribute("CONTEXT");
		}
		String prgMovimento = "";
		StringBuffer query_totale = new StringBuffer("");

		if (context.startsWith("valida")) {
			prgMovimento = (String) req.getAttribute("PRGMOVIMENTOAPP");
			query_totale.append(SELECT_SQL_APP);
			if (context.equalsIgnoreCase("valida")) {
				from = "FROM AM_MOVIMENTO_APPOGGIO amp ";
			}
			if (context.equalsIgnoreCase("validaArchivio")) {
				from = "FROM AM_MOV_APP_ARCHIVIO amp ";
			}
			query_totale.append(from);
			where = "Where amp.PRGMOVIMENTOAPP = " + prgMovimento;
			query_totale.append(where);
		} else {
			// if(context.equalsIgnoreCase("consulta") || context.equalsIgnoreCase("rettifica")){
			prgMovimento = (String) req.getAttribute("PRGMOVIMENTO");
			if (prgMovimento != null) {
				query_totale.append(SELECT_SQL_BASE);
				where = "Where amp.PRGMOVIMENTO = " + prgMovimento;
				where += " AND amp.CODMANSIONETUTORE = mans.CODMANSIONE(+) ";
				where += " AND amp.CODQUALIFICASRQ = srq.CODQUALIFICASRQ (+) ";
				where += " AND amp.prgmovimento =  mov.prgmovimento (+) ";

			}
			query_totale.append(where);
		}
		// }
		return query_totale.toString();
	}
}