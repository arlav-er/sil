package it.eng.sil.action.report;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Map;
import java.util.Vector;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.SessionContainer;
import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.base.SourceBeanException;
import com.engiweb.framework.dispatching.action.AbstractHttpAction;
import com.engiweb.framework.error.EMFErrorSeverity;
import com.engiweb.framework.error.EMFInternalError;
import com.engiweb.framework.error.EMFUserError;
import com.inet.report.Engine;

import it.eng.afExt.utils.DateUtils;
import it.eng.afExt.utils.LogUtils;
import it.eng.afExt.utils.MessageCodes;
import it.eng.afExt.utils.SourceBeanUtils;
import it.eng.afExt.utils.TransactionQueryExecutor;
import it.eng.sil.bean.Documento;
import it.eng.sil.bean.ProtocollaException;
import it.eng.sil.security.User;
import it.eng.sil.util.Utils;

public class AbstractSimpleReport extends AbstractHttpAction {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(AbstractSimpleReport.class.getName());

	private final String className = this.getClass().getName();

	private Documento doc = new Documento();
	private Vector params;
	private Map promptFields;
	private String reportPath;
	private BigDecimal numProt;

	protected void setParams(Vector newParams) {
		this.params = newParams;
	}

	protected Vector getParams() {
		return this.params;
	}

	public void service(SourceBean request, SourceBean response) {
		_logger.debug(className + "::service()");

		// recupero eventuali parametri...
		String cdnLavoratoreStr = request.containsAttribute("cdnLavoratore")
				? request.getAttribute("cdnLavoratore").toString()
				: "";
		BigDecimal cdnLavoratore = null;
		if (!cdnLavoratoreStr.equals("")) {
			cdnLavoratore = new BigDecimal(cdnLavoratoreStr);
		}
		// recupera sessione e id utente connesso
		RequestContainer requestContainer = getRequestContainer();
		SessionContainer sessionContainer = (SessionContainer) requestContainer.getSessionContainer();
		User user = (User) sessionContainer.getAttribute(User.USERID);
		String codCpi = user.getCodRif();
		BigDecimal userid = new BigDecimal(user.getCodut());
		String flgAutocertificazione = (String) request.getAttribute("flgAutocertificazione");
		flgAutocertificazione = (flgAutocertificazione == null) ? "N" : flgAutocertificazione;

		// davide
		// SI SETTANO I DATI PER LA PROTOCOLLAZIONE (SE RICHIESTA) DEL DOCUMENTO
		// SU DB
		// (VEDI Documento.insert(TransactionQueryExecutor))
		String tipoDoc = (String) request.getAttribute("tipoDoc");
		if (tipoDoc != null) {
			setCodTipoDocumento(tipoDoc);
		}
		String tipoProt = (String) request.getAttribute("protAutomatica");
		if (tipoProt != null && !tipoProt.equals(""))
			doc.setTipoProt(tipoProt);

		String numProt = (String) request.getAttribute("numProt");
		if (numProt != null && !numProt.equals(""))
			setNumProtocollo(new BigDecimal(numProt));

		String annoProt = (String) request.getAttribute("annoProt");
		if (annoProt != null && !annoProt.equals(""))
			setNumAnnoProt(new BigDecimal(annoProt));

		String dataOraProt = (String) request.getAttribute("dataOraProt");
		if (dataOraProt != null && !dataOraProt.equals(""))
			doc.setDatProtocollazione(dataOraProt);

		String pagina = (String) request.getAttribute("pagina");
		if (pagina != null)
			this.setPagina(pagina);

		// end davide
		// Se dataOraProt <> null allora si sta chiedendo la protocollazione a
		// meno che codstatoatto sia gia' impostato
		String codStatoAtto = (String) request.getAttribute("codStatoAtto");
		if (codStatoAtto != null && !codStatoAtto.equals(""))
			doc.setCodStatoAtto(codStatoAtto);
		else if (dataOraProt != null && !dataOraProt.equals(""))
			doc.setCodStatoAtto("PR");

		doc.setCodCpi(codCpi); // posseduto in sessione dall'utente
		doc.setCdnLavoratore(cdnLavoratore);
		doc.setPrgAzienda(null);
		doc.setPrgUnita(null);
		doc.setCodTipoDocumento(tipoDoc);
		doc.setFlgAutocertificazione(flgAutocertificazione);
		doc.setStrDescrizione("");
		doc.setFlgDocAmm("S");
		doc.setFlgDocIdentifP("N");

		String currentDate = DateUtils.getNow();
		doc.setDatInizio(currentDate); // sysdate
		doc.setStrNumDoc(null); // null (Es. per la Carta di Identita. Ma per i
								// report non dovrebbe mai essere utilizzato)
		doc.setStrEnteRilascio(codCpi); // =codCpi?????????????????????????

		// GG 18/2/2005 - Uso il "CodMonoIO" passato (se non c'è ci metto per
		// default "O")
		String docInOut = SourceBeanUtils.getAttrStr(request, "docInOut", "O");
		setCodMonoIO(docInOut);

		doc.setDatAcqril(currentDate); // sysdate
		doc.setCodModalitaAcqril(null); // cod corrispondente a "rilasciato dal
										// cpi"
		doc.setCodTipoFile(null); // da decodificare
		doc.setStrNomeDoc(""); //

		doc.setDatFine(null); // null

		doc.setStrNote(""); // null

		// Setting userid...
		doc.setCdnUtIns(userid);
		doc.setCdnUtMod(userid);
	}

