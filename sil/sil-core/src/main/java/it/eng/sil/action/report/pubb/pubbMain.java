package it.eng.sil.action.report.pubb;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Vector;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.configuration.ConfigSingleton;
import com.engiweb.framework.dbaccess.DataConnectionManager;
import com.engiweb.framework.dbaccess.sql.DataConnection;
import com.engiweb.framework.dbaccess.sql.SQLCommand;
import com.engiweb.framework.dbaccess.sql.result.DataResult;
import com.engiweb.framework.dbaccess.sql.result.DataResultInterface;
import com.engiweb.framework.dbaccess.sql.result.ScrollableDataResult;
import com.engiweb.framework.error.EMFErrorSeverity;
import com.engiweb.framework.util.QueryExecutor;

import it.eng.afExt.dbaccess.sql.IDynamicStatementProvider;
import it.eng.afExt.dbaccess.sql.IDynamicStatementProvider2;
import it.eng.afExt.utils.LogUtils;
import it.eng.afExt.utils.MessageCodes;
import it.eng.afExt.utils.ReportOperationResult;
import it.eng.sil.action.report.AbstractSimpleReport;
import it.eng.sil.action.report.ReportException;

public class pubbMain extends AbstractSimpleReport {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(pubbMain.class.getName());

	public void service(SourceBean request, SourceBean response) {
		super.service(request, response);
		ReportOperationResult reportOperation = new ReportOperationResult(this, response);
		String apriFile = (String) request.getAttribute("apriFileBlob");
		if (apriFile != null && apriFile.equalsIgnoreCase("true")) {
			BigDecimal prgDoc = new BigDecimal((String) request.getAttribute("prgDocumento"));
			this.openDocument(request, response, prgDoc);
		} else {
			String utente = (String) request.getAttribute("UTENTE");
			String tipoFile = (String) request.getAttribute("tipoFile");
			if (tipoFile != null)
				setStrNomeDoc("Elenco_pubblicazioni." + tipoFile);
			else
				setStrNomeDoc("Elenco_pubblicazioni.pdf");
			setStrDescrizione("Elenco_pubblicazioni");
			String nomeReport = (String) request.getAttribute("NOMEFILE");
			setReportPath("pubb/" + nomeReport);

			Vector params = new Vector(1);
			// params.add(request.getAttribute("cdnLavoratore"));
			// params.add("101");

			String datPubblicazioneDal = (String) request.getAttribute("DATPUBBLICAZIONEDAL");
			if (datPubblicazioneDal != null && !datPubblicazioneDal.equals("")) {
				params.add(request.getAttribute("datPubblicazioneDal"));
			} else {
				params.add("01/01/1900");
			}

			String datPubblicazioneAl = (String) request.getAttribute("DATPUBBLICAZIONEAL");
			if (datPubblicazioneAl != null && !datPubblicazioneAl.equals("")) {
				params.add(request.getAttribute("datPubblicazioneAl"));
			} else {
				params.add("31/12/2100");
			}

			String numPubblicazioneDal = (String) request.getAttribute("NUMPUBBLICAZIONEDAL");
			if (numPubblicazioneDal != null && !numPubblicazioneDal.equals("")) {
				params.add(request.getAttribute("NUMPUBBLICAZIONEDAL"));
			} else {
				// params.add(BigInteger.valueOf(Long.MIN_VALUE).toString());
				params.add(BigInteger.ZERO.toString());
			}

			String numPubblicazioneAl = (String) request.getAttribute("NUMPUBBLICAZIONEAL");
			if (numPubblicazioneAl != null && !numPubblicazioneAl.equals("")) {
				params.add(request.getAttribute("NUMPUBBLICAZIONEAL"));
			} else {
				params.add(BigInteger.valueOf(Long.MAX_VALUE).toString());
			}

			String numAnnoDal = (String) request.getAttribute("NUMANNODAL");
			if (numAnnoDal != null && !numAnnoDal.equals("")) {
				params.add(request.getAttribute("NUMANNODAL"));
			} else {
				params.add("1900");
			}

			String numAnnoAl = (String) request.getAttribute("NUMANNOAL");
			if (numAnnoAl != null && !numAnnoAl.equals("")) {
				params.add(request.getAttribute("NUMANNOAL"));
			} else {
				params.add("2100");
			}

			String numAnno = (String) request.getAttribute("ANNO");
			if (numAnno != null && !numAnno.equals("")) {
				params.add(request.getAttribute("ANNO"));
			} else {
				params.add("0");
			}

			// setParams(params);

			String tipoDoc = (String) request.getAttribute("tipoDoc");
			if (tipoDoc != null)
				setCodTipoDocumento(tipoDoc);

			String salva = (String) request.getAttribute("salvaDB");
			String apri = (String) request.getAttribute("apri");

			if ((nomeReport != null) && (nomeReport.equalsIgnoreCase("NewPubbl_az_CC.rpt"))) {
				try {
					com.inet.report.Engine eng = null;
					if ((salva != null) && salva.equalsIgnoreCase("true")) {
						eng = executeQueriesPubbl_az(request, response);
						if (insertDocument(request, response, eng)) {
						}
					} else if ((apri != null) && apri.equalsIgnoreCase("true")) {
						eng = executeQueriesPubbl_az(request, response);
						showDocument(request, response, eng);
					}
				} catch (Exception e) {
					it.eng.sil.util.TraceWrapper.debug(_logger, getClass().getName() + "::service()", e);

				}
			}

			if ((nomeReport != null) && (nomeReport.equalsIgnoreCase("Pubbl_CC.rpt"))) {
				try {
					com.inet.report.Engine eng = null;
					if ((salva != null) && salva.equalsIgnoreCase("true")) {
						eng = executeQueriesPubbl(request, response);
						if (insertDocument(request, response, eng)) {
						}
					} else if ((apri != null) && apri.equalsIgnoreCase("true")) {
						eng = executeQueriesPubbl(request, response);
						showDocument(request, response, eng);
					}
				} catch (ReportException ee) {
					setOperationFail(request, response, ee);
				} catch (Exception e) {
					setOperationFail(request, response, e);
					it.eng.sil.util.TraceWrapper.debug(_logger, getClass().getName() + "::service()", e);

				}

			}
		} // else
	}

