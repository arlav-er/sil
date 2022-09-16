package it.eng.sil.coop.webservices.apapi;

import java.util.Vector;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.dbaccess.sql.DataConnection;
import com.engiweb.framework.error.EMFErrorSeverity;
import com.engiweb.framework.error.EMFInternalError;
import com.engiweb.framework.error.EMFUserError;
import com.engiweb.framework.util.QueryExecutor;

import it.eng.afExt.utils.MessageCodes;
import it.eng.sil.Values;
import it.eng.sil.util.DBAccess;

public class DBUtils {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(DBUtils.class.getName());

	public static SourceBean executeSelect(String query_name, Object[] inputParameters, DataConnection dc)
			throws EMFUserError {
		SourceBean ret = null;
		if (dc != null) {
			DBAccess dbAcc = new DBAccess();
			try {
				ret = dbAcc.selectToSourceBean(query_name, inputParameters, dc);
			} catch (EMFInternalError e) {
				_logger.error("Errore nell'esecuzione della query: " + query_name);
				throw new EMFUserError(EMFErrorSeverity.ERROR, MessageCodes.ClicLavoro.CODE_ERR_INTERNO, new Vector());
			}
		} else {
			ret = (SourceBean) QueryExecutor.executeQuery(query_name, inputParameters, "SELECT", Values.DB_SIL_DATI);
		}
		return ret;
	}

}
