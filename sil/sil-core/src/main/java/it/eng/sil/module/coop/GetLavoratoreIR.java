/*
 * Creato il 23-Mar-04
 * Author: rolfini
 */
package it.eng.sil.module.coop;

import com.engiweb.framework.base.SessionContainer;
import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.base.SourceBeanException;

import it.eng.afExt.utils.MessageCodes;
import it.eng.afExt.utils.ReportOperationResult;
import it.eng.afExt.utils.StringUtils;
import it.eng.sil.coop.DataSourceJNDI;
import it.eng.sil.coop.endpoints.EndPoint;
import it.eng.sil.coop.wsClient.serviziLavoratore.ServiziLavoratore;
import it.eng.sil.coop.wsClient.serviziLavoratore.ServiziLavoratoreServiceLocator;
import it.eng.sil.module.AbstractSimpleModule;
import it.eng.sil.module.voucher.Properties;
import it.eng.sil.security.User;

public class GetLavoratoreIR extends AbstractSimpleModule {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(GetLavoratoreIR.class.getName());

	static final int maxRisultatiAccettabili = 10;

	public void service(SourceBean request, SourceBean response) {

		ServiziLavoratoreServiceLocator locator = null;
		String codiceFiscale = null;
		String nome = null;
		String cognome = null;
		String dataNascita = null;
		String comune = null;
		String OK = "TRUE";
		// parametri di ricerca
		codiceFiscale = (String) request.getAttribute("strCodiceFiscale");
		nome = (String) request.getAttribute("strNome");
		cognome = (String) request.getAttribute("strCognome");
		dataNascita = (String) request.getAttribute("datnasc");
		comune = (String) request.getAttribute("codComNas");
		String tipoRic = StringUtils.getAttributeStrNotNull(request, "tipoRicerca");

		_logger.debug("GetLavoratoreIR (ricerca lavoratore): codice fiscale=" + codiceFiscale + ", cognome=" + cognome
				+ ", nome=" + nome + ", data nascita=" + dataNascita + ", comune nascita=" + comune + ", tipo ricerca="
				+ tipoRic);

		ReportOperationResult reportOperation = new ReportOperationResult(this, response);
		String address = null;
		SourceBean rows = null;
		try {
			// Savino 11/09/2006: controllo cooperazione abilitata
			String coopAttiva = System.getProperty("cooperazione.enabled");
			if (coopAttiva == null || coopAttiva.equals("false")) {
				response.setAttribute("COOP_ABILITATA", "false");
				return;
			}

			if (getRequestContainer() != null && getRequestContainer().getSessionContainer() != null) {
				SessionContainer session = getRequestContainer().getSessionContainer();
				User user = (User) session.getAttribute(User.USERID);
				String tipoGruppoCollegato = user.getCodTipo();
				if (tipoGruppoCollegato.equalsIgnoreCase(Properties.SOGGETTO_ACCREDITATO)) {
					response.setAttribute("DISABILITA_COOP_SOGGETTO_ACCRED", "true");
					return;
				}
			}

			DataSourceJNDI dataSourceJndi = new DataSourceJNDI();
			String dataSourceJndiName = dataSourceJndi.getJndi();

			locator = new ServiziLavoratoreServiceLocator();

			EndPoint endPoint = new EndPoint();
			endPoint.init(dataSourceJndiName, "IdxRegServiziLavoratore");

			address = endPoint.getUrl();

			_logger.debug("GetLavoratoreIR: codice fiscale=" + codiceFiscale + ", URL Web Service=" + address);

			locator.setServiziLavoratoreEndpointAddress(address);
			ServiziLavoratore service = locator.getServiziLavoratore();
			int i = 0;

			String rowString = service.getLavoratoreIR(codiceFiscale, nome, cognome, dataNascita, comune, tipoRic);
			rows = SourceBean.fromXMLString(rowString);
			if (rows.getAttributeAsVector("ROW").size() > maxRisultatiAccettabili) {
				response.setAttribute("TROPPI_RISULTATI", "Y");
				reportOperation.reportFailure(MessageCodes.Coop.ERR_TROPPI_RISULTATI);
				OK = "FALSE";
			} else {
				if (rows.getAttributeAsVector("ROW").size() == 0) {
					response.setAttribute("NESSUN_RISULTATO", "Y");
					OK = "FALSE";
				}
			}
			response.setAttribute(rows);
			// necessario per chiamare il modulo successivo in cooperazione
			response.setAttribute("IS_OK", OK);
		} catch (javax.xml.rpc.ServiceException se) {
			it.eng.sil.util.TraceWrapper.debug(_logger,
					"GetLavoratoreIR: impossibile chiamare il Web Service. Codice fiscale=" + codiceFiscale
							+ ", URL Web Service=" + address,
					se);

			reportOperation.reportFailure(MessageCodes.Coop.ERR_CONNESSIONE_IR);
		} catch (java.rmi.RemoteException re) {
			it.eng.sil.util.TraceWrapper.debug(_logger,
					"GetLavoratoreIR: errore nell'esecuzione remota del Web Service. Codice fiscale=" + codiceFiscale
							+ ", URL Web Service=" + address,
					re);

			reportOperation.reportFailure(MessageCodes.Coop.ERR_CPI_MASTER_IR);
		} catch (SourceBeanException sbe) {
			it.eng.sil.util.TraceWrapper.debug(_logger,
					"GetLavoratoreIR: errore nella risposta del Web Service. Codice fiscale=" + codiceFiscale
							+ ", URL Web Service=" + address + ", risposta XML=" + rows,
					sbe);

			reportOperation.reportFailure(MessageCodes.Coop.ERR_RESPONSE_XML_MALFORMATO);
		} catch (Exception e) {
			reportOperation.reportFailure(MessageCodes.General.OPERATION_FAIL);
			it.eng.sil.util.TraceWrapper.debug(_logger, "GetLavoratoreIR: errore generico. Codice fiscale="
					+ codiceFiscale + ", URL Web Service=" + address, e);

		}
	}

}