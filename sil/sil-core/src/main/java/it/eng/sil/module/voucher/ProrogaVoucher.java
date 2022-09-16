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

public class ProrogaVoucher extends AbstractSimpleModule {

	private static final long serialVersionUID = -1533726811526662058L;
	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(ProrogaVoucher.class.getName());

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

		String strPrgVoucher = StringUtils.getAttributeStrNotNull(request, "prgvoucher");
		String strGGProrogaAttivazione = StringUtils.getAttributeStrNotNull(request, "numGiorniProAttivazione");
		String strGGProrogaChiusura = StringUtils.getAttributeStrNotNull(request, "numGiorniProChiusura");
		String strPrgColloquio = StringUtils.getAttributeStrNotNull(request, "prgColloquio");
		String strPrgPercorso = StringUtils.getAttributeStrNotNull(request, "prgPercorso");

		String dataMaxAttivazione = StringUtils.getAttributeStrNotNull(request, "datmaxattivazione");
		String dataMaxConclusione = StringUtils.getAttributeStrNotNull(request, "datmaxconclusione");
		Integer ggProrogaAttivazione = null;
		Integer ggProrogaChiusura = null;

		if (!strGGProrogaAttivazione.equals("")) {
			ggProrogaAttivazione = new Integer(strGGProrogaAttivazione);
		} else {
			ggProrogaAttivazione = new Integer(0);
		}
		if (!strGGProrogaChiusura.equals("")) {
			ggProrogaChiusura = new Integer(strGGProrogaChiusura);
		} else {
			ggProrogaChiusura = new Integer(0);
		}

		String dataNewMaxAttivazione = dataMaxAttivazione;
		String dataNewMaxConclusione = dataMaxConclusione;
		if (ggProrogaAttivazione.intValue() > 0) {
			dataNewMaxAttivazione = DateUtils.aggiungiNumeroGiorni(dataNewMaxAttivazione,
					ggProrogaAttivazione.intValue());
		}
		if (ggProrogaChiusura.intValue() > 0) {
			dataNewMaxConclusione = DateUtils.aggiungiNumeroGiorni(dataNewMaxConclusione, ggProrogaChiusura.intValue());
		}

		/*
		 * if (DateUtils.compare(dataNewMaxAttivazione, dataNewMaxConclusione) > 0) {
		 * reportOperation.reportFailure(MessageCodes.MSGVOUCHER.ERRORE_DATE_PROROGA_VOUCHER); } else {
		 */
		BigDecimal prgColloquio = new BigDecimal(strPrgColloquio);
		BigDecimal prgPercorso = new BigDecimal(strPrgPercorso);
		BigDecimal prgVoucher = new BigDecimal(strPrgVoucher);
		boolean skipCheckProroga = false;

		try {

			transactionExecutor = new TransactionQueryExecutor(getPool(), this);
			transactionExecutor.initTransaction();

			Voucher vch = new Voucher(prgVoucher, prgPercorso, prgColloquio);

			conn = transactionExecutor.getDataConnection();

			Integer esito = vch.proroga(conn, ggProrogaAttivazione, ggProrogaChiusura, cdnParUtente, skipCheckProroga);

			if (esito.intValue() == 0) {
				transactionExecutor.commitTransaction();
				reportOperation.reportSuccess(MessageCodes.General.OPERATION_SUCCESS);
			} else {
				transactionExecutor.rollBackTransaction();
				switch (Math.abs(esito.intValue())) {
				case Properties.ERRORE_DOTERESIDUACPI_PROROGA:
					reportOperation.reportFailure(MessageCodes.MSGVOUCHER.ERRORE_DOTERESIDUACPI_PROROGA);
					break;
				case Properties.ERRORE_STATO_VCH:
					reportOperation.reportFailure(MessageCodes.MSGVOUCHER.ERRORE_STATO_VCH);
					break;
				case Properties.ERRORE_REITERAZIONE_PRG_ATT:
					reportOperation.reportFailure(MessageCodes.MSGVOUCHER.ERRORE_REITERAZIONE_PRG_ATT);
					break;
				case Properties.ERRORE_REITERAZIONE_PRG_CHI:
					reportOperation.reportFailure(MessageCodes.MSGVOUCHER.ERRORE_REITERAZIONE_PRG_CHI);
					break;
				case Properties.ERRORE_TIPOLOGIA_PRG:
					reportOperation.reportFailure(MessageCodes.MSGVOUCHER.ERRORE_TIPOLOGIA_PRG);
					break;
				case MessageCodes.General.CONCORRENZA:
					reportOperation.reportFailure(MessageCodes.General.CONCORRENZA);
					break;
				default:
					reportOperation.reportFailure(MessageCodes.General.OPERATION_FAIL);
				}
			}
		}

		catch (Exception ex) {
			if (transactionExecutor != null) {
				transactionExecutor.rollBackTransaction();
			}
			reportOperation.reportFailure(MessageCodes.General.OPERATION_FAIL);
			it.eng.sil.util.TraceWrapper.debug(_logger, className + ": errore ", ex);
		} finally {
			Utils.releaseResources(conn, null, null);
		}
		// }
	}

}