package it.eng.sil.action.report.cig;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.error.EMFUserError;

import it.eng.sil.action.report.AbstractSimpleReport;

public class StampaDomandaCig extends AbstractSimpleReport {

	public void service(SourceBean request, SourceBean response) {
		super.service(request, response);
		String apriFile = (String) request.getAttribute("apriFileBlob");
		if (apriFile != null && apriFile.equalsIgnoreCase("true")) {
			BigDecimal prgDoc = new BigDecimal((String) request.getAttribute("prgDocumento"));
			this.openDocument(request, response, prgDoc);
		} else {
			String tipoFile = (String) request.getAttribute("tipoFile");
			if (tipoFile != null) {
				setStrNomeDoc("DomandaCIG." + tipoFile);
			} else {
				setStrNomeDoc("DomandaCIG.pdf");
			}
			setStrDescrizione("Domanda CIG");

			setReportPath("Cig/DomandaCIG_CC.rpt");

			// impostazione parametri del report
			Map prompts = new HashMap();

			// solo se e' richiesta la protocollazione i parametri vengono
			// inseriti nella Map
			try {
				addPromptFieldsProtocollazione(prompts, request);
			} catch (EMFUserError ue) {
				setOperationFail(request, response, ue);
				reportFailure(ue, "StampaDomandaCig.service()", "");
			}

			String prgaccordo = (String) request.getAttribute("prgaccordo");
			prompts.put("prgaccordo", prgaccordo);

			setPromptFields(prompts);

			String tipoDoc = (String) request.getAttribute("tipoDoc");
			if (tipoDoc != null)
				setCodTipoDocumento(tipoDoc);

			String salva = (String) request.getAttribute("salvaDB");
			String apri = (String) request.getAttribute("apri");
			if (salva != null && salva.equalsIgnoreCase("true"))
				insertDocument(request, response);
			else if (apri != null && apri.equalsIgnoreCase("true"))
				showDocument(request, response);
		}
	}
}
