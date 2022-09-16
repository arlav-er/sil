/*
 * Creato il 23-Mar-04
 * Author: rolfini
 */
package it.eng.sil.module.coop;

import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.utils.MessageCodes;
import it.eng.afExt.utils.ReportOperationResult;
import it.eng.sil.coop.DataSourceJNDI;
import it.eng.sil.coop.endpoints.EndPoint;
import it.eng.sil.coop.wsClient.serviziLavoratore.CpiMasterLavoratoreBean;
import it.eng.sil.coop.wsClient.serviziLavoratore.ServiziLavoratore;
import it.eng.sil.coop.wsClient.serviziLavoratore.ServiziLavoratoreServiceLocator;
import it.eng.sil.module.AbstractSimpleModule;

public class GetCpiMasterIR extends AbstractSimpleModule {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(GetCpiMasterIR.class.getName());

	public void service(SourceBean request, SourceBean response) {

		ServiziLavoratoreServiceLocator locator = null;
		CpiMasterLavoratoreBean cpiMaster = null;
		String codiceFiscale = null;
		String cognome = null;
		String nome = null;
		String datNasc = null;
		String codComNas = null;
		String codCpiMaster = null;
		String codTipoMaster = null;
		String dataMaster = null;

		codiceFiscale = (String) request.getAttribute("strCodiceFiscale");

		ReportOperationResult reportOperation = new ReportOperationResult(this, response);
		String address = null;
		try {
			DataSourceJNDI dataSourceJndi = new DataSourceJNDI();
			String dataSourceJndiName = dataSourceJndi.getJndi();

			locator = new ServiziLavoratoreServiceLocator();

			EndPoint endPoint = new EndPoint();
			endPoint.init(dataSourceJndiName, "IdxRegServiziLavoratore");

			address = endPoint.getUrl();

			_logger.debug("GetCpiMasterIR: codice fiscale=" + codiceFiscale + ", URL Web Service=" + address);

			locator.setServiziLavoratoreEndpointAddress(address);
			ServiziLavoratore service = locator.getServiziLavoratore();

			cpiMaster = service.getCpiMasterIR(codiceFiscale);
			codiceFiscale = cpiMaster.getCodiceFiscale();
			cognome = cpiMaster.getCognome();
			nome = cpiMaster.getNome();
			datNasc = cpiMaster.getDatNasc();
			codComNas = cpiMaster.getCodComNas();
			codCpiMaster = cpiMaster.getCodCpiMaster();
			codTipoMaster = cpiMaster.getCodTipoMaster();
			dataMaster = cpiMaster.getDataMaster();

			SourceBean rows = new SourceBean("ROWS");
			SourceBean row = new SourceBean("ROW");
			if (codCpiMaster != null) {
				row.setAttribute("codiceFiscale", cpiMaster.getCodiceFiscale());
				row.setAttribute("cognome", cpiMaster.getCognome());
				row.setAttribute("nome", cpiMaster.getNome());
				row.setAttribute("datNasc", cpiMaster.getDatNasc());
				row.setAttribute("codComNas", cpiMaster.getCodComNas());
				row.setAttribute("codCpiMaster", cpiMaster.getCodCpiMaster());
				row.setAttribute("codTipoMaster", cpiMaster.getCodTipoMaster());
				row.setAttribute("dataMaster", cpiMaster.getDataMaster());
			} else {
				// non ci sono risultati -> il lavoratore non è stato trovato
				// sull'Indice
				reportOperation.reportFailure(MessageCodes.Coop.ERR_IR_LAV_NOTFOUND);
			}

			rows.setAttribute(row);
			response.setAttribute(rows);
		} catch (javax.xml.rpc.ServiceException se) {
			it.eng.sil.util.TraceWrapper.debug(_logger,
					"GetCpiMasterIR: impossibile chiamare il Web Service. Codice fiscale=" + codiceFiscale
							+ ", URL Web Service=" + address,
					se);

			reportOperation.reportFailure(MessageCodes.Coop.ERR_CONNESSIONE_IR);
		} catch (java.rmi.RemoteException re) {
			it.eng.sil.util.TraceWrapper.debug(_logger,
					"GetCpiMasterIR: errore nell'esecuzione remota del Web Service. Codice fiscale=" + codiceFiscale
							+ ", URL Web Service=" + address,
					re);

			reportOperation.reportFailure(MessageCodes.Coop.ERR_CPI_MASTER_IR);
		} catch (Exception e) {
			reportOperation.reportFailure(MessageCodes.General.OPERATION_FAIL);
			it.eng.sil.util.TraceWrapper.debug(_logger,
					"GetCpiMasterIR: errore generico. Codice fiscale=" + codiceFiscale + ", URL Web Service=" + address,
					e);

		}

	}

}