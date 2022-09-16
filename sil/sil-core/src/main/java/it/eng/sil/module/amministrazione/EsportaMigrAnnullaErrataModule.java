package it.eng.sil.module.amministrazione;

import java.util.Calendar;
import java.util.Date;
import java.util.Vector;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.dispatching.module.AbstractModule;
import com.engiweb.framework.error.EMFErrorSeverity;
import com.engiweb.framework.error.EMFUserError;
import com.engiweb.framework.util.QueryExecutor;

import it.eng.afExt.utils.FileUtils;
import it.eng.afExt.utils.MessageAppender;
import it.eng.afExt.utils.MessageCodes;
import it.eng.afExt.utils.SourceBeanUtils;

/**
 * @author Luigi Antenucci
 */
public class EsportaMigrAnnullaErrataModule extends AbstractModule {

	public void service(SourceBean request, SourceBean response) throws Exception {

		try {

			// Recupero record con i dati correnti
			SourceBean row = EsportaMigrUtils.recuperaDati();

			// Controllo correttezza dati recuperati e azione da fare
			faiControlli(request, row);

			// Salvo il nuovo stato dei dati
			annullaErrata(request, response, row);

		} catch (EMFUserError ue) {
			getErrorHandler().addError(ue);
		}
	}

	/**
	 * Controllo correttezza dati recuperati e azione da fare
	 */
	private void faiControlli(SourceBean request, SourceBean row) throws EMFUserError {

		int timeOut;
		timeOut = SourceBeanUtils.getAttrInt(getConfig(), "TIMEOUT.MINIMAL", 30);
		timeOut = SourceBeanUtils.getAttrInt(row, "NUMMINUTIMIGRAZIONI", timeOut);

		// 1. flgInCorso = 'S'
		boolean flgInCorso = SourceBeanUtils.getAttrBoolean(row, "FLGINCORSO", false);
		if (!flgInCorso) {
			throw new EMFUserError(EMFErrorSeverity.BLOCKING, MessageCodes.EsportaMigrazioni.ERR_ANNULLA_NONINCORSO);
		}
		// 2. flgInvio = 'N'
		// Savino 01/02/2006: controllo come da specifiche. Parrebbe inutile
		boolean flgInvio = SourceBeanUtils.getAttrBoolean(row, "FLGINVIO", false);
		if (flgInvio) {
			throw new EMFUserError(EMFErrorSeverity.BLOCKING, MessageCodes.EsportaMigrazioni.ERR_ANNULLA_NONINVIATO);
		}
		// 3. data e ora corrente - data ora inizio elaborazione > 30 minuti
		String dataLancio = SourceBeanUtils.getAttrStr(row, "DATALANCIO");

		try {
			// Data di avvio
			Date dumDate = EsportaMigrUtils.convertDate(dataLancio);

			// Orario attuale meno il timeout
			Calendar calendar = Calendar.getInstance();
			calendar.add(Calendar.MINUTE, -timeOut);
			Date outDate = calendar.getTime();

			if (dumDate.after(outDate)) { // non sono fuori dal tempo limite
				throw new Exception("non fuori time out!");
			}
		} catch (Exception e) {
			Vector paramV = new Vector(2);
			paramV.add(dataLancio);
			paramV.add(String.valueOf(timeOut));
			throw new EMFUserError(EMFErrorSeverity.BLOCKING, MessageCodes.EsportaMigrazioni.ERR_ANNULLA_ENTRO_TIMEOUT,
					paramV);
		}
	}

	/**
	 * Controllo correttezza dati recuperati e azione da fare
	 */
	private void annullaErrata(SourceBean request, SourceBean response, SourceBean row) throws EMFUserError {

		// Recupero e controllo il path in cui salvare
		String savetoPath = EsportaMigrUtils.getAndCheckSaveToPath(row);

		/*
		 * 1) CANCELLO L'ULTIMO RECORD DI STATO (quello da cui ho appena letto le info)
		 */
		try {
			SourceBean config = getConfig();
			String pool = (String) config.getAttribute("POOL");
			SourceBean upQuery = (SourceBean) config.getAttribute("DELETE_QUERY");

			// I parametri li ficco nella "request" così che il QueryExecutor
			// possa leggerli.
			request.setAttribute("PRGMIGRAZIONE", row.getAttribute("PRGMIGRAZIONE"));

			Boolean done = (Boolean) QueryExecutor.executeQuery(getRequestContainer(), getResponseContainer(), pool,
					upQuery, QueryExecutor.DELETE);

			if ((done == null) || (!done.booleanValue())) {
				throw new Exception("Errore durante la cancellazione");
			}
		} catch (Exception e) {
			throw new EMFUserError(EMFErrorSeverity.BLOCKING, MessageCodes.EsportaMigrazioni.ERR_ANNULLA_CANTDELRECORD);
		}

		/*
		 * 2) CANCELLO TUTTI I FILE NELLA DIRECTORY DI SALVATAGGIO
		 */
		boolean success = FileUtils.deleteAllFilesInPath(savetoPath, 1, false);
		if (!success) {
			throw new EMFUserError(EMFErrorSeverity.BLOCKING, MessageCodes.EsportaMigrazioni.ERR_ANNULLA_CANTDELFILES);
		}

		MessageAppender.appendMessage(response, MessageCodes.EsportaMigrazioni.OK_ANNULLA_FATTO);
	}

}
