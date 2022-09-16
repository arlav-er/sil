/*
 * Creato il 1-lug-04
 */
package it.eng.sil.module.agenda;

import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.utils.MessageCodes;
import it.eng.afExt.utils.ReportOperationResult;
import it.eng.afExt.utils.StringUtils;
import it.eng.afExt.utils.TransactionQueryExecutor;
import it.eng.sil.module.AbstractSimpleModule;

/**
 * @author roccetti
 */
public class InserisciServizioPrestazioneAttivitaModule extends AbstractSimpleModule {

	/**
	 * @see com.engiweb.framework.dispatching.module.AbstractModule#service(com.engiweb.framework.base.SourceBean,
	 *      com.engiweb.framework.base.SourceBean)
	 */
	public void service(SourceBean request, SourceBean response) throws Exception {

		ReportOperationResult reportOperation = new ReportOperationResult(this, response);

		// Segnalazione soli errori/problemi
		disableMessageIdSuccess();

		// Transazione per DE_SERVIZI, MA_SERVIZIO_PRESTAZIONE, MA_SERVIZIO_TIPOATTIVITA
		TransactionQueryExecutor trans = new TransactionQueryExecutor(getPool());
		this.enableTransactions(trans);
		trans.initTransaction();

		// Dopo ogni operazione raccoglie il risultato
		boolean result = true;

		// Esecuzione della query su DE_SERVIZIO)
		setMessageIdFail(MessageCodes.InserisciServizioPrestazioneAttivita.DE_SERVIZIO_INSERT_FAIL);
		setSectionQueryInsert("QUERY_INSERT_DE_SERVIZIO");
		result = doInsert(request, response);

		if (!result) {
			trans.rollBackTransaction();
			return;
		}

		// Esecuzione della query su MA_SERVIZIO_PRESTAZIONE)
		String codPrestazione = StringUtils.getAttributeStrNotNull(request, "CODICEPRESTAZIONE");
		if (codPrestazione != null && codPrestazione.length() > 0) {
			setMessageIdFail(MessageCodes.InserisciServizioPrestazioneAttivita.MA_SERVIZIO_PRESTAZIONE_INSERT_FAIL);
			setSectionQueryInsert("QUERY_INSERT_MA_SERVIZIO_PRESTAZIONE");
			result = doInsert(request, response);

			if (!result) {
				trans.rollBackTransaction();
				return;
			}
		}

		String codTipoAttivita = StringUtils.getAttributeStrNotNull(request, "CODICETIPOATTIVITA");
		if (codTipoAttivita != null && codTipoAttivita.length() > 0) {
			// Esecuzione della query su MA_SERVIZIO_PRESTAZIONE)
			setMessageIdFail(MessageCodes.InserisciServizioPrestazioneAttivita.MA_SERVIZIO_TIPOATTIVITA_INSERT_FAIL);
			setSectionQueryInsert("QUERY_INSERT_MA_SERVIZIO_TIPOATTIVITA");
			result = doInsert(request, response);

			if (!result) {
				trans.rollBackTransaction();
				return;
			}
		}

		if (!result) {
			trans.rollBackTransaction();
			response.setAttribute("inserito", "false");
			reportOperation.reportFailure(MessageCodes.General.OPERATION_FAIL);
			return;
		} else {
			trans.commitTransaction();
			response.setAttribute("inserito", "true");
			reportOperation.reportSuccess(MessageCodes.General.OPERATION_SUCCESS);
		}

	}

}
