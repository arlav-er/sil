package it.eng.sil.module.amministrazione;

import java.math.BigDecimal;
import java.util.Vector;

import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.utils.DateUtils;
import it.eng.afExt.utils.MessageCodes;
import it.eng.afExt.utils.ReportOperationResult;
import it.eng.afExt.utils.TransactionQueryExecutor;
import it.eng.sil.module.AbstractSimpleModule;
import it.eng.sil.util.amministrazione.impatti.ListaMobilita;
import it.eng.sil.util.amministrazione.impatti.MobilitaBean;

public class RiapriMobilitaIscr extends AbstractSimpleModule {

	public void service(SourceBean request, SourceBean response) throws Exception {
		boolean ret = false;
		String forza_aggiornamento = null;
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

		// Inizio controlli sulle date
		String datInizioMob = request.getAttribute("datInizio").toString();
		String datFineMob = request.containsAttribute("datFine") ? request.getAttribute("datFine").toString() : "";
		String datFineMobOrig = request.containsAttribute("datFineOrig")
				? request.getAttribute("datFineOrig").toString()
				: "";
		String datMaxDiff = request.containsAttribute("datMaxDiff") ? request.getAttribute("datMaxDiff").toString()
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
		// Fine controlli sulle date
		Object cdnlavoratore = request.getAttribute("cdnlavoratore");
		BigDecimal prgMobilita = new BigDecimal(request.getAttribute("prgMobilitaIscr").toString());
		TransactionQueryExecutor transExec = null;

		String prgMovimento = request.containsAttribute("prgMovimento")
				? request.getAttribute("prgMovimento").toString()
				: "";

		SourceBean rowWarning = new SourceBean("WARNINGMOBILITA");

		try {
			transExec = new TransactionQueryExecutor(getPool());
			enableTransactions(transExec);
			transExec.initTransaction();
			// in aggiornamento controllo l'intersezione con altri periodi di mobilità
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

			if (checkStatoOccManuali(datInizioMob, datFineMob, cdnlavoratore, transExec)) {
				reportOperation.reportWarning(MessageCodes.Mobilita.WARNING_RIAPRI_SOCC_MANUALE);
			}

			// prima di fare l'aggiornamento controllo l'associazione con il movimento
			if (prgMovimento.equals("")) {
				UtilsMobilita.setMovimentoInMobilita(request, rowWarning, transExec);
			}
			ret = doUpdate(request, response);
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
		response.setAttribute(rowWarning);
	}// end service

	public boolean checkStatoOccManuali(String dataInizio, String dataFine, Object cdnLavoratore,
			TransactionQueryExecutor txExecutor) throws Exception {
		boolean ret = false;
		Object params[] = new Object[5];
		params[0] = cdnLavoratore;
		params[1] = dataInizio;
		params[2] = dataFine;
		params[3] = dataInizio;
		params[4] = dataFine;
		SourceBean row = (SourceBean) txExecutor.executeQuery("AMSTR_STATO_OCC_MANUALE_IN_MOBILITA", params, "SELECT");
		if (row != null) {
			row = row.containsAttribute("ROW") ? (SourceBean) row.getAttribute("ROW") : row;
			if (row.containsAttribute("numStatiOccManuali")) {
				Integer numSOcc = new Integer(row.getAttribute("numStatiOccManuali").toString());
				if (numSOcc.intValue() > 0) {
					ret = true;
				}
			}
		}
		return ret;
	}

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
