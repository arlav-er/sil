/*
 * Created on 10-nov-06
 *
 */
package it.eng.sil.module.profil;

import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.dbaccess.DataConnectionManager;
import com.engiweb.framework.dbaccess.Utils;
import com.engiweb.framework.dbaccess.sql.DataConnection;
import com.engiweb.framework.dispatching.module.AbstractModule;
import com.engiweb.framework.error.EMFInternalError;
import com.engiweb.framework.error.EMFUserError;
import com.engiweb.framework.util.QueryExecutorObjectExt;

import it.eng.sil.Values;

/**
 * @author vuoto
 * 
 */

public class ProfMonitoraggio extends AbstractModule {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(ProfMonitoraggio.class.getName());
	private String className = this.getClass().getName();

	public void service(SourceBean request, SourceBean response) {
		_logger.debug(className + "::service()");

		DataConnection connSIL = null;
		DataConnection connDWH = null;
		try {
			connSIL = DataConnectionManager.getInstance().getConnection(Values.DB_SIL_DATI);
			connDWH = DataConnectionManager.getInstance().getConnection(Values.DB_SIL_DWH);

			String cdut = (String) request.getAttribute("cdut");
			SourceBean sb = recuperaLogin(connSIL, cdut);
			String strLogin = (String) sb.getAttribute("row.strlogin");

			response.setAttribute("strlogin", strLogin);
			sb = recuperaGruppi(connDWH, strLogin);

			response.setAttribute(sb);

		} catch (Exception e) {

			it.eng.sil.util.TraceWrapper.fatal(_logger, className + "::service(...)", e);

		} finally {

			Utils.releaseResources(connSIL, null, null);
			Utils.releaseResources(connDWH, null, null);

		}

	}

	private SourceBean recuperaLogin(DataConnection connSIL, String cdut) throws EMFInternalError, EMFUserError {

		QueryExecutorObjectExt qSIL = new QueryExecutorObjectExt();
		qSIL.setDataConnection(connSIL);
		qSIL.setTransactional(false);

		qSIL.setStatement("SELECT t.STRLOGIN FROM TS_UTENTE t where t.CDNUT=?");
		qSIL.setType(QueryExecutorObjectExt.SELECT);

		List inputParameters = new ArrayList();
		inputParameters.add(connSIL.createDataField("id", Types.VARCHAR, cdut));
		qSIL.setInputParameters(inputParameters);

		return (SourceBean) qSIL.execExt();

	}

	private SourceBean recuperaGruppi(DataConnection connDWH, String strlogin) throws EMFInternalError, EMFUserError {
		QueryExecutorObjectExt qDWH = new QueryExecutorObjectExt();
		qDWH.setDataConnection(connDWH);
		qDWH.setTransactional(false);

		String query = "SELECT g.ID CODICE, g.description DESCRIZIONE, decode(m.USERNAME,? ,'CHECKED', '') CHECKED"
				+ "  FROM EXO_GROUP g,  EXO_MEMBERSHIP m" + "  WHERE " + "   g.FLGSIL='S'"
				+ "   and g.ID = m.GROUPID(+)" + "   and m.username (+)=? ";

		qDWH.setStatement(query);
		qDWH.setType(QueryExecutorObjectExt.SELECT);

		List inputParameters = new ArrayList();
		inputParameters.add(connDWH.createDataField("id", Types.VARCHAR, strlogin));
		inputParameters.add(connDWH.createDataField("id", Types.VARCHAR, strlogin));

		qDWH.setInputParameters(inputParameters);
		return (SourceBean) qDWH.execExt();

	}

}