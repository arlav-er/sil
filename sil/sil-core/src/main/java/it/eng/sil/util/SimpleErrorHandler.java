package it.eng.sil.util;

import java.util.Collection;
import java.util.Iterator;

import com.engiweb.framework.error.EMFErrorHandler;
import com.engiweb.framework.error.EMFUserError;

public class SimpleErrorHandler {
	EMFErrorHandler engErrorHandler = null;
	boolean noError = true;
	boolean isAuthorized = true;

	public SimpleErrorHandler(EMFErrorHandler _engErrorHandler) {
		engErrorHandler = _engErrorHandler;
		noError = (engErrorHandler != null) && (engErrorHandler.isOK());

		if (!noError) {
			Collection errList = engErrorHandler.getErrors();
			Iterator iter = errList.iterator();
			Exception ex = null;
			EMFUserError userError = null;
			int codError = 0;

			while (iter.hasNext()) {
				ex = (Exception) iter.next();

				if (ex instanceof EMFUserError) {
					userError = (EMFUserError) ex;
					codError = userError.getCode();

					if (codError == 20001) {
						// Si e' richiesto la pagina (o il modulo)
						// ma quest'ultimo ha lanciato una eccezione di
						// non autorizzazione (codice 20001)
						isAuthorized = false;
					} else {
						//
					}
				}
			}
		}
	}

	public boolean containsErrors() {
		return !noError;
	}

	public boolean isAuthorized() {
		return isAuthorized;
	}

	public Collection getErrorCollection() {
		if (engErrorHandler != null) {
			return engErrorHandler.getErrors();
		} else {
			return null;
		}
	}
}
