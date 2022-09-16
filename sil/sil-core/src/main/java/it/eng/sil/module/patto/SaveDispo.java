package it.eng.sil.module.patto;

import java.util.ArrayList;

import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.utils.DateUtils;
import it.eng.afExt.utils.MessageCodes;
import it.eng.afExt.utils.ReportOperationResult;
import it.eng.afExt.utils.TransactionQueryExecutor;
import it.eng.sil.module.AbstractSimpleModule;
import it.eng.sil.module.movimenti.processors.ProcessorsUtils;
import it.eng.sil.module.patto.bean.Patto;
import it.eng.sil.util.Utils;
import it.eng.sil.util.amministrazione.impatti.ControlliException;
import it.eng.sil.util.amministrazione.impatti.DBLoad;
import it.eng.sil.util.amministrazione.impatti.MobilitaException;
import it.eng.sil.util.amministrazione.impatti.ProTrasfoException;
import it.eng.sil.util.amministrazione.impatti.SituazioneAmministrativaFactory;
import it.eng.sil.util.amministrazione.impatti.StatoOccupazionaleBean;

/**
 * NOTE: 1) nel caso sia presente la dichiarazione non viene fatto alcun controllo sel flag (S o N) 2) se non ci sono
 * movimenti non faccio niente con lo stato occupazionale 3) problema: se e' presente uno stato occupazionale precedente
 * cosa faccio? Lo aggiorno o lo chiudo?
 */
public class SaveDispo extends AbstractSimpleModule {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(SaveDispo.class.getName());

	public void service(SourceBean request, SourceBean response) throws Exception {
		ReportOperationResult reportOperation = new ReportOperationResult(this, response);
		if (chiudiAtto(request)) {
			TransactionQueryExecutor transExec = null;
			int idSuccess = this.disableMessageIdSuccess();
			int idFail = this.disableMessageIdFail();
			SourceBean rowWarning = null;
			try {
				if (!operazioneNonCorretta(request)) {
					transExec = new TransactionQueryExecutor(getPool(), this);
					enableTransactions(transExec);
					transExec.initTransaction();
					//
					if (verificaStatoAttoInChiusura(request, response)) {
						String dataRif = (String) request.getAttribute("datDichiarazione");
						String dataFine = (String) request.getAttribute("datFine");
						String cdnLavoratore = Utils.notNull(request.getAttribute("cdnLavoratore"));
						String dataAttuale = DateUtils.getNow();
						StatoOccupazionaleBean statoOccupazionaleAperto = DBLoad.getStatoOccupazionale(cdnLavoratore,
								transExec);
						if (statoOccupazionaleAperto.getProgressivoDB() == null)
							statoOccupazionaleAperto = StatoOccupazionaleBean.creaStatoOccupazionaleIniziale(request);
						String dataStatoOcc = statoOccupazionaleAperto.getDataInizio();
						if (DateUtils.compare(dataFine, dataAttuale) <= 0 && dataStatoOcc != null) {
							String dataInizio = dataRif;
							if (!doUpdate(request, response))
								throw new Exception("impossibile aggiornare am_dich_disponibilita");

							// se esiste un patto 150, anche se non
							// protocollato, viene chiuso
							ChiusuraTabelleManager.patto297(request, response, this);
							// ricalcolo impatti
							StatoOccupazionaleBean statoOccFinale = SituazioneAmministrativaFactory
									.newInstance(cdnLavoratore, dataFine, transExec).calcolaImpatti();
						} else {
							// chiusura standard
							ChiusuraTabelleManager.statoOccupazionale(request, response, this);
							if (request.containsAttribute("PRGSTATOOCCUPAZ")) {
								request.delAttribute("PRGSTATOOCCUPAZ");
							}
							if (!doUpdate(request, response))
								throw new Exception("impossibile aggiornare am_dich_disponibilita");
							// se esiste un patto 150, anche se non
							// protocollato, viene chiuso
							ChiusuraTabelleManager.patto297(request, response, this);
							// eventuale riapertura accordo generico
							Patto ptAccordo = new Patto();
							ptAccordo.controllaAperturaAccordoGenerico(cdnLavoratore, getRequestContainer(), transExec);
						}
						transExec.commitTransaction();
						reportOperation.reportSuccess(MessageCodes.General.UPDATE_SUCCESS);
					} else {
						transExec.rollBackTransaction();
						reportOperation.reportFailure(MessageCodes.DID.CHIUSURA_DID_DA_PR_A_PA_NON_AMMESSA);
					}
				} else {
					reportOperation.reportFailure(MessageCodes.DID.ANNULLADID_CHIUSURADID_NON_POSSIBILE);
				}
			}

			catch (MobilitaException me) {
				if (transExec != null) {
					transExec.rollBackTransaction();
				}
				it.eng.sil.util.TraceWrapper.debug(_logger, "Errore in chiusura did.", (Exception) me);

				addConfirm(request, response, me.getCode());
			}

			catch (ControlliException ce) {
				if (transExec != null) {
					transExec.rollBackTransaction();
				}
				it.eng.sil.util.TraceWrapper.debug(_logger, "Errore in chiusura did.", (Exception) ce);

				addConfirm(request, response, ce.getCode());
			}

			catch (ProTrasfoException proTraEx) {
				reportOperation.reportFailure(proTraEx.getCode());
				if (transExec != null) {
					transExec.rollBackTransaction();
				}
				it.eng.sil.util.TraceWrapper.debug(_logger, "Errore in chiusura did", (Exception) proTraEx);

			}

			catch (Exception e) {
				// rollback
				reportOperation.reportFailure(MessageCodes.General.UPDATE_FAIL, e, "execute()",
						"chiusura dichiarazione disponibilità in transazione fallita");
				if (transExec != null)
					transExec.rollBackTransaction();
				it.eng.sil.util.TraceWrapper.debug(_logger, "Errore in chiusura did", e);

			}
		} else {
			doUpdate(request, response);
		}
	}

