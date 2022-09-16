package it.eng.sil.action.report.amministrazione;

import java.math.BigDecimal;
import java.util.Vector;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.ResponseContainer;
import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.configuration.ConfigSingleton;
import com.engiweb.framework.util.QueryExecutor;

import it.eng.afExt.utils.LogUtils;
import it.eng.sil.action.report.AbstractSimpleReport;

public class StampaProtGiornaliero extends AbstractSimpleReport {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(StampaProtGiornaliero.class.getName());

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
				setStrNomeDoc("ProtGiornaliero." + tipoFile);
			else
				setStrNomeDoc("ProtGiornaliero.pdf");
			setStrDescrizione("Stampa Protocollo giornaliero");
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
					eng = executeQueriesLavTipoCond(request, response);
					if (insertDocument(request, response, eng)) {
					}
				} else if ((apri != null) && apri.equalsIgnoreCase("true")) {
					eng = executeQueriesLavTipoCond(request, response);
					showDocument(request, response, eng);
				}
			} catch (Exception e) {
				it.eng.sil.util.TraceWrapper.debug(_logger, getClass().getName() + "::service()", e);

			}
		}

	}

	com.inet.report.Engine executeQueriesLavTipoCond(SourceBean request, SourceBean response) throws Exception {
		String tipoFile = (String) request.getAttribute("tipoFile");
		SourceBean listaLavTipoCondizioni = doDynamicSelect(request, response);

		Vector vLavTipoCond = listaLavTipoCondizioni.getAttributeAsVector("ROW");

		ApiStampaProtGiornaliero report = new ApiStampaProtGiornaliero((SourceBean) getConfig());

		String installAppPath = ConfigSingleton.getRootPath() + java.io.File.separatorChar;
		report.setInstallAppPath(installAppPath);
		report.setFileType(tipoFile);
		report.setElencolav(vLavTipoCond);
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
		RequestContainer reqCont = getRequestContainer();
		ResponseContainer resCont = getResponseContainer();

		String pool = (String) getConfig().getAttribute("POOL");
		SourceBean statement = (SourceBean) getConfig().getAttribute("SELECT_QUERY");

		SourceBean beanRows = (SourceBean) QueryExecutor.executeQuery(reqCont, resCont, pool, statement, "SELECT");

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
