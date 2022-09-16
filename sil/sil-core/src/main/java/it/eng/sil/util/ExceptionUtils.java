package it.eng.sil.util;

import java.sql.SQLException;

import com.engiweb.framework.error.EMFInternalError;

import it.eng.sil.module.QueryStrategyException;

public class ExceptionUtils {

	public static int getSQLErrorCode(Exception err) {
		int errorCode = 0;
		Throwable ex = null;
		if (err instanceof Exception)
			ex = (Exception) err;
		if (ex instanceof QueryStrategyException)
			ex = (Exception) ((QueryStrategyException) err).getException();
		if (ex instanceof EMFInternalError)
			ex = ((EMFInternalError) ex).getNativeException();
		if (ex instanceof SQLException) {
			errorCode = ((SQLException) ex).getErrorCode();
		}
		return errorCode;
	}

	public static void controlloDateRecordPrecedente(Object res, int CodMessaggioErrore) throws Exception {
		if (errorOccurred(res)) {
			int sqlCode = getSQLErrorCode((Exception) res);

			if (sqlCode == CodMessaggioErrore) {
				raiseQueryStrategyException(res);
			} else {
				raiseException(res);
			}
		}
	}

	public static boolean errorOccurred(Object err) {
		if (err instanceof QueryStrategyException)
			return true;
		if (err instanceof Exception)
			return true;
		return false;
	}

	public static void raiseQueryStrategyException(Object err) throws QueryStrategyException {
		if (err instanceof QueryStrategyException)
			throw (QueryStrategyException) err;
		if (err instanceof Exception)
			throw new QueryStrategyException("", (Exception) err);
		else
			throw new QueryStrategyException("impossibile generare un errore coerente", null);
	}

	public static void raiseException(Object err) throws Exception {
		Exception e = null;
		if (err instanceof QueryStrategyException)
			e = (Exception) ((QueryStrategyException) err).getException();
		if (err instanceof Exception)
			e = (Exception) err;
		else
			e = new Exception("impossibile generare un errore coerente");
		throw e;
	}
}