package it.eng.sil.module.sap;

import com.engiweb.framework.base.SourceBean;

public class InsertAltro extends InsertSAP {
	private static final long serialVersionUID = 1L;

	public void service(SourceBean request, SourceBean response) {

		doInsertUpdate(request, response);
	}
}