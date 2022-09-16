/*
 * Created on Aug 30, 2006
 */
package it.eng.sil.module.anag;

import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.utils.CF_utils;
import it.eng.afExt.utils.CfException;
import it.eng.afExt.utils.MessageCodes;
import it.eng.afExt.utils.ReportOperationResult;
import it.eng.sil.module.AbstractSimpleModule;
import it.eng.sil.util.Utils;

/**
 * @author savino
 */
public class CheckCodiceFiscale extends AbstractSimpleModule {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(CheckCodiceFiscale.class.getName());

	public void service(SourceBean serviceRequest, SourceBean serviceResponse) {
		ReportOperationResult reportOperation = new ReportOperationResult(this, serviceResponse);
		SourceBean lavoratore = (SourceBean) serviceRequest.getAttribute("M_GetLavoratoreAnag.rows.row");
		if (lavoratore == null) {
			reportOperation.reportFailure(MessageCodes.General.OPERATION_FAIL);
			_logger.debug(
					"Tentativo di controllo validita' codice fiscale per un lavoratore i cui dati sono stati aggiornati/inseriti da remoto tramite scheda lavoratore in cooperazione. OPERAZIONE FALLITA. Dati del lavoratore non trovati dal modulo");

			return;
		}
		String cognome, nome, codiceFiscale, dataNascita, codComuneNascita, sesso;
		String operazione = Utils.notNull(serviceRequest.getAttribute("OPERAZIONE"));
		if (operazione.equals("AGGIORNAMENTO_SCHEDA_LAVORATORE")) {
			// mostro il messaggio di aggiornamento eseguito con successo, ma
			// solo se aggiorno i dati
			// nel caso di inserimento di un lavoratore non presente non mostro
			// alcun messaggio di esito operazione
			reportOperation.reportSuccess(MessageCodes.General.UPDATE_SUCCESS);
		}
		codiceFiscale = (String) lavoratore.getAttribute("strCodiceFiscale");
		cognome = (String) lavoratore.getAttribute("strCognome");
		nome = (String) lavoratore.getAttribute("strNome");
		dataNascita = (String) lavoratore.getAttribute("datNasc");
		sesso = (String) lavoratore.getAttribute("strSesso");
		codComuneNascita = (String) lavoratore.getAttribute("codComNas");

		try {
			CF_utils.verificaCF(codiceFiscale, nome, cognome, sesso, dataNascita, codComuneNascita);
		} catch (CfException cfEx) {
			reportOperation.reportSuccess(cfEx.getMessageIdFail());
		} catch (Exception e) {
			reportOperation.reportFailure(MessageCodes.General.OPERATION_FAIL);
			it.eng.sil.util.TraceWrapper.debug(_logger, "Controllo codice fiscale per il lavoratore " + codiceFiscale
					+ " fallito. Dati lavoratore letti: " + lavoratore, e);

		}
	}

}