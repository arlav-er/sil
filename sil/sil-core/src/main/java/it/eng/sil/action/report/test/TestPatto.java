package it.eng.sil.action.report.test;

import java.util.Vector;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.util.QueryExecutor;

import it.eng.afExt.utils.LogUtils;
import it.eng.sil.action.report.AbstractSimpleReport;

public class TestPatto extends AbstractSimpleReport {
	private static final String TRUE = "TRUE";

	/**
	 * Indica "delete" andato a buon fine.
	 */
	protected final static String DELETE_OK = "DELETE_OK";

	/**
	 * Indica "insert" andato a buon fine.
	 */
	protected final static String INSERT_OK = "INSERT_OK";

	/**
	 * Indica "select" andato a buon fine.
	 */
	protected final static String SELECT_OK = "SELECT_OK";
	protected final static String SELECT_FAIL = "SELECT_FAIL";

	public void service(SourceBean request, SourceBean response) {
		try {
			executeQueries(request, response);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void executeQueries(SourceBean request, SourceBean response) throws Exception {
		SourceBean obbligoFormativo = doSelect(request, response, "QUERY_OBBLIGO_FORMATIVO");
		SourceBean permessoDiSoggiorno = doSelect(request, response, "QUERY_PERM_SOGG");
		SourceBean statoOccupazionale = doSelect(request, response, "QUERY_STATO_OCCUPAZIONALE");
		SourceBean listeMobilita = doSelect(request, response, "QUERY_ISCR_MOBILITA");
		SourceBean indispTemporanee = doSelect(request, response, "QUERY_INDISP_TEMP");
		SourceBean esperienzeProfessionali = doSelect(request, response, "QUERY_ESPERIENZE_LAV");
		SourceBean titoliStudio = doSelect(request, response, "QUERY_TITOLI_STUDIO");
		SourceBean corsiFormazione = doSelect(request, response, "QUERY_FORMAZIONE_PROF");
		SourceBean collocamentoMirato = doSelect(request, response, "QUERY_ISRC_COLLOCAMENTO_MIRATO");
		SourceBean indisponibilitaPressoImprese = doSelect(request, response, "QUERY_INDISPONIBILITA");
		SourceBean appuntamenti = doSelect(request, response, "QUERY_APPUNTAMENTI");
		SourceBean azioniConcordate = doSelect(request, response, "QUERY_AZIONI");
		SourceBean ambitoProfessionale = doSelect(request, response, "QUERY_AMBITO_PROFESSIONALE");

		// SourceBean p = doSelect(request, response, "QUERY_IMPEGNI");
		// nella stampa gli impegni non ci sono
		SourceBean infoGenerali = doSelect(request, response, "QUERY_INFO_GENERALI");

		/**/
		Vector indispTemps = indispTemporanee.getAttributeAsVector("ROW");
		Vector esperienzeProfs = esperienzeProfessionali.getAttributeAsVector("ROW");
		Vector titoliStuds = titoliStudio.getAttributeAsVector("ROW");
		Vector corsiFormazs = corsiFormazione.getAttributeAsVector("ROW");
		Vector indisponibilitaPressoImps = indisponibilitaPressoImprese.getAttributeAsVector("ROW");
		Vector appuntaments = appuntamenti.getAttributeAsVector("ROW");
		Vector azioniConcordats = azioniConcordate.getAttributeAsVector("ROW");
		Vector ambitoProfs = ambitoProfessionale.getAttributeAsVector("ROW");

		//
		it.eng.sil.action.report.test.Patto report = new it.eng.sil.action.report.test.Patto();
		report.setInfoGenerali((SourceBean) ((Vector) infoGenerali.getAttribute("ROW")).get(0));
		report.setObbligoFormativo((SourceBean) obbligoFormativo.getAttribute("ROW"));
		report.setPermessoDiSoggiorno((SourceBean) permessoDiSoggiorno.getAttribute("ROW"));
		report.setStatoOccupazionale((SourceBean) statoOccupazionale.getAttribute("ROW"));
		report.setListeMobilita((SourceBean) listeMobilita.getAttribute("ROW"));
		report.setIndispTemporanee(indispTemps);
		report.setEsperienzeProfessionali(esperienzeProfs);
		report.setTitoliStudio(titoliStuds);
		report.setCollocamentoMirato((SourceBean) collocamentoMirato.getAttribute("ROW"));
		report.setCorsiFormazione(corsiFormazs);
		report.setIndisponibilitaPressoImprese(indisponibilitaPressoImps);
		report.setAppuntamenti(appuntaments);
		report.setAzioniConcordate(azioniConcordats);
		report.setAmbitoProfessionale(ambitoProfs);
		report.start();
	}

	/**
	 * 
	 */
	public SourceBean doSelect(SourceBean request, SourceBean response, String queryName) throws Exception {
		String pool = getPool();
		SourceBean statement = getSelectStatement(queryName);

		SourceBean beanRows = null;
		beanRows = (SourceBean) QueryExecutor.executeQuery(getRequestContainer(), getResponseContainer(), pool,
				statement, "SELECT");

		// ReportOperationResult reportOperation = new
		// ReportOperationResult(this, response);
		try {
			LogUtils.logDebug("doSelect", "bean rows [" + beanRows.toXML() + "]", this);

			response.setAttribute(SELECT_OK, TRUE);
		} catch (Exception ex) {
			response.setAttribute(SELECT_FAIL, TRUE);

			// reportOperation.reportFailure(MessageCodes.General.GET_ROW_FAIL,
			// ex, "doSelect", "method failed");
		}

		return beanRows;
	}

	/**
	 * 
	 */
	protected String getPool() {
		return (String) getConfig().getAttribute("POOL");
	}

	/**
	 * 
	 */
	protected SourceBean getSelectStatement(String queryName) {
		SourceBean beanQuery = null;
		beanQuery = (SourceBean) getConfig().getAttribute(queryName);

		return beanQuery;
	}
}

// class Patto
