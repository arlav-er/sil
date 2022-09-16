/*
 * Creato il 13-Apr-04
 * Author: rolfini
 * 
 */
package it.eng.sil.coop.webservices.schedaLavoratore;

import java.sql.Connection;

import com.engiweb.framework.base.ResponseContainer;
import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.dbaccess.DataConnectionManager;
import com.engiweb.framework.dispatching.soapchannel.AdapterSOAP;
import com.engiweb.framework.error.EMFErrorHandler;
import com.engiweb.framework.error.EMFErrorSeverity;
import com.engiweb.framework.error.EMFUserError;
import com.engiweb.framework.presentation.PresentationRendering;
import com.engiweb.framework.util.QueryExecutor;

import it.eng.afExt.utils.MessageCodes;
import it.eng.sil.Values;

/**
 * @author rolfini
 * 
 */
public class SchedaLavoratore {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(SchedaLavoratore.class.getName());

	public String getDatiPersonali(String requestString) {

		String response = null;
		String strCodiceFiscaleLav;
		SourceBean serviceRequest = null;
		boolean lavoratoreNonPresente = true;
		try {
			serviceRequest = SourceBean.fromXMLString(requestString);
			boolean existVers = serviceRequest.containsAttribute("strVersion");

			if (existVers && !((String) serviceRequest.getAttribute("strVersion")).equals("")) {
				strCodiceFiscaleLav = (String) serviceRequest.getAttribute("strCodiceFiscaleLav");
				if (strCodiceFiscaleLav != null) {
					String cdnLavoratore = recuperaCdnLavoratore(strCodiceFiscaleLav);
					serviceRequest.setAttribute("CDNLAVORATORE", cdnLavoratore);
					// Savino 20/11/2006: chiusura ipotetica falla.. Ora verra'
					// chiamata sempre e solo la page "coop_scheda_lavoratore"
					serviceRequest.updAttribute("page", "coop_scheda_lavoratore");
					// l'informazione va inserita nella response generale
					lavoratoreNonPresente = cdnLavoratore.equals("");
				}
				if (!lavoratoreNonPresente) {
					AdapterSOAP soap = new AdapterSOAP();
					response = soap.service(serviceRequest.toXML());
					response = aggiungiCodiceFiscale(response, strCodiceFiscaleLav);
					response = readVersionAndPutInResponse(response);
				} else {
					ResponseContainer rs = new ResponseContainer();
					rs.setErrorHandler(new EMFErrorHandler());
					rs.getErrorHandler().addError(new EMFUserError(EMFErrorSeverity.ERROR,
							MessageCodes.Coop.ERR_SCHEDA_LAVORATORE_LAV_NOT_FOUND));
					SourceBean serviceResponse = new SourceBean("SERVICE_RESPONSE");
					serviceResponse.setAttribute("LAVORATORE_NON_TROVATO", "TRUE");
					rs.setServiceResponse(serviceResponse);
					response = PresentationRendering.render(rs, null);
				}
			} else {
				response = null;
			}
		} catch (Exception e) {
			e.printStackTrace();
			it.eng.sil.util.TraceWrapper.fatal(_logger, "..." + requestString, e);

		}
		return response;

	}

	/**
	 * @param strCodiceFiscaleLav
	 * @return
	 */
	private String recuperaCdnLavoratore(String strCodiceFiscaleLav) {
		SourceBean lav = (SourceBean) QueryExecutor.executeQuery("SELECT_AN_LAVORATORE",
				new Object[] { strCodiceFiscaleLav }, "SELECT", /* "COOP_DATI" */Values.DB_SIL_DATI);

		return it.eng.sil.util.Utils.notNull(lav.getAttribute("row.cdnLavoratore"));
	}

	// aggiunge il codice fiscale del lavoratore alla response
	private String aggiungiCodiceFiscale(String response, String codiceFiscale) {
		String res = response;
		int index = response.indexOf("<RESPONSE>");
		if (index >= 0) {
			String part2 = response.substring(index + 9);
			String part1 = "<RESPONSE codiceFiscale=\"" + codiceFiscale + "\" ";
			res = part1 + part2;
		}
		return res;
	}

	// legge e aggiunge la versione "esterna" alla response
	private String readVersionAndPutInResponse(String response) throws Exception {
		Connection conn = null;
		try {
			conn = DataConnectionManager.getInstance().getConnection().getInternalConnection();
			it.eng.sil.coop.utils.QueryExecutor qe = new it.eng.sil.coop.utils.QueryExecutor(conn);
			String stm = "select strver from ts_generale";
			SourceBean row = qe.executeQuery(stm, null);
			String versione = it.eng.sil.util.Utils.notNull(row.getAttribute("row.strver"));
			String res = response;
			int index = response.indexOf("<RESPONSE ");
			if (index >= 0) {
				String part2 = response.substring(index + 9);
				String part1 = "<RESPONSE versione=\"" + versione + "\" ";
				res = part1 + part2;
			}
			return res;
		} finally {
			if (conn != null) {
				conn.close();
			}
		}
	}
}