package it.eng.sil.module.agenda;

import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.utils.MessageCodes;
import it.eng.afExt.utils.ReportOperationResult;
import it.eng.afExt.utils.StringUtils;
import it.eng.afExt.utils.TransactionQueryExecutor;
import it.eng.sil.module.AbstractSimpleModule;

public class MAggiornaAzione extends AbstractSimpleModule {
	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(AzioneList.class.getName());

	public MAggiornaAzione() {
	}

	public void service(SourceBean request, SourceBean response) throws Exception {
		ReportOperationResult reportOperation = new ReportOperationResult(this, response);

		// Segnalazione soli errori/problemi
		int idSuccess = this.disableMessageIdSuccess();
		int idFail = this.disableMessageIdFail();
		boolean msgWarningReinvioSaap = false;

		// Transazione per DE_SERVIZI, MA_SERVIZIO_PRESTAZIONE, MA_SERVIZIO_TIPOATTIVITA
		TransactionQueryExecutor trans = new TransactionQueryExecutor(getPool());
		this.enableTransactions(trans);
		trans.initTransaction();

		// Dopo ogni operazione raccoglie il risultato
		boolean result = true;

		// Prima verifico se per il SERVIZIO in questione stiamo per inserire/aggiornare una nuova/vecchia TIPOATTIVITA
		/*
		 * setSectionQuerySelect("QUERY_SELECT_MA_SERVIZIO_TIPOATTIVITA"); SourceBean rowConfig2 = doSelect(request,
		 * response); String esisteMaServizioTipoAttivita = "0"; if (rowConfig2 != null) { esisteMaServizioTipoAttivita
		 * = rowConfig2.containsAttribute("ROW.NUM")?rowConfig2.getAttribute("ROW.NUM").toString():"0";
		 * 
		 * }
		 */

		String comboObbiettivoMisuraYeiOld = StringUtils.getAttributeStrNotNull(request,
				"COMBOOBBIETTIVOMISURAYEI_OLD");
		String comboObbiettivoMisuraYeiNew = StringUtils.getAttributeStrNotNull(request,
				"COMBOOBBIETTIVOMISURAYEIDETT");
		String azioneOld = StringUtils.getAttributeStrNotNull(request, "AZIONE_OLD");
		String azioneNew = StringUtils.getAttributeStrNotNull(request, "AZIONEDETT");
		String comboTipoAttivitaOld = StringUtils.getAttributeStrNotNull(request, "COMBOTIPOATTIVITA_OLD");
		String comboTipoAttivitaNew = StringUtils.getAttributeStrNotNull(request, "COMBOTIPOATTIVITADETT");
		String comboPrestazioneOld = StringUtils.getAttributeStrNotNull(request, "COMBOPRESTAZIONE_OLD");
		String comboPrestazioneNew = StringUtils.getAttributeStrNotNull(request, "COMBOPRESTAZIONEDETT");
		String flagMisuraYeiOld = StringUtils.getAttributeStrNotNull(request, "FLAGMISURAYEI_OLD");
		String flagMisuraYeiNew = StringUtils.getAttributeStrNotNull(request, "FLAGMISURAYEIDETT");
		String flagPolAttivaOld = StringUtils.getAttributeStrNotNull(request, "FLAGPOLATTIVAOLD");
		String flagPolAttivaNew = StringUtils.getAttributeStrNotNull(request, "FLAGPOLATTIVADETT");

		String prgAzioni = StringUtils.getAttributeStrNotNull(request, "PRGAZIONI");

		// verifico se il campo AZIONE è variato
		if (!azioneOld.trim().equalsIgnoreCase(azioneNew.trim())) {

			_logger.debug("sil.module.agenda.MAggiornaAzione " + "::azioneOld:" + azioneOld);
			_logger.debug("sil.module.agenda.MAggiornaAzione " + "::azioneNew:" + azioneNew);
			setSectionQueryUpdate("QUERY_UPDATE_AZIONE");
			result = doUpdate(request, response);
			if (!result) {
				setMessageIdFail(MessageCodes.General.DELETE_FAILED_FK);
				trans.rollBackTransaction();
				return;
			}
		}

		if (!comboObbiettivoMisuraYeiOld.trim().equals(comboObbiettivoMisuraYeiNew.trim())) {
			_logger.debug(
					"sil.module.agenda.MAggiornaAzione " + "::obbiettivoMisuraYeiOld:" + comboObbiettivoMisuraYeiOld);
			_logger.debug(
					"sil.module.agenda.MAggiornaAzione " + "::obbiettivoMisuraYeiNew:" + comboObbiettivoMisuraYeiNew);
		}
		if (!comboTipoAttivitaOld.trim().equals(comboTipoAttivitaNew.trim())) {
			_logger.debug("sil.module.agenda.MAggiornaAzione " + "::tipoAttivitaOld:" + comboTipoAttivitaOld);
			_logger.debug("sil.module.agenda.MAggiornaAzione " + "::tipoAttivitaNew:" + comboTipoAttivitaNew);
		}
		if (!comboPrestazioneOld.trim().equals(comboPrestazioneNew.trim())) {
			_logger.debug("sil.module.agenda.MAggiornaAzione " + "::PrestazioneOld:" + comboPrestazioneOld);
			_logger.debug("sil.module.agenda.MAggiornaAzione " + "::PrestazioneNew:" + comboPrestazioneNew);

		}
		if (!flagMisuraYeiOld.trim().equals(flagMisuraYeiNew.trim())
				&& (flagMisuraYeiOld.trim().equalsIgnoreCase("") || flagMisuraYeiNew.trim().equalsIgnoreCase(""))) {
			_logger.debug("sil.module.agenda.MAggiornaAzione " + ":flagMisuraYeiOld:" + flagMisuraYeiOld);
			_logger.debug("sil.module.agenda.MAggiornaAzione " + ":flagMisuraYeiNew:" + flagMisuraYeiNew);

		}
		if (!flagPolAttivaOld.trim().equals(flagPolAttivaNew.trim())
				&& (flagPolAttivaOld.trim().equalsIgnoreCase("") || flagPolAttivaNew.trim().equalsIgnoreCase(""))) {
			_logger.debug("sil.module.agenda.MAggiornaAzione " + ":flagPolAttivaOld:" + flagPolAttivaOld);
			_logger.debug("sil.module.agenda.MAggiornaAzione " + ":flagPolAttivaNew:" + flagPolAttivaNew);

		}

	}

}
