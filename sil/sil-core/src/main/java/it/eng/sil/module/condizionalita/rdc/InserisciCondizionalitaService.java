package it.eng.sil.module.condizionalita.rdc;

import java.math.BigDecimal;
import java.util.Calendar;

import com.engiweb.framework.base.SessionContainer;
import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.error.EMFInternalError;

import it.eng.afExt.dbaccess.sql.DBKeyGenerator;
import it.eng.afExt.utils.DateUtils;
import it.eng.afExt.utils.ReportOperationResult;
import it.eng.afExt.utils.StringUtils;
import it.eng.afExt.utils.TransactionQueryExecutor;
import it.eng.sil.Values;
import it.eng.sil.module.AbstractSimpleModule;
import it.eng.sil.security.User;

public class InserisciCondizionalitaService extends AbstractSimpleModule {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5063256029388592127L;

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger
			.getLogger(InserisciCondizionalitaService.class.getName());

	public static final int INSERIMENTO_OK = 10007;
	public static final int INSERIMENTO_KO = 10008;
	public static final int OPERAZIONE_FALLITA = 10010;

	@Override
	public void service(SourceBean serviceRequest, SourceBean serviceResponse) throws Exception {

		ReportOperationResult reportOperation = new ReportOperationResult(this, serviceResponse);
		TransactionQueryExecutor trans = null;
		SessionContainer sessionContainer = getRequestContainer().getSessionContainer();
		User user = (User) sessionContainer.getAttribute(User.USERID);
		BigDecimal userid = new BigDecimal(user.getCodut());

		disableMessageIdFail();
		disableMessageIdSuccess();

		String cdnLavoratore = StringUtils.getAttributeStrNotNull(serviceRequest, "cdnLavoratore");
		String codCpi = StringUtils.getAttributeStrNotNull(serviceRequest, "codiceCpi");
		String codEvento = StringUtils.getAttributeStrNotNull(serviceRequest, "tipoEvento");
		String cfOperatoreCpi = StringUtils.getAttributeStrNotNull(serviceRequest, "cfOperatoreCpi");
		String protocolloInps = StringUtils.getAttributeStrNotNull(serviceRequest, "protInps");
		String dataDomanda = StringUtils.getAttributeStrNotNull(serviceRequest, "dataDomanda");
		String dataEvento = StringUtils.getAttributeStrNotNull(serviceRequest, "dataEvento");
		String codDomCondizionalita = StringUtils.getAttributeStrNotNull(serviceRequest, "tipoDomanda");
		String prgPercorso = StringUtils.getAttributeStrNotNull(serviceRequest, "prgPercorso");
		String prgColloquio = StringUtils.getAttributeStrNotNull(serviceRequest, "prgColloquio");

		Calendar calDomanda = Calendar.getInstance();
		calDomanda.setTime(DateUtils.getDate(dataDomanda));

		Calendar calEvento = Calendar.getInstance();
		calEvento.setTime(DateUtils.getDate(dataEvento));

		String strNote = StringUtils.getAttributeStrNotNull(serviceRequest, "strnote");

		try {

			// inserimento nel db
			trans = new TransactionQueryExecutor(Values.DB_SIL_DATI);

			trans.initTransaction();

			BigDecimal prgAmCondizionalita = DBKeyGenerator.getNextSequence(trans.getDataConnection(),
					"S_AM_CONDIZIONALITA");

			trans.executeQuery("INSERT_AM_CONDIZIONALITA",
					new Object[] { dataDomanda, codEvento, protocolloInps, strNote, userid,
							new BigDecimal(cdnLavoratore), codCpi, prgAmCondizionalita, dataEvento,
							new BigDecimal(prgPercorso), userid, new BigDecimal(prgColloquio), cfOperatoreCpi,
							codDomCondizionalita },
					"INSERT");
			// -- COMMIT TRANSAZIONE
			trans.commitTransaction();
			serviceResponse.setAttribute("PRGCONDIZIONALITA", prgAmCondizionalita);
			reportOperation.reportSuccess(INSERIMENTO_OK);

		} catch (EMFInternalError emf) {
			trans.rollBackTransaction();
			_logger.error("Errore: " + emf);
			serviceResponse.setAttribute("ESITO_KO", "ESITO_KO");
			reportOperation.reportFailure(INSERIMENTO_KO);
		} catch (Throwable e) {
			trans.rollBackTransaction();
			_logger.error("Errore: " + e);
			serviceResponse.setAttribute("ESITO_KO", "ESITO_KO");
			reportOperation.reportFailure(OPERAZIONE_FALLITA);
		} finally {
			trans.closeConnTransaction();
			serviceResponse.setAttribute("CONDIZIONALITA_DONE", "CONDIZIONALITA_DONE");
		}

	}

}
