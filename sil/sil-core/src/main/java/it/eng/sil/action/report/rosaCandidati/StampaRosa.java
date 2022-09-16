package it.eng.sil.action.report.rosaCandidati;

import java.math.BigDecimal;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.configuration.ConfigSingleton;
import com.engiweb.framework.error.EMFErrorSeverity;
import com.engiweb.framework.error.EMFUserError;
import com.inet.report.Engine;

import it.eng.afExt.utils.MessageCodes;
import it.eng.afExt.utils.ReportOperationResult;
import it.eng.afExt.utils.SourceBeanUtils;
import it.eng.sil.action.report.AbstractSimpleReport;
import it.eng.sil.module.AccessoSemplificato;

public class StampaRosa extends AbstractSimpleReport {
	// private static final String TRUE = "TRUE";

	/**
	 * Indica "delete" andato a buon fine.
	 */
	// protected final static String DELETE_OK = "DELETE_OK";
	// protected final static String INSERT_OK = "INSERT_OK";
	// protected final static String INSERT_FAIL = "INSERT_FAIL";
	/**
	 * Indica "insert" andato a buon fine.
	 */

	/**
	 * Indica "select" andato a buon fine.
	 */
	// protected final static String SELECT_OK = "SELECT_OK";
	// protected final static String SELECT_FAIL = "SELECT_FAIL";
	public void service(SourceBean request, SourceBean response) {
		super.service(request, response);
		ReportOperationResult reportOperation = new ReportOperationResult(this, response);
		try {
			String apriFile = (String) request.getAttribute("apriFileBlob");
			if ((apriFile != null) && apriFile.equalsIgnoreCase("true")) {
				BigDecimal prgDoc = new BigDecimal((String) request.getAttribute("prgDocumento"));
				this.openDocument(request, response, prgDoc);
			} else {
				setStrDescrizione("Elenco candidati");
				String tipoFile = (String) request.getAttribute("tipoFile");

				if (tipoFile != null) {
					setStrNomeDoc("ElencoCandidati." + tipoFile);
				} else {
					setStrNomeDoc("ElencoCandidati.pdf");
				}
				// in questo caso si utilizza un engine creata con le api: ha
				// senso impostare il report path?
				// da controllare
				setReportPath("rosaCandidati/ElencoCandidati_CC.rpt");
				//
				String annoProt = (String) request.getAttribute("annoProt");
				String dataProtocollo = (String) request.getAttribute("dataOraProt");
				BigDecimal numProt = SourceBeanUtils.getAttrBigDecimal(request, "numProt", null);
				// Settaggio strchiavetabella
				String strChiaveTabella = request.containsAttribute("strChiaveTabella")
						? (String) request.getAttribute("strChiaveTabella")
						: "";
				if ((strChiaveTabella != null) && !strChiaveTabella.equals("")) {
					setStrChiavetabella(strChiaveTabella);
				}
				/*
				 * IL NUMKLOCK VIENE PRESO DA DB NELLA CLASSE DOCUMENTO
				 * 
				 * String kLock = (String) request.getAttribute("kLockProt"); if ((kLock != null) && !kLock.equals(""))
				 * { setNumKeyLock(new BigDecimal(kLock)); }
				 */
				/*
				 * la superclasse ha gia' impostato il tipo documento, quindi ripetere qui l'istruzione e' inutile.
				 * 
				 * String tipoDoc = (String) request.getAttribute("tipoDoc"); if (tipoDoc != null) {
				 * setCodTipoDocumento(tipoDoc); }
				 */
				String salva = (String) request.getAttribute("salvaDB");
				String apri = (String) request.getAttribute("apri");
				com.inet.report.Engine eng = null;
				if ((salva != null) && salva.equalsIgnoreCase("true")) {
					eng = executeQueries(request, response/* , numProt */);
					if (insertDocument(request, response, eng)) {
					}
				} else if ((apri != null) && apri.equalsIgnoreCase("true")) {
					eng = executeQueries(request, response/* , numProt */);
					showDocument(request, response, eng);
				}
				// E' ANCORA UTILIZZATO "datUltimoProtocollo"?
				if ((numProt != null) && annoProt != null) {
					request.updAttribute("datUltimoProtocollo", dataProtocollo);
				}
			}
		} catch (EMFUserError ue) {
			// si e' verificato un errore nell'accesso ai dati. L'errore e' gia'
			// presente nell'error handler del
			// response container
			setOperationFail(request, response, ue);
		} catch (Exception e) {
			// si e' verificato un errore generico o nel report. In questo caso
			// va anche inserito l'errore
			// nell'error handler del response container
			setOperationFail(request, response, e);
			reportOperation.reportFailure(MessageCodes.General.OPERATION_FAIL, e, "StampaRosa.service()", "");
		}
	}

