/*
 * Creato il 13-set-05
 *
 * Per modificare il modello associato a questo file generato, aprire
 * Finestra&gt;Preferenze&gt;Java&gt;Generazione codice&gt;Codice e commenti
 */
package it.eng.sil.module.amministrazione;

import java.math.BigDecimal;
import java.util.List;
import java.util.Vector;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.utils.DateUtils;
import it.eng.afExt.utils.MessageCodes;
import it.eng.afExt.utils.ReportOperationResult;
import it.eng.afExt.utils.StringUtils;
import it.eng.afExt.utils.TransactionQueryExecutor;
import it.eng.sil.module.AbstractSimpleModule;
import it.eng.sil.util.Sottosistema;
import it.eng.sil.util.amministrazione.impatti.DBStore;
import it.eng.sil.util.amministrazione.impatti.ListaMobilita;
import it.eng.sil.util.amministrazione.impatti.MobilitaBean;
import it.eng.sil.util.amministrazione.impatti.PattoBean;
import it.eng.sil.util.amministrazione.impatti.SituazioneAmministrativaFactory;
import it.eng.sil.util.amministrazione.impatti.StatoOccupazionaleBean;

/**
 * @author togna
 * 
 *         Per modificare il modello associato al commento di questo tipo generato, aprire
 *         Finestra&gt;Preferenze&gt;Java&gt;Generazione codice&gt;Codice e commenti
 */
public class DeleteMobilita extends AbstractSimpleModule {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(DeleteMobilita.class.getName());
	private TransactionQueryExecutor transactionExecutor;

	public void service(SourceBean serviceRequest, SourceBean serviceResponse) throws Exception {
		ReportOperationResult reportOperation = new ReportOperationResult(this, serviceResponse);
		int idSuccess = this.disableMessageIdSuccess();
		int idFail = this.disableMessageIdFail();
		Object cdnlavoratore = serviceRequest.getAttribute("cdnlavoratore");
		StatoOccupazionaleBean nuovoStatoOcc = null;
		try {
			transactionExecutor = new TransactionQueryExecutor(getPool(), this);
			transactionExecutor.initTransaction();
			enableTransactions(transactionExecutor);
			setSectionQueryDelete("DELETE_DISPONIBILITA_MOBILITA");
			if (!doDelete(serviceRequest, serviceResponse)) {
				throw new Exception("Impossibile eliminare le disponibilità");
			}
			String page = (String) serviceRequest.getAttribute("PAGE");
			if (page != null && page.equalsIgnoreCase("MobilitaInfoStorPage")) {
				setSectionQueryDelete("DELETE_MOBILITA_LAV_PATTO_SCELTA_PRGMOBILITA");
			} else {
				setSectionQueryDelete("DELETE_MOBILITA_LAV_PATTO_SCELTA");
			}
			if (!doDelete(serviceRequest, serviceResponse)) {
				throw new Exception("Impossibile eliminare il legame con il patto");
			}
			setSectionQueryDelete("DELETE_RISULTATO_MOBILITA_VALIDAZIONE");
			if (!doDelete(serviceRequest, serviceResponse)) {
				throw new Exception("Impossibile eliminare la mobilità");
			}
			setSectionQueryDelete("DELETE_MOBILITA");
			if (!doDelete(serviceRequest, serviceResponse)) {
				throw new Exception("Impossibile eliminare la mobilità");
			}
			if (Sottosistema.MO.isOn()) {
				boolean chiudiPatto = true;
				SourceBean patto = null;
				List patti = PattoBean.getPatti(cdnlavoratore, transactionExecutor);
				if (patti.size() > 0) {
					// Recupero eventuale altri periodi di mobilità del
					// lavoratore
					ListaMobilita mobilita = new ListaMobilita(cdnlavoratore, transactionExecutor);
					Vector rowsMobilita = mobilita.getMobilita();
					for (int i = 0; i < patti.size(); i++) {
						chiudiPatto = true;
						SourceBean sb = (SourceBean) patti.get(i);
						String dataStipula = (String) sb.getAttribute("datStipula");
						String dataFinePatto = (String) sb.getAttribute("datFine");
						// un solo patto deve risultare aperto(quelli già chiusi
						// non li considero)
						if (dataFinePatto != null)
							continue;
						BigDecimal prgDichDispo = (BigDecimal) sb.getAttribute(PattoBean.PRG_DICH_DISPO);
						if (prgDichDispo == null) {
							for (int k = 0; k < rowsMobilita.size(); k++) {
								SourceBean sbMobilita = (SourceBean) rowsMobilita.get(k);
								String dataInizioMob = sbMobilita.getAttribute(MobilitaBean.DB_DAT_INIZIO).toString();
								if (DateUtils.compare(dataStipula, dataInizioMob) >= 0) {
									chiudiPatto = false;
									break;
								}
							}
						} else {
							chiudiPatto = false;
						}
						if (chiudiPatto) {
							DBStore.chiudiPatto297(cdnlavoratore, DateUtils.getNow(), "PU",
									RequestContainer.getRequestContainer(), transactionExecutor);
						}
					}
				}

				boolean permettiImpatti = UtilsMobilita.controllaPermessi(new BigDecimal(cdnlavoratore.toString()),
						transactionExecutor);
				// ricostruisco la storia del lavoratore
				if (permettiImpatti) {
					nuovoStatoOcc = ricalcolaStatoOccupazionale(serviceRequest, serviceResponse);
				} else {
					reportOperation.reportSuccess(MessageCodes.ImportMov.WAR_NO_COMPETENZA_LAV);
				}
			}
			transactionExecutor.commitTransaction();
			this.setMessageIdSuccess(idSuccess);
			reportOperation.reportSuccess(MessageCodes.General.OPERATION_SUCCESS);
		} catch (Exception e) {
			transactionExecutor.rollBackTransaction();
			this.setMessageIdFail(idFail);
			reportOperation.reportFailure(MessageCodes.General.OPERATION_FAIL, e, "service", "");
			it.eng.sil.util.TraceWrapper.debug(_logger, "DeleteMobilita.service()", e);

		}
	}

	private StatoOccupazionaleBean ricalcolaStatoOccupazionale(SourceBean serviceRequest, SourceBean serviceResponse)
			throws Exception {
		// forzature in ricalcolo impatti
		if (!serviceRequest.containsAttribute("FORZA_INSERIMENTO"))
			serviceRequest.setAttribute("FORZA_INSERIMENTO", "true");
		else
			serviceRequest.updAttribute("FORZA_INSERIMENTO", "true");
		if (!serviceRequest.containsAttribute("CONTINUA_CALCOLO_SOCC"))
			serviceRequest.setAttribute("CONTINUA_CALCOLO_SOCC", "true");
		else
			serviceRequest.updAttribute("CONTINUA_CALCOLO_SOCC", "true");
		// ricalcolo impatti
		StatoOccupazionaleBean statoOccupazionale = SituazioneAmministrativaFactory
				.newInstance(StringUtils.getAttributeStrNotNull(serviceRequest, "cdnLavoratore"),
						StringUtils.getAttributeStrNotNull(serviceRequest, "datInizio"), transactionExecutor)
				.calcolaImpatti();
		return statoOccupazionale;

	}

}