package it.eng.sil.module.movimenti;

import java.math.BigDecimal;

import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.dbaccess.sql.DBKeyGenerator;
import it.eng.afExt.utils.MessageCodes;
import it.eng.afExt.utils.ReportOperationResult;
import it.eng.sil.Values;
import it.eng.sil.module.AbstractSimpleModule;

public class InserisciMovimento extends AbstractSimpleModule {
	public void service(SourceBean request, SourceBean response) {
		ReportOperationResult reportOperation = new ReportOperationResult(this, response);
		int idSuccess = this.disableMessageIdSuccess();
		try {
			BigDecimal prgMovimento = DBKeyGenerator.getNextSequence(Values.DB_SIL_DATI, "S_AM_MOVIMENTO");

			if (prgMovimento != null) {
				setKeyinRequest(prgMovimento, request);
				this.setMessageIdSuccess(idSuccess);
				this.setSectionQueryInsert("QUERIES.QUERY_INSERT");
				doInsert(request, response);
				response.setAttribute("prgMovimento", prgMovimento);
			}
		} catch (Exception ex) {
			reportOperation.reportFailure(MessageCodes.General.INSERT_FAIL);

		}
	}

	private void setKeyinRequest(BigDecimal prgMovimento, SourceBean request) throws Exception {
		if (request.getAttribute("prgMovimento") != null) {
			request.delAttribute("prgMovimento");
		}

		request.setAttribute("prgMovimento", prgMovimento);
	}
}
