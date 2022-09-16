package it.eng.sil.action.report.amministrazione;

import java.math.BigDecimal;
import java.util.Vector;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.configuration.ConfigSingleton;
import com.engiweb.framework.dbaccess.DataConnectionManager;
import com.engiweb.framework.dbaccess.sql.DataConnection;
import com.engiweb.framework.dbaccess.sql.SQLCommand;
import com.engiweb.framework.dbaccess.sql.result.DataResult;
import com.engiweb.framework.dbaccess.sql.result.DataResultInterface;
import com.engiweb.framework.dbaccess.sql.result.ScrollableDataResult;
import com.engiweb.framework.util.QueryExecutor;

import it.eng.afExt.dbaccess.sql.IDynamicStatementProvider;
import it.eng.afExt.dbaccess.sql.IDynamicStatementProvider2;
import it.eng.afExt.utils.LogUtils;
import it.eng.sil.action.report.AbstractSimpleReport;
import it.eng.sil.util.Utils;

public class StampaServizioLavoro extends AbstractSimpleReport {
	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger
			.getLogger(StampaLavObbligoIstruzione.class.getName());

	public void service(SourceBean request, SourceBean response) {
		super.service(request, response);

		String apriFile = (String) request.getAttribute("apriFileBlob");
		if (apriFile != null && apriFile.equalsIgnoreCase("true")) {
			BigDecimal prgDoc = new BigDecimal((String) request.getAttribute("prgDocumento"));
			this.openDocument(request, response, prgDoc);
		} else {
			String utente = (String) request.getAttribute("UTENTE");
			String tipoFile = (String) request.getAttribute("tipoFile");
			if (tipoFile != null)
				setStrNomeDoc("DonneServizioLavoro." + tipoFile);
			else
				setStrNomeDoc("DonneServizioLavoro.pdf");
			setStrDescrizione("Donne Provinciale per Servizio Lavoro");
			String nomeReport = (String) request.getAttribute("NOMEFILE");
			setReportPath("Amministrazione/" + nomeReport);

			String tipoDoc = (String) request.getAttribute("tipoDoc");
			if (tipoDoc != null)
				setCodTipoDocumento(tipoDoc);

			String salva = (String) request.getAttribute("salvaDB");
			String apri = (String) request.getAttribute("apri");

			try {
				com.inet.report.Engine eng = null;
				if ((salva != null) && salva.equalsIgnoreCase("true")) {
					eng = executeQueriesLavSL(request, response);
					if (insertDocument(request, response, eng)) {
					}
				} else if ((apri != null) && apri.equalsIgnoreCase("true")) {
					eng = executeQueriesLavSL(request, response);
					showDocument(request, response, eng);
				}
			} catch (Exception e) {
				it.eng.sil.util.TraceWrapper.debug(_logger, getClass().getName() + "::service()", e);
			}
		}
	}

	com.inet.report.Engine executeQueriesLavSL(SourceBean request, SourceBean response) throws Exception {
		String tipoFile = (String) request.getAttribute("tipoFile");
		SourceBean listaLavSL = doDynamicSelect(request, response);

		String sesso = Utils.notNull(request.getAttribute("sesso"));
		String dat = Utils.notNull(request.getAttribute("datada"));

		Vector vLavSL = listaLavSL.getAttributeAsVector("ROW");
		ApiServizioLav report = new ApiServizioLav((SourceBean) getConfig());

		String numProt = Utils.notNull(request.getAttribute("numProt"));
		String numAnno = Utils.notNull(request.getAttribute("annoProt"));
		String dataProt = Utils.notNull(request.getAttribute("dataOraProt"));

		String installAppPath = ConfigSingleton.getRootPath() + java.io.File.separatorChar;
		report.setInstallAppPath(installAppPath);
		report.setFileType(tipoFile);
		report.setSesso(sesso);
		report.setDatada(dat);
		report.setElencolav(vLavSL);
		report.setNumAnno(numAnno);
		report.setDataProt(dataProt);
		report.setNumProt(numProt);
		report.start();

		return report.getEngine();
	}

	public SourceBean doSelect(SourceBean request, SourceBean response, String queryName) throws Exception {
		String pool = getPool();
		SourceBean statement = getSelectStatement(queryName);
		SourceBean beanRows = null;
		beanRows = (SourceBean) QueryExecutor.executeQuery(getRequestContainer(), getResponseContainer(), pool,
				statement, "SELECT");
		try {
			LogUtils.logDebug("doSelect", "bean rows [" + beanRows.toXML(false, true) + "]", this);
		} catch (Exception ex) {
			it.eng.sil.util.TraceWrapper.debug(_logger, getClass().getName() + "::doSelect()", ex);
		}
		return beanRows;
	}

	public SourceBean doDynamicSelect(SourceBean request, SourceBean response) {

		DataConnection dc = null;
		DataConnectionManager dcm = null;
		SQLCommand cmdSelect = null;
		DataResult dr = null;

		String pool = (String) getConfig().getAttribute("POOL");
		SourceBean query = (SourceBean) getConfig().getAttribute("SELECT_QUERY");
		SourceBean beanRows = null;

		try {
			String statementProviderClassName = (String) query.getAttribute("STATEMENT_PROVIDER.CLASS");
			Object statementProvider = Class.forName(statementProviderClassName).newInstance();
			String statement = "";

			if (statementProvider instanceof IDynamicStatementProvider) {
				statement = ((IDynamicStatementProvider) statementProvider).getStatement(getRequestContainer(),
						getConfig());
			} else if (statementProvider instanceof IDynamicStatementProvider2) {
				statement = ((IDynamicStatementProvider2) statementProvider).getStatement(request, response);
			}

			dcm = DataConnectionManager.getInstance();

			if (dcm == null) {
				LogUtils.logError("doDynamicSelect", "dcm null", this);
			}

			dc = dcm.getConnection(pool);

			if (dc == null) {
				LogUtils.logError("doDynamicSelect", "dc null", this);
			}

			cmdSelect = dc.createSelectCommand(statement);

			dr = cmdSelect.execute();
			ScrollableDataResult sdr = null;

			if (dr.getDataResultType().equals(DataResultInterface.SCROLLABLE_DATA_RESULT)) {
				sdr = (ScrollableDataResult) dr.getDataObject();
			}

			beanRows = sdr.getSourceBean();
			if (sdr != null)
				sdr.close();

		} catch (Exception e) {
			LogUtils.logError("doDynamicSelect", "Error", e, this);
		} finally {
			com.engiweb.framework.dbaccess.Utils.releaseResources(dc, cmdSelect, null);
		}

		return beanRows;
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
