package it.eng.sil.module.amministrazione;

import java.math.BigDecimal;

import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.utils.MessageCodes;
import it.eng.afExt.utils.ReportOperationResult;
import it.eng.afExt.utils.TransactionQueryExecutor;
import it.eng.sil.module.AbstractSimpleModule;
import it.eng.sil.module.QueryStrategyException;
import it.eng.sil.module.patto.PattoManager;
import it.eng.sil.util.ExceptionUtils;

public class InsertPermSoggiorno extends AbstractSimpleModule {
	public void service(SourceBean request, SourceBean response) throws Exception {
		boolean ret = false;
		Object res = null;
		ReportOperationResult reportOperation = new ReportOperationResult(this, response);

		// this.setSectionQueryUpdate();
		if (PattoManager.withPatto(request)) {
			TransactionQueryExecutor transExec = null;
			int idSuccess = this.disableMessageIdSuccess();
			int idFail = this.disableMessageIdFail();

			try {
				transExec = new TransactionQueryExecutor(getPool());

				PattoManager patto = new PattoManager(this, transExec);
				transExec.initTransaction();
				// this.setSectionQuerySelect("GET_NEW_AM_INDISP_TEMP");

				BigDecimal prgPermSogg = getPrgPermSogg(request, response);
				setParameterForPermSogg(prgPermSogg, request);
				res = this.doInsert(request, response, true);
				ExceptionUtils.controlloDateRecordPrecedente(res,
						MessageCodes.General.ERR_INTERSEZIONE_DATA_REC_STORICIZZATO);
				ret = ((Boolean) res).booleanValue();

				if (ret) {
					if (request.getAttribute("PRG_TAB") != null) {
						request.delAttribute("PRG_TAB");
					}

					request.setAttribute("PRG_TAB", prgPermSogg);
					ret = patto.execute(request, response);
				} else {
					throw new Exception("");
				}

				//
				if (ret) {
					transExec.commitTransaction();
					this.setMessageIdSuccess(idSuccess);
					this.setMessageIdFail(idFail);
					reportOperation.reportSuccess(idSuccess);
				} else {
					throw new Exception();
				}

			} catch (QueryStrategyException qse) {
				reportOperation.reportFailure(MessageCodes.General.ERR_INTERSEZIONE_DATA_REC_STORICIZZATO);

				if (transExec != null) {
					transExec.rollBackTransaction();
				}
			} catch (Exception e) {
				// this.setMessageIdFail(MessageCodes.General.INSERT_FAIL);
				reportOperation.reportFailure(MessageCodes.General.INSERT_FAIL);

				if (transExec != null) {
					transExec.rollBackTransaction();
				}
			}
		} else {
			int idSuccess = this.disableMessageIdSuccess();
			disableMessageIdFail();
			enableSimpleQuery(true);
			BigDecimal prgMobilitaIscr = getPrgPermSogg(request, response);

			try {
				if (prgMobilitaIscr != null) {
					setParameterForPermSogg(prgMobilitaIscr, request);
					this.setMessageIdSuccess(idSuccess);
					res = doInsert(request, response, true);
					ExceptionUtils.controlloDateRecordPrecedente(res,
							MessageCodes.General.ERR_INTERSEZIONE_DATA_REC_STORICIZZATO);
					reportOperation.reportSuccess(idSuccess);
				}
			} catch (QueryStrategyException qse) {
				reportOperation.reportFailure(MessageCodes.General.ERR_INTERSEZIONE_DATA_REC_STORICIZZATO);
			} catch (Exception e) {
				reportOperation.reportFailure(MessageCodes.General.INSERT_FAIL);
			}
		}
	}

	private BigDecimal getPrgPermSogg(SourceBean request, SourceBean response) {
		this.setSectionQuerySelect("GET_NEW_AM_EX_PERM_SOGG");

		SourceBean sb = doSelect(request, response);

		if (sb != null) {
			return (BigDecimal) sb.getAttribute("ROW.PRGPERMSOGG");
		} else {
			return null;
		}
	}

	private void setParameterForPermSogg(BigDecimal pk, SourceBean request) throws Exception {
		if (request.getAttribute("prgPermSogg") != null) {
			request.delAttribute("prgPermSogg");
		}

		request.setAttribute("prgPermSogg", pk);
	}
}
