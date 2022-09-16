package it.eng.sil.module.anag;

import java.rmi.RemoteException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Vector;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.base.SourceBeanException;
import com.engiweb.framework.dbaccess.sql.DataConnection;
import com.engiweb.framework.dbaccess.sql.SQLCommand;
import com.engiweb.framework.dbaccess.sql.result.DataResult;
import com.engiweb.framework.dbaccess.sql.result.DataResultInterface;
import com.engiweb.framework.error.EMFInternalError;

import it.eng.afExt.utils.MessageCodes;
import it.eng.afExt.utils.ReportOperationResult;
import it.eng.sil.coop.DataSourceJNDI;
import it.eng.sil.coop.endpoints.EndPointSingleton;
import it.eng.sil.module.AbstractSimpleModule;
import it.eng.sil.module.voucher.Properties;
import it.eng.sil.mysap.model.ejb.stateless.ws.exportsap.MySapWsException;
import it.eng.sil.mysap.model.ejb.stateless.ws.exportsap.SchedaAnagraficaProfessionaleHeader;
import it.eng.sil.mysap.model.ejb.stateless.ws.exportsap.SchedaAnagraficaProfessionaleWSProxy;
import it.eng.sil.security.PageAttribs;
import it.eng.sil.security.User;
import it.eng.sil.util.Utils;

//Modulo mysap buttato - NON USARE
public class VerificaEsistenzaSapPortale extends AbstractSimpleModule {

	private static final long serialVersionUID = -1533726811528932058L;
	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger
			.getLogger(VerificaEsistenzaSapPortale.class.getName());
	private String className = this.getClass().getName();
	private String END_POINT_NAME = "GestioneSapPortale";
	private SourceBean listaSapHeader;
	public static final String FORMATO_DATA = "dd/MM/yyyy";
	private int numeroRisultati = 0;