	/**
	 * Set della Map che contiene i parametri in formato &lt;nome-parametro (case sensitive), valore (in formato
	 * stringa)&gt;
	 * 
	 * @param promptFields
	 */
	protected void setPromptFields(Map promptFields) {
		this.promptFields = promptFields;
	}

	protected boolean insertDocument(SourceBean request, SourceBean response) {
		return this.insertDocument(request, response, null, null);
	}

	/**
	 * Imposta i parametri di ingresso del report. Possiamo passarli in due modi: con un vettore di valori (posizionale)
	 * o per nome-parametro. In quest'ultimo caso bisogna creare un oggetto di tipo Map &lt;'nome parametro',
	 * 'valore'&gt;.
	 * 
	 * @exception (NESSUNA
	 *                ECCEZIONE: COMMENTATA IllegalArgumentException se non sono stati impostati ne' il vettore ne' la
	 *                map dei parametri.)
	 */
	private void setCrystalClearPrompts() {
		if (params != null)
			doc.setCrystalClearPrompts(params);
		else if (promptFields != null)
			doc.setCrystalClearPromptFields(promptFields);
		// else throw new IllegalArgumentException("non sono presenti parametri
		// di ingresso per il report");
	}

	/**
	 * 
	 * @param request
	 * @param response
	 * @param eng
	 *            l'engine di Crystal Clear creato con le relative api.
	 * @return true se l'inserimento e' andato a buon fine, false altrimenti
	 */
	protected boolean insertDocument(SourceBean request, SourceBean response, com.inet.report.Engine eng) {
		return insertDocument(request, response, null, eng);
	}

	protected boolean insertDocument(SourceBean request, SourceBean response, TransactionQueryExecutor txExec) {
		return insertDocument(request, response, txExec, null);
	}

