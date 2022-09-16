package it.eng.sil.module.sap;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.SessionContainer;
import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.base.SourceBeanException;
import com.engiweb.framework.dispatching.module.AbstractHttpModule;
import com.engiweb.framework.error.EMFErrorSeverity;
import com.engiweb.framework.error.EMFUserError;
import com.engiweb.framework.util.QueryExecutor;
import com.jspsmart.upload.Request;
import com.jspsmart.upload.SmartUpload;
import com.jspsmart.upload.SmartUploadException;

import it.eng.afExt.utils.MessageCodes;
import it.eng.sil.Values;
import it.eng.sil.bean.LavoratoreBean;
import it.eng.sil.security.User;

public class ImportaSAPMassiva extends AbstractHttpModule {

	private static final long serialVersionUID = -1706689475208344449L;

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(ImportaSAPMassiva.class.getName());
	private String className = this.getClass().getName();
	private HttpServletRequest httpRequest = null;
	private HttpServletResponse httpResponse = null;
	private ServletConfig servletConfig = null;
	private Request jspRequest = null;
	private SmartUpload mySmartUpload = null;
	private java.io.File file = null;
	private String nomeFile = null;
	private String xmlSAP = null;
	private List<String> listaSAP = null;
	private List<String> nomeFileSAP = null;

	// Metodo per inizializzare lo SmartUpload che consente il recupero dei dati dalla request
	private void initialize() throws ServletException, IOException, SmartUploadException {
		httpRequest = this.getHttpRequest();
		httpResponse = this.getHttpResponse();
		servletConfig = this.getServletConfig();

		mySmartUpload = new SmartUpload();
		mySmartUpload.initialize(servletConfig, httpRequest, httpResponse);
		mySmartUpload.upload();
		jspRequest = mySmartUpload.getRequest();
	}

	public void service(SourceBean request, SourceBean response) throws SourceBeanException {
		// Inizializzazione e recupero dati dalla request. Recupera il primo file passato come parametro.
		try {
			initialize();
			getUploadedFile();
			if (this.file == null) {
				throw new FileNotFoundException("Impossibile procedere con l'importazione della sap");
			}
		} catch (Exception e) {
			this.getErrorHandler()
					.addError(new EMFUserError(EMFErrorSeverity.ERROR, MessageCodes.ImportaSAP.ERR_NOME_FILE));
			it.eng.sil.util.TraceWrapper.debug(_logger, className + "::service", e);
			return;
		}
		BigDecimal progressivo = null;
		try {
			// Estrazione cdnFunzione e settaggio in response
			response.setAttribute("CDNFUNZIONE", getAttributeAsString("cdnFunzione"));
			RequestContainer requestContainer = getRequestContainer();
			SessionContainer sessionContainer = requestContainer.getSessionContainer();
			User user = (User) sessionContainer.getAttribute(User.USERID);
			BigDecimal userID = new BigDecimal(user.getCodut());
			boolean isOKMaster = false;
			boolean isSap2 = true;
			// BigDecimal sap2 =
			// getResponseContainer().getServiceResponse().containsAttribute("M_CheckSap2.ROWS.ROW.datediff")?
			// (BigDecimal)
			// getResponseContainer().getServiceResponse().getAttribute("M_CheckSap2.ROWS.ROW.datediff"):null;
			// if(sap2!= null && sap2.intValue()>=0){
			// isSap2= true;
			// }

			String strValoreConfig = getResponseContainer().getServiceResponse()
					.containsAttribute("M_CONFIG_IMPORTAZIONE_SAP.ROWS.ROW.STRVALORE")
							? getResponseContainer().getServiceResponse()
									.getAttribute("M_CONFIG_IMPORTAZIONE_SAP.ROWS.ROW.STRVALORE").toString()
							: "";

			if (listaSAP != null && listaSAP.size() > 0) {
				try {
					progressivo = getProgressivoImportazione();
					masterRisultatoImportazione(progressivo, listaSAP.size(), userID);
					isOKMaster = true;
					response.setAttribute("PROGRESSIVO", progressivo);
				} catch (Exception eR) {
					it.eng.sil.util.TraceWrapper.debug(_logger, className + "::service", eR);
				}
				for (int i = 0; i < listaSAP.size(); i++) {
					xmlSAP = listaSAP.get(i);
					ImportaSAP importer = new ImportaSAP();
					importer.settaParametri(strValoreConfig);
					LavoratoreBean lav = importer.importa(isSap2, request, response, requestContainer, user, xmlSAP);
					if (isOKMaster) {
						if (lav != null) {
							detailRisultatoImportazione(progressivo, lav, importer, userID);
						} else {
							detailErroreGenericoRisultatoImportazione(progressivo, importer, userID,
									nomeFileSAP.get(i));
						}
					}
				}
			} else {
				try {
					progressivo = getProgressivoImportazione();
					masterRisultatoImportazione(progressivo, 1, userID);
					isOKMaster = true;
				} catch (Exception eR) {
					it.eng.sil.util.TraceWrapper.debug(_logger, className + "::service", eR);
				}
				ImportaSAP importer = new ImportaSAP();
				importer.settaParametri(strValoreConfig);
				LavoratoreBean lav = importer.importa(isSap2, request, response, requestContainer, user, xmlSAP);
				if (isOKMaster) {
					if (lav != null) {
						detailRisultatoImportazione(progressivo, lav, importer, userID);
					} else {
						detailErroreGenericoRisultatoImportazione(progressivo, importer, userID, nomeFileSAP.get(0));
					}
					response.setAttribute("PROGRESSIVO", progressivo);
				}
			}
		} catch (Exception e) {
			this.getErrorHandler()
					.addError(new EMFUserError(EMFErrorSeverity.ERROR, MessageCodes.General.OPERATION_FAIL));
			it.eng.sil.util.TraceWrapper.debug(_logger, className + "::service", e);
			return;
		}
	}

