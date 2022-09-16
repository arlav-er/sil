package it.eng.sil.action.report.amministrazione;

import java.io.File;
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
import com.inet.report.Engine;

import it.eng.afExt.dbaccess.sql.IDynamicStatementProvider;
import it.eng.afExt.dbaccess.sql.IDynamicStatementProvider2;
import it.eng.afExt.utils.LogUtils;
import it.eng.afExt.utils.StringUtils;
import it.eng.sil.Values;
import it.eng.sil.action.report.AbstractSimpleReport;
import it.eng.sil.util.Utils;

public class StampaListaSanatoriaRedditi extends AbstractSimpleReport {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger
			.getLogger(StampaListaSanatoriaRedditi.class.getName());

	private String className = this.getClass().getName();
	// filtri di ricerca da passare all'API per visualizzarli nella stampa
	private String dataInizioRichDa = "";
	private String dataInizioRichA = "";
	private String descCpi = "";

	public void service(SourceBean request, SourceBean response) {

		super.service(request, response);

		this.dataInizioRichDa = StringUtils.getAttributeStrNotNull(request, "dataInizioRicerca");
		this.dataInizioRichA = StringUtils.getAttributeStrNotNull(request, "dataInizioRicercaA");
		String codCpi = (StringUtils.getAttributeStrNotNull(request, "CodCPI")).trim();
		if (!codCpi.equals("")) {
			try {
				Object paramsCpi[] = new Object[1];
				paramsCpi[0] = codCpi;
				SourceBean cpiSb = (SourceBean) doSelect(paramsCpi, "GET_CODCPI");
				this.descCpi = StringUtils.getAttributeStrNotNull(cpiSb, "ROW.DESCRIZIONE");
			} catch (Exception eCpi) {
				it.eng.sil.util.TraceWrapper.debug(_logger, getClass().getName() + "::service()", eCpi);
			}

		}
		String apriFile = (String) request.getAttribute("apriFileBlob");
		if (apriFile != null && apriFile.equalsIgnoreCase("true")) {
			BigDecimal prgDoc = new BigDecimal((String) request.getAttribute("prgDocumento"));
			this.openDocument(request, response, prgDoc);
		} else {
			String tipoFile = (String) request.getAttribute("tipoFile");
			if (tipoFile != null) {
				setStrNomeDoc("SanatoriaRedditi." + tipoFile);
			} else {
				setStrNomeDoc("SanatoriaRedditi.pdf");
			}

			setStrDescrizione("Elenco Sanatoria Redditi");
			setReportPath("Amministrazione/ListaSanatoriaRedditi_CC.rpt");

			String tipoDoc = (String) request.getAttribute("tipoDoc");
			if (tipoDoc != null)
				setCodTipoDocumento(tipoDoc);

			String salva = (String) request.getAttribute("salvaDB");
			String apri = (String) request.getAttribute("apri");
			//
			try {
				com.inet.report.Engine eng = null;
				if ((salva != null) && salva.equalsIgnoreCase("true")) {
					eng = executeQueryLista(request, response);
					insertDocument(request, response, eng);
				} else if ((apri != null) && apri.equalsIgnoreCase("true")) {
					eng = executeQueryLista(request, response);
					showDocument(request, response, eng);
				}
			} catch (Exception e) {
				it.eng.sil.util.TraceWrapper.debug(_logger, getClass().getName() + "::service()", e);

			}
		}

	}

	Engine executeQueryLista(SourceBean request, SourceBean response) throws Exception {
		String tipoFile = (String) request.getAttribute("tipoFile");
		//
		SourceBean listaSanatoria = doDynamicSelect(request, response);
		Vector vectListaSanatoria = listaSanatoria.getAttributeAsVector("ROW");
		ApiListaSanatoriaRedditi report = new ApiListaSanatoriaRedditi((SourceBean) getConfig());
		String dataProt = Utils.notNull(request.getAttribute("dataOraProt"));
		String numProt = Utils.notNull(request.getAttribute("numProt"));
		String numAnno = Utils.notNull(request.getAttribute("annoProt"));
		//
		String installAppPath = ConfigSingleton.getRootPath() + File.separatorChar;
		report.setInstallAppPath(installAppPath);
		report.setFileType(tipoFile);
		report.setElenco(vectListaSanatoria);
		report.setNumAnno(numAnno);
		report.setDataProt(dataProt);
		report.setNumProt(numProt);
		report.setDataInizioRicercaDa(this.dataInizioRichDa);
		report.setDataInizioRicercaA(this.dataInizioRichA);
		report.setCodCpi(this.descCpi);
		//
		report.start();
		return report.getEngine();
	}

	public SourceBean doSelect(Object params[], String queryName) throws Exception {
		SourceBean beanRows = null;
		beanRows = (SourceBean) QueryExecutor.executeQuery(queryName, params, "SELECT", Values.DB_SIL_DATI);
		return beanRows;
	}

	public SourceBean doDynamicSelect(SourceBean request, SourceBean response) throws Exception {

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

			if (dc != null) {
				dc.close();
			}
		} catch (Exception e) {
			if (dc != null)
				dc.close();
			LogUtils.logError("doDynamicSelect", "Error", e, this);
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
