// Decompiled by DJ v3.4.4.74 Copyright 2003 Atanas Neshkov  Date: 29/09/2005 14.43.40
// Home Page : http://members.fortunecity.com/neshkov/dj.html  - Check often for new version!
// Decompiler options: packimports(3) 
// Source File Name:   GetDatiPersonali.java

package it.eng.sil.module.coop;

import java.sql.Connection;
import java.util.List;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.base.SourceBeanException;
import com.engiweb.framework.dbaccess.DataConnectionManager;

import it.eng.afExt.utils.MessageCodes;
import it.eng.afExt.utils.ReportOperationResult;
import it.eng.sil.coop.DataSourceJNDI;
import it.eng.sil.coop.endpoints.EndPoint;
import it.eng.sil.coop.utils.QueryExecutor;
import it.eng.sil.coop.wsClient.schedaLavoratore.SchedaLavoratore;
import it.eng.sil.coop.wsClient.schedaLavoratore.SchedaLavoratoreServiceLocator;
import it.eng.sil.module.AbstractSimpleModule;
import it.eng.sil.security.User;
import it.eng.sil.util.CompatibilitaVersionePoli;
import it.eng.sil.util.InfoProvinciaSingleton;

public class GetDatiPersonali extends AbstractSimpleModule {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(GetDatiPersonali.class.getName());

	public static String SCHEDA_LAVORATORE_COOP_ID = "SCHEDA_LAVORATORE_COOP_ID";

