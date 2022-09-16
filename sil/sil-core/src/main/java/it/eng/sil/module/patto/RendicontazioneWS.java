package it.eng.sil.module.patto;

import java.math.BigDecimal;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.dbaccess.sql.DataConnection;
import com.engiweb.framework.message.MessageBundle;
import com.engiweb.framework.util.QueryExecutorObject;

import it.eng.afExt.utils.MessageCodes;
import it.eng.afExt.utils.ReportOperationResult;
import it.eng.sil.module.AbstractSimpleModule;
import it.eng.sil.module.seta.ErroreSeta;
import it.eng.sil.module.seta.RendicontazioneUtils;
import it.eng.sil.util.blen.StringUtils;

public class RendicontazioneWS extends AbstractSimpleModule {

	private static final long serialVersionUID = -6816573620797609037L;

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(RendicontazioneWS.class.getName());

	@Override
	public void service(SourceBean serviceRequest, SourceBean serviceResponse) throws Exception {

		ReportOperationResult reportOperation = new ReportOperationResult(this, serviceResponse);
		disableMessageIdFail();
		disableMessageIdSuccess();

		String strCdnLavoratore = (String) serviceRequest.getAttribute("cdnlavoratore");
		BigDecimal bdCdnLavoratore = null;

		if (!StringUtils.isEmpty(strCdnLavoratore)) {
			bdCdnLavoratore = new BigDecimal(strCdnLavoratore);
		} else {
			_logger.error("RendicontazioneWS: impossibile recuperare cdnlavoratore");
			return;
		}

		this.setSectionQuerySelect("XML_GET_PARTECIPANTE");
		SourceBean rowGetPartecipante = doSelect(serviceRequest, serviceResponse);
		/*
		 * if (rowGetPartecipante != null) { String partecipanteCdnLavoratore =
		 * rowGetPartecipante.containsAttribute("row.cdnlavoratore")?rowGetPartecipante.getAttribute("row.cdnlavoratore"
		 * ).toString():"0"; }
		 */

		this.setSectionQuerySelect("XML_GET_Partecipante_GG_DatiGiovane_codprovincia");
		SourceBean rowGetPartecipanteDatiGiovaneCodProv = doSelect(serviceRequest, serviceResponse);

		this.setSectionQuerySelect("XML_GET_PARTECIPANTE_PROFILING_PATTO");
		SourceBean rowGetPartecipanteProfilingPatto = doSelect(serviceRequest, serviceResponse);

		this.setSectionQuerySelect("XML_GET_Partecipante_GG_Movimentazione");
		SourceBean rowGetPartecipanteMovimentazione = doSelect(serviceRequest, serviceResponse);

		this.setSectionQuerySelect("XML_GET_WS_CREDENTIALS");
		SourceBean rowGetWsCredentials = doSelect(serviceRequest, serviceResponse);

		_logger.debug("Inoltra Rendicontazione da pagina Lista Colloqui chiamata sendPartecipante cdnlavoratore:"
				+ strCdnLavoratore);
		RendicontazioneUtils rendicontazione = new RendicontazioneUtils();
		boolean isFromBatch = false;
		ErroreSeta erroreSeta = rendicontazione.sendPartecipante(bdCdnLavoratore, rowGetPartecipante,
				rowGetPartecipanteDatiGiovaneCodProv, rowGetPartecipanteProfilingPatto,
				rowGetPartecipanteMovimentazione, rowGetWsCredentials, isFromBatch);
		_logger.debug("Inoltra Rendicontazione da pagina Lista Colloqui return sendPartecipanteGG erroreSifer:"
				+ erroreSeta.errCod);

		if (erroreSeta.errCod != 0) {

			String dettaglioErrore = "";
			if (erroreSeta.erroreEsteso != null) {
				dettaglioErrore = erroreSeta.erroreEsteso;
			}

			DataConnection dc = null;
			try {
				QueryExecutorObject qExec = PartecipanteGGUtils.getQueryExecutorObject();
				dc = qExec.getDataConnection();

				// traccio l'errore sul DB
				RendicontazioneUtils.tracciaErrore(dc.getInternalConnection(), bdCdnLavoratore, erroreSeta);
			} catch (Throwable e) {
				_logger.error("RendicontazioneWS: impossibile tracciare l'errore", (Exception) e);
			} finally {
				if (dc != null) {
					dc.close();
				}
			}

			// RISPOSTA
			if (MessageCodes.RENDICONTAZIONE.ERR_WS_DATI_LAVORATORE == erroreSeta.errCod) {
				int koWs = MessageCodes.RENDICONTAZIONE.ERR_WS_DATI_LAVORATORE;
				reportOperation.reportFailure(erroreSeta.errCod, "", "", MessageBundle.getMessage(String.valueOf(koWs)),
						erroreSeta.params);
			} else {
				reportOperation.reportFailure(erroreSeta.errCod, "", "", dettaglioErrore, erroreSeta.params);
			}
		} else {
			reportOperation.reportSuccess(MessageCodes.YG.WS_PARTECIPANTE_OK);
		}

		/*
		 * mostro l'elenco delle azioni non inviate, sempre escluso il caso in cui non abbia inviato niente a causa dei
		 * dati obbligatori mancanti
		 */
		/*
		 * if (erroreSeta.errCod != MessageCodes.YG.ERR_WS_PARTECIPANTE_GG_DATI_LAVORATORE) { // aggiungiamo possibile
		 * polite attive non valise reportPoliticheAttiveNonValide(politicheAttiveNonValideBeanRows, reportOperation); }
		 */

		return;

	}

}
