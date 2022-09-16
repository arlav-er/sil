package it.eng.sil.module.iscrizioni;

import java.math.BigDecimal;

import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.utils.MessageCodes;
import it.eng.afExt.utils.ReportOperationResult;
import it.eng.afExt.utils.StringUtils;
import it.eng.afExt.utils.TransactionQueryExecutor;
import it.eng.sil.module.AbstractSimpleModule;

public class SaveIscrizione extends AbstractSimpleModule {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(SaveIscrizione.class.getName());
	private final String className = StringUtils.getClassName(this);

	private TransactionQueryExecutor transExec;
	private ReportOperationResult reportOperation;
	private int msgCode;

	public void service(SourceBean request, SourceBean response) throws Exception {

		int idSuccess = this.disableMessageIdSuccess();

		reportOperation = new ReportOperationResult(this, response);
		msgCode = MessageCodes.General.OPERATION_FAIL;

		try {
			transExec = new TransactionQueryExecutor(getPool());
			this.enableTransactions(transExec);

			transExec.initTransaction();

			boolean success = false;

			if (request.containsAttribute("inserisci")) {
				BigDecimal prgAltraIscr = getprgAltraIscr(request, response);
				if (prgAltraIscr != null) {
					setKeyinRequest(prgAltraIscr, request);
					this.setSectionQueryInsert("QUERY_INSERT_DOMANDA_CIG");
					success = this.doInsert(request, response);
				} else
					throw new Exception();
			}
			if (request.containsAttribute("aggiorna")) {
				request.delAttribute("NUMKLOALTRAISCR");
				this.setSectionQuerySelect("QUERY_SELECT");
				SourceBean row = doSelect(request, response);
				String NUMKLOALTRAISCR = String
						.valueOf(((BigDecimal) row.getAttribute("ROW.NUMKLOALTRAISCR")).intValue());
				request.setAttribute("NUMKLOALTRAISCR", NUMKLOALTRAISCR);
				this.setSectionQueryUpdate("QUERY_UPDATE_ISCR_CIG");
				success = this.doUpdate(request, response);
			}
			if (success) {
				transExec.commitTransaction();
				reportOperation.reportSuccess(idSuccess);
			} else {
				transExec.rollBackTransaction();
			}
		} catch (Exception e) {
			reportOperation.reportFailure(msgCode, e, "services()", "Errore generico");
			transExec.rollBackTransaction();
		}
	}

	private BigDecimal getprgAltraIscr(SourceBean request, SourceBean response) throws Exception {
		this.setSectionQuerySelect("QUERY_NEXTVAL");
		SourceBean beanprgAltraIscr = (SourceBean) doSelect(request, response);
		return (BigDecimal) beanprgAltraIscr.getAttribute("ROW.PRGALTRAISCR");
	}

	private void setKeyinRequest(BigDecimal PRGALTRAISCR, SourceBean request) throws Exception {
		if (request.getAttribute("PRGALTRAISCR") != null) {
			request.delAttribute("PRGALTRAISCR");
		}
		request.setAttribute("PRGALTRAISCR", PRGALTRAISCR);
		request.setAttribute("strChiaveTabella", PRGALTRAISCR.toString());
	}
}
