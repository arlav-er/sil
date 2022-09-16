package it.eng.sil.module.voucher;

import java.math.BigDecimal;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.dbaccess.Utils;
import com.engiweb.framework.dbaccess.sql.DataConnection;

import it.eng.afExt.utils.DateUtils;
import it.eng.afExt.utils.MessageCodes;
import it.eng.afExt.utils.ReportOperationResult;
import it.eng.afExt.utils.StringUtils;
import it.eng.afExt.utils.TransactionQueryExecutor;
import it.eng.sil.module.AbstractSimpleModule;
import it.eng.sil.security.User;

public class AssegnaVoucher extends AbstractSimpleModule {

	private static final long serialVersionUID = -1533726811526662058L;
	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(AssegnaVoucher.class.getName());

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
		boolean generaCodice = false;

		String codiceAttivazione = StringUtils.getAttributeStrNotNull(request, "codAttivazione");
		String strPrgColloquio = StringUtils.getAttributeStrNotNull(request, "prgColloquio");
		String strPrgPercorso = StringUtils.getAttributeStrNotNull(request, "prgPercorso");
		String dataAssegnazione = StringUtils.getAttributeStrNotNull(request, "dtAssegnazione");
		BigDecimal prgColloquio = new BigDecimal(strPrgColloquio);
		BigDecimal prgPercorso = new BigDecimal(strPrgPercorso);
		// dataAssegnazione deve arrivare sempre valorizzato
		if (dataAssegnazione.equals("")) {
			dataAssegnazione = DateUtils.getNow();
		}
		//
		try {

			transactionExecutor = new TransactionQueryExecutor(getPool(), this);
			transactionExecutor.initTransaction();

			Voucher vch = new Voucher(prgPercorso, prgColloquio, codiceAttivazione);

			if (codiceAttivazione.equals("")) {
				// devo generare dinamicamente un codice di attivazione
				generaCodice = true;
				vch.lockNewNumCodiceAttivazione(transactionExecutor);
				codiceAttivazione = vch.getCodProvincia() + vch.getNumAnnoCodice() + vch.getNumCodice().toString();
				vch.setCodiceAttivazione(codiceAttivazione);
			}

			conn = transactionExecutor.getDataConnection();

			Integer esito = vch.assegnaAllaData(conn, cdnParUtente, dataAssegnazione);

			if (esito.intValue() == 0) {
				if (generaCodice) {
					vch.unlockNewNumCodiceAttivazione(transactionExecutor);
				}
				transactionExecutor.commitTransaction();
				reportOperation.reportSuccess(MessageCodes.General.OPERATION_SUCCESS);
				response.setAttribute("VOUCHERASSEGNATO", "true");
			} else {
				transactionExecutor.rollBackTransaction();
				switch (Math.abs(esito.intValue())) {
				case Properties.ERRORE_ESITOAZIONE_ASSEGNAZIONE:
					reportOperation.reportFailure(MessageCodes.MSGVOUCHER.ERRORE_ESITOAZIONE_ASSEGNAZIONE);
					break;
				case Properties.ERRORE_DOTERESIDUACPI_ASSEGNAZIONE:
					reportOperation.reportFailure(MessageCodes.MSGVOUCHER.ERRORE_DOTERESIDUACPI_ASSEGNAZIONE);
					break;
				case Properties.ERRORE_DOTERESIDUAPATTO_ASSEGNAZIONE:
					reportOperation.reportFailure(MessageCodes.MSGVOUCHER.ERRORE_DOTERESIDUAPATTO_ASSEGNAZIONE);
					break;
				case Properties.ERRORE_VOUCHER_GIA_PRESENTE_ASSEGNAZIONE:
					reportOperation.reportFailure(MessageCodes.MSGVOUCHER.ERRORE_VOUCHER_GIA_PRESENTE_ASSEGNAZIONE);
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