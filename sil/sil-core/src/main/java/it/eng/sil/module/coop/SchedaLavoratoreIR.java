/*
 * Created on May 26, 2006
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package it.eng.sil.module.coop;

import com.engiweb.framework.base.SourceBean;

import it.eng.sil.coop.DataSourceJNDI;
import it.eng.sil.coop.endpoints.EndPoint;
import it.eng.sil.coop.wsClient.schedaLavoratore.SchedaLavoratore;
import it.eng.sil.coop.wsClient.schedaLavoratore.SchedaLavoratoreServiceLocator;
import it.eng.sil.module.AbstractSimpleModule;

/**
 * @author savino
 */
public class SchedaLavoratoreIR extends AbstractSimpleModule {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(SchedaLavoratoreIR.class.getName());

	/* 
	 */
	public void service(SourceBean serviceRequest, SourceBean serviceResponse) throws Exception {
		SchedaLavoratoreServiceLocator locator = null;

		String codiceFiscale = (String) serviceRequest.getAttribute("strcodicefiscale");
		String codProvincia = (String) serviceRequest.getAttribute("codProvincia");

		try {
			DataSourceJNDI dataSourceJndi = new DataSourceJNDI();
			String dataSourceJndiName = dataSourceJndi.getJndi();

			locator = new SchedaLavoratoreServiceLocator();

			EndPoint endPoint = new EndPoint();
			endPoint.init(dataSourceJndiName, "SchedaLavoratore_" + codProvincia);

			String address = endPoint.getUrl();
			locator.setSchedaLavoratoreEndpointAddress(address);
			SchedaLavoratore service = locator.getSchedaLavoratore();
			String rowString = service.getDatiPersonali(codiceFiscale);
			SourceBean rows = SourceBean.fromXMLString(rowString);
			serviceResponse.setAttribute(rows);
		} catch (Exception e) {
			it.eng.sil.util.TraceWrapper.debug(_logger, "errore ..", e);

		}

	}

}