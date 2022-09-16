package it.eng.sil.module.profil;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.base.SourceBeanException;
import com.engiweb.framework.error.EMFInternalError;

import it.eng.afExt.utils.CF_utils;
import it.eng.afExt.utils.CfException;
import it.eng.afExt.utils.MessageCodes;
import it.eng.afExt.utils.ReportOperationResult;
import it.eng.afExt.utils.StringUtils;
import it.eng.afExt.utils.TransactionQueryExecutor;
import it.eng.sil.module.AbstractSimpleModule;

public class ProfInsertNewTipoGruppo extends AbstractSimpleModule {

	@Override
	public void service(SourceBean serviceRequest, SourceBean serviceResponse)
			throws EMFInternalError, SourceBeanException {
		RequestContainer requestContainer = getRequestContainer();
		ReportOperationResult reportOperation = new ReportOperationResult(this, serviceResponse);
		TransactionQueryExecutor transExec = null;
		SourceBean ret = null;
		boolean ret2 = false;
		int idSuccess = this.disableMessageIdSuccess();
		int idFail = this.disableMessageIdFail();
		try {
			String codTipoGruppo = StringUtils.getAttributeStrNotNull(serviceRequest, "CODTIPO");
			if (codTipoGruppo.equalsIgnoreCase(Properties.TTPO_GRUPPO_SOGGETTI_ACCREDITATI)) {
				// controllo su codice fiscale
				String codiceFiscale = StringUtils.getAttributeStrNotNull(serviceRequest, "strCodiceFiscale");
				if (codiceFiscale.equals("")) {
					reportOperation.reportFailure(MessageCodes.CodiceFiscale.ERR_LUNGHEZZA);
					serviceResponse.setAttribute("INSERT_EROR", "true");
					return;
				} else {
					try {
						if (CF_utils.verificaParzialeCF(codiceFiscale) != 0) {
							serviceResponse.setAttribute("INSERT_EROR", "true");
							return;
						}
					} catch (CfException eCF) {
						reportOperation.reportFailure(eCF.getMessageIdFail());
						serviceResponse.setAttribute("INSERT_EROR", "true");
						return;
					}
				}
			}
			transExec = new TransactionQueryExecutor(getPool());
			transExec.initTransaction();
			ret = super.doSelect(serviceRequest, serviceResponse);
			if (ret != null) {

				// prelevo il NEXTVAL dal sourcebean
				// e lo inserisco in request
				if (ret.containsAttribute("ROW.DO_NEXTVAL")) {
					Object prgStatoOcc = ret.getAttribute("ROW.DO_NEXTVAL");
					serviceRequest.setAttribute("CDNTIPOGRUPPO", prgStatoOcc);
					requestContainer.setAttribute("CDNTIPOGRUPPO", prgStatoOcc);

				} else {
					throw new Exception("Modulo di nextval ha fallito.");
				}

				ret2 = super.doInsert(serviceRequest, serviceResponse);
			}
			if (ret != null && ret2) {
				transExec.commitTransaction();
				this.setMessageIdSuccess(idSuccess);
				this.setMessageIdFail(idFail);
				reportOperation.reportSuccess(idSuccess);
			} else {
				throw new Exception();
			}

		} catch (Exception e) {
			reportOperation.reportFailure(MessageCodes.General.INSERT_FAIL);
			if (transExec != null) {
				transExec.rollBackTransaction();
			}
			serviceResponse.setAttribute("INSERT_EROR", "true");
		}
	}

}
