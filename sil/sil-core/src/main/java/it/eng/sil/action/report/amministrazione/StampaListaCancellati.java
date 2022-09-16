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
import it.eng.afExt.utils.StringUtils;
import it.eng.sil.Values;
import it.eng.sil.action.report.AbstractSimpleReport;
import it.eng.sil.util.Utils;

public class StampaListaCancellati extends AbstractSimpleReport {
	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(StampaListaCancellati.class.getName());

	// filtri di ricerca da passare all'API per visualizzarli nella stampa
	private String dataComitatoDa = "";
	private String dataComitatoA = "";
	private String tipoLista = "";
	private String descCpi = "";

	public void service(SourceBean request, SourceBean response) {
		super.service(request, response);

		this.dataComitatoDa = StringUtils.getAttributeStrNotNull(request, "dataInDA");
		this.dataComitatoA = StringUtils.getAttributeStrNotNull(request, "dataInA");
		this.tipoLista = StringUtils.getAttributeStrNotNull(request, "CodTipoLista");
		String codCpi = (StringUtils.getAttributeStrNotNull(request, "CodCPI")).trim();
		if (!codCpi.equals("")) {
			try {
				Object paramsCpi[] = new Object[1];
				paramsCpi[0] = codCpi;
				SourceBean cpiSb = (SourceBean) doSelect("GET_CODCPI", paramsCpi);
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
			if (tipoFile != null)
				setStrNomeDoc("ListaCancellati." + tipoFile);
			else
				setStrNomeDoc("ListaCancellati.pdf");
			setStrDescrizione("Lista Cancellati");
			setReportPath("Amministrazione/ListaCancellati_CC.rpt");

			String tipoDoc = (String) request.getAttribute("tipoDoc");
			if (tipoDoc != null)
				setCodTipoDocumento(tipoDoc);

			String salva = (String) request.getAttribute("salvaDB");
			String apri = (String) request.getAttribute("apri");

			try {
				com.inet.report.Engine eng = null;
				if ((salva != null) && salva.equalsIgnoreCase("true")) {
					eng = executeQueriesListaCanc(request, response);
					insertDocument(request, response, eng);
				} else if ((apri != null) && apri.equalsIgnoreCase("true")) {
					eng = executeQueriesListaCanc(request, response);
					showDocument(request, response, eng);
				}
			} catch (Exception e) {
				it.eng.sil.util.TraceWrapper.debug(_logger, getClass().getName() + "::service()", e);
			}
		}
	}

	com.inet.report.Engine executeQueriesListaCanc(SourceBean request, SourceBean response) throws Exception {
		String tipoFile = (String) request.getAttribute("tipoFile");
		SourceBean listaLavSL = doDynamicSelect(request, response);

		Vector vLav = listaLavSL.getAttributeAsVector("ROW");
		ApiListaCancellati report = new ApiListaCancellati((SourceBean) getConfig());

		String numProt = Utils.notNull(request.getAttribute("numProt"));
		String numAnno = Utils.notNull(request.getAttribute("annoProt"));
		String dataProt = Utils.notNull(request.getAttribute("dataOraProt"));

		String installAppPath = ConfigSingleton.getRootPath() + java.io.File.separatorChar;
		report.setInstallAppPath(installAppPath);
		report.setFileType(tipoFile);
		report.setElencolav(vLav);
		report.setNumAnno(numAnno);
		report.setDataProt(dataProt);
		report.setNumProt(numProt);
		report.setDataComitatoDa(this.dataComitatoDa);
		report.setDataComitatoA(this.dataComitatoA);
		String descTipoLista = "";
		if (!this.tipoLista.equals("")) {
			SourceBean rowsTipoLista = doSelect("GET_MO_TIPO_LISTA", null);
			if (rowsTipoLista != null) {
				Vector vettTipoLista = rowsTipoLista.getAttributeAsVector("ROW");
				int sizeTipoLista = vettTipoLista.size();
				for (int i = 0; i < sizeTipoLista; i++) {
					SourceBean rowCurr = (SourceBean) vettTipoLista.get(i);
					String codiceLista = rowCurr.getAttribute("CODICE").toString();
					if (codiceLista.equalsIgnoreCase(this.tipoLista)) {
						descTipoLista = StringUtils.getAttributeStrNotNull(rowCurr, "DESCRIZIONE");
						break;
					}
				}
			}
		}
		report.setTipoLista(descTipoLista);
		report.setCodCpi(this.descCpi);
		//
		report.start();
		return report.getEngine();
	}

	public SourceBean doSelect(String stmName, Object params[]) throws Exception {
		SourceBean result = null;
		result = (SourceBean) QueryExecutor.executeQuery(stmName, params, "SELECT", Values.DB_SIL_DATI);
		return result;
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