	com.inet.report.Engine executeQueriesPubbl_az(SourceBean request, SourceBean response) throws Exception {
		String tipoFile = (String) request.getAttribute("tipoFile");
		SourceBean listaPubblicazioni = doDynamicSelect(request, response);

		Vector vPubblicazioni = listaPubblicazioni.getAttributeAsVector("ROW");
		for (int i = 0; i < vPubblicazioni.size(); i++) {
			BigDecimal richiesta = (BigDecimal) ((SourceBean) vPubblicazioni.get(i)).getAttribute("PRGRICHIESTAAZ");
			request.setAttribute("richiesta", richiesta);
			SourceBean listaMansioniRichiesta = doSelect(request, response, "SELECT_MANSIONI_QUERY");
			SourceBean listaDiffusioniRichiesta = doSelect(request, response, "SELECT_DIFFUSIONE_QUERY");
			((SourceBean) vPubblicazioni.get(i)).setAttribute("MANSIONI", listaMansioniRichiesta);
			((SourceBean) vPubblicazioni.get(i)).setAttribute("DIFFUSIONI", listaDiffusioniRichiesta);
			request.delAttribute("richiesta");
		}

		ApiPubblAz report = new ApiPubblAz();

		String installAppPath = ConfigSingleton.getRootPath() + java.io.File.separatorChar;
		report.setInstallAppPath(installAppPath);
		report.setFileType(tipoFile);
		report.setElencoPubbl(listaPubblicazioni.getAttributeAsVector("ROW"));

		report.start();

		return report.getEngine();
	}

	com.inet.report.Engine executeQueriesPubbl(SourceBean request, SourceBean response)
			throws Exception, ReportException {
		String tipoFile = (String) request.getAttribute("tipoFile");
		SourceBean listaPubblicazioni = doDynamicSelect(request, response);
		if (!listaPubblicazioni.containsAttribute("ROW")) {
			response.setAttribute("operationResult", "ERROR");
			throw new ReportException(EMFErrorSeverity.ERROR, MessageCodes.General.REPORT_EMPTY);
		}
		Vector vPubblicazioni = listaPubblicazioni.getAttributeAsVector("ROW");
		for (int i = 0; i < vPubblicazioni.size(); i++) {
			BigDecimal richiesta = (BigDecimal) ((SourceBean) vPubblicazioni.get(i)).getAttribute("PRGRICHIESTAAZ");
			request.setAttribute("richiesta", richiesta);
			SourceBean listaMansioniRichiesta = doSelect(request, response, "SELECT_MANSIONI_QUERY");
			((SourceBean) vPubblicazioni.get(i)).setAttribute("RIF_MANSIONI", listaMansioniRichiesta);
			request.delAttribute("richiesta");
		}

		ApiPubbl report = new ApiPubbl();

		String installAppPath = ConfigSingleton.getRootPath() + java.io.File.separatorChar;
		report.setInstallAppPath(installAppPath);
		// report.setNumProtocollo(numProtocollo);
		report.setFileType(tipoFile);
		report.setElencoPubbl(listaPubblicazioni.getAttributeAsVector("ROW"));

		report.start();

		return report.getEngine();
	}

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

		ReportOperationResult reportOperation = new ReportOperationResult(this, response);

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
			// reportOperation.reportFailure(MessageCodes.General.REPORT_EMPTY,
			// e, "doSelect", "method failed");
			// setOperationFail(request, response, e);

		} finally {
			com.engiweb.framework.dbaccess.Utils.releaseResources(dc, cmdSelect, dr);
		}

		// ReportOperationResult reportOperation = new
		// ReportOperationResult(this, response);

		try {
			LogUtils.logDebug("doDynamicSelect",
					"bean rows [" + ((beanRows != null) ? beanRows.toXML(false) : "null") + "]", this);

			if (beanRows != null)
				response.setAttribute(beanRows);

			// response.setAttribute(SELECT_OK, "TRUE");
			// if (this.messageIdSuccess>0)
			// reportOperation.reportSuccess(this.messageIdSuccess);
		} catch (Exception ex) {
			// LogUtils.logError("doDynamicSelect", "Error", e, this);
			LogUtils.logError("doDynamicSelect", "Error", ex, this);
			// reportOperation.reportFailure(this.messageIdFail, ex,
			// "doDynamicSelect", "method failed");
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
