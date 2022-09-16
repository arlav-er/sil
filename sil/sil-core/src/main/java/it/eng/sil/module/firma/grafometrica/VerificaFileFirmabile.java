package it.eng.sil.module.firma.grafometrica;

import java.math.BigDecimal;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.base.SourceBeanException;
import com.engiweb.framework.dispatching.module.AbstractHttpModule;

import it.eng.afExt.utils.MessageCodes;
import it.eng.afExt.utils.ReportOperationResult;
import it.eng.sil.bean.Documento;
import it.eng.sil.module.pi3.ProtocolloPi3DBManager;
import it.eng.sil.util.blen.StringUtils;

public class VerificaFileFirmabile extends AbstractHttpModule {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String className = this.getClass().getName();
	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(VerificaFileFirmabile.class.getName());

	@Override
	public void service(SourceBean serviceRequest, SourceBean serviceResponse) throws Exception {

		ReportOperationResult reportOperation = new ReportOperationResult(this, serviceResponse);

		ProtocolloPi3DBManager dbManager = new ProtocolloPi3DBManager();

		String prgDocumento = (String) serviceRequest.getAttribute("PRGDOCUMENTO");
		String docType = (String) serviceRequest.getAttribute("DOCUMENTTYPE");

		Documento documentoStampa = null;
		BigDecimal prgTemplateStampa = null;
		if (!StringUtils.isEmpty(prgDocumento)) {
			documentoStampa = new Documento();
			documentoStampa.setPrgDocumento(new BigDecimal(prgDocumento));
			documentoStampa.selectStampaParam();
			prgTemplateStampa = documentoStampa.getPrgTemplateStampa();
		}

		try {

			if (prgTemplateStampa == null) {
				prgTemplateStampa = dbManager.getPrgTemplateStampa(docType);
			}

			boolean isDocumentFirmabile = false;

			if (prgTemplateStampa != null) {
				isDocumentFirmabile = dbManager.isDocumentTypeGraphSignature(prgTemplateStampa.toString());

				if (isDocumentFirmabile) {
					serviceResponse.setAttribute("FLGFIRMAGRAFO", "S");
				} else {
					serviceResponse.setAttribute("FLGFIRMAGRAFO", "N");
				}
			} else {
				serviceResponse.setAttribute("FLGFIRMAGRAFO", "N");
			}

		} catch (SourceBeanException e) {
			reportOperation.reportFailure(MessageCodes.General.OPERATION_FAIL, e, "service", "");
			e.printStackTrace();
		} catch (Exception e) {
			reportOperation.reportFailure(MessageCodes.General.OPERATION_FAIL, e, "service", "");
			e.printStackTrace();
		}

	}

}