	private String getAttributeAsString(String param) {
		return (String) jspRequest.getParameter(param);
	}

	private void getUploadedFile() throws IOException, SmartUploadException {
		com.jspsmart.upload.File myFile = mySmartUpload.getFiles().getFile(0);
		if (!myFile.isMissing()) {
			// verifico che l'estensione sia xml
			if (!"xml".equalsIgnoreCase(myFile.getFileExt()) && !"zip".equalsIgnoreCase(myFile.getFileExt())) {
				throw new IOException("Formato file non valido");
			}

			nomeFile = myFile.getFileName();
			file = java.io.File.createTempFile("MOBTEMP", null, null);
			myFile.saveAs(file.getAbsolutePath(), SmartUpload.SAVE_PHYSICAL);

			if ("zip".equalsIgnoreCase(myFile.getFileExt())) {
				ZipFile fileZip = new ZipFile(file.getAbsolutePath());
				ZipInputStream zis = new ZipInputStream(new FileInputStream(file.getAbsolutePath()));
				ZipEntry entry = null;
				listaSAP = new ArrayList<String>();
				nomeFileSAP = new ArrayList<String>();
				while ((entry = zis.getNextEntry()) != null) {
					if (!entry.isDirectory()) {
						StringBuffer s = new StringBuffer();
						InputStream is = fileZip.getInputStream(entry);
						BufferedReader br = null;
						StringBuilder sb = new StringBuilder();
						String line;

						br = new BufferedReader(new InputStreamReader(is));
						while ((line = br.readLine()) != null) {
							sb.append(line);
						}
						listaSAP.add(sb.toString());
						nomeFileSAP.add(entry.getName());
					}
				}
			} else {
				StringBuffer s = new StringBuffer();
				char[] buff = new char[1024];
				FileReader fr = new FileReader(file.getAbsoluteFile());
				int n = 0;
				while ((n = fr.read(buff)) > 0) {
					s.append(buff, 0, n);
				}
				fr.close();
				nomeFileSAP = new ArrayList<String>();
				xmlSAP = s.toString();
				nomeFileSAP.add(nomeFile);
			}
		}
	}