	/**
	 * 
	 * @throws EMFUserError
	 *             se si verifica un errore nell'accesso ai dati
	 * @throws Exception
	 *             se si verifica un errore nel report
	 */
	Engine executeQueries(SourceBean request, SourceBean response/*
																	 * , BigDecimal numProtocollo
																	 */) throws EMFUserError, Exception {
		String tipoFile = (String) request.getAttribute("tipoFile");
		AccessoSemplificato accessoSemplificato = new AccessoSemplificato(this);
		accessoSemplificato.setSectionQuerySelect("DETTAGLIO_ROSA_QUERY");
		SourceBean dettaglioRosa = accessoSemplificato.doSelect(request, response);
		if (dettaglioRosa == null)
			throw new EMFUserError(EMFErrorSeverity.ERROR, MessageCodes.General.OPERATION_FAIL);
		// Savino 08/09/2005
		// vengono aggiunte le mansioni della richiesta
		accessoSemplificato.setSectionQuerySelect("MANSIONI_RICHIESTA");
		SourceBean mansioni = accessoSemplificato.doSelect(request, response, false);
		if (mansioni == null)
			throw new EMFUserError(EMFErrorSeverity.ERROR, MessageCodes.General.OPERATION_FAIL);
		dettaglioRosa.setAttribute("row.mansioni", mansioni.getAttributeAsVector("row"));
		// SourceBean dettaglioRosa = doSelect(request, response,
		// "DETTAGLIO_ROSA_QUERY");
		accessoSemplificato.setSectionQuerySelect("LISTA_CANDIDATI_QUERY");
		SourceBean listaCandidatiRosa = accessoSemplificato.doDynamicSelect(request, response);
		if (listaCandidatiRosa == null)
			throw new EMFUserError(EMFErrorSeverity.ERROR, MessageCodes.General.OPERATION_FAIL);
		// SourceBean listaCandidatiRosa = doDynamicSelect(request, response);
		it.eng.sil.action.report.rosaCandidati.ApiStampaRosa report = new it.eng.sil.action.report.rosaCandidati.ApiStampaRosa();
		String installAppPath = ConfigSingleton.getRootPath() + java.io.File.separatorChar;
		report.setInstallAppPath(installAppPath);
		// report.setNumProtocollo(numProtocollo);
		report.setFileType(tipoFile);
		report.setElencoCandidati(listaCandidatiRosa.getAttributeAsVector("ROW"));
		report.setInfoGenerali((SourceBean) dettaglioRosa.getAttribute("ROW"));
		report.start();
		return report.getEngine();
	}

