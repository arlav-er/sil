package it.eng.sil.action.report.ido;

import java.util.HashMap;
import java.util.Map;

import com.engiweb.framework.base.SourceBean;

import it.eng.sil.action.report.AbstractSimpleReport;

public class StampaCollMiratoLavCancellati extends AbstractSimpleReport {
	private static final long serialVersionUID = 1L;
	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger
			.getLogger(StampaCollMiratoLavCancellati.class.getName());

	public void service(SourceBean request, SourceBean response) {
		super.service(request, response);
		setStrDescrizione("Stampa Collocamento Mirato Lavoratori Cancellati");
		setReportPath("pubb/StampaCollMiratoLavCancellati.rpt");

		// impostazione parametri del report
		Map prompts = new HashMap();

		String prgrosa = (String) request.getAttribute("PRGROSA");

		prompts.put("prgRosa", prgrosa);

		// ora si chiede di usare il passaggio dei parametri per nome e
		// non per posizione (col vettore, passaggio di default)
		setPromptFields(prompts);

		String tipoFile = (String) request.getAttribute("tipoFile");
		if (tipoFile != null)
			setStrNomeDoc("StampaCollMiratoLavCancellati." + tipoFile);
		else
			setStrNomeDoc("StampaCollMiratoLavCancellati.pdf");

		String tipoDoc = (String) request.getAttribute("tipoDoc");

		if (tipoDoc != null) {
			setCodTipoDocumento(tipoDoc);
		}

		try {
			showDocument(request, response);

		} catch (Exception e) {
			it.eng.sil.util.TraceWrapper.debug(_logger, getClass().getName() + "::service()", e);
			return;
		}

	}

	protected String getPool() {
		return (String) getConfig().getAttribute("POOL");
	}

	protected SourceBean getSelectStatement(String queryName) {
		SourceBean beanQuery = null;
		beanQuery = (SourceBean) getConfig().getAttribute(queryName);

		return beanQuery;
	}
}
