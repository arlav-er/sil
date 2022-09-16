package it.eng.sil.module.patto;

import it.eng.afExt.utils.MessageCodes;
import it.eng.afExt.utils.ReportOperationResult;
import it.eng.sil.coop.webservices.sifer.ErroreSifer;
import it.eng.sil.module.AbstractSimpleModule;
import it.eng.sil.module.movimenti.constant.Properties;
import it.eng.sil.util.InfoRegioneSingleton;
import it.eng.sil.util.amministrazione.impatti.DBLoad;

import java.math.BigDecimal;
import java.util.Vector;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.dbaccess.sql.DataConnection;
import com.engiweb.framework.util.QueryExecutorObject;

public class PartecipanteGG extends AbstractSimpleModule {

	/**
	 * 
	 */
	private static final long serialVersionUID = -427006718869451470L;

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(PartecipanteGG.class.getName());

	@Override
	public void service(SourceBean serviceRequest, SourceBean serviceResponse) throws Exception {
		String strCdnLavoratore = null;
		BigDecimal cdnLavoratore = null;
		if (serviceRequest != null) {
			strCdnLavoratore = (String) serviceRequest.getAttribute("CDNLAVORATORE");
			try {
				cdnLavoratore = new BigDecimal(strCdnLavoratore);
			} catch (NumberFormatException e) {
				_logger.error("PartecipanteGG: impossibile recuperare cdnlavoratore");
				return;
			}
		} else {
			_logger.error("PartecipanteGG: impossibile recuperare cdnlavoratore");
			return;
		}

		ReportOperationResult reportOperation = new ReportOperationResult(this, serviceResponse);
		disableMessageIdFail();
		disableMessageIdSuccess();
		
		String codRegioneSil = InfoRegioneSingleton.getInstance().getCodice();

		setSectionQuerySelect("GET_GG_DATIGIOVANE_ANLAV");
		SourceBean anLavBeanRows = doSelect(serviceRequest, serviceResponse, false);
		setSectionQuerySelect("GET_GG_DATIGIOVANE_PERMSOGG");
		SourceBean permSoggBeanRows = doSelect(serviceRequest, serviceResponse, false);
		setSectionQuerySelect("GET_GG_DATIGIOVANE_CODPROVINCIA");
		SourceBean codProvinciaBeanRows = doSelect(serviceRequest, serviceResponse, false);
		setSectionQuerySelect("GET_GG_DATIGIOVANE_TITSTUDIO");
		SourceBean titStudioBeanRows = doSelect(serviceRequest, serviceResponse, false);
		if (codRegioneSil != null && codRegioneSil.equalsIgnoreCase(Properties.TN)) {
			setSectionQuerySelect("GET_GG_DATIPROFILING_PATTO_DYN");
		}
		else {
			setSectionQuerySelect("GET_GG_DATIPROFILING_PATTO");
		}
		SourceBean pattoBeanRows = doSelect(serviceRequest, serviceResponse, false);
		setSectionQuerySelect("GET_GG_POLITICHEATTIVENONVALIDE");
		SourceBean politicheAttiveNonValideBeanRows = doSelect(serviceRequest, serviceResponse, false);
		setSectionQuerySelect("GET_GG_MOVIMENTAZIONE");
		SourceBean movimentazioneBeanRows = doSelect(serviceRequest, serviceResponse, false);
		setSectionQuerySelect("GET_AZ_PATTO_ATTIVAZIONE_REG_GG");
		SourceBean pattoRegionaleRows = doSelect(serviceRequest, serviceResponse, false);
		setSectionQuerySelect("GET_DATI_CONFERIMENTO_DID");
		SourceBean conferimentoRows = doSelect(serviceRequest, serviceResponse, false);
		setSectionQuerySelect("GET_GG_CRED_WS");
		SourceBean wsRows = doSelect(serviceRequest, serviceResponse, false);

		ErroreSifer erroreSifer = PartecipanteGGUtils.sendPartecipanteGG(cdnLavoratore, anLavBeanRows,
				permSoggBeanRows, codProvinciaBeanRows, titStudioBeanRows, pattoBeanRows, movimentazioneBeanRows,
				wsRows, pattoRegionaleRows, conferimentoRows);
		if (erroreSifer.errCod != 0) {
			/* traccio errore in DB */
			DataConnection dc = null;
			try {
				QueryExecutorObject qExec = PartecipanteGGUtils.getQueryExecutorObject();
				dc = qExec.getDataConnection();
				PartecipanteGGUtils.tracciaErrore(dc.getInternalConnection(), cdnLavoratore, erroreSifer);
				String dettaglioErrore = "";
				if (erroreSifer.erroreEsteso != null) {
					dettaglioErrore = erroreSifer.erroreEsteso;
				}
				reportOperation.reportFailure(erroreSifer.errCod, "", "", dettaglioErrore, erroreSifer.params);
			}
			catch (Throwable e) {
				_logger.error("PartecipanteGG: impossibile tracciare l'errore", (Exception) e);
			}
			finally {
				if (dc != null) {
					dc.close();
				}
			}
		} else {
			reportOperation.reportSuccess(MessageCodes.YG.WS_PARTECIPANTE_OK);
		}
		/*
		 * mostro l'elenco delle azioni non inviate, sempre escluso il caso in
		 * cui non abbia inviato niente a causa dei dati obbligatori mancanti
		 */
		if (erroreSifer.errCod != MessageCodes.YG.ERR_WS_PARTECIPANTE_GG_DATI_LAVORATORE) {
			// aggiungiamo possibile polite attive non valise
			reportPoliticheAttiveNonValide(politicheAttiveNonValideBeanRows, reportOperation);
		}
		return;
	}

	/*
	 * Crea un report di errore sulla pagina per ogni politica attica con azione
	 * di tipo formazione ma esito non di tipo formazione
	 */
	private void reportPoliticheAttiveNonValide(SourceBean politicheAttiveNonValideBeanRows,
			ReportOperationResult reportOperation) {
		if (politicheAttiveNonValideBeanRows != null) {
			Vector politicheAttiveNonValideBeanVector = politicheAttiveNonValideBeanRows.getAttributeAsVector("ROW");

			String descrizione = null;

			for (int i = 0; i < politicheAttiveNonValideBeanVector.size(); i++) {
				SourceBean politicheAttiveNonValideBeanRow = (SourceBean) politicheAttiveNonValideBeanVector
						.elementAt(i);

				descrizione = (String) politicheAttiveNonValideBeanRow.getAttribute("STRDESCRIZIONE");

				Vector<String> paramV = new Vector<String>(1);
				paramV.add(descrizione);
				reportOperation.reportFailure(MessageCodes.YG.ERR_WS_PARTECIPANTE_GG_POLITICA_ATTIVA, "WS SIL-SIFER",
						"ERRORE INTERNO", paramV);
			}
		}
	}

}
