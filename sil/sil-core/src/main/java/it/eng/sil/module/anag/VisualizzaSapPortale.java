package it.eng.sil.module.anag;

import java.math.BigDecimal;
import java.rmi.RemoteException;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.base.SourceBeanException;

import it.eng.afExt.utils.MessageCodes;
import it.eng.afExt.utils.ReportOperationResult;
import it.eng.sap.xml.sap.SchedaAnagraficaProfessionaleDTO;
import it.eng.sil.coop.DataSourceJNDI;
import it.eng.sil.coop.endpoints.EndPointSingleton;
import it.eng.sil.module.AbstractSimpleModule;
import it.eng.sil.mysap.model.ejb.stateless.ws.exportsap.MySapWsException;
import it.eng.sil.mysap.model.ejb.stateless.ws.exportsap.SchedaAnagraficaProfessionaleWSProxy;
import it.eng.sil.security.User;
import it.eng.sil.util.Utils;

public class VisualizzaSapPortale extends AbstractSimpleModule {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1533726811528932058L;
	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(VisualizzaSapPortale.class.getName());
	private String className = this.getClass().getName();
	private String END_POINT_NAME = "GestioneSapPortale";
	public static final String FORMATO_DATA = "dd/MM/yyyy";

	public void service(SourceBean request, SourceBean response) {
		User user = (User) getRequestContainer().getSessionContainer().getAttribute("@@USER@@");
		BigDecimal p_cdnUtente = new BigDecimal(user.getCodut());

		String cdnLavoratore = Utils.notNull(request.getAttribute("CDNLAVORATORE"));
		Integer idSap = Integer.parseInt(Utils.notNull(request.getAttribute("idSap")));
		String userName = Utils.notNull(request.getAttribute("userName"));
		ReportOperationResult reportOperation = new ReportOperationResult(this, response);
		disableMessageIdFail();
		disableMessageIdSuccess();

		try {
			_logger.info("CHIAMATA DETTAGLIO SAP SU PORTALE, IDSAP =" + idSap + ", userName =" + userName);

			DataSourceJNDI dataSourceJndi = new DataSourceJNDI();
			String dataSourceJndiName = dataSourceJndi.getJndi();
			EndPointSingleton eps = EndPointSingleton.getInstance(dataSourceJndiName);
			String VisualizzaSapPortaleEndPoint = eps.getUrl(END_POINT_NAME);

			// verifica se il ws è disponibile
			if (VisualizzaSapPortaleEndPoint.startsWith("__sbagliato__")) {
				reportOperation.reportFailure(MessageCodes.YG.ERR_WS_VERIFICASAP_PORTALE_FUNZ_NON_ATTIVA);
			} else {
				SchedaAnagraficaProfessionaleWSProxy sapPortaleWSProxy = new SchedaAnagraficaProfessionaleWSProxy(
						VisualizzaSapPortaleEndPoint);

				SchedaAnagraficaProfessionaleDTO xmlSapPortale = sapPortaleWSProxy.getSapUtenteDTO(idSap, userName);

				response.setAttribute("SAPPORTALE", xmlSapPortale);
			}
		} catch (MySapWsException mex) {
			reportOperation.reportFailure(MessageCodes.YG.ERR_WS_VERIFICASAP_PORTALE_DA_DETTAGLIARE, true,
					mex.getFaultString());
			it.eng.sil.util.TraceWrapper.fatal(_logger, className + ": " + mex.getFaultString(), mex);
		} catch (RemoteException rex) {
			reportOperation.reportFailure(MessageCodes.YG.ERR_WS_VERIFICASAP_PORTALE_SERVIZIO_NON_ATTIVO);
			it.eng.sil.util.TraceWrapper.fatal(_logger, className + ": errore chiamata SAP Portale", rex);
		} catch (SourceBeanException sex) {
			reportOperation.reportFailure(MessageCodes.General.OPERATION_FAIL);
			it.eng.sil.util.TraceWrapper.fatal(_logger, className + ": errore generico visualizza SAP Portale", sex);
		}
	}
}