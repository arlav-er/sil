package it.eng.sil.module.sap;

import java.util.Vector;

import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.utils.MessageCodes;
import it.eng.afExt.utils.ReportOperationResult;
import it.eng.afExt.utils.StringUtils;

public class InsertTitStu extends InsertSAP {
	private static final long serialVersionUID = 1L;

	@Override
	public void service(SourceBean request, SourceBean response) {
		try {
			String strNumTitoli = ((String) request.getAttribute("numTitoli"));
			String check = "";
			ReportOperationResult reportOperation = new ReportOperationResult(this, response);
			this.disableMessageIdSuccess();
			this.disableMessageIdFail();
			this.disableMessageIdElementDuplicate();

			if ((strNumTitoli != null && !strNumTitoli.equals(""))) {
				int numTitoli = new Integer(strNumTitoli).intValue();
				for (int i = 0; i < numTitoli; i++) {
					check = (String) request.getAttribute(SAP_SEZ_TITOLI + "_chkImporta_" + i);
					if (check != null && check.equals("on")) {
						String codTitolo = StringUtils.getAttributeStrNotNull(request,
								SAP_SEZ_TITOLI + "_codTitolo_" + i);

						request.delAttribute("codTitolo");
						request.setAttribute("codTitolo", codTitolo);
						request.delAttribute("codTipoTitolo");
						request.setAttribute("codTipoTitolo", getTipoTitolo(codTitolo));
						request.delAttribute("numAnno");
						request.setAttribute("numAnno",
								StringUtils.getAttributeStrNotNull(request, SAP_SEZ_TITOLI + "_numAnno_" + i));
						request.delAttribute("strSpecifica");
						request.setAttribute("strSpecifica",
								StringUtils.getAttributeStrNotNull(request, SAP_SEZ_TITOLI + "_strSpecifica_" + i));
						request.delAttribute("flgPrincipale");
						request.setAttribute("flgPrincipale",
								StringUtils.getAttributeStrNotNull(request, SAP_SEZ_TITOLI + "_flgPrincipale_" + i));
						request.delAttribute("codMonoStato");
						request.setAttribute("codMonoStato",
								StringUtils.getAttributeStrNotNull(request, SAP_SEZ_TITOLI + "_codMonoStato_" + i));
						request.delAttribute("strIstscolastico");
						request.setAttribute("strIstscolastico",
								StringUtils.getAttributeStrNotNull(request, SAP_SEZ_TITOLI + "_strIstscolastico_" + i));
						request.delAttribute("strLocalita");
						request.setAttribute("strLocalita",
								StringUtils.getAttributeStrNotNull(request, SAP_SEZ_TITOLI + "_strLocalita_" + i));
						request.delAttribute("codComune");
						request.setAttribute("codComune",
								StringUtils.getAttributeStrNotNull(request, SAP_SEZ_TITOLI + "_codComune_" + i));
						request.delAttribute("strVoto");
						request.setAttribute("strVoto",
								StringUtils.getAttributeStrNotNull(request, SAP_SEZ_TITOLI + "_strVoto_" + i));

						Vector<String> params = new Vector<String>(1);
						params.add(StringUtils.getAttributeStrNotNull(request, SAP_SEZ_TITOLI + "_descrTitolo_" + i));

						if (doInsertNoDuplicate(request, response)) {
							reportOperation.reportWarning(MessageCodes.ImportaSAP.INSERT_TITOLO_STUDIO_LAV_OK,
									"service", "Titolo di Studio correttamente importato", params);

							if (codTitolo.substring(codTitolo.length() - 3).equals("000"))
								reportOperation.reportWarning(MessageCodes.ImportaSAP.TITOLO_STUDIO_NON_SPEC_LAV_OK,
										"service", "Il Titolo di Studio indicato non è specifico", params);

						} else {
							reportOperation.reportWarning(MessageCodes.ImportaSAP.INSERT_TITOLO_STUDIO_LAV_FAIL,
									"service", "Titolo di Studio già presente", params);
						}
					}
				}
			}
		} catch (Exception e) {
			_logger.error(e);
		}
	}
}
