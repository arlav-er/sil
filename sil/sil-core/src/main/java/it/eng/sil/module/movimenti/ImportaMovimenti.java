package it.eng.sil.module.movimenti;

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
import it.eng.sil.module.movimenti.processors.IdentificaMovimento;
import it.eng.sil.module.movimenti.processors.InsertAgevolazioniApp;
import it.eng.sil.module.movimenti.processors.InsertAvvPerCVE;
import it.eng.sil.module.movimenti.processors.InsertData;
import it.eng.sil.module.movimenti.processors.SelectFieldsMovimentoAppoggio;
import it.eng.sil.module.movimenti.processors.TransformCodTempo;

/**
 * Classe del modulo che implementa l'importazione dei movimenti da file esterni. Il file viene allegato alla request
 * con una form di tipo "multipart/form-data" e deve essere recuperato in maniera particolare. Utilizza la classe
 * Importer per effettuare l'estrazione e l'elaborazione dei record dal file sorgente.
 * <p>
 * 
 * @author Paolo Roccetti
 */
public class ImportaMovimenti extends AbstractHttpModule {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(ImportaMovimenti.class.getName());

	private String className = this.getClass().getName();
	private String pool = null;
	private HttpServletRequest httpRequest = null;
	private HttpServletResponse httpResponse = null;
	private ServletConfig servletConfig = null;
	private Request jspRequest = null;
	private SmartUpload mySmartUpload = null;
	private com.jspsmart.upload.File myFile = null;
	private String filename = null;
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
	 * Servizio che estrae da un file TXT i record dei movimenti e popola la tabella AM_MOVIMENTO_APPOGGIO del database.
	 * Il file viene estratto dalla request.
	 */
	public void service(SourceBean request, SourceBean response) throws SourceBeanException {

		// Inizializzazione e recupero dati dalla request. Recupera il primo
		// file passato come parametro.
		try {
			initialize();
			getUploadedFile();
			if (this.file == null) {
				throw new FileNotFoundException("Impossibile importare i movimenti indicati");
			}
		} catch (Exception e) {
			this.getErrorHandler()
					.addError(new EMFUserError(EMFErrorSeverity.ERROR, MessageCodes.ImportMov.ERR_NOME_FILE));
			it.eng.sil.util.TraceWrapper.debug(_logger, className + "::service", e);

			return;
		}
		// recupero nome del pool di connessioni
		pool = (String) getConfig().getAttribute("POOL");

		// Estrazione versione
		String version = getAttributeAsString("version");

		// Estrazione cdnFunzione e settaggio in response
		response.setAttribute("CDNFUNZIONE", getAttributeAsString("cdnFunzione"));

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
		try {
			extr = new TXTExtractor(new java.io.FileInputStream(file), configbase + "TXTRecordMapping.xml", version);
		} catch (Exception e) {
			it.eng.sil.util.TraceWrapper.debug(_logger, "Impossibile istanziare l'estrattore per i record. ", e);

			// chiudo la connessione del MultipleTransactionQueryExecutor
			trans.closeConnection();
			return;
		}

		// Istanzio e configuro i processori
		RecordProcessor procCodTempo = new TransformCodTempo("Trasforma_Tempo_Avviamento");
		RecordProcessor selectCorrectFields = new SelectFieldsMovimentoAppoggio("Selezione_Dati_Movimento",
				"importazione_file_txt", trans);
		// RecordProcessor selectCodContr = new
		// SelectCodContratto("Seleziona_Codice_Contratto", dataconn);
		String configproc = configbase + "processors" + File.separator;
		RecordProcessor insertMovApp;
		RecordProcessor insertAvvPerCve;
		RecordProcessor insertBenefici;
		boolean avviamentoCPTVE = false;
		try {
			insertMovApp = new InsertData("Inserisci_Movimento_Appoggio", trans,
					configproc + "InsertAM_MOVIMENTI_APPOGGIO.xml", "INSERT_MOVIMENTO_APPOGGIO",
					(BigDecimal) getRequestContainer().getSessionContainer().getAttribute("_CDUT_"));
			insertBenefici = new InsertAgevolazioniApp("Inserisci_Benefici_Appoggio", trans, avviamentoCPTVE);
			insertAvvPerCve = new InsertAvvPerCVE("Inserisci_Avviamento_per_CVE", trans,
					configproc + "insertMovimentoAppPerCVE.xml", "INSERT_AVVIAMENTO_PER_CVE",
					(BigDecimal) getRequestContainer().getSessionContainer().getAttribute("_CDUT_"));
		} catch (Exception e) {
			it.eng.sil.util.TraceWrapper.debug(_logger, "Impossibile istanziare il processore per i record. ", e);

			// chiudo la connessione del MultipleTransactionQueryExecutor
			trans.closeConnection();
			return;
		}

		// Li aggiungo all'importer
		importer.addProcessor(new IdentificaMovimento());
		importer.addProcessor(procCodTempo);
		importer.addProcessor(selectCorrectFields);
		// importer.addProcessor(selectCodContr);
		importer.addProcessor(insertAvvPerCve);
		importer.addProcessor(insertMovApp);
		importer.addProcessor(insertBenefici);

		// Imposto i dati necessari nell'importer
		importer.setIdRecordKey("PRGMOVIMENTOAPP");
		importer.setRecordExtractor(extr);

		// Processo i records e poi aggiungo il risultato alla response del
		// modulo
		SourceBean result = importer.importRecords(trans);
		response.setAttribute(result);

		// chiudo la connessione del MultipleTransactionQueryExecutor
		trans.closeConnection();
	}

	private BigDecimal getAttributeAsBigDecimal(String param) {
		String tmp = (String) jspRequest.getParameter(param);
		if ((tmp != null) && (!tmp.equals(""))) {

			return new BigDecimal(tmp);

		}
		return null;

	}

	private String getAttributeAsString(String param) {
		return (String) jspRequest.getParameter(param);
	}

	private void getUploadedFile() throws IOException, SmartUploadException {

		com.jspsmart.upload.File myFile = mySmartUpload.getFiles().getFile(0);

		if (!myFile.isMissing()) {

			filename = myFile.getFileName();

			file = File.createTempFile("MOVTEMP", null, null);

			myFile.saveAs(file.getAbsolutePath(), SmartUpload.SAVE_PHYSICAL);
		}
	}
}