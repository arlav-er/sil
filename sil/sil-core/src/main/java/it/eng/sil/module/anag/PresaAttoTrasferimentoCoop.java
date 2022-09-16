/*
 * Creato il 1-lug-04
 */
package it.eng.sil.module.anag;

import java.math.BigDecimal;
import java.util.Vector;

import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.utils.DateUtils;
import it.eng.afExt.utils.MessageCodes;
import it.eng.afExt.utils.ReportOperationResult;
import it.eng.afExt.utils.StringUtils;
import it.eng.afExt.utils.TransactionQueryExecutor;
import it.eng.sil.module.AbstractSimpleModule;
import it.eng.sil.util.amministrazione.impatti.DBLoad;
import it.eng.sil.util.amministrazione.impatti.StatoOccupazionaleBean;

/**
 * @author rolfini featuring roccetti (TraIntraProvinciale)
 *
 *         Prende atto di un trasferimento avvenuto: Aggiorna il domicilio del lavoratore su AN_LAVORATORE Chiude
 *         AN_LAV_STORIA_INF, chiude AM_ELENCO_ANAGRAFICO, Chiude l'eventuale patto aperto per il lavoratore su
 *         AM_PATTO_LAVORATORE
 */
public class PresaAttoTrasferimentoCoop extends AbstractSimpleModule {
	// NOTA 11/09/2006 Savino: proprieta' TransactionQueryExecutor necessaria perche' viene utilizzata in DBLoad
	private TransactionQueryExecutor trans;

	public void enableTransactions(TransactionQueryExecutor executor) {
		super.enableTransactions(executor);
		trans = executor;
	}

	/**
	 * 
	 */
	public void service(SourceBean request, SourceBean response) throws Exception {
		ReportOperationResult reportOperation = new ReportOperationResult(this, response);
		StatoOccupazionaleBean so = null;
		// Segnalazione soli errori/problemi
		disableMessageIdSuccess();
		//
		request.setAttribute("CODMONOTIPOCPI", "T");
		request.setAttribute("CODMONOTIPOORIG", "R");

		boolean isPattoAperto = false;
		// Controllo se ho un patto da chiudere
		String prgPattoLav = StringUtils.getAttributeStrNotNull(request, "PRGPATTOLAVORATORE");
		if (!prgPattoLav.equals("") && !prgPattoLav.equalsIgnoreCase("null")) {
			isPattoAperto = true;
		}
		// Transazione per AN_LAVORATORE, AN_LAV_STORIA_INF, AM_ELENCO_ANAGRAFICO, AM_PATTO_LAVORATORE
		// TransactionQueryExecutor trans = new TransactionQueryExecutor(getPool());
		// this.enableTransactions(trans);
		// trans.initTransaction();
		// Dopo ogni operazione raccoglie il risultato
		boolean result = true;
		setSectionQuerySelect("GET_DID_VALIDA");
		SourceBean did = doSelect(request, response, false); // sarà da chiudere!
		// aggiorno domicilio lavoratore
		setSectionQueryUpdate("QUERY_UPDATE_DOM_LAV");
		result = doUpdate(request, response);

		if (!result) {
			// trans.rollBackTransaction();
			response.setAttribute("trasferito", "false");
			reportOperation.reportFailure(MessageCodes.Trasferimento.ERR_UPDATE_DOM_LAV);
			throw new Exception("operazione terminata con errori");
		}
		// Esecuzione della query su AN_LAV_STORIA_INF (Il record precedente viene chiuso con una stored procedure)
		setSectionQueryInsert("QUERY_INSERT_AN_LAV_S");
		result = doInsert(request, response);

		if (!result) {
			// trans.rollBackTransaction();
			response.setAttribute("trasferito", "false");
			reportOperation.reportFailure(MessageCodes.Trasferimento.ERR_CLOSE_AN_LAV_S);
			throw new Exception("operazione terminata con errori");
		}
		// Chiudo il record precedente su AM_ELENCO_ANAGRAFICO
		setSectionQueryUpdate("QUERY_UPDATE_AM_EL_ANAG");
		result = doUpdate(request, response);

		if (!result) {
			// trans.rollBackTransaction();
			response.setAttribute("trasferito", "false");
			reportOperation.reportFailure(MessageCodes.Trasferimento.ERR_CLOSE_AM_EL_ANAG);
			throw new Exception("operazione terminata con errori");
		}
		// Chiudo la DID
		if (did != null && did.containsAttribute("row")) {
			setSectionQueryUpdate("CLOSE_DICH_DISP");
			request.updAttribute("prgdichdisponibilita", did.getAttribute("row.prgdichdisponibilita"));
			BigDecimal numKlo = (BigDecimal) did.getAttribute("row.numklodichdisp");
			request.updAttribute("numklodichdisp", numKlo.add(new BigDecimal(1)));
			boolean ret = doUpdate(request, response);
			if (!ret) {
				// trans.rollBackTransaction();
				response.setAttribute("trasferito", "false");
				reportOperation.reportFailure(MessageCodes.Trasferimento.ERR_UPD_AM_DICH_DISP);
				throw new Exception("operazione terminata con errori");
			}
		}

		if (isPattoAperto) {
			// Chiudo il record precedente su AM_PATTO_LAVORATORE
			setSectionQueryUpdate("QUERY_UPDATE_AM_PATTO_LAV");
			result = doUpdate(request, response);
			if (!result) {
				// trans.rollBackTransaction();
				response.setAttribute("trasferito", "false");
				reportOperation.reportFailure(MessageCodes.Trasferimento.ERR_CLOSE_AM_PATTO_LAV);
				throw new Exception("operazione terminata con errori");
			}
		}
		// 4/7/2005 - Correzione anomalia che permetteva di avere due stati occ. alla stessa data
		Vector vectSo = DBLoad.getStatiOccupazionali(request.getAttribute("cdnLavoratore"), trans);
		so = vectSo.size() > 0 ? new StatoOccupazionaleBean((SourceBean) vectSo.get(vectSo.size() - 1)) : null;
		if (so != null
				&& (DateUtils.compare(so.getDataInizio(), (String) request.getAttribute("DATTRASFERIMENTO")) == 0)) {
			// Aggiorno lo stato occupazionale
			BigDecimal numklostatooccupaz = so.getNumKlo().add(new BigDecimal(1));
			BigDecimal prgStatoOcc = so.getPrgStatoOccupaz();
			request.setAttribute("numklostatooccupaz", numklostatooccupaz);
			request.setAttribute("prgStatoOccupaz", prgStatoOcc);
			setSectionQueryUpdate("QUERY_UPDATE_STATO_OCC");
			request.updAttribute("codStatoOcc", "C");
			result = doUpdate(request, response);
		} else {
			// Inserisco un nuovo stato occupazionale
			setSectionQueryInsert("INSERT_NEW_STATO_OCC");
			request.updAttribute("codStatoOcc", "C");
			result = doInsert(request, response);
		}
		// 4/7/2005 - Fine correzione anomalia
		if (!result) {
			// trans.rollBackTransaction();
			response.setAttribute("trasferito", "false");
			reportOperation.reportFailure(MessageCodes.StatoOccupazionale.ERRORE_INS_STATO_OCC);
			throw new Exception("operazione terminata con errori");
		} else {
			// trans.commitTransaction();
			response.setAttribute("trasferito", "true");
			reportOperation.reportSuccess(MessageCodes.General.OPERATION_SUCCESS);
		}
	}

}
