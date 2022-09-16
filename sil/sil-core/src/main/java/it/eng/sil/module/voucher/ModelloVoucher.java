package it.eng.sil.module.voucher;

import java.math.BigDecimal;
import java.util.Vector;

import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.utils.MessageCodes;
import it.eng.afExt.utils.ReportOperationResult;
import it.eng.sil.module.AbstractSimpleModule;

public class ModelloVoucher extends AbstractSimpleModule {

	public void service(SourceBean request, SourceBean response) throws Exception {
		int idSuccess = this.disableMessageIdSuccess();
		int idFail = this.disableMessageIdFail();
		ReportOperationResult reportOperation = new ReportOperationResult(this, response);
		SourceBean row = doSelect(request, response);
		if (row != null) {
			Vector<SourceBean> v = row.getAttributeAsVector("ROW");
			if (v.size() > 0) {
				SourceBean rowMod = v.get(0);
				rowMod = rowMod.containsAttribute("ROW") ? (SourceBean) rowMod.getAttribute("ROW") : rowMod;
				BigDecimal prgModello = (BigDecimal) rowMod.getAttribute("PRGMODVOUCHER");
				response.setAttribute("ESISTEMODELLO", prgModello);
			} else {
				reportOperation.reportFailure(MessageCodes.MSGVOUCHER.MODELLO_ASSENTE);
			}
		} else {
			reportOperation.reportFailure(MessageCodes.General.OPERATION_FAIL);
		}
	}
}