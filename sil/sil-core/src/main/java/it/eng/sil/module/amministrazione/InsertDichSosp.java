package it.eng.sil.module.amministrazione;

import java.math.BigDecimal;

import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.utils.ReportOperationResult;
import it.eng.sil.module.AbstractSimpleModule;

public class InsertDichSosp extends AbstractSimpleModule {
	public void service(SourceBean request, SourceBean response) throws Exception {

		ReportOperationResult reportOperation = new ReportOperationResult(this, response);
		int idSuccess = this.disableMessageIdSuccess();
		int idFail = this.disableMessageIdFail();

		try {
			BigDecimal prgDichSosp = getPrgDichSosp(request, response);
			if (prgDichSosp != null) {
				setKeyinRequest(prgDichSosp, request);
				this.setSectionQueryInsert("QUERY_INSERT");
				this.setSectionQuerySelect("QUERY_SELECT");
				this.setMessageIdSuccess(idSuccess);
				doInsertNoDuplicate(request, response);
			} else {
				reportOperation.reportFailure(idFail);
			}
		} catch (Exception ex) {
		}

	}

	private BigDecimal getPrgDichSosp(SourceBean request, SourceBean response) throws Exception {
		this.setSectionQuerySelect("QUERY_NEXTVAL");

		SourceBean beanPrgDichSosp = (SourceBean) doSelect(request, response);

		return (BigDecimal) beanPrgDichSosp.getAttribute("ROW.DO_NEXTVAL");
	}

	private void setKeyinRequest(BigDecimal prgDichSosp, SourceBean request) throws Exception {
		if (request.getAttribute("PRGDICHSOSPENSIONE") != null) {
			request.delAttribute("PRGDICHSOSPENSIONE");
		}

		request.setAttribute("PRGDICHSOSPENSIONE", prgDichSosp);
	}

}