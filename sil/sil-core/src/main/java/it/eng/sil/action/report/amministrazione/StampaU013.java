package it.eng.sil.action.report.amministrazione;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import com.engiweb.framework.base.SourceBean;

import it.eng.sil.action.report.AbstractSimpleReport;

public class StampaU013 extends AbstractSimpleReport {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(StampaU013.class.getName());

	private static final long serialVersionUID = 9015433517028506547L;

	private static String reportDescription = "U013";
	private static String reportRelativePath = "Amministrazione/StampaU013_UMB_CC.rpt";
	private static String reportOutputFileName = "U013";

	public void service(SourceBean request, SourceBean response) {

		super.service(request, response);

		String apriFile = (String) request.getAttribute("apriFileBlob");
		if (apriFile != null && apriFile.equalsIgnoreCase("true")) {
			BigDecimal prgDoc = new BigDecimal((String) request.getAttribute("prgDocumento"));
			this.openDocument(request, response, prgDoc);
		} else {

			try {

				setStrDescrizione(reportDescription);

				String tipoFile = (String) request.getAttribute("tipoFile");
				if (tipoFile != null) {
					setStrNomeDoc(reportOutputFileName + "." + tipoFile);
				} else {
					setStrNomeDoc(reportOutputFileName + ".pdf");
				}

				setReportPath(reportRelativePath);

				// impostazione parametri input del report
				Map prompts = new HashMap();
				prompts.put("cdnLavoratore", request.getAttribute("cdnLavoratore"));
				prompts.put("codCPI", request.getAttribute("codCPI"));
				setPromptFields(prompts);

				addPromptFieldsProtocollazione(prompts, request);

				String salva = (String) request.getAttribute("salvaDB");
				String apri = (String) request.getAttribute("apri");

				if (salva != null && salva.equalsIgnoreCase("true")) {
					insertDocument(request, response);
				} else if (apri != null && apri.equalsIgnoreCase("true")) {
					showDocument(request, response);
				}

			} catch (Exception e) {

				setOperationFail(request, response, e);
				_logger.error(e.getMessage());

			}

		}

	}
}
