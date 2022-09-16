package it.eng.sil.module.cig;

import java.sql.Connection;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.dbaccess.DataConnectionManager;

import it.eng.afExt.utils.SourceBeanUtils;
import it.eng.sil.coop.utils.QueryExecutor;
import it.eng.sil.module.AbstractSimpleModule;

public class DynStatementGetUrlAccordoSin extends AbstractSimpleModule {
	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger
			.getLogger(DynStatementGetUrlAccordoSin.class.getName());

	private static final String DES_KEY = "TODO";

	private static final String SELECT_SQL_BASE = " ts.strvalore " + "from ts_config_loc ts, am_altra_iscr isc "
			+ "inner join ci_accordo acc on isc.prgaccordo = acc.prgaccordo " + "where ts.codtipoconfig = 'SARE_CIG' "
			+ "and ts.strcodrif = substr(acc.codAccordo, 4, 2) ";

	public void service(SourceBean serviceRequest, SourceBean serviceResponse) throws Exception {
		Connection conn = DataConnectionManager.getInstance().getConnection().getInternalConnection();
		QueryExecutor qe = new QueryExecutor(conn);

		String token = "ENCRYPT (acc.codAccordo || '|' || to_char(sysdate, 'dd/mm/yyyy hh24:mi:ss'),'" + DES_KEY
				+ "' ) as token, ";
		String prgAltraIscr = SourceBeanUtils.getAttrStrNotNull(serviceRequest, "PRGALTRAISCR");
		String query = "select " + token + SELECT_SQL_BASE;
		query += (" and isc.prgAltraIscr ='" + prgAltraIscr + "' ");

		try {
			SourceBean row = qe.executeQuery(query, null);
			serviceResponse.setAttribute(row);
			serviceResponse.setAttribute(SELECT_OK, "TRUE");
		} catch (Exception e) {
			it.eng.sil.util.TraceWrapper.fatal(_logger, "", e);
		} finally {
			try {
				conn.close();
			} catch (Exception e) {
				it.eng.sil.util.TraceWrapper.debug(_logger, "", e);
			}
		}
	}
}
