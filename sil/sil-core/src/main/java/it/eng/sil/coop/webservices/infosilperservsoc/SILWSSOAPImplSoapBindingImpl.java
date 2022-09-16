/**
 * SILWSSOAPImplSoapBindingImpl.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package it.eng.sil.coop.webservices.infosilperservsoc;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Date;
import java.util.Properties;
import java.util.Vector;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.configuration.ConfigSingleton;
import com.engiweb.framework.util.QueryExecutor;

import it.eng.afExt.utils.DateUtils;
import it.eng.sil.Values;
import it.eng.sil.util.Utils;

public class SILWSSOAPImplSoapBindingImpl implements it.eng.sil.coop.webservices.infosilperservsoc.SILWSSOAPImpl {
	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(SILWSSOAPImpl.class.getName());
	static final String filePropName = "InfoSilPerServSoc.properties";

	public it.eng.sil.coop.webservices.infosilperservsoc.Response getInfoLavoratore(
			it.eng.sil.coop.webservices.infosilperservsoc.Request req) throws java.rmi.RemoteException {
		Properties listaProperties;

		GestoreAutenticazione login = null;
		Vector rows = null;
		String cdnlavoratore = null;

		Response res = new Response();

		// Istanzio il gestore per l'autenticazione passandogli il file di properties che contiene le credenziali
		try {
			login = new GestoreAutenticazione(filePropName);
		} catch (IOException ioe) {
			it.eng.sil.util.TraceWrapper.error(_logger, "SILWSSOAPImpl:getInfoLavoratore", ioe);
			res.setErrore("01");
			return res;
		}

		if (!login.checkCredenziali(req.getUsername(), req.getPassword())) {
			res.setErrore("02");
			return res;
		}

		String s = ConfigSingleton.getRootPath() + File.separator + "WEB-INF" + File.separator + "classes"
				+ File.separator + filePropName;
		File f = new File(s);
		listaProperties = new Properties();
		try {
			listaProperties.load(new FileInputStream(f));
		} catch (IOException ioe) {
			it.eng.sil.util.TraceWrapper.error(_logger, "SILWSSOAPImpl:getInfoLavoratore", ioe);
			res.setErrore("01");
			return res;
		}

		Date conv_data_null = DateUtils.convertStringToDate(listaProperties.getProperty("CONV_DATA_NULL"));

		String cf = req.getCf();
		res.setCf(cf);
		// recupero i dati anagrafici del lavoratore
		SourceBean anagraficaLav = (SourceBean) QueryExecutor.executeQuery("GET_DATI_GENERALI_LAVORATORE",
				new Object[] { cf }, "SELECT", Values.DB_SIL_DATI);

		cdnlavoratore = Utils.notNull(anagraficaLav.getAttribute("ROW.CDNLAVORATORE"));

		if ((cdnlavoratore == null) || (cdnlavoratore.equalsIgnoreCase(""))) {
			res.setErrore("03");
			return res;
		}

		res.setNome(Utils.notNull(anagraficaLav.getAttribute("ROW.NOME")));
		res.setCognome(Utils.notNull(anagraficaLav.getAttribute("ROW.COGNOME")));

		if (!Utils.notNull(anagraficaLav.getAttribute("ROW.DATANASCITA")).equals("")) {
			res.setDataNascita(
					DateUtils.convertStringToDate(Utils.notNull(anagraficaLav.getAttribute("ROW.DATANASCITA"))));
		} else
			res.setDataNascita(conv_data_null);

		res.setStatoOcc(Utils.notNull(anagraficaLav.getAttribute("ROW.STATOOCC")));

		if (!Utils.notNull(anagraficaLav.getAttribute("ROW.DATAINIZIOSTATOOCC")).equals("")) {
			res.setDataInizioStatoOcc(
					DateUtils.convertStringToDate(Utils.notNull(anagraficaLav.getAttribute("ROW.DATAINIZIOSTATOOCC"))));
		} else
			res.setDataInizioStatoOcc(conv_data_null);

		// recupero i dati della DID
		SourceBean datiDid = (SourceBean) QueryExecutor.executeQuery("GET_DATI_DID", new Object[] { cdnlavoratore },
				"SELECT", Values.DB_SIL_DATI);

		if (!Utils.notNull(datiDid.getAttribute("ROW.DATADID")).equals("")) {
			res.setDataDid(DateUtils.convertStringToDate(Utils.notNull(datiDid.getAttribute("ROW.DATADID"))));
		} else
			res.setDataDid(conv_data_null);

		res.setTipoDid(Utils.notNull(datiDid.getAttribute("ROW.TIPODID")));
		res.setStatoDid(Utils.notNull(datiDid.getAttribute("ROW.STATODID")));

		// recupero le azioni concordate del lavoratore
		SourceBean sb = (SourceBean) QueryExecutor.executeQuery("GET_AZIONI_CONCORDATE", new Object[] { cdnlavoratore },
				"SELECT", Values.DB_SIL_DATI);

		rows = sb.getAttributeAsVector("ROW");

		if (rows != null) {

			int numAzioniConcordate = rows.size();

			if (numAzioniConcordate > 0)
				res.setListaAzioniConcordate(new AzioneConcordata[numAzioniConcordate]);
			else
				res.setListaAzioniConcordate(null);

			for (int i = 0; i < rows.size(); i++) {
				SourceBean row = (SourceBean) rows.get(i);

				res.setListaAzioniConcordate(i, new AzioneConcordata());

				if (!Utils.notNull(row.getAttribute("datColloquio")).equals("")) {
					res.getListaAzioniConcordate(i).setDataColloquio(
							DateUtils.convertStringToDate(Utils.notNull(row.getAttribute("datColloquio"))));
				} else
					res.getListaAzioniConcordate(i).setDataColloquio(conv_data_null);
				res.getListaAzioniConcordate(i).setAzione(Utils.notNull(row.getAttribute("azione")));
				if (!Utils.notNull(row.getAttribute("datStimata")).equals("")) {
					res.getListaAzioniConcordate(i).setDataStimata(
							DateUtils.convertStringToDate(Utils.notNull(row.getAttribute("datStimata"))));
				} else
					res.getListaAzioniConcordate(i).setDataStimata(conv_data_null);
				res.getListaAzioniConcordate(i).setEsito(Utils.notNull(row.getAttribute("esitoDataSvolgimento")));
				res.getListaAzioniConcordate(i).setDomicilio(Utils.notNull(row.getAttribute("indirizzoDom")));
				res.getListaAzioniConcordate(i).setTelefono(Utils.notNull(row.getAttribute("telefono")));
				res.getListaAzioniConcordate(i).setCpi(Utils.notNull(row.getAttribute("cpiTitComp")));
			}
		}

		// recupero i colloqui del lavoratore
		sb = (SourceBean) QueryExecutor.executeQuery("GET_COLLOQUI", new Object[] { cdnlavoratore }, "SELECT",
				Values.DB_SIL_DATI);

		rows = sb.getAttributeAsVector("ROW");

		if (rows != null) {

			int numColloqui = rows.size();

			if (numColloqui > 0)
				res.setListaColloqui(new Colloquio[numColloqui]);
			else
				res.setListaColloqui(null);

			for (int i = 0; i < rows.size(); i++) {
				SourceBean row = (SourceBean) rows.get(i);

				res.setListaColloqui(i, new Colloquio());
				if (!Utils.notNull(row.getAttribute("datcolloquio")).equals("")) {
					res.getListaColloqui(i).setDataColloquio(
							DateUtils.convertStringToDate(Utils.notNull(row.getAttribute("datcolloquio"))));
				} else
					res.getListaColloqui(i).setDataColloquio(conv_data_null);
				res.getListaColloqui(i).setServizio(Utils.notNull(row.getAttribute("strdescrizione")));
			}
		}

		// recupero le segnalazioni e le esclusioni dalle rose del lavoratore
		sb = (SourceBean) QueryExecutor.executeQuery("GET_SEGNALAZIONI_IN_ROSE", new Object[] { cdnlavoratore },
				"SELECT", Values.DB_SIL_DATI);

		rows = sb.getAttributeAsVector("ROW");

		if (rows != null) {

			int numSegnalazioni = rows.size();

			if (numSegnalazioni > 0)
				res.setListaSegnalazioniRosa(new SegnalazioneRosa[numSegnalazioni]);
			else
				res.setListaSegnalazioniRosa(null);

			for (int i = 0; i < rows.size(); i++) {
				SourceBean row = (SourceBean) rows.get(i);

				res.setListaSegnalazioniRosa(i, new SegnalazioneRosa());
				if (!Utils.notNull(row.getAttribute("datSegnalazione")).equals("")) {
					res.getListaSegnalazioniRosa(i).setDataSegnalazione(
							DateUtils.convertStringToDate(Utils.notNull(row.getAttribute("datSegnalazione"))));
				} else
					res.getListaSegnalazioniRosa(i).setDataSegnalazione(conv_data_null);
				res.getListaSegnalazioniRosa(i).setSegnalazione(Utils.notNull(row.getAttribute("SEGNALAZIONE")));
			}
		}

		// recupero gli appuntamenti e i colloqui del lavoratore
		sb = (SourceBean) QueryExecutor.executeQuery("GET_APPUNTAMENTI_COLLOQUI", new Object[] { cdnlavoratore },
				"SELECT", Values.DB_SIL_DATI);

		rows = sb.getAttributeAsVector("ROW");

		if (rows != null) {

			int numAppuntamenti = rows.size();

			if (numAppuntamenti > 0)
				res.setListaAppuntamenti(new Appuntamento[numAppuntamenti]);
			else
				res.setListaAppuntamenti(null);

			for (int i = 0; i < rows.size(); i++) {
				SourceBean row = (SourceBean) rows.get(i);

				res.setListaAppuntamenti(i, new Appuntamento());
				if (!Utils.notNull(row.getAttribute("datAppuntamento")).equals("")) {
					res.getListaAppuntamenti(i).setDataAppuntamento(
							DateUtils.convertStringToDate(Utils.notNull(row.getAttribute("datAppuntamento"))));
				} else
					res.getListaAppuntamenti(i).setDataAppuntamento(conv_data_null);
				res.getListaAppuntamenti(i).setDettaglio(Utils.notNull(row.getAttribute("DETTAGLIO")));
			}
		}

		return res;
	}

}