	/**
	 * Inserisce nel db il documento (eventualmente protocollandolo) creato da Crystal Clear. Nella response viene
	 * impostato il flag del risultato (operationResult=SUCCESS|ERROR)
	 * 
	 * @return
	 */
	protected boolean insertDocument(SourceBean request, SourceBean response, TransactionQueryExecutor txExec,
			Engine engine) {
		boolean ok = false;
		setCrystalClearPrompts();
		doc.setCrystalClearRelativeReportFile(reportPath);
		try {
			if (engine != null)
				doc.setEngine(engine);
			if (txExec == null)
				doc.insert();
			else
				doc.insert(txExec);
			setOperationSuccess(request, response);
			/*
			 * if (numProt != null) { response.setAttribute("numProt", numProt.toString()); if (doc.getNumProtocollo()
			 * != null) { response.setAttribute("numProtIns", doc.getNumProtocollo().toString()); } }
			 */
			ok = true;
		} catch (Exception ex) {
			it.eng.sil.util.TraceWrapper.debug(_logger, className + "::service()", ex);

			try {
				// impostazione del risultato di errore nella response
				setOperationFail(request, response, ex);
			} catch (Exception sbEx) {
				it.eng.sil.util.TraceWrapper.debug(_logger, className + "::service()", sbEx);

			}
			ok = false;
		}

		finally {
			return ok;
		}
		// catch (Exception ex)
	}// insertDocument()

	/**
	 * Inserisce nel db il documento (eventualmente protocollandolo) creato da Crystal Clear. Nella response viene
	 * impostato il flag del risultato (operationResult=SUCCESS|ERROR)
	 * 
	 * @return
	 */
	protected boolean updateDocument(SourceBean request, SourceBean response, TransactionQueryExecutor txExec,
			Engine engine) {
		boolean ok = false;
		setCrystalClearPrompts();
		doc.setCrystalClearRelativeReportFile(reportPath);
		try {
			if (engine != null) {
				doc.setEngine(engine);
			}
			doc.update(txExec);
			setOperationSuccess(request, response);
			/*
			 * if (numProt != null) { response.setAttribute("numProt", numProt.toString()); if (doc.getNumProtocollo()
			 * != null) { response.setAttribute("numProtIns", doc.getNumProtocollo().toString()); } }
			 */
			ok = true;
		} catch (Exception ex) {
			it.eng.sil.util.TraceWrapper.debug(_logger, className + "::service()", ex);

			try {
				// impostazione del risultato di errore nella response
				setOperationFail(request, response, ex);
			} catch (Exception sbEx) {
				it.eng.sil.util.TraceWrapper.debug(_logger, className + "::service()", sbEx);

			}
			ok = false;
		}

		finally {
			return ok;
		}
		// catch (Exception ex)
	}// insertDocument()

	/**
	 * @deprecated utilizzare insertDocument(SourceBean , SourceBean , TransactionQueryExecutor)
	 * 
	 * @param txExecutor
	 * @throws Exception
	 */
	protected void insert(TransactionQueryExecutor txExecutor) throws Exception {
		setCrystalClearPrompts();
		doc.setCrystalClearRelativeReportFile(reportPath);
		doc.insert(txExecutor);
	}

	// In questo caso non viene chiamato il metodo doc.insert() perché si vuole
	// solo
	// visualizzare il file e non salvarlo. Si crea un file temporaneo che poi
	// verra'
	// recuperato (e visualizzato) nella download.jsp tramite il metodo
	// getTempFile()
	protected void showDocument(SourceBean request, SourceBean response) {
		this.showDocument(request, response, null, null);
	}

	protected void showDocument(SourceBean request, SourceBean response, TransactionQueryExecutor txExec) {
		this.showDocument(request, response, null, txExec);
	}

	protected void showDocument(SourceBean request, SourceBean response, Engine engine) {
		this.showDocument(request, response, engine, null);
	}

	protected void showDocument(SourceBean request, SourceBean response, Engine eng, TransactionQueryExecutor txExec) {
		// String asAttachment = (String) request.getAttribute("asAttachment");
		setCrystalClearPrompts();
		doc.setCrystalClearRelativeReportFile(reportPath);
		try {
			if (eng != null) {
				doc.createReportTempFile(eng, txExec);
			} else {
				doc.createReportTempFile(txExec);
			}
			setOperationSuccess(request, response);
		} catch (SourceBeanException sbEx) {
			it.eng.sil.util.TraceWrapper.debug(_logger, className + "::service()", sbEx);

		} // catch (SourceBeanException ex)
		catch (Exception ex) {
			it.eng.sil.util.TraceWrapper.debug(_logger, className + "::service()", ex);

			try {
				response.setAttribute("operationResult", "ERROR");
			} catch (SourceBeanException sbEx) {
				it.eng.sil.util.TraceWrapper.debug(_logger, className + "::service()", sbEx);

			}
		}
		// catch (Exception ex)
	}

