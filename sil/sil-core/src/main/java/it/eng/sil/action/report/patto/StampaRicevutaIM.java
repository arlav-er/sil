package it.eng.sil.action.report.patto;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import com.engiweb.framework.base.SourceBean;

import it.eng.sil.action.report.AbstractSimpleReport;

public class StampaRicevutaIM extends AbstractSimpleReport {

	public void service(SourceBean request, SourceBean response) {
		super.service(request, response);
		String apriFile = (String) request.getAttribute("apriFileBlob");
		if (apriFile != null && apriFile.equalsIgnoreCase("true")) {
			BigDecimal prgDoc = new BigDecimal((String) request.getAttribute("prgDocumento"));
			this.openDocument(request, response, prgDoc);
		} else {
			String tipoFile = (String) request.getAttribute("tipoFile");
			if (tipoFile != null) {
				setStrNomeDoc("RicevutaDID." + tipoFile);
			} else {
				setStrNomeDoc("RicevutaDID.pdf");
			}
			setStrDescrizione("Ricevuta DID");

			setReportPath("patto/RicevutaDID_VDA_CC.rpt");

			Map prompts = new HashMap();

			String prgDichDisp = (String) request.getAttribute("prgDichDisp");
			prompts.put("prgDichDisp", prgDichDisp);

			String codCPI = (String) request.getAttribute("codCPI");
			prompts.put("codCPI", codCPI);

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
