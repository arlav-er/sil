/*
 * Creato il 21-mar-06
 *
 * Per modificare il modello associato a questo file generato, aprire
 * Finestra&gt;Preferenze&gt;Java&gt;Generazione codice&gt;Codice e commenti
 */
package it.eng.sil.action.report.amministrazione;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.configuration.ConfigSingleton;
import com.engiweb.framework.dbaccess.DataConnectionManager;
import com.engiweb.framework.dbaccess.sql.DataConnection;
import com.engiweb.framework.dbaccess.sql.SQLCommand;
import com.engiweb.framework.dbaccess.sql.result.DataResult;
import com.engiweb.framework.dbaccess.sql.result.DataResultInterface;
import com.engiweb.framework.dbaccess.sql.result.ScrollableDataResult;
import com.engiweb.framework.error.EMFUserError;
import com.engiweb.framework.util.QueryExecutor;
import com.inet.report.Engine;

import it.eng.afExt.dbaccess.sql.IDynamicStatementProvider;
import it.eng.afExt.dbaccess.sql.IDynamicStatementProvider2;
import it.eng.afExt.utils.LogUtils;
import it.eng.sil.Values;
import it.eng.sil.action.report.AbstractSimpleReport;
import it.eng.sil.util.Utils;

/**
 * @author riccardi
 * 
 *         Per modificare il modello associato al commento di questo tipo generato, aprire
 *         Finestra&gt;Preferenze&gt;Java&gt;Generazione codice&gt;Codice e commenti
 */
public class Mobilita extends AbstractSimpleReport {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(Mobilita.class.getName());

	public void service(SourceBean request, SourceBean response) {

		super.service(request, response);

		String apriFile = (String) request.getAttribute("apriFileBlob");
		if (apriFile != null && apriFile.equalsIgnoreCase("true")) {
			BigDecimal prgDoc = new BigDecimal((String) request.getAttribute("prgDocumento"));
			this.openDocument(request, response, prgDoc);
		} else {
			String tipoFile = (String) request.getAttribute("tipoFile");
			if (tipoFile != null)
				setStrNomeDoc("ListaMobilita." + tipoFile);
			else
				setStrNomeDoc("ListaMobilita.pdf");
			setStrDescrizione("ListaMobilita");
			String nomeReport = (String) request.getAttribute("NOMEFILE");
			setReportPath("Amministrazione/" + nomeReport);

			// setReportPath("Amministrazione/Mobilita_CC.rpt");

			Vector params = new Vector();
			// params.add(request.getAttribute("cdnLavoratore"));
			// params.add("101");
			Map prompts = new HashMap();
			try {
				addPromptFieldsProtocollazione(prompts, request);
			} catch (EMFUserError ue) {
				setOperationFail(request, response, ue);
				return;
			}
			// ora si chiede di usare il passaggio dei parametri per nome e
			// non per posizione (col vettore, passaggio di default)
			setPromptFields(prompts);
			// TODO perche' viene passato il cdnLavoratore? Nel report con le
			// api non c'e' questo parametro-prompt!!
			/*
			 * String cdnLavoratore = (String) request.getAttribute("cdnLavoratore"); if (cdnLavoratore != null &&
			 * !cdnLavoratore.equals("")) { params.add(request.getAttribute("cdnLavoratore")); }
			 * 
			 * setParams(params);
			 */

			String tipoDoc = (String) request.getAttribute("tipoDoc");
			if (tipoDoc != null)
				setCodTipoDocumento(tipoDoc);

			String salva = (String) request.getAttribute("salvaDB");
			String apri = (String) request.getAttribute("apri");

			try {
				com.inet.report.Engine eng = null;
				if ((salva != null) && salva.equalsIgnoreCase("true")) {
					eng = executeQuery(request, response);
					insertDocument(request, response, eng);
				} else if ((apri != null) && apri.equalsIgnoreCase("true")) {
					eng = executeQuery(request, response);
					showDocument(request, response, eng);
				}
			} catch (Exception e) {
				it.eng.sil.util.TraceWrapper.debug(_logger, getClass().getName() + "::service()", e);

			}
		}

	}

	Engine executeQuery(SourceBean request, SourceBean response) throws EMFUserError, Exception {
		String tipoFile = (String) request.getAttribute("tipoFile");

		SourceBean listaMobilita = doDynamicSelect(request, response);
		Vector vecMobilita = listaMobilita.getAttributeAsVector("ROW");

		ApiMobilita report = new ApiMobilita((SourceBean) getConfig());

		String dataProt = Utils.notNull(request.getAttribute("dataOraProt"));

		String installAppPath = ConfigSingleton.getRootPath() + java.io.File.separatorChar;
		report.setInstallAppPath(installAppPath);
		report.setMobilita(vecMobilita);
		report.setFileType(tipoFile);
		report.setDataProt(dataProt);
		String config_Mob = "0";
		SourceBean rowConfig = doSelect("ST_GETCONFIGURAZIONE_MOBILITA_GEN", null);
		if (rowConfig != null) {
			config_Mob = rowConfig.containsAttribute("ROW.NUM") ? rowConfig.getAttribute("ROW.NUM").toString() : "0";

		}
		report.setConfig_Mob(config_Mob);
		report.start();

		return report.getEngine();
	}

	public SourceBean doSelect(String stmName, Object params[]) throws Exception {
		SourceBean result = null;
		result = (SourceBean) QueryExecutor.executeQuery(stmName, params, "SELECT", Values.DB_SIL_DATI);
		return result;
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

	public SourceBean doDynamicSelect(SourceBean request, SourceBean response) throws Exception {

		DataConnection dc = null;
		DataConnectionManager dcm = null;
		SQLCommand cmdSelect = null;
		DataResult dr = null;
		ScrollableDataResult sdr = null;

		String pool = (String) getConfig().getAttribute("POOL");
		SourceBean query = (SourceBean) getConfig().getAttribute("SELECT_QUERY");
		SourceBean beanRows = null;

		try {
			String statementProviderClassName = (String) query.getAttribute("STATEMENT_PROVIDER.CLASS");

			// INSTANZIA LA CLASSE CHE RITORNA LE QUERY
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

			// eseguiamo la query
			dr = cmdSelect.execute();

			// crea la lista con il dataresult
			if (dr.getDataResultType().equals(DataResultInterface.SCROLLABLE_DATA_RESULT)) {
				sdr = (ScrollableDataResult) dr.getDataObject();
			}

			beanRows = sdr.getSourceBean();
		} catch (Exception e) {
			LogUtils.logError("doDynamicSelect", "Error", e, this);
		}

		finally {
			if (sdr != null) {
				sdr.close();
			}
			if (cmdSelect != null) {
				cmdSelect.close();
			}
			if (dc != null) {
				dc.close();
			}
		}
		return beanRows;
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