	public void service(SourceBean serviceRequest, SourceBean serviceResponse) throws SourceBeanException {
		ReportOperationResult reportOperation = new ReportOperationResult(this, serviceResponse);
		SchedaLavoratoreServiceLocator locator = null;
		String codiceFiscale = (String) serviceRequest.getAttribute("strcodicefiscale");
		String codProvincia = (String) serviceRequest.getAttribute("codProvinciaMaster");
		String rowString = null;
		String address = null;
		String versioneLocale = null;
		boolean ok = false;

		try {
			// CONTROLLO: SOLO SE POSITIVO SI PROSEGUE
			// per prima cosa controlliamo che la provincia da cui si vuole
			// caricare la scheda lavoratore
			// sia remota, ovvero non sia la stessa che vuole aprire la scheda
			// lavoratore in cooperazione
			String codProvinciaLocale = InfoProvinciaSingleton.getInstance().getCodice();
			if (codProvincia.equals(codProvinciaLocale)) {
				reportOperation.reportFailure(MessageCodes.Coop.CHIAMATA_A_SE_STESSI);
				serviceResponse.setAttribute("ERRORE_ID", "POLO_LOCALE_COMPETENTE");
				_logger.debug(
						"GetDatiPersonali (scheda lavoratore): tentativo si aprire la scheda di un lavoratore gia' competente del polo locale."
								+ "Codice fiscale=" + codiceFiscale + ", codProvincia=" + codProvincia
								+ ", codProvinciaLocale=" + codProvinciaLocale + ", url web service=" + address);

				return;
			}
			// se OK si chiama il web service
			try {
				User user = (User) getRequestContainer().getSessionContainer().getAttribute(User.USERID);
				AccessoSchedaLavoratore accessoSchedaLav = new AccessoSchedaLavoratore();
				List moduliAccessibili = accessoSchedaLav.moduliAccessibili(user);
				SourceBean requestSOAP = new SourceBean("SERVICE_REQUEST");
				requestSOAP.setAttribute("page", "coop_scheda_lavoratore");
				requestSOAP.setAttribute("strCodiceFiscaleLav", codiceFiscale);
				for (int i = 0; i < moduliAccessibili.size(); i++)
					requestSOAP.setAttribute(moduliAccessibili.get(i).toString(), "1");
				DataSourceJNDI dataSourceJndi = new DataSourceJNDI();
				String dataSourceJndiName = dataSourceJndi.getJndi();

				versioneLocale = this.leggiVersione();
				requestSOAP.setAttribute("strVersion", versioneLocale);

				locator = new SchedaLavoratoreServiceLocator();

				EndPoint endPoint = new EndPoint();
				endPoint.init(dataSourceJndiName, "SchedaLavoratore_" + codProvincia);

				address = endPoint.getUrl();

				_logger.debug("GetDatiPersonali (scheda lavoratore): codice fiscale=" + codiceFiscale
						+ ", codProvincia=" + codProvincia + ", url web service=" + address + ", request xml="
						+ requestSOAP.toString());

				if (address == null)
					throw new javax.xml.rpc.ServiceException(
							"GetDatiPersonali (scheda lavoratore): chiamata del servizio non possibile. URL WS NULL");

				locator.setSchedaLavoratoreEndpointAddress(address);
				SchedaLavoratore service = locator.getSchedaLavoratore();
				rowString = service.getDatiPersonali(requestSOAP.toXML());
				ok = true;
			} catch (javax.xml.rpc.ServiceException se) {
				// da SchedaLavoratore service= locator.getSchedaLavoratore();
				// o da controllo address null
				reportOperation.reportFailure(MessageCodes.Coop.ERR_SCHEDA_LAVORATORE);
				SourceBean errorResponse = new SourceBean("RESPONSE");
				serviceResponse.setAttribute("ERRORE_ID", "CHIAMATA");
				getRequestContainer().getSessionContainer().setAttribute(SCHEDA_LAVORATORE_COOP_ID, errorResponse);
				it.eng.sil.util.TraceWrapper.debug(_logger,
						"GetDatiPersonali (scheda lavoratore): errore nella chiamata del web service."
								+ "Codice fiscale=" + codiceFiscale + ", codProvincia=" + codProvincia
								+ ", url web service=" + address,
						se);

			}

			catch (java.rmi.RemoteException re) {
				reportOperation.reportFailure(MessageCodes.Coop.ERR_SCHEDA_LAVORATORE);
				SourceBean errorResponse = new SourceBean("RESPONSE");
				serviceResponse.setAttribute("ERRORE_ID", "ESECUZIONE_REMOTA");
				getRequestContainer().getSessionContainer().setAttribute(SCHEDA_LAVORATORE_COOP_ID, errorResponse);
				it.eng.sil.util.TraceWrapper.debug(_logger,
						"GetDatiPersonali (scheda lavoratore): errore remoto nella esecuzione del web service."
								+ "Codice fiscale=" + codiceFiscale + ", codProvincia=" + codProvincia
								+ ", url web service=" + address,
						re);

			} catch (Exception e) {
				reportOperation.reportFailure(MessageCodes.Coop.ERR_SCHEDA_LAVORATORE);
				SourceBean errorResponse = new SourceBean("RESPONSE");
				serviceResponse.setAttribute("ERRORE_ID", "GENERICO");
				getRequestContainer().getSessionContainer().setAttribute(SCHEDA_LAVORATORE_COOP_ID, errorResponse);
				it.eng.sil.util.TraceWrapper.debug(_logger,
						"GetDatiPersonali (scheda lavoratore): errore generico." + "Codice fiscale=" + codiceFiscale
								+ ", codProvincia=" + codProvincia + ", url web service=" + address,
						e);

			}
			// proseguo solo se ho ottenuto una risposta corretta in xml
			if (!ok)
				return;

			try {
				SourceBean rows = SourceBean.fromXMLString(rowString);
				serviceResponse.setAttribute(rows);
				if (rows.getAttribute("SERVICE_RESPONSE.LAVORATORE_NON_TROVATO") != null) {
					// bisogna generare un errore
					reportOperation.reportFailure(MessageCodes.Coop.ERR_SCHEDA_LAVORATORE_LAV_NOT_FOUND);
					SourceBean errorResponse = new SourceBean("RESPONSE");
					serviceResponse.setAttribute("ERRORE_ID", "LAVORATORE_NON_TROVATO");
					getRequestContainer().getSessionContainer().setAttribute(SCHEDA_LAVORATORE_COOP_ID, errorResponse);
					_logger.debug("Scheda Lavoratore: codice fiscale=" + codiceFiscale + ", codProvincia="
							+ codProvincia + ". Lavoratore non presente nel polo remoto");

					serviceResponse.setAttribute("IS_OK", "FALSE");
				} else {
					if (!rows.containsAttribute("versione")) {
						// bisogna generare un errore di versioning
						reportOperation.reportFailure(MessageCodes.Coop.ERR_VERSIONING);

						SourceBean errorResponse = new SourceBean("RESPONSE");
						serviceResponse.setAttribute("ERRORE_ID", "ERRORE_VERSIONING");
						getRequestContainer().getSessionContainer().setAttribute(SCHEDA_LAVORATORE_COOP_ID,
								errorResponse);
						_logger.debug("Scheda Lavoratore: codice fiscale=" + codiceFiscale + ", codProvincia="
								+ codProvincia + ". Errore di versioning tra i poli");

						serviceResponse.setAttribute("IS_OK", "FALSE");
					} else {
						String versioneEsterna = (String) rows.getAttribute("versione");
						CompatibilitaVersionePoli compVers = new CompatibilitaVersionePoli("SCLAVCO");
						boolean ckComp = compVers.checkCompatibilitaVersionePoli(versioneLocale, versioneEsterna);
						if (ckComp) {
							getRequestContainer().getSessionContainer().setAttribute(SCHEDA_LAVORATORE_COOP_ID, rows);
							serviceResponse.setAttribute("IS_OK", "TRUE");
						} else {
							SourceBean errorResponse = new SourceBean("RESPONSE");
							serviceResponse.setAttribute("ERRORE_ID", "ERRORE_VERSIONING");
							getRequestContainer().getSessionContainer().setAttribute(SCHEDA_LAVORATORE_COOP_ID,
									errorResponse);
							_logger.debug("Scheda Lavoratore: codice fiscale=" + codiceFiscale + ", codProvincia="
									+ codProvincia + ". Errore di versioning tra i poli");

							serviceResponse.setAttribute("IS_OK", "FALSE");
						}
					}
				}
			} catch (SourceBeanException sbe) {
				reportOperation.reportFailure(MessageCodes.Coop.ERR_SCHEDA_LAVORATORE_XML_MALFORMATO);
				SourceBean errorResponse = new SourceBean("RESPONSE");
				serviceResponse.setAttribute("ERRORE_ID", "DATI");
				getRequestContainer().getSessionContainer().setAttribute(SCHEDA_LAVORATORE_COOP_ID, errorResponse);
				it.eng.sil.util.TraceWrapper.debug(_logger, "Scheda Lavoratore: codice fiscale=" + codiceFiscale
						+ ", codProvincia=" + codProvincia + ". Dati ricevuti non in formato xml", sbe);

			}
		} catch (Exception eGen) {
			it.eng.sil.util.TraceWrapper.debug(_logger, "Scheda Lavoratore: codice fiscale=" + codiceFiscale
					+ ", codProvincia=" + codProvincia + ". Errore generico", eGen);

			reportOperation.reportFailure(MessageCodes.Coop.ERR_SCHEDA_LAVORATORE);
			serviceResponse.setAttribute("ERRORE_ID", "GENERICO");
			getRequestContainer().getSessionContainer().delAttribute(SCHEDA_LAVORATORE_COOP_ID);

		}
	}

	private String leggiVersione() throws Exception {
		Connection conn = DataConnectionManager.getInstance().getConnection().getInternalConnection();
		QueryExecutor qe = new QueryExecutor(conn);
		String stm = "select strver from ts_generale";
		SourceBean row = qe.executeQuery(stm, null);
		return it.eng.sil.util.Utils.notNull(row.getAttribute("row.strver"));
	}

}