	/**
	 * 
	 */
	/*
	 * public SourceBean doSelect(SourceBean request, SourceBean response, String queryName) throws Exception { String
	 * pool = getPool(); SourceBean statement = getSelectStatement(queryName);
	 * 
	 * SourceBean beanRows = null; beanRows = (SourceBean) QueryExecutor.executeQuery(getRequestContainer(),
	 * getResponseContainer(), pool, statement, "SELECT");
	 * 
	 * //ReportOperationResult reportOperation = new ReportOperationResult(this, response); try {
	 * LogUtils.logDebug("doSelect", "bean rows [" + beanRows.toXML(false, true) + "]", this);
	 * 
	 * response.setAttribute(SELECT_OK, TRUE); } catch (Exception ex) { response.setAttribute(SELECT_FAIL, TRUE);
	 * 
	 * //reportOperation.reportFailure(MessageCodes.General.GET_ROW_FAIL, ex, "doSelect", "method failed"); }
	 * 
	 * return beanRows; }
	 */
	/*
	 * public Object doUpdate(SourceBean request, SourceBean response, String queryName) throws Exception { String pool
	 * = getPool(); SourceBean statement = getSelectStatement(queryName);
	 * 
	 * Boolean beanRows = null; beanRows = (Boolean) QueryExecutor.executeQuery(getRequestContainer(),
	 * getResponseContainer(), pool, statement, "UPDATE");
	 * 
	 * //ReportOperationResult reportOperation = new ReportOperationResult(this, response); try {
	 * //LogUtils.logDebug("doSelect", "bean rows [" + beanRows.toXML(false, true) + "]", this);
	 * 
	 * response.setAttribute("UPDATE_OK", TRUE); } catch (Exception ex) { response.setAttribute("UPDATE_FAIL", TRUE);
	 * 
	 * //reportOperation.reportFailure(MessageCodes.General.GET_ROW_FAIL, ex, "doSelect", "method failed"); }
	 * 
	 * return beanRows; }
	 * 
	 */
	/*
	 * public SourceBean doDynamicSelect(SourceBean request, SourceBean response) { //oggetti per l'esecuzione della
	 * query. //non verrà usato il query executor perché la query non //e' definita nei file di configurazione, ma viene
	 * reperita tramite //uno statement provider DataConnection dc = null; DataConnectionManager dcm = null; SQLCommand
	 * cmdSelect = null; DataResult dr = null;
	 * 
	 * String pool = (String) getConfig().getAttribute("POOL"); SourceBean query = (SourceBean)
	 * getConfig().getAttribute("LISTA_CANDIDATI_QUERY"); SourceBean beanRows = null; //ListIFace list = new
	 * GenericList();
	 * 
	 * try { String statementProviderClassName = (String) query.getAttribute("STATEMENT_PROVIDER.CLASS");
	 * 
	 * //INSTANZIA LA CLASSE CHE RITORNA LE QUERY Object statementProvider =
	 * Class.forName(statementProviderClassName).newInstance(); String statement = "";
	 * 
	 * if (statementProvider instanceof IDynamicStatementProvider) { statement = ((IDynamicStatementProvider)
	 * statementProvider).getStatement(getRequestContainer(), getConfig()); } else if (statementProvider instanceof
	 * IDynamicStatementProvider2) { statement = ((IDynamicStatementProvider2) statementProvider).getStatement(request,
	 * response); }
	 * 
	 * dcm = DataConnectionManager.getInstance();
	 * 
	 * if (dcm == null) { LogUtils.logError("doDynamicSelect", "dcm null", this); }
	 * 
	 * dc = dcm.getConnection(pool);
	 * 
	 * if (dc == null) { LogUtils.logError("doDynamicSelect", "dc null", this); }
	 * 
	 * cmdSelect = dc.createSelectCommand(statement);
	 * 
	 * //eseguiamo la query dr = cmdSelect.execute();
	 * 
	 * //crea la lista con il dataresult ScrollableDataResult sdr = null;
	 * 
	 * if (dr.getDataResultType().equals(DataResultInterface.SCROLLABLE_DATA_RESULT)) { sdr = (ScrollableDataResult)
	 * dr.getDataObject(); }
	 * 
	 * beanRows = sdr.getSourceBean(); } catch (Exception e) { LogUtils.logError("doDynamicSelect", "Error", e, this); }
	 * // ReportOperationResult reportOperation = new ReportOperationResult(this, response);
	 * 
	 * try { LogUtils.logDebug("doDynamicSelect", "bean rows [" + ((beanRows != null) ? beanRows.toXML(false) : "null")
	 * + "]", this);
	 * 
	 * response.setAttribute(beanRows);
	 * 
	 * //response.setAttribute(SELECT_OK, "TRUE"); //if (this.messageIdSuccess>0)
	 * reportOperation.reportSuccess(this.messageIdSuccess); } catch (Exception ex) {
	 * //LogUtils.logError("doDynamicSelect", "Error", e, this); LogUtils.logError("doDynamicSelect", "Error", ex,
	 * this); // reportOperation.reportFailure(this.messageIdFail, ex, "doDynamicSelect", "method failed"); }
	 * 
	 * return beanRows; }
	 * 
	 */

