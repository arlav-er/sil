package it.eng.sil.action.report.amministrazione;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Statement;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.SessionContainer;
import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.configuration.ConfigSingleton;
import com.engiweb.framework.dbaccess.sql.DataConnection;
import com.engiweb.framework.error.EMFUserError;
import com.engiweb.framework.util.QueryExecutor;

import it.eng.afExt.utils.LogUtils;
import it.eng.sil.action.report.AbstractSimpleReport;
import it.eng.sil.util.Utils;

public class StampaDBFInviati extends AbstractSimpleReport {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(StampaDBFInviati.class.getName());

	DataConnection dataConn = null;
	Connection con = null;
	Statement stmt = null;
	// String sid = "";

	public void service(SourceBean request, SourceBean response) {
		super.service(request, response);
		RequestContainer r = getRequestContainer();
		SessionContainer sescon = r.getSessionContainer();

		String apriFile = (String) request.getAttribute("apriFileBlob");
		if (apriFile != null && apriFile.equalsIgnoreCase("true")) {
			BigDecimal prgDoc = new BigDecimal((String) request.getAttribute("prgDocumento"));
			this.openDocument(request, response, prgDoc);
		} else {
			String utente = (String) request.getAttribute("UTENTE");
			String tipoFile = (String) request.getAttribute("tipoFile");
			if (tipoFile != null)
				setStrNomeDoc("MobilitaEsportate." + tipoFile);
			else
				setStrNomeDoc("MobilitaEsportate.pdf");
			setStrDescrizione("Mobilità esportate il " + new Date());
			String nomeReport = (String) request.getAttribute("NOMEFILE");
			setReportPath("Amministrazione/" + nomeReport);
			/*
			 * Vector params = new Vector(); //params.add(request.getAttribute("cdnLavoratore")); //params.add("101");
			 * 
			 * String cdnLavoratore = (String) request.getAttribute("cdnLavoratore"); if (cdnLavoratore != null &&
			 * !cdnLavoratore.equals("")) { params.add(request.getAttribute("cdnLavoratore")); }
			 * 
			 * if (cdnLavoratore == null || cdnLavoratore.equalsIgnoreCase("")) { }
			 * 
			 * setParams(params);
			 */
			Map prompts = new HashMap();
			// TODO a che serve il cdnlavoratore?
			// sid =(String) request.getAttribute("sid");

			// sid =(String) sescon.getAttribute("HTTP_SESSION_ID");
			// prompts.put("cdnLavoratore", request.getAttribute("cdnLavoratore"));
			// TODO a regime con docarea nella request i dati della
			// protocollazione non dovranno esserci, per cui non sara'
			// necessario annullarli.
			// e comunque farlo qui e' inutile, bisogna annullarlo in Documento.
			prompts.put("numProt", "");
			try {
				addPromptFieldsProtocollazione(prompts, request);
			} catch (EMFUserError ue) {
				setOperationFail(request, response, ue);
				return;
			}
			// ora si chiede di usare il passaggio dei parametri per nome e
			// non per posizione (col vettore, passaggio di default)
			setPromptFields(prompts);

			/* fine modifica parametri di promps */
			String tipoDoc = (String) request.getAttribute("tipoDoc");
			if (tipoDoc != null)
				setCodTipoDocumento(tipoDoc);

			String salva = (String) request.getAttribute("salvaDB");
			String apri = (String) request.getAttribute("apri");

			try {
				com.inet.report.Engine eng = null;
				if ((salva != null) && salva.equalsIgnoreCase("true")) {
					eng = executeQueriesReportDBF(request, response);
					if (insertDocument(request, response, eng)) {
					}
				} else if ((apri != null) && apri.equalsIgnoreCase("true")) {
					eng = executeQueriesReportDBF(request, response);
					showDocument(request, response, eng);
				}
			} catch (Exception e) {
				it.eng.sil.util.TraceWrapper.debug(_logger, getClass().getName() + "::service()", e);
				return;
			}

			/* Aggiorno quelli esportati */
			doUpdateExported();
		}

	}

	com.inet.report.Engine executeQueriesReportDBF(SourceBean request, SourceBean response) throws Exception {
		String tipoFile = (String) request.getAttribute("tipoFile");

		SourceBean listaMob = doSelect(request, response, "QUERY_MOB_APPOGGIO");

		Vector vMobe = listaMob.getAttributeAsVector("ROW");

		ApiStampaDBFInviati report = new ApiStampaDBFInviati((SourceBean) getConfig());

		String numProt = Utils.notNull(request.getAttribute("numProt"));
		String numAnno = Utils.notNull(request.getAttribute("annoProt"));
		String dataProt = Utils.notNull(request.getAttribute("dataOraProt"));

		String installAppPath = ConfigSingleton.getRootPath() + java.io.File.separatorChar;
		report.setInstallAppPath(installAppPath);
		report.setFileType(tipoFile);
		report.setElencoMobEsportati(vMobe);
		report.setNumAnno(numAnno);
		report.setDataProt(dataProt);
		report.setNumProt(numProt);
		report.start();

		return report.getEngine();
	}

	public SourceBean doSelect(SourceBean request, SourceBean response, String queryName) throws Exception {
		String pool = getPool();
		SourceBean statement = getSelectStatement(queryName);

		SourceBean beanRows = (SourceBean) QueryExecutor.executeQuery(getRequestContainer(), getResponseContainer(),
				pool, statement, "SELECT");

		// ReportOperationResult reportOperation = new
		// ReportOperationResult(this, response);
		try {
			LogUtils.logDebug("doSelect", "bean rows [" + beanRows.toXML(false, true) + "]", this);
		} catch (Exception ex) {
			it.eng.sil.util.TraceWrapper.debug(_logger, getClass().getName() + "::doSelect()", ex);

		}

		return beanRows;
	}

	/* Una volta esportati e stampati, devo registrarli come inviati */
	public void doUpdateExported() {

		String pool = getPool();
		SourceBean statement = getSelectStatement("QUERY_UPD_APPOGGIO");

		QueryExecutor.executeQuery(getRequestContainer(), getResponseContainer(), pool, statement, "UPDATE");
		try {
			LogUtils.logDebug("doUpdateExported", "update dbf esportati completo", this);
		} catch (Exception ex) {
			it.eng.sil.util.TraceWrapper.debug(_logger, getClass().getName() + "::doSelect()", ex);

		}

	}

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