	private boolean chiudiAtto(SourceBean request) {
		String dataFine = (String) request.getAttribute("DATFINE");
		return (dataFine != null && dataFine.length() > 0);
	}

	private boolean operazioneNonCorretta(SourceBean request) {
		String dataFine = (String) request.getAttribute("DATFINE");
		String codStatoAtto = (String) request.getAttribute("CODSTATOATTO");
		boolean chiusuraDid = (dataFine != null && dataFine.length() > 0);
		boolean annullaDid = (codStatoAtto != null && !codStatoAtto.equals("PA") && !codStatoAtto.equals("PR"));
		return (chiusuraDid && annullaDid);
	}

	private boolean verificaStatoAttoInChiusura(SourceBean request, SourceBean response) throws Exception {
		this.setSectionQuerySelect("QUERY_SELECT_CODSTATOATTO_DID");
		SourceBean row = this.doSelect(request, response, false);
		if (row == null)
			throw new Exception("impossibile leggere la did del lavoratore");
		String codStatoAtto = row.containsAttribute("ROW.CODSTATOATTO")
				? row.getAttribute("ROW.CODSTATOATTO").toString()
				: "";
		String codStatoAttoNew = (String) request.getAttribute("CODSTATOATTO");
		if (codStatoAtto.equalsIgnoreCase("PR") && codStatoAttoNew.equalsIgnoreCase("PA")) {
			return false;
		} else {
			return true;
		}
	}

	private void addConfirm(SourceBean request, SourceBean response, int code) throws Exception {
		String forzaRicostruzione = "";
		String continuaRicalcoloSOccManuale = "";
		ArrayList warnings = new ArrayList();
		ArrayList nested = new ArrayList();
		switch (code) {
		case MessageCodes.StatoOccupazionale.STATO_OCCUPAZIONALE_MANUALE:
		case MessageCodes.StatoOccupazionale.CONSERVA_STATO_OCCUPAZIONALE_MANUALE:
		case MessageCodes.StatoOccupazionale.STATO_OCC_CON_MESI_SOSP_ANZ_NON_CALCOLATI:
		case MessageCodes.StatoOccupazionale.STATO_OCC_CON_DATA_ANZIANITA_ERRATA:
			continuaRicalcoloSOccManuale = "true";
			if (request.containsAttribute("FORZA_INSERIMENTO")) {
				forzaRicostruzione = request.getAttribute("FORZA_INSERIMENTO").toString();
			}
			break;
		default:
			forzaRicostruzione = "true";
			if (request.containsAttribute("CONTINUA_CALCOLO_SOCC")) {
				continuaRicalcoloSOccManuale = request.getAttribute("CONTINUA_CALCOLO_SOCC").toString();
			}

		}
		SourceBean puResult = ProcessorsUtils.createResponse("", "SaveDispo", new Integer(code),
				"Situazione di incongruenza", warnings, nested);
		ProcessorsUtils.addConfirm(puResult, "Vuoi forzare l' operazione?", "ripetiOperazione",
				new String[] { forzaRicostruzione, continuaRicalcoloSOccManuale }, true);
		SourceBean sb = new SourceBean("RECORD");
		sb.setAttribute("RESULT", "ERROR");
		sb.setAttribute(puResult);
		response.setAttribute(sb);
	}

}