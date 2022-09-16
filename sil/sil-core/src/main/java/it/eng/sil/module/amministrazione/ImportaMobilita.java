package it.eng.sil.module.amministrazione;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.base.SourceBeanException;
import com.engiweb.framework.configuration.ConfigSingleton;
import com.engiweb.framework.dispatching.module.AbstractHttpModule;
import com.engiweb.framework.error.EMFErrorSeverity;
import com.engiweb.framework.error.EMFUserError;
import com.jspsmart.upload.Request;
import com.jspsmart.upload.SmartUpload;
import com.jspsmart.upload.SmartUploadException;

import it.eng.afExt.utils.MessageCodes;
import it.eng.sil.module.movimenti.Importer;
import it.eng.sil.module.movimenti.MultipleTransactionQueryExecutor;
import it.eng.sil.module.movimenti.RecordExtractor;
import it.eng.sil.module.movimenti.RecordProcessor;
import it.eng.sil.module.movimenti.TXTExtractor;
import it.eng.sil.module.movimenti.processors.IdentificaMobilita;
import it.eng.sil.module.movimenti.processors.InsertData;

/**
 * Classe del modulo che implementa l'importazione di mobilita da file esterni. Il file viene allegato alla request con
 * una form di tipo "multipart/form-data" e deve essere recuperato in maniera particolare. Utilizza la classe Importer
 * per effettuare l'estrazione e l'elaborazione dei record dal file sorgente.
 */

public class ImportaMobilita extends AbstractHttpModule {

	private static final long serialVersionUID = -1706689475208344449L;

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(ImportaMobilita.class.getName());
	private String className = this.getClass().getName();
	private HttpServletRequest httpRequest = null;
	private HttpServletResponse httpResponse = null;
	private ServletConfig servletConfig = null;
	private Request jspRequest = null;
	private SmartUpload mySmartUpload = null;
	private java.io.File file = null;

	// Metodo per inizializzare lo SmartUpload che consente il recupero dei dati
	// dalla request
	private void initialize() throws ServletException, IOException, SmartUploadException {
		httpRequest = this.getHttpRequest();
		httpResponse = this.getHttpResponse();
		servletConfig = this.getServletConfig();

		mySmartUpload = new SmartUpload();
		mySmartUpload.initialize(servletConfig, httpRequest, httpResponse);
		mySmartUpload.upload();
		jspRequest = mySmartUpload.getRequest();
	}

	/**
	 * Servizio che estrae da un file TXT o da un file DBF i record delle mobilità e popola la tabella
	 * AM_MOBILITA_ISCR_APP o la tabella AM_MOB_ISCR_DAENTE_BK del database. Il file viene estratto dalla request.
	 */
	public void service(SourceBean request, SourceBean response) throws SourceBeanException {
		// Inizializzazione e recupero dati dalla request. Recupera il primo
		// file passato come parametro.
		try {
			initialize();
			getUploadedFile();
			if (this.file == null) {
				throw new FileNotFoundException("Impossibile importare le mobilità indicate");
			}
		} catch (Exception e) {
			this.getErrorHandler()
					.addError(new EMFUserError(EMFErrorSeverity.ERROR, MessageCodes.ImportMov.ERR_NOME_FILE));
			it.eng.sil.util.TraceWrapper.debug(_logger, className + "::service", e);

			return;
		}

		// Estrazione versione
		String version = getAttributeAsString("version");
		// Estrazione cdnFunzione e settaggio in response
		response.setAttribute("CDNFUNZIONE", getAttributeAsString("cdnFunzione"));
		// String tipoFile = jspRequest.getParameter("tipoFile");
		String tipoFile = "PROV";
		// MultipleTransactionQueryExecutor da utilizzare
		MultipleTransactionQueryExecutor trans = null;
		try {
			trans = new MultipleTransactionQueryExecutor((String) getConfig().getAttribute("POOL"));
		} catch (Exception error) {
			it.eng.sil.util.TraceWrapper.debug(_logger,
					"Impossibile istanziare il MultipleTransactionQueryExecutor per importare i dati sul DB. ", error);

			return;
		}
		// Istanzio l'importatore
		Importer importer = new Importer();
		// Creo il nome del file di configurazione per l'estrattore
		String configbase = ConfigSingleton.getRootPath() + File.separator + "WEB-INF" + File.separator + "conf"
				+ File.separator + "import" + File.separator;
		// Istanzio l'estrattore
		RecordExtractor extr;
		// Configuro i processori
		String configproc = configbase + "processors" + File.separator;
		if (tipoFile.equalsIgnoreCase("PROV")) {
			// importazione file provinciale
			try {
				extr = new TXTExtractor(new java.io.FileInputStream(file), configbase + "TXTMOBILITARecordMapping.xml",
						version);
			} catch (Exception e) {
				it.eng.sil.util.TraceWrapper.debug(_logger, "Impossibile istanziare l'estrattore per i record. ", e);

				// chiudo la connessione del MultipleTransactionQueryExecutor
				trans.closeConnection();
				return;
			}
			RecordProcessor insertMobilitaApp;
			try {
				insertMobilitaApp = new InsertData("Inserisci_Mobilita_Appoggio", trans,
						configproc + "InsertAM_MOBILITA_APPOGGIO.xml", "INSERT_MOBILITA_APPOGGIO",
						(BigDecimal) getRequestContainer().getSessionContainer().getAttribute("_CDUT_"));
			} catch (Exception e) {
				it.eng.sil.util.TraceWrapper.debug(_logger, "Impossibile istanziare il processore per i record. ", e);

				// chiudo la connessione del MultipleTransactionQueryExecutor
				trans.closeConnection();
				return;
			}
			// Li aggiungo all'importer
			importer.addProcessor(new IdentificaMobilita());
			importer.addProcessor(insertMobilitaApp);
			// Imposto i dati necessari nell'importer
			importer.setIdRecordKey("PRGMOBILITAISCRAPP");
			importer.setRecordExtractor(extr);
			// Processo i records e poi aggiungo il risultato alla response del
			// modulo
			SourceBean result = importer.importRecords(trans);
			response.setAttribute(result);
		}
		// chiudo la connessione del MultipleTransactionQueryExecutor
		trans.closeConnection();
	}

	private String getAttributeAsString(String param) {
		return (String) jspRequest.getParameter(param);
	}

	private void getUploadedFile() throws IOException, SmartUploadException {
		com.jspsmart.upload.File myFile = mySmartUpload.getFiles().getFile(0);

		if (!myFile.isMissing()) {

			// verifico che l'estensione sia txt
			if (!"txt".equalsIgnoreCase(myFile.getFileExt())) {
				throw new IOException("Formato file non valido");
			}

			myFile.getFileName();
			file = File.createTempFile("MOBTEMP", null, null);
			myFile.saveAs(file.getAbsolutePath(), SmartUpload.SAVE_PHYSICAL);
		}
	}

}