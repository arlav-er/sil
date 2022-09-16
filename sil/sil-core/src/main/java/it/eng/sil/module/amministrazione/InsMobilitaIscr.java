package it.eng.sil.module.amministrazione;

import java.math.BigDecimal;

import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.utils.DateUtils;
import it.eng.afExt.utils.MessageCodes;
import it.eng.afExt.utils.ReportOperationResult;
import it.eng.afExt.utils.TransactionQueryExecutor;
import it.eng.sil.module.AbstractSimpleModule;
import it.eng.sil.module.QueryStrategyException;
import it.eng.sil.module.patto.PattoManager;
import it.eng.sil.util.ExceptionUtils;
import it.eng.sil.util.amministrazione.impatti.SituazioneAmministrativaFactory;
import it.eng.sil.util.amministrazione.impatti.StatoOccupazionaleBean;

public class InsMobilitaIscr extends AbstractSimpleModule {
	public void service(SourceBean request, SourceBean response) throws Exception {
		boolean ret = false;
		Object res = null;
		String forza_inserimento = null;
		ReportOperationResult reportOperation = new ReportOperationResult(this, response);
		int idSuccess = this.disableMessageIdSuccess();
		int idFail = this.disableMessageIdFail();
		SourceBean codAttiva = null;
		String codStatoMob = (String) request.getAttribute("codStatoMob");
		SourceBean serviceResponse = getResponseContainer().getServiceResponse();
		String configurazioneMob = "0";
		if (serviceResponse.containsAttribute("M_GetConfig_Mobilita.ROWS.ROW.NUM")) {
			configurazioneMob = serviceResponse.getAttribute("M_GetConfig_Mobilita.ROWS.ROW.NUM").toString();
		}
		forza_inserimento = (String) request.getAttribute("CONTINUA_INSERIMENTO_MOB");

		if (configurazioneMob.equals("0")) {
			if (forza_inserimento != null && !forza_inserimento.equals("true")) {
				try {
					// verifica se lo stato è impostato e se la mobilità è sospesa, in caso contrario chiedo
					// conferma per continuare l'operazione di inserimento
					if (codStatoMob != null && !codStatoMob.equals("")) {
						this.setSectionQuerySelect("GET_CODMONO_MOBILITA");
						codAttiva = doSelect(request, response);
						if (!codAttiva.getAttribute("ROW.CODMONOATTIVA").equals("S")) {
							throw new Exception("Situazione di incongruenza");
						}
					}
				} catch (Exception e) {
					addConfirm(request, response);
					return;
				}
			}
		}

		// data inizio movimento e data fine movimento sono campi obbligatori
		// nella maschera di gestione dell'iscrizione
		String datFineMov = request.containsAttribute("datFineMovHid")
				? request.getAttribute("datFineMovHid").toString()
				: "";
		String datInizioMov = request.containsAttribute("datInizMovHid")
				? request.getAttribute("datInizMovHid").toString()
				: "";

		// Inizio controlli sulle date
		try {
			if (datInizioMov.equals("") || datFineMov.equals("")) {
				throw new Exception("Il periodo lavorativo è obbligatorio.");
			}
		} catch (Exception e) {
			this.setMessageIdFail(idFail);
			reportOperation.reportFailure(MessageCodes.Mobilita.ERR_PERIODO_LAVORATIVO_MOBILITA);
			return;
		}
		String datInizioMob = request.containsAttribute("datInizioHid")
				? request.getAttribute("datInizioHid").toString()
				: "";
		if (datInizioMob.equals("")) {
			datInizioMob = DateUtils.giornoSuccessivo(datFineMov);
			request.updAttribute("datInizioHid", datInizioMob);
		}
		try {
			if (DateUtils.compare(datInizioMob, DateUtils.getNow()) > 0) {
				throw new Exception("Non è possibile registrare un'iscrizione con data inizio futura.");
			}
		} catch (Exception e) {
			this.setMessageIdFail(idFail);
			reportOperation.reportFailure(MessageCodes.Mobilita.ERR_DATA_INIZIO_MOBILITA_FUTURA);
			return;
		}
		try {
			if (!datInizioMob.equals(DateUtils.giornoSuccessivo(datFineMov))) {
				throw new Exception(
						"La data inizio mobilità deve essere uguale al giorno successivo la cessazione del rapporto lavorativo.");
			}
		} catch (Exception e) {
			this.setMessageIdFail(idFail);
			reportOperation.reportFailure(MessageCodes.Mobilita.ERR_DATA_INIZIO_MOBILITA_ERRATA);
			return;
		}
		String datFineMob = request.containsAttribute("datFine") ? request.getAttribute("datFine").toString() : "";
		String datFineMobOrig = request.containsAttribute("datFineOrig")
				? request.getAttribute("datFineOrig").toString()
				: "";
		String datMaxDiff = request.containsAttribute("datMaxDiff") ? request.getAttribute("datMaxDiff").toString()
				: "";
		String datFineIndennita = request.containsAttribute("dataFineIndenn")
				? request.getAttribute("dataFineIndenn").toString()
				: "";

		try {
			if (!datFineMobOrig.equals("") && DateUtils.compare(datInizioMob, datFineMobOrig) > 0) {
				throw new Exception("");
			}
		} catch (Exception e) {
			this.setMessageIdFail(idFail);
			reportOperation.reportFailure(MessageCodes.Mobilita.ERR_DATFINEMOBORIG_DATINIZIO);
			return;
		}
		try {
			if (!datFineMob.equals("") && DateUtils.compare(datInizioMob, datFineMob) > 0) {
				throw new Exception("");
			}
		} catch (Exception e) {
			this.setMessageIdFail(idFail);
			reportOperation.reportFailure(MessageCodes.Mobilita.ERR_DATFINEMOB_DATINIZIO);
			return;
		}
		try {
			if (!datFineMob.equals("") && !datMaxDiff.equals("") && DateUtils.compare(datFineMob, datMaxDiff) > 0) {
				throw new Exception("");
			}
		} catch (Exception e) {
			this.setMessageIdFail(idFail);
			reportOperation.reportFailure(MessageCodes.Mobilita.ERR_DATAMAXDIFF_DATFINEMOB);
			return;
		}
		try {
			if (!datMaxDiff.equals("") && !datFineIndennita.equals("")
					&& DateUtils.compare(datFineIndennita, datMaxDiff) > 0) {
				throw new Exception("");
			}
		} catch (Exception e) {
			this.setMessageIdFail(idFail);
			reportOperation.reportFailure(MessageCodes.Mobilita.ERR_DATAMAXDIFF_DATFINEINDENNITA);
			return;
		}
		// Fine controlli sulle date

		Object cdnlavoratore = request.getAttribute("cdnlavoratore");
		String prgMovimento = request.containsAttribute("prgMovimento")
				? request.getAttribute("prgMovimento").toString()
				: "";
		TransactionQueryExecutor transExec = null;
		// forzature in ricalcolo impatti
		if (!request.containsAttribute("FORZA_INSERIMENTO")) {
			request.setAttribute("FORZA_INSERIMENTO", "true");
		} else {
			request.updAttribute("FORZA_INSERIMENTO", "true");
		}
		if (!request.containsAttribute("CONTINUA_CALCOLO_SOCC")) {
			request.setAttribute("CONTINUA_CALCOLO_SOCC", "true");
		} else {
			request.updAttribute("CONTINUA_CALCOLO_SOCC", "true");
		}

		SourceBean rowWarning = new SourceBean("WARNINGMOBILITA");
		if (PattoManager.withPatto(request)) {
			try {
				transExec = new TransactionQueryExecutor(getPool());
				PattoManager patto = new PattoManager(this, transExec);
				enableTransactions(transExec);
				transExec.initTransaction();
				BigDecimal prgMobilitaIscr = getPrgMobilitaIscr(request, response);
				setParameterForMBIscr(prgMobilitaIscr, request);
				// prima di fare l'inserimento controllo l'associazione con il
				// movimento
				if (prgMovimento.equals("")) {
					UtilsMobilita.setMovimentoInMobilita(request, rowWarning, transExec);
				}
				res = this.doInsert(request, response, true);
				ExceptionUtils.controlloDateRecordPrecedente(res,
						MessageCodes.General.ERR_INTERSEZIONE_DATA_REC_STORICIZZATO);
				ret = ((Boolean) res).booleanValue();
				if (ret) {
					if (request.getAttribute("PRG_TAB") != null) {
						request.delAttribute("PRG_TAB");
					}
					request.setAttribute("PRG_TAB", prgMobilitaIscr);
					ret = patto.execute(request, response);
				} else {
					throw new Exception("La mobilità corrente interseca una mobilità precedente.");
				}
				if (ret) {
					boolean permettiImpatti = UtilsMobilita.controllaPermessi(new BigDecimal(cdnlavoratore.toString()),
							transExec);
					// ricostruisco la storia del lavoratore
					if (permettiImpatti) {
						request.setAttribute("DISABILITA_SCORRIMENTO_MOBILITA", "true");
						StatoOccupazionaleBean statoOccupazionale = SituazioneAmministrativaFactory
								.newInstance(cdnlavoratore.toString(), datInizioMob, transExec).calcolaImpatti();
						reportOperation.reportSuccess(MessageCodes.ScorrimentoMobilita.NO_OPERAZIONE);
					} else {
						reportOperation.reportSuccess(MessageCodes.ImportMov.WAR_NO_COMPETENZA_LAV);
					}
					transExec.commitTransaction();
					this.setMessageIdSuccess(idSuccess);
					reportOperation.reportSuccess(idSuccess);
				} else {
					throw new Exception();
				}
			}

			catch (QueryStrategyException qse) {
				if (transExec != null) {
					transExec.rollBackTransaction();
				}
				this.setMessageIdFail(idFail);
				reportOperation.reportFailure(MessageCodes.Mobilita.ERR_INTERSEZIONE_MOBILITA);
				return;
			}

			catch (Exception e) {
				if (transExec != null) {
					transExec.rollBackTransaction();
				}
				this.setMessageIdFail(idFail);
				reportOperation.reportFailure(MessageCodes.General.INSERT_FAIL);
				return;
			}

		} else {
			BigDecimal prgMobilitaIscr = getPrgMobilitaIscr(request, response);
			try {
				if (prgMobilitaIscr != null) {
					transExec = new TransactionQueryExecutor(getPool());
					enableTransactions(transExec);
					transExec.initTransaction();
					setParameterForMBIscr(prgMobilitaIscr, request);
					// prima di fare l'inserimento controllo l'associazione con
					// il movimento
					if (prgMovimento.equals("")) {
						UtilsMobilita.setMovimentoInMobilita(request, rowWarning, transExec);
					}
					res = this.doInsert(request, response, true);
					ExceptionUtils.controlloDateRecordPrecedente(res,
							MessageCodes.General.ERR_INTERSEZIONE_DATA_REC_STORICIZZATO);
					ret = ((Boolean) res).booleanValue();
					if (!ret) {
						throw new Exception("La mobilità corrente interseca una mobilità precedente.");
					}
					boolean permettiImpatti = UtilsMobilita.controllaPermessi(new BigDecimal(cdnlavoratore.toString()),
							transExec);
					// ricostruisco la storia del lavoratore
					if (permettiImpatti) {
						request.setAttribute("DISABILITA_SCORRIMENTO_MOBILITA", "true");
						StatoOccupazionaleBean statoOccupazionale = SituazioneAmministrativaFactory
								.newInstance(cdnlavoratore.toString(), datInizioMob, transExec).calcolaImpatti();
						reportOperation.reportSuccess(MessageCodes.ScorrimentoMobilita.NO_OPERAZIONE);
					} else {
						reportOperation.reportSuccess(MessageCodes.ImportMov.WAR_NO_COMPETENZA_LAV);
					}
					transExec.commitTransaction();
					this.setMessageIdSuccess(idSuccess);
					reportOperation.reportSuccess(idSuccess);
				}
			}

			catch (QueryStrategyException qse) {
				if (transExec != null) {
					transExec.rollBackTransaction();
				}
				this.setMessageIdFail(idFail);
				reportOperation.reportFailure(MessageCodes.Mobilita.ERR_INTERSEZIONE_MOBILITA);
				return;
			}

			catch (Exception e) {
				if (transExec != null) {
					transExec.rollBackTransaction();
				}
				this.setMessageIdFail(idFail);
				reportOperation.reportFailure(MessageCodes.General.INSERT_FAIL);
				return;
			}
		}
		response.setAttribute(rowWarning);
	}

	private BigDecimal getPrgMobilitaIscr(SourceBean request, SourceBean response) {
		this.setSectionQuerySelect("GET_NEW_AM_MOBILITA_ISCR");

		SourceBean sb = doSelect(request, response);

		if (sb != null) {
			return (BigDecimal) sb.getAttribute("ROW.PRGMOBILITAISCR");
		} else {
			return null;
		}
	}

	private void setParameterForMBIscr(BigDecimal pk, SourceBean request) throws Exception {
		if (request.getAttribute("prgMobilitaIscr") != null) {
			request.delAttribute("prgMobilitaIscr");
		}

		request.setAttribute("prgMobilitaIscr", pk);
	}

	private void addConfirm(SourceBean request, SourceBean response) throws Exception {

		SourceBean sb = new SourceBean("RECORD");
		sb.setAttribute("RESULT", "ERRORINS");
		response.setAttribute(sb);
	}
}