	/**
	 * Legge da db il documento binario (pdf, gif etc.)
	 * 
	 * @param request
	 * @param response
	 * @param prgDoc
	 *            il progressivo del documento da leggere.
	 */
	protected void openDocument(SourceBean request, SourceBean response, BigDecimal prgDoc) {
		// String asAttachment = (String) request.getAttribute("asAttachment");

		try {
			doc.setPrgDocumento(prgDoc);
			doc.selectExtBlob();
			setOperationSuccess(request, response);
		} catch (SourceBeanException sbEx) {
			it.eng.sil.util.TraceWrapper.debug(_logger, className + "::service()", sbEx);

		} catch (Exception ex) {
			it.eng.sil.util.TraceWrapper.debug(_logger, className + "::service()", ex);

			try {
				response.setAttribute("operationResult", "ERROR");
			} catch (SourceBeanException sbEx) {
				it.eng.sil.util.TraceWrapper.debug(_logger, className + "::service()", sbEx);

			}
		}
	}

	/**
	 * In caso di successo dell'operazione imposta il risultato nella response. In ogni caso bisogna impostare
	 * l'attributo "operationResult" nella response con valori "SUCCESS" o "ERROR"
	 */
	protected void setOperationSuccess(SourceBean request, SourceBean response) throws Exception {
		String asAttachment = (String) request.getAttribute("asAttachment");

		if (asAttachment == null) {
			asAttachment = "false";
		}
		response.updAttribute("theDocument", doc);
		response.updAttribute("asAttachment", asAttachment);
		response.updAttribute("operationResult", "SUCCESS");
	}

	/**
	 * In caso di successo dell'operazione imposta il risultato nella response. In ogni caso bisogna impostare
	 * l'attributo "operationResult" nella response con valori "SUCCESS" e possibili warning
	 */
	protected void setOperationSuccessWithWarning(SourceBean request, SourceBean response, ArrayList warning)
			throws Exception {
		String asAttachment = (String) request.getAttribute("asAttachment");

		if (asAttachment == null) {
			asAttachment = "false";
		}
		response.updAttribute("theDocument", doc);
		response.updAttribute("asAttachment", asAttachment);
		response.updAttribute("operationResult", "SUCCESS");

		response.updAttribute("WARNINGREPORT", warning);
	}

	/**
	 * In ogni caso bisogna impostare l'attributo "operationResult" nella response con valori "SUCCESS" o "ERROR"
	 * 
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	protected void setOperationFail(SourceBean request, SourceBean response) {
		this.setOperationFail(request, response, new Exception("Errore generico"));
	}

	/**
	 * In ogni caso bisogna impostare l'attributo "operationResult" nella response con valori "SUCCESS" o "ERROR"
	 * 
	 * @param request
	 * @param response
	 * @param ex
	 */
	protected void setOperationFail(SourceBean request, SourceBean response, Exception ex) {
		try {
			response.updAttribute("operationResult", "ERROR");
			response.updAttribute("theDocument", doc);
			if (ex instanceof ProtocollaException) {
				response.setAttribute("errorCode", Integer.toString(((ProtocollaException) ex).getMessageIdFail()));
			} else if (ex instanceof EMFUserError) {
				getErrorHandler().addError((EMFUserError) ex);
				// response.setAttribute("errorCode", Integer.toString(
				// ((EMFUserError) ex).getCode() ) );
			} else if (ex instanceof EMFInternalError) {
				EMFInternalError emf = (EMFInternalError) ex;
				if (emf.getNativeException() instanceof SQLException) {
					if (((SQLException) emf.getNativeException()).getErrorCode() == MessageCodes.General.CONCORRENZA) {
						response.setAttribute("errorCode", Integer.toString(MessageCodes.General.CONCORRENZA));
					}
				}
			} else {
				response.updAttribute("errorCode", "0");
			}
		} catch (Exception e) {
		}
	}

