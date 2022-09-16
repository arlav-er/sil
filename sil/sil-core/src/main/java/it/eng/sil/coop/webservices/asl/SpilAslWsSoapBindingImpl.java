/**
 * SpilAslWsSoapBindingImpl.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package it.eng.sil.coop.webservices.asl;

import java.io.IOException;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.util.QueryExecutor;

import it.eng.afExt.utils.CF_utils;
import it.eng.afExt.utils.DateUtils;
import it.eng.sil.Values;
import it.eng.sil.util.Utils;

public class SpilAslWsSoapBindingImpl implements it.eng.sil.coop.webservices.asl.SPILASLWSImpl {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(SPILASLWSImpl.class.getName());
	static final String filePropName = "InfoSpilAsl.properties";

	public it.eng.sil.coop.webservices.asl.GetInfoLavoratoreResponse getInfoLavoratore(
			it.eng.sil.coop.webservices.asl.Request req) throws java.rmi.RemoteException {
		GetInfoLavoratoreResponse lavoratore = null;
		GestoreAutenticazione login = null;
		SourceBean lav = null;
		String cognome = null;
		String nome = null;
		String dataNascita = null;
		String descStatoOcc = null;
		String dataInizioStatoOcc = null;
		String codiceFisc = req.getCf();
		if (codiceFisc == null)
			codiceFisc = "";
		String dataRif = req.getData();
		if (dataRif == null)
			dataRif = "";

		try {
			login = new GestoreAutenticazione(filePropName);
		} catch (IOException ioe) {
			it.eng.sil.util.TraceWrapper.error(_logger, "SPILASLWSImpl:getInfoLavoratore", ioe);
			lavoratore = new GetInfoLavoratoreResponse("01", "", "", codiceFisc, "", "", "");
			return lavoratore;
		}

		if (!login.checkCredenziali(req.getUsername(), req.getPassword())) {
			lavoratore = new GetInfoLavoratoreResponse("02", "", "", codiceFisc, "", "", "");
			return lavoratore;
		}

		try {
			if (codiceFisc.equals("") || codiceFisc.length() != 16) {
				throw new Exception("Codice fiscale non corretto.");
			}
			String ncodicefiscale = CF_utils.verificaCF(codiceFisc);
		} catch (Exception ex) {
			it.eng.sil.util.TraceWrapper.error(_logger, "SPILASLWSImpl:getInfoLavoratore", ex);
			lavoratore = new GetInfoLavoratoreResponse("04", "", "", codiceFisc, "", "", "");
			return lavoratore;
		}

		try {
			if (dataRif.equals("") || !DateUtils.isValidDate(dataRif, 0)) {
				throw new Exception("Data non corretta.");
			}
		} catch (Exception ex) {
			it.eng.sil.util.TraceWrapper.error(_logger, "SPILASLWSImpl:getInfoLavoratore", ex);
			lavoratore = new GetInfoLavoratoreResponse("05", "", "", codiceFisc, "", "", "");
			return lavoratore;
		}

		try {
			String dataOdierna = DateUtils.getNow();
			if (DateUtils.compare(dataRif, dataOdierna) > 0) {
				throw new Exception("Data futura.");
			}
		} catch (Exception ex) {
			it.eng.sil.util.TraceWrapper.error(_logger, "SPILASLWSImpl:getInfoLavoratore", ex);
			lavoratore = new GetInfoLavoratoreResponse("06", "", "", codiceFisc, "", "", "");
			return lavoratore;
		}

		try {
			Object[] params = new Object[] { dataRif, dataRif, codiceFisc };
			lav = (SourceBean) QueryExecutor.executeQuery("GET_INFO_LAVORATORE_ASL", params, "SELECT",
					Values.DB_SIL_DATI);
			if (lav != null) {
				lav = (lav.containsAttribute("ROW") ? (SourceBean) lav.getAttribute("ROW") : lav);
				String cdnlavoratore = Utils.notNull(lav.getAttribute("cdnLav"));
				if ((cdnlavoratore == null) || (cdnlavoratore.equalsIgnoreCase(""))) {
					lavoratore = new GetInfoLavoratoreResponse("03", "", "", codiceFisc, "", "", "");
					return lavoratore;
				}
				cognome = Utils.notNull(lav.getAttribute("cognomeLav"));
				nome = Utils.notNull(lav.getAttribute("nomeLav"));
				dataNascita = Utils.notNull(lav.getAttribute("dataNascitaLav"));
				descStatoOcc = Utils.notNull(lav.getAttribute("descStatoOccLav"));
				dataInizioStatoOcc = Utils.notNull(lav.getAttribute("dataInizioSoccLav"));
				lavoratore = new GetInfoLavoratoreResponse("00", cognome, nome, codiceFisc, dataNascita, descStatoOcc,
						dataInizioStatoOcc);
			} else {
				lavoratore = new GetInfoLavoratoreResponse("03", "", "", codiceFisc, "", "", "");
				return lavoratore;
			}
		} catch (Exception e) {
			lavoratore = new GetInfoLavoratoreResponse("07", "", "", codiceFisc, "", "", "");
		}
		return lavoratore;
	}

}
