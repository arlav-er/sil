/**
 *
 * @author Franco Vuoto
 * @version 1.0
 * 
 * Created on 16-ott-06
 *
 */

package it.eng.sil.module.login;

import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.dbaccess.DataConnectionManager;
import com.engiweb.framework.dbaccess.Utils;
import com.engiweb.framework.dbaccess.sql.DataConnection;
import com.engiweb.framework.error.EMFInternalError;
import com.engiweb.framework.error.EMFUserError;
import com.engiweb.framework.util.QueryExecutorObjectExt;

import it.eng.sil.Values;
import it.eng.sil.module.AbstractSimpleModule;
import it.eng.sil.security.DES;

public class Monitoraggio extends AbstractSimpleModule {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(Monitoraggio.class.getName());

	private String className = this.getClass().getName();
	SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");

	public Monitoraggio() {
	}

	public void service(SourceBean request, SourceBean response) {
		_logger.debug(className + "::service()");

		DataConnection connSIL = null;
		DataConnection connDWH = null;

		try {
			connSIL = DataConnectionManager.getInstance().getConnection(Values.DB_SIL_DATI);
			connDWH = DataConnectionManager.getInstance().getConnection(Values.DB_SIL_DWH);

			this.setSectionQuerySelect("QUERIES.SELECT_QUERY");
			SourceBean rowsSourceBean = doSelect(request, response, false);

			String shaPassword = (String) rowsSourceBean.getAttribute("ROW.sha1Password");
			String login = (String) rowsSourceBean.getAttribute("ROW.strlogin");

			rowsSourceBean = recuperaGruppi(login);

			if (rowsSourceBean.getAttributeAsVector("ROW").size() > 0) {

				// TUTTO OK
				// L'utente ha almeno un profilo associato
				// Si passa alla costruzione del token
				String momentoEstrazione = format.format(new Date());

				String TOKEN = DES.getInstance().encrypt(shaPassword + "::" + momentoEstrazione);
				response.setAttribute("TOKEN", TOKEN);
			}

		} catch (Exception ex) {
			it.eng.sil.util.TraceWrapper.debug(_logger, className + "::service()", ex);

		} finally {

			Utils.releaseResources(connSIL, null, null);
			Utils.releaseResources(connDWH, null, null);

		}

	}

	private SourceBean recuperaGruppi(String strlogin) throws EMFInternalError, EMFUserError {
		DataConnection connDWH = DataConnectionManager.getInstance().getConnection(Values.DB_SIL_DWH);
		QueryExecutorObjectExt qDWH = new QueryExecutorObjectExt();
		qDWH.setDataConnection(connDWH);
		qDWH.setTransactional(false);

		String query = "SELECT g.ID CODICE " + "  FROM EXO_GROUP g,  EXO_MEMBERSHIP m" + "  WHERE " + "   g.FLGSIL='S'"
				+ "   and g.ID = m.GROUPID" + "   and m.username =? ";

		qDWH.setStatement(query);
		qDWH.setType(QueryExecutorObjectExt.SELECT);

		List inputParameters = new ArrayList();
		inputParameters.add(connDWH.createDataField("id", Types.VARCHAR, strlogin));

		qDWH.setInputParameters(inputParameters);
		return (SourceBean) qDWH.execExt();

	}

}