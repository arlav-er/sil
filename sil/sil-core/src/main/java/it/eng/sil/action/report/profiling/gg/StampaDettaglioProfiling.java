package it.eng.sil.action.report.profiling.gg;

import java.util.HashMap;
import java.util.Map;

import com.engiweb.framework.base.SourceBean;

import it.eng.sil.action.report.AbstractSimpleReport;
import it.eng.sil.security.User;

public class StampaDettaglioProfiling extends AbstractSimpleReport {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8373278610452162443L;

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger
			.getLogger(StampaDettaglioProfiling.class.getName());

	private static String reportRelativePath = "profilingGG/ReportProfilingGG.rpt";

	public void service(SourceBean request, SourceBean response) {

		super.service(request, response);

		Object prgProfiling = request.getAttribute("PRGYGPROFILING");

		String reportOutputFileName = "PROFILING_GG";
		String reportDescription = "PROFILING_GG";

		User user = (User) getRequestContainer().getSessionContainer().getAttribute(User.USERID);

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
			// prompts.put("cdnLavoratore", request.getAttribute("cdnLavoratore"));
			prompts.put("PRGYGPROFILING", prgProfiling);
			prompts.put("cdnLavoratore", request.getAttribute("cdnLavoratore"));

			addPromptFieldsProtocollazione(prompts, request);

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

		} catch (Exception e) {

			setOperationFail(request, response, e);
			_logger.error(e.getMessage());

		}
	}

}
