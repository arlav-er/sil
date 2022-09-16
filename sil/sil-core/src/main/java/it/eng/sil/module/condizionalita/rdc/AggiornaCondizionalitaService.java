package it.eng.sil.module.condizionalita.rdc;

import java.math.BigDecimal;

import com.engiweb.framework.base.SessionContainer;
import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.utils.ReportOperationResult;
import it.eng.afExt.utils.StringUtils;
import it.eng.afExt.utils.TransactionQueryExecutor;
import it.eng.sil.Values;
import it.eng.sil.module.AbstractSimpleModule;
import it.eng.sil.security.User;

public class AggiornaCondizionalitaService extends AbstractSimpleModule {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5063256029388592127L;

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger
			.getLogger(AggiornaCondizionalitaService.class.getName());

	public static final int ESITO_OK = 10004;
	public static final int ESITO_KO = 10005;

	@Override
	public void service(SourceBean serviceRequest, SourceBean serviceResponse) throws Exception {

		ReportOperationResult reportOperation = new ReportOperationResult(this, serviceResponse);
		TransactionQueryExecutor trans = null;
		SessionContainer sessionContainer = getRequestContainer().getSessionContainer();
		User user = (User) sessionContainer.getAttribute(User.USERID);
		BigDecimal userid = new BigDecimal(user.getCodut());

		disableMessageIdFail();
		disableMessageIdSuccess();

		String prgCondizionalita = StringUtils.getAttributeStrNotNull(serviceRequest, "PRGCONDIZIONALITA");
		String numKLo = StringUtils.getAttributeStrNotNull(serviceRequest, "NUMKLO");
		String codEvento = StringUtils.getAttributeStrNotNull(serviceRequest, "tipoEvento");
		String cfOperatoreCpi = StringUtils.getAttributeStrNotNull(serviceRequest, "cfOperatoreCpi");
		String protocolloInps = StringUtils.getAttributeStrNotNull(serviceRequest, "protInps");
		String dataDomanda = StringUtils.getAttributeStrNotNull(serviceRequest, "dataDomanda");
		String dataEvento = StringUtils.getAttributeStrNotNull(serviceRequest, "dataEvento");
		String codDomCondizionalita = StringUtils.getAttributeStrNotNull(serviceRequest, "tipoDomanda");
		String prgPercorso = StringUtils.getAttributeStrNotNull(serviceRequest, "prgPercorso");
		String prgColloquio = StringUtils.getAttributeStrNotNull(serviceRequest, "prgColloquio");

		String strNote = StringUtils.getAttributeStrNotNull(serviceRequest, "strnote");

		try {
			trans = new TransactionQueryExecutor(Values.DB_SIL_DATI);

			trans.initTransaction();

			trans.executeQuery("UPDATE_AM_CONDIZIONALITA",
					new Object[] { dataDomanda, codEvento, protocolloInps, strNote, dataEvento,
							new BigDecimal(prgPercorso), userid, new BigDecimal(prgColloquio), cfOperatoreCpi,
							codDomCondizionalita, new BigDecimal(numKLo), new BigDecimal(prgCondizionalita) },
					"UPDATE");
			// -- COMMIT TRANSAZIONE
			trans.commitTransaction();
			serviceResponse.setAttribute("PRGCONDIZIONALITA", prgCondizionalita);
			reportOperation.reportSuccess(ESITO_OK);

		} catch (Throwable e) {
			if (trans != null) {
				trans.rollBackTransaction();
			}
			_logger.error("Errore: " + e);
			serviceResponse.setAttribute("ESITO_KO", "ESITO_KO");
			reportOperation.reportFailure(ESITO_KO);
		} finally {
			serviceResponse.setAttribute("CONDIZIONALITA_DONE", "CONDIZIONALITA_DONE");
		}

	}

}
