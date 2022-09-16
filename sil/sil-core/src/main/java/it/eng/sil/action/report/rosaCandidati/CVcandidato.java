package it.eng.sil.action.report.rosaCandidati;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.error.EMFUserError;

import it.eng.sil.action.report.AbstractSimpleReport;

public class CVcandidato extends AbstractSimpleReport {

	public void service(SourceBean request, SourceBean response) {

		super.service(request, response);

		String apriFile = (String) request.getAttribute("apriFileBlob");
		if (apriFile != null && apriFile.equalsIgnoreCase("true")) {
			BigDecimal prgDoc = new BigDecimal((String) request.getAttribute("prgDocumento"));
			this.openDocument(request, response, prgDoc);
		} else {
			try {
				String tipoFile = (String) request.getAttribute("tipoFile");
				if (tipoFile != null)
					setStrNomeDoc("CVcandidato." + tipoFile);
				else
					setStrNomeDoc("CVcandidato.pdf");
				setStrDescrizione("CV candidato rosa IDO");
				setReportPath("RosaCandidati_IDO/CVcandidato_CC.rpt");

				// impostazione parametri del report
				Map prompts = new HashMap();

				prompts.put("pCdnLavoratore", request.getAttribute("cdnLavoratore"));
				prompts.put("pMostraPerLavoratore", request.getAttribute("mostraPerLavoratore"));

				// solo se e' richiesta la protocollazione i parametri vengono
				// inseriti nella Map
				addPromptFieldsProtocollazione(prompts, request);

				// ora si chiede di usare il passaggio dei parametri per nome e
				// non per posizione (col vettore, passaggio di default)
				setPromptFields(prompts);

				String salva = (String) request.getAttribute("salvaDB");
				String apri = (String) request.getAttribute("apri");
				if (salva != null && salva.equalsIgnoreCase("true"))
					insertDocument(request, response);
				else if (apri != null && apri.equalsIgnoreCase("true"))
					showDocument(request, response);
			} catch (EMFUserError ue) {
				setOperationFail(request, response, ue);
				reportFailure(ue, "CVcandidato.service()", "");
			}
		} // else
	}
}