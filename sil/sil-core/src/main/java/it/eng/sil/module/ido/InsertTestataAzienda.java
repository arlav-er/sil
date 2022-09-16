package it.eng.sil.module.ido;

import java.math.BigDecimal;

import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.utils.CF_utils;
import it.eng.afExt.utils.CfAZException;
import it.eng.afExt.utils.MessageCodes;
import it.eng.afExt.utils.PIVA_utils;
import it.eng.afExt.utils.PatInailUtils;
import it.eng.afExt.utils.PivaException;
import it.eng.afExt.utils.ReportOperationResult;
import it.eng.sil.module.AbstractSimpleModule;

public class InsertTestataAzienda extends AbstractSimpleModule {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(InsertTestataAzienda.class.getName());
	private String className = this.getClass().getName();

	public void service(SourceBean request, SourceBean response) {
		// controlli server side codice fiscale
		String strCodiceFiscale = (String) request.getAttribute("strCodiceFiscale");
		// check partita iva super-FLUO
		String strPartitaIva = (String) request.getAttribute("strPartitaIva");
		String strFlgDatiOk = (String) request.getAttribute("FLGDATIOK");
		String strPatInail = request.containsAttribute("strPatInail") ? request.getAttribute("strPatInail").toString()
				: "";
		ReportOperationResult reportOperation = new ReportOperationResult(this, response);
		boolean canInsertTestata = true;
		// tengo traccia dell'esito del calcolo del codice fiscale
		boolean esito = false;

		if (!strFlgDatiOk.equals("S")) {
			try {
				CF_utils.verificaCFAzienda(strCodiceFiscale);
			} catch (CfAZException cfAZEx) {
				// se il check code non è valido setto esito a true
				esito = true;
				reportOperation.reportFailure(cfAZEx.getMessageIdFail(), null, "verificaCFAzienda", "");
				if (cfAZEx.getMessageIdFail() == MessageCodes.CodiceFiscaleAzienda.ERR_LUNGHEZZA)
					canInsertTestata = false;
			}

			// check partita iva
			// super-FLUO
			// if (!strPartitaIva.equals("")) {
			// 25/10/2004 (savino) : il controllo specifica sulla valorizzazione
			// del campo viene fatto da PIVA_utils.
			// Codice errore: MessageCodes.PartitaIva.NON_VALORIZZATA
			try {
				PIVA_utils.verifyPartitaIva(strPartitaIva);
			} catch (PivaException PEx) {
				reportOperation.reportFailure(PEx.getMessageIdFail(), null, "verifyPartitaIva", "");
				if (PEx.getMessageIdFail() == MessageCodes.PartivaIva.ERR_LUNGHEZZA)
					canInsertTestata = false;
			}
			// }
		}

		int idSuccess = this.disableMessageIdSuccess();
		try {
			BigDecimal prgAzienda = getPrgAzienda(request, response);
			if (prgAzienda != null) {
				// se il check code è valido inserisco
				if (!esito) {
					setKeyinRequest(prgAzienda, request);
					this.setMessageIdSuccess(idSuccess);
					this.setSectionQuerySelect("QUERY_INSERT");

					if (!canInsertTestata) {
						_logger.debug(className + "::service()");

						// reportOperation.reportFailure(MessageCodes.CodiceFiscaleAzienda.ERR_GIA_PRESENTE);
						response.setAttribute("ERR_TESTATA", "");
					}
					if (!doInsert(request, response)) {
						_logger.debug(className + "::service()");

						reportOperation.reportFailure(MessageCodes.CodiceFiscaleAzienda.ERR_GIA_PRESENTE);
						response.setAttribute("ERR_TESTATA", "");
					}
				}
				// altrimenti non inserisco e viene visualizzato un messaggio di
				// errore
				else {
					reportOperation.reportFailure(MessageCodes.General.OPERATION_FAIL);
					// TracerSingleton.log( it.eng.sil.Values.APP_NAME,
					// TracerSingleton.CRITICAL, className + "::service()");
					response.setAttribute("ERR_TESTATA", "");
				}

				if (!strPatInail.equals("")) {
					if (!(PatInailUtils.controllaInail(strPatInail))) {
						reportOperation.reportFailure(MessageCodes.ImportMov.WAR_NUM_INAIL_ERRATO, null,
								"controllaInail", "");
					}
				} else {
					reportOperation.reportFailure(MessageCodes.ImportMov.WAR_NUM_INAIL_NOVALORIZ, null,
							"controllaInail", "");
				}
			} // end if (prgAzienda != null)
		} // end try

		catch (Exception ex) {

			it.eng.sil.util.TraceWrapper.debug(_logger, className + "::service()", ex);

			try {
				response.setAttribute("ERR_TESTATA", "");
			} catch (Exception ex1) {
				it.eng.sil.util.TraceWrapper.debug(_logger, className + "::service()", ex1);

			}

			reportOperation.reportFailure(MessageCodes.General.INSERT_FAIL);
		} // end catch
	}

	private BigDecimal getPrgAzienda(SourceBean request, SourceBean response) throws Exception {
		this.setSectionQuerySelect("QUERY_SEQUENCE");
		SourceBean beanPrgAzienda = (SourceBean) doSelect(request, response);
		return (BigDecimal) beanPrgAzienda.getAttribute("ROW.prgAzienda");
	}

	private void setKeyinRequest(BigDecimal prgAzienda, SourceBean request) throws Exception {
		if (request.getAttribute("PRGAZIENDA") != null) {
			request.delAttribute("PRGAZIENDA");
		}
		request.setAttribute("PRGAZIENDA", prgAzienda);
	}
}