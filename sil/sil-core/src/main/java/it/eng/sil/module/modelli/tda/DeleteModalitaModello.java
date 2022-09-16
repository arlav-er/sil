package it.eng.sil.module.modelli.tda;

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

public class DeleteModalitaModello extends AbstractSimpleModule {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3326084560660448289L;
	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(DeleteModalitaModello.class.getName());

	@Override
	public void service(SourceBean serviceRequest, SourceBean serviceResponse) throws Exception {

		User user = (User) getRequestContainer().getSessionContainer().getAttribute(User.USERID);

		TransactionQueryExecutor transactionExecutor = null;
		DataConnection conn = null;

		ReportOperationResult reportOperation = new ReportOperationResult(this, serviceResponse);
		disableMessageIdFail();
		disableMessageIdSuccess();

		BigDecimal cdnParUtente = new BigDecimal(user.getCodut());
		String strPrgModelloTda = StringUtils.getAttributeStrNotNull(serviceRequest, "PRGMODVOUCHER");
		BigDecimal prgModelloTda = new BigDecimal(strPrgModelloTda);

		String strPrgModalitaModelloTda = StringUtils.getAttributeStrNotNull(serviceRequest, "PRGMODMODALITA");
		BigDecimal prgModalitaModelloTda = new BigDecimal(strPrgModalitaModelloTda);

		ModelloTda modelloTda = new ModelloTda(prgModelloTda);
		ModalitaModelloTda modalita = new ModalitaModelloTda(prgModalitaModelloTda);
		try {

			transactionExecutor = new TransactionQueryExecutor(getPool(), this);
			transactionExecutor.initTransaction();

			conn = transactionExecutor.getDataConnection();

			Integer esitoCancellazione = modalita.eliminaModalitaModelloTDA(conn, cdnParUtente);

			if (esitoCancellazione.intValue() == 0) {
				transactionExecutor.commitTransaction();
				Utils.releaseResources(conn, null, null);
				reportOperation.reportSuccess(MessageCodes.MODELLITDA.MODALITA_MOD_DEL_OK);
				// aggiornamento valore totale
				transactionExecutor = new TransactionQueryExecutor(getPool(), this);
				transactionExecutor.initTransaction();
				conn = transactionExecutor.getDataConnection();
				Integer esitoTotale = modelloTda.calcolaValoreTotaleModelloTDA(conn, cdnParUtente);
				if (esitoTotale.intValue() == Properties.MODELLO_CALC_VAL_CALCOLATO
						|| esitoTotale.intValue() == Properties.MODELLO_CALC_VAL_IMPUTABILE) {
					transactionExecutor.commitTransaction();
					switch (Math.abs(esitoTotale.intValue())) {
					case Properties.MODELLO_CALC_VAL_CALCOLATO:
						reportOperation.reportSuccess(MessageCodes.MODELLITDA.MODELLO_CALC_VAL_CALCOLATO);
						break;
					case Properties.MODELLO_CALC_VAL_IMPUTABILE:
						reportOperation.reportSuccess(MessageCodes.MODELLITDA.MODELLO_CALC_VAL_IMPUTABILE);
						break;
					}
				} else {
					transactionExecutor.rollBackTransaction();
					switch (Math.abs(esitoTotale.intValue())) {
					case Properties.ERRORE_VALTOT_MODTDA_NONCALC:
						reportOperation.reportFailure(MessageCodes.MODELLITDA.ERRORE_VALTOT_MODTDA_NONCALC);
						break;
					case MessageCodes.General.CONCORRENZA:
						reportOperation.reportFailure(MessageCodes.General.CONCORRENZA);
						break;
					default:
						reportOperation.reportFailure(MessageCodes.General.OPERATION_FAIL);
					}
				}
			} else {
				transactionExecutor.rollBackTransaction();
				switch (Math.abs(esitoCancellazione.intValue())) {
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
			it.eng.sil.util.TraceWrapper.debug(_logger, DeleteModalitaModello.class.getName() + ": errore ", ex);
		} finally {
			Utils.releaseResources(conn, null, null);
		}
	}

}
