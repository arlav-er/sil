package it.eng.sil.module.ido;

import java.math.BigDecimal;

import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.utils.MessageCodes;
import it.eng.afExt.utils.ReportOperationResult;
import it.eng.afExt.utils.StringUtils;
import it.eng.sil.module.AbstractSimpleModule;

public class MInserisciComuneRichiesta extends AbstractSimpleModule {
	public void service(SourceBean request, SourceBean response) {

		ReportOperationResult reportOperation = new ReportOperationResult(this, response);
		int idSuccess = this.disableMessageIdSuccess();
		try {
			Boolean updateFlg = new Boolean(false);
			String flgInvioCL = StringUtils.getAttributeStrNotNull(request, "flgInvioCL");

			if ("S".equals(flgInvioCL)) {
				this.setSectionQuerySelect("QUERY_SELECT");
				SourceBean sbQueryDuplicate = doSelect(request, response);
				if (!sbQueryDuplicate.containsAttribute("ROW")) {
					this.setSectionQuerySelect("VERIFICA_FLGCL");
					SourceBean sbFlgCL = doSelect(request, response);
					BigDecimal numFlag = (BigDecimal) sbFlgCL.getAttribute("ROW.CONTAFLAG");
					if (numFlag.intValue() > 0) {
						setSectionQueryUpdate("QUERY_UPDATE_FLGCL");
						updateFlg = doUpdate(request, response);

						if (!updateFlg.booleanValue())
							throw new Exception("Errore nell'aggiornamento della mansione");
					}
				}
				response.delAttribute("USER_MESSAGE");
			}

			setSectionQuerySelect("QUERY_SELECT");
			doInsertNoDuplicate(request, response);
		}

		catch (Exception ex) {
			reportOperation.reportFailure(MessageCodes.General.INSERT_FAIL);
		}
	}
}
