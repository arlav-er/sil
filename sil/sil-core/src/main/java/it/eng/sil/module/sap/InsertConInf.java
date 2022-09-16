package it.eng.sil.module.sap;

import java.util.Vector;

import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.utils.MessageCodes;
import it.eng.afExt.utils.ReportOperationResult;
import it.eng.afExt.utils.StringUtils;

public class InsertConInf extends InsertSAP {
	private static final long serialVersionUID = 1L;

	@Override
	public void service(SourceBean request, SourceBean response) {
		try {
			String strNumConInfo = ((String) request.getAttribute("numConInfo"));
			String check = "";
			ReportOperationResult reportOperation = new ReportOperationResult(this, response);
			this.disableMessageIdSuccess();
			this.disableMessageIdFail();
			this.disableMessageIdElementDuplicate();

			if ((strNumConInfo != null && !strNumConInfo.equals(""))) {
				int numConInfo = new Integer(strNumConInfo).intValue();
				for (int i = 0; i < numConInfo; i++) {
					check = (String) request.getAttribute(SAP_SEZ_CON_INF + "_chkImporta_" + i);
					if (check != null && check.equals("on")) {
						request.delAttribute("codTipoInfo");
						request.setAttribute("codTipoInfo",
								StringUtils.getAttributeStrNotNull(request, SAP_SEZ_CON_INF + "_codTipoInfo_" + i));
						request.delAttribute("codDettInfo");
						request.setAttribute("codDettInfo",
								StringUtils.getAttributeStrNotNull(request, SAP_SEZ_CON_INF + "_codDettInfo_" + i));
						request.delAttribute("cdnGrado");
						request.setAttribute("cdnGrado",
								StringUtils.getAttributeStrNotNull(request, SAP_SEZ_CON_INF + "_cdnGrado_" + i));
						request.delAttribute("strDescInfo");
						request.setAttribute("strDescInfo",
								StringUtils.getAttributeStrNotNull(request, SAP_SEZ_CON_INF + "_strDescInfo_" + i));

						Vector<String> params = new Vector<String>(1);
						params.add(
								StringUtils.getAttributeStrNotNull(request, SAP_SEZ_CON_INF + "_descrDettInfo_" + i));

						if (doInsertNoDuplicate(request, response)) {
							reportOperation.reportWarning(MessageCodes.ImportaSAP.INSERT_CON_INFO_LAV_OK, "service",
									"Conoscenza Informatica correttamente importato", params);
						} else {
							reportOperation.reportWarning(MessageCodes.ImportaSAP.INSERT_CON_INFO_LAV_FAIL, "service",
									"Conoscenza Informatica già presente", params);
						}
					}
				}
			}
		} catch (Exception e) {
			_logger.error(e);
		}
	}
}