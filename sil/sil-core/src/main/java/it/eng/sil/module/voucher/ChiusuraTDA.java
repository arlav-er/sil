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

public class ChiusuraTDA extends AbstractSimpleModule {

	private static final long serialVersionUID = -1533726811526662058L;
	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(ChiusuraTDA.class.getName());

	private String className = this.getClass().getName();

	public void service(SourceBean request, SourceBean response) throws Exception {
		TransactionQueryExecutor transExec = null;
		DataConnection conn = null;
		User user = (User) getRequestContainer().getSessionContainer().getAttribute(User.USERID);
		BigDecimal cdnParUtente = new BigDecimal(user.getCodut());
		String dataMassimaErogazione = StringUtils.getAttributeStrNotNull(request, "datmaxconclusione");
		String dataFineErogazione = StringUtils.getAttributeStrNotNull(request, "datconclusione");
		String progressivoVoucher = StringUtils.getAttributeStrNotNull(request, "PRGVOUCHER");
		String codVchTipoRisultato = StringUtils.getAttributeStrNotNull(request, "codVchTipoRisultato");
		String descRisultatoRaggiunto = StringUtils.getAttributeStrNotNull(request, "descServizioRisultato");
		BigDecimal prgVoucher = new BigDecimal(progressivoVoucher);

		String dataOdierna = DateUtils.getNow();

		int idSuccess = this.disableMessageIdSuccess();
		int idFail = this.disableMessageIdFail();
		ReportOperationResult reportOperation = new ReportOperationResult(this, response);
		disableMessageIdFail();
		disableMessageIdSuccess();

		_logger.info("La procedura di chiusura voucher e' stata chiamata");

		if (dataFineErogazione.equals("") || DateUtils.compare(dataFineErogazione, dataOdierna) > 0) {
			reportOperation.reportFailure(MessageCodes.MSGVOUCHER.ERRORE_DATA_CHIUSURA_FUTURA);
			return;
		}

		if (dataFineErogazione.equals("") || DateUtils.compare(dataFineErogazione, dataMassimaErogazione) > 0) {
			reportOperation.reportFailure(MessageCodes.MSGVOUCHER.ERRORE_DATA_CHIUSURA_SCADENZA_VOUCHER);
			return;
		}

		this.setSectionQuerySelect("QUERY_ENTE");
		SourceBean row = doSelect(request, response);
		if (row != null) {
			String codStatoVch = row.getAttribute("ROW.codstatovoucher") != null
					? row.getAttribute("ROW.codstatovoucher").toString()
					: "";
			String cfEnte = row.getAttribute("ROW.strcfenteaccreditato") != null
					? row.getAttribute("ROW.strcfenteaccreditato").toString()
					: "";
			if (!codStatoVch.equalsIgnoreCase(StatoEnum.ATTIVATO.getCodice())) {
				reportOperation.reportFailure(MessageCodes.MSGVOUCHER.ERRORE_CHIUSURA_STATO_VOUCHER);
				return;
			}
			if (!cfEnte.equalsIgnoreCase(user.getCfUtenteCollegato())) {
				reportOperation.reportFailure(MessageCodes.MSGVOUCHER.ERRORE_CHIUSURA_VOUCHER_ENTE_COLLEGATO);
				return;
			}
		} else {
			reportOperation.reportFailure(MessageCodes.General.OPERATION_FAIL);
			return;
		}

		try {

			String pool = (String) getConfig().getAttribute("POOL");
			transExec = new TransactionQueryExecutor(pool);
			transExec.initTransaction();

			Voucher vch = new Voucher(prgVoucher);
			conn = transExec.getDataConnection();

			Integer esito = vch.chiusuraTDA(conn, dataFineErogazione, codVchTipoRisultato, descRisultatoRaggiunto,
					cdnParUtente);

			if (esito != null) {
				if (esito.intValue() == 0) {
					transExec.commitTransaction();
					reportOperation.reportSuccess(MessageCodes.General.OPERATION_SUCCESS);
				} else {
					transExec.rollBackTransaction();

					switch (Math.abs(esito.intValue())) {
					case Properties.ERRORE_CHIUSURA_EV_ATT_OBIETT:
						reportOperation.reportFailure(MessageCodes.MSGVOUCHER.ERRORE_CHIUSURA_EV_ATT_OBIETT);
						break;
					case Properties.ERRORE_CHIUSURA_MODALITA_ASSENTE:
						reportOperation.reportFailure(MessageCodes.MSGVOUCHER.ERRORE_CHIUSURA_MODALITA_ASSENTE);
						break;
					case Properties.ERRORE_CHIUSURA_MODALITA_TIPO_S:
						reportOperation.reportFailure(MessageCodes.MSGVOUCHER.ERRORE_CHIUSURA_MODALITA_TIPO_S);
						break;
					case Properties.ERRORE_CHIUSURA_MODALITA_TIPO_T:
						reportOperation.reportFailure(MessageCodes.MSGVOUCHER.ERRORE_CHIUSURA_MODALITA_TIPO_T);
						break;
					case Properties.ERRORE_STATO_VCH_CHIUSURA:
						reportOperation.reportFailure(MessageCodes.MSGVOUCHER.ERRORE_STATO_VCH);
						break;
					case Properties.ERRORE_QUALIFICA_CHIUSURA:
						reportOperation.reportFailure(MessageCodes.MSGVOUCHER.ERRORE_QUALIFICA_CHIUSURA);
						break;
					case Properties.ERRORE_CHIUSURA_IMPORTI_SR:
						reportOperation.reportFailure(MessageCodes.MSGVOUCHER.ERRORE_CHIUSURA_IMPORTI_SR);
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
			it.eng.sil.util.TraceWrapper.error(_logger, "ChiusuraTDA", e);
			reportOperation.reportFailure(MessageCodes.General.OPERATION_FAIL);
		} finally {
			Utils.releaseResources(conn, null, null);
		}
	}

}
