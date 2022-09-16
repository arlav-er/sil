/*
 * Creato il 6-giu-06
 *
 * Per modificare il modello associato a questo file generato, aprire
 * Finestra&gt;Preferenze&gt;Java&gt;Generazione codice&gt;Codice e commenti
 */
package it.eng.sil.module.amministrazione;

import com.engiweb.framework.base.SourceBean;

/**
 * @author gritti
 * 
 * Per modificare il modello associato al commento di questo tipo generato,
 * aprire Finestra&gt;Preferenze&gt;Java&gt;Generazione codice&gt;Codice e
 * commenti
 */
import it.eng.afExt.utils.MessageCodes;
import it.eng.afExt.utils.ReportOperationResult;
import it.eng.sil.module.AbstractSimpleModule;
import it.eng.sil.module.QueryStrategyException;
import it.eng.sil.util.ExceptionUtils;

public class ASInsValISEE extends AbstractSimpleModule {
	public void service(SourceBean request, SourceBean response) throws Exception {
		// calcolo del punteggio isee
		// BigDecimal valore = new BigDecimal("100");

		// request.setAttribute("numpuntiisee", valore);

		Object res = null;
		ReportOperationResult reportOperation = new ReportOperationResult(this, response);

		try {
			disableMessageIdSuccess();
			disableMessageIdFail();
			enableSimpleQuery(true);
			res = doInsert(request, response, true);
			ExceptionUtils.controlloDateRecordPrecedente(res, MessageCodes.General.ERR_INTERSEZIONE_DATA_REC_ESISTENTE);
			reportOperation.reportSuccess(MessageCodes.General.INSERT_SUCCESS);
		} catch (QueryStrategyException qse) {
			reportOperation.reportFailure(MessageCodes.General.ERR_INTERSEZIONE_DATA_REC_ESISTENTE);
		}
	}
}