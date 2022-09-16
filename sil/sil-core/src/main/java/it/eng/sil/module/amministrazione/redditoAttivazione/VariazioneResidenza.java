package it.eng.sil.module.amministrazione.redditoAttivazione;

import java.rmi.RemoteException;
import java.text.ParseException;

import javax.xml.datatype.DatatypeConfigurationException;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.base.SourceBeanException;

import it.eng.afExt.utils.MessageCodes;
import it.eng.afExt.utils.ReportOperationResult;
import it.eng.sil.coop.DataSourceJNDI;
import it.eng.sil.coop.endpoints.EndPointSingleton;
import it.eng.sil.coop.webservices.nuovoRedditoAttivazione.NuovoRedditoAttivazioneProxy;
import it.eng.sil.coop.webservices.nuovoRedditoAttivazione.patToInps.common.EsitoComunicazioneType;
import it.eng.sil.coop.webservices.nuovoRedditoAttivazione.patToInps.response.ComunicazioneVariazioneResidenzaFuoriTrentoType;
import it.eng.sil.coop.webservices.nuovoRedditoAttivazione.patToInps.response.VariazioneResidenzaBean;
import it.eng.sil.module.AbstractSimpleModule;

public class VariazioneResidenza extends AbstractSimpleModule {

	private String END_POINT_NAME_CLIENT = "NuovoRedditoAttivazione";
	private static final long serialVersionUID = 5863792564864524603L;
	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(VariazioneResidenza.class.getName());
	private String className = this.getClass().getName();

	public void service(SourceBean request, SourceBean response) throws ParseException {

		ReportOperationResult reportOperation = new ReportOperationResult(this, response);

		////////////
		// METODO WS
		/////////////
		_logger.debug("Chiamata al WS");
		EsitoComunicazioneType esito = null;

		VariazioneResidenzaBean beanResidenza = new VariazioneResidenzaBean(request);

		try {
			ComunicazioneVariazioneResidenzaFuoriTrentoType domanda = beanResidenza
					.comunicazioneVariazioneResidenzaNra();

			DataSourceJNDI dataSourceJndi = new DataSourceJNDI();
			String dataSourceJndiName = dataSourceJndi.getJndi();
			EndPointSingleton eps = EndPointSingleton.getInstance(dataSourceJndiName);
			String urlNRA = eps.getUrl(END_POINT_NAME_CLIENT);
			// pezza per la PDD Trento
			urlNRA += "/comunicazioneVariazioneResidenza";
			NuovoRedditoAttivazioneProxy nra = new NuovoRedditoAttivazioneProxy(urlNRA);

			esito = nra.comunicazioneVariazioneResidenza(domanda);
		} catch (DatatypeConfigurationException e) {
			// TODO Auto-generated catch block
			_logger.error("Error: " + className + " Message:" + e.getMessage());
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			_logger.error("Error: " + className + " Message:" + e.getMessage());
		}

		// Imposta i i parametri per l'aggiornamento
		try {
			if (esito != null) {
				if (esito.getCodice().equals("A100")) {
					response.setAttribute("invio_corretto", "ok");
					reportOperation.reportSuccess(MappaturaErroriClientNRA.getSilCode("A100"));
				} else {
					reportOperation.reportFailure(MappaturaErroriClientNRA.getSilCode(esito.getCodice()));
				}
			} else
				reportOperation.reportFailure(MessageCodes.General.OPERATION_FAIL);
		} catch (SourceBeanException e) {
			reportOperation.reportFailure(MessageCodes.General.OPERATION_FAIL);
		}
	}
}
