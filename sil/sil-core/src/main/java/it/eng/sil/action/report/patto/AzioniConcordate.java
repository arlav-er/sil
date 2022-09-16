package it.eng.sil.action.report.patto;

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

import it.eng.afExt.dbaccess.sql.IDynamicStatementProvider;
import it.eng.afExt.dbaccess.sql.IDynamicStatementProvider2;
import it.eng.afExt.utils.LogUtils;
import it.eng.sil.action.report.AbstractSimpleReport;
import it.eng.sil.util.Utils;

public class AzioniConcordate extends AbstractSimpleReport {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(AzioniConcordate.class.getName());

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
				setStrNomeDoc("AzioniConcordate." + tipoFile);
			else
				setStrNomeDoc("AzioniConcordate.pdf");
			setStrDescrizione("Azioni Concordate");
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
			prompts.put("cdnLavoratore", request.getAttribute("cdnLavoratore"));
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
					eng = executeQueriesAzConcordare(request, response);
					if (insertDocument(request, response, eng)) {
					}
				} else if ((apri != null) && apri.equalsIgnoreCase("true")) {
					eng = executeQueriesAzConcordare(request, response);
					showDocument(request, response, eng);
				}
			} catch (Exception e) {
				it.eng.sil.util.TraceWrapper.debug(_logger, getClass().getName() + "::service()", e);

			}
		}

	}

	com.inet.report.Engine executeQueriesAzConcordare(SourceBean request, SourceBean response) throws Exception {
		String tipoFile = (String) request.getAttribute("tipoFile");
		SourceBean listaAzioniConcordate = doDynamicSelect(request, response);

		Vector vAzioniConcordate = listaAzioniConcordate.getAttributeAsVector("ROW");

		// for (int i=0; i<vAzioniConcordate.size();i++){
		// BigDecimal
		// richiesta=(BigDecimal)((SourceBean)vAzioniConcordate.get(i)).getAttribute("PRGRICHIESTAAZ");
		// request.setAttribute("richiesta",richiesta);
		// SourceBean listaMansioniRichiesta =
		// doSelect(request,response,"SELECT_MANSIONI_QUERY");
		// SourceBean listaDiffusioniRichiesta =
		// doSelect(request,response,"SELECT_DIFFUSIONE_QUERY");
		// ((SourceBean)vAzioniConcordate.get(i)).setAttribute("MANSIONI",listaMansioniRichiesta);
		// ((SourceBean)vAzioniConcordate.get(i)).setAttribute("DIFFUSIONI",listaDiffusioniRichiesta);
		// request.delAttribute("richiesta");
		// }

		ApiAzioniConcordate report = new ApiAzioniConcordate((SourceBean) getConfig());

		String numProt = Utils.notNull(request.getAttribute("numProt"));
		String numAnno = Utils.notNull(request.getAttribute("annoProt"));
		String dataProt = Utils.notNull(request.getAttribute("dataOraProt"));

		String installAppPath = ConfigSingleton.getRootPath() + java.io.File.separatorChar;
		report.setInstallAppPath(installAppPath);
		report.setFileType(tipoFile);
		report.setElencoAzioni(vAzioniConcordate);
		report.setNumAnno(numAnno);
		report.setDataProt(dataProt);
		report.setNumProt(numProt);
		report.start();

		return report.getEngine();
	}

	// com.inet.report.Engine executeQueriesPubbl(SourceBean request, SourceBean
	// response)
	// throws Exception {
	// String tipoFile = (String) request.getAttribute("tipoFile");
	// SourceBean listaPubblicazioni = doDynamicSelect(request, response);
	//
	// Vector vPubblicazioni = listaPubblicazioni.getAttributeAsVector("ROW");
	// for (int i=0; i<vPubblicazioni.size();i++){
	// BigDecimal
	// richiesta=(BigDecimal)((SourceBean)vPubblicazioni.get(i)).getAttribute("PRGRICHIESTAAZ");
	// request.setAttribute("richiesta",richiesta);
	// SourceBean listaMansioniRichiesta =
	// doSelect(request,response,"SELECT_MANSIONI_QUERY");
	// ((SourceBean)vPubblicazioni.get(i)).setAttribute("RIF_MANSIONI",listaMansioniRichiesta);
	// request.delAttribute("richiesta");
	// }
	//
	// ApiAzioniConcordate report = new ApiAzioniConcordate();
	//
	// String installAppPath = ConfigSingleton.getRootPath();
	// report.setInstallAppPath(installAppPath);
	// //report.setNumProtocollo(numProtocollo);
	// report.setFileType(tipoFile);
	// report.setElencoPubbl(listaPubblicazioni.getAttributeAsVector("ROW"));
	//
	// report.start();
	//
	// return report.getEngine();
	// }

	public SourceBean doSelect(SourceBean request, SourceBean response, String queryName) throws Exception {
		String pool = getPool();
		SourceBean statement = getSelectStatement(queryName);

		SourceBean beanRows = null;
		beanRows = (SourceBean) QueryExecutor.executeQuery(getRequestContainer(), getResponseContainer(), pool,
				statement, "SELECT");

		// ReportOperationResult reportOperation = new
		// ReportOperationResult(this, response);
		try {
			LogUtils.logDebug("doSelect", "bean rows [" + beanRows.toXML(false, true) + "]", this);
		} catch (Exception ex) {
			it.eng.sil.util.TraceWrapper.debug(_logger, getClass().getName() + "::doSelect()", ex);

		}

		return beanRows;
	}

	public SourceBean doDynamicSelect(SourceBean request, SourceBean response) {
		// oggetti per l'esecuzione della query.
		// non verrà usato il query executor perché la query non
		// e' definita nei file di configurazione, ma viene reperita tramite
		// uno statement provider
		DataConnection dc = null;
		DataConnectionManager dcm = null;
		SQLCommand cmdSelect = null;
		DataResult dr = null;

		String pool = (String) getConfig().getAttribute("POOL");
		SourceBean query = (SourceBean) getConfig().getAttribute("SELECT_QUERY");
		SourceBean beanRows = null;
		// ListIFace list = new GenericList();

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

	/**
	 * 
	 */
	protected SourceBean getSelectStatement(String queryName) {
		SourceBean beanQuery = null;
		beanQuery = (SourceBean) getConfig().getAttribute(queryName);

		return beanQuery;
	}

}