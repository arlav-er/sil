package it.eng.sil.util.amministrazione.impatti;

import com.engiweb.framework.error.EMFErrorSeverity;
import com.engiweb.framework.error.EMFUserError;

public class ControlliException extends EMFUserError {
	private int code = 0;

	/**
	 * int errorCode il codice mappato nel file imessages_it_IT.properties
	 */
	public ControlliException(int errorCode) {
		super(EMFErrorSeverity.ERROR, errorCode);
		code = errorCode;
	}

	public int getCode() {
		return code;
	}
}
