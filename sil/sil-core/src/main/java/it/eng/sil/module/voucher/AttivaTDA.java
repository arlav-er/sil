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

public class AttivaTDA extends AbstractSimpleModule {

	private static final long serialVersionUID = -1533726811526662058L;
	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(AttivaTDA.class.getName());

	private String className = this.getClass().getName();

	public void service(SourceBean request, SourceBean response) throws Exception {
		TransactionQueryExecutor transExec = null;
		DataConnection conn = null;
		User user = (User) getRequestContainer().getSessionContainer().getAttribute(User.USERID);
		BigDecimal cdnParUtente = new BigDecimal(user.getCodut());
		String tipoGruppoCollegato = user.getCodTipo();

		int idSuccess = this.disableMessageIdSuccess();
		int idFail = this.disableMessageIdFail();
		ReportOperationResult reportOperation = new ReportOperationResult(this, response);
		disableMessageIdFail();
		disableMessageIdSuccess();
		String cfEnte = "";
		String sedeEnte = "";
		if (tipoGruppoCollegato.equalsIgnoreCase(it.eng.sil.module.voucher.Properties.SOGGETTO_ACCREDITATO)) {
			cfEnte = StringUtils.getAttributeStrNotNull(request, "enteAtt");
			sedeEnte = StringUtils.getAttributeStrNotNull(request, "sedeEnteAttivazione");
		} else {
			String cfCodSedeEnte = StringUtils.getAttributeStrNotNull(request, "sedeEnteAttivazione");
			if (!cfCodSedeEnte.equalsIgnoreCase("")) {
				String[] sede = cfCodSedeEnte.split("!");
				cfEnte = sede[0].toUpperCase();
				sedeEnte = sede[1].toUpperCase();
			}
		}

		String cfLavoratore = StringUtils.getAttributeStrNotNull(request, "strCodiceFiscaleCittadino");
		String codiceAttivazione = StringUtils.getAttributeStrNotNull(request, "strCodiceTitolo");
		String dataAttivazione = StringUtils.getAttributeStrNotNull(request, "dtAttivazione");
		// dataAttivazione deve arrivare sempre valorizzato
		if (dataAttivazione.equals("")) {
			dataAttivazione = DateUtils.getNow();
		}
		_logger.info("La procedura di attivazione voucher e' stata chiamata");
		//
		try {
			String pool = (String) getConfig().getAttribute("POOL");
			transExec = new TransactionQueryExecutor(pool);
			transExec.initTransaction();

			Voucher vch = new Voucher();
			conn = transExec.getDataConnection();

			String esito = vch.attivaAllaData(conn, cfEnte, sedeEnte, cfLavoratore, codiceAttivazione, cdnParUtente,
					dataAttivazione);

			if (esito != null && !esito.equals("")) {
				Integer esitoOp = new Integer(esito);
				if (esitoOp.intValue() == 0) {
					transExec.commitTransaction();
					reportOperation.reportSuccess(MessageCodes.General.OPERATION_SUCCESS);
				} else {
					transExec.rollBackTransaction();

					switch (Math.abs(esitoOp.intValue())) {
					case Properties.ERRORE_CODICE_ATT_INESISTENTE:
						reportOperation.reportFailure(MessageCodes.MSGVOUCHER.ERRORE_CODICE_ATT_INESISTENTE);
						break;
					case Properties.ERRORE_CODICE_ATT_LAV_INESISTENTE:
						reportOperation.reportFailure(MessageCodes.MSGVOUCHER.ERRORE_CODICE_ATT_LAV_INESISTENTE);
						break;
					case Properties.ERRORE_ATTIVAZIONE_SCADUTA:
						reportOperation.reportFailure(MessageCodes.MSGVOUCHER.ERRORE_ATTIVAZIONE_SCADUTA);
						break;
					case Properties.ERRORE_VOUCHER_NON_ATTIVABILE:
						reportOperation.reportFailure(MessageCodes.MSGVOUCHER.ERRORE_VOUCHER_NON_ATTIVABILE);
						break;
					case Properties.ERRORE_ENTE_ATTIVAZIONE_SERVIZI:
						reportOperation.reportFailure(MessageCodes.MSGVOUCHER.ERRORE_ENTE_ATTIVAZIONE_SERVIZI);
						break;
					case MessageCodes.General.CONCORRENZA:
						reportOperation.reportFailure(MessageCodes.General.CONCORRENZA);
						break;
					default:
						reportOperation.reportFailure(MessageCodes.MSGVOUCHER.ERRORE_GENERICO_ATTIVAZIONE);
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
			it.eng.sil.util.TraceWrapper.error(_logger, "AttivaTDA", e);
			reportOperation.reportFailure(MessageCodes.General.OPERATION_FAIL);
		} finally {
			Utils.releaseResources(conn, null, null);
		}
	}

}
