package it.eng.sil.module.sap;

import java.util.Vector;

import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.utils.MessageCodes;
import it.eng.afExt.utils.ReportOperationResult;
import it.eng.afExt.utils.StringUtils;

@SuppressWarnings("serial")
public class InsertForPro extends InsertSAP {

	@Override
	public void service(SourceBean request, SourceBean response) {
		try {
			String strNumCorsi = ((String) request.getAttribute("numCorsi"));
			String check = "";
			ReportOperationResult reportOperation = new ReportOperationResult(this, response);
			this.disableMessageIdSuccess();
			this.disableMessageIdFail();
			this.disableMessageIdElementDuplicate();

			if ((strNumCorsi != null && !strNumCorsi.equals(""))) {
				int numCorsi = new Integer(strNumCorsi).intValue();
				for (int i = 0; i < numCorsi; i++) {
					check = (String) request.getAttribute(SAP_SEZ_FOR_PRO + "_chkImporta_" + i);
					if (check != null && check.equals("on")) {
						String descrCorso = StringUtils.getAttributeStrNotNull(request,
								SAP_SEZ_FOR_PRO + "_strDescrizione_" + i);
						request.delAttribute("strDescrizione");
						request.setAttribute("strDescrizione", descrCorso);

						String codCorso = StringUtils.getAttributeStrNotNull(request,
								SAP_SEZ_FOR_PRO + "_codCorso_" + i);
						request.delAttribute("codCorso");
						request.setAttribute("codCorso", codCorso);

						request.delAttribute("numAnno");
						request.setAttribute("numAnno",
								StringUtils.getAttributeStrNotNull(request, SAP_SEZ_FOR_PRO + "_numAnno_" + i));

						request.delAttribute("strContenuto");
						request.setAttribute("strContenuto",
								StringUtils.getAttributeStrNotNull(request, SAP_SEZ_FOR_PRO + "_strContenuto_" + i));

						request.delAttribute("strEnte");
						request.setAttribute("strEnte",
								StringUtils.getAttributeStrNotNull(request, SAP_SEZ_FOR_PRO + "_strEnte_" + i));

						request.delAttribute("flgCompletato");
						request.setAttribute("flgCompletato",
								StringUtils.getAttributeStrNotNull(request, SAP_SEZ_FOR_PRO + "_flgCompletato_" + i));

						Vector<String> params = new Vector<String>(1);
						params.add(descrCorso);

						boolean isElemEsistente = false;
						if (!"".equals(codCorso)) {
							SourceBean beanSelect = doSelect(request, response, false);
							Vector vect = beanSelect.getAttributeAsVector("ROW");
							isElemEsistente = (beanSelect != null) && (vect.size() > 0);
						}

						if (isElemEsistente) {
							reportOperation.reportWarning(MessageCodes.ImportaSAP.INSERT_FOR_PRO_LAV_FAIL, "service",
									"Corso già presente", params);
						} else {
							doInsert(request, response);
							reportOperation.reportWarning(MessageCodes.ImportaSAP.INSERT_FOR_PRO_LAV_OK, "service",
									"Corso correttamente importato", params);
						}
					}
				}
			}
		} catch (Exception e) {
			_logger.error(e);
		}
	}
}
