package it.eng.sil.module.sap;

import java.util.Vector;

import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.utils.MessageCodes;
import it.eng.afExt.utils.ReportOperationResult;
import it.eng.afExt.utils.StringUtils;

public class InsertAbilita extends InsertSAP {
	private static final long serialVersionUID = 1L;

	@Override
	public void service(SourceBean request, SourceBean response) {
		try {
			ReportOperationResult reportOperation = new ReportOperationResult(this, response);
			this.disableMessageIdSuccess();
			this.disableMessageIdFail();
			this.disableMessageIdElementDuplicate();

			String check = (String) request.getAttribute(SAP_SEZ_ABILITA_PATENTI + "_chkImporta");
			if (check != null && check.equals("on")) {
				int numPatenti = request.getAttributeAsVector(SAP_SEZ_ABILITA_PATENTI + "_codAbilitazioneGen").size();
				for (int i = 0; i < numPatenti; i++) {
					SourceBean patente = getPatenteSIL(StringUtils.getAttributeStrNotNull(request,
							SAP_SEZ_ABILITA_PATENTI + "_codAbilitazioneGen_" + i));
					if (patente != null) {
						request.delAttribute("codAbilitazioneGen");
						request.setAttribute("codAbilitazioneGen",
								StringUtils.getAttributeStrNotNull(patente, "ROW.codAbilitazioneGen"));

						Vector<String> params = new Vector<String>(1);
						params.add(StringUtils.getAttributeStrNotNull(patente, "ROW.strDescrizione"));

						if (doInsertNoDuplicate(request, response)) {
							reportOperation.reportWarning(MessageCodes.ImportaSAP.INSERT_PATENTE_LAV_OK, "service",
									"Patente correttamente importata", params);
						} else {
							reportOperation.reportWarning(MessageCodes.ImportaSAP.INSERT_PATENTE_LAV_FAIL, "service",
									"Patente già presente", params);
						}
					} else {
						Vector<String> params = new Vector<String>(1);
						params.add(StringUtils.getAttributeStrNotNull(request,
								SAP_SEZ_ABILITA_PATENTI + "_descrAbilitazioneGen_" + i));

						reportOperation.reportWarning(MessageCodes.ImportaSAP.INSERT_PATENTE_LAV_FAIL_NO_MAP, "service",
								"Patente non importata non essendoci una corrispondenza nel SIL", params);
					}
				}
			}

			check = (String) request.getAttribute(SAP_SEZ_ABILITA_PATENTINI + "_chkImporta");
			if (check != null && check.equals("on")) {
				int numPatentini = request.getAttributeAsVector(SAP_SEZ_ABILITA_PATENTINI + "_codAbilitazioneGen")
						.size();
				for (int i = 0; i < numPatentini; i++) {
					SourceBean patentino = getPatentinoSIL(StringUtils.getAttributeStrNotNull(request,
							SAP_SEZ_ABILITA_PATENTINI + "_codAbilitazioneGen_" + i));
					if (patentino != null) {
						request.delAttribute("codAbilitazioneGen");
						request.setAttribute("codAbilitazioneGen",
								StringUtils.getAttributeStrNotNull(patentino, "ROW.codAbilitazioneGen"));

						Vector<String> params = new Vector<String>(1);
						params.add(StringUtils.getAttributeStrNotNull(patentino, "ROW.strDescrizione"));

						if (doInsertNoDuplicate(request, response)) {
							reportOperation.reportWarning(MessageCodes.ImportaSAP.INSERT_PATENTINO_LAV_OK, "service",
									"Patentino correttamente importato", params);
						} else {
							reportOperation.reportWarning(MessageCodes.ImportaSAP.INSERT_PATENTINO_LAV_FAIL, "service",
									"Patentino già presente", params);
						}
					} else {
						Vector<String> params = new Vector<String>(1);
						params.add(StringUtils.getAttributeStrNotNull(request,
								SAP_SEZ_ABILITA_PATENTINI + "_descrAbilitazioneGen_" + i));

						reportOperation.reportWarning(MessageCodes.ImportaSAP.INSERT_PATENTINO_LAV_FAIL_NO_MAP,
								"service", "Patentino non importato non essendoci una corrispondenza nel SIL", params);
					}
				}
			}

			check = (String) request.getAttribute(SAP_SEZ_ABILITA_ALBI + "_chkImporta");
			if (check != null && check.equals("on")) {
				int numAlbi = request.getAttributeAsVector(SAP_SEZ_ABILITA_ALBI + "_codAbilitazioneGen").size();
				for (int i = 0; i < numAlbi; i++) {
					SourceBean albo = getAlboSIL(StringUtils.getAttributeStrNotNull(request,
							SAP_SEZ_ABILITA_ALBI + "_codAbilitazioneGen_" + i));
					if (albo != null) {
						request.delAttribute("codAbilitazioneGen");
						request.setAttribute("codAbilitazioneGen",
								StringUtils.getAttributeStrNotNull(albo, "ROW.codAbilitazioneGen"));

						Vector<String> params = new Vector<String>(1);
						params.add(StringUtils.getAttributeStrNotNull(albo, "ROW.strDescrizione"));

						if (doInsertNoDuplicate(request, response)) {
							reportOperation.reportWarning(MessageCodes.ImportaSAP.INSERT_ALBO_LAV_OK, "service",
									"Albo correttamente importato", params);
						} else {
							reportOperation.reportWarning(MessageCodes.ImportaSAP.INSERT_ALBO_LAV_FAIL, "service",
									"Albo già presente", params);
						}
					} else {
						Vector<String> params = new Vector<String>(1);
						params.add(StringUtils.getAttributeStrNotNull(request,
								SAP_SEZ_ABILITA_ALBI + "_descrAbilitazioneGen_" + i));

						reportOperation.reportWarning(MessageCodes.ImportaSAP.INSERT_ALBO_LAV_FAIL_NO_MAP, "service",
								"Albo non importato non essendoci una corrispondenza nel SIL", params);
					}
				}
			}
		} catch (Exception e) {
			_logger.error(e);
		}
	}
}