	/**
	 * 
	 * @param request
	 * @param response
	 * @param errorCodeInterno
	 *            (codice di errore utente)
	 */
	public void setOperationFail(SourceBean request, SourceBean response, int errorCodeInterno) {
		try {
			response.updAttribute("operationResult", "ERROR");
			response.updAttribute("theDocument", doc);
			response.updAttribute("errorCode", errorCodeInterno);
		} catch (Exception e) {
		}
	}

	/**
	 * @deprecated utilizzare la classe ReportOperationResult Equivale al metodo della classe ReportOperationResult
	 * 
	 * @param messageCode
	 * @param exp
	 * @param methodName
	 * @param problem
	 */
	public void reportFailure(int messageCode, Exception exp, String methodName, String problem) {

		if (messageCode > 0) {

			getErrorHandler().addError(new EMFUserError(EMFErrorSeverity.ERROR, messageCode));
		}

		LogUtils.logError(methodName, problem, exp, this);
	}

	/**
	 * @deprecated utilizzare la classe ReportOperationResult Equivale al metodo della classe ReportOperationResult
	 * 
	 * @param err
	 * @param methodName
	 * @param problem
	 */
	public void reportFailure(EMFUserError err, String methodName, String problem) {

		if (err.getCode() > 0) {

			getErrorHandler().addError(err);
		}

		LogUtils.logError(methodName, problem, err, this);
	}

	protected void setCodCpi(String newCodCpi) {
		this.doc.setCodCpi(newCodCpi);
	}

	protected void setCdnLavoratore(BigDecimal newCdnLavoratore) {
		this.doc.setCdnLavoratore(newCdnLavoratore);
	}

	protected void setPrgAzienda(BigDecimal newPrgAzienda) {
		this.doc.setPrgAzienda(newPrgAzienda);
	}

	protected void setPrgUnita(BigDecimal newPrgUnita) {
		this.doc.setPrgUnita(newPrgUnita);
	}

	protected void setCodTipoDocumento(String newCodTipoDocumento) {
		this.doc.setCodTipoDocumento(newCodTipoDocumento);
	}

	protected void setFlgAutocertificazione(String newFlgAutocertificazione) {
		this.doc.setFlgAutocertificazione(newFlgAutocertificazione);
	}

	protected void setStrDescrizione(String newStrDescrizione) {
		this.doc.setStrDescrizione(newStrDescrizione);
	}

	protected void setFlgDocAmm(String newFlgDocAmm) {
		this.doc.setFlgDocAmm(newFlgDocAmm);
	}

	protected void setFlgDocIdentifP(String newFlgDocIdentfP) {
		this.doc.setFlgDocIdentifP(newFlgDocIdentfP);
	}

	protected void setDatInizio(String newDatInizio) {
		this.doc.setDatInizio(newDatInizio);
	}

	protected void setStrNumDoc(String newStrNumDoc) {
		this.doc.setStrNumDoc(newStrNumDoc);
	}

	protected void setStrEnteRilascio(String newStrEnteRilascio) {
		this.doc.setStrEnteRilascio(newStrEnteRilascio);
	}

	protected void setCodMonoIO(String newCodMonoIO) {
		this.doc.setCodMonoIO(newCodMonoIO);
	}

	protected void setDatAcqril(String newDatAcqril) {
		this.doc.setDatAcqril(newDatAcqril);
	}

