package it.eng.sil.module.sap;

import java.util.Vector;

import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.utils.MessageCodes;
import it.eng.afExt.utils.ReportOperationResult;
import it.eng.afExt.utils.StringUtils;

public class InsertLingue extends InsertSAP {
	private static final long serialVersionUID = 1L;

	@Override
	public void service(SourceBean request, SourceBean response) {
		try {
			String strNumLingue = ((String) request.getAttribute("numLingue"));
			String check = "";
			ReportOperationResult reportOperation = new ReportOperationResult(this, response);
			this.disableMessageIdSuccess();
			this.disableMessageIdFail();
			this.disableMessageIdElementDuplicate();

			if ((strNumLingue != null && !strNumLingue.equals(""))) {
				int numLingue = new Integer(strNumLingue).intValue();
				for (int i = 0; i < numLingue; i++) {
					check = (String) request.getAttribute(SAP_SEZ_LINGUE + "_chkImporta_" + i);
					if (check != null && check.equals("on")) {

						request.delAttribute("codLingua");
						request.setAttribute("codLingua",
								StringUtils.getAttributeStrNotNull(request, SAP_SEZ_LINGUE + "_codLingua_" + i));
						request.delAttribute("codModlingua");
						request.setAttribute("codModlingua",
								StringUtils.getAttributeStrNotNull(request, SAP_SEZ_LINGUE + "_codModlingua_" + i));
						request.delAttribute("cdnGradoLetto");
						request.setAttribute("cdnGradoLetto", getGradoLingua(
								StringUtils.getAttributeStrNotNull(request, SAP_SEZ_LINGUE + "_cdnGradoLetto_" + i)));
						request.delAttribute("cdnGradoScritto");
						request.setAttribute("cdnGradoScritto", getGradoLingua(
								StringUtils.getAttributeStrNotNull(request, SAP_SEZ_LINGUE + "_cdnGradoScritto_" + i)));
						request.delAttribute("cdnGradoParlato");
						request.setAttribute("cdnGradoParlato", getGradoLingua(
								StringUtils.getAttributeStrNotNull(request, SAP_SEZ_LINGUE + "_cdnGradoParlato_" + i)));
						request.delAttribute("flgCertificato");
						request.setAttribute("flgCertificato",
								StringUtils.getAttributeStrNotNull(request, SAP_SEZ_LINGUE + "_flgCertificato_" + i));

						Vector<String> params = new Vector<String>(1);
						params.add(StringUtils.getAttributeStrNotNull(request, SAP_SEZ_LINGUE + "_descrLingua_" + i));

						if (doInsertNoDuplicate(request, response)) {
							reportOperation.reportWarning(MessageCodes.ImportaSAP.INSERT_LINGUA_LAV_OK, "service",
									"Lingua correttamente importata", params);
						} else {
							reportOperation.reportWarning(MessageCodes.ImportaSAP.INSERT_LINGUA_LAV_FAIL, "service",
									"Lingua già presente", params);
						}
					}
				}
			}
		} catch (Exception e) {
			_logger.error(e);
		}
	}
}