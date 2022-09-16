package it.eng.sil.module.condizionalita.rdc;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Vector;

import javax.xml.rpc.holders.StringHolder;

import com.engiweb.framework.base.SessionContainer;
import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.utils.DateUtils;
import it.eng.afExt.utils.ReportOperationResult;
import it.eng.afExt.utils.StringUtils;
import it.eng.afExt.utils.TransactionQueryExecutor;
import it.eng.sil.Values;
import it.eng.sil.coop.DataSourceJNDI;
import it.eng.sil.coop.endpoints.EndPointSingleton;
import it.eng.sil.module.AbstractSimpleModule;
import it.eng.sil.security.User;
import it.gov.lavoro.servizi.EventiCondizionalitaRDC.EventiCondizionalitaRDCWSProxy;
import it.gov.lavoro.servizi.EventiCondizionalitaRDC.types.Cod_evento;

public class EventiCondizionalitaService extends AbstractSimpleModule {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5063256029388592127L;

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger
			.getLogger(EventiCondizionalitaService.class.getName());

	private String END_POINT = "condizionalita";

	public static final int WS_CONDZIONALITA = 961001;
	public static final int ERR_INPUT = 961002;
	public static final int ERR_LOG = 961003;
	public static final int ERR_GENERICO = 961004;
	public static final int ERR_CF = 961005;
	public static final int ERR_EVENTO = 961006;
	public static final int ESITO_OK = 961007;
	public static final int ESITO_KO_PARAM = 961008;

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

		String cfBeneficiario = StringUtils.getAttributeStrNotNull(serviceRequest, "cfBeneficiario");
		String codiceCpiMin = StringUtils.getAttributeStrNotNull(serviceRequest, "codiceCpiMin");
		String codEvento = StringUtils.getAttributeStrNotNull(serviceRequest, "tipoEvento");
		String cfOperatoreCpi = StringUtils.getAttributeStrNotNull(serviceRequest, "cfOperatoreCpi");
		String protocolloInps = StringUtils.getAttributeStrNotNull(serviceRequest, "protInps");
		String dataDomanda = StringUtils.getAttributeStrNotNull(serviceRequest, "dataDomanda");
		String dataEvento = StringUtils.getAttributeStrNotNull(serviceRequest, "dataEvento");

		Calendar calDomanda = Calendar.getInstance();
		calDomanda.setTime(DateUtils.getDate(dataDomanda));

		Calendar calEvento = Calendar.getInstance();
		calEvento.setTime(DateUtils.getDate(dataEvento));

		String strNote = StringUtils.getAttributeStrNotNull(serviceRequest, "strnote");
		StringHolder codEsito = new StringHolder();
		StringHolder descrEsito = new StringHolder();

		try {
			DataSourceJNDI dataSourceJndi = new DataSourceJNDI();
			String dataSourceJndiName = dataSourceJndi.getJndi();
			EndPointSingleton eps = EndPointSingleton.getInstance(dataSourceJndiName);
			String endPoint = null;

			endPoint = eps.getUrl(END_POINT);

			EventiCondizionalitaRDCWSProxy eventiCondizionalitaProxy = new EventiCondizionalitaRDCWSProxy(endPoint);

			eventiCondizionalitaProxy.loadEventiCondizionalitaRDC(codiceCpiMin, Cod_evento.fromValue(codEvento),
					cfBeneficiario, cfOperatoreCpi, protocolloInps, calDomanda, calEvento, strNote, codEsito,
					descrEsito);

			if (codEsito.value.equalsIgnoreCase("E100") || codEsito.value.equalsIgnoreCase("OK")) {
				trans = new TransactionQueryExecutor(Values.DB_SIL_DATI);
				trans.initTransaction();

				String numKLo = StringUtils.getAttributeStrNotNull(serviceRequest, "NUMKLO");

				trans.executeQuery("UPDATE_AM_CONDIZIONALITA_INVIO_ANPAL",
						new Object[] { userid, new BigDecimal(numKLo), new BigDecimal(prgCondizionalita) }, "UPDATE");
				// -- COMMIT TRANSAZIONE
				trans.commitTransaction();
				serviceResponse.setAttribute("ESITO_OK", "ESITO_OK");
				serviceResponse.setAttribute("PRGCONDIZIONALITA", prgCondizionalita);
				reportOperation.reportSuccess(ESITO_OK);

				/*
				 * }else if(codEsito.value.equalsIgnoreCase("E001")){ reportOperation.reportFailure(ERR_INPUT); }else
				 * if(codEsito.value.equalsIgnoreCase("E997")){ reportOperation.reportFailure(ERR_LOG); }else
				 * if(codEsito.value.equalsIgnoreCase("E999")){ reportOperation.reportFailure(ERR_GENERICO); }else
				 * if(codEsito.value.equalsIgnoreCase("E101")){ reportOperation.reportFailure(ERR_CF); }else
				 * if(codEsito.value.equalsIgnoreCase("E102")){ reportOperation.reportFailure(ERR_EVENTO); }
				 */
			} else {
				serviceResponse.setAttribute("ESITO_KO", "ESITO_KO");
				Vector<String> descrizioneErrore = new Vector<String>(2);
				descrizioneErrore.add(0, codEsito.value);
				descrizioneErrore.add(1, descrEsito.value);
				reportOperation.reportFailure(ESITO_KO_PARAM, "service", "", descrizioneErrore);

			}
		} catch (Throwable e) {
			if (trans != null)
				trans.rollBackTransaction();
			_logger.error("Errore: " + e);
			serviceResponse.setAttribute("ESITO_KO", "ESITO_KO");
			reportOperation.reportFailure(WS_CONDZIONALITA);
		} finally {
			if (trans != null)
				trans.closeConnTransaction();
			serviceResponse.setAttribute("CONDIZIONALITA_DONE", "CONDIZIONALITA_DONE");
		}

	}

}
