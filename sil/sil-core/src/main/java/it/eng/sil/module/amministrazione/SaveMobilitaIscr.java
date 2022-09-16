package it.eng.sil.module.amministrazione;

import java.math.BigDecimal;
import java.util.Vector;

import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.utils.DateUtils;
import it.eng.afExt.utils.MessageCodes;
import it.eng.afExt.utils.ReportOperationResult;
import it.eng.afExt.utils.TransactionQueryExecutor;
import it.eng.sil.module.AbstractSimpleModule;
import it.eng.sil.module.patto.PattoManager;
import it.eng.sil.util.amministrazione.impatti.ListaMobilita;
import it.eng.sil.util.amministrazione.impatti.MobilitaBean;
import it.eng.sil.util.amministrazione.impatti.SituazioneAmministrativaFactory;
import it.eng.sil.util.amministrazione.impatti.StatoOccupazionaleBean;

public class SaveMobilitaIscr extends AbstractSimpleModule {

	public void service(SourceBean request, SourceBean response) throws Exception {
		boolean ret = false;
		String forza_aggiornamento = null;
		int codeMessaggioScorrimento = MessageCodes.ScorrimentoMobilita.MATERNITA;
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

		forza_aggiornamento = (String) request.getAttribute("CONTINUA_AGGIORNAMENTO_MOB");

		if (configurazioneMob.equals("0")) {
			if (forza_aggiornamento != null && !forza_aggiornamento.equals("true")) {
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

		String datInizioMov = request.containsAttribute("datInizMovHid")
				? request.getAttribute("datInizMovHid").toString()
				: "";
		String datFineMov = request.containsAttribute("datFineMovHid")
				? request.getAttribute("datFineMovHid").toString()
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
		String datInizioMob = request.getAttribute("datInizioHid").toString();
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
		BigDecimal prgMobilita = new BigDecimal(request.getAttribute("prgMobilitaIscr").toString());
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
		String prgMovimento = request.containsAttribute("prgMovimento")
				? request.getAttribute("prgMovimento").toString()
				: "";
		String flgSchedaOld = request.containsAttribute("FLGSCHEDA") ? request.getAttribute("FLGSCHEDA").toString()
				: "";
		String flgSchedaNew = request.containsAttribute("flagDisponibilita")
				? request.getAttribute("flagDisponibilita").toString()
				: "";
		boolean scorrimento = false;
		// recupero vecchia data fine indennità e nuova data fine indennità(se
		// sono diverse allora devo effettuare
		// lo scorrimento della mobilità)
		String datFineIndennOld = request.containsAttribute("dataFineIndennHid")
				? request.getAttribute("dataFineIndennHid").toString()
				: "";
		String datFineIndennNew = request.containsAttribute("dataFineIndenn")
				? request.getAttribute("dataFineIndenn").toString()
				: "";
		if (!datFineIndennOld.equals(datFineIndennNew)) {
			if (request.containsAttribute("ABILITASCORRIMENTO")
					&& request.getAttribute("ABILITASCORRIMENTO").toString().equalsIgnoreCase("true")) {
				scorrimento = true;
			}
		}

		SourceBean rowWarning = new SourceBean("WARNINGMOBILITA");
		if (PattoManager.withPatto(request)) {
			try {
				transExec = new TransactionQueryExecutor(getPool());
				PattoManager patto = new PattoManager(this, transExec);
				enableTransactions(transExec);
				transExec.initTransaction();
				// in aggiornamento controllo l'intersezione con altri periodi
				// di mobilità
				try {
					if (checkIntersezioneMobilita(prgMobilita, datInizioMob, datFineMob, cdnlavoratore, transExec)) {
						throw new Exception("La mobilità si interseca con un altro periodo di mobilità.");
					}
				} catch (Exception e) {
					if (transExec != null) {
						transExec.rollBackTransaction();
					}
					this.setMessageIdFail(idFail);
					reportOperation.reportFailure(MessageCodes.Mobilita.ERR_INTERSEZIONE_MOBILITA);
					return;
				}
				// prima di fare l'aggiornamento controllo l'associazione con il
				// movimento
				if (prgMovimento.equals("")) {
					UtilsMobilita.setMovimentoInMobilita(request, rowWarning, transExec);
				}

				ret = this.doUpdate(request, response);
				if (ret) {
					ret = patto.execute(request, response);
				} else {
					throw new Exception("");
				}

				if (ret) {
					boolean permettiImpatti = UtilsMobilita.controllaPermessi(new BigDecimal(cdnlavoratore.toString()),
							transExec);
					// ricostruisco la storia del lavoratore
					if (permettiImpatti) {
						if (!scorrimento) {
							request.setAttribute("DISABILITA_SCORRIMENTO_MOBILITA", "true");
							reportOperation.reportSuccess(MessageCodes.ScorrimentoMobilita.NO_OPERAZIONE);
						} else {
							reportOperation.reportSuccess(codeMessaggioScorrimento);
						}
						StatoOccupazionaleBean statoOccupazionale = SituazioneAmministrativaFactory
								.newInstance(cdnlavoratore.toString(), datInizioMob, transExec).calcolaImpatti();
					} else {
						reportOperation.reportSuccess(MessageCodes.ImportMov.WAR_NO_COMPETENZA_LAV);
					}
					// Se il flag Scheda Disponibilità passa da S a N allora
					// cancello tutte le disponibilità
					if (flgSchedaOld.equalsIgnoreCase("S") && flgSchedaNew.equals("")) {
						setSectionQueryDelete("DELETE_DISPONIBILITA_MOBILITA");
						ret = doDelete(request, response);
					}
					if (ret) {
						transExec.commitTransaction();
						this.setMessageIdSuccess(idSuccess);
						reportOperation.reportSuccess(idSuccess);
					} else {
						throw new Exception();
					}
				} else {
					throw new Exception();
				}

			} catch (Exception e) {
				if (transExec != null) {
					transExec.rollBackTransaction();
				}
				this.setMessageIdFail(idFail);
				reportOperation.reportFailure(MessageCodes.General.UPDATE_FAIL);
			}
		} else {
			try {
				transExec = new TransactionQueryExecutor(getPool());
				enableTransactions(transExec);
				transExec.initTransaction();
				// in aggiornamento controllo l'intersezione con altri periodi
				// di mobilità
				try {
					if (checkIntersezioneMobilita(prgMobilita, datInizioMob, datFineMob, cdnlavoratore, transExec)) {
						throw new Exception("La mobilità si interseca con un altro periodo di mobilità.");
					}
				} catch (Exception e) {
					if (transExec != null) {
						transExec.rollBackTransaction();
					}
					this.setMessageIdFail(idFail);
					reportOperation.reportFailure(MessageCodes.Mobilita.ERR_INTERSEZIONE_MOBILITA);
					return;
				}
				// prima di fare l'aggiornamento controllo l'associazione con il
				// movimento
				if (prgMovimento.equals("")) {
					UtilsMobilita.setMovimentoInMobilita(request, rowWarning, transExec);
				}
				ret = doUpdate(request, response);
				if (!ret) {
					throw new Exception();
				}
				boolean permettiImpatti = UtilsMobilita.controllaPermessi(new BigDecimal(cdnlavoratore.toString()),
						transExec);
				// ricostruisco la storia del lavoratore
				if (permettiImpatti) {
					if (!scorrimento) {
						request.setAttribute("DISABILITA_SCORRIMENTO_MOBILITA", "true");
						reportOperation.reportSuccess(MessageCodes.ScorrimentoMobilita.NO_OPERAZIONE);
					} else {
						reportOperation.reportSuccess(codeMessaggioScorrimento);
					}
					StatoOccupazionaleBean statoOccupazionale = SituazioneAmministrativaFactory
							.newInstance(cdnlavoratore.toString(), datInizioMob, transExec).calcolaImpatti();
				} else {
					reportOperation.reportSuccess(MessageCodes.ImportMov.WAR_NO_COMPETENZA_LAV);
				}
				// Se il flag Scheda Disponibilità passa da S a N allora
				// cancello tutte le disponibilità
				if (flgSchedaOld.equalsIgnoreCase("S") && flgSchedaNew.equals("")) {
					setSectionQueryDelete("DELETE_DISPONIBILITA_MOBILITA");
					ret = doDelete(request, response);
				}
				if (!ret) {
					throw new Exception();
				}
				transExec.commitTransaction();
				this.setMessageIdSuccess(idSuccess);
				reportOperation.reportSuccess(idSuccess);
			} catch (Exception e) {
				if (transExec != null) {
					transExec.rollBackTransaction();
				}
				this.setMessageIdFail(idFail);
				reportOperation.reportFailure(MessageCodes.General.UPDATE_FAIL);
			}
		}
		response.setAttribute(rowWarning);
	}// end service

	public boolean checkIntersezioneMobilita(BigDecimal prgMobilita, String dataInizio, String dataFine,
			Object cdnLavoratore, TransactionQueryExecutor txExecutor) throws Exception {
		ListaMobilita listaMob = new ListaMobilita(cdnLavoratore, txExecutor);
		Vector rowsMobilita = listaMob.getMobilita();
		SourceBean sbMobilita = null;
		MobilitaBean mobilita = null;
		for (int i = 0; i < rowsMobilita.size(); i++) {
			sbMobilita = (SourceBean) rowsMobilita.get(i);
			mobilita = new MobilitaBean(sbMobilita);
			if (!mobilita.getPrgMobilitaIscr().equals(prgMobilita)) {
				String dataInizioMobCurr = mobilita.getDataInizio();
				String dataFineMobCurr = mobilita.getDataFine();
				if (dataFineMobCurr != null && !dataFineMobCurr.equals("")) {
					if (dataFine == null || dataFine.equals("")) {
						if (DateUtils.compare(dataInizio, dataFineMobCurr) <= 0) {
							return true;
						}
					} else {
						if (DateUtils.compare(dataInizio, dataInizioMobCurr) <= 0) {
							if (DateUtils.compare(dataFine, dataInizioMobCurr) >= 0) {
								return true;
							}
						} else {
							if (DateUtils.compare(dataInizio, dataFineMobCurr) <= 0) {
								return true;
							}
						}
					}
				} else {
					if (dataFine == null || dataFine.equals("")) {
						return true;
					} else {
						if (DateUtils.compare(dataFine, dataInizioMobCurr) >= 0) {
							return true;
						}
					}
				}
			}
		}
		return false;
	}

	private void addConfirm(SourceBean request, SourceBean response) throws Exception {

		SourceBean sb = new SourceBean("RECORD");
		sb.setAttribute("RESULT", "ERRORSAVE");
		response.setAttribute(sb);
	}
}// end class SaveMobilitaIscr
