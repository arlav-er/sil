package it.eng.sil.module.cig;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.dbaccess.sql.IDynamicStatementProvider;
import it.eng.afExt.utils.SourceBeanUtils;

public class DynStatementGetInfoPerOrienter implements IDynamicStatementProvider {
	private static final String DES_KEY = "ZuI%DE!G";

	private static final String SELECT_SQL_BASE = " lav.strcodicefiscale as cf, " + "lav.strnome as nome, "
			+ "lav.strcognome as cogn, " + "nvl(acc.codaccordo,isc.prgaltraiscr) as codAcc, " + "ts.strvalore, "
			+ " CASE " + "    WHEN ciLav.strCell is not null  " + "    	THEN ciLav.strCell "
			+ "	WHEN ciLav.strAltroTel is not null  " + "		THEN ciLav.strAltroTel "
			+ "	WHEN lav.strCell is not null " + "	 	THEN lav.strCell " + " 	WHEN lav.strTelDom is not null "
			+ "	 	THEN lav.strTelDom " + " 	WHEN lav.strTelRes is not null " + "		THEN lav.strTelRes "
			+ "   WHEN lav.strTelAltro is not null " + "	 	THEN lav.strTelAltro " + " END as tel "
			+ "from ts_ws, ts_config_loc ts, am_altra_iscr isc "
			+ "join an_lavoratore lav on isc.cdnlavoratore = lav.cdnlavoratore "
			+ "left join ci_accordo acc on isc.prgaccordo = acc.prgaccordo "
			+ "left join ci_lavoratore ciLav on (ciLav.prgaccordo = acc.prgaccordo and ciLav.Cdnlavoratore = lav.cdnlavoratore) "
			+ "where ts.codtipoconfig='ORIENTER' " + " and ts_ws.codservizio='ORIENTER2' ";

	public String getStatement(RequestContainer requestContainer, SourceBean config) {
		SourceBean req = requestContainer.getServiceRequest();

		String token = "ENCRYPT (ts_ws.STRUSERID || '|' || decrypt(ts_ws.STRPASSWORD, '" + DES_KEY
				+ "') || '|' || TO_CHAR(sysdate, 'dd/mm/yyyy hh24:mi:ss'),'" + DES_KEY + "') as token, ";

		String prgAltraIscr = SourceBeanUtils.getAttrStrNotNull(req, "PRGALTRAISCR");

		String query = "select " + token + SELECT_SQL_BASE;

		if (!prgAltraIscr.equals("")) {
			query += (" and isc.prgAltraIscr ='" + prgAltraIscr + "' ");
		}

		return query;
	}
}
