package it.eng.sil.action.report.amministrazione;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.error.EMFUserError;

import it.eng.sil.action.report.AbstractSimpleReport;

public class StampaTitoliMinist extends AbstractSimpleReport {

	public void service(SourceBean request, SourceBean response) {
		super.service(request, response);
		String apriFile = (String) request.getAttribute("apriFileBlob");
		if (apriFile != null && apriFile.equalsIgnoreCase("true")) {
			BigDecimal prgDoc = new BigDecimal((String) request.getAttribute("prgDocumento"));
			this.openDocument(request, response, prgDoc);
		} else {
			String tipoFile = (String) request.getAttribute("tipoFile");
			if (tipoFile != null) {
				setStrNomeDoc("titoliministeriali." + tipoFile);
			} else {
				setStrNomeDoc("titoliministeriali.pdf");
			}
			setStrDescrizione("Titoli");
			setReportPath("Amministrazione/Titoli_MIN_CC.rpt");

			// impostazione parametri del report
			Map prompts = new HashMap();

			prompts.put("codCpi", request.getAttribute("codCPI"));
			prompts.put("cdnlav", request.getAttribute("cdnLavoratore"));

			// solo se e' richiesta la protocollazione i parametri vengono
			// inseriti nella Map
			try {
				addPromptFieldsProtocollazione(prompts, request);
			} catch (EMFUserError ue) {
				setOperationFail(request, response, ue);
				reportFailure(ue, "StampaTitoliMinist.service()", "");
			}

			/*
			 * String prgMobilita = (String) request.getAttribute("prgMobilita"); prompts.put("prgMobilita",
			 * prgMobilita);
			 * 
			 * String regioneCRT = (String) request.getAttribute("regioneCRT"); prompts.put("regioneCRT", regioneCRT);
			 * 
			 * String provCRT = (String) request.getAttribute("provCRT"); prompts.put("provCRT", provCRT);
			 */

			// ora si chiede di usare il passaggio dei parametri per nome e
			// non per posizione (col vettore, passaggio di default)
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