	public void service(SourceBean request, SourceBean response) {
		User user = (User) getRequestContainer().getSessionContainer().getAttribute("@@USER@@");
		String page = Utils.notNull(request.getAttribute("PAGE"));
		boolean canSAP = true;

		if (page.equals("AnagRicercaPage")) {
			PageAttribs attributi = new PageAttribs(user, page);
			List sezioni = attributi.getSectionList();
			canSAP = sezioni.contains("ACCOUNT_SAP_DA_PORTALE");
		}

		String tipoGruppoCollegato = user.getCodTipo();
		if (tipoGruppoCollegato.equalsIgnoreCase(Properties.SOGGETTO_ACCREDITATO)) {
			canSAP = false;
		}

		if (canSAP) {
			String cdnLavoratore = Utils.notNull(request.getAttribute("CDNLAVORATORE"));
			String strCodiceFiscale = Utils.notNull(request.getAttribute("strCodiceFiscale"));
			ReportOperationResult reportOperation = new ReportOperationResult(this, response);
			disableMessageIdFail();
			disableMessageIdSuccess();
			DataConnection conn = null;

			try {
				_logger.info("CHIAMATA VERIFICA ESISTENZA SAP SU PORTALE");

				if (cdnLavoratore.isEmpty()) {
					_logger.info(", STRCODICEFISCALE =" + strCodiceFiscale);
				} else {
					_logger.info(", CDNLAVORATORE =" + cdnLavoratore);
					SourceBean anLavoratoreSB = doSelect(request, response, false);
					strCodiceFiscale = (String) anLavoratoreSB.getAttribute("ROW.STRCODICEFISCALE");
				}
				DataSourceJNDI dataSourceJndi = new DataSourceJNDI();
				String dataSourceJndiName = dataSourceJndi.getJndi();
				EndPointSingleton eps = EndPointSingleton.getInstance(dataSourceJndiName);
				String VerificaEsistenzaSapPortaleEndPoint = eps.getUrl(END_POINT_NAME);
				// verifica se il ws è disponibile
				if (VerificaEsistenzaSapPortaleEndPoint.startsWith("__sbagliato__")) {
					reportOperation.reportFailure(MessageCodes.YG.ERR_WS_VERIFICASAP_PORTALE_FUNZ_NON_ATTIVA);
				} else {
					SchedaAnagraficaProfessionaleWSProxy sapPortaleWSProxy = new SchedaAnagraficaProfessionaleWSProxy(
							VerificaEsistenzaSapPortaleEndPoint);

					SchedaAnagraficaProfessionaleHeader[] xmlSapPortaleHeader = sapPortaleWSProxy
							.getListaSap(strCodiceFiscale);

					listaSapHeader = new SourceBean("ROWS");
					conn = getDataConnection();

					for (int i = 0; i < xmlSapPortaleHeader.length; i++) {
						SourceBean row = new SourceBean("ROW");

						row.setAttribute("idSap", xmlSapPortaleHeader[i].getIdSap().toString());
						row.setAttribute("userName", xmlSapPortaleHeader[i].getUserName());
						row.setAttribute("email", xmlSapPortaleHeader[i].getEmail());
						row.setAttribute("strCodiceFiscale", strCodiceFiscale);
						SimpleDateFormat sdf = new SimpleDateFormat(FORMATO_DATA);
						row.setAttribute("dataNascita",
								sdf.format((xmlSapPortaleHeader[i].getDataNascita().getTime())));
						String codComune = xmlSapPortaleHeader[i].getComuneNascita();
						// decodifica il codice comune
						String strDenominazione = "";
						SQLCommand select = null;
						String strSelect = "select strDenominazione from DE_COMUNE where codCom = '" + codComune + "'";
						_logger.debug(strSelect);
						try {
							select = conn.createSelectCommand(strSelect);
							DataResult risultato = select.execute();
							DataResultInterface dataResult = risultato.getDataObject();
							SourceBean rows = dataResult.getSourceBean();
							@SuppressWarnings("rawtypes")
							Vector riga = rows.getAttributeAsVector("ROW");
							SourceBean sourceBean = (SourceBean) riga.get(0);
							strDenominazione = sourceBean.getAttribute("strDenominazione").toString();
						} catch (EMFInternalError e) {
							strDenominazione = codComune;
							_logger.error("Errore nella decodifica del codice comune: " + e.getMessage());
						} finally {
							try {
								select.close();
							} catch (EMFInternalError e) {
								_logger.error("Errore nella chiusura dell'istruzione SQL: " + e.getMessage());
							}
						}
						row.setAttribute("comuneNascita", strDenominazione);
						String dtm = sdf.format(xmlSapPortaleHeader[i].getDtmIns().getTime());
						row.setAttribute("dtmins", dtm);

						listaSapHeader.setAttribute(row);
						numeroRisultati++;
					}
					setRisultatoOK();
					response.setAttribute(listaSapHeader);
					getRequestContainer().getSessionContainer().setAttribute("ROWS", listaSapHeader);
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
				it.eng.sil.util.TraceWrapper.fatal(_logger,
						className + ": errore generico verifica esistenza SAP Portale", sex);
			} catch (EMFInternalError emf) {
				reportOperation.reportFailure(MessageCodes.YG.ERR_WS_VERIFICASAP_PORTALE_SERVIZIO_NON_ATTIVO);
				it.eng.sil.util.TraceWrapper.fatal(_logger, className + ": errore chiamata SAP Portale", emf);
			} finally {
				com.engiweb.framework.dbaccess.Utils.releaseResources(conn, null, null);
			}
		}
	}

	private void setRisultatoOK() throws SourceBeanException {
		listaSapHeader.setAttribute("CURRENT_PAGE", new Integer("1"));
		listaSapHeader.setAttribute("NUM_PAGES", new Integer("1"));
		listaSapHeader.setAttribute("ROWS_X_PAGE", new Integer(numeroRisultati));
		listaSapHeader.setAttribute("NUM_RECORDS", new Integer(numeroRisultati));
	}
}