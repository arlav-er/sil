package it.eng.sil.action.report.collocamentoMirato;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.error.EMFUserError;

import it.eng.sil.action.report.AbstractSimpleReport;

public class StampaCollMirato extends AbstractSimpleReport {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(StampaCollMirato.class.getName());

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
					setStrNomeDoc("IscrizioneCollocamentoMirato." + tipoFile);
				else
					setStrNomeDoc("IscrizioneCollocamentoMirato.pdf");

				setStrDescrizione("Iscrizione Collocamento Mirato");
				setReportPath("Collocamento_Mirato/IscrCollMirato_CC.rpt");

				// impostazione parametri del report
				Map prompts = new HashMap();

				prompts.put("cdnLavoratore", request.getAttribute("cdnLavoratore"));
				prompts.put("cdnLavoratoreEncrypt", request.getAttribute("cdnLavoratoreEncrypt"));
				prompts.put("prgCmIscr", request.getAttribute("prgCmIscr"));
				prompts.put("numProt", "");
				try {
					addPromptFieldsProtocollazione(prompts, request);
				} catch (EMFUserError ue) {
					setOperationFail(request, response, ue);
					return;
				}
				setPromptFields(prompts);

				String salva = (String) request.getAttribute("salvaDB");
				String apri = (String) request.getAttribute("apri");
				if (salva != null && salva.equalsIgnoreCase("true"))
					insertDocument(request, response);
				else if (apri != null && apri.equalsIgnoreCase("true"))
					showDocument(request, response);

			} catch (Exception e) {
				it.eng.sil.util.TraceWrapper.debug(_logger, getClass().getName() + "::service()", e);

			}
		}
	}
}
