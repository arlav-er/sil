package it.eng.sil.action.report.tirocinio;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import com.engiweb.framework.base.SourceBean;
import com.inet.report.Engine;

import it.eng.afExt.utils.MessageCodes;
import it.eng.sil.action.report.AbstractSimpleReport;

public class Tirocinio extends AbstractSimpleReport {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(Tirocinio.class.getName());

	private String fileType = Engine.EXPORT_PDF;

	public void setFileType(String eft) {
		if ("PDF".equalsIgnoreCase(eft))
			this.fileType = Engine.EXPORT_PDF;
		else if ("RTF".equalsIgnoreCase(eft))
			this.fileType = Engine.EXPORT_RTF;
		else if ("HTML".equalsIgnoreCase(eft))
			this.fileType = Engine.EXPORT_HTML;
		else if ("XLS".equalsIgnoreCase(eft))
			this.fileType = Engine.EXPORT_XLS;
		else
			this.fileType = Engine.EXPORT_PDF;
	}

	public String getFileType() {
		return this.fileType;
	}

	public void service(SourceBean request, SourceBean response) {

		super.service(request, response);

		try {
			String apriFile = (String) request.getAttribute("apriFileBlob");
			if (apriFile != null && apriFile.equalsIgnoreCase("true")) {
				BigDecimal prgDoc = new BigDecimal((String) request.getAttribute("prgDocumento"));
				this.openDocument(request, response, prgDoc);
			} else {
				String tipoFile = (String) request.getAttribute("tipoFile");
				if (tipoFile != null)
					setStrNomeDoc("Tirocinio." + tipoFile);
				else
					setStrNomeDoc("Tirocinio.pdf");
				setStrDescrizione("Tirocinio");

				// interruttore CM
				/*
				 * if (Sottosistema.CM.isOff()) { setReportPath("Curriculum/CV_CC.rpt"); }else{
				 * setReportPath("Curriculum/CV_CC_L68.rpt"); }
				 */

				setReportPath("Tirocinio/Tirocinio_VDA_CC.rpt ");

				// impostazione parametri del report
				Map prompts = new HashMap();

				prompts.put("pCodLav", request.getAttribute("cdnLavoratore"));
				prompts.put("cdnLavoratore", request.getAttribute("cdnLavoratore")); // inutile ora, toglierlo!
				prompts.put("PRGTIROCINIO", request.getAttribute("PRGTIROCINIO"));
				prompts.put("cdnLavoratoreEncrypt", request.getAttribute("cdnLavoratoreEncrypt"));
				prompts.put("CODCPIUT", request.getAttribute("CODCPIUT"));

				// if (Sottosistema.CM.isOn() && !"false".equals(request.getAttribute("isCM"))) {
				// prompts.put("interCM", "true");
				// } else {
				// prompts.put("interCM", "false");
				// }
				//
				// prompts.put("showNoteCPI", Utils.notNull(request.getAttribute("showNoteCPI")));

				// solo se e' richiesta la protocollazione i parametri vengono
				// inseriti nella Map
				addPromptFieldsProtocollazione(prompts, request);

				// ora si chiede di usare il passaggio dei parametri per nome e
				// non per posizione (col vettore, passaggio di default)
				setPromptFields(prompts);

				/*
				 * la superclasse ha gia' impostato il tipo documento, quindi ripetere qui l'istruzione e' inutile.
				 * String tipoDoc = (String)request.getAttribute("tipoDoc"); if (tipoDoc != null)
				 * setCodTipoDocumento(tipoDoc);
				 */

				String salva = (String) request.getAttribute("salvaDB");
				String apri = (String) request.getAttribute("apri");
				if (salva != null && salva.equalsIgnoreCase("true")) {
					insertDocument(request, response);
				} else if (apri != null && apri.equalsIgnoreCase("true")) {
					showDocument(request, response);
				}
			}
			// else
			/*
			 * } catch (EMFUserError ue) { setOperationFail(request, response, ue); reportFailure(ue,
			 * "CurriculumDisponibilita.service()", "");
			 */
		} catch (Exception e) {
			setOperationFail(request, response, e);
			reportFailure(MessageCodes.General.OPERATION_FAIL, e, "Tirocinio.service()", "");

		}
	}

} // class
