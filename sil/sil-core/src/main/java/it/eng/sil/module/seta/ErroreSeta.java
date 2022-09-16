package it.eng.sil.module.seta;

import java.util.Vector;

import com.engiweb.framework.error.EMFErrorSeverity;
import com.engiweb.framework.error.EMFUserError;

public class ErroreSeta {
	public static final int OK = 0;
	public final int errCod;
	public final Vector<String> params;
	public final String erroreEsteso;

	public ErroreSeta(int errCod, Vector<String> params) {
		this.errCod = errCod;
		this.params = params;
		EMFUserError error = new EMFUserError(EMFErrorSeverity.ERROR, errCod, params);
		this.erroreEsteso = error.getDescription();
	}

	public ErroreSeta(int errCod) {
		this.errCod = errCod;
		this.params = null;
		if (errCod != OK) {
			EMFUserError error = new EMFUserError(EMFErrorSeverity.ERROR, errCod);
			this.erroreEsteso = error.getDescription();
		} else {
			this.erroreEsteso = null;
		}
	}

	public ErroreSeta(int errCod, String erroreEsteso) {
		this.errCod = errCod;
		this.params = null;
		this.erroreEsteso = erroreEsteso;
	}
}
