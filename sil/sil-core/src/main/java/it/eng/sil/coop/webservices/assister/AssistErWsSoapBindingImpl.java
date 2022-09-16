/**
 * SpilAslWsSoapBindingImpl.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package it.eng.sil.coop.webservices.assister;

import java.util.Vector;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.util.QueryExecutor;

import it.eng.afExt.utils.CF_utils;
import it.eng.sil.Values;
import it.eng.sil.util.Utils;

public class AssistErWsSoapBindingImpl implements AssistErWsImpl {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger
			.getLogger(AssistErWsSoapBindingImpl.class.getName());

	public GetLavoratoreResponse getInfoLavoratore(GetLavoratoreRequest request) throws java.rmi.RemoteException {
		GetLavoratoreResponse response = null;
		GestoreAutenticazione login = null;
		String strCodiceFiscale = Utils.notNull(request.getCf());

		login = new GestoreAutenticazione();

		if (!login.checkCredenziali(request.getUsername(), request.getPassword())) {
			// controllo credenziali forzato a 99: Errore non previsto
			response = new GetLavoratoreResponse("99", strCodiceFiscale);
			return response;
		}

		try {
			if (strCodiceFiscale.equals("") || strCodiceFiscale.length() != 16) {
				throw new Exception("Codice fiscale non corretto.");
			}
			CF_utils.verificaCF(strCodiceFiscale);
		} catch (Exception ex) {
			it.eng.sil.util.TraceWrapper.error(_logger, "AssistErWsSoapBindingImpl:getInfoLavoratore", ex);
			// verifica CF forzato a 99: Errore non previsto
			response = new GetLavoratoreResponse("99", strCodiceFiscale);
			return response;
		}

		try {
			Object[] params = new Object[] { strCodiceFiscale };
			SourceBean lav = (SourceBean) QueryExecutor.executeQuery("GET_LAVORATORE_ASSISTER", params, "SELECT",
					Values.DB_SIL_DATI);
			if (lav != null) {

				try {
					Vector vector = lav.getAttributeAsVector("ROW");
					/*
					 * restituisce il primo record con il permesso di soggiorno più recente (order by datScadenza desc)
					 */
					SourceBean bean = (SourceBean) vector.get(0);

					// 00 Esito OK
					response = new GetLavoratoreResponse("00", bean);
					return response;
				} catch (Exception e) {
					_logger.error(e);
				}
			} else {
				// 01: Lavoratore non trovato
				response = new GetLavoratoreResponse("01", strCodiceFiscale);
				return response;
			}
		} catch (Exception e) {
			_logger.error(e);
			// 99: Errore non previsto
			response = new GetLavoratoreResponse("99", strCodiceFiscale);
		}
		return response;
	}

}
