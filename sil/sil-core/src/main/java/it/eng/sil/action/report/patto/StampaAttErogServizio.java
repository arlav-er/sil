package it.eng.sil.action.report.patto;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import com.engiweb.framework.base.SourceBean;

import it.eng.sil.action.report.AbstractSimpleReport;

public class StampaAttErogServizio extends AbstractSimpleReport {

	public void service(SourceBean request, SourceBean response) {
		super.service(request, response);
		String apriFile = (String) request.getAttribute("apriFileBlob");
		if (apriFile != null && apriFile.equalsIgnoreCase("true")) {
			BigDecimal prgDoc = new BigDecimal((String) request.getAttribute("prgDocumento"));
			this.openDocument(request, response, prgDoc);
		} else {
			String tipoFile = (String) request.getAttribute("tipoFile");
			if (tipoFile != null) {
				setStrNomeDoc("StampaErogazioneServizio." + tipoFile);
			} else {
				setStrNomeDoc("StampaErogazioneServizio.pdf");
			}
			setStrDescrizione("Stampa Erogazione Servizio");

			setReportPath("patto/attestazione_erogazione_servizio_CAL_CC.rpt");

			Map prompts = new HashMap();

			prompts.put("cdnLavoratore", request.getAttribute("cdnLavoratore"));
			prompts.put("prgColloquio", request.getAttribute("prgColloquio"));
			prompts.put("prgPercorso", request.getAttribute("prgPercorso"));

			setPromptFields(prompts);

			String tipoDoc = (String) request.getAttribute("tipoDoc");
			if (tipoDoc != null)
				setCodTipoDocumento(tipoDoc);

			String salva = (String) request.getAttribute("salvaDB");
			String apri = (String) request.getAttribute("apri");
			if (salva != null && salva.equalsIgnoreCase("true")) {
				setStrChiavetabella((String) request.getAttribute("strchiavetabella"));
				insertDocument(request, response);
			} else if (apri != null && apri.equalsIgnoreCase("true"))
				showDocument(request, response);

		}

	}
}