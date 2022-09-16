package it.eng.sil.module.documenti;

import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.dbaccess.sql.IDynamicStatementProvider2;

public class DocHasInfoStoricheDyn implements IDynamicStatementProvider2 {

	private final static String QUERY_M_DOC_HASINFOSTORICHE = "SELECT doc.prgdocumento "
			+ "FROM  am_documento doc, am_documento_coll doccol, ts_componente com "
			+ "WHERE doccol.prgdocumento(+) = doc.prgdocumento " + "AND com.cdncomponente(+) = doccol.cdncomponente "
			+ "AND (    DECODE (doc.datfine, NULL, 'S', 'N') = 'N'         AND doc.datfine < TRUNC (SYSDATE)       )";

	@Override
	public String getStatement(SourceBean request, SourceBean response) {
		StringBuffer buf = new StringBuffer(QUERY_M_DOC_HASINFOSTORICHE);
		String cdnLavoratoreObj = (String) request.getAttribute("cdnLavoratore");
		if (cdnLavoratoreObj != null && !"".equals(cdnLavoratoreObj)) {
			buf.append(" AND doc.cdnlavoratore = ");
			buf.append(cdnLavoratoreObj);
		}

		String prgAziendaObj = (String) request.getAttribute("prgAzienda");
		String prgUnitaObj = (String) request.getAttribute("prgUnita");
		if (prgAziendaObj != null && !"".equals(prgAziendaObj) && prgUnitaObj != null && !"".equals(prgUnitaObj)) {
			buf.append(" AND doc.prgazienda = ");
			buf.append(prgAziendaObj);
			buf.append(" AND doc.prgunita = ");
			buf.append(prgUnitaObj);
		}

		String paginaObj = (String) request.getAttribute("pagina");
		if (paginaObj != null && !"".equals(paginaObj)) {
			buf.append(" AND com.strpage = '");
			buf.append(paginaObj);
			buf.append("'");
		}

		return buf.toString();
	}
}
