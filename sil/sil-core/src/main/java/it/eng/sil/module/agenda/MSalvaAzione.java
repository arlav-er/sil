/*
 * Creato il 1-lug-04
 */
package it.eng.sil.module.agenda;

import java.math.BigDecimal;

import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.utils.ReportOperationResult;
import it.eng.afExt.utils.StringUtils;
import it.eng.afExt.utils.TransactionQueryExecutor;
import it.eng.sil.module.AbstractSimpleModule;

/**
 * @author roccetti
 */
public class MSalvaAzione extends AbstractSimpleModule {

	/**
	 * @see com.engiweb.framework.dispatching.module.AbstractModule#service(com.engiweb.framework.base.SourceBean,
	 *      com.engiweb.framework.base.SourceBean)
	 */
	public void service(SourceBean request, SourceBean response) throws Exception {

		ReportOperationResult reportOperation = new ReportOperationResult(this, response);

		// Segnalazione soli errori/problemi
		disableMessageIdSuccess();

		TransactionQueryExecutor trans = new TransactionQueryExecutor(getPool());
		this.enableTransactions(trans);
		trans.initTransaction();

		// Dopo ogni operazione raccoglie il risultato
		boolean result = true;

		// Prima prendo il prg massimo da de_azione che referenzia de_azione_ragg
		setSectionQuerySelect("QUERY_SELECT_MAX_DE_AZIONE");
		SourceBean rowConfig2 = doSelect(request, response);
		BigDecimal prgazioni = new BigDecimal(1);
		Integer prga = 1;
		Integer prgazioneragg = Integer
				.parseInt(StringUtils.getAttributeStrNotNull(request, "comboObbiettivoMisuraYeiDett"));
		if (rowConfig2 != null) {
			prgazioni = (BigDecimal) rowConfig2.getAttribute("ROW.MAXPRGAZIONI");
			prga = prgazioni.intValue();
			prga++;
		}

		// setMessageIdFail(MessageCodes.General.INSERT_FAIL);
		request.setAttribute("PRGA", prga);
		request.setAttribute("PRGAZIONERAGG", prgazioneragg);
		setSectionQueryInsert("QUERY_INSERT_DE_AZIONE");
		result = doInsert(request, response);
		System.out.println();

		if (!result) {
			trans.rollBackTransaction();
			return;
		}
		//
		//
		//
		//
		// // Esecuzione della query su MA_SERVIZIO_PRESTAZIONE)
		// String codPrestazione = StringUtils.getAttributeStrNotNull(request, "CODICEPRESTAZIONE");
		// if (codPrestazione != null && codPrestazione.length() > 0)
		// {
		// setMessageIdFail(MessageCodes.InserisciServizioPrestazioneAttivita.MA_SERVIZIO_PRESTAZIONE_INSERT_FAIL);
		// setSectionQueryInsert("QUERY_INSERT_MA_SERVIZIO_PRESTAZIONE");
		// result = doInsert(request, response);
		//
		// if (!result) {
		// trans.rollBackTransaction();
		// return;
		// }
		// }
		//
		// String codTipoAttivita = StringUtils.getAttributeStrNotNull(request, "CODICETIPOATTIVITA");
		// if (codTipoAttivita != null && codTipoAttivita.length() > 0)
		// {
		// // Esecuzione della query su MA_SERVIZIO_PRESTAZIONE)
		// setMessageIdFail(MessageCodes.InserisciServizioPrestazioneAttivita.MA_SERVIZIO_TIPOATTIVITA_INSERT_FAIL);
		// setSectionQueryInsert("QUERY_INSERT_MA_SERVIZIO_TIPOATTIVITA");
		// result = doInsert(request, response);
		//
		// if (!result) {
		// trans.rollBackTransaction();
		// return;
		// }
		// }
		//
		// if (!result) {
		// trans.rollBackTransaction();
		// response.setAttribute("inserito", "false");
		// reportOperation.reportFailure(MessageCodes.General.OPERATION_FAIL);
		// return;
		// } else {
		// trans.commitTransaction();
		// response.setAttribute("inserito", "true");
		// reportOperation.reportSuccess(MessageCodes.General.OPERATION_SUCCESS);
		// }

	}

}
