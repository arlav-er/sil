package it.eng.sil.module.amministrazione;

import java.math.BigDecimal;

import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.utils.MessageCodes;
import it.eng.afExt.utils.ReportOperationResult;
import it.eng.afExt.utils.TransactionQueryExecutor;
import it.eng.sil.module.AbstractSimpleModule;
import it.eng.sil.module.patto.PattoManager;

public class InsertIndispTemp extends AbstractSimpleModule {
	public void service(SourceBean request, SourceBean response) throws Exception {
		boolean ret = false;
		boolean scorrimento = false;
		String codiceIndisp = request.containsAttribute("codIndTemp") ? request.getAttribute("codIndTemp").toString()
				: "";
		String cdnLav = request.containsAttribute("cdnLavoratore") ? request.getAttribute("cdnLavoratore").toString()
				: "";
		String datInizioIndisp = request.containsAttribute("datInizio") ? request.getAttribute("datInizio").toString()
				: "";
		TransactionQueryExecutor transExec = null;
		ReportOperationResult reportOperation = new ReportOperationResult(this, response);
		int idSuccess = this.disableMessageIdSuccess();
		int idFail = this.disableMessageIdFail();
		// this.setSectionQueryUpdate();
		if (PattoManager.withPatto(request)) {
			try {
				transExec = new TransactionQueryExecutor(getPool());
				enableTransactions(transExec);
				PattoManager patto = new PattoManager(this, transExec);
				transExec.initTransaction();
				// this.setSectionQuerySelect("GET_NEW_AM_INDISP_TEMP");
				BigDecimal prgIndispTemp = getPrgIndispTemp(request, response);
				setParameterForIndisp(prgIndispTemp, request);
				// this.setSectionQueryInsert("INSERT_IND_T");
				ret = this.doInsert(request, response);
				if (ret) {
					if (request.getAttribute("PRG_TAB") != null) {
						request.delAttribute("PRG_TAB");
					}
					request.setAttribute("PRG_TAB", prgIndispTemp);
					ret = patto.execute(request, response);
				} else {
					throw new Exception("");
				}
				//
				if (ret) {
					// chiamata per eventuale scorrimento
					scorrimento = UtilsMobilita.scorrimentoPerIndisp(request, codiceIndisp, cdnLav, datInizioIndisp,
							transExec);
					transExec.commitTransaction();
					this.setMessageIdSuccess(idSuccess);
					this.setMessageIdFail(idFail);
					reportOperation.reportSuccess(idSuccess);
					if (scorrimento) {
						reportOperation.reportSuccess(MessageCodes.ScorrimentoMobilita.MATERNITA);
					}
				} else {
					throw new Exception();
				}
			} catch (Exception e) {
				reportOperation.reportFailure(MessageCodes.General.INSERT_FAIL);
				if (transExec != null) {
					transExec.rollBackTransaction();
				}
			}
		} else {
			try {
				transExec = new TransactionQueryExecutor(getPool());
				enableTransactions(transExec);
				transExec.initTransaction();
				BigDecimal prgIndispTemp = getPrgIndispTemp(request, response);
				if (prgIndispTemp != null) {
					setParameterForIndisp(prgIndispTemp, request);
					ret = doInsert(request, response);
					if (!ret) {
						throw new Exception();
					}
					// chiamata per eventuale scorrimento
					scorrimento = UtilsMobilita.scorrimentoPerIndisp(request, codiceIndisp, cdnLav, datInizioIndisp,
							transExec);
					transExec.commitTransaction();
					this.setMessageIdSuccess(idSuccess);
					this.setMessageIdFail(idFail);
					reportOperation.reportSuccess(idSuccess);
					if (scorrimento) {
						reportOperation.reportSuccess(MessageCodes.ScorrimentoMobilita.MATERNITA);
					}
				} else {
					throw new Exception("Errore recupero sequence");
				}
			} catch (Exception e) {
				reportOperation.reportFailure(MessageCodes.General.INSERT_FAIL);
				if (transExec != null) {
					transExec.rollBackTransaction();
				}
			}
		}
	}

	private BigDecimal getPrgIndispTemp(SourceBean request, SourceBean response) {
		this.setSectionQuerySelect("GET_NEW_AM_INDISP_TEMP");

		SourceBean sb = doSelect(request, response);

		if (sb != null) {
			return (BigDecimal) sb.getAttribute("ROW.PRGINDISPTEMP");
		} else {
			return null;
		}
	}

	private void setParameterForIndisp(BigDecimal pk, SourceBean request) throws Exception {
		if (request.getAttribute("prgIndispTemp") != null) {
			request.delAttribute("prgIndispTemp");
		}

		request.setAttribute("prgIndispTemp", pk);
	}
}

// end service
// end class SaveIndispTemp
