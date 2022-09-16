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

public class StampaListaMobilitaComitato extends AbstractSimpleReport {
	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger
			.getLogger(StampaListaMobilitaComitato.class.getName());

	// filtri di ricerca da passare all'API per visualizzarli nella stampa
	private String dataCPM = "";
	// private String dataDomandaA = "";
	private String tipoLista = "";
	private String statoRichiesta = "";
	private String descCpi = "";

	public void service(SourceBean request, SourceBean response) {
		super.service(request, response);

		this.dataCPM = (StringUtils.getAttributeStrNotNull(request, "dataRiunone")).trim();
		// this.dataDomandaA = (StringUtils.getAttributeStrNotNull(request, "dataRiunoneA")).trim();
		this.tipoLista = (StringUtils.getAttributeStrNotNull(request, "CodTipoLista")).trim();
		this.statoRichiesta = (StringUtils.getAttributeStrNotNull(request, "codEsito")).trim();
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
				setStrNomeDoc("ListaMobilitaPerComitato." + tipoFile);
			else
				setStrNomeDoc("ListaMobilitaPerComitato.pdf");
			setStrDescrizione("Lista Mobilità per Comitato");
			setReportPath("Amministrazione/ListaMobilitaComitato_CC.rpt");

			String tipoDoc = (String) request.getAttribute("tipoDoc");
			if (tipoDoc != null)
				setCodTipoDocumento(tipoDoc);

			String salva = (String) request.getAttribute("salvaDB");
			String apri = (String) request.getAttribute("apri");

			try {
				com.inet.report.Engine eng = null;
				if ((salva != null) && salva.equalsIgnoreCase("true")) {
					eng = executeQueries(request, response);
					insertDocument(request, response, eng);
				} else if ((apri != null) && apri.equalsIgnoreCase("true")) {
					eng = executeQueries(request, response);
					showDocument(request, response, eng);
				}
			} catch (Exception e) {
				it.eng.sil.util.TraceWrapper.debug(_logger, getClass().getName() + "::service()", e);
			}
		}
	}

	com.inet.report.Engine executeQueries(SourceBean request, SourceBean response) throws Exception {
		String tipoFile = (String) request.getAttribute("tipoFile");
		SourceBean listaLavSL = doDynamicSelect(request, response);

		Vector vLav = listaLavSL.getAttributeAsVector("ROW");
		ApiMobilitaComitato report = new ApiMobilitaComitato((SourceBean) getConfig());

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
		report.setDataCPM(this.dataCPM);
		// report.setDataDomandaA(this.dataDomandaA);
		String descTipoLista = "";
		if (!this.tipoLista.equals("")) {
			SourceBean rowsTipoLista = doSelect("GET_MO_TIPO_LISTA", null);
			if (rowsTipoLista != null) {
				Vector vettTipoLista = rowsTipoLista.getAttributeAsVector("ROW");
				int sizeTipoLista = vettTipoLista.size();
				Vector tipoListaStampa = StringUtils.split(this.tipoLista, ",");
				int sizeTipoListaStampa = tipoListaStampa.size();
				for (int k = 0; k < sizeTipoListaStampa; k++) {
					String codiceListaStampa = tipoListaStampa.get(k).toString();
					for (int i = 0; i < sizeTipoLista; i++) {
						SourceBean rowCurr = (SourceBean) vettTipoLista.get(i);
						String codiceLista = rowCurr.getAttribute("CODICE").toString();
						if (codiceLista.equalsIgnoreCase(codiceListaStampa)) {
							if (descTipoLista.equals("")) {
								descTipoLista = StringUtils.getAttributeStrNotNull(rowCurr, "DESCRIZIONE");
							} else {
								descTipoLista = descTipoLista + "/"
										+ StringUtils.getAttributeStrNotNull(rowCurr, "DESCRIZIONE");
							}
							break;
						}
					}
				}
			}
		}
		report.setTipoLista(descTipoLista);
		String descStatoRich = "";
		if (!this.statoRichiesta.equals("")) {
			SourceBean rowsStato = doSelect("AMSTR_GET_DE_MB_STATO", null);
			if (rowsStato != null) {
				Vector vettStati = rowsStato.getAttributeAsVector("ROW");
				int sizeTipoLista = vettStati.size();
				for (int i = 0; i < sizeTipoLista; i++) {
					SourceBean rowCurr = (SourceBean) vettStati.get(i);
					String codiceStato = rowCurr.getAttribute("CODICE").toString();
					if (codiceStato.equalsIgnoreCase(this.statoRichiesta)) {
						descStatoRich = StringUtils.getAttributeStrNotNull(rowCurr, "DESCRIZIONE");
						break;
					}
				}
			}
		}
		report.setStatoRichiesta(descStatoRich);
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
