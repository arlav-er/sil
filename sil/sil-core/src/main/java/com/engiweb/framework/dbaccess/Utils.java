package com.engiweb.framework.dbaccess;

import com.engiweb.framework.dbaccess.sql.DataConnection;
import com.engiweb.framework.dbaccess.sql.SQLCommand;
import com.engiweb.framework.dbaccess.sql.result.DataResult;
import com.engiweb.framework.dbaccess.sql.result.DataResultInterface;
import com.engiweb.framework.dbaccess.sql.result.ScrollableDataResult;
import com.engiweb.framework.error.EMFErrorSeverity;
import com.engiweb.framework.error.EMFInternalError;

public class Utils {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(Utils.class.getName());

	private Utils() {
		super();
	} // private Utils()

	public static EMFInternalError generateInternalError(Exception ex, String method) {
		return new EMFInternalError(EMFErrorSeverity.ERROR, method, ex);
	} // public static EMFInternalError generateInternalError(Exception ex,
		// String method)

	public static void releaseResources(DataConnection connection, SQLCommand command, DataResult result) {
		if (result != null) {
			DataResultInterface resultInterface = result.getDataObject();
			if ((resultInterface != null) && (resultInterface instanceof ScrollableDataResult)) {
				try {
					((ScrollableDataResult) resultInterface).close();
				} // try
				catch (EMFInternalError ie) {
					it.eng.sil.util.TraceWrapper.error(_logger,
							"Utils::releaseResources: ((ScrollableDataResult)resultInterface).close()", (Exception) ie);

				} // catch (EMFInternalError ie)
			} // if ((resultInterface != null) && (resultInterface
				// instanceof ScrollableDataResult))
		} // if (result != null)
		if (command != null) {
			try {
				command.close();
			} // try
			catch (EMFInternalError ie) {
				it.eng.sil.util.TraceWrapper.error(_logger, "Utils::releaseResources: command.close()", (Exception) ie);

			} // catch (EMFInternalError ie)
		} // if (command != null)
		if (connection != null) {
			try {
				connection.close();
			} // try
			catch (EMFInternalError ie) {
				it.eng.sil.util.TraceWrapper.error(_logger, "Utils::releaseResources: connection.close()",
						(Exception) ie);

			} // catch (EMFInternalError ie)
		} // if (connection != null)
	} // public static void releaseResources(DataConnection connection,
		// SQLCommand command, DataResult result)
	
	
	
	
	public static void printSilStackedElements(String marker) {
		StackTraceElement[] stackElems = Thread.currentThread().getStackTrace();
		for (StackTraceElement elem : stackElems) {
			if (elem.getClassName().startsWith("it.eng.sil") || 
					elem.getClassName().startsWith("com.engiweb")) {
				_logger.info("---" + marker +  "---" + elem.getClassName() + "::" + elem.getMethodName() + ":" + elem.getLineNumber());
			}
		}
	}

} // public class Utils
