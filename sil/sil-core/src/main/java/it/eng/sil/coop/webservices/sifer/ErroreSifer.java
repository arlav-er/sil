package it.eng.sil.coop.webservices.sifer;

import java.util.Vector;

import com.engiweb.framework.error.EMFErrorSeverity;
import com.engiweb.framework.error.EMFUserError;

public class ErroreSifer {
	public static final int OK = 0;
	public final int errCod;
	public final int errCodDB;
	public final Vector<String> params;
	public final String erroreEsteso;
	public String strXMLInviato = "";

	public ErroreSifer(int errCod, Vector<String> params) {
		this.errCod = errCod;
		this.params = params;
		this.errCodDB = 0;
		EMFUserError error = new EMFUserError(EMFErrorSeverity.ERROR, errCod, params);
		this.erroreEsteso = error.getDescription();
	}

	public ErroreSifer(int errCod) {
		this.errCod = errCod;
		this.params = null;
		this.errCodDB = 0;
		if (errCod != OK) {
			EMFUserError error = new EMFUserError(EMFErrorSeverity.ERROR, errCod);
			this.erroreEsteso = error.getDescription();
		} else {
			this.erroreEsteso = null;
		}
	}

	public ErroreSifer(int errCod, String erroreEsteso) {
		this.errCod = errCod;
		this.errCodDB = 0;
		this.params = null;
		this.erroreEsteso = erroreEsteso;
	}

	public ErroreSifer(int errCod, String erroreEsteso, int errCodDB) {
		this.errCod = errCod;
		this.errCodDB = errCodDB;
		this.params = null;
		this.erroreEsteso = erroreEsteso;
	}

	public ErroreSifer(int errCod, int errCodDB) {
		this.errCod = errCod;
		this.errCodDB = errCodDB;
		this.params = null;
		if (errCod != OK) {
			EMFUserError error = new EMFUserError(EMFErrorSeverity.ERROR, errCod);
			this.erroreEsteso = error.getDescription();
		} else {
			this.erroreEsteso = null;
		}
	}

	public ErroreSifer(int errCod, Vector<String> params, int errCodDB) {
		this.errCod = errCod;
		this.errCodDB = errCodDB;
		this.params = params;
		EMFUserError error = new EMFUserError(EMFErrorSeverity.ERROR, errCod, params);
		this.erroreEsteso = error.getDescription();
	}

	public String getStrXMLInviato() {
		return strXMLInviato;
	}

	public void setStrXMLInviato(String strXMLInviato) {
		this.strXMLInviato = strXMLInviato;
	}
}
