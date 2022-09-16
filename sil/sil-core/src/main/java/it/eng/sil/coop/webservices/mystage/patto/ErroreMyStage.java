package it.eng.sil.coop.webservices.mystage.patto;

import java.util.Vector;

import com.engiweb.framework.error.EMFErrorSeverity;
import com.engiweb.framework.error.EMFUserError;

public class ErroreMyStage {
	public static final int OK = 0;
	public final int errCod;
	public final Vector<String> params;
	public final String erroreEsteso;

	public ErroreMyStage(int errCod, Vector<String> params) {
		this.errCod = errCod;
		this.params = params;
		EMFUserError error = new EMFUserError(EMFErrorSeverity.ERROR, errCod, params);
		this.erroreEsteso = error.getDescription();
	}

	public ErroreMyStage(int errCod) {
		this.errCod = errCod;
		this.params = null;
		if (errCod != OK) {
			EMFUserError error = new EMFUserError(EMFErrorSeverity.ERROR, errCod);
			this.erroreEsteso = error.getDescription();
		} else {
			this.erroreEsteso = null;
		}
	}

	public ErroreMyStage(int errCod, String erroreEsteso) {
		this.errCod = errCod;
		this.params = null;
		this.erroreEsteso = erroreEsteso;
	}
}
