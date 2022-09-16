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

public class AggiornaModalitaTDA extends AbstractSimpleModule {

	private static final long serialVersionUID = -1533726811526662058L;
	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(AggiornaModalitaTDA.class.getName());

	private String className = this.getClass().getName();

	public void service(SourceBean request, SourceBean response) throws Exception {
		TransactionQueryExecutor transExec = null;
		DataConnection conn = null;
		User user = (User) getRequestContainer().getSessionContainer().getAttribute(User.USERID);
		BigDecimal cdnParUtente = new BigDecimal(user.getCodut());
		String progressivoVoucher = StringUtils.getAttributeStrNotNull(request, "PRGVOUCHER");
		String progressivoModalita = StringUtils.getAttributeStrNotNull(request, "PRGMODMODALITA");
		String durataEffettiva = StringUtils.getAttributeStrNotNull(request, "durataEffettiva");
		BigDecimal prgVoucher = new BigDecimal(progressivoVoucher);
		BigDecimal prgModalita = new BigDecimal(progressivoModalita);
		int idSuccess = this.disableMessageIdSuccess();
		int idFail = this.disableMessageIdFail();
		ReportOperationResult reportOperation = new ReportOperationResult(this, response);
		disableMessageIdFail();
		disableMessageIdSuccess();

		_logger.info("La procedura di aggiornamento modalità voucher e' stata chiamata");

		try {
			String pool = (String) getConfig().getAttribute("POOL");
			transExec = new TransactionQueryExecutor(pool);
			transExec.initTransaction();

			Voucher vch = new Voucher(prgVoucher);
			conn = transExec.getDataConnection();

			Integer esito = null;

			if (!durataEffettiva.equals("")) {
				BigDecimal durataEff = new BigDecimal(durataEffettiva);
				esito = vch.aggiornaModalitaDurata(conn, prgModalita, durataEff, cdnParUtente);
			} else {
				esito = vch.cancellaModalitaDurata(conn, prgModalita, cdnParUtente);
			}

			if (esito != null) {
				if (esito.intValue() == 0) {
					transExec.commitTransaction();
					reportOperation.reportSuccess(MessageCodes.General.OPERATION_SUCCESS);
				} else {
					transExec.rollBackTransaction();

					switch (Math.abs(esito.intValue())) {
					case Properties.ERRORE_MODALITA_SPESAEFFETTIVA:
						reportOperation.reportFailure(MessageCodes.MSGVOUCHER.ERRORE_MODALITA_SPESAEFFETTIVA);
						break;
					case Properties.ERRORE_STATO_VCH:
						reportOperation.reportFailure(MessageCodes.MSGVOUCHER.ERRORE_STATO_VCH);
						break;
					case Properties.ERRORE_MODALITA_TIPOSERVIZIO:
						reportOperation.reportFailure(MessageCodes.MSGVOUCHER.ERRORE_MODALITA_TIPOSERVIZIO);
						break;
					case MessageCodes.General.CONCORRENZA:
						reportOperation.reportFailure(MessageCodes.General.CONCORRENZA);
						break;
					default:
						reportOperation.reportFailure(MessageCodes.General.OPERATION_FAIL);
					}
				}
			} else {
				transExec.rollBackTransaction();
				reportOperation.reportFailure(MessageCodes.General.OPERATION_FAIL);
			}
		}

		catch (Exception e) {
			if (transExec != null) {
				transExec.rollBackTransaction();
			}
			it.eng.sil.util.TraceWrapper.error(_logger, "AggiornaModalitaTDA", e);
			reportOperation.reportFailure(MessageCodes.General.OPERATION_FAIL);
		} finally {
			Utils.releaseResources(conn, null, null);
		}
	}

}
