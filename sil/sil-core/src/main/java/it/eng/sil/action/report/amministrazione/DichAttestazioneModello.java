/*
 * Creato il Mar 31, 2005
 *
 */
package it.eng.sil.action.report.amministrazione;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.base.XMLObject;
import com.engiweb.framework.error.EMFErrorSeverity;
import com.engiweb.framework.error.EMFUserError;

import it.eng.afExt.utils.DateUtils;
import it.eng.afExt.utils.MessageCodes;
import it.eng.afExt.utils.ReportOperationResult;
import it.eng.sil.action.report.AbstractSimpleReport;
import it.eng.sil.action.report.UtilsConfig;
import it.eng.sil.module.AccessoSemplificato;
import it.eng.sil.security.User;
import it.eng.sil.util.Utils;

/**
 * @author savino
 * 
 */
public class DichAttestazioneModello extends AbstractSimpleReport {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(DichAttestazioneModello.class.getName());

	public void service(SourceBean request, SourceBean response) {
		super.service(request, response);
		String apriFile = (String) request.getAttribute("apriFileBlob");
		ReportOperationResult reportOperation = new ReportOperationResult(this, response);
		if (apriFile != null && apriFile.equalsIgnoreCase("true")) {
			BigDecimal prgDoc = new BigDecimal((String) request.getAttribute("prgDocumento"));
			this.openDocument(request, response, prgDoc);
		} else {
			setStrDescrizione("Dichiarazione/Attestazione");
			String tipoFile = (String) request.getAttribute("tipoFile");
			if (tipoFile != null) {
				setStrNomeDoc("DichiarazioneAttestazione." + tipoFile);
			} else {
				setStrNomeDoc("DichiarazioneAttestazione.pdf");
			}

			// Gestione configurazione stampa
			UtilsConfig utility = new UtilsConfig("DIC_ATT");
			String configProvincia = utility.getConfigurazione();
			if (configProvincia.equalsIgnoreCase("NT")) {
				setReportPath("Amministrazione/Modello_DichAtt_CC.rpt");
			} else {
				setReportPath("Amministrazione/Modello_DichAtt_" + configProvincia + "_CC.rpt");
			}
			try {
				AccessoSemplificato db = new AccessoSemplificato(this);
				SourceBean infoSpecificheDelReport = db.doSelect(request, response, false);
				if (infoSpecificheDelReport == null)
					throw new EMFUserError(EMFErrorSeverity.ERROR, MessageCodes.General.OPERATION_FAIL);
				String dataInizio = Utils.notNull(infoSpecificheDelReport.getAttribute("row.datInizio"));
				String dataFine = Utils.notNull(infoSpecificheDelReport.getAttribute("row.datFine"));
				String prgModelloStampa = Utils.notNull(infoSpecificheDelReport.getAttribute("row.prgModelloStampa"));
				String codTipoDocumento = Utils.notNull(infoSpecificheDelReport.getAttribute("row.codTipoDocumento"));
				String noteSpeciciche = Utils.notNull(infoSpecificheDelReport.getAttribute("row.strNoteSpecifiche"));
				String descrizioneOggettoModello = Utils
						.notNull(infoSpecificheDelReport.getAttribute("row.descrizioneOggetto"));
				// impostazione della descrizione del documento
				setFlgAutocertificazione("");
				setFlgDocIdentifP("");
				setFlgDocAmm("");
				setStrDescrizione(descrizioneOggettoModello);
				// impostazione dei parametri del report
				Map prompts = new HashMap();
				prompts.put("cdnLavoratore", request.getAttribute("cdnLavoratore"));
				prompts.put("dataInizio", dataInizio);
				prompts.put("dataFine", dataFine);
				prompts.put("noteSpecifiche", noteSpeciciche);
				prompts.put("prgModelloStampa", prgModelloStampa);
				prompts.put("tipologia", codTipoDocumento);
				User user = (User) getRequestContainer().getSessionContainer().getAttribute("@@USER@@");
				prompts.put("codCpi", user.getCodRif());
				// solo se e' richiesta la protocollazione i parametri vengono
				// inseriti nella Map
				// se manca anche solo un parametro il metodo lancia una
				// eccezione.

				addPromptFieldsProtocollazione(prompts, request);
				setPromptFields(prompts);
				// sovrascrivo le date inizio e acquisizione/rilascio
				setDatAcqril(dataInizio);
				setDatInizio(DateUtils.getNow());
				setDatFine("");
				setStrEnteRilascio(String.valueOf(user.getDescGruppo()));
			} catch (EMFUserError ue) {
				it.eng.sil.util.TraceWrapper.debug(_logger, "", (XMLObject) ue);

				setOperationFail(request, response, ue);
				reportOperation.reportFailure(ue, "DichAttestazioneModello.service()",
						"la visualizzazione dei dati della protocollazione non e' possibile a causa della mancanza di un parametro necessario al report.");
				return;
			}
			String salva = (String) request.getAttribute("salvaDB");
			String apri = (String) request.getAttribute("apri");
			if (salva != null && salva.equalsIgnoreCase("true")) {
				insertDocument(request, response);
			} else if (apri != null && apri.equalsIgnoreCase("true")) {
				showDocument(request, response);
			}
		}
	}

}