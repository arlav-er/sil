package it.eng.sil.module.amministrazione;

import java.util.Vector;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.dispatching.module.AbstractModule;
import com.engiweb.framework.error.EMFErrorSeverity;
import com.engiweb.framework.error.EMFInternalError;
import com.engiweb.framework.error.EMFUserError;
import com.engiweb.framework.util.QueryExecutor;

import it.eng.afExt.utils.FileUtils;
import it.eng.afExt.utils.MessageCodes;
import it.eng.afExt.utils.SourceBeanUtils;

/**
 * @author Luigi Antenucci
 */
public class EsportaMigrSetInvioModule extends AbstractModule {

	public void service(SourceBean request, SourceBean response) throws Exception {

		try {

			// Recupero record con i dati correnti
			SourceBean row = EsportaMigrUtils.recuperaDati();

			// Controllo correttezza dati recuperati e azione da fare
			faiControlli(request, row);

			// Salvo il nuovo stato dei dati
			aggiornaDati(request, row);

		} catch (EMFUserError ue) {
			getErrorHandler().addError(ue);
		}
	}

	/**
	 * Controllo correttezza dati recuperati e azione da fare
	 */
	private void faiControlli(SourceBean request, SourceBean row) throws EMFUserError {

		try {
			boolean flgInvio = SourceBeanUtils.getAttrBoolean(row, "FLGINVIO");
			boolean flgInCorso = SourceBeanUtils.getAttrBoolean(row, "FLGINCORSO");

			boolean flgInvioNuovo = SourceBeanUtils.getAttrBoolean(request, "FLGINVIO");

			if (flgInCorso) {
				throw new EMFUserError(EMFErrorSeverity.BLOCKING,
						MessageCodes.EsportaMigrazioni.ERR_SETINVIO_FLGINCORSO);
			}

			// Recupero e controllo il path in cui salvare
			String savetoPath = EsportaMigrUtils.getAndCheckSaveToPath(row);

			Vector paramV = new Vector(2);
			paramV.add(savetoPath);
			paramV.add(flgInvioNuovo ? "S&igrave;" : "No");
			// Se si passa da "N" a "S", voglio che la dir "savetoPath" sia
			// VUOTA.
			if (!flgInvio && flgInvioNuovo) {
				try {
					FileUtils.checkNoFilesInPath(savetoPath, 1); // genera
																	// eccezione
																	// se almeno
																	// un file
																	// esiste
				} catch (Exception ex) {
					throw new EMFUserError(EMFErrorSeverity.BLOCKING,
							MessageCodes.EsportaMigrazioni.ERR_SETINVIO_PATH_NOT_EMPTY, paramV);
				}
			}
			// Se si passa da "S" a "N", voglio che la dir "savetoPath" sia
			// NON-VUOTA.
			else if (flgInvio && !flgInvioNuovo) {
				boolean isVuota = false;
				try {
					FileUtils.checkNoFilesInPath(savetoPath, 1); // genera
																	// eccezione
																	// se almeno
																	// un file
																	// esiste
					isVuota = true; // Se sono qui, la dir. è vuota.
				} catch (Exception ex) {
				}
				if (isVuota) {
					throw new EMFUserError(EMFErrorSeverity.BLOCKING,
							MessageCodes.EsportaMigrazioni.ERR_SETINVIO_PATH_EMPTY, paramV);
				}
			}
			// altrimenti: che cavolo vuoi cambiare?
			else {
				throw new EMFUserError(EMFErrorSeverity.BLOCKING,
						MessageCodes.EsportaMigrazioni.ERR_SETINVIO_NULLA_DA_FARE);
			}

		} catch (EMFUserError ue) {
			throw ue;
		} catch (EMFInternalError ie) {
			throw new EMFUserError(EMFErrorSeverity.BLOCKING, MessageCodes.EsportaMigrazioni.ERR_SETINVIO_CONTROLLO);
		}
	}

	/**
	 * Controllo correttezza dati recuperati e azione da fare
	 */
	private void aggiornaDati(SourceBean request, SourceBean row) throws EMFUserError {

		// SALVATAGGIO NUOVO STATO
		try {
			SourceBean config = getConfig();
			String pool = (String) config.getAttribute("POOL");
			SourceBean upQuery = (SourceBean) config.getAttribute("UPDATE_QUERY");

			// I parametri li ficco nella "request" così che il QueryExecutor
			// possa leggerli.
			request.setAttribute("PRGMIGRAZIONE", row.getAttribute("PRGMIGRAZIONE"));

			Boolean done = (Boolean) QueryExecutor.executeQuery(getRequestContainer(), getResponseContainer(), pool,
					upQuery, QueryExecutor.UPDATE);

			if ((done == null) || (!done.booleanValue())) {
				throw new Exception("Errore durante il salvataggio");
			}

		} catch (Exception e) {
			throw new EMFUserError(EMFErrorSeverity.BLOCKING, MessageCodes.EsportaMigrazioni.ERR_SETINVIO_CANTDOIT);
		}
	}

}