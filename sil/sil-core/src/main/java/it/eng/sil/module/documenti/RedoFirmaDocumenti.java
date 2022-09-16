package it.eng.sil.module.documenti;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.math.BigDecimal;

import org.apache.commons.io.IOUtils;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.SessionContainer;
import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.dbaccess.DataConnectionManager;
import com.engiweb.framework.dbaccess.sql.DataConnection;
import com.engiweb.framework.error.EMFInternalError;

import it.eng.afExt.utils.MessageCodes;
import it.eng.afExt.utils.ReportOperationResult;
import it.eng.afExt.utils.StringUtils;
import it.eng.afExt.utils.TransactionQueryExecutor;
import it.eng.sil.bean.Documento;
import it.eng.sil.module.AbstractSimpleModule;
import it.eng.sil.module.firma.grafometrica.FirmaDocumenti;
import it.eng.sil.module.pi3.ProtocolloPi3DBManager;
import it.eng.sil.security.User;

public class RedoFirmaDocumenti extends AbstractSimpleModule {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(RedoFirmaDocumenti.class.getName());

	private final String className = StringUtils.getClassName(this);

	@Override
	public void service(SourceBean serviceRequest, SourceBean serviceResponse) throws Exception {

		ReportOperationResult reportOperation = new ReportOperationResult(this, serviceResponse);

		// Segnalazione soli errori/problemi
		int idSuccess = this.disableMessageIdSuccess();
		int idFail = this.disableMessageIdFail();
		int errorCode = MessageCodes.General.OPERATION_FAIL;

		String prgDoc = (String) serviceRequest.getAttribute("prgDocumento");
		String cdnFunzione = (String) serviceRequest.getAttribute("cdnFunzione");

		RequestContainer requestContainer = getRequestContainer();
		SessionContainer sessionContainer = (SessionContainer) requestContainer.getSessionContainer();

		ProtocolloPi3DBManager dbManager = new ProtocolloPi3DBManager();

		Documento doc = new Documento();
		doc.setPrgDocumento(new BigDecimal(prgDoc));
		try {

			setSectionQuerySelect("QUERY_IS_STAMPE_PARAM");
			SourceBean rowStampeParam = doSelect(serviceRequest, serviceResponse, false);
			if (rowStampeParam != null) {
				if (rowStampeParam.containsAttribute("ROW.prgtemplatestampa")) {
					doc.selectStampaParam();
				} else {
					doc.select();
				}
			} else {
				doc.select();
			}

			// LEGGE IL FILE E LO METTE IN TEMPFILE DI DOCUMENTO
			doc = setTempPdfFileToDocument(doc);

			// VERIFICA SE IL TIPO DI DOCUMENTO E' PREDISPOSTO PER ESSERE FIRMATO DA SISTEMA
			// BigDecimal prgTemplateStampa = dbManager.getPrgTemplateStampa(doc.getCodTipoDocumento());
			// boolean isDocumentTypeFirmabile = dbManager.isDocumentTypeGraphSignature(prgTemplateStampa.toString());

			// VERIFICA SE IL DOCUMENTO E' STATO GIA' FIRMATO GRAFOMETRICAMENTE
			// boolean isDocumentoGiaFirmato = dbManager.isAllegatoDocumentoFirmato(prgDoc.toString());

			// VERIFICA SE IL DOCUMENTO E' DI UN UTENTE LAVORATORE ED HA IL CONSENSO ATTIVO PER EFFETTUARE LA FIRMA
			/*
			 * boolean isLavoratoreConsensoAttivo = false; String codiceConsenso = ""; if(doc.getCdnLavoratore()!=null){
			 * if (!StringUtils.isEmpty(doc.getCdnLavoratore().toString())){ Consenso consenso = new Consenso(null);
			 * ConsensoFirmaBean cfb = consenso.getConsensoFirma(doc.getCdnLavoratore().toString()); codiceConsenso =
			 * cfb.getCodiceStatoConsenso(); if(!StringUtils.isEmpty(codiceConsenso) &&
			 * codiceConsenso.equals(GConstants.CONSENSO_ATTIVO_CODICE)){ isLavoratoreConsensoAttivo = true; } } }
			 */

			BigDecimal newNumklo = doc.getNumKloDocumento();
			newNumklo = newNumklo.add(new BigDecimal("1"));
			doc.setNumKloDocumento(newNumklo);

			String ipOperatore = it.eng.afExt.utils.StringUtils.getAttributeStrNotNull(serviceRequest, "ipOperatore");
			User user = (User) sessionContainer.getAttribute(User.USERID);

			FirmaDocumenti firma = new FirmaDocumenti();
			boolean esitoFirma = firma.firmaDocumento(serviceRequest, serviceResponse, user, null, doc, ipOperatore);
			_logger.info("RedoFirma: ***esitoFirma*** " + esitoFirma);

			if (!esitoFirma) {
				reportOperation.reportFailure(MessageCodes.FirmaGrafometrica.WS_OPERATION_FAIL);
			} else {

				// ConvertToPdf convertToPdf = new ConvertToPdf();

				// OutputStream osInf = new FileOutputStream(doc.getTempFile());
				// OutputStream osOutf = convertToPdf.createPDF("<p>Firma Grafometrica</p>", osInf,false);
				ByteArrayOutputStream decryptedByteOutputStream = new ByteArrayOutputStream();
				IOUtils.copy(new FileInputStream(doc.getTempFile()), decryptedByteOutputStream);

				decryptedByteOutputStream.flush();
				decryptedByteOutputStream.close();

				TransactionQueryExecutor txBlobFirma = null;

				try {
					txBlobFirma = new TransactionQueryExecutor(getPool());
					txBlobFirma.initTransaction();

					doc.inserisciBlob(txBlobFirma);
					doc.update(txBlobFirma);
					txBlobFirma.commitTransaction();

					serviceResponse.setAttribute("cdnFunzione", prgDoc);
					serviceResponse.setAttribute("prgDocumento", prgDoc);

				} catch (Throwable ex) {
					if (txBlobFirma != null) {
						try {
							txBlobFirma.rollBackTransaction();
						} catch (EMFInternalError e) {
							e.printStackTrace();
						}
					}
					_logger.error(ex.getMessage());
					reportOperation.reportFailure(errorCode);
				}
			}

		} catch (Throwable ex) {
			_logger.error(ex.getMessage());
			reportOperation.reportFailure(MessageCodes.FirmaGrafometrica.WS_NON_RAGGIUNGIBILE);
		}
	}

	private Documento setTempPdfFileToDocument(Documento documento) throws Exception {

		boolean successRename = false;

		DataConnection dc = null;
		String pool = (String) getConfig().getAttribute("POOL");
		DataConnectionManager dcm = DataConnectionManager.getInstance();
		dc = dcm.getConnection(pool);

		documento.readBLOB(dc);

		File outFile = documento.getTempFile();

		if (outFile.exists()) {
			int index = outFile.getAbsolutePath().lastIndexOf(".");
			String nameFileOut = outFile.getAbsolutePath().substring(0, index);
			File filePdf = new File(nameFileOut + ".pdf");
			successRename = outFile.renameTo(filePdf);

			if (successRename) {
				documento.setTempFile(filePdf);
			}
		}

		dc.close();

		return documento;

	}

}