	/**
	 * 
	 */
	/*
	 * public boolean doInsert(SourceBean request, SourceBean response, String queryName) throws Exception { String pool
	 * = getPool(); SourceBean statement = getSelectStatement(queryName);
	 * 
	 * 
	 * java.lang.Boolean ret = new Boolean("false"); ret = (Boolean) QueryExecutor.executeQuery(getRequestContainer(),
	 * getResponseContainer(), pool, statement, "INSERT");
	 * 
	 * //ReportOperationResult reportOperation = new ReportOperationResult(this, response); try {
	 * LogUtils.logDebug("doINsert", "", this);
	 * 
	 * response.setAttribute(INSERT_OK, TRUE); } catch (Exception ex) { response.setAttribute(INSERT_FAIL, TRUE);
	 * //reportOperation.reportFailure(MessageCodes.General.GET_ROW_FAIL, ex, "doSelect", "method failed"); }
	 * 
	 * return ret.booleanValue(); }
	 */
	/**
	 * 
	 */
	/*
	 * protected String getPool() { return (String) getConfig().getAttribute("POOL"); }
	 */
	/**
	 * 
	 */
	/*
	 * protected SourceBean getSelectStatement(String queryName) { SourceBean beanQuery = null; beanQuery = (SourceBean)
	 * getConfig().getAttribute(queryName);
	 * 
	 * return beanQuery; }
	 */
	/*
	 * private void creaStatoOcc(SourceBean request, SourceBean response) throws Exception { /* leggi movimenti se non
	 * ci sono movimenti e' inoccupato se ci sono bisogna controllare se risulta disoccupato o no se tutto bene
	 * inserisci stato occupazionale
	 */
	/*
	 * String codStatoOcc = null; //setSectionQuerySelect("QUERY_MOVIMENTI");
	 * 
	 * SourceBean row = doSelect(request, response, "QUERY_MOVIMENTI");
	 * 
	 * if (row == null) { throw new Exception("impossibile leggere da am_movimenti"); }
	 * 
	 * Vector v = row.getAttributeAsVector("ROWS.ROW");
	 * 
	 * if (v.size() == 0) { // il lavoratore e' inoccupato codStatoOcc = "I"; } else { SourceBean mov =(SourceBean)
	 * v.get(0); BigDecimal mesiInattivita = (BigDecimal)mov.getAttribute("datfinemov"); if (mesiInattivita==null){ //
	 * il lavoratore e' attualmente occupato (datfinemov=null) throw new Exception("Impossibile rilasciare la
	 * dichiarazine di immediata disponibilità: il lavoratore e' occupato"); } else { // il lavoratore e' disoccupato e
	 * bisogna vedere in quale categoria rientra // per ora lo considero disoccupato codStatoOcc = "D"; } } // leggi il
	 * prg //setSectionQuerySelect("QUERY_NEXT_AM_STATO_OCC"); row = doSelect(request,
	 * response,"QUERY_NEXT_AM_STATO_OCC"); Object nextVal = row.getAttribute("ROW.NEXTVAL");
	 * request.delAttribute("prgStatoOccupaz"); request.setAttribute("prgStatoOccupaz", nextVal);
	 * //setSectionQueryInsert("QUERY_INSERT_STATO_OCCUPAZ"); request.delAttribute("datInizio");
	 * request.setAttribute("datInizio", DateUtils.getNow()); request.delAttribute("CODSTATOOCCUPAZ");
	 * request.setAttribute("CODSTATOOCCUPAZ", codStatoOcc); if (!doInsert(request,
	 * response,"QUERY_INSERT_STATO_OCCUPAZ")) throw new Exception("impossibile creare lo stato occupazionale"); }
	 */
}
