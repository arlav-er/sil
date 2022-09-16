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

public class InserisciAggiornaModelloTda extends AbstractSimpleModule {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3678650555054719991L;
	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger
			.getLogger(InserisciAggiornaModelloTda.class.getName());

	private String className = this.getClass().getName();

	@Override
	public void service(SourceBean request, SourceBean response) throws Exception {
		User user = (User) getRequestContainer().getSessionContainer().getAttribute(User.USERID);
		String operationType = StringUtils.getAttributeStrNotNull(request, "OPERAZIONEMODELLO");

		TransactionQueryExecutor transactionExecutor = null;
		DataConnection conn = null;

		ReportOperationResult reportOperation = new ReportOperationResult(this, response);
		disableMessageIdFail();
		disableMessageIdSuccess();

		BigDecimal cdnParUtente = new BigDecimal(user.getCodut());

		String flagCM = StringUtils.getAttributeStrNotNull(request, "tdaCM");
		String modelloAttivo = StringUtils.getAttributeStrNotNull(request, "modelloAttivo");
		String strCodModalita = StringUtils.getAttributeStrNotNull(request, "codModalita");
		String strGiorniAttivazione = StringUtils.getAttributeStrNotNull(request, "giorniAttivazione");
		String strGiorniErogazione = StringUtils.getAttributeStrNotNull(request, "giorniErogazione");
		String strValoreMax = StringUtils.getAttributeStrNotNull(request, "strValoreMax");
		BigDecimal giorniAtt = null;
		if (strGiorniAttivazione != "") {
			giorniAtt = new BigDecimal(strGiorniAttivazione);
		}
		BigDecimal giorniErog = null;
		if (strGiorniErogazione != "") {
			giorniErog = new BigDecimal(strGiorniErogazione);
		}
		Double valoreMax = null;
		if (strValoreMax != "") {
			valoreMax = new Double(strValoreMax);
		}
		ModelloTda modelloTda = null;
		boolean isInsert = operationType.equalsIgnoreCase("INSERISCI") ? true : false;
		boolean isDisattiva = false;
		if (isInsert) {
			String strPrgAzioni = StringUtils.getAttributeStrNotNull(request, "prgAzioni");
			BigDecimal prgAzioni = new BigDecimal(strPrgAzioni);
			modelloTda = new ModelloTda(null, prgAzioni, modelloAttivo);

		} else {
			isDisattiva = operationType.equalsIgnoreCase("DISATTIVA") ? true : false;
			String strPrgModelloTda = StringUtils.getAttributeStrNotNull(request, "PRGMODVOUCHER");
			BigDecimal prgModelloTda = new BigDecimal(strPrgModelloTda);
			String strNumKL = StringUtils.getAttributeStrNotNull(request, "NUMKLO");
			BigDecimal numKeyLock = new BigDecimal(strNumKL);
			modelloTda = new ModelloTda(prgModelloTda, numKeyLock);
		}

		try {

			transactionExecutor = new TransactionQueryExecutor(getPool(), this);
			transactionExecutor.initTransaction();

			conn = transactionExecutor.getDataConnection();

			Integer esito = null;
			Integer esitoCoerenza = null;
			Integer esitoAttivazione = null;
			if (isInsert) {
				esito = modelloTda.inserisciModello(conn, flagCM, strCodModalita, giorniAtt, giorniErog, valoreMax,
						cdnParUtente);
			} else if (!isDisattiva) {
				esito = modelloTda.aggiornaModello(conn, flagCM, strCodModalita, giorniAtt, giorniErog, valoreMax,
						cdnParUtente);
			} else if (isDisattiva) {
				esito = modelloTda.attivaDisattivaModello(conn, cdnParUtente, modelloAttivo);
			}

			if (!isDisattiva && esito.intValue() > 0) {
				transactionExecutor.commitTransaction();
				Utils.releaseResources(conn, null, null);
				if (isInsert) {
					reportOperation.reportSuccess(MessageCodes.MODELLITDA.MODELLO_INS_OK);
					response.setAttribute("INS_OK", "INS_OK");
					response.setAttribute("PRGMODVOUCHER", esito);
					modelloTda.setPrgModello(new BigDecimal(esito.intValue()));
				} else {
					reportOperation.reportSuccess(MessageCodes.MODELLITDA.MODELLO_UPD_OK);
				}
				// aggiornamento valore totale
				transactionExecutor = new TransactionQueryExecutor(getPool(), this);
				transactionExecutor.initTransaction();
				conn = transactionExecutor.getDataConnection();
				Integer esitoTotale = modelloTda.calcolaValoreTotaleModelloTDA(conn, cdnParUtente);
				boolean canContinue = true;
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
					canContinue = false;
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
				// controlli di coerenza perl'attivazione di un modello
				if (canContinue && StringUtils.isFilledNoBlank(modelloAttivo) && modelloAttivo.equalsIgnoreCase("S")) {
					Utils.releaseResources(conn, null, null);
					transactionExecutor = new TransactionQueryExecutor(getPool(), this);
					transactionExecutor.initTransaction();
					conn = transactionExecutor.getDataConnection();
					boolean isAttivabile = false;
					esitoCoerenza = modelloTda.controllaCoerenzaModello(conn);
					if (esitoCoerenza >= 0) {
						switch (Math.abs(esitoCoerenza.intValue())) {
						case Properties.COERENZA_PIENA:
							isAttivabile = true;
							break;
						case Properties.ERRORE_NON_BLOCCANTE_ATTIVITA:
							reportOperation.reportSuccess(MessageCodes.MODELLITDA.ERRORE_NON_BLOCCANTE_ATTIVITA);
							isAttivabile = true;
							break;
						case Properties.ERRORE_BLOCCANTE_MODELLO:
							isAttivabile = false;
							reportOperation.reportFailure(MessageCodes.MODELLITDA.ERRORE_BLOCCANTE_MODELLO);
							break;
						case Properties.ERRORE_BLOCCANTE_MODALITA:
							isAttivabile = false;
							reportOperation.reportFailure(MessageCodes.MODELLITDA.ERRORE_BLOCCANTE_MODALITA);
							break;
						}
						if (isAttivabile) {
							esitoAttivazione = modelloTda.attivaDisattivaModello(conn, cdnParUtente, modelloAttivo);
							if (esitoAttivazione.intValue() == 0) {
								transactionExecutor.commitTransaction();
								reportOperation.reportSuccess(MessageCodes.MODELLITDA.MODELLO_ATTIVATO);
							} else {
								transactionExecutor.rollBackTransaction();
								switch (Math.abs(esitoAttivazione.intValue())) {
								case MessageCodes.General.CONCORRENZA:
									reportOperation.reportFailure(MessageCodes.General.CONCORRENZA);
									break;
								default:
									reportOperation.reportFailure(MessageCodes.General.OPERATION_FAIL);
								}
							}
						}
					} else {
						reportOperation.reportFailure(MessageCodes.General.OPERATION_FAIL);
					}
				} else if (!canContinue && StringUtils.isFilledNoBlank(modelloAttivo)
						&& modelloAttivo.equalsIgnoreCase("S")) {
					reportOperation.reportFailure(MessageCodes.MODELLITDA.ERRORE_BLOCCANTE_MODELLO);
				}
			} else if (!isDisattiva && esito.intValue() == -1) {
				transactionExecutor.rollBackTransaction();
				switch (Math.abs(esito.intValue())) {
				case MessageCodes.General.CONCORRENZA:
					reportOperation.reportFailure(MessageCodes.General.CONCORRENZA);
					break;
				default:
					reportOperation.reportFailure(MessageCodes.General.OPERATION_FAIL);
				}
			}

			if (isDisattiva && esito.intValue() == 0) {
				transactionExecutor.commitTransaction();
				reportOperation.reportSuccess(MessageCodes.MODELLITDA.MODELLO_DISATTIVATO);
			} else if (isDisattiva) {
				transactionExecutor.rollBackTransaction();
				switch (Math.abs(esito.intValue())) {
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
