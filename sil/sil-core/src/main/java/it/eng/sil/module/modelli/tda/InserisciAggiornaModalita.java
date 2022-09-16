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

public class InserisciAggiornaModalita extends AbstractSimpleModule {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3678650555054719991L;
	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger
			.getLogger(InserisciAggiornaModalita.class.getName());

	private String className = this.getClass().getName();

	@Override
	public void service(SourceBean request, SourceBean response) throws Exception {
		User user = (User) getRequestContainer().getSessionContainer().getAttribute(User.USERID);
		String operationType = StringUtils.getAttributeStrNotNull(request, "OPERAZIONEMODALITA");

		TransactionQueryExecutor transactionExecutor = null;
		DataConnection conn = null;

		ReportOperationResult reportOperation = new ReportOperationResult(this, response);
		disableMessageIdFail();
		disableMessageIdSuccess();

		BigDecimal cdnParUtente = new BigDecimal(user.getCodut());
		String strPrgModelloTda = StringUtils.getAttributeStrNotNull(request, "PRGMODVOUCHER");
		BigDecimal prgModelloTda = new BigDecimal(strPrgModelloTda);

		String codModalita = StringUtils.getAttributeStrNotNull(request, "codModalita");
		String tipoDurata = StringUtils.getAttributeStrNotNull(request, "strTipoDurata");
		String strDurataMin = StringUtils.getAttributeStrNotNull(request, "strDurataMin");
		String strDurataMax = StringUtils.getAttributeStrNotNull(request, "strDurataMax");
		String tipoRimborso = StringUtils.getAttributeStrNotNull(request, "strRimborso");
		String strValUnit = StringUtils.getAttributeStrNotNull(request, "strValUnit");
		String strValTot = StringUtils.getAttributeStrNotNull(request, "strValTot");
		String strPercentuale = StringUtils.getAttributeStrNotNull(request, "strPercentuale");
		BigDecimal durataMinima = null;
		if (strDurataMin != "") {
			durataMinima = new BigDecimal(strDurataMin);
		}
		BigDecimal durataMassima = null;
		if (strDurataMax != "") {
			durataMassima = new BigDecimal(strDurataMax);
		}
		Double valoreUnitario = null;
		if (strValUnit != "") {
			valoreUnitario = new Double(strValUnit);
		}
		Double valoreTotale = null;
		if (strValTot != "") {
			valoreTotale = new Double(strValTot);
		}
		BigDecimal percentuale = null;
		if (strPercentuale != "") {
			percentuale = new BigDecimal(strPercentuale);
		}
		ModalitaModelloTda modalitaModello = null;
		ModelloTda modelloTda = new ModelloTda(prgModelloTda);
		boolean isInsert = operationType.equalsIgnoreCase("INSERISCI") ? true : false;
		if (isInsert) {
			modalitaModello = new ModalitaModelloTda(codModalita, prgModelloTda);
		} else {
			String strPrgModalita = StringUtils.getAttributeStrNotNull(request, "PRGMODMODALITA");
			BigDecimal prgModalita = new BigDecimal(strPrgModalita);
			String strNumKL = StringUtils.getAttributeStrNotNull(request, "NUMKLO");
			BigDecimal numKeyLock = new BigDecimal(strNumKL);
			modalitaModello = new ModalitaModelloTda(prgModalita, numKeyLock, codModalita, prgModelloTda);
		}

		try {

			transactionExecutor = new TransactionQueryExecutor(getPool(), this);
			transactionExecutor.initTransaction();

			conn = transactionExecutor.getDataConnection();

			BigDecimal esito = modalitaModello.gestisciModalita(conn, isInsert, tipoDurata, durataMinima, durataMassima,
					percentuale, tipoRimborso, valoreUnitario, valoreTotale, cdnParUtente);

			if (esito.intValue() > 0) {
				transactionExecutor.commitTransaction();
				Utils.releaseResources(conn, null, null);
				if (isInsert) {
					response.setAttribute("PRGMODMODALITA", esito);
					reportOperation.reportSuccess(MessageCodes.MODELLITDA.MODALITA_MOD_INS_OK);
				} else {
					reportOperation.reportSuccess(MessageCodes.MODELLITDA.MODALITA_MOD_UPD_OK);
				}
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
				if (isInsert) {
					response.setAttribute("INS_MOD_ERROR", "INS_MOD_ERROR");
				}
				transactionExecutor.rollBackTransaction();
				switch (Math.abs(esito.intValue())) {
				case Properties.ERRORE_MODELLO_ATTIVO:
					reportOperation.reportFailure(MessageCodes.MODELLITDA.ERRORE_MODELLO_ATTIVO);
					break;
				case Properties.ERRORE_MODALITA_TOT_VAL:
					reportOperation.reportFailure(MessageCodes.MODELLITDA.ERRORE_MODALITA_TOT_VAL);
					break;
				case Properties.ERRORE_MODELLO_CALC_VAL:
					reportOperation.reportFailure(MessageCodes.MODELLITDA.ERRORE_MODELLO_CALC_VAL);
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
	}
}
