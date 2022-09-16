package it.eng.sil.module.agenda;

import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.utils.MessageCodes;
import it.eng.afExt.utils.ReportOperationResult;
import it.eng.afExt.utils.StringUtils;
import it.eng.afExt.utils.TransactionQueryExecutor;
import it.eng.sil.module.AbstractSimpleModule;

public class MAggiornaServizio extends AbstractSimpleModule {
	public MAggiornaServizio() {
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
		setSectionQuerySelect("QUERY_SELECT_MA_SERVIZIO_TIPOATTIVITA");
		SourceBean rowConfig2 = doSelect(request, response);
		String esisteMaServizioTipoAttivita = "0";
		if (rowConfig2 != null) {
			esisteMaServizioTipoAttivita = rowConfig2.containsAttribute("ROW.NUM")
					? rowConfig2.getAttribute("ROW.NUM").toString()
					: "0";

		}

		String codTipoAttivitaNew = StringUtils.getAttributeStrNotNull(request, "CODICETIPOATTIVITA");
		if (!esisteMaServizioTipoAttivita.equals("0")) {

			// Controllo se codice attivita' non è stato valorizzato
			// SE NON VALORIZZATO ALLORA CANCELLARE DA MAPPATURA
			if (codTipoAttivitaNew == null || codTipoAttivitaNew.trim().equals("")) {
				setSectionQueryDelete("QUERY_DELETE_MA_SERVIZIO_TIPOATTIVITA");
				result = doDelete(request, response);
			} else {
				if (!codTipoAttivitaNew.equals(esisteMaServizioTipoAttivita))
					msgWarningReinvioSaap = true;
				// Esecuzione della update su MA_SERVIZIO_TIPOATTIVITA
				setSectionQueryUpdate("QUERY_UPDATE_MA_SERVIZIO_TIPOATTIVITA");
				result = doUpdate(request, response);
			}
		} else {
			if (codTipoAttivitaNew != null && !codTipoAttivitaNew.trim().equals("")) {
				// Esecuzione della inserte su MA_SERVIZIO_TIPOATTIVITA
				setSectionQueryInsert("QUERY_INSERT_MA_SERVIZIO_TIPOATTIVITA");
				result = doInsert(request, response);
			}
		}
		if (!result) {
			setMessageIdFail(MessageCodes.General.DELETE_FAILED_FK);
			trans.rollBackTransaction();
			return;
		}

		// Prima verifico se per il SERVIZIO in questione stiamo per inserire/aggiornare una nuova/vecchia PRESTAZIONE
		setSectionQuerySelect("QUERY_SELECT_MA_SERVIZIO_PRESTAZIONE");
		SourceBean rowConfig = doSelect(request, response);
		String esisteMaServizioPrestazione = "0";
		if (rowConfig != null) {
			esisteMaServizioPrestazione = rowConfig.containsAttribute("ROW.NUM")
					? rowConfig.getAttribute("ROW.NUM").toString()
					: "0";

		}

		// Controllo se codice prestazione non è stato valorizzato
		String codPrestazione = StringUtils.getAttributeStrNotNull(request, "CODICEPRESTAZIONE");
		if (!esisteMaServizioPrestazione.equals("0")) {
			// SE NON VALORIZZATO ALLORA CANCELLARE DA MAPPATURA
			if (codPrestazione == null || codPrestazione.trim().equals("")) {
				setSectionQueryDelete("QUERY_DELETE_MA_SERVIZIO_PRESTAZIONE");
				result = doDelete(request, response);
			} else {
				// Esecuzione della update su MA_SERVIZIO_PRESTAZIONE
				setSectionQueryUpdate("QUERY_UPDATE_MA_SERVIZIO_PRESTAZIONE");
				result = doUpdate(request, response);
			}

		} else {
			if (codPrestazione != null && !codPrestazione.trim().equals("")) {
				// Esecuzione della insert su MA_SERVIZIO_PRESTAZIONE
				setSectionQueryInsert("QUERY_INSERT_MA_SERVIZIO_PRESTAZIONE");
				result = doInsert(request, response);
			}
		}
		if (!result) {
			setMessageIdFail(MessageCodes.General.DELETE_FAILED_FK);
			trans.rollBackTransaction();
			return;
		}

		// Esecuzione della update su DE_SERVIZIO
		setSectionQueryUpdate("QUERY_UPDATE_SERVIZIO");
		result = doUpdate(request, response);

		if (!result) {
			trans.rollBackTransaction();
			response.setAttribute("aggiornato", "false");
			reportOperation.reportFailure(MessageCodes.General.OPERATION_FAIL);
		} else {
			trans.commitTransaction();
			response.setAttribute("aggiornato", "true");

			reportOperation.reportFailure(MessageCodes.General.OPERATION_SUCCESS);
			if (msgWarningReinvioSaap)
				reportOperation
						.reportFailure(MessageCodes.MsgWarningReinvioSaap.MA_SERVIZIO_TIPOATTIVITA_UPDATE_SUCCESS_WARN);
		}

	}

}