	private BigDecimal getProgressivoImportazione() throws Exception {
		SourceBean row = null;
		row = (SourceBean) QueryExecutor.executeQuery("NEXT_S_AM_SP_IMPORTAZIONE", null, "SELECT", Values.DB_SIL_DATI);
		if (row == null)
			throw new Exception("impossibile recuperare il progressivo importazione");
		row = row.containsAttribute("ROW") ? (SourceBean) row.getAttribute("ROW") : row;
		return (BigDecimal) row.getAttribute("KEY");
	}

	private void masterRisultatoImportazione(BigDecimal progressivo, int numFile, BigDecimal userID) throws Exception {
		Object[] params = new Object[4];
		params[0] = progressivo;
		params[1] = numFile;
		params[2] = nomeFile;
		params[3] = userID;
		Boolean res = (Boolean) QueryExecutor.executeQuery("INS_AM_SP_IMPORTAZIONE", params, "INSERT",
				Values.DB_SIL_DATI);
		if (!res.booleanValue())
			throw new Exception("Impossibile creare il risultato dell'importazione sap da file");
	}

	private void detailRisultatoImportazione(BigDecimal progressivo, LavoratoreBean lav, ImportaSAP importer,
			BigDecimal userID) throws Exception {
		String msgErrore = importer.getMsgErrCode();
		if (msgErrore != null && msgErrore.length() > 2000) {
			msgErrore = msgErrore.substring(0, 2000);
		}
		Object[] params = new Object[11];
		params[0] = progressivo;
		params[1] = userID;
		params[2] = importer.getCodErrore();
		params[3] = importer.getFlgAnagraficaPresente();
		params[4] = importer.getFlgDidPresente();
		params[5] = lav.getNumEsperienzeLavoro();
		params[6] = lav.getNumEsperienzeLavoroImportate();
		params[7] = lav.getNumEsperienzeLavoro() - lav.getNumEsperienzeLavoroImportate();
		params[8] = msgErrore;
		params[9] = lav.getCodiceFiscale();
		params[10] = lav.geteEsitoDatiAmministrativi();
		Boolean res = (Boolean) QueryExecutor.executeQuery("INS_AM_SP_IMPORTAZIONE_DETAIL", params, "INSERT",
				Values.DB_SIL_DATI);
		if (!res.booleanValue())
			throw new Exception("Impossibile inserire il dettaglio dell'importazione sap da file");
	}

	private void detailErroreGenericoRisultatoImportazione(BigDecimal progressivo, ImportaSAP importer,
			BigDecimal userID, String nomeFileSAP) throws Exception {
		String msgErrore = importer.getMsgErrCode();
		if (msgErrore != null && msgErrore.length() > 2000) {
			msgErrore = msgErrore.substring(0, 2000);
		}
		if (nomeFileSAP != null && nomeFileSAP.length() > 16) {
			String[] vettNomeFile = nomeFileSAP.split("/");
			String nomeApp = vettNomeFile[vettNomeFile.length - 1];
			if (nomeApp.length() > 16) {
				nomeFileSAP = nomeApp.substring(0, 16);
			}
		}
		Object[] params = new Object[11];
		params[0] = progressivo;
		params[1] = userID;
		params[2] = importer.getCodErrore();
		params[3] = null;
		params[4] = null;
		params[5] = null;
		params[6] = null;
		params[7] = null;
		params[8] = msgErrore;
		params[9] = nomeFileSAP;
		params[10] = null;
		Boolean res = (Boolean) QueryExecutor.executeQuery("INS_AM_SP_IMPORTAZIONE_DETAIL", params, "INSERT",
				Values.DB_SIL_DATI);
		if (!res.booleanValue())
			throw new Exception("Impossibile inserire il dettaglio dell'importazione sap da file");
	}

}