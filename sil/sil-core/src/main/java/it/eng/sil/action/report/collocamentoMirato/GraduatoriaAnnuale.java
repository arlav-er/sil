package it.eng.sil.action.report.collocamentoMirato;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.dbaccess.SQLStatements;
import com.engiweb.framework.error.EMFUserError;
import com.engiweb.framework.util.QueryExecutor;

import it.eng.afExt.utils.StringUtils;
import it.eng.sil.Values;
import it.eng.sil.action.report.AbstractSimpleReport;

public class GraduatoriaAnnuale extends AbstractSimpleReport {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(GraduatoriaAnnuale.class.getName());

	private static final String thisClassName = StringUtils.getClassName(GraduatoriaAnnuale.class);

	private String prgGraduatoria = "";

	public void service(SourceBean request, SourceBean response) {
		super.service(request, response);

		// recupero i parametri
		RequestContainer requestContainer = getRequestContainer();

		String tipoStampa = (String) request.getAttribute("tipoStampa");
		try {
			prgGraduatoria = (String) request.getAttribute("PRGGRADUATORIA");

			String apriFile = (String) request.getAttribute("apriFileBlob");
			if (apriFile != null && apriFile.equalsIgnoreCase("true")) {
				BigDecimal prgDoc = new BigDecimal((String) request.getAttribute("prgDocumento"));
				this.openDocument(request, response, prgDoc);
			} else {
				String tipoFile = (String) request.getAttribute("tipoFile");
				if (tipoFile != null) {
					setStrNomeDoc("GraduatoriaAnnuale." + tipoFile);
				} else {
					setStrNomeDoc("GraduatoriaAnnuale.pdf");
				}
				setStrDescrizione("Stampa graduatoria annuale");
				setReportPath("Collocamento_Mirato/GraduatoriaAnnuale_CC.rpt");

				// impostazione parametri del report
				Map prompts = new HashMap();

				prompts.put("prgGraduatoria", prgGraduatoria);
				prompts.put("numProt", "");
				try {
					addPromptFieldsProtocollazione(prompts, request);
				} catch (EMFUserError ue) {
					setOperationFail(request, response, ue);
					return;
				}
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
			} // else
		} catch (Exception e) {
			it.eng.sil.util.TraceWrapper.debug(_logger, this.getClass().getName() + "::service", e);

			setOperationFail(request, response, e);
		}
	}

	/**
	 * Rende la query SQL dello statement col nome passato.
	 */
	private String getStatement(String statementName) {

		return SQLStatements.getStatement(statementName);
	}

	private void writeLog(String str) {
		_logger.debug(str);

	}

	protected SourceBean getSelectStatement(String queryName) {
		SourceBean beanQuery = null;
		beanQuery = (SourceBean) getConfig().getAttribute(queryName);

		return beanQuery;
	}

	public SourceBean doSelect(String queryName) throws Exception {
		SourceBean statement = getSelectStatement(queryName);

		SourceBean beanRows = null;
		beanRows = (SourceBean) QueryExecutor.executeQuery(getRequestContainer(), getResponseContainer(),
				Values.DB_SIL_DATI, statement, "SELECT");
		return beanRows;
	}

}// class