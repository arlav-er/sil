package it.eng.sil.module.anag;

import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.utils.CF_utils;
import it.eng.afExt.utils.CfException;
import it.eng.afExt.utils.MessageCodes;
import it.eng.afExt.utils.ReportOperationResult;
import it.eng.sil.module.AbstractSimpleModule;

public class InsertLavoratore extends AbstractSimpleModule {
	public void service(SourceBean request, SourceBean response) {
		String strCodiceFiscale = (String) request.getAttribute("strCodiceFiscale");
		String strNome = (String) request.getAttribute("strNome");
		String strCognome = (String) request.getAttribute("strCognome");
		String strSesso = (String) request.getAttribute("strSesso");
		String datNasc = (String) request.getAttribute("datNasc");
		String codComNas = (String) request.getAttribute("codComNas");

		ReportOperationResult reportOperation = new ReportOperationResult(this, response);

		try {
			if (request.getAttribute("FLGCFOK").toString().compareTo("S") != 0) {
				CF_utils.verificaCF(strCodiceFiscale, strNome, strCognome, strSesso, datNasc, codComNas);
			}
		} catch (CfException cfEx) {
			reportOperation.reportFailure(cfEx.getMessageIdFail());
		}

		try {

			this.setSectionQuerySelect("QUERY_SELECT");
			SourceBean lav = doSelect(request, response);
			if (lav != null && lav.containsAttribute("ROW.CDNLAVORATORE")) {
				throw new Exception("Codice Fiscale già presente in archivio.");
			} else {
				this.setSectionQueryInsert("QUERY_INSERT");
				doInsert(request, response);
			}
		} catch (Exception ex) {
			// reportOperation.reportFailure(ex,"InsertLavoratore","Codice
			// Fiscale già presente in archivio.");
			reportOperation.reportFailure(MessageCodes.General.ELEMENT_DUPLICATED, ex, "services()", "insert");
		}

	}

}