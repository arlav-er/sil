/*
 * Creato il 1-lug-04
 */
package it.eng.sil.module.anag;

import java.math.BigDecimal;

import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.utils.MessageCodes;
import it.eng.afExt.utils.ReportOperationResult;
import it.eng.afExt.utils.StringUtils;
import it.eng.afExt.utils.TransactionQueryExecutor;
import it.eng.sil.module.AbstractSimpleModule;

/**
 * @author roccetti
 * 
 *         Effettua il trasferimnento di competenza amministartiva intraprovinciale: Aggiorna il domicilio del
 *         lavoratore su AN_LAVORATORE Chiude e riapre AN_LAV_STORIA_INF, chiude e riapre AM_ELENCO_ANAGRAFICO, Chiude
 *         l'eventuale patto aperto per il lavoratore su AM_PATTO_LAVORATORE
 */
public class TraIntraProvinciale extends AbstractSimpleModule {

	/**
	 * @see com.engiweb.framework.dispatching.module.AbstractModule#service(com.engiweb.framework.base.SourceBean,
	 *      com.engiweb.framework.base.SourceBean)
	 */
	public void service(SourceBean request, SourceBean response) throws Exception {

		ReportOperationResult reportOperation = new ReportOperationResult(this, response);

		// Segnalazione soli errori/problemi
		disableMessageIdSuccess();

		boolean isPattoAperto = false;
		// Controllo se ho un patto da chiudere
		String prgPattoLav = StringUtils.getAttributeStrNotNull(request, "PRGPATTOLAVORATORE");
		if (!prgPattoLav.equals("") && !prgPattoLav.equalsIgnoreCase("null")) {
			isPattoAperto = true;
		}
		// cerco la did

		/*
		 * //Controllo se ho un patto da chiudere setSectionQuerySelect("QUERY_FIND_PATTO_APERTO"); SourceBean pattoRows
		 * = doSelect(request,response);
		 * 
		 * //estraggo l'identificativo dell'eventuale patto aperto Vector row = pattoRows.getAttributeAsVector("ROW");
		 * if (row.size() == 1) { BigDecimal prgPatto = (BigDecimal) ((SourceBean)
		 * row.get(0)).getAttribute("PRGPATTOLAVORATORE"); if (prgPatto != null) {
		 * request.setAttribute("PRGPATTOLAVORATORE", prgPatto); isPattoAperto = true; } } else if (row.size() > 1) {
		 * reportOperation.reportFailure(MessageCodes.Trasferimento.ERR_PIU_PATTI_APERTI); return; }
		 */

		// Transazione per AN_LAVORATORE, AN_LAV_STORIA_INF,
		// AM_ELENCO_ANAGRAFICO, AM_PATTO_LAVORATORE
		TransactionQueryExecutor trans = new TransactionQueryExecutor(getPool());
		this.enableTransactions(trans);
		trans.initTransaction();

		// Dopo ogni operazione raccoglie il risultato
		boolean result = true;
		setSectionQuerySelect("GET_DID_VALIDA");
		SourceBean did = doSelect(request, response, false);
		// aggiorno domicilio lavoratore
		setMessageIdFail(MessageCodes.Trasferimento.ERR_UPDATE_DOM_LAV);
		setSectionQueryUpdate("QUERY_UPDATE_DOM_LAV");
		result = doUpdate(request, response);

		if (!result) {
			trans.rollBackTransaction();
			return;
		}

		// Esecuzione della query su AN_LAV_STORIA_INF (Il record precedente
		// viene chiuso con una stored procedure)
		setMessageIdFail(MessageCodes.Trasferimento.ERR_CLOSE_AN_LAV_S);
		setSectionQueryInsert("QUERY_INSERT_AN_LAV_S");
		result = doInsert(request, response);

		if (!result) {
			trans.rollBackTransaction();
			return;
		}

		// Chiudo il record precedente su AM_ELENCO_ANAGRAFICO
		setMessageIdFail(MessageCodes.Trasferimento.ERR_CLOSE_AM_EL_ANAG);
		setSectionQueryUpdate("QUERY_UPDATE_AM_EL_ANAG");
		result = doUpdate(request, response);

		if (!result) {
			trans.rollBackTransaction();
			return;
		}

		// Apro un nuovo record su AM_ELENCO_ANAGRAFICO
		setSectionQueryNextVal("GET_AM_ELENCO_ANAGRAFICO_NEXTVAL");
		Object prgElAnag = doNextVal(request, response);
		setMessageIdFail(MessageCodes.Trasferimento.ERR_OPEN_AM_EL_ANAG);
		setSectionQueryInsert("QUERY_INSERT_AM_EL_ANAG");
		result = doInsert(request, response, "PRGELENCOANAGRAFICO");
		//
		if (did != null && did.containsAttribute("row")) {
			setSectionQueryUpdate("UPDATE_DICH_DISP");
			setMessageIdFail(MessageCodes.Trasferimento.ERR_UPD_AM_DICH_DISP);
			request.updAttribute("prgdichdisponibilita", did.getAttribute("row.prgdichdisponibilita"));
			BigDecimal numKlo = (BigDecimal) did.getAttribute("row.numklodichdisp");
			request.updAttribute("numklodichdisp", numKlo.add(new BigDecimal(1)));
			boolean ret = doUpdate(request, response);
			if (!ret) {
				trans.rollBackTransaction();
				return;
			}

		}
		if (isPattoAperto) {
			if (!result) {
				trans.rollBackTransaction();
				return;
			}

			// Chiudo il record precedente su AM_PATTO_LAVORATORE
			setMessageIdFail(MessageCodes.Trasferimento.ERR_CLOSE_AM_PATTO_LAV);
			setSectionQueryUpdate("QUERY_UPDATE_AM_PATTO_LAV");
			result = doUpdate(request, response);

		}

		if (!result) {
			trans.rollBackTransaction();
			response.setAttribute("trasferito", "false");
			reportOperation.reportFailure(MessageCodes.General.OPERATION_FAIL);
			return;
		} else {
			trans.commitTransaction();
			response.setAttribute("trasferito", "true");
			reportOperation.reportSuccess(MessageCodes.General.OPERATION_SUCCESS);
		}

	}

}