	protected void setCodModalitaAcqril(String newCodModalitaAcqril) {
		this.doc.setCodModalitaAcqril(newCodModalitaAcqril);
	}

	protected void setCodTipoFile(String newCodTipoFile) {
		this.doc.setCodTipoFile(newCodTipoFile);
	}

	protected void setStrNomeDoc(String newStrNomeDoc) {
		this.doc.setStrNomeDoc(newStrNomeDoc);
	}

	protected void setDatFine(String newDatFine) {
		this.doc.setDatFine(newDatFine);
	}

	protected void setNumAnnoProt(BigDecimal newNumAnnoProt) {
		this.doc.setNumAnnoProt(newNumAnnoProt);
	}

	protected void setNumProtocollo(BigDecimal newNumProtocollo) {
		this.doc.setNumProtocollo(newNumProtocollo);
	}

	protected void setNumKeyLock(BigDecimal newNumKeyLock) {
		this.doc.setNumKeyLock(newNumKeyLock);
	}

	protected void setStrChiavetabella(String chiaveTabella) {
		this.doc.setChiaveTabella(chiaveTabella);
	}

	protected void setStrNote(String newStrNote) {
		this.doc.setStrNote(newStrNote);
	}

	protected void setCdnUtIns(BigDecimal newCdnUtIns) {
		this.doc.setCdnUtIns(newCdnUtIns);
	}

	protected void setCdnUtMod(BigDecimal newCdnUtMod) {
		this.doc.setCdnUtMod(newCdnUtMod);
	}

	protected void setSkipSaving(boolean newSkipSaving) {
		this.doc.setSkipSaving(newSkipSaving);
	}

	protected void setReportPath(String reportPath) {
		this.reportPath = reportPath;
	}

	protected String getReportPath() {
		return this.reportPath;
	}

	protected void setPagina(String newPagina) {
		this.doc.setPagina(newPagina);
	}

	protected void setDatProtocollazione(String newPar) {
		this.doc.setDatProtocollazione(newPar);
	}

	/**
	 * Se la protocollazione e' richiesta allora la Map prompts viene popolata con i valori del n. di protocollo, anno
	 * protocollo, data e ora protocollo, docInOut.
	 * 
	 * @param prompts
	 *            la Map nella quale inserire le coppie ( nome-parametro, valore ) del prompt field.
	 * @param request
	 * @return la stessa Map prompts ricevuta come parametro
	 * @throws EMFUserError
	 *             se viene richiesta la protocollazione (salvaDB=true) e almeno uno dei parametri richiesti e' assente.
	 */
	protected Map addPromptFieldsProtocollazione(Map prompts, SourceBean request) throws EMFUserError {
		String numProt = Utils.notNull(request.getAttribute("numProt"));
		String numAnno = Utils.notNull(request.getAttribute("annoProt"));
		String dataProt = Utils.notNull(request.getAttribute("dataOraProt"));
		String docInOut = Utils.notNull(request.getAttribute("docInOut"));
		// SE LA DATA DI PROTOCOLLAZIONE E' VALORIZZATA ALLORA STIAMO
		// PROTOCOLLANDO IL DOCUMENTO
		boolean protocollare = !"".equals(dataProt);
		if (/* !numProt.equals("") && ! numAnno.equals("") */protocollare) {
			if (dataProt.equals("") || docInOut.equals(""))
				throw new EMFUserError(EMFErrorSeverity.ERROR, MessageCodes.Protocollazione.ERR_PARAMETRO_ASSENTE);
			String descrizioneInOut;
			if (docInOut.equalsIgnoreCase("I"))
				descrizioneInOut = "Input";
			else
				descrizioneInOut = "Output";
			prompts.put("numProt", numProt);
			prompts.put("numAnnoProt", numAnno);
			prompts.put("dataProt", dataProt);
			prompts.put("docInOut", descrizioneInOut);
		}
		return prompts;
	}

	protected Documento getDocumento() {
		return this.doc;
	}

}