package it.eng.sil.action.report.ido;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;
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
import it.eng.afExt.utils.StringUtils;
import it.eng.sil.action.report.AbstractSimpleReport;

public class StampaProspNoScop extends AbstractSimpleReport {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(StampaProspetti.class.getName());

	private String className = this.getClass().getName();
	private BigDecimal prgRichEsonero = null;

	public void service(SourceBean request, SourceBean response) {

		super.service(request, response);

		String apriFile = (String) request.getAttribute("apriFileBlob");
		if (apriFile != null && apriFile.equalsIgnoreCase("true")) {
			BigDecimal prgDoc = new BigDecimal((String) request.getAttribute("prgDocumento"));
			this.openDocument(request, response, prgDoc);
		} else {
			String tipoFile = (String) request.getAttribute("tipoFile");
			if (tipoFile != null)
				setStrNomeDoc("StampaAziendeConProspetti." + tipoFile);
			else
				setStrNomeDoc("StampaAziendeConProspetti.pdf");
			setStrDescrizione("Stampa Aziende con Prospetti");
			String nomeReport = (String) request.getAttribute("NOMEFILE");
			setReportPath("ido/" + nomeReport);
			String tipoDoc = (String) request.getAttribute("tipoDoc");
			if (tipoDoc != null)
				setCodTipoDocumento(tipoDoc);

			String salva = (String) request.getAttribute("salvaDB");
			String apri = (String) request.getAttribute("apri");

			Map prompts = new HashMap();
			// solo se e' richiesta la protocollazione i parametri vengono inseriti nella Map
			try {
				addPromptFieldsProtocollazione(prompts, request);
			} catch (EMFUserError e1) {
				setOperationFail(request, response, e1);
				return;
			}
			setPromptFields(prompts);
			try {
				com.inet.report.Engine eng = null;
				if ((salva != null) && salva.equalsIgnoreCase("true")) {
					eng = executeQueryAdesioni(request, response);
					insertDocument(request, response, eng);
				} else if ((apri != null) && apri.equalsIgnoreCase("true")) {
					eng = executeQueryAdesioni(request, response);
					showDocument(request, response, eng);
				}
			} catch (Exception e) {
				it.eng.sil.util.TraceWrapper.debug(_logger, getClass().getName() + "::service()", e);

			}
		}
	}

	Engine executeQueryAdesioni(SourceBean request, SourceBean response) throws Exception {
		String tipoFile = (String) request.getAttribute("tipoFile");
		SourceBean listaAziProspetti = doDynamicSelect(request, response);

		String anno = StringUtils.getAttributeStrNotNull(request, "ANNO");
		String codMonoCategoria = StringUtils.getAttributeStrNotNull(request, "CODMONOCATEGORIA");
		String codMonoStatoProspetto = StringUtils.getAttributeStrNotNull(request, "CODMONOSTATOPROSPETTO");
		Vector vecAziProspetti = listaAziProspetti.getAttributeAsVector("ROW");

		Vector finale = new Vector();

		String concZero = "";
		for (int i = 0; i < vecAziProspetti.size(); i++) {
			SourceBean rowProspetti = (SourceBean) vecAziProspetti.get(i);
			String scopertura = (String) rowProspetti.getAttribute("SCOPERTURA");

			if (scopertura != null && !scopertura.equals("")) {
				StringTokenizer st = new StringTokenizer(scopertura, ";");
				if (st.hasMoreElements()) {

					String disScopTot = st.nextToken();
					concZero = disScopTot.substring(0, 1);
					// questa modifica è stata necessaria perché il valore passato può essere della forma -,num
					if (concZero.equals("-")) {
						disScopTot = StringUtils.replace(disScopTot, "-,", "-0,");
						disScopTot = StringUtils.replace(disScopTot, "-.", "-0.");
					} else {
						disScopTot = (concZero.equals(",") || concZero.equals(".")) ? "0" + disScopTot : disScopTot;
					}

					String disScopNom = st.nextToken();
					concZero = disScopNom.substring(0, 1);
					if (concZero.equals("-")) {
						disScopNom = StringUtils.replace(disScopNom, "-,", "-0,");
						disScopNom = StringUtils.replace(disScopNom, "-.", "-0.");
					} else {
						disScopNom = (concZero.equals(",") || concZero.equals(".")) ? "0" + disScopNom : disScopNom;
					}

					String disScopNum = st.nextToken();
					concZero = disScopNum.substring(0, 1);
					if (concZero.equals("-")) {
						disScopNum = StringUtils.replace(disScopNum, "-,", "-0,");
						disScopNum = StringUtils.replace(disScopNum, "-.", "-0.");
					} else {
						disScopNum = (concZero.equals(",") || concZero.equals(".")) ? "0" + disScopNum : disScopNum;
					}

					String art18ScopTot = st.nextToken();
					concZero = art18ScopTot.substring(0, 1);
					if (concZero.equals("-")) {
						art18ScopTot = StringUtils.replace(art18ScopTot, "-,", "-0,");
						art18ScopTot = StringUtils.replace(art18ScopTot, "-.", "-0.");
					} else {
						art18ScopTot = (concZero.equals(",") || concZero.equals(".")) ? "0" + art18ScopTot
								: art18ScopTot;
					}

					String art18ScopNom = st.nextToken();
					concZero = art18ScopNom.substring(0, 1);
					if (concZero.equals("-")) {
						art18ScopNom = StringUtils.replace(art18ScopNom, "-,", "-0,");
						art18ScopNom = StringUtils.replace(art18ScopNom, "-.", "-0.");
					} else {
						art18ScopNom = (concZero.equals(",") || concZero.equals(".")) ? "0" + art18ScopNom
								: art18ScopNom;
					}

					String art18ScopNum = st.nextToken();
					concZero = art18ScopNum.substring(0, 1);
					if (concZero.equals("-")) {
						art18ScopNum = StringUtils.replace(art18ScopNum, "-,", "-0,");
						art18ScopNum = StringUtils.replace(art18ScopNum, "-.", "-0.");
					} else {
						art18ScopNum = (concZero.equals(",") || concZero.equals(".")) ? "0" + art18ScopNum
								: art18ScopNum;
					}

					Float distot = new Float(disScopTot.replace(',', '.'));
					Float art18STot = new Float(art18ScopTot.replace(',', '.'));

					float risDis = distot.floatValue();
					float risArt18 = art18STot.floatValue();

					rowProspetti.setAttribute("disScopTot", disScopTot);
					rowProspetti.setAttribute("disScopNom", disScopNom);
					rowProspetti.setAttribute("disScopNum", disScopNum);
					rowProspetti.setAttribute("art18ScopNom", art18ScopNom);
					rowProspetti.setAttribute("art18ScopNum", art18ScopNum);
					finale.add(rowProspetti);

				}
			}
		}

		ApiStampaProspettiNoScop report = new ApiStampaProspettiNoScop((SourceBean) getConfig());

		report.setAziProspetto(finale);

		String installAppPath = ConfigSingleton.getRootPath() + java.io.File.separatorChar;
		report.setInstallAppPath(installAppPath);
		report.setFileType(tipoFile);

		report.setAnno(anno);
		report.setCodMonoCategoria(codMonoCategoria);
		report.setCodMonoStatoProspetto(codMonoStatoProspetto);
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
		} catch (Exception e) {
			LogUtils.logError("doDynamicSelect", "Error", e, this);
		} finally {
			com.engiweb.framework.dbaccess.Utils.releaseResources(dc, cmdSelect, dr);
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
