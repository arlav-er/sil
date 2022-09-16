package it.eng.sil.module.patto;

import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.utils.MessageCodes;
import it.eng.afExt.utils.ReportOperationResult;
import it.eng.afExt.utils.TransactionQueryExecutor;
import it.eng.sil.module.AbstractSimpleModule;

public class SaveElAnag extends AbstractSimpleModule {
	public void service(SourceBean request, SourceBean response) throws Exception {

		if (!isTrancactionUpdate(request)) {
			// di fatto l'unica ragione di aggiornamento del record e' quella di
			// chiusura del record stesso per cui
			// si avra' sempre una operazione in transazione
			doUpdate(request, response);

			return;
		}

		// apri la transazione
		TransactionQueryExecutor transExec = null;
		ReportOperationResult reportOperation = null;
		this.disableMessageIdFail();
		this.disableMessageIdSuccess();

		boolean ret = true;

		try {
			reportOperation = new ReportOperationResult(this, response);
			transExec = new TransactionQueryExecutor(getPool());
			enableTransactions(transExec);
			transExec.initTransaction();

			if (isChiudiAnagrafica(request)) {
				chiudiPattoAnagrafica(request, response);
			}

			// gestione privacy
			/*******************************************************************
			 * if (PrivacyManager.hasNuovaAutorizzazione(request)) { // aggiorna il record vecchio (ho bisogno del prg)
			 * con datfine = sysdate PrivacyManager.newPrivacy(request, response, this); // aggiorna la anagrafica }
			 * else { // se e' presente la data di cancellazione vuol dire che si sta uscendo dall' elenco anagrafico //
			 * quindi bisogna chiudere la concessione dell' autorizzazione alla privacy if
			 * (PrivacyManager.hasUpdateAutorizzazione(request)) { PrivacyManager.updatePrivacy(request, response,
			 * this); } }
			 ******************************************************************/
			// aggiorno finalmente la anagrafica del lavoratore
			setSectionQueryUpdate("QUERY_UPDATE");
			ret = doUpdate(request, response);

			if (!ret) {
				throw new Exception("Impossibile aggiornare la anagrafica del lavoratore");
			}

			//
			transExec.commitTransaction();
			reportOperation.reportSuccess(MessageCodes.General.UPDATE_SUCCESS);
		} catch (Exception e) {
			transExec.rollBackTransaction();
			reportOperation.reportFailure(MessageCodes.General.UPDATE_FAIL, e, "execute()",
					"aggiornamento am_anagrafica in transazione con am_privacy");
		}
	}

	private void chiudiPattoAnagrafica(SourceBean request, SourceBean response) throws Exception {
		// chiusura patto
		boolean ret = false;
		setSectionQuerySelect("QUERY_SELECT_PATTO");

		SourceBean row = doSelect(request, response);
		Object datInizio = null;
		String datFine = (String) request.getAttribute("DTMCAN");
		datInizio = request.getAttribute("datFine");
		request.delAttribute("datFine");
		request.setAttribute("datFine", datFine);
		//
		if (row.getAttribute("ROW") != null) {
			// chiusura patto
			setSectionQueryUpdate("QUERY_CHIUDI_PATTO");
			ret = doUpdate(request, response);

			if (!ret) {
				throw new Exception("Impossibile chiudere il patto corrente");
			}
		}

		setSectionQuerySelect("QUERY_SELECT_DICH_DISP");
		row = doSelect(request, response);

		if (row.getAttribute("ROW") != null) {
			// chiusura dichiarazione immediata disponibilita'
			setSectionQueryUpdate("QUERY_CHIUDI_DICH_DISP");
			ret = doUpdate(request, response);

			if (!ret) {
				throw new Exception("Impossibile chiudere la dichiarazione di disponiblita'");
			}

			// stato occupazionale
			String codMotivoFineAtto = (String) request.getAttribute("cdntipocan");
			request.updAttribute("codMotivoFineAtto", codMotivoFineAtto);
			// bisogna inserire lo stato occupazioanale solo se il lavoratore ha
			// la did
			ChiusuraTabelleManager.statoOccupazionale(request, response, this);
		}
		// ripristino lo stato della request
		request.delAttribute("datFine");

		if (datInizio != null) {
			request.setAttribute("datFine", datInizio);
		}
	}

	/*
	 * private boolean hasNuovaAutorizzazione(SourceBean request) { return ((String)
	 * request.getAttribute("PRIVACY_OP")).startsWith("NEW"); }
	 */
	/*
	 * private boolean hasUpdateAutorizzazione(SourceBean request) { return ((String)
	 * request.getAttribute("PRIVACY_OP")).startsWith("UPD"); }
	 */
	private boolean isChiudiAnagrafica(SourceBean request) {
		return (request.getAttribute("DTMCAN") != null) && (((String) request.getAttribute("DTMCAN")).length() > 0);
	}

	private boolean isTrancactionUpdate(SourceBean request) {
		String dataCancellazione = (String) request.getAttribute("DTMCAN");
		return dataCancellazione.length() > 0;
		// return (!((String)
		// request.getAttribute("PRIVACY_OP")).equals("NO_OP")) ||
		// (dataCancellazione.length() > 0);
	}

	/**
	 * aggiorno l'eventuale record di privacy con flgAutoriz = N
	 */
	/*
	 * private void updatePrivacy(SourceBean request, SourceBean response) throws Exception { String newFlagAut =
	 * ((String) request.getAttribute("PRIVACY_OP")).equals("UPD__N") ? "N" : "S"; request.delAttribute("flgAutoriz");
	 * request.setAttribute("flgAutoriz", newFlagAut); setSectionQueryUpdate("QUERY_UPDATE_PRIVACY");
	 * 
	 * if (!doUpdate(request, response)) { throw new Exception("Impossibile aggiornare la privacy del lavoratore"); } }
	 */
	/**
	 * Chiude il record attuale (datfine=sysdate) e crea un nuovo record
	 */
	/*
	 * private void newPrivacy(SourceBean request, SourceBean response) throws Exception { if (!((String)
	 * request.getAttribute("prgPrivacy")).equals("")) { setSectionQueryUpdate("QUERY_UPDATE_PRIVACY");
	 * request.setAttribute("datFinePrivacy", DateUtils.getNow());
	 * 
	 * if (!doUpdate(request, response)) { throw new Exception("Impossibile aggiornare la privacy del lavoratore"); }
	 * 
	 * request.delAttribute("datFinePrivacy"); }
	 * 
	 * setSectionQueryInsert("QUERY_INSERT_PRIVACY"); request.delAttribute("flgAutoriz");
	 * 
	 * String newFlagAut = ((String) request.getAttribute("PRIVACY_OP")).equals("NEW_S") ? "S" : "N";
	 * request.setAttribute("flgAutoriz", newFlagAut);
	 * 
	 * if (!doInsert(request, response)) { throw new Exception("Impossibile inserire una nuova di privacy"); } }
	 */
}

// SaveElAnag
