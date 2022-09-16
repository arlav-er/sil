package it.eng.sil.module.voucher;

import java.math.BigDecimal;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.dbaccess.Utils;
import com.engiweb.framework.dbaccess.sql.DataConnection;

import it.eng.afExt.utils.MessageCodes;
import it.eng.afExt.utils.ReportOperationResult;
import it.eng.afExt.utils.StringUtils;
import it.eng.afExt.utils.TransactionQueryExecutor;
import it.eng.sil.module.AbstractSimpleModule;
import it.eng.sil.security.User;

public class RiapriTDA extends AbstractSimpleModule {

	private static final long serialVersionUID = -1533726811526662058L;
	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(RiapriTDA.class.getName());

	private String className = this.getClass().getName();

	public void service(SourceBean request, SourceBean response) throws Exception {
		User user = (User) getRequestContainer().getSessionContainer().getAttribute(User.USERID);
		BigDecimal cdnParUtente = new BigDecimal(user.getCodut());
		TransactionQueryExecutor transactionExecutor = null;
		DataConnection conn = null;
		int idSuccess = this.disableMessageIdSuccess();
		int idFail = this.disableMessageIdFail();
		ReportOperationResult reportOperation = new ReportOperationResult(this, response);
		disableMessageIdFail();
		disableMessageIdSuccess();

		String prgKeyVoucher = StringUtils.getAttributeStrNotNull(request, "PRGVOUCHER");
		BigDecimal prgVoucher = new BigDecimal(prgKeyVoucher);

		try {

			transactionExecutor = new TransactionQueryExecutor(getPool(), this);
			transactionExecutor.initTransaction();

			Voucher vch = new Voucher(prgVoucher);

			conn = transactionExecutor.getDataConnection();

			Integer esito = vch.riapri(conn, cdnParUtente);

			if (esito.intValue() == 0) {
				transactionExecutor.commitTransaction();
				reportOperation.reportSuccess(MessageCodes.General.OPERATION_SUCCESS);
			} else {
				transactionExecutor.rollBackTransaction();

				switch (Math.abs(esito.intValue())) {
				case Properties.ERRORE_STATO_VCH_RIAPRI:
					reportOperation.reportFailure(MessageCodes.MSGVOUCHER.ERRORE_STATO_VCH_RIAPRI);
					break;
				case Properties.ERRORE_RESIDUO_VCH_RIAPRI:
					reportOperation.reportFailure(MessageCodes.MSGVOUCHER.ERRORE_RESIDUO_VCH_RIAPRI);
					break;
				case MessageCodes.General.CONCORRENZA:
					reportOperation.reportFailure(MessageCodes.General.CONCORRENZA);
					break;
				default:
					reportOperation.reportFailure(MessageCodes.General.OPERATION_FAIL);
				}
			}
		} catch (Exception ex) {
			if (transactionExecutor != null) {
				transactionExecutor.rollBackTransaction();
			}
			reportOperation.reportFailure(MessageCodes.General.OPERATION_FAIL);
			it.eng.sil.util.TraceWrapper.debug(_logger, className + ": errore ", ex);
		} finally {
			Utils.releaseResources(conn, null, null);
		}
	}

}