package it.eng.sil.module.cig;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.dbaccess.sql.IDynamicStatementProvider;
import it.eng.afExt.utils.SourceBeanUtils;

public class DynamicListaIscrAperteCorsiCig implements IDynamicStatementProvider {
	private static final String SELECT_SQL_BASE = "select to_char(iscr.datInizio, 'DD/MM/YYYY') as datInizio,"
			+ " to_char(iscr.datFine, 'DD/MM/YYYY') as datFine," + " iscr.PRGALTRAISCR," + " iscr.codtipoiscr"
			+ " from am_altra_iscr iscr, de_tipo_iscr tipo_iscr" + " where iscr.CODSTATO is null"
			+ " and iscr.CODMOTCHIUSURAISCR is null" + " and SYSDATE between iscr.datinizio and iscr.datfine"
			+ " and iscr.codtipoiscr = tipo_iscr.codtipoiscr" + " and tipo_iscr.flgcorso = 'S'";

	public String getStatement(RequestContainer requestContainer, SourceBean config) {
		SourceBean req = requestContainer.getServiceRequest();

		String cdnLavoratore = SourceBeanUtils.getAttrStrNotNull(req, "cdnLavoratore");

		StringBuffer query_totale = new StringBuffer(SELECT_SQL_BASE);
		StringBuffer buf = new StringBuffer();

		if (!cdnLavoratore.equals("")) {
			buf.append(" AND iscr.CDNLAVORATORE ='" + cdnLavoratore + "' ");
		}

		query_totale.append(buf.toString());
		return query_totale.toString();

	}
}
