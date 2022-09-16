package it.eng.sil.action.report.redditoattivazione;

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

import it.eng.afExt.dbaccess.sql.IDynamicStatementProvider;
import it.eng.afExt.dbaccess.sql.IDynamicStatementProvider2;
import it.eng.afExt.utils.LogUtils;
import it.eng.sil.action.report.AbstractSimpleReport;
import it.eng.sil.util.Utils;

public class StampaListaNuovoRA extends AbstractSimpleReport {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(StampaListaNuovoRA.class.getName());

	public void service(SourceBean request, SourceBean response) {
		super.service(request, response);

		String tipoFile = (String) request.getAttribute("tipoFile");
		if (tipoFile != null)
			setStrNomeDoc("NuovoRA." + tipoFile);
		else
			setStrNomeDoc("NuovoRA.pdf");

		setStrDescrizione("Nuovo Reddito Attivazione");
		String nomeReport = "ListaNRA_CC.rpt";

		setReportPath("redditoattivazione/" + nomeReport);

		Map prompts = new HashMap();
		prompts.put("cdnLavoratore", request.getAttribute("cdnLavoratore"));
		try {
			addPromptFieldsProtocollazione(prompts, request);
		} catch (EMFUserError ue) {
			setOperationFail(request, response, ue);
			return;
		}
		setPromptFields(prompts);

		String tipoDoc = (String) request.getAttribute("tipoDoc");
		if (tipoDoc != null) {
			setCodTipoDocumento(tipoDoc);
		}

		try {
			com.inet.report.Engine eng = null;
			eng = executeQueriesRiepilogo(request, response);
			showDocument(request, response, eng);
		} catch (Exception e) {
			it.eng.sil.util.TraceWrapper.debug(_logger, getClass().getName() + "::service()", e);
		}
	}

	com.inet.report.Engine executeQueriesRiepilogo(SourceBean request, SourceBean response) throws Exception {
		String tipoFile = (String) request.getAttribute("tipoFile");
		SourceBean lista = doDynamicSelect(request, response);

		Vector vLista = lista.getAttributeAsVector("ROW");

		ApiListaNuovoRA report = new ApiListaNuovoRA((SourceBean) getConfig());

		String numProt = Utils.notNull(request.getAttribute("numProt"));
		String numAnno = Utils.notNull(request.getAttribute("annoProt"));
		String dataProt = Utils.notNull(request.getAttribute("dataOraProt"));

		String installAppPath = ConfigSingleton.getRootPath() + java.io.File.separatorChar;
		report.setInstallAppPath(installAppPath);
		report.setFileType(tipoFile);
		report.setElencoNRA(vLista);
		report.setNumAnno(numAnno);
		report.setDataProt(dataProt);
		report.setNumProt(numProt);
		report.start();

		return report.getEngine();
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
			ScrollableDataResult sdr = null;

			if (dr.getDataResultType().equals(DataResultInterface.SCROLLABLE_DATA_RESULT)) {
				sdr = (ScrollableDataResult) dr.getDataObject();
			}

			beanRows = sdr.getSourceBean();
			if (sdr != null) {
				sdr.close();
			}

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